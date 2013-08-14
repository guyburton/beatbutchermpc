package libBBMPC.utils;

import java.io.File;
import java.security.InvalidParameterException;

import javax.sound.sampled.AudioFormat;

import libBBMPC.utils.data.Sample;


public class AudioFunctions {
	/**
	 * This function performs a normalize function on a byte array of formatted audio data
	 * @param d the raw audio data
	 * @param f the format of the data array
	 */
	public static void normalize(Sample s) {
		if (s == null)
			return;
		byte[] d = s.getData();
		double max = 0;
		byte[] b = new byte[s.getFormat().getFrameSize()
				/ s.getFormat().getChannels()];
		for (int i = 0; i < d.length;) {
			for (int j = 0; j < b.length; j++) {
				b[j] = d[i++];
			}
			double x = bytesToFloat(b);
			if (x > max) {
				max = x;
			}
		}
		// leave 3 per cent headroom
		max *= 1.03f;
		int i = 0;
		while (i < d.length - b.length) {
			for (int j = 0; j < b.length; j++) {
				b[j] = d[i+j];
			}
			float x = bytesToFloat(b);
			x = x / (float) max;
			floatToBytes(x, b);
			for (int j = 0; j < b.length; j++) {
				d[i+j] = b[j];
				i++;
			}			
		}
	}
	/**
	 * Performs transient detection on the provided audio data
	 * @param sensitivity an arbitrary percentage value determining sensitivity of algorithm
	 * @param data a the audio data byte array
	 * @param f the format of the the data array
	 */
	public static int[] autochop(int sensitivity, Sample s){
		throw new UnsupportedOperationException("Autochop not yet supported!");
	}
	/**
	 * Applies gain to the provided data array
	 * @param gain the linear gain to be applied (not in dB)
	 * @param d
	 */
	public static void gain(float gain, byte[] d, AudioFormat f){
		float x;
		final int bytes = f.getFrameSize()/f.getChannels();
		for (int i=0;i<d.length; i+=2){
			x = d[i] & 0xff; 
			for (int j=1; j<bytes; j++)
				x = (int)x| (d[i+j]<<8*j);
			x *= Math.pow(2d, 8*bytes) * gain;
			for (int j=0; j<bytes; j++){
				d[i+j] = (byte)((int)x & 0x000000FF);
				x=(((int)x) & 0xFFFFFF00)>>8;
			}
		}
	}
	/**
	 * Reverses the audio data given between the points start and end
	 * @param s
	 * @param start
	 * @param end
	 * @return
	 */
	public static void reverseSlice(final byte[] d, final AudioFormat f, final int start, final int end){
		// allocate buffer
		byte[] d2 = new byte[(int)(end-start) * f.getFrameSize()];
		// fill buffer
		for (int j=0; j<d2.length; j++){
			d2[j] = d[((int)start * f.getFrameSize())+j];
		}
		// put buffer reversed values back into original array
		int i=0;
		while(i<d2.length-f.getFrameSize()){
			for (int j=0; j<f.getFrameSize(); j++){
				int x = (d2.length) - i - (f.getFrameSize()-j);
				int y = i + start*f.getFrameSize() + j;
				d[y] = d2[x];
			}
			i += f.getFrameSize();
		}
	}
	public static final String[] MONO_CONVERSION_SOURCES = { "Left Channel", "Right Channel", "Stereo Mix", "Cancel"};

	/**
	 * Converts the given stereo audio data into mono
	 * @param an integer index representing one of the strings in MONO_CONVERSION_SOURCES
	 * @param data_src the byte array containing audio information
	 * @param data_dest a preallocated byte array of half the data_src length to be filled
	 * @param f the audio format of the data array
	 * @return the new audio format of the data
	 */
	public static AudioFormat toMono(final int selection, final byte[] data_src, final byte[] data_dest, final AudioFormat f){
		if (f.getChannels()!=2)
			throw new RuntimeException("Clip is not stereo!");
		int j=0;
		final int bytes = f.getFrameSize()/f.getChannels();
		switch(selection){
		case 0:
			for (int i=0; i<data_dest.length;){
				for (int x=0; x<bytes; x++)
					data_dest[i++] = data_src[j++];
				for (int x=0; x<bytes; x++)
					j++;
			}
				break;
		case 1:
			for (int i=0; i<data_dest.length;){
				for (int x=0; x< bytes; x++)
					j++;
				for (int x=0; x<f.getFrameSize()/f.getChannels(); x++)
					data_dest[i++] = data_src[j++];
			}
			break;
		case 2:
			//TODO:make sure this works!
			for (int i=0; i<data_dest.length;){
				for (int x=0; x< bytes*2; x++)
					data_dest[i+x] = (byte)(data_src[j+x]/2.f);
				i+=bytes;
				j+=bytes*2;
			}
			break;
		default:
		}
		return new AudioFormat(f.getEncoding(), f.getSampleRate(), f.getSampleSizeInBits(),1, 2, f.getFrameRate(), f.isBigEndian());
	}
	/**
	 * Converts an array of bytes representing one sample into a floating point value
	 * between 0f and 1f
	 * @param b the little endian byte array to convert
	 * @return the floating point equivalent between 0 and 1
	 */
	public static float bytesToFloat(byte[] b){
		switch(b.length){
		case 1: return b[0]/256f;
		case 2:
			return (Math.abs(((b[0] & 0xFF)
					 | (b[1] << 8) )
					/ 32768.0f));
		case 3:
			return Math.abs(((b[0] & 0xFF)
				 | ((b[1]&0xFF) << 8)
				 | (b[2] << 16))
					/ 8388606.0f);
		default:
			throw new UnsupportedOperationException("Unsupported Audio format!");
		}
	}
	/**
	 * Converts a floating point value into a little endian byte array
	 * @param f the floating point value between 0 and 1
	 * @param b the byte array (the length determines conversion bit depth)
	 */
	public static void floatToBytes(float f, byte[] b){
		if (f<0) throw new InvalidParameterException("Float value cannot be negative");
		if (f>1) throw new InvalidParameterException("Float value cannot be greater than 1");
		switch(b.length){
		case 1:
			f*=256;
			b[0] = (byte)f;
			return;
		case 2:
			f*=32768f;
			int x = (int)f;
			b[0] = (byte)(x & 0x00FF);
			int z = (x & 0xFF00);
			b[1] = (byte)(z>>8);
			return;
		case 3:
			f*=8388606.0f;
			int y = (int)f;
			b[0] = (byte)(y & 0x0000FF);
			b[1] = (byte)((y & 0x00FF00)>>8);
			b[2] = (byte)((y & 0xFF0000)>>16);
			return;
		}
		throw new UnsupportedOperationException("Byte array too long!");
	}
	/**
	 * Tests whether a file is a wave file
	 * @param f the file to test
	 * @return true or false
	 */
	public static boolean isWav(File f){
		if (f==null)
			return false;
		return f.getName().toLowerCase().endsWith(".wav");
	}
	/**
	 * Converts the given value in dB to linear gain
	 * @param dB 
	 * @return
	 */
	public static double dBtoLinear(double dB){
		return Math.pow(10, dB/10);
	}
	/**
	 * Converts the given linear gain value into dB
	 * @param lin
	 * @return
	 */
	public static double linearToDB(double lin){
		return 10 * Math.log10(lin);
	}
}

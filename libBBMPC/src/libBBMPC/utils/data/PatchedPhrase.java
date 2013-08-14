package libBBMPC.utils.data;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import libBBMPC.utils.MIDISequencer;


public class PatchedPhrase {
	public final Sample s;
	public final double bpm;
	public PatchedPhrase(Sample s, double bpm){
		this.s = s;
		this.bpm = bpm;
	}
	public PatchedPhrase(final InputStream in, final String name) throws FileFormatException, IOException, UnsupportedAudioFileException{
		if (!getString(in).equals("RIFF")) throw new FileFormatException("Not a RIFF file");
		final int dataSize = get4ByteLEInt(in);
		System.out.println("Size: "+ dataSize + " (+ 8)");
		if (!getString(in).equals("WAVE")) throw new FileFormatException("Not a WAVE file");
		if (!getString(in).equals("fmt ")) throw new FileFormatException("Did not find fmt chunk");
		final int fmtSize = get4ByteLEInt(in);
		final int compressionCode = get2ByteLEInt(in);
		if (compressionCode != 1) throw new UnsupportedAudioFileException();
		final int channels = get2ByteLEInt(in);
		final int sampleRate = get4ByteLEInt(in);
		//final int bytesPerSecond = 
		get4ByteLEInt(in);
		//final int blockAlign = 
		get2ByteLEInt(in);
		final int bitsPerSample = get2ByteLEInt(in);
		for (int i=0; i<fmtSize-16; i++){
			System.out.println("Extra fmt bytes");
			System.out.println(in.read());
		}
		final AudioFormat format = new AudioFormat(sampleRate, bitsPerSample, channels, true, false);
		if (!getString(in).equals("smpl")) throw new FileFormatException("Did not find smpl chunk");
		//final int smplSize = 
		get4ByteLEInt(in);
		final int manafacturer = get2ByteLEInt(in);
		//final int unknown = 
			get2ByteLEInt(in);//0x1000
		//final int product = 
			get4ByteLEInt(in);//94
		//final int period = 
			get4ByteLEInt(in);// 1/samplerate
		//final int baseNote = 
			get4ByteLEInt(in); // 60
		//final int pitchFraction = 
			get4ByteLEInt(in); // 0 
		//final int SMPTEFormat = 
			get4ByteLEInt(in); //0
		//final int SMPTEOffset = 
			get4ByteLEInt(in); // 0
		final int numLoops = get4ByteLEInt(in); // 1
		//final int samplerData = 
		get4ByteLEInt(in); // 20
		for (int i=0; i<numLoops; i++){
			System.out.println("Loops");
			System.out.println(get4ByteLEInt(in));
		}
		//final int unknown2 = 
		get4ByteLEInt(in); // 0
		//final int unknown3 = 
		get4ByteLEInt(in); // 0
		final int length = get4ByteLEInt(in); // length in samples
		//final int unknown4 = 
		get4ByteLEInt(in); // 0
		//final int unknown5 = 
		get4ByteLEInt(in); // 0
		//final int product2 = 
		get2ByteLEInt(in); // 0x1000
		//final int baseNote2 = 
		get2ByteLEInt(in); // base note 60
		//final int unknown6 = 
		get4ByteLEInt(in);//0
		//final int unknown7 = 
		get4ByteLEInt(in);//0
		//final int unknown8 = 
		get2ByteLEInt(in);//0
		//final int length2 = 
		get4ByteLEInt(in); // length in samples
		//final int unknown9 = 
		get2ByteLEInt(in); // 0
		if (manafacturer!=0x47) throw new FileFormatException("Not an Akai format file");
		if(!getString(in).equals("chsh")) throw new FileFormatException("Not a ChopShop file");
		final int chshSize = get4ByteLEInt(in);
		//final int unknown10 = 
		get4ByteLEInt(in); // 0x0001
		//final int numMarkers = 
		get4ByteLEInt(in); // 14
		//final int unknown11 = 
		get4ByteLEInt(in); // 1351
		final int[] ticks = new int[(chshSize-12)/8];
		final int[] markers = new int[ticks.length];
		for (int i=0; i<ticks.length; i++){
			ticks[i] = get4ByteLEInt(in);
			markers[i] = get4ByteLEInt(in);
		}
		if (!getString(in).equals("data")) throw new FileFormatException("Could not find data chunk");
		int size = get4ByteLEInt(in);
		final byte[] data = new byte[size];
		in.read(data);
		s = new Sample( data,format, name );
		for (int i:markers){
			if (i != 0)
				s.addMarker(new Marker(i));
		}
		final float beats = ticks[ticks.length-1]/96f;
		final float minutes = length*1f/sampleRate/60f;
		bpm = (beats/minutes);
	}
	public void save(final OutputStream out) throws IOException{
		final int[] markers = s.getMarkerSamples();
		final int[] ticks = MIDISequencer.getTicks(s, bpm);
		final int fmtSize=16;
		final int smplSize=80;
		final int chshSize = 12 + markers.length*4*2;
		final int size = s.getData().length + 4+ 8*3 + fmtSize + smplSize + chshSize;
		out.write("RIFF".getBytes());
		write4ByteLEInt(out, size);
		out.write("WAVE".getBytes());
		out.write("fmt ".getBytes());
		write4ByteLEInt(out,fmtSize);
		write2ByteLEInt(out,1);//PCM
		write2ByteLEInt(out, s.getFormat().getChannels());
		write4ByteLEInt(out, (int)s.getFormat().getFrameRate());
		write4ByteLEInt(out, (int)(s.getFormat().getFrameRate()* s.getFormat().getFrameSize()));
		write2ByteLEInt(out, 4);//block alignment;
		write2ByteLEInt(out, s.getFormat().getSampleSizeInBits());
		// extras
		out.write("smpl".getBytes());
		write4ByteLEInt(out,smplSize);
		write2ByteLEInt(out, 0x0047); // manafacturer
		write2ByteLEInt(out, 0x100); // mpc1000?? 
		write4ByteLEInt(out, 0x0000005E); // model
		write4ByteLEInt(out, (int)( 1000000000/s.getFormat().getSampleRate() )); // nanos per byte
		write4ByteLEInt(out, 60); // base note
		
		write4ByteLEInt(out,0); // padding
		write4ByteLEInt(out,0); // padding
		write4ByteLEInt(out,0); // padding
		write4ByteLEInt(out,1); // no idea!
		write4ByteLEInt(out, 20); // extra bytes for akai...
		write4ByteLEInt(out,0); // one zero loop
		write4ByteLEInt(out, 0);
		write4ByteLEInt(out, 0);
		write4ByteLEInt(out, s.getSamples());
		write4ByteLEInt(out, 0);
		write4ByteLEInt(out, 0);
		write2ByteLEInt(out, 0x0001);
		write2ByteLEInt(out, 60);
		write4ByteLEInt(out, 0);
		write4ByteLEInt(out, 0);
		write2ByteLEInt(out, 0);
		write4ByteLEInt(out, s.getSamples());
		write2ByteLEInt(out, 0);
		
		out.write("chsh".getBytes());
		write4ByteLEInt(out, chshSize);
		write4ByteLEInt(out, 1);//no idea maybe chop shop version?
		write4ByteLEInt(out, markers.length-1);
		write4ByteLEInt(out, 1351); // NO idea!
		
		for (int i=0; i<markers.length; i++){
			write4ByteLEInt(out, ticks[i]);
			write4ByteLEInt(out, markers[i]);
		}
		
		out.write("data".getBytes());
		write4ByteLEInt(out,s.getData().length);		
		out.write(s.getData());
	}
	private static void write4ByteLEInt(final OutputStream out, final int x) throws IOException{
		out.write((x&0x000000FF));
		out.write((x&0x0000FF00)>>8);
		out.write((x&0x00FF0000)>>16);
		out.write((x&0xFF000000)>>24);
	}
	private static void write2ByteLEInt(final OutputStream out, final int x) throws IOException{
		out.write((x&0x000000FF));
		out.write((x&0x0000FF00)>>8);
	}
	private static int get2ByteLEInt(final InputStream in) throws IOException{
		final int a = in.read();
		final int b = in.read()<<8;
		return a | b;
	}
	private static int get4ByteLEInt(final InputStream in) throws IOException{
		final int a = in.read();
		final int b = in.read()<<8;
		final int c = in.read()<<16;
		final int d = in.read()<<24;
		return d | b | c | a;
	}
	private static String getString(final InputStream in) throws IOException{
		return new String(""+(char)in.read() + (char)in.read() + (char)in.read() + (char)in.read());
	}
}

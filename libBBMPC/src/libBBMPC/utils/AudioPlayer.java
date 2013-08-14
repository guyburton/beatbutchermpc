package libBBMPC.utils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * The AudioPlayer class is a class allowing byte arrays of audio to be played. 
 * @author Guy
 *
 */
public class AudioPlayer {

	private static AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0f, 16, 2, 4, 44100.0f, false);
	private static SourceDataLine line;
	private static Thread thread;
	public static final int AUDIO_BUFFER_SIZE = 4096;
	
	private static volatile boolean loop, playing = false;
	private static byte[] data;
	private static volatile int start, end;
	private static volatile int loop_start = 0;
	
	private static boolean initialized = false;
	
	public static void open()
	{
		if (!initialized)
		{
			try {	
				line = AudioSystem.getSourceDataLine(format);
				line.open(format, format.getFrameSize()*AUDIO_BUFFER_SIZE);
				initialized = true;
				
				thread = new Thread(new Player());
				thread.start();
			} catch (LineUnavailableException e) {
				line = null;
				e.printStackTrace();
			}
		}

	}
	
	public static boolean isInitialized()
	{
		return initialized;
	}
	
	public static boolean isPlaying(){
		return playing;
	}
	
	/**
	 * This method temporarily pauses playback. Positional data is not stored.
	 * Does not shut thread or close audio line.
	 */
	public synchronized static void pause(){
		playing = false;
	}
	
	/**
	 * This method shuts the audio player
	 */
	public static void close()
	{
		initialized = false;
	}
	
	/**
	 * This method plays the currently stored audio data between the sample points given
	 * @param s the start point to play from in samples
	 * @param e the end point to stop playback in samples
	 */
	public synchronized static void play(final int s, final int e, final byte[] d, final AudioFormat f, boolean looping){
		if (s<0 || e > d.length/format.getFrameSize() ) {
			throw new RuntimeException("Start or end value out of range!");
		}
		if (data != null){
			System.out.println("Stopping playback before continuing");
			stop();
		}
		
		if (!f.matches(format))
			throw new RuntimeException("Format did not match audio player format, must convert!");
		
		data = d;
		start = s;
		loop_start = s;
		end = e;
		loop = looping;
		playing = true;
	}
	
	public static AudioFormat GetFormat()
	{
		return format;
	}
	
	/**
	 * Stops play back and unloads current data and destroys thread
	 */
	public synchronized static void stop(){
		playing = false;
		
		while(line != null && line.isRunning())
			Thread.yield();

		data = null;
		start = 0;
		end = 0;
	}
	
	private AudioPlayer(){};
	
	/**
	 * This class contains the logic for the audio player thread
	 * @author Guy
	 */
	private static class Player implements Runnable{
		public void run(){
			while(initialized){
				// do nothing while we have no data
				if(start >= end){
					if (loop && playing){
						System.out.println("Looped");
						start = loop_start;
					}
					else{
						playing = false;					
					}
				}			
				if(!playing){
					if (line.isRunning()){
						System.out.println("Stopped line");
						line.flush();
						line.stop();						
					}
				}
				else{
					if (!line.isRunning())
						line.start();
					ServiceBuffer();
				}
				Thread.yield();
			}
			line.close();
		}
		
		private synchronized static void ServiceBuffer()
		{
			final int fs = format.getFrameSize();
			int write = fs * line.available(); // maximum bytes to write		
			// if its worth our while writing then do it
			//if (write > 4096)
			{
				// if we would go off the end of the array, dont write as much
				if (start * fs + write >= end){
					write = (end - start) * fs;
				}
				
				if (write < 0)
				{
					System.out.println("Error!");
				}
				
				write = Math.min(write, AUDIO_BUFFER_SIZE);
				int bytes = line.write(data, start * fs, write);
				start += bytes / fs;
				//System.out.println("Wrote " + write + " bytes to device");
			}
		}
	}
}

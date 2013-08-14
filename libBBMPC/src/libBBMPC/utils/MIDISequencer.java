package libBBMPC.utils;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import libBBMPC.utils.data.Sample;


public class MIDISequencer {
	
	public static final int CHOP_MIDI_NOTE_BEGIN = 0;
	
	/**
	 * Contains the MIDI file types we can read and write
	 */
	public final static int[] types = javax.sound.midi.MidiSystem.getMidiFileTypes();
	private static final double PPQ = 96;
	//statically initialise MIDI system (can be shared between instances)
	private static Sequencer sequencer = null;
	static {
		try {
			sequencer = MidiSystem.getSequencer(false);
			System.out.println("Devices Available:");
			for (MidiDevice.Info i :MidiSystem.getMidiDeviceInfo())
				System.out.println(i.getName());
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a MIDI sequence with sequential notes occuring at 
	 * @param chops
	 * @param bpm
	 * @return
	 */
	public static Sequence getSeq(final Sample s, final double bpm){	
		try {
			final Sequence seq = new Sequence(Sequence.PPQ, 96);
			final Track t = seq.createTrack();
			final int velocity = 100;
			final double ticksPerSample = bpm * 96/60/s.getFormat().getSampleRate();
			final int[] chops = s.getMarkerSamples();
			for (int i=0; i<chops.length-1; i++){
				long ticks = (long)((double)chops[i]*ticksPerSample);
				ShortMessage sm = new ShortMessage();
				sm.setMessage(ShortMessage.NOTE_ON, CHOP_MIDI_NOTE_BEGIN+i, velocity);
				MidiEvent event = new MidiEvent(sm, ticks);
				t.add(event);
				System.out.println("note "+(CHOP_MIDI_NOTE_BEGIN+i)+" on@"+event.getTick());
			}
			for (int i=1; i<chops.length; i++){
				long ticks = (long)((double)chops[i]*ticksPerSample);
				//note off previous message
				ShortMessage sm = new ShortMessage();
				sm.setMessage(ShortMessage.NOTE_OFF, CHOP_MIDI_NOTE_BEGIN-1+i, 0);
				MidiEvent event = new MidiEvent(sm, ticks-1);
				t.add(event);
				System.out.println("noteoff@"+event.getTick());
			}
			return seq;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void playSeq(final double bpm, final double warp, final boolean repeat, final Sample sample){
		try{
			final Sequence s = getSeq(sample, bpm);
			final int[] chops = sample.getMarkerSamples();
			repeatAll = repeat;
			sequencer.setSequence(s);
			sequencer.open();
			sequencer.setLoopStartPoint(0);
			sequencer.setLoopEndPoint(s.getTickLength());
			sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
			sequencer.setTempoFactor((float)warp);
			sequencer.setTempoInBPM((float)bpm);
			sequencer.getTransmitter().setReceiver(new Receiver(){
				public void close(){}
				public void send(MidiMessage m, long ms){
					if (m.getStatus() == ShortMessage.NOTE_ON){
						int i = m.getMessage()[1]- CHOP_MIDI_NOTE_BEGIN;
						//System.out.println("Playing slice "+(i-1));
						int j = ((i+1)==chops.length)?0:i+1;
						AudioPlayer.play(chops[i],chops[j], sample.getData(), sample.getFormat(), repeatAll);
					}else if (m.getStatus() == ShortMessage.NOTE_OFF){
						AudioPlayer.pause();
					}
				}
			});
			sequencer.start();
		}catch(MidiUnavailableException e){
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} 		
	}
	
	private static boolean repeatAll;
	
	public static void SetRepeatAll(boolean r)
	{
		repeatAll = r;
	}
	public static void SetWarp(double warp)
	{
		sequencer.setTempoFactor((float)warp);
	}
	/**
	 * Does what it says on the tin
	 * @return
	 */
	public static boolean isPlaying(){
		return sequencer.isRunning();
	}
	
	public static void stopSeq(){
		if (sequencer.isOpen()){
			sequencer.stop();
			sequencer.close();
		}
	}
	// stop construction!
	private MIDISequencer(){}

	public static int[] getTicks(Sample s, double bpm) {
		final int[] chops = s.getMarkerSamples();
		final double ticksPerSample = bpm * PPQ/60/s.getFormat().getSampleRate();
		final int[] ticks = new int[chops.length];
		for (int i=0; i<chops.length-1; i++){
			ticks[i] = (int)((double)chops[i]*ticksPerSample);
		}
		return ticks;
	}
}

package com.bbmpc;


import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.swing.JOptionPane;

import libBBMPC.mvc.DefaultFunctionFactory;
import libBBMPC.mvc.Function;
import libBBMPC.mvc.FunctionFactory;
import libBBMPC.mvc.FunctionProvider;
import libBBMPC.mvc.ImmutableFunction;
import libBBMPC.mvc.NoStateChangeException;
import libBBMPC.mvc.UndoFunction;
import libBBMPC.utils.AudioFunctions;
import libBBMPC.utils.AudioPlayer;
import libBBMPC.utils.MIDISequencer;
import libBBMPC.utils.SamplePlayer;
import libBBMPC.utils.data.Bound;
import libBBMPC.utils.data.PatchedPhraseArgs;
import libBBMPC.utils.data.Sample;
import libBBMPC.utils.gui.WaveMarkerEditPanel;

/**
 * Provides extension editing capability to WavePanel
 * @author Guy
 *
 */
class AudioFunctionsProvider implements FunctionProvider{
	private FunctionFactory[] functions = new FunctionFactory[10];
	public AudioFunctionsProvider(final WaveMarkerEditPanel wavepanel, final SamplePlayer samplePlayer){
		functions[0] = new FunctionFactory(){
			private final static String name = "Reverse Slice";
			public Function getFunction(){
				return new UndoFunction(){
					Bound b;
					public String getName(){
						return name; 
					}
					public void execute(Object o) {
						b = wavepanel.getCurrentSliceBounds();
					}
					public void redo(){
						AudioFunctions.reverseSlice(wavepanel.getSample().getData(),
								wavepanel.getSample().getFormat(), b.start, b.end);
						wavepanel.repaint();
					}
					public void undo(){
						AudioFunctions.reverseSlice(wavepanel.getSample().getData(), wavepanel.getSample().getFormat(), b.start, b.end);
						wavepanel.repaint();
					}
				};
			}
			public String getFunctionName(){
				return name;
			}
		};
		
		functions[1] = new FunctionFactory(){
			private final static String name = "Convert To Mono";
			public Function getFunction(){
				return new UndoFunction(){
					int operation;
					Sample backup;
					Sample mono;
					public void execute(Object o) throws NoStateChangeException{
						if (wavepanel.getSample() == null || wavepanel.getSample().getFormat().getChannels()==1){
							JOptionPane.showMessageDialog(null,"Cannot perform operation on a mono sample!");
							throw new NoStateChangeException();
						}	
						operation = JOptionPane.showOptionDialog(
										null, 
										"Select a source for mono clip", 
										MainWindow.APP_NAME,
										JOptionPane.CLOSED_OPTION,
										JOptionPane.INFORMATION_MESSAGE, 
										null,
										AudioFunctions.MONO_CONVERSION_SOURCES, 
										AudioFunctions.MONO_CONVERSION_SOURCES[2]);
						backup = wavepanel.getSample();
						final AudioFormat f = backup.getFormat();
						final byte[] data = backup.getData();		
						if (operation != 3){
							byte[] d = new byte[data.length/2];
							AudioFormat f2 = AudioFunctions.toMono(operation, data, d, f);
							mono = new Sample(d,f2, backup.getName());
							backup.copyMarkersTo(mono);
							wavepanel.setSample(mono);		
						}
					}
					public void redo(){
						wavepanel.setSample(mono);
						wavepanel.repaint();
					}
					public void undo(){
						wavepanel.setSample(backup);
						wavepanel.repaint();
					}
					public String getName(){
						return name; 
					}
				};
			}	
			public String getFunctionName(){
				return name;
			}
		};
		functions[2] = new FunctionFactory(){
			private final static String name = "Normalize";
			public Function getFunction(){
				return new UndoFunction(){
					byte[] data;
					public void execute(Object o){
						byte[] data = wavepanel.getSample().getData();
						data = Arrays.copyOf(data,data.length);
					}
					public void redo(){
						AudioFunctions.normalize(wavepanel.getSample());
						wavepanel.repaint();
					}
					public void undo(){
						final byte[] temp = wavepanel.getSample().getData();
						for (int i=0; i<data.length; i++){
							temp[i] = data[i];
						}
						wavepanel.repaint();
					}
					public String getName(){
						return name;
					}
				};
			}
			public String getFunctionName(){
				return name;
			}
		};
		/**
		 * Reverses the sample. Should be an identity function so undo is simple
		 */
		functions[3] = new FunctionFactory(){
			private final static String name = "Reverse Sample";
			public Function getFunction(){
				return new UndoFunction(){
					public void execute(Object o) {
					}
					public void redo(){
						AudioFunctions.reverseSlice(wavepanel.getSample().getData(),
													wavepanel.getSample().getFormat(), 0, 
													wavepanel.getSample().getSamples());
						wavepanel.repaint();
					}
					public void undo() {
						redo();						
					}
					public String getName(){
						return name; 
					}
				};
			}
			public String getFunctionName(){
				return name;
			}
		};
		functions[4] = new FunctionFactory(){
			private final static String name = "Reverse All Slices";
			public Function getFunction(){
				return new UndoFunction(){
					public void execute(Object o){}
					public void redo(){
						wavepanel.setCurrentMarker(null);
						wavepanel.selectSlice(0);
						for (int i=0; i<wavepanel.getSample().getNumMarkers()+1;i++){
							Bound b = wavepanel.getCurrentSliceBounds();
							AudioFunctions.reverseSlice(wavepanel.getSample().getData(), 
														wavepanel.getSample().getFormat(), 
														b.start, b.end);
							wavepanel.nextSlice();
						}
						wavepanel.repaint();
					}
					public void undo(){
						redo();
					}
					public String getName(){
						return name;
					}
				};
			}
			public String getFunctionName(){
				return name;
			}
		};
		functions[5] = new FunctionFactory(){
			private final static String name = "Delete Slice";
			public Function getFunction(){
				return new UndoFunction(){
					Bound b;
					byte[] data;
					public void execute(Object o){
						b= wavepanel.getCurrentSliceBounds();
					}
					public void redo(){
						Sample s = wavepanel.getSample();
						data = Arrays.copyOfRange(s.getData(), 
								b.start * s.getFormat().getFrameSize(), 
								b.end * s.getFormat().getFrameSize());
						wavepanel.setSample(s.removeSamples(b.start, b.end));
					}
					public void undo(){
						Sample s = wavepanel.getSample();
						byte[] d = new byte[data.length+s.getData().length];
						for (int i=0; i< b.start* s.getFormat().getFrameSize(); i++){
							d[i] = s.getData()[i];
						}
						int j=0;
						for (int i=b.start* s.getFormat().getFrameSize(); 
							i< b.end* s.getFormat().getFrameSize(); i++){
							d[i] = data[j++];
						}
						for (int i=b.end* s.getFormat().getFrameSize(); i<d.length; i++){
							d[i] = s.getData()[i-data.length];
						}
						Sample sa = new Sample(d, s.getFormat(), s.getName());
						s.copyMarkersTo(sa);
						wavepanel.setSample(sa);
					}
					public String getName(){
						return name;
					}
				};
			}
			public String getFunctionName(){
				return name;
			}
		};
		
		functions [6] = new DefaultFunctionFactory(new ImmutableFunction(){
			public void execute(Object o){
				PatchedPhraseArgs p = (PatchedPhraseArgs)o;
				if (p.s == null)
					return;
				samplePlayer.setSample(p.s);
				if (!MIDISequencer.isPlaying()){
					if (AudioPlayer.isPlaying())
						AudioPlayer.stop();
					System.out.println("Playing patched phrase at 100 bpm, warp factor " + p.bpm);
					MIDISequencer.playSeq(p.bpm, 
										  p.warp, 
										  p.loopSamples, 
										  p.s);
				}else{
					MIDISequencer.stopSeq();
					AudioPlayer.stop();
				}
			}
			public String getName(){
				return "Play Patched Phrase";
			}
		});
		
		functions[7] = new DefaultFunctionFactory(new ImmutableFunction(){
			public void execute(Object o) {
				cleanAudio();
				if (wavepanel.getSample() != null)
				{
					samplePlayer.setSample(wavepanel.getSample());
					samplePlayer.playSlice(wavepanel.getCurrentMarker(),false); // TODO loop
				}
			}
			public String getName(){
				return "Play Current Slice";
			}
		});
		functions[8] = new DefaultFunctionFactory(new ImmutableFunction(){
			public void execute(Object o) {
				cleanAudio();
				samplePlayer.play(false); // TODO loop
			}
			public String getName(){
				return "Play Sample";
			}
		});
		functions[9] = new DefaultFunctionFactory(new ImmutableFunction(){
			public void execute(Object o){
				cleanAudio();
			}
			public String getName(){
				return "Stop";
			}
		});
	}

	public boolean contains(String s) {
		for (FunctionFactory c: functions){
			if (c.getFunctionName().equals(s))
				return true;
		}
		return false;
	}

	public Function getFunction(String s) {
		for (FunctionFactory c: functions){
			if (c.getFunctionName().equals(s)){
				return c.getFunction();
			}
		}
		return null;
	}
	public String[] getFunctionNames() {
		String[] s = new String[functions.length];
		int i=0;
		for (FunctionFactory f: functions){
			s[i++] = f.getFunctionName();
		}
		return s;
	}
	
	private static void cleanAudio()
	{
		if (MIDISequencer.isPlaying())
			MIDISequencer.stopSeq();
		if (AudioPlayer.isPlaying())
			AudioPlayer.stop();
	}
}

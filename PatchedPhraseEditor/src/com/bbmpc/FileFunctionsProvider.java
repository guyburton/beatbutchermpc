package com.bbmpc;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

import libBBMPC.mvc.DefaultFunctionFactory;
import libBBMPC.mvc.Function;
import libBBMPC.mvc.FunctionFactory;
import libBBMPC.mvc.FunctionProvider;
import libBBMPC.mvc.ImmutableFunction;
import libBBMPC.utils.AudioFunctions;
import libBBMPC.utils.FileManager;
import libBBMPC.utils.data.FileFormatException;
import libBBMPC.utils.data.PatchedPhrase;
import libBBMPC.utils.data.Sample;

class FileFunctionsProvider implements FunctionProvider {
	private final List<FunctionFactory> functions = new ArrayList<FunctionFactory>();
	private Sample currentSample;
	
	public interface SampleListener
	{
		void SampleLoaded(Sample s, double tempo);
		void SampleClosed();
	}
	
	private List<SampleListener> listeners = new ArrayList<SampleListener>();
	
	public void AddSampleListener(SampleListener l)
	{
		listeners.add(l);
	}
	
	public FileFunctionsProvider(){
		AddSampleListener(new SampleListener(){
			@Override
			public void SampleClosed() {
				currentSample = null;
			}
			@Override
			public void SampleLoaded(Sample s, double tempo) {
				currentSample = s;
			}			
		});
		
		functions.add(new DefaultFunctionFactory(new Function(){
			public void execute(Object param){
				File f = FileManager.chooseOpen(FileManager.wavFilter);
				if (f==null || !AudioFunctions.isWav(f)) return;
				if (currentSample != null)
					for(SampleListener l: listeners)
						l.SampleClosed();
				
				PatchedPhrase p = null;
				InputStream stream = null;
				try {
					stream = new FileInputStream(f);
					p = new PatchedPhrase(stream, f.getName());
					for(SampleListener l: listeners)
						l.SampleLoaded(p.s, p.bpm);
					return;
				} catch (FileNotFoundException e) {
					return;
				} catch (FileFormatException e) {
				} catch (IOException e) {
				} catch (UnsupportedAudioFileException e) {
				}
				finally
				{
					if (stream != null)
						try {
							stream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
				Sample sa = loadWAV(f);
				for(SampleListener l: listeners)
					l.SampleLoaded(sa, 100.0d); // TODO work out bpm
			}
			public String getName(){
				return "Load Sample";
			}
		}));
		
		functions.add(new DefaultFunctionFactory(new ImmutableFunction(){
			public void execute(Object param){
				double tempo = Double.parseDouble(param.toString());
				if (currentSample == null) return;
				
				File f = FileManager.chooseSave();
				if (f==null) return;
				try {
					PatchedPhrase p = new PatchedPhrase(currentSample, tempo);
					p.save( new FileOutputStream(f) );
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(null, "There was an error saving the file");
					e.printStackTrace();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "There was an error saving the file");
					e.printStackTrace();
				}
			}
			public String getName(){
				return "Save Patched Phrase";
			}
		}));

		/**
		 * This function shows an open dialog to choose a folder to output a batch of waves to
		 * It then adds the saved files to the sample pool
		 * @param fm FunctionManager object
		 * @return array of the file objects where the samples were saved
		 */
		functions.add(new DefaultFunctionFactory(new ImmutableFunction(){
			public String getName(){
				return "Save All Slices";
			}
			public void execute(Object param){
				if (currentSample ==null) return;
				File f = FileManager.chooseDirectorySave();
				if (f==null) return;
				saveAllSlices(f, currentSample);
			}
		}));
		
		functions.add(new DefaultFunctionFactory(new Function(){
			public String getName(){
				return "Close";
			}
			public void execute(Object param){
				for(SampleListener l: listeners)
					l.SampleClosed();
			}
		}));
		
		functions.add(new DefaultFunctionFactory(new ImmutableFunction(){
			public String getName(){
				return "Save Current Slice";
			}
			public void execute(Object param){
				int[] p = (int[]) param;
				int start = p[0], end = p[1];
				if (currentSample ==null)
					return;
				File f= FileManager.chooseSave();
				if (f==null)return;
				f = new File(f.getAbsolutePath()+".wav");
				saveCurrentSlice(f, currentSample, start, end);
			}
		}));
		functions.add( new DefaultFunctionFactory(new ImmutableFunction(){
			public void execute(Object o){
				JOptionPane.showMessageDialog(null, "BeatButcherMPC Patched Phrase Editor was written by Guy Burton.\nIt is available freely for download at http://www.beatbutchermpc.com.\nFeel free to get in touch: guy@beatbutchermpc.com\nPlease consult licence document for more details on redistribution.\n", "BeatButcherMPC Patched Phrase Editor", JOptionPane.INFORMATION_MESSAGE );
			}
			public String getName(){
				return "About";
			}
		}));
	}
	
	/**
	 * This method returns the specified File as a Sample 
	 * @param f
	 */
	public static Sample loadWAV(File f){
		try {	
			Sample s = new Sample(f);
			return s;
		} catch (UnsupportedAudioFileException e1) {
			JOptionPane.showMessageDialog(null, "The file format is not supported!");
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "There was an error when opening the file");
			e1.printStackTrace();
			return null;
		}
	}
	
	/**
	 * This method saves all of the current slices to wave files
	 * @param f the directory to fill
	 * @param fm FunctionManager object for status updates
	 * @return an array of the stored files
	 */
	public static File[] saveAllSlices(File f, Sample currentSample){
		final Sample s = currentSample;
		File[] files = new File[s.getNumMarkers()+1];
		for (int i=0; i<s.getNumMarkers(); i++){
			files[i] = new File(f.getPath()+"\\"+s.getName()+"("+i+")"+".wav");
			saveCurrentSlice(files[i], s, s.getMarkerSamples()[i], s.getMarkerSamples()[i+1]);
		}
		return files;
	}
	/**
	 * This method saves the currently select slice of a sample to a specified file
	 * @param f the file to be saved to
	 * @param s the sample to be saved
	 */
	public static void saveCurrentSlice(File f, Sample currentSample, int start, int end){
		Sample sa = currentSample.subSample(start, end, "");
		try{
			FileManager.saveSample(f,sa);
		}catch(IOException e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "There was an error saving the sample");
		}
	}
	
	public boolean contains(String s) {
		for (FunctionFactory f:functions){
			if (f.getFunctionName().equals(s))
				return true;
		}
		return false;
	}
	public String[] getFunctionNames() {
		String[] s = new String[functions.size()];
		int i=0;
		for (FunctionFactory f: functions){
			s[i++] = f.getFunctionName();
		}
		return s;
	}
	public Function getFunction(String s) {
		for (FunctionFactory f:functions){
			if (f.getFunctionName().equals(s))
				return f.getFunction();
		}
		return null;
	}

}

package libBBMPC.utils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import libBBMPC.utils.data.Program;
import libBBMPC.utils.data.Sample;





public class FileManager {
	/**
	 * This method saves the sample to a file
	 * @param f the output file
	 * @param sa the sample to be saved
	 * @throws IOException 
	 */
	public static void saveSample(File f, Sample sa) throws IOException{
		ByteArrayInputStream bis = new ByteArrayInputStream(sa.getData());
		AudioInputStream ais = new AudioInputStream(bis,sa.getFormat(),sa.getSamples());
		AudioSystem.write(ais,AudioFileFormat.Type.WAVE, f);
		ais.close();
		bis.close();
	}
	/**
	 * Saves the program to the specified file
	 * @param f
	 * @param p
	 * @throws IOException 
	 */
	public static void savePGM(File f, Program p) throws IOException {
		FileOutputStream out = new FileOutputStream(f);
		p.output(out);
		out.close();
	}
	public static FileFilter wavFilter = new FileFilter(){
		public boolean accept(File f){
			if (AudioFunctions.isWav(f) || f.isDirectory())
				return true;
			return false;
		}
		public String getDescription(){
			return "*.wav (Wave File)";
		}
	};
	public static File chooseOpen(FileFilter f){
		JFileChooser chooser = new JFileChooser("Choose a file");
		chooser.setFileFilter(f);
		int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	        File fx = chooser.getSelectedFile();
	        if (f.accept(fx)){
		        return fx;
	        }
	    }
	    return null;
	}
	public static File chooseDirectorySave(){
		JFileChooser chooser = new JFileChooser("Choose a directory");

	    chooser.setCurrentDirectory(null);
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    //
	    // disable the "All files" option.
	    //
	    chooser.setAcceptAllFileFilterUsed(false);
		int returnVal = chooser.showSaveDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	        File fx = chooser.getSelectedFile();
	        return fx;
	    }
	    return null;
	}
	
	public static File chooseSave()
	{
		JFileChooser chooser = new JFileChooser("Save Patched Phrase");
		chooser.setFileFilter(wavFilter);
		int returnVal = chooser.showSaveDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	        File fx = chooser.getSelectedFile();
	        return fx;
	    }
	    return null;
	}
	
	public static FileFilter midiFilter = new FileFilter(){
		public boolean accept(File f){
			if (f.getName().toLowerCase().endsWith(".mid") || f.isDirectory())
				return true;
			return false;
		}
		public String getDescription(){
			return "*.mid (MIDI data)";
		}
	};
	public static FileFilter batchFilter = new FileFilter(){
		public boolean accept(File f){
			if (f.isDirectory())
				return true;
			return false;
		}
		public String getDescription(){
			return "Folder";
		}
	};
	public static FileFilter pgmFilter = new FileFilter(){
		public boolean accept(File f){
			if (f.getName().toLowerCase().endsWith(".pgm") || f.isDirectory())
				return true;
			return false;
		}
		public String getDescription(){
			return "*.pgm (Akai Program file)";
		}
	};
}

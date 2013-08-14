package libBBMPC.utils.data;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Class Sample contains all audio data, format and name
 * Sample also contains methods and data structures to represent a series of slice points
 * @author Guy Burton
 */
public class Sample{
	private final Vector<Marker> markers = new Vector<Marker>();
	private String name;
	private final byte[] data;
	private final AudioFormat format;
	/**
	 * Creates a new sample from a file on disk
	 * @param f File to load sample data from
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public Sample(File f) throws IOException, UnsupportedAudioFileException{
		name = f.getName().substring(0, f.getName().length()-4);
		
		AudioInputStream stream = AudioSystem.getAudioInputStream(f);
		format = stream.getFormat();
				
		if (format.getFrameRate()!= 44100 || format.getSampleSizeInBits() != 16)
		{
			stream.close();
			throw new UnsupportedAudioFileException();			
		}
		int l = (int)(stream.getFrameLength() * format.getFrameSize());
		data = new byte[l];
		stream.read(data);
		stream.close();
	}
	/**
	 * Create sample from raw data
	 * @param data the audio data array
	 * @param format the format of the audio array
	 * @param name the name of the new sample 
	 */
	public Sample(byte[] data, AudioFormat format, String name){
		this.data = data;
		this.name = name;
		if (name.toLowerCase().endsWith(".wav"))
			name = name.substring(0, name.length()-4);
		this.format = format;
	}
	/**
	 * Performs a shallow copy of all markers from this sample to another
	 * Removes all markers past end of new sample
	 * @param s the sample to copy markers to
	 */
	public void copyMarkersTo(Sample s){
		for (Marker m: markers){
			if (m.samples < s.getSamples())
				s.addMarker(m);
		}
	}
	
	/**
	 * Creates a new sample from a section of existing sample
	 * @param start
	 * @param end
	 * @param suffix the addition to the name of the sample
	 * @return
	 */
	public Sample subSample(int start, int end, String suffix){
		return new Sample(getSlice(start,end),format, getName()+suffix);
	}
	/**
	 * Removes a section of audio data from the data array
	 * @param s start point in samples
	 * @param e end point in samples
	 * @return
	 */
	public Sample removeSamples(int s, int e){
		if (e<s)
			throw new RuntimeException("End cannot be larger than start!");
		int start = s * format.getFrameSize();
		int end = e * format.getFrameSize();
		byte[] d = new byte[data.length - (end - start)];
		for (int i=0; i<start; i++){
			d[i] = data[i];
		}
		for (int i=end; i<data.length; i++){
			d[i-end+start] = data[i];
		}
		Sample n = new Sample(d, format, name);
		int len = end - start;
		for (Marker m: markers){
			if (m.samples < s){
				n.addMarker(m);
			}
			else if (m.samples >= e){
				m.samples -= len/format.getFrameSize();
				n.addMarker(m);
			}
		}
		return n;
	}
	public String toString(){
		return name+".wav";
	}
	public int getSamples(){
		return data.length/format.getFrameSize();
	}
	public AudioFormat getFormat(){
		return format;
	}
	public byte[] getData(){
		return data;
	}
	public String getName(){
		return name;
	}
	public void setName(String s){
		name = s;
	}
	//public Vector<Marker> getMarkers(){
	//	return markers;
	//}
	public int[] getMarkerSamples(){
		int[] m = new int[markers.size()+2];
		m[0] = 0;
		for (int i=0; i<markers.size(); i++){
			m[i+1] = markers.get(i).samples;
		}
		m[m.length-1] = getSamples();
		return m;
	}

	public void clearMarkers(){
		markers.clear();
	}
	public void removeMarker(Marker m){
		markers.remove(m);
	}
	public boolean addMarker(Marker m){
		for(Marker i:markers){
			if (i.samples == m.samples)
				return false;
		}
		markers.add(m);
		Collections.sort(markers);
		return true;
	}
	public int getNumMarkers(){
		return markers.size();
	}
	
	public boolean equals(Sample s){
		return(s.getName().equals(getName()));
	}

	public byte[] getSlice(int start, int end){
		byte[] d = new byte[(int)(end-start) * format.getFrameSize()];
		for (int j=0; j<d.length; j++){
			d[j] = data[start*format.getFrameSize()+j];
		}
		return d;
	}	
	public void moveMarker(final Marker m, final int l) {
		m.samples = l;
		Collections.sort(markers);
	}
	/**
	 * Returns the nearest marker to the sample point given or returns null
	 * @param samp the sample point to search from
	 * @param tol the maximum distance a marker may be
	 * @return
	 */
	public Marker nearestMarker(int samp, double tol){
		// this method could be a lot more efficient but how many markers are there likely to be!
		int min = getSamples();
		Marker best = null;
		int j=0;
		for (Marker m: markers){
			final int x = Math.abs(m.samples-samp);
			if (x<min){
				min=x;
				best = m;
			}
			j++;
		}
		if (best!=null && Math.abs(best.samples-samp)<(tol*getSamples()))
			return best;
		return null;
	}
	public int getLastMarkerID() {
		if (markers.size()==0) return 0;
		return markers.lastElement().ID;
	}
	public Vector<Marker> getMarkers() {
		return markers;
	}
	/**
	 * Returns the marker which is the first on the left of the sample value
	 * Can be used to select the slice which a sample resides in
	 * @param x the sample value
	 * @return
	 */
	public Marker nextMarkerBeforeSample(int x) {
		// remember it is a sorted list!
		Marker r = null;
		for (Marker m: markers){
			int temp = m.samples-x;
			if (temp<0){
				r = m;
			}else{
				return r;
			}
		}
		return r;
	}
	public boolean equals(Object s){
		if ( !(s instanceof Sample) )
			return false;
		return equals((Sample)s);
	}
	public void deleteMarker(Marker m) {
		markers.remove(m);
	}
}

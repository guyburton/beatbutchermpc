package libBBMPC.utils;

import java.util.Vector;

import libBBMPC.utils.data.Marker;
import libBBMPC.utils.data.Sample;


/**
 * This class handles the simple logic of playing the correct section of a sample and outputting the 
 * bytes to the AudioPlayer. This decouples the Sample class from the AudioPlayer in case we want to re-use.
 * @author Guy
 *
 */
public class SamplePlayer {
	private Sample sample;
	public void setSample(Sample s)
	{
		this.sample = s;
		AudioPlayer.open();
	}
	public void play(boolean loop){
		playSlice(0, sample.getSamples(),loop);
	}
    private synchronized void playSlice(final int start, final int end, boolean loop){
		if (sample.getData()==null) return;
		AudioPlayer.play(start, end, sample.getData(),sample.getFormat(), loop);
	}
	public void playSlice(final int marker, final boolean loop){
		Vector<Marker> markers = sample.getMarkers();
		if (marker==-1){
			int end = sample.getSamples();
			if (markers.size()>0)
				end = markers.firstElement().samples; 
			playSlice(0, end, loop);
			return;
		}
		final Marker x1 = markers.get(marker);
		if (x1 == markers.lastElement()){
			playSlice(x1.samples, sample.getSamples(), loop);
			return;
		}
		final Marker x2 = markers.get(marker+1);
		if (x1 == null) 
			playSlice(0, x2.samples, loop);
		playSlice(x1.samples, x2.samples, loop);
	}
	public void playSlice(Marker m, boolean loop){
		if (m!=null){ 
			playSlice(sample.getMarkers().indexOf(m), loop);
		}
		else
		{
			playSlice(-1, loop);
		}
	}
}

package libBBMPC.utils.gui;

import java.awt.Color;
import java.awt.Graphics;
import libBBMPC.utils.data.Marker;
import libBBMPC.utils.data.Sample;


/**
 * Adds functionality to the WavePanel by displaying Samples instead of raw audio
 * Also displays markers stored in Sample and allows selection or custom functions
 * on mouse clicks
 * Note: A Marker is a point measured in samples along the chronological timeline of the sample
 * A Slice is a period of time bounded by two Marker's
 * @author Guy
 *
 */
public class WaveMarkerPanel extends WavePanel {
	private final static long serialVersionUID = 72343l;
	protected Sample s;
	/**
	 * Gets the currently displayed sample
	 * @return
	 */
	public Sample getSample(){
		return s;
	}
	/**
	 * Sets the sample to be displayed by the panel and refreshes
	 * @param s
	 */
	public void setSample(final Sample s){
		this.s = s;
		if (s != null)
		{
			super.data=s.getData();
			super.format=s.getFormat();
		}
		else	
		{
			super.data = null;
			super.format = null;
		}
		repaint();
	}	
	
	public void paint(Graphics g){
		super.paint(g);
		paintMarkers(g);
	}
	public void paintMarkers(Graphics g){
		if (s==null) return;
		for (Marker m: s.getMarkers()){
			g.setColor(Color.GREEN);
			int y = (int)(( m.samples-offset) / getSamplesPerPixel());
			g.drawLine(y, 0, y, getHeight());
			g.drawString(m.ID+"", y+1, g.getFont().getSize());
		}
	}
	public boolean addMarker(int x){
		if (s==null) return false;
		if (x > s.getSamples())	return false;
		Marker m = new Marker(x);
		return s.addMarker(m);		
	}
	public void deleteMarkers(Marker[] ms){
		Sample s = getSample();
		if (s==null)
			return;
		for (Marker m: ms)
			s.removeMarker(m);
		repaint();
	}
}

package libBBMPC.utils.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.swing.JPanel;

import libBBMPC.mvc.DefaultFunctionFactory;
import libBBMPC.mvc.Function;
import libBBMPC.mvc.FunctionFactory;
import libBBMPC.mvc.FunctionProvider;
import libBBMPC.mvc.ImmutableFunction;
import libBBMPC.mvc.NoStateChangeException;
import libBBMPC.utils.AudioFunctions;

public class WavePanel extends JPanel implements FunctionProvider {
	private final static long serialVersionUID = 4l;
	private int highlight_x, highlight_x2;
	public float hZoom=1;
	private float vZoom=1;
	public int offset=0;
	protected byte[] data;
	protected AudioFormat format;
	private Vector<FunctionFactory> functions = new Vector<FunctionFactory>();
	private int mouseXPos;
	private Font font;
	private final DecimalFormat df = new DecimalFormat("#.##");
	public WavePanel(){
		super();
		super.addMouseMotionListener(new MouseMotionListener(){
			public void mouseMoved(MouseEvent e){
				mouseXPos = e.getX();
			}
			public void mouseDragged(MouseEvent arg0) {}
		});
		super.setPreferredSize(new Dimension(super.getWidth(),100));
		// this function keeps the mouse position at the same ratio across screen whilst zooming
		functions.add(new DefaultFunctionFactory(new ImmutableFunction(){
			public void execute(Object o) throws NoStateChangeException {
				if (data == null)
					return;
				if (getSamplesPerPixel()>=1){
				//	double onScreenOffsetSamples = samplesAtPos - offset;
					//double oldZoom = hZoom;
					// take away samples from on screen offset according to new zoom value so that
					// it is the same sample at mouseXPos
					
					int samplesAtPos = getSamplesFromPixel(mouseXPos);
					
					hZoom = hZoom * 1.5f;
					if (hZoom > 128)
						hZoom = 128;

					offset = samplesAtPos - (int)(mouseXPos * getSamplesPerPixel());
					
					
				}else{
					hZoom = data.length/format.getFrameSize() / getWidth();
				}
				repaint();
			}
			public String getName() {
				return "Zoom In Horizontal";
			}
		}));
		functions.add(new DefaultFunctionFactory(new ImmutableFunction(){
			public void execute(Object o) throws NoStateChangeException {
				hZoom /= 1.5f;
				if (hZoom < 1.0f){
					hZoom = 1.0f;
				}
				offset -= (int)(getSamplesPerPixel() * getWidth() / hZoom);
				if (offset < 0)
					offset = 0;
				repaint();
			}
			public String getName() {
				return "Zoom Out Horizontal";
			}
		}));
		functions.add(new DefaultFunctionFactory(new ImmutableFunction(){
			public void execute(Object o) throws NoStateChangeException {
				vZoom *= 1.5f;
				if (vZoom > 32.0f)
					vZoom = 32.0f;
				repaint();
			}
			public String getName() {
				return "Zoom In Vertical";
			}
		}));
		functions.add(new DefaultFunctionFactory(new ImmutableFunction(){
			public void execute(Object o) throws NoStateChangeException {
				vZoom /= 1.5f;
				if (vZoom < 0.125f)
					vZoom = 0.125f;
				repaint();
			}
			public String getName() {
				return "Zoom Out Vertical";
			}
		}));
		functions.add(new DefaultFunctionFactory(new ImmutableFunction(){
			public void execute(Object o) throws NoStateChangeException {
				if (data==null) return;
				offset+=getSamplesPerPixel()*16; // TODO really?
				if (offset > (data.length/format.getFrameSize()) - getSamplesPerPixel()*getWidth()){
					offset = (int) ((data.length/format.getFrameSize() - getSamplesPerPixel()*getWidth()));
				}
				repaint();
			}
			public String getName() {
				return "Scroll Right";
			}
		}));
		functions.add(new DefaultFunctionFactory(new ImmutableFunction(){
			public void execute(Object o) throws NoStateChangeException {
				if (data == null) return;
				offset -= getSamplesPerPixel()*16;
				if (offset < 0)
					offset = 0;
				repaint();
			}
			public String getName() {
				return "Scroll Left";
			}
		}));
		super.addMouseWheelListener(new MouseWheelListener(){
			public void mouseWheelMoved(MouseWheelEvent e){
				repaint();
			}
		});
	}
	
	public void paint(Graphics g){
		if (font == null)
			font = g.getFont();
		else
			g.setFont(font);
		g.setColor(this.getBackground());
		g.fillRect(0, 0, super.getWidth(), super.getHeight());
		if(data==null)
			return;
		
		g.setColor(Color.GRAY);
		g.fillRect(highlight_x, 0, highlight_x2-highlight_x, super.getHeight());

		if (data == null)
			return;
		g.setColor(Color.BLUE);
		// we are going to use width * channels * bits per sample of bytes
		byte[] b = new byte[format.getFrameSize()/format.getChannels()];
		int samplesPerPixel = (int)getSamplesPerPixel();
		int x=0;
		for (int i=0; i< data.length; ){
			// check we arent going to run out of samples
			if ( ((i+format.getFrameSize()-1)*samplesPerPixel + (offset+samplesPerPixel)*format.getFrameSize() ) > data.length){
				break;
			}		
			for (int channel=0; channel<format.getChannels(); channel++){
				double d=0;
				int start = (int)(i * samplesPerPixel + offset * format.getFrameSize());
				i+=b.length;
				// calculate mean sample over hidden samples
				for (int k=0; k<samplesPerPixel*format.getFrameSize(); k+=format.getFrameSize()){
					for(int m=0; m<b.length; m++){
						b[m] = data[start+k+m];
					}
					d+= AudioFunctions.bytesToFloat(b);
				}
				d/=samplesPerPixel;
				d *= super.getHeight()/2/format.getChannels(); // multiply -1.0 to 1.0 float over panel height per channel
				d *= vZoom; //apply zoom factor 
				d = Math.min(d, (float)super.getHeight()/4 ); // make sure its going over bounds
				g.drawLine(x, (2*channel+1)*super.getHeight()/2/format.getChannels()+(int) d, x, (2*channel+1)*super.getHeight()/2/format.getChannels()-(int)d);
			}
			x++;
		}
		g.setColor(Color.YELLOW);
		g.drawString("VZoom x"+df.format(vZoom), getWidth()-80, getHeight()-20);
		g.drawString("HZoom x"+df.format(hZoom), getWidth()-80, getHeight()-5);
	}
	protected float getSamplesPerPixel(){
		if (data==null)
			return 0;
		return data.length/format.getFrameSize() / getWidth() / hZoom;
	}
	public void setHighlight(int x1, int x2){
		highlight_x = x1;
		highlight_x2 = x2;
	}
	public int getSamplesFromPixel(int px){
		return (int)(px*getSamplesPerPixel()+offset);
	}
	/**
	 * {@inheritDoc}
	 */
	public boolean contains(String s) {
		for (FunctionFactory ff: functions){
			if (ff.getFunctionName().equals(s))
				return true;
		}
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	public Function getFunction(String s) {
		for (FunctionFactory ff: functions){
			if (ff.getFunctionName().equals(s))
				return ff.getFunction();
		}
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	public String[] getFunctionNames() {
		String[] s = new String[functions .size()];
		int i=0;
		for (FunctionFactory f: functions){
			s[i++] = f.getFunctionName();
		}
		return s;
	}
}

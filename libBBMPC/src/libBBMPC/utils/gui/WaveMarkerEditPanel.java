package libBBMPC.utils.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import libBBMPC.mvc.DefaultFunctionFactory;
import libBBMPC.mvc.Function;
import libBBMPC.mvc.FunctionFactory;
import libBBMPC.mvc.FunctionManager;
import libBBMPC.mvc.FunctionProvider;
import libBBMPC.mvc.ImmutableFunction;
import libBBMPC.mvc.NoStateChangeException;
import libBBMPC.mvc.UndoFunction;
import libBBMPC.utils.AudioPlayer;
import libBBMPC.utils.SamplePlayer;
import libBBMPC.utils.data.Bound;
import libBBMPC.utils.data.Marker;
import libBBMPC.utils.data.Sample;
/**
 * Add to WavePanel the concept of a current marker and adds several edit operation modes
 * @author Guy
 */
public class WaveMarkerEditPanel extends WaveMarkerPanel implements FunctionProvider{
	
	/**
	 * Value a percentage of the total number of samples (between 0d and 1.0)
	 */
	public static final double MARKER_CLICK_TOLERANCE = 0.05;
	
	private static final long serialVersionUID = 6985610209596069894L;
	private Marker currentMarker=null;
	
	public static enum MODE {
		ADD, SELECT, MOVE, DELETE;
	}
	public interface ModeChangeListener
	{
		public void WavePanelModeChanged(MODE oldMode, MODE newMode);
	}
	private MODE mode = MODE.SELECT;
	private int lastClicked = -1;
	private final FunctionManager fm;
	private Vector<MarkerChangeListener> listeners = new Vector<MarkerChangeListener>();
	private Vector<FunctionFactory> functions = new Vector<FunctionFactory>();	
	private Vector<ModeChangeListener> modeListeners = new Vector<ModeChangeListener>();
	
	public WaveMarkerEditPanel(final FunctionManager fm, final SamplePlayer samplePlayer){
		this.fm = fm;
		this.addMouseListener(new MouseAdapter() {			

			@Override
			public void mousePressed(MouseEvent e) {
				click(e.getPoint().x, e.getPoint().y);
			}

		});
		
		functions.add(new FunctionFactory(){
			private static final String name = "Move Marker";
			public String getFunctionName(){
				return name;
			}
			public Function getFunction(){
				return new UndoFunction(){
					Marker m;
					int oldSamples, newSamples;
					public void execute(Object o) throws NoStateChangeException{
						// set value here
						if (currentMarker == null){
							currentMarker = s.nearestMarker(lastClicked, MARKER_CLICK_TOLERANCE);
							repaint();
							throw new NoStateChangeException();
						}
						m = currentMarker;
						currentMarker = null;
						oldSamples = m.samples;
						newSamples = lastClicked;
						if (oldSamples == newSamples)
							throw new NoStateChangeException();
					}
					public void undo(){
						s.moveMarker(m, oldSamples);
						for(MarkerChangeListener c:listeners){
							c.markersChanged(new MarkerChangeEvent(MarkerChangeEvent.TYPE.MOVE, newSamples, oldSamples));
						}
						repaint();
					}
					public void redo(){
						s.moveMarker(m, newSamples);
						for(MarkerChangeListener c:listeners){
							c.markersChanged(new MarkerChangeEvent(MarkerChangeEvent.TYPE.MOVE, oldSamples, newSamples));
						}
						repaint();
					}
					public String getName(){
						return name;
					}
				};
			}
		});
		functions.add(new FunctionFactory(){
			private static final String name = "Delete Marker";
			public String getFunctionName(){
				return name;
			}
			public Function getFunction(){
				return new UndoFunction(){
					Marker m;
					public void execute(Object o) throws NoStateChangeException{
						// set value here
						m = s.nearestMarker(lastClicked, MARKER_CLICK_TOLERANCE);
						if (m==null)
							throw new NoStateChangeException();
					}
					public void undo(){
						s.addMarker(m);
						repaint();
					}
					public void redo(){
						if (m==null) return;
						s.deleteMarker(m);
						for(MarkerChangeListener c:listeners){
							c.markersChanged(new MarkerChangeEvent(MarkerChangeEvent.TYPE.DELETE, m.samples, -1));
						}
						repaint();
					}
					public String getName(){
						return name;
					}
				};
			}
		});
		functions.add(new FunctionFactory(){
			private static final String name = "Add Marker";
			public String getFunctionName(){
				return name;
			}
			public Function getFunction(){
				return new UndoFunction(){
					Marker m;
					public void execute(Object o) throws NoStateChangeException{
						// set value here
						m = new Marker(lastClicked);
					}
					public void undo(){
						s.deleteMarker(m);
						repaint();
					}
					public void redo(){
						if (m==null || s == null) return;
						s.addMarker(m);
						for(MarkerChangeListener c:listeners){
							c.markersChanged(new MarkerChangeEvent(MarkerChangeEvent.TYPE.ADD, m.samples, -1));
						}
						repaint();
					}
					public String getName(){
						return name;
					}
				};
			}
		});
		// these functions may seem pointless but they give a nice name for keyboard shortcuts :)
		functions.add(new DefaultFunctionFactory(new ImmutableFunction(){
			public void execute(Object o){
				if (s == null)
					return;
				currentMarker = s.nextMarkerBeforeSample(lastClicked);
				repaint();
				boolean loop = false;
				if  (s != null)
					samplePlayer.playSlice(getCurrentMarker(), loop);
			}
			public String getName(){
				return "Select Marker";
			}
		}));
		
		functions.add(new DefaultFunctionFactory(new ImmutableFunction(){
			public void execute(Object o){
				setMode(WaveMarkerEditPanel.MODE.ADD);
			}
			public String getName() {
				return "Add Marker Mode";
			}
		}));
		functions.add(new DefaultFunctionFactory(new ImmutableFunction(){
			public void execute(Object o){
				setMode(WaveMarkerEditPanel.MODE.SELECT);
			}
			public String getName() {
				return "Select Marker Mode";
			}
		}));
		functions.add(new DefaultFunctionFactory(new ImmutableFunction(){
			public void execute(Object o){
				setMode(WaveMarkerEditPanel.MODE.MOVE);
			}
			public String getName() {
				return "Move Marker Mode";
			}
		}));
		functions.add(new DefaultFunctionFactory(new ImmutableFunction(){
			public void execute(Object o){
				setMode(WaveMarkerEditPanel.MODE.DELETE);
			}
			public String getName() {
				return "Delete Marker Mode";
			}
		}));
		
		functions.add(new DefaultFunctionFactory(new Function(){
			public void execute(Object o) throws NoStateChangeException {
				boolean loop = false;
				nextSlice();
				if (AudioPlayer.isPlaying()){
					AudioPlayer.stop();
				}
				if  (s == null)
					throw new NoStateChangeException();
				samplePlayer.playSlice(getCurrentMarker(),loop);
			}
			public String getName(){
				return "Next Slice";
			}
		}));
		functions.add(new DefaultFunctionFactory(new Function(){
			public void execute(Object o) throws NoStateChangeException{
				boolean loop = false;
				prevSlice();
				if (AudioPlayer.isPlaying()){
					AudioPlayer.stop();
				}
				
				if  (s == null)
					throw new NoStateChangeException();
				samplePlayer.playSlice(getCurrentMarker(),
							loop);
			}
			public String getName(){
				return "Previous Slice";
			}
		}));
	}
	

	public void addMarkerChangeListener(final MarkerChangeListener m){
		listeners.add(m);
	}
	public void removeMarkerChangeListener(final MarkerChangeListener m){
		listeners.remove(m);
	}
	
	public void addModeChangeListener(final ModeChangeListener m){
		modeListeners.add(m);
	}
	public void removeModeChangeListener(final ModeChangeListener m){
		modeListeners.remove(m);
	}
	/**
	 * Changes the mode of the wave edit session and calls notify on any registered listeners
	 * @param mode
	 */
	public void setMode(MODE mode) {	
		MODE old = this.mode;
		MODE n = mode;
		if (s == null)
			n = old;
		
		this.mode = n;
		currentMarker=null;
		
		for(ModeChangeListener l : modeListeners)
			l.WavePanelModeChanged(this.mode, n);	
	}
	public MODE getMode() {
		return mode;
	}
	
	public void setSample(Sample s)
	{
		if (s == null)
			setMode(MODE.SELECT);
		super.setSample(s);
	}

	/**
	 * returns the currently selected marker
	 * @return
	 */
	public Marker getCurrentMarker(){
		return currentMarker;
	}
	/**
	 * sets the currently selected marker
	 * @param m
	 */
	public void setCurrentMarker(Marker m){
		currentMarker = m;
		setHighlight();
		repaint();
	}
	
	/**
	 * Selects a slice based on the nearest markers to the parameter value
	 * @param x the value in samples to select
	 */
	public void selectSlice(int x){
		currentMarker = s.nextMarkerBeforeSample(x);
		setHighlight();
		repaint();
	}
	/**
	 * Selects the next slice to the currently selected one
	 */
	public void nextSlice(){
		if (s==null) return;
		Vector<Marker> markers = s.getMarkers();
		if (markers.size()>0){
			if (currentMarker==null)
				currentMarker = markers.firstElement();
			else if (currentMarker == markers.lastElement())
				return;
			else {
				currentMarker = markers.get(markers.indexOf(currentMarker)+1);
			}
		}
		setHighlight();
		repaint();
	}
	
	/**
	 * Selects the previous slice to the currently selected one
	 */
	public void prevSlice(){
		if (s==null) return;
		Vector<Marker> markers = s.getMarkers();
		if (currentMarker==null)
			return;
		else if (currentMarker == markers.firstElement())
			currentMarker = null;
		else {
			currentMarker = markers.get(markers.indexOf(currentMarker)-1);
		}
		setHighlight();
		repaint();
	}
	/**
	 * Returns the sample bounds of the currently selected slice
	 * @return
	 */
	public Bound getCurrentSliceBounds(){
		if (s==null) return null;
		long start = 0;
		long end   = s.getSamples();
		Vector<Marker> markers = s.getMarkers();
		if (currentMarker == null){
			if( markers.size()>0) 
				end = markers.firstElement().samples;
		}
		else {
			int i = markers.indexOf(currentMarker);
			if (i<markers.size()-1){
				start = currentMarker.samples;
				end = markers.get(i+1).samples;
			}
			else{
				start = currentMarker.samples;
				end = s.getSamples();
			}
		}
		return new Bound((int)start, (int)end);
	}	
	public void setHighlight(){
		if (s==null) return;
		Bound bo = getCurrentSliceBounds();
		int x = (int) ((bo.start-offset)/getSamplesPerPixel());
		int x2= (int) ((bo.end-offset)/getSamplesPerPixel());
		super.setHighlight(x, x2);
	}
	public void paint(Graphics g){
		setHighlight();
		super.paint(g);
		if (s==null || currentMarker==null) return;
		g.setColor(Color.YELLOW);
		int y = (int)(( currentMarker.samples-offset) / getSamplesPerPixel());
		g.drawLine(y, 0, y, getHeight());
		g.drawString(currentMarker.ID+"", y+1, g.getFont().getSize());
	}
	public void click(int x, int y) {
		System.out.println("Click");
		lastClicked = super.getSamplesFromPixel(x);
		switch(mode){
		case SELECT:
			fm.execute("Select Marker", null);
			break;
		case ADD:
			fm.execute("Add Marker", null);
			break;
		case DELETE:
			fm.execute("Delete Marker", null);
			break;
		case MOVE:
			fm.execute("Move Marker", null);
			break;
		}
	}
	public boolean addMarker(int samples){
		if(super.addMarker(samples)){
			for(MarkerChangeListener c:listeners){
				c.markersChanged(new MarkerChangeEvent(MarkerChangeEvent.TYPE.ADD, samples,-1));
			}
			selectSlice(samples);
			return true;
		}
		return false;
	}
	public boolean contains(String s) {
		for (FunctionFactory ff: functions){
			if (ff.getFunctionName().equals(s))
				return true;
		}
		return super.contains(s);
	}
	public Function getFunction(String s) {
		for (FunctionFactory ff: functions){
			if (ff.getFunctionName().equals(s))
				return ff.getFunction();
		}
		return super.getFunction(s);
	}
	public String[] getFunctionNames() {
		String[] superNames = super.getFunctionNames();
		String[] s = new String[functions.size() + superNames.length];
		int i=0;
		for (FunctionFactory f: functions){
			s[i++] = f.getFunctionName();
		}
		for (String x: superNames){
			s[i++] = x;
		}
		return s;
	}
}
package libBBMPC.utils.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JPanel;

/**
 * HotSpotPanel is a class which extends JPanel, drawing an Image and handling
 * mouse clicks to activate the HotSpot selection.
 * @author Guy
 *
 */
public class HotSpotPanel extends JPanel {
	private static final long serialVersionUID = 218252168268802886L;
	private Image im;
	private Vector<HotSpotGroup> maps = new Vector<HotSpotGroup>();
	private int x,y;
	
	public HotSpotPanel(Image i){
		im = i;
		super.setPreferredSize(new Dimension(im.getWidth(null)/2, im.getHeight(null)/2));
		super.setMaximumSize(new Dimension(im.getWidth(null)/2, im.getHeight(null)/2));
		
		super.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				double ratio_w = (float)getWidth()/im.getWidth(null);
				double ratio_h = (float)getHeight()/im.getHeight(null);
				int x = e.getX();
				int y = e.getY();
				final boolean ctl = (e.getModifiers()&MouseEvent.CTRL_MASK)==0;
				// whichever of these is bigger must be adhered to
				if (ratio_w < ratio_h)
				{
					System.out.println("Clicked "+ x+"," +(y-HotSpotPanel.this.y));
					for (HotSpotGroup group: maps){
						group.click((int)(x / ratio_w), (int)((y-HotSpotPanel.this.y) / ratio_w), ctl);
					}
				}
				else
				{
					System.out.println("Clicked "+ (x - HotSpotPanel.this.x)+"," +y);
					for (HotSpotGroup group: maps){
						group.click((int)((x - HotSpotPanel.this.x)/ratio_h), (int)(y / ratio_h), ctl);
					}
				}				
				repaint();
			}
		});
	}

	public void paint(Graphics g){
		double ratio_w = (float)getWidth()/im.getWidth(null);
		double ratio_h = (float)getHeight()/im.getHeight(null);
		// whichever of these is bigger must be adhered to (if very narrow, must be short etc)
		if (ratio_w < ratio_h)
		{
			int height = (int)( im.getHeight(null) * ratio_w);
			x = 0;
			y = (getHeight()-height)/2;
			g.drawImage(im, x, y, getWidth(), height, null);
		}
		else	
		{
			int width = (int)(im.getWidth(null) * ratio_h);
			x = (getWidth()-width)/2;
			y = 0;
			g.drawImage(im, x, y, width, getHeight(), null);
		}
		
		g.setColor(Color.RED);
		final float ratio = (float)Math.min(ratio_w, ratio_h);
		//System.out.println("Image ratio:" + ratio);
		for (HotSpotGroup group: maps){
			for (HotSpot s: group.getSelectedZones()){
				g.fillRect(x + (int)(s.x*ratio), y + (int)(s.y*ratio), (int)(s.w*ratio),(int)( s.h*ratio));
			}
		}
	}
	public void addHotSpotGroup(final HotSpotGroup g){
		maps.add(g);
	}
}

package libBBMPC.utils.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * HotSpotGroup represents a group of HotSpot objects, and tracks their
 * selection status, allowing single or multiple selection modes.
 * @author Guy
 *
 */
public class HotSpotGroup{
	private final Vector<HotSpot> map = new Vector<HotSpot>();
	private ActionListener action;
	private final Set<HotSpot> selected = new HashSet<HotSpot>();
		
	public enum SELECTIONMODE{
		SINGLE_SELECTION,
		MULTIPLE_SELECTION,
	}	
	private SELECTIONMODE selectionMode=SELECTIONMODE.SINGLE_SELECTION;
	public void addHotSpot(HotSpot h){
		map.add(h);
	}
	public void setActionListener(ActionListener a){
		action = a;
	}
	public SELECTIONMODE getSelectionMode(){
		return selectionMode;
	}
	public void setSelectionMode(SELECTIONMODE mode){
		selectionMode = mode;
	}
	public HotSpot[] getSelectedZones(){
		return selected.toArray(new HotSpot[selected.size()]);
	}
	public void click(int x, int y, boolean ctl) {
		boolean flag = false;
		for(int i=0; i<map.size(); i++){
			HotSpot h = map.get(i);
			if(x >= h.x && x <= h.x+h.w && y >=h.y && y <= h.y+h.h){
				flag = true;
				if ((selectionMode == SELECTIONMODE.SINGLE_SELECTION) || (ctl)){
					selected.clear();
					selected.add(h);
				}else{
					if (selected.contains(h))
						selected.remove(h);
					else
						selected.add(h);
				}
			}
		}
		if (flag && action != null)
			action.actionPerformed(new ActionEvent(this, 0, "Zone Clicked"));
	}
	
	public void setSelected(int i, boolean x){
		if (x)
			selected.add(map.get(i));
		else 
			selected.remove(map.get(i));
	}
	public void setSelected(HotSpot s, boolean x){
		if (!map.contains(s))
			throw new RuntimeException("Cannot select hotspot which is not in hot spot map!");
		if (x)
			selected.add(s);
		else
			selected.remove(s);
	}
	public HotSpot getHotSpot(int x){
		return map.get(x);
	}
}
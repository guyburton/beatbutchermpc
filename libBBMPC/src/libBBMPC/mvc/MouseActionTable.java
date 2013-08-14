package libBBMPC.mvc;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * TODO This needs some work in order to add some sort of interface type binding model for function parameters
 * @author Guy
 *
 */
public class MouseActionTable implements TableModel, MouseListener, MouseWheelListener{
	private static final Preferences prefs = Preferences.userNodeForPackage(Shortcut.class);
	private final Vector<TableModelListener> listeners = new Vector<TableModelListener>();
	private final String[] actions = {"Wheel Up", "Wheel Down", "Wheel Up", "Wheel Down",	"Wheel Up", "Wheel Down", "Left Click","Left Click","Left Click","Right Click","Right Click","Right Click",};
	private final String[] modifiers = {"","","CTRL","CTRL","SHIFT","SHIFT","CTRL","SHIFT","CTRL+SHIFT","CTRL","SHIFT", "CTRL+SHIFT"};
	private String[] functions = {"","","","","","","","","","","",""};
	private FunctionProvider fp;
	private FunctionManager fm;
	//TODO: Add middle button support (not urgent)
	public MouseActionTable(final FunctionProvider fp, final FunctionManager fm){
		this.fm = fm;
		this.fp = fp;
		load();
	}
	public void addTableModelListener(TableModelListener e) {
		listeners.add(e);
	}
	public Class<?> getColumnClass(int arg0) {
		return String.class;
	}
	public int getColumnCount() {
		return 3;
	}
	public String getColumnName(int col) {
		switch(col){
		case 0: return "Action";
		case 1: return "Modifiers";
		case 2: return "Function";
		}
		return "";
	}
	public int getRowCount() {
		return actions.length;
	}
	public Object getValueAt(int row, int col) {
		switch(col){
			case 0:	return actions[row];
			case 1: return modifiers[row];
			case 2: return functions[row];
		}
		return "";
	}

	public boolean isCellEditable(int row, int col) {
		return col==2;
	}

	public void removeTableModelListener(TableModelListener e) {
		listeners.remove(e);
	}

	public void setValueAt(Object s, int row, int col) {
		String f = (String)s;
		if (!f.trim().equals("") && fp.contains(f))
			functions[row] = f;
	}
	/**
	 * saves the function mappings to a platform independent backing store
	 */
	public void save(){
		try {
			prefs.clear();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		for (int i=0; i<actions.length; i++){
			prefs.putByteArray(""+i, functions[i].getBytes());
		}
	}
	/**
	 * Loads function mappings from the platform independent backing store
	 */
	public void load(){
		for (int i=0; i<actions.length; i++){
			functions[i] = new String(prefs.getByteArray(""+i, "".getBytes()));
		}
		for (TableModelListener l: listeners)
			l.tableChanged(new TableModelEvent(this));
	}
	/**
	 * Clears any mappings from the backing store
	 */
	public void clear(){
		for (int i=0; i<functions.length; i++){
			functions[i] = "";
		}
		for (TableModelListener l:listeners)
			l.tableChanged(new TableModelEvent(this));
	}
	public void mouseClicked(MouseEvent e) {
		if (e.getModifiers()== KeyEvent.CTRL_MASK){
			if (e.getButton()==MouseEvent.BUTTON1){
				if (functions[6].equals(""))
					return;
				fm.execute(functions[6], null);
			}else if (e.getButton()==MouseEvent.BUTTON2){
				if (functions[9].equals(""))
					return;
				fm.execute(functions[9], null);
			}
		}else if (e.getModifiers()== KeyEvent.SHIFT_MASK){
			if (e.getButton()==MouseEvent.BUTTON1){
				if (functions[7].equals(""))
					return;
				fm.execute(functions[7], null);
			}else if (e.getButton()==MouseEvent.BUTTON2){
				if (functions[10].equals(""))
					return;
				fm.execute(functions[10], null);
			}
		}else if ((e.getModifiers()& KeyEvent.SHIFT_MASK)>0 &&
				(e.getModifiers() & KeyEvent.CTRL_MASK)>0){
			if (e.getButton()==MouseEvent.BUTTON1){
				if (functions[8].equals(""))
					return;
				fm.execute(functions[8], null);
			}else if (e.getButton()==MouseEvent.BUTTON2){
				if (functions[11].equals(""))
					return;
				fm.execute(functions[11], null);
			}
		}
	}
	
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getModifiers()==0){
			int x = e.getWheelRotation();
			if (x<0){
				if (!functions[0].equals(""))
					fm.execute(functions[0], null);
			}else if (x>0){
				if (!functions[1].equals(""))
					fm.execute(functions[1], null);
			}
		}else if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) > 0){
			int x = e.getWheelRotation();
			if (x>0){
				if (!functions[2].equals(""))
					fm.execute(functions[2], null);
			}else if (x<0){
				if (!functions[3].equals(""))
					fm.execute(functions[3], null);
			}
		}else if ((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) > 0){
			int x = e.getWheelRotation();
			if (x>0){
				if (!functions[4].equals(""))
					fm.execute(functions[4], null);
			}else if (x<0){
				if (!functions[5].equals(""))
					fm.execute(functions[5], null);
			}
		}
	}
}

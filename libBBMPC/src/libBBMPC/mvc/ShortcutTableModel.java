package libBBMPC.mvc;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class ShortcutTableModel implements TableModel{
	private final Vector<TableModelListener> listeners = new Vector<TableModelListener>();
	private final FunctionManager fm;
	private final JComponent root;
	private final Vector<Shortcut> shortcuts = new Vector<Shortcut>();
	private static final Preferences prefs = Preferences.userNodeForPackage(Shortcut.class);
	public ShortcutTableModel(final FunctionManager fm, final JComponent root){
		this.fm = fm;
		this.root = root;
	}
	public void addTableModelListener(TableModelListener e) {
		listeners.add(e);
	}
	public Class<?> getColumnClass(int x) {
		return String.class;
	}
	public int getColumnCount() {
		return 3;
	}
	/**
	 * Adds a function to the shortcut map
	 * @param f
	 */
	public void addFunction(final String f){
		ActionMap amap = root.getActionMap();
		if (amap.get(f)!=null){
			throw new RuntimeException("Function Already present in action map: "+f);
		}
		amap.put(f, new AbstractAction(){
			private static final long serialVersionUID = 6114623688901018680L;
			public void actionPerformed(ActionEvent arg0) {
				fm.execute(f, null);					
			}
		});
		final Shortcut s = new Shortcut(f, -1, -1);
		shortcuts.add(s);
		Collections.sort(shortcuts);
		load(s);
		for (TableModelListener l: listeners){
			l.tableChanged(new TableModelEvent(this));
		}
	}
	public String getColumnName(int x) {
		switch (x){
		case 0:
			return "Action";
		case 1:
			return "Key";
		case 2:
			return "Modifiers";
		}
		return null;
	}

	public int getRowCount() {
		return shortcuts.size();
	}

	public Object getValueAt(int row, int col) {
		Shortcut s = shortcuts.get(row);
		switch(col){
		case 0:
			return s.function;
		case 1: 
			if (s.key == -1)
				return "";
			return KeyEvent.getKeyText(s.key).toUpperCase();
		case 2:
			if (s.modifiers == -1)
				return "";
			return KeyEvent.getKeyModifiersText(s.modifiers).toUpperCase();
		}
		return 0;
	}
	/**
	 * Sets the value at a position in the table. 
	 * Does not affect actual key bindings or persistent storage until save() is called
	 */
	public void setValueAt(Object e, int row, int col) {
		// TODO: check for dupes
		switch(col){
		case 0:
			return;
		case 1:
			shortcuts.get(row).key = (Integer)e;
			break ;
		case 2:
			shortcuts.get(row).modifiers = (Integer)e;
			break ;
		}
		for (TableModelListener l : listeners){
			l.tableChanged(new TableModelEvent(this));
		}
	}
	public boolean isCellEditable(int row, int col) {
		return false;
	}
	public void removeTableModelListener(TableModelListener e) {
		listeners.remove(e);
	}
	/**
	 * This functions loads key bindings for all action maps from persistent storage
	 * Can be used to restore settings to previously saved point
	 */
	public void load(){
		for (Shortcut s: shortcuts){
			load(s);
		}
	}
	/**
	 * Loads key bindings for a given shortcut string and loads them into the Shortcut object
	 * as well as the root component input map
	 * @param s
	 */
	private void load(Shortcut s){
		s.modifiers = prefs.getInt(s.function+"M", -1);
		s.key = prefs.getInt(s.function+"K", -1);
		if (s.modifiers == -1 && s.key == -1) return;
		InputMap imap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		final KeyStroke keystroke =  KeyStroke.getKeyStroke(s.key, s.modifiers);
		if (imap.get(keystroke)!=null)
			imap.put(keystroke, null);
		imap.put(keystroke, s.function);
		for (TableModelListener l: listeners)
			l.tableChanged(new TableModelEvent(this));
	}
	public static void clear(){
		try {
			prefs.clear();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
	public static int getModifiers(String e){
		int x = 0;
		e = e.toUpperCase();
		if (e.contains("CTRL"))
			x |= KeyEvent.CTRL_DOWN_MASK;
		if (e.contains("SHIFT"))
			x |= KeyEvent.SHIFT_DOWN_MASK;
		if (e.contains("ALT"))
			x |= KeyEvent.ALT_DOWN_MASK;
		return x;
	}
	/*public Shortcut[] getShortcutMap() {
		Shortcut[] s = new Shortcut[shortcuts.size()];
		for (int i=0; i<shortcuts.size(); i++){
			Shortcut g = shortcuts.get(i);
			s[i] = new Shortcut(g.function, g.key, g.modifiers);
		}
		return s;
	}
	public void setShortcutMap(Shortcut[] s){
		shortcuts.clear();
		for (Shortcut x: s){
			shortcuts.add(x);
		}
		for (TableModelListener l: listeners)
			l.tableChanged(new TableModelEvent(this));
	}*/
	/**
	 * Saves shortcuts to backing store
	 * @throws BackingStoreException
	 */
	public void save() throws BackingStoreException{
		prefs.clear();
		for (Shortcut s: shortcuts){
			if (s.key!=-1 && s.key!= -1){
				prefs.putInt(s.function+"K", s.key);
				prefs.putInt(s.function+"M", s.modifiers);
			}
		}
		load(); // load all key bindings 
	}
	public void addFunctions(String[] functions) {
		for (String s: functions)
			addFunction(s);
	}
}
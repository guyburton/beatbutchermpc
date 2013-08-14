package libBBMPC.mvc;

import java.awt.Image;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JOptionPane;
/**
 * This class provides execution of Function objects, honouring their type of Function,
 * ImmutableFunction or UndoFunction and thus maintaining the Function history at all
 * times.
 * 
 * Functions should be added using the addFunctionProvider methods.
 * Update should be overridden if a standard GUI update method is required after all
 * methods. (maybe make it take an interface as a field/constructor argument)
 * @author Guy Burton
 *
 */
public abstract class FunctionManager{
	private final Stack<UndoFunction> undo = new Stack<UndoFunction>();
	private final Stack<UndoFunction> redo = new Stack<UndoFunction>();
	private Vector<FunctionEventListener> listeners = new Vector<FunctionEventListener>();
	public final static int HISTORY_SIZE = 10;
	private Vector<FunctionProvider> providers = new Vector<FunctionProvider>();
	private Image icon;
	
	public void execute(final String x, Object parameter){
		System.out.println("Executing: "+x);
		if (x.equals("Show History")){
			FunctionHistoryDialog d = new FunctionHistoryDialog(FunctionManager.this);
			if (icon!=null)
				d.setIcon(icon);
			return;
		}
		for (FunctionProvider p:providers){
			if (p.contains(x)){
				Function f = p.getFunction(x);
				try {
					f.execute(parameter);
				} catch (NoStateChangeException e) {
					return;
				}
				if (f instanceof UndoFunction){
					addFunction(((UndoFunction)f));
				}else if (!(f instanceof ImmutableFunction)){
					clearHistory();
				}
				update();
				return;
			}
		}
		JOptionPane.showMessageDialog(null, "Function: "+x+" is not implemented!");	
	}
	/**
	 * Adds a FunctionEventListener to the FunctionManager to be notified of
	 * events on the event history stack.
	 * @param f
	 */
	public void addFunctionEventListener(FunctionEventListener f){
		listeners.add(f);
	}
	/**
	 * Creates a new instance of FunctionManager. Uses the provided icon when a history
	 * dialog window is shown.
	 * @param icon
	 */
	public FunctionManager(Image icon){
		this.icon = icon;
	}
	/**
	 * Creates a new instance of FunctionManager
	 */
	public FunctionManager(){}
	/**
	 * Sets the icon shown when a history dialog window is created.
	 * @param icon
	 */
	public void setIcon(Image icon){
		this.icon = icon;
	}
	/**
	 * Adds a function provider to the FunctionManager.
	 * @param fp
	 */
	public void addFunctionProvider(FunctionProvider fp){
		providers.add(fp);
	}
	private void addFunction(UndoFunction f){
		if (undo.size()>=HISTORY_SIZE){
			undo.remove(0);
		}
		undo.push(f);
		f.redo();
		redo.clear();
		for (FunctionEventListener listener: listeners)
			if (listener!=null)
				listener.functionEventPerformed(new FunctionEvent(FunctionEvent.TYPE.ADD, undo.peek(), null));
	}
	/**
	 * Clears the function history.
	 */
	public void clearHistory(){
		for (FunctionEventListener listener: listeners)
			if (listener!=null)
				listener.functionEventPerformed(new FunctionEvent(FunctionEvent.TYPE.CLEAR, null, null));
		undo.clear();
		redo.clear();
	}
	/**
	 * Undo reverses the action of the last function on the function history stack
	 */
	public void doUndo(){
		if (undo.size() == 0) return;
		UndoFunction f = undo.pop();
		f.undo();
		redo.push(f);
		for (FunctionEventListener listener: listeners)
			if (listener!=null){
				UndoFunction f1 = null;
				UndoFunction f2 = null;
				if (undo.size()>0){
					f1 = undo.peek();
				}
				if (redo.size()>0){
					f2 = redo.peek();
				}
				listener.functionEventPerformed(new FunctionEvent(FunctionEvent.TYPE.UNDO,f1, f2));
			}
		update();
	}
	/**
	 * Redo performs the first function on the redo history stack. 
	 */
	public void doRedo(){
		if (redo.size()==0) return;
		UndoFunction f = redo.pop();
		f.redo();
		undo.push(f);
		for (FunctionEventListener listener: listeners)
			if (listener!=null){
				UndoFunction f1 = null;
				UndoFunction f2 = null;
				if (undo.size()>0){
					f1 = undo.peek();
				}
				if (redo.size()>0){
					f2 = redo.peek();
				}
				listener.functionEventPerformed(new FunctionEvent(FunctionEvent.TYPE.REDO,f1, f2));
			}
		update();
	}
	/**
	 * Provides a list of Function names from the redo stack
	 * @return
	 */
	public String[] getRedoList() {
		String[] s = new String[redo.size()];
		for (int i=0; i<s.length; i++){
			s[i] = redo.get(i).getName();
		}
		return s;
	}
	/**
	 * Provides a list of Function names from the undo stack
	 * @return
	 */
	public String[] getUndoList() {
		String[] s = new String[undo.size()];
		for (int i=0; i<s.length; i++){
			s[i] = undo.get(i).getName();
		}
		return s;
	}
	
	/**
	 * Override this method to provide a global GUI update on all function execution.
	 */
	public abstract void update();
	
	/**
	 * Returns the function names of all Functions which are executable
	 * @return
	 */
	public String[] getFunctions() {
		Vector<String> functions = new Vector<String>();
		for (FunctionProvider fp: providers)
			for(String s: fp.getFunctionNames())
				functions.add(s);
		final String[] f = new String[functions.size()];
		return functions.toArray(f);
	}
	/**
	 * Removes a FunctionEventListener from the list
	 * @param listener
	 */
	public void removeFunctionEventListener(FunctionEventListener listener) {
		listeners.remove(listener);
	}
}
package libBBMPC.mvc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;




/**
 * Convenience class to ease adding menu items to function manager with no parameters
 * @author Guy
 *
 */
public class FunctionMenuItem extends JMenuItem implements ActionListener{
	private static final long serialVersionUID = -6593286833031321181L;
	private final FunctionManager fm;
	private final String name;
	public FunctionMenuItem(FunctionManager fm, String name){
		super(name);
		super.addActionListener(this);
		this.fm = fm;
		this.name = name;
	}
	public void actionPerformed(ActionEvent e){
		fm.execute(name, null);
	}
}

package libBBMPC.mvc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;


/**
 * Just a convenience class to implement a JButton to bind to a function with no parameters
 * @author Guy
 *
 */
public class FunctionButton extends JButton implements ActionListener{
	private static final long serialVersionUID = -4792395402902957981L;
	private final String function;
	private final FunctionManager fm;
	public FunctionButton(FunctionManager fm, String function, ImageIcon im){
		super(im);
		if (im==null){
			super.setText(function);
		}
		super.addActionListener(this);
		super.setToolTipText(function);
		//super.addMouseListener(this);
		this.function = function;
		this.fm = fm;
	}
	public void actionPerformed(ActionEvent e){
		fm.execute(function, null);
	}
	//public void mouseEntered(MouseEvent e){
	//	fm.setStatus(function);
	//}
}

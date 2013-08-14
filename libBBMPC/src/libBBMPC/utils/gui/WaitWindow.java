package libBBMPC.utils.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WaitWindow extends JDialog {
	private static final long serialVersionUID = 3992773074084668049L;
	private final JButton cancel = new JButton("Cancel");
	private final JLabel label = new JLabel("Please wait...");
	private ActionListener listener;
	private Thread t;
	public WaitWindow(JComponent parent){
		super();
		if (parent!=null)
			super.setLocationRelativeTo(parent);
		super.setSize(150,60);
		super.setAlwaysOnTop(true);
		JPanel p = new JPanel(new BorderLayout());
		p.add(label, BorderLayout.CENTER);
		p.add(cancel, BorderLayout.SOUTH);
		super.add(p);
		super.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				cancel();
			}
		});
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				cancel();
			}
		});
		super.setVisible(true);
		t = new Thread(){
			int count = 0;
			public void run(){
				try{
					while(true){
						String s = "Please Wait";
						for (int i=0; i<count; i++){
							s = s + ".";
						}
						count++;
						if (count >=5)
							count = 0;
						label.setText(s);
						Thread.sleep(1000);
					}
				}catch(InterruptedException e){
					// do nothing
				}
			}
		};
		t.start();
	}
	public void dispose(){
		t.interrupt();
		super.dispose();
	}
	public void setActionListener(ActionListener listener){
		this.listener = listener;
	}
	public void removeActionListener(){
		listener = null;
	}
	private void cancel(){
		if (listener!=null)
			listener.actionPerformed(new ActionEvent(this, 0, "CANCEL"));
	}
}


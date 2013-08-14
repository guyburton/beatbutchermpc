package libBBMPC.utils.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class HelpWindow extends JFrame {
	private static final long serialVersionUID = -2443271780485265091L;
	private JEditorPane html = new JEditorPane();
	private JEditorPane index = new JEditorPane();
	public HelpWindow(final URL homePage, final URL indexPage){
		JPanel p = new JPanel(new BorderLayout());
		p.add(new JScrollPane(html), BorderLayout.CENTER);
		p.add(new JScrollPane(index), BorderLayout.WEST);
		super.add(p);
		index.setEditable(false);
		try{
			html.setPage(homePage);
			index.setPage(indexPage);
			index.setMinimumSize(new Dimension(200,300));
			index.addHyperlinkListener(new HyperlinkListener(){
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType()==HyperlinkEvent.EventType.ACTIVATED){
			 	        try {
							html.setPage(e.getURL());
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			});
		}catch(IOException e){
			JOptionPane.showMessageDialog(null,"Could not load help file!");
			super.dispose();
		}
		super.setTitle("Help");
		super.setVisible(true);
		super.setLocation(200,200);

		super.setSize(new Dimension(640,480));
	}
}

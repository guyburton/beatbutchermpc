package libBBMPC.mvc;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.prefs.BackingStoreException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class ShortcutManager extends JDialog implements TableModelListener{
	private static final long serialVersionUID = -8475752359180060025L;
	private JTable table = new JTable();
	private JButton hide = new JButton("Cancel");
	private JButton reset = new JButton("Clear");
	private JButton save = new JButton("Save");
	int key = -1;
	int modifiers = -1;
	public ShortcutManager(final ShortcutTableModel tableModel){
		super();
		tableModel.addTableModelListener(this);
		JPanel root = new JPanel(new BorderLayout());
		table.setModel(tableModel);
		root.add(new JScrollPane(table), BorderLayout.NORTH);
		JPanel buttons = new JPanel(new GridLayout(1,3));
		buttons.add(hide);
		buttons.add(reset);
		buttons.add(save);
		save.setDefaultCapable(true);
		root.add(buttons, BorderLayout.SOUTH);
		super.setContentPane(root);
		super.setTitle("Keyboard Actions");
		super.pack();
		super.setVisible(true);
		hide.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				tableModel.load();
				tableModel.removeTableModelListener(ShortcutManager.this);
				ShortcutManager.this.setVisible(false);
				ShortcutManager.this.dispose();
			}
		});
		reset.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ShortcutTableModel.clear();
			}
		});
		save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setVisible(false);
				try {
					tableModel.save();
				} catch (BackingStoreException e1) {
					JOptionPane.showMessageDialog(ShortcutManager.this, 
							"There was a problems saving shortcut configuration");
				}
				tableModel.removeTableModelListener(ShortcutManager.this);
				ShortcutManager.this.setVisible(false);
				ShortcutManager.this.dispose();
			}
		});
		final JDialog d = new JDialog(this);
		final JPanel panel = new JPanel(new BorderLayout());
		d.add(panel);
		d.setSize(150,100);
		d.setTitle("BeatButcherMPC");
		d.setAlwaysOnTop(true);
		d.setModalityType(ModalityType.APPLICATION_MODAL);
		
		final JButton ok = new JButton("OK");
		final JButton cancel = new JButton("Cancel");
		final JPanel btns = new JPanel(new GridLayout(1,2));
		final JLabel keyLabel = new JLabel("Key:");
		btns.add(ok);
		btns.add(cancel);
		panel.add(new JLabel("Press a key with modifiers"), BorderLayout.NORTH);
		panel.add(keyLabel, BorderLayout.CENTER);
		panel.add(btns, BorderLayout.SOUTH);
		final KeyListener kl = new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				key = e.getKeyCode();
				modifiers = e.getModifiers();
				final String k = KeyEvent.getKeyText(e.getKeyCode());
				String mods = KeyEvent.getKeyModifiersText(e.getModifiers());
				mods = mods.replaceAll(k,"");
				keyLabel.setText("Key(s): " + k + 
						(mods.length()>0 ? " + ":"") +  mods);
			}
		};
		panel.addKeyListener(kl);
		ok.addKeyListener(kl);
		cancel.addKeyListener(kl);
		ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int row = table.getSelectedRow();
				tableModel.setValueAt(key, row, 1);
				tableModel.setValueAt(modifiers, row, 2);
				d.setVisible(false);
			}
		});
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				d.setVisible(false);
			}
		});
		table.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if (e.getClickCount()>1){
					d.setVisible(true);
				}
			}
		});
	}
	public void tableChanged(TableModelEvent e) {
		repaint();				
	}	
}

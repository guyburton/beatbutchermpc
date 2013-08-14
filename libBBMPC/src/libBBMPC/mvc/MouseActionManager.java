package libBBMPC.mvc;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class MouseActionManager extends JDialog{
	private static final long serialVersionUID = -2229131323845169351L;
	private final MouseActionTable tableModel;
	private final JComboBox options;
	private final JTable table = new JTable();
	private JButton hide = new JButton("Cancel");
	private JButton reset = new JButton("Clear");
	private JButton save = new JButton("Save");
	
	public MouseActionManager(final FunctionProvider fp, final FunctionManager fm){
		super.setSize(350,400);
		super.setTitle("Wave Editor Mouse Actions");
		super.add(new JScrollPane(table), BorderLayout.CENTER);
		final JPanel south = new JPanel(new GridLayout(1,3));
		south.add(hide);
		south.add(reset);
		south.add(save);
		super.add(south, BorderLayout.SOUTH);
		options = new JComboBox(fp.getFunctionNames());
		options.setEditable(true);
		tableModel = new MouseActionTable(fp, fm);
		table.setModel(tableModel);
		table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(options));
		
		hide.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		});
		reset.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				tableModel.clear();
			}
		});
		save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				tableModel.save();
				dispose();
			}
		});
	}
}

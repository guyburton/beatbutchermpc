package libBBMPC.mvc;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class FunctionHistoryDialog{
	private static final long serialVersionUID = 8785064935936854937L;
	private final JDialog dialog = new JDialog();
	private final JList undoList = new JList();
	private final JList redoList = new JList();
	private final JButton undoButton = new JButton("Undo");
	private final JButton hideButton = new JButton("Hide");
	private final JButton redoButton = new JButton("Redo");
	public FunctionHistoryDialog(final FunctionManager fm){
		JPanel buttonPanel = new JPanel(new GridLayout(3,1));
		buttonPanel.add(undoButton);
		buttonPanel.add(hideButton);
		buttonPanel.add(redoButton);
		JPanel root = new JPanel(new BorderLayout());
		root.add(new JScrollPane(undoList), BorderLayout.NORTH);
		root.add(new JScrollPane(redoList), BorderLayout.SOUTH);
		root.add(buttonPanel);
		dialog.setContentPane(root);
		dialog.setTitle("Undo / Redo History");
		dialog.pack();
		dialog.setVisible(true);
		redoList.setListData(fm.getRedoList());
		undoList.setListData(fm.getUndoList());
		final FunctionEventListener listener = new FunctionEventListener(){
			public void functionEventPerformed(FunctionEvent f) {
				redoList.setListData(fm.getRedoList());
				undoList.setListData(fm.getUndoList());
			}
		};
		undoButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				fm.doUndo();
				redoList.setListData(fm.getRedoList());
				undoList.setListData(fm.getUndoList());
			}
		});
		redoButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				fm.doRedo();
				redoList.setListData(fm.getRedoList());
				undoList.setListData(fm.getUndoList());
			}
		});
		hideButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				fm.removeFunctionEventListener(listener);
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
		fm.addFunctionEventListener(listener);
	}
	public void setIcon(Image im){
		dialog.setIconImage(im);
	}
}


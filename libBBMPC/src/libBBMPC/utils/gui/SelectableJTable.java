package libBBMPC.utils.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class SelectableJTable extends JTable{
	private static final long serialVersionUID = 4106829182928570543L;
	private int selectedColumn=-1;
	private Vector<Cell> selectedCells = new Vector<Cell>();
	
	public SelectableJTable(){
		super.getTableHeader().addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				int col = columnAtPoint(e.getPoint());
				selectedColumn = col;
			}
		});
		super.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				int col = columnAtPoint(e.getPoint());
				int row = rowAtPoint(e.getPoint());
				if ((e.getModifiers()&MouseEvent.CTRL_MASK)>0){
					selectedCells.add(new Cell(row, col));
				}else if ((e.getModifiers()&MouseEvent.SHIFT_MASK)>0){
					if (selectedCells.size()==0){
						selectedCells.add(new Cell(row,col));
						return;
					}
					Cell c = selectedCells.lastElement();
					selectedCells.clear();
						for (int i=Math.min(c.column,col); i<=Math.max(c.column,col); i++){
							for (int j=Math.min(c.row,row); j<=Math.max(c.row,row);j++){
								selectedCells.add(new Cell(j,i));
							}
						}
				}else{
					selectedCells.clear();
					selectedCells.add(new Cell(row,col));
				}
				selectedColumn = -1;
				repaint();
			}
		});
		super.setRowSelectionAllowed(true);
		super.setColumnSelectionAllowed(true);
		super.setColumnSelectionAllowed(true);
		super.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}
	public int getSelectedColumn(){
        return selectedColumn;
	}
	public boolean isSelected(int r, int c){
		return selectedCells.contains(new Cell(r,c));
	}
	private class Cell{
		int row, column;
		public Cell(int r, int c){
			row = r;
			column = c;
		}
		public boolean equals(Cell c){
			return(row==c.row)&&(column==c.column);				
		}
		public boolean equals(Object o){
			if (o instanceof Cell)
				return equals((Cell)o);
			return false;
		}
	}
}

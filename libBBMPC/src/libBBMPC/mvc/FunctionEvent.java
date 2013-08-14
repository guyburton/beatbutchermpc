package libBBMPC.mvc;


public class FunctionEvent {
	public enum TYPE{
		ADD,
		UNDO,
		REDO,
		CLEAR
	}
	private TYPE type;
	private UndoFunction undo, redo;
	public FunctionEvent(TYPE t, UndoFunction undo, UndoFunction redo){
		this.type = t;
		this.undo = undo;
		this.redo = redo;
	}
	public TYPE getType(){
		return type;
	}
	public UndoFunction getUndoFunction(){
		return undo;
	}
	public UndoFunction getRedoFunction(){
		return redo;
	}
}

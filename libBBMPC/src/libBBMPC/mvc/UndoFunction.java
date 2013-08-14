package libBBMPC.mvc;


public interface UndoFunction extends Function{
	public void redo();
	public void undo();
}

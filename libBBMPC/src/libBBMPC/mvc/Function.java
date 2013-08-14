package libBBMPC.mvc;
public interface Function {
	public void execute(Object param) throws NoStateChangeException;
	public String getName();
}

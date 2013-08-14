package libBBMPC.utils.gui;


public class MarkerChangeEvent {
	public static enum TYPE{
		ADD, MOVE, DELETE;
	}
	private final TYPE type;
	private final int oldSamples;
	private final int newSamples;
	public MarkerChangeEvent(TYPE t, int o, int n){
		type = t;
		oldSamples = o;
		newSamples = n;
	}
	public TYPE getType(){
		return type;
	}
	public int getOldMarker(){
		return oldSamples;
	}
	public int getNewMarker(){
		return newSamples;
	}
}

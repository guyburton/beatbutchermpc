package libBBMPC.utils.data;


public class Marker implements Comparable<Marker> {
	public int samples;
	public static int id_count = 0;
	public final int ID = id_count++;
	public Marker(int s){
		samples = s;
	}
	public String toString(){
		return ID + ": "+samples;
	}
	public String getLetter(){
		char c = (char)(65 + ID);
		return c+ "";
	}
	public int compareTo(Marker m){
		if (m.samples > samples)
			return -1;
		else if (m.samples == samples)
			return 0;
		else
			return 1;
	}
	public boolean equals(Object o){
		if (o instanceof Marker){
			Marker m = (Marker)o;
			if (m.samples==samples)
				return true;
		}
		return false;
	}
}

package libBBMPC.mvc;

public class Shortcut implements Comparable<Shortcut>{
	public String function;
	public int key;
	public int modifiers;
	public Shortcut(String f, int key, int modifiers){
		this.function = f;
		this.key = key;
		this.modifiers = modifiers;
	}
	public boolean equals(Shortcut s){
		return s.key == key && s.modifiers == modifiers;
	}
	public boolean equals(Object o){
		if (o instanceof Shortcut)
			return equals((Shortcut)o);
		return false;
	}
	public int compareTo(Shortcut s) {
		return (function.compareTo(s.function));
	}
}

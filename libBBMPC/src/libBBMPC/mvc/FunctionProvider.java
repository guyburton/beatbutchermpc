package libBBMPC.mvc;

import libBBMPC.mvc.Function;

public interface FunctionProvider {
	public Function getFunction(String s);
	public boolean contains(String s);
	public String[] getFunctionNames();
}

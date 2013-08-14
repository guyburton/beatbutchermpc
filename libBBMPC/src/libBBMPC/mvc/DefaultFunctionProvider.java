package libBBMPC.mvc;

import java.util.Vector;



/**
 * This class is the controller connecting the ProgramPanel view to the Program
 * model
 * 
 * @author Guy Burton
 */
public class DefaultFunctionProvider implements FunctionProvider {
	protected Vector<FunctionFactory> functions = new Vector<FunctionFactory>();
	public boolean contains(String s) {
		for (FunctionFactory ff : functions) {
			if (ff.getFunctionName().equals(s)) {
				return true;
			}
		}
		return false;
	}
	public Function getFunction(String s) {
		for (FunctionFactory ff : functions) {
			if (ff.getFunctionName().equals(s)) {
				return ff.getFunction();
			}
		}
		return null;
	}
	public String[] getFunctionNames() {
		String[] s = new String[functions.size()];
		int i=0;
		for (FunctionFactory f: functions){
			s[i++] = f.getFunctionName();
		}
		return s;
	}
}

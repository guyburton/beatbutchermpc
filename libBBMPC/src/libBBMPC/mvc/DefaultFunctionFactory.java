package libBBMPC.mvc;

import libBBMPC.mvc.Function;
import libBBMPC.mvc.FunctionFactory;

public class DefaultFunctionFactory implements FunctionFactory{
	private final Function f;
	public DefaultFunctionFactory(Function f){
		this.f = f;
	}
	public Function getFunction() {
		return f;
	}
	public String getFunctionName() {
		return f.getName();
	}
}
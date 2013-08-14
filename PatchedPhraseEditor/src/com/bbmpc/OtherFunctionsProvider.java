package com.bbmpc;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import libBBMPC.mvc.Function;
import libBBMPC.mvc.FunctionFactory;
import libBBMPC.mvc.FunctionProvider;
import libBBMPC.mvc.NoStateChangeException;
import libBBMPC.mvc.UndoFunction;
import libBBMPC.utils.data.Marker;
import libBBMPC.utils.data.Sample;


class OtherFunctionsProvider implements FunctionProvider {
	private final List<FunctionFactory> functions = new ArrayList<FunctionFactory>();
	
	public class DivideArgs
	{	
		public final Sample s;
		public final int slices;
		public final int swing;
		public DivideArgs(Sample s, int slices, int swing)
		{
			this.s = s;
			this.slices = slices;
			this.swing = swing;
		}
	}
	
	public OtherFunctionsProvider()
	{		
		/**
		 * This method divides the wave in the wavepanel with the specified parameters
		 * @param slices the total number of slices to divide the clip into
		 * @param swing a value between 50 and 75 denoting the shift of every 'odd'
		 * beat towards the next 'even' one. 
		 */
		functions.add( new FunctionFactory(){
			private final static String name = "Divide Sample";
			public Function getFunction(){
				return new UndoFunction(){	
					DivideArgs d;
					Vector<Marker> markers;
					public void execute(Object o) throws NoStateChangeException{
						if (! (o instanceof DivideArgs))
							throw new NoStateChangeException();
						d = (DivideArgs)o;
						markers = d.s.getMarkers();
					}
					public void redo(){
						d.s.clearMarkers();
						final int x = (int)d.s.getSamples()/d.slices;
						for (int i=1; i< d.slices; i++){
							int n = x*i;
							if (i%2!=0){
								n += (d.swing - 50)/100.f * x; 
							}
							d.s.addMarker(new Marker(n));
						}
					}
					public void undo(){
						d.s.clearMarkers();
						for (Marker m: markers)
							d.s.addMarker(m);
					}
					public String getName(){
						return name;
					}
				};
			}
			public String getFunctionName() {
				return name;
			};
		});
		
		functions.add( new FunctionFactory(){
			private final static String name = "Clear Markers";
			public Function getFunction(){
				return new UndoFunction(){
					Vector<Marker> markers;
					Sample s;
					public void execute(Object o) throws NoStateChangeException
					{
						if(o instanceof Sample == false)
							throw new NoStateChangeException();
						s = ((Sample)o);
						markers = s.getMarkers();
					}
					public void redo(){
						s.clearMarkers();
					}
					public void undo(){
						for (Marker m:markers)
							s.addMarker(m);
					}
					public String getName(){
						return name;
					}
				};
			}
			public String getFunctionName(){
				return name;
			}
		});
	}
	public boolean contains(String s) {
		for (FunctionFactory f: functions){
			if (f.getFunctionName().equals(s)){
				return true;
			}
		}
		return false;
	}
	public Function getFunction(String s) {
		for (FunctionFactory f: functions){
			if (f.getFunctionName().equals(s)){
				return f.getFunction();
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

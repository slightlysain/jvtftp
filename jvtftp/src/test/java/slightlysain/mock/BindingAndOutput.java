package slightlysain.mock;

import java.io.PrintWriter;

import groovy.lang.Binding;

public class BindingAndOutput extends Binding {
	private PrintWriter output;
	
	BindingAndOutput(PrintWriter out) {
		output = out;
	}
	
	public PrintWriter getOutPrintWriter() {
		return output;
	}
	
}

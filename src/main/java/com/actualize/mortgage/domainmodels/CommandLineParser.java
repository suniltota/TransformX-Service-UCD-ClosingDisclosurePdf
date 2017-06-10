package com.actualize.mortgage.domainmodels;

public class CommandLineParser {
	private final String args[];

	public CommandLineParser(String args[]) {
		this.args = args;
	}

	String arg(String arg) {
		String match = null;
		for (int i = 0; i < args.length; i++)
			if (args[i].equals(arg))
				return args[i+1];
		return match;
	}
}

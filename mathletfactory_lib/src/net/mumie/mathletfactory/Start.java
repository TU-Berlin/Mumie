/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2010 Technische Universitaet Berlin
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.mumie.mathletfactory;

import net.mumie.mathletfactory.appletskeleton.BaseApplet;
import net.mumie.mathletfactory.appletskeleton.system.SystemPropertyIF;
import net.mumie.mathletfactory.util.logging.LoggingUI;
import net.mumie.mathletfactory.util.xml.DatasheetExplorer;

/**
 * This class can be used to use one of the MathletFactory library tools which must
 * be specified as an argument to the <code>main</code> method.
 * When no arguments are specified the list of available tools is printed out.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class Start {
	
	public final static String VERSION_PARAMETER = "version";
	public final static String DSEXPL_PARAMETER = "dsexplorer";
	public final static String LOGUI_PARAMETER = "logui";
	public final static String HELP_PARAMETER = "help";
	
	/**
	 * Starts the application starter for the MathletFactory library tools.
	 * When no arguments are specified the list of available tools is printed out.
	 * 
	 * @param args the tool to start
	 */
	public static void main(String[] args) {
		System.out.println("MathletFactory library tools\n");
		
		for(int i = 0; i < args.length; i++) {
			if(equalsParam(args[i], VERSION_PARAMETER)) {
				BaseApplet baseApplet = createContext();
				String libVersion = baseApplet.getMathletRuntime().getSystemProperty(SystemPropertyIF.LIB_VERSION_PROPERTY);;
				String libVersionString = (libVersion != null) ? libVersion : "unknown";
				System.out.println("Version of MathletFactory library is: " + libVersionString);
			}
			else if(equalsParam(args[i], DSEXPL_PARAMETER)) {
				System.out.println("Starting Datasheet Explorer...");
				DatasheetExplorer.main(new String[0]);
			}
			else if(equalsParam(args[i], LOGUI_PARAMETER)) {
				System.out.println("Starting Logging User Interface...");
				LoggingUI.main(new String[0]);
			}
			else if(equalsParam(args[i], HELP_PARAMETER)) {
				BaseApplet baseApplet = createContext();
				if(baseApplet.getRuntimeSupport().isJavaHelpAvailable()) {
					System.out.println("Starting Java Help...");
					baseApplet.getRuntimeSupport().showHelp();
				}
			}
			else {
				printHelp();
			}
		}
		if(args.length == 0)
			printHelp();
	}
	
	private static void printHelp() {
		System.out.println(
			"Tool options:\n" +
			getFullParam(VERSION_PARAMETER) + "\n" +
			"    Prints out the version of the library\n" +
			getFullParam(DSEXPL_PARAMETER) + "\n" +
			"    Starts the Datasheet Explorer for viewing Mumie Datasheets\n" +
			getFullParam(LOGUI_PARAMETER) + "\n" +
			"    Starts the Logging User Interface for setting up the logging framework" +
			"\n"
		);
	}
	
	private static boolean equalsParam(String orgParam, String paramBase) {
		return orgParam.equals("-" + paramBase) || orgParam.equals("--" + paramBase);
	}
	
	private static String getFullParam(String baseParam) {
		return "--" + baseParam;
	}
	
	private static BaseApplet createContext() {
		BaseApplet baseApplet = new BaseApplet() {
			public void reset() {}
		};
		baseApplet.init();
		return baseApplet;
	}
	
//	private static void exit(String errorMessage) {
//		System.err.println(errorMessage);
//		System.exit(-1);
//	}
}
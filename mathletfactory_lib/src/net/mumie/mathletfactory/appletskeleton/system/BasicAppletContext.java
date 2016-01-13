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

package net.mumie.mathletfactory.appletskeleton.system;

/**
 * This interface wraps the basic {@link java.applet.Applet} methods.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public interface BasicAppletContext {
	
	/**
	 * @see java.applet.Applet#init()
	 */
	void init();
	
	/**
	 * @see java.applet.Applet#start()
	 */
	void start();
	
	/**
	 * @see java.applet.Applet#stop()
	 */
	void stop();
	
	/**
	 * @see java.applet.Applet#destroy()
	 */
	void destroy();

	/**
	 * Returns the value of the named parameter or <code>null</code> if
	 * no such parameter was set.
	 * @param name name of the parameter
	 * @see java.applet.Applet#getParameter(java.lang.String)
	 */
	String getParameter(String name);
}

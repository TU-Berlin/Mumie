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

package net.mumie.mathletfactory.appletskeleton;

import java.util.Locale;

import javax.swing.JApplet;

import net.mumie.mathletfactory.appletskeleton.system.BasicAppletContext;
import net.mumie.mathletfactory.appletskeleton.system.MathletExerciseContext;
import net.mumie.mathletfactory.appletskeleton.system.MathletRuntime;
import net.mumie.mathletfactory.appletskeleton.system.MathletRuntimeSupport;
import net.mumie.mathletfactory.appletskeleton.system.MathletUIContext;

/**
 * This interface technically describes a <i>Mumie Mathlet</i> with all of its properties and capabilities.
 * It acts as a base interface for all mathlets.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public interface MathletContext extends BasicAppletContext, MathletUIContext, MathletExerciseContext {
	
	/**
	 * Returns the value of the named parameter or the default value if
	 * no such parameter was set.
	 * @param name name of the parameter
	 * @param defaultValue default value
	 * @see java.applet.Applet#getParameter(java.lang.String)
	 */
	String getParameter(String name, String defaultValue);
	
	/**
	 * Reads in and returns the value of the named parameter from the HTML tag or <code>null</code> if
	 * no such parameter was set.
	 * @param name name of the parameter
	 */
	String readParameter(String name);
	
	/**
	 * Returns the locale of this mathlet context.
	 */
	Locale getLocale();

	/**
	 * Returns this mathlet as a {@link JApplet}.
	 */
	JApplet getJApplet();
	
	/**
	 * Returns the runtime for this mathlet.
	 */
	MathletRuntime getMathletRuntime();
	
	/**
	 * Returns the runtime support for this mathlet context.
	 */
	MathletRuntimeSupport getRuntimeSupport();
	
	/**
	 * Returns the runtime class of this mathlet.
	 */
	Class getAppletClass();

	/**
	 * Returns the short class name of this mathlet (i.e. class name without package name).
	 */
	String getShortName();

	/**
	 * Returns the package name of this mathlet.
	 */
	String getPackageName();
	
	/**
	 * Returns the localized message for the given key or null
   * if no corresponding message is found neither in the global nor in the applet
   * specific messages file.
	 */
	String getMessage(String key);
	
  /**
   * Returns the localized parametrized message for the given key and parameters or <code>null</code>
   * if no corresponding message is found neither in the global nor in the applet
   * specific messages file.
   */
  String getMessage(String key, Object[] params);
  
	/**
	 * Reports an error to the mathlet runtime system.
	 * @see MathletRuntimeSupport#reportError(Throwable)
	 */
	void reportError(Throwable error);
	
	/**
	 * Returns whether errors have been reported to the mathlet runtime system.
	 * @see MathletRuntimeSupport#hasErrors()
	 */
	boolean hasErrors();
  
  /**
   * Sets all internal accessible fields to <code>null</code> in order to release 
   * any consumed heap space. This method should only be invoked from inside the 
   * {@link #destroy()} method of the applet. Note that only non-final and
   * non-private fields may be cleared.
   */
  void clearInternalFields();
}

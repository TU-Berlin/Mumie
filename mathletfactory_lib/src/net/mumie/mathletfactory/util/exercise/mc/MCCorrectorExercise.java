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

package net.mumie.mathletfactory.util.exercise.mc;

import net.mumie.cocoon.util.CocoonEnabledDataSheet;
import net.mumie.japs.datasheet.DataSheet;
import net.mumie.mathletfactory.util.exercise.CorrectorHelper;
import net.mumie.mathletfactory.util.text.UnicodeConverter;
import net.mumie.mathletfactory.util.xml.MathMLSerializable;
import net.mumie.mathletfactory.util.xml.marking.MarkingDocBuilder;

/**
 * This class is the base for all MC-Problem-Correctors.
 * It is a concrete implementation of {@link net.mumie.mathletfactory.util.exercise.mc.MCAbstractExercise}.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public class MCCorrectorExercise extends MCAbstractExercise {
	
	private CorrectorHelper m_helper;
	private DataSheet m_datasheet;
	
	/**
	 * Constructs a new instance for the given input and output datasheets.
	 */
	MCCorrectorExercise(CocoonEnabledDataSheet inputDS, CocoonEnabledDataSheet outputDS) {
		m_helper = new CorrectorHelper(inputDS, outputDS);
		m_datasheet = new DataSheet(inputDS.getDocument());
	}

	/**
	 * Returns the worker instance.
	 */
	protected CorrectorHelper getHelper() {
		return m_helper;
	}
	
	/**
	 * Informs the corrector worker that all evaluations have been finished. 
	 */
	protected void finish() {
		getHelper().finish();
	}
	
	/**
	 * Sets the score to the given value.
	 */
	protected void setScore(double score) {
		getHelper().setScore(score);
	}
	
	public DataSheet getQuestionSheet() {
		return m_datasheet;
	}
	
	public String getProblemString(String path) {
		return getHelper().getProblemString(path);
	}
	
	public boolean problemElementExists(String path) {
		return getHelper().problemElementExists(path);
	}
	
	public void loadElement(String path, MathMLSerializable mmobject) {
		getHelper().loadElement(path, mmobject);
	}
	
	public String getString(String path) {
		return getHelper().getString(path);
	}
	
	protected String preparseText(String text) {
		// replace "written" Unicode expressions with  Unicode characters (e.g. \u2200)
		return UnicodeConverter.getExpressedJavaUnicode(super.preparseText(text));
	}
	
	/**
	 * Returns the marking builder for constructing the marking part.
	 */
	protected MarkingDocBuilder getMarkingDocBuilder() {
    return getHelper().getMarkingDocBuilder();
  }
}

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

import net.mumie.japs.datasheet.DataSheet;
import net.mumie.mathletfactory.util.exercise.MumieExercise;
import net.mumie.mathletfactory.util.exercise.MumieExerciseIF;
import net.mumie.mathletfactory.util.text.UnicodeConverter;
import net.mumie.mathletfactory.util.xml.MathMLSerializable;

/**
 * This class is the base for all MC-Problem-Applets.
 * It is a concrete implementation of {@link net.mumie.mathletfactory.util.exercise.mc.MCAbstractExercise}.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public class MCAppletExercise extends MCAbstractExercise {

	private MumieExercise m_basicExercise;
	
	/**
	 * Constructs a new instance for the given exercise mathlet context.
	 */
	public MCAppletExercise(MumieExerciseIF exercise) {
		m_basicExercise = new MumieExercise(EXERCISE_MC_TYPE, exercise);
	}
	
	public DataSheet getQuestionSheet() {
		return getExercise().getQuestionSheet();
	}
	
	public String getProblemString(String path) {
		return getExercise().getProblemString(path);
	}
	
	public boolean problemElementExists(String path) {
		return getExercise().problemElementExists(path);
	}
	
	public void loadElement(String path, MathMLSerializable mmobject) {
		getExercise().loadElement(path, mmobject);
	}
	
	public String getString(String path) {
		return getExercise().getString(path);
	}
	
	/**
	 * Returns the worker instance.
	 */
	private MumieExercise getExercise() {
		return m_basicExercise;
	}
	
	/**
	 * Returns if this exercise is editable or not.
	 * The default implementation always returns <code>true</code>.
	 * @see net.mumie.mathletfactory.util.exercise.MumieExercise#isEditable()
	 */
	public boolean isEditable() {
		return getExercise().isEditable();
	}
	
	
	protected String preparseText(String text) {
		// replace "written" Unicode expressions with  Unicode characters (e.g. \u2200)
		return UnicodeConverter.getExpressedJavaUnicode(super.preparseText(text));
	}
	
	/**
	 * Informs the exercise worker to start a new saving process.
	 */
	public void save() {
		getExercise().save();
	}
	
	public void finishAnswerSheet() {
		setEditedFlag();
		getExercise().setLocalSheet(getAnswerSheet());
	}
}

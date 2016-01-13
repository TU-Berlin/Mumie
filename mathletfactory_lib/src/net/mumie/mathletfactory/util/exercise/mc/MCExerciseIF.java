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
import net.mumie.mathletfactory.util.xml.MathMLSerializable;
import net.mumie.mathletfactory.util.xml.XMLUtils;

/**
 * This interface describes the abstract functionalies of both applet and corrector exercise classes.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public interface MCExerciseIF extends MCProblemConstants {

	/**
	 * Returns the question sheet.
	 * @return an instance of {@link DataSheet}
	 */
	DataSheet getQuestionSheet();
	
	/**
	 * Returns the answer type for the specified question.
	 * @param questionNr the question's number
	 * @return the answer type (see {@link MCProblemConstants})
	 * @throws MCProblemException if no valid TYPE element is found for the given question
	 */
	int getAnswerType(int questionNr);
	
	/**
	 * Loads the question task for the specified question from a datasheet.
	 * @param questionNr the question's number
	 * @return the task text
	 */
	String loadQuestionTask(int questionNr);
	
	/**
	 * Returns the total number of questions.
	 */
	int getQuestionCount();
	
	/**
	 * Returns the number of answers/assertions for the specified question.
	 * @param questionNr the question's number
	 */
	int getAnswerCount(int questionNr);
	
	/**
	 * Loads the number class for the specified question from a datasheet.
	 * @param questionNr the question's number
	 * @see XMLUtils#getNumberClassFromField(String)
	 */
	Class loadNumberClass(int questionNr);
	
	/**
	 * Loads the answer/assertion text for the specified question and answer from a datasheet.
	 * @param questionNr the question's number
	 * @param answerNr the answer's number
	 * @return the answer/assertion text
	 */
	String loadAssertion(int questionNr, int answerNr);
	
	/**
	 * Returns whether a problem element exists under the specified path in the question sheet.
	 * @param path a path in the question sheet below the common problem path
	 */
	boolean problemElementExists(String path);
	
	/**
	 * Loads the MathML content under the specified absolute path in the question sheet into the given MathML-object.
	 * @param path an absolute path in the question sheet
	 * @param mmobject an instance of {@link MathMLSerializable}
	 */
	void loadElement(String path, MathMLSerializable mmobject);
	
	/**
	 * Returns a problem string with the specified path from the question sheet or <code>null</code> 
	 * if no such XML element exists.
	 * @param path a path in the question sheet below the common problem path
	 */
	String getProblemString(String path);
	
	/**
	 * Returns a problem string with the specified absolute path from the question sheet or <code>null</code> 
	 * if no such XML element exists.
	 * @param path an absolute path in the question
	 */
	String getString(String path);
}

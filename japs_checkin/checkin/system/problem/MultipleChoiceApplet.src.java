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

package net.mumie.mathlet;

import net.mumie.japs.datasheet.DataSheet;
import net.mumie.mathletfactory.appletskeleton.BaseApplet;
import net.mumie.mathletfactory.util.exercise.MchoiceForm;
import net.mumie.mathletfactory.util.exercise.MultipleChoiceExerciseIF;
import net.mumie.mathletfactory.util.exercise.MumieExercise;
import net.mumie.mathletfactory.util.xml.XMLUtils;

/**
 * This class serves as a wrapper object for HTML forms that are intended to be saved 
 * in the same manner as exercise applet answers.
 * 
 * @author Markus Gronau <gronau@math.tu-berlin.de>
 * @mm.type applet
 * @mm.section content/misc/media/applets
 * @mm.copyright Copyright (c) 2006-2007 by Technische Universitaet Berlin
 * @mm.requireJar system/libraries/jar_mathlet_factory.meta.xml
 * @mm.sign true
 * @mm.docstatus finished
 * @mm.status devel_ok
 * @mm.description Multiple Choice Applet
 * @mm.rating none
 * @mm.changelog 2006-04-10: Initial
 * @mm.width 200
 * @mm.height 50
 */
public class MultipleChoiceApplet extends BaseApplet implements MultipleChoiceExerciseIF {

	private MchoiceForm form;
	private MumieExercise exercise;
	
	public void init() {
		try {
			super.init();
			
			exercise = new MumieExercise(this);
			
			form = new MchoiceForm();
			form.readDataSheet(exercise.getQuestionSheet());
			
		} catch(Throwable t) {
			reportError(t);
		}
	}
	
	public boolean collectAnswers() {
		try {
			DataSheet localSheet = XMLUtils.createDataSheet();
			form.toDataSheet(localSheet);
			exercise.setLocalSheet(localSheet);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
//
// Wrapper methods defined in interface above
//
	
	public void setControl (String name, String value) {
		form.setControl(name, value);
	}
	
	public void unsetControl (String name) {
		form.unsetControl(name);
	}
	
	public void toggleControl (String name, String value) {
		form.toggleControl(name, value);
	}
	
	/**
	 * Empty implementation.
	 */
	public void clearSubtask() {}
	
	/**
	 * Empty implementation.
	 */
	public void reset() {}

}

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

package net.mumie.mathlet.generic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import net.mumie.japs.datasheet.DataSheet;
import net.mumie.mathletfactory.appletskeleton.NoCanvasApplet;
import net.mumie.mathletfactory.util.exercise.ExerciseConstants;
import net.mumie.mathletfactory.util.exercise.MumieExercise;
import net.mumie.mathletfactory.util.exercise.MumieExerciseIF;
import net.mumie.mathletfactory.util.xml.XMLUtils;

/**
 * @author Markus Gronau <gronau@math.tu-berlin.de>
 * @mm.type applet
 * @mm.section content/lineare_algebra/media/applets
 * @mm.copyright Copyright (c) 2008 by Technische Universitaet Berlin
 * @mm.requireJar system/libraries/jar_mathlet_factory.meta.xml
 * @mm.sign true
 * @mm.docstatus 
 * @mm.status devel_ok
 * @mm.description generic applet for text input problems
 * @mm.rating none
 * @mm.changelog 
 * @mm.width 600
 * @mm.height 800
*/
public class DefaultTextInputProblemApplet extends NoCanvasApplet implements MumieExerciseIF, ExerciseConstants {

  public final static int CANNOT_READ_FILE_DIALOG_CONSTANT = 1001;
  
	/** Field holding the exercise worker instance. */
	private MumieExercise m_exercise;
	
	/** Field holding the datasheet for HTML answers. */
	private DataSheet m_answerSheet;
	
	public void init() {
		super.init();
		try {
			// create exercise instance
			m_exercise = new MumieExercise(this);
			// create answer sheet
			m_answerSheet = XMLUtils.createDataSheet();
		} catch(Throwable t) {
			reportError(t);
		}
	}

	public boolean collectAnswers() {
		// write answers to outgoing datasheet
		m_exercise.setLocalSheet(m_answerSheet);
		// replace old answer sheet with new one
		m_answerSheet = XMLUtils.createDataSheet();
		return true;
	}
	
	/**
	 * Saves the given text under the "user/answer" path below the specified sub path.
	 * If the applet is not fully initialized yet, this method does nothing.
	 * 
	 * @param subPath a path below "user/answer"
	 * @param text the text to be saved; may not be <code>null</code>
	 * @throws IllegalArgumentException if the sub path or the text are <code>null</code>
	 */
	public void setTextAnswer (String subPath, String text) throws IllegalArgumentException {
		try {
			// check if applet is initialized
			if(!isActive())
				return;// not yet, do nothing
			if(subPath == null)
				throw new IllegalArgumentException("Sub path for text answer must not be null !");
			if(text == null)
				throw new IllegalArgumentException("Text answer must not be null !");
			// write the answer text below the sub path under "user/answer"
			m_answerSheet.put(USER_ANSWER_PATH + PATH_SEPARATOR + subPath, text);
		} catch(Throwable t) {
			reportError(t);
		}
	}
  
  /**
   * Saves the content of a file under the "user/answer" path below the specified sub path by
   * showing an open file dialog. Canceling the dialog does nothing. 
   * If the applet is not fully initialized yet, this method does nothing.
   * 
   * @param subPath a path below "user/answer"
   * @throws IllegalArgumentException if the sub path or the text are <code>null</code>
   */
  public void readTextAnswerFromFile (String subPath) {
    try {
      // check if applet is initialized
      if(!isActive())
        return;// not yet, do nothing
      JFileChooser chooser = new JFileChooser();
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      chooser.setMultiSelectionEnabled(false);
      chooser.setFileFilter(new FileFilter() {
        public boolean accept(File f) {
          if(f.isDirectory())
            return true;
          if(f.isFile() && f.getName().endsWith(".java"))
            return true;
          return false;
        }

        public String getDescription() {
          return "Java-Quell-Dateien (*.java)";
        }
      });
      int choice = chooser.showOpenDialog(this);
      if(choice == JFileChooser.APPROVE_OPTION) { // do nothing on canceling
        File file = chooser.getSelectedFile();
        FileInputStream in = new FileInputStream(file);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int bytes = 0;
        byte[] data = new byte[1024];
        while( (bytes = in.read(data)) > -1) {
          out.write(data, 0, bytes);
        }
        in.close();
        setTextAnswer(subPath, out.toString());
      }
    } catch (IOException e) {
      showDialog(CANNOT_READ_FILE_DIALOG_CONSTANT, new Object[] { e.getLocalizedMessage() });
    }
  }

	public void clearSubtask() {} // should not be called

	public void reset() {} // should not be called
}

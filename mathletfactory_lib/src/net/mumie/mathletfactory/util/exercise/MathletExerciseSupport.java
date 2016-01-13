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

package net.mumie.mathletfactory.util.exercise;

import java.io.File;

import net.mumie.cocoon.util.ProblemCorrector;
import net.mumie.mathletfactory.appletskeleton.MathletContext;

public class MathletExerciseSupport {
	
  private final static String CORRECTOR_CLASSES_LOCATION = "net.mumie.corrector";

	private final static int INACTIVE_MODE = -1;
	
	private final static int LOCAL_PREVIEW_MODE = 0;
	
	private final static int LOCAL_DEBUG_MODE = 1;
	
	private final static int REMOTE_JAPS_MODE = 2;
	
	
	private final MathletContext m_mathlet;
	
	private final boolean m_isHomeworkMode;
	
  private final int m_mode;
  

	private MumieExercise m_exercise;
	
  private MultipleTasksIF m_multipleTasksMathlet;

  private SelectableDataIF m_selectableDataMathlet;
	
	private String m_selectedData = null;
  
	private int m_selectedSubtask = -1;
	
	private boolean m_save = false;
	
	private boolean m_showAppletFrame = false;
	
	public MathletExerciseSupport(MathletContext mathlet) {
		m_mathlet = mathlet;
		
    // check if subtasks are selectable from html
    if(mathlet instanceof MultipleTasksIF)
      m_multipleTasksMathlet = (MultipleTasksIF) mathlet;
    
    // check if data is selectable from html
    if(mathlet instanceof SelectableDataIF)
      m_selectableDataMathlet = (SelectableDataIF) mathlet;

    // activating homework mode
    if(mathlet.getParameter(ExerciseConstants.HOMEWORK_MODE_PARAM) != null
    		&& isJAPSClientAvailable()
    		&& isJAPSDatasheetAvailable()) {
    	m_isHomeworkMode = true;
    	String homeworkModeParam = mathlet.getParameter(ExerciseConstants.HOMEWORK_MODE_PARAM);
    	if(homeworkModeParam.equals(ExerciseConstants.REMOTE_JAPS_MODE)) {
    		m_mode = REMOTE_JAPS_MODE;
        if (mathlet.getParameter(ExerciseConstants.URL_PREFIX_PARAM) == null)
        	throw new RuntimeException("Applet parameter \"" + ExerciseConstants.URL_PREFIX_PARAM + "\" must be set in remote japs mode !");
        if (mathlet.getParameter(ExerciseConstants.PROBLEM_REF_PARAM) == null)
        	throw new RuntimeException("Applet parameter \"" + ExerciseConstants.PROBLEM_REF_PARAM + "\" must be set in remote japs mode !");
    	}
    	else if(homeworkModeParam.equals(ExerciseConstants.LOCAL_DEBUG_MODE)) {
	  		m_mode = LOCAL_DEBUG_MODE;
	  		if( !(mathlet instanceof MultipleChoiceExerciseIF)) {// for testing purposes 
	        if(mathlet.getParameter(ExerciseConstants.INPUT_DATASHEET_PARAM) == null)
	        	throw new RuntimeException("Applet parameter \"" + ExerciseConstants.INPUT_DATASHEET_PARAM + "\" must be set in local debug mode !");
	  		}
	  	}
    	else if(homeworkModeParam.equals(ExerciseConstants.LOCAL_PREVIEW_MODE)) {
	  		m_mode = LOCAL_PREVIEW_MODE;
        if (mathlet.getParameter(ExerciseConstants.INPUT_DATASHEET_PARAM) == null)
        	throw new RuntimeException("Applet parameter \"" + ExerciseConstants.INPUT_DATASHEET_PARAM + "\" must be set in local preview mode !");
        if (mathlet.getParameter(ExerciseConstants.OUTPUT_DATASHEET_PARAM) == null)
        	throw new RuntimeException("Applet parameter \"" + ExerciseConstants.OUTPUT_DATASHEET_PARAM + "\" must be set in local preview mode !");
	  	} else
	  		throw new RuntimeException("Applet parameter \"" + ExerciseConstants.HOMEWORK_MODE_PARAM + "\" cannot be recognized: " + homeworkModeParam);
    	
      // start selection thread if necessary
      if(mathlet instanceof MumieExerciseIF)
        new SelectionHelperThread().startRunning();
	  } else { // homework mode is disabled / inactive
	  	m_isHomeworkMode = false;
	  	m_mode = INACTIVE_MODE;
	  }
	}
		
	void setExercise(MumieExercise exercise) {
		m_exercise = exercise;
	}
	
	public MumieExercise getExercise() {
		return m_exercise;
	}
	
	public ProblemCorrector loadCorrector() throws Throwable {
		String correctorClass = m_mathlet.getParameter(ExerciseConstants.CORRECTOR_CLASS_PARAM);
		if(correctorClass == null)
			correctorClass = CORRECTOR_CLASSES_LOCATION + "." + m_mathlet.getShortName();
		return loadCorrector(null, correctorClass);
	}
	
	protected ProblemCorrector loadCorrector(File correctorFile, String correctorClass) throws Throwable {
		return (ProblemCorrector) m_mathlet.getRuntimeSupport().loadClass(correctorFile, correctorClass);
	}

	public synchronized void save() {
		m_save = true;
	}
	
	public synchronized void showAppletFrame() {
		m_showAppletFrame = true;
	}
	
  public synchronized String getSelectedData() {
    return m_selectedData;
  }
  
  public synchronized void selectSubtask(int subtaskNr) {
    m_selectedSubtask = subtaskNr;
  }

  public synchronized int getSelectedSubtask() {
    return m_selectedSubtask;
  }
  
  public synchronized void selectData(String path) {
    m_selectedData = path;
  }

  public boolean isHomeworkMode() {
  	return m_isHomeworkMode;
  }
	
	public boolean isDebugMode() {
		return m_mode == LOCAL_DEBUG_MODE;
	}
	
	public boolean isPreviewMode() {
		return m_mode == LOCAL_PREVIEW_MODE;
	}
	
	public boolean isRemoteMode() {
		return m_mode == REMOTE_JAPS_MODE;
	}

  /** Checks, whether the library JAPS Datasheet is available. */
  public boolean isJAPSDatasheetAvailable() {
    try {
      Class.forName("net.mumie.japs.datasheet.DataSheet");
    } catch (ClassNotFoundException e) {
      return false;
    } catch (Throwable e) {
    	System.err.println("Error while loading class DataSheet: " + e);
      return false;
    }
    return true;
  }

  /** Checks, whether the library JAPS Client is available. */
  public boolean isJAPSClientAvailable() {
    try {
      Class.forName("net.mumie.japs.client.JapsClient");
    } catch (ClassNotFoundException e) {
      return false;
    } catch (Throwable e) {
    	System.err.println("Error while loading class JapsClient: " + e);
      return false;
    }
    return true;
  }
  
  class SelectionHelperThread extends Thread {

    boolean m_run = true;

    public void run() {
      while (m_run) {
        try {
        	if(m_mathlet.getJApplet().isActive()) {
	          if (m_selectableDataMathlet != null
	              && getSelectedData() != null) {
	          	try {
	          		m_selectableDataMathlet.selectElement(getSelectedData());
	          	} catch(Throwable t) {
	          		m_mathlet.reportError(t);
	          	}
	            m_selectableDataMathlet.selectData(null);
	          }
	          if (m_multipleTasksMathlet != null
	              && getSelectedSubtask() != -1) {
	          	try {
	          		m_multipleTasksMathlet
	          				.selectTask(getSelectedSubtask());
	          		m_exercise.setCurrentSubtask(getSelectedSubtask());
	          	} catch(Throwable t) {
	          		m_mathlet.reportError(t);
	          	}
	            m_multipleTasksMathlet.selectSubtask(-1);
	          }
	          if(m_save) {
	          	try {
	          		m_exercise.save();
	          	} catch(Throwable t) {
	          		m_mathlet.reportError(t);
	          	}
	          	m_save = false;
	          }
	          if(m_showAppletFrame) {
	          	try {
	          		m_mathlet.getRuntimeSupport().showContentFrame();
	          	} catch(Throwable t) {
	          		m_mathlet.reportError(t);
	          	}
	          	m_showAppletFrame = false;
	          }
        	}
          if(Thread.currentThread().isAlive()) // avoid InterruptedExceptions
          	Thread.sleep(100);// these values will be checked every 100ms
        } catch (InterruptedException e) { // may happen if applet is destroyed
          stopRunning();
        }
      }
    }

    public void stopRunning() {
      m_run = false;
    }

    public void startRunning() {
      m_run = true;
      start();
    }
  }
}

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

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalButtonUI;

import net.mumie.mathletfactory.appletskeleton.NoCanvasApplet;
import net.mumie.mathletfactory.appletskeleton.util.ControlPanel;
import net.mumie.mathletfactory.util.exercise.MumieExerciseIF;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;
import net.mumie.mathletfactory.util.xml.DatasheetRenderer;

/**
 * This class is the base for all generic and custom MC-Problem-Applets.
 * Sub-classing applets must only implement the method {@link #initQuestion(int)}.
 *
 * @see #initQuestion(int)
 *
 * @author gronau
 * @mm.docstatus finished
 */
public class MCProblemApplet extends NoCanvasApplet implements MumieExerciseIF, MCProblemConstants {

	private final static MumieLogger LOGGER = MumieLogger.getLogger(MCProblemApplet.class);
	private final static LogCategory MC_CATEGORY = LOGGER.getCategory("exercise.mc");
	
	public final static Color BACKGROUND_COLOR = Color.WHITE;
	public final static int MARGIN = 10;
	
	/** Field holding the worker instance. */
	private MCAppletExercise m_exercise;
	
	/** Field holding the panel for every question. */
	private MCQuestionPanel[] m_questionPanels;
	
	private ControlPanel m_contentPane;
	
	public final void init() {
		try {
			super.init();
			m_contentPane = new ControlPanel();
			// disable help and reset buttons
			setBottomButtonsVisible(false);
			// set background color
      getMCContentPane().setBackground(BACKGROUND_COLOR);
			// hide text (even if it is empty)
			setTitleVisible(false);
			// remove bevel border
			getCenterTabbedPanel().setBorder(null);
			// initialize worker instance of MumieExercise
			m_exercise = createExercise();
			LOGGER.log(MC_CATEGORY, "Initializing MC-Problem-Applet for " + getQuestionCount() + " questions");
			// set up questions in GUI
			createQuestions();
			// load answers and adjust GUI elements accordingly
			loadAnswers();
			// only show scrollbars and border if necessary
			Dimension controlPanelDim = getMCContentPane().getPreferredSize();
			// add margin to pref size
			controlPanelDim.width += MARGIN;
			controlPanelDim.height += MARGIN;
			Dimension contentPaneDim = getMyContentPane().getSize();
			if(contentPaneDim.height < controlPanelDim.height || contentPaneDim.width < controlPanelDim.width) {
				// activate scroll bars
        getMCContentPane().setScrollable(true);
				if(isDebugMode())
					System.err.println("Warning: mc-applet size is to small, minimal size should be: [width=" + controlPanelDim.width + ",height=" + controlPanelDim.height + "]");
			} else {
				// remove border from applet
				((JComponent) getMyContentPane()).setBorder(null);
			}
			// replace default control panel with new one
			getControlTabbedPanel().addTab("", getMCContentPane());
			getControlTabbedPanel().remove(getControlPanel());
		} catch(Throwable t) {
			reportError(t);
		}
	}
  
  /**
   * Creates the exercise instance for this applet.
   */
  protected MCAppletExercise createExercise() {
    return new MCAppletExercise(this);
  }
  
  /**
   * Returns the control panel for this MC applet.
   */
  public ControlPanel getMCContentPane() {
    return m_contentPane;
  }
	
  /**
   * Creates the question for the GUI.
   */
  private void createQuestions() {
    getMCContentPane().setLeftAlignment();
    m_questionPanels = new MCQuestionPanel[getQuestionCount()];
    createQuestions(m_questionPanels);
    // create bottom buttons
    MCStyleButton saveButton = new MCStyleButton(getMessage("exercise.save"));
    saveButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          getExercise().save();
        } catch(Throwable t) {
          reportError(t);
        }
      }
    });
    MCStyleButton clearButton = new MCStyleButton(getMessage("Reset"));
    clearButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        reset();
      }
    });
    // add the buttons at the bottom
    if(getExercise().isEditable()) {
      getMCContentPane().insertLineBreaks(2);
      getMCContentPane().add(saveButton);
      getMCContentPane().add(clearButton);
    }
    if(isDebugMode()) {
      getMCContentPane().add(new DebugButton());
    }
    getMCContentPane().insertLineBreak();
  }
  
  /**
   * Creates and adds the task and answers for every question to the GUI.
   * Basically calls {@link #initQuestion(int)}.
   */
  protected void createQuestions(MCQuestionPanel[] panels) {
    for(int question = 1; question <= getQuestionCount(); question++) {
      panels[question - 1] = createQuestion(question);
      // add vertical space between 2 questions
      if(question < getQuestionCount())
        getMCContentPane().insertLineBreaks(2);
      // initialize question components
      initQuestion(question);
    }
  }
  
	/**
	 * Creates and adds the task and answers for the given question to the GUI.
	 */
  protected MCQuestionPanel createQuestion(int questionNr) {
		int answerType = getExercise().getAnswerType(questionNr);
		if(LOGGER.isActiveCategory(MC_CATEGORY)) {
			LOGGER.log(MC_CATEGORY, "Reading answer type \"" + MCAbstractExercise.getAnswerTypeName(answerType) + "\" for question " + questionNr);
		}
		MCQuestionPanel panel = new MCQuestionPanel(answerType, getExercise().loadPrecision(questionNr), getExercise().isEditable());
    // add the only answer for text questions
    if(answerType == TEXT_ANSWER_TYPE) {
      int[] dim = getExercise().parseTextSizeParameters(questionNr);
      panel.createTextAnswer(dim[0], dim[1]);
    }
		// add question panel to applet
    getMCContentPane().add(panel);
    return panel;
	}
	
	/**
	 * Returns the total number of questions.
	 */
	protected int getQuestionCount() {
		return getExercise().getQuestionCount();
	}
	
	/**
	 * Initializes the task and all answers/assertions for the specified question.
	 * This method is intented to be subclassed by custom MC-Problem-Applets for all or only some questions.
	 * @param questionNr the question's number
	 */
	protected void initQuestion(int questionNr) {
		setTask(questionNr, getExercise().loadQuestionTask(questionNr));
		int answerCount = getExercise().getAnswerCount(questionNr);
		LOGGER.log(MC_CATEGORY, "Reading " + answerCount + " answers for question " + questionNr);
		for(int i = 1; i <= answerCount; i++) {
			addAssertion(questionNr, getExercise().loadAssertion(questionNr, i));
		}
	}
	
	/**
	 * Returns the worker instance.
	 */
	protected MCAppletExercise getExercise() {
		return m_exercise;
	}
	
	/**
	 * Returns the question panel for the specified question.
	 * @param questionNr the question's number
	 * @return an instance of {@link MCQuestionPanel}
	 */
	protected MCQuestionPanel getQuestionPanel(int questionNr) {
		return m_questionPanels[questionNr-1];
	}
	
	/*
	 *  Methods for creating the answers
	 */
	
	/**
	 * Sets the task component for the specified question.
	 * @param questionNr the question's number
	 */
	protected void setTask(int questionNr, Component taskComponent) {
		getQuestionPanel(questionNr).setTask(taskComponent);
	}
	
	/**
	 * Sets the task text for the specified question.
	 * @param questionNr the question's number
	 */
	protected void setTask(int questionNr, String taskText) {
		getQuestionPanel(questionNr).setTask(getExercise().parseText(taskText, questionNr, -1));
	}
	
	/**
	 * Adds the answer/assertion component for the specified question.
	 * @param questionNr the question's number
	 */
	protected void addAssertion(int questionNr, Component assertionComponent) {
		getQuestionPanel(questionNr).addAssertion(assertionComponent);
	}
	
	/**
	 * Adds the answer/assertion text for the specified question.
	 * @param questionNr the question's number
	 */
	protected void addAssertion(int questionNr, String assertionText) {
		getQuestionPanel(questionNr).addAssertion(getExercise().parseText(assertionText, questionNr, getQuestionPanel(questionNr).getAnswerCount() + 1));
	}
		
  /* Basically calls {@link #getQuestionPanel(int).clearAnswers()}. */
	public void reset() {
		for(int q = 1; q <= getQuestionCount(); q++) {
			getQuestionPanel(q).clearAnswers();
		}
	}
	
  /**
   * Loads the saved answers and adjusts the necessary GUI elements.
   * Basically calls {@link #loadAnswer(int)}.
   */
  protected void loadAnswers() {
    for(int q = 1; q <= getQuestionCount(); q++) {
      loadAnswer(q);
    }
  }
  
	/**
	 * Loads the saved answer for the given question.
	 */
  protected void loadAnswer(int questionNr) {
		int answerType = getExercise().getAnswerType(questionNr);
		switch(answerType) {
		case UNIQUE_ANSWER_TYPE:
			loadUniqueAnswer(questionNr);
			break;
		case MULTIPLE_ANSWER_TYPE:
			loadMultipleAnswers(questionNr);
			break;
		case YESNO_ANSWER_TYPE:
			loadYesNoAnswers(questionNr);
			break;
    case TEXT_ANSWER_TYPE:
      loadTextAnswer(questionNr);
      break;
		}
	}
	
	/**
	 * Loads and adjusts the saved "unique" answers for the specified question.
	 * @param questionNr the question's number
	 */
	private void loadUniqueAnswer(int questionNr) {
		Integer uniqueAnswer = getExercise().getIntegerAnswer(QUESTION_PREFIX + questionNr);
		// only save edited answer (i.e. != null)
		if(uniqueAnswer != null) {
			getQuestionPanel(questionNr).setUniqueAnswer(uniqueAnswer.intValue());
		}
	}

	/**
	 * Loads and adjusts the saved "multiple" answers for the specified question.
	 * @param questionNr the question's number
	 */
	private void loadMultipleAnswers(int questionNr) {
		Vector selected = new Vector();
		for(int a = 1; a <= getExercise().getAnswerCount(questionNr); a++) {
			Boolean answer = getExercise().getBooleanAnswer(QUESTION_PREFIX + questionNr + PATH_SEPARATOR 
					+ ANSWER_PREFIX + a);
			if(answer != null && answer.booleanValue())
				selected.add(new Integer(a));
		}
		int[] answers = new int[selected.size()];
		for(int i = 0; i < answers.length; i++)
			answers[i] = ((Integer) selected.get(i)).intValue();
		if(answers.length > 0)
			getQuestionPanel(questionNr).setMultipleAnswers(answers);
	}

	/**
	 * Loads and adjusts the saved "yesno" answers for the specified question.
	 * @param questionNr the question's number
	 */
	private void loadYesNoAnswers(int questionNr) {
		Boolean[] answers = new Boolean[getExercise().getAnswerCount(questionNr)];
		for(int i = 1; i <= answers.length; i++) {
			Boolean answer = getExercise().getBooleanAnswer(QUESTION_PREFIX + questionNr + PATH_SEPARATOR 
					+ ANSWER_PREFIX + i);
			if(answer != null)
				answers[i - 1] = answer;
		}
		if(answers.length > 0)
			getQuestionPanel(questionNr).setYesNoAnswers(answers);
	}

  /**
   * Loads the saved text answer for the specified question.
   * @param questionNr the question's number
   */
  private void loadTextAnswer(int questionNr) {
      String answer = getExercise().getTextAnswer(questionNr);
      if(answer != null)
        getQuestionPanel(questionNr).setTextAnswer(answer);
  }

  /* Basically calls {@link #collectAnswers(int)}. */
	public boolean collectAnswers() {
		// clear and create "own" MC-Problem answer sheet
		getExercise().clearAnswerSheet();
		// save answers per question
		for(int question = 1; question <= getQuestionCount(); question++) {
      collectAnswers(question);
		}
		// save answers to "real" answer sheet in MumieExercise
		getExercise().finishAnswerSheet();
		// no errors occured
		return true;
	}
  
  /**
   * Collects the answers for the given question.
   */
  protected void collectAnswers(int questionNr) {
    int answerType = getExercise().getAnswerType(questionNr);
    switch(answerType) {
    case UNIQUE_ANSWER_TYPE:
      collectUniqueAnswer(questionNr);
      break;
    case MULTIPLE_ANSWER_TYPE:
      collectMultipleAnswers(questionNr);
      break;
    case YESNO_ANSWER_TYPE:
      collectYesNoAnswers(questionNr);
      break;
    case TEXT_ANSWER_TYPE:
      collectTextAnswer(questionNr);
      break;
    }
  }
	
	/**
	 * Collects the unique answer from the specified question.
	 * @param questionNr the number of the specified question
	 */
	private void collectUniqueAnswer(int questionNr) {
		MCQuestionPanel panel = getQuestionPanel(questionNr);
		int uniqueAnswer = panel.getUniqueAnswer();
		// only save edited answer (i.e. != -1)
		if(uniqueAnswer != -1)
			getExercise().setAnswer(QUESTION_PREFIX + questionNr, uniqueAnswer);
	}

	/**
	 * Collects the multiple answers from the specified question.
	 * @param questionNr the number of the specified question
	 */
	private void collectMultipleAnswers(int questionNr) {
		MCQuestionPanel panel = getQuestionPanel(questionNr);
		int[] multipleAnswers = panel.getMultipleAnswers();
		for(int i = 0; i < multipleAnswers.length; i++) {
			getExercise().setAnswer(QUESTION_PREFIX + questionNr + PATH_SEPARATOR 
					+ ANSWER_PREFIX + multipleAnswers[i], true);
		}
	}

	/**
	 * Collects the yes/no answers from the specified question.
	 * @param questionNr the number of the specified question
	 */
	private void collectYesNoAnswers(int questionNr) {
		MCQuestionPanel panel = getQuestionPanel(questionNr);
		Boolean[] yesnoAnswers = panel.getYesNoAnswers();
		for(int i = 0; i < yesnoAnswers.length; i++) {
			if(yesnoAnswers[i] != null)
				getExercise().setAnswer(QUESTION_PREFIX + questionNr + PATH_SEPARATOR 
						+ ANSWER_PREFIX + (i + 1), yesnoAnswers[i].booleanValue());
		}
	}

  /**
   * Collects the text answer from the specified question.
   * @param questionNr the number of the specified question
   */
  private void collectTextAnswer(int questionNr) {
    String answer = getQuestionPanel(questionNr).getTextAnswer();
    if(answer.trim().length() > 0)
      getExercise().setAnswer(QUESTION_PREFIX + questionNr + PATH_SEPARATOR 
          + TEXT_ANSWER_PREFIX, answer);
  }

	public void clearSubtask() {
		reset();
	}
	
	class MCStyleButton extends JButton {
		MCStyleButton(String text) {
			super(text);
			setFont(getFont().deriveFont(Font.PLAIN));
			setBackground(new Color(220, 220, 220));
			setBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(3, 10, 3, 10)));
      setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		
	  public void updateUI() {
	    setUI(MCButtonUI.createUI(this));
	  }			
	}
	
	static class MCButtonUI extends MetalButtonUI {
		private final static MCButtonUI m_staticButtonUI = new MCButtonUI();
	  public static ComponentUI createUI(JComponent c) {
	  	return m_staticButtonUI;
	  }
	}
	
	class DebugButton extends MCStyleButton {
		
		DebugButton() {
			super("Debug");
			this.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent ae) {
					DatasheetRenderer renderer = new DatasheetRenderer(getExercise().getQuestionSheet());
					JFrame frame = new JFrame("Datasheet Renderer");
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					frame.getContentPane().add(renderer);
		      frame.pack();
		      frame.setSize(600, 500);
		      frame.setVisible(true);
	      }
      });
		}
	}
}

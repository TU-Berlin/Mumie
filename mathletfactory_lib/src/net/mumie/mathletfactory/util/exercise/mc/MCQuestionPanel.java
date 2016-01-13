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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;

import net.mumie.mathletfactory.appletskeleton.util.ControlPanel;
import net.mumie.mathletfactory.display.layout.MatrixLayout;
import net.mumie.mathletfactory.display.noc.MMPanel;
import net.mumie.mathletfactory.display.noc.function.MMFunctionPanel;
import net.mumie.mathletfactory.display.noc.op.OperationPanel;
import net.mumie.mathletfactory.display.util.TextPanel;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.MRealNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.math.util.MString;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.analysis.function.MMFunctionDefByOp;
import net.mumie.mathletfactory.mmobject.analysis.function.multivariate.MMFunctionOverR2;
import net.mumie.mathletfactory.util.ResourceManager;
import net.mumie.mathletfactory.util.xml.ExerciseObjectIF;

/**
 * This class represents the graphic representation of a single question including task and answers inside 
 * the MC-Problem-Applet Framework. It mainly acts as a helper class for the MC-Problem-Applet itself.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public class MCQuestionPanel extends JPanel implements MCProblemConstants {

	private final int m_answerType, m_precision;
	private final boolean m_isEditable;
	private JPanel m_answerTable;
	private MatrixLayout m_tableLayout;
	private Vector m_answers = new Vector();
	private ButtonGroup m_uniqueRadioButtonGroup = new ButtonGroup();
	
	/**
	 * Contructs a new question panel for the specified answer type.
	 * @param answerType an answer type constant (see {@link MCProblemConstants})
	 */
	public MCQuestionPanel(int answerType, int precision, boolean isEditable) {
		m_answerType = answerType;
		m_precision = precision;
		m_isEditable = isEditable;
		m_tableLayout = new MatrixLayout(0, getColumnCount());
		m_answerTable = new JPanel(m_tableLayout);
		m_answerTable.setOpaque(false);
		JPanel tableResizer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tableResizer.setOpaque(false);
		tableResizer.add(m_answerTable);
		setLayout(new BorderLayout());
		setOpaque(false);
		add(tableResizer, BorderLayout.CENTER);
	}
	
	private Component createAnswerBox() {
		switch(m_answerType) {
		case UNIQUE_ANSWER_TYPE:
			return new UniqueAnswerBox(m_isEditable);
		case MULTIPLE_ANSWER_TYPE:
			return new MultipleAnswerBox(m_isEditable);
		case YESNO_ANSWER_TYPE:
			return new YesNoAnswerBox(m_isEditable);
//    case TEXT_ANSWER_TYPE: // not used here
//      return new TextAnswerBox(-1 ,-1 , m_isEditable);
		}
		// should never be called
		return null;
	}
	
	/**
	 * Sets the task component.
	 */
	public void setTask(Component taskComponent) {
		add(taskComponent, BorderLayout.NORTH);
	}
	
	/**
	 * Sets the task text.
	 */
	public void setTask(ExerciseObjectIF[] entries) {
		setTask(createTextComponent(entries));
	}
	
	public Component createTextComponent(ExerciseObjectIF[] entries) {
		ControlPanel cp = new ControlPanel();
		cp.setOpaque(false);
		cp.setLeftAlignment();
		for(int i = 0; i < entries.length; i++) {
			// insert line breaks
			if(entries[i] instanceof MString && ((MString) entries[i]).getValue().equals("\\\\")) {
				cp.insertLineBreak();
				continue;
			}
			JComponent viewer = createViewer(entries[i]);
			cp.add(viewer);
		}
		return cp;
	}
	
	private JComponent createViewer(ExerciseObjectIF object) {
		try {
			JComponent viewer = null;
			// return string's panel
			if(object instanceof MString)
				viewer =  new TextPanel(((MString) object).getValue());
			// return number's panel
			if(object instanceof MNumber)
				viewer =  NumberFactory.getNewMMInstanceFor((MNumber) object).getAsContainerContent();
			// return function's panel
			if(object instanceof Operation) {
				Operation operation = (Operation) object;
				MMObjectIF function = null;
				if(MRealNumber.class.isAssignableFrom(operation.getNumberClass())) {
					function = new MMFunctionDefByOp(operation.getNumberClass(), "0");
					// workaround for normalize bug
					((MMFunctionDefByOp) function).setNormalForm(operation.isNormalForm());
					((MMFunctionDefByOp) function).setOperation(operation);
					// end of workaround
				} else {
					function = new MMFunctionOverR2(operation.getNumberClass(), "0");
					// workaround for normalize bug
					((MMFunctionOverR2) function).setNormalForm(operation.isNormalForm());
					((MMFunctionOverR2) function).setOperation(operation);
					// end of workaround
				}
				// don't show function label
				((MMObjectIF) function).getDisplayProperties().setLabelDisplayed(false);
				// disable warning button
				MMFunctionPanel functionPanel = (MMFunctionPanel) function.getAsContainerContent();
				functionPanel.setShowWarnButton(false);
				viewer = functionPanel;
			}
			if(viewer == null)
				throw new MCProblemException("Cannot create viewer for unknown MathML object: " + object.getClass().getName());
			// adjust background color
			viewer.setBackground(MCProblemApplet.BACKGROUND_COLOR);
			// adjust rendering precision (if necessary and possible)
			if(viewer instanceof OperationPanel && getPrecision() != 2) {
				((OperationPanel) viewer).setPrecision(getPrecision());
				((OperationPanel) viewer).render();
			}
			// workaround for "truncated" fractions
			if(viewer instanceof MMFunctionPanel) {
				Dimension d = viewer.getPreferredSize();
				((MMPanel) viewer).setWidth(d.width+1);
				((MMPanel) viewer).setHeight(d.height+1);
			}
			return viewer;
		} catch (Exception e) {
			throw new MCProblemException("Cannot create viewer from MathML content!", e);
		}
	}
	
	/**
	 * Adds a new answer/assertion component.
	 */
	public void addAssertion(Component assertionComponent) {
		AnswerComponent answer = new AnswerComponent(assertionComponent, createAnswerBox());
		m_answers.add(answer);
		((JComponent) answer.m_answerComponent).setBorder(new LineBorder(Color.GRAY));
		((JComponent) answer.m_answerBox).setBorder(new LineBorder(Color.GRAY));
		m_answerTable.add(answer.m_answerComponent);
		m_answerTable.add(answer.m_answerBox);
		m_tableLayout.setDimension(m_answers.size(), getColumnCount());
		m_answerTable.revalidate();
	}
  
  public void createTextAnswer(int rowCount, int columnCount) {
    AnswerComponent answer = new AnswerComponent(null, new TextAnswerBox(rowCount, columnCount, m_isEditable));
    m_answers.add(answer);
    m_answerTable.add(answer.m_answerBox);
    m_tableLayout.setDimension(m_answers.size(), getColumnCount());
    m_answerTable.revalidate();
  }
  
  private int getColumnCount() {
    return (m_answerType != TEXT_ANSWER_TYPE ? 2 : 1);
  }
	
	/**
	 * Adds a new answer/assertion consisting of a list of answer/assertion entries.
	 */
	public void addAssertion(ExerciseObjectIF[] assertionEntries) {
		addAssertion(createTextComponent(assertionEntries));
	}
	
	/**
	 * Resets the selections for every answer.
	 */
	public void clearAnswers() {
		for(int i = 1; i <= getAnswerCount(); i++) {
			AnswerBoxIF box = (AnswerBoxIF) getAnswer(i).m_answerBox;
			box.clearAnswer();
		}
	}
	
	/**
	 * Sets the selection state of the only selectable answer with the given number.
	 * Must only be invoked on "unique" answers.
	 * @param answer the answer's number
	 * @throws MCProblemException if the current answer type is not "unique"
	 */
	public void setUniqueAnswer(int answer) {
		if(m_answerType != UNIQUE_ANSWER_TYPE)
			throw new MCProblemException("Can only be invoked on UNIQUE answers!");
		AnswerBoxIF box = (AnswerBoxIF) getAnswer(answer).m_answerBox;
		box.setSelected(new Boolean(true));
	}
	
	/**
	 * Returns the number of the only selected answer or <code>-1</code> if no selection was made.
	 * @throws MCProblemException if the current answer type is not "unique"
	 */
	public int getUniqueAnswer() {
		if(m_answerType != UNIQUE_ANSWER_TYPE)
			throw new MCProblemException("Can only be invoked on UNIQUE answers!");
		for(int i = 1; i <= getAnswerCount(); i++) {
			AnswerBoxIF box = (AnswerBoxIF) getAnswer(i).m_answerBox;
			if(box.isSelected().booleanValue())
				return i;
		}
		return -1;
	}
	
	/**
	 * Sets the selection state for the specified answers where the value is a list of selected answer numbers.
	 * Must only be invoked on "multiple" answers.
	 * @param answer the answer's number
	 * @throws MCProblemException if the current answer type is not "multiple"
	 */
	public void setMultipleAnswers(int[] answers) {
		if(m_answerType != MULTIPLE_ANSWER_TYPE)
			throw new MCProblemException("Can only be invoked on MULTIPLE answers!");
		for(int i = 0; i < answers.length; i++) {
			AnswerBoxIF box = (AnswerBoxIF) getAnswer(answers[i]).m_answerBox;
			box.setSelected(new Boolean(true));
		}
	}
	
	/**
	 * Returns a list with the numbers of the selected answers or an empty list if no selection was made.
	 * @throws MCProblemException if the current answer type is not "multiple"
	 */
	public int[] getMultipleAnswers() {
		if(m_answerType != MULTIPLE_ANSWER_TYPE)
			throw new MCProblemException("Can only be invoked on MULTIPLE answers!");
		Vector selected = new Vector();
		for(int i = 1; i <= getAnswerCount(); i++) {
			AnswerBoxIF box = (AnswerBoxIF) getAnswer(i).m_answerBox;
			if(box.isSelected().booleanValue())
				selected.add(new Integer(i));
		}
		int[] result = new int[selected.size()];
		for(int i = 0; i < result.length; i++) {
			result[i] = ((Integer) selected.get(i)).intValue();
		}
		return result;
	}
	
	/**
	 * Sets the selection state for all answers from the given list of boolean values.
	 * Not selected answers are thereby represented by <code>null</code>.
	 * Must only be invoked on "yesno" answers.
	 * @param answer the answer's number
	 * @throws MCProblemException if the current answer type is not "yesno"
	 */
	public void setYesNoAnswers(Boolean[] answers) {
		if(m_answerType != YESNO_ANSWER_TYPE)
			throw new MCProblemException("Can only be invoked on YESNO answers!");
		for(int i = 0; i < answers.length; i++) {
			AnswerBoxIF box = (AnswerBoxIF) getAnswer(i + 1).m_answerBox;
			box.setSelected(answers[i]);
		}
	}
	
	/**
	 * Returns a list of boolean values representing the state of each single answer.
	 * Not selected answers are thereby represented by <code>null</code>.
	 * @throws MCProblemException if the current answer type is not "yesno"
	 */
	public Boolean[] getYesNoAnswers() {
		if(m_answerType != YESNO_ANSWER_TYPE)
			throw new MCProblemException("Can only be invoked on YESNO answers!");
		Boolean[] result = new Boolean[getAnswerCount()];
		for(int i = 1; i <= getAnswerCount(); i++) {
			AnswerBoxIF box = (AnswerBoxIF) getAnswer(i).m_answerBox;
			result[i-1] = box.isSelected();
		}
		return result;
	}
	
  /**
   * Sets the text for the only answer in this text question.
   * Must only be invoked on "text" answers.
   * @param answer the answer's number
   * @throws MCProblemException if the current answer type is not "text"
   */
  public void setTextAnswer(String text) {
    if(m_answerType != TEXT_ANSWER_TYPE)
      throw new MCProblemException("Can only be invoked on TEXT answers!");
    // text questions have only one single answer
    TextAnswerBox box = (TextAnswerBox) getAnswer(1).m_answerBox;
    box.setText(text);
  }
  /**
   * Returns the text for the only answer in this text question.
   * Must only be invoked on "text" answers.
   * @throws MCProblemException if the current answer type is not "text"
   */
  public String getTextAnswer() {
    if(m_answerType != TEXT_ANSWER_TYPE)
      throw new MCProblemException("Can only be invoked on TEXT answers!");
    // text questions have only one single answer
    TextAnswerBox box = (TextAnswerBox) getAnswer(1).m_answerBox;
    return box.getText();
  }
  
	private AnswerComponent getAnswer(int answerNr) {
		return (AnswerComponent) m_answers.get(answerNr - 1);
	}
	
	public int getAnswerCount() {
		return m_answers.size();
	}
	
	/**
	 * Returns the precision used for rendering for this question panel.
	 */
	public int getPrecision() {
		return m_precision;
	}
	
	class AnswerComponent {
		private Component m_answerComponent;
		private Component m_answerBox;
		
		AnswerComponent(Component answerComponent, Component answerBox) {
			m_answerComponent = answerComponent;
			m_answerBox = answerBox;
		}
	}
	
	interface AnswerBoxIF {
		void clearAnswer();
		Boolean isSelected();
		void setSelected(Boolean selected);
		void setEditable(boolean editable);
	}
	
	class UniqueAnswerBox extends JPanel implements AnswerBoxIF {
		
		private JRadioButton m_radioButton;
		
		UniqueAnswerBox(boolean editable) {
			m_radioButton = new JRadioButton();
			m_uniqueRadioButtonGroup.add(m_radioButton);
			add(m_radioButton);
			setOpaque(false);
			m_radioButton.setOpaque(false);
			setEditable(editable);
		}
		
		public Boolean isSelected() {
			return new Boolean(m_radioButton.isSelected());
		}
		
		public void setSelected(Boolean selected) {
			if(selected == null)
				return;
			m_radioButton.setSelected(selected.booleanValue());
		}
		
		public void setEditable(boolean editable) {
			m_radioButton.setEnabled(editable);
		}
		
		public void clearAnswer() {
			// work around for removing the selection state
			m_uniqueRadioButtonGroup.remove(m_radioButton);
			m_radioButton.setSelected(false);
			m_uniqueRadioButtonGroup.add(m_radioButton);
		}
	}
	
	class MultipleAnswerBox extends JPanel implements AnswerBoxIF {
		
		private JCheckBox m_checkBox;
		
		MultipleAnswerBox(boolean editable) {
			m_checkBox = new JCheckBox();
			add(m_checkBox);
			setOpaque(false);
			m_checkBox.setOpaque(false);
			setEditable(editable);
		}
		
		public Boolean isSelected() {
			return new Boolean(m_checkBox.isSelected());
		}
		
		public void setSelected(Boolean selected) {
			if(selected == null)
				return;
			m_checkBox.setSelected(selected.booleanValue());
		}
		
		public void setEditable(boolean editable) {
			m_checkBox.setEnabled(editable);
		}
		
		public void clearAnswer() {
			m_checkBox.setSelected(false);
		}
	}
	
	class YesNoAnswerBox extends JPanel implements AnswerBoxIF {
		
		private JRadioButton m_yesButton, m_noButton;
		private ButtonGroup m_answerGroup = new ButtonGroup();
		
		YesNoAnswerBox(boolean editable) {
			m_yesButton = new JRadioButton(ResourceManager.getMessage("yes"));
			m_yesButton.setFont(m_yesButton.getFont().deriveFont(Font.PLAIN));
			m_noButton = new JRadioButton(ResourceManager.getMessage("no"));
			m_noButton.setFont(m_noButton.getFont().deriveFont(Font.PLAIN));
			m_answerGroup.add(m_yesButton);
			m_answerGroup.add(m_noButton);
			add(m_yesButton);
			add(m_noButton);
			setOpaque(false);
			m_yesButton.setOpaque(false);
			m_noButton.setOpaque(false);
			setEditable(editable);
		}
		
		public Boolean isSelected() {
			if(m_yesButton.isSelected())
				return new Boolean(true);
			if(m_noButton.isSelected())
				return new Boolean(false);
			return null;
		}
		
		public void setSelected(Boolean selected) {
			if(selected == null)
				return;
			if(selected.booleanValue())
				m_yesButton.setSelected(true);
			else
				m_noButton.setSelected(true);
		}
		
		public void setEditable(boolean editable) {
			m_yesButton.setEnabled(editable);
			m_noButton.setEnabled(editable);
		}
		
		public void clearAnswer() {
			m_answerGroup.remove(m_yesButton);
			m_answerGroup.remove(m_noButton);
			m_yesButton.setSelected(false);
			m_noButton.setSelected(false);
			m_answerGroup.add(m_yesButton);
			m_answerGroup.add(m_noButton);
		}
	}
  
  class TextAnswerBox extends JPanel implements AnswerBoxIF {
    
    private JTextComponent m_textComponent;
    
    TextAnswerBox(int rowCount, int columnCount, boolean editable) {
      if(columnCount == -1)
        columnCount = 40;
      if(rowCount == -1)
        rowCount = 1;
      if(rowCount == 1) {
        m_textComponent = new JTextField(columnCount);
      } else {
        m_textComponent = new JTextArea(rowCount, columnCount);
        m_textComponent.setBorder(new LineBorder(Color.GRAY));
      }
      add(m_textComponent);
      setOpaque(false);
      m_textComponent.setOpaque(false);
      setEditable(editable);
    }
    
    public void setText(String text) {
      m_textComponent.setText(text);
    }
    
    public String getText() {
      return m_textComponent.getText();
    }
    
    public Boolean isSelected() {
      throw new MCProblemException("Must not be used for text questions!");
    }
    
    public void setSelected(Boolean selected) {
      throw new MCProblemException("Must not be used for text questions!");
    }
    
    public void setEditable(boolean editable) {
      m_textComponent.setEditable(editable);
    }
    
    public void clearAnswer() {
      m_textComponent.setText(null);
    }
  }
}

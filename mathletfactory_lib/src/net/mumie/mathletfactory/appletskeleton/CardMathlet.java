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

import javax.swing.JTabbedPane;

import net.mumie.mathletfactory.appletskeleton.layout.BasicCard;
import net.mumie.mathletfactory.appletskeleton.layout.ExerciseCard;
import net.mumie.mathletfactory.appletskeleton.layout.SubtaskCard;

public abstract class CardMathlet extends BaseApplet {

	public final static int DEMO_CARD = BasicCard.DEMO_CARD;
	public final static int TRAINING_CARD = BasicCard.TRAINING_CARD;
	public final static int EXERCISE_CARD = BasicCard.EXERCISE_CARD;
	
	public final static int USER_CARD = BasicCard.USER_CARD;
	
  protected JTabbedPane m_tabPane;

	protected abstract void addCards();
	
	public final void init() {
		try {
			super.init();
			
//			getContentPane()
			m_tabPane = createTabbedPane();
			
			addCards();
		} catch(Throwable t) {
			reportError(t);
		}
	}
	
	public final void start() {
		super.start();
	}
	
	protected void addCard(int type, BasicCard card) {
		// check for wrong method invocation...
		if(card instanceof SubtaskCard)
			throw new IllegalArgumentException("Cannot add subtask cards via this method !");
		// handle exercise cards otherwise
		if(card instanceof ExerciseCard) {
			if(type == EXERCISE_CARD) { // check for wrong type
				addExerciseCard((ExerciseCard) card);
				return;
			} else
				throw new IllegalArgumentException("Can only add exercise cards with correct type !");
		}
		// setting type...
		card.setType(type);
		// adding card...
		addCardImpl(card);
		// initializing card...
		card.createObjects();
		card.defineDependencies();
		card.addObjects();
	}
	
	private void addExerciseCard(ExerciseCard card) {
		if(isHomeworkMode())
			card.initializeExercise(this);
		else
			return;
		// adding card...
		addCardImpl(card);
		// initializing subtasks...
		card.addSubtasks();
	}
	
	private void addCardImpl(BasicCard card) {
		m_tabPane.addTab(card.getTitle(), card.getTopLevelComponent());
	}
	
	protected void showCard(BasicCard card) {
		m_tabPane.setSelectedComponent(card.getTopLevelComponent());
	}
	
	protected JTabbedPane createTabbedPane() {
		return new JTabbedPane();
	}
	
	public void reset() {}
}

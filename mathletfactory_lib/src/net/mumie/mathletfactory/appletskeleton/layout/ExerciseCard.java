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

package net.mumie.mathletfactory.appletskeleton.layout;

import javax.swing.JComponent;

import net.mumie.mathletfactory.action.message.IllegalUsageException;
import net.mumie.mathletfactory.appletskeleton.CardMathlet;
import net.mumie.mathletfactory.util.exercise.MumieExercise;

public class ExerciseCard extends BasicCard {

	private MumieExercise m_exercise;
		
	public void addSubtasks() {}
	
	public ExerciseCard() {
		super(EXERCISE_CARD);
	}
	
	public JComponent getContent() {
		return null;
	}
	
	public void initializeExercise(CardMathlet mathlet) {
		m_exercise = new MumieExercise(mathlet);
	}
	
	public MumieExercise getExercise() {
		return m_exercise;
	}
	
	public final void setType(int type) {
		throw new IllegalUsageException("Cannot change the card type of an exercise card !");
	}

	public final void createObjects() {}

	public final void addObjects() {}

	public final void defineDependencies() {}

	public void reset() {
	}
}

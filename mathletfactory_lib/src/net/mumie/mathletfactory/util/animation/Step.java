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

package net.mumie.mathletfactory.util.animation;

import java.util.Vector;

import net.mumie.mathletfactory.action.updater.DependencyUpdater;
import net.mumie.mathletfactory.mmobject.MMObjectIF;

/**
 * @author Markus Gronau
 *
 * This class is used to ...
 * 
 */
public class Step {
	
	private long m_duration;
	private int m_callCount;
//	private String m_currentDescription;
	private Vector m_dependencies = new Vector();
	
	public Step(long ms) {
		this(ms, 0);
	}
	
	public Step(long ms, int callCount) {
	  m_duration = ms;
	  m_callCount = callCount;
	}
	
	public void begin() {
		
	}
	
	public void end() {
		
	}
	
	public void proceed(Progress progress) {
	  
	}
	
	public void prepareForPreviousStep() {
		
	}
	
	public void prepareForNextStep() {
		
	}
	
	public long getDuration() {
		return m_duration;
	}
	
	public void setDuration(long ms) {
		m_duration = ms;
	}
	
	public int getCallCount() {
	  return m_callCount;
	}
	
	public void setCallCount(int callCount) {
	  m_callCount = callCount;
	}
	
	public void updateDependencies(Progress progress) {
		for(int i = 0; i < m_dependencies.size(); i++) {
			((AnimationDependencyUpdater)m_dependencies.get(i)).setProgress(progress);
			((AnimationDependencyUpdater)m_dependencies.get(i)).update();
			((AnimationDependencyUpdater)m_dependencies.get(i)).finishUpdate();
		}
	}
	
	public void setDependenciesEnabled(boolean enable) {
		for(int i = 0; i < m_dependencies.size(); i++) {
			((AnimationDependencyUpdater)m_dependencies.get(i)).setActive(enable);
			if(enable)
				((AnimationDependencyUpdater)m_dependencies.get(i)).update();
		}
	}
	
	public void addDependency(MMObjectIF dependent, MMObjectIF free, AnimationDependencyIF dependency) {
		m_dependencies.add(new AnimationDependencyUpdater(dependent, free, dependency));
	}
	
	public void addDependency(MMObjectIF dependent, MMObjectIF[] free, AnimationDependencyIF dependency) {
		m_dependencies.add(new AnimationDependencyUpdater(dependent, free, dependency));
	}
	
	public void removeDependency(DependencyUpdater up) {
		//m_dependencies.remove(up);
	}
	/**
	 * Returns the current description on the text label.
	 */
//	public String getCurrentDescription() {
//		return m_currentDescription;
//	}

	/**
	 * Sets the current description on the text label.
	 */
//	public void setCurrentDescription(String currentDescription) {
////		if(currentDescription.startsWith("<html>"))
////			m_currentDescription = currentDescription;
////		else
////			m_currentDescription = new String("<html>" + currentDescription);
//		m_currentDescription = currentDescription;
//	}

}

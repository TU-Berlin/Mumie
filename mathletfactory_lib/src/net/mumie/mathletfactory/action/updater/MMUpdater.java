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

package net.mumie.mathletfactory.action.updater;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.action.message.SpecialCaseEvent;
import net.mumie.mathletfactory.action.message.SpecialCaseListener;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
;

/**
 * This class serves as base class for all specialized updaters. An updater is
 * activated by one of the {@link net.mumie.mathletfactory.mmobject.MMObjectIF
 * MMObject}s acting as {@link #m_parent parent} and invoking it by its
 * {@link net.mumie.mathletfactory.action.handler.MMHandler MMHandler}.
 * The updater performes the necessary calculations using the state of the
 * parents (e.g. calculating the center location of given points by using their
 * coordinates) and propagates the changes to the {@link #m_child child} object
 * by using its reference. This is both done in {@link #userDefinedUpdate},
 * which has to be defined by the subclass.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */

public abstract class MMUpdater extends ActionManager {

	private static Logger logger = Logger.getLogger(MMUpdater.class.getName());

	/**
	 * The list containing all 
	 * {@link net.mumie.mathletfactory.action.message.SpecialCaseListener}s. 
	 */
	protected List m_specialCaseListenersList = new LinkedList();

	/**
	 *  the mmobject that will be updated/adjusted by the this updater.
	 */
	protected MMObjectIF m_child;

	/**
	 *  the mmobjects m_child depends on
	 */
	protected MMObjectIF[] m_parents;

	private int m_updateInvocationCount = 0;

	/**
	 * terminates if the #update() method will be performed or not.
	 */
	private boolean m_isActive = true;

	/**
	 *  Adds this updater to the updater list in the parent objects and sets
	 *  {@link #m_child} to child.
	 *  @param child   The object that needs to be updated dependent on the
	 *                  state of the parents.
	 *  @param parents The objects that will be able to force an update.
	 */
	public MMUpdater(MMObjectIF child, MMObjectIF[] parents) {
		m_child = child;
		m_parents = new MMObjectIF[parents.length];
		for (int i = 0; i < m_parents.length; i++) {
			m_parents[i] = parents[i];
			m_parents[i].addUpdater(this);
		}
	}

	public final void startUpdateCycleFromHere() {
		ActionManager.reset();
		update();
		ActionManager.reset();
	}

	/**
	 * This method will typically be called from within a MMHandler to which this
	 * MMUpdater is registered.
	 * Internally, first the MMUpdater's {@link userDefinedUpdate userDefinedUpdate()}
	 * and then the updateGeomRelations() method of the slave object are called.
	 */
	public final void update() {
		if (m_isActive) { //only work if the updater is activated!

			// increase the invocation count for the current action cycle
			// (invocation count will be reset in the updater's finish() method)
			//m_updateInvocationCount++;
			
			if (logger.isLoggable(java.util.logging.Level.FINE))
				logger.fine(
					getParent(0).getLabel()
						+ " determines "
						+ getSlave().getLabel()
						+ ": "
						+ m_updateInvocationCount);

			/* this a pure method to avoid ending in cycle-loops: the updater
			 can only be finitly often applied in one action cycle*/
			if (m_updateInvocationCount
				> ActionManager.getMaxUpdateInvocationCount()) {
				return;
			}
			else {
				m_updateInvocationCount++;
				/* if the first time invoked add this Updater to the centrally held list
				 of the ActionManager*/
				if (m_updateInvocationCount == 1)
					ActionManager.addInvokedUpdater(this);
				this.userDefinedUpdate();
				if (ActionManager.getActionCallCount() <= 1)
					ActionManager.addObjectToRedraw(m_child);
				if (m_child != null)
					m_child.invokeUpdaters();
			}
		}
		else if (logger.isLoggable(java.util.logging.Level.FINE))
			logger.fine(
				"updater " + this +" is currently not active, omitting it's update");
	}

	public final void finishUpdate() {
		m_updateInvocationCount = 0;
	}

	/**
	 * This method is dedicated to be implemented in the real MMUpdater classes
	 * that inherit from this abstract class.
	 */
	public abstract void userDefinedUpdate();

	/**
	 * @return a reference of the slave (which is a MMObject) registered
	 * in this MMUpdater.
	 */
	public MMObjectIF getSlave() {
		return m_child;
	}

	public int getParentCount() {
		return m_parents.length;
	}

	public MMObjectIF getParent(int i) {
		if (i >= 0 && i < getParentCount())
			return m_parents[i];
		else
			throw new IllegalArgumentException("tried to access parent with illegal index");
	}

	public int getInvocationCount() {
		return m_updateInvocationCount;
	}

	
	/**
	 * Sets the status of this <code>MMUpdater</code> to the desired value -
	 * <code>false</code> will deactivate it's {@link #update()} method
	 * within an <em>action cycle</em>.
	 * 
	 * @param aFlag
	 */
	public void setActive(boolean aFlag){
		m_isActive = aFlag;
	}

	
	/**
	 * Returns whether the {@link #update()} method of this <code>MMUpdater</code>
	 * is activated or not.
	 * 
	 * @return
	 */
	public boolean isActive(){
		return m_isActive;
	}

	/** The <code>listener</code> will be informed if this handler produces a special case.*/
	public void addSpecialCaseListener(SpecialCaseListener listener) {
		m_specialCaseListenersList.add(listener);
	}
	
	/** Removes the <code>listener</code> for source from the list. It will no longer be informed of special Case events. */
	public void removeSpecialCaseListener(SpecialCaseListener listener) {
		for(int i = 0; i < m_specialCaseListenersList.size(); i++) {
			if(m_specialCaseListenersList.get(i) == listener) {
				m_specialCaseListenersList.remove(i);
				return;
			}
		}
	}

	/** Informs all registered special case listeners, that the given event has occurred. */
	public void fireSpecialCaseEvent(SpecialCaseEvent e) {
		Iterator i = m_specialCaseListenersList.iterator();
		while (i.hasNext())
			 ((SpecialCaseListener) i.next()).displaySpecialCase(e);
	}
}
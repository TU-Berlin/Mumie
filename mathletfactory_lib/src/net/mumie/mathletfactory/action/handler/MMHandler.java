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

package net.mumie.mathletfactory.action.handler;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.action.message.SpecialCaseEvent;
import net.mumie.mathletfactory.action.message.SpecialCaseListener;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.util.xml.ExerciseObjectIF;


/**
 * <code>MMHandler</code> serves as the base class to define user defined interaction with {@link net.mumie.mathletfactory.mmobject.MMObjectIF}s. In this context, the terminus "<b>user interaction</b>"
 * means interacting with an <code>MMObjectIF</code> by using the mouse or the keyboard. For example, imagine moving an <code>MMAffine2DPoint</code> by dragging it with the mouse or changing a
 * <code>MMDefaultR2Endomorphism</code> by entering new values in it's matrix representation with the keyboard. <br>
 * <br>
 * In the <b>AppletFactory</b> we follow the philosophy that for a specific interaction with a <code>MMObjectIF</code> (in general given by a combination of {@link java.awt.event.MouseEvent} and
 * {@link java.awt.event.KeyEvent} which is wrapped into an {@link net.mumie.mathletfactory.action.MMEvent}) there must be exactly one <code>MMHandler</code> that defines what happens to due this
 * <code>MMEvent</code>. Therefore, a {@link net.mumie.mathletfactory.mmobject.MMObjectIF} can hold a list of <code>MMHandler</code>s and an instance of <code>MMHandler</code> can be added to
 * this list by the <code>MMObjectIF</code>'s {@link net.mumie.mathletfactory.mmobject.MMObjectIF#addHandler(net.mumie.mathletfactory.action.handler.MMHandler) addHandler} method. <br>
 * That is, the triple ({@link net.mumie.mathletfactory.mmobject.MMObjectIF}, {@link net.mumie.mathletfactory.action.handler.MMHandler}, {@link net.mumie.mathletfactory.action.MMEvent}) defines
 * the "<b>entry point</b>" of an "<b>action cycle</b>". See the documentation of {@link net.mumie.mathletfactory.action.ActionManager} for more information on this topic. It is important to
 * understand that each instance of <code>MMHandler</code> only works for a single <code>MMObjectIF</code>. In this context we refer to the <code>MMObjectIF</code> being the <b>master</b> of
 * the <code>MMHandler</code> whose {@link #getMaster()} method will return the reference to this master object. <br>
 * <br>
 * After the user interaction (by mouse or keyboard) all registered <code>MMHandler
 * </code>s of a {@link net.mumie.mathletfactory.mmobject.MMObjectIF} check if they are able to work with the
 * generated <code>MMEvent</code> by calling the method {@link #dealsWith(net.mumie.mathletfactory.action.MMEvent) dealsWith}. Observe however, that you can deactivate a <code>MMHandler</code> by
 * using {@link #setActive(boolean)} with value <code>false</code>: The <code>MMHandler</code> would then refuse to work with the specific <code>MMEvent</code> although it could do principally.
 * <br>
 * <br>
 * If the <code>MMHandler</code> can work with the current <code>MMEvent</code> it will perform it's {@link #doAction(net.mumie.mathletfactory.action.MMEvent doAction} method, which starts
 * (respecivelly continues) the <b>action cycle</b>. To properly terminate this action cycle, finally the {@link #finish()} method must be called. Typically, for <code>MMObjectIF</code>s that are
 * currently rendered in a {@link net.mumie.mathletfactory.display.MMCanvas} these methods will be called from within the controller of this <code>MMCanvas</code> (see
 * {@link net.mumie.mathletfactory.action.CanvasControllerIF}, {@link net.mumie.mathletfactory.action.DefaultCanvasController}). <br>
 * <br>
 * Please observe, that <code>MMHandler</code> only serves as the abstract super class for user specific implementations of handlers: The <code>MMHandler</code> already implements all the
 * necessary stuff to properly be used within an action cycle and so encapsulates the "mechanism behind the scenes" from the applet programmer, Writing own handlers always starts with inheriting from
 * <code>MMHandler</code> and there are exactly three protected abstract methods that must be implemented: <br>
 * <ol>
 * <li>{@link #userDefinedDealWith(net.mumie.mathletfactory.action.MMEvent) userDefinedDealWith}</li>
 * <li>{@link #userDefinedAction(net.mumie.mathletfactory.action.MMEvent) userDefinedAction}</li>
 * <li>{@link #userDefinedFinish() userDefinedFinish}</li>
 * </ol>
 * 
 * @see net.mumie.mathletfactory.action.MMEvent
 * @see net.mumie.mathletfactory.action.ActionManager
 * @see net.mumie.mathletfactory.action.updater.MMUpdater
 * @see net.mumie.mathletfactory.mmobject.MMObjectIF
 * 
 * @author vossbeck
 * 
 * @mm.docstatus finished
 */
public abstract class MMHandler extends ActionManager implements Cloneable {

	/**
	 * The list containing all {@link net.mumie.mathletfactory.action.message.SpecialCaseListener}s.
	 */
	protected List m_specialCaseListenersList = new LinkedList();

	/**
	 * The <code>MMObjectIF</code> this instance of <code>MMHandler</code> is working for.
	 */
	protected MMObjectIF m_master;

	/**
	 * The visualisation component of the <code>MMObjectIF</code> to which this <code>
	 * MMHandler</code> is bound.
	 */
	private JComponent m_display;

	/**
	 * Determines, whether the <code>MMUpdater</code>s of dependent <code>MMObjectIF
	 * </code>s shall be invoked during the doAction (=true) or at the termination of the action cycle (=false). This
	 * option might be relevant due to performance issues.
	 * 
	 */
	private boolean m_invokeUpdatersDuringAction = true;

	/* defines if the <code>MMObjectIF</code> shall be redrawn during doAction() */
	private boolean m_redrawDuringAction = true;

	/* may be set to false to deactivate this handler */
	private boolean m_active = true;

	public MMHandler( JComponent display ) {
		m_display = display;
	}

	/**
	 * Processes the incoming <code>MMEvent</code> by executing the user defined actions, if this <code>MMHandler</code> can work with it (see
	 * {@link #dealsWith(net.mumie.mathletfactory.action.MMEvent) dealsWith} ). <br>
	 * With regard of the user defined settings (see {#link isRedrawDuringAction()} and {@link isUpdateDuringAction()}) <code>MMObjectIF</code>s that depend on the <code>MMHandler</code>'s
	 * <b>master</b> (see {@link #getMaster()}) will recursively be updated during the action processing and all affected <code>MMObjectIF</code>s will be redrawn.
	 * 
	 * @return TODO
	 * 
	 * @see #userDefinedAction(net.mumie.mathletfactory.action.MMEvent)
	 * @see #dealsWith(net.mumie.mathletfactory.action.MMEvent)
	 * @see net.mumie.mathletfactory.action.DefaultCanvasController#controlAction()
	 */
	public final boolean doAction( MMEvent event ) {
		if ( dealsWith( event ) ) {
			ActionManager.increaseActionCallCount();
			if ( ActionManager.getActionCallCount() == 1 ) {
				ActionManager.setEntryObject( getMaster() );
				ActionManager.addObjectToRedraw( getMaster() );
			}
			userDefinedAction( event );
			if ( getMaster() instanceof ExerciseObjectIF ) {
				if ( ( ( ExerciseObjectIF ) getMaster() ).isEdited() == false ) ( ( ExerciseObjectIF ) getMaster() ).setEdited( true );
			}
			if ( m_invokeUpdatersDuringAction ) {
				m_master.invokeUpdaters();
				/*
				 * all involved updaters must reset their internal data, especially their updateCount.
				 */
				ActionManager.finishUpdaters();
			}
			if ( m_redrawDuringAction ) {
				ActionManager.redrawObjectsInCurrentCycle();
			}
//			getMaster().render();
			return true;
		} else {
			return false;
		}
		// throw new IllegalUsageException( "tried to invoke a MMHandler with non compliant MMEvent" );
	}

	/**
	 * Returns whether the current instance of <code>MMHandler</code> is able to work with the given {@link net.mumie.mathletfactory.action.MMEvent}. <br>
	 * <br>
	 * This method will typically be called from within the <code>controlAction(MMEvent)
	 * </code> method of a <code>CanvasControllerIF</code> (e.g.
	 * {@link net.mumie.mathletfactory.action.DefaultCanvasController}), to check if this instance of <code>MMHandler</code> can be invoked by calling this <code>MMHandler</code>'s
	 * {@link #doAction(net.mumie.mathletfactory.action.MMEvent) doAction} method with the given <code>MMEvent</code>. <br>
	 * <br>
	 * <b>Attention:</b> This method will always return <code>false</code> if {@link #isActive()} returns <code>false</code>, i.e. if this <code>MMHandler</code> was deactivated.
	 * 
	 * @see #isActive()
	 * @see #setActive()
	 * 
	 * @param event
	 * @return
	 * 
	 */
	public final boolean dealsWith( MMEvent event ) {
		if ( m_active ) {
			return userDefinedDealsWith( event );
		}
		return false;
	}

	/**
	 * Implement this method in the user specific implementation of a <code>MMHandler</code> to define on which <code>MMEvents</code> this <code>MMHandler</code> shall react.
	 */
	protected abstract boolean userDefinedDealsWith( MMEvent event );

	/**
	 * Implement this method in the user specific implementation of a <code>MMHandler</code> to describe what handler specific action should be done within the <code>doAction
	 * </code> method of this
	 * <code>MMHandler</code>.
	 * 
	 * @see #doAction(net.mumie.mathletfactory.action.MMEvent)
	 */
	protected abstract void userDefinedAction( MMEvent event );

	/**
	 * Returns the master <code>MMObjectIF</code> this <code>MMHandler</code> is working for.
	 * 
	 * @mm.sideeffects a reference to an internal field is returned
	 */
	public MMObjectIF getMaster() {
		return m_master;
	}

	/**
	 * Sets <code>master</code> to be the <b>master</b> <code>MMObjectIF</code> of this {@link MMHandler}. <br>
	 * <br>
	 * <b>Attention:</b>Although this method is public it is not intended for exhaustive public use. In general methodcall appears when a <code>MMHandler</code> is added to a
	 * <code>MMObjectIF</code> (see {@link net.mumie.mathletfactory.mmobject.MMObjectIF#addHandler(net.mumie.mathletfactory.action.handler.MMHandler) addHandler} in <code>MMObjectIF</code> for
	 * more information on this issue).
	 * 
	 * @see net.mumie.mathletfactory.mmobject.MMObjectIF#addHandler(net.mumie.mathletfactory.action.handler.MMHandler)
	 * 
	 * @mm.sideeffects after the method call the internal field of this <code>MMHandler
	 * </code> and <code>master</code> point to the same <code>MMObjectIF</code>.
	 */
	public void setMaster( MMObjectIF master ) {
		m_master = master;
	}

	/**
	 * Sets the <code>MMHandler</code>'s display to the given <code>display</code>. Observe, that each <code>MMHandler</code> is working on exactly one visualisation of a
	 * {@link net.mumie.mathletfactory.mmobject.MMObjectIF}, for example on a specific {@link net.mumie.mathletfactory.display.MMCanvas} the <code>MMObjectIF</code> was added to.
	 * 
	 * @mm.sideeffects the internal field and <code>
	 * display</code> point to the same <code>JComponent</code> after this method call.
	 */
	public void setDisplay( JComponent display ) {
		m_display = display;
	}

	/**
	 * Returns the visualisation display on which this <code>MMHandler</code> is working. Observe, that each <code>MMHandler</code> is working on exactly one visualisation of a
	 * {@link net.mumie.mathletfactory.mmobject.MMObjectIF}, for example on a specific {@link net.mumie.mathletfactory.display.MMCanvas} the <code>MMObjectIF</code> was added to.
	 * 
	 * @mm.sideeffects a reference to the internal held <code>JComponent</code> is returned.
	 */
	public JComponent getDisplay() {
		return m_display;
	}

	/**
	 * This method is called, when an action finishes (a mouse button is released after dragging or klicking, a previously pressed key is released, etc.). It calls
	 * {@link #userDefinedAction userDefinedAction()} and issues a <code>repaint()</code> if the {@link #m_master master} is a
	 * {@link net.mumie.mathletfactory.mmobject.MMCanvasObjectIF MMCanvasObject}. This method must be called when an <b>action cycle</b> shall be properly terminated, typically it will be called
	 * within the {@link net.mumie.mathletfactory.action.CanvasControllerIF#finishAction() finishAction} method in <code>CanvasControllerIF</code>. <br>
	 * <br>
	 * This method is dedicated to clean up internal settings in this <code>MMHandler</code>, so that it will be prepared for properly processing a new action cycle.
	 */
	public void finish() {
		if ( !m_invokeUpdatersDuringAction ) m_master.invokeUpdaters();
		this.userDefinedFinish();
		if ( !m_redrawDuringAction ) ActionManager.redrawObjectsInCurrentCycle();
		ActionManager.reset();
	}

	/**
	 * Implement this method in the user specific implementation of a <code>MMHandler</code> to define how this <code>MMHandler</code> must reset it's internal settings to be prepared for a new
	 * action cycle.
	 * 
	 */
	protected abstract void userDefinedFinish();

	/**
	 * Returns a flat copy of this <code>MMHandler</code>, which especially means, that this instance and it's clone have fields pointing to the same display (a <code>JComponent</code>) and
	 * master <code>MMObjectIF</code>.
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch ( CloneNotSupportedException e ) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Calling this method with value <code>false</code> deactivates this <code>MMHandler
	 * </code> which means that this <code>MMHandler</code> will no longer process it's
	 * {@link #doAction(net.mumie.mathletfactory.action.MMEvent)} method.
	 */
	public void setActive( boolean aFlag ) {
		m_active = aFlag;
	}

	/**
	 * Returns whether this <code>MMHandler</code> would currently process it's {@link #doAction(net.mumie.mathletfactory.action.MMEvent)} method or not. A deactivated <code>MMHandler</code> acts
	 * if it were not existent.
	 */
	public boolean isActive() {
		return m_active;
	}

	/**
	 * Determines if {@link net.mumie.mathletfactory.mmobject.MMObjectIF}s that depend on the <code>MMHandler</code>s master should be updated while processing the
	 * {@link #doAction(net.mumie.mathletfactory.action.MMEvent)} method or not, when calling this method with value <code>false</code>.
	 */
	public void setUpdateDuringAction( boolean aFlag ) {
		m_invokeUpdatersDuringAction = aFlag;
	}

	/**
	 * Returns whether {@link net.mumie.mathletfactory.mmobject.MMObjectIF}s that depend on the <code>MMHandler</code>s master will be updated while processing the
	 * {@link #doAction(net.mumie.mathletfactory.action.MMEvent)} method or not.
	 */
	public boolean isUpdateDuringAction() {
		return m_invokeUpdatersDuringAction;
	}

	/**
	 * Determines if the involved {@link net.mumie.mathletfactory.mmobject.MMObjectIF}s in the current action cycle should be redrawn when processing the
	 * {@link #doAction(net.mumie.mathletfactory.action.MMEvent)} method. <br>
	 * <br>
	 * The default setting is <code>true</code> but observe that it might be convenient to set it to <code>false</code> when too costly calculations must be done during method processing.
	 */
	public void setDrawDuringAction( boolean aFlag ) {
		m_redrawDuringAction = aFlag;
	}

	/**
	 * Returns whether the involved {@link net.mumie.mathletfactory.mmobject.MMObjectIF}s in the current action cycle should be redrawn when processing the
	 * {@link #doAction(net.mumie.mathletfactory.action.MMEvent)} method.
	 */
	public boolean isReDrawDuringAction() {
		return m_redrawDuringAction;
	}

	/** The <code>listener</code> will be informed if this handler produces a special case. */
	public void addSpecialCaseListener( SpecialCaseListener listener ) {
		m_specialCaseListenersList.add( listener );
	}

	/** Informs all registered special case listeners, that the given event has occurred. */
	public void fireSpecialCaseEvent( SpecialCaseEvent e ) {
		Iterator i = m_specialCaseListenersList.iterator();
		while ( i.hasNext() )
			( ( SpecialCaseListener ) i.next() ).displaySpecialCase( e );
	}

}

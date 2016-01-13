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

package net.mumie.mathletfactory.action;

import java.io.Serializable;
import java.util.Iterator;

import javax.swing.JComponent;

import net.mumie.mathletfactory.action.message.IllegalUsageException;
import net.mumie.mathletfactory.action.updater.MMUpdater;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.util.PointerArrayList;

/**
 * This class is the central class for controlling the rerendering and redrawing
 * due to external action of the user, generally in form of
 * {@link java.awt.event.MouseEvent}s 
 * (for example, dragging an <code>MMAffine2DPoint</code> to another location) or
 * {@link java.awt.event.KeyEvent}s
 * (for example, entering new entries in the matrix representation of a
 * <code>MMLinearMap</code>).
 * <br>
 * Observe however, that an "<b>action cycle</b>" can also be started explicitly by method
 * calls as described above.
 * Each &quot;action cycle&quot; is characterized by a single root object, which is
 * an instance of {@link net.mumie.mathletfactory.mmobject.MMObjectIF}.
 * A <code>MMObjectIF</code> may become the entry
 * object either by calling the method
 * {@link #performActionCycleFromObject(net.mumie.mathletfactory.mmobject.MMObjectIF) performActionCycleFromObject}
 * or when
 * one of it's registered {@link net.mumie.mathletfactory.action.handler.MMHandler}s is
 * invoked by the user by acting with the mouse or the keyboard.
 * <br><br>
 * We now give a brief overview of what happens in such an "<b>action cycle</b>":<br>
 * If the action cycle was activated by a
 * {@link net.mumie.mathletfactory.action.handler.MMHandler}'s
 * {@link net.mumie.mathtletfactory.action.handler.MMHandler#doAction(net.mumie.mathletfactory.action.MMEvent) doAction}
 * method then first the specific commands of this <code>MMHandler</code> will be done.
 * Then the entry object will be scheduled to be rerendered and redrawn (see the methods
 * {@link net.mumie.mathletfactory.mmobject.MMObjectIF#render()} and
 * {@link net.mumie.mathletfactory.mmobject.MMObjectIF#draw()} for more information) and
 * following the object's registered {@link net.mumie.mathletfactory.action.updater.MMUpdater}s
 * are called to update dependent <code>MMObjectIF</code>s. This process will be recursively
 * continued. Each such involved <code>MMObjectIF</code> will also be scheduled for a new
 * render and redraw. Finally all scheduled objects will be rerendered
 * and afterwards redrawn.
 * <br><br>
 * This class essentially offers 2 public methods to explicitly start/stop an action cycle:
 * <ol>
 * <li>{@link net.mumie.mathletfactory.action.ActionManager#performActionCycleFromObject(net.mumie.mathletfactory.mmobject.MMObjectIF) performActionCycleFromMMObject(oneObject)}
 * <li>{@link net.mumie.mathletfactory.action.ActionManager#performActionCycleFromObjects(net.mumie.mathletfactory.mmobject.MMObjectIF[]) performActionCycleFromMMObject(someObjects)}
 * </ol>
 * To be more precise, these methods can and should explicitly be used by an applet
 * programmer to start or reset an "action cycle".
 * 
 * @author vossbeck
 * 
 * @see net.mumie.mathletfactory.action.handler.MMHandler
 * @see net.mumie.mathletfactory.action.updater.MMUpdater
 * 
 * @mm.docstatus finished
 */
public class ActionManager implements Serializable {

	/**
	 * This constant limits how often the method
	 * {@link net.mumie.mathletfactory.action.updater.MMUpdater#update(net.mumie.mathletfactory.action.MMEvent) update}
	 * in a <code>MMUpdater</code> will really be performed within one "action cycle".
	 * To be specific, if it is entered more often it will simply be skipped. The purpose of
	 * this is to avoid infinite loops within one "action cycle".
	 */
	public static final int MAX_UPDATER_INVOCATION_COUNT = 3;

  private static Object lock = new Object();

	private static int m_max_updater_invocation_Count =
		MAX_UPDATER_INVOCATION_COUNT;

	private static int m_actionCallCount = 0;

//	private static boolean m_updateEntryObject = false;

	private static PointerArrayList m_entryObjects = new PointerArrayList();

	private static PointerArrayList m_updatableEntryObjects = new PointerArrayList();

	private static PointerArrayList m_objectsToRender = new PointerArrayList();

	private static PointerArrayList m_componentsToRedraw = new PointerArrayList();

	private static PointerArrayList m_listOfInvokedUpdaters = new PointerArrayList();

	/**
	 * Returns the <code>MMObjectIF</code> that is the root of the current
	 * "action cycle".
	 * If there is actually no entry object, which will happen if no "action cycle" is
	 * running, <code>null</code> will be returned.
	 * 
	 * @mm.sideeffects a reference to the root object is returned.
	 * 
	 * @return
	 */
	public static MMObjectIF getEntryObject() {
		if (m_entryObjects.size() != 0)
			/* if size==0 the call get(0) leads to NullpointerException*/
			return (MMObjectIF) m_entryObjects.get(0);
		else
			return null;
	}

	/**
	 * Returns, how often a <code>MMUpdater</code> will be called within one
	 * <em>action cycle</em>.
	 * <p>
	 * The number how often the
	 * {@link net.mumie.mathletfactory.action.updater.MMUpdater#update()}
	 * method from <code>MMUpdater</code> will be called within one <em>
	 * action cycle</em> is limited to avoid "infinite loops".
	 * 
	 * @return how often the <code>update</code> method in <code>MMUpdater</code>
	 * will be called within one <em>action cycle</em>.
	 */
	public static int getMaxUpdateInvocationCount() {
		return m_max_updater_invocation_Count;
	}

	/**
	 * Sets the number how often an instance of <code>MMUpdater</code> will be
	 * called within one <em>action cycle</em> to the desired value.
	 * <p>
	 * The number how often the
	 * {@link net.mumie.mathletfactory.action.updater.MMUpdater#update()}
	 * method from <code>MMUpdater</code> will be called within one <em>
	 * action cycle</em> is limited to avoid "infinite loops".
	 * Reducing this number may increase performance of an application, but observe
	 * that it must be at least equal to 1.
	 * 
	 * @param maxInvocationCountPerCycle will become the number how often
	 * the <code>update</code> mehtod of an instance of <code>MMUpdater</code>
	 * will be called within one <em>action cycle</em>.
	 */
	public static void setMaxUpdateInvocationCount(int maxInvocationCountPerCycle) {
		if (maxInvocationCountPerCycle >= 1)
			m_max_updater_invocation_Count = maxInvocationCountPerCycle;
		else
			throw new IllegalArgumentException(
				"number of maximal invocation counts must be at least 1.");
	}

	/**
	 * Sets the root
	 * {@link net.mumie.mathletfactory.mmobject.MMObjectIF} of an action cycle.
	 * Observe that there may only be a single root object and that this methods throws
	 * an
	 * {@link net.mumie.mathletfactory.IllegalUsageException} if it is tried to set a 
	 * second root object.<br>
	 * This method is typically be called from within the
	 * {@link net.mumie.mathletfactory.action.handler.MMHandler#doAction(net.mumie.mathletfactory.action.MMEvent) doAction}
	 * method of a <code>MMHandler</code> or from
	 * {@link #performActionCycleFromObject(net.mumie.mathletfactory.mmobject.MMObjectIF performActionCycleFromObject}
	 * when a new action cycle is built up.
	 * 
	 * @param obj
	 * 
	 * @throws net.mumie.mathletfactory.IllegalUsageException
	 */
	protected synchronized static void setEntryObject(MMObjectIF obj) {
		  /* only a single cycle root may exist*/
		  if (m_entryObjects.size() == 0)
			 m_entryObjects.add(obj);
		  else
			 throw new IllegalUsageException("tried to set a second root for an action cycle");
	}

	/**
	 * Adds and stores <code>anUpdater</code> for the current action cycle.
	 * <br>
	 * This method is dedicted to be called in the
	 * {@link net.mumie.mathletfactory.action.updater.MMUpdater#update(net.mumie.mathletfactory.action.MMEvent) update}
	 * method of an <code>MMUpdater</code> which is involved in the current action cycle.
	 * This storage is necessary for properly finishing an action cycle, because all
	 * invoked <code>MMUpdater</code>s must be correctly reseted by calling their
	 * {@link net.mumie.mathletfactory.action.updater.MMUpdater#finishUpdate() finishUpdate}
	 * method.
	 * 
	 * @param anUpdater that is invoked within the current action cycle.
	 */
	protected static synchronized void addInvokedUpdater(MMUpdater anUpdater) {
		if (!m_listOfInvokedUpdaters.contains(anUpdater))
			m_listOfInvokedUpdaters.add(anUpdater);
	}

	/**
	 * Calls the
	 * {@link net.mumie.mathletfactory.action.updater.MMUpdater#finishUpdate() finishUpdate}
	 * method of all registered <code>MMUpdater</code>s.
	 * <br>
	 * This method is typically called from other methods of this <code>ActionManager</code>
	 * and from within the
	 * <code>MMHandler</code>s
	 * {@link net.mumie.mathletfactory.action.handler.MMHandler.doAction(net.mumie.mathletfactory.action.MMEvent) doAction}
	 * method.
	 */
	protected static void finishUpdaters() {
		for (int i = 0; i < m_listOfInvokedUpdaters.size(); i++)
			 ((MMUpdater) m_listOfInvokedUpdaters.get(i)).finishUpdate();
	}

	/**
	 * Increases the counter how often the
	 * {@link net.mumie.mathletfactory.action.handler.MMHandler.doAction(net.mumie.mathletfactory.action.MMEvent) doAction}
	 * method of the active <code>MMHandler</code> was called.
	 * This method is only be called from within the before mentioned <code>doAction</code>
	 * method.
	 */
	protected static void increaseActionCallCount() {
		m_actionCallCount++;
	}

	/**
	 * Returns how often the
	 * {@link net.mumie.mathletfactory.action.handler.MMHandler.doAction(net.mumie.mathletfactory.action.MMEvent) doAction}
	 * method of the active <code>MMHandler</code> in this action cycle was called.
	 * This method is used within the <code>MMUpdater</code> and <code>MMHandler</code> to
	 * decide if some operations like registering an entry object or a updater is necessary.
	 * 
	 * @return
	 */
	protected static int getActionCallCount() {
		return m_actionCallCount;
	}

	/**
	 * Schedules a <code>MMObjectIF</code> for redrawing in the current action cycle.
	 * Method is called from within the methods
	 * {@link net.mumie.mathletfactory.action.handler.MMHandler.doAction(net.mumie.mathletfactory.action.MMEvent) doAction}
	 * in <code>MMHandler</code> and
	 * {@link net.mumie.mathletfactory.action.updater.MMUpdater.update(net.mumie.mathletfactory.action.MMEvent) update}
	 * in <code>MMUpdater</code>.
	 * 
	 * @param obj
	 */
	protected static synchronized void addObjectToRedraw(MMObjectIF obj) {
		if (!((obj == null))) {
			// object is scheduled to be rerendered within this cycle:
			addObjectToReRender(obj);
			Iterator componentsToRedraw =
				obj.getDisplayTransformerMap().keySet().iterator();
			// all visualise components ( MMCanvas or  MMPanel) are scheduled for repainting:
			while (componentsToRedraw.hasNext())
				ActionManager.addComponentToRedraw(
					(JComponent) componentsToRedraw.next());
			if (obj instanceof MMCanvasObjectIF
				&& ((MMCanvasObjectIF) obj).getCanvas() != null)
				ActionManager.addComponentToRedraw(
					((MMCanvasObjectIF) obj).getCanvas());
		}
	}

	private static synchronized void addObjectToReRender(MMObjectIF obj) {
		if (!m_objectsToRender.contains(obj))
			m_objectsToRender.add(obj);
	}

	private static void addComponentToRedraw(JComponent comp) {
		if (!m_componentsToRedraw.contains(comp))
			m_componentsToRedraw.add(comp);
	}

	private static void renderAll() {
		Iterator objectsToRender = m_objectsToRender.iterator();
		while (objectsToRender.hasNext()) {
			MMObjectIF object = (MMObjectIF) objectsToRender.next();
			object.render();
			//			Iterator displaysToRender =
			//				object.getDisplayTransformerMap().keySet().iterator();
			//			while (displaysToRender.hasNext())
			//				object.render((JComponent) displaysToRender.next());
		}
	}

	private static void repaintComponents() {
		for (int i = 0; i < m_componentsToRedraw.size(); i++){
      //System.out.println("repainting "+m_componentsToRedraw.get(i));
			 ((JComponent) m_componentsToRedraw.get(i)).repaint();
    }
	}

	/**
	 * Renders and afterwards redraws all scheduled
	 * {@link net.mumie.mathletfactory.mmobject.MMObjectIF}s in this action cycle.
	 */
	protected static void redrawObjectsInCurrentCycle() {
		//System.out.println("ActionManager::enter redrawInCurrentCycle");
		//System.out.println("ActionManager:: --> renderAll()");
		renderAll();
		//System.out.println("ActionManager:: --> repaintComponents()");
		repaintComponents();
		//System.out.println("ActionManager::exit redrawInCurrentCycle");
	}

	/**
	 * Starts and properly terminates an "action cycle" with <code>anMMObject</code> as root.
	 * <br>
	 * <code>anMMObject</code> will be registered as entry object (that means the root) of
	 * a new "action cycle", as a consequence it will be rerendered an redrawn at the end of
	 * the action cycle. All
	 * {@link net.mumie.mathletfactory.mmobject.MMObjectIF}s that depend on <code>anMMObject
	 * </code> will be adjusted and also scheduled for rerendering and redrawing. Observe
	 * that this is an recursive process, that is, the dependent of dependents will be 
	 * adjusted also.
	 * <br>
	 * Finally all scheduled <code>MMObjectIF</code>s will be rerendered and redrawn and
	 * this <code>ActionManager</code> will be reset to an initial state ready to manage
	 * a new "action cycle".
	 * <p>
	 * <b>Warning:</b>
	 * <br>
	 * Observe, that this method may not be called from within a <code>MMHandler</code>'s
	 * <code>userDefinedAction()</code> method or a <code>MMUpdater</code>'s
	 * <code>userDefinedUpdate()</code> method.
	 * 
	 * @param anMMObject  The <code>MMObjectIF</code> that will be the root of the new
	 * <em>action cycle</em>.
	 */
	public static void performActionCycleFromObject(MMObjectIF anMMObject) {
    try {
      synchronized(lock) {
        setEntryObject(anMMObject);
        addObjectToRedraw(anMMObject);
        anMMObject.invokeUpdaters();
        finishUpdaters();
        renderAll();
        repaintComponents();
        reset();
      }
    } catch (Exception e) {
      System.out.println("Interrupted action cycle...");
      e.printStackTrace();
    }
	}

	/**
	 * Starts and properly terminates an "action cycle" from each
	 * {@link net.mumie.mathletfactory.mmobject.MMObjectIF} in the traversed array. See
	 * {@link #performActionCycleFromObject(net.mumie.mathletfactory.mmobject.MMObjectIF) performActionCycle}
	 * for a more detailed description.
	 * 
	 * <p>
	 * <b>Warning:</b>
	 * <br>
	 * Observe, that this method may not be called from within a <code>MMHandler</code>'s
	 * <code>userDefinedAction()</code> method or a <code>MMUpdater</code>'s
	 * <code>userDefinedUpdate()</code> method.
	 * 
	 * @param objects The list of <code>MMObjectIF</code>s that will (seperatly) be the
	 * root of an action cycle.
	 */
	public static void performActionCycleFromObjects(MMObjectIF[] objects) {
    synchronized(lock){
      for (int i = 0; i < objects.length; i++) {
        MMObjectIF obj = objects[i];
        setEntryObject(obj);
        addObjectToRedraw(obj);
        obj.invokeUpdaters();
        m_entryObjects.clear();
      }
      finishUpdaters();
      renderAll();
      repaintComponents();
      reset();
    }
	}

	/**
	 * Interrupts the current "action cycle" and resets all internal data of this
	 * <code>ActionManager</code>.
	 * <br>
	 * As the active
	 * {@link net.mumie.mathletfactory.action.handler.MMHandler}
	 * (as their is any) all actually registered
	 * {@link net.mumie.mathletfactory.action.updater.MMUpdater}s
	 * will be forced to reset their internal state and afterwards all the data related to
	 * the current "action cycle" will be
	 * cleard off. To be specific, after the <code>reset</code> call there will no longer
	 * be an entry <code>MMObjectIF</code>, an active <code>MMHandler</code> or a registered
	 * <code>MMUpdater</code>.
	 * <br>
	 * 
	 */
	protected static synchronized void reset() {
		finishUpdaters();
		m_actionCallCount = 0;
		m_entryObjects.clear();
		m_listOfInvokedUpdaters.clear();
		m_componentsToRedraw.clear();
		m_objectsToRender.clear();
	}

	public static synchronized void addUpdatableEntryObject(MMObjectIF anObject) {
		m_updatableEntryObjects.add(anObject);
	}

	public static boolean isUpdatableEntryObject(MMObjectIF anObject) {
		return m_updatableEntryObjects.contains(anObject);
	}

}

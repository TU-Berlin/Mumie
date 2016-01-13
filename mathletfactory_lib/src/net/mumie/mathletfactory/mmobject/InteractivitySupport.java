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

package net.mumie.mathletfactory.mmobject;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.action.handler.MMHandler;
import net.mumie.mathletfactory.action.updater.DependencyIF;
import net.mumie.mathletfactory.action.updater.DependencyUpdater;
import net.mumie.mathletfactory.action.updater.MMUpdater;
import net.mumie.mathletfactory.display.MMCanvas;


/**
 * This class serves as support for instances of {@link net.mumie.mathletfactory.mmobject.MMObjectIF} -- these must implement methods from {@link net.mumie.mathletfactory.mmobject.InteractivityIF}.
 * The <code>InteractivitySupport</code> is a correct implementation of an <code>InteractivityIF</code> and instances of <code>MMObjectIF</code> may delegate those methods to this support.
 * 
 * @author vossbeck, Paehler
 * @mm.docstatus finished
 */
public class InteractivitySupport implements InteractivityIF {

	private boolean debug = false;

	/* the mmobject this support is working for */
	private final MMObjectIF m_client;

//	private boolean m_selected = false;

	/**
	 * Constructs and ties this interactivity support to the specified mmobject master.
	 */
	public InteractivitySupport( final MMObjectIF master ) {
		m_client = master;
	}

	/**
	 * Returns the <code>MMObjectIF</code> for which this instance of <code>InteractivitySupport</code> is working for.
	 */
	public MMObjectIF getClient() {
		return m_client;
	}

	/**
	 * The interactivity for a <code>MMCanvasObject</code> is performed by instances of <code>MMHandler</code>. Each canvas object may own several handlers (due to different events) and these are
	 * stored in this <code>ArrayList
	 * </code>.
	 */
	private ArrayList m_handlers = new ArrayList();

	/**
	 * This <code>ArrayList</code> stores all registered updaters.
	 */
	private ArrayList m_updaters = new ArrayList();

	/**
	 * At given time only one of all registered handlers for this object is the active one.
	 */
	private MMHandler m_activeHandler = null;

	public void resetActiveHandler() {
		m_activeHandler = null;
	}

	public void invokeUpdaters() {
		for ( int i = 0; i < getUpdaterCount(); i++ ) {

			MMUpdater u = getUpdater( i );
			// *** old code ***
			// it is sure that the entry object, the only one with an active
			// handler,
			// must not be updated anymore!

			// leaving this out, yields to problems within
			// LinearMapSimpleBasedOnDouble:

			// if (u.getSlave() != ActionManager.getEntryObject()
			// || ActionManager.isUpdatableEntryObject(u.getSlave())) {

			// *** end of old code ***

			// new idea: after having externally interacted with an mmobject (by
			// mouse or by
			// keyboard) it may still be possible that an mmobject can be
			// changed by other objects
			// on that it depends/
			// In case of conflicting changes the changes made by the updater
			// will be
			// dominating:

			// Example:
			// dragging an affine line by mouse, the x,y-value of the initial-
			// and endpoint may
			// depend on further function evaluations.
			// The line will not take the position where you dragged it, but the
			// position will
			// be determined by the mentioned functions occuring in the update
			// cycle.
			if ( debug ) {
				System.out.println( "---->" );
				if ( getClient().getLabel() != null )
					System.out.println( getClient().getLabel() );
				else
					System.out.println( getClient() );
				System.out.println( "using updater:" + i );
				System.out.println( "invocation count: " + u.getInvocationCount() );
			}
			u.update();
			// }
		}
	}

	public void addHandler( MMHandler aHandler ) {
		/* avoid adding the same handler type twice for one display */
		for ( int i = 0; i < getHandlerCount(); i++ ) {
			boolean sameHandlerClass = getHandler( i ).getClass().equals( aHandler.getClass() );
			boolean withSameDisplay = getHandler( i ).getDisplay() != null && aHandler.getDisplay() != null && getHandler( i ).getDisplay().equals( aHandler.getDisplay() );
			if ( sameHandlerClass && withSameDisplay ) 
				return;
		}
		/* handler with already existing master mmobject will be cloned */
		if ( aHandler.getMaster() != null ) 
			aHandler = ( MMHandler ) aHandler.clone();
		m_handlers.add( aHandler );
		aHandler.setMaster( getClient() );
	}

	public void removeAllHandlers() {
		m_handlers.removeAll( m_handlers );
	}

	public void removeHandler( MMHandler ahandler ) {
		m_handlers.remove( ahandler );
	}

	public boolean doAction( MMEvent aMMEvent ) {
		for ( int i = 0; i < getHandlerCount(); i++ ) {
			MMHandler handler = getHandler( i );
			if ( handler.dealsWith( aMMEvent ) ) {
				m_activeHandler = handler;
				m_activeHandler.doAction( aMMEvent );
				return true;
			}
		}
		return false;
	}

	public int getHandlerCount() {
		return m_handlers.size();
	}

	public void addUpdater( MMUpdater anUpdater ) {
		m_updaters.add( anUpdater );
	}

	public void removeAllUpdaters() {
		m_updaters.removeAll( m_updaters );
	}

	public void removeUpdater( MMUpdater anUpdater ) {
		m_updaters.remove( anUpdater );
	}

	public int getUpdaterCount() {
		return m_updaters.size();
	}

	public MMHandler getActiveHandler() {
		return m_activeHandler;
	}

	private MMHandler getHandler( int index ) {
		return ( MMHandler ) m_handlers.get( index );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.mumie.mathletfactory.mmobject.InteractivityIF#getHandlers()
	 */
	public List getHandlers() {
		return this.m_handlers;
	}

	private MMUpdater getUpdater( int index ) {
		return ( MMUpdater ) m_updaters.get( index );
	}

	public boolean isSelectable() {
		return getHandlerCount() > 0 && m_client.isVisible();// && m_client.isEditable();
	}

	public boolean isSelected() {
		if ( getClient() instanceof MMCanvasObjectIF ) {
			MMCanvasObjectIF obj = ( MMCanvasObjectIF ) getClient();
			MMCanvas canvas = obj.getCanvas();
			if ( canvas == null ) {
				boolean selected = false;
				Object key = null;
				for ( Iterator it = obj.getDisplayTransformerMap().keySet().iterator(); !selected && it.hasNext(); ) {
					key = it.next();
					if ( key instanceof MMCanvas ) {
						selected |= ( ( MMCanvas ) key ).getController().getSelectedObject() == this.getClient();
					}
				}
				return selected;
			} else {
				return canvas.getController().getSelectedObject() == getClient();
			}
		} else {
			return false;
		}
	}

	public void dependsOn( MMObjectIF freeObject, DependencyIF dependency ) {
		DependencyUpdater u = new DependencyUpdater( m_client, freeObject, dependency );
	}

	public void dependsOn( MMObjectIF[] freeObjects, DependencyIF dependency ) {
		DependencyUpdater u = new DependencyUpdater( m_client, freeObjects, dependency );
	}
}

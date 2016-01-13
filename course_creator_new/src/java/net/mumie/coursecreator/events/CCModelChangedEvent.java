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

package net.mumie.coursecreator.events;

import java.util.EventObject;

import net.mumie.coursecreator.Graph;

/**
 * Extend the {@link EventObject}.
 * Determins between events because of the panels changing or
 * the graphs being changed.
 * 
 * @author <a href="mailto:binder@math.tu-berlin.de>Jens Binder</a>
 * @author <a href="mailto:vrichter@math.tu-berlin.de>Verena Richter</a>
 * @version $Id: CCModelChangedEvent.java,v 1.5 2009/03/30 12:40:31 vrichter Exp $
 */

public class CCModelChangedEvent extends java.util.EventObject {

	/**
	 * different Events which can occur
	 */
    public static final int UNDEFINED = -1;
    public static final int CHANGED_PANELS = 0;
    public static final int CHANGED_GRAPHS = 1;
    public static final int SET_PATH=2;
    public static final int ADDED_CELL=3;

    static final long serialVersionUID=0;
    
    private Graph changedGraph;
    /** type of change */
    private int type;

    /**
     * Create a new CCModelChangedEvent.
     * @param source Object, source of the event
     * @param cg JGraph, graph which was changed
     * @param t int, type of change event
     */
    public CCModelChangedEvent(Object source, Graph cg, int t) {
	super(source);
	changedGraph = cg;
	type = t;
    }

    /**
     * Returns the changed graph.
     */
    public Graph getChangedGraph() {
	return changedGraph;
    }

    /**
     * Returns the type of the change event
     */ 
    public int getType() {
	return type;
    }

	/* (non-Javadoc)
	 * @see java.util.EventObject#toString()
	 */
	@Override
	public String toString() {
		
		return "Source "+this.getSource()+ " Type "+this.getType()+ " ChangedGraph " +this.getChangedGraph();
	}

}

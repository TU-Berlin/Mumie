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

package net.mumie.coursecreator.graph;

import com.jgraph.graph.DefaultGraphModel;
import com.jgraph.graph.Edge;

/**
 * 
 * @author <a href="mailto:binder@math.tu-berlin.de>Jens Binder</a>
 * @author <a href="mailto:vrichter@math.tu-berlin.de>Verena Richter</a>
 * @version $Id: NavGraphModel.java,v 1.3 2007/06/25 16:53:58 binder Exp $
 *
 */
public class NavGraphModel extends DefaultGraphModel {
	
	static final long serialVersionUID=0;
	// Override Superclass Method
	public boolean acceptsSource(Object edge, Object port) {
		// Source only Valid if not Equal Target
		if (((Edge) edge).getTarget() == port)
			return false;
		return true;
	}

	// Override Superclass Method
	public boolean acceptsTarget(Object edge, Object port) {
		// Target only Valid if not Equal Source
		if (((Edge) edge).getSource() == port)
			return false;
		return true;
	}

}

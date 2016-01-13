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

package net.mumie.mathletfactory.appletskeleton.jr3d;


import java.awt.BorderLayout;

import net.mumie.mathletfactory.appletskeleton.SingleCanvasApplet;
import net.mumie.mathletfactory.display.jr3d.MMJR3DCanvas;
import net.mumie.mathletfactory.math.util.AffineDouble;


/**
 * @author jweber
 * 
 */
public class SingleJR3DCanvasApplet extends SingleCanvasApplet {

	protected double[] m_worldPosition = new double[] { 0, -2, 0 }, m_worldDirection = new double[] { 0, 1, 0 }, m_worldUpwards = new double[] { 0, 0, 1 };

	/** Adds canvas components and parses parameters. */
	public void init() {
		super.init();
		m_canvas = new MMJR3DCanvas();
		m_canvasPane.add( m_canvas, BorderLayout.CENTER );
		getCanvasParameters();
	}

	/** Parses parameters that set the world dimensions. */
	public void getCanvasParameters() {
		try {
			if ( getParameter( "worldPosition" ) != null ) m_worldPosition = AffineDouble.parseArray( getParameter( "worldPosition" ) );
			if ( getParameter( "worldDirection" ) != null ) m_worldDirection = AffineDouble.parseArray( getParameter( "worldDirection" ) );
			if ( getParameter( "worldUpwards" ) != null ) m_worldUpwards = AffineDouble.parseArray( getParameter( "worldUpwards" ) );
		} catch ( Exception e ) {}
		adjustWorld2WorldView();
	}

	/** Updates the viewer position and direction. */
	public void adjustWorld2WorldView() {
		getCanvas3D().setLookAt( m_worldPosition, m_worldDirection, m_worldUpwards );
	}

	public MMJR3DCanvas getCanvas3D() {
		return ( MMJR3DCanvas ) getCanvas();
	}

	/** Resets the view position to initial coordinates. */
	public void reset() {
		super.reset();
		getCanvasParameters();
//		getCanvas3D().getWorld2Screen().getHistory().clear();
//		getCanvas3D().getWorld2Screen().addSnapshotToHistory();
	}
}

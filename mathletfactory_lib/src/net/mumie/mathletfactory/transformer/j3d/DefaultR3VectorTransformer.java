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

package net.mumie.mathletfactory.transformer.j3d;

import net.mumie.mathletfactory.display.j3d.J3DDrawable;
import net.mumie.mathletfactory.display.j3d.J3DLineSegmentDrawable;
import net.mumie.mathletfactory.display.j3d.J3DPointDrawable;
import net.mumie.mathletfactory.display.j3d.MMJ3DCanvas;
import net.mumie.mathletfactory.math.algebra.linalg.NumberVector;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.math.util.Affine3DDouble;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR3Vector;

/**
 * This class performs the complete display of the abstract mathematical object
 * {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR3Vector} to the screen.
 * Any of such vectors is displayed as an arrow from the origin (i.e. the point
 * 0,0,0 in math) to the point (x,y,z) where x,y,z represent the default coordinates
 * of the vector due to its vector space (see
 * {@link net.mumie.mathletfactory.algebra.linalg.NumberVector#getDefaultCoordinates} for
 * more information).
 *
 * More precicsely: If (x,y,z) are the default coordinates of the
 * {@link net.mumie.mathletfactory.algebra.linalg.NumberVector} we have as representation
 * in the &quot;worldDraw&quot; two <code>double[3]</code>s,
 * the initial point having <b>always</b> coordinates (0,0,0) and the end point
 * having always coordinates (x,y,z).
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class DefaultR3VectorTransformer
	extends Canvas3DObjectJ3DTransformer {

	/** The initital point of the vector arrow. */
	private final double[] m_origin = new double[3];

	/** The end point of the vector arrow. */
	private final double[] m_endPoint = new double[3];

	private final double[] m_tmpForCalc = new double[3];

	/** 
	 * Constructs the transformer for the given 
	 * {@link net.mumie.mathletfactory.display.j3d.MMJ3DCanvas} display.
	 */
	public DefaultR3VectorTransformer() {
		m_allDrawables = new J3DDrawable[] { new J3DPointDrawable(), new J3DLineSegmentDrawable()};
		m_activeDrawable = m_allDrawables[1];
	}

	public double[] getWorldPickPointFromMaster() {
		return m_endPoint;
	}

	public void initialize(MMObjectIF masterObject) {
		super.initialize(masterObject);
		((J3DLineSegmentDrawable) m_allDrawables[1]).setCanvas((MMJ3DCanvas) getMasterAsCanvasObject().getCanvas());
	}

	public void synchronizeWorld2Screen() {
		// test if m_endPointInWorld equals (0,0): then render only a Point:
    if (getRealMasterObject().getDefaultCoordinatesRef().isZero()) {
      ((J3DLineSegmentDrawable)m_allDrawables[1]).removeShape();
      m_activeDrawable = m_allDrawables[0];
      ((J3DPointDrawable) m_allDrawables[0]).setPoint(m_endPoint);
    } else {    
      ((J3DPointDrawable)m_allDrawables[0]).removeShape();
		  m_activeDrawable = m_allDrawables[1];
		  ((J3DLineSegmentDrawable) m_allDrawables[1]).setPoints(m_origin, m_endPoint);
    }
	}

	public void synchronizeMath2Screen() {
		// it is possible to use the getDefaultCoordinatesRef() method, because
		// math2World() will not change them!
		math2World(getRealMasterObject().getDefaultCoordinatesRef(), m_endPoint);
    math2World(getRealMasterObject().getRootingPoint(), m_origin);
    Affine3DDouble.add(m_endPoint, m_origin);
		synchronizeWorld2Screen();
	}

	public void render() {
		renderVectorLabel(m_endPoint);
		super.render();
	}

	/**
	 * Returns the master object (see
	 * {@link net.mumie.mathletfactory.transformer.CanvasObjectTransformer#m_masterMMObject})
	 * explicitly cast to {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR3Vector}.
	 */
	private MMDefaultR3Vector getRealMasterObject() {
		return (MMDefaultR3Vector) m_masterMMObject;
	}

	public void getMathObjectFromScreen(
		double[] javaScreenCoordinates,
		NumberTypeDependentIF mathObject) {
		NumberVector vector = (NumberVector) mathObject;
		//System.out.println("coords are "+vector.getDefaultCoordinates());
		MMJ3DCanvas canvas = ((MMJ3DCanvas) getMasterAsCanvasObject().getCanvas());
		screen2World(javaScreenCoordinates, m_tmpForCalc, getDistanceInZProj());
    math2World(getRealMasterObject().getRootingPoint(), m_origin);
    Affine3DDouble.sub(m_tmpForCalc, m_origin);
		vector.getDefaultCoordinateRef(1).setDouble(m_tmpForCalc[0]);
		vector.getDefaultCoordinateRef(2).setDouble(m_tmpForCalc[1]);
		vector.getDefaultCoordinateRef(3).setDouble(m_tmpForCalc[2]);
	}

	/** Empty implementation. */
	public void getScreenPointFromMath(
		NumberTypeDependentIF entity,
		double[] javaScreenCoordinates) {
	}
}

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

package net.mumie.mathletfactory.transformer.g2d;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.g2d.G2DLineDrawable;
import net.mumie.mathletfactory.display.g2d.G2DPointDrawable;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.number.MMComplex;
import net.mumie.mathletfactory.mmobject.number.MMComplexRational;
import net.mumie.mathletfactory.transformer.Canvas2DObjectTransformer;

/**
 * This class performs the complete display of the mathematical object
 * {@link net.mumie.mathletfactory.mmobject.number.MMComplex} to the screen.
 * Any complex vector is displayed as an arrow from the origin (i.e. the point
 * 0,0 in math) to the point (x,y) where x,y represent the real part and the 
 * imaginary part of a complex number.
 *
 * More precisely: If (x,y) are the coordinates of the
 * {@link net.mumie.mathletfactory.number.MComplex} we have as representation
 * in the &quot;worldDraw&quot; two {@link net.mumie.mathletfactory.geom.affine.Affine2DPoint}s,
 * the initial point having <b>always</b> coordinates (0,0) and the end point
 * having always coordinates (x,y).
 *
 * @author Mrose
 * @mm.docstatus finished
 */
public class ComplexVectorTransformer extends Canvas2DObjectTransformer {
	private final double[] m_originCoordsInWorld = new double[] { 0., 0. };
	private final double[] m_endCoordsInWorld = new double[2];
	private final double[] m_originCoordsOnScreen = new double[2];
	private final double[] m_endCoordsOnScreen = new double[2];
	private final double[] m_tmp = new double[2];
	
	/**
	 * Creates the drawables: point (for the complex zero) and line (for any
	 * other complex number).
	 */
	public ComplexVectorTransformer() {
		m_allDrawables =
			new CanvasDrawable[] {
				new G2DPointDrawable(),
				new G2DLineDrawable()};
	}
	public void synchronizeWorld2Screen() {
		// test if master object is the zero vector:
		if (((MNumber)m_masterMMObject).isZero()) {
			m_activeDrawable = m_allDrawables[0];
			world2Screen(m_originCoordsInWorld, m_originCoordsOnScreen);
			getPointDrawable().setPoint(
				m_originCoordsOnScreen[0],
				m_originCoordsOnScreen[1]);
		}
		else {
			m_activeDrawable = m_allDrawables[1];
			world2Screen(m_originCoordsInWorld, m_originCoordsOnScreen);
			world2Screen(m_endCoordsInWorld, m_endCoordsOnScreen);
			getArrowDrawable().setPoints(
				m_originCoordsOnScreen[0],
				m_originCoordsOnScreen[1],
				m_endCoordsOnScreen[0],
				m_endCoordsOnScreen[1]);
		}
	}
	public void synchronizeMath2Screen() {
		// it is possible to use the getDefaultCoordinatesRef() method, because
		// math2World() will not change them!
    if(m_masterMMObject instanceof MMComplex){
		m_endCoordsInWorld[0] = ((MMComplex)m_masterMMObject).getRe();
		m_endCoordsInWorld[1] = ((MMComplex)m_masterMMObject).getIm();
    } else { // must be instance of MMComplexRational    
      m_endCoordsInWorld[0] = ((MMComplexRational)m_masterMMObject).getRe().getDouble();
      m_endCoordsInWorld[1] = ((MMComplexRational)m_masterMMObject).getIm().getDouble();
    }
		synchronizeWorld2Screen();
	}
	public void draw() {
		m_activeDrawable.draw((MMCanvasObjectIF) m_masterMMObject);
		if (m_masterMMObject.getLabel() != null
			&& ((MMCanvasObjectIF) m_masterMMObject).isVisible()) {
			float endX = (float) m_endCoordsOnScreen[0];
			float endY = (float) m_endCoordsOnScreen[1];
			Graphics2D gr =
				((MMG2DCanvas) getMasterAsCanvasObject().getCanvas())
					.getGraphics2D();
			gr.setColor(Color.black);
			gr.setFont(new Font("helvetica", Font.BOLD, 14));
			gr.drawString(m_masterMMObject.getLabel(), endX + 2, endY - 2);
		}
	}
	// the following should be the methods to be used in future:
	public void getMathObjectFromScreen(
		double[] javaScreenCoordinates,
		NumberTypeDependentIF aPoint) {
		screen2World(javaScreenCoordinates, m_tmp);
		((Affine2DPoint) aPoint).setX(m_tmp[0]);
		((Affine2DPoint) aPoint).setY(m_tmp[1]);
	}
	public void getScreenPointFromMath(
		NumberTypeDependentIF aPoint,
		double[] javaScreenCoordinates) {
		m_tmp[0] = ((Affine2DPoint) aPoint).getXAsDouble();
		m_tmp[1] = ((Affine2DPoint) aPoint).getYAsDouble();
	}
	private G2DLineDrawable getArrowDrawable() {
		return (G2DLineDrawable) m_allDrawables[1];
	}
	private G2DPointDrawable getPointDrawable() {
		return (G2DPointDrawable) m_allDrawables[0];
	}
	/**
	 * returns the master object (see
	 * {@link net.mumie.mathletfactory.transformer.CanvasObjectTransformer#m_masterMMObject})
	 * explicitly casted to {@link net.mumie.mathletfactory.mmobject.MMDefaultR2Vector}.
	 */
	public MMComplex getRealMasterObject() {
		return (MMComplex) m_masterMMObject;
	}
	//	// methods will be deleted in future!!!
	//	public NumberTypeDependentIF getMathObjectFromScreen(AffinePoint screenPoint) {
	//		return null;
	//	}
	//	public void getMathObjectFromScreen(
	//		AffinePoint screenPoint,
	//		NumberTypeDependentIF mathObject) {
	//	}
	//	public AffinePoint getScreenPointFromMath(NumberTypeDependentIF entity) {
	//		return null;
	//	}
}

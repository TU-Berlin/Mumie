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

import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.j3d.J3DLineSegmentDrawable;
import net.mumie.mathletfactory.display.j3d.J3DParameterizedSurfaceDrawable;
import net.mumie.mathletfactory.display.j3d.J3DPointDrawable;
import net.mumie.mathletfactory.display.j3d.J3DRemovableDrawable;
import net.mumie.mathletfactory.display.j3d.MMJ3DCanvas;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.util.Affine3DDouble;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DSubspace;

/**
 *  This class transforms real subspaces (i.e. planes, lines, points, empty 
 *  space) of an 3d affine space in the Java3D rendering system. The type of 
 *  the subspace is determined at runtime.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class Affine3DSubspaceTransformer
	extends Affine3DDefaultTransformer {

	private double[][] m_points;
	private final double[] m_centerOfGravity = new double[3];
	private final double[] m_direction = new double[3];
	private double[][] m_pointsOutOfRange;

	public Affine3DSubspaceTransformer() {
		m_allDrawables =
			new CanvasDrawable[] {
				new J3DPointDrawable(),
				new J3DLineSegmentDrawable(),
				new J3DParameterizedSurfaceDrawable()};
		m_activeDrawable = m_allDrawables[0];
	}

	public double[] getWorldPickPointFromMaster() {
		return m_centerOfGravity;
	}

	public void synchronizeMath2Screen() {
		int dimension = getRealMaster().getDimension();
		if (dimension == -1) // empty space
			return;     
		m_points = new double[dimension + 1][3];
		m_pointsOutOfRange = new double[dimension + 1][3];
		m_centerOfGravity[0] = 0;
		m_centerOfGravity[1] = 0;
		m_centerOfGravity[2] = 0;
		for (int i = 0; i < dimension + 1; i++) {
			// transfer coordinates
			math2World(getRealMaster().getAffineCoordinates()[i], m_points[i]);

			// calculate the center of gravity
			m_centerOfGravity[0] += m_points[i][0] / (dimension + 1);
			m_centerOfGravity[1] += m_points[i][1] / (dimension + 1);
			m_centerOfGravity[2] += m_points[i][2] / (dimension + 1);
			//System.out.println("point "+i+" is: "+m_points[i][0]+", "+m_points[i][1]+", "+m_points[i][2]);
		}

		if (dimension == 0) { // draw point :
			m_activeDrawable = m_allDrawables[0];
			((J3DPointDrawable) m_activeDrawable).setPoint(m_points[0]);
			return;
		}

		// calculate direction vector from first to second point
		Affine3DDouble.setFrom(m_direction, m_points[1]);
		Affine3DDouble.sub(m_direction, m_points[0]);

		// for line or plane calculate the points, that are outside the view volume
		(
			(MMJ3DCanvas) getMasterAsCanvasObject()
				.getCanvas())
				.getPointOutOfViewRange(
			m_points[0],
			m_direction,
			m_pointsOutOfRange[0]);
		Affine3DDouble.scale(m_direction, -1);
		(
			(MMJ3DCanvas) getMasterAsCanvasObject()
				.getCanvas())
				.getPointOutOfViewRange(
			m_points[1],
			m_direction,
			m_pointsOutOfRange[1]);

		if (dimension == 1) { // draw line :
			m_activeDrawable = m_allDrawables[1];
			((J3DLineSegmentDrawable) m_activeDrawable).setPoints(
				m_pointsOutOfRange[0],
				m_pointsOutOfRange[1]);
			return;
		}
		// draw plane:

    final int NUMOFSEGMENTS = 5;
	  NumberTuple[][] values = new NumberTuple[NUMOFSEGMENTS][NUMOFSEGMENTS];
    NumberTuple point1 = getRealMaster().getAffineCoordinates()[0], point2 = getRealMaster().getAffineCoordinates()[1],
    point3 = getRealMaster().getAffineCoordinates()[2];
    for(int i=0;i<NUMOFSEGMENTS;i++)
      for(int j=0;j<NUMOFSEGMENTS;j++){
        // calculate direction vector from first to third point
        NumberTuple direction1 = new NumberTuple(point2).subFrom(point1), direction2 = new NumberTuple(point3).subFrom(point1);
        values[i][j] = direction1.multiplyWithScalar((NUMOFSEGMENTS-1)/2-j).addTo(point1)
        .addTo(direction2.multiplyWithScalar((NUMOFSEGMENTS-1)/2-i));
      }
        
//    values[0][0] = new NumberTuple(point1).subFrom(direction1).subFrom(direction2);
//    values[0][1] = new NumberTuple(point1).subFrom(direction2);
//    values[0][2] = new NumberTuple(point1).addTo(direction1).subFrom(direction2);
//    values[1][0] = new NumberTuple(point1).subFrom(direction1);
//    values[1][1] = new NumberTuple(point1);
//    values[1][2] = new NumberTuple(point1).addTo(direction1);
//    values[2][0] = new NumberTuple(point1).addTo(direction2).subFrom(direction1);
//    values[2][1] = new NumberTuple(point1).addTo(direction2);
//    values[2][2] = new NumberTuple(point1).addTo(direction2).addTo(direction1);
		m_activeDrawable = m_allDrawables[2];
    if(J3DParameterizedSurfaceDrawable.valuesChanged(((J3DParameterizedSurfaceDrawable) m_activeDrawable).getValues(), values))
		  ((J3DParameterizedSurfaceDrawable) m_activeDrawable).setValues(values);
	}

	private MMAffine3DSubspace getRealMaster() {
		return (MMAffine3DSubspace) m_masterMMObject;
	}

	public void render() {
    CanvasDrawable oldDrawable = m_activeDrawable;
		super.render();    
    if(oldDrawable != m_activeDrawable)
      ((J3DRemovableDrawable)oldDrawable).removeShape();
		renderLabel(m_centerOfGravity);
	}

}

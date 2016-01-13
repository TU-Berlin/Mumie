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

package net.mumie.mathletfactory.action.jr3d;


import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DLine;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DPoint;
import de.jreality.scene.IndexedLineSet;
import de.jreality.scene.PointSet;
import de.jreality.scene.data.Attribute;


/**
 * @author jweber
 * 
 */
public class JRGeometryUtils {

	public static MMAffine3DPoint getPointFromPointSet( PointSet ps, int index ) {
		return JRGeometryUtils.getPointFromPointSet( MDouble.class, ps, index );
	}

	public static MMAffine3DPoint getPointFromPointSet( Class numberClass, PointSet ps, int index ) {
		double[][] points = new double[ps.getNumPoints()][];
		ps.getVertexAttributes( Attribute.COORDINATES ).toDoubleArrayArray( points );

		return new MMAffine3DPoint( numberClass, points[index][0], points[index][1], points[index][2] );
	}

	public static MMAffine3DPoint[] getPointsFromPointSet( PointSet ps ) {
		return JRGeometryUtils.getPointsFromPointSet( MDouble.class, ps );
	}

	public static MMAffine3DPoint[] getPointsFromPointSet( Class numberClass, PointSet ps ) {
		MMAffine3DPoint[] result = new MMAffine3DPoint[ps.getNumPoints()];

		for ( int i = 0; i < ps.getNumPoints(); i++ ) {
			result[i] = JRGeometryUtils.getPointFromPointSet( numberClass, ps, i );
		}

		return result;
	}

	public static MMAffine3DPoint[] getLinePointsFromLineSet( IndexedLineSet ls, int index ) {
		return JRGeometryUtils.getLinePointsFromLineSet( MDouble.class, ls, index );
	}

	public static MMAffine3DPoint[] getLinePointsFromLineSet( Class numberClass, IndexedLineSet ls, int index ) {
		double[][] points = new double[ls.getNumPoints()][];
		ls.getVertexAttributes( Attribute.COORDINATES ).toDoubleArrayArray( points );

		int[] indices = ls.getEdgeAttributes( Attribute.INDICES ).toIntArrayArray().getValueAt( index ).toIntArray( null );

		MMAffine3DPoint[] result = new MMAffine3DPoint[indices.length];
		for ( int i = 0; i < indices.length; i++ ) {
			result[i] = new MMAffine3DPoint( numberClass, points[indices[i]][0], points[indices[i]][1], points[indices[i]][2] );
		}

		return result;
	}

	public static MMAffine3DLine getLineFromLineSet( IndexedLineSet ls, int index ) {
		return JRGeometryUtils.getLineFromLineSet( MDouble.class, ls, index );
	}

	public static MMAffine3DLine getLineFromLineSet( Class numberClass, IndexedLineSet ls, int index ) {
		MMAffine3DPoint[] linePoints = JRGeometryUtils.getLinePointsFromLineSet( numberClass, ls, index );
		return new MMAffine3DLine( linePoints[0], linePoints[1] );
	}

	public static MMAffine3DLine[] getLinesFromLineSet( IndexedLineSet ls ) {
		return JRGeometryUtils.getLinesFromLineSet( MDouble.class, ls );
	}

	public static MMAffine3DLine[] getLinesFromLineSet( Class numberClass, IndexedLineSet ls ) {
		MMAffine3DLine[] result = new MMAffine3DLine[ls.getNumEdges()];

		for ( int i = 0; i < ls.getNumEdges(); i++ ) {
			result[i] = JRGeometryUtils.getLineFromLineSet( numberClass, ls, i );
		}

		return result;
	}

	// TODO Behandlung der Flaechen
	// TODO weitere Operationen?
}

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

package net.mumie.mathletfactory.display.jr3d;


import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.display.SurfaceDisplayProperties;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR3Vector;
import de.jreality.math.Matrix;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Transformation;


/**
 * @author jweber
 * 
 */
public class JR3DVectorDrawable extends JR3DDrawable {

	public static final int[][] INDICIES = new int[9][];
	public static final double[][] VERTICES = new double[10][];

	static {
		VERTICES[0] = new double[] { 0.0, 0.0, 0.0 };
		VERTICES[1] = new double[] { 0.9, 0.01, -0.01 };
		VERTICES[2] = new double[] { 0.9, 0.01, 0.01 };
		VERTICES[3] = new double[] { 0.9, -0.01, -0.01 };
		VERTICES[4] = new double[] { 0.9, -0.01, 0.01 };

		VERTICES[5] = new double[] { 1.0, 0.0, 0.0 };
		VERTICES[6] = new double[] { 0.9, 0.02, -0.02 };
		VERTICES[7] = new double[] { 0.9, 0.02, 0.02 };
		VERTICES[8] = new double[] { 0.9, -0.02, -0.02 };
		VERTICES[9] = new double[] { 0.9, -0.02, 0.02 };

		INDICIES[0] = new int[] { 0, 1, 2 };
		INDICIES[1] = new int[] { 0, 2, 4 };
		INDICIES[2] = new int[] { 0, 4, 3 };
		INDICIES[3] = new int[] { 0, 3, 1 };

		INDICIES[4] = new int[] { 6, 7, 8, 9 };
		INDICIES[5] = new int[] { 5, 6, 7 };
		INDICIES[6] = new int[] { 5, 7, 9 };
		INDICIES[7] = new int[] { 5, 9, 8 };
		INDICIES[8] = new int[] { 5, 8, 6 };
	}

	protected JR3DFaceSetDrawable m_vectorDrawable;
	protected JR3DPointSetDrawable m_labelDrawable;

	public JR3DVectorDrawable() {
		this( null );
	}

	public JR3DVectorDrawable( final String name ) {
		super( name );

		this.m_vectorDrawable = new JR3DFaceSetDrawable( "vector" );
		this.m_vectorDrawable.setFaces( VERTICES, INDICIES );

		this.m_labelDrawable = new JR3DPointSetDrawable( "label" );
		this.m_labelDrawable.setPoints( new double[][] { VERTICES[0] } );

		this.m_sceneContent.addChild( this.m_vectorDrawable.getViewContent() );
		this.m_sceneContent.addChild( this.m_labelDrawable.getViewContent() );
	}

	public void setCoordinates( MMDefaultR3Vector v ) {
		this.setCoordinates( v.getDefaultCoordinates() );
		this.m_labelDrawable.setPointsLabels( new String[] { v.getLabel() } );
	}

	public void setCoordinates( NumberTuple c ) {
		double[] coord = c.toDoubleArray();

		double vectorLength = Math.sqrt( Math.pow( coord[0], 2 ) + Math.pow( coord[1], 2 ) + Math.pow( coord[2], 2 ) );

		Transformation transform = new Transformation();
		MatrixBuilder.euclidean().scale( vectorLength ).assignTo( transform );

		if ( vectorLength != 0.0 ) {
			double[] crossProduct = new double[] { 0, -coord[2], coord[1] };
			double angle = Math.acos( coord[0] / vectorLength );

			Transformation rotate = new Transformation();
			MatrixBuilder.euclidean().rotate( angle, crossProduct ).assignTo( rotate );
			transform.multiplyOnRight( rotate.getMatrix() );
		}

		this.m_vectorDrawable.setPoints( this.transformVertices( VERTICES, transform ) );
		this.m_labelDrawable.setPoints( new double[][] { coord } );
	}

	private double[][] transformVertices( double[][] vertices, Transformation t ) {
		double[][] result = new double[vertices.length][];
		double[][] vertices3D = new double[vertices.length][];

		Matrix m = new Matrix( t.getMatrix() );

		for ( int i = 0; i < vertices.length; i++ ) {
			vertices3D[i] = new double[] { vertices[i][0], vertices[i][1], vertices[i][2], 1.0 };
			result[i] = new double[3];

			for ( int c = 0; c < 3; c++ ) {
				double[] row = m.getRow( c );
				for ( int j = 0; j < row.length; j++ ) {
					result[i][c] += vertices3D[i][j] * row[j];
				}
			}
		}
		return result;
	}

	public void render( DisplayProperties properties ) {
		SurfaceDisplayProperties sp = null;
		if ( !( properties instanceof SurfaceDisplayProperties ) ) {
			sp = new SurfaceDisplayProperties( properties );
			sp.setFilled( true );
			sp.setGridVisible( true );
		} else {
			sp = ( SurfaceDisplayProperties ) properties;
		}
		JR3DFaceSetDrawable.setSurfaceDisplayProperties( m_vectorDrawable, this.m_sceneContent.getAppearance(), sp, this.m_selected );

		PointDisplayProperties ps = new PointDisplayProperties( properties );
		ps.setPointRadius( 1 );
		JR3DPointSetDrawable.setPointDisplayProperties( m_labelDrawable, this.m_labelDrawable.m_sceneContent.getAppearance(), ps, false );
	}
}

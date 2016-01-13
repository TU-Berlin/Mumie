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


import de.jreality.math.MatrixBuilder;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.Transformation;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.display.jr3d.hints.JR3DShaderRenderingHints;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultRN.RNDisplayProperties;


/**
 * @author jweber TODO Die Bennenung der SGC
 * 
 */
public class JR3DCoordAxisDrawable extends JR3DDrawable {

	public static final String[] AXIS_NAMES = new String[] { "x", "y", "z" };
	public static final double[][] BASIS_3D = new double[][] { { 1, 0, 0 }, { 0, 0, 1 }, { 0, 1, 0 } };
	public static final Transformation[] AXIS_TRANSFORMATIONS = new Transformation[3];

	static {
		for ( int i = 0; i < 3; i++ ) {
			JR3DCoordAxisDrawable.AXIS_TRANSFORMATIONS[i] = new Transformation();

			double rotateDegree = Math.toRadians( 90 * Math.pow( -1, i / 2 ) );
			MatrixBuilder.euclidean().rotate( rotateDegree, JR3DCoordAxisDrawable.BASIS_3D[i] ).assignTo( JR3DCoordAxisDrawable.AXIS_TRANSFORMATIONS[i] );
		}
	}

	private JR3DCompositeDrawable m_axisDrawable;
	private JR3DPointSetDrawable[] m_labelPointsDrawable;

	private SceneGraphComponent[] m_coordAxis;

	private boolean m_scalePointsVisible = true;
	private boolean m_axisNamesVisible = true;

	protected double m_scale = 1;
	protected double m_extent = 10;
	protected double a = 0.2;

	public JR3DCoordAxisDrawable() {
		super( "coord axis" );
		this.m_axisDrawable = new JR3DCompositeDrawable( "abstractAxis", new JR3DDrawable[] { new JR3DPointSetDrawable( "axisScalePoints" ), new JR3DLineSetDrawable( "axisLine" ), new JR3DFaceSetDrawable( "axisArrow" ) } );
		this.m_labelPointsDrawable = new JR3DPointSetDrawable[3];

		this.m_coordAxis = new SceneGraphComponent[3];

		for ( int i = 0; i < 3; i++ ) {
			this.m_labelPointsDrawable[i] = new JR3DPointSetDrawable( "axisLabelPoints" );

			this.m_coordAxis[i] = new SceneGraphComponent( JR3DCoordAxisDrawable.AXIS_NAMES[i] + "Axis" );
			this.m_coordAxis[i].addChild( this.m_axisDrawable.getViewContent() );
			this.m_coordAxis[i].addChild( this.m_labelPointsDrawable[i].getViewContent() );

			this.m_sceneContent.addChild( this.m_coordAxis[i] );
		}

		this.setExtentAndScale( this.m_extent, this.m_scale );
	}

	public void setExtentAndScale( double extent, double scale ) {
		this.m_extent = Math.abs( extent );
		this.m_scale = Math.abs( scale );
		this.update();
	}

	public void setScalePointsVisible( boolean v ) {
		if ( this.m_scalePointsVisible != v ) {
			this.m_scalePointsVisible = v;
			JR3DPointSetDrawable pointsDrawable = ( JR3DPointSetDrawable ) this.m_axisDrawable.getJR3DDrawable( 0 );
			pointsDrawable.setVisible( v );
		}
	}

	public void setAxisNamesVisible( boolean v ) {
		if ( this.m_axisNamesVisible != v ) {
			this.m_axisNamesVisible = v;
			for ( int i = 0; i < 3; i++ ) {
				this.m_labelPointsDrawable[i].setVisible( v );
			}
		}
	}

	public void update() {
		for ( int i = 0; i < this.m_labelPointsDrawable.length; i++ ) {
			this.m_labelPointsDrawable[i].setPoints( new double[][] { { this.m_extent, 0.0, 0.0 } } );
			this.m_labelPointsDrawable[i].setPointsLabels( new String[] { JR3DCoordAxisDrawable.AXIS_NAMES[i] } );
		}

		JR3DLineSetDrawable lineDrawable = ( ( JR3DLineSetDrawable ) this.m_axisDrawable.getJR3DDrawable( 1 ) );
		lineDrawable.setLines( new double[][] { { 0.0, 0.0, 0.0 }, { this.m_extent, 0.0, 0.0 } }, new int[][] { { 0, 1 } } );

		JR3DPointSetDrawable pointsDrawable = ( ( JR3DPointSetDrawable ) this.m_axisDrawable.getJR3DDrawable( 0 ) );
		double[][] points = new double[( int ) ( this.m_extent / this.m_scale )][3];
		for ( int i = 0; i < points.length; i++ ) {
			points[i] = new double[] { i * this.m_scale, 0.0, 0.0 };
		}
		pointsDrawable.setPoints( points );

		JR3DFaceSetDrawable facesDrawable = ( ( JR3DFaceSetDrawable ) this.m_axisDrawable.getJR3DDrawable( 2 ) );
		points = new double[5][3];
		points[0] = new double[] { this.m_extent + 1, 0.0, 0.0 };
		points[1] = new double[] { this.m_extent, a / 2, a / 2 };
		points[2] = new double[] { this.m_extent, a / 2, -a / 2 };
		points[3] = new double[] { this.m_extent, -a / 2, a / 2 };
		points[4] = new double[] { this.m_extent, -a / 2, -a / 2 };
		facesDrawable.setFaces( points, new int[][] { { 1, 2, 3, 4 }, { 0, 1, 2 }, { 0, 1, 3 }, { 0, 3, 4 }, { 0, 2, 4 } } );

		for ( int i = 0; i < 3; i++ ) {
			int basisIndex = ( i * 2 ) % 3;
			double xTranslation = -JR3DCoordAxisDrawable.BASIS_3D[basisIndex][0] * ( ( int ) ( this.m_extent / ( this.m_scale * 2 ) ) ) * this.m_scale;
			double yTranslation = -JR3DCoordAxisDrawable.BASIS_3D[basisIndex][1] * ( ( int ) ( this.m_extent / ( this.m_scale * 2 ) ) ) * this.m_scale;
			double zTranslation = -JR3DCoordAxisDrawable.BASIS_3D[basisIndex][2] * ( ( int ) ( this.m_extent / ( this.m_scale * 2 ) ) ) * this.m_scale;

			Transformation t = new Transformation( JR3DCoordAxisDrawable.AXIS_TRANSFORMATIONS[i].getMatrix() );
			t.multiplyOnLeft( MatrixBuilder.euclidean().translate( xTranslation, yTranslation, zTranslation ).getArray() );

			this.m_coordAxis[i].setTransformation( t );
		}
	}

	public void render( DisplayProperties properties ) {
		PointDisplayProperties rp = null;

		if ( !( properties instanceof PointDisplayProperties ) || !( properties instanceof RNDisplayProperties ) ) {
			rp = new PointDisplayProperties( properties );
//		 DP: old
//			rp.getShaderDisplayProperties().setEdgeVisible( true );
//		 DP: new
			new JR3DShaderRenderingHints(rp).setEdgeVisible( true );
		} else {
			rp = ( RNDisplayProperties ) properties;
		}

		this.m_axisDrawable.getDrawableProperties().copyPropertiesFrom(rp);
		this.m_axisDrawable.render( rp );
		for ( int i = 0; i < 3; i++ ) {
			this.m_labelPointsDrawable[i].getDrawableProperties().copyPropertiesFrom(rp);
			this.m_labelPointsDrawable[i].render( rp );
		}
	}
}

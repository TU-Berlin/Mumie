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


import java.awt.Color;

import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.display.jr3d.hints.JR3DPointRenderingHints;
import net.mumie.mathletfactory.display.jr3d.hints.JR3DShaderRenderingHints;
import net.mumie.mathletfactory.display.jr3d.hints.JR3DTextRenderingHints;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DPoint;
import de.jreality.geometry.PointSetFactory;
import de.jreality.scene.Appearance;
import de.jreality.scene.Geometry;
import de.jreality.scene.PointSet;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.data.StorageModel;
import de.jreality.scene.data.StringArray;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.DefaultTextShader;
import de.jreality.shader.RenderingHintsShader;
import de.jreality.shader.ShaderUtility;


/**
 * @author jweber
 * 
 */
public class JR3DPointSetDrawable extends JR3DDrawable {
	
	private JR3DPointRenderingHints m_pointRenderingHints;

	public static void setPointDisplayProperties( JR3DPointSetDrawable d, Appearance app, PointDisplayProperties p, boolean selected ) {
		JR3DShaderRenderingHints shaderDP = d.getShaderRenderingHints();
		JR3DPointRenderingHints pdp = d.getPointRenderingHints();
		Color diffuseColor = null;
		if ( selected ) {
			diffuseColor = p.getSelectionColor();
		} else {
			diffuseColor = shaderDP.getDiffuseColor();
		}

		JR3DTextRenderingHints tp = d.getTextRenderingHints();
		JR3DShaderRenderingHints sp = ( JR3DShaderRenderingHints ) shaderDP.clone();
		sp.setDiffuseColor( diffuseColor );

		DefaultGeometryShader geometryShader = ShaderUtility.createDefaultGeometryShader( app, true );
		RenderingHintsShader hintsShader = ShaderUtility.createDefaultRenderingHintsShader( app, true );

		DefaultPointShader pointShader = ( DefaultPointShader ) geometryShader.getPointShader();
//		pointShader.setAttenuatePointSize( p.getAttenuatePointSize() );
//		pointShader.setPointRadius( p.getSphereRadius() );
//		pointShader.setPointSize( ( double ) p.getPointRadius() );
//		pointShader.setSpheresDraw( p.getSpheresDraw() );
		pointShader.setAttenuatePointSize( new Boolean(pdp.getAttenuatePointSize() ));
		pointShader.setPointRadius( new Double(pdp.getSphereRadius() ));
		pointShader.setPointSize( new Double( pdp.getPointRadius() ));
		pointShader.setSpheresDraw( new Boolean(pdp.getSpheresDraw() ));

		if ( pdp.getSpheresDraw() ) {
			JR3DDrawable.setPolygonProperties( ( DefaultPolygonShader ) pointShader.getPolygonShader(), sp );
		} else {
			pointShader.setDiffuseColor( sp.getDiffuseColor() );
		}

		JR3DDrawable.setTextProperties( ( DefaultTextShader ) pointShader.getTextShader(), tp );
		if ( tp.renderedWithObjectColor() ) {
			( ( DefaultTextShader ) pointShader.getTextShader() ).setDiffuseColor( sp.getDiffuseColor() );
		}

//		hintsShader.setTransparencyEnabled( sp.getTransparency() != 0 );
//
//		geometryShader.setShowPoints( sp.isVertexVisible() );
//		geometryShader.setShowLines( sp.isEdgeVisible() );
//		geometryShader.setShowFaces( sp.isFaceVisible() );
		hintsShader.setTransparencyEnabled( new Boolean(p.getTransparency() != 0 ));

		geometryShader.setShowPoints( new Boolean(sp.isVertexVisible() ));
		geometryShader.setShowLines( new Boolean(sp.isEdgeVisible() ));
		geometryShader.setShowFaces( new Boolean(sp.isFaceVisible() ));
	}

	public JR3DPointSetDrawable() {
		this(null);
	}

	public JR3DPointSetDrawable( final String name ) {
		super( name );
		m_pointRenderingHints = new JR3DPointRenderingHints(getDrawableProperties());
	}

	public void setPoints( MMAffine3DPoint[] points ) {
		double[][] coord = new double[points.length][];
		for ( int i = 0; i < points.length; i++ ) {
			coord[i] = points[i].getXYZ();
		}
		this.setPoints( coord );

		String[] labels = new String[points.length];
		for ( int i = 0; i < labels.length; i++ ) {
			labels[i] = points[i].getLabel();
		}
		this.setPointsLabels( labels );
	}

	public void setPoints( double[][] points ) {
		Geometry geometry = this.m_sceneContent.getGeometry();

		if ( geometry != null ) {
			if ( geometry instanceof PointSet ) {
				PointSet pSet = ( PointSet ) this.m_sceneContent.getGeometry();
				if ( pSet.getNumPoints() == points.length ) {
					pSet.setVertexAttributes( Attribute.COORDINATES, StorageModel.DOUBLE_ARRAY.array( 3 ).createReadOnly( points ) );
				} else {
					pSet.setNumPoints( points.length );
					pSet.setVertexAttributes( Attribute.COORDINATES, StorageModel.DOUBLE_ARRAY.array( 3 ).createReadOnly( points ) );
				}
			}
		} else {
			PointSetFactory psf = new PointSetFactory();
			psf.setVertexCount( points.length );
			psf.setVertexCoordinates( points );
			psf.update();

			this.m_sceneContent.setGeometry( psf.getGeometry() );
		}
	}

	public void setPointLabel( String label, int index ) {
		Geometry g = this.m_sceneContent.getGeometry();

		if ( g instanceof PointSet ) {
			PointSet pointSet = ( PointSet ) g;

			String[] labels = new String[pointSet.getNumPoints()];

			StringArray labelSet = ( StringArray ) pointSet.getVertexAttributes( Attribute.LABELS );
			for ( int i = 0; i < labels.length; i++ ) {
				if ( labelSet == null ) {
					labels[i] = null;
				} else {
					labels[i] = labelSet.getValueAt( i );
				}
			}

			labels[index] = label;
			pointSet.setVertexAttributes( Attribute.LABELS, StorageModel.STRING_ARRAY.createReadOnly( labels ) );
		}
	}

	public void setPointsLabels( String[] labels ) {
		Geometry g = this.m_sceneContent.getGeometry();
		if ( g instanceof PointSet ) {
			PointSet pointSet = ( PointSet ) g;
			pointSet.setVertexAttributes( Attribute.LABELS, StorageModel.STRING_ARRAY.createReadOnly( labels ) );
		}
	}

	public void render( DisplayProperties properties ) {
		PointDisplayProperties pointProperties = null;
		if ( !( properties instanceof PointDisplayProperties ) ) {
			pointProperties = new PointDisplayProperties( properties );
		} else {
			pointProperties = ( PointDisplayProperties ) properties;
		}
		JR3DPointSetDrawable.setPointDisplayProperties( this, this.m_sceneContent.getAppearance(), pointProperties, this.m_selected );
	}
	
	protected JR3DPointRenderingHints getPointRenderingHints() {
		return m_pointRenderingHints;
	}
}
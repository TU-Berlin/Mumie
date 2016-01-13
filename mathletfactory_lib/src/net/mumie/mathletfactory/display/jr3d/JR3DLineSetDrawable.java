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
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.jr3d.hints.JR3DLineRenderingHints;
import net.mumie.mathletfactory.display.jr3d.hints.JR3DShaderRenderingHints;
import net.mumie.mathletfactory.display.jr3d.hints.JR3DTextRenderingHints;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DLineSegment;
import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.scene.Appearance;
import de.jreality.scene.Geometry;
import de.jreality.scene.IndexedLineSet;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.data.IntArrayArray;
import de.jreality.scene.data.StorageModel;
import de.jreality.scene.data.StringArray;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.DefaultTextShader;
import de.jreality.shader.RenderingHintsShader;
import de.jreality.shader.ShaderUtility;


/**
 * @author jweber
 * 
 */
public class JR3DLineSetDrawable extends JR3DPointSetDrawable {
	
	private JR3DLineRenderingHints m_lineRenderingHints;

	public static void setLineDisplayProperties( JR3DLineSetDrawable d, Appearance app, LineDisplayProperties p, boolean selected ) {
		JR3DLineRenderingHints ldp = d.getLineRenderingHints();
		JR3DShaderRenderingHints sdp = d.getShaderRenderingHints();
		Color diffuseColor = null;
		if ( selected ) {
			diffuseColor = p.getSelectionColor();
		} else {
			diffuseColor = sdp.getDiffuseColor();
		}

		JR3DTextRenderingHints tp = d.getTextRenderingHints();
		JR3DShaderRenderingHints sp = new JR3DShaderRenderingHints(sdp);//( JR3DShaderRenderingHints ) sdp.clone();
		sp.setDiffuseColor( diffuseColor );

		DefaultGeometryShader geometryShader = ShaderUtility.createDefaultGeometryShader( app, true );
		RenderingHintsShader hintsShader = ShaderUtility.createDefaultRenderingHintsShader( app, true );

		DefaultLineShader lineShader = ( DefaultLineShader ) geometryShader.getLineShader();
//		lineShader.setLineWidth( p.getLineWidth() );
//		lineShader.setTubeDraw( p.getTubesDraw() );
//		lineShader.setTubeRadius( p.getTubeRadius() );
		lineShader.setLineWidth( new Double(ldp.getLineWidth() ));
		lineShader.setTubeDraw( new Boolean(ldp.getTubesDraw() ));
		lineShader.setTubeRadius( new Double(ldp.getTubeRadius() ));

		if ( ldp.getTubesDraw() ) {
			JR3DDrawable.setPolygonProperties( ( DefaultPolygonShader ) lineShader.getPolygonShader(), sp );
		} else {
			lineShader.setDiffuseColor( sp.getDiffuseColor() );
		}

		JR3DDrawable.setTextProperties( ( DefaultTextShader ) lineShader.getTextShader(), tp );
		if ( tp.renderedWithObjectColor() ) {
			( ( DefaultTextShader ) lineShader.getTextShader() ).setDiffuseColor( sp.getDiffuseColor() );
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

	public JR3DLineSetDrawable() {
		this(null);
	}

	public JR3DLineSetDrawable( final String name ) {
		super( name );
		m_lineRenderingHints = new JR3DLineRenderingHints(getDrawableProperties());
	}

	public void setPoints( double[][] points ) {
		Geometry geometry = this.m_sceneContent.getGeometry();

		if ( geometry != null ) {
			if ( geometry instanceof IndexedLineSet ) {
				IndexedLineSet lineSet = ( IndexedLineSet ) this.m_sceneContent.getGeometry();

				if ( lineSet.getNumPoints() == points.length ) {
					super.setPoints( points );
				} else {
					throw new IllegalArgumentException( "number of points not correct" );
				}
			}
		} 
	}

	public void setLines( MMAffine3DLineSegment[] lines ) {
		double[][] coord = new double[lines.length * 2][];
		int[][] indices = new int[lines.length][];

		NumberTuple pointCoord = null;
		for ( int i = 0; i < lines.length; i++ ) {
			indices[i] = new int[] { i * 2, i * 2 + 1 };

			pointCoord = lines[i].getInitialPoint().getAffineCoordinatesOfPoint();
			coord[indices[i][0]] = new double[] { pointCoord.getEntry( 1 ).getDouble(), pointCoord.getEntry( 2 ).getDouble(), pointCoord.getEntry( 3 ).getDouble() };
			pointCoord = lines[i].getEndPoint().getAffineCoordinatesOfPoint();
			coord[indices[i][1]] = new double[] { pointCoord.getEntry( 1 ).getDouble(), pointCoord.getEntry( 2 ).getDouble(), pointCoord.getEntry( 3 ).getDouble() };
		}
		this.setLines( coord, indices );
	}

	public void setLines( double[][] points, int[][] indices ) {
		Geometry geometry = this.m_sceneContent.getGeometry();

		if ( geometry != null ) {
			if ( geometry instanceof IndexedLineSet ) {
				IndexedLineSet lineSet = ( IndexedLineSet ) this.m_sceneContent.getGeometry();
				if ( lineSet.getNumPoints() == points.length && lineSet.getNumEdges() == indices.length ) {
					lineSet.setVertexAttributes( Attribute.COORDINATES, StorageModel.DOUBLE_ARRAY.array( 3 ).createReadOnly( points ) );
					lineSet.setEdgeAttributes( Attribute.INDICES, new IntArrayArray.Array( indices ) );
				} else {
					lineSet.setNumPoints( points.length );
					lineSet.setVertexAttributes( Attribute.COORDINATES, StorageModel.DOUBLE_ARRAY.array( 3 ).createReadOnly( points ) );

					lineSet.setNumEdges( indices.length );
					lineSet.setEdgeAttributes( Attribute.INDICES, new IntArrayArray.Array( indices ) );
				}
			}
		} else {
			IndexedLineSetFactory lsf = new IndexedLineSetFactory();

			lsf.setVertexCount( points.length );
			lsf.setVertexCoordinates( points );
			lsf.setLineCount( indices.length );
			lsf.setEdgeIndices( indices );
			lsf.update();

			this.m_sceneContent.setGeometry( lsf.getGeometry() );
		}
	}

	public void setLineLabel( String label, int index ) {
		Geometry g = this.m_sceneContent.getGeometry();

		if ( g instanceof IndexedLineSet ) {
			IndexedLineSet lineSet = ( IndexedLineSet ) g;

			String[] labels = new String[lineSet.getNumEdges()];

			StringArray labelSet = ( StringArray ) lineSet.getEdgeAttributes( Attribute.LABELS );
			for ( int i = 0; i < labels.length; i++ ) {
				if ( labelSet == null ) {
					labels[i] = null;
				} else {
					labels[i] = labelSet.getValueAt( i );
				}
			}

			labels[index] = label;
			lineSet.setEdgeAttributes( Attribute.LABELS, StorageModel.STRING_ARRAY.createReadOnly( labels ) );
		}
	}

	public void setLinesLabels( String[] labels ) {
		Geometry g = this.m_sceneContent.getGeometry();

		if ( g instanceof IndexedLineSet ) {
			IndexedLineSet lineSet = ( IndexedLineSet ) g;
			lineSet.setEdgeAttributes( Attribute.LABELS, StorageModel.STRING_ARRAY.createReadOnly( labels ) );
		}
	}

	public void render( DisplayProperties properties ) {
		LineDisplayProperties lineProperties = null;
		if ( !( properties instanceof LineDisplayProperties ) ) {
			lineProperties = new LineDisplayProperties( properties );
		} else {
			lineProperties = ( LineDisplayProperties ) properties;
		}
		JR3DLineSetDrawable.setLineDisplayProperties( this, this.m_sceneContent.getAppearance(), lineProperties, this.m_selected );
	}
	
	protected JR3DLineRenderingHints getLineRenderingHints() {
		return m_lineRenderingHints;
	}
}
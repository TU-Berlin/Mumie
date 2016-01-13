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
import net.mumie.mathletfactory.display.SurfaceDisplayProperties;
import net.mumie.mathletfactory.display.jr3d.hints.JR3DShaderRenderingHints;
import net.mumie.mathletfactory.display.jr3d.hints.JR3DTextRenderingHints;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.IndexedFaceSetUtility;
import de.jreality.scene.Appearance;
import de.jreality.scene.Geometry;
import de.jreality.scene.IndexedFaceSet;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.data.DoubleArrayArray;
import de.jreality.scene.data.IntArrayArray;
import de.jreality.scene.data.StorageModel;
import de.jreality.scene.data.StringArray;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.DefaultTextShader;
import de.jreality.shader.RenderingHintsShader;
import de.jreality.shader.ShaderUtility;


/**
 * @author jweber
 * 
 */
public class JR3DFaceSetDrawable extends JR3DLineSetDrawable {

	public static void setSurfaceDisplayProperties( JR3DFaceSetDrawable d, Appearance app, SurfaceDisplayProperties p, boolean selected ) {
		JR3DShaderRenderingHints sdp = d.getShaderRenderingHints();
		Color diffuseColor = null;
		if ( selected ) {
			diffuseColor = p.getSelectionColor();
		} else {
			diffuseColor = sdp.getDiffuseColor();
		}

		JR3DTextRenderingHints tp = d.getTextRenderingHints();
		JR3DShaderRenderingHints sp = ( JR3DShaderRenderingHints ) sdp.clone();
		sp.setDiffuseColor( diffuseColor );

		DefaultGeometryShader geometryShader = ShaderUtility.createDefaultGeometryShader( app, true );
		RenderingHintsShader hintsShader = ShaderUtility.createDefaultRenderingHintsShader( app, true );

		if ( p.isFilled() ) {
			DefaultPolygonShader polyShader = ( DefaultPolygonShader ) geometryShader.getPolygonShader();
			JR3DDrawable.setPolygonProperties( polyShader, sp );

			JR3DDrawable.setTextProperties( ( DefaultTextShader ) polyShader.getTextShader(), tp );
			if ( tp.renderedWithObjectColor() ) {
				( ( DefaultTextShader ) polyShader.getTextShader() ).setDiffuseColor( sp.getDiffuseColor() );
			}
		}

		if ( p.isBorderDisplayed() ) {
			LineDisplayProperties lp = new LineDisplayProperties( p );
			lp.setLineWidth( p.getBorderWidth() );
			new JR3DShaderRenderingHints(lp).setDiffuseColor( p.getBorderColor() );

			JR3DLineSetDrawable.setLineDisplayProperties( d, app, lp, selected );
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

	public static void createSurface( JR3DFaceSetDrawable face, double[][] vertices, int nv, int nu ) {
		// Anzahl der Punkte
		int sp = ( nu + 1 ) * ( nv + 1 );

		if ( vertices.length != sp ) {
			throw new IllegalArgumentException( "number of vertices must be equals '( nu + 1 ) * ( nv + 1 )'" );
		}

		// Anzahl Quadrate des Polygonobjektes
		int polygonsCount = nu * nv;

		int[][] faces = new int[polygonsCount][];

		double[][] textures = new double[sp][2];
		double xStep = 1.0 / nu;
		double yStep = 1.0 / nv;

		// Flaechen setzen
		int zz = 0;
		for ( int j = 0; j <= nv; j++ ) {
			for ( int i = 0; i <= nu; i++ ) {
				// Punkte für ein Quadrat definieren
				int p1 = i * ( nv + 1 ) + j;

				if ( i < nu && j < nv ) {
					int p2 = i * ( nv + 1 ) + j + 1;
					int p3 = ( i + 1 ) * ( nv + 1 ) + j + 1;
					int p4 = ( i + 1 ) * ( nv + 1 ) + j;
					faces[zz++] = new int[] { p1, p2, p3, p4 };
				}

				textures[p1] = new double[] { xStep * i, yStep * j };
			}
		}

		face.setFaces( vertices, faces );

		IndexedFaceSet faceSet = ( IndexedFaceSet ) face.getViewContent().getGeometry();
		faceSet.setVertexAttributes( Attribute.TEXTURE_COORDINATES, new DoubleArrayArray.Array( textures, 2 ) );
	}

	public JR3DFaceSetDrawable() {
		super();
	}

	public JR3DFaceSetDrawable( final String name ) {
		super( name );
	}

	public void setLines( double[][] vertices, int[][] indices ) {
		throw new UnsupportedOperationException( "unsupported operation" );
	}

	public void setFaces( NumberTuple[] vertices, int[][] faceIndices ) {
		double[][] doubleVertices = new double[vertices.length][3];
		for ( int i = 0; i < vertices.length; i++ ) {
			doubleVertices[i][0] = vertices[i].getEntry( 1, 1 ).getDouble();
			doubleVertices[i][1] = vertices[i].getEntry( 2, 1 ).getDouble();
			doubleVertices[i][2] = vertices[i].getEntry( 3, 1 ).getDouble();
		}
		this.setFaces( doubleVertices, faceIndices );
	}

	public void setFaces( double[][] vertices, int[][] faceIndices ) {
		Geometry geometry = this.m_sceneContent.getGeometry();

		if ( geometry != null ) {
			if ( geometry instanceof IndexedFaceSet ) {
				IndexedFaceSet faceSet = ( IndexedFaceSet ) this.m_sceneContent.getGeometry();
				if ( faceSet.getNumPoints() == vertices.length ) {
					faceSet.setVertexAttributes( Attribute.COORDINATES, StorageModel.DOUBLE_ARRAY.array( 3 ).createReadOnly( vertices ) );
					faceSet.setFaceAttributes( Attribute.INDICES, new IntArrayArray.Array( faceIndices ) );
				} else {
					faceSet.setNumPoints( vertices.length );
					faceSet.setVertexAttributes( Attribute.COORDINATES, StorageModel.DOUBLE_ARRAY.array( 3 ).createReadOnly( vertices ) );

					faceSet.setNumFaces( faceIndices.length );
					faceSet.setFaceAttributes( Attribute.INDICES, new IntArrayArray.Array( faceIndices ) );
				}

				IndexedFaceSetUtility.calculateAndSetEdgesFromFaces( faceSet );
			}
		} else {
			IndexedFaceSetFactory fsf = new IndexedFaceSetFactory();

			fsf.setVertexCount( vertices.length );
			fsf.setVertexCoordinates( vertices );

			fsf.setFaceCount( faceIndices.length );
			fsf.setFaceIndices( faceIndices );

			fsf.setGenerateEdgesFromFaces( true );
			fsf.setGenerateFaceNormals( true );
			fsf.setGenerateVertexNormals( true );

			fsf.update();

			this.m_sceneContent.setGeometry( fsf.getGeometry() );
		}

	}

	public void setFaceLabel( String label, int index ) {
		Geometry g = this.m_sceneContent.getGeometry();

		if ( g instanceof IndexedFaceSet ) {
			IndexedFaceSet faceSet = ( IndexedFaceSet ) g;

			String[] labels = new String[faceSet.getNumFaces()];

			StringArray labelSet = ( StringArray ) faceSet.getFaceAttributes( Attribute.LABELS );
			for ( int i = 0; i < labels.length; i++ ) {
				if ( labelSet == null ) {
					labels[i] = null;
				} else {
					labels[i] = labelSet.getValueAt( i );
				}
			}

			labels[index] = label;
			faceSet.setFaceAttributes( Attribute.LABELS, StorageModel.STRING_ARRAY.createReadOnly( labels ) );
		}
	}

	public void setFacesLabels( String[] labels ) {
		Geometry g = this.m_sceneContent.getGeometry();
		if ( g instanceof IndexedFaceSet ) {
			IndexedFaceSet faceSet = ( IndexedFaceSet ) g;
			faceSet.setFaceAttributes( Attribute.LABELS, StorageModel.STRING_ARRAY.createReadOnly( labels ) );
		}
	}

	public void render( DisplayProperties properties ) {
		SurfaceDisplayProperties surfaceProps = null;
		if ( !( properties instanceof SurfaceDisplayProperties ) ) {
			surfaceProps = new SurfaceDisplayProperties( properties );
		} else {
			surfaceProps = ( SurfaceDisplayProperties ) properties;
		}
		JR3DFaceSetDrawable.setSurfaceDisplayProperties( this, this.m_sceneContent.getAppearance(), surfaceProps, this.m_selected );
	}
}

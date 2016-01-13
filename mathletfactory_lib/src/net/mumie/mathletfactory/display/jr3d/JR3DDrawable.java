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


import java.io.File;
import java.io.IOException;

import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.display.jr3d.hints.JR3DShaderRenderingHints;
import net.mumie.mathletfactory.display.jr3d.hints.JR3DTextRenderingHints;
import de.jreality.scene.Appearance;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.DefaultTextShader;
import de.jreality.shader.ImageData;
import de.jreality.shader.Texture2D;
import de.jreality.util.Input;


/**
 * @author jweber
 * 
 */
public abstract class JR3DDrawable extends CanvasDrawable {

	protected boolean m_selected;
	protected SceneGraphComponent m_sceneContent;
	
	private JR3DShaderRenderingHints m_shaderRenderingHints;
	private JR3DTextRenderingHints m_textRenderingHints;
	
	private String m_label;

	public static void setPolygonProperties( DefaultPolygonShader dps, JR3DShaderRenderingHints sp ) {
		dps.setAmbientColor( sp.getAmbientColor() );
//		dps.setAmbientCoefficient( sp.getAmbientCoeffizient() );
		dps.setAmbientCoefficient( new Double(sp.getAmbientCoeffizient() ));

		dps.setDiffuseColor( sp.getDiffuseColor() );
//		dps.setDiffuseCoefficient( sp.getDiffuseCoeffizient() );
		dps.setDiffuseCoefficient( new Double(sp.getDiffuseCoeffizient() ));

		dps.setSpecularColor( sp.getSpecularColor() );
//		dps.setSpecularCoefficient( sp.getSpecularCoeffizient() );
//		dps.setSpecularExponent( sp.getSpecularExponent() );
		dps.setSpecularCoefficient( new Double(sp.getSpecularCoeffizient() ));
		dps.setSpecularExponent( new Double(sp.getSpecularExponent() ));

		// Texturen
		try {
			String imagePath = sp.getTextureFilePath();
			if ( imagePath != null ) {
				ImageData id = ImageData.load( Input.getInput( new File( imagePath ) ) );
				Texture2D tex = dps.createTexture2d();
				tex.setImage( id );
//				tex.setApplyMode( Texture2D.GL_MODULATE );
				tex.setApplyMode( new Integer(Texture2D.GL_MODULATE ));
			}
		} catch ( IOException e ) {
			System.err.println( "cannot create Texture: " + sp.getTextureFilePath() );
		}
	}

	public static void setTextProperties( DefaultTextShader dts, JR3DTextRenderingHints tp ) {
//		dts.setAlignment( tp.getAlignment() );
		dts.setAlignment( new Integer(tp.getAlignment() ));
		dts.setDiffuseColor( tp.getDiffuseColor() );
		dts.setFont( tp.getFont() );
//		dts.setScale( tp.getScale() );
//		dts.setShowLabels( tp.isLabelVisible() );
		dts.setScale( new Double(tp.getScale() ));
		dts.setShowLabels( new Boolean(tp.isLabelVisible() ));
	}

	public JR3DDrawable() {
		this( null );
	}

	public JR3DDrawable( final String name ) {
//		if ( name == null || name.trim().equals( "" ) ) {
			this.m_sceneContent = new SceneGraphComponent();
//		} else {
//			this.m_sceneContent = new SceneGraphComponent( name );
//		}
		this.m_sceneContent.setAppearance( new Appearance() );
		m_shaderRenderingHints = new JR3DShaderRenderingHints(getDrawableProperties());
		m_textRenderingHints = new JR3DTextRenderingHints(getDrawableProperties());
	}

	public void setVisible( boolean b ) {
		this.m_sceneContent.setVisible( b );
		super.setVisible( b );
	}

	public void setSelected( boolean b ) {
		this.m_selected = b;
	}

	public boolean isSelected() {
		return this.m_selected;
	}

	public SceneGraphComponent getViewContent() {
		return this.m_sceneContent;
	}

	protected void drawObject( MMCanvas canvas, DisplayProperties properties ) {}

	protected void drawSelection( Object destination, DisplayProperties properties ) {}

	/**
	 * @deprecated
	 */
//	@Deprecated
	public boolean isAtScreenLocation( int xOnScreen, int yOnScreen ) {
		return false;
	}

	public void render( DisplayProperties properties ) {}

	protected JR3DShaderRenderingHints getShaderRenderingHints() {
		return m_shaderRenderingHints;
	}
	
	protected JR3DTextRenderingHints getTextRenderingHints() {
		return m_textRenderingHints;
	}
	
	/**
	 * Returns the label of this drawable. May be <code>null</code>.
	 */
	public String getLabel() {
		return m_label;
	}
	
	/**
	 * Sets the label of this drawable. May be <code>null</code>.
	 */
	public void setLabel(String label) {
		if(m_label != null && !m_label.equals(label)) {
			m_label = label;
			m_sceneContent.setName(label);
		}
	}
}

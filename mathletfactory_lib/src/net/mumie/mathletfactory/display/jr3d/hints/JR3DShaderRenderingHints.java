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

package net.mumie.mathletfactory.display.jr3d.hints;


import java.awt.Color;

import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.ScreenTypeRenderingHints;
import net.mumie.mathletfactory.util.property.DefaultPropertyMap;
import net.mumie.mathletfactory.util.property.PropertyMapIF;
import de.jreality.shader.CommonAttributes;


/**
 * This class contains common rendering hints for JReality drawables.
 * These can consist of a set of points, line segments and/or surfaces.
 * The visualization of each of these set members can be done through
 * the following methods:
 * <ul>
 * <li>{@link #setFaceVisible(boolean)}</li>
 * <li>{@link #setEdgeVisible(boolean)}</li>
 * <li>{@link #setVertexVisible(boolean)}</li>
 * </ul>
 * 
 * @author weber, gronau
 * @mm.docstatus finished
 */
public class JR3DShaderRenderingHints extends ScreenTypeRenderingHints {

	/** Base path for all {@link JR3DShaderRenderingHints} properties. */
	public final static String DP_BASE_PATH = "display.jr3d.shader.";
	
	//
	// property names
	//

	/** 
	 * Name of the <i>face draw</i> property.
	 * @see #isFaceVisible()
	 */
	public final static String FACE_DRAW_PROPERTY = DP_BASE_PATH + CommonAttributes.FACE_DRAW;

	/** 
	 * Name of the <i>edge draw</i> property.
	 * @see #isEdgeVisible()
	 */
	public final static String EDGE_DRAW_PROPERTY = DP_BASE_PATH + CommonAttributes.EDGE_DRAW;

	/** 
	 * Name of the <i>vertex draw</i> property.
	 * @see #isVertexVisible()
	 */
	public final static String VERTEX_DRAW_PROPERTY = DP_BASE_PATH + CommonAttributes.VERTEX_DRAW;

	/** 
	 * Name of the <i>ambient color</i> property.
	 * @see #getAmbientColor()
	 */
	public final static String AMBIENT_COLOR_PROPERTY = DP_BASE_PATH + CommonAttributes.AMBIENT_COLOR;

	/** 
	 * Name of the <i>ambient coefficient</i> property.
	 * @see #getAmbientCoeffizient()
	 */
	public final static String AMBIENT_COEFFICIENT_PROPERTY = DP_BASE_PATH + CommonAttributes.AMBIENT_COEFFICIENT;

	/** 
	 * Name of the <i>diffuse color</i> property.
	 * @see #getDiffuseColor()
	 */
	public final static String DIFFUSE_COLOR_PROPERTY = DP_BASE_PATH + CommonAttributes.DIFFUSE_COLOR;

	/** 
	 * Name of the <i>diffuse coefficient</i> property.
	 * @see #getDiffuseCoeffizient()
	 */
	public final static String DIFFUSE_COEFFICIENT_PROPERTY = DP_BASE_PATH + CommonAttributes.DIFFUSE_COEFFICIENT;

	/** 
	 * Name of the <i>specular color</i> property.
	 * @see #getSpecularColor()
	 */
	public final static String SPECULAR_COLOR_PROPERTY = DP_BASE_PATH + CommonAttributes.SPECULAR_COLOR;

	/** 
	 * Name of the <i>specular coefficient</i> property.
	 * @see #getSpecularCoeffizient()
	 */
	public final static String SPECULAR_COEFFICIENT_PROPERTY = DP_BASE_PATH + CommonAttributes.SPECULAR_COEFFICIENT;

	/** 
	 * Name of the <i>specular exponent</i> property.
	 * @see #getSpecularExponent()
	 */
	public final static String SPECULAR_EXPONENT_PROPERTY = DP_BASE_PATH + CommonAttributes.SPECULAR_EXPONENT;

//	/** 
//	 * Name of the <i>transparency</i> property.
//	 * @see #getTransparency()
//	 */
//	public final static String TRANSPARENCY_PROPERTY = DP_BASE_PATH + CommonAttributes.TRANSPARENCY;

	/** 
	 * Name of the <i>texture file</i> property.
	 * @see #getTextureFilePath()
	 */
	public final static String TEXTURE_FILE_PROPERTY = DP_BASE_PATH + "textureFile";

//	/**
//	 * Name of the <i>border color</i> property.
//	 * @see #getBorderColor()
//	 */
//	public final static String BORDER_COLOR_PROPERTY = DP_BASE_PATH + "borderColor";
	
//	/**
//	 * Name of the <i>border width</i> property.
//	 * @see #getBorderWidth()
//	 */
//	public final static String BORDER_WIDTH_PROPERTY = DP_BASE_PATH + "borderWidth";

//	/**
//	 * Name of the <i>selection color</i> property.
//	 * @see #getSelectionColor()
//	 */
//	public final static String SELECTION_COLOR_PROPERTY = DP_BASE_PATH + "selectionColor";

//	/**
//	 * Name of the <i>dash pattern</i> property.
//	 * @see #getDashPattern()
//	 */
//	public final static String DASH_PATTERN_PROPERTY = DP_BASE_PATH + "dashPattern";

	//
	// default values
	//

	/** Default value for the <i>ambient color</i> property. */
	public final static Color AMBIENT_COLOR_DEFAULT = Color.WHITE;

	/** Default value for the <i>ambient coefficient</i> property. */
	public final static double AMBIENT_COEFFICIENT_DEFAULT = 0.05;
	
	/** Default value for the <i>diffuse color</i> property. */
	public final static Color DIFFUSE_COLOR_DEFAULT = Color.BLACK;
	
	/** Default value for the <i>diffuse coefficient</i> property. */
	public final static double DIFFUSE_COEFFICIENT_DEFAULT = CommonAttributes.DIFFUSE_COEFFICIENT_DEFAULT;
	
	/** Default value for the <i>specular color</i> property. */
	public final static Color SPECULAR_COLOR_DEFAULT = Color.WHITE;
	
	/** Default value for the <i>specular coefficient</i> property. */
	public final static double SPECULAR_COEFFICIENT_DEFAULT = CommonAttributes.SPECULAR_COEFFICIENT_DEFAULT;
	
	/** Default value for the <i>specular exponent</i> property. */
	public final static double SPECULAR_EXPONENT_DEFAULT = CommonAttributes.SPECULAR_EXPONENT_DEFAULT;
	
//	/** Default value for the <i>transparency</i> property. */
//	public final static double TRANSPARENCY_DEFAULT = 0.0;
	
//	/** Default value for the <i>border color</i> property. */
//	public final static Color BORDER_COLOR_DEFAULT = Color.BLACK;
	
//	/** Default value for the <i>border width</i> property. */
//	public final static double BORDER_WIDTH_DEFAULT = 1.0;
	
	/** Default value for the <i>selection color</i> property. */
	public final static Color SELECTION_COLOR_DEFAULT = Color.ORANGE;
	
//	/** Default value for a non-dashed pattern. */
//	public final static float[] NO_DASH_DEFAULT = new float[] { 10f, 0f };
//	
//	/** Default value for the <i>dash pattern</i> property. */
//	public final static float[] DASH_PATTERN_DEFAULT = new float[] { 10f, 10f };

//	/** Field holding a reference to the display properties instance. */
//	private final DisplayProperties m_props;
	
	public JR3DShaderRenderingHints(PropertyMapIF properties) {
		super(properties);
	}

	/**
	 * Returns if the drawable's faces are visible.
	 * The default value is <code>true</code>.
	 */
	public boolean isFaceVisible() {
		Object value = this.getProperty( FACE_DRAW_PROPERTY );
		if ( value == null ) {
			value = new Boolean( true );
		}
		return (( Boolean ) value).booleanValue();
	}

	/**
	 * Sets whether the drawable's faces should be visible
	 * and returns the previous value (may be the default value).
	 * @see #isFaceVisible()
	 */
	public boolean setFaceVisible( boolean v ) {
		Object value = this.setProperty( FACE_DRAW_PROPERTY, new Boolean( v ) );
		if ( value == null ) {
			value = new Boolean( true );
		}
		return (( Boolean ) value).booleanValue();
	}

	/**
	 * Returns if the drawable's edges are visible.
	 * The default value is <code>true</code>.
	 */
	public boolean isEdgeVisible() {
		Object value = this.getProperty( EDGE_DRAW_PROPERTY );
		if ( value == null ) {
			value = new Boolean( true );
		}
		return (( Boolean ) value).booleanValue();
	}

	/**
	 * Sets whether the drawable's edges should be visible
	 * and returns the previous value (may be the default value).
	 * @see #isEdgeVisible()
	 */
	public boolean setEdgeVisible( boolean v ) {
		Object value = this.setProperty( EDGE_DRAW_PROPERTY, new Boolean( v ) );
		if ( value == null ) {
			value = new Boolean( true );
		}
		return (( Boolean ) value).booleanValue();
	}

	/**
	 * Returns if the drawable's vertices are visible.
	 * The default value is <code>true</code>.
	 */
	public boolean isVertexVisible() {
		Object value = this.getProperty( VERTEX_DRAW_PROPERTY );
		if ( value == null ) {
			value = new Boolean( true );
		}
		return (( Boolean ) value).booleanValue();
	}

	/**
	 * Sets whether the drawable's vertices should be visible
	 * and returns the previous value (may be the default value).
	 * @see #isEdgeVisible()
	 */
	public boolean setVertexVisible( boolean v ) {
		Object value = this.setProperty( VERTEX_DRAW_PROPERTY, new Boolean( v ) );
		if ( value == null ) {
			value = new Boolean( true );
		}
		return (( Boolean ) value).booleanValue();
	}

	/**
	 * Returns the ambient color, i.e. the light color of the outgoing radiation.
	 */
	public Color getAmbientColor() {
		Object value = this.getProperty( AMBIENT_COLOR_PROPERTY );
		if ( value == null )
			value = getProperty(DisplayProperties.OBJECT_COLOR_PROPERTY);
		if ( value == null )
			value = DisplayProperties.DEFAULT.getProperty(DisplayProperties.OBJECT_COLOR_PROPERTY);
		return ( Color ) value;
	}

	/**
	 * Sets the ambient color and returns the previous value (may be the default value).
	 * @see #getAmbientColor()
	 */
	public Color setAmbientColor( Color c ) {
		Object value = this.setProperty( AMBIENT_COLOR_PROPERTY, c );
		if ( value == null ) {
			value = JR3DShaderRenderingHints.AMBIENT_COLOR_DEFAULT;
		}
		return ( Color ) value;
	}

	/**
	 * Returns the ambient coefficient, i.e. the light intensity of the outgoing radiation.
	 */
	public double getAmbientCoeffizient() {
		Object value = this.getProperty( AMBIENT_COEFFICIENT_PROPERTY );
		if ( value == null ) {
			value = new Double( JR3DShaderRenderingHints.AMBIENT_COEFFICIENT_DEFAULT );
		}
		return (( Double ) value).doubleValue();
	}

	/**
	 * Sets the ambient coefficient and returns the previous value (may be the default value).
	 * @see #getAmbientCoeffizient()
	 */
	public double setAmbientCoeffizient( double c ) {
		Object value = this.setProperty( AMBIENT_COEFFICIENT_PROPERTY, new Double( c ) );
		if ( value == null ) {
			value = new Double( JR3DShaderRenderingHints.AMBIENT_COEFFICIENT_DEFAULT );
		}
		return (( Double ) value).doubleValue();
	}

	/**
	 * Returns the diffuse color, i.e. the real object color.
	 */
	public Color getDiffuseColor() {
		Object value = this.getProperty( DIFFUSE_COLOR_PROPERTY );
		if ( value == null )
			value = getProperty(DisplayProperties.OBJECT_COLOR_PROPERTY);
		if ( value == null )
			value = DisplayProperties.DEFAULT.getProperty(DisplayProperties.OBJECT_COLOR_PROPERTY);
		return ( Color ) value;
	}

	/**
	 * Sets the diffuse color and returns the previous value (may be the default value).
	 * @see #getDiffuseColor()
	 */
	public Color setDiffuseColor( Color c ) {
		this.setProperty( AMBIENT_COLOR_PROPERTY, c );
		Object value = this.setProperty( DIFFUSE_COLOR_PROPERTY, c );
		if ( value == null ) {
			value = JR3DShaderRenderingHints.DIFFUSE_COLOR_DEFAULT;
		}
		return ( Color ) value;
	}

	/**
	 * Returns the diffuse coefficient, i.e. the intensity of the object's color.
	 */
	public double getDiffuseCoeffizient() {
		Object value = this.getProperty( DIFFUSE_COEFFICIENT_PROPERTY );
		if ( value == null ) {
			value = new Double( JR3DShaderRenderingHints.DIFFUSE_COEFFICIENT_DEFAULT );
		}
		return (( Double ) value).doubleValue();
	}

	/**
	 * Sets the diffuse coefficient and returns the previous value (may be the default value).
	 * @see #getDiffuseColor()
	 */
	public double setDiffuseCoeffizient( double c ) {
		Object value = this.setProperty( DIFFUSE_COEFFICIENT_PROPERTY, new Double( c ) );
		if ( value == null ) {
			value = new Double( JR3DShaderRenderingHints.DIFFUSE_COEFFICIENT_DEFAULT );
		}
		return (( Double ) value).doubleValue();
	}

	/**
	 * Returns the specular color, i.e. the color of reflected lights on the object's surface.
	 */
	public Color getSpecularColor() {
		Object value = this.getProperty( SPECULAR_COLOR_PROPERTY );
		if ( value == null ) {
			value = JR3DShaderRenderingHints.SPECULAR_COLOR_DEFAULT;
		}
		return ( Color ) value;
	}

	/**
	 * Sets the specular color and returns the previous value (may be the default value).
	 * @see #getSpecularColor()
	 */
	public Color setSpecularColor( Color c ) {
		Object value = this.setProperty( SPECULAR_COLOR_PROPERTY, c );
		if ( value == null ) {
			value = JR3DShaderRenderingHints.SPECULAR_COLOR_DEFAULT;
		}
		return ( Color ) value;
	}

	/**
	 * Returns the specular coefficient, i.e. the color's intensity 
	 * of reflected lights on the object's surface.
	 */
	public double getSpecularCoeffizient() {
		Object value = this.getProperty( SPECULAR_COEFFICIENT_PROPERTY );
		if ( value == null ) {
			value = new Double( JR3DShaderRenderingHints.SPECULAR_COEFFICIENT_DEFAULT );
		}
		return (( Double ) value).doubleValue();
	}

	/**
	 * Sets the specular coefficient and returns the previous value (may be the default value).
	 * @see #getSpecularCoeffizient()
	 */
	public double setSpecularCoeffizient( double c ) {
		Object value = this.setProperty( SPECULAR_COEFFICIENT_PROPERTY, new Double( c ) );
		if ( value == null ) {
			value = new Double( JR3DShaderRenderingHints.SPECULAR_COEFFICIENT_DEFAULT );
		}
		return (( Double ) value).doubleValue();
	}

	/**
	 * Returns the specular exponent, i.e. the color's intensity 
	 * of reflected lights on the object's surface.
	 */
	public double getSpecularExponent() {
		Object value = this.getProperty( SPECULAR_EXPONENT_PROPERTY );
		if ( value == null ) {
			value = new Double( JR3DShaderRenderingHints.SPECULAR_EXPONENT_DEFAULT );
		}
		return (( Double ) value).doubleValue();
	}

	/**
	 * Sets the specular exponent and returns the previous value (may be the default value).
	 * @see #getSpecularExponent()
	 */
	public double setSpecularExponent( double e ) {
		Object value = this.setProperty( SPECULAR_EXPONENT_PROPERTY, new Double( e ) );
		if ( value == null ) {
			value = new Double( JR3DShaderRenderingHints.SPECULAR_EXPONENT_DEFAULT );
		}
		return (( Double ) value).doubleValue();
	}

//	/**
//	 * Returns the transparency coefficient.
//	 */
//	public double getTransparency() {
//		Object value = this.getProperty( TRANSPARENCY_PROPERTY );
//		if ( value == null ) {
//			value = new Double( JR3DShaderRenderingHints.TRANSPARENCY_DEFAULT );
//		}
//		return (( Double ) value).doubleValue();
//	}
//
//	/**
//	 * Sets the transparency coefficient and returns the previous value (may be the default value).
//	 * @see #getSpecularExponent()
//	 */
//	public void setTransparency( double t ) {
//		setProperty( TRANSPARENCY_PROPERTY, new Double( t ) );
//	}

//	/**
//	 * Returns the border color, i.e. the color of the edges.
//	 * @see #getDefaultBorderColor()
//	 */
//	public Color getBorderColor() {
//		Object value = this.getProperty( BORDER_COLOR_PROPERTY );
//		if ( value == null ) {
//			value = getDefaultBorderColor();
//		}
//		return ( Color ) value;
//	}

//	/**
//	 * Returns the default border color.
//	 */
//	public Color getDefaultBorderColor() {
//		return BORDER_COLOR_DEFAULT;
//	}
//	
//	/**
//	 * Sets the border color.
//	 * @see #getBorderColor()
//	 */
//	public void setBorderColor( Color c ) {
//		setProperty( BORDER_COLOR_PROPERTY, c );
//	}

//	/**
//	 * Returns the border width.
//	 */
//	public int getBorderWidth() {
//		Object value = this.getProperty( BORDER_WIDTH_PROPERTY );
//		if ( value == null ) {
//			value = new Double( JR3DShaderRenderingHints.BORDER_WIDTH_DEFAULT );
//		}
//		return (int) (( Double ) value).doubleValue();
//	}
//
//	/**
//	 * Sets the border width and returns the previous value (may be the default value).
//	 */
//	public double setBorderWidth( double w ) {
//		Object value = this.setProperty( BORDER_WIDTH_PROPERTY, new Double( w ) );
//		if ( value == null ) {
//			value = new Double( JR3DShaderRenderingHints.BORDER_WIDTH_DEFAULT );
//		}
//		return (( Double ) value).doubleValue();
//	}

//	/**
//	 * Returns the selection color.
//	 */
//	public Color getSelectionColor() {
//		Object value = this.getProperty( SELECTION_COLOR_PROPERTY );
//		if ( value == null ) {
//			value = JR3DShaderRenderingHints.SELECTION_COLOR_DEFAULT;
//		}
//		return ( Color ) value;
//	}
//
//	/**
//	 * Sets the selection color and returns the previous value (may be the default value).
//	 */
//	public Color setSelectionColor( Color c ) {
//		Object value = this.setProperty( SELECTION_COLOR_PROPERTY, c );
//		if ( value == null ) {
//			value = JR3DShaderRenderingHints.SELECTION_COLOR_DEFAULT;
//		}
//		return ( Color ) value;
//	}

//	/**
//	 * Returns the dash pattern.
//	 */
//	public float[] getDashPattern() {
//		Object value = this.getProperty( DASH_PATTERN_PROPERTY );
//		if ( value == null ) {
//			value = JR3DShaderRenderingHints.NO_DASH_DEFAULT;
//		}
//		return ( float[] ) value;
//	}
//
//	/**
//	 * Sets the dash pattern and returns the previous value (may be the default value).
//	 */
//	public void setDashPattern( float[] p ) {
//		setProperty( DASH_PATTERN_PROPERTY, p );
//	}

	/**
	 * Returns the path of the texture file which contains texture properties for polygons.
	 */
	public String getTextureFilePath() {
		Object value = this.getProperty( TEXTURE_FILE_PROPERTY );
		if ( value != null ) {
			return ( String ) value;
		}
		return null;
	}

	/**
	 * Sets the path of the texture file and returns the previous value (may be the default value).
	 * @see #getTextureFilePath()
	 */
	public String setTextureFilePath( String imageFilePath ) {
		Object value = this.setProperty( TEXTURE_FILE_PROPERTY, imageFilePath );
		if ( value != null ) {
			return ( String ) value;
		}
		return null;
	}

//	/**
//	 * Returns whether the border is be drawn.
//	 */
//	public boolean getDrawBorder() {
//		return isEdgeVisible();
//	}
//
//	/**
//	 * Sets if the border should be drawn.
//	 */
//	public void setDrawBorder( boolean drawBorder ) {
//		setEdgeVisible( drawBorder );
//	}

	public void initialize() {
//		m_props.setDefaultProperty(DisplayProperties.SELECTION_COLOR_PROPERTY, SELECTION_COLOR_DEFAULT);
	}

	public Object clone() {
		JR3DShaderRenderingHints cloned = new JR3DShaderRenderingHints(new DefaultPropertyMap(getMasterMap()));
		cloned.copyPropertiesFrom(this);
		return cloned;
	}
}

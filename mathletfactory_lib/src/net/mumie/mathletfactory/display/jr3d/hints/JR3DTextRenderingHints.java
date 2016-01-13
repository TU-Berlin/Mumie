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
import java.awt.Font;

import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.ScreenTypeRenderingHints;
import net.mumie.mathletfactory.util.property.PropertyMapIF;
import de.jreality.shader.CommonAttributes;


/**
 * This class contains text rendering hints for JReality drawables.
 * 
 * @author weber, gronau
 * @mm.docstatus finished
 */
public class JR3DTextRenderingHints extends ScreenTypeRenderingHints {

	/** Base path for all {@link JR3DTextRenderingHints} properties. */
	public final static String DP_BASE_PATH = "display.jr3d.text.";
	
	//
	// property names
	//

	/** 
	 * Name of the <i>alignment</i> property.
	 * @see #getAlignment()
	 */
	public final static String ALIGNMENT_PROPERTY = DP_BASE_PATH + CommonAttributes.ALIGNMENT;
	
	/** 
	 * Name of the <i>font</i> property.
	 * @see #getFont()
	 */
	public final static String FONT_PROPERTY = DP_BASE_PATH + CommonAttributes.FONT;
	
	/** 
	 * Name of the <i>diffuse color</i> property.
	 * @see #getDiffuseColor()
	 */
	public final static String DIFFUSE_COLOR_PROPERTY = DP_BASE_PATH + CommonAttributes.DIFFUSE_COLOR;
	
	/** 
	 * Name of the <i>scale</i> property.
	 * @see #getScale()
	 */
	public final static String SCALE_PROPERTY = DP_BASE_PATH + CommonAttributes.SCALE;

	/** 
	 * Name of the <i>show labels</i> property.
	 * @see #isLabelVisible()
	 */
	public final static String SHOW_LABELS_PROPERTY = DP_BASE_PATH + "showLabels"; // Standardmaessig an

	/** 
	 * Name of the <i>font rendered with object color</i> property.
	 * @see #renderedWithObjectColor()
	 */
	public final static String FONT_RENDERED_WITH_OBJECT_COLOR_PROPERTY = DP_BASE_PATH + "fontRenderedWithObjectColor"; // Standardmaessig an

	//
	// default values
	//
	
	/** Default value for the <i>alignment</i> property. */
	public final static int ALIGNMENT_DEFAULT = 1;
	
	/** Default value for the <i>font</i> property. */
	public final static Font FONT_DEFAULT = MumieTheme.DEFAULT_THEME.getControlTextFont();
	
	/** Default value for the <i>diffuse color</i> property. */
	public final static Color DIFFUSE_COLOR_DEFAULT = Color.BLACK;
	
	/** Default value for the <i>scale</i> property. */
	public final static double SCALE_DEFAULT = 0.013;

	public JR3DTextRenderingHints(PropertyMapIF properties) {
		super(properties);
	}

	/**
	 * Returns the text alignment.
	 */
	public int getAlignment() {
		Object value = this.getProperty( ALIGNMENT_PROPERTY );
		if ( value == null ) {
			value = new Integer( JR3DTextRenderingHints.ALIGNMENT_DEFAULT );
		}
		return ((Integer) value).intValue();
	}

	/**
	 * Sets the text alignment and returns the previous value (may be the default value).
	 * @see #getAlignment()
	 */
	public int setAlignment( int a ) {
		Object value = this.setProperty( ALIGNMENT_PROPERTY, new Integer( a ) );
		if ( value == null ) {
			value = new Integer( JR3DTextRenderingHints.ALIGNMENT_DEFAULT );
		}
		return ((Integer) value).intValue();
	}

	/**
	 * Returns the text font.
	 */
	public Font getFont() {
		Object value = this.getProperty( FONT_PROPERTY );
		if ( value == null )
			value = getProperty(DisplayProperties.FONT_PROPERTY);
		if ( value == null )
			value = DisplayProperties.DEFAULT.getProperty(DisplayProperties.FONT_PROPERTY);
		return ( Font ) value;
	}

	/**
	 * Sets the text font and returns the previous value (may be the default value).
	 * @see #getFont()
	 */
	public void setFont( Font v ) {
		setProperty( FONT_PROPERTY, v );
	}

	/**
	 * Returns the text's diffuse color.
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
	 * Sets the text's diffuse color and returns the previous value (may be the default value).
	 * @see #getDiffuseColor()
	 */
	public Color setDiffuseColor( Color v ) {
		Object value = this.setProperty( DIFFUSE_COLOR_PROPERTY, v );
		if ( value == null ) {
			value = JR3DTextRenderingHints.DIFFUSE_COLOR_DEFAULT;
		}
		return ( Color ) value;
	}

	/**
	 * Returns the text's scale.
	 */
	public double getScale() {
		Object value = this.getProperty( SCALE_PROPERTY );
		if ( value == null ) {
			value = new Double( JR3DTextRenderingHints.SCALE_DEFAULT );
		}
		return ((Double) value).doubleValue();
	}

	/**
	 * Sets the text's scale and returns the previous value (may be the default value).
	 * @see #getScale()
	 */
	public double setScale( double s ) {
		Object value = this.setProperty( SCALE_PROPERTY, new Double( s ) );
		if ( value == null ) {
			value = new Double( JR3DTextRenderingHints.SCALE_DEFAULT );
		}
		return ((Double) value).doubleValue();
	}

	/**
	 * Returns if the text label is visible.
	 */
	public boolean isLabelVisible() {
		Object value = this.getProperty( SHOW_LABELS_PROPERTY );
		if ( value == null )
			value = getProperty(DisplayProperties.LABEL_DISPLAYED_PROPERTY);
		if ( value == null )
			value = DisplayProperties.DEFAULT.getProperty(DisplayProperties.LABEL_DISPLAYED_PROPERTY);
		return ((Boolean) value).booleanValue();
	}

	/**
	 * Sets whether the text label should be visible 
	 * and returns the previous value (may be the default value).
	 * @see #getScale()
	 */
	public boolean setLabelVisible( boolean v ) {
		Object value = this.setProperty( SHOW_LABELS_PROPERTY, new Boolean( v ) );
		if ( value == null ) {
			value = new Boolean( true );
		}
		return ((Boolean) value).booleanValue();
	}

	/**
	 * Returns if the text is rendered with the object color.
	 */
	public boolean renderedWithObjectColor() {
		Object value = this.getProperty( FONT_RENDERED_WITH_OBJECT_COLOR_PROPERTY );
		if ( value == null )
			value = getProperty(DisplayProperties.FONT_RENDERED_WITH_OBJECT_COLOR_PROPERTY);
		if ( value == null )
			value = DisplayProperties.DEFAULT.getProperty(DisplayProperties.FONT_RENDERED_WITH_OBJECT_COLOR_PROPERTY);
		return ((Boolean) value).booleanValue();
	}

	/**
	 * Sets whether the text should be rendered with the object color 
	 * and returns the previous value (may be the default value).
	 * @see #renderedWithObjectColor()
	 */
	public boolean setRenderedWithObjectColor( boolean v ) {
		Object value = this.setProperty( FONT_RENDERED_WITH_OBJECT_COLOR_PROPERTY, new Boolean( v ) );
		if ( value == null ) {
			value = new Boolean( true );
		}
		return ((Boolean) value).booleanValue();
	}
	
	/**
	 * Empty implementation.
	 */
	public void initialize() {}

	public Object clone() {
		JR3DTextRenderingHints cloned = new JR3DTextRenderingHints(new DisplayProperties());
		cloned.copyPropertiesFrom(this);
    return cloned;
	}
}

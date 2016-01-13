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

import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.ScreenTypeRenderingHints;
import net.mumie.mathletfactory.util.property.PropertyMapIF;
import de.jreality.shader.CommonAttributes;

/**
 * This class contains rendering hints for JReality drawables using {@link LineDisplayProperties}.
 * A line can either be rendered as an object consisting of multiple polygons (if the <i>tubes draw</i> 
 * property is <code>true</code>) or as a pixel line (if the <i>tubes draw</i> property is <code>false</code>).
 * 
 * @see #getTubesDraw()
 * 
 * @author weber, gronau
 * @mm.docstatus finished
 */
public class JR3DLineRenderingHints extends ScreenTypeRenderingHints {

	/** Base path for all {@link JR3DLineRenderingHints} properties. */
	public final static String DP_BASE_PATH = "display.jr3d.line.";
	
	//
	// property names
	//

	/** 
	 * Name of the <i>tubes draw</i> property.
	 * @see #getTubesDraw()
	 */
	public final static String TUBES_DRAW_PROPERTY = DP_BASE_PATH + CommonAttributes.TUBES_DRAW;

	/** 
	 * Name of the <i>tube radius</i> property.
	 * @see #getTubeRadius()
	 */
	public final static String TUBE_RADIUS_PROPERTY = DP_BASE_PATH + CommonAttributes.TUBE_RADIUS;

	/** 
	 * Name of the <i>line width</i> property.
	 * @see #getLineWidth()
	 */
	public final static String LINE_WIDTH = DP_BASE_PATH + CommonAttributes.LINE_WIDTH;
	
	//
	// default values
	//
	
	/** Default value for the <i>tube radius</i> property. */
	public final static double TUBE_RADIUS_DEFAULT = 0.01;

//	/** Field holding a reference to the display properties instance. */
//	private final LineDisplayProperties m_props;

	public JR3DLineRenderingHints(PropertyMapIF properties) {
		super(properties);
//		m_props = displayProps;
	}
	
	public void initialize() {
//		m_props.setDefaultProperty(JR3DShaderRenderingHints.EDGE_DRAW_PROPERTY, true);
//		m_props.setDefaultProperty(JR3DShaderRenderingHints.VERTEX_DRAW_PROPERTY, false);
//		m_props.setDefaultProperty(JR3DShaderRenderingHints.FACE_DRAW_PROPERTY, false);
	}
	
	/**
	 * Sets the line width in pixels (only applies if the <i>tubes draw</i> property 
	 * is set to <code>false</code>).
	 */
	public void setLineWidth( double width ) {
		this.setProperty( LINE_WIDTH, new Double( width ) );
	}

	/**
	 * Returns the line width in pixels (only applies if the <i>tubes draw</i> property 
	 * is set to <code>false</code>).
	 */
	public double getLineWidth() {
		Object value = this.getProperty( LINE_WIDTH );
		if ( value == null ) // read commom property instead
			value = this.getProperty(LineDisplayProperties.LINE_WIDTH_PROPERTY);
		if ( value == null ) // read default property instead
			value = LineDisplayProperties.DEFAULT.getProperty(LineDisplayProperties.LINE_WIDTH_PROPERTY);
		return (( Double ) value).doubleValue();
	}

	/**
	 * Returns the line width in space units (only applies if the <i>tubes draw</i> property 
	 * is set to <code>true</code>).
	 */
	public double getTubeRadius() {
		Object value = this.getProperty( TUBE_RADIUS_PROPERTY );
		if ( value == null ) {
			value = new Double( TUBE_RADIUS_DEFAULT );
		}
		return (( Double ) value).doubleValue();
	}

	/**
	 * Sets the line width in space units (only applies if the <i>tubes draw</i> property 
	 * is set to <code>true</code>).
	 */
	public void setTubeRadius( double radius ) {
		this.setProperty( TUBE_RADIUS_PROPERTY, new Double( radius ) );
	}

	/**
	 * Returns if the line is rendered as an object consisting of multiple polygons (if the <i>tubes draw</i> 
	 * property is <code>true</code>) or as a pixel line (if the <i>tubes draw</i> property is <code>false</code>).
	 */
	public boolean getTubesDraw() {
		Object value = this.getProperty( TUBES_DRAW_PROPERTY );
		if ( value == null ) {
			value = new Boolean( false );
		}
		return (( Boolean ) value).booleanValue();
	}

	/**
	 * Sets whether the line should be rendered as an object consisting of multiple polygons.
	 * 
	 * @see #getTubesDraw()
	 */
	public void setTubesDraw( boolean v ) {
		this.setProperty( TUBES_DRAW_PROPERTY, new Boolean( v ) );
	}
}

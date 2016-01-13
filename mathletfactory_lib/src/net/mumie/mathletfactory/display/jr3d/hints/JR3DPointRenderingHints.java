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

import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.display.ScreenTypeRenderingHints;
import net.mumie.mathletfactory.util.property.PropertyMapIF;
import de.jreality.shader.CommonAttributes;

/**
 * This class contains rendering hints for JReality drawables using {@link PointDisplayProperties}.
 * A point can either be rendered as a sphere (if the <i>spheres draw</i> property is <code>true</code>) 
 * or as a pixel circle (if the <i>spheres draw</i> property is <code>false</code>).
 * 
 * @see #getSpheresDraw()
 * 
 * @author weber, gronau
 * @mm.docstatus finished
 */
public class JR3DPointRenderingHints extends ScreenTypeRenderingHints {

	/** Base path for all {@link JR3DPointRenderingHints} properties. */
	public final static String DP_BASE_PATH = "display.jr3d.point.";
	
	//
	// property names
	//

	/** 
	 * Name of the <i>spheres draw</i> property.
	 * @see #getSpheresDraw()
	 */
	public final static String SPHERES_DRAW_PROPERTY = DP_BASE_PATH + CommonAttributes.SPHERES_DRAW; // Standardwert: false

	/** 
	 * Name of the <i>sphere radius</i> property.
	 * @see #getSphereRadius()
	 */
	public final static String SPHERE_RADIUS_PROPERTY = DP_BASE_PATH + CommonAttributes.POINT_RADIUS;

	/** 
	 * Name of the <i>point radius</i> property.
	 * @see #getPointRadius()
	 */
	public final static String POINT_RADIUS_PROPERTY = DP_BASE_PATH + CommonAttributes.POINT_SIZE;
	
	/** 
	 * Name of the <i>attenuate point size</i> property.
	 * @see #getAttenuatePointSize()
	 */
	public final static String ATTENUATE_POINT_SIZE_PROPERTY = DP_BASE_PATH + CommonAttributes.ATTENUATE_POINT_SIZE;// Standardwert: false

	//
	// default values
	//
	
	/** Default value for the <i>sphere radius</i> property. */
	public final static double SPHERE_RADIUS_DEFAULT = 0.05;
	
//	/** Field holding a reference to the display properties instance. */
//	private final PointDisplayProperties m_props;

	public JR3DPointRenderingHints(PropertyMapIF properties) {
		super(properties);
//		m_props = displayProps;
	}
	
	public void initialize() {
//		m_props.setDefaultProperty(JR3DShaderRenderingHints.VERTEX_DRAW_PROnew JR3DTextRenderingHints(p)PERTY, true);
//		m_props.setDefaultProperty(JR3DShaderRenderingHints.EDGE_DRAW_PROPERTY, false);
//		m_props.setDefaultProperty(JR3DShaderRenderingHints.FACE_DRAW_PROPERTY, false);
	}
	
	/**
	 * Sets the point radius in pixels (only applies if the <i>spheres draw</i> property 
	 * is set to <code>false</code>).
	 */
	public void setPointRadius( int radius ) {
		this.setProperty( POINT_RADIUS_PROPERTY, new Double( Math.abs( radius ) ) );
	}
	
	/**
	 * Returns the point radius in pixels (only applies if the <i>spheres draw</i> property 
	 * is set to <code>false</code>).
	 */
	public int getPointRadius() {
		Object value = this.getProperty(POINT_RADIUS_PROPERTY);
		if ( value == null ) // read commom property instead
			value = getProperty(PointDisplayProperties.POINT_RADIUS_PROPERTY);
		if ( value == null ) // read default property instead
			value = PointDisplayProperties.DEFAULT.getProperty(PointDisplayProperties.POINT_RADIUS_PROPERTY);
		return (( Double ) value).intValue();
	}

	/**
	 * Returns if the point is rendered as a sphere (if the <i>spheres draw</i> property is <code>true</code>) 
	 * or as a pixel circle (if the <i>spheres draw</i> property is <code>false</code>).
	 */
	public boolean getSpheresDraw() {
		Object value = this.getProperty( SPHERES_DRAW_PROPERTY );
		if ( value == null ) {
			value = new Boolean( false );
		}
		return (( Boolean ) value).booleanValue();
	}

	/**
	 * Sets whether the point should be rendered as a sphere.
	 * @see #getSpheresDraw()
	 */
	public void setSpheresDraw( boolean v ) {
		this.setProperty( SPHERES_DRAW_PROPERTY, new Boolean( v ) );
	}

	/**
	 * Returns if the point size is attenuated.
	 */
	public boolean getAttenuatePointSize() {
		Object value = this.getProperty( ATTENUATE_POINT_SIZE_PROPERTY );
		if ( value == null ) {
			value = new Boolean( false );
		}
		return (( Boolean ) value).booleanValue();
	}

	/**
	 * Returns whether the point size should be attenuated.
	 */
	public void setAttenuatePointSize( boolean v ) {
		this.setProperty( ATTENUATE_POINT_SIZE_PROPERTY, new Boolean( v ) );
	}

	/**
	 * Returns the point radius in space units (only applies if the <i>spheres draw</i> property 
	 * is set to <code>true</code>).
	 */
	public double getSphereRadius() {
		Object value = this.getProperty( SPHERE_RADIUS_PROPERTY );
		if ( value == null ) {
			value = new Double( SPHERE_RADIUS_DEFAULT );
		}
		return (( Double ) value).doubleValue();
	}

	/**
	 * Sets the point radius in space units (only applies if the <i>spheres draw</i> property 
	 * is set to <code>true</code>).
	 */
	public void setSphereRadius( double r ) {
		this.setProperty( SPHERE_RADIUS_PROPERTY, new Double( Math.abs( r ) ) );
	}
}

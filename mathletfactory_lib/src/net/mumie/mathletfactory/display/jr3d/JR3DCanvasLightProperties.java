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
import java.awt.geom.Point2D;

import net.mumie.mathletfactory.util.property.DefaultPropertyMap;


/**
 * @author JWebR
 * 
 */
public class JR3DCanvasLightProperties extends DefaultPropertyMap {

	public final static String DP_BASE_PATH = "display.jr3d.canvas.light.";
	
	// Lichtarten
	public final static String AMBIENT_LIGHT = "ambientLight";
	public final static String DIRECTIONAL_LIGHT = "directionalLight";
	public final static String POINT_LIGHT = "pointLight";
	public final static String SPOT_LIGHT = "spotLight";

	// Die moeglichen Einstellungen fuer die jeweilige Lichtart
	public final static String LIGHT_TYPE = "lightType";
	public final static String LIGHT_INTENSITY = "lightIntensity";
	public final static String LIGHT_COLOR = "lightColor";

	// Die Schatteneinstellungen haengen mit Licht zusammen, deswegen hier zu finden
	// Diese Einstellungen sind jeher fuer 2D - Canvas gedacht
	public final static String SHADOW_COLOR = "shadowColor";
	public final static String SHADOW_OFFSET = "shadowOffset";

	// Die Standard-Werte fuer Lichfarbe und Intensitaet
	public final static String LIGHT_TYPE_DEFAULT = JR3DCanvasLightProperties.DIRECTIONAL_LIGHT;
	public final static Color LIGHT_COLOR_DEFAULT = Color.WHITE;
	public final static double LIGHT_INTENSITY_DEFAULT = 3.0;

	// Die Schatten-Standardwerte
	public final static Color SHADOW_COLOR_DEFAULT = Color.GRAY;
	public final static Point2D SHADOW_OFFSET_DEFAULT = new Point2D.Double();

	public JR3DCanvasLightProperties() {
		super();
	}

	private JR3DCanvasLightProperties(DefaultPropertyMap map) {
		super(map);
	}

	public String getLightType() {
		Object value = this.getProperty( DP_BASE_PATH + LIGHT_TYPE );
		if ( value == null ) {
			value = LIGHT_TYPE_DEFAULT;
		}
		return ( String ) value;
	}

	public String setLightType( String type ) {
		Object value = this.setProperty( DP_BASE_PATH + LIGHT_TYPE, type );
		if ( value == null ) {
			value = LIGHT_TYPE_DEFAULT;
		}
		return ( String ) value;
	}

	public double getIntensity() {
		Object value = this.getProperty( DP_BASE_PATH + LIGHT_INTENSITY );
		if ( value == null ) {
			value = new Double( LIGHT_INTENSITY_DEFAULT );
		}
//		return ( Double ) value;
		return ((Double) value).doubleValue();
	}

	public double setIntensity( double intensity ) {
		Object value = this.setProperty( DP_BASE_PATH + LIGHT_INTENSITY, new Double( intensity ) );
		if ( value == null ) {
			value = new Double( LIGHT_INTENSITY_DEFAULT );
		}
//		return ( Double ) value;
		return ((Double) value).doubleValue();
	}

	public Color getLightColor() {
		Object value = this.getProperty( DP_BASE_PATH + LIGHT_COLOR );
		if ( value == null ) {
			value = LIGHT_COLOR_DEFAULT;
		}
		return ( Color ) value;
	}

	public Color setLightColor( Color c ) {
		Object value = this.setProperty( DP_BASE_PATH + LIGHT_COLOR, c );
		if ( value == null ) {
			value = LIGHT_COLOR_DEFAULT;
		}
		return ( Color ) value;
	}

	public Color getShadowColor() {
		Object value = this.getProperty( DP_BASE_PATH + SHADOW_COLOR );
		if ( value == null ) {
			value = SHADOW_COLOR_DEFAULT;
		}
		return ( Color ) value;
	}

	public Color setShadowColor( Color c ) {
		Object value = this.setProperty( DP_BASE_PATH + SHADOW_COLOR, c );
		if ( value == null ) {
			value = SHADOW_COLOR_DEFAULT;
		}
		return ( Color ) value;
	}

	public Point2D getShadowOffset() {
		Object value = this.getProperty( DP_BASE_PATH + SHADOW_OFFSET );
		if ( value == null ) {
			value = SHADOW_OFFSET_DEFAULT;
		}
		return ( Point2D ) value;
	}

	public Point2D setShadowOffset( Point2D o ) {
		Object value = this.setProperty( DP_BASE_PATH + SHADOW_OFFSET, o );
		if ( value == null ) {
			value = SHADOW_OFFSET_DEFAULT;
		}
		return ( Point2D ) value;
	}

	public Object clone() {
  	DefaultPropertyMap map = (DefaultPropertyMap) super.clone();
  	JR3DCanvasLightProperties result = new JR3DCanvasLightProperties(map);
    return result;
	}
}

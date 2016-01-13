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

import net.mumie.mathletfactory.util.property.DefaultPropertyMap;
import de.jreality.shader.CommonAttributes;


/**
 * Allgemeine Einstellungen fuer ein Canvas
 * 
 * @author JWebR
 * 
 */
public class JR3DCanvasViewerProperties extends DefaultPropertyMap {

	public final static String DP_BASE_PATH = "display.jr3d.canvas.viewer.";
	
	// Die moeglichen Einstellungen fuer ein Canvas (An, Aus)
	public final static String LIGHTING_ENABLED = CommonAttributes.LIGHTING_ENABLED; // Standardmaessig an
	public final static String ANTIALIASING_ENABLED = CommonAttributes.ANTIALIASING_ENABLED; // Standardmaessig an
	public final static String FOG_ENABLED = CommonAttributes.FOG_ENABLED; // Standardmaessig aus

	// Hintegrundfarbe und Nebelfarbe (nur wenn angeschaltet)
	public final static String BACKGROUND_COLOR = CommonAttributes.BACKGROUND_COLOR;
	public final static String FOG_COLOR = CommonAttributes.FOG_COLOR;

	// Die Standard Farben fuer Hintegrund und Nebel
	public final static Color BG_COLOR_DEFAULT = Color.WHITE;
	public final static Color FG_COLOR_DEFAULT = Color.WHITE;

	public JR3DCanvasViewerProperties() {
		super();
	}

	private JR3DCanvasViewerProperties(DefaultPropertyMap map) {
		super(map);
	}

	public boolean isLightingEnabled() {
		Object value = this.getProperty( DP_BASE_PATH + LIGHTING_ENABLED );
		if ( value == null ) {
			value = new Boolean( true );
		}
//		return ( Boolean ) value;
		return ((Boolean) value).booleanValue();
	}

	public boolean setLightingEnabled( boolean v ) {
		Object value = this.setProperty( DP_BASE_PATH + LIGHTING_ENABLED, new Boolean( v ) );
		if ( value == null ) {
			value = new Boolean( true );
		}
//		return ( Boolean ) value;
		return ((Boolean) value).booleanValue();
	}

	public boolean isAntialiasingEnabled() {
		Object value = this.getProperty( DP_BASE_PATH + ANTIALIASING_ENABLED );
		if ( value == null ) {
			value = new Boolean( true );
		}
//		return ( Boolean ) value;
		return ((Boolean) value).booleanValue();
	}

	public boolean setAntialiasingEnabled( boolean v ) {
		Object value = this.setProperty( DP_BASE_PATH + ANTIALIASING_ENABLED, new Boolean( v ) );
		if ( value == null ) {
			value = new Boolean( true );
		}
//		return ( Boolean ) value;
		return ((Boolean) value).booleanValue();
	}

	public boolean isFogEnabled() {
		Object value = this.getProperty( DP_BASE_PATH + FOG_ENABLED );
		if ( value == null ) {
			value = new Boolean( false );
		}
//		return ( Boolean ) value;
		return ((Boolean) value).booleanValue();
	}

	public boolean setFogEnabled( boolean v ) {
		Object value = this.setProperty( DP_BASE_PATH + FOG_ENABLED, new Boolean( v ) );
		if ( value == null ) {
			value = new Boolean( false );
		}
//		return ( Boolean ) value;
		return ((Boolean) value).booleanValue();
	}

	public Color getBgColor() {
		Object value = this.getProperty( DP_BASE_PATH + BACKGROUND_COLOR );
		if ( value == null ) {
			value = JR3DCanvasViewerProperties.BG_COLOR_DEFAULT;
		}
		return ( Color ) value;
	}

	public Color setBgColor( Color c ) {
		Object value = this.setProperty( DP_BASE_PATH + BACKGROUND_COLOR, c );
		if ( value == null ) {
			value = JR3DCanvasViewerProperties.BG_COLOR_DEFAULT;
		}
		return ( Color ) value;
	}

	public Color getFogColor() {
		Object value = this.getProperty( DP_BASE_PATH + FOG_COLOR );
		if ( value == null ) {
			value = JR3DCanvasViewerProperties.FG_COLOR_DEFAULT;
		}
		return ( Color ) value;
	}

	public Color setFogColor( Color c ) {
		Object value = this.setProperty( DP_BASE_PATH + FOG_COLOR, c );
		if ( value == null ) {
			value = JR3DCanvasViewerProperties.FG_COLOR_DEFAULT;
		}
		return ( Color ) value;
	}

	public Object clone() {
  	DefaultPropertyMap map = (DefaultPropertyMap) super.clone();
		JR3DCanvasViewerProperties result = new JR3DCanvasViewerProperties(map);
    return result;
	}
}

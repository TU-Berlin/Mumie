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

import net.mumie.mathletfactory.display.ScreenTypeRenderingHints;
import net.mumie.mathletfactory.display.SurfaceDisplayProperties;

/**
 * This class contains rendering hints for JReality drawables using {@link SurfaceDisplayProperties}.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class JR3DSurfaceRenderingHints extends ScreenTypeRenderingHints {

//	/** Field holding a reference to the display properties instance. */
//	private SurfaceDisplayProperties m_props;
	
	public JR3DSurfaceRenderingHints(SurfaceDisplayProperties displayProps) {
		super(displayProps);
//		m_props = displayProps;
	}
	
	public void initialize() {
//		m_props.setDefaultProperty(JR3DShaderRenderingHints.VERTEX_DRAW_PROPERTY, false);
//		m_props.setDefaultProperty(JR3DShaderRenderingHints.EDGE_DRAW_PROPERTY, m_props.isBorderDisplayed());
//		m_props.setDefaultProperty(SurfaceDisplayProperties.GRID_COLOR_PROPERTY, m_props.getBorderColor());
//		m_props.setDefaultProperty(SurfaceDisplayProperties.GRID_VISIBLE_PROPERTY, m_props.isBorderDisplayed());
//		m_props.setDefaultProperty(JR3DShaderRenderingHints.DIFFUSE_COLOR_PROPERTY, 
//				SurfaceDisplayProperties.SURFACE_COLOR_DEFAULT);
	}
}

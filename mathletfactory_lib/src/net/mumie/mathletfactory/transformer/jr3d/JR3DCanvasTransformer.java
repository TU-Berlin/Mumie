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

package net.mumie.mathletfactory.transformer.jr3d;


import java.util.Iterator;
import java.util.Map;

import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.display.jr3d.JR3DDrawable;
import net.mumie.mathletfactory.display.util.LabelSupport;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.transformer.CanvasObjectTransformer;
import de.jreality.scene.Geometry;
import de.jreality.scene.SceneGraphComponent;


/**
 * @author jweber
 */
public abstract class JR3DCanvasTransformer extends CanvasObjectTransformer {
	
	private LabelSupport m_labelSupport = new LabelSupport(LabelSupport.ST_3D_CANVAS);

	public void getMathObjectFromScreen( double[] javaScreenCoordinates, NumberTypeDependentIF mathObject ) {}

	public void getScreenPointFromMath( NumberTypeDependentIF entity, double[] javaScreenCoordinates ) {}

	public void synchronizeWorld2Screen() {}

	public double[] getWorldPickPointFromMaster() {
		return null;
	}

	public void initialize( MMObjectIF masterObject ) {
		super.initialize(masterObject);
	}
	
	public SceneGraphComponent getViewContent() {
		return ( ( JR3DDrawable ) this.getActiveDrawable() ).getViewContent();
	}

	public static boolean contains( SceneGraphComponent content, Geometry g ) {
		boolean contains = false;
		if ( content.getGeometry() == g ) {
			contains = true;
		} else {
			for ( Iterator it = content.getChildNodes().iterator(); !contains && it.hasNext(); ) {
				Object current = it.next();
				if ( current instanceof SceneGraphComponent ) {
					contains = JR3DCanvasTransformer.contains( ( SceneGraphComponent ) current, g );
				}
			}
		}
		return contains;
	}

	public MMCanvas getCanvas() {
		MMCanvas canvas = null;

//		Map<?, ?> transformerMap = this.getMaster().getDisplayTransformerMap();
//		for ( Iterator<?> it = transformerMap.keySet().iterator(); canvas == null && it.hasNext(); ) {
		Map transformerMap = this.getMaster().getDisplayTransformerMap();
		for ( Iterator it = transformerMap.keySet().iterator(); canvas == null && it.hasNext(); ) {
			Object key = it.next();
			if ( transformerMap.get( key ) == this ) {
				canvas = ( MMCanvas ) key;
			}
		}
		return canvas;
	}
	
	protected void renderLabel() {
		if(getMaster().getLabel() != null) {
			m_labelSupport.setLabel(getMaster().getLabel());
			((JR3DDrawable) getActiveDrawable()).setLabel(m_labelSupport.getLabel());
		}
	}
	
//	protected ScreenTypeRenderingHints[] getRenderingHints() {
//		if(m_renderingHints == null) {
//			DisplayProperties displayProperties = getMaster().getDisplayProperties();
//			JR3DShaderRenderingHints shaderHints = new JR3DShaderRenderingHints(getMaster().getDisplayProperties());
//			ScreenTypeRenderingHints hints = null;
//			
//			if(displayProperties instanceof PointDisplayProperties)
//				hints = new JR3DPointRenderingHints((PointDisplayProperties) displayProperties);
//			else if(displayProperties instanceof LineDisplayProperties)
//				hints = new JR3DLineRenderingHints((LineDisplayProperties) displayProperties);
//			else if(displayProperties instanceof PolygonDisplayProperties)
//				hints = new JR3DPolygonRenderingHints((PolygonDisplayProperties) displayProperties);
//			else if(displayProperties instanceof SurfaceDisplayProperties)
//				hints = new JR3DSurfaceRenderingHints((SurfaceDisplayProperties) displayProperties);
//			else if(displayProperties instanceof RNDisplayProperties)
//				hints = new JR3DDefaultRNRenderingHints((RNDisplayProperties) displayProperties);
//			m_renderingHints = new ScreenTypeRenderingHints[] { shaderHints, hints };
//		}
//		return m_renderingHints;
//	}
}

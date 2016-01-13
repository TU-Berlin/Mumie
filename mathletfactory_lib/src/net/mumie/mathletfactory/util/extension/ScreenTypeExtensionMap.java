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

package net.mumie.mathletfactory.util.extension;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.transformer.GeneralTransformer;
import net.mumie.mathletfactory.util.extension.ScreenTypeExtension.TransformType;

import org.w3c.dom.Node;

public class ScreenTypeExtensionMap {

	private Map m_screenTypes = new HashMap();
	
	public ScreenTypeExtensionMap() {
		
	}
	
	public ScreenTypeExtensionMap(Node[] xmlNodes) {
		addScreenTypeExtensions(xmlNodes);
	}
	
	public void addScreenTypeExtensions(Node[] xmlNodes) {
		for(int i = 0; i < xmlNodes.length; i++){
			addScreenTypeExtension(xmlNodes[i]);
		}
	}
	
	public void addScreenTypeExtension(Node xmlNode) {
		ScreenTypeExtension extension = new ScreenTypeExtension(xmlNode);
		addScreenTypeExtension(extension);
	}
	
	public void addScreenTypeExtension(ScreenTypeExtension extension) {
		ScreenTypeExtension saved = getScreenTypeExtension(extension.getName());
		if(saved != null)
			saved.addProperties(extension);
		else
			m_screenTypes.put(extension.getName(), extension);
	}
	
	public ScreenTypeExtension getScreenTypeExtension(String name) {
		ScreenTypeExtension extension = (ScreenTypeExtension) m_screenTypes.get(name);
		return extension;
	}
	
	public void copyFrom(ScreenTypeExtensionMap map) {
		Set keys = map.m_screenTypes.keySet();// keys are screen type names
		for(Iterator i = keys.iterator(); i.hasNext(); ) {
			String name = (String) i.next();
			this.addScreenTypeExtension(map.getScreenTypeExtension(name));
		}
	}
	
	public void clear() {
		m_screenTypes.clear();
	}
	
	public void propagateValues() {
		Set keys = this.m_screenTypes.keySet();// keys are screen type names
		for(Iterator i = keys.iterator(); i.hasNext(); ) {
			String name = (String) i.next();
			// register screen type
			int screenType = GeneralTransformer.addScreenType(name);
			ScreenTypeExtension e = getScreenTypeExtension(name);
			
			// register canvas class for non-noc-screen types
			if(e.getScreenDimension() != ScreenTypeExtension.SCREEN_DIMENSION_NOC)
				MMCanvas.registerCanvas(name, e.getScreenDimension(), e.getCanvasClass());
			
			// register transform types
			TransformType[] types = e.getTransformTypes();
			for(int j = 0; j < types.length; j++) {
				int transformType = GeneralTransformer.addTransformType(types[j].getName());
				GeneralTransformer.register(types[j].getTransformerClass(), transformType, screenType, types[j].getMasterClass());
			}
			
			// register rendering hints
			Set hintsKeys = e.getRenderingHints().keySet();
			for(Iterator k = hintsKeys.iterator(); k.hasNext(); ) {
				String transformer = (String) k.next();
				GeneralTransformer.addRenderingHints(transformer, screenType, e.getRenderingHints(transformer));				
			}
		}
	}
}

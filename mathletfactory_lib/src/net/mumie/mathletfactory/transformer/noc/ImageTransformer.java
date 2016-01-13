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

package net.mumie.mathletfactory.transformer.noc;

import net.mumie.mathletfactory.display.noc.MMImagePanel;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.util.MMImage;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;

/**
 * Transformer for
 * {@link net.mumie.mathletfactory.mmobject.util.MMImage}.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public class ImageTransformer extends ContainerObjectTransformer{

	public void initialize(MMObjectIF masterObject) {
		super.initialize(masterObject);
		
		m_mmPanel = new MMImagePanel(getMaster(), this);
		render();
	}
	
	public void render() {
		super.render();
//		if(!getRealMaster().isStatic())
//			throw new TodoException("Non static images cannot be displayed in container!");
		int width = getRealMaster().getWidth();
		int height = getRealMaster().getHeight();
		if(getRealMaster().getRealImage() != null) {
			if(width == -1)
				width = getRealMaster().getRealImage().getWidth();
			if(height == -1)
				height = getRealMaster().getRealImage().getHeight();
		} else {
			if(width == -1)
				width = 256;
			if(height == -1)
				height = 192;
		}
		getRealPanel().setWidth(width);
		getRealPanel().setHeight(height);
		getRealPanel().render();
	}
	
	public MMImage getRealMaster() {
		return (MMImage) getMaster();
	}
	
	public MMImagePanel getRealPanel() {
		return (MMImagePanel) getMMPanel();
	}
}

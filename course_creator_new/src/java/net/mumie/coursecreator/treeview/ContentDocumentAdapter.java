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

package net.mumie.coursecreator.treeview;

import javax.swing.JOptionPane;

import net.mumie.coursecreator.CCController;
import net.mumie.coursecreator.xml.XMLConstants;
import net.mumie.coursecreator.graph.cells.*;
import org.jaxen.JaxenException;
import org.w3c.dom.Element;


/**
 * 
 * @author <a href="mailto:binder@math.tu-berlin.de>Jens Binder</a>
 * @author <a href="mailto:vrichter@math.tu-berlin.de>Verena Richter</a>
 * @version $Id: ContentDocumentAdapter.java,v 1.9 2009/03/30 12:40:13 vrichter Exp $
 */
public class ContentDocumentAdapter extends AbstractDocumentAdapter implements DocumentAdapter{
	
	private CCController controller;
	public ContentDocumentAdapter(CCController c,Element rootElement) {
		super(c,rootElement);
		this.controller = c;
		Element cat = null;
		
		try{
			cat = (Element)this.getSingleNode(rootElement,XMLConstants.PREFIX_META+"category");
		}
		catch(JaxenException e){
			CCController.dialogErrorOccured(
					"ContentDocumentAdapter: JaxenException", 
					"ContentDocumentAdapter: JaxenException: " + e, 
					JOptionPane.ERROR_MESSAGE);
		}
		
		String name;
		if(cat!=null){
			name = cat.getAttribute("name");
		}else{
			name = rootElement.getLocalName();			
		}
		
		this.setCategoryName(name);
		int category=-1;
		if(isMainComponent(name)) category=MainComponentConstants.getCategoryAsInt(name);
		else if (isSubComponent(name)) category = SubComponentConstants.getCategoryAsInt(name);
		babble("found Element with cat "+category+" and categoryname "+name);
		this.setCategory(category);

		babble("created a new ContentDocAd:" 
				+ " name = " + this.getName() 
				+ " type = " + this.getDocumentType() 
				);
		
	}
	
	private boolean isMainComponent(String name)
	{
		int k = MainComponentConstants.getCategoryAsInt(name);
		if(k!=-1) return true;
		else return false;
	}
	
	private boolean isSubComponent(String name)
	{
		int k = SubComponentConstants.getCategoryAsInt(name);
		if(k!=-1) return true;
		else return false;
	}
	
	public DocumentAdapter[] getChildDocs(){
		return null;
	}
	
	public String getPath()
	{
		return this.path + ".meta.xml";
	}
	
	public void setPath(String p)
	{
		this.path = p + "/"+this.pureName;
	}

	public CCController getController() {
		return controller;
	}
	private void babble(String msg){
		if (this.controller.getConfig().getDEBUG() && this.controller.getConfig().getDEBUG_DOCUMENT_ADAPTER()) {
			System.out.println("ContentDocumentAdapter: "+msg);	    
			} // end of if ()
	}
}

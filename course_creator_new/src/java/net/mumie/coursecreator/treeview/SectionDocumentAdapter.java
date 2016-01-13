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

import java.util.LinkedList;

import javax.swing.JOptionPane;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.mumie.coursecreator.xml.XMLConstants;
import net.mumie.coursecreator.CCController;
/**
 * 
 * @author <a href="mailto:binder@math.tu-berlin.de">Jens Binder</a>
 * @version $Id: SectionDocumentAdapter.java,v 1.13 2009/03/18 19:17:03 vrichter Exp $
 */

public class SectionDocumentAdapter extends AbstractDocumentAdapter implements DocumentAdapter
{

	private CCController controller;
	public SectionDocumentAdapter(CCController c,Element rootElement) {
		super(c,rootElement);
		this.controller = c;
		Element contains_elem = null; 
		try{
			//babble(XMLConstants.PREFIX_META+"contains");
			contains_elem = (Element)this.getSingleNode(rootElement,XMLConstants.PREFIX_META+"contains");
		}
		catch(Exception e){
			CCController.dialogErrorOccured(
					"SectionDocumentAdapter: Exception", 
					"SectionDocumentAdapter: Exception: " + e, 
					JOptionPane.ERROR_MESSAGE);
		}
		
		if(contains_elem!=null)
		{
			babble("ZUSAMMENBAUEN!!!!!!!!!!");
			NodeList contained = contains_elem.getChildNodes();
			//babble("has " + contained.getLength() + " child elements");
			LinkedList<DocumentAdapter> list = new LinkedList<DocumentAdapter>();
			for (int j = 0; j < contained.getLength(); j++)
			{

				
				Element containedX = (Element)contained.item(j);
				
				DocumentAdapter XAsDA = null;
				if(containedX.getLocalName().equals("section")){
					XAsDA = new SectionDocumentAdapter(this.controller,(Element)contained.item(j));
					XAsDA.setPath(this.path);
					babble("addlast Section");
					list.addLast(XAsDA);
				}else {
					XAsDA = new ContentDocumentAdapter(this.controller,(Element)contained.item(j));
					XAsDA.setPath(this.path);
					
						if (this.isWithoutGeneric(XAsDA)||
								(this.isGeneric(XAsDA)&&this.controller.getConfig().getSHOW_GENERIC())||
							(!this.isGeneric(XAsDA)&&this.controller.getConfig().getSHOW_NON_GENERIC())){
						babble("addlast content");
							list.addLast(XAsDA);
						}
				}
								
			}	
			this.containedDocs = null;		
			this.containedDocs = new DocumentAdapter[list.size()];
			for (int i = 0; i< list.size();i++){containedDocs[i] = list.get(i);
			babble("LIST :"+ list.get(i));
			
			}
		}
		
		this.setCategory(AbstractDocumentAdapter.SECTION_CAT);
		this.setCategoryName(AbstractDocumentAdapter.ARROW_CMP);
//		babble("created a new SectionDocumentAdapter: path=" + this.getPath() + " name = " + this.getName() + " description = " + this.getDescription() + " type = " + this.getDocumentType() + " id = " + this.getID());
		babble("new SectionDocAd: name = " + this.getName() + " type = " + this.getDocumentType() );
	}
	
	private boolean isGeneric(DocumentAdapter da){
		boolean val = da.getDocumentType().startsWith("generic");
		babble (da.getDocumentType() +" is Generic "+ val);
		return val;
	}
	
	private boolean isWithoutGeneric(DocumentAdapter da){
		boolean val =(da.getDocumentType().endsWith("element")||
				da.getDocumentType().endsWith("summary")||
				da.getDocumentType().endsWith("problem"));
		return !val;
	}
	
	public DocumentAdapter[] getChildDocs()
	{
		return this.containedDocs;
	}

	public String getPath()
	{
		return this.path + "/";
	}
	
	public void setPath(String p)
	{
		this.path = p+"/"+this.pureName;
	}

	public CCController getController() {
		return controller;
	}
	private void babble(String msg){
		if (this.controller.getConfig().getDEBUG() && this.controller.getConfig().getDEBUG_DOCUMENT_ADAPTER()) {
			System.out.println("SectionDocumentAdapter: "+msg);	    
			} // end of if ()
	}
}

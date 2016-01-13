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

import java.net.MalformedURLException;
import java.net.URL;


import net.mumie.coursecreator.CCController;
import net.mumie.coursecreator.tools.StringCutter;
import net.mumie.coursecreator.xml.XMLConstants;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author <a href="mailto:binder@math.tu-berlin.de>Jens Binder</a>
 * @author <a href="mailto:lehmannf@math.tu-berlin.de>Fritz Lehmann-Grube</a>
 * @version $Id: AbstractDocumentAdapter.java,v 1.15 2009/03/18 19:17:10 vrichter Exp $
 */
public abstract class AbstractDocumentAdapter {

	public static final String UNDISPLAYABLE = "undefined_doctype";
	public static final int SECTION_CAT = -1;
	public static final String ARROW_EXP = "arrow_exp";
	public static final String ARROW_CMP = "arrow_cmp";

	/**
	 * This document's type as a Mumie "document_type".
	 */

	private String docType = null; 

	/**
	 * This document's category (Mumie).
	 */

	private int docCategory = -1;
	
	private String docCategoryName = "no_cat";
	
	/**
	 * The title of the document which is displayed when the node is
	 * selected.
	 */
	private String name = "";
	
	protected String path = XMLConstants.PATH_UNDEFINED;

	
	/**
	 * The description of the document which is displayed when the node is
	 * touched.
	 */
	private String description = "";

	/**
	 * The urlPrefix of the document to be displayed
	 */
	private String urlPrefix;

	/**
	 * The ID of the document to be displayed
	 */
	private int id;
	
	private int contained_in_id;

	/**
	 * The pure name of the document to be displayed
	 */
	protected String pureName = "";

	/**
	 * The time of creation of the document to be displayed
	 */
	public String created;

	/**
	 * The time of last modification of the document to be displayed
	 */
	public String lastModified;

	// all PATHs encountered in the XML document are considered
	// relative to docBase
	protected static URL docBase;

	// alternative method (which only works for a single instance) to
	// set a PATH which all other PATHs are considered relative to
	protected String pathPrefix =""; 
	

	protected CCController controller;
	/**
	 * The contained documents (when this is a section).
	 */
	public DocumentAdapter[] containedDocs;

	/**
	 * Creates a new <code>DocumentAdapter</code> instance.
	 *
	 * @param rootElement a dom Element value
	 */
	public AbstractDocumentAdapter (CCController c,Element rootElement) 
	{
		this.controller = c;

		this.docType = rootElement.getLocalName();
		this.id = (new Integer(rootElement.getAttribute("id"))).intValue();
		this.path = rootElement.getAttribute("path");
		//babble("found path="+this.path);
		NodeList childNodes = rootElement.getChildNodes();
		//babble("found " + childNodes.getLength() + "child elements");
		for (int i = 0; i < childNodes.getLength(); i++)
		{
			Node childNode = childNodes.item(i);
			if ( childNode.getNodeType() == Node.ELEMENT_NODE )
			{
				//childNode = (Element)childNode;
				String childLocalName = childNode.getLocalName();
				if( childLocalName.equals("name") )
				{
					//babble("found NAME child element");
					if(childNode.hasChildNodes())
						this.name = childNode.getFirstChild().getNodeValue();
				}
				else if( childNode.getLocalName().equals("description") )
				{
					//babble("found DESCRIPTION child element");
					if(childNode.hasChildNodes()){
						
						this.description = StringCutter.cutSpaces(childNode.getFirstChild().getNodeValue());
					}
				}
				else if( childNode.getLocalName().equals("pure_name") )
				{
					//babble("found PURE_NAME child element");
					if(childNode.hasChildNodes())
						this.pureName = childNode.getFirstChild().getNodeValue();
				}
				else if( childNode.getLocalName().equals("created") )
				{
					//babble("found CREATED child element");
					this.created = ((Element)childNode).getAttribute("value");
				}
				else if( childNode.getLocalName().equals("last_modified") )
				{
					//babble("found LAST_MODIFIED child element");
					this.lastModified = ((Element)childNode).getAttribute("value");
				}
				else if( childNode.getLocalName().equals("contained_in") )
				{
					//babble("found CONTAINED_IN child element");
					this.contained_in_id = Integer.parseInt(((Element)childNode).getAttribute("id"));
				}
			}
		}
	}

	
	/**
	 * returns the Node which is specified by the XPath-expression-argument "nodepath"
	 * within the DOM-document specified by the root - Element 
	 */
	protected Node getSingleNode(Object root, String nodepath) throws JaxenException{
		XPath path;		
		path = new DOMXPath(nodepath);
		path.addNamespace("mumie",XMLConstants.NS_META);		
		return (Node) path.selectSingleNode(root);		
	}
	
	abstract String getPath();
	
	/**
	 * The ID of the document in the database
	 *
	 * @return that <code>ID</code> 
	 */
	public int getID(){
		return id; 
	}

	/**
	 * Set the ID of the document in the database
	 *
	 * @param newID an <code>ID</code> value
	 */
//	public void setID(int newID){
//		id = newID; 
//	} 

	/**
	 * Set the prefix which is prepended to <b>this</b>
	 * <code>DocumentAdapter</code>'s URL. This is different from
	 * {@link #setDocumentBase(URL) setDocumentBase} which sets a URL
	 * to which the URLs of <b>all</b> <code>DocumentAdapter</code>s
	 * are relative.
	 *
	 * @param pfx a <code>String</code> to be prepended to the URL
	 * @see #setURL(URL)
	 * @see #getURL
	 */
	public void setURLPrefix (String pfx){
		urlPrefix=pfx; 
	}

	/**
	 * Construct the "real" URL from the (possibly relative) URL
	 * string that was given to the constructor
	 *
	 * @param localPart a <code>String</code> value
	 * @return an <code>URL</code> value
	 */
	public URL resolveURL (String localPart) throws MalformedURLException
	{

		if (localPart == null) {
			return null;
		} // end of if ()

		docBase = getDocumentBase();
		URL docURL = null;
		if (docBase == null) {
			babble ("URL is meant to be local (docBase not set)");	    
			docURL = new URL(urlPrefix + localPart);
		} // end of if ()
		else {
			docURL = new URL(docBase, urlPrefix + localPart);
//			babble("non-local URL: " + docURL.toString());	    
		} // end of if () else


		return docURL; 
	}

	/**
	 * Get the "document base". All URLs encountered in the XML
	 * document are considered relative to the document base. This is
	 * quite similar to the <code>BASE</code> element in HTML. 
	 * @return a <code>URL</code> representing the
	 * document base
	 * @see <a href="http://www.w3.org/TR/html4/struct/links.html#h-12.4" target="_new">The HTML 4.01 specification: the <code>BASE</code> element</a>
	 */
	public static URL getDocumentBase() {
		return docBase;
	}

	/**
	 * Set the "document base". All URLs encountered in the XML
	 * document are considered relative to the document base. This is
	 * quite similar to the <code>BASE</code> element in HTML. 
	 * 
	 * @param newDocumentBase a <code>URL</code> representing the
	 * document base
	 * @see <a href="http://www.w3.org/TR/html4/struct/links.html#h-12.4" target="_new">The HTML 4.01 specification: the <code>BASE</code> element</a>
	 */
	public static void setDocumentBase(URL newDocumentBase) {
		docBase = newDocumentBase;
	}


	/**
	 * The "name" string displayed in the navigation tree. 
	 * @return the name string.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the "name" string displayed in the navigation tree. 
	 * @param newName The new name string.
	 */
	public void setName(String newName) {
		this.name = newName;
	}

	/**
	 * The "description" string displayed in the navigation tree. 
	 * @return the description string.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the "description" string displayed in the navigation tree. 
	 * @param newDescription The new description string.
	 */
	public void setDescription(String newDescription) {
		this.description = newDescription;
	}

	/**
	 * The String representation of this object. It's eqiuvalent to
	 * the name. 
	 * @return the name.
	 * @see #getName()
	 */
	public String toString() {
		return name;
	}

	/**
	 * Get the document type. The corresponding (private) field controls 
	 * @return the type of this document, as a String.
	 */
	public String getDocumentType(){
		return this.docType; 
	}

	/**
	 * Set the document type. No plausibility checks are made (yet). If
	 * this is set to an unknown type, the {@link MumieTreeCellRenderer}
	 * will use the default Java icons. 
	 * @see #getDocumentType()
	 * @param newDocType The new document type, as a String.
	 */
	public void setDocumentType(String newDocType){

		if (newDocType == null) {
			this.docType = UNDISPLAYABLE; 
		} else {
			this.docType = newDocType; 
		} // end of if-else

	}

	/**
	 * Get the document category. The corresponding (private) field controls
	 * @return the category of this document, as a int.
	 */
	public int getCategory(){
		/*int cat=-1;
		if (this.docCategory==-1 && (
			this.getDocumentType().equals("course_section")||
			this.getDocumentType().equals("worksheet")))
			cat=9;
		else
			cat = this.docCategory;*/
		return this.docCategory;
	}

	/**
	 * Set the document category. No plausibility checks are made (yet). If
	 * this is set to an unknown category, the {@link MumieTreeCellRenderer}
	 * will use the default Java icons. 
	 * @see #getDocumentCategory()
	 * @param newDocCategory The new document category, as a int.
	 */
	public void setCategory(int newDocCategory){
		this.docCategory = newDocCategory; 
	} 


	public String getCategoryName(){
		return this.docCategoryName;
	}
	
	public void setCategoryName(String name){
		this.docCategoryName = name;
	}
	
	/**
	 * Get the contained documents 
	 */
	public abstract DocumentAdapter[] getChildDocs();
	
	
	public boolean hasChildren()
	{
		if(this.containedDocs==null)
			return false;
		else return true;
	}
	
	public int childrenCount()
	{
		if(hasChildren())
			return this.containedDocs.length	;
		else return 0;
	}
	

	/**
	 * Whether the document should be displayed in the <code>_top</code>
	 * frame.
	 * @return always <code>false</code>; if you need this capability in
	 * your TreeViewApplet instance, override this method. 
	 * @see WebPageAdapter#needsTopFrame()
	 */
	public boolean needsTopFrame() {
		return false; 
	}

	private void babble (String msg){
		if (this.controller.getConfig().getDEBUG() && this.controller.getConfig().getDEBUG_DOCUMENT_ADAPTER()) {
		System.out.println("AbstractDocumentAdapter: "+msg);	    
		} // end of if ()

	}

	public String getCreated() {return created;}
	public String getModified() {return lastModified;}
	public int getSection() {return contained_in_id;}
	public String getPureName() {return pureName;}

}// DocumentAdapter

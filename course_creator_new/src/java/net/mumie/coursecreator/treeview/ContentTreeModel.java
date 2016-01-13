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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

import net.mumie.japs.client.*;

import net.mumie.coursecreator.CCController;
/**
 * The ContentTreeModel inherits directly from the DefaultTreeModel and defines the actions when the Tree
 * is expanded or collapsed
 * @author <a href="mailto:binder@math.tu-berlin.de>Jens Binder</a>
 * @version $Id: ContentTreeModel.java,v 1.14 2009/03/19 15:56:59 vrichter Exp $
 */

public class ContentTreeModel extends DefaultTreeModel 
implements TreeWillExpandListener{
	static final long serialVersionUID=0;
	private CCController controller;
	private JapsClient jc;
	private String urlPrefix;
	
	static final String rootSectionId = "0";
	
	public ContentTreeModel(CCController c,JapsClient japsclient, String urlPrefix) {
		
		super(new DefaultMutableTreeNode());
		this.controller = c;
		SectionDocumentAdapter rootSec=null;
		try{
			rootSec = this.MakeTreeNode(japsclient, urlPrefix, rootSectionId);
		}catch(Exception e)
		{
			babble("unhandled Exception ... "+e.toString());
		}
		
		ContentTreeNode rootNode= new ContentTreeNode(rootSec);
		
		rootNode.attachChildren();
		
		this.setRoot(rootNode);	
		
		this.jc = japsclient;
		this.urlPrefix = urlPrefix;
		// TODO Auto-generated constructor stub
	}
	
	protected SectionDocumentAdapter MakeTreeNode(JapsClient japsClient, String urlPrefix, String id)
	throws IOException, NullPointerException, JapsClientException
	{
		// get the content
		HttpURLConnection conn = japsClient.get(TreeConstants.TREE_SECTION_VIEW_XML_URL + id);
		
		InputStream in = conn.getInputStream();  
		Document rootDocument=null;
		DocumentBuilder builder;
		
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();			
			factory.setIgnoringComments(true);
			factory.setNamespaceAware(true);			
			builder = factory.newDocumentBuilder();
			
			rootDocument = builder.parse(in);			
		}
		catch (IOException ioe){
			babble(" IOException while accessing DOMBuilder: " +ioe );
		}
		catch (Exception e) {
			babble("Exception while accessing DOMBuilder: " +e );
		}
		
		SectionDocumentAdapter section = new SectionDocumentAdapter(this.controller,rootDocument.getDocumentElement());
		return section;
	}
	
	public void treeWillExpand(TreeExpansionEvent e){
		babble("################ treeExpandedEvent ################");
		ContentTreeNode node = (ContentTreeNode) e.getPath().getLastPathComponent();
		DocumentAdapter oldDoc = (DocumentAdapter)node.getUserObject();
		SectionDocumentAdapter newDoc = null;
		try
		{
			newDoc = this.MakeTreeNode(jc, this.urlPrefix, String.valueOf(oldDoc.getID()));
			newDoc.setCategoryName(AbstractDocumentAdapter.ARROW_EXP);
		}
		catch(Exception exc)
		{
			babble("Exception while expanding the tree: "+exc.toString());
		}
		babble("node old "+ node.getContent().childrenCount());
		if(newDoc!=null) node.setContent(newDoc);
		babble("node new "+ node.getContent().childrenCount());
		
	}
	
	
	public void treeWillCollapse(TreeExpansionEvent e)	{
		ContentTreeNode node = (ContentTreeNode) e.getPath().getLastPathComponent();
		DocumentAdapter oldDoc = (DocumentAdapter)node.getUserObject();
		oldDoc.setCategoryName(AbstractDocumentAdapter.ARROW_CMP);
	}
	
	
	public int getChildCount(Object parent)
	{
		ContentTreeNode node = (ContentTreeNode)parent;
		DocumentAdapter doc = node.getContent();
		int count = doc.childrenCount();
		//babble("*** getChildcount: " + count);
		return count;
	}
	
	public Object getChild(Object parent, int index)
	{
		//babble("++++++++++++++++ calling getChild+++++++++");
		ContentTreeNode node = (ContentTreeNode) parent;
		DocumentAdapter doc = node.getContent();
		DocumentAdapter[] docChildren = doc.getChildDocs();
		ContentTreeNode newChild = new ContentTreeNode(docChildren[index]);
		//babble("+++++++ found TreeNode with " + newChild.getUserObject());
		
		return newChild;
	}
	
	void babble(String bab){
		if (this.controller.getConfig().getDEBUG()&&
				this.controller.getConfig().getDEBUG_CONTENT_TREE_MODEL()) 
			System.out.println("ContentTreeModel: "+bab);
	}
}

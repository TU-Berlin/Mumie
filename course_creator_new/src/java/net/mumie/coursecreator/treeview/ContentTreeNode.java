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

import javax.swing.tree.DefaultMutableTreeNode;
import net.mumie.coursecreator.CCController;
/**
 * Represents a node in the DirTree.
 */

public class ContentTreeNode extends DefaultMutableTreeNode{
	
	static final long serialVersionUID=0;
	
	protected boolean childrenChecked = false;
	private CCController controller;
	
	/**
	 * Creates a new node for the specified directory.
	 */
	public ContentTreeNode(DocumentAdapter doc)
	{
		super(doc);
		this.controller = doc.getController();
		if(!(doc instanceof SectionDocumentAdapter)){
			this.setAllowsChildren(false);
			babble("not Section Adapter / is leaf?: "+this.isLeaf()+" how many children?: "+this.getChildCount());			
		}else{
			this.setAllowsChildren(true);
			babble("created Section TreeNode");
		}
		babble("DocumentAdapter: " + doc);
	}

	/** isLeaf if userObject is not instanceof SectionDocumentAdapter */
	public boolean isLeaf (){
		DocumentAdapter doc = (DocumentAdapter) this.userObject;
		if (doc instanceof SectionDocumentAdapter)
			return false;
		else return true;
	}


	public void attachChildren ()
	{
		this.removeAllChildren();
		
		Object content = this.getContent();
		DocumentAdapter castedContent = (DocumentAdapter)content;
		babble("-> found content "+this.getUserObject() + " has children? "+castedContent.hasChildren());
		
		if(castedContent.hasChildren())
		{
			DocumentAdapter[] documents = castedContent.getChildDocs();
			babble("-> attaching the children to the node ");
			for (int i = 0; i < documents.length; i++)
			{
				this.add(new ContentTreeNode(documents[i]));
			}
		}

	}


	/**
	 * Returns the directory.
	 */
	public DocumentAdapter getContent (){
		return (DocumentAdapter)this.getUserObject();
	}

	/**
	 * sets the directory.
	 */
	public void setContent(DocumentAdapter d){
		this.userObject=d;
	}

	/**
	 * Returns the local name of the directory.
	 */
	public String toString ()	{
		return this.getContent().getName();
	}
	
	void babble(String bab){
		if (this.controller.getConfig().getDEBUG_CONTENT_TREE_NODE()&&
				this.controller.getConfig().getDEBUG())
			System.out.println("ContentTreeNode: "+bab);
	}
}

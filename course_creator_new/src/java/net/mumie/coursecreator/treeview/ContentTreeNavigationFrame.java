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

import net.mumie.coursecreator.graph.cells.MainComponentConstants;
import net.mumie.coursecreator.graph.cells.SubComponentConstants;
import net.mumie.japs.client.JapsClient;
import net.mumie.japs.client.JapsClientException;


/**
 *TODO not used
 * @author <a href="mailto:binder@math.tu-berlin.de">Jens Binder</a>
 * @author <a href="mailto:lehmannf@math.tu-berlin.de">Fritz Lehmann-Grube</a>
 * @version $$
 */

public class ContentTreeNavigationFrame extends TreeNavigationFrame {


	static final long serialVersionUID=0;
	static final String TREE_VIEW_XML_URL = "protected/view-raw/pseudo-document/type-name/section/id/";
	
	static final String rootSectionId = "0";

	public ContentTreeNavigationFrame(boolean ofl) {
		super(ofl);
		this.setTitle("ElementSelector");

		this.setPreview(true); 
	}
	
	/**
	 * this method builds the Model for the ContentTree. It invokes the constructor of the TreeModel  
	 * @param japsClient
	 * @param urlPrefix
	 * @return
	 * @throws IOException
	 * @throws NullPointerException
	 * @throws JapsClientException
	 */
	public ContentTreeModel MakeTreeTopModel(JapsClient japsClient, String urlPrefix)
	throws IOException, NullPointerException, JapsClientException
	{
		ContentTreeModel tree_model = new ContentTreeModel(this.controller,japsClient,urlPrefix);
		return tree_model;
	}
	
	
	/**
	 * Action on "set" button press - load course into CourseCreator
	 */	
	protected void setButtonAction() {
		if (this.controller != null) {
			if (this.selectedDocumentType.equals(MainComponentConstants.CAT_SECTION)){
				this.controller.setPath(selectedDocumentPath, selectedDocumentTitle, MainComponentConstants.getCategoryAsInt(MainComponentConstants.CAT_SECTION));
				babble("setButtonAction (SECTION) with Path="+selectedDocumentPath+" Title="+selectedDocumentTitle+" Cat="+MainComponentConstants.getCategoryAsInt(MainComponentConstants.CAT_SECTION));
			}
			else if (this.selectedDocumentType.equals(SubComponentConstants.CAT_WORKSHEET)){
				this.controller.setPath(selectedDocumentPath, selectedDocumentTitle, selectedDocumentCategory);
				babble("setButtonAction (WORKSHEET) with Path="+selectedDocumentPath+" Title="+selectedDocumentTitle+" Cat="+selectedDocumentCategory);
			}
			else if (this.selectedDocumentType.equals("generic_summary")){
				this.controller.setSummary(selectedDocumentPath);
				babble("setButtonAction (SUMMARY) with Path="+selectedDocumentPath+" Title="+selectedDocumentTitle+" Cat="+selectedDocumentCategory);
			}
			else{
				this.controller.setPath(selectedDocumentPath, selectedDocumentTitle, selectedDocumentCategory);
				babble("setButtonAction (OTHER) with Path="+selectedDocumentPath+" Title="+selectedDocumentTitle+" Cat="+selectedDocumentCategory);
			}			
		}
		this.controller.notifyModel();
	}
	
	void babble(String bab){
		if (this.controller.getConfig().getDEBUG()&& this.controller.getConfig().getDEBUG_CONTENT_TREE_NAVIGATION())
			System.out.println("ContentTreeNavigationFrame: "+bab);
	}
	
}

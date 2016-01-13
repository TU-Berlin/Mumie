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

import net.mumie.japs.client.JapsClient;
import net.mumie.japs.client.JapsClientException;

import org.xml.sax.SAXException;

/**
* <p>
* Extend the {@link TreeNavigationFrame} to be used as
* CourseLoader by the CourseCreator.TODO This feature is deactivated at the moment since
* we're loading courses only from the local filesystem.
* The option of loading courses directly into the database will be implemented in future releases
*
* @author <a href="mailto:binder@math.tu-berlin.de">Jens Binder</a>
* @author <a href="mailto:vrichter@math.tu-berlin.de">Verena Richter</a>
* @version $Id: CourseLoaderTreeNavigationFrame.java,v 1.19 2009/03/30 12:40:12 vrichter Exp $
*/

public class CourseLoaderTreeNavigationFrame extends TreeNavigationFrame{
	
	static final long serialVersionUID=0;
	
	String setButtonText = "Oeffnen";
	String cancelButtonText = "Cancel";
	static final String COURSE_TREE_XML_URL = "protected/index-raw/treeview/courses?mode=loader";
	
	public CourseLoaderTreeNavigationFrame(){
		super(true);
		this.setTitle("CourseLoader");
		// the rest is done in the init-method of the superclass
	}
	protected SectionDocumentAdapter MakeTreeTopNode(JapsClient japsClient, String urlPrefix)
	throws SAXException, IOException, NullPointerException, JapsClientException
	{
		
		//HttpURLConnection conn = japsClient.get(urlPrefix + COURSE_TREE_XML_URL);
//		babble("will try to login");
//		char[] pw = {'j','e','n','s'};
//		japsClient.login("jens", pw);
//		babble("try to do a get");
//		HttpURLConnection conn = japsClient.get(COURSE_TREE_XML_URL);
//		babble("HeaderField:"+conn.getHeaderField(JapsResponseHeader.LOGIN_REQUIRED));
    	
//		InputStream in = conn.getInputStream();  
    	 
//    	MyXMLHandler xmlHdl = new MyXMLHandler(in);
//    	xmlHdl.doTheBloodyParsing();    	
//    	DefaultMutableTreeNode root = xmlHdl.rootNode; 
//    	
//    	babble("Root node:\n    Title: '" 
//    			+ ((DocumentAdapter)root.getUserObject()).toString()
//    			+ "'\n    Children: " + root.getChildCount() 
//    			+ "\n    Depth: " + root.getDepth()); 	
    	
//    	return root;
	  return null;
	}
	
	protected ContentTreeModel MakeTreeTopModel(JapsClient japsClient, String urlPrefix)
	throws IOException, NullPointerException, JapsClientException
	{
		return null;
	}
	
	/**
	 * Action on "set" button press - load course into CourseCreator
	 */
	
	protected void setButtonAction() {		
		if (this.controller != null )
			this.controller.loadFromJAPS(selectedDocumentPath);
		setVisible(false); // close window
	}
	
	
	void babble(String bab){
		if (this.controller.getConfig().getDEBUG()) 
			System.out.println("CourseLoaderTreeNavigationFrame: "+bab);
	}
}



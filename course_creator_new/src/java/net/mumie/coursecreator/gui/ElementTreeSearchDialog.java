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

package net.mumie.coursecreator.gui;

/**
 * diese Klasse layoutet und implementiert die Suche nach bestimmten Kriterien
 * in der Datenbank
 */

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import net.mumie.coursecreator.CCController;
import net.mumie.coursecreator.treeview.AbstractDocumentAdapter;
import net.mumie.coursecreator.treeview.ContentDocumentAdapter;
import net.mumie.coursecreator.treeview.ContentTreeModel;
import net.mumie.coursecreator.treeview.ContentTreeNode;
import net.mumie.coursecreator.treeview.DocumentAdapter;
import net.mumie.coursecreator.treeview.SectionDocumentAdapter;
import net.mumie.coursecreator.treeview.TreeConstants;
import net.mumie.coursecreator.xml.XMLConstants;
import net.mumie.japs.client.JapsClient;
import net.mumie.japs.client.JapsClientException;

public class ElementTreeSearchDialog extends CCDialog 
implements 
//TreeWillExpandListener,
ListSelectionListener
 
{
	
	static final long serialVersionUID=0;
	private CCController cccontroller;
	
	private JTree ntree;
	private JList myList;
	private LinkedList list;
	
	private int width = 620;
	private int height = 580;
	
	private ElementMetaPanel searchPanel;
	private JPanel p;
	private JButton btSearch;
	private JScrollPane spOutput;
	  
	private JapsClient japsClient;
	
	private LinkedList<String> ids;
	private LinkedList<DocumentAdapter> found;

	private String LOG = "LOG.log";
	private String ERROR = "ERROR.log";
	

	
	
	public ElementTreeSearchDialog(CCController c){
		super(CCController.frame,c,"Suche");
		this.cccontroller = c;
		this.buildLayout();
		this.addComponentListener(this);

		this.ids = new LinkedList<String>();
		this.found = new LinkedList<DocumentAdapter>();
		this.japsClient = cccontroller.getJapsClient();
	}
	
	/** do the Layout */
	private void buildLayout(){
		this.setResizable(false);
		this.setAlwaysOnTop(false);
		
		setSize(this.width,this.height);
		this.setLocation(this.cccontroller.getConfig().getPOSITION_ELEMENTTREESEARCH());
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
		
		this.searchPanel = new ElementMetaPanel(this.cccontroller);

		this.searchPanel.setEditable(true);
		p.add(searchPanel);
		

		btSearch = new JButton("Suche");
		btSearch.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				search();
			}
			
		});
		p.add(btSearch);
		// Ausgabefeld
		this.spOutput = new JScrollPane( new JTree(new DefaultMutableTreeNode()));
		p.add(spOutput);
		this.add(p);
		
	}

	/** die Suchmethode */
	private void search(){

		list=new LinkedList<DocumentAdapter>();
		this.ids = new LinkedList<String>();
		ContentTreeModel top_model = new ContentTreeModel(this.cccontroller,
    				this.cccontroller.getJapsClient(), this.cccontroller.prefixURL());

		
		if (this.spOutput!=null) p.remove(spOutput);
		

//    	ntree = new JTree(top_model);
//    	expand(((ContentTreeNode)ntree.getModel().getRoot()).getContent());
//    	if (ntree== null)//FIXME sinnvoll, ntree nur einmal zu erstellen
    	{
    		//erzeuge baum, aber nur einmailg, da es sonst zu lange dauert.
    		ntree = new JTree(top_model);
    	
    		CCController.frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	    	createTree(((ContentTreeNode)top_model.getRoot()).getContent());
	    	ntree = new JTree(top_model);
	    	CCController.frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	    	this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	    	
    	}
    	
    	myList = new JList(list.toArray());

    	myList.addListSelectionListener(this);
    	myList.setToolTipText("katze");
    	this.spOutput= new JScrollPane( myList);
		p.add(spOutput);
		p.validate();
		p.repaint();
		
	}
	
	
		
	
	
	/**erzeugt (einmalig) rekursiv den kompletten Baum
	 * @param node wurzelknoten
	 */
	private void createTree(DocumentAdapter node){
		
		if (this.ids.contains(String.valueOf(node.getID()))){
			return;
		}
		this.ids.addLast(String.valueOf(node.getID()));
		
    	if (node.childrenCount()==0) return;
    		
    	for (int i = 0; i< node.childrenCount(); i++){
    		
    		DocumentAdapter nextChild = ((DocumentAdapter)node).getChildDocs()[i];
    		
    		if (!nextChild.toString().equals(ntree.getModel().getRoot().toString())){
	    		
	    		if (nextChild instanceof SectionDocumentAdapter){
	    		
		    		SectionDocumentAdapter newdoc =
		    		this.makeSectionTreeNode( String.valueOf(nextChild.getID()));
		    		if (newdoc== null){
		    			System.err.println("ERROR: newdoc is null!");
		    			return;
		    		}
		    		newdoc.setCategoryName(AbstractDocumentAdapter.ARROW_EXP);
		    		nextChild=newdoc;
		    			    		
		    		this.createTree(nextChild);
	    		}else if (nextChild instanceof ContentDocumentAdapter){
	    			ContentDocumentAdapter newdoc =
			    		this.makeContentTreeNode( String.valueOf(nextChild.getID()));
			    		if (newdoc== null){
			    			System.err.println("ERROR: newdoc is null!");
			    			
			    			return;
			    		}
			    		if (this.testForSearchOptions(newdoc)) {
			    			this.list.addLast(newdoc);
			    		newdoc.setCategoryName(AbstractDocumentAdapter.ARROW_CMP);
			    		nextChild=newdoc;
			    		}
	    		}
    		}
    	}
		
	}
	

	/**
	 * testet ob alle Kriterien erfuellt sind
	 * @param d
	 * @return true, flaas dokument auf Suche passt
	 */
	private boolean testForSearchOptions(ContentDocumentAdapter d){
		if (!this.containName(this.searchPanel.getCcssTitle(),d.getName(),this.searchPanel.txtfield_title.getText()))return false;
		if (!this.containName(this.searchPanel.getCcssDesc(),d.getDescription(),this.searchPanel.txtfield_desc.getText()))return false;
		if (!this.containName(this.searchPanel.getCcssFile(),d.getPath(),this.searchPanel.txtfield_file.getText()))return false;
		if (!this.containName(this.searchPanel.getCcssPure(),d.getPureName(),this.searchPanel.txtfield_pure.getText()))return false;
		if (!this.containName(this.searchPanel.getCcssCat(),d.getCategoryName(),this.searchPanel.txtfield_cat.getText()))return false;
		if (!this.containName(this.searchPanel.getCcssTyp(),d.getDocumentType(),this.searchPanel.txtfield_typ.getText()))return false;
		if (!this.containName(this.searchPanel.getCcssId(),d.getID(),this.searchPanel.txtfield_id.getText()))return false;
		if (!this.containName(this.searchPanel.getCcssSection(),d.getSection(),this.searchPanel.txtfield_section.getText()))return false;
		if (!this.containName(this.searchPanel.getCcssCreated(),d.getCreated(),this.searchPanel.txtfield_created.getText()))return false;
		if (!this.containName(this.searchPanel.getCcssModified(),d.getModified(),this.searchPanel.txtfield_modified.getText()))return false;
		return true;
	}

	/**
	 * falls Ignore gibts immer true
	 * sonst, falls der text vorhanden und And --> true<br>
	 * der text nicht vorhanden und Not --> true
	 * sonst false
	 * 
	 * @param comboboxContent IGNORE,AND,NOT
	 * @param documentAdaptertext der Text der Node
	 * @param searchtext der zu suchende Text
	 * @return 
	 */
	private boolean containName(String comboboxContent,String documentAdaptertext, String searchtext){
		
		if (comboboxContent.equals(ElementMetaPanel.IGNORE))return true;
		boolean isIn = documentAdaptertext.contains(searchtext);
		if (comboboxContent.equals(ElementMetaPanel.AND))	return isIn;
		else return !isIn;
	}
	
	private boolean containName(String comboboxContent,int documentAdaptertext, String searchtext){
		
		if (comboboxContent.equals(ElementMetaPanel.IGNORE))return true;
		boolean isIn = String.valueOf(documentAdaptertext).contains(searchtext);
		if (comboboxContent.equals(ElementMetaPanel.AND))	return isIn;
		else return !isIn;
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
	
	private SectionDocumentAdapter makeSectionTreeNode( String id){
		this.japsClient = this.cccontroller.getJapsClient();
		if (this.japsClient== null) {
			CCController.dialogErrorOccured("JapsClient ERROR", "Auch nach dem retry ist japsClient== null", JOptionPane.ERROR_MESSAGE);
		
			return null;
		}
	
		HttpURLConnection conn =null;
		try {
			conn = this.japsClient.get(TreeConstants.TREE_SECTION_VIEW_XML_URL + id);
		} catch (MalformedURLException e1) {
			CCController.dialogErrorOccured("JapsClientERROR", "MalformedURLException "+e1, JOptionPane.ERROR_MESSAGE);
			return null;
		} catch (JapsClientException e1) {
			CCController.dialogErrorOccured("JapsClientERROR", "JapsClientException "+e1+"id "+id, JOptionPane.ERROR_MESSAGE);
			return null;
		} catch (IOException e1) {
			CCController.dialogErrorOccured("JapsClientERROR", "IOException "+e1, JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		InputStream in = null;
		try {
			in = conn.getInputStream();
		} catch (IOException e1) {
			CCController.dialogErrorOccured("IOException", "IOException "+e1, JOptionPane.ERROR_MESSAGE);
			return null;
		}  
		//babble("XML URL is: " + conn.getURL().toString()); 
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
			CCController.dialogErrorOccured("DOM ERROR", "IOException while accessing DOMBuilder "+ioe, JOptionPane.ERROR_MESSAGE);
			System.err.println(" IOException while accessing DOMBuilder: " +ioe );
		}
		catch (Exception e) {
			CCController.dialogErrorOccured("DOM ERROR", "Exception while accessing DOMBuilder "+e, JOptionPane.ERROR_MESSAGE);

		}
		
		SectionDocumentAdapter section = new SectionDocumentAdapter(this.cccontroller,rootDocument.getDocumentElement());
		return section;
	}
	
	
	private ContentDocumentAdapter makeContentTreeNode( String id){
		this.japsClient = this.cccontroller.getJapsClient();
		if (this.japsClient== null) {
			CCController.dialogErrorOccured("JapsClient ERROR", "Auch nach dem retry ist japsClient== null", JOptionPane.ERROR_MESSAGE);
		
			return null;
		}
	
		HttpURLConnection conn =null;
		try {
			conn = this.japsClient.get(TreeConstants.TREE_SECTION_VIEW_XML_URL + id);
		} catch (MalformedURLException e1) {
			CCController.dialogErrorOccured("JapsClientERROR", "MalformedURLException "+e1, JOptionPane.ERROR_MESSAGE);
			return null;
		} catch (JapsClientException e1) {
			CCController.dialogErrorOccured("JapsClientERROR", "JapsClientException "+e1+"id "+id, JOptionPane.ERROR_MESSAGE);
			return null;
		} catch (IOException e1) {
//			CCController.dialogErrorOccured("JapsClientERROR", "IOException "+e1, JOptionPane.ERROR_MESSAGE);
			this.log(" IOException : " +e1 ,true,ERROR);
			return null;
		}
		
		InputStream in = null;
		try {
			in = conn.getInputStream();
		} catch (IOException e1) {
//			CCController.dialogErrorOccured("IOException", "IOException "+e1, JOptionPane.ERROR_MESSAGE);
			this.log(" IOException : " +e1 ,true,ERROR);
			return null;
		}  
		//babble("XML URL is: " + conn.getURL().toString()); 
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
			CCController.dialogErrorOccured("DOM ERROR", "IOException while accessing DOMBuilder "+ioe, JOptionPane.ERROR_MESSAGE);
//			System.err.println(" IOException while accessing DOMBuilder: " +ioe + " In war: "+in );
			String s="";
			byte[] buffer;
			try {
				buffer = new byte[1024];
				in.read(buffer);
//				 for ( int len; (len = in.read(buffer)) != -1; ) 
					 s=s+ new String(buffer); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				this.log("Das geht uebrigends schief!", true,ERROR);
				e.printStackTrace();
			} 
		   
		    
			this.log("IOException while accessing DOMBuilder: " +ioe + " In war: "+s ,true,ERROR);
			return null;
		}
		catch (Exception e) {
			CCController.dialogErrorOccured("DOM ERROR", "Exception while accessing DOMBuilder "+e, JOptionPane.ERROR_MESSAGE);
//			System.err.println(" Exception while accessing DOMBuilder: " +e + " In war: "+in );
			String s="";
			byte[] buffer;
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream(); 
				byte[] bytes = new byte[1024]; 
				
				int r; 
				
				while ((r = in.read(bytes,0,1024)) > 0) {
					out.write(bytes,0,r); 
				} // end of while ()
				
				s=out.toString(out.size());
//				s= new String(out);
			} catch (IOException w) {
				// TODO Auto-generated catch block
				s="das haette nicht passiern duerfen!";
				this.log("Das gehtuebrigends schief!", true,ERROR);
			} 
			
			this.log("Exception while accessing DOMBuilder: " +e + " In war: "+s ,true,ERROR);
			return null;

		}
		
		ContentDocumentAdapter section = new ContentDocumentAdapter(this.cccontroller,rootDocument.getDocumentElement());
		return section;
	}
	
	/** overwrite for ccconfig */
	public void componentMoved(ComponentEvent e) {
		this.cccontroller.getConfig().setPOSITION_ELEMENTTREESEARCH(e.getComponent().getLocation());
	}
	
	 /**
     * Method for logging the activities of this class
     * @param bab the <code> String </code> which appears in the Code
     * @param ln
     */
    private void log(String bab,boolean ln,String name){
    	FileWriter f ;
		File logFile = new File(name);
		if (logFile.exists()){
			try{f= new FileWriter(name, true);}
			catch (Exception e){
				CCController.dialogErrorOccured(
						"GraphLoader Exception",
						"<html>Exception<p>"+e+"</html>",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		else {
			try{f= new FileWriter(name);}
			catch (Exception e){
				CCController.dialogErrorOccured(
						"GraphLoader Exception",
						"<html>Exception<p>"+e+"</html>",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		try{if (ln) f.write(bab+"\n");
			else f.write(bab);
    		
		    // Write to file
		    f.flush();
		    f.close();
		}
		catch (Exception e){
			CCController.dialogErrorOccured(
					"GraphLoader Exception",
					"<html>Exception<p>"+e+"</html>",
					JOptionPane.ERROR_MESSAGE);
			
			return;
		}
    }
    
    public void actionPerformed(ActionEvent e) {
		this.cccontroller.getConfig().setDefaultSearchValues();
	
	}

    private String attach(String s,String label,String att){
    	return s.concat(label+": <b>"+att+"</b><br>");
    }
    
    private String attach(String s,String label,int att){
    	return s.concat(label+": <b>"+att+"</b><br>");
    }
    
    
    
	public void valueChanged(ListSelectionEvent e) {
		
		String text = "<html>";
		text=this.attach(text, "name", ((DocumentAdapter)myList.getSelectedValue()).getName());
		text=this.attach(text, "description", ((DocumentAdapter)myList.getSelectedValue()).getDescription());
		text=this.attach(text, "Pfad", ((DocumentAdapter)myList.getSelectedValue()).getPath());
		text=this.attach(text, "purename", ((DocumentAdapter)myList.getSelectedValue()).getPureName());
		text=this.attach(text, "ID", ((DocumentAdapter)myList.getSelectedValue()).getID());
		text=this.attach(text, "Section", ((DocumentAdapter)myList.getSelectedValue()).getSection());
		text=this.attach(text, "Kategorie", ((DocumentAdapter)myList.getSelectedValue()).getCategoryName());
		text=this.attach(text, "Typ", ((DocumentAdapter)myList.getSelectedValue()).getDocumentType());
		text=this.attach(text, "Created", ((DocumentAdapter)myList.getSelectedValue()).getCreated());
		text=this.attach(text, "Modified", ((DocumentAdapter)myList.getSelectedValue()).getModified());
		
		text.concat("</html>");
		myList.setToolTipText(text);
		
	}
	
}

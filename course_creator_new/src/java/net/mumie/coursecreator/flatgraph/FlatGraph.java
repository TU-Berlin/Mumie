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

package net.mumie.coursecreator.flatgraph;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;



import net.mumie.coursecreator.CCController;
import net.mumie.coursecreator.CCModel;
import net.mumie.coursecreator.CommandConstants;
import net.mumie.coursecreator.Graph;
 

import net.mumie.coursecreator.graph.MetaInfos;
import net.mumie.coursecreator.xml.GraphXMLizer;
import net.mumie.coursecreator.xml.XMLConstants;


/**
 * <p>
 * The main class for managing a FlatGraph. 
 * </p>
 * It provides methods for saving and editing a graph
 * 
 * 
 * @author <a href="mailto:vrichter@math.tu-berlin.de>Verena Richter</a>
 */

public class FlatGraph extends JTree implements Graph{
	
	static final long serialVersionUID=0;
	private int undoNumber = 0;
	 private String undoName = "iamaflatgraphandihopenobodygetsmyname";
	 
    private int lid_counter = 0; //
    private LinkedList<String> lids;
    
    private int nid_counter = 0; //
    
    private boolean changed;
    private FlatGraphController controller;
    
    private CCController ccController;
    private CCModel ccModel;
    
    private boolean setStatusDefault = false;
    private boolean setHomeCatDefault = false;
        
    private MetaInfos metas;
    private LinkedList<FlatCell> cells;
//  toXML
    private Document document = null;
    private DocumentBuilder builder;
    
    private Object[] lastMarkedCells;
    
    /**
     * Create a new NavGraph instance.
     */
    public FlatGraph(CCController c,CCModel ccmodel) {
    	
    	this.ccModel = ccmodel;
    	this.ccController = c;
    		
    	this.metas = new MetaInfos(this.ccController);
    	
    	controller = new FlatGraphController(this);
    	
    	this.lids = new LinkedList<String>();
    	this.cells = new LinkedList<FlatCell>();
    	this.changed = true; // well .. setChange methode also notifys model
    	
    	//needed for saveXML
    	try {
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    		builder = factory.newDocumentBuilder();
    	} catch (ParserConfigurationException pce) {
    		CCController.dialogErrorOccured(
    				"NavGraph ParserConfigurationException",
    				"<html>ParserConfigurationException while getting document builder<p>"+pce+"</html>",
    				JOptionPane.ERROR_MESSAGE);
    	}	

    }
    
    
    
    /**
     * determines if the current Graph is a SectionGraph
     * @return true graph is a Section
     *         false Graph is no Section
     */
    public boolean isSectionGraph(){
    	if (this.metas.getGraphType()==MetaInfos.GRAPHTYPE_SECTION) return true;
    	return false;
    } 
    
    
    /**
     * @return true graph is a Problem
     *         false Graph is no Problem
     */
    public boolean isProblemGraph(){
    	if (this.metas.getGraphType()==MetaInfos.GRAPHTYPE_HOMEWORK || 
    			this.metas.getGraphType()==MetaInfos.GRAPHTYPE_PRELEARN ||
    			this.metas.getGraphType()==MetaInfos.GRAPHTYPE_SELFTEST) return true;
    	return false;
    } 
     /**
     * @return true graph is a Element
     *         false Graph is no Element
     */
    public boolean isElementGraph(){
    	if (this.metas.getGraphType()==MetaInfos.GRAPHTYPE_ELEMENT) return true;
    	return false;
    } 
  
    
    public void setGraphType(int type){
    	this.metas.setGraphType(type);
    }
    
   /**
     * creates new cell and generates nid and lid for it
     * @param cellType
     * @param category
     * @return
     */
    public Object createCell(int cellType, int category){//TODO implement
    	Object vertex=null;
       	return vertex; 
    }

   
    public Object createCell(int cellType, String path, int cat, int nid, String lid, String label){//TODO implement
    	Object vertex=null;
       	return vertex; 
    }

   
    public Object insertCell(Object vertex){//TODO implement
    	
   
    	
    	return vertex;
    }
    
    
    
	/**
	 * deletes all cells and edges which are selected,
	 * deletes a subComponent if its parent is selected
	 * deletes an edge if its start or endcell is selected
	 *
	 */
    public void delete(){//TODO implement
    	
    	    }
  	    /**
     * Get the MetaInfos for this Graph
     * @return <code>MetaInfos</code>
     */
	public MetaInfos getMetaInfos() {
		return metas;
	}
	
	/**
     * Set the meta informations of the graph.
     * @param metas MetaInfos, the new meta infos of the graph
     */
    public void setMetaInfos(MetaInfos metas) {
    	this.metas = metas;
    }

	public FlatGraphController getController() {
		return controller;
	}
    
	public int getNid_counter() {return nid_counter;}
	private void setNid_counter(int nc){this.nid_counter=nc;}
	
	public int getLid_counter() {return lid_counter;}
	public void setLid_counter(int lid_counter) { this.lid_counter = lid_counter;}
	
	public boolean getChanged() {return changed;}
	/**
	 * sets changedvariable
	 * returns if the changedvariable changed.. is importent for rebuild the menu..
	 * calls notify model!
	 * @param changed
	 * @return
	 */
	public void setChanged(boolean changed) {
		this.changed = changed;
		updateUndo();
		this.ccController.notifyModel();
	}


    /**
     * sets the summary
     * @param url
     */
    public void setSummary(String url){
    	if (!this.getSummary().equals(url)){
    		this.setChanged(true);
    		this.metas.setSummary(url);
    	}
    }
    
    /**
     * returns the Summary
     * @return a {@link String} with the path to the summary of this graph
     */
    public String getSummary(){
    	return this.metas.getSummaryPath();
    	}
	
	/*******************************************
	// toXML-Methods
	*******************************************/
	
	////////////////////////////////////////////
	// MetaMethodes
	////////////////////////////////////////////
	
    /**
     * provides the XML-Syntax for the meta-Information and thus 
     * for the Meta-XML-file
     */
    public String toXMLMeta(){
    	String xml = "";
    	Document doc  = this.buildMetaDocument();

    	try {
			xml = GraphXMLizer.toXMLString(doc);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}

    	return xml;
    }
	
	/**
     *  creates the MetaDocument 
     * @return
     */
    private Document buildMetaDocument(){

    	this.document = builder.newDocument();
    	
    	if (this.document != null) {
    		
    		//rootElement des XMLFiles
    		Element root = document.createElementNS(XMLConstants.NS_META, XMLConstants.PREFIX_META + XMLConstants.getRootTag(this));
        	
    		//UseMode
    		root.setAttribute("use-mode","checkin");
    		
    		//NameSpace
    		root.setAttribute("xmlns:mumie",XMLConstants.NS_META);
    		 
    		document.appendChild(root);
    		
    		//MetaInfos	
    		this.getXMLMetaInfos(root);

    		root.appendChild(this.getXMLMetaComponents());
    		
    		//contentType
    		Element conType = document.createElementNS(XMLConstants.NS_META,XMLConstants.PREFIX_META + "content_type");
    		conType.setAttribute("type","text");
    		conType.setAttribute("subtype","xml");
    		root.appendChild(conType);

    	}
    	return document;

    }
    

    /**
     * create MetaInfo part of the XML file
     * 
     */
    private void getXMLMetaInfos(Element xmlParent)   {
    	MetaInfos meta = this.getMetaInfos();
    	meta.setDefaults();
    	
    	//status
    	Element status = this.document.createElementNS(XMLConstants.NS_META, XMLConstants.PREFIX_META + "status");
    	status.setAttribute("name",MetaInfos.getStatusAsString(meta.getStatus()));
    	xmlParent.appendChild(status);
    	
    	//name
    	Element name = this.document.createElementNS(XMLConstants.NS_META, XMLConstants.PREFIX_META + "name");
    	Node nameData = this.document.createTextNode(meta.getName());
    	name.appendChild(nameData);
    	xmlParent.appendChild(name);
    	
    	// description
    	Element description = this.document.createElementNS(XMLConstants.NS_META, XMLConstants.PREFIX_META  + "description");
    	Node descriptionData = this.document.createTextNode(meta.getDescription());
    	description.appendChild(descriptionData);
    	xmlParent.appendChild(description);
    	
    	//copyright
    	Element copyright = this.document.createElementNS(XMLConstants.NS_META,XMLConstants.PREFIX_META + "copyright");
    	Node copyrightData = this.document.createTextNode(meta.getCopyright());
    	copyright.appendChild(copyrightData);
    	xmlParent.appendChild(copyright);
    	
    	//summary
    	if (meta.getSummaryPath()!=null){
	    	Element summary = this.document.createElementNS(XMLConstants.NS_META, XMLConstants.PREFIX_META + "generic_summary");
			summary.setAttribute("path",String.valueOf(meta.getSummaryPath()));
			xmlParent.appendChild(summary);
    	}
    	
    	//authors
    	Element authors = this.document.createElementNS(XMLConstants.NS_META,XMLConstants.PREFIX_META + "authors");
    	int[] aus = meta.getAuthors();
    	if (aus != null) {
    		for (int i = 0; i < aus.length; i++) {
    			Element author = this.document.createElementNS(XMLConstants.NS_META, XMLConstants.PREFIX_META + "author");
    			author.setAttribute("id",String.valueOf(aus[i]));
    			authors.appendChild(author);
    		}
    	}
    	xmlParent.appendChild(authors);
    	
    	//starttime
    	if (this.isProblemGraph()){
    	
    		this.getCCController().getModel().setUpdateDateEvent();
    		
    		Element sDate = this.document.createElementNS(XMLConstants.NS_META, XMLConstants.PREFIX_META + "timeframe_start");
    		sDate.setAttribute("value", meta.getFormattedDate(meta.getDateStartCal()));
    		sDate.setAttribute("raw", meta.getRawDate(meta.getDateStartCal()));
    		xmlParent.appendChild(sDate); 

    		Element eDate = this.document.createElementNS(XMLConstants.NS_META,XMLConstants.PREFIX_META + "timeframe_end");
    		eDate.setAttribute("value", meta.getFormattedDate(meta.getDateEndCal()));
    		eDate.setAttribute("raw", meta.getRawDate(meta.getDateEndCal()));
    		xmlParent.appendChild(eDate);
    	}

    	// category
   
    	if (this.isProblemGraph())
    	if (meta.getGraphType() != XMLConstants.UNDEFINED_ID) {
    		Element category = this.document.createElementNS(XMLConstants.NS_META,XMLConstants.PREFIX_META + "category");
    		category.setAttribute("name",MetaInfos.getGraphtypeAsString(meta.getGraphType()));
    		xmlParent.appendChild(category);
    		
    	}
    	

    	if ((this.isSectionGraph())) {    		
    		if((meta.getClassPath() != XMLConstants.PATH_UNDEFINED))
    		{
    			Element elClass = this.document.createElementNS(XMLConstants.NS_META, XMLConstants.PREFIX_META + "class");
    			elClass.setAttribute("path",String.valueOf(meta.getClassPath())+".meta.xml");
    			elClass.setAttribute("name",String.valueOf(meta.getClassName()));
    			xmlParent.appendChild(elClass);
    		}
    			    
    	}
    }
    
    private LinkedList<FlatCell> getCells(){
    	return this.cells;
    }
    
    /* (non-Javadoc)
	 * @see net.mumie.coursecreator.Graph#createCell(int, java.lang.String, int, int, java.lang.String, java.lang.Object, int, int, java.lang.String)
	 */
	public Object createCell(int cellType, String path, int cat, int nid, String lid, Object parent, int x, int y, String label) {
		// TODO Auto-generated method stub
		return null;
	}



	/* (non-Javadoc)
	 * @see net.mumie.coursecreator.Graph#getButtonEnable(java.lang.String)
	 */
	public boolean getButtonEnable(String button) {
		
		if (button.equals(CommandConstants.BUTTON_CONNECT)){
			//connect
			return false;

		} else if (button.equals(CommandConstants.BUTTON_DELETE)){
			// delete selected
			return this.getSelectionCount()>0; 

		} else if (button.equals(CommandConstants.BUTTON_EDGEDIR)){
			// change edgeDirection
			return false;
		} else if (button.equals(CommandConstants.BUTTON_EXISTS)){
			return false;
		} else if (button.equals(CommandConstants.BUTTON_RED)){
			return false;

		} else if (button.equals(CommandConstants.BUTTON_SWAP)){
			return false;
		} else if (button.equals(CommandConstants.BUTTON_ZOOMIN)){
			return false;
		} else if (button.equals(CommandConstants.BUTTON_ZOOMOUT)){
			return false;
			
		}else if (button.equals(CommandConstants.BUTTON_SHOWSOURCE)){
			return (this.getCells().size()!=0);
		}else if (button.equals(CommandConstants.BUTTON_UNDO)){
			return (this.undoNumber>0);
		
		}
		return false;

	}



	/* (non-Javadoc)
	 * @see net.mumie.coursecreator.Graph#getSelectionCell()
	 */
	public Object getSelectionCell() {
		// TODO Auto-generated method stub
		return null;
	}



	/* (non-Javadoc)
	 * @see net.mumie.coursecreator.Graph#getSelectionCells()
	 */
	public Object[] getSelectionCells() {
		// TODO Auto-generated method stub
		return null;
	}



	/**
     * create the components part of the XML file
     */
    private Element getXMLMetaComponents() {
    	
    	Element components = document.createElementNS(XMLConstants.NS_META, XMLConstants.PREFIX_META + "components");
    	
    	LinkedList<FlatCell> cellList = this.getCells();
   
		for (int i=0;i<cellList.size();i++) {
			FlatCell cell = cellList.get(i);
		
			// insert Elements
			Element xmlElem = cell.getMetaXML(document);
			components.appendChild(xmlElem);
		    				
		}
    	return components;
	}
    
    ///////////////////////////////////////////////
    // ContentXML Methodes
    ///////////////////////////////////////////////
    
    /**
     * provides the XML-Syntax for the content-File
     */
    public String toXMLContent(){
    	String xml = "";
    	Document doc = this.buildContentDocument();

    	try {
			xml = GraphXMLizer.toXMLString(doc);
			} catch (UnsupportedEncodingException e) {
			
				e.printStackTrace();
			} catch (TransformerException e) {
			
				e.printStackTrace();
			}
    	
    	return xml;
    }
    
    /**
     * creates the ContentDocument 
     * @return
     */
    private Document buildContentDocument(){

    	
    	this.document = builder.newDocument();
    	
    	if (this.document != null) {

    		// root
    		Element csection = document.createElementNS(
    				XMLConstants.getNS_STRUCT(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())),
    				XMLConstants.getPrefix(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())) + 
    						XMLConstants.getRootTag(this));
    		
    		// namespace
    		csection.setAttribute("xmlns:" + XMLConstants.getPrefixName(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())),
    				XMLConstants.getNS_STRUCT(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())));
    		
    		
    		document.appendChild(csection);
    		
    		//net
    		csection.appendChild(this.getXMLContentNet());
    		
    	}
    	
    	return document;
	}
    
    /**
     * creates the netXMLElement for Content
     * @return
     */
    private Element getXMLContentNet(){
		Element net = this.document.createElementNS(XMLConstants.getNS_STRUCT(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())),
				XMLConstants.getPrefix(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())) + "net");
		
//		 NetNodes
		net.appendChild(this.getXMLContentNodes());

		return net;
    }

    /**
     * creates the nodesXMLElement for Content
     * @return
     */
    private Element getXMLContentNodes(){
		Element nodes = this.document.createElementNS(XMLConstants.getNS_STRUCT(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())),
				XMLConstants.getPrefix(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())) + "nodes");

		LinkedList<FlatCell> objs = this.getCells();
		for(int i = 0; i < objs.size(); i++) {

			Element elem;

			FlatCell cell = objs.get(i); 
		
			elem = (Element)cell.getContentXML(this.document, 
					XMLConstants.getPrefix(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())));
		
			nodes.appendChild(elem);
		

    	}
    	return nodes;
    }
    
    public String toString(){return this.getCells().toString();}

	/** is need to set MetaInfoPanel-Status-radioButton to default 
	 * 
	 * @param HomeCatDefault true if defaultcategory is to be set
	 */
	public void setSetStatus(boolean setStatus) {
		this.setStatusDefault = setStatus;
	}
	
	/** CCModel asks for when an cell is inserted and the inserted Cell
	 * was first MainComponendcell and it was an Problemcell.
	 * if this function will return true the metaInfoField defaultCatrgory is set to homework. 
	 * 
	 */
	public boolean getsetHomeCatDefault() {
		return setHomeCatDefault;
	}

	/** is need to set MetaInfoPanel-Category for Problems 
	 * 
	 * @param HomeCatDefault true if defaultcategory is to be set
	 */
	public void setsetHomeCatDefault(boolean HomeCatDefault) {
		this.setHomeCatDefault= HomeCatDefault;
	}

	public Object[] getLastMarkedCells() {
		return this.lastMarkedCells;
	}

	public void setLastMarkedCell(Object[] cell) {
		this.lastMarkedCells = cell;
	}

	public CCController getCCController() {
		return ccController;
	}
	

	public int getUndoNumber() {
		return this.undoNumber;
	}

	public void setUndoNumber(int n) {
		this.undoNumber= n;
	}

	
	private void updateUndo(){
		 this.undoNumber++;
		 this.ccModel.updateUndo(this);
	}

	public String getUndoName() {
		return this.undoName;
	}
	public void setUndoName(String s) {
		this.undoName = s;
	}
}

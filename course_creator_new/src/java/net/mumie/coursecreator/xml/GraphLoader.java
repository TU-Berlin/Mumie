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

package net.mumie.coursecreator.xml;

import java.io.*;
import java.util.*;

import net.mumie.coursecreator.CCController;

import net.mumie.coursecreator.CCModel;
import net.mumie.coursecreator.CommandConstants;
import net.mumie.coursecreator.Graph;

import net.mumie.coursecreator.graph.MetaInfos;
import net.mumie.coursecreator.graph.cells.BranchCellConstants;
import net.mumie.coursecreator.graph.cells.CellConstants;
import net.mumie.coursecreator.graph.cells.ComponentCellConstants;
import net.mumie.coursecreator.graph.cells.MainComponentConstants;
import net.mumie.coursecreator.graph.cells.SubComponentConstants;


import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.jaxen.JaxenException;

import java.awt.Point;

/**
 * <p>
 * The GraphLoader has the the function to load and parse course-files which are
 * locally stored. It will fill up the <code>MetaInfos</code> and build an internal
 * structure of the net 
 * </p>
 * @author <a href="mailto:binder@math.tu-berlin.de>Jens Binder</a>
 * @author <a href="mailto:vrichter@math.tu-berlin.de>Verena Richter</a>
 * @version $Id: GraphLoader.java,v 1.44 2009/03/18 19:16:58 vrichter Exp $
 *
 */
public class GraphLoader {

	// too lazy to write these Strings again and again ;)
	final static private String MUMIE = XMLConstants.PREFIX_META;
	private String PREFIX; 	
	private String LOG = "load.log";

	private static HashMap<String,XPath> xPathList;
	private int graphType; // values from MetaInfos.GRAPHTYPE_lala
	
	private CCModel model; // graph needs a model
	private MetaInfos metaInfos;

	private LinkedList<CellHolder> cells;
	private LinkedList<EdgeHolder> edges;
	
	
    public GraphLoader(CCModel m){
    	
    	this.model = m;
    	clear();
			
	   }

	/**
	 * sets graphloader to default (empty)
	 * VERY IMPORTENT! otherwise errors emerge from old data  
	 */
    private void clear() {
    	xPathList = new HashMap<String,XPath>();
	
		File log = new File(LOG);
		if (log.exists()){log.delete(); }
		
		this.metaInfos = new MetaInfos(this.model.getController());
		this.cells = new LinkedList<CellHolder>();
		this.edges = new LinkedList<EdgeHolder>();
    }

    /**
     * Load graph from given file.
     * @param dir      directory of the file
     * @param fileName name of the file - with .meta.xml, or without both works
     */   
    public Graph load(String dir, String fileName) {
	
    	this.logln("\n\n\n\n\n-------------------------------------\n-------------------------------------");
    	this.logln("GMT:"+GregorianCalendar.getInstance().getTime().toString());
    	this.logln("parse .. "+dir+"/"+fileName);
    	this.logln("-------------------------------------");
    
    	if (fileName.endsWith(".content.xml")) {
    		fileName = fileName.substring(0, fileName.length()-".content.xml".length()).concat(".meta.xml");
    	}
    	
    	if (!fileName.endsWith(".meta.xml")) {
    		fileName = fileName.concat(".meta.xml");
    	}
    	
    	if (!dir.equals("")) dir = dir.concat("/");
    	File f = new File(dir+fileName);
    	if (!f.exists()){
    		CCController.dialogErrorOccured(
					"GraphLoader: File not exists", 
					"GraphLoader:  File "+f.getName()+" does not exists in Directory "+dir,
					JOptionPane.ERROR_MESSAGE);
    		return null;
    	}
    	
	 	try {
		    parse(dir,fileName);
		    
		    
		} catch (Exception e) { 
			CCController.dialogErrorOccured(
					"GraphLoader: Exception", 
					"GraphLoader: Exception "+e,
					JOptionPane.ERROR_MESSAGE);
		}

		this.metaInfos.setSaveName(fileName.substring(0,fileName.length()-9));
		this.metaInfos.setSaveDir(dir);
		
		Graph g = this.createGraph();
		babble("g.getMetaInfos() "+g.getMetaInfos());
		
		return g;
		
		
    }
    
    private Graph createGraph(){
    	
    	
    	if (this.isNavGraph()){// TODO an andere Graphtypen anpassen
    		NavGraphLoader ngl =new NavGraphLoader(this);
    	return ngl.getGraph();
    	}
    	
    	return null;
    }
    
    protected MetaInfos getMetaInfos(){
    	return this.metaInfos;
    }
    
    private boolean isNavGraph(){// TODO an andere Graphtypen anpassen
    	return true;
    } 
    /**
     * parses a File and sets all informations to this {@link NavGraph} and the 
     * corresponding {@link MetaInfos}
     * @param path  {@link File}-Object with the path where the document to parse is situated
     * @param FileName {@link String} name of the file
     * @param metas the {@link MetaInfos} which shall be filled with data 
     */
    public void parse(String path, String FileName) {
		
    	// clean..
    	this.clear();
    	
    	FileName = FileName.substring(0,FileName.length()-".meta.xml".length());
    	
    	babble("parse: parsing file"+path+FileName);
		Document docMeta = null;
		Document docContent = null;
		DocumentBuilder builder;
		
		try{
			babble("try to Build Factory");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();			
			factory.setIgnoringComments(true);
			factory.setNamespaceAware(true);			
			builder = factory.newDocumentBuilder();
			
			babble("new documentbuilder done..");
			docMeta = builder.parse(new File(path+FileName+".meta.xml"));
			babble("parse metaFile done..");
			babble("MetaFile is "+docMeta);
		}
		catch (IOException ioe){
			CCController.dialogErrorOccured(
					"GraphLoader: IOException", 
					"GraphLoader: IOException while accessing DOMBuilder: " +ioe ,
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		catch (Exception e) {
			CCController.dialogErrorOccured(
					"GraphLoader: Exception", 
					"GraphLoader: Exception while accessing DOMBuilder: " +e ,
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		Element rootMeta = docMeta.getDocumentElement();
		babble("rootMeta is: "+rootMeta);
		
		try {// TODO an andere Graphtypen anpassen
			babble("try set graphtyp start .. "+ rootMeta.getNodeName());
			 if (rootMeta.getNodeName().equals(MUMIE +  XMLConstants.ROOT_TAG[MetaInfos.GRAPHTYPE_SECTION])) {
				this.metaInfos.setGraphType(MetaInfos.GRAPHTYPE_SECTION);
				
				this.logln("setType to course: "+this.metaInfos.getGraphType());
				babble("GraphHandler: setType to course: "+this.metaInfos.getGraphType());
			}
			 
			else if (rootMeta.getNodeName().equals(MUMIE + XMLConstants.ROOT_TAG[MetaInfos.GRAPHTYPE_ELEMENT])) {
				this.metaInfos.setGraphType(MetaInfos.GRAPHTYPE_ELEMENT);
				this.logln("setType to section: "+this.metaInfos.getGraphType());
				babble("GraphHandler: setType to section: "+this.metaInfos.getGraphType());
			}
			else if (rootMeta.getNodeName().equals(MUMIE + XMLConstants.ROOT_TAG[Math.min(MetaInfos.GRAPHTYPE_HOMEWORK,Math.min(MetaInfos.GRAPHTYPE_PRELEARN,MetaInfos.GRAPHTYPE_SELFTEST))])) {
				this.metaInfos.setGraphType(Math.min(MetaInfos.GRAPHTYPE_HOMEWORK,Math.min(MetaInfos.GRAPHTYPE_PRELEARN,MetaInfos.GRAPHTYPE_SELFTEST)));
				this.logln("setType to subsection: "+this.metaInfos.getGraphType());
				babble("GraphHandler: setType to subsection: "+this.metaInfos.getGraphType());
			}
			
			else {
				String msg = "ERROR: not a valid course, course_section or worksheet!";
				this.logln(msg);
				
				CCController.dialogErrorOccured(
						"GraphLoader: JaxenException", 
						"GraphLoader: JaxenException: " +msg ,
						JOptionPane.ERROR_MESSAGE);
				throw new JaxenException(msg); 
			}
				
			babble("... done try set graphtyp");
			this.graphType = this.metaInfos.getGraphType();
			
			PREFIX = XMLConstants.getPrefix(this.graphType); // is it a course, course_section or worksheet
			
			babble("this.setMetaInfos(rootMeta) ..." + rootMeta);
			this.setMetaInfos(rootMeta);
			
			babble(".. done this.setMetaInfos(rootMeta)");
			
			String contentFileName = FileName + ".content.xml"; 
			
			docContent = builder.parse(new File(path+contentFileName));
			Element rootContent = docContent.getDocumentElement();
			
			this.setCourseComponents(rootMeta,rootContent);
			
			babble("CourseComponents anzahl "+ this.getCells().size());
			
			this.setEdges(rootContent);
			babble("CourseEdges anzahl "+ this.getEdges().size());
			this.metaInfos.setGraphType(this.graphType);
		}
		catch (JaxenException je) {
			CCController.dialogErrorOccured(
					"GraphLoader: JaxenException", 
					"GraphLoader: JaxenException: " +je ,
					JOptionPane.ERROR_MESSAGE);
		}
		catch (Exception e){
			CCController.dialogErrorOccured(
					"GraphLoader: Exception", 
					"GraphLoader: Exception: " +e ,
					JOptionPane.ERROR_MESSAGE);
		}
	}


	/**
	 * returns the Node which is specified by the XPath-expression-argument "nodepath"
	 * within the DOM-document specified by the root - Element 
	 */
	private Node getSingleNode(Object root, String nodepath) throws JaxenException{
		XPath path;		
		
		if (xPathList.containsKey(nodepath)){
			path = (XPath) xPathList.get(nodepath);
		}			
		else {
			
			path = new DOMXPath(nodepath);
			path.addNamespace("mumie",XMLConstants.NS_META);
		    path.addNamespace(XMLConstants.getPrefixName(this.metaInfos.getGraphType()),XMLConstants.getNS_STRUCT(this.metaInfos.getGraphType()));
		    path.addNamespace("cabs",XMLConstants.getNS_STRUCT(-1));
		    xPathList.put(nodepath,path);
		    
		}
		return (Node) path.selectSingleNode(root);
		
	}
	
	
	/**
	 * returns a List of Nodes which is specified by the XPath-expression-argument "nodepath"
	 * within the DOM-document specified by the root - Element
	 */
	private List getAllNodes(Object root, String nodepath) throws JaxenException
	{
		XPath path; 
		if (xPathList.containsKey(nodepath)){
			path = (XPath) xPathList.get(nodepath);			
		}			
		else{
			path =new DOMXPath(nodepath);
			path.addNamespace("mumie",XMLConstants.NS_META);
			path.addNamespace(XMLConstants.getPrefixName(this.metaInfos.getGraphType()),XMLConstants.getNS_STRUCT(this.metaInfos.getGraphType()));
			xPathList.put(nodepath,path);
		}		
		return path.selectNodes(root);
	}	
	
	/**
	 * sets all the MetaInfos of the graph with Element root
	 */
	private void setMetaInfos(Element root) throws JaxenException{
		this.logln("is setMetaInfos ...");
		Node temp;
		this.logln("...");
		this.logln("MUMIE" +MUMIE);
		String preMeta = "/*/"+MUMIE;		
				
		this.logln("setMetaInfos ...");

		//name
		temp = this.getSingleNode(root,preMeta+"name/text()");
		if (temp!=null) {
			metaInfos.setName(temp.getNodeValue());
			this.logln("\t Name: "+temp.getNodeValue());
		}
		else this.logln("\t ERROR: Name not exists");

		//description
		temp = this.getSingleNode(root,preMeta+"description/text()");
		if (temp!=null){
			metaInfos.setDescr(temp.getNodeValue());
			this.logln("\t Description: "+temp.getNodeValue());
		}
		else this.logln("\t ERROR Description not exists");
		// summary
		temp = this.getSingleNode(root, preMeta+"generic_summary/@path");
		if(temp!=null){
			metaInfos.setSummary(temp.getNodeValue());
			this.logln("\t Summary: "+temp.getNodeValue());
		}
		else this.logln("\t ERROR Summary not exists");
		//status
		temp = this.getSingleNode(root,preMeta+"status/@name");
		if (temp!=null){
			metaInfos.setStatus(temp.getNodeValue());
			this.logln("\t Status: "+temp.getNodeValue());
		}
		else this.logln("\t ERROR: Status not exists");
		//copyright
		temp = this.getSingleNode(root,preMeta+"copyright/text()");
		if (temp!=null){
			metaInfos.setCopyright(temp.getNodeValue());
			this.logln("\t Copyright: "+temp.getNodeValue());
		}
		else this.logln("\t ERROR: Copyright not exists");

		if (this.metaInfos.isSectionGraph()){
			//load classPath
			temp = this.getSingleNode(root,preMeta+"class/@path");
			if (temp!=null){
				String path = temp.getNodeValue();
				path = path.substring(0,path.length()-".meta.xml".length());
				metaInfos.setClassPath(path);
				this.logln("\t ELClassPath: "+path);
			}
			else this.logln("\t ERROR: ELClassPath not exists");
			// load className
			temp = this.getSingleNode(root,preMeta+"class/@name");
			if (temp!=null){
				metaInfos.setClassName(temp.getNodeValue());
				this.logln("\t ELClassName: "+temp.getNodeValue());
			}
			else this.logln("\t ERROR: ELClassName not exists");
		}
		
		// category
		if (this.metaInfos.isProblemGraph()){
			temp = this.getSingleNode(root,preMeta+"category/@name");//
			
			if (temp!=null) {//maybe it doesn't exist, because its not an Problem 
				if (MetaInfos.isProblemGraphType(this.metaInfos.getGraphType()))//
				this.graphType = MetaInfos.getGraphTypeAsInt(temp.getNodeValue());//
				this.logln("\t Graphtype: "+temp.getNodeValue());
			}else this.logln("\t ERROR: Graphtype not exists");
		
		
			//Timeframe (only for problems)
			temp = this.getSingleNode(root,preMeta+"timeframe_start/@raw");
			
			if (temp!=null){
				metaInfos.setDateStartCal(MetaInfos.newCalendar(Long.parseLong(temp.getNodeValue())));
				this.logln("\t DateStart: "+temp.getNodeValue());
			}else this.logln("\t DateStart not exists");
			
			
			temp = this.getSingleNode(root,preMeta+"timeframe_end/@raw");
			if (temp!=null){ 
				metaInfos.setDateEndCal(MetaInfos.newCalendar(Long.parseLong(temp.getNodeValue())));
				this.logln("\t DateEnd: "+temp.getNodeValue());
			}else this.logln("\t DateEnd not exists");
			
		}
		//authors
		List auth = this.getAllNodes(root,preMeta+"authors/"+MUMIE+"author/@id");
		if (auth!=null){			
			this.logln("\t Authors:...");
			Iterator it = auth.iterator();
			while (it.hasNext()) {
				temp = (Node) it.next();
				metaInfos.addAuthor(Integer.parseInt(temp.getNodeValue()));
				this.logln("\t \t Author: "+temp.getNodeValue());
			}
			this.logln("\t ... done Authors");
		}else this.logln("\t ERROR: no Authors exists");
		
		this.logln("... done MetaInfos");
	}
	
	/**
	 * sets all Cells for a Graph
	 * @param rootMeta - the root <code>Element</code> of the Metafile
	 * @param rootContent - the root <code>Element </code> of the Contentfile
	 * @throws JaxenException
	 */
	private void setCourseComponents(Element rootMeta, Element rootContent) throws JaxenException{
		
		this.logln("Komponents ...");
		String mainDocType = MainComponentConstants.getComponentType(this.metaInfos.getGraphType());
		this.logln("mainDocType "+mainDocType);
		String subDocType;
		if (mainDocType.equals(MetaInfos.getGraphtypeAsString(MetaInfos.GRAPHTYPE_SECTION)))
			subDocType= SubComponentConstants.getComponentType(SubComponentConstants.DOCTYPE_SUBSECTION);
		else subDocType= SubComponentConstants.getComponentType(SubComponentConstants.DOCTYPE_SUBELEMENT);
		this.logln("subDocType "+subDocType);
		
		List mainComps = this.getAllNodes(rootMeta,"//"+MUMIE+"components//"+MUMIE+mainDocType);//"element");
		List branchOrComps = this.getAllNodes(rootContent,"//"+PREFIX+"net//"+PREFIX+"nodes//"+PREFIX+"or");
		List branchAndComps = this.getAllNodes(rootContent,"//"+PREFIX+"net//"+PREFIX+"nodes//"+PREFIX+"and");
		
		for (int i=0; i<branchOrComps.size();i++){//or branches
			
			Node act = (Node) branchOrComps.get(i);	
			int nid = Integer.parseInt(this.getSingleNode(act,"./@nid").getNodeValue());

			// getting position
			int posx = Integer.parseInt(this.getSingleNode(act,"./@posx").getNodeValue())-CommandConstants.ZWOELF;
			int posy = Integer.parseInt(this.getSingleNode(act,"./@posy").getNodeValue())-CommandConstants.ZWOELF;

			int cat = BranchCellConstants.getCategoryAsInt(BranchCellConstants.ORSYM);

			this.logln("Branch Or with nid "+nid +" Position ("+posx+","+posy+")");
			this.cells.addLast(new CellHolder(CellConstants.CELLTYPE_BRANCH,ComponentCellConstants.PATH_UNDEFINED, cat,nid,null,null,posx,posy,null));

			
		}
		for (int i=0; i<branchAndComps.size();i++){//and Branches
		
			Node act = (Node) branchAndComps.get(i);	
			int nid = Integer.parseInt(this.getSingleNode(act,"./@nid").getNodeValue());
			
			// getting position
			int posx = Integer.parseInt(this.getSingleNode(act,"./@posx").getNodeValue())-CommandConstants.ZWOELF;
			int posy = Integer.parseInt(this.getSingleNode(act,"./@posy").getNodeValue())-CommandConstants.ZWOELF;
			int cat = BranchCellConstants.getCategoryAsInt(BranchCellConstants.ANDSYM);

			this.logln("Branch And with nid "+nid +" Position ("+posx+","+posy+")");
			
			this.cells.addLast(new CellHolder(CellConstants.CELLTYPE_BRANCH,ComponentCellConstants.PATH_UNDEFINED,cat,nid,null,null,posx,posy,null));
		}
		
		for (int i=0; i<mainComps.size();i++){// MainComponentcells
			Node act = (Node) mainComps.get(i);
			this.logln("MainComponentCell:");
			// get the lid and id of the node 
			String lid = this.getSingleNode(act,"./@lid").getNodeValue();
			this.logln("\t lid "+lid);
			String path = this.getSingleNode(act,"./@path").getNodeValue(); 
			this.logln("\t path "+path);
			
			// parsing the nid
			String contentSource = "//" + PREFIX + "net//"+ PREFIX + "nodes//" + PREFIX + mainDocType+"[@lid=\""+lid+"\"]";
			int nid = Integer.parseInt(this.getSingleNode(rootContent,contentSource+"/@nid").getNodeValue());
			this.logln("\t nid "+nid);
			
			// get the label of the node 
			Node labelNode = this.getSingleNode(act,MUMIE+"ref_attribute[@name=\"label\"]/@value");
			String label;
			if (labelNode== null) label = ComponentCellConstants.DEFAULT_LABEL;// maybe label does not exist in DataBase
			else label = labelNode.getNodeValue();
			this.logln("\t label "+label);
			
			int cat = MainComponentConstants.getDefaultCategory(this.graphType);//FIXME well category is not in xmlFile..
			
			if (this.graphType!=MetaInfos.GRAPHTYPE_SECTION){// coursesectione doesnt have a cat, so cat is given by default
				Node catNode = this.getSingleNode(act , MUMIE+"category/@name");
				
				if (catNode==null) catNode = this.getSingleNode(act,"./@category");// FIXME its old!
				if (catNode==null){
					this.logln("\t ERROR category not exists set to "+MainComponentConstants.getCategoryAsString(cat));	
				}else {
					cat = MainComponentConstants.getCategoryAsInt((catNode.getNodeValue()));
					this.logln("\t category "+cat);
				}
			}
			
			// getting position
			int posx = Integer.parseInt(this.getSingleNode(rootContent,contentSource+"/@posx").getNodeValue())-CommandConstants.ZWOELF;
			int posy = Integer.parseInt(this.getSingleNode(rootContent,contentSource+"/@posy").getNodeValue())-CommandConstants.ZWOELF;
			this.logln("\t Position ("+posx+","+posy+")");
			//creating and inserting new MainComponentCell
			
			CellHolder c =new CellHolder(CellConstants.CELLTYPE_MAINCOMPONENT,path,cat,nid,lid,null, posx, posy,label);
			this.cells.addLast(c);
			
			
//			 get the Points iff it is a Problem 
			if (MetaInfos.isProblemGraphType(this.graphType)){
				
				Node pointNode = this.getSingleNode(act,MUMIE+"ref_attribute[@name=\"points\"]/@value");
				
				int points;
				if (pointNode== null){ 
					this.logln("ERROR: points not exists");
					c.setPoints(0);
					c.setHasPoints(false);
				}
				else{
					points = Integer.parseInt(pointNode.getNodeValue());
					this.logln("\t points: "+points);
					c.setHasPoints(true);
					c.setPoints(points);
				}
				
			}
			//getting subs
			List subs  = this.getAllNodes(this.getSingleNode(rootContent,contentSource),PREFIX+subDocType);
			
			CellHolder[] align0 = new CellHolder[subs.size()];
			CellHolder[] align1 = new CellHolder[subs.size()];
			CellHolder[] align2 = new CellHolder[subs.size()];
			CellHolder[] align3 = new CellHolder[subs.size()];// lists to sort subcells by count in xml
			int length0 = 0; 
			int length1 = 0;
			int length2 = 0;
			int length3 = 0;// stores real size of buffer
			
//			insert subcells for this MainCell 
			for (int k=0;k<subs.size();k++){
				Node actSub = (Node) subs.get(k);
				this.logln("\t SubComponentCell: ..");
				// get the lid of the node 
				String lidSub = this.getSingleNode(actSub,"./@lid").getNodeValue();
				this.logln("\t \t lid: "+lidSub);
				String metaSource = "//" + MUMIE + subDocType+"[@lid=\""+lidSub+"\"]";

				String pathSub = this.getSingleNode(this.getSingleNode(rootMeta,metaSource),"./@path").getNodeValue();
				this.logln("\t \t path: "+pathSub);
				Node labelNodeSub = this.getSingleNode(this.getSingleNode(rootMeta,metaSource),MUMIE+"ref_attribute[@name=\"label\"]/@value");
				
				String labelSub;
				if (labelNodeSub== null){
					labelSub = ComponentCellConstants.DEFAULT_LABEL;// maybe label does not exist in DataBase
					this.logln("\t ERROR: label doesn't exists");
				}
				else{
					labelSub = labelNodeSub.getNodeValue();
					this.logln("\t \t label: "+labelSub);
				}
				
				int count = Integer.parseInt(this.getSingleNode(actSub,"./@count").getNodeValue());
				this.logln("\t \t count: "+count);
				int align = SubComponentConstants.getAlignAsInt(this.getSingleNode(actSub,"./@align").getNodeValue());
				this.logln("\t \t align: "+align);
				int catSub = SubComponentConstants.getDefaultCategory(this.graphType,align);

				Node catSubNode = this.getSingleNode(this.getSingleNode(rootMeta, metaSource),MUMIE+"category/@name");
				if (catSubNode==null) catSubNode = this.getSingleNode(this.getSingleNode(rootMeta,metaSource),"./@category");
										//FIXME its for old documents!
				if (catSubNode==null){
					this.logln("\t \t ERROR category not exists set to "+SubComponentConstants.getCategoryAsString(catSub));	
				}else {
					catSub = SubComponentConstants.getCategoryAsInt((catSubNode.getNodeValue()));
					this.logln("\t category "+catSub);
				}
				
				
				
				this.logln("\t ... done SubComponetCell");
				CellHolder sc = new CellHolder(CellConstants.CELLTYPE_SUBCOMPONENT,pathSub,catSub,-1,lidSub,c,-1,-1,labelSub);
//				this.cells.addLast(sc);

				switch (align){// buffer subvertices to order them later
				case 0: align0[count-1]=sc; length0++;break;
				case 1: align1[count-1]=sc; length1++;break;
				case 2: align2[count-1]=sc; length2++;break;
				default:{ align3[count-1]=sc;length3++;}
				}
			}
			
			// well now all subcells are inserted, but maybe not in order!
			LinkedList<CellHolder> newSubList = new LinkedList<CellHolder>();
			
			for (int k=0;k<length0;k++)newSubList.addLast(align0[k]);
			for (int k=0;k<length1;k++)newSubList.addLast(align1[k]);
			for (int k=0;k<length2;k++)newSubList.addLast(align2[k]);
			for (int k=0;k<length3;k++)newSubList.addLast(align3[k]);
			
			c.setSubCells(newSubList);
			this.logln("...done MainComponentCell");
		}
	}


	
    private void setEdges(Element rootContent) throws JaxenException{
    	String redPath = "//" + PREFIX + "thread//"+ PREFIX + "arcs//" + PREFIX+"arc";
    	String path = "//" + PREFIX + "net//"+ PREFIX + "arcs//" + PREFIX+"arc";
    	
    	List edgesRed = this.getAllNodes(rootContent,redPath);
    	List edges = this.getAllNodes(rootContent,path);
   	
    	//all existing edges
		for (int i= 0 ; i<edges.size();i++){
			boolean red = false;
			Node act = (Node)edges.get(i);
			int startNid = Integer.parseInt(this.getSingleNode(act,"./@from").getNodeValue());
			int endNid = Integer.parseInt(this.getSingleNode(act,"./@to").getNodeValue());
		
			List points  = this.getAllNodes(act,PREFIX+"point"); 
			LinkedList<Point> pointList = new LinkedList<Point>();

			//getting all points
			for (int k=0;k<points.size();k++){
				
				 int x = Integer.parseInt(this.getSingleNode(points.get(k),"./@posx").getNodeValue());
				 int y = Integer.parseInt(this.getSingleNode(points.get(k),"./@posy").getNodeValue());
				 
				 pointList.addLast(new Point(x,y));
			}
						
			//proof iff existing edges are also red
			for (int k =0;k<edgesRed.size();k++){
				Node actRed = (Node) edgesRed.get(k);
			
				//same start/endCells?
				if ((Integer.parseInt(this.getSingleNode(actRed,"./@from").getNodeValue())==startNid)){
					if ((Integer.parseInt(this.getSingleNode(actRed,"./@to").getNodeValue())==endNid)){
						List pointsRed  = this.getAllNodes(actRed,PREFIX+"point");
						//same number of points??				
						if (pointsRed.size()==points.size()){
							boolean same = true;
							
							//same points??			
							for (int l=0;l<pointsRed.size();l++){
								if (!((Integer.parseInt(this.getSingleNode(pointsRed.get(l),"./@posx").getNodeValue())==((Point)pointList.get(l)).x)&&
								 (Integer.parseInt(this.getSingleNode(pointsRed.get(l),"./@posy").getNodeValue())==((Point)pointList.get(l)).y)))same = false;
							}
							if (same){
								edgesRed.remove(k);
								red = true;
								k=edgesRed.size();
								break;
							}
						}
					}
				}	
			}
			
			this.edges.addLast(new EdgeHolder(startNid,endNid,pointList,red,true));
			
			
			
		}			
		
		// remaining edges are only red
		for (int i =0;i<edgesRed.size();i++){
			Node act = (Node) edgesRed.get(i);
			
			int startNid = Integer.parseInt(this.getSingleNode(act,"./@from").getNodeValue());
			int endNid = Integer.parseInt(this.getSingleNode(act,"./@to").getNodeValue());
			  
			List points  = this.getAllNodes(act,PREFIX+"point"); 
			LinkedList<Point> pointList = new LinkedList<Point>();
			for (int k=0;k<points.size();k++){
				 
				 int x = Integer.parseInt(this.getSingleNode(points.get(k),"./@posx").getNodeValue());
				 int y = Integer.parseInt(this.getSingleNode(points.get(k),"./@posy").getNodeValue());
				 pointList.addLast(new Point(x,y));
			}
			
			this.edges.addLast(new EdgeHolder(startNid,endNid,pointList,true,false));
			
		}
    }
    
    private void logln(String bab){
    	this.log(bab, true);
    }
    
    /**
     * Method for logging the activities of this class
     * @param bab the <code> String </code> which appears in the Code
     * @param ln
     */
    private void log(String bab,boolean ln){
    	
    	if (this.model.getController().getConfig().getDEBUG())
    	{
    		
    		
    		FileWriter f ;
    		File logFile = new File(LOG);
    		if (logFile.exists()){
    			
    			try{
    				f= new FileWriter(LOG, true);
    			
    			}
    			catch (Exception e){
    				CCController.dialogErrorOccured(
    						"GraphLoader Exception",
    						"<html>Exception<p>"+e+"</html>",
    						JOptionPane.ERROR_MESSAGE);
    				return;
    			}
    		}
    		else {
    			
    			try{
    				f= new FileWriter(LOG);
    			}
    			catch (Exception e){
    				CCController.dialogErrorOccured(
    						"GraphLoader Exception",
    						"<html>Exception<p>"+e+"</html>",
    						JOptionPane.ERROR_MESSAGE);
    				return;
    			}
    		}
    		try{
    			if (ln) f.write(bab+"\n");
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
				
				return;}
		    
    	}
    }
    private void babble(String bab){
    	this.logln(bab);
        	if (this.model.getController().getConfig().getDEBUG_GRAPH_LOADER()&&
        			this.model.getController().getConfig().getDEBUG())
        		System.out.println("GraphLoader: " +bab);
        			
	}

	/**
	 * @return the model
	 */
	public Object getModel() {
		return model;
	}

	/**
	 * @return the cells
	 */
	public LinkedList<CellHolder> getCells() {
		return cells;
	}

	/**
	 * @return the edges
	 */
	public LinkedList<EdgeHolder> getEdges() {
		return edges;
	}
}
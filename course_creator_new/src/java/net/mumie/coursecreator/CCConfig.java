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

package net.mumie.coursecreator;
/**
 * the class stores the configuration<br>
 *  das Configfile ist $HOME/.coursecreator.xml"<br>
 *  jeder Wert hat einen Defaultwert, der gegebenenfalls gesetzt werden kann.<br>
 *  Jeder Wert hat zudem eine set und get-Methode, gegebenenfalls
 *  auch eine toggle-Methode.<br>
 *  nach jeder set und toggle-Methode muss writeconfig
 *	aufgerufen werden, um die aktuellen Daten insConfig zu schreiben! 
 * @author <a href="mailto:vrichter@math.tu-berlin.de>Verena Richter</a>
 */
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.util.Vector;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import net.mumie.coursecreator.gui.ElementMetaPanel;
import net.mumie.coursecreator.xml.GraphXMLizer;
public class CCConfig {
	
	DocumentBuilder builder;	
	Document doc=null;

	///////////////////////////////////////////////
	//defaultValues
	///////////////////////////////////////////////
	private static final int 		DEFAULT_MOVE_STEP = 10; // number of pixel if cells are moves by keyboard
	private static final int 		DEFAULT_GRID_SIZE = 10; // Grid for Graph
	private static final boolean  	DEFAULT_SHOWGRID = false;
	// TreeNavigationFrame
	private static final boolean  	DEFAULT_SHOW_GENERIC = true;
	private static final boolean  	DEFAULT_SHOW_NON_GENERIC = true;
	private static final boolean  	DEFAULT_SHOW_METAINFOPANE = true;
	
	private static final boolean  	DEFAULT_SHOW_EXCEPTIONS = false;
	
	// verzeichnisse
	private static final String 		DEFAULT_STORE_DIR = "";
	private static final String 		DEFAULT_LOAD_DIR = "";
	private static final String 		DEFAULT_CERT_DIR = "";
	private static final String 		DEFAULT_DOC_DIR = "/docs/";
	
	// size
	private static final Dimension	DEFAULT_SIZE_TREE_NAV = new Dimension(460,650);
	private static final Dimension	DEFAULT_SIZE_COURSECREATOR = new Dimension(1000,600);
	// position
	private static final Point	DEFAULT_POSITION_LOGIN = new Point(30,30);
	private static final Point	DEFAULT_POSITION_KEY = new Point(30,30);
	private static final Point	DEFAULT_POSITION_PRESENTATION = new Point(30,30);
	private static final Point	DEFAULT_POSITION_ELEMENTTREESEARCH = new Point(30,30);
	private static final Point	DEFAULT_POSITION_COURSECREATOR = new Point(30,30);
	private static final Point	DEFAULT_POSITION_TREE_NAV = new Point(30,30);
	
	// Shortcuts
	//	dateisystem
	private static final String DEFAULT_KEY_NEW 	= "CTRL + n"; // neue Datei
	private static final String DEFAULT_KEY_OPEN 	= "CTRL + l"; // Datei oeffnen
	private static final String DEFAULT_KEY_SAVE 	= "CTRL + s"; // datei speichern
	
	// graphen manipulieren
	private static final String DEFAULT_KEY_CHECKRED	= "ALT + c"; //check red line
	private static final String DEFAULT_KEY_RED  		= "ALT + r"; // Kante rot togglen
	private static final String DEFAULT_KEY_EXISTS	= "ALT + e"; // Kante schwarz togglen
	private static final String DEFAULT_KEY_GRID		= "ALT + g";// am gitter ausrichten
	private static final String DEFAULT_KEY_CONNECT	= "ALT + v"; // verbinden zweier Elemente
	private static final String DEFAULT_KEY_EDGEDIR	= "ALT + o"; // kantenrichtung aendern
	private static final String DEFAULT_KEY_SWAP		= "ALT + t"; // Tauschen zweier Komponenten
	
	private static final String DEFAULT_KEY_DELETE	= "DELETE"; // loeschen
	
	private static final String DEFAULT_KEY_UP		= "ALT + 8"; // 
	private static final String DEFAULT_KEY_DOWN		= "ALT + 2"; //
	private static final String DEFAULT_KEY_LEFT		= "ALT + 4"; //
	private static final String DEFAULT_KEY_RIGHT		= "ALT + 6"; //
	
	private static final String DEFAULT_KEY_MOVEIN	= "ALT + i"; // for Subelements
	private static final String DEFAULT_KEY_MOVEOUT	= "ALT + a"; // for Subelements
	
	private static final String DEFAULT_KEY_ZOOMIN	= "ALT + x"; // zoomen
	private static final String DEFAULT_KEY_ZOOMOUT	= "ALT + y"; // zoomen
	
	
	private String ICON_PATH;
	private String configFile="";
	private String backupFile="";
	private String localeConfigFile="";
	
	///////////////////////////////////////////////
	//tags im xmlfile
	///////////////////////////////////////////////
	
	
	private static String TAG_ROOT= "ccconfig";
	private static String TAG_MOVE_STEP = "movestep";
	private static String TAG_GRIDSIZE = "gridsize";
	private static String TAG_SHOWGRID = "showgrid";
	
	private static String TAG_SERVER_LIST = "ccserverlist";
	private static String TAG_SERVER = "server";
	
	private static String TAG_STORE_DIR 	= "storedir";
	private static String TAG_LOAD_DIR 	= "loaddir";
	private static String TAG_CERTS_DIR 	= "certdir";
	private static String TAG_DOC_DIR 	= "docdir";
	
	private static String TAG_EXCEPTIONS = "exceptions";
	
	private static String TAG_DEBUG = "debug";
	private static String TAG_DEBUG_LIST = "debuglist";
	
	private static String TAG_DEBUG_CCCONTROLLER="cccontroller";
	private static String TAG_DEBUG_CCCONFIG="ccconfig";
	private static String TAG_DEBUG_CCMODEL="ccmodel";
	private static String TAG_DEBUG_COURSECREATOR="coursecreator";
	
	private static String TAG_DEBUG_NAVGRAPH="navgraph";
	private static String TAG_DEBUG_NAVGRAPH_CONTROLLER="navgraphcontroller";
	private static String TAG_DEBUG_NAVGRAPH_MARQUEE_HANDLER="navgraphmarqueehandler";
	
	private static String TAG_DEBUG_BRANCH="branch";
	private static String TAG_DEBUG_MAINCOMPONENT="maincomponent";
	private static String TAG_DEBUG_SUBCOMPONENT="subcomponent";
	private static String TAG_DEBUG_CCEDGE="ccedge";
	
	private static String TAG_DEBUG_META_INFOS="metainfos";
	private static String TAG_DEBUG_META_INFO_FIELD="metainfofield";
	private static String TAG_DEBUG_META_INFO_FIELD_CONTROLLER="metainfofieldcontroller";
	
	private static String TAG_DEBUG_CONTENT_TREE_MODEL="contenttreemodel";
	private static String TAG_DEBUG_CONTENT_TREE_NAVIGATION="contenttreenavigation";
	private static String TAG_DEBUG_CONTENT_TREE_NODE="contenttreenode";
	private static String TAG_DEBUG_DOCUMENT_ADAPTER="documentadapter";
	
	private static String TAG_DEBUG_GRAPH_LOADER="graphloader";
	
	// search
	private static String TAG_SEARCH_LIST 		= "searchlist";
	private static String TAG_SEARCH	 		= "search";
	private static String TAG_SEARCH_TITLE 	= "searchtitle";
	private static String TAG_SEARCH_CAT 		= "searchcat";
	private static String TAG_SEARCH_CREATED 	= "searchcreated";
	private static String TAG_SEARCH_DESCRIPTION 	= "searchdescription";
	private static String TAG_SEARCH_FILE 		= "searchfile";
	private static String TAG_SEARCH_ID		= "searchid";
	private static String TAG_SEARCH_MODIFIED	= "searchmodified";
	private static String TAG_SEARCH_PURE		= "searchpure";
	private static String TAG_SEARCH_SECTION	= "searchsection";
	private static String TAG_SEARCH_TYP		= "searchtyp";
	
//	dateisystem
	private static String TAG_KEY_LIST 		= "keylist";
	private static String TAG_KEY 				= "key";
	
	private static String TAG_KEY_NEW 			= "new"; // neue Datei
	private static String TAG_KEY_OPEN 		= "open"; // Datei oeffnen
	private static String TAG_KEY_RED  		= "red"; // Kante rot togglen
	private static String TAG_KEY_SAVE 		= "save"; // datei speichern
	
	// graphen manipulieren
	private static String TAG_KEY_CHECKRED		= "checkred"; //check red line
	private static String TAG_KEY_EXISTS		= "exists"; // Kante schwarz togglen
	private static String TAG_KEY_GRID			= "grid";// am gitter ausrichten
	private static String TAG_KEY_CONNECT		= "connect"; // verbinden zweier Elemente
	private static String TAG_KEY_EDGEDIR		= "edgedir"; // kantenrichtung aendern
	private static String TAG_KEY_SWAP			= "swap"; // Tauschen zweier Komponenten
	
	private static String TAG_KEY_DELETE		= "delete"; // loeschen
	
	private static String TAG_KEY_UP			= "up"; // 
	private static String TAG_KEY_DOWN			= "down"; //
	private static String TAG_KEY_LEFT			= "left"; //
	private static String TAG_KEY_RIGHT		= "right"; //
	
	private static String TAG_KEY_MOVEIN		= "movein"; // for Subelements
	private static String TAG_KEY_MOVEOUT		= "moveout"; // for Subelements
	
	private static String TAG_KEY_ZOOMIN		= "zoomin"; // zoomen
	private static String TAG_KEY_ZOOMOUT		= "zoomout"; // zoomen
	
	//	TreeNavigationFrame
	// show generic/nongeneric
	private static String TAG_TREE_NAV_FRAME_SHOW_GENERIC="showgeneric";
	private static String TAG_TREE_NAV_FRAME_SHOW_NON_GENERIC="shownongeneric";
	
	// show Metainfopane
	private static String TAG_TREE_NAV_FRAME_SHOW_METAINFOPANE="showmetainfopane";
	
	// sizes and positions
	private static String TAG_SIZE_LIST = "sizelist";
	private static String TAG_SIZE = "size";
	private static String TAG_SIZE_TREE_NAV = "sizetreenav";
	private static String TAG_SIZE_COURSECREATOR = "sizecoursecreator";

	private static String TAG_POSITION_LIST = "positionlist";
	private static String TAG_POSITION = "position";
	private static String TAG_POSITION_TREE_NAV = "positiontreenav";
	private static String TAG_POSITION_COURSECREATOR = "positioncoursecreator";
	private static String TAG_POSITION_LOGIN = "positionlogin";
	private static String TAG_POSITION_KEY = "positionkey";
	private static String TAG_POSITION_PRESENTATION = "positionpresentation";
	private static String TAG_POSITION_ELEMENTTREESEARCH = "positionelementtreesearch";

	
	///////////////////////////
	//data
	private Vector<String> serverList;
	private int MOVE_STEP;
	private int GRIDSIZE;
	private boolean SHOWGRID;
	
	private boolean SHOW_GENERIC;
	private boolean SHOW_NON_GENERIC;
	
	private boolean SHOW_METAINFOPANE;
	private static boolean SHOW_EXCEPTIONS;
	
	// Groessen und Positionen
	private Dimension SIZE_TREE_NAVIGATION = DEFAULT_SIZE_TREE_NAV;
	private Dimension SIZE_COURSE_CREATOR= DEFAULT_SIZE_COURSECREATOR;

	private Point POSITION_TREE_NAVIGATION = DEFAULT_POSITION_TREE_NAV;
	private Point POSITION_COURSE_CREATOR = DEFAULT_POSITION_COURSECREATOR;

	private Point POSITION_LOGIN = DEFAULT_POSITION_LOGIN;
	private Point POSITION_KEY = DEFAULT_POSITION_KEY;
	private Point POSITION_PRESENTATION = DEFAULT_POSITION_PRESENTATION;
	private Point POSITION_ELEMENTTREESEARCH = DEFAULT_POSITION_ELEMENTTREESEARCH;
	private String	STORE_DIR;
	private String	LOAD_DIR;
	private String	CERT_DIR;// Certificat
	private String	DOC_DIR;// Documentation
	

	
	// default values for debug settings
	private boolean DEBUG								= false;
	
	private boolean DEBUG_CCCONTROLLER 				= false;
	private boolean DEBUG_COURSE_CREATOR 				= false;
	private boolean DEBUG_CCMODEL				 		= false;
	private boolean DEBUG_CCCONFIG 					= false;
	
	private boolean DEBUG_NAVGRAPH 					= false;
	private boolean DEBUG_NAVGRAPH_CONTROLLER 		= false;
	private boolean DEBUG_NAVGRAPH_MARQUEE_HANDLER 	= false;
	
	private boolean DEBUG_MAINCOMPONENT 				= false;
	private boolean DEBUG_SUBCOMPONENT 				= false;
	private boolean DEBUG_BRANCH 						= false;
	private boolean DEBUG_CCEDGE 						= false;

	private boolean DEBUG_META_INFOS 					= false;
	private boolean DEBUG_META_INFO_FIELD 			= false;
	private boolean DEBUG_META_INFO_FIELD_CONTROLLER 	= false;
	
	private boolean DEBUG_CONTENT_TREE_MODEL 			= false;
	private boolean DEBUG_CONTENT_TREE_NAVIGATION 	= false;
	private boolean DEBUG_CONTENT_TREE_NODE 			= false;
	private boolean DEBUG_DOCUMENT_ADAPTER 			= false;
	
	private boolean DEBUG_GRAPH_LOADER 				= false;
	
	//search
	private static String SEARCH_TITLE 	= ElementMetaPanel.IGNORE;
	private static String SEARCH_CAT 		= ElementMetaPanel.IGNORE;
	private static String SEARCH_CREATED 	= ElementMetaPanel.IGNORE;
	private static String SEARCH_DESCRIPTION= ElementMetaPanel.IGNORE;
	private static String SEARCH_FILE 		= ElementMetaPanel.IGNORE;
	private static String SEARCH_ID		= ElementMetaPanel.IGNORE;
	private static String SEARCH_MODIFIED	= ElementMetaPanel.IGNORE;
	private static String SEARCH_PURE		= ElementMetaPanel.IGNORE;
	private static String SEARCH_SECTION	= ElementMetaPanel.IGNORE;
	private static String SEARCH_TYP		= ElementMetaPanel.IGNORE;
	
	//Tastenkuerzel
	private String KEY_NEW 		= DEFAULT_KEY_NEW; // neue Datei
	private String KEY_OPEN 		= DEFAULT_KEY_OPEN; // Datei oeffnen
	private String KEY_RED  		= DEFAULT_KEY_RED; // Kante rot togglen
	private String KEY_SAVE 		= DEFAULT_KEY_SAVE; // datei speichern
	
	// graphen manipulieren
	private String KEY_CHECKRED	= DEFAULT_KEY_CHECKRED; //check red line
	private String KEY_EXISTS		= DEFAULT_KEY_EXISTS; // Kante schwarz togglen
	private String KEY_GRID		= DEFAULT_KEY_GRID;// am gitter ausrichten
	private String KEY_CONNECT		= DEFAULT_KEY_CONNECT; // verbinden zweier Elemente
	private String KEY_EDGEDIR		= DEFAULT_KEY_EDGEDIR; // kantenrichtung aendern
	private String KEY_SWAP		= DEFAULT_KEY_SWAP; // Tauschen zweier Komponenten
	
	private String KEY_DELETE		= DEFAULT_KEY_DELETE; // loeschen
	
	private String KEY_UP			= DEFAULT_KEY_UP; // 
	private String KEY_DOWN		= DEFAULT_KEY_DOWN; //
	private String KEY_LEFT		= DEFAULT_KEY_LEFT; //
	private String KEY_RIGHT		= DEFAULT_KEY_RIGHT; //
	
	private String KEY_MOVEIN		= DEFAULT_KEY_MOVEIN; // for Subelements
	private String KEY_MOVEOUT		= DEFAULT_KEY_MOVEOUT; // for Subelements
	
	private String KEY_ZOOMIN		= DEFAULT_KEY_ZOOMIN; // zoomen
	private String KEY_ZOOMOUT		= DEFAULT_KEY_ZOOMOUT; // zoomen
	
	public CCConfig(CCController c){

		configFile = CourseCreator.MM_BUILD_PREFIX +"/etc/coursecreator/config.xml";
		backupFile = configFile + ".bak";
		
		localeConfigFile = System.getenv("HOME")+"/.coursecreator.xml";
		ICON_PATH = CourseCreator.MM_BUILD_PREFIX +"/share/coursecreator/pics/";
		this.serverList = new Vector<String>();
		File f = new File(configFile);
		
		if (!f.exists()){
			f = new File(backupFile);
			if (!f.exists()){
				CCController.dialogErrorOccured(
						"no valid configuration exists",
								"<html> es existiert keine g\u00fcltige Konfigurationsdatei.<br> Verwende Defaultwerte.",
								JOptionPane.INFORMATION_MESSAGE);
				this.setDefaultValues();
			} else {
				if (!this.readConfig(f)) this.setDefaultValues();// sth went wrong?
			}
		}else{ //copy old file to backup
			
			FileOutputStream fos = null;
			FileInputStream input = null;
			try {
				fos = new FileOutputStream(new File(backupFile));
				input = new FileInputStream(f);
				byte[] buffer = new byte[ 0xFFFF ]; 
			    for ( int len; (len = input.read(buffer)) != -1; ) fos.write( buffer, 0, len ); 
			}
			catch ( IOException e ) { 
			
				CCController.dialogErrorOccured(
						"CCConfig IOException",
						"<html>IOException beim Schreiben der Konfigurationsdatei<p>ERROR: "+e+"</html>", JOptionPane.ERROR_MESSAGE);
				
			      e.printStackTrace(); 
			} 
			finally {
				if ( input != null ) 
					try { input.close(); } 
					catch ( IOException e ) {
						CCController.dialogErrorOccured(
							"CCConfig IOException",
							"<html>IOException beim Schliessen der Konfigurationsdatei<p>ERROR: "+e+"</html>", JOptionPane.ERROR_MESSAGE);
					} 
				if ( fos != null ) 
					try { fos.close(); }
					catch ( IOException e ) { 
						CCController.dialogErrorOccured(
								"CCConfig IOException",
								"<html>IOException beim Schliessen der Konfigurationsdatei<p>ERROR: "+e+"</html>", JOptionPane.ERROR_MESSAGE);
					} 
			}
			
			if (!this.readConfig(f)) this.setDefaultValues();// sth went wrong?
		}
		
		File localf = new File(localeConfigFile);
		
		if (localf.exists()){
			this.readConfig(localf);
		}
	}
	
	/**
	 * reads the configuration
	 * @param f
	 * @return
	 */
	private boolean readConfig(File f){
		babble("Read file "+f);
		InputStream input=null;
		try {
			input = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			
			CCController.dialogErrorOccured(
					"CCConfig FileNotFoundException",
					"<html>FileNotFoundException: " +f+
					"not found<p>ERROR: "+e+"</html>", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		try {			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();			
			factory.setIgnoringComments(true);
			factory.setNamespaceAware(false);			
			builder = factory.newDocumentBuilder();
		}
		catch (Exception e){
			
			CCController.dialogErrorOccured(
					"CCConfig Exception",
					"<html> Exception while accessing DOMBuilder<p>ERROR: "
					+e+"</html>", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		try { 
			doc = builder.parse(input);
		}
		catch (Exception e){
			
			CCController.dialogErrorOccured(
					"CCConfig Exception",
					"<html> an Exception occured while parsing Document<p>ERROR: "
					+e+"</html>", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		Element root = doc.getDocumentElement();

		if (!root.getNodeName().equals(TAG_ROOT)) return false;

//		 movestep
		Node tmp = this.getSingleNode(root, TAG_MOVE_STEP);
		if (tmp!=null) this.MOVE_STEP = Integer.parseInt(tmp.getAttributes().getNamedItem("value").getNodeValue());
		else this.MOVE_STEP = DEFAULT_MOVE_STEP;
		
		tmp = this.getSingleNode(root, TAG_GRIDSIZE);
		
		if (tmp!=null&&tmp.getAttributes()!=null&&tmp.getAttributes().getNamedItem("value")!=null ){ 
			this.GRIDSIZE = Integer.parseInt(tmp.getAttributes().getNamedItem("value").getNodeValue());
		
		}
		else this.GRIDSIZE = DEFAULT_GRID_SIZE;
		
		tmp = this.getSingleNode(root, TAG_SHOWGRID);
		if (tmp!=null&&tmp.getAttributes()!=null&&tmp.getAttributes().getNamedItem("value")!=null ){ 
			this.SHOWGRID = Boolean.parseBoolean(tmp.getAttributes().getNamedItem("value").getNodeValue());
		}
		else this.SHOWGRID = DEFAULT_SHOWGRID;
		
//		 showGeneric
		tmp = this.getSingleNode(root, TAG_TREE_NAV_FRAME_SHOW_GENERIC);
		if (tmp!=null) this.SHOW_GENERIC = 
			Boolean.parseBoolean(tmp.getAttributes().getNamedItem("value").getNodeValue());
		else this.SHOW_GENERIC = DEFAULT_SHOW_GENERIC;
		
		tmp = this.getSingleNode(root, TAG_TREE_NAV_FRAME_SHOW_NON_GENERIC);
		if (tmp!=null) this.SHOW_NON_GENERIC= 
			Boolean.parseBoolean(tmp.getAttributes().getNamedItem("value").getNodeValue());
		else this.SHOW_NON_GENERIC = DEFAULT_SHOW_NON_GENERIC;

//		show MetaInfoPane
		tmp = this.getSingleNode(root, TAG_TREE_NAV_FRAME_SHOW_METAINFOPANE);
		if (tmp!=null) this.SHOW_METAINFOPANE = 
			Boolean.parseBoolean(tmp.getAttributes().getNamedItem("value").getNodeValue());
		else this.SHOW_METAINFOPANE = DEFAULT_SHOW_METAINFOPANE;

		tmp = this.getSingleNode(root, TAG_EXCEPTIONS);
		if (tmp!=null) SHOW_EXCEPTIONS= 
			Boolean.parseBoolean(tmp.getAttributes().getNamedItem("value").getNodeValue());
		else SHOW_EXCEPTIONS = DEFAULT_SHOW_EXCEPTIONS;
		
		//serverList
		tmp = this.getSingleNode(root, TAG_SERVER_LIST);
		if (tmp!=null){
			NodeList children = tmp.getChildNodes();
			for (int i=0;i<children.getLength();i++){
				Node child = children.item(i);
				if(child.getNodeName().equals(TAG_SERVER)){
					String server = child.getAttributes().getNamedItem("url").getNodeValue();
					this.addServer(server,false,false);
				}
			}
		}
		
		//size and position of treenavigationframe
		tmp = this.getSingleNode(root, TAG_SIZE_LIST);
		if (tmp!=null){ 
			NodeList children = tmp.getChildNodes();
			for (int i=0;i<children.getLength();i++){
				Node child = children.item(i);
				if(child.getNodeName().equals(TAG_SIZE)){
					this.setSizeValue(
							child, child.getAttributes().getNamedItem("name").getNodeValue());
				}
			}
		}
		tmp = this.getSingleNode(root, TAG_POSITION_LIST);
		if (tmp!=null){ 
			NodeList children = tmp.getChildNodes();
			for (int i=0;i<children.getLength();i++){
				Node child = children.item(i);
				if(child.getNodeName().equals(TAG_POSITION)){
					this.setPositionValue(
							child, child.getAttributes().getNamedItem("name").getNodeValue());
				}
			}
		}
		
		
		// default store/load dir
		tmp = this.getSingleNode(root, TAG_STORE_DIR);
		if (tmp!=null) this.STORE_DIR = tmp.getAttributes().getNamedItem("value").getNodeValue();
		else this.STORE_DIR = DEFAULT_STORE_DIR;
		
		tmp = this.getSingleNode(root, TAG_LOAD_DIR);
		if (tmp!=null) this.LOAD_DIR = tmp.getAttributes().getNamedItem("value").getNodeValue();
		else this.LOAD_DIR = DEFAULT_LOAD_DIR;
		
//		 Certificate
		tmp = this.getSingleNode(root, TAG_CERTS_DIR);
		if (tmp!=null) this.CERT_DIR = tmp.getAttributes().getNamedItem("value").getNodeValue();
		else this.CERT_DIR = DEFAULT_CERT_DIR;
		
//		 Certificate
		tmp = this.getSingleNode(root, TAG_DOC_DIR);
//		if (tmp!=null) 
		if (tmp!=null&&tmp.getAttributes()!=null&&tmp.getAttributes().getNamedItem("value")!=null )
			this.DOC_DIR = tmp.getAttributes().getNamedItem("value").getNodeValue();
		else this.DOC_DIR = DEFAULT_DOC_DIR;
			
		//debug
		tmp = this.getSingleNode(root, TAG_DEBUG_LIST);
		
		if (tmp!=null)	{
			NodeList debugC = tmp.getChildNodes();
			for (int i=0;i<debugC.getLength();i++){
				Node child = debugC.item(i);
			
				if(child.getNodeName().equals(TAG_DEBUG)){
					this.setDebugValue(child,child.getAttributes().getNamedItem("name").getNodeValue());
				}				
			}
		}else babble("WARNING: no DebugInfos!");
		
//		search
		tmp = this.getSingleNode(root, TAG_SEARCH_LIST);
		
		if (tmp!=null)	{
			NodeList debugC = tmp.getChildNodes();
			for (int i=0;i<debugC.getLength();i++){
				Node child = debugC.item(i);
			
				if(child.getNodeName().equals(TAG_SEARCH)){
					this.setSearchValue(child,child.getAttributes().getNamedItem("name").getNodeValue());
				}				
			}
		}else babble("WARNING: no SearchInfos!");
		
//		tastenkuerzel
		tmp = this.getSingleNode(root, TAG_KEY_LIST);
		
		if (tmp!=null)	{
			NodeList keyC = tmp.getChildNodes();
			for (int i=0;i<keyC.getLength();i++){
				Node child = keyC.item(i);
			
				if(child.getNodeName().equals(TAG_KEY)){
					this.setKeyValue(child,child.getAttributes().getNamedItem("name").getNodeValue());
				}				
			}
		}
		
		return true;
	}
	
	
	/**
	 * sets the windowsize 
	 * @param node
	 * @param name
	 */
	private void setSizeValue(Node node, String name){
		int width = Integer.parseInt(
				(node.getAttributes().getNamedItem("width").getNodeValue()));
		int height = Integer.parseInt
				((node.getAttributes().getNamedItem("height").getNodeValue()));
		
		if (name.equals(TAG_SIZE_TREE_NAV))
			this.SIZE_TREE_NAVIGATION=new Dimension( width,height);
	
		else if (name.equals(TAG_SIZE_COURSECREATOR))
			this.SIZE_COURSE_CREATOR=new Dimension( width,height);
		else 
			CCController.dialogErrorOccured(
				"Unbekanntes Tag",
				"<html> Das Tag<br>" +name+" ist unbekannt",
				JOptionPane.INFORMATION_MESSAGE);

	}
	
	/**
	 * sets positions
	 * @param node
	 * @param tag
	 */
	private void setPositionValue(Node node, String tag){
		int x = Integer.parseInt(
				(node.getAttributes().getNamedItem("x").getNodeValue()));
		int y = Integer.parseInt
				((node.getAttributes().getNamedItem("y").getNodeValue()));
		if (tag.equals(TAG_POSITION_TREE_NAV))
			this.POSITION_TREE_NAVIGATION=new Point(x,y);
	
		else if (tag.equals(TAG_POSITION_COURSECREATOR))
			this.POSITION_COURSE_CREATOR=new Point(x,y);
		
		else if (tag.equals(TAG_POSITION_LOGIN))
			this.POSITION_LOGIN=new Point(x,y);

		else if (tag.equals(TAG_POSITION_KEY))
			this.POSITION_KEY=new Point(x,y);
		else if (tag.equals(TAG_POSITION_PRESENTATION))
			this.POSITION_PRESENTATION=new Point(x,y);
		else if (tag.equals(TAG_POSITION_ELEMENTTREESEARCH))
			this.POSITION_ELEMENTTREESEARCH=new Point(x,y);
		else 		CCController.dialogErrorOccured(
				"Unbekanntes Tag",
				"<html> Das Tag<br>" +tag+" ist unbekannt",
				JOptionPane.INFORMATION_MESSAGE);

}

	/**
	 * sets keyvalue
	 * @param node
	 * @param name
	 */
	private void setKeyValue(Node node, String name){
		String key = (node.getAttributes().getNamedItem("value").getNodeValue());
		
		if (name.equals(TAG_KEY_CHECKRED)) 		this.KEY_CHECKRED=key;
		else if (name.equals(TAG_KEY_CONNECT)) this.KEY_CONNECT=key;
		else if (name.equals(TAG_KEY_DELETE)) 	this.KEY_DELETE=key;
		else if (name.equals(TAG_KEY_DOWN)) 	this.KEY_DOWN=key;
		else if (name.equals(TAG_KEY_EDGEDIR)) this.KEY_EDGEDIR=key;
		else if (name.equals(TAG_KEY_EXISTS)) 	this.KEY_EXISTS=key;
		else if (name.equals(TAG_KEY_GRID)) 	this.KEY_GRID=key;
		else if (name.equals(TAG_KEY_LEFT)) 	this.KEY_LEFT=key;
		else if (name.equals(TAG_KEY_MOVEIN)) 	this.KEY_MOVEIN=key;
		else if (name.equals(TAG_KEY_MOVEOUT)) this.KEY_MOVEOUT=key;
		else if (name.equals(TAG_KEY_NEW)) 		this.KEY_NEW=key;
		else if (name.equals(TAG_KEY_OPEN)) 	this.KEY_OPEN=key;
		else if (name.equals(TAG_KEY_RED)) 		this.KEY_RED=key;
		else if (name.equals(TAG_KEY_RIGHT)) 	this.KEY_RIGHT=key;
		else if (name.equals(TAG_KEY_SAVE)) 	this.KEY_SAVE=key;
		else if (name.equals(TAG_KEY_SWAP)) 	this.KEY_SWAP=key;
		else if (name.equals(TAG_KEY_UP)) 		this.KEY_UP=key;
		else if (name.equals(TAG_KEY_ZOOMIN)) 	this.KEY_ZOOMIN=key;
		else if (name.equals(TAG_KEY_ZOOMOUT)) this.KEY_ZOOMOUT=key;
		
		else 		CCController.dialogErrorOccured(
				"Unbekanntes Tag",
				"<html> Das Tag<br>" +name+" ist unbekannt",
				JOptionPane.INFORMATION_MESSAGE);

	}

	/**
	 * sets debugvalue
	 * @param node
	 * @param name
	 */
	private void setDebugValue(Node node, String name){
		boolean value = Boolean.parseBoolean((node.getAttributes().getNamedItem("value").getNodeValue()));
		babble("name "+name +" value "+value);
		
		if (name.equals(TAG_DEBUG)) this.DEBUG=value;
		
		else if (name.equals(TAG_DEBUG_CCCONFIG))this.DEBUG_CCCONFIG=value;
		else if (name.equals(TAG_DEBUG_CCCONTROLLER))this.DEBUG_CCCONTROLLER=value;
		else if (name.equals(TAG_DEBUG_CCMODEL))this.DEBUG_CCMODEL=value;
		else if (name.equals(TAG_DEBUG_COURSECREATOR)) this.DEBUG_COURSE_CREATOR=value;
		
		else if (name.equals(TAG_DEBUG_NAVGRAPH)) this.DEBUG_NAVGRAPH=value;
		else if (name.equals(TAG_DEBUG_NAVGRAPH_CONTROLLER)) this.DEBUG_NAVGRAPH_CONTROLLER=value;
		else if (name.equals(TAG_DEBUG_NAVGRAPH_MARQUEE_HANDLER)) this.DEBUG_NAVGRAPH_MARQUEE_HANDLER=value;
		
		else if (name.equals(TAG_DEBUG_MAINCOMPONENT)) this.DEBUG_MAINCOMPONENT=value;
		else if (name.equals(TAG_DEBUG_SUBCOMPONENT)) this.DEBUG_SUBCOMPONENT=value;
		else if (name.equals(TAG_DEBUG_CCEDGE))this.DEBUG_CCEDGE=value;
		else if (name.equals(TAG_DEBUG_BRANCH))this.DEBUG_BRANCH=value;
		
		else if (name.equals(TAG_DEBUG_META_INFO_FIELD)) this.DEBUG_META_INFO_FIELD=value;
		else if (name.equals(TAG_DEBUG_META_INFO_FIELD_CONTROLLER)) this.DEBUG_META_INFO_FIELD_CONTROLLER=value;
		else if (name.equals(TAG_DEBUG_META_INFOS)) this.DEBUG_META_INFOS=value;
		
		else if (name.equals(TAG_DEBUG_CONTENT_TREE_MODEL)) this.DEBUG_CONTENT_TREE_MODEL=value;
		else if (name.equals(TAG_DEBUG_CONTENT_TREE_NAVIGATION))this.DEBUG_CONTENT_TREE_NAVIGATION=value;
		else if (name.equals(TAG_DEBUG_CONTENT_TREE_NODE))this.DEBUG_CONTENT_TREE_NODE=value;
		else if (name.equals(TAG_DEBUG_DOCUMENT_ADAPTER)) this.DEBUG_DOCUMENT_ADAPTER=value;

		else if (name.equals(TAG_DEBUG_GRAPH_LOADER)) this.DEBUG_GRAPH_LOADER=value;
		else 
			CCController.dialogErrorOccured(
					"Unbekanntes Tag",
					"<html> Das Tag<br>" +name+" ist unbekannt",
					JOptionPane.INFORMATION_MESSAGE);
	
	}

	/**
	 * sets value for search window
	 * @param node
	 * @param name
	 */
	private void setSearchValue(Node node, String name){
		String v = (node.getAttributes().getNamedItem("value").getNodeValue());
		
		if (name.equals(TAG_SEARCH_TITLE))			SEARCH_TITLE=v;
		else if (name.equals(TAG_SEARCH_CAT))		SEARCH_CAT=v;
		else if (name.equals(TAG_SEARCH_CREATED))	SEARCH_CREATED=v;
		else if (name.equals(TAG_SEARCH_DESCRIPTION))	SEARCH_DESCRIPTION=v;
		else if (name.equals(TAG_SEARCH_FILE))		SEARCH_FILE=v;
		else if (name.equals(TAG_SEARCH_ID))		SEARCH_ID=v;
		else if (name.equals(TAG_SEARCH_MODIFIED))	SEARCH_MODIFIED=v;
		else if (name.equals(TAG_SEARCH_PURE))		SEARCH_PURE=v;
		else if (name.equals(TAG_SEARCH_SECTION))	SEARCH_SECTION=v;
		else if (name.equals(TAG_SEARCH_TYP))		SEARCH_TYP=v;
		else CCController.dialogErrorOccured(
				"Unbekanntes Tag",
				"<html> Das Tag<br>" +name+" ist unbekannt",
				JOptionPane.INFORMATION_MESSAGE);

	}
	/**
	 * sets all defaultValues if they have defaults..
	 */
	private void setDefaultValues(){
		
		this.MOVE_STEP = DEFAULT_MOVE_STEP;
		this.GRIDSIZE = DEFAULT_GRID_SIZE;
		this.SHOWGRID = DEFAULT_SHOWGRID;
		this.SHOW_GENERIC = DEFAULT_SHOW_GENERIC;
		this.SHOW_NON_GENERIC = DEFAULT_SHOW_NON_GENERIC;
		this.SHOW_METAINFOPANE = DEFAULT_SHOW_METAINFOPANE;
		SHOW_EXCEPTIONS = DEFAULT_SHOW_EXCEPTIONS;
		
		this.serverList = new Vector<String>();
		
		// stores
		this.STORE_DIR = DEFAULT_STORE_DIR;
		this.LOAD_DIR = DEFAULT_LOAD_DIR;
		this.CERT_DIR = DEFAULT_CERT_DIR;
		this.DOC_DIR = DEFAULT_DOC_DIR;
		

		// sizes and positions
		this.SIZE_TREE_NAVIGATION = DEFAULT_SIZE_TREE_NAV;
		this.SIZE_COURSE_CREATOR = DEFAULT_SIZE_COURSECREATOR;
		this.POSITION_TREE_NAVIGATION = DEFAULT_POSITION_TREE_NAV;
		this.POSITION_COURSE_CREATOR = DEFAULT_POSITION_COURSECREATOR;
		this.POSITION_KEY = DEFAULT_POSITION_KEY;
		this.POSITION_LOGIN = DEFAULT_POSITION_LOGIN;
		this.POSITION_PRESENTATION = DEFAULT_POSITION_PRESENTATION;
		this.POSITION_ELEMENTTREESEARCH = DEFAULT_POSITION_ELEMENTTREESEARCH;
		
//		Tastenkuerzel
		this.KEY_CHECKRED 	= DEFAULT_KEY_CHECKRED;
		this.KEY_CONNECT 	= DEFAULT_KEY_CONNECT;
		this.KEY_DELETE 	= DEFAULT_KEY_DELETE;
		this.KEY_DOWN 		= DEFAULT_KEY_DOWN;
		this.KEY_EDGEDIR 	= DEFAULT_KEY_EDGEDIR;
		this.KEY_EXISTS		= DEFAULT_KEY_EXISTS;
		this.KEY_GRID 		= DEFAULT_KEY_GRID;
		this.KEY_LEFT 		= DEFAULT_KEY_LEFT;
		this.KEY_MOVEIN 	= DEFAULT_KEY_MOVEIN;
		this.KEY_MOVEOUT 	= DEFAULT_KEY_MOVEOUT;
		this.KEY_NEW 		= DEFAULT_KEY_NEW;
		this.KEY_OPEN 		= DEFAULT_KEY_OPEN;
		this.KEY_RED		= DEFAULT_KEY_RED;
		this.KEY_RIGHT		= DEFAULT_KEY_RIGHT;
		this.KEY_SAVE		= DEFAULT_KEY_SAVE;
		this.KEY_SWAP		= DEFAULT_KEY_SWAP;
		this.KEY_UP			= DEFAULT_KEY_UP;
		this.KEY_ZOOMIN		= DEFAULT_KEY_ZOOMIN;
		this.KEY_ZOOMOUT	= DEFAULT_KEY_ZOOMOUT;
				
	}
	/**
	 * sets defaultvalues for the searchframe<br>
	 * (the defaultbutton needs it)
	 */
	public void setDefaultSearchValues(){
		SEARCH_TITLE 	= ElementMetaPanel.AND;
		SEARCH_CAT 		= ElementMetaPanel.IGNORE;
		SEARCH_CREATED 	= ElementMetaPanel.IGNORE;
		SEARCH_DESCRIPTION= ElementMetaPanel.IGNORE;
		SEARCH_FILE 	= ElementMetaPanel.IGNORE;
		SEARCH_ID		= ElementMetaPanel.IGNORE;
		SEARCH_MODIFIED	= ElementMetaPanel.IGNORE;
		SEARCH_PURE		= ElementMetaPanel.IGNORE;
		SEARCH_SECTION	= ElementMetaPanel.IGNORE;
		SEARCH_TYP		= ElementMetaPanel.IGNORE;
	}
	
	/**
	 * sets default keys (the defaultbutton needs it)
	 *
	 */
	public void setDefaultKeys(){
		this.KEY_CHECKRED 	= DEFAULT_KEY_CHECKRED;
		this.KEY_CONNECT 	= DEFAULT_KEY_CONNECT;
		this.KEY_DELETE 	= DEFAULT_KEY_DELETE;
		this.KEY_DOWN 		= DEFAULT_KEY_DOWN;
		this.KEY_EDGEDIR 	= DEFAULT_KEY_EDGEDIR;
		this.KEY_EXISTS		= DEFAULT_KEY_EXISTS;
		this.KEY_GRID 		= DEFAULT_KEY_GRID;
		this.KEY_LEFT 		= DEFAULT_KEY_LEFT;
		this.KEY_MOVEIN 	= DEFAULT_KEY_MOVEIN;
		this.KEY_MOVEOUT 	= DEFAULT_KEY_MOVEOUT;
		this.KEY_NEW 		= DEFAULT_KEY_NEW;
		this.KEY_OPEN 		= DEFAULT_KEY_OPEN;
		this.KEY_RED		= DEFAULT_KEY_RED;
		this.KEY_RIGHT		= DEFAULT_KEY_RIGHT;
		this.KEY_SAVE		= DEFAULT_KEY_SAVE;
		this.KEY_SWAP		= DEFAULT_KEY_SWAP;
		this.KEY_UP			= DEFAULT_KEY_UP;
		this.KEY_ZOOMIN		= DEFAULT_KEY_ZOOMIN;
		this.KEY_ZOOMOUT	= DEFAULT_KEY_ZOOMOUT;
	}
	/**
	 * returns a Node with specified name under root
	 * @param root
	 * @param name
	 * @return
	 */
	private Node getSingleNode(Element root,String name){
		NodeList children = root.getChildNodes();
		
		for (int i=0;i<children.getLength();i++){
			Node child = children.item(i);
			
			if (child.getNodeName().equals(name))return child;
		}
		return null;
			
	}
	
	/** loescht server mit namen s, falls dieser vorhandn */
	public void removeServer(String s){
		if (this.serverList.contains(s)){
			this.serverList.remove(s);
		}
		this.writeConfig(true);
	}
	
	/**
	 * adds an Server to config
	 * writes also to localConfig file, 
	 * but this function is also called when the programm starts,
	 * so then not to save is importent
	 * @param s
	 */
	public void addServer(String s, boolean writeLocalConfig, boolean addFirst){
		babble ("add server "+s);
		if (this.serverList.contains(s)){ 
			for (int i=0;i<serverList.size();i++){
				if (serverList.get(i).toString().equals(s)){
					this.serverList.remove(s);
				}
			}
		}
		
		if (addFirst) this.serverList.add(0,s);
		else this.serverList.add(s);

		if (writeLocalConfig)this.writeConfig(true);
	}
	
	/**
	 * write the configfile
	 * @param local
	 */
	public void writeConfig(boolean local){
		
		File f = null;
		if (local) f=new File(this.localeConfigFile);
		else f=new File(this.configFile);
		
		Document doc  = this.buildDocument();
		if (this.doc == null) return;
    		
		//rootElement des XMLFiles
		Element root = doc.createElement(TAG_ROOT);
    	doc.appendChild(root);
    	
    	//darstellung
//    	if (this.MOVE_STEP!=DEFAULT_MOVE_STEP){
    		Element moveStep = doc.createElement(TAG_MOVE_STEP);
    		moveStep.setAttribute("value", String.valueOf(this.MOVE_STEP));
    		root.appendChild(moveStep);
//    	}
    		Element gridsize = doc.createElement(TAG_GRIDSIZE);
    		gridsize.setAttribute("value", String.valueOf(this.GRIDSIZE));
    		root.appendChild(gridsize);
    		
    		Element showgrid = doc.createElement(TAG_SHOWGRID);
    		showgrid.setAttribute("value", String.valueOf(this.SHOWGRID));
    		root.appendChild(showgrid);
    	
    	//showGeneric
//    	if (this.SHOW_GENERIC!=DEFAULT_SHOW_GENERIC){
    		Element showGeneric = doc.createElement(TAG_TREE_NAV_FRAME_SHOW_GENERIC);
    		showGeneric.setAttribute("value", String.valueOf(this.SHOW_GENERIC));
    		root.appendChild(showGeneric);
//    	}
    	
//    	if (this.SHOW_NON_GENERIC!=DEFAULT_SHOW_NON_GENERIC){
    		Element showNonGeneric = doc.createElement(TAG_TREE_NAV_FRAME_SHOW_NON_GENERIC);
    		showNonGeneric.setAttribute("value", String.valueOf(this.SHOW_NON_GENERIC));
    		root.appendChild(showNonGeneric);
//    	}

        	//showMetaInfoPane
        		Element showMetaInfoPane = doc.createElement(TAG_TREE_NAV_FRAME_SHOW_METAINFOPANE);
        		showMetaInfoPane.setAttribute("value", String.valueOf(this.SHOW_METAINFOPANE));
        		root.appendChild(showMetaInfoPane);
//        	}

        		Element showException = doc.createElement(TAG_EXCEPTIONS);
        		showException.setAttribute("value", String.valueOf(SHOW_EXCEPTIONS));
        		root.appendChild(showException);

    		
    	//serverlist
    	Element serverListxml = doc.createElement(TAG_SERVER_LIST);
    	for (int i=0;i<this.serverList.size();i++){
    		Element serverxml = doc.createElement(TAG_SERVER);
    		serverxml.setAttribute("url", this.serverList.get(i).toString());
    		serverListxml.appendChild(serverxml);
    		babble("server In file "+ this.serverList.get(i));
    	}
    	root.appendChild(serverListxml);
    	
    	//store directory
    	Element storeDir = doc.createElement(TAG_STORE_DIR);
    	storeDir.setAttribute("value", this.STORE_DIR);
    	root.appendChild(storeDir);
		
    	Element loadDir = doc.createElement(TAG_LOAD_DIR);
    	loadDir.setAttribute("value", this.LOAD_DIR);
    	root.appendChild(loadDir);

    	//certificate
    	Element certDir = doc.createElement(TAG_CERTS_DIR);
    	certDir.setAttribute("value", this.CERT_DIR);
    	root.appendChild(certDir);
    	

    	//documentation
    	Element docDir = doc.createElement(TAG_DOC_DIR);
    	docDir.setAttribute("value", this.DOC_DIR);
    	root.appendChild(docDir);
    	
    	//sizes
    	Element sizeList = doc.createElement(TAG_SIZE_LIST);
    	this.createSizeEntry(TAG_SIZE_TREE_NAV, this.getSIZE_TREE_NAVIGATION(), sizeList);
    	
    	this.createSizeEntry(TAG_SIZE_COURSECREATOR, this.getSIZE_COURSE_CREATOR(), sizeList);
    	root.appendChild(sizeList);
    	
    	// positions
    	root.appendChild(createPositionList(doc));
    	
    	//debugeinstellungen
   		root.appendChild(createDebugList(doc));
   		
   		//searchList
   		root.appendChild(createSearchList(doc));
    	
//    	keyEinstellungen
		root.appendChild( createKeyList(doc));
    	String xml;
    	try {
			xml = GraphXMLizer.toXMLString(doc);
			} catch (UnsupportedEncodingException e) {
				CCController.dialogErrorOccured(
						"CCConfig UnsupportedEncodingException",
						"<html>UnsupportedEncodingException<p>ERROR: " +
						e+"</html>", JOptionPane.ERROR_MESSAGE);
				
				return;
			} catch (TransformerException e) {
				CCController.dialogErrorOccured(
						"CCConfig TransformerException",
						"<html>TransformerException<p>ERROR: " +
						e+"</html>", JOptionPane.ERROR_MESSAGE);
				
				return;
			}
			try {
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(xml.getBytes());
				
			} catch (FileNotFoundException e) {
				CCController.dialogErrorOccured(
						"CCConfig FileNotFoundException",
						"<html>FileNotFoundException<p>ERROR: " +
						e+"</html>", JOptionPane.ERROR_MESSAGE);
			
				return;
			} catch (IOException e) {
				CCController.dialogErrorOccured(
						"CCConfig IOException",
						"<html>IOException<p>ERROR: " +
						e+"</html>", JOptionPane.ERROR_MESSAGE);

				return;
			}

	}

	/**
	 * erstellt positionElement fuer xml
	 * @param doc
	 * @return
	 */
	private Element createPositionList(Document doc) {
		Element positionList = doc.createElement(TAG_POSITION_LIST);
    	this.createPositionEntry(
    			TAG_POSITION_TREE_NAV, this.getPOSITION_TREE_NAVIGATION(), positionList);
    	
    	this.createPositionEntry(
    			TAG_POSITION_COURSECREATOR, this.getPOSITION_COURSE_CREATOR(), positionList);

    	this.createPositionEntry(
    			TAG_POSITION_LOGIN, this.getPOSITION_LOGIN(), positionList);

    	this.createPositionEntry(
    			TAG_POSITION_KEY, this.getPOSITION_KEY(), positionList);
    	
    	this.createPositionEntry(
    			TAG_POSITION_PRESENTATION, this.getPOSITION_PRESENTATION(), positionList);
    	this.createPositionEntry(
    			TAG_POSITION_ELEMENTTREESEARCH, this.getPOSITION_ELEMENTTREESEARCH(), positionList);
		return positionList;
	}

	/**
	 * erstellt keyElement fuer xml
	 * @param doc
	 * @return
	 */
	private Element createKeyList(Document doc) {
		Element keyList = doc.createElement(TAG_KEY_LIST);
    	
    	this.createKeyEntry(TAG_KEY_CHECKRED, 	this.getKey_checkRed(),	keyList);
    	this.createKeyEntry(TAG_KEY_CONNECT, 	this.getKey_connect(),	keyList);
    	this.createKeyEntry(TAG_KEY_DELETE, 	this.getKey_delete(), 	keyList);
    	this.createKeyEntry(TAG_KEY_DOWN, 		this.getKey_down(),		keyList);
    	this.createKeyEntry(TAG_KEY_EDGEDIR, 	this.getKey_edgeDir(), 	keyList);
		this.createKeyEntry(TAG_KEY_EXISTS, 	this.getKey_exists(), 	keyList);
		this.createKeyEntry(TAG_KEY_GRID, 		this.getKey_grid(), 	keyList);
		this.createKeyEntry(TAG_KEY_LEFT, 		this.getKey_left(), 	keyList);
		this.createKeyEntry(TAG_KEY_MOVEIN, 	this.getKey_movein(),	keyList);
		this.createKeyEntry(TAG_KEY_MOVEOUT, 	this.getKey_moveout(),	keyList);
		this.createKeyEntry(TAG_KEY_NEW, 		this.getKey_new(), 		keyList);
		this.createKeyEntry(TAG_KEY_OPEN, 		this.getKey_open(), 	keyList);
		this.createKeyEntry(TAG_KEY_RED, 		this.getKey_red(), 		keyList);
		this.createKeyEntry(TAG_KEY_RIGHT, 		this.getKey_right(), 	keyList);
		this.createKeyEntry(TAG_KEY_SAVE, 		this.getKey_save(), 	keyList);
		this.createKeyEntry(TAG_KEY_SWAP, 		this.getKey_swap(),	 	keyList);
		this.createKeyEntry(TAG_KEY_UP, 		this.getKey_up(), 		keyList);
		this.createKeyEntry(TAG_KEY_ZOOMIN, 	this.getKey_zoomin(), 	keyList);
		this.createKeyEntry(TAG_KEY_ZOOMOUT, 	this.getKey_zoomout(), 	keyList);
		return keyList;
	}

	/**
	 * @param doc
	 * @return
	 */
	private Element createDebugList(Document doc) {
		Element debugList = doc.createElement(TAG_DEBUG_LIST);
    	
    	this.createDebugEntry(TAG_DEBUG, this.getDEBUG(), debugList);
    	
		this.createDebugEntry(TAG_DEBUG_CCCONTROLLER, this.getDEBUG_CCCONTROLLER(), debugList);
		this.createDebugEntry(TAG_DEBUG_CCCONFIG, this.getDEBUG_CCCONFIG(), debugList);
		this.createDebugEntry(TAG_DEBUG_CCMODEL, this.getDEBUG_CCMODEL(), debugList);
		this.createDebugEntry(TAG_DEBUG_COURSECREATOR, this.getDEBUG_COURSE_CREATOR(), debugList);
    		
		this.createDebugEntry(TAG_DEBUG_NAVGRAPH, this.getDEBUG_NAVGRAPH(), debugList);
		this.createDebugEntry(TAG_DEBUG_NAVGRAPH_CONTROLLER, this.getDEBUG_NAVGRAPH_CONTROLLER(), debugList);
		this.createDebugEntry(TAG_DEBUG_NAVGRAPH_MARQUEE_HANDLER, this.getDEBUG_NAVGRAPH_MARQUEE_HANDLER(), debugList);
		
		this.createDebugEntry(TAG_DEBUG_MAINCOMPONENT, this.getDEBUG_MAINCOMPONENT(), debugList);
		this.createDebugEntry(TAG_DEBUG_SUBCOMPONENT, this.getDEBUG_SUBCOMPONENT(), debugList);
		this.createDebugEntry(TAG_DEBUG_BRANCH, this.getDEBUG_BRANCH(), debugList);
		this.createDebugEntry(TAG_DEBUG_CCEDGE, this.getDEBUG_CCEDGE(), debugList);
		
		this.createDebugEntry(TAG_DEBUG_META_INFOS, this.getDEBUG_META_INFOS(), debugList);
		this.createDebugEntry(TAG_DEBUG_META_INFO_FIELD, this.getDEBUG_META_INFO_FIELD(), debugList);
		this.createDebugEntry(TAG_DEBUG_META_INFO_FIELD_CONTROLLER, this.getDEBUG_META_INFO_FIELD_CONTROLLER(), debugList);
		
		this.createDebugEntry(TAG_DEBUG_CONTENT_TREE_MODEL, this.getDEBUG_CONTENT_TREE_MODEL(), debugList);
		this.createDebugEntry(TAG_DEBUG_CONTENT_TREE_NAVIGATION, this.getDEBUG_CONTENT_TREE_NAVIGATION(), debugList);
		this.createDebugEntry(TAG_DEBUG_CONTENT_TREE_NODE, this.getDEBUG_CONTENT_TREE_NODE(), debugList);
		this.createDebugEntry(TAG_DEBUG_DOCUMENT_ADAPTER, this.getDEBUG_DOCUMENT_ADAPTER(), debugList);
		this.createDebugEntry(TAG_DEBUG_GRAPH_LOADER, this.getDEBUG_GRAPH_LOADER(), debugList);
		return debugList;
	}	
	
	/**
	 * creates an entry for the given name and value like
	 * <code><debug name="cccontroller" value="true"/> </code>
	 * and appends this to parent
	 * @param name
	 * @param value
	 * @param parent
	 * @return
	 */
	private Element createDebugEntry(String name, boolean value,Element parent){
		Element debug = doc.createElement(TAG_DEBUG);
		debug.setAttribute("name",name);
		debug.setAttribute("value",String.valueOf(value));
		parent.appendChild(debug);
		return debug;
	}
	
	/**
	 * erstellt searchElement fuer xml
	 * @param doc
	 * @return
	 */
	private Element createSearchList(Document doc) {
		Element searchList = doc.createElement(TAG_SEARCH_LIST);
    	
    	this.createSearchEntry(TAG_SEARCH_CAT, this.getSEARCH_CAT(), searchList);
    	this.createSearchEntry(TAG_SEARCH_CREATED, this.getSEARCH_CREATED(), searchList);
    	this.createSearchEntry(TAG_SEARCH_DESCRIPTION, this.getSEARCH_DESCRIPTION(), searchList);
    	this.createSearchEntry(TAG_SEARCH_FILE, this.getSEARCH_FILE(), searchList);
    	this.createSearchEntry(TAG_SEARCH_ID, this.getSEARCH_ID(), searchList);
    	this.createSearchEntry(TAG_SEARCH_MODIFIED, this.getSEARCH_MODIFIED(), searchList);
    	this.createSearchEntry(TAG_SEARCH_PURE, this.getSEARCH_PURE(), searchList);
    	this.createSearchEntry(TAG_SEARCH_SECTION, this.getSEARCH_SECTION(), searchList);
    	this.createSearchEntry(TAG_SEARCH_TITLE, this.getSEARCH_TITLE(), searchList);
    	this.createSearchEntry(TAG_SEARCH_TYP, this.getSEARCH_TYP(), searchList);
    	
		return searchList;
	}
	
	/**
	 * creates an entry for the given name and value like
	 * <code><debug name="cccontroller" value="true"/> </code>
	 * and appends this to parent
	 * @param name
	 * @param value
	 * @param parent
	 * @return
	 */
	private Element createSearchEntry(String name, String value,Element parent){
		Element search = doc.createElement(TAG_SEARCH);
		search.setAttribute("name",name);
		search.setAttribute("value",String.valueOf(value));
		parent.appendChild(search);
		return search;
	}
	
	/**
	 * creates an entry for the given name and value like
	 * <code><key name="new" value="n"/> </code>
	 * and appends this to parent
	 * @param name
	 * @param value
	 * @param parent
	 * @return
	 */
	private Element createKeyEntry(String name, String value, Element parent){
		Element key = doc.createElement(TAG_KEY);
		key.setAttribute("name",name);
		key.setAttribute("value",String.valueOf(value));
		parent.appendChild(key);
		return key;
	}
	
	/**
	 * creates an entry for the given name and value like
	 * <code><key name="new" value="n"/> </code>
	 * and appends this to parent
	 * @param name
	 * @param value
	 * @param parent
	 * @return
	 */
	private Element createSizeEntry(String name, Dimension value,Element parent){
		Element entry = doc.createElement(TAG_SIZE);
		entry.setAttribute("name",name);
		entry.setAttribute("height",String.valueOf(value.height));
		entry.setAttribute("width",String.valueOf(value.width));
		parent.appendChild(entry);
		return entry;
	}
	
	/**
	 * creates an entry for the given name and value like
	 * <code><key name="new" value="n"/> </code>
	 * and appends this to parent
	 * @param name
	 * @param value
	 * @param parent
	 * @return
	 */
	private Element createPositionEntry(String name, Point value,Element parent){
		Element entry = doc.createElement(TAG_POSITION);
		entry.setAttribute("name",name);
		entry.setAttribute("x",String.valueOf(value.x));
		entry.setAttribute("y",String.valueOf(value.y));
		parent.appendChild(entry);
		return entry;
	}
	
	/**
	 * builds the document
	 * @return
	 */
	 private Document buildDocument(){
			try {			
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();			
				factory.setIgnoringComments(true);
				factory.setNamespaceAware(false);			
				builder = factory.newDocumentBuilder();
			}
			catch (Exception e){
				CCController.dialogErrorOccured(
						"Exception",
						"<html> CCConfig: Exception while accessing DOMBuilder: <br>"+e,
						JOptionPane.ERROR_MESSAGE);
				return null;
			}
		 this.doc = builder.newDocument();
		 
		 return this.doc;
	 }
	
	 
    /**
     * Method for debugging-outputs
     * @param bab
     */
    void babble(String bab){
		if (this.getDEBUG_CCCONFIG() && this.getDEBUG())
			System.out.println("CCConfig: "+bab);
	}

	public int getMOVE_STEP() {return MOVE_STEP;}
	public void setMOVE_STEP(int move_step) {MOVE_STEP = move_step;this.writeConfig(true);}

	public int getGRIDSIZE() { return GRIDSIZE;}
	public void setGRIDSIZE(int gs) {GRIDSIZE = gs;this.writeConfig(true);}
	
	public boolean getSHOWGRID() {return SHOWGRID;}
	public void setSHOWGRID(boolean gs) {SHOWGRID = gs;this.writeConfig(true);}

	public Vector<String> getServerList() {return serverList;}
	public void setServerList(Vector<String> serverList) {this.serverList = serverList;this.writeConfig(true);}

	public String getICON_PATH() {return ICON_PATH;}

//	DEBUG 
	public boolean getDEBUG() {return DEBUG;}
	public void setDEBUG(boolean debug) {DEBUG = debug;this.writeConfig(true);}
	public void toggleDEBUG(){ this.DEBUG=!this.DEBUG;this.writeConfig(true);}
	
//	DEBUG_CCCONTROLLER
	public boolean getDEBUG_CCCONTROLLER() {return DEBUG_CCCONTROLLER;}
	public void setDEBUG_CCCONTROLLER(boolean debug) {DEBUG_CCCONTROLLER = debug;this.writeConfig(true);}
	public void toggleDEBUG_CCCONTROLLER(){ this.DEBUG_CCCONTROLLER=!this.DEBUG_CCCONTROLLER;this.writeConfig(true);}
	
//	DEBUG_COURSE_CREATOR
	public boolean getDEBUG_COURSE_CREATOR() {return DEBUG_COURSE_CREATOR;}
	public void setDEBUG_COURSE_CREATOR(boolean debug) {DEBUG_COURSE_CREATOR = debug;this.writeConfig(true);}
	public void toggleDEBUG_COURSE_CREATOR(){this.DEBUG_COURSE_CREATOR=!this.DEBUG_COURSE_CREATOR;this.writeConfig(true);}
	
//	DEBUG_CCCONFIG
	public boolean getDEBUG_CCCONFIG() {return this.DEBUG_CCCONFIG;}
	public void setDEBUG_CCCONFIG(boolean debug) {this.DEBUG_CCCONFIG = debug;this.writeConfig(true);}
	public void toggleDEBUG_CCCONFIG(){this.DEBUG_CCCONFIG=!this.DEBUG_CCCONFIG;this.writeConfig(true);}

//	DEBUG_CCMODEL
	public boolean getDEBUG_CCMODEL() {return this.DEBUG_CCMODEL;}
	public void setDEBUG_CCMODEL(boolean debug) {this.DEBUG_CCMODEL = debug;this.writeConfig(true);}
	public void toggleDEBUG_CCMODEL(){this.DEBUG_CCMODEL=!this.DEBUG_CCMODEL;this.writeConfig(true);}
	
//	 DEBUG_NAVGRAPH;

	public boolean getDEBUG_NAVGRAPH() {return this.DEBUG_NAVGRAPH;}
	public void setDEBUG_NAVGRAPH(boolean debug) {this.DEBUG_NAVGRAPH = debug;this.writeConfig(true);}
	public void toggleDEBUG_NAVGRAPH(){this.DEBUG_NAVGRAPH=!this.DEBUG_NAVGRAPH;this.writeConfig(true);}
	
//	 DEBUG_NAVGRAPH_CONTROLLER;
	public boolean getDEBUG_NAVGRAPH_CONTROLLER() {return this.DEBUG_NAVGRAPH_CONTROLLER;}
	public void setDEBUG_NAVGRAPH_CONTROLLER(boolean debug) {this.DEBUG_NAVGRAPH_CONTROLLER = debug;this.writeConfig(true);}
	public void toggleDEBUG_NAVGRAPH_CONTROLLER(){this.DEBUG_NAVGRAPH_CONTROLLER=!this.DEBUG_NAVGRAPH_CONTROLLER;this.writeConfig(true);}
	
//	 DEBUG_NAVGRAPH_MARQUEE_HANDLER;
	public boolean getDEBUG_NAVGRAPH_MARQUEE_HANDLER() {return this.DEBUG_NAVGRAPH_MARQUEE_HANDLER;}
	public void setDEBUG_NAVGRAPH_MARQUEE_HANDLER(boolean debug) {this.DEBUG_NAVGRAPH_MARQUEE_HANDLER = debug;this.writeConfig(true);}
	public void toggleDEBUG_NAVGRAPH_MARQUEE_HANDLER(){this.DEBUG_NAVGRAPH_MARQUEE_HANDLER=!this.DEBUG_NAVGRAPH_MARQUEE_HANDLER;this.writeConfig(true);}
	
//	
//	 DEBUG_MAINCOMPONENT;
	public boolean getDEBUG_MAINCOMPONENT() {return this.DEBUG_MAINCOMPONENT;}
	public void setDEBUG_MAINCOMPONENT(boolean debug) {this.DEBUG_MAINCOMPONENT = debug;this.writeConfig(true);}
	public void toggleDEBUG_MAINCOMPONENT(){this.DEBUG_MAINCOMPONENT=!this.DEBUG_MAINCOMPONENT;this.writeConfig(true);}
	
//	 DEBUG_SUBCOMPONENT;
	public boolean getDEBUG_SUBCOMPONENT() {return this.DEBUG_SUBCOMPONENT;}
	public void setDEBUG_SUBCOMPONENT(boolean debug) {this.DEBUG_SUBCOMPONENT = debug;this.writeConfig(true);}
	public void toggleDEBUG_SUBCOMPONENT(){this.DEBUG_SUBCOMPONENT=!this.DEBUG_SUBCOMPONENT;this.writeConfig(true);}
	
//	 DEBUG_BRANCH;
	public boolean getDEBUG_BRANCH() {return this.DEBUG_BRANCH;}
	public void setDEBUG_BRANCH(boolean debug) {this.DEBUG_BRANCH = debug;this.writeConfig(true);}
	public void toggleDEBUG_BRANCH(){this.DEBUG_BRANCH=!this.DEBUG_BRANCH;this.writeConfig(true);}
	
//	 DEBUG_CCEDGE;
	public boolean getDEBUG_CCEDGE() {return this.DEBUG_CCEDGE;}
	public void setDEBUG_CCEDGE(boolean debug) {this.DEBUG_CCEDGE = debug;this.writeConfig(true);}
	public void toggleDEBUG_CCEDGE(){this.DEBUG_CCEDGE=!this.DEBUG_CCEDGE;this.writeConfig(true);}
	
//	 DEBUG_META_INFOS;
	public boolean getDEBUG_META_INFOS() {return this.DEBUG_META_INFOS;}
	public void setDEBUG_META_INFOS(boolean debug) {this.DEBUG_META_INFOS = debug;this.writeConfig(true);}
	public void toggleDEBUG_META_INFOS(){this.DEBUG_META_INFOS=!this.DEBUG_META_INFOS;this.writeConfig(true);}
	
//	 DEBUG_META_INFO_FIELD;
	public boolean getDEBUG_META_INFO_FIELD() {return this.DEBUG_META_INFO_FIELD;}
	public void setDEBUG_META_INFO_FIELD(boolean debug) {this.DEBUG_META_INFO_FIELD = debug;this.writeConfig(true);}
	public void toggleDEBUG_META_INFO_FIELD(){this.DEBUG_META_INFO_FIELD=!this.DEBUG_META_INFO_FIELD;this.writeConfig(true);}
	
//	 DEBUG_META_INFO_FIELD_CONTROLLER;
	public boolean getDEBUG_META_INFO_FIELD_CONTROLLER() {return this.DEBUG_META_INFO_FIELD_CONTROLLER;}
	public void setDEBUG_META_INFO_FIELD_CONTROLLER(boolean debug) {this.DEBUG_META_INFO_FIELD_CONTROLLER = debug;this.writeConfig(true);}
	public void toggleDEBUG_META_INFO_FIELD_CONTROLLER(){this.DEBUG_META_INFO_FIELD_CONTROLLER=!this.DEBUG_META_INFO_FIELD_CONTROLLER;this.writeConfig(true);}
	
//	 DEBUG_CONTENT_TREE_MODEL;
	public boolean getDEBUG_CONTENT_TREE_MODEL() {return this.DEBUG_CONTENT_TREE_MODEL;}
	public void setDEBUG_CONTENT_TREE_MODEL(boolean debug) {this.DEBUG_CONTENT_TREE_MODEL = debug;this.writeConfig(true);}
	public void toggleDEBUG_CONTENT_TREE_MODEL(){this.DEBUG_CONTENT_TREE_MODEL=!this.DEBUG_CONTENT_TREE_MODEL;this.writeConfig(true);}
	
//	 DEBUG_CONTENT_TREE_NAVIGATION;
	public boolean getDEBUG_CONTENT_TREE_NAVIGATION() {return this.DEBUG_CONTENT_TREE_NAVIGATION;}
	public void setDEBUG_CONTENT_TREE_NAVIGATION(boolean debug) {this.DEBUG_CONTENT_TREE_NAVIGATION = debug;this.writeConfig(true);}
	public void toggleDEBUG_CONTENT_TREE_NAVIGATION(){this.DEBUG_CONTENT_TREE_NAVIGATION=!this.DEBUG_CONTENT_TREE_NAVIGATION;this.writeConfig(true);}
	
//	 DEBUG_CONTENT_TREE_NODE;
	public boolean getDEBUG_CONTENT_TREE_NODE() {return this.DEBUG_CONTENT_TREE_NODE;}
	public void setDEBUG_CONTENT_TREE_NODE(boolean debug) {this.DEBUG_CONTENT_TREE_NODE = debug;this.writeConfig(true);}
	public void toggleDEBUG_CONTENT_TREE_NODE(){this.DEBUG_CONTENT_TREE_NODE=!this.DEBUG_CONTENT_TREE_NODE;this.writeConfig(true);}
	
//	 DEBUG_DOCUMENT_ADAPTER;
	public boolean getDEBUG_DOCUMENT_ADAPTER() {return this.DEBUG_DOCUMENT_ADAPTER;}
	public void setDEBUG_DOCUMENT_ADAPTER(boolean debug) {this.DEBUG_DOCUMENT_ADAPTER = debug;this.writeConfig(true);}
	public void toggleDEBUG_DOCUMENT_ADAPTER(){this.DEBUG_DOCUMENT_ADAPTER=!this.DEBUG_DOCUMENT_ADAPTER;this.writeConfig(true);}
	
//	 DEBUG_GRAPH_LOADER;
	public boolean getDEBUG_GRAPH_LOADER() {return this.DEBUG_GRAPH_LOADER;}
	public void setDEBUG_GRAPH_LOADER(boolean debug) {this.DEBUG_GRAPH_LOADER = debug;this.writeConfig(true);}
	public void toggleDEBUG_GRAPH_LOADER(){this.DEBUG_GRAPH_LOADER=!this.DEBUG_GRAPH_LOADER;this.writeConfig(true);}

	//showGeneric
	public boolean getSHOW_GENERIC() {return SHOW_GENERIC;}
	public void setSHOW_GENERIC(boolean b) {SHOW_GENERIC = b; this.writeConfig(true);}
	public boolean getSHOW_NON_GENERIC() {return SHOW_NON_GENERIC;}
	public void setSHOW_NON_GENERIC(boolean s) {SHOW_NON_GENERIC = s;this.writeConfig(true);}
	
//	showMetaInfoPane
	public boolean getSHOW_METAINFOPANE() {return SHOW_METAINFOPANE;}
	public void setSHOW_METAINFOPANE(boolean b) {SHOW_METAINFOPANE = b; this.writeConfig(true);}

//	showException
	public static boolean getSHOW_EXCEPTION() {return SHOW_EXCEPTIONS;}
	public void setSHOW_EXCEPTION(boolean b) {SHOW_EXCEPTIONS= b; this.writeConfig(true);}
	public void toggleShow_EXCEPTION(){ SHOW_EXCEPTIONS=!SHOW_EXCEPTIONS;}

	
	//load/store dir
	public String getLOAD_DIR() { return LOAD_DIR;}
	public void setLOAD_DIR(String load_dir) {LOAD_DIR = load_dir;this.writeConfig(true);}

	public String getSTORE_DIR() {return STORE_DIR;}
	public void setSTORE_DIR(String store_dir) {STORE_DIR = store_dir;this.writeConfig(true);}

//	certificate
	public String getCERT_DIR() { return CERT_DIR;}
	public void setCERT_DIR(String dir) {CERT_DIR = dir;this.writeConfig(true);}

//	documentation
	public String getDOC_DIR() { return DOC_DIR;}
	public void setDOC_DIR(String dir) {DOC_DIR = dir;this.writeConfig(true);}

//sizes and positions
//	 treeNav
	public Dimension getSIZE_TREE_NAVIGATION() {return SIZE_TREE_NAVIGATION;}
	public void setSIZE_TREE_NAVIGATION(Dimension size_tree_navigation) {
		SIZE_TREE_NAVIGATION = size_tree_navigation;
		this.writeConfig(true);
	}
	//CourseCreator
	public Dimension getSIZE_COURSE_CREATOR() {return SIZE_COURSE_CREATOR;}
	public void setSIZE_COURSE_CREATOR(Dimension d) {
		SIZE_COURSE_CREATOR = d;
		this.writeConfig(true);
	}
	
//	 treeNav
	public Point getPOSITION_TREE_NAVIGATION() {return POSITION_TREE_NAVIGATION;}
	public void setPOSITION_TREE_NAVIGATION(Point p) {
		POSITION_TREE_NAVIGATION = p;
		this.writeConfig(true);
	}
	//CourseCreator
	public Point getPOSITION_COURSE_CREATOR() {return POSITION_COURSE_CREATOR;}
	public void setPOSITION_COURSE_CREATOR(Point p) {
		POSITION_COURSE_CREATOR = p;
		this.writeConfig(true);
	}
	/** login
	 * @return POSITION_LOGIN
	 */
	public Point getPOSITION_LOGIN() {return POSITION_LOGIN;}
	public void setPOSITION_LOGIN(Point p) {
		POSITION_LOGIN = p;
		this.writeConfig(true);
	}
	/** @return POSITION_KEY */
	public Point getPOSITION_KEY() {return POSITION_KEY;}
	
	/** @param POSITION_KEY the POSITION_KEY to set */
	public void setPOSITION_KEY(Point p) {
		POSITION_KEY = p;
		this.writeConfig(true);
	}
	/** @return POSITION_ELEMENTTREESEARCH */
	public Point getPOSITION_ELEMENTTREESEARCH() {return POSITION_ELEMENTTREESEARCH;}
	
	/** @param POSITION_ELEMENTTREESEARCH the POSITION_ELEMENTTREESEARCH to set */
	public void setPOSITION_ELEMENTTREESEARCH(Point p) {
		POSITION_ELEMENTTREESEARCH = p;
		this.writeConfig(true);
	}
	
	/** @return POSITION_PRESENTATION */
	public Point getPOSITION_PRESENTATION() {return POSITION_PRESENTATION;}
	
	/** @param POSITION_PRESENTATION the POSITION_PRESENTATION to set */
	public void setPOSITION_PRESENTATION(Point p) {
		POSITION_PRESENTATION = p;
		this.writeConfig(true);
	}
	
	
	/** @return the key_checkRed */
	public String getKey_checkRed() { return KEY_CHECKRED;}
	
	/** @param key_checkRed the key_checkRed to set */
	public void setKey_checkRed(String key) { this.KEY_CHECKRED = key; this.writeConfig(true);}
	
	/** @return the key_delete */
	public String getKey_delete() { return KEY_DELETE;}
	
	/** @param key the key_delete to set */
	public void setKey_delete(String key) { this.KEY_DELETE = key; this.writeConfig(true);}
	
	/** @return the key_down */
	public String getKey_down() {return KEY_DOWN;}
	
	/** @param key the key_down to set */
	public void setKey_down(String key) {this.KEY_DOWN = key; this.writeConfig(true);}
	
	/** @return the key_edgeDir */ 
	public String getKey_edgeDir() {return KEY_EDGEDIR;}
	
	/** @param key the key_edgeDir to set */
	public void setKey_edgeDir(String key) { this.KEY_EDGEDIR = key; this.writeConfig(true);}
	
	/** @return the key_exists	 */
	public String getKey_exists() {return KEY_EXISTS;	}
	
	/** @param key the key_exists to set */
	public void setKey_exists(String key) {this.KEY_EXISTS = key; this.writeConfig(true);}
	
	/** @return the key_grid */
	public String getKey_grid() {return KEY_GRID;}
	
	/** @param key the key_grid to set */
	public void setKey_grid(String key) {this.KEY_GRID = key;	this.writeConfig(true);}
	
	/** @return the key_left */
	public String getKey_left() {return KEY_LEFT;	}
	
	/** @param key_left the key_left to set */
	public void setKey_left(String key) {this.KEY_LEFT = key; this.writeConfig(true);}
	
	/** @return the key_movein */
	public String getKey_movein() {return KEY_MOVEIN;}
	
	/** @param key the key_movein to set */
	public void setKey_movein(String key) {this.KEY_MOVEIN = key; this.writeConfig(true);}
	
	/** @return the key_moveout */
	public String getKey_moveout() {return KEY_MOVEOUT;}
	
	/** @param key the key_moveout to set */
	public void setKey_moveout(String key) {this.KEY_MOVEOUT = key; this.writeConfig(true);}
	
	/** @return the key_new */
	public String getKey_new() {return KEY_NEW;}
	
	/** @param key the key_new to set */
	public void setKey_new(String key) {this.KEY_NEW = key; this.writeConfig(true);}
	
	/** @return the key_open */
	public String getKey_open() {return KEY_OPEN;}
	
	/** @param key the key_open to set */
	public void setKey_open(String key) {this.KEY_OPEN = key; this.writeConfig(true);}
	
	/** @return the key_red */
	public String getKey_red() {return KEY_RED;}
	
	/** @param key the key_red to set */
	public void setKey_red(String key) {this.KEY_RED = key; this.writeConfig(true);}
	
	/** @return the key_right */
	public String getKey_right() {return KEY_RIGHT;}
	
	/** @param key the key_right to set */
	public void setKey_right(String key) {this.KEY_RIGHT = key; this.writeConfig(true);}
	
	/** @return the key_save */
	public String getKey_save() {return KEY_SAVE;}
	
	/** @param key the key_save to set */
	public void setKey_save(String key) {this.KEY_SAVE = key; this.writeConfig(true);}
	
	/** @return the key_swap */
	public String getKey_swap() {return KEY_SWAP;}
	
	/** @param key the key_swap to set */
	public void setKey_swap(String key) {this.KEY_SWAP = key; this.writeConfig(true);}
	
	/** @return the key_up */
	public String getKey_up() {return KEY_UP;}
	
	/** @param key the key_up to set */
	public void setKey_up(String key) {this.KEY_UP = key; this.writeConfig(true);}
	
	/** @return the key_verbinden */
	public String getKey_connect() {return KEY_CONNECT;}
	
	/** @param key the key_verbinden to set*/
	public void setKey_connect(String key) {this.KEY_CONNECT = key; this.writeConfig(true);}
	
	/** @return the key_zoomin */
	public String getKey_zoomin() {return KEY_ZOOMIN;}
	
	/** @param key the key_zoomin to set */
	public void setKey_zoomin(String key) {this.KEY_ZOOMIN = key; this.writeConfig(true);}
	
	/** @return the key_zoomout */
	public String getKey_zoomout() {return KEY_ZOOMOUT;}
	
	/** @param key the key_zoomout to set */
	public void setKey_zoomout(String key) {this.KEY_ZOOMOUT = key; this.writeConfig(true);}

	/** @return the sEARCH_CAT */
	public String getSEARCH_CAT() {return SEARCH_CAT;}

	/** @param search_cat the sEARCH_CAT to set */
	public void setSEARCH_CAT(String search_cat) { SEARCH_CAT = search_cat; this.writeConfig(true);}

	/** @return the sEARCH_CREATED*/
	public String getSEARCH_CREATED() {return SEARCH_CREATED;	}

	 /** @param search_created the sEARCH_CREATED to set*/
	 public void setSEARCH_CREATED(String search_created) { SEARCH_CREATED = search_created; this.writeConfig(true);}
	
	 /** @return the sEARCH_CREATED*/
	public String getSEARCH_DESCRIPTION() {return SEARCH_DESCRIPTION;	}

	/** @param search_created the sEARCH_CREATED to set*/
	public void setSEARCH_DESCRIPTION(String s) { SEARCH_DESCRIPTION = s; this.writeConfig(true);}
		
	 /** @return the sEARCH_FILE */
	 public String getSEARCH_FILE() { return SEARCH_FILE; }
	
	 /** @param search_file the sEARCH_FILE to set */
	 public void setSEARCH_FILE(String search_file) { SEARCH_FILE = search_file; this.writeConfig(true);}
	
	 /** @return the sEARCH_ID */
	 public String getSEARCH_ID() { return SEARCH_ID; }
	
	 /** @param search_id the sEARCH_ID to set */
	 public void setSEARCH_ID(String search_id) { SEARCH_ID = search_id; this.writeConfig(true);}
	
	 /** @return the sEARCH_MODIFIED */
	 public String getSEARCH_MODIFIED() { return SEARCH_MODIFIED; }
	
	 /** @param search_modified the sEARCH_MODIFIED to set */
	 public void setSEARCH_MODIFIED(String search_modified) { SEARCH_MODIFIED = search_modified; this.writeConfig(true);}
	
	 /** @return the sEARCH_PURE */
	 public String getSEARCH_PURE() { return SEARCH_PURE; }
	
	 /** @param search_pure the sEARCH_PURE to set */
	 public void setSEARCH_PURE(String search_pure) { SEARCH_PURE = search_pure; this.writeConfig(true);}
	
	 /** @return the sEARCH_SECTION */
	 public String getSEARCH_SECTION() { return SEARCH_SECTION; }
	
	 /** @param search_section the sEARCH_SECTION to set */
	 public void setSEARCH_SECTION(String search_section) { SEARCH_SECTION = search_section; this.writeConfig(true);}
	
	 /** @return the sEARCH_TITLE */
	 public String getSEARCH_TITLE() { return SEARCH_TITLE; }
	
	 /** @param search_title the sEARCH_TITLE to set */
	 public void setSEARCH_TITLE(String search_title) { SEARCH_TITLE = search_title; this.writeConfig(true);}
	
	 /** @return the sEARCH_TYP */
	 public String getSEARCH_TYP() { return SEARCH_TYP; }
	
	 /** @param search_typ the sEARCH_TYP to set */
	 public void setSEARCH_TYP(String search_typ) { SEARCH_TYP = search_typ; this.writeConfig(true);}
}
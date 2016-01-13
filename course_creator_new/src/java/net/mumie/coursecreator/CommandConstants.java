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
 * Klasse mit konstanten, die fuer Actionlistener benoetigt werden.
 */
import java.awt.Color;

public class CommandConstants {
	
	/**
	 *  menu commands
	 */
	public static final String NEW_NAVGRAPH = "NEW_NAVGRAPH";
	public static final String NEW_FLAT = "NEW_FLAT";
	public static final String NEW_GRAPH ="NEW_GRAPH"; //erzeugt erst Abfrage, welcher Graphtyp neu erstellt werden soll.
	public static final String SAVE = "SAVE";
	public static final String SAVEAS = "SAVEAS";
	public static final String LOAD = "LOAD";
	public static final String SAVECHECKIN = "SAVECHECKIN";
	public static final String REMOVE_GRAPH = "REMOVE_GRAPH";
	public static final String QUIT = "QUIT";
	public static final String SUMMARY = "SUMMARY";
	public static final String SHOW_SUMMARY = "SHOW_SUMMARY";
	public static final String CHECK_REDLINE = "CHECK_REDLINE";
	public static final String DEL_REDLINE = "DEL_REDLINE";
	public static final String UNDO = "UNDO";
	public static final String NEW_COURSE = "NEW_COURSE";
	public static final String NEW_COURSESECTION = "NEW_COURSESECTION";
	public static final String NEW_PROBLEM = "NEW_PROBLEM";
	
	// check graph
	public static final String SHOW_SOURCES = "SHOW_SOURCES";
	public static final String SHOW_DRAINS = "SHOW_DRAINS";
	public static final String TEST_FOR_CIRCLES = "TEST_FOR_CIRCLES";
	
	public static final String SHOW_ELEMENTSELECTOR = "SHOW_ELEMENTSELECTOR";
	public static final String SHOW_ARROWS = "SHOW_ARROWS";
	public static final String SHOW_ALIAS = "SHOW_ALIAS";
	public static final String SHOW_FILESOURCE = "SHOW_FILESOURCE";
	public static final String SET_MOVESTEP = "SET_MOEVESTEP";
	public static final String SET_GRIDSIZE = "SET_GRIDSIZE";
	public static final String SHOW_GRID = "SHOW_GRID";
	public static final String SHOW_HELP = "SHOW_HELP";
	public static final String SHOW_ABOUT = "SHOW_ABOUT";
	
	public static final String SWITCH_PAINT_ORDER = "SWITCH_PAINT_ORDER";
	
	//Einstellungen
	public static final String LOGIN = "LOGIN";
	public static final String ASSIGN_CERTS = "ASSIGN_CERTS";
	public static final String SHOW_DEBUG_DIALOG = "SHOW_DEBUG_DIALOG";
	public static final String SHOW_KEYSET_DIALOG = "SHOW_KEYSET_DIALOG";
	public static final String SHOW_PRESENTATION_DIALOG = "SHOW_PRESENTATION_DIALOG";
	public static final String SHOW_ADMIN_DIALOG = "SHOW_ADMIN_DIALOG";
	
	
	//treeNavigationFrame-Commands
	public static final String TREEVIEW_SET = "TREEVIEW_SET";
	public static final String TREEVIEW_SEARCH = "TREEVIEW_SEARCH";
	public static final String TREEVIEW_RELOAD = "TREEVIEW_RELOAD";
	public static final String TREEVIEW_CANCEL = "TREEVIEW_CANCEL";
	public static final String TREEVIEW_PREVIEW = "TREEVIEW_PREVIEW";
	public static final String TREEVIEW_METAINFOS = "TREEVIEW_METAINFOS";
	public static final String TREEVIEW_SHOW_GENERIC = "TREEVIEW_SHOW_GENERIC";
	public static final String TREEVIEW_SHOW_NON_GENERIC = "TREEVIEW_SHOW_NON_GENERIC";
	public static final String TREEVIEW_SHOW_METAINFOPANE = "TREEVIEW_SHOW_METAINFOPANE";
	
	
//	buffermenu
	public static final String GRAPH = "GRAPH";
	

	// insert a cell
	public static final String ADD_CELL = "ADDCELL"; 
	
//	toolbar cmds	
	public static final String SETSTART = "SETSTART";
	public static final String CONNECT = "CONNECT";
	public static final String SWAP = "SWAP";	
	public static final String TOGGLERED = "TOGGLERED";
	public static final String TOGGLEEXISTS = "TOGGLEEXISTS";
	public static final String DELETE = "DELETE";
	public static final String ZOOM_IN = "ZOOM_IN";
	public static final String ZOOM_OUT = "ZOOM_OUT";
	public static final String NAVUP = "NAVUP";
	public static final String NAVDOWN = "NAVDOWN";
	public static final String CHANGE_EDGE_DIRECTION = "CHANGE_EDGE_DIRECTION";
	public static final String ALIGN = "ALIGN";
	public static final String BUFFERREFRESH = "BUFFERREFRESH";
	public static final String UPDATE_METAINFODATE = "UPDATE_METAINFODATE";
	
	//commands for Radiobuttons in metaInfofield
	public static final String META_INFO_FIELD_ST_ = "META_INFO_FIELD_ST_";
	public static final String META_INFO_FIELD_CAT_ = "META_INFO_FIELD_CAT_";
	public static final String META_INFO_FIELD_CLASS = "META_INFO_FIELD_CLASS";
	public static final String META_INFO_FIELD_OK = "META_INFO_FIELD_OK";
	public static final String META_INFO_FIELD_CANCEL = "META_INFO_FIELD_CANCEL";
	
	
	//keys
	public static final String KEYPRESSED_MOVEOUT = "KEYPRESSED_MOVEOUT";
	public static final String KEYPRESSED_CHECKRED = "KEYPRESSED_CHECKRED";
	public static final String KEYPRESSED_EXISTS = "KEYPRESSED_EXISTS";
	public static final String KEYPRESSED_GRID = "KEYPRESSED_GRID";
	public static final String KEYPRESSED_MOVEIN = "KEYPRESSED_MOVEIN";
	public static final String KEYPRESSED_OPEN = "KEYPRESSED_OPEN";
	public static final String KEYPRESSED_NEW = "KEYPRESSED_NEW";
	public static final String KEYPRESSED_EDGEDIR = "KEYPRESSED_EDGEDIR";
	public static final String KEYPRESSED_RED = "KEYPRESSED_RED";
	public static final String KEYPRESSED_SAVE = "KEYPRESSED_SAVE";
	public static final String KEYPRESSED_CONNECT = "KEYPRESSED_CONNECT";
	public static final String KEYPRESSED_SWAP = "KEYPRESSED_SWAP";
	public static final String KEYPRESSED_DELETE = "KEYPRESSED_DELETE";
  	public static final String KEYPRESSED_ZOOMIN = "KEYPRESSED_ZOOMIN";
	public static final String KEYPRESSED_ZOOMOUT = "KEYPRESSED_ZOOMOUT";
	public static final String KEYPRESSED_UP = "KEYPRESSED_UP";
	public static final String KEYPRESSED_DOWN = "KEYPRESSED_DOWN";
	public static final String KEYPRESSED_LEFT = "KEYPRESSED_LEFT";
	public static final String KEYPRESSED_RIGHT = "KEYPRESSED_RIGHT";
	
//	 move cells by keyboard
	public static final String MOVE_UP = "MOVE_UP"; 
	public static final String MOVE_DOWN = "MOVE_DOWN";
	public static final String MOVE_LEFT = "MOVE_LEFT";
	public static final String MOVE_RIGHT = "MOVE_RIGHT";
	
//	 to move subcells 
	public static final String MOVE_SUBCELL_IN = "MOVE_SUBCELL_IN";
	public static final String MOVE_SUBCELL_OUT = "MOVE_SUBCELL_OUT";
	
	
	public static final int UP 		= 0;
	public static final int DOWN 	= 1;
	public static final int LEFT 	= 2;
	public static final int RIGHT 	= 3;
	
	//some other staff
	public static final int SNAP_SIZE = 1;
	public static final boolean PAINT_FIRSTNLAST = false; 
	public static final int EDGE_ARROW_WIDTH=5;
	public static final int EDGE_ARROW_HEIGHT=4;
	public static final boolean EDGE_ARROW_DRAW_VERTICAL=false;
	
	public static final int ZWOELF=12;
	
	// used in DebugFrame to set debug values
	public static final String D_EXCEPTION					= "D_EXCEPTION";
	public static final String D_DEBUG 						= "D_DEBUG";
	public static final String D_COURSE_CREATOR 				= "D_COURSE_CREATOR";
	public static final String D_CCCONTROLLER				= "D_CCCONTROLLER";
	public static final String D_CCCONFIG					= "D_CCCONFIG";
	public static final String D_CCMODEL						= "D_CCMODEL";
	
	public static final String D_NAVGRAPH 					= "D_NAVGRAPH";
	public static final String D_NAVGRAPH_CONTROLLER			= "D_NAVGRAPH_CONTROLLER";
	public static final String D_NAV_MARQUE_HANDLER			= "D_NAV_MARQUE_HANDLER";
	
	public static final String D_MAINCOMPONENT				= "D_MAINCOMPONENT";
	public static final String D_SUBCOMPONENT				= "D_SUBCOMPONENT";

	public static final String D_BRANCH						= "D_BRANCH";
	public static final String D_CCEDGE						= "D_CCEDGE";
	
	public static final String D_META_INFOS					= "D_META_INFOS";
	public static final String D_META_INFO_FIELD				= "D_META_INFO_FIELD";
	public static final String D_META_INFO_FIELD_CONTROLLER	= "D_META_INFO_FIELD_CONTROLLER";
	
	public static final String D_GRAPH_LOADER				= "D_GRAPH_LOADER";
	public static final String D_CONTENT_TREE_MODEL			= "D_CONTENT_TREE_MODEL";
	public static final String D_CONTENT_TREE_NODE			= "D_CONTENT_TREE_NODE";
	public static final String D_CONTENT_TREE_NAVIGATION		= "D_CONTENT_TREE_NAVIGATION";
	public static final String D_DOCUMENT_ADAPTER 			= "D_DOCUMENT_ADAPTER";
     
	
	// keystrings
	public static final String stringEmpty = "- leer -";
	
	public static final String stringCTRL = "CTRL";
	public static final String stringALT = "ALT";
	public static final String stringMETA = "META";
	public static final String stringSHIFT = "SHIFT";
	
	public static final String stringENTER = "ENTER";
	public static final String stringBACKSPACE = "BACK-SPACE";
	public static final String stringESC = "ESC";
	public static final String stringSPACE = "SPACE";
	
	public static final String stringPAUSE = "PAUSE";
	
	public static final String stringDelete = "DELETE";
	public static final String stringPAGEUP = "PAGE-UP";
	public static final String stringPAGEDOWN = "PAGE-DOWN";
	public static final String stringEND = "END";
	public static final String stringHOME = "HOME";
	public static final String stringINSERT = "INSERT";
	
	public static final String stringLEFT = "LEFT";
	public static final String stringRIGHT = "RIGHT";
	public static final String stringUP = "UP";
	public static final String stringDOWN = "DOWN";
	
	public static final String stringPLUS = "+";
	public static final String stringMINUS = "-";
	public static final String stringF = "F";

	public static final String BUTTON_CONNECT	= "BUTTON_CONNECT";
	public static final String BUTTON_EXISTS		= "BUTTON_EXISTS";
	public static final String BUTTON_RED		= "BUTTON_RED";
	public static final String BUTTON_DELETE		= "BUTTON_DELETE";
	public static final String BUTTON_DELSINGSEC	= "BUTTON_DELSINGSEC";
	public static final String BUTTON_EDGEDIR	= "BUTTON_EDGEDIR";
	public static final String BUTTON_SWAP		= "BUTTON_SWAP";
	public static final String BUTTON_ZOOMIN 	= "BUTTON_ZOOMIN";
	public static final String BUTTON_ZOOMOUT 	= "BUTTON_ZOOMOUT";
	public static final String BUTTON_SHOWSOURCE	= "BUTTON_SHOWSOURCE";
	public static final String BUTTON_UNDO 	= "BUTTON_UNDO";
	
	public static final Color COLOR_WARNING = new Color(  255,125,125); 
}


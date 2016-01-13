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

package net.mumie.mathletfactory.util.help;

import java.awt.Dimension;
import java.awt.Point;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.help.BadIDException;
import javax.help.HelpSet;
import javax.help.Map;
import javax.help.TOCView;
import javax.swing.tree.DefaultMutableTreeNode;

import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;
import net.mumie.mathletfactory.util.property.Property;
import net.mumie.mathletfactory.util.property.PropertyMapIF;

public class MumieHelpSet extends HelpSet {
	
	public final static String INDEX_ID = "index";
	
	public final static String APPLET_INFO_ID = "applet_info";
	
	private MumieMap m_map = new MumieMap(); 
	
	private URL m_base;
	
	private MumieHelpTOCView m_tocView;
	
	private final static MumieLogger LOGGER = MumieLogger.getLogger(MumieHelpSet.class);
	private final static LogCategory HELP_ID_MAP_CATEGORY = LOGGER.getCategory("help.id-map");
	private final static LogCategory HELP_TOC_CATEGORY = LOGGER.getCategory("help.toc");
	
	MumieHelpSet() {
		super();
		
		setLocalMap(m_map);
		setHomeID(INDEX_ID);
		setTitle("Mumie Hilfe");
		
		// set up toc view
		m_tocView = new MumieHelpTOCView(null);
		addView(m_tocView);

		//		MainWindow.getPresentation(this, "Mumie Hilfe");
		Vector v = new Vector();
//		v.add(new )
		Hashtable tmp = new Hashtable();		      
		Presentation p = new Presentation("main window", true, false, new Dimension(800, 600), new Point(200, 200), "Mumie Hilfe", null, true, v);
		addPresentation(p, true);
	}
	
	MumieHelpSet(MumieHelp help, URL base, PropertyMapIF id2urlMap, DefaultMutableTreeNode treeData) {
		super();
		m_base = base;
		m_map.addAllMappings(id2urlMap);
		
		setLocalMap(m_map);
		setHomeID(INDEX_ID);
		setTitle("Mumie Hilfe");

		// set up toc view
		m_tocView = new MumieHelpTOCView(treeData);
		addView(m_tocView);
	}
	
	public URL getBase() {
		return m_base;
	}
	
	MumieHelpTOCView getTOCView() {
		return m_tocView;
	}
	
	public void setAppletURL(String url) {
		addMapping(APPLET_INFO_ID, url);
	}
	
	public void addMapping(String id, String url) {
		((MumieMap) getLocalMap()).addMapping(id, url);
	}

	class MumieMap implements Map {
	
		/** Map holding for each id an URL .*/ 
		private HashMap m_map = new HashMap();
				
		MumieMap() {
		}
		
		void addAllMappings(PropertyMapIF map) {
			Property[] props = map.getProperties();
			for(int i = 0; i < props.length; i++) {
				addMapping(props[i].getName(), (String) props[i].getValue());
			}
		}
		
		public void addMapping(String id, String url) {
			try {
				m_map.put(id, url);
				LOGGER.log(HELP_ID_MAP_CATEGORY, "Adding id \"" + id + "\" for URL \"" + url + "\"");
			} catch (BadIDException e) {
				throw new IllegalArgumentException("Help ID is not valid: " + id);
			} 
		}
		
		public Enumeration getAllIDs() {
			return new IDWrapperEnumeration(new Vector(m_map.keySet()).elements(), MumieHelpSet.this);
		}
	
		public ID getClosestID(URL url) {
			Map.ID id = getIDFromURL(url);
			if(id != null)
				return id;
			return getHomeID();
		}
	
		public ID getIDFromURL(URL url) {
			Set ids = m_map.keySet();
			for(Iterator i = ids.iterator(); i.hasNext(); ){
				String id = (String) i.next();
					if(m_map.get(id).equals(url))
						return Map.ID.create(id, MumieHelpSet.this);
			}
			return null;
		}
	
		public Enumeration getIDs(URL url) {
			Vector v = new Vector();
			Enumeration e = getAllIDs();
			while(e.hasMoreElements()) {
				String key = (String) e.nextElement();
				try {
				if(url.sameFile(new URL(m_base, key)))
					v.add(key);
		    } catch (Exception ex) {} // do nothing
			}
			return v.elements();
		}
	
		public URL getURLFromID(ID id) throws MalformedURLException {
			String idName = id.getIDString();
			String url = (String) m_map.get(idName);
			LOGGER.log(HELP_ID_MAP_CATEGORY, "Getting URL from id \"" + idName + "\" = \"" + url + "\" from base " + m_base);
			if(url == null)
				return null;
			URL result = getClass().getResource(url);
			if(result != null)
				return result;
			return new URL(m_base, url);
		}
	
		public boolean isID(URL url) {
			return getIDFromURL(url) != null;
		}
	
		public boolean isValidID(String id, HelpSet helpSet) {
			return true;
//			return m_map.get(id) != null;
		}
	
		/**
		 * Wraps an enumeration of id names into an enumeration of JavaHelp IDs.
		 */
	  private class IDWrapperEnumeration implements Enumeration {
	  	private Enumeration e;
	
			private HelpSet hs;
	
			/**
			 * Creates an new enumeration of JavaHelp IDs by wraping the given enumeration of id names.
			 * @param e an enumeration of id names
			 * @param hs the help set to create ids with
			 */
			public IDWrapperEnumeration(Enumeration e, HelpSet hs) {
				this.e = e;
				this.hs = hs;
			}
	
			public boolean hasMoreElements() {
				return e.hasMoreElements();
			}
	
			public Object nextElement() {
				Object back = null;
				try {
					back = ID.create((String) e.nextElement(), hs);
				} catch (Exception ex) {} // do nothing
				return back;
			}
		}
	}
	
	class MumieHelpTOCView extends TOCView {
		
		private DefaultMutableTreeNode m_treeData;
		
		private DefaultTOCFactory m_treeItemFactory;
		
		MumieHelpTOCView(DefaultMutableTreeNode treeData) {
			super(MumieHelpSet.this, "TOC", "Inhaltsverzeichnis", new Hashtable());
			m_treeItemFactory = new DefaultTOCFactory();
			if(treeData != null)
				m_treeData = treeData;
			else
				m_treeData = new DefaultMutableTreeNode();
		}
		
		public DefaultMutableTreeNode createTOCItem(String target, String text) {
			LOGGER.log(HELP_TOC_CATEGORY, "Creating toc item \"" + text + "\" for target \"" + target + "\"");
			Hashtable params = new Hashtable();
			params.put("target", target == null ? "" : target);
			params.put("text", text);
			return new DefaultMutableTreeNode(m_treeItemFactory.createItem("tocitem", params, MumieHelpSet.this, null));
		}
		
		public DefaultMutableTreeNode getDataAsTree() {
			return m_treeData;
		}
		
		public String getMergeType() {
			return "javax.help.UniteAppendMerge";
		}
	}
}
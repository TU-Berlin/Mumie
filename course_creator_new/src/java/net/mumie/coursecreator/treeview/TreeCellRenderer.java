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

import java.awt.Component;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Color;

import net.mumie.coursecreator.CCController;
import net.mumie.coursecreator.treeview.AbstractDocumentAdapter;

/***
 * MumieTreeCellRenderer.java
 *
 * Created: Thu Jan 23 12:59:02 2003
 **/
/**
 * Displays icons in the tree depending on the category of the document
 * associated with a tree node
 *
 * TODO: handle icon map from database
 *
 * @author <a href="mailto:sinha@math.tu-berlin.de">Uwe Sinha</a>
 * @author <a href="mailto:hoffmana@ma.tum.de">Andreas Hoffmann</a>
 * @version $Id: TreeCellRenderer.java,v 1.8 2008/05/28 12:39:37 vrichter Exp $
 */

public class TreeCellRenderer extends DefaultTreeCellRenderer {
	
	static final long serialVersionUID=0;
	IconMap icons; 

	HashMap oldIcons;
	
	LinkedList<String> missingKeys; //a List of missing IconKeys <br> just for Information

	/**
	 * Creates a new <code>MumieTreeCellRenderer</code> instance with given
	 * properties. If the <code>navtree.icon.baseurl</code> property is
	 * set, it is assumed that the icons are to be fetched from URLs. If
	 * not, the icons must be provided with the applet so they can be
	 * accessed by the class loader. <strong>N.B.:</strong> if you want
	 * your icons to be fetched from a URL, but don't want to provide a
	 * base URL, simply set <code>navtree.icon.baseurl</code> to the empty
	 * string.
	 *
	 * @param pr a <code>Properties</code> value
	 */
	public TreeCellRenderer(Properties pr) {
		super(); 
		
	}

	/**
	 *
	 */ 

	public TreeCellRenderer(IconMap iconMap){
		super(); 
		icons = iconMap; 
		missingKeys = new LinkedList<String>();
	}

	/**
	 * <p> Initialization method called by the constructor. This variant is
	 * called if the <code>navtree.icon.baseurl</code> property is set.
	 * </p>
	 *
	 * <p> All icons are loaded into a {@link java.util.HashMap HashMap},
	 * the keys being the third (and last) component of the name of the
	 * property corresponding to the icon, the first two components being
	 * <code>navtree.icon.</code>. E.g., an icon referenced by the property
	 * <code>navtree.icon.definition</code> will be stored along with the
	 * key <code>definition</code>. </p>
	 *
	 * @param pr a <code>Properties</code> value
	 * @param baseURL the <code>String</code> which has been read from the
	 * <code>navtree.icon.baseurl</code> property by the constructor. 
	 * @exception MalformedURLException if no valid <code>URL</code> object
	 * can be constructed from the value of <code>navtree.icon.baseurl</code>. 
	 * 
	 * @see #MumieTreeCellRenderer(Properties)
	 * @see #loadIcons(Properties)
	 */
	protected void loadIcons(Properties pr, String baseURL) 
	throws MalformedURLException{
		Enumeration names; 
		StringBuffer sb = new StringBuffer(baseURL);
		for (names = pr.propertyNames();names.hasMoreElements();) {
			String currentProp = (String)names.nextElement();
			String parts[]; 
			parts = currentProp.split("\\.",3); 
			if (baseURL.length() == 0 || !isCompleteBaseURL(baseURL)) {
				sb.append("/"); 
			} // end of if ()
			sb.append(pr.getProperty(currentProp)); 
			URL iconURL = new URL(sb.toString());
			ImageIcon icon = new ImageIcon(iconURL);
			oldIcons.put(parts[2],icon); 
			//reset StringBuffer
			sb.delete(0,sb.length());
			sb.insert(0,baseURL); 
		} // end of for ()

	}

	/**
	 * Initialization method called by the constructor. This variant is
	 * called if the <code>navtree.icon.baseurl</code> property is
	 * <strong>not</strong> set.
	 *
	 * @param pr a <code>Properties</code> value
	 * 
	 * @see #MumieTreeCellRenderer(Properties)
	 * @see #loadIcons(Properties, String)
	 */
	protected void loadIcons(Properties pr){
		Enumeration names; 
		for (names = pr.propertyNames();names.hasMoreElements();) {
			String currentProp = (String)names.nextElement();
			String parts[]; 
			StringBuffer sb; 
			if (currentProp.startsWith("navtree.icon.")) {
				parts = currentProp.split("\\."); 	
				String iconPath= pr.getProperty(currentProp);  
				try {
					ImageIcon icon = 
						new ImageIcon(this.getClass().getResource(iconPath));
					sb = new StringBuffer(parts[2]); 
					if (parts.length > 3) {
						sb.append(".").append(parts[3]); 
					} // end of if ()		    
					oldIcons.put(sb.toString(),icon); 		     
				} catch (NullPointerException e) {
					CCController.dialogErrorOccured(
							"TreeCellRenderer: NullPointerException", 
							"TreeCellRenderer: NullPointerException: could not load an icon for property \"" + currentProp +"\" \n Error:" + e, 
							JOptionPane.ERROR_MESSAGE);
	 
				} // end of try-catch
			} // end of if ()	    
		} // end of for ()	
	}

	private boolean isCompleteBaseURL(String url){
		final String endChars[] = {"/" , "=" , "?", ";"}; 
		for (int i=0; i < endChars.length ; i++) {
			if (url.endsWith(endChars[i])) {
				return true; 
			} // end of if ()
		} // end of for ()
		return false;      
	}

	/**
	 * Describe <code>getTreeCellRendererComponent</code> method here.
	 *
	 * Configures the renderer based on the passed in components. The value
	 * is set from messaging the tree with <code>convertValueToText</code>,
	 * which ultimately invokes <code>toString</code> on
	 * <code>value</code>. The foreground color is set based on the
	 * <code>sel</code>ection and the icon is set based on on
	 * <code>leaf</code> and <code>expanded</code>.
	 * 
	 * @return the <code>Component</code> that the renderer uses to draw
	 * the <code>value</code>.
	 */
	public Component getTreeCellRendererComponent(
			JTree tree,
			Object value, // the "current" tree node
			boolean sel,
			boolean expanded,
			boolean leaf,
			int row,
			boolean hasFocus) {

		super.getTreeCellRendererComponent(
				tree, value, sel,
				expanded, leaf, row,
				hasFocus);

		String iconKey = getNodeCategory(value);
		String iconType = getNodeType(value);
		
		// this will display the description in the tool tip
		setToolTipText(getNodeDescription(value));

		ImageIcon im; 
		try {	    
			if (iconKey.equals(AbstractDocumentAdapter.ARROW_CMP))
			{
				im = ((IconMap)icons).getIcon(iconType, AbstractDocumentAdapter.ARROW_CMP);
			} // end of if ()
			else if(iconKey.equals(AbstractDocumentAdapter.ARROW_EXP))
			{
				im = ((IconMap)icons).getIcon(iconType, AbstractDocumentAdapter.ARROW_EXP);
			}
			else {
				im = ((IconMap)icons).getIcon(iconType, iconKey);
			} // end of if () else

		} catch (NumberFormatException e) {
			im = ((IconMap)icons).getIcon(iconType, iconKey);
		} // end of try-catch
  
		if (im != null) {
			setIcon(im);
		} // end of if ()
		else {
			if (!this.missingKeys.contains(iconKey)){
				this.missingKeys.addLast(iconKey);
			
			CCController.dialogErrorOccured
			(
					"TreeCellRenderer: Error", 
					"TreeCellRenderer: Error: No icon found for key:" + iconKey, 
					JOptionPane.INFORMATION_MESSAGE);
			}

		} // end of if () else

		return this;
	}

	/**
	 * Describe <code>getNodeType</code> method here.
	 *
	 * @param value an <code>Object</code> value
	 * @return a <code>String</code> value
	 */
	protected String getNodeType(Object value) {
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode)value;
		DocumentAdapter nodeInfo =
			(DocumentAdapter)(node.getUserObject());
		return nodeInfo.getDocumentType();
	}

	/**
	 * Describe <code>getNodeCategory</code> method here.
	 *
	 * @param value an <code>Object</code> value
	 * @return a <code>String</code> value
	 */
	protected String getNodeCategory(Object value) {
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode)value;
		DocumentAdapter nodeInfo =
			(DocumentAdapter)(node.getUserObject());
		return nodeInfo.getCategoryName();
	}

	/**
	 * Describe <code>getNodeDescription</code> method here.
	 *
	 * @param value an <code>Object</code> value
	 * @return a <code>String</code> value
	 */
	protected String getNodeDescription(Object value) {
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode)value;
		AbstractDocumentAdapter nodeInfo =
			(AbstractDocumentAdapter)(node.getUserObject());
		return nodeInfo.getDescription();
	}


	public void setBackground(Color color) {
		if (color != null) {
			super.setBackground(color); 
		} // end of if ()
	}
	public void setBackgroundSelectionColor(Color color){
		if (color != null) {
			super.setBackgroundSelectionColor(color); 
		} // end of if ()
	}
	public  void setTextSelectionColor(Color color) {
		if (color != null) {
			super.setTextSelectionColor(color); 
		} // end of if ()
	}
	public void setBackgroundNonSelectionColor(Color color) {
		if (color != null) {
			super.setBackgroundNonSelectionColor(color); 
		} // end of if ()   
	} 


}// MumieTreeCellRenderer

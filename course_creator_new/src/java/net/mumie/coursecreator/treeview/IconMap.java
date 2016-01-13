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

import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

import java.awt.Color;
import java.io.IOException;
import java.net.*;
import java.util.Map;
import java.util.Iterator;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;

import net.mumie.coursecreator.treeview.AbstractDocumentAdapter;
import net.mumie.coursecreator.CCController;
/**
 * Holds all the Icons for the TreeView - should be rewritten due to really bad code!
 * @author japs
 * @author <a href="mailto:binder@math.tu-berlin.de>Jens Binder</a>
 * @version $Id: IconMap.java,v 1.11 2009/03/30 12:38:17 vrichter Exp $
 */
public class IconMap {
	
	/*
	 * the HashMap with the Icons - each key has another Map with the types of the Icon (f.ex. two times motivation and so on)
	 */
	private HashMap icons;
	ColorMap colorMap;
	
	URI iconBase = null;	
	
	// Document for parsing the IconXML-File (see build.xml)
	private DocumentBuilder builder;	
	private CCController controller;
	public IconMap(CCController c) { 
		icons = new HashMap();
		colorMap = new ColorMap();
		this.controller = c;
	}
	
	public void init(String Index) {
		InputStream input=null;
		babble("init IconMap with Index: "+Index);
		try {
			input = new FileInputStream(Index);
			babble("found Index-File: "+input.toString());
		}catch (Exception fnfe){
			babble("FileNotFound: "+fnfe+" with "+Index);
		}
		this.parse(input);		
	}
	
	public ImageIcon putIcon(String cat, String type, String url) 
	throws Exception
	{
		String iconBaseStr = iconBase.toString();
		ImageIcon im = null; 
		
		try {
			im = doPutIcon(cat,type,url);
			return im; 
		} catch (ClassCastException e) {
			return null; 
		} catch (RuntimeException re){
			try {	
				CCController.dialogErrorOccured(
						"Iconmap RunTimeException" ,
						"Iconmap RunTimeException: "+re, 
						JOptionPane.ERROR_MESSAGE);
				
				im = doPutIcon(cat, type, (iconBaseStr.endsWith("/") ? iconBaseStr : iconBaseStr + "/") + url); 
			} catch (Exception ex) {
				CCController.dialogErrorOccured(
						"Iconmap Exception" ,
						"Iconmap Exception: "+ex, 
						JOptionPane.ERROR_MESSAGE);

				throw ex;
			} // end of inner try-catch
			
			return im; 
		} catch (IOException ioe){
			CCController.dialogErrorOccured(
					"Iconmap IOException" ,
					"Iconmap IOException: "+ioe, 
					JOptionPane.ERROR_MESSAGE);

			try {	
				im = doPutIcon(cat,	type, (iconBaseStr.endsWith("/") ? iconBaseStr : iconBaseStr + "/") + url); 
			} catch (Exception ex) {
				CCController.dialogErrorOccured(
						"Iconmap Exception" ,
						"Iconmap Exception: "+ex, 
						JOptionPane.ERROR_MESSAGE);

				throw ex;
			} // end of inner try-catch
			
			return im; 
		} // end of try-catch
		
	} 
	
	private ImageIcon doPutIcon(String cat, String type, String name) 
	throws ClassCastException, RuntimeException, IOException, URISyntaxException
	{
		URI realURI; 
		
		if (iconBase == null) {
			realURI = new URI(name); 
		} // end of if ()
		else {
			realURI = new URI(iconBase+name); 
		} // end of if () else
		
		byte[] b = loadImage(realURI);
		
		HashMap typeMap;
		ImageIcon ret;
		if (icons.get(cat)==null) {
			typeMap = new HashMap();
		}
		else typeMap = (HashMap)icons.get(cat);
		typeMap.put(type, new ImageIcon(b));
		
		
		return (ImageIcon)icons.put(cat, typeMap);
	}
	
	private byte[] loadImage(URI uri) throws IOException {
		
		InputStream ist = new FileInputStream(uri.getPath());
		//InputStream ist = url.openStream();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		byte[] bytes = new byte[1024]; 
		
		int r; 
		
		while ((r = ist.read(bytes,0,1024)) > 0) {
			out.write(bytes,0,r); 
		} // end of while ()
		
		return out.toByteArray(); 
	}
	
	public ImageIcon getIcon(String type, String cat){
		//babble("try to get Icon with type="+type+" and cat="+cat);
		try {
			HashMap typeMap = (HashMap)icons.get(cat);
			if (typeMap!=null) return (ImageIcon)typeMap.get(type);
			else return null;
		} catch (ClassCastException e) {
			CCController.dialogErrorOccured(
					"Iconmap ClassCastException" ,
					"Iconmap ClassCastException: " +e+
					"\n Garbage entry (not an icon) for type "+cat, 
					JOptionPane.ERROR_MESSAGE);
			 
			return null;
		} catch (IndexOutOfBoundsException ie){
			CCController.dialogErrorOccured(
					"Iconmap IndexOutOfBoundsException",
					"Iconmap IndexOutOfBoundsException: "+ie,
					JOptionPane.ERROR_MESSAGE);			
			return null;
		} // end of try-catch
		
	}
	
	/**
	 * Get the IconBase value.
	 * @return the IconBase value.
	 */
	public URI getIconBase() {
		return iconBase;
	}
	
	/**
	 * Set the IconBase value.
	 * @param newIconBase The new IconBase value.
	 */
	public void setIconBase(URI newIconBase) {
		this.iconBase = newIconBase;
	}
	
	/**
	 * Set the IconBase value.
	 * @param newIconBase The new IconBase value.
	 */
	public void setIconBase(String newIconBase) {
		try {
			this.iconBase = new URI(newIconBase);
		} catch (URISyntaxException e) {
			CCController.dialogErrorOccured(
					"Iconmap: URISyntaxException", 
					"Iconmap: URISyntaxException \n"
					+e+"\n Cannot set iconBase to malformed URI " 
					+ newIconBase,  
					JOptionPane.ERROR_MESSAGE);
		} // end of try-catch
		
	}
	
	public ColorMap getColorMap(){
		if (this.colorMap.isEmpty()){
			colorMap.put("bgcol", new Color(Integer.decode("#FFFFFF").intValue()));
			colorMap.put("selcol", new Color(Integer.decode("#C1FFC1").intValue()));
			colorMap.put("seltextcol", new Color(Integer.decode("#0000C0").intValue()));
		}		
		return colorMap;
	}
	
	public String toString(){
		
		StringBuffer sb = new StringBuffer(); 
		Map.Entry entry; 
		
			for (Iterator it = icons.entrySet().iterator(); it.hasNext();) {
				entry = (Map.Entry)it.next();
				sb.append("( ").append("[Type '").append(entry.getKey());
				if (entry.getValue() instanceof ImageIcon) {
					sb.append(" == [Icon]");
				} // end of if ()
				else if (entry.getValue() == null){
					sb.append(" == [null]");
				} // end of if () else
				else {
					sb.append(" == [?]"); 
				} // end of else
				sb.append(")\n"); 	    
			} // end of for (Iterator it = this.entrySet().iterator();
			//             it.hasNext();)		
		return sb.toString(); 
	}
	
	/**
	 * parses the IconIndex.xml using DOM 
	 * in IconIndex.xml are the paths for the Icons stored!
	 * @param input - the FileInputStream from which the XML-Data is read
	 */
	private void parse(InputStream input) {
		
		// initialize DOMBuilder
		try {			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();			
			factory.setIgnoringComments(true);
			factory.setNamespaceAware(false);			
			this.builder = factory.newDocumentBuilder();
		}
		catch (Exception e){
			CCController.dialogErrorOccured(
					"Iconmap: Exception", 
					"Iconmap: Exception while accessing DOMBuilder: \n" +e,
					JOptionPane.ERROR_MESSAGE);
		}
		
		Document doc=null;			
		
		if (input!=null){
			try{
				babble("... parsing ...");
				doc = builder.parse(input);
				babble("... done");
			}
			catch (IOException ioe) {
				CCController.dialogErrorOccured(
						"Iconmap: IOException", 
						"Iconmap: IOException occured while parsing Document: " +ioe,
						JOptionPane.ERROR_MESSAGE);
			}
			catch (Exception e){
				CCController.dialogErrorOccured(
						"Iconmap: Exception", 
						"Iconmap: Exception occured while parsing Document: " +e,
						JOptionPane.ERROR_MESSAGE);
			}
			
			Element root = doc.getDocumentElement();

			if (root.getNodeName().equals("Layout")){
				NodeList children = root.getChildNodes();
				for (int i=0;i<children.getLength();i++){
					Node child = children.item(i);
					//to avoid NullPointerExceptions cause of text-nodes
					if(!child.getNodeName().equals("#text")){															
						// the two arrowsurl
						if (child.getNodeName().equals("Arrow")){
							String uri = child.getAttributes().getNamedItem("filename").getNodeValue();										
							boolean exp = child.getAttributes().getNamedItem("expanded").getNodeValue().equals("1");
							String type = child.getAttributes().getNamedItem("type").getNodeValue();
							try {
								if (exp)
									putIcon(AbstractDocumentAdapter.ARROW_EXP,type,uri);
								else
									putIcon(AbstractDocumentAdapter.ARROW_CMP,type,uri);
							}catch(Exception e){}
						}
						if (child.getNodeName().equals("Color")){
							String key = child.getAttributes().getNamedItem("key").getNodeValue();
							String value = child.getAttributes().getNamedItem("value").getNodeValue();
							this.colorMap.put(key,new Color(Integer.decode(value).intValue()));							
						}
						// the other Icons 
						if (child.getNodeName().equals("Icon")){						
							String uri = child.getAttributes().getNamedItem("filename").getNodeValue();			
							try{
								String cat = child.getAttributes().getNamedItem("category").getNodeValue();
								String type = child.getAttributes().getNamedItem("type").getNodeValue();
								putIcon(cat, type, uri);								
							}catch(Exception e){}
						}
					}						
				}
				
			}
		}
	}
	
	void babble (String msg){
    	if (controller.getConfig().getDEBUG()) System.out.println(this.getClass() +": " + msg); 
    }
	
}// IconMap
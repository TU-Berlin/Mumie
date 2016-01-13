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

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

import net.mumie.coursecreator.graph.NavGraph;
import net.mumie.coursecreator.graph.cells.CCGraphCell;
import net.mumie.coursecreator.graph.cells.CCEdgeCell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.jaxen.JaxenException;

import java.awt.Point;

/**
 * GraphHolder is to keep parts of graphs which we need for swapping cells
 * @author vrichter
 *
 */
public class GraphHolder {
	
	private static HashMap<String,XPath> xPathList;
	private Document document;
	
	public GraphHolder(Document doc){
    	xPathList = new HashMap<String,XPath>();
    	this.document = doc;
	}
	
	public void setEdges(Element rootContent,NavGraph graph) throws JaxenException{
    	
    	String redPath = "//" + "thread//"+ "arcs//" + "arc";
    	String path = "//" + "net//"+ "arcs//" +"arc";
    	
    	List<CCEdgeCell> edgesRed = this.getAllNodes(rootContent,redPath);
    	List<CCEdgeCell> edges = this.getAllNodes(rootContent,path);
    	
    	
    	//all existing edges
		for (int i= 0 ; i<edges.size();i++){
			boolean red = false;
			Node act = (Node)edges.get(i);
			int startNid = Integer.parseInt(this.getSingleNode(act,"./@from").getNodeValue());
			int endNid = Integer.parseInt(this.getSingleNode(act,"./@to").getNodeValue());
		
			List<CCEdgeCell> points  = this.getAllNodes(act,"point"); 
			LinkedList<Point> pointList = new LinkedList<Point>();

			//getting all points
			for (int k=0;k<points.size();k++){
				
				 int x = Integer.parseInt(this.getSingleNode(points.get(k),"./@posx").getNodeValue());
				 int y = Integer.parseInt(this.getSingleNode(points.get(k),"./@posy").getNodeValue());
				 
				 pointList.addLast(new Point(x,y));
			}
			CCGraphCell startCell = graph.getCellByNid(startNid);
			CCGraphCell endCell = graph.getCellByNid(endNid);
			
			//proof iff existing edges are also red
			for (int k =0;k<edgesRed.size();k++){
				Node actRed = (Node) edgesRed.get(k);
			
				//same start/endCells?
				if ((Integer.parseInt(this.getSingleNode(actRed,"./@from").getNodeValue())==startNid)){
					if ((Integer.parseInt(this.getSingleNode(actRed,"./@to").getNodeValue())==endNid)){
						List pointsRed  = this.getAllNodes(actRed,"point");
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
			
			graph.insertEdge(startCell,endCell,pointList,red,true);
			
			
			
		}			
		
		// remaining edges are only red
		for (int i =0;i<edgesRed.size();i++){
			Node act = (Node) edgesRed.get(i);
			
			int startNid = Integer.parseInt(this.getSingleNode(act,"./@from").getNodeValue());
			int endNid = Integer.parseInt(this.getSingleNode(act,"./@to").getNodeValue());
			  
			List points  = this.getAllNodes(act,"point"); 
			LinkedList<Point> pointList = new LinkedList<Point>();
			for (int k=0;k<points.size();k++){
				 
				 int x = Integer.parseInt(this.getSingleNode(points.get(k),"./@posx").getNodeValue());
				 int y = Integer.parseInt(this.getSingleNode(points.get(k),"./@posy").getNodeValue());
				 pointList.addLast(new Point(x,y));
			}
			
			CCGraphCell startCell = graph.getCellByNid(startNid);
			CCGraphCell endCell = graph.getCellByNid(endNid);			
			graph.insertEdge(startCell,endCell,pointList,true,false);
		}
    }
	
	/**
	 * returns a List of Nodes which is specified by the XPath-expression-argument "nodepath"
	 * within the DOM-document specified by the root - Element
	 */
	private List<CCEdgeCell> getAllNodes(Object root, String nodepath) throws JaxenException
	{
		
		XPath path; 
		if (xPathList.containsKey(nodepath)){
			path = (XPath) xPathList.get(nodepath);			
		}
		else{
			path =new DOMXPath(nodepath);
			xPathList.put(nodepath,path);
		}
		
		return path.selectNodes(root);
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
		    xPathList.put(nodepath,path);
		    
		}
		return (Node) path.selectSingleNode(root);
		
	}
	
	
	
	
	/**
	 * generates an xmlElement which containes xmlCode for an edgeList and two graphcells which are interchanged.
	 * @param edges LinkedList of edges which are to be hold.
	 * @param nid1 one of the swap-cells
	 * @param nid2 one of the swap-cells
	 * @return 
	 */public Element makeSwapCellXML(LinkedList<CCEdgeCell> edges, String nid1,String nid2){
		
//		 root
		Element csection = document.createElementNS("","edges");
		
//		net and thread
		Element net=this.generateEdgeXML(edges, nid1, nid2,"net");
		csection.appendChild(net);
		
		Element thread=this.generateEdgeXML(edges,nid1, nid2,"thread");
		csection.appendChild(thread);
	
		document.appendChild(csection);
		
		return csection;
	}
	
	
	/**

	 * generates an xmlElement which contains all red or exists edges (depends on elementString)
	 * and interchanges nid1 and nid2.<p>
	 * @param elementString is thread or net as String.<br> 
	 * @param edges LinkedList of edges which are to be hold.
	 */
	private Element generateEdgeXML(LinkedList<CCEdgeCell> edges, String nid1,String nid2, String elementString) {
		
		Element arcs = document.createElementNS("","arcs");

		//now get all xml code
		for (int i = 0;i<edges.size(); i++){
			CCEdgeCell e = edges.get(i);
			if (((e).getExists()&& elementString.equals("net"))
					||((e).getRed()&& elementString.equals("thread"))){
				Element elm = e.getContentXML(document,"",false);
				
				//startCell
		    	String sNid = String.valueOf(e.getStartCell().getNodeID());
		    	
		    	//endCell    	
		    	String eNid = String.valueOf(e.getEndCell().getNodeID());
		
		    	// now happens the most importent:
		    	// swap the nids
		    	if (nid1.equals(sNid)) sNid=nid2;
		    	else if (nid2.equals(sNid)) sNid=nid1;
		    	
		    	if (nid1.equals(eNid)) eNid=nid2;
		    	else if (nid2.equals(eNid)) eNid=nid1;
		    	
		    	//write the nid to xml
		    	if (!e.getReverse()){
		    		
					elm.setAttribute("from",sNid);
					elm.setAttribute("to",eNid);
		    	}else{
		    		elm.setAttribute("from",eNid);
					elm.setAttribute("to",sNid);
		    	}
				arcs.appendChild(elm);
			}
		}
		
		Element root = this.document.createElementNS("", elementString);
		root.appendChild(arcs);
		return root;
	}
}

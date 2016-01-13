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

package net.mumie.coursecreator.graph;

import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.LinkedList;

import javax.swing.JOptionPane;
import javax.xml.transform.TransformerException;

import net.mumie.coursecreator.CCController;
import net.mumie.coursecreator.CCModel;
import net.mumie.coursecreator.CommandConstants;
import net.mumie.coursecreator.Graph;
import net.mumie.coursecreator.events.CCModelChangedEvent;
import net.mumie.coursecreator.graph.cells.CCEdgeCell;
import net.mumie.coursecreator.graph.cells.CCEdgeView;
import net.mumie.coursecreator.graph.cells.CCGraphCell;

/**
 * this class manages all NavGraphs.<br> 
 * If you need a Navgraph you get it with this.getMainGraph.<br>
 * any Methodes for the NavGraph are just forwarded
 * @author vrichter  <a href="mailto:vrichter@math.tu-berlin.de>Verena Richter</a>
 *
 */

public class NavModel {
	
	private CCModel ccModel;
	
	protected NavGraph graphMain;

	private int lfnr = 0;
    
    
    /**
     * Create a new NavModel instance.
     */
    public NavModel(CCModel model){ 
		
		this.ccModel = model;
		
		
	
		this.ccModel.addModelChangeListener(this.ccModel.getController());
		this.ccModel.addModelChangeListener(this.ccModel.getCourseCreator());
		
	}
    
    /**
     * Create a new course.
     * A not saved course will be lost.
     */
    public Graph newCourse() {
    	NavGraph t=new NavGraph(this);
    	t.pre=false;
    	t.setUndoName(t.getUndoName().concat("_"+String.valueOf(this.lfnr++)));
    	if (this.getMainGraph()!=null)
    		this.getMainGraph().removeGraphSelectionListener(this.ccModel.getController());
    		
		this.setGraphMain(t);
		
		return t;
    }
    

    /**
     * Connect two Elements/Nodes/Sections/Exercises.
     */
    public void connect() {
	    this.getMainGraph().connect();
    }

    /**
     * Interchanges two Elements/Nodes/Sections/Exercises (GraphCells)
     */
    public void swapGraphCells() {
	    this.getMainGraph().swapGraphCells();
    }
    
    /**
     * Deletes selected Elements/Sections/Nodes/Subelements/Edges/Exercises.
     */
    public void delete() {
	    this.graphMain.delete();
    }
    
	public void toggleRed(){
		
		this.getMainGraph().toggleRed();
	}
	
	public void toggleExists(){
		
		this.getMainGraph().toggleExists();
	}
    
    /**
     * Zoom in selected graph.
     */
    public void zoomIn() {
    	this.getMainGraph().zoomIn();
    }
    /**
     * Zoom out selected graph.
     */
    public void zoomOut() {
    	this.getMainGraph().zoomOut();
    }

    /**
     * toggles the direction of the small arrow at each edge
     *
     */
    public void toggleEdgeDirection(){
    	this.getMainGraph().toggleEdgeDirection();
    	
    }
 
    /**
     * FIXME - is interesting for the key-codes
     * @param type
     */
    public void moveSubCell( String type ){
    	this.graphMain.moveSubCell(type);

    }
    
    /**
     * removes the shown Graph if there are at least 2 Graphs..
     * .. well it only removes the first buffered graph .. 
     *
     */
    public void removeGraph(int nr){
    		
    	CCModel.IndexGraph ig=this.ccModel.getNextGraph(CCModel.KURS_NAVGRAPH, 0);

    	int first = ig.getIndex();
    	if (first== nr){//erster Graph ist zu loeschender Graph 
    		CCModel.IndexGraph next=this.ccModel.getNextGraph(CCModel.KURS_NAVGRAPH, first);
    		this.setGraphMain((NavGraph)next.getGraph());
    	}
    
    }
  
    /**
     * 
     * @param dir
     * @param fileName
     * @param graph
     * @return 0 if everything is fine
     * 			-1 if graph is empty
     * 			-2 if graph exists and is open but user wants to save again
     * 			-3 if graph exists and is open but user wants to abort
     * 			-4  graph exists do but user wants to overwrite
     * 			-5 graph exists and user wants to save under an new name
     * @throws IOException
     * @throws TransformerException
     */
   public int saveGraph(String dir, String fileName, NavGraph graph, boolean dummy)
   	throws IOException, TransformerException {
      
	   String metaFileName = fileName + ".meta.xml";
	   String contentFileName = fileName + ".content.xml";
	   
		File metaFile = new File(dir, metaFileName);
		
		if (!dummy) if (metaFile.exists()){//file exists
			int oldGraph=this.ccModel.graphIsOpen(metaFileName,dir);
			if (-1!=oldGraph){// file exists and is open!
				
				if (!fileName.equals(graph.getMetaInfos().getSaveName())){
					int dec =ccModel.getController().dialogFileToSaveExistAndIsOpen();
		
					if (dec==0) return -2;//user wants to try to save again
					if (dec==1) return -3;// user wants to abort
					
//						well..cells user wants to save and to delete the other graph's name
					this.ccModel.getBufferedGraphs().get(oldGraph).getMetaInfos().setSaveName(null);
					this.ccModel.getBufferedGraphs().get(oldGraph).setChanged(true);

				}
			
			}
			int dec = this.ccModel.getController().dialogOverwrite(dir, fileName); 
			switch (dec){ 
				case 0: break;// overwrite existing file
				case 1: return -4;// user dont want to overwirte existing file
				case 2: return -5;// save under new name
				default:break;
			}
		}
	
		FileOutputStream fos = new FileOutputStream(metaFile);
	    fos.write(graph.toXMLMeta().getBytes());
	    
	    // Write to file
	    fos.flush();
	    fos.close();

	    File contentFile = new File(dir, contentFileName);
    	fos = new FileOutputStream(contentFile);
 	    fos.write(graph.toXMLContent().getBytes());
 	    
 	    // Write to file
 	    fos.flush();
 	    fos.close();
 	    if (!dummy){
	 	    graph.getMetaInfos().setSaveName(fileName);
	 	    graph.getMetaInfos().setSaveDir(dir);
	 	    this.ccModel.notify(graph, CCModelChangedEvent.CHANGED_GRAPHS, false);
	 	    graph.setChanged(false);
 	    }
    	return 0;
    }

       
    /**
     * calls setSourcesOrDrains2SelectionCells of maingraph
     * @param isSource
     */
    public void showSourcesOrDrains(boolean isSource){
    	this.graphMain.showSourcesOrDrains(isSource);
    } 

    /** calls showCircles of Maingraph */
    public void showCircles(){
    	this.getMainGraph().showCircles();
    } 
    
    /** Set graphMain to given graph. */
    public void setGraphMain(NavGraph ng) {
    	this.graphMain=ng;
		this.getMainGraph().addGraphSelectionListener(this.ccModel.getController());
		
    }
    /** Returns the root graph. */
    public NavGraph getRoot() {
	return this.getMainGraph();
    }
	
	/**
	 * moves all selected cells about CommandConstants.MOVE_STEPs Pixels
	 * towards given Direction (type)
	 * @param type
	 */
	public void move(int type){
		
		int movestep = this.ccModel.getController().getConfig().getMOVE_STEP();
		Object[] objs = this.getMainGraph().getSelectionCells();
		for (int i= 0;i<objs.length;i++){
			Object ob= objs[i];
			if (ob instanceof CCGraphCell){

				CCGraphCell cell = (CCGraphCell)ob;
				switch (type){
				case CommandConstants.UP:
					cell.setPosY(cell.getPosY()-movestep);
					break;
				case CommandConstants.DOWN:
					cell.setPosY(cell.getPosY()+movestep);
					break;
				case CommandConstants.RIGHT:
					cell.setPosX(cell.getPosX()+movestep);
					break;
				case CommandConstants.LEFT:
					cell.setPosX(cell.getPosX()-movestep);
					break;
				}
			}else if (ob instanceof CCEdgeCell){
				LinkedList<Point> list = ((CCEdgeCell)ob).getDrawEdgeList();
				CCEdgeView ev = (CCEdgeView)this.getMainGraph().getCellView(ob);
				for (int j=0;j<list.size();j++){
					Point p = (Point)list.get(j);
					switch (type){
					case CommandConstants.UP:
						p.translate(0,-movestep);
						
						break;
					case CommandConstants.DOWN:
						p.translate(0,movestep);
						break;

					case CommandConstants.RIGHT:
						if ((j!=0)&&(j!=list.size()-1)){//first and last point do not move
							if ((j==1)||(j==list.size()-2)){
								if ((list.size()>3))
									p.translate(movestep/2,0);// second point moves only the half
							}
							else p.translate(movestep,0);
						}
						break;
					case CommandConstants.LEFT:
						if ((j!=0)&&(j!=list.size()-1)){
							if ((j==1)||(j==list.size()-2)){
								if ((list.size()>3))
								p.translate(-movestep/2,0);
							}
							else p.translate(-movestep,0);
						}
						break;
					}
					ev.setPoint(j+1,p);	
				}
			}
			this.getMainGraph().cellMoved(this.getMainGraph().getCellView(ob));
			 
		}
		
		//proof if some elements have negative x,y-Components..
		Point p = this.getMainGraph().getSmallestPositionValue();
		if (p.x <0 || p.y < 0){
			int moveX = 0;
			int moveY = 0;
			if (p.x<0) moveX = -p.x;
			if (p.y<0) moveY = -p.y;
			this.getMainGraph().moveWholeGraph(moveX,moveY);
		}
	
		this.getMainGraph().setChanged(true);
		
	}
	
	/**
	    * called 
	    * - by actionPerformed,
	    * - when on exit graphs are unsaved or
	    * - by itself if user wants to save again if the graphname exists and the graph with the selected name is open
	    * - its also used, when sourcecode is displayed in Systemeditor
	    * 
	    * saves NavGraph ng to the File ng.saveName if ng.saveName exists or opens a fileSelectDialog
	    * if save is successful saveName is set otherwise it is not set.
	    * if ng is null, graphMain is saved.
	    * empty graphs are not saveable
	    * if saveAs is true  
	    */
	   public void save(NavGraph ng,boolean saveAs){
	   	
	   	if (ng==null) ng = this.graphMain; //
	   	
		if (ng.getMainComponentCells().size()==0){//&& (ng.getGraphCells().size()!=0)){
			// Graph is not empty, but there is no MainComponentCell. So it has no Graphtyp! 
			//so set Graphtyp.. 
			ng.getMetaInfos().setGraphType(MetaInfos.GRAPHTYPE_SECTION);

		}
		
		// proof if graph is empty
		if (ng.getMetaInfos().getGraphType()==MetaInfos.GRAPHTYPE_UNDEFINED){
			// dont save empty graphs
			JOptionPane.showMessageDialog(CCController.frame, "Graph ist leer", 
					  "Fehler beim Speichern", 
					  JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// proof if graph changed
		if (!ng.getChanged()){
			// dont save unchanged graphs
			if (!this.ccModel.getController().dialogNoChanges()) return;
		}
		String fileName ;
		String fileDir;
	   	if (ng.getMetaInfos().getSaveName()==null || saveAs){

	   		String fileNDir = this.ccModel.getController().getFileNameByDialog(true);
	   		fileName = fileNDir.substring(fileNDir.lastIndexOf("/")+1);
	   		fileDir =fileNDir.substring(0, fileNDir.length()-fileName.length());
	   	}
	   	else {
	   		fileName = ng.getMetaInfos().getSaveName();
	   		fileDir = ng.getMetaInfos().getSaveDir();
	   	}

			try {
			    if(!fileName.equals("")) {
			    	
					int opt = this.saveGraph(fileDir, fileName,ng, false);

					switch(opt){
						case 0: // everything is fine
							if (this.ccModel.getController().dialogSaveSucceed()) this.ccModel.getController().showCode(fileDir+ fileName);
					     	break;
						case -1: // graph is empty
							 JOptionPane.showMessageDialog(CCController.frame, "Graph ist leer", 
									  "Fehler beim Speichern", 
									  JOptionPane.INFORMATION_MESSAGE);
							break;
						case -2: this.save(ng,saveAs);// graph exists and is open but user wants to save again
							break;
						case -3: // graph exists and is open but user wants to abort
							break;
						case -4: // graph exists but user wants to overwrite 
							break;
						case -5: // graph exists and user wants to save under new name
							this.save(ng,true);
							break;
						default: ;
					}
			    } // end of if (!filename.equals("")) 
			} catch (IOException e) {
			    JOptionPane.showMessageDialog(CCController.frame, "Fehler beim lokalen Speichern:\n" 
							  + e.getMessage(), 
							  "Fehler beim Speichern", 
							  JOptionPane.ERROR_MESSAGE);
			}
			catch (TransformerException te) {
			    Throwable x = te;
			    if (te.getException() != null)
				x = te.getException();
			    x.printStackTrace();

			    JOptionPane.showMessageDialog(CCController.frame, "Fehler beim lokalen Speichern:\n" 
							  + x.toString(), 
							  "Fehler beim Speichern", 
							  JOptionPane.ERROR_MESSAGE);
			} // end of try-catch
			
	   }
	   
	
	public NavGraph getMainGraph(){return this.graphMain;}
	
	public CCModel getCCModel(){return this.ccModel;} 
	/**
	 * static method for bugtracking
	 * @param bab
	 */
	void babble(String bab){
		if (this.ccModel.getController().getConfig().getDEBUG_CCMODEL() && this.ccModel.getController().getConfig().getDEBUG()) System.out.println("CCModel:"+bab);
	}
}

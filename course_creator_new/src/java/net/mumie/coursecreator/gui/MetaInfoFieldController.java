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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JSpinner;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgraph.event.GraphSelectionEvent;
import com.jgraph.event.GraphSelectionListener;

import net.mumie.coursecreator.CCModel;
import net.mumie.coursecreator.CommandConstants;
import net.mumie.coursecreator.graph.cells.ComponentCell;
import net.mumie.coursecreator.graph.cells.MainComponentCell;


public class MetaInfoFieldController 
	implements ActionListener, CaretListener,ChangeListener,KeyListener, GraphSelectionListener,FocusListener
	{

	
	/** the metaInfoField this controller belongs to */
	private MetaInfoField metaInfoField; 
	
	public CCModel model;
	private ClassChooser classChooser;

	/**
	 * Controller for MetaInfoField
	 * @param mif MetaInfoField for which this is listener
	 * @param m CCModel to update MetaInfos
	 */
	public MetaInfoFieldController(MetaInfoField mif,CCModel m){
		this.metaInfoField = mif;
		this.model = m;
		
	}
	  /**
     * ActionListenerfunction for MetaInfoField when NameField, descriptionField or LabelField
     * changes
     * calls the UpdateMethode for name or description (if it was)
     * otherwise it sets the label
     */
    public void caretUpdate(CaretEvent e){
    	if ((e.getSource().equals(this.getMetaInfoField().getTxtfield_descr()))||
    			(e.getSource().equals(this.getMetaInfoField().getTxtfield_name()))){
	    	String newName = this.getMetaInfoField().getNameValue();
	    	String newDescr = this.getMetaInfoField().getDescrValue(); 
	    	
	//    	next line is very importent, because the nameField is "" while save 
	    	if (newName.equals("")||(newDescr.equals("")))return;
	    	
	    	if (e.getSource().equals(this.getMetaInfoField().getTxtfield_descr())){
	    		if (this.getMetaInfoField().getTxtfield_descr().isEditable())
	    			this.model.updateMetaInfosDescr(newDescr);
	    	}
	    	else if (e.getSource().equals(this.getMetaInfoField().getTxtfield_name())){
	    		if (this.getMetaInfoField().getTxtfield_name().isEditable()){
	    			this.model.updateMetaInfosName(newName);
	    		}
	    	}
    	}else if (e.getSource().equals(this.getMetaInfoField().getTxtfield_label())){
    		if (this.getMetaInfoField().getTxtfield_label().isEditable()){
    			if (this.model.getKursTyp()==CCModel.KURS_NAVGRAPH){
	    			((ComponentCell)this.model.getFirstGraph().getSelectionCell()).setLabel(this.getMetaInfoField().getLabelValue());
	    			this.getMetaInfoField().setLabelColor(getMetaInfoField().getLabelValue());
	    			this.model.getController().modelChanged(null);
	    			
	    			
	    			
	    			
	    			
	    			
	    			
	    			
	    			
    			}
    		}
    	}
    	
	}
    
	  /**
     * eventlistener for both date JSpinner 
     * calls the UpdateMethode for start or end-date
     */
    public void stateChanged(ChangeEvent e){
    	babble("stateChanged start "+e.getSource().equals(this.getMetaInfoField().getDateStartSpinner()));
    	babble("this.getMetaInfoField().getDateStartCal(): "+this.getMetaInfoField().getDateStartCal());
    	if (e.getSource().equals(this.getMetaInfoField().getDateStartSpinner()))
    		this.model.updateMetaInfosStart(this.getMetaInfoField().getDateStartCal());
    	else
    		if (e.getSource().equals(this.getMetaInfoField().getDateEndSpinner())) 
    	this.model.updateMetaInfosEnd(this.getMetaInfoField().getDateEndCal());
    		else if (e.getSource().equals(this.getMetaInfoField().getPointsSpinner())){
    			
    			if (this.model.getKursTyp()==CCModel.KURS_NAVGRAPH){// TODO anpassen an weitere Kurstypen
    				if (this.model.getFirstGraph().getSelectionCell() instanceof MainComponentCell){
//    					

    					((MainComponentCell)this.model.getFirstGraph().getSelectionCell()).setPoints(
    							this.getMetaInfoField().getPointsValue());
    					this.getMetaInfoField().setKomponentData();
    				}
    			}
    		}
    }
    
    /**
     * ActionEventFunction for Radiobuttons
     * Commands are like this:
     * 
     * "META_INFO_FIELD_ST_"+value 
     * with value is an number in (0,5) defined in MetaInfos beginning with ST_
     * or same with:
     * 
     * "META_INFO_FIELD_CAT_"+value
     * where value is given by MetaInfos.GRAPHTYPE_
     * 
     * last actions are set by classchooser// to call classChooser
     * 
     * Function then calls allways the model-UpdateMethode 
     */
    public void actionPerformed(ActionEvent e){
    	String cmd = e.getActionCommand(); 
    	
    	if ((cmd.length() > CommandConstants.META_INFO_FIELD_ST_.length())&&
    			cmd.startsWith(CommandConstants.META_INFO_FIELD_ST_)){//radiobutton switches in statusfield
    		int type = Integer.parseInt(cmd.substring(CommandConstants.META_INFO_FIELD_ST_.length()));
    		this.model.updateMetaInfos(CommandConstants.META_INFO_FIELD_ST_,type);
    			
    	}else if ((cmd.length() > CommandConstants.META_INFO_FIELD_CAT_.length())&&
    			cmd.startsWith(CommandConstants.META_INFO_FIELD_CAT_)){//radiobuttons switches in categoryfield
    		int type = Integer.parseInt(cmd.substring(CommandConstants.META_INFO_FIELD_CAT_.length()));
    		this.model.updateMetaInfos(CommandConstants.META_INFO_FIELD_CAT_,type);

    		
		}else if (cmd.equals(CommandConstants.META_INFO_FIELD_CLASS)) {
			//classChange button pressed
			this.classChooser = new ClassChooser(this, this.getMetaInfoField().getJapsclient());
			classChooser.setVisible(true);
		
		}else if (cmd.equals(CommandConstants.META_INFO_FIELD_OK)){
			// OK Button in classChooser Dialog pressed
			this.getMetaInfoField().setClassName(classChooser.getClassName());
			this.getMetaInfoField().setClassPath(classChooser.getClassPath());
			classChooser.setVisible(false);
			this.model.updateMetaInfos();
		
		}else if (cmd.equals(CommandConstants.META_INFO_FIELD_CANCEL)){
			// Cancel Button in classChooser Dialog pressed
			classChooser.setVisible(false);
		}else if (e.getSource().equals(this.metaInfoField.getCbPoints())){
			if (this.model.getKursTyp()==CCModel.KURS_NAVGRAPH){
				if (this.model.getFirstGraph().getSelectionCell() instanceof MainComponentCell){
					((MainComponentCell)this.model.getFirstGraph().getSelectionCell()).setHasPoints(
						this.getMetaInfoField().getCbPoints().isSelected());	
					this.getMetaInfoField().setKomponentData();
				}
				
			}
		}
    }
    
    public ClassChooser getClassChooser(){return this.classChooser;}
    
    void babble(String bab){
//    	if (this.model.getMainGraph().getCCController().config.getDEBUG_META_INFO_FIELD_CONTROLLER()
//    			&& this.model.getMainGraph().getCCController().config.getDEBUG())
//    		System.out.println("MetaInfoFieldController: "+bab);
    	}
	public MetaInfoField getMetaInfoField() {
		return metaInfoField;
	}
	
	
	
	public void keyPressed(KeyEvent e) {
		
		if (e.getSource().equals(((JSpinner.DefaultEditor)this.getMetaInfoField().getDateStartSpinner().getEditor()).getTextField())){
			babble("START");
			
			this.stateChanged(new ChangeEvent(this.getMetaInfoField().getDateStartSpinner()));
		}
		else if (e.getSource().equals(((JSpinner.DefaultEditor)this.getMetaInfoField().getDateEndSpinner().getEditor()).getTextField())){
			babble("END");
			this.stateChanged(new ChangeEvent(this.getMetaInfoField().getDateEndSpinner()));
		}else if (e.getSource().equals(((JSpinner.DefaultEditor)this.getMetaInfoField().getPointsSpinner().getEditor()).getTextField())){
			if (Character.isDigit( e.getKeyChar()))e.consume();
			else return;
			//TODO: hier noch string zusammenbauen!
			
			this.stateChanged(new  ChangeEvent( this.getMetaInfoField().getPointsSpinner()));
//			
			this.getMetaInfoField().setKomponentData();
			
		}
	}
	public void keyReleased(KeyEvent e) {

		babble("keyReleased "+e);
	}
	public void keyTyped(KeyEvent e) {

		babble("keyTyped "+e);
	}
	
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		babble("lostfocus");
		if (e.getSource().equals(((JSpinner.DefaultEditor)this.metaInfoField.getDateStartSpinner().getEditor()).getTextField())){
				babble("start");
			this.metaInfoField.resetDateStartString();
		}
	}
	
	public void valueChanged(GraphSelectionEvent arg0) {
		this.getMetaInfoField().setKomponentData();
	}
}

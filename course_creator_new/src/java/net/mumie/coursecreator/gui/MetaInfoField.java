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

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


import net.mumie.coursecreator.CCController;
import net.mumie.coursecreator.CCModel;
import net.mumie.coursecreator.CommandConstants;
import net.mumie.coursecreator.CourseCreator;
import net.mumie.coursecreator.graph.MetaInfos;
import net.mumie.coursecreator.graph.NavGraph;
import net.mumie.coursecreator.graph.cells.ComponentCell;
import net.mumie.coursecreator.graph.cells.ComponentCellConstants;
import net.mumie.coursecreator.graph.cells.MainComponentCell;
import net.mumie.coursecreator.tools.StringCutter;
import net.mumie.coursecreator.events.CCModelChangedEvent;
import net.mumie.coursecreator.events.CCModelChangeListener;

import net.mumie.japs.client.JapsClient;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * This class is responsible for the display and change of the Metainfos in the right bottom field of the CC.
 * It will change also the infos displayed in the CourseCreator concerning the name of the course
 * @author <a href="mailto:binder@math.tu-berlin.de>Jens Binder</a>
 * @author <a href="mailto:vrichter@math.tu-berlin.de>Verena Richter</a>
 * @version $Id: MetaInfoField.java,v 1.52 2009/03/30 12:40:17 vrichter Exp $
 */

public class MetaInfoField extends JPanel implements CCModelChangeListener{
	static final long serialVersionUID=0;
	
	private MetaInfos metas;
	private JFrame cc;
	
	private JTextField txtfield_name;
    private JTextField txtfield_descr;
    
    private JLabel txtfield_summary;
    private JLabel txtfield_filename;

    static int startVal;

    private JLabel txtfield_class;
    private JButton btClass;
    
    private JRadioButton rbPremature;
    private JRadioButton rbDevelOk;
    private JRadioButton rbContentOk;
    private JRadioButton rbComplete;
    private JRadioButton rbPubl;
    private JRadioButton rbFinal;
    private JRadioButton rbPrelearn;
    private JRadioButton rbHomework;
    private JRadioButton rbSelfTest;    
    
    private JSpinner dateStart;
    private JSpinner dateEnd;
    private String 	dateStartString;
    
    private JPanel panelNameDesc;
    private JPanel panelSumFile;
    private JPanel panelStatus;
    private JPanel panelClass;
    private JPanel panelTime;
    private JPanel panelCat;
    
    private JButton btShow;
    private JButton btChange;
    
    private JTextField tfLabel;
    private JSpinner sPoints;
    private JCheckBox cbPoints;
    private JapsClient japsclient=null;

    
    /** the actionListenerclass */
    private MetaInfoFieldController controller;
	
	public MetaInfoField(JFrame parentFrame, MetaInfos meta)
	{
		this.cc=parentFrame;
		this.controller = new MetaInfoFieldController(this,((CourseCreator)this.cc).getModel());
		this.metas=meta;
		this.buildLayout();
	}
	
	/**
	 * sets the JapsClient to communicate with the server
	 * @param jc - the japsclient which is to use for communication
	 */
	public void setJapsClient(JapsClient jc){
		this.japsclient=jc;
	}
	

	
	public void modelChanged(CCModelChangedEvent me){
		babble("MetaInfoField: ModelChanged "+me.getSource());
		
		NavGraph ng = (NavGraph) me.getChangedGraph();
		babble("changed Graph "+ng);
		if (ng != null){
			this.metas = ng.getMetaInfos();
		} else this.metas = new MetaInfos(this.metas.controller);
		babble("metas "+this.metas);
		
		this.enableButtons();
		this.setKomponentData();	
		
		if ((me.getType()==CCModelChangedEvent.CHANGED_GRAPHS)) 
			this.updateData();
	}
	
	
	/** enables / disables den show code button
	 */
	private void enableButtons(){
		if (this.metas.controller.getModel().getFirstGraph()==null)this.btShow.setEnabled(false);
		else
		this.btShow.setEnabled(
				this.metas.controller.getModel().getFirstGraph().getButtonEnable(CommandConstants.BUTTON_SHOWSOURCE));
		
		this.btChange.setEnabled(this.metas.controller.getModel().getFirstGraph()!=null);
	}


	
	/** setzt label und Punkte, bzw enabled/disabled die Felder
	 */
	protected void setKomponentData(){
		
		if ((((CourseCreator)this.cc).getModel().getFirstGraph()!=null)&&
				(((CourseCreator)this.cc).getModel().getFirstGraph().getSelectionCount()==1)){
			
			
			String label = "";
			int points = this.getPointsValue();
			boolean hasPoints =false;
			boolean hasLabel =false;
			Object sel = ((CourseCreator)this.cc).getModel().getFirstGraph().getSelectionCell();
			boolean pointsEnabled = false;
			
			if (((CourseCreator)this.cc).getModel().getKursTyp()== CCModel.KURS_NAVGRAPH){
				if (sel instanceof ComponentCell){
					label =((ComponentCell)sel).getLabel();
					
					hasLabel=true;
				}
				
				if (sel instanceof MainComponentCell&&((CourseCreator)this.cc).getModel().getFirstGraph().isProblemGraph()){ 
					hasPoints = ((MainComponentCell)sel).getHasPoints();
					if (hasPoints) points = ((MainComponentCell)sel).getPoints();
					
				}
				pointsEnabled = (((CourseCreator)this.cc).getModel().getFirstGraph().isProblemGraph()
					&& (sel instanceof MainComponentCell));
				if ((!hasPoints)||(!(sel instanceof MainComponentCell))){
					
				}

			}
			this.tfLabel.setEnabled(hasLabel);
			this.tfLabel.setEditable(hasLabel);
			
			this.tfLabel.setVisible(hasLabel);
//			this.tfLabel.removeCaretListener(this.controller);
			this.tfLabel.setText(label);
//			this.tfLabel.addCaretListener(this.controller);
			setLabelColor(label);
			
			setPointSpinnerColor(hasPoints);
			
			this.cbPoints.setEnabled(pointsEnabled);
			this.cbPoints.setSelected(hasPoints);
			this.cbPoints.setVisible(pointsEnabled);
			this.sPoints.setValue(points);
			this.sPoints.setEnabled(hasPoints);
			this.sPoints.setVisible(hasPoints);
			
		}else{
			this.tfLabel.setEnabled(false);
			this.tfLabel.setEditable(false);
			this.tfLabel.setVisible(false);
			this.cbPoints.setSelected(false);
			this.cbPoints.setEnabled(false);
			this.cbPoints.setVisible(false);
			this.sPoints.setEnabled(false);
			this.sPoints.setVisible(false);
			
		}
	}
	/**setzt die Farbe des labelfeldes auf rot, falls das 
	 * label ComponentCellConstants.DEFAULT_LABEL ist, sonst auf weiss
	 * @param label
	 */ 
	protected void setLabelColor(String label){
		if (label.equals(ComponentCellConstants.DEFAULT_LABEL)){
			tfLabel.setBackground(CommandConstants.COLOR_WARNING);
		}
		else {
			tfLabel.setBackground(Color.white);
		}
	}
	/** setzt die Farbe des pointspinners auf rot,
	 * falls keine Punkte gesetzt sind sonst weiss
	 * @param hasPoints
	 */
	protected void setPointSpinnerColor(boolean hasPoints) {
		if ((this.getPointsValue()==0)&& hasPoints){
			((JSpinner.DefaultEditor)sPoints.getEditor()).getTextField().setBackground(CommandConstants.COLOR_WARNING); 
		}
		else{
			((JSpinner.DefaultEditor)sPoints.getEditor()).getTextField().setBackground(Color.white);
		}
	}
	
	
	private void updateData()
	{
		babble("metas.getName() "+metas.getName());
		this.setNameValue(metas.getName());
		this.setDescrValue(metas.getDescription());
		this.setSummaryValue();
		this.setFilenameValue();
		
		switch(metas.getStatus()) {       
			case MetaInfos.ST_PRE : rbPremature.setSelected(true); break;
			case MetaInfos.ST_DEVEL_OK : rbDevelOk.setSelected(true); break;
			case MetaInfos.ST_CONTENT_OK : rbContentOk.setSelected(true); break;
			case MetaInfos.ST_CONTENT_COMPLETE : rbComplete.setSelected(true); break;
			case MetaInfos.ST_PUBLISHABLE : rbPubl.setSelected(true); break;
			case MetaInfos.ST_FINAL : rbFinal.setSelected(true); break;	
			default :  rbPremature.setSelected(true); break;
		}

		if (this.panelClass.isVisible()) {
			this.setClassValue();
			btClass.setEnabled(!this.metas.controller.getOffline());
		}
		
		if (this.panelTime.isVisible()) {
	
			try {
			//start end Time	
				Calendar cal = metas.getDateStartCal();

				if (cal ==null){
					cal = GregorianCalendar.getInstance();
					cal.setLenient(true);
					cal.roll(Calendar.HOUR_OF_DAY,true);
					cal.set(Calendar.MINUTE,0);
				}
				
				this.dateStart.getModel().setValue(cal.getTime());
			
				cal = metas.getDateEndCal();
				
				if (cal ==null){
					cal = GregorianCalendar.getInstance();
					cal.setLenient(true);
					cal.roll(Calendar.HOUR_OF_DAY,true);
					cal.set(Calendar.MINUTE,0);
				}
				
				this.dateEnd.getModel().setValue(cal.getTime());

				
			} catch (NumberFormatException nfe) {
				dateFormatErrorDialog(); 
			} // end of try-catch
			
			//category
			switch(metas.getGraphType()) {  
				case MetaInfos.GRAPHTYPE_HOMEWORK: this.rbHomework.setSelected(true); break;
				case MetaInfos.GRAPHTYPE_PRELEARN: this.rbPrelearn.setSelected(true); break;
				case MetaInfos.GRAPHTYPE_SELFTEST: this.rbSelfTest.setSelected(true); break;
			}
		}
		
		
	}
	
	/**
	 * set default for category for Problems. default is Homework.<br>
	 * it is only called, when first Problemcell is inserted.
	 */
	public void setDefaultCat(){this.rbHomework.setSelected(true);}
	
	/**
	 * set default status. default is pre.<br>
	 * it is only called, when first cell is inserted.
	 */
	public void setDefaultStatus(){this.rbPremature.setSelected(true);}
	
	/**
	 * returns simply the value of the name-Field
	 * @return String with the name of this Graph
	 */
	public String getNameValue(){return this.txtfield_name.getText();}
	
	/**
	 * sets value to textfield_name
	 * @param val new value for name-textfield 
	 */
	public void setNameValue(String val){
		this.txtfield_name.setText(val);}

	
	/**
	 * returns simply the value of the desc-Field
	 * @return String with the description of this Graph
	 */
	public String getDescrValue(){ return this.txtfield_descr.getText(); }
	
	/**
	 * sets value to textfield_descr
	 * @param val new value for descriptiontextfield 
	 */
	public void setDescrValue(String val){this.txtfield_descr.setText(val);}
	
	/**
	 * returns simply the value of the label-Field
	 * @return String with the label of the selected cell
	 */
	public String getLabelValue(){ return this.tfLabel.getText(); }
	
	/**
	 * returns simply the value of the point-spinner
	 * @return Points of the selected cell
	 */
	public int getPointsValue(){ return ((SpinnerNumberModel)this.sPoints.getModel()).getNumber().intValue(); }
	
	
	/**
	 * sets Summary value from metaInfos to field
	 * if no value exists entry is 'no summary'
	 * shriks summary 
	 */
	public void setSummaryValue(){
		String val = null;
		if (metas.getSummaryPath()!= null&& metas.getSummaryPath()!=""){
			
			val = metas.getSummaryPath();
			this.txtfield_summary.setToolTipText(
					StringCutter.cutEquidistant(val, "/", 80, "<br>", "<html>", "<html>"));
						
	    	val = StringCutter.cut(val,"/", this.txtfield_summary.getSize().getWidth()-20,
	    			this.getGraphics().getFontMetrics(),"..",true);

		}else {
			val = "kein Summary gesetzt";
			this.txtfield_summary.setToolTipText("");
		}
		this.txtfield_summary.setText(val);
	}

	/**
	 * sets className to MetaInfos and to txtfiled_class.text
	 * @param val the new ClassName
	 */
	public void setClassName(String val){
		if (val.equals("")) return;
		this.metas.setClassName(val);
		this.txtfield_class.setText(val);
	}
	
	/**
	 * sets classPath to MetaInfos and to txtfiled_class.tooltiptext
	 * @param val the new ClassPath
	 */
	public void setClassPath(String path){
		if (path.equals("")) return;
		this.metas.setClassPath(path);
		this.txtfield_class.setToolTipText(path);
	}
		
	/**
	 * sets Class value from metaInfos to field
	 * for tooltiptext and textField
	 */
	public void setClassValue(){
		this.txtfield_class.setToolTipText(metas.getClassPath());
		this.txtfield_class.setText(this.metas.getClassName());
	}
	/**
	 * sets filename value from MetaInfos to field
	 * adds '.meta.xml' to filename
	 * if no Filename exists output is 'file not saved'
	 *
	 */
	public void setFilenameValue(){
		String val = null; 
		if (metas.getSaveName()!=null && this.metas.getSaveName()!="")
			val = metas.getSaveName()+".meta.xml";
		else val = "Datei nicht gespeichert";
		this.txtfield_filename.setText(val);
		if (metas.getSaveDir()!=null){
		this.txtfield_filename.setToolTipText(
				StringCutter.cutEquidistant(metas.getSaveDir(), "/", 60, "<br>", "<html>Verzeichnis:<br>", "<html>"));
		
		}
	}
	
	/**
	 * returns the value of the DateEnd-Field
	 * @return Calendar with the dateEnd-Value
	 */
	public Calendar getDateEndCal(){
		return MetaInfos.newCalendar(((SpinnerDateModel)this.dateEnd.getModel()).getDate().getTime());
	}
	
	/**
	 * returns the value of the DateStart-Field
	 * @return Calendar with the dateStart-Value
	 */
	public Calendar getDateStartCal(){
		return MetaInfos.newCalendar(((SpinnerDateModel)this.dateStart.getModel()).getDate().getTime()); 
	}

	/**
	 * returns status of this graph
	 * if none is selected return is  MetaInfos.ST_PRE
	 * @return status of the mainGraph but  MetaInfos.ST_PRE if no status is set.
	 */
	public int getStatus(){
		if(rbFinal.isSelected()) 	return MetaInfos.ST_FINAL;
		if(rbDevelOk.isSelected()) 	return MetaInfos.ST_DEVEL_OK;
		if(rbContentOk.isSelected())return MetaInfos.ST_CONTENT_OK;
		if(rbComplete.isSelected())	return MetaInfos.ST_CONTENT_COMPLETE;
		if(rbPubl.isSelected())		return MetaInfos.ST_PUBLISHABLE;

		return MetaInfos.ST_PRE;
		
	}
	/**
	 * returns graphtype of this graph !Attention: graphtype only makes sence if probemgraph!
	 * if none is selected return is  MetaInfos.GRAPHTYPE_HOMEWORK
	 * @return graphtype of the mainGraph but MetaInfos.GRAPHTYPE_HOMEWORK if no graph is set.
	 */
	public int getGraphType(){

		if (rbPrelearn.isSelected()) return MetaInfos.GRAPHTYPE_PRELEARN;
		if (rbSelfTest.isSelected()) return MetaInfos.GRAPHTYPE_SELFTEST;
		return MetaInfos.GRAPHTYPE_HOMEWORK;

	}
	/**
	 * This method reads all the infos put into the the SWING-Fields and build a
	 * <code>MetaInfos</class> out of it. This method should be called everytime BEFORE
	 * a new Graph is established in order to save all the information in the 
	 * <code>NavGraph</code>
	 * @return <code>MetaInfos</code> class constructed from the information given by the user
	 */
	public MetaInfos getMetaInfos(){
		this.metas.setName(this.getNameValue());
		this.metas.setDescr(this.getDescrValue());
		this.metas.setStatus(this.getStatus());
		
		if (this.panelClass.isVisible()){
			this.metas.setClassPath(this.txtfield_class.getToolTipText());
			this.metas.setClassName(this.txtfield_class.getText());
		}
		if (this.panelTime.isVisible()) {	
			try {
				if (!this.metas.isSameDate(this.metas.getDateStartCal(),((SpinnerDateModel)this.dateStart.getModel()).getDate()))
				{
					babble
					("actionPerformed(): isSameDate() returned false for dateStart");			    
					Calendar cal = MetaInfos.newCalendar(((SpinnerDateModel)this.dateStart.getModel()).getDate().getTime());
					this.metas.setDateStartCal(cal);
				}
				
				
			} catch (NumberFormatException nfe) {
				dateFormatErrorDialog(); 
			} // end of try-catch
			try {		    
				if (!this.metas.isSameDate(this.metas.getDateEndCal(),((SpinnerDateModel)this.dateEnd.getModel()).getDate()))
				{
					babble
					("actionPerformed(): isSameDate() returned false for dateEnd");
					Calendar cal = MetaInfos.newCalendar(((SpinnerDateModel)this.dateEnd.getModel()).getDate().getTime());
					this.metas.setDateEndCal(cal);
				}
				
			} catch (NumberFormatException nfex) {
				dateFormatErrorDialog(); 	
			}
			
			if (this.metas.getDateStartCal().after(this.metas.getDateEndCal())) {
				dateErrorDialog("Abgabedatum liegt vor dem Ausgabedatum!");
			} 
			
			this.metas.setGraphType(this.getGraphType());
			
		}		
		return this.metas;
	}
	
	/**
	 * This method shows an error Dialog in case the DateFormat is wrong 
	 *
	 */
	private void dateFormatErrorDialog(){		
		JOptionPane.showMessageDialog(this,
				"Fehlerhaftes Abgabe- oder Anfangsdatum: es d\u00fcrfen nur Zahlen vorkommen!",
				"Falsches Datumsformat", 
				JOptionPane.ERROR_MESSAGE
		);
	}
	
	private void dateErrorDialog(String msg){
		
		JOptionPane.showMessageDialog(this, msg, "Falsches Datum", 
				JOptionPane.ERROR_MESSAGE);
	}
	/**
	 * Enables the name and desc panel
	 * @param b
	 */
	public void enableNameDescPanel(boolean b) {
		
		this.txtfield_name.setEditable(b);
		this.txtfield_descr.setEditable(b);
		
		
	}
	
	
	/** makes an update of the ELClass-Combo Box in case data is changing */
	public void enableELClass(){
		this.btClass.setEnabled(!this.metas.controller.getOffline());
	}

	/** makes an update of the ELClass-Combo Box in case data is changing */
	public void enableELClass(boolean b){
		this.btClass.setEnabled(b);
	}
	
	/**
	 * Enables the status panel
	 * @param b
	 */
	public void enableStatusPanel(boolean b) {
		Component[] content = this.panelStatus.getComponents();
		for (int i = 0; i < content.length; i++) {
			content[i].setEnabled(b);
		}

		
	}
	/** Enables the CLPanel for choosing a Lehrveranstaltung
	 * @param b */
	public void visibleEnableCLPanel(boolean b) {
		this.panelClass.setEnabled(b);
		this.panelClass.setVisible(b);
	}
	
	/** enables CategoryFrame
	 * CategoryFrame is need for ProblemGraphs
	 * @param b */
	public void visibleEnableCategoryFrame(boolean b){ 
		this.panelCat.setEnabled(b);
		this.panelCat.setVisible(b);
	}
	
	/** enables and sets DateFrame visible
	 * TimeFrame is need, if Graph is a ProblemGraph,
	 * but not if it is a Selftest
	 * @param b */
	public void visibleEnableDateFrame(boolean b){ 
		this.panelTime.setEnabled(b);
		this.panelTime.setVisible(b);
	}

	
	/**
	 * creates the radioButtons for the Statusgroup
	 * @param title the Title for the radioButton
	 * @param toolTippText the ToolTippText
	 * @param cmd the actionCommand
	 * @return
	 */
	private JRadioButton createStatusRadioButtons(String title,String toolTippText,String type, int cmd){

		JRadioButton rb = new JRadioButton(title);
		rb.setBackground(cc.getBackground());
		rb.setToolTipText(toolTippText);
		rb.addActionListener(this.controller);
		rb.setActionCommand(type.concat(String.valueOf(cmd)));
		return rb;
	}
	
	/** sets for a Component Minimum-, Preferred-, and MaximumSize */
	private void setFixedSize(JComponent comp, Dimension d){
		comp.setMinimumSize(d);
		comp.setPreferredSize(d);
		comp.setMaximumSize(d);
	}
	
	/**
	 * build the layout of this panel
	 *
	 */
	private void buildLayout(){
		int width = 400;
		
		//Textfield Dimensions
		Dimension d_20 = new Dimension(width,20);
		Dimension d_50 = new Dimension(width,50);
		Dimension d_55 = new Dimension(width,55);
		Dimension d_60 = new Dimension(width,60);
		Dimension d_75 = new Dimension(width,75);
		
		
		this.setBackground(cc.getBackground());
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

		// text field for name
		this.panelNameDesc=this.createNameAndDescriptionPanel(d_60);
		this.add(this.panelNameDesc);
		
		this.panelSumFile = createSummaryAndFileNamePane(d_60);
		this.add(this.panelSumFile);
		
		this.panelStatus = createStatusPanel(d_75);
		this.add(this.panelStatus);
		
		//CATEGORY-PANEL catPanel
		// course section category
		this.panelCat = createCatPanel(d_50);	    
		this.add(panelCat);
		
		panelCat.setEnabled(false);
		panelCat.setVisible(false);
		
		this.panelTime= this.createTimeFramePanel(d_75);
		this.add(panelTime);
		
		panelClass = this.createClassPanel(d_50);
		this.add(panelClass);
		panelClass.setEnabled(false);
		panelClass.setVisible(false);
		
		JPanel platzhalter = new JPanel();
		this.setFixedSize(platzhalter, d_20);
		this.add(platzhalter);
		
		this.add(createKomponentDataPanel(d_55));
		this.enableButtons();
		this.setKomponentData();
	}

	/**
	 * @param d
	 */
	private JPanel createCatPanel(Dimension d) {
		JPanel p = new JPanel();
		p.setBackground(cc.getBackground());
		p.setLayout(new GridLayout(1,2));
		p.setMinimumSize(d);
		p.setPreferredSize(d);
		p.setMaximumSize(d);
		
		p.setBorder(new TitledBorder(new EtchedBorder(),"Kategorie"));
		
		ButtonGroup catbg =  new ButtonGroup();

		rbPrelearn = this.createStatusRadioButtons("Pre-Learning", "vorbereitende \u00dcbung",
				CommandConstants.META_INFO_FIELD_CAT_,MetaInfos.GRAPHTYPE_PRELEARN);
		p.add(rbPrelearn);
		catbg.add(rbPrelearn);
		
		rbHomework = this.createStatusRadioButtons("Homework","nachbereitende \u00dcbung",
				CommandConstants.META_INFO_FIELD_CAT_,MetaInfos.GRAPHTYPE_HOMEWORK);
		p.add(rbHomework);
		catbg.add(rbHomework);	
		
		rbSelfTest = this.createStatusRadioButtons("Self-Test","ein Selbsttest",
				CommandConstants.META_INFO_FIELD_CAT_,MetaInfos.GRAPHTYPE_SELFTEST);
		p.add(rbSelfTest);
		catbg.add(rbSelfTest);
		return p;
	}

	/**
	 * @param d
	 * @return
	 */
	private JPanel createStatusPanel(Dimension d) {
		JPanel p = new JPanel();
		p.setBackground(cc.getBackground());
		p.setBorder(new TitledBorder(new EtchedBorder(),"Status"));
		p.setLayout(new GridLayout(2,3));
		
		p.setMinimumSize(d);
		p.setPreferredSize(d);
		p.setMaximumSize(d);

		ButtonGroup bg = new ButtonGroup();

		rbPremature = createStatusRadioButtons("vorl\u00e4ufig","pre",
				CommandConstants.META_INFO_FIELD_ST_,MetaInfos.ST_PRE);
		p.add(rbPremature);
		bg.add(rbPremature);

		rbDevelOk = this.createStatusRadioButtons("Entwurf","devel_ok",
				CommandConstants.META_INFO_FIELD_ST_,MetaInfos.ST_DEVEL_OK);
		p.add(rbDevelOk);
		bg.add(rbDevelOk);

		rbContentOk = this.createStatusRadioButtons("inhaltlich ok","content_ok",
				CommandConstants.META_INFO_FIELD_ST_,MetaInfos.ST_CONTENT_OK);
		p.add(rbContentOk);
		bg.add(rbContentOk);

		rbComplete = this.createStatusRadioButtons("inhaltlich vollst\u00e4ndig","content_complete",
				CommandConstants.META_INFO_FIELD_ST_,MetaInfos.ST_CONTENT_COMPLETE);
		p.add(rbComplete);
		bg.add(rbComplete);

		rbPubl = this.createStatusRadioButtons("freigegeben","ok_for_publication",
				CommandConstants.META_INFO_FIELD_ST_,MetaInfos.ST_PUBLISHABLE);
		p.add(rbPubl);
		bg.add(rbPubl);

		rbFinal = this.createStatusRadioButtons("endg\u00fcltig","final",
				CommandConstants.META_INFO_FIELD_ST_,MetaInfos.ST_FINAL);
		p.add(rbFinal);
		bg.add(rbFinal);
		return p;
	}

	/**
	 * Creates the TimeFramePanel
	 * needs width
	 * @param width
	 */
	private JPanel createTimeFramePanel(Dimension d) {
		//TIME FRAME PANEL tfpanel
		 
		JPanel p = new JPanel();
		// text fields for time frame 
		p.setBackground(cc.getBackground());
		p.setLayout(new GridLayout(2,1));
		p.setMinimumSize(d);
		p.setMaximumSize(d);
		
		p.setBorder(new TitledBorder(new EtchedBorder(),"Bearbeitungszeitraum"));
		// zu niedrig, sonst o.k. -- ggf. EmptyBorder statt EtchedBorder
		JLabel lblStartD = new JLabel("Ausgabe: ");
		lblStartD.setBackground(cc.getBackground());
		p.add(lblStartD);
		
		dateStart = new JSpinner(new SpinnerDateModel());
		dateStart.setEditor(new JSpinner.DateEditor(dateStart,"dd.MM.yyyy HH:mm"));
		dateStart.addChangeListener(this.controller);
		((JSpinner.DefaultEditor)dateStart.getEditor()).getTextField().addKeyListener(this.controller);
		((JSpinner.DefaultEditor)dateStart.getEditor()).getTextField().addFocusListener(this.controller);
		
		Calendar cal = MetaInfos.newCalendar(((SpinnerDateModel)this.dateStart.getModel()).getDate().getTime());
		this.metas.setDateStartCal(cal);
		
		p.add(dateStart);    
		
		JLabel lblDueD = new JLabel("Abgabe: ");
		lblDueD.setBackground(cc.getBackground());
		p.add(lblDueD);
		
		dateEnd = new JSpinner(new SpinnerDateModel());
		dateEnd.setEditor(new JSpinner.DateEditor(dateEnd,"dd.MM.yyyy HH:mm"));
		dateEnd.addChangeListener(this.controller);
		((JSpinner.DefaultEditor)dateEnd.getEditor()).getTextField().addKeyListener(this.controller);
		((JSpinner.DefaultEditor)dateEnd.getEditor()).getTextField().addFocusListener(this.controller);
		
		this.metas.setDateEndCal(MetaInfos.newCalendar(
				((SpinnerDateModel)this.dateEnd.getModel()).getDate().getTime()));
		
		p.add(dateEnd);
		
		p.setEnabled(false);
		p.setVisible(false);
		return p;
	}
	private JPanel createKomponentDataPanel(Dimension d) {
		JPanel p = new JPanel();
		JPanel plinks = new JPanel();
		JPanel prechts = new JPanel();
		p.setBackground(cc.getBackground());
		p.setLayout(new GridLayout(1,2));
		p.setMinimumSize(d);
		p.setMaximumSize(d);
		
		p.setBorder(new TitledBorder(new EtchedBorder(),"Daten zur markierten Komponente"));
		
		JLabel lLabel = new JLabel("Label: ");
		
		tfLabel = new JTextField(ComponentCellConstants.DEFAULT_LABEL);
		lLabel.setLabelFor(tfLabel);
		tfLabel.addCaretListener(this.controller);
		
		plinks.setLayout(new BorderLayout());
		plinks.setBorder(new EtchedBorder());
		plinks.add(lLabel,BorderLayout.WEST);
		plinks.add(tfLabel,BorderLayout.CENTER);
		
		
		SpinnerNumberModel modelPoints = new SpinnerNumberModel( 0, 0, Integer.MAX_VALUE, 1 ); 
		sPoints = new JSpinner(modelPoints);
		this.setFixedSize(sPoints,new Dimension(50, 5));
		sPoints.addChangeListener(this.controller);
		((JSpinner.DefaultEditor)sPoints.getEditor()).getTextField().addKeyListener(this.controller);
		cbPoints = new JCheckBox("Punkte setzen");
		cbPoints.addActionListener(this.controller);
		prechts.setLayout(new BorderLayout());
		prechts.setBorder(new EtchedBorder());
		prechts.add(cbPoints,BorderLayout.WEST);
		prechts.add(sPoints,BorderLayout.EAST);
		
		p.add(plinks);
		p.add(prechts);
		
		return p;
		
	}
	/**
	 * Creates style
	 * @param anchor GridBagConstraints.anchor
	 * @param insetsTop GridBagConstraints.insets.top
	 * @param insetsRight GridBagConstraints.insets.right
	 * @param insetsBottom GridBagConstraints.insets.bottom
	 * @param insetsleft GridBagConstraints.insets.left
	 * @param x GridBagConstraints.gridx
	 * @param y GridBagConstraints.gridy
	 * @return new GridBagConstraints
	 */
	private GridBagConstraints createGridBagContrains(
							int anchor,
							int insetsTop,
							int insetsRight,
							int insetsBottom,
							int insetsleft,
							int x,
							int y,
							double weightX) {
		GridBagConstraints compStyle = new GridBagConstraints();
	    compStyle.anchor = anchor;
	    compStyle.insets.top = insetsTop;
	    compStyle.insets.right = insetsRight;
	    compStyle.insets.bottom = insetsBottom;
	    compStyle.insets.left = insetsleft;
	    compStyle.gridx = x;
	    compStyle.gridy = y;
	    compStyle.weightx = weightX;
		return compStyle;
	}
	
	/**
	 * Creates style
	 * @param anchor GridBagConstraints.anchor
	 * @param insetsTop GridBagConstraints.insets.top
	 * @param insetsRight GridBagConstraints.insets.right
	 * @param insetsBottom GridBagConstraints.insets.bottom
	 * @param insetsleft GridBagConstraints.insets.left
	 * @return new GridBagConstraints
	 */
	private GridBagConstraints createGridBagContrains(
							int anchor,
							int insetsTop,
							int insetsRight,
							int insetsBottom,
							int insetsleft,
							double weightX) {
		GridBagConstraints compStyle = new GridBagConstraints();
	    compStyle.anchor = anchor;
	    compStyle.insets.top = insetsTop;
	    compStyle.insets.right = insetsRight;
	    compStyle.insets.bottom = insetsBottom;
	    compStyle.insets.left = insetsleft;
	    compStyle.weightx = weightX;
		return compStyle;
	}
	
	/**
	 * creates the summary and FileNamePanel
	 * @param d
	 * @return
	 */
	private JPanel createSummaryAndFileNamePane(Dimension d) {
		
		Dimension dLabelSmall = new Dimension(75,20);
		JPanel summaryAndFilenamePanel = new JPanel();
	
		// GridBagConstraints for labels and fields:
		    GridBagConstraints labelSummaryStyle =this.createGridBagContrains(
		    			GridBagConstraints.WEST, 0, 0, 0, 4, 0, 0, 0); 
		    
		    GridBagConstraints labelFileNameStyle = this.createGridBagContrains(
    				GridBagConstraints.WEST, 0, 0, 4, 4, 0, 1, 0);

		    GridBagConstraints fieldSummaryStyle =this.createGridBagContrains(
		    			GridBagConstraints.WEST, 0, 0, 0, 0, 1, 0, 0); 
		    fieldSummaryStyle.gridwidth = GridBagConstraints.REMAINDER;
		    
		    GridBagConstraints fieldFileNameStyle = this.createGridBagContrains(
		    			GridBagConstraints.WEST, 0, 0, 4, 0, 1, 1, 1);
		    fieldFileNameStyle.gridwidth = GridBagConstraints.REMAINDER;
		    
		    GridBagConstraints buttonStyleChange = this.createGridBagContrains(
	    			GridBagConstraints.EAST, 4, 6, 0, 0, 2, 0, 0); 
		    
		    GridBagConstraints buttonStyleShow = this.createGridBagContrains(
	    			GridBagConstraints.EAST, 4, 6, 0, 0, 2, 1, 0);
		    
		summaryAndFilenamePanel.setBackground(cc.getBackground());
		summaryAndFilenamePanel.setLayout(new GridBagLayout());
		summaryAndFilenamePanel.setBorder(new EtchedBorder());
		this.setFixedSize(summaryAndFilenamePanel, d);

		JLabel lblSummary = new JLabel("Summary:");
		txtfield_summary = new JLabel();
		
		try {
			ImageIcon icon = new ImageIcon(
					CourseCreator.loadImage(new URI(
					CourseCreator.MM_BUILD_PREFIX 
					+"/share/coursecreator/pics/change.png")));
			btChange = new JButton(icon);
			this.setFixedSize(btChange, new Dimension(icon.getIconWidth()+4,icon.getIconHeight()+4));
		} catch (URISyntaxException e) {
			CCController.dialogErrorOccured(
					"CourseCreator URISyntaxException",
					"CourseCreator URISyntaxException: "+e,
					JOptionPane.WARNING_MESSAGE);
			btChange = new JButton("Change");
			this.setFixedSize(btChange, new Dimension(85,20));
		} catch (IOException e) {
			CCController.dialogErrorOccured(
					"CourseCreator IOException",
					"CourseCreator IOException: "+e,
					JOptionPane.WARNING_MESSAGE);
			btChange = new JButton("Change");
			this.setFixedSize(btChange, new Dimension(85,20));
		}
		
		btChange.addActionListener(this.metas.controller);
		btChange.setActionCommand(CommandConstants.SHOW_ELEMENTSELECTOR);
		btChange.setEnabled(true);
		
		
		try {
			ImageIcon icon = new ImageIcon(
					CourseCreator.loadImage(new URI(
					CourseCreator.MM_BUILD_PREFIX 
					+"/share/coursecreator/pics/show.png")));
			btShow = new JButton(icon);
			this.setFixedSize(btShow, new Dimension(icon.getIconWidth()+4,icon.getIconHeight()+4));
		} catch (URISyntaxException e) {
			CCController.dialogErrorOccured(
					"CourseCreator URISyntaxException",
					"CourseCreator URISyntaxException: "+e,
					JOptionPane.WARNING_MESSAGE);
			btShow = new JButton("Show");
			this.setFixedSize(btShow, new Dimension(85,20));
		} catch (IOException e) {
			CCController.dialogErrorOccured(
					"CourseCreator IOException",
					"CourseCreator IOException: "+e,
					JOptionPane.WARNING_MESSAGE);
			btShow = new JButton("Show");
			this.setFixedSize(btShow, new Dimension(85,20));
		}
		
		btShow.addActionListener(this.metas.controller);
		btShow.setActionCommand(CommandConstants.SHOW_FILESOURCE);
		btShow.setEnabled(false);
		
		
		this.setFixedSize(lblSummary,dLabelSmall);
		summaryAndFilenamePanel.add(lblSummary,labelSummaryStyle);
		txtfield_summary.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		this.setSummaryValue();
		summaryAndFilenamePanel.add(txtfield_summary,fieldSummaryStyle);
		summaryAndFilenamePanel.add(btChange,buttonStyleChange);
		
		JLabel lblFileName = new JLabel("Dateiname:");
		this.setFixedSize(lblFileName,dLabelSmall);
		lblFileName.setBackground(cc.getBackground());
		summaryAndFilenamePanel.add(lblFileName,labelFileNameStyle);
		
				
		txtfield_filename= new JLabel();
		this.setFilenameValue();
		summaryAndFilenamePanel.add(txtfield_filename,fieldFileNameStyle);
		summaryAndFilenamePanel.add(btShow,buttonStyleShow);
		
		return summaryAndFilenamePanel;
	}
	
	
	private JPanel createNameAndDescriptionPanel(Dimension d){
		Dimension dlabel = new Dimension(90,25);
		Dimension dEdit = new Dimension(d.width-95,d.height/2-6);
		GridBagConstraints labelNameStyle =this.createGridBagContrains(
				GridBagConstraints.WEST, 4, 0, 0, 0, 0, 0, 0);
		    
		GridBagConstraints editNameStyle = this.createGridBagContrains(
				GridBagConstraints.EAST, 4, 4, 0, 0, 1, 0, 1);
		
		GridBagConstraints labelDiscriptionStyle =this.createGridBagContrains(
				GridBagConstraints.WEST, 0, 0, 4, 0, 0, 1, 0);
		    
		GridBagConstraints editDiscriptionStyle = this.createGridBagContrains(
				GridBagConstraints.EAST, 0, 4, 4, 0,  1);
		editDiscriptionStyle.gridy=1;
		editDiscriptionStyle.gridwidth =GridBagConstraints.REMAINDER;
		    
		JPanel nameAndDescriptionPanel	= new JPanel();
		nameAndDescriptionPanel.setLayout(new GridBagLayout());
		nameAndDescriptionPanel.setBorder(new EtchedBorder());
		this.setFixedSize(nameAndDescriptionPanel,d);
		
		JLabel lblName = new JLabel("Name:");
		this.setFixedSize(lblName, dlabel);
		nameAndDescriptionPanel.add(lblName,labelNameStyle);

		txtfield_name = new JTextField(metas.getName());
		this.setFixedSize(txtfield_name,dEdit);
		txtfield_name.addCaretListener(this.controller);
		nameAndDescriptionPanel.add(txtfield_name, editNameStyle);

		// text area for description
		JLabel lblDescr = new JLabel("Beschreibung:");
		this.setFixedSize(lblDescr, dlabel);
		nameAndDescriptionPanel.add(lblDescr,labelDiscriptionStyle);

		txtfield_descr = new JTextField(metas.getDescription());
		this.setFixedSize(txtfield_descr,dEdit);
		txtfield_descr.addCaretListener(this.controller);
		
		nameAndDescriptionPanel.add(txtfield_descr, editDiscriptionStyle);
		    
		    
		return nameAndDescriptionPanel;
	}
	
	private JPanel createClassPanel(Dimension d){
		JPanel classPanel = new JPanel();
	
		// GridBagConstraints for labels and fields:
		    GridBagConstraints pathStyle =this.createGridBagContrains(
		    		GridBagConstraints.WEST, 4, 0, 4, 0, 0, 0, 1);
		    
		    GridBagConstraints buttonStyle = 
		    	this.createGridBagContrains(
			    		GridBagConstraints.EAST, 4, 4, 4, 0, 1, 0, 0);
		    
		
		classPanel.setLayout(new GridBagLayout());
		classPanel.setBorder(new TitledBorder(new EtchedBorder(),"Lehrveranstaltung"));
		this.setFixedSize(classPanel, d);

		txtfield_class = new JLabel();
		this.setClassValue();
		classPanel.add(txtfield_class,pathStyle);
	
		try{
			ImageIcon icon = new ImageIcon(
					CourseCreator.loadImage(new URI(
					CourseCreator.MM_BUILD_PREFIX 
					+"/share/coursecreator/pics/change.png")));
		
			btClass = new JButton(icon);
			this.setFixedSize(btClass, new Dimension(icon.getIconWidth()+4,icon.getIconHeight()+4));
		} catch (URISyntaxException e) {
			CCController.dialogErrorOccured(
					"CourseCreator URISyntaxException",
					"CourseCreator URISyntaxException: "+e,
					JOptionPane.WARNING_MESSAGE);
			btClass = new JButton("Change");
			this.setFixedSize(btClass, new Dimension(85,20));
		} catch (IOException e) {
			CCController.dialogErrorOccured(
					"CourseCreator IOException",
					"CourseCreator IOException: "+e,
					JOptionPane.WARNING_MESSAGE);
			btClass = new JButton("Change");
			this.setFixedSize(btClass, new Dimension(85,20));
		}
		
		btClass.addActionListener(this.controller);
		btClass.setActionCommand(CommandConstants.META_INFO_FIELD_CLASS);
		btClass.setEnabled(!this.metas.controller.getOffline());
		classPanel.add(btClass,buttonStyle);
		
		return classPanel;
	}
	
	public JSpinner getDateStartSpinner() {
		return dateStart;
	}
    
    public JSpinner getDateEndSpinner() {
		return dateEnd;
	}
    
    public JSpinner getPointsSpinner() {
		return sPoints;
	}
    
    public JTextField getTxtfield_descr() {
		return txtfield_descr;
	}

    public JTextField getTxtfield_label() {
		return tfLabel;
	}
    
    public JCheckBox getCbPoints() {
		return cbPoints;
	}  
	public JTextField getTxtfield_name() {
		return txtfield_name;
	}

	void babble(String bab){
		if (this.metas.controller.getConfig().getDEBUG() 
				&& this.metas.controller.getConfig().getDEBUG_META_INFO_FIELD())
			System.out.println("MetaInfoField: "+bab);
	}

	public JLabel getTxtfield_class() {
		return txtfield_class;
	}

	public JapsClient getJapsclient() {
		return japsclient;
	}
	
	/** @return the ClassChooser Dialog */
	public ClassChooser getClassChooser(){
		return this.controller.getClassChooser();
	}

	public String getDateStartString() {
		return dateStartString;
	}

	public void setDateStartString(String dateStartString) {
		this.dateStartString = dateStartString;
	}

	public void resetDateStartString() {
		babble(" " +((SpinnerDateModel)this.dateStart.getModel()).getValue());

		babble(" " +this.dateStartString);
		this.dateStartString = this.metas.getFormattedDate(this.getDateStartCal());
		babble(" " +this.dateStartString);
	}
	public MetaInfoFieldController getController(){
		return this.controller;
	}
}

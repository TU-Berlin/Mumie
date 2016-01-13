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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import net.mumie.coursecreator.CCController;

/**
 * this class implements the MetaInfopanel of the Treenavigation frame. Same class is used for the search panel.
 * it implements its own actionlistener, but saerchfunctions are in ElementTreeSearchDialog.
 * @author <a href="mailto:vrichter@math.tu-berlin.de">Verena Richter</a>
 *
 */
public class ElementMetaPanel extends JPanel implements ActionListener{
	static final long serialVersionUID=0;
	
	CCController controller;
	
	public JTextField txtfield_title;
	public JTextField txtfield_desc;
	public JTextField txtfield_file;
	public JTextField txtfield_pure;
	public JTextField txtfield_id;
	public JTextField txtfield_typ;
	public JTextField txtfield_cat;
	public JTextField txtfield_section;
	public JTextField txtfield_created;
	public JTextField txtfield_modified;
	
	private CCSearchSelector ccssTitle;
	private CCSearchSelector ccssDesc;
	private CCSearchSelector ccssFile;
	private CCSearchSelector ccssPure;
	private CCSearchSelector ccssId;
	private CCSearchSelector ccssTyp;
	private CCSearchSelector ccssCat;
	private CCSearchSelector ccssSection;
	private CCSearchSelector ccssCreated;
	private CCSearchSelector ccssModified;
	
	
	static public String IGNORE	= "ignore";
	static public String AND		= "and";
	static public String NOT		= "not";
	
	/** this specifies the combobox */
	class CCSearchSelector extends JComboBox {
		static final long serialVersionUID=0;
    	
		
		public CCSearchSelector(ActionListener a){	
			String[] select = {AND, NOT ,IGNORE}; 
			for ( String s : select) this.addItem( s );
			this.addActionListener(a);
						
		}
	}
    	
	private int txtfieldHeight = 15;
	    
	public ElementMetaPanel(CCController con){
		this.controller = con;
		this.buildLayout();
		
		
	}
	
	
	////////////////////////////////////////////////////
	// LAYOUT methodes //
	////////////////////////////////////////////////////
	private void buildLayout(){
		
		
		// gap between components
		Dimension dGap = new Dimension(3,txtfieldHeight);
		
		// there is only one panel in the mainpanel; 
		// metaPane is the only one
		JPanel metaPane = new JPanel();
		metaPane.setLayout(new BoxLayout(metaPane, BoxLayout.Y_AXIS));
		metaPane.add(Box.createHorizontalGlue());
		metaPane.setBorder(new TitledBorder(new EtchedBorder(),""));
		
		// well .. its not possible to surrender this the createDataPanel-methode
		txtfield_title = new JTextField();
		txtfield_desc = new JTextField();
		txtfield_file = new JTextField();
		txtfield_pure = new JTextField();

		//the checkboxes
		this.ccssDesc = new CCSearchSelector(this);
		this.ccssFile = new CCSearchSelector(this);
		this.ccssCat  = new CCSearchSelector(this);
		this.ccssCreated = new CCSearchSelector(this);
		this.ccssId  = new CCSearchSelector(this);
		this.ccssModified = new CCSearchSelector(this);
		this.ccssPure = new CCSearchSelector(this);
		this.ccssSection = new CCSearchSelector(this);
		this.ccssTitle = new CCSearchSelector(this);
		this.ccssTyp = new CCSearchSelector(this);
		
		metaPane.add(createDataPanel(txtfield_title, "Name",ccssTitle, dGap));
		metaPane.add(createDataPanel(txtfield_desc, "Beschreibung", ccssDesc, dGap));
		metaPane.add(createDataPanel(txtfield_file, "Path", ccssFile, dGap));
		metaPane.add(createPurenameIDPanel(dGap));
		metaPane.add(createTypKatSecPanel(dGap));
		metaPane.add(createDatePane(dGap));
	
		setComboboxes();
		
		this.setLayout(new GridLayout(1,1));
		this.add(metaPane );
		
	}


	/** set the values from ccconfig */
	private void setComboboxes() {
		this.ccssCat.setSelectedItem(this.controller.getConfig().getSEARCH_CAT());
		this.ccssCreated.setSelectedItem(this.controller.getConfig().getSEARCH_CREATED());
		this.ccssDesc.setSelectedItem(this.controller.getConfig().getSEARCH_DESCRIPTION());
		this.ccssFile.setSelectedItem(this.controller.getConfig().getSEARCH_FILE());
		this.ccssId.setSelectedItem(this.controller.getConfig().getSEARCH_ID());
		this.ccssModified.setSelectedItem(this.controller.getConfig().getSEARCH_MODIFIED());
		this.ccssPure.setSelectedItem(this.controller.getConfig().getSEARCH_PURE());
		this.ccssSection.setSelectedItem(this.controller.getConfig().getSEARCH_SECTION());
		this.ccssTitle.setSelectedItem(this.controller.getConfig().getSEARCH_TITLE());
		this.ccssTyp.setSelectedItem(this.controller.getConfig().getSEARCH_TYP());
	}


	
	/** creates one of the first lines of the metainfopanel */  
	private JPanel createDataPanel(JTextField txtfield, String text, CCSearchSelector sel,  Dimension hole){
    	JLabel label = new JLabel(text+":");
			
		JPanel panel = new JPanel();
    	panel.setBorder(new TitledBorder(new EtchedBorder(),""));
    	panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    	
    	panel.add(sel);
    	panel.add(Box.createRigidArea(hole));
    	panel.add(label);
    	panel.add(Box.createRigidArea(hole));
    	panel.add(txtfield);
    	panel.add(Box.createHorizontalGlue());
		return panel;
	
	}
	

	/**
	 * creates a small box with label and a label with content
	 * @param txtfield the contentLabel
	 * @param name name of the label
	 * @param dLabel dimension of label
	 * @param dGap gap between label and contentlabel
	 * @return
	 */
	private JPanel createField(JTextField txtfield,String name, CCSearchSelector sel, Dimension dGap){
		
		JLabel label = new JLabel(name +":");
		
		JPanel p = new JPanel();
    	p.setBorder(new TitledBorder(new EtchedBorder(),""));
    	p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
    	
    	p.add(sel);
    	p.add(Box.createRigidArea(dGap));
    	p.add(label);
    	p.add(Box.createRigidArea(dGap));
    	p.add(txtfield);
		return p;
	}
	
	/**
	 * creates the panel which contains Typ, Category, ID and Section
	 * @param dLabel Dimension of label
	 * @param dNameDimension of component
	 * @param gap dimension of gap
	 * @return the TypKatIDSecPanel
	 */
	private JPanel createTypKatSecPanel(Dimension gap) {

		JPanel p = new JPanel(new GridLayout());
		GridBagLayout gbl = new GridBagLayout();
    	p.setLayout( gbl ); 
    	
    	//Categorie
    	txtfield_cat = new JTextField();
    	
    	JPanel panelKat = this.createField(txtfield_cat, "Kategorie", ccssCat, gap);
    	this.setLayoutConstrains(panelKat, gbl, 1, 0, GridBagConstraints.BOTH, new Insets(1,1,1,1), -1, -1,-1, 0.4, 1);
    	//Section
		txtfield_section = new JTextField();
    	JPanel panelSection = this.createField(txtfield_section, "Section", this.ccssSection, gap);
    	this.setLayoutConstrains(panelSection, gbl, 0, 0, GridBagConstraints.BOTH, new Insets(1,1,1,1), -1, -1,-1, 0.2, 1);
		// Typ
		this.txtfield_typ = new JTextField();
		JPanel panelTyp = this.createField(txtfield_typ, "Typ", this.ccssTyp, gap);
		this.setLayoutConstrains(panelTyp, gbl, 2, 0, GridBagConstraints.BOTH, new Insets(1,1,1,1), -1, -1,-1, 0.4, 1);
		
		p.add(panelSection);
    	p.add(panelKat);
    	p.add(panelTyp);
		return p;
	}
	
	private JPanel createDatePane(Dimension hole){
		
//		 Created
		this.txtfield_created = new JTextField();
		JPanel panelCreated = this.createField(txtfield_created, "Created", this.ccssCreated, hole);
		
		
//		 Modified
		this.txtfield_modified= new JTextField();
		JPanel panelModified = this.createField(txtfield_modified, "Modified",this.ccssModified, hole);
		
		JPanel p = new JPanel(new GridLayout());
    	p.add(panelCreated);
    	p.add(panelModified);
    	
    	return p;
    	

	}
	/**
	 * sets constrains to the Components<br>
	 * set -1 or null (for insets if not need)<br>
	 * @param comp the button
	 * @param gbl the layout
	 * @param gridx 
	 * @param gridy
	 * @param fill
	 * @param insets
	 * @param anchor
	 */
	private void setLayoutConstrains(JPanel comp, GridBagLayout gbl,
			int gridx,int gridy, 
			int fill, 
			Insets insets, 
			int anchor, 
			int ipadx, int ipady,
			double weightx, double weighty) { 
		
		GridBagConstraints constrains = new GridBagConstraints();
		if (gridx!=-1) constrains.gridx=gridx;
		if (gridy!=-1) constrains.gridy=gridy;
		if (fill!=-1) constrains.fill = fill;
		if (insets!=null) constrains.insets = insets;
		if (anchor!=-1) constrains.anchor = anchor;
		if  (ipadx!=-1) constrains.ipadx = ipadx;
		if  (ipady!=-1) constrains.ipady = ipady;
		if  (weightx!=-1) constrains.weightx = weightx;
		if  (weighty!=-1) constrains.weighty = weighty;
		
		gbl.setConstraints(comp,constrains);
	}
	
	private JPanel createPurenameIDPanel(Dimension gap){
		GridBagLayout gbl = new GridBagLayout();
		JPanel p = new JPanel();
		p.setLayout( gbl ); 
		 
		 
//		 ID
		txtfield_id = new JTextField();
    	JPanel panelID = this.createField(txtfield_id, "ID",this.ccssId, gap);
    	this.setLayoutConstrains(panelID, gbl, 1, 0, GridBagConstraints.BOTH, 
    			new Insets(1,1,1,1), -1, -1 ,-1, 0.2, 1);
    	
    	JPanel panelPure = this.createField(txtfield_pure, "pure_name",this.ccssPure, gap);

		
		this.setLayoutConstrains(panelPure, gbl, 0, 0, GridBagConstraints.BOTH, 
    			new Insets(1,1,1,1), -1, -1 ,-1, 0.8, 1);
		
		p.add(panelPure);
		p.add(panelID);
		
    	return p;
    	

	}

	/**
	 * this frame can be used a search-Frame.<br>
	 * therefore it is necessary to make it editable but if IGNORE is set, it is also not editable
	 * @param b true if it is editable "in general"
	 */
	public void setEditable(boolean b){
		
		this.txtfield_title.setEditable(b && !this.ccssTitle.getSelectedItem().equals(ElementMetaPanel.IGNORE));
		this.txtfield_desc.setEditable(b && !this.ccssDesc.getSelectedItem().equals(ElementMetaPanel.IGNORE));
		this.txtfield_cat.setEditable(b && !this.ccssCat.getSelectedItem().equals(ElementMetaPanel.IGNORE));
		this.txtfield_created.setEditable(b && !this.ccssCreated.getSelectedItem().equals(ElementMetaPanel.IGNORE));
		this.txtfield_file.setEditable(b && !this.ccssFile.getSelectedItem().equals(ElementMetaPanel.IGNORE));
		this.txtfield_id.setEditable(b && !this.ccssId.getSelectedItem().equals(ElementMetaPanel.IGNORE));
		this.txtfield_modified.setEditable(b && !this.ccssModified.getSelectedItem().equals(ElementMetaPanel.IGNORE));
		this.txtfield_pure.setEditable(b && !this.ccssPure.getSelectedItem().equals(ElementMetaPanel.IGNORE));
		this.txtfield_section.setEditable(b && !this.ccssSection.getSelectedItem().equals(ElementMetaPanel.IGNORE));
		this.txtfield_typ.setEditable(b && !this.ccssTyp.getSelectedItem().equals(ElementMetaPanel.IGNORE));
		
		
		this.ccssCat.setVisible(b);
		this.ccssCreated.setVisible(b);
		this.ccssDesc.setVisible(b);
		this.ccssFile.setVisible(b);
		this.ccssId.setVisible(b);
		this.ccssModified.setVisible(b);
		this.ccssPure.setVisible(b);
		this.ccssSection.setVisible(b);
		this.ccssTitle.setVisible(b);
		this.ccssTyp.setVisible(b);
		
	}
	


	public void actionPerformed(ActionEvent e) {
		if (!( e.getSource() instanceof JComboBox)) return;
		
		JComboBox jbc = (JComboBox) e.getSource();
		
		boolean vis = (!(jbc.getSelectedItem().equals(ElementMetaPanel.IGNORE)));
		
		if (jbc.equals(this.ccssCat)) {
			this.txtfield_cat.setEditable(vis);
			controller.getConfig().setSEARCH_CAT(jbc.getSelectedItem().toString());
		}
		else if (jbc.equals(this.ccssDesc)){
			this.txtfield_desc.setEditable(vis);
			controller.getConfig().setSEARCH_DESCRIPTION(jbc.getSelectedItem().toString());
		}
		else if (jbc.equals(this.ccssCreated)){
			this.txtfield_created.setEditable(vis);
			controller.getConfig().setSEARCH_CREATED(jbc.getSelectedItem().toString());
		}
		else if (jbc.equals(this.ccssFile)){
			this.txtfield_file.setEditable(vis);
			controller.getConfig().setSEARCH_FILE(jbc.getSelectedItem().toString());
		}
		else if (jbc.equals(this.ccssId)){
			this.txtfield_id.setEditable(vis);
			controller.getConfig().setSEARCH_ID(jbc.getSelectedItem().toString());
		}
		else if (jbc.equals(this.ccssModified)){
			this.txtfield_modified.setEditable(vis);
			controller.getConfig().setSEARCH_MODIFIED(jbc.getSelectedItem().toString());
		}
		else if (jbc.equals(this.ccssPure)){
			this.txtfield_pure.setEditable(vis);
			controller.getConfig().setSEARCH_PURE(jbc.getSelectedItem().toString());
		}
		else if (jbc.equals(this.ccssSection)){
			this.txtfield_section.setEditable(vis);
			controller.getConfig().setSEARCH_SECTION(jbc.getSelectedItem().toString());
		}
		else if (jbc.equals(this.ccssTitle)){
			this.txtfield_title.setEditable(vis);
			controller.getConfig().setSEARCH_TITLE(jbc.getSelectedItem().toString());
		}
		else if (jbc.equals(this.ccssTyp)) {
			this.txtfield_typ.setEditable(vis);
			controller.getConfig().setSEARCH_TYP(jbc.getSelectedItem().toString());
		}
	}


	/** @return the ccssTitle	 */
	public String getCcssTitle() {		return (String)ccssTitle.getSelectedItem();	}

	/** @return the ccssDesc	 */
	public String getCcssDesc() {return (String)ccssDesc.getSelectedItem();	}

	/** @return the ccssFile	 */
	public String getCcssFile() {return (String)ccssFile.getSelectedItem();}

	/** @return the ccssPure	 */
	public String getCcssPure() {return (String)ccssPure.getSelectedItem();	}

	/** @return the ccssId	 */
	public String getCcssId() {return (String)ccssId.getSelectedItem();	}

	/** @return the ccssTyp	 */
	public String getCcssTyp() {return (String)ccssTyp.getSelectedItem();	}

	/** @return the ccssCat	 */
	public String getCcssCat() {return (String)ccssCat.getSelectedItem();	}

	/** @return the ccssSection	 */
	public String getCcssSection() {return (String)ccssSection.getSelectedItem();	}

	/** @return the ccssCreated	 */
	public String getCcssCreated() {return (String)ccssCreated.getSelectedItem();	}

	/** @return the ccssModified	 */
	public String getCcssModified() {return (String)ccssModified.getSelectedItem();	}
}

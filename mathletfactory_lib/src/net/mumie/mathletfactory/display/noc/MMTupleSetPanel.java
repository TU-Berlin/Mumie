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

package net.mumie.mathletfactory.display.noc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.mumie.mathletfactory.appletskeleton.util.theme.PlainButton;
import net.mumie.mathletfactory.display.noc.matrix.MMNumberMatrixPanel;
import net.mumie.mathletfactory.display.noc.number.MMNumberPanel;
import net.mumie.mathletfactory.display.noc.symbol.ParenSymbolLabel;
import net.mumie.mathletfactory.display.util.TextPanel;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MComplexRational;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.math.number.MOpNumber;
import net.mumie.mathletfactory.math.number.MRational;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.PropertyHandlerIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMNumberTuple;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMTupleSet;
import net.mumie.mathletfactory.mmobject.number.MMOpNumber;
import net.mumie.mathletfactory.mmobject.util.TupleSetEntry;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;
import net.mumie.mathletfactory.util.ResourceManager;

public class MMTupleSetPanel extends MMPanel implements PropertyChangeListener {

	private MMNumberTuple[] m_vectors;
	private MMOpNumber[] m_factors, m_variables;
	private int m_dimension, m_numberOfComponents, m_numberOfVariables;
	private MMNumberMatrixPanel[] m_vectorsPanel;
	private MMNumberPanel[] m_factorsPanel, m_variablesPanel;
	private PlainButton m_plusButton, m_minusButton, m_plusButton2, m_minusButton2;
	private JPanel m_leftPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,0));
	private JPanel m_rightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,0));
	private JPanel panelWithLabelAndButton;
	private JLabel m_inLabel;
	private boolean m_isEditable, m_isRightSideVisible, m_useFactors, m_buttonsEnabled;
	private JCheckBox withFactors;
	private ButtonPanel buttonPanel;
	private boolean nonManualSelectionChange = false;
	
	public MMTupleSetPanel(MMObjectIF masterObject, ContainerObjectTransformer transformer) {
		super(masterObject, transformer);
		addPropertyChangeListener((PropertyHandlerIF) getMaster());
		
		initialize();
		updateLeftMMObjects();
		updateRightMMObjects();
	
		JPanel inner = new JPanel(new FlowLayout(FlowLayout.CENTER,5,0));
		JPanel inner_frame = new JPanel(new BorderLayout());
		inner.add(m_leftPanel);
		inner.add(m_rightPanel);
		inner_frame.add(inner,BorderLayout.CENTER);
		Font font = this.getFont().deriveFont(Font.PLAIN,20);
		inner_frame.add(new ParenSymbolLabel("}", font, 1.2),BorderLayout.EAST);
		inner_frame.add(new ParenSymbolLabel("{", font, 1.2),BorderLayout.WEST);
		
		JPanel panelWithLabel = new JPanel();
		if(super.getComponent(0) instanceof TextPanel)
			panelWithLabel.add(super.getComponent(0));//set label at non-standard panel-position
		panelWithLabel.add(inner_frame);
		
		panelWithLabelAndButton = new JPanel(new BorderLayout());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		panelWithLabelAndButton.add(panelWithLabel,BorderLayout.EAST);
		panelWithLabelAndButton.add(buttonPanel,BorderLayout.SOUTH);
		
		add(panelWithLabelAndButton);
	}

	public void setDimension(int dimension){
		if(m_dimension==dimension)return;
		m_dimension=dimension;
		for(int i=0;i<m_numberOfComponents;i++)
			if(m_vectors[i].getRowCount()!=m_dimension)
				m_vectors[i].resize(m_dimension, 1);
		
	}
	
	/**
	 * @return dimension of the vectors used in the tuple set panel
	 */
	public int getDimension(){
		return m_dimension;
	}
	
	public int getLeftComponentCount(){
		return m_numberOfComponents;
	}
	
	public int getRightComponentCount(){
		return m_numberOfVariables;
	}
	
	public boolean isButtonsEnabled(){
		return m_buttonsEnabled;
	}
	
	public void setButtonsEnabled(boolean aFlag){
		m_buttonsEnabled=aFlag;
		buttonPanel.setVisible(m_buttonsEnabled);
	}
	
	public boolean isEditable(){
		return m_isEditable;
	}
	
	public void setEditable(boolean flag){
		m_isEditable=flag;
		for (int j = 0; j < m_numberOfComponents; j++) {
			m_vectors[j].setEditable(m_isEditable);
			m_factors[j].setEditable(m_isEditable);
		}
		for (int j = 0; j < m_numberOfVariables; j++) {
			m_variables[j].setEditable(m_isEditable);
		}
		m_minusButton.setEnabled(m_isEditable);
		m_minusButton2.setEnabled(m_isEditable);
		m_plusButton.setEnabled(m_isEditable);
		m_plusButton2.setEnabled(m_isEditable);
		withFactors.setEnabled(m_isEditable);
	}
	
	public void setRightVariablesEnabled(boolean flag){
		m_isRightSideVisible = flag;
		m_rightPanel.setVisible(m_isRightSideVisible);
		buttonPanel.m_rightButtonPanel.setVisible(m_isRightSideVisible);
		updateBorder();
	}
	
	public boolean isRightVariablesEnabled(){
		return m_isRightSideVisible;
	}
	
	private void updateBorder(){
		if(m_isRightSideVisible&&m_numberOfVariables>0)
			m_leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, getRealMaster().getDisplayProperties().getObjectColor()));
		else m_leftPanel.setBorder(null);
	}
	
	public boolean isFactorsEnabled(){
		return m_useFactors;
	}
	
	public void setFactorsEnabled(boolean flag){
		m_useFactors=flag;
		if(m_useFactors!=withFactors.isSelected()){
			nonManualSelectionChange=true;
			withFactors.setSelected(m_useFactors);
		}
		updateLeftMMObjects();
	}
	
	public JPanel getButtonPanel(){
		return buttonPanel;
	}
	
	private void setInLabel(){
		if(getRealMaster().getNumberClass()==MComplex.class||getRealMaster().getNumberClass()==MComplexRational.class)
			m_inLabel=new JLabel("\u2208 \u2102");
		else if(getRealMaster().getNumberClass()==MDouble.class||getRealMaster().getNumberClass()==MRational.class)
			m_inLabel=new JLabel("\u2208 \u211D");
		else if(getRealMaster().getNumberClass()==MInteger.class)m_inLabel=new JLabel("\u2208 \u2124");
		else m_inLabel=new JLabel("\u2208 K");
	}
	
	private void initialize() {
		m_useFactors=getRealMaster().isFactorsEnabled();
		buttonPanel = new ButtonPanel();		
		setRightVariablesEnabled(getRealMaster().isRightVariablesEnabled());
		setDimension(getRealMaster().getDimension());	
		m_leftPanel.setAlignmentX(0);
		m_rightPanel.setAlignmentX(0);
		setInLabel();
		setButtonsEnabled(getRealMaster().isButtonsEnabled());
		setEditable(getRealMaster().isEditable());
	}

	public void updateLeftMMObjects(){
		m_numberOfComponents=getRealMaster().getNumberOfComponents();
		m_factors = new MMOpNumber[m_numberOfComponents];
		m_vectors = new MMNumberTuple[m_numberOfComponents];
		m_vectorsPanel = new MMNumberMatrixPanel[m_numberOfComponents];
		m_factorsPanel = new MMNumberPanel[m_numberOfComponents];
		for (int j = 0; j < m_numberOfComponents; j++) {
			m_factors[j]=new MMOpNumber(getRealMaster().getNumberClass());
			m_factors[j].setUsedVariables(true);
			m_factors[j].setEditable(m_isEditable);
			m_vectors[j]=new MMNumberTuple(MOpNumber.getOpClass(getRealMaster().getNumberClass()),m_dimension);
			m_vectors[j].setEditable(m_isEditable);
			m_vectors[j].setUsedVariables(true);
			m_vectorsPanel[j]=(MMNumberMatrixPanel)m_vectors[j].getAsContainerContent();
			m_factorsPanel[j]=(MMNumberPanel)m_factors[j].getAsContainerContent();
			m_factorsPanel[j].setVisible(isFactorsEnabled());
			m_factorsPanel[j].addPropertyChangeListener(this);
			m_vectorsPanel[j].addPropertyChangeListener(this);
		}
		updateLeftValues(getRealMaster().getVectors(), getRealMaster().getFactors());
		updateLayoutLeftComponents();
	}
	
	private void updateLayoutLeftComponents(){
		m_leftPanel.removeAll();
		if(m_numberOfComponents==0) 
			m_leftPanel.setVisible(false);
		else{
			for(int i=0; i<m_numberOfComponents;i++)addNewLeftComponent(i);
			m_leftPanel.setVisible(true);
		}
	}
	
	private void addNewLeftComponent(int index){
		if(index>0) m_leftPanel.add(new JLabel("+"));
		m_leftPanel.add(m_factorsPanel[index]);
		m_leftPanel.add(m_vectorsPanel[index]);
	}

	public void updateRightMMObjects(){
		m_numberOfVariables=getRealMaster().getNumberOfVariables();
		m_variables = new MMOpNumber[m_numberOfVariables];
		m_variablesPanel = new MMNumberPanel[m_numberOfVariables];
		for (int j = 0; j < m_numberOfVariables; j++) {
			m_variables[j]=new MMOpNumber(getRealMaster().getNumberClass());
			m_variables[j].setUsedVariables(true);
			m_variables[j].setEditable(getRealMaster().isEditable());
			m_variablesPanel[j]=(MMNumberPanel)m_variables[j].getAsContainerContent();
			m_variablesPanel[j].addPropertyChangeListener(this);
		}
		updateRightValues(getRealMaster().getRightVariables());
		updateLayoutRightComponents();
	}
	
	private void updateLayoutRightComponents(){
		m_rightPanel.removeAll();
		if(m_numberOfVariables==0||!isRightVariablesEnabled())
			m_rightPanel.setVisible(false);
		else{ 
			for(int i=0; i<m_numberOfVariables;i++)addNewRightComponent(i);
			if(m_numberOfVariables>0)
				m_rightPanel.add(m_inLabel);
			m_rightPanel.setVisible(true);
		}
		updateBorder();
	}
	
	private void addNewRightComponent(int index){
		if(index>0) m_rightPanel.add(new JLabel(","));
		m_rightPanel.add(m_variablesPanel[index]);
	}
	
	public void updateLeftValues(NumberTuple[] vectors, MOpNumber[] factors){
		for (int j = 0; j < m_numberOfComponents; j++) {
			if(m_useFactors){
				m_factors[j].set(factors[j].copy());
				m_factors[j].setEdited(factors[j].isEdited());
				m_factors[j].render();
			}
			for (int i = 1; i <= m_dimension; i++) {
				m_vectors[j].setEntry(i, vectors[j].getEntry(i).copy());
				m_vectors[j].setEdited(i, vectors[j].isEdited(i));
				m_vectors[j].render();
			}
		}
	}
	
	public void updateRightValues(MOpNumber[] variables){
		if(m_isRightSideVisible)
		for (int j = 0; j < m_numberOfVariables; j++) {
			m_variables[j].set(variables[j].copy());
			m_variables[j].setEdited(variables[j].isEdited());
			m_variables[j].render();
		}
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(PropertyHandlerIF.OPERATION)||
				evt.getPropertyName().equals(PropertyHandlerIF.MATRIX_ENTRY)){
		TupleSetEntry tOld = null;
		TupleSetEntry tNew = null;
		if(evt.getSource() instanceof MMNumberMatrixPanel) {
			for(int i=0;i<m_numberOfComponents;i++)
				if (m_vectors[i] == ((MMNumberMatrixPanel) evt.getSource()).getMaster()) {
					tOld = new TupleSetEntry(evt.getOldValue(),TupleSetEntry.VECTOR_ENTRY,i);
					tNew = new TupleSetEntry(evt.getNewValue(),TupleSetEntry.VECTOR_ENTRY,i);
					firePropertyChange(PropertyHandlerIF.SET_ENTRY,tOld,tNew);
					break;
				}
		}else if(evt.getSource() instanceof MMNumberPanel) {
			boolean found = false;
			for(int i=0;i<m_numberOfComponents;i++)
				if (m_factors[i] == ((MMNumberPanel) evt.getSource()).getMaster()) {
					tOld = new TupleSetEntry(evt.getOldValue(),TupleSetEntry.FACTOR,i);
					tNew = new TupleSetEntry(evt.getNewValue(),TupleSetEntry.FACTOR,i);
					firePropertyChange(PropertyHandlerIF.SET_ENTRY,tOld,tNew);
					found=true;
					break;
				}
			if(!found)for(int i=0;i<m_numberOfVariables;i++)
				if (m_variables[i] == ((MMNumberPanel) evt.getSource()).getMaster()) {
					tOld = new TupleSetEntry(evt.getOldValue(),TupleSetEntry.VARIABLE,i);
					tNew = new TupleSetEntry(evt.getNewValue(),TupleSetEntry.VARIABLE,i);
					firePropertyChange(PropertyHandlerIF.SET_ENTRY,tOld,tNew);
					break;
				}
		}
		}else {
			if(evt.getPropertyName().equalsIgnoreCase("addingOrRemoving")){
				firePropertyChange(PropertyHandlerIF.SET_ENTRY,new TupleSetEntry(),
						new TupleSetEntry(null,TupleSetEntry.ADDING_OR_REMOVING_COMPONENT,((Integer)evt.getNewValue()).intValue()));
			}
		}
	}
	
	private MMTupleSet getRealMaster(){
		return (MMTupleSet) getMaster();
	}

	private class ButtonPanel extends JPanel{

		private JPanel m_leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,5,0));
		private JPanel m_rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,5,0));
		public ButtonPanel() {
			ImageIcon icon = new ImageIcon(getClass().getResource("/resource/icon/plus.gif"));
		    m_plusButton = new PlainButton(icon); 
		    m_minusButton = new PlainButton(new ImageIcon(getClass().getResource("/resource/icon/minus.gif")));
		    m_plusButton.setBorderPainted(false);
		    m_plusButton.setFocusPainted(false);
		    m_minusButton.setBorderPainted(false);
		    m_minusButton.setFocusPainted(false);
		    m_plusButton.setPreferredSize(new Dimension(icon.getIconWidth()+2, icon.getIconHeight()+2));
		    m_minusButton.setPreferredSize(new Dimension(icon.getIconWidth()+2, icon.getIconHeight()+2));
		    m_plusButton.setToolTipText(ResourceManager.getMessage("Add_expression"));
		    m_minusButton.setToolTipText(ResourceManager.getMessage("Remove_expression"));
		    m_plusButton.addActionListener(new ActionListener(){
		      public void actionPerformed(ActionEvent e){
		    		propertyChange(new PropertyChangeEvent((Object)withFactors,"addingOrRemoving",
		    				null,(Object)new Integer(TupleSetEntry.ADDING_LEFT_COMPONENT)));
		      }
		    } );
		    m_minusButton.addActionListener(new ActionListener(){
		      public void actionPerformed(ActionEvent e){
		    		propertyChange(new PropertyChangeEvent((Object)withFactors,"addingOrRemoving",
		    				null,(Object)new Integer(TupleSetEntry.REMOVING_LEFT_COMPONENT)));
		      }
		    } );
		    m_plusButton2 = new PlainButton(icon); 
		    m_minusButton2 = new PlainButton(new ImageIcon(getClass().getResource("/resource/icon/minus.gif")));
		    m_plusButton2.setBorderPainted(false);
		    m_plusButton2.setFocusPainted(false);
		    m_minusButton2.setBorderPainted(false);
		    m_minusButton2.setFocusPainted(false);
		    m_plusButton2.setPreferredSize(new Dimension(icon.getIconWidth()+2, icon.getIconHeight()+2));
		    m_minusButton2.setPreferredSize(new Dimension(icon.getIconWidth()+2, icon.getIconHeight()+2));
		    m_plusButton2.setToolTipText(ResourceManager.getMessage("Add_expression"));
		    m_minusButton2.setToolTipText(ResourceManager.getMessage("Remove_expression"));
		    m_plusButton2.addActionListener(new ActionListener(){
		      public void actionPerformed(ActionEvent e){
		    		propertyChange(new PropertyChangeEvent((Object)withFactors,"addingOrRemoving",
		    				null,(Object)new Integer(TupleSetEntry.ADDING_RIGHT_COMPONENT)));
		      }
		    } );
		    m_minusButton2.addActionListener(new ActionListener(){
		      public void actionPerformed(ActionEvent e){
		    		propertyChange(new PropertyChangeEvent((Object)withFactors,"addingOrRemoving",
		    				null,(Object)new Integer(TupleSetEntry.REMOVING_RIGHT_COMPONENT)));
		      }
		    } );
		    m_leftButtonPanel.add(new JLabel(ResourceManager.getMessage("tuple_set_1")));
		    m_leftButtonPanel.add(m_minusButton);
		    m_leftButtonPanel.add(m_plusButton);
		    m_rightButtonPanel.add(new JLabel(ResourceManager.getMessage("tuple_set_2")));
		    m_rightButtonPanel.add(m_minusButton2);
		    m_rightButtonPanel.add(m_plusButton2);
			
		    withFactors = new JCheckBox(ResourceManager.getMessage("tuple_set_3"),m_useFactors);
		    withFactors.addItemListener(new ItemListener(){
		    	public void itemStateChanged(ItemEvent evt) {
		    		if(nonManualSelectionChange){
		    			nonManualSelectionChange = false;
		    			return;
		    		}
		    		int selectionValue;
		    		if(withFactors.isSelected())selectionValue = TupleSetEntry.ENABLING_FACTORS;
		    		else selectionValue = TupleSetEntry.DISABLING_FACTORS;
		    		propertyChange(new PropertyChangeEvent((Object)withFactors,"addingOrRemoving",
		    				null,(Object)new Integer(selectionValue)));
			    }
		    });
			setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
			JPanel factorPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			factorPanel.add(withFactors);
			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,5,0));
			buttonPanel.add(m_leftButtonPanel);
			buttonPanel.add(m_rightButtonPanel);
			add(buttonPanel);
			add(factorPanel);
		}
	}
}

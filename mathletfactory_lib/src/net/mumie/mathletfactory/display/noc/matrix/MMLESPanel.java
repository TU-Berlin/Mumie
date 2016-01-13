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

package net.mumie.mathletfactory.display.noc.matrix;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.mumie.mathletfactory.display.layout.MatrixLayout;
import net.mumie.mathletfactory.display.noc.MMCompoundPanel;
import net.mumie.mathletfactory.display.noc.number.MMNumberPanel;
import net.mumie.mathletfactory.display.noc.symbol.ParenSymbolLabel;
import net.mumie.mathletfactory.math.algebra.linalg.Matrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.op.node.AddOp;
import net.mumie.mathletfactory.math.algebra.op.node.MultOp;
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MComplexRational;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.MOpNumber;
import net.mumie.mathletfactory.math.number.MRealNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.PropertyHandlerIF;
import net.mumie.mathletfactory.mmobject.util.MMString;
import net.mumie.mathletfactory.mmobject.util.MatrixEntry;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;
/**
 * This class can be used to represent a NumberMatrix as a Linear Equation System.
 * 
 * @author markov
 * @mm.docstatus finished
 */
public class MMLESPanel extends MMCompoundPanel implements PropertyChangeListener {
	/** the quantity of Variables*/
	private int m_variableQantity;
	/** A field for the master object*/
	private NumberMatrix m_matrix;
	/** A panel for the number alligment*/
	private JPanel layoutPanels[][];
	/** The last variable ist the "=" symbol*/
	private MMString[] m_variableNames;
	/** Field holding the old matrix*/
	private NumberMatrix m_oldMatrix;
	/** All number entries as MM objects*/
	private MMObjectIF[][] m_entries;
	/** The sings of the entries*/
	private MMString[][] signs;
	/** Panels for the sings*/
	private JComponent[][] signPanels;
	
	private ParenSymbolLabel[][] leftBracket;
	
	private ParenSymbolLabel[][] rightBracket;
	/** Panels for the entries*/
	private JComponent[][] m_entriePanels;
	/** Panels containing the left and right bracet panels and the entrie panel*/
	private JPanel[][] m_entrieWithBracketsPanels;
	/** Panels for the variable names*/
	private JComponent[][] varPanels;
	/** Flag for the panels being editable. */
	private boolean m_isEditable;
	/** Flag for the singular entries being editable*/
	private Boolean[][] m_entriesEditable;

	public MMLESPanel(MMObjectIF master, ContainerObjectTransformer transformer) {
		super(master, transformer);
		this.addPropertyChangeListener((PropertyHandlerIF) getMaster());
		createSurface();
	}

	/**
	 * Creates the layout of the MMPanel.
	 */
	public void createSurface() {
		initializeVars();
		getViewerComponent().setLayout(new MatrixLayout(m_matrix.getRowCount(), (m_matrix.getColumnCount()) +1));
		getViewerComponent().setAlignmentX(RIGHT_ALIGNMENT);
		getViewerComponent().removeAll();
		for (int i = 1; i <= m_matrix.getRowCount(); i++) {
			for (int j = 1; j <= m_matrix.getColumnCount(); j++) {
				m_entries[i - 1][j - 1] = NumberFactory.getNewMMInstanceFor(m_matrix.getEntry(i, j));
				((MNumber)m_entries[i - 1][j - 1]).setEdited(((MNumber)m_matrix.getEntryRef(i, j)).isEdited());
				signs[i - 1][j - 1] = new MMString("");
				
				signPanels[i - 1][j - 1] = signs[i - 1][j - 1].getAsContainerContent();
				varPanels[i - 1][j - 1] = m_variableNames[j - 1].getAsContainerContent();
				m_entriePanels[i - 1][j - 1] = m_entries[i - 1][j - 1].getAsContainerContent();
				m_entriePanels[i - 1][j - 1].addPropertyChangeListener(this);
				m_entries[i - 1][j - 1].setEditable(m_isEditable);
				layoutPanels[i - 1][j - 1] = new JPanel(new FlowLayout(FlowLayout.RIGHT));
				m_entrieWithBracketsPanels[i - 1][j - 1] = new JPanel();
				m_entrieWithBracketsPanels[i - 1][j - 1].setLayout(new BorderLayout());
				
				if (j != m_variableQantity + 1) { // a var, not a inhomogeneity
					m_entrieWithBracketsPanels[i-1][j-1].add(leftBracket[i - 1][j - 1], BorderLayout.WEST);
					m_entrieWithBracketsPanels[i-1][j-1].add(m_entriePanels[i - 1][j - 1], BorderLayout.CENTER);
					m_entrieWithBracketsPanels[i-1][j-1].add(rightBracket[i - 1][j - 1], BorderLayout.EAST);
					layoutPanels[i-1][j-1].add(signPanels[i - 1][j - 1]);
					layoutPanels[i-1][j-1].add(m_entrieWithBracketsPanels[i - 1][j - 1]);
					layoutPanels[i-1][j-1].add(varPanels[i - 1][j - 1]);
//					layoutPanels[i-1][j-1].setRightAlignment();
					getViewerComponent().add(layoutPanels[i-1][j-1]);
					
				} else { // an inhomogeneity
					getViewerComponent().add(varPanels[i - 1][j - 1]);
					layoutPanels[i-1][j-1].add(signPanels[i - 1][j - 1]);
					layoutPanels[i-1][j-1].add(leftBracket[i - 1][j - 1]);
					layoutPanels[i-1][j-1].add(m_entriePanels[i - 1][j - 1]);
					layoutPanels[i-1][j-1].add(rightBracket[i - 1][j - 1]);
//					layoutPanels[i-1][j-1].setLeftAlignment();
					getViewerComponent().add(layoutPanels[i-1][j-1]);
				}
			}
		}
	}

	private void initializeVars(){
		m_matrix 		= (NumberMatrix) getMaster();
		m_oldMatrix 	= m_matrix.deepCopy();
		m_isEditable	= getMaster().isEditable();
		layoutPanels	= new JPanel[m_matrix.getRowCount()][m_matrix.getColumnCount()];
		signPanels 		= new JComponent[m_matrix.getRowCount()][m_matrix.getColumnCount()];
		m_entriePanels 	= new JComponent[m_matrix.getRowCount()][m_matrix.getColumnCount()];
		varPanels 		= new JComponent[m_matrix.getRowCount()][m_matrix.getColumnCount()];
		m_entries 		= new MMObjectIF[m_matrix.getRowCount()][m_matrix.getColumnCount()];
		signs 			= new MMString  [m_matrix.getRowCount()][m_matrix.getColumnCount()];
		leftBracket 	= new ParenSymbolLabel[m_matrix.getRowCount()][m_matrix.getColumnCount()];
		rightBracket 	= new ParenSymbolLabel[m_matrix.getRowCount()][m_matrix.getColumnCount()];
		m_entrieWithBracketsPanels = new JPanel[m_matrix.getRowCount()][m_matrix.getColumnCount()];

		m_variableQantity = m_matrix.getColumnCount() - 1;

		switch (m_variableQantity) {
			case 1: {
				m_variableNames = new MMString[] { new MMString("x"),
						new MMString("=")};
				break;
			}
			case 2: {
				m_variableNames = new MMString[] { new MMString("x"),
						new MMString("y"),	new MMString("=") };
				break;
			}
			case 3: {
				m_variableNames = new MMString[] { new MMString("x"),
						new MMString("y"), new MMString("z"),
						new MMString("=") };
				break;
			}
			default: {
				char ch = 'a';
				m_variableNames = new MMString[m_variableQantity+1];
				for (int i = 0; i < m_variableQantity; i++) {
					m_variableNames[i] = new MMString(new String(new char[] {ch++}));
				}
				m_variableNames[m_variableQantity] = new MMString("=");
			}
		}
		m_entriesEditable = new Boolean[m_matrix.getRowCount()][m_matrix.getColumnCount()];
		for (int i = 0; i < m_matrix.getRowCount(); i++) {
			for (int j = 0; j < m_matrix.getColumnCount(); j++) {
				m_entriesEditable[i][j] = new Boolean(false);
				leftBracket[i][j] = new ParenSymbolLabel("(",getFont());
				//rightBracket[i][j] = new JLabel(")");
				rightBracket[i][j] = new ParenSymbolLabel(")",getFont());
			}
		}
	}
	
	/**
	 * Synchronizes the MMPanel to the master object
	 * 
	 * @param aMatrix the master object
	 */
	public void setValuesFromNumberMatrix(NumberMatrix aMatrix){
		if (aMatrix.getColumnCount() != m_matrix.getColumnCount()
				|| aMatrix.getRowCount() != m_matrix.getRowCount())
			throw new IllegalArgumentException(
					"Dimensions of the Matrix and the drawable's data model are not equal!");
		for (int r = 1; r <= m_matrix.getRowCount(); r++) {
			for (int c = 1; c <= m_matrix.getColumnCount(); c++) {
				MNumber entry = aMatrix.getEntryAsNumberRef(r, c);
				((MNumber) m_entries[r-1][c-1]).set(isPositiv((MNumber) entry)?(MNumber) entry:((MNumber) entry).negated());
				((MNumber) m_entries[r-1][c-1]).setEdited(entry.isEdited());
				m_entries[r-1][c-1].render();
			}
		}
	};
	
	/**
	 * Registers a change in a matrix entry that this listens to and fires a new 
	 * change event of type MATRIX_ENTRY and NUMBERMATRIX
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (!evt.getPropertyName().equals(PropertyHandlerIF.NUMBER)&&
			!evt.getPropertyName().equals(PropertyHandlerIF.OPERATION))
		return;
		
		System.out.println("event: " + evt.getPropertyName());

		MatrixEntry newValue = null;
		MatrixEntry oldValue = null;
		Matrix     newMatrix = null;
		if (evt.getPropertyName().equals(PropertyHandlerIF.OPERATION)) {
			Start:for (int r = 1; r <= getRows(); r++) {
				for (int c = 1; c <= getColumns(); c++) {
					if (m_entries[r-1][c-1] == ((MMNumberPanel) evt.getSource()).getMaster()) {
						newValue = new MatrixEntry(m_entries[r-1][c-1], r, c);
						oldValue = new MatrixEntry(((NumberMatrix)getMaster()).getEntry(r, c), r, c);
						if(signs[r-1][c-1].getValue() == "-"){
							((MOpNumber)newValue.getValue()).negate();
						}
						break Start;
					}
				}
			}
		}
		if (evt.getPropertyName().equals(PropertyHandlerIF.NUMBER)) {
			Start:for (int r = 1; r <= getRows(); r++) {
				for (int c = 1; c <= getColumns(); c++) {
					if (m_entries[r-1][c-1] == ((MMNumberPanel) evt.getSource()).getMaster()) {
						newValue = new MatrixEntry(evt.getNewValue(), r, c);
						oldValue = new MatrixEntry(evt.getOldValue(), r, c);
						if(signs[r-1][c-1].getValue() == "-"){
							((MNumber)newValue.getValue()).negate();
						}
						break Start;
					}
				}
			}
		}
		firePropertyChange(PropertyHandlerIF.MATRIX_ENTRY, oldValue,
				newValue);
		newMatrix = getValuesAsNumberMatrix();
		firePropertyChange(PropertyHandlerIF.NUMBERMATRIX, m_oldMatrix,
				newMatrix);
		m_oldMatrix.setEntryRef(newValue.getRowPosition(), newValue
				.getColumnPosition(), newValue.getValue());
	
	}
	
	/**
	 * Creates a NumberMatrix from a MMNumberMatrix
	 * 
	 * @return a new NumberMatrix
	 */
	public NumberMatrix getValuesAsNumberMatrix() {
		NumberMatrix result = new NumberMatrix(getMaster().getNumberClass(),
				getColumns(), getRows());
		for (int r = 1; r <= getRows(); r++) {
			for (int c = 1; c <= getColumns(); c++) {
				MNumber n = ((MNumber) m_entries[r-1][c-1]).copy();
				n.setEdited(((MNumber) m_entries[r-1][c-1]).isEdited());
				result.setEntryRef(r, c, n);
				// matrix indices are 1-based!
			}
		}
		return result;
	}
	
	/**
	 * Returns the number of columns.
	 */
	public int getColumns() {
		return m_matrix.getColumnCount();
	}

	/**
	 * Returns the number of rows.
	 */
	public int getRows() {
		return m_matrix.getRowCount();
	}
	/**
	 * Sets the visibility of numbers, signs and variables panels
	 */
	public void render() {
		setVisibilityOfPanels();
	}
	
	private void setVisibilityOfPanels(){
		for (int i = 1; i <= m_matrix.getRowCount(); i++) {
			boolean firstInRow = true;
			boolean lastInRow = false;
			boolean allZero = true;
			for (int j = 1; j <= m_matrix.getColumnCount(); j++) {
				if(j == m_matrix.getColumnCount()-1) lastInRow = true;
				boolean isPositiv = isPositiv(m_matrix.getEntry(i, j));
				boolean isInhomogenety = m_matrix.getColumnCount() == j;
				boolean isZero = m_matrix.getEntry(i, j).isZero();
				if(!isZero) allZero = false;
				boolean isOne = isOne(m_matrix.getEntry(i, j));
				boolean isalone = isAlone(m_matrix.getEntry(i, j));
				String sign;
				if (!isPositiv) sign = "-"; else	sign = "+";

				signs[i - 1][j - 1].setValue(sign);

				if (firstInRow && isPositiv || isInhomogenety && isPositiv || isZero && !m_isEditable) {
					signPanels[i - 1][j - 1].setVisible(false);
				} else {
					signPanels[i - 1][j - 1].setVisible(true);
					
				}
				
				if (((isZero || isOne) && !isInhomogenety && !m_isEditable)&&!(allZero && lastInRow)) {
					m_entriePanels[i - 1][j - 1].setVisible(false);
				} else {
					m_entriePanels[i - 1][j - 1].setVisible(true);
					
				}
				
				if (isZero && !m_isEditable && !isInhomogenety) {
					varPanels[i - 1][j - 1].setVisible(false);
				} else {
					varPanels[i - 1][j - 1].setVisible(true);
					firstInRow = false;
					
				}
				
				if((m_matrix.getEntry(i, j) instanceof MComplex ||
					m_matrix.getEntry(i, j) instanceof MComplexRational||
					m_matrix.getEntry(i, j) instanceof MOpNumber) && !isalone){
					leftBracket[i - 1][j - 1].setVisible(true);
					rightBracket[i - 1][j - 1].setVisible(true);
				}else{
					leftBracket[i - 1][j - 1].setVisible(false);
					rightBracket[i - 1][j - 1].setVisible(false);
				}
				if(!isPositiv){
					((MNumber)m_entries[i - 1][j - 1]).set(m_matrix.getEntry(i, j).negate());
				}
				signs[i - 1][j - 1].render();
				m_entries[i-1][j-1].render();
			}
		}
		
	}
	
	/**
	 * Checks if the paramether is a sum of two or more figures
	 * 
	 * @param aNumber the number to be checked.
	 * @return true if a real number, complex number with a
	 * Im = 0 or a simple(singular figure) OpNumber is to be cheked.
	 * False otherwise.
	 */
	private boolean isAlone(MNumber aNumber) {
		
		if(aNumber instanceof MComplex){
			if(((MComplex)aNumber).getRe() == 0 || ((MComplex)aNumber).getIm() == 0){
					return true;
			}else{
					return false;
			}
		}else if(aNumber instanceof MComplexRational){
			if(((MComplexRational)aNumber).getRe().getDouble() == 0 || ((MComplexRational)aNumber).getIm().getDouble() == 0){
				return true;
		}else{
				return false;
		}
		}else if(aNumber instanceof MRealNumber){
				return true;
		}else if(aNumber instanceof MOpNumber){
			if(!(((MOpNumber)aNumber).getOperation().getOpRoot() instanceof AddOp)){
				if(((MOpNumber)aNumber).getOperation().getOpRoot() instanceof MultOp && 
						((MOpNumber)aNumber).getOperation().getOpRoot().getChildren()[0].getFactor().getDouble() == -1)
					return false;
				return true;
			}else
				return false;
		}else{
			throw new IllegalArgumentException("The Number Class: " + aNumber.getClass() + " is not supported for LES Trasnformer" );
		}
	}

	/**
	 * Checks if the parameter is a positive number.
	 * 
	 * @param aNumber the number to be checked
	 * @return true if the parameter is a positive real number
	 * or a complex number with positive Re and Im=0
	 * or a not simple(not singular figure) OpNumber. False otherwise.
	 */
	private boolean isPositiv(MNumber aNumber) {
		if(aNumber instanceof MComplex){
			if(((MComplex)aNumber).getRe() != 0){
				if(((MComplex)aNumber).getRe()<0)
					return false;
				else
					return true;
			}else{
				if(((MComplex)aNumber).getIm()<0)
					return false;
				else
					return true;
			}
		}else if(aNumber instanceof MComplexRational){
			if(((MComplexRational)aNumber).getRe().getSign() != 0){
				if(((MComplexRational)aNumber).getRe().getSign() == -1)
					return false;
				else
					return true;
			}else{
				if(((MComplexRational)aNumber).getIm().getSign() == -1)
					return false;
				else
					return true;
			}
		}else if(aNumber instanceof MRealNumber){
			if(aNumber.getDouble() < 0)
				return false;
			else
				return true;
		}else if(aNumber instanceof MOpNumber){
			if(((MOpNumber)aNumber).getOperation().getOpRoot().isLeaf() &&
					((MOpNumber)aNumber).getOperation().getOpRoot().getFactor().getDouble() == -1)
				return false;
			else
				return true;
		}else{
			throw new IllegalArgumentException("The Number Class: " + aNumber.getClass() + " is not supported for LES Trasnformer" );
		}
	}
	
	/**
	 * Checks if the value of the parameter is one.
	 * 
	 * @param aNumber The number to be checked
	 * @return true if the parameter is a Real number and is +/-1 or
	 * complex number and Re=+/-1 and Im=0. False otherwise.
	 */
	private boolean isOne(MNumber aNumber) {
		if(aNumber instanceof MComplex){
			if(Math.abs(((MComplex)aNumber).getRe()) == 1 && ((MComplex)aNumber).getIm() == 0)
				return true;
			else
				return false;
		}else if(aNumber instanceof MComplexRational){
			if(Math.abs(((MComplexRational)aNumber).getRe().getDouble()) == 1 && ((MComplexRational)aNumber).getIm().isZero())
				return true;
			else
				return false;
		}else if(aNumber instanceof MRealNumber){
			if(Math.abs(aNumber.getDouble()) == 1)
				return true;
			else
				return false;
		}else if(aNumber instanceof MOpNumber){
			if(((MOpNumber)aNumber).abs().getDouble() == 1){
				return true;
			}else
				return false;
		}else{
				throw new IllegalArgumentException("The Number Class: " + aNumber.getClass() + "is not supported for LES Trasnformer" );
		}
		
	}
	
	/**
	 * Sets the name of a specific variable
	 * 
	 * @param name the new name of the variable
	 * @param position the position where the name must be changed.
	 * Position is 1 based as the matrix itself 
	 */
	public void setVariableName(String name, int position) {
		if(position > m_variableNames.length-1)
			throw new IllegalArgumentException("position value may not be greater than matrix width - 1");
		m_variableNames[position - 1].setValue(name);
		m_variableNames[position - 1].render();
	}
	
	/**
	 * Sets the names of all variables in the LES
	 * 
	 * @param names array containing the names of the variables. Must contain
	 * exactly as much names as there variables are.
	 */
	public void setVariableNames(String[] names) {
		if(names.length != m_variableNames.length-1)
			throw new IllegalArgumentException("The number of variable names may not deiffer than matrix width - 1");
		for(int i = 0; i < m_variableQantity; i++){
			m_variableNames[i].setValue(names[i]);
			m_variableNames[i].render();
		}
	}

	/**
	 * Sets whether the matrix drawable should be editable or not. This method
	 * passes the editable-flag to all the renderers.
	 */
	public void setEditable(boolean editable) {
		if (editable == m_isEditable)
			return;
		m_isEditable = editable;
		for (int i = 0; i < m_matrix.getRowCount(); i++) {
			for (int j = 0; j < m_matrix.getColumnCount(); j++) {
				if (m_entries[i][j] != null) {
					m_entriesEditable[i][j] = new Boolean(editable);
					m_entries[i][j].setEditable(editable);
				}
			}
		}
		render();
	}
}
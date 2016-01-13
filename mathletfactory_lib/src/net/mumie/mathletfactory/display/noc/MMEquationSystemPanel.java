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

/*
 * Created on 07.10.2003
 *
 */
package net.mumie.mathletfactory.display.noc;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.layout.MatrixLayout;
import net.mumie.mathletfactory.display.noc.number.MMNumberSimplePanel;
import net.mumie.mathletfactory.display.noc.symbol.ParenSymbolLabel;
import net.mumie.mathletfactory.math.algebra.linalg.Matrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.MMEquationSystem;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class is used to visualize an equation system in a container.
 *
 * @author Gronau
 * @mm.docstatus finished
 */
public class MMEquationSystemPanel extends MMPanel implements PropertyChangeListener {

	private NumberMatrix m_matrix;

	int m_nrOfCols;

	/** Matrix containing the coefficients for each variable in a row vector
	 * as a <code>MMObjectIF</code>. */
	private MMObjectIF[] m_mmCoeffs;

	/** Array containing the right side for each equation in a row vector
	 * as a <code>MMObjectIF</code>. */
	private MMObjectIF[] m_mmRightSide;

	private MMEquationSystem m_eqSys;

	private JPanel m_eqSysPane = new JPanel();

	public MMEquationSystemPanel(MMObjectIF masterObject, ContainerObjectTransformer transformer) {
		super(masterObject, transformer);

		m_eqSys = (MMEquationSystem) masterObject;
		m_matrix = m_eqSys.getCoeffMatrix();
		NumberTuple rightSide = m_eqSys.getInhomogeneityVector();
		initializeDrawables(m_eqSys.getEquationsCount(), m_eqSys.getContainedVariablesCount());

		m_nrOfCols = 3 * m_eqSys.getContainedVariablesCount() + 2;
		m_eqSysPane.setLayout(new MatrixLayout(m_eqSys.getEquationsCount(), m_nrOfCols));

//		setLayout(new BorderLayout());

		Box box = new Box(BoxLayout.X_AXIS);
		box.add(new ParenSymbolLabel("{", getMaster().getDisplayProperties().getFont()));
		box.add(m_eqSysPane);
		add(box, BorderLayout.CENTER);
	}

	/*
	 * before each cell of the left side is a label for the sign, i.e. a single line for three variables has 2*3 (left side) + 1 ("="-sign) + 1 (right side)
	 */
	private void initializeDrawables(int rows, int cols) {
		m_mmCoeffs = new MMObjectIF[Matrix.getIndexFromEntry(rows, cols, cols) + 1];
		m_mmRightSide = new MMObjectIF[rows];
		MNumber m = NumberFactory.newInstance(getMaster().getNumberClass());
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				int index = Matrix.getIndexFromEntry(r+1, c+1, cols);
				m_mmCoeffs[index] = NumberFactory.getNewMMInstanceFor(m);
				m_mmCoeffs[index].setDisplayProperties(
					new DisplayProperties());
				MMNumberSimplePanel c2 = (MMNumberSimplePanel)m_mmCoeffs[index].getAsContainerContent(GeneralTransformer.NUMBER_SIMPLE_TRANSFORM);
				c2.setDisplayAbsValue(true);
				m_eqSysPane.add(new JLabel(" + "));
				m_eqSysPane.add(c2);
				m_eqSysPane.add(new JLabel(m_eqSys.getContainedVariables()[c]));
				c2.addPropertyChangeListener(this);
			}

			m_eqSysPane.add(new JLabel(" = "));
//			m_eqSysPane.add(new JLabel(" + "));
			m_mmRightSide[r] = NumberFactory.getNewMMInstanceFor(m);
			JComponent c = (MMNumberSimplePanel)m_mmRightSide[r].getAsContainerContent(GeneralTransformer.NUMBER_SIMPLE_TRANSFORM);
			m_eqSysPane.add(c);
			c.addPropertyChangeListener(this);
		}
		System.out.println("comps: " + m_eqSysPane.getComponentCount());
	}

	public void setValues(NumberMatrix coeffs, NumberTuple rightSide) {
		if (coeffs.getColumnCount() != m_matrix.getColumnCount()
			|| coeffs.getRowCount() != m_matrix.getRowCount())
			throw new IllegalArgumentException("Dimensions of the NumberMatrix and the drawable's data model are not equal!");
		for (int r = 1; r <= m_matrix.getRowCount(); r++) {
			boolean isFirstEntry = true;
			for (int c = 1; c <= m_matrix.getColumnCount(); c++) {
				int index = Matrix.getIndexFromEntry(r, c, m_matrix.getColumnCount());
				MNumber mValue = coeffs.getEntryAsNumberRef(r, c);

				JLabel signLabel = (JLabel)m_eqSysPane.getComponents()[Matrix.getIndexFromEntry(r, 3*c-2, m_nrOfCols)];
				JLabel varLabel = (JLabel)m_eqSysPane.getComponents()[Matrix.getIndexFromEntry(r, 3*c, m_nrOfCols)];

				MMNumberSimplePanel nrPane = (MMNumberSimplePanel)m_eqSysPane.getComponents()[Matrix.getIndexFromEntry(r, 3*c-1, m_nrOfCols)];

				if(mValue.getDouble() == 0) {
					nrPane.setVisible(false);
					signLabel.setText("");
					varLabel.setText("");
				}
				else if(mValue.getDouble() < 0) {
					signLabel.setText(" - ");
					isFirstEntry = false;
					if(mValue.getDouble() == -1)
						nrPane.setVisible(false);
				}
				else { // mValue > 0
					if(mValue.getDouble() == 1)
						nrPane.setVisible(false);
					if(isFirstEntry) {
						signLabel.setText("");
						isFirstEntry = false;
					}
					else
						signLabel.setText(" + ");
				}
				((MNumber) m_mmCoeffs[index]).set(mValue);
//				m_mmCoeffs[index].setEditable(m_isEditable);
				m_mmCoeffs[index].render();
			}
			MNumber rside = rightSide.getEntry(r,1);
			((MNumber) m_mmRightSide[r-1]).set(rside);
			m_mmRightSide[r-1].render();
		}

	}

	/**
	 * This panel listens to single changes in the number drawables it holds and forwards a new PropertyChange-Event
	 * (type: PropertyHandlerIF.NUMBERMATRIX) with the new matrix entries to the master.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
//		if (!evt.getPropertyName().equals(PropertyHandlerIF.NUMBER))
//			return;
//		firePropertyChange(
//			PropertyHandlerIF.NUMBERMATRIX,
//			getMaster(),
//			getValuesAsNumberMatrix());
	}

	public void render() {
		//		for(int i = 0; i < m_relPanels.length; i++)
		//		m_relPanels[i].ren
		System.out.println("render in MMEquationSystemPanel!");
	}

}

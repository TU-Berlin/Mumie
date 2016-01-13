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

package net.mumie.mathletfactory.transformer.noc;

import java.awt.Color;

import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.display.noc.MMPanel;
import net.mumie.mathletfactory.display.noc.matrix.MatrixBorder;
import net.mumie.mathletfactory.display.noc.matrix.StringMatrixPanel;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMNumberMatrix;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;

/**
 * Transformer for {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMNumberMatrix}.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class MatrixImageTransformer extends ContainerObjectTransformer implements ChangeListener {

	private StringMatrixPanel m_matrixPanel;
	
  public void initialize(MMObjectIF masterObject) {
    super.initialize(masterObject);
    int rowCount = getRealMaster().getRowCount();
    int colCount = getRealMaster().getColumnCount();
    String[] bitmap = new String[rowCount * colCount];
    for(int r = 0; r < rowCount; r++) {
      for(int c = 0; c < colCount; c++) {
        bitmap[r*colCount+c] = "\u2588";
      }
     }
    m_matrixPanel = new StringMatrixPanel(bitmap, getRealMaster().getRowCount(), getRealMaster().getColumnCount());
    m_matrixPanel.setCellHeight(25);
    m_matrixPanel.setCellWidth(25);
    m_matrixPanel.setBorderType(MatrixBorder.BRACKETS);
    m_matrixPanel.setHorizontalCellAlignment(SwingConstants.CENTER);
    m_matrixPanel.setHorizontalCellAlignment(SwingConstants.CENTER);
    m_matrixPanel.setFont(m_matrixPanel.getFont().deriveFont((float)20));
    getRealMaster().addDimensionChangeListener(this);
    // trick because StringMatrixPanel is not a MMPanel
    m_mmPanel = new MMPanel(getMaster(), this, m_matrixPanel) {};
    render();
  }
    
	public void render() {
  	super.render();
    int rowCount = getRealMaster().getRowCount();
    int colCount = getRealMaster().getColumnCount();
    for(int r = 1; r <= rowCount; r++) {
      for(int c = 1; c <= colCount; c++) {
      	MNumber entry = (MNumber)getRealMaster().getEntryRef(r, c);
      	int value = (int) entry.getDouble();
      	Color color = null;
      	if( ! entry.isEdited()) {
      		color = new Color(255, 175, 175); // rosa for non edited numbers
      	} else if(value > 510) { // green for numbers > 510
      		color = new Color(0, 255, 0);
      	} else if(value > 255) { // 255: white -> green: 510
      		int i = Math.abs(value - 510);
      		color = new Color(i, 255, i);
      	} else if(value >= 0) { // 0: black -> white: 255
      		color = new Color(value, value, value);
      	} else if(value < -255) { // red for numbers < -255
      		color = new Color(255, 0, 0);
      	} else { // -255: red -> 0: black
      		int i = Math.abs(value);
      		color = new Color(i, 0, 0);
      	}
    		m_matrixPanel.setCellForeground(r, c, color);
      }
     }
	}

  private MMNumberMatrix getRealMaster() {
    return (MMNumberMatrix) getMaster();
  }

  public void stateChanged(ChangeEvent event) {
  	throw new TodoException();
  }
}

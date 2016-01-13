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

package net.mumie.mathletfactory.util.exercise;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import net.mumie.cocoon.util.DefaultCocoonEnabledDataSheet;
import net.mumie.cocoon.util.ProblemCorrectionException;
import net.mumie.cocoon.util.ProblemCorrector;
import net.mumie.japs.datasheet.DataSheet;
import net.mumie.mathletfactory.appletskeleton.MathletContext;
import net.mumie.mathletfactory.util.exercise.receipt.Receipt;
import net.mumie.mathletfactory.util.exercise.receipt.ReceiptPanel;
import net.mumie.mathletfactory.util.xml.DatasheetRenderer;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;

/**
 * This class is used to debug a corrector class of an exercise mathlet.
 * It shows the incoming datasheet from the applet and the outgoing datasheet
 * from the corrector class.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public class CorrectorDebugger extends JFrame{

  private JSplitPane m_splitter;
  private DatasheetRenderer m_answerRenderer, m_correctorRenderer; 

  public CorrectorDebugger(MathletContext mathlet, ProblemCorrector corrector, DataSheet ds) throws ProblemCorrectionException {
    super(mathlet.getShortName());

    ds.indent();
    m_answerRenderer = new DatasheetRenderer(ds);
    ReceiptPanel receiptPanel = new ReceiptPanel(new Receipt(ds));
    receiptPanel.setHeaderVisible(false);
    m_answerRenderer.addTab("Receipt View", receiptPanel);
    JPanel answerPanel = new JPanel(new BorderLayout());
    answerPanel.add(m_answerRenderer, BorderLayout.CENTER);
    answerPanel.add(new JLabel("<html><b>Input Data Sheet for Corrector Class", JLabel.CENTER), BorderLayout.NORTH);
    JPanel correctorPanel = new JPanel(new BorderLayout());
    m_correctorRenderer = new DatasheetRenderer();
    correctorPanel.add(m_correctorRenderer, BorderLayout.CENTER);
    correctorPanel.add(new JLabel("<html><b><center>Output Data Sheet from Corrector Class", JLabel.CENTER), BorderLayout.NORTH);
    m_splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, answerPanel, correctorPanel);
    m_splitter.setDividerLocation(0.5);
    m_splitter.setResizeWeight(0.5);
    answerPanel.setPreferredSize(new Dimension(800, 700));
    correctorPanel.setPreferredSize(new Dimension(800, 700));
    getContentPane().add(m_splitter);
    pack();
    setSize(800, 600);
    center();
    setVisible(true);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    debug(corrector, ds);
  }

  private void debug(ProblemCorrector cInstance, DataSheet ds) throws ProblemCorrectionException {
    try {
      DefaultCocoonEnabledDataSheet answerSheet = new DefaultCocoonEnabledDataSheet();
      DefaultCocoonEnabledDataSheet correctorSheet = new DefaultCocoonEnabledDataSheet();
      answerSheet.setEmptyDocument(XMLUtils.createDocumentBuilder());
      correctorSheet.setEmptyDocument(XMLUtils.createDocumentBuilder());
      answerSheet.merge(ds, DataSheet.REPLACE);
      cInstance.correct(answerSheet, correctorSheet);
      correctorSheet.indent();
      Document doc = correctorSheet.getDocument();
      XMLUtils.replaceNode(correctorSheet.getRootElement(), XMLUtils.createEmptyMarkingNode(doc), XMLUtils.createUneditedNode(doc));
      m_correctorRenderer.setDatasheet(correctorSheet);
      m_splitter.setDividerLocation(0.5);
    } catch(ProblemCorrectionException pcex) {
      m_correctorRenderer.setError("Error occurred while running corrector: " + pcex);
      throw pcex;
    } catch(Throwable t) {
    	m_correctorRenderer.setError("Error occurred while running corrector: " + t);
      throw new ProblemCorrectionException(t);
    }
  }

  /** Centers this frame on the screen. */
  public void center() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (screenSize.width - getWidth())/2;
    int y = (screenSize.height - getHeight())/2;
    setLocation(x,y);
  }
}

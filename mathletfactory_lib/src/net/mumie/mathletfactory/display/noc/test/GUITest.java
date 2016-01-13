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

package net.mumie.mathletfactory.display.noc.test;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalLookAndFeel;

import net.mumie.mathletfactory.appletskeleton.system.MathletRuntime;
import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.geom.affine.AffineSpace;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.algebra.MMRelation;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR3;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR3Vector;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMNumberMatrix;
import net.mumie.mathletfactory.mmobject.algebra.poly.MMPolynomial;
import net.mumie.mathletfactory.mmobject.analysis.function.MMFunctionDefByOp;
import net.mumie.mathletfactory.mmobject.analysis.function.MMFunctionSeriesDefByOp;
import net.mumie.mathletfactory.mmobject.analysis.function.MMPiecewiseFunction;
import net.mumie.mathletfactory.mmobject.analysis.function.MMPowerSeriesDefByOp;
import net.mumie.mathletfactory.mmobject.analysis.function.MMTaylorSeriesDefByOp;
import net.mumie.mathletfactory.mmobject.analysis.function.multivariate.MMFunctionOverR2;
import net.mumie.mathletfactory.mmobject.analysis.function.multivariate.MMParametricFunctionInR2;
import net.mumie.mathletfactory.mmobject.analysis.function.multivariate.MMParametricFunctionInR3;
import net.mumie.mathletfactory.mmobject.analysis.sequence.MMRecursiveSequenceDefByOp;
import net.mumie.mathletfactory.mmobject.analysis.sequence.MMSequenceDefByOp;
import net.mumie.mathletfactory.mmobject.analysis.sequence.MMSeriesDefByOp;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DAngle;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DSubspace;
import net.mumie.mathletfactory.mmobject.number.MMComplex;
import net.mumie.mathletfactory.mmobject.number.MMComplexRational;
import net.mumie.mathletfactory.mmobject.number.MMDouble;
import net.mumie.mathletfactory.mmobject.number.MMInteger;
import net.mumie.mathletfactory.mmobject.number.MMOpNumber;
import net.mumie.mathletfactory.mmobject.number.MMRational;
import net.mumie.mathletfactory.mmobject.set.MMInterval;
import net.mumie.mathletfactory.mmobject.set.MMSetDefByRel;
import net.mumie.mathletfactory.mmobject.util.MMDimension;
import net.mumie.mathletfactory.mmobject.util.MMImage;
import net.mumie.mathletfactory.mmobject.util.MMString;
import net.mumie.mathletfactory.mmobject.util.MMStringMatrix;
import net.mumie.mathletfactory.transformer.GeneralTransformer;
import net.mumie.mathletfactory.util.Graphics2DUtils;
import net.mumie.mathletfactory.util.exercise.ImageHelper;

public class GUITest extends JFrame {

	private GUITestControlPanel m_controlPanel;
	private JTabbedPane m_tabPane;
	
	GUITest() {
		super();
		
		m_controlPanel = new GUITestControlPanel();
		m_tabPane = new JTabbedPane();
		m_tabPane.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				m_controlPanel.deselect();
			}
		});

		
		createCards();
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(m_tabPane, BorderLayout.CENTER);
		getContentPane().add(m_controlPanel, BorderLayout.SOUTH);
	}
	
	private void createCards() {
		// primitive numbers
		JPanel numbersPanel = new JPanel(new GridLayout(2, 3));
		numbersPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		numbersPanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMDouble())));
		numbersPanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMDouble(), GeneralTransformer.NUMBER_SLIDER_TRANSFORM)));
		numbersPanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMRational())));
		numbersPanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMInteger())));
		numbersPanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMComplex())));
		numbersPanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMComplexRational())));
		m_tabPane.addTab("Numbers", numbersPanel);
		
		// Symbol
		JPanel symbolPanel = new JPanel(new GridLayout(1, 2));
		symbolPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		symbolPanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMOpNumber())));
		symbolPanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMString())));
		m_tabPane.addTab("Symbol", symbolPanel);
		
		// Functions I
		JPanel function1Panel = new JPanel(new GridLayout(2, 2));
		function1Panel.setBorder(BorderFactory.createLoweredBevelBorder());
		function1Panel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMFunctionDefByOp(MDouble.class, "2x"))));
		function1Panel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMFunctionOverR2(MDouble.class, "2x+2y"))));
		function1Panel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMRelation(MDouble.class, "2x=y AND x=2", MMRelation.NORMALIZE_NONE))));
		function1Panel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMPolynomial(MDouble.class, 4))));
		m_tabPane.addTab("Functions I", function1Panel);

		// Functions II
		JPanel function2Panel = new JPanel(new GridLayout(1, 2));
		function2Panel.setBorder(BorderFactory.createLoweredBevelBorder());
		function2Panel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMParametricFunctionInR2(MDouble.class, "2x", "2y"))));
		function2Panel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMParametricFunctionInR3(MDouble.class, "2x", "2y", "2z"))));
		m_tabPane.addTab("Functions II", function2Panel);

		// Functions III
		JPanel function3Panel = new JPanel(new GridLayout(1, 1));
		function3Panel.setBorder(BorderFactory.createLoweredBevelBorder());
		function3Panel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMPiecewiseFunction(MDouble.class, new String[] {"2x", "2/x"}, new MMInterval[] {new MMInterval(MDouble.class, "(-infinity;0)"), new MMInterval(MDouble.class, "[0;infinity)")}))));
		m_tabPane.addTab("Functions III", function3Panel);

		// Interval
		JPanel intervalPanel = new JPanel(new GridLayout(1, 3));
		intervalPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		intervalPanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMInterval(MDouble.class, "[0;3)"))));
		intervalPanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMInterval(MDouble.class, "[0;3)"), GeneralTransformer.INTERVAL_VARIABLE_TRANSFORM)));
		intervalPanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMSetDefByRel(MDouble.class, "x>2"))));
		m_tabPane.addTab("Interval", intervalPanel);

		// Images
		JPanel imagePanel = new JPanel(new GridLayout(1, 1));
		imagePanel.setBorder(BorderFactory.createLoweredBevelBorder());
		ImageHelper imgHelper = new ImageHelper();
		MMImage img = new MMImage();
		img.setRealImage(Graphics2DUtils.loadImageFromClasspath("/resource/icon/mumie_happy.gif"));
		imagePanel.add(m_controlPanel.addTestPanel(new GUITestPanel(img)));
		m_tabPane.addTab("Images", imagePanel);

		// Geom
		JPanel geomPanel = new JPanel(new GridLayout(1, 2));
		geomPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		geomPanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMAffine2DAngle(MDouble.class))));
		geomPanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMAffine3DSubspace(new AffineSpace(MDouble.class, 2, 3)))));
		m_tabPane.addTab("Geom", geomPanel);

		// Sequence
		JPanel sequencePanel = new JPanel(new GridLayout(3, 2));
		sequencePanel.setBorder(BorderFactory.createLoweredBevelBorder());
		sequencePanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMSequenceDefByOp(MDouble.class, "2x^3"))));
		sequencePanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMRecursiveSequenceDefByOp(MDouble.class, "2x^3"))));
		sequencePanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMSeriesDefByOp(MDouble.class, "2x^3"))));
		sequencePanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMFunctionSeriesDefByOp(MDouble.class, "2x^3"))));
		sequencePanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMTaylorSeriesDefByOp(MDouble.class, new MMFunctionDefByOp(MDouble.class, "2x^3"), 1))));
		sequencePanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMPowerSeriesDefByOp(MDouble.class, "2x^3", 0, new Operation(MDouble.class, "2",true)))));
		m_tabPane.addTab("Sequence", sequencePanel);

		//
//		// System
//		JPanel systemPanel = new JPanel(new GridLayout(1, 1));
//		systemPanel.setBorder(BorderFactory.createLoweredBevelBorder());
//		systemPanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMEquationSystem(MDouble.class))));
//		m_tabPane.addTab("System", systemPanel);

		// Matrices
		JPanel matrixPanel = new JPanel(new GridLayout(2, 2));
		matrixPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		matrixPanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMNumberMatrix(MDouble.class, 2, 2))));
		matrixPanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMStringMatrix(3, 3))));
		matrixPanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMDimension(4, 4))));
		matrixPanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMNumberMatrix(MDouble.class, 4, 4), GeneralTransformer.MATRIX_IMAGE_TRANSFORM)));
		m_tabPane.addTab("Matrices", matrixPanel);

		// Vectors
		JPanel vectorPanel = new JPanel(new GridLayout(1, 2));
		vectorPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		vectorPanel.add(m_controlPanel.addTestPanel(new GUITestPanel(new MMDefaultR2(MDouble.class).getNewFromDefaultCoordinates(1, 2))));
		vectorPanel.add(m_controlPanel.addTestPanel(new GUITestPanel((MMDefaultR3Vector) new MMDefaultR3(MDouble.class).getNewFromCoordinates(1, 2, 3))));
		m_tabPane.addTab("Vectors", vectorPanel);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
    MumieTheme mumieTheme = MumieTheme.DEFAULT_THEME;
    MetalLookAndFeel.setCurrentTheme(mumieTheme);
    try {
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//      SwingUtilities.updateComponentTreeUI(getMyContentPane());
      MathletRuntime.createStaticRuntime(null);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
		GUITest frame = new GUITest();
		frame.setTitle("MathletFactory GUI Test");
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(900, 600);
		frame.setVisible(true);
	}

}

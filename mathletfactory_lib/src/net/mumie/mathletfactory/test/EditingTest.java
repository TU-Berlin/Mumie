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

package net.mumie.mathletfactory.test;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;

import net.mumie.japs.datasheet.DataSheet;
import net.mumie.japs.datasheet.DataSheetException;
import net.mumie.mathletfactory.action.handler.Affine2DMouseTranslateHandler;
import net.mumie.mathletfactory.action.handler.Vector2DMouseGridTranslateHandler;
import net.mumie.mathletfactory.appletskeleton.system.MathletRuntime;
import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MRational;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2Vector;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMNumberMatrix;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMNumberTuple;
import net.mumie.mathletfactory.mmobject.algebra.poly.MMPolynomial;
import net.mumie.mathletfactory.mmobject.analysis.function.MMFunctionDefByOp;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.mmobject.geom.affine.MMCoordinateSystem;
import net.mumie.mathletfactory.mmobject.number.MMComplex;
import net.mumie.mathletfactory.mmobject.number.MMComplexRational;
import net.mumie.mathletfactory.mmobject.number.MMDouble;
import net.mumie.mathletfactory.mmobject.number.MMInteger;
import net.mumie.mathletfactory.mmobject.number.MMOpNumber;
import net.mumie.mathletfactory.mmobject.number.MMRational;
import net.mumie.mathletfactory.mmobject.util.MMString;
import net.mumie.mathletfactory.mmobject.util.MMStringMatrix;
import net.mumie.mathletfactory.util.BasicApplicationFrame;
import net.mumie.mathletfactory.util.xml.DatasheetRenderer;
import net.mumie.mathletfactory.util.xml.ExerciseObjectIF;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Node;

public class EditingTest extends JPanel {

	private DataSheet m_datasheet;
	private boolean m_loadFromDatasheet = true;
	
	public EditingTest() {
		super(new BorderLayout());
		
    Logger.getLogger("").setLevel(Level.OFF);
		loadDatasheet();
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel();
		JCheckBox loadFromDSButton = new JCheckBox("Werte aus XML-Datei laden", m_loadFromDatasheet);
		loadFromDSButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				m_loadFromDatasheet = !m_loadFromDatasheet;
			}
		});
		bottomPanel.add(loadFromDSButton);
		add(bottomPanel, BorderLayout.SOUTH);

		JPanel numberPanel = new JPanel(new GridLayout(3, 2));
		tabbedPane.addTab("Zahlen", numberPanel);

		numberPanel.add(new ComponentPanel(new MMDouble(), "d_1"));
		numberPanel.add(new ComponentPanel(new MMRational(), "r_1"));
		numberPanel.add(new ComponentPanel(new MMComplex(), "c_1"));
		numberPanel.add(new ComponentPanel(new MMComplexRational(), "c_2"));
		numberPanel.add(new ComponentPanel(new MMInteger(), "i_1"));
		numberPanel.add(new ComponentPanel(new MMOpNumber(MRational.class), "f_3"));

		JPanel funcPanel = new JPanel(new GridLayout(3, 2));
		tabbedPane.addTab("Funktionen", funcPanel);
		
		funcPanel.add(new ComponentPanel(new MMFunctionDefByOp(MDouble.class, "0"), "f_1"));
		funcPanel.add(new ComponentPanel(new MMPolynomial(MDouble.class, 4), "f_3"));
		funcPanel.add(new ComponentPanel(new MMString(""), null));
		
		JPanel matrixPanel = new JPanel(new GridLayout(3, 2));
		tabbedPane.addTab("Matrizen/Vektoren", matrixPanel);
		
		JButton completelyEditedButton = new JButton("Completely Edited");
		JPanel mp = new JPanel();
		mp.add(completelyEditedButton);
		
		matrixPanel.add(new ComponentPanel(new MMNumberMatrix(MDouble.class, 2), "m_1"));
		matrixPanel.add(new ComponentPanel(new MMStringMatrix(2, 2), null));
		matrixPanel.add(new ComponentPanel(new MMNumberTuple(MDouble.class, 2), null));

		JPanel canvasAffinePanel = new JPanel(new BorderLayout());
		JPanel affineBottomPanel = new JPanel(new GridLayout(1, 2));
		canvasAffinePanel.add(affineBottomPanel, BorderLayout.SOUTH);
		tabbedPane.addTab("Canvas-Objekte", canvasAffinePanel);
		MMG2DCanvas g2dCanvas = new MMG2DCanvas();
		canvasAffinePanel.add(g2dCanvas, BorderLayout.CENTER);
		g2dCanvas.addObject(new MMCoordinateSystem());
		MMDefaultR2 r2 = new MMDefaultR2(MDouble.class);
		MMDefaultR2Vector vector2D = r2.getNewFromDefaultCoordinates(0, 0);
		vector2D.setCoordinates(1, 1);
		vector2D.getDisplayProperties().setTransparency(0.5);
		vector2D.setObjectColor(Color.RED);
		vector2D.addHandler(new Vector2DMouseGridTranslateHandler(g2dCanvas, 0.2));
		g2dCanvas.addObject(vector2D.getAsCanvasContent());
		MMAffine2DPoint p1 = new MMAffine2DPoint(MDouble.class, 2, 1);
		p1.addHandler(new Affine2DMouseTranslateHandler(g2dCanvas));
		g2dCanvas.addObject(p1.getAsCanvasContent());
		g2dCanvas.autoScale(true);
		affineBottomPanel.add(new ComponentPanel(vector2D, "v_1"));
		affineBottomPanel.add(new ComponentPanel(p1, null));
		
		JPanel dsPanel = new JPanel(new BorderLayout());
		tabbedPane.addTab("Datasheet", dsPanel);
		dsPanel.add(new DatasheetRenderer(m_datasheet));
	}
	
	private void loadDatasheet() {
		m_datasheet = XMLUtils.loadDataSheetFromURL(getClass().getResource("EditingTestValues.xml"));
	}

	class TextAreaDialog extends JDialog {
		
		String m_content;
		
		TextAreaDialog(Component c, String content) {
			setModal(true);
			setLocation(c.getLocationOnScreen());
			getContentPane().setLayout(new BorderLayout());
			
			JLabel titleLabel = new JLabel("MathML-Inhalt zum Laden:");
			getContentPane().add(titleLabel, BorderLayout.NORTH);
			
			final JTextArea area = new JTextArea(content, 15, 50);
			area.setEditable(true);
			area.setBackground(Color.WHITE);
			JScrollPane scroller = new JScrollPane(area);
			getContentPane().add(scroller, BorderLayout.CENTER);
			JPanel bottomPane = new JPanel();
			JButton okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					m_content = area.getText();
					dispose();
				}
			});
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					dispose();
				}
			});
			bottomPane.add(okButton);
			bottomPane.add(cancelButton);
			getContentPane().add(bottomPane, BorderLayout.SOUTH);
			pack();
			setVisible(true);
		}
		
		String getContent() {
			return m_content;
		}
	}
	
	class ComponentPanel extends JPanel {
		
		ExerciseObjectIF m_exObj;
		MMObjectIF m_mmObj;
		String m_dsSubPath;
		String m_oldXMLContent;
		
		ComponentPanel(final Object obj, String dsSubPath) {
			this(obj, dsSubPath, null);
		}
		
		ComponentPanel(final Object obj, String dsSubPath, Component extraComp) {
			super(new BorderLayout());
			m_exObj = (ExerciseObjectIF) obj;
			m_mmObj = (MMObjectIF) obj;
			if(dsSubPath != null)
				m_dsSubPath = "common/problem/" + dsSubPath;
			
			m_mmObj.setEditable(true);
			
			JPanel resizerPanel = new JPanel();
			resizerPanel.add(m_mmObj.getAsContainerContent());
			add(resizerPanel, BorderLayout.CENTER);
			
			if(extraComp != null)
				add(extraComp, BorderLayout.SOUTH);
			
			setBorder(new TitledBorder(getSimpleName(obj.getClass())));
			
			JPanel buttonPanel = new JPanel(new GridLayout(2, 2));
			JPanel buttonResizerPanel = new JPanel();
			buttonResizerPanel.add(buttonPanel);
			add(buttonResizerPanel, BorderLayout.EAST);
			
			JButton clearButton = new JButton("clear");
			clearButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					m_exObj.setEdited(false);
				}
			});
			buttonPanel.add(clearButton);
			JButton loadButton = new JButton("load");
			loadButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					Node xmlNode = null;
					if(m_dsSubPath != null && m_loadFromDatasheet) {
						try {
							xmlNode = m_datasheet.getAsElement(m_dsSubPath);
						} catch (DataSheetException e) {
							e.printStackTrace();
						}
					} else {
						if(m_oldXMLContent == null) {
							if(m_dsSubPath == null)
								m_oldXMLContent = "";
							else {
								try {
									m_oldXMLContent = XMLUtils.nodeToString(m_datasheet.getAsElement(m_dsSubPath), true);
								} catch (DataSheetException e) {
									e.printStackTrace();
								}
							}
						} 
						String content = new TextAreaDialog(ComponentPanel.this, m_oldXMLContent).getContent();
						if(content == null) // user has canceled
							return;
						m_oldXMLContent = content;
						xmlNode = XMLUtils.createElementFromString(content);
					}
					m_exObj.setMathMLNode(xmlNode);
				}
			});
			buttonPanel.add(loadButton);
			JButton renderButton = new JButton("render");
			renderButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					m_mmObj.render();
				}
			});
			buttonPanel.add(renderButton);
			JButton printButton = new JButton("print");
			printButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					System.out.println("------------------------------------------------------");
					System.out.println(getSimpleName(obj.getClass()));
					System.out.println("edited: " + m_exObj.isEdited());
					try {
						Method m = obj.getClass().getMethod("isCompletelyEdited", new Class[0]);
						if(m != null) {
							System.out.println("completely: " + m.invoke(obj, new Object[0]));
						}
					} 
					catch (SecurityException e) {	}
					catch (NoSuchMethodException e) {}
					catch (IllegalArgumentException e) {}
					catch (IllegalAccessException e) {}
					catch (InvocationTargetException e) {}
					
					System.out.println(m_mmObj.toString());
					System.out.println(XMLUtils.nodeToString(m_exObj.getMathMLNode(XMLUtils.getDefaultDocument()), true));
					System.out.println("------------------------------------------------------");
				}
			});
			buttonPanel.add(printButton);
		}
		
	}	
	
	static String getSimpleName(Class aClass) {
		String fullName = aClass.getName();
    try {
    	return fullName.substring(fullName.lastIndexOf('.') + 1);    	
    } catch(IndexOutOfBoundsException e) {// applet is in default package
    	return fullName;
    }
	}

	public static void main(String[] args) {
    MumieTheme mumieTheme = MumieTheme.DEFAULT_THEME;
    MetalLookAndFeel.setCurrentTheme(mumieTheme);
    try {
    	MathletRuntime.createStaticRuntime(null);
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
  		EditingTest editingTestPanel = new EditingTest();  		
      SwingUtilities.updateComponentTreeUI(editingTestPanel);
      BasicApplicationFrame frame = new BasicApplicationFrame(editingTestPanel, 550, 400);
      frame.show();
    } catch (Exception ex) {
      System.err.println(ex);
    }

 	}
}

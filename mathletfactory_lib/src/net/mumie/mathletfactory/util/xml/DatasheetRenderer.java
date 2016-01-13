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

package net.mumie.mathletfactory.util.xml;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.mumie.japs.datasheet.DataSheet;
import net.mumie.japs.datasheet.DataSheetException;
import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.action.message.XMLParsingException;
import net.mumie.mathletfactory.display.layout.MatrixLayout;
import net.mumie.mathletfactory.display.util.TextPanel;
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MComplexRational;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.MRational;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMNumberMatrix;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMNumberTuple;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMOpMatrix;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMTupleSet;
import net.mumie.mathletfactory.mmobject.number.MMOpNumber;
import net.mumie.mathletfactory.mmobject.util.MMStringMatrix;
import net.mumie.mathletfactory.util.ResourceManager;
import net.mumie.mathletfactory.util.exercise.ExerciseConstants;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DatasheetRenderer extends JPanel {

  public final static int EXTENDED_VIEW = 0;
  
  public final static int MARKING_VIEW = 1;
  
  private String[] NUMBER_CLASSES = { "MRational", "MDouble", "MComplex", "MInteger",
      "MComplexRational" };
  
  private String[] NODE_TYPES = { "none", "number", "complex number", "rational number",
  		"number matrix", "operation matrix", "string matrix", "column vector", "row vector",
  		"operation (number)", "plain text", "<marking> node's content"
  };
  
  private final static int NONE_TYPE_INDEX = 0;
  private final static int NUMBER_TYPE_INDEX = 1;
  private final static int COMPLEX_NUMBER_TYPE_INDEX = 2;
  private final static int RATIONAL_NUMBER_TYPE_INDEX = 3;
  private final static int NUMBER_MATRIX_TYPE_INDEX = 4;
  private final static int OPERATION_MATRIX_TYPE_INDEX = 5;
  private final static int STRING_MATRIX_TYPE_INDEX = 6;
  private final static int COLUMN_VECTOR_TYPE_INDEX = 7;
//  private final static int ROW_VECTOR_TYPE_INDEX = 8;
  private final static int OPERATION_TYPE_INDEX = 9;
  private final static int PLAIN_TEXT_TYPE_INDEX = 10;
  private final static int MARKING_TYPE_INDEX = 11;
  
  private int m_view;

  private JPanel m_treePane, m_bottomPane, m_centerPane, m_dynPane, m_typePanel;

  private JLabel m_pathLabel, m_typeLabel;
  
  private CardLayout m_typeLayout;
  
  private boolean m_isUserComboAction = true;

  private JTree m_tree;

  private JComboBox m_classChooser, m_typeChooser;

  private JTabbedPane m_tabPane;

  private JEditorPane m_editorPane;
  
  private JButton m_saveButton;
  private String m_oldDir;
  
  private Class m_numberClass = MRational.class;
  private DataSheet m_ds;
  private String m_selectedPath;
  private int m_matrixType = NUMBER_MATRIX_TYPE_INDEX;

  private Font m_smallFont = new Font("Dialog", Font.PLAIN, 12);
  private Font m_smallItalicFont = new Font("Dialog", Font.ITALIC, 12);
  private Font m_boldFont = new Font("Dialog", Font.BOLD, 12);
  private Font m_largeFont = new Font("Dialog", Font.BOLD, 14);

  public DatasheetRenderer() {
  	this(null, EXTENDED_VIEW);
  }

  public DatasheetRenderer(DataSheet ds) {
    this(ds, EXTENDED_VIEW);
  }
  
  public DatasheetRenderer(int view) {
    this(null, view);
  }
  
  public DatasheetRenderer(DataSheet ds, int view) {
    super(new BorderLayout());
    
    m_view = view;

    m_editorPane = new JEditorPane("text/plain", "");
    JScrollPane scroller2 = new JScrollPane(m_editorPane);

    m_treePane = new JPanel(new BorderLayout());
    m_tabPane = new JTabbedPane();
    m_tabPane.addTab("Tree View", m_treePane);
    m_tabPane.addTab("XML View", scroller2);

    if(view == EXTENDED_VIEW)
      add(m_tabPane, BorderLayout.CENTER);

    m_centerPane = new JPanel(new BorderLayout());
    m_bottomPane = new JPanel(new BorderLayout());

    m_treePane.add(m_centerPane, BorderLayout.CENTER);
    m_treePane.add(m_bottomPane, BorderLayout.SOUTH);

    m_tree = new JTree();
    DatasheetObjectRenderer renderer = new DatasheetObjectRenderer();
    m_tree.setCellRenderer(renderer);

    m_tree.setShowsRootHandles(false);
    JScrollPane scroller = new JScrollPane(m_tree);
    m_centerPane.add(scroller, BorderLayout.CENTER);

    Box leftCol = Box.createVerticalBox();
    leftCol.add(new JLabel("<html><b>Path: "));
    leftCol.add(new JLabel("<html><b>Type: "));
    leftCol.add(new JLabel("<html><b>Number class: "));
    JPanel bottomColsPanel = new JPanel(new BorderLayout());
    bottomColsPanel.setAlignmentY(JPanel.LEFT_ALIGNMENT);
    bottomColsPanel.add(leftCol, BorderLayout.WEST);
    Box rightCol = Box.createVerticalBox();
    m_pathLabel = new JLabel(" ");
    m_pathLabel.setFont(m_smallFont);
//    m_pathLabel.setHorizontalAlignment(JLabel.LEFT);
    rightCol.add(m_pathLabel);
    
    m_typeLabel = new JLabel(" ");
    m_typeChooser = new JComboBox(new String[] {"number matrix", "operation matrix", "string matrix"});
//    m_typeChooser.setEnabled(false);
    m_typeLayout = new CardLayout();
    m_typePanel = new JPanel(m_typeLayout);
    m_typePanel.add(m_typeLabel, "label");
    m_typePanel.add(m_typeChooser, "combo");
    
//    m_typeLabel = new JLabel(" ");
//    m_typeLabel.setFont(m_smallFont);
    rightCol.add(m_typePanel);
    m_classChooser = new JComboBox(NUMBER_CLASSES);
    m_classChooser.setFont(m_smallFont);
//    JPanel classChooserSaveButtonPanel = new JPanel(new GridLayout(1, 2));
//    classChooserSaveButtonPanel.add(m_classChooser);
    m_saveButton = new JButton("Save Datasheet...");
    m_saveButton.setFont(m_smallFont);
//    classChooserSaveButtonPanel.add(m_saveButton);
    rightCol.add(m_classChooser);
    rightCol.add(m_saveButton);
//    rightCol.add(classChooserSaveButtonPanel);
    bottomColsPanel.add(rightCol, BorderLayout.CENTER);
    m_dynPane = new JPanel();
//    JPanel dynPaneResizer = new JPanel()
    if(view == EXTENDED_VIEW)
      m_bottomPane.add(m_dynPane, BorderLayout.CENTER);
    else if(view == MARKING_VIEW)
      add(m_dynPane, BorderLayout.CENTER);
    
    m_bottomPane.add(bottomColsPanel, BorderLayout.SOUTH);

    m_bottomPane.setBorder(new EtchedBorder());
    
    m_saveButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      	String dirToOpen = m_oldDir == null ? "" : m_oldDir;
        JFileChooser fileChooser = new JFileChooser(dirToOpen);
      	int result = fileChooser.showSaveDialog(DatasheetRenderer.this);
        if (result == JFileChooser.APPROVE_OPTION) {
          File file = fileChooser.getSelectedFile();
          m_oldDir = file.getAbsolutePath();
//          String path = file.getAbsolutePath();
          XMLUtils.saveDataSheetToFile(m_ds, file);
        }//else is user canceling
      }
    });
    
    m_typeChooser.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();
        String typeName = (String) cb.getSelectedItem();
        if(typeName.equals(NODE_TYPES[NUMBER_MATRIX_TYPE_INDEX])) {
        		m_matrixType = NUMBER_MATRIX_TYPE_INDEX;
        } else if(typeName.equals(NODE_TYPES[OPERATION_MATRIX_TYPE_INDEX])) {
        		m_matrixType = OPERATION_MATRIX_TYPE_INDEX;
        } else if(typeName.equals(NODE_TYPES[STRING_MATRIX_TYPE_INDEX])) {
      			m_matrixType = STRING_MATRIX_TYPE_INDEX;
        }
        if(m_isUserComboAction)
        		selectionChanged();
      }
    });

    m_classChooser.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();
        String className = (String) cb.getSelectedItem();
        if (className.equals("MDouble")) {
          m_numberClass = MDouble.class;
        } else if (className.equals("MComplex")) {
          m_numberClass = MComplex.class;
        } else if (className.equals("MInteger")) {
          m_numberClass = MInteger.class;
        } else if (className.equals("MRational")) {
          m_numberClass = MRational.class;
        } else if (className.equals("MComplexRational")) {
          m_numberClass = MComplexRational.class;
        }
        TreePath path = m_tree.getSelectionModel().getSelectionPath();
        m_tree.clearSelection();
        m_tree.getSelectionModel().setSelectionPath(path);
      }
    });

    m_tree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
      		selectionChanged();
      }
    });
    
    if(ds != null) {
      setDatasheet(ds);
    }
  }
  
  public void addTab(String tabTitle, Component component) {
    m_tabPane.addTab(tabTitle, component);
  }
  
  private void selectionChanged() {
    m_dynPane.removeAll();
    m_dynPane.setLayout(new FlowLayout());
    m_bottomPane.validate();
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) m_tree
        .getLastSelectedPathComponent();
    if (node == null)
      return;
    m_selectedPath = getDatasheetPath(node);
    m_pathLabel.setText(m_selectedPath);
    setType(NONE_TYPE_INDEX);

    try {
      if (node.getChildCount() == 1) {
        DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) node.getFirstChild();
        final DatasheetObject firstObj = (DatasheetObject) firstChild.getUserObject();
        if(firstObj.getName().equalsIgnoreCase("mtable")) {
          try {
            Element matrixNode = m_ds.getAsElement(getDatasheetPath(node));
            if(matrixNode == null)
              return;
            setType(m_matrixType);
            m_dynPane.add(getMatrixPanel(matrixNode));
          } catch (DataSheetException e1) {
            e1.printStackTrace();
          }
        } else if(firstObj.getName().equalsIgnoreCase("mn")) {
          try {
             Element numberNode = m_ds.getAsElement(getDatasheetPath(node));
            if(numberNode == null)
              return;
            setType(NUMBER_TYPE_INDEX);
            m_dynPane.add(getNumberPanel(numberNode));
          } catch (DataSheetException e3) {
            e3.printStackTrace();
          }
        } else if(firstObj.getName().equalsIgnoreCase("cnum")) {
          try {
            Element numberNode = m_ds.getAsElement(getDatasheetPath(node));
            if(numberNode == null)
              return;
            setType(COMPLEX_NUMBER_TYPE_INDEX);
            m_dynPane.add(getComplexNumberPanel(numberNode));
          } catch (DataSheetException e3) {
            e3.printStackTrace();
          }
        } else if(firstObj.getName().equalsIgnoreCase("mfrac")) {
          try {
            Element numberNode = m_ds.getAsElement(getDatasheetPath(node));
            if(numberNode == null)
              return;
            setType(RATIONAL_NUMBER_TYPE_INDEX);
            m_dynPane.add(getRationalNumberPanel(numberNode));
          } catch (DataSheetException e3) {
            e3.printStackTrace();
          }
        } else if(firstObj.getName().equalsIgnoreCase("mrow")
              && firstObj.getObjectType() == DatasheetObject.COLUMN_VECTOR_TYPE) {
          try {
            Element tupleNode = m_ds.getAsElement(getDatasheetPath(node));
            if(tupleNode == null)
              return;
            setType(COLUMN_VECTOR_TYPE_INDEX);
            m_dynPane.add(getVectorPanel(tupleNode));
          } catch (DataSheetException e3) {
            e3.printStackTrace();
          }
        } else if(firstObj.getName().equalsIgnoreCase("mrow")
              || firstObj.getName().equalsIgnoreCase("msup")
              || firstObj.getName().equalsIgnoreCase("mi")) {
          try {
            Element opNode = m_ds.getAsElement(getDatasheetPath(node));
            if(opNode == null)
              return;
            setType(OPERATION_TYPE_INDEX);
            m_dynPane.add(getOpNumberPanel(opNode));
          } catch (DataSheetException e3) {
            e3.printStackTrace();
          }
        } else if(firstObj.getName().equalsIgnoreCase("marking")) {
        	showMarking();
        } else if(firstObj.getObjectType() == DatasheetObject.STRING_TYPE) {
        		setType(PLAIN_TEXT_TYPE_INDEX);
          JEditorPane l = new JEditorPane("text/plain", firstObj.getName()) {
          	public Dimension getPreferredSize() {
          		if(m_bottomPane == null || firstObj.getName().length() < 10)
          			return super.getPreferredSize();
          		else
          			return new Dimension(m_bottomPane.getWidth()-10, 50);

          	}
          };
          l.setFont(m_largeFont);
          l.revalidate();
          JScrollPane scroller3 = new JScrollPane(l);
          scroller3.setBorder(null);
          m_dynPane.add(scroller3);
        }
        m_bottomPane.validate();
        m_bottomPane.repaint();
      }
    } catch(XMLParsingException pex) {
      m_dynPane.add(new JLabel("<html><center><b>Error while parsing XML node<br>(XMLParsingException)"));
      m_bottomPane.validate();
    } catch(TodoException tex) {
      m_dynPane.add(new JLabel("<html><center><b>Error while parsing XML node<br>(TodoException)"));
      m_bottomPane.validate();
    } catch(NullPointerException npex) {
      m_dynPane.add(new JLabel("<html><center><b>Error while parsing XML node<br>(NullPointerException)"));
      npex.printStackTrace();
      m_bottomPane.validate();
    } catch(UnsupportedOperationException unopex) {
      m_dynPane.add(new JLabel("<html><center><b>Error while parsing XML node<br>(UnsupportedOperationException)"));
      m_bottomPane.validate();
    }
  }
  
  private void setType(int type) {
  		m_typeLabel.setText(NODE_TYPES[type]);
  		if(type == NUMBER_MATRIX_TYPE_INDEX || type == OPERATION_MATRIX_TYPE_INDEX || type == STRING_MATRIX_TYPE_INDEX) {
  			m_isUserComboAction = false;
  			m_typeChooser.setSelectedItem(NODE_TYPES[type]);
  			m_isUserComboAction = true;
  			m_typeLayout.show(m_typePanel, "combo");
  		} else {
  			m_typeLayout.show(m_typePanel, "label");
  		}
  }
  
  private void showMarking() {
    if(m_ds == null)
      return;
    Element markingNode = null;
    Double score = null;
    try {
      markingNode = m_ds.getAsElement(ExerciseConstants.USER_MARKING_PATH);
      score = m_ds.getAsDouble(ExerciseConstants.USER_SCORE_PATH);
    } catch (DataSheetException e) {
      setError(e.toString());
      return;
    }   
    if(markingNode == null || score == null)
      return;
    setType(MARKING_TYPE_INDEX);
    m_dynPane.setLayout(new BorderLayout());
    MatrixLayout layout = new MatrixLayout(1, 3);
    layout.setHorizontalGap(0);
    layout.setVerticalGap(0);
    JPanel markingPanel = new JPanel(layout);
    markingPanel.setBackground(Color.WHITE);
    JComponent c = new TextPanel(ResourceManager.getMessage("exercise.debug.answer_node"));
    c.setBorder(new LineBorder(Color.LIGHT_GRAY));
    markingPanel.add(c);
    c = new TextPanel(ResourceManager.getMessage("exercise.debug.solution_node"));
    c.setBorder(new LineBorder(Color.LIGHT_GRAY));
    markingPanel.add(c);
    c = new TextPanel(ResourceManager.getMessage("exercise.debug.explanation_node"));
    c.setBorder(new LineBorder(Color.LIGHT_GRAY));
    markingPanel.add(c);
    JScrollPane scroller4 = new JScrollPane(markingPanel) {
    	public Dimension getPreferredSize() {
    		Dimension sd = super.getPreferredSize();
    		Dimension d = new Dimension(sd);
    		if(d.height > (int) (0.66*(double)m_treePane.getHeight()))
    			d.height = (int) (0.66*(double)m_treePane.getHeight());
    		return d;
    	}
    };
    m_dynPane.add(scroller4, BorderLayout.CENTER);
    if(m_view == MARKING_VIEW)
      m_dynPane.add(new TextPanel(ResourceManager.getMessage("exercise.debug.score_node") + " " + (int) (score.doubleValue() * 100) + "%"), BorderLayout.NORTH);
    Node subtasksNode = XMLUtils.getNextNonTextNode(markingNode, 0);
    for(int s = 0; s < subtasksNode.getChildNodes().getLength(); s++) {
      Node subtaskNode = subtasksNode.getChildNodes().item(s);
      if(subtaskNode.getNodeType() == Node.TEXT_NODE)
      	continue;
      layout.setDimension(markingPanel.getComponentCount()/3 + 1, 3);
      for(int l = 0; l < subtaskNode.getChildNodes().getLength(); l++) {
        Node firstNode = subtaskNode.getChildNodes().item(l);
        if(firstNode.getNodeType() == Node.TEXT_NODE)
        	continue;
        Box nodePanel = Box.createVerticalBox();
        nodePanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
        markingPanel.add(nodePanel);
        if(firstNode.getChildNodes().getLength() == 0)
        	continue;
        for(int k = 0; k < firstNode.getChildNodes().getLength(); k++) {
          Node mNode = firstNode.getChildNodes().item(k);
          if(mNode.getNodeType() == Node.TEXT_NODE)
          	continue;
        	if(mNode.getNodeName().equalsIgnoreCase("par")) { // paragraph
        		if(mNode.getChildNodes().getLength() == 1 
        				&& mNode.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE) { // only text in paragraph
          		JLabel parLabel = createLabel(false, "<html>" + mNode.getChildNodes().item(0).getNodeValue().trim());
              nodePanel.add(parLabel);          			
        		} else { // text and math content in paragraph
        			JPanel linePanel = new JPanel();
              linePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
              nodePanel.add(linePanel);
          		for(int i = 0; i < mNode.getChildNodes().getLength(); i++) {
          			Node n = mNode.getChildNodes().item(i);
          			if(n.getNodeType() == Node.TEXT_NODE) {
          				linePanel.add(createLabel(false, "<html>" + n.getNodeValue().trim()));
          			} else if(n.getNodeName().equalsIgnoreCase("math")) {
          				showMathNode(n, linePanel);
          			}
          		}
        		}
        	} else if(mNode.getNodeName().equalsIgnoreCase("math")) {
        		Node mathNode = XMLUtils.getNextNonTextNode(mNode, 0);
      			JPanel resizer = new JPanel();
            nodePanel.add(resizer);
      			showMathNode(mNode, resizer);
        	}
        }
      }
    }
  }
  
  private JLabel createLabel(boolean boldFont, String text) {
    JLabel l = new JLabel(text);
    l.setFont(l.getFont().deriveFont(boldFont ? Font.BOLD : Font.PLAIN));
    return l;
  }
  
  private void showMathNode(Node n, JPanel p) {
		Node mathNode = XMLUtils.getNextNonTextNode(n, 0);
//		JPanel resizer = new JPanel();
//		markingPanel.add(resizer);
		if(mathNode.getNodeName().equalsIgnoreCase("mtable")) {
			p.add(getMatrixPanel(mathNode));
		} else if(mathNode.getNodeName().equalsIgnoreCase("mrow")) { // vector and operation
	    if(mathNode.getAttributes() != null && mathNode.getAttributes().getNamedItem("class") != null) {
//	    	String classType = mathNode.getAttributes().getNamedItem("class").getNodeValue().trim();
//	    	if(classType.equalsIgnoreCase("column-vector")) {
	    	if(mathNode.getAttributes().getNamedItem("class").getNodeValue().equalsIgnoreCase("tuple-set"))
	    		p.add(getTupleSetPanel(mathNode));
	    	else p.add(getVectorPanel(mathNode));
	    } else { // op!
	    	p.add(getOpNumberPanel(mathNode));
	    }
		} else if(mathNode.getNodeName().equalsIgnoreCase("mn")) { // number
			p.add(getNumberPanel(mathNode));
		} else if(mathNode.getNodeName().equalsIgnoreCase("cnum")) { // complex number
			p.add(getComplexNumberPanel(mathNode));
		} else if(mathNode.getNodeName().equalsIgnoreCase("mfrac")) { // rational number
			p.add(getRationalNumberPanel(mathNode));
		} else if(mathNode.getNodeName().equalsIgnoreCase("msup")) { // operation, single power
			p.add(getOpNumberPanel(mathNode));
		} else if(mathNode.getNodeName().equalsIgnoreCase("mi")) { // operation, single variable
			p.add(getOpNumberPanel(mathNode));
		}
		else {
			p.add(new JLabel("Unknown math content"));
		}
  }
  
  private JComponent getVectorPanel(Node node) {
    MMNumberTuple tuple = new MMNumberTuple(m_numberClass, 1);
    tuple.setMathMLNode(node);
  	return tuple.getAsContainerContent();
  }
  
  private JComponent getTupleSetPanel(Node node) {
  	MMTupleSet set = new MMTupleSet(m_numberClass,0);
  	set.setMathMLNode(node);
  	return set.getAsContainerContent();
  }
  
  private JComponent getMatrixPanel(Node node) {
  		if(m_matrixType == NUMBER_MATRIX_TYPE_INDEX) {
  	    MMNumberMatrix matrix = new MMNumberMatrix(m_numberClass, 1);
  	    matrix.setMathMLNode(node);
  	  		return matrix.getAsContainerContent();
  		} else if(m_matrixType == OPERATION_MATRIX_TYPE_INDEX) {
  	    MMOpMatrix matrix = new MMOpMatrix(m_numberClass, 1);
  	    matrix.setMathMLNode(node);
  	  		return matrix.getAsContainerContent();
  		} else if(m_matrixType == STRING_MATRIX_TYPE_INDEX) {
  	    MMStringMatrix matrix = new MMStringMatrix(1, 1);
  	    matrix.setMathMLNode(node);
  	  		return matrix.getAsContainerContent();
  		}
  		return null;
  }
  
//  private JComponent getOperationPanel(Node node) {
//    Operation op = new Operation(m_numberClass, "0", false);
//    op.setMathMLNode(node);
//    MMFunctionDefByOp func = new MMFunctionDefByOp(op, FiniteBorelSet.getRealAxis(MDouble.class));
//    func.getOperation().setNormalForm(false);
//    MMFunctionPanel funcPanel = (MMFunctionPanel) func.getAsContainerContent();
//    funcPanel.setFont(m_largeFont);
//    funcPanel.setFunctionLabelVisible(false);
//    return funcPanel;
//  }
  
  private JComponent getOpNumberPanel(Node node) {
  	MMOpNumber number = new MMOpNumber(m_numberClass);
  	number.setMathMLNode(node);
  	return number.getAsContainerContent();
  }
  
  private JComponent getNumberPanel(Node node) {
    MNumber number = NumberFactory.newInstance(m_numberClass);
    number.setMathMLNode(node);
    MMObjectIF numberPanel = NumberFactory.getNewMMInstanceFor(number);
    return numberPanel.getAsContainerContent();  	
  }
  
  private JComponent getComplexNumberPanel(Node node) {
    boolean globalClassIsComplex = m_numberClass.equals(MComplex.class) || m_numberClass.equals(MComplexRational.class);
    MNumber number = NumberFactory.newInstance(globalClassIsComplex ? m_numberClass : MComplex.class);
    number.setMathMLNode(node);
    MMObjectIF numberPanel = NumberFactory.getNewMMInstanceFor(number);
    return numberPanel.getAsContainerContent();  	
  }
  
  private JComponent getRationalNumberPanel(Node node) {
    MNumber number = NumberFactory.newInstance(MRational.class);
    number.setMathMLNode(node);
    MMObjectIF numberPanel = NumberFactory.getNewMMInstanceFor(number);
  	return numberPanel.getAsContainerContent();
  }
  
  public void setError(String message) {
  	remove(m_tabPane);
  	add(new JLabel("<html><b><font color='red'>" + message));
  }

  public void setDatasheet(DataSheet ds) {
  	m_ds = ds;
    Node dsNode = ds.getRootElement();
    DefaultMutableTreeNode topNode = new DefaultMutableTreeNode(new DatasheetObject(""));
    // for all subnodes of <data_sheet> node
    for(int i = 0; i < dsNode.getChildNodes().getLength(); i++) {
      if(dsNode.getChildNodes().item(i).getNodeType() == Node.TEXT_NODE)
        continue;
      DefaultMutableTreeNode childNode = createNodes(dsNode.getChildNodes().item(i));
      topNode.add(childNode);
    }
    m_tree.setModel(new DefaultTreeModel(topNode));
    m_tree.setRootVisible(false);
    for(int j = 0; j < topNode.getChildCount(); j++) {
      TreeNode level1Node = topNode.getChildAt(j);
      for(int k = 0; k < level1Node.getChildCount(); k++) {
        TreeNode level2Node = level1Node.getChildAt(k);
        for(int m = 0; m < level2Node.getChildCount(); m++) {
          TreeNode level3Node = level2Node.getChildAt(m);
          m_tree.scrollPathToVisible(new TreePath(((DefaultMutableTreeNode)level3Node).getPath()));
        }
      }
    }
    if(m_view == MARKING_VIEW)
      showMarking();
    m_centerPane.revalidate();
    try {
    	m_editorPane.setText(ds.toXMLCode());
  		m_editorPane.revalidate();
		} catch (DataSheetException e) {
			e.printStackTrace();
		}
  }

  public DefaultMutableTreeNode createNodes(Node top) {
    String nodeName = null;
    int objectType = DatasheetObject.UNKNOWN_TYPE;
    if (top.getAttributes() != null
        && top.getAttributes().getNamedItem("name") != null) {
      Node attr = top.getAttributes().getNamedItem("name");
      nodeName = attr.getNodeValue();
    } else {
      if (top.getNodeType() == Node.TEXT_NODE)
        nodeName = top.getNodeValue().trim();
      else
        nodeName = top.getNodeName().trim();
    }
    DatasheetObject dsObj = new DatasheetObject(nodeName);
    DefaultMutableTreeNode result = new DefaultMutableTreeNode(dsObj);
    for (int i = 0; i < top.getChildNodes().getLength(); i++) {
      if (top.getChildNodes().item(i).getNodeType() == Node.TEXT_NODE
          && top.getChildNodes().item(i).getNodeValue().trim().length() == 0)
        continue;
      result.add(createNodes(top.getChildNodes().item(i)));
    }
    // setting node type property
    if(top.getNodeName().equalsIgnoreCase("data"))
      ((DatasheetObject)result.getUserObject()).setNodeType(DatasheetObject.DATA_TYPE);
    else if(top.getNodeName().equalsIgnoreCase("unit"))
      ((DatasheetObject)result.getUserObject()).setNodeType(DatasheetObject.UNIT_TYPE);
    else if(top.getNodeType() == Node.TEXT_NODE)
      ((DatasheetObject)result.getUserObject()).setObjectType(DatasheetObject.STRING_TYPE);

    // reading class attribute and setting object type property
    if(top.getAttributes() != null && top.getAttributes().getNamedItem("class") != null) {
    	String classType = top.getAttributes().getNamedItem("class").getNodeValue().trim();
    	if(classType.equalsIgnoreCase("column-vector")) {
    		objectType = DatasheetObject.COLUMN_VECTOR_TYPE;
    	} else if(classType.equalsIgnoreCase("bmatrix")) {
    		objectType = DatasheetObject.MATRIX_TYPE;
    	}
    	((DatasheetObject)result.getUserObject()).setObjectType(objectType);
    }
    return result;
  }

  public String getDatasheetPath(DefaultMutableTreeNode n) {
    String result = new String();
    TreeNode[] paths = n.getPath();
    // first is datasheet root, last is node itself
    for (int i = 1; i < paths.length; i++) {
      result += paths[i];
      if (i < paths.length - 1)// not after last child!
        result += "/";
    }
    return result;
  }

  public JTree getTree() {
  	return m_tree;
  }

  class DatasheetObjectRenderer extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree tree, Object value,
        boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
          hasFocus);
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
      if(!(node.getUserObject() instanceof DatasheetObject))
        return this;
      DatasheetObject obj = (DatasheetObject) node.getUserObject();
      if(obj.getNodeType() == DatasheetObject.DATA_TYPE) {
        setIcon(new ImageIcon(getClass().getResource("/resource/icon/bean16.gif")));
        setFont(m_boldFont);
      } else if(obj.getNodeType() == DatasheetObject.UNIT_TYPE) {
        setIcon(new ImageIcon(getClass().getResource("/resource/icon/jar16.gif")));
        setFont(m_smallFont);
      } else {
//        if(!node.isLeaf())
//          setIcon(new ImageIcon(getClass().getResource("/resource/icon/jar16.gif")));
        setFont(m_smallItalicFont);
      }
      return this;
    }
  }

  class DatasheetObject {

    public final static int UNKNOWN_TYPE = 0;
    public final static int UNIT_TYPE = 1;
    public final static int DATA_TYPE = 2;
    public final static int NUMBER_TYPE = 10;
    public final static int MATRIX_TYPE = 11;
    public final static int COLUMN_VECTOR_TYPE = 12;
    public final static int ROW_VECTOR_TYPE = 13;
    public final static int STRING_TYPE = 20;

    private String m_name, m_display;//, m_path
    private int m_nodeType = UNKNOWN_TYPE;
    private int m_objectType = UNKNOWN_TYPE;

    DatasheetObject(String name) {
      setName(name);
    }

    String getName() {
      return m_name;
    }

    void setName(String name) {
      m_name = name;
      if(m_display == null)
        m_display = name;
    }

    void setNodeType(int type) {
      m_nodeType = type;
    }

    int getNodeType() {
      return m_nodeType;
    }

    void setObjectType(int type) {
    	m_objectType = type;
    }

    int getObjectType() {
    	return m_objectType;
    }

    public String toString() {
      return getName();
    }
  }
}

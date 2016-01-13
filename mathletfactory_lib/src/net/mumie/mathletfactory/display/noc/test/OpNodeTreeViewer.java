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
import java.awt.Canvas;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.mumie.mathletfactory.appletskeleton.system.MathletRuntime;
import net.mumie.mathletfactory.math.algebra.op.OpTransform;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.node.VariableOp;
import net.mumie.mathletfactory.math.algebra.op.rule.OpRule;
import net.mumie.mathletfactory.math.algebra.op.rule.expand.ExpandInternalDataRule;
import net.mumie.mathletfactory.math.algebra.op.rule.expand.ExpandPowerOfSumRule;
import net.mumie.mathletfactory.math.algebra.op.rule.expand.ExpandPowerRule;
import net.mumie.mathletfactory.math.algebra.op.rule.expand.ExpandProductRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.CollapseEqualOpsRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.CollapseInverseFunctionRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.CollapseNrtRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.CollapsePowerOfIRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.CollapsePowerRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.CollapseRationalPowerRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.HandleFunctionSymmetryRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.NormalizeAddRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.NormalizeConstantRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.NormalizeMultRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.PropagateConstantsRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.RemoveNeutralElementRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.RemoveZeroExponentRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.RemoveZeroMultRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.SummarizeEqualAddChildrenRule;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.SummarizeEqualMultChildrenRule;
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.mmobject.number.MMDouble;

/**
 * This class shows graphically the tree of operation nodes of a given expression.
 * 
 * @author jweber
 *
 */
public class OpNodeTreeViewer extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private final int width = 800;
	private final int hight = 600;

	private SelectDialog m_dialog = new SelectDialog(this, false);
	
	private OperationView m_operation = new OperationView();
	
	private String m_oldExpression = "0";
	
	private TreeCanvas m_tree = new TreeCanvas(m_operation.getOperation().getOpRoot());
		
	private ButtonPanel m_buttons = new ButtonPanel();
	
	public OpNodeTreeViewer() {
		super("TreeView");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		
		JPanel treePanel = new JPanel();
		treePanel.setBorder(new TitledBorder("TreeView"));
		treePanel.setLayout(new BorderLayout());
		treePanel.add(BorderLayout.CENTER, m_tree);
		
		getContentPane().add(BorderLayout.NORTH, m_operation);
		getContentPane().add(BorderLayout.CENTER, treePanel);
		getContentPane().add(BorderLayout.SOUTH, m_buttons);
		
		setSize(width, hight);
		addActionListeners();		
	}
	
	public void addActionListeners() {
		ActionListener applyRule = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_dialog.pack();
				m_dialog.setVisible(true);
//				if (m_dialog.getExitStatus() == SelectDialog.CANCEL)
//					return;
//				m_tree.setOpRoot(OpTransform.applyRules(m_tree.getOpRoot(), m_dialog.getSelectedRules()));
//				m_operation.setOperation(m_tree.getOpRoot());
			}
		};
		
		ActionListener normalize = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_tree.setOpRoot(OpTransform.normalize(m_tree.getOpRoot()));
				m_operation.setOperation(m_tree.getOpRoot());
			}
		};
		
    ActionListener expand = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        m_tree.setOpRoot(OpTransform.expand(m_tree.getOpRoot()));
        m_operation.setOperation(m_tree.getOpRoot());
      }
    };
    
		ActionListener reset = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		};
		
		m_buttons.addActionListener(applyRule, m_buttons.APPLY_RULE_NR);
		m_buttons.addActionListener(normalize, m_buttons.NORMALIZE_NR);
    m_buttons.addActionListener(expand, m_buttons.EXPAND_NR);
		m_buttons.addActionListener(reset, m_buttons.RESET_NR);
		
		m_operation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_oldExpression = m_operation.getText();
				refreshAll();
			}
		});
	}
	
	private void refreshAll() {
		m_operation.setOperation(m_operation.getText());
		m_tree.setOpRoot(m_operation.getOperation().getOpRoot());
	}

	public void reset() {
		m_operation.setOperation(m_oldExpression);
		refreshAll();
	}
	
	class TreeCanvas extends Canvas {
		private static final long serialVersionUID = 1L;
		
		private final int width = 100;
		
		private final int hight = 70;
		
		private OpNode m_root;
		
		public TreeCanvas(OpNode root) {
			m_root = root;
			setBackground(Color.WHITE);
		}
		
		public void paint(Graphics g) {
			g.translate(this.getWidth() / 2, 5);
			drawNode(g, m_root, -50, 10);
			
			if (m_root.getChildren() != null) { 
				drawTree(g, new int[] {0}, 10 + hight, new OpNode[][] {m_root.getChildren()});
			}
		}	
		
		private void drawNode(Graphics g, OpNode node, int x, int y) {
			g.drawRect(x, y, width, hight);
			g.drawLine(x, y + 20, width + x, y + 20);
			g.drawString(getSimpleName(node.getClass()), x + 20, y + 15);
			
			x = x + 5;
			y = y + 35;
			
			g.drawString("Faktor = " + node.getFactor().toString(), x, y);
			
			if (node.getClass().isAssignableFrom(NumberOp.class)) {
				if (node.nodeToString().length() > 5)
					g.drawString("Base = " + node.nodeToString().substring(0, 5) + "...", x, y + 15);
				else
					g.drawString("Base = " + node.nodeToString(), x, y + 15);
				y = y + 15;
			}
			
			if (node.getClass().isAssignableFrom(VariableOp.class)) {
				g.drawString("Identifier = " + ((VariableOp)node).getIdentifier(), x, y + 15);
				y = y + 15;
			}
			
			g.drawString("Exponent = " + node.getExponent(), x, y + 15);
		}
		
		private void drawTree(Graphics g, int[] x, int y, OpNode[][] children) {
			if (children == null) return;
			
			int childrenCount = 0;
			int differenz = 0;
						
			for (int i = 0; i < children.length; i++) {
				if (children[i] == null) continue;
				childrenCount = childrenCount + children[i].length;
			}
						
			int start = -1 * (childrenCount * width + (children.length - 1) * 20) / 2;
			
			int[] otherX = new int[childrenCount];
			OpNode[] allChildren = new OpNode[childrenCount];
			int count = 0;
			
			for (int i = 0; i < children.length; i++) {
				if (children[i] == null) continue;
				start = start + differenz + i * 20;
				for (int j = 0; j < children[i].length; j++) {
					drawNode(g, children[i][j], start + j * width, y + 20);
					otherX[count] = start + j * width + width / 2;
					allChildren[count++] = children[i][j];
				}
				differenz = children[i].length * width; 
				
				g.setColor(Color.BLUE);
				g.drawLine(x[i], y, (start + differenz / 2), y + 20);
				g.setColor(Color.BLACK);
			}
			drawTree(g, otherX, y + 20 + hight, getChildrenNodes(allChildren));
		}
				
		public OpNode getOpRoot() {
			return m_root;
		}
		
		public void setOpRoot(OpNode root) {
			m_root = root;
			repaint();
		}
		
		public OpNode[][] getChildrenNodes(OpNode[] root) {
			boolean getResult = false;
			OpNode[][] result = new OpNode[root.length][];
			
			for (int i = 0; i < root.length; i++) { 
				result[i] = root[i].getChildren();
				if (result[i] != null) getResult = true;
			}
			
			if (getResult) return result;
			return null;
		}
	}
	
	class ButtonPanel extends JPanel {
		private static final long serialVersionUID = 1L;
	
		public final String[] BUTTONS = new String[] {"Normalize", "Expand", "Apply rule", "Reset"};
		
		public final int NORMALIZE_NR = 0;
		
    public final int EXPAND_NR = 1;
    
		public final int APPLY_RULE_NR = 2;
		
		public final int RESET_NR = 3;
		
		private Vector m_buttons = new Vector();
		
		public ButtonPanel() {
			super();
			setLayout(new FlowLayout());
			
			for (int i = 0; i < BUTTONS.length; i++) {
				m_buttons.add(new JButton(BUTTONS[i]));
				add(((JButton) m_buttons.get(i)));
			}
		}
		
		public void addActionListener(ActionListener l, int buttonNr) {
			if (buttonNr < m_buttons.size()) 
				((JButton) m_buttons.get(buttonNr)).addActionListener(l);
		}
		
		public void removeActionListener(ActionListener l, int buttonNr) {
			if (buttonNr < m_buttons.size()) 
				((JButton) m_buttons.get(buttonNr)).removeActionListener(l);
		}		
	}
	
	class OperationView extends JPanel {
		private static final long serialVersionUID = 1L;

		private Operation m_operation;
		
		private JTextField m_panel = new JTextField(20);
		private MMDouble test = new MMDouble();
		
		public OperationView() {
			super();
			setBorder(new TitledBorder("Operation"));
			setLayout(new GridLayout(2, 1));
			
			m_operation = new Operation(MComplex.class, "0", false);
			m_panel.setText("0");
			
			m_panel.setEditable(true);
						
			add(m_panel);
			
			test.setEditable(true);
			add(test.getAsContainerContent());
		}
				
		public void addActionListener(ActionListener l) {
			m_panel.addActionListener(l);
		}
		
		public void removeActionListener(ActionListener l) {
			m_panel.removeActionListener(l);
		}
		
		public Operation getOperation() {
			return m_operation;
		}	
		
		public void setOperation(OpNode root) {
			m_operation.setOpRoot(root);
			m_panel.setText(m_operation.toString());
		}
		
		public void setOperation(String t) {
			setOperation(new Operation(MComplex.class, t, false).getOpRoot());
		}
		
		public String getText() {
			return m_panel.getText();
		}
	}
		
	class SelectDialog extends JDialog {
		private static final long serialVersionUID = 1L;

		public static final int OK = 1;
		
		public static final int CANCEL = 0;
		
		public final OpRule[] selectableRules = new OpRule[] {
				new NormalizeMultRule(), new CollapseEqualOpsRule(),
				new CollapsePowerOfIRule(), new PropagateConstantsRule(),
				new RemoveZeroMultRule(), new RemoveZeroExponentRule(),
				new SummarizeEqualAddChildrenRule(),
				new SummarizeEqualMultChildrenRule(),
				new RemoveNeutralElementRule(), new CollapsePowerRule(),
				new CollapseRationalPowerRule(), new NormalizeConstantRule(),
				new CollapseInverseFunctionRule(), new CollapseNrtRule(),
				new HandleFunctionSymmetryRule(), new NormalizeAddRule(),
			    new ExpandProductRule(), new ExpandInternalDataRule(),
			    new ExpandPowerRule(), new ExpandPowerOfSumRule()
		};

		private int m_status = CANCEL;
		
		private int m_selectedCount = 0;
		
		private OpRule[] m_selected_rules;
		
		private Vector m_rules = new Vector();

		private JButton m_applyButton = new JButton("Apply Rule(s)"), m_closeButton = new JButton("Close");
						
		private JPanel m_button_panel = new JPanel();

		private JPanel m_rules_panel = new JPanel();

		public SelectDialog(Frame window, boolean modal) {
			super(window, modal);

			this.setTitle("Rules");
			this.setResizable(false);
			this.getContentPane().setLayout(new BorderLayout());
			m_button_panel.setLayout(new FlowLayout());
			m_rules_panel.setLayout(new GridLayout(0, 1));
			
			for (int i = 0; i < selectableRules.length; i++) {
				m_rules.add(new RuleCheckBox(selectableRules[i]));
				m_rules_panel.add(((RuleCheckBox) m_rules.get(i)));
			}

			m_button_panel.add(m_applyButton);
			m_button_panel.add(m_closeButton);

			this.getContentPane().add(BorderLayout.NORTH, m_rules_panel);
			this.getContentPane().add(BorderLayout.SOUTH, m_button_panel);
			
			addActions();
		}
		
		private void addActions() {
			ActionListener selectRuleAction = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JCheckBox checkBox = (JCheckBox)e.getSource();
					if (checkBox.isSelected()) 
						m_selectedCount++;
					else
						m_selectedCount--;
				}
			};
			
			for (int i = 0; i < m_rules.size(); i++) {
				((RuleCheckBox) m_rules.get(i)).addActionListener(selectRuleAction);
			}
			
			m_applyButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					m_selected_rules = new OpRule[m_selectedCount];
					
					int count = 0;
					for (int i = 0; i < m_rules.size(); i++) {
						if (((RuleCheckBox) m_rules.get(i)).isSelected())
							m_selected_rules[count++] = ((RuleCheckBox) m_rules.get(i)).getRule();
					}	
					m_status = OK;
					applyRules();
				}
			});
			
			m_closeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					m_status = CANCEL;
					setVisible(false);
				}
			});
		}
		
		private void applyRules() {
			if (getExitStatus() == SelectDialog.CANCEL)
				return;
			m_tree.setOpRoot(OpTransform.applyRules(m_tree.getOpRoot(), getSelectedRules()));
			m_operation.setOperation(m_tree.getOpRoot());
		}
		
		public OpRule[] getSelectedRules() {
			return m_selected_rules;
		}
		
		public int getExitStatus() {
			return m_status;
		}
		
		public void addRules(OpRule[] rules) {
			for (int i = 0; i < rules.length; i++) {
				addRule(rules[i]);
			}
		}
		
		public void addRule(OpRule rule) {
			System.out.println(isContained(rule));
			if (isContained(rule) == -1) {
				m_rules.add(new RuleCheckBox(rule));;
				m_rules_panel.add(new RuleCheckBox(rule));
				validate();
			}
		}
		
		private int isContained(OpRule rule) {
			for (int i = 0; i < m_rules.size(); i++) {
				if (getSimpleName(((RuleCheckBox) m_rules.get(i)).getRule().getClass()).equals(getSimpleName(rule.getClass())))
					return i;
			}
			return -1;
		}
		
		public OpRule deleteRule(OpRule rule) {
			int index = isContained(rule);
			if (index != -1) { 
				m_rules_panel.remove(((RuleCheckBox) m_rules.get(index)));
				validate();
				return ((RuleCheckBox) m_rules.remove(index)).getRule();
			}
			return null;
		}
		
		class RuleCheckBox extends JCheckBox {
			private static final long serialVersionUID = 1L;
			
			private OpRule m_rule;
			
			public RuleCheckBox(OpRule rule) {
				super(getSimpleName(rule.getClass()));
				m_rule = rule;
			}
			
			public OpRule getRule() {
				return m_rule;
			}
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
		try {
			MathletRuntime.createStaticRuntime(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		OpNodeTreeViewer window = new OpNodeTreeViewer();
		window.setVisible(true);
	}
}
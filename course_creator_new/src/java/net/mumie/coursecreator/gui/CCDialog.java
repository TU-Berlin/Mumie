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

/**
 * ueberklasse fuer ccdialogklassen mit defaultbutton und closebutton
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.mumie.coursecreator.CCController;
import net.mumie.coursecreator.CourseCreator;

public class CCDialog extends JDialog  implements FocusListener
,ComponentListener
, ActionListener
, KeyListener{
	
	static final long serialVersionUID=0;
	protected CCController cccontroller;
	protected Color bgColor = new Color(220,220,220);
    
	protected JButton defaultButton;
	protected JButton closeButton;
	
    protected WindowListener windowListener =
		new WindowAdapter ()
	{
		public void windowClosing (WindowEvent event)
		{
			CCDialog dialog = CCDialog.this;
			dialog.stop();
		}
	};
	
	
	public CCDialog(CourseCreator frame ,CCController c,String name){
	
		
		super(CCController.frame,name);
		this.cccontroller = c;
		
		this.addComponentListener(this);
		
	}

	/** erzeugt Close und Defaultbutton mit listener this */
	protected JPanel createDefaultNCloseButton(){
		GridBagLayout gbl = new GridBagLayout();
		JPanel filePanel	= new JPanel();
		filePanel.setLayout(gbl);
		filePanel.setSize(this.getSize().width, 30);
		
		 this.defaultButton = new JButton("DefaultWerte");
		 this.setConstrains(defaultButton, gbl, 0, 0, GridBagConstraints.NONE,
				 new Insets(1,1,1,1), GridBagConstraints.SOUTHWEST);
		 
		 defaultButton.addActionListener(this);
		filePanel.add(defaultButton); 
		
		this.closeButton = new JButton("Beenden");
		 this.setConstrains(closeButton, gbl, 1, 0, GridBagConstraints.NONE,
				 new Insets(1,1,1,1), GridBagConstraints.EAST);
		 
		 closeButton.addActionListener(this);
		filePanel.add(closeButton);
		return filePanel;
	}
	
	/**
	 * 
	 * @param key
	 * @param s
	 */
	protected JFormattedTextField createTextfield( String s, GridBagLayout gbl,int index, JPanel panel, int compHeight) {
		
		
		JFormattedTextField key = new JFormattedTextField();
		
		key.addKeyListener(this);
		key.addFocusListener(this);
		key.setBackground(bgColor);
		setConstrains(key, gbl,1, index,GridBagConstraints.NONE, 100,compHeight, new Insets(1,2,1,1),0,0);
		
		JLabel label = new JLabel(s);
		
		setConstrains(label, gbl,0, index,GridBagConstraints.HORIZONTAL,100,compHeight,new Insets(1,1,1,2),1,0);
		
		panel.add(label);
		
		return key;
		
	}
	
	/**
	 * 
	 * @param comp
	 * @param gbl
	 * @param gridx
	 * @param gridy
	 * @param fill
	 * @param ipadx
	 * @param ipady
	 * @param insets
	 * @param weightx
	 * @param weighty
	 */
	protected void setConstrains(Component comp, GridBagLayout gbl,int gridx,int gridy, int fill, int ipadx, int ipady, Insets insets, double weightx, double weighty) {
		GridBagConstraints constrains = new GridBagConstraints();
		constrains.gridx=gridx;
		constrains.gridy=gridy;
		
		constrains.fill = fill;
		constrains.ipadx = ipadx;
		constrains.anchor = GridBagConstraints.EAST;
		constrains.insets = insets;
		constrains.weightx = weightx;
		constrains.weighty = weighty;
		gbl.setConstraints(comp,constrains);
	}
	/**
	 * sets constrains to the buttons
	 * @param comp the button
	 * @param gbl the layout
	 * @param gridx 
	 * @param gridy
	 * @param fill
	 * @param insets
	 * @param anchor
	 */
	protected void setConstrains(Component comp, GridBagLayout gbl,int gridx,int gridy, int fill, Insets insets, int anchor) {
		GridBagConstraints constrains = new GridBagConstraints();
		constrains.gridx=gridx;
		constrains.gridy=gridy;
		
		constrains.fill = fill;
		constrains.anchor = GridBagConstraints.EAST;
		constrains.insets = insets;
		constrains.anchor = anchor;
		gbl.setConstraints(comp,constrains);
	}
	/** (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.closeButton)) this.stop() ;
	}

	public void focusGained(FocusEvent e) {}
	public void focusLost(FocusEvent e) {}
	
	public void componentMoved(ComponentEvent e) {	}
	public void componentHidden(ComponentEvent e) {}
	public void componentResized(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	
	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	
	
	/** sets this dialog non-visible */
	public void stop(){
		this.setVisible(false);
	}
	
}

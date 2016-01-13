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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;
import javax.swing.JPanel;

import net.mumie.coursecreator.CourseCreator;
import net.mumie.coursecreator.CCController;

/** dialog for debugging<br>
 * its just for programmers.
 * @author <a href="mailto:vrichter@math.tu-berlin.de">Verena Richter</a>
 * @version $Id: DebugDialog.java,v 1.5 2009/03/19 15:53:30 vrichter Exp $
 */
public class DebugDialog extends JDialog {
	
	static final long serialVersionUID=0;
	private CourseCreator cc;
	private CCController controller;
	
	
	private DebugFrame debugFrame;
	
	
	/**
	 * The window listener.
	 */

	private WindowListener windowListener =
		new WindowAdapter ()
	{
		public void windowClosing (WindowEvent event)
		{
			DebugDialog dialog = DebugDialog.this;
			dialog.stop();
		}
	};
	
	public DebugDialog(CCController cccontroller) {
		super(CCController.frame,"Debug-Dialog");
		cc = CCController.frame;
		controller = cccontroller;	
		
		this.buildLayout();
		
	}
	
	/** do the Layout */
	private void buildLayout(){
		
		// Dimensions of the Dialog		
		int heightField = 70;
		int smallheightField = 35;
		int numFields = 7;
		
	    // sets the Layout
		this.getContentPane().setLayout(new GridBagLayout());
	    this.addWindowListener(this.windowListener);
	    
	    JPanel mainPane = new JPanel(new GridLayout(numFields,1));
	    
	    this.debugFrame = new DebugFrame(controller.getConfig(),cc,true);
	    
	    
	    mainPane.add(this.debugFrame);
	    // panel for general options
	    
	    
	    GridBagConstraints mainPaneStyle = new GridBagConstraints();
	    mainPaneStyle.anchor = GridBagConstraints.CENTER;
	    mainPaneStyle.insets.top = 4;
	    mainPaneStyle.insets.right = 4;
	    mainPaneStyle.insets.bottom = 4;
	    mainPaneStyle.insets.left = 4;
	    mainPaneStyle.gridx = 0;
	    mainPaneStyle.gridy = 0;
	    
	    this.getContentPane().add(mainPane,mainPaneStyle);
	    
	    int height = heightField *numFields+smallheightField;
	    this.setSize(this.debugFrame.getWidth()+20, height);	 
	}


	
	/** set this window non-visible */	
	public void stop(){
		this.setVisible(false);
	}
	
	void babble(String bab){
		if (this.controller.getConfig().getDEBUG()) System.out.println("CCLogindialog:"+bab);
	}
	
}

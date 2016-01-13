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


import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import net.mumie.coursecreator.CCController;

/**
 * diese Klasse soll die Graphischen Einstellungen koordinieren.
 * <b> wird bisher nicht benutzt</b>
 * es muss fuer die einzelnen Graphtypen unterschieden werden.
 * @author <a href="mailto:vrichter@math.tu-berlin.de">Verena Richter</a>
 */
public class PresentationDialog extends CCDialog{
	static final long serialVersionUID=0;
	
	
	private int compHeight = 15;
	private int width = 350;
	private int height = 580;
    
	private WindowListener windowListener =
		new WindowAdapter (){
		public void windowClosing (WindowEvent event)	{
			PresentationDialog dialog = PresentationDialog.this;
			dialog.stop();
		}
	};
	
	public PresentationDialog(CCController c) {
	
		super(CCController.frame,c,"Darstellung");
		this.cccontroller = c;
		this.buildLayout();
		this.addComponentListener(this);
		
		this.setSettings();
		
	}

	private void buildLayout(){

		this.setResizable(false);
		
		setSize(this.width,this.height);
		this.setLocation(this.cccontroller.getConfig().getPOSITION_PRESENTATION());
		this.setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));
		
		this.add(this.createNavGraph());
		this.add(this.createFlatGraph());
		this.add(this.createDefaultNCloseButton());
		
		this.addWindowListener(this.windowListener);
	}
		

	/** Einstellungen fuer den NavGraph*/
	private JPanel createNavGraph(){
		GridBagLayout gbl = new GridBagLayout();
		
		JPanel filePanel	= new JPanel();
		filePanel.setLayout(gbl);
		filePanel.setBorder(new TitledBorder(new EtchedBorder(),"NavGraph"));
		filePanel.setSize(this.width, this.compHeight*3);
		
		return filePanel;
		
	}
	
	/** Einstellungen fuer den FlatGraph*/
	private JPanel createFlatGraph(){//TODO implement this
		GridBagLayout gbl = new GridBagLayout();
		
		JPanel filePanel	= new JPanel();
		filePanel.setLayout(gbl);
		filePanel.setBorder(new TitledBorder(new EtchedBorder(),"FlatGraph"));
		filePanel.setSize(this.width, this.compHeight*3);
		
		return filePanel;
	}
	
	/**
	 * sets to all this.key_ ..s the value of the configclass
	 *
	 */
	private void setSettings(){//TODO implement this
		
	}
	
}

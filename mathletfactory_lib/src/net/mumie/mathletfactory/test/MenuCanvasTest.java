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

/**
 * MenuCanvasTest.java
 *
 * @author Created by Omnicore CodeGuide
 */

package net.mumie.mathletfactory.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

import net.mumie.mathletfactory.action.handler.global.GlobalHandler;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;
import net.mumie.mathletfactory.display.layout.SimpleGridConstraints;
import net.mumie.mathletfactory.display.layout.SimpleGridLayout;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

public class MenuCanvasTest extends JPanel implements ActionListener {

	private MMG2DCanvas m_canvas;
	private JRadioButtonMenuItem[] ghItems;

	public MenuCanvasTest() {

		SimpleGridLayout sgl = new SimpleGridLayout(1, 2, this);
		sgl.fixRow(2);

		m_canvas = new MMG2DCanvas();
		setBackground(m_canvas.getBackground());
		m_canvas.addObject(new MMAffine2DPoint(MDouble.class, 0, 0));

		add(m_canvas, new SimpleGridConstraints(1, 1));

		JMenuBar menubar = new JMenuBar();
		menubar.setBackground(m_canvas.getBackground());
		add(menubar, new SimpleGridConstraints(1, 2));

		JMenu globHandlerMenu = new JMenu("globalHandler");
		globHandlerMenu.setBackground(m_canvas.getBackground());
		menubar.add(globHandlerMenu);
		GlobalHandler[] globalHandler = m_canvas.getGlobalHandlers();
		ghItems = new JRadioButtonMenuItem[globalHandler.length];
		for (int i = 0; i < globalHandler.length; i++) {
			ghItems[i] =
				new JRadioButtonMenuItem(
					globalHandler[i].getClass().getName(),
					m_canvas.getGlobalHandler(i).isActive());
			ghItems[i].setBackground(m_canvas.getBackground());
			ghItems[i].addActionListener(this);
			globHandlerMenu.add(ghItems[i]);
		}
	}

	public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < m_canvas.getGlobalHandlerCount(); i++) {
			if (e.getSource() == ghItems[i]) {
				GlobalHandler gh = m_canvas.getGlobalHandler(i);
				m_canvas.getGlobalHandler(i).setActive(ghItems[i].isSelected());
			}
		}
	}

	public static void main(String[] args) {
		java.util.logging.Logger.getLogger("").setLevel(
			java.util.logging.Level.WARNING);
		MenuCanvasTest myPanel = new MenuCanvasTest();
		BasicApplicationFrame f = new BasicApplicationFrame(myPanel);
		f.pack();
		f.setVisible(true);
	}

}

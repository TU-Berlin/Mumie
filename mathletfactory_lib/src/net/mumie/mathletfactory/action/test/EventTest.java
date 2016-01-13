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

package net.mumie.mathletfactory.action.test;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JApplet;
import javax.swing.JPanel;

import net.mumie.mathletfactory.util.BasicApplicationFrame;

public class EventTest extends JApplet implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
	
	private JPanel panel;
	
	public void init() {
		panel = new JPanel();
		panel.setFocusable(true);
		
		panel.addMouseListener(this);
		panel.addMouseMotionListener(this);
		panel.addMouseWheelListener(this);
		panel.addKeyListener(this);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panel);
	}
	
	public void mouseClicked(MouseEvent e) {
		System.out.println("mouseClicked");
	}

	public void mouseEntered(MouseEvent e) {
		System.out.println("mouseEntered");
	}

	public void mouseExited(MouseEvent e) {
		System.out.println("mouseExited");
	}

	public void mousePressed(MouseEvent e) {
		System.out.println("mousePressed");
	}

	public void mouseReleased(MouseEvent e) {
		System.out.println("mouseReleased");
	}

	public void mouseDragged(MouseEvent e) {
		System.out.println("mouseDragged");
	}

	public void mouseMoved(MouseEvent e) {
		System.out.println("mouseMoved");
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		System.out.println("mouseWheelMoved");
	}

	public void keyPressed(KeyEvent e) {
		System.out.println("keyPressed");
	}

	public void keyReleased(KeyEvent e) {
		System.out.println("keyReleased");
	}

	public void keyTyped(KeyEvent e) {
		System.out.println("keyTyped");
	}

	public static void main(String[] args) {
		EventTest testApplet = new EventTest();
		testApplet.init();
		testApplet.start();
		BasicApplicationFrame frame = new BasicApplicationFrame(testApplet, 400, 400);
		frame.show();
	}

}

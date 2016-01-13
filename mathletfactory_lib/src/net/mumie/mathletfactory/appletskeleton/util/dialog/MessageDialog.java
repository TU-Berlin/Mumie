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

package net.mumie.mathletfactory.appletskeleton.util.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.mumie.mathletfactory.appletskeleton.MathletContext;

public class MessageDialog extends JDialog {
	
	public final static int INFO_MESSAGE = 0;
	public final static int ERROR_MESSAGE = 1;
	
	final static String INFO_ICON_PATH = "/resource/icon/info24.gif";
	
	final static String ERROR_ICON_PATH = "/resource/icon/error.png";
	
	public MessageDialog(MathletContext mathlet, Object message, String messageID) {
		this(mathlet, message, INFO_MESSAGE, messageID);
	}
	
	public MessageDialog(MathletContext mathlet, Object message, int messageType, String messageID) {
		super(mathlet.getAppletFrame(), true);
		setResizable(false);
		JLabel iconLabel = new JLabel();
		if(messageType == INFO_MESSAGE) {
			iconLabel.setIcon(new ImageIcon(getClass().getResource(INFO_ICON_PATH)));
			setTitle(mathlet.getMessage("Message"));
		} else if(messageType == ERROR_MESSAGE) {
			iconLabel.setIcon(new ImageIcon(getClass().getResource(ERROR_ICON_PATH)));
			setTitle(mathlet.getMessage("Error"));
		}
		iconLabel.setBorder(new EmptyBorder(20, 20, 0, 20));
		
		JPanel bottomPane = new JPanel();
		bottomPane.setBorder(new EmptyBorder(0, 5, 10, 5));
		JButton closeButton = new JButton("OK");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				dispose();
			}
		});
		bottomPane.add(closeButton);
		JLabel contentLabel = new JLabel(message.toString());
		contentLabel.setBorder(new EmptyBorder(10, 5, 0, 10));
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(iconLabel, BorderLayout.WEST);
		getContentPane().add(contentLabel, BorderLayout.CENTER);
		getContentPane().add(bottomPane, BorderLayout.SOUTH);
		
		// install F1 help
		mathlet.getRuntimeSupport().installHelp(getRootPane(), messageID);
		
		// install help button (if java help is available)
		if(mathlet.getRuntimeSupport().isJavaHelpAvailable()) {
			JButton helpButton = new JButton(mathlet.getMessage("Help"));
			bottomPane.add(helpButton);
			mathlet.getRuntimeSupport().installHelp(helpButton, messageID);
		}
		
		pack();
		center();
		setVisible(true);
	}
	
  /** Centers this frame on the screen. */
  public void center() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (screenSize.width - getWidth())/2;
    int y = (screenSize.height - getHeight())/2;
    setLocation(x,y);
  }
}

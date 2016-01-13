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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.JLabel;

import net.mumie.mathletfactory.appletskeleton.MathletContext;
import net.mumie.mathletfactory.appletskeleton.system.SystemPropertyIF;
import net.mumie.mathletfactory.display.util.PlainTextView;
import net.mumie.mathletfactory.display.util.ViewPanel;
import net.mumie.mathletfactory.util.ResourceManager;

public class AboutDialog extends DefaultDialog {
	
	private final static Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
	private final static Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);

	public AboutDialog(MathletContext mathlet, Object[] params) {
		super(mathlet, params);
	}
	
	public void finishGenericLayout() {
		insertLineBreak();
		setLeftAlignment();
		addText("\\textbf{Mathlet Name:} ");
		addControl(new ViewPanel(new PlainTextView(getContext().getShortName())));
		insertLineBreak();
		addText("\\textbf{Mathlet Version}: ");
		String mathletVersion = getContext().getMathletRuntime().getSystemProperty(SystemPropertyIF.MATHLET_VERSION_PROPERTY);
		addText(mathletVersion != null ? mathletVersion : "???");
		// centerPane.insertLineBreak();
		// centerPane.addText("<b>Mathlet Datum: ");
		// centerPane.addText(m_mathletDate != null ? m_mathletDate : "???");
		insertLineBreak();
		addText("\\textbf{Library Version}: ");
		String libVersion = getContext().getMathletRuntime().getSystemProperty(SystemPropertyIF.LIB_VERSION_PROPERTY);
		addText(libVersion != null ? libVersion : "???");
		insertLineBreaks(2);
		setLeftAlignment();
		addText("\\textbf{Powered by Mumie} (");
		final JLabel hyperLink = new JLabel("http://www.mumie.net");
		hyperLink.setForeground(Color.BLUE);
		hyperLink.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				try {
					getContext().getJApplet().getAppletContext().showDocument(new URL("http://www.mumie.net"));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
			public void mouseEntered(MouseEvent e) {
				hyperLink.setCursor(HAND_CURSOR);
			}
			
			public void mouseExited(MouseEvent e) {
				hyperLink.setCursor(DEFAULT_CURSOR);
			}
		});
		addControl(hyperLink);
		addText(")");
		insertLineBreak();
		setIcon(ResourceManager.getIcon("/resource/icon/mumie_icon_large.png"));
	}
}

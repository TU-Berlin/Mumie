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

package net.mumie.mathletfactory.display.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class can be used to obtain screen type dependant labels as specified 
 * in the Label Specification.
 * TODO Label Specification
 * 
 * @author gronau
 * @mm.docstatus outstanding
 */
public class LabelSupport {

	public final static int ST_NO_CANVAS = 0;
	public final static int ST_2D_CANVAS = 1;
	public final static int ST_3D_CANVAS = 2;
	
	private final static String[] SCREEN_TYPE_NAMES = { "noc", "2d", "3d" };
	
	private final int m_screenType;
	private String m_typeLabel, m_labelString;
	
	public LabelSupport(int screenType) {
		m_screenType = screenType;
	}
	
	public int getScreenType() {
		return m_screenType;
	}
	
	public void setLabel(String label) {
		// break if new and old label are equal
		if(m_labelString != null) {
			if(label != null && m_labelString.equals(label))
				return;
		} else { // m_labelString is null!
			// new label is also null
			if(label == null)
				return;
		}
		m_labelString = label;
		// reset value
		if(label == null) {
			m_typeLabel = null;
			return;
		}
		m_typeLabel = new String(label);
		for(int i = 0; i < SCREEN_TYPE_NAMES.length; i++) {
			// break if no more screen type labels are left in type label
			if(m_typeLabel.indexOf('$') == -1 || m_typeLabel.length() == 0)
				break;
			/*
			 * explanation of choosen regular expression:
			 * ------------------------------------------
			 * "\\$"					'$' character
			 * "(\\+?|\\-?)"	group for plus or minus sign (or none)
			 * "\\[...\\]"		'[' and ']' characters in label around "..."
			 * "([^\\$]+)"		group for content ("..."); any character except '$'; multiple times ('+')
			*/
			String expression = "\\$(\\+?|\\-?)" + SCREEN_TYPE_NAMES[i] + "\\[([^\\$]+)\\]";
			Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
			boolean found = false;
			do {
				Matcher matcher = pattern.matcher(m_typeLabel);
				found = matcher.find();
				if(found) {
					String plusMinusSign = matcher.group(1);
					String innerContent = matcher.group(2);
					boolean included = (plusMinusSign.length() == 0 || plusMinusSign.equals("+"));
					boolean thisGroup = (i == getScreenType());
					m_typeLabel = matcher.replaceFirst(thisGroup && included ? innerContent : "");
				}
			} while(found);
		}
	}
	
	public String getLabel() {
		return m_typeLabel;
	}

	public static void main(String[] args) {
		System.out.println("---------noc------------");
		LabelSupport ls1 = new LabelSupport(ST_NO_CANVAS);
		ls1.setLabel("v_1$noc[=]");
		System.out.println(ls1.getLabel());
		ls1.setLabel("$noc[ is element of ]");
		System.out.println(ls1.getLabel());
		ls1.setLabel("v$noc[<]$noc[=]$2d[-]");
		System.out.println(ls1.getLabel());
		ls1.setLabel("v$+noc[=]");
		System.out.println(ls1.getLabel());
		ls1.setLabel("v$-noc[=]");
		System.out.println(ls1.getLabel());
		ls1.setLabel("v$2d[=]");
		System.out.println(ls1.getLabel());
		System.out.println("---------2d-------------");
		LabelSupport ls2 = new LabelSupport(ST_2D_CANVAS);
		ls2.setLabel("v$2d[=]");
		System.out.println(ls2.getLabel());
		ls2.setLabel("v$noc[<]$noc[=]$2d[-]");
		System.out.println(ls2.getLabel());
	}
}

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

import java.util.regex.Pattern;

import javax.swing.JComponent;

import net.mumie.mathletfactory.display.util.tex.TeXView;

public class StyledTextView {

	/**
	 * Returns an appropriate {@link ViewIF} instance for the given text that can be used
	 * to render arbitrary plaint text, TeX and HTML content on a default component.
	 * 
	 * @param text a plaint text, TeX or HTML expression
	 */
	public static ViewIF createStyledTextView(String text) {
		return createStyledTextView(text, null);
	}
	
	/**
	 * Returns an appropriate {@link ViewIF} instance for the given text that can be used
	 * to render arbitrary plaint text, TeX and HTML content on the given component.
	 * 
	 * @param text a plaint text, TeX or HTML expression
	 */
	public static ViewIF createStyledTextView(String text, JComponent c) {
		// TeX text
		if(isTeXContent(text))
			return TeXView.createTeXView(text);
		// HTML text
		if(isHTMLContent(text))
			return new HTMLView(text, c);
		// render plain text with TeX viewer
		return TeXView.createTeXView(text);
	}

	
	public static boolean isTeXContent(String text) {
		/*
		 * explanation of choosen regular expression:
		 * ------------------------------------------
		 * "[a-zA-Z]+"     one or many characters, a-z & A-Z (command NAME)
		 * "\\\\(?:...)"    group for command name; non-capturing group ("?:") 
		 * "\\{(...)\\}"   group for content between brackets
		 * "?:.*"          content between brackets; zero or more ("*") characters (any, "."); non-capturing group ("?:") 
		*/
		// TeX command with opening and closing brackets
		if(matches("\\\\(?:[a-zA-Z]+)\\{(?:.*)\\}", text))
			return true;
		return false;
	}

	public static boolean isHTMLContent(String text) {
		/*
		 * explanation of choosen regular expression:
		 * ------------------------------------------
		 * "[a-zA-Z]+"       one or many characters, a-z & A-Z (tag NAME)
		 * "\\<(...)\\>"     group for opening tag
		 * "\\<\\/(...)\\>"	 group for closing tag
		 * "\\1"             tag name of closing tag must match capturing group #1
		 * "(?:.*)"          content between tags; zero or more ("*") characters (any, "."); non-capturing group ("?:") 
		*/
		// expression with opening and closing tags
		if(matches("\\<([a-zA-Z]+)\\>(?:.*)\\<\\/(\\1)\\>", text))
			return true;
		// expression with opening "html" tag
		if(matches("\\<([html]+)\\>", text))
			return true;
		// expression with opening "b" tag
		if(matches("\\<([b]+)\\>", text))
			return true;
		return false;
	}
	
	private static boolean matches(String expression, String text) {
		Pattern p = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		return p.matcher(text).find();
	}
}

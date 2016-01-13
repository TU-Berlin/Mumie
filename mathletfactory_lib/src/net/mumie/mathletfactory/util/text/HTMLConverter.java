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

package net.mumie.mathletfactory.util.text;


/**
 * This class offers methods for transforming written HTML into Unicode texts.
 *
 * @author gronau
 * @mm.docstatus outstanding
 */
public class HTMLConverter {

	private final static CharacterReplacement[] HTML_REPLACEMENTS = {
		new CharacterReplacement("&szlig;", 0x00DF),
		new CharacterReplacement("&auml;", 0x00E4),
		new CharacterReplacement("&Auml;", 0x00C4),
		new CharacterReplacement("&ouml;", 0x00F6),
		new CharacterReplacement("&Ouml;", 0x00D6),
		new CharacterReplacement("&uuml;", 0x00FC),
		new CharacterReplacement("&Uuml;", 0x00DC),
		new CharacterReplacement("&nbsp;", 0x0020)
	};

	/**
	 * Returns a copy of the given text where all special characters in the given text are replaced.
	 * @param text a text containing special characters
	 */
	public static String toUnicode(String text) {
		String result = new String(text);
		// replace HTML commands (e.g. "&auml;")
		for(int i = 0; i < HTML_REPLACEMENTS.length; i++) {
			result = result.replaceAll(HTML_REPLACEMENTS[i].pattern, Character.toString(HTML_REPLACEMENTS[i].replacement));
		}
		return result;
	}
}

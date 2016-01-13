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

import net.mumie.mathletfactory.action.message.TodoException;


/**
 * This class converts written Unicode expressions (e.g. \\u220) to their expressed Unicode character pendants and vice versa.
 * Written Unicode expressions can be used in non-Unicode encodings (such as ASCII or UTF8).
 * Expressed Unicode characters can be used in all Unicode-enabled technologies (such as Java).
 *
 * @author gronau
 * @mm.docstatus finished
 */
public class UnicodeConverter {
	
	/** Constant pointing to the Java syntax for written Unicode expressions (e.g. "\u2200"). */
	public final static int JAVA_SYNTAX = 0;
	
	/** Constant pointing to the XML syntax for written Unicode expressions (e.g. "&#x2200"). */
	public final static int XML_SYNTAX = 1;

	/**
	 * Parses the given text for written Unicode expressions and replaces them with their expressed Unicode pendants.
	 * The resulting string is returned.
	 * @param text a text containing written Unicode characters
	 * @param syntax a constant pointing to a specific syntax for the written Unicode expressions
	 * @return a text containing expressed Unicode characters
	 * @see #JAVA_SYNTAX
	 * @see #XML_SYNTAX
	 */
	public static String getExpressedUnicode(String text, int syntax) {
		if(syntax == JAVA_SYNTAX)
			return getExpressedJavaUnicode(text);
		if(syntax == XML_SYNTAX)
			throw new TodoException("XML syntax not yet implemented!");
		throw new IllegalArgumentException("Illegal syntax constant: " + syntax);
	}
	
	/**
	 * Parses the given text for written Unicode expressions in Java syntax and replaces them with their expressed Unicode pendants.
	 * The resulting string is returned.
	 * @param text a text containing written Unicode characters in Java syntax
	 * @return a text containing expressed Unicode characters
	 */
	public static String getExpressedJavaUnicode(String text) {
		String result = new String(text);
		int pos = 0;
		while((pos = result.indexOf("\\u", pos)) != -1) {
			try {
				String hexValue = result.substring(pos+2, pos+2+4);
				char charValue = (char) Integer.valueOf(hexValue, 16).intValue();
				result = result.replaceAll("\\\\u" + hexValue, new String(new char[] { charValue }));
			} catch(StringIndexOutOfBoundsException e) {
				throw new IllegalArgumentException("Illegal Unicode definition: " + text);
			}
		}
		return result;
	}
	
	/**
	 * Parses the given text for non-ASCII characters and replaces each of them with their written Unicode pendants.
	 * The resulting string is returned.
	 * @param text a text containing expressed Unicode characters
	 * @param syntax a constant pointing to a specific syntax for the written Unicode expressions
	 * @return a text containing written Unicode characters
	 * @see #JAVA_SYNTAX
	 * @see #XML_SYNTAX
	 */
	public static String getWrittenUnicode(String text, int syntax) {
		if(syntax == JAVA_SYNTAX)
			return getWrittenJavaUnicode(text);
		if(syntax == XML_SYNTAX)
			return getWrittenXMLUnicode(text);
		throw new IllegalArgumentException("Illegal syntax constant: " + syntax);
	}
	
	/**
	 * Parses the given text for non-ASCII characters and replaces each of them with their written Unicode pendants in Java syntax.
	 * The resulting string is returned.
	 * @param text a text containing expressed Unicode characters in Java syntax
	 * @return a text containing written Unicode characters
	 */
	public static String getWrittenJavaUnicode(String text) {
		StringBuffer result = new StringBuffer();
		for(int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			// append ASCII characters 
			if(c < 128)
				result.append(c);
			else
				result.append("\\u" + toHexString(c, 4));
		}
		return result.toString();
	}
	
	/**
	 * Parses the given text for non-ASCII characters and replaces each of them with their written Unicode pendants in XML syntax.
	 * The resulting string is returned.
	 * @param text a text containing expressed Unicode characters in XML syntax
	 * @return a text containing written Unicode characters
	 */
	public static String getWrittenXMLUnicode(String text) {
		StringBuffer result = new StringBuffer();
		for(int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			// append ASCII characters 
			if(c < 128)
				result.append(c);
			else
				result.append("&#x" + toHexString(c, 4));
		}
		return result.toString();
	}
	
	/**
	 * Returns the string representation of the given character as a hexadecimal value with the specified minimum number of bits.
	 * @param c a character
	 * @param bitCount minimum number if bits in the resulting string
	 */
	public static String toHexString(char c, int bitCount) {
		StringBuffer buffer = new StringBuffer();
		String hexValue = Integer.toHexString(c);
		for(int i = 0; i < bitCount - hexValue.length(); i++) {
			buffer.append('0');
		}
		return buffer.toString() + hexValue;
	}
}

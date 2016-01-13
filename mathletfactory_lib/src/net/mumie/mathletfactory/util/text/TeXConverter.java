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
 * This class offers methods for transforming written TeX into Unicode texts.
 *
 * @author gronau
 * @mm.docstatus outstanding
 */
public class TeXConverter {
	
	private final static char ACCENT_GRAVE_CHAR = '`';

	private final static char ACCENT_ACUTE_CHAR = '\'';

	private final static char ACCENT_CIRCUMFLEX_CHAR = '^';

	private final static char ACCENT_TILDE_CHAR = '~';

	private final static char UMLAUT_CHAR = '\"';

	private final static SpecialCharacter[] SPECIAL_CHARACTERS = {
			new SpecialCharacter('a', new CharacterTransform[] {
					new CharacterTransform(ACCENT_GRAVE_CHAR, 127),
					new CharacterTransform(ACCENT_ACUTE_CHAR, 128),
					new CharacterTransform(ACCENT_CIRCUMFLEX_CHAR, 129),
					new CharacterTransform(ACCENT_TILDE_CHAR, 130),
					new CharacterTransform(UMLAUT_CHAR, 131) }),
			new SpecialCharacter('e', new CharacterTransform[] {
					new CharacterTransform(ACCENT_GRAVE_CHAR, 131),
					new CharacterTransform(ACCENT_ACUTE_CHAR, 132),
					new CharacterTransform(ACCENT_CIRCUMFLEX_CHAR, 133),
					new CharacterTransform(UMLAUT_CHAR, 134) }),
			new SpecialCharacter('i', new CharacterTransform[] {
					new CharacterTransform(ACCENT_GRAVE_CHAR, 131),
					new CharacterTransform(ACCENT_ACUTE_CHAR, 132),
					new CharacterTransform(ACCENT_CIRCUMFLEX_CHAR, 133),
					new CharacterTransform(UMLAUT_CHAR, 134) }),
			new SpecialCharacter('n',
					new CharacterTransform[] { new CharacterTransform(ACCENT_TILDE_CHAR,
							131) }),
			new SpecialCharacter('o', new CharacterTransform[] {
					new CharacterTransform(ACCENT_GRAVE_CHAR, 131),
					new CharacterTransform(ACCENT_ACUTE_CHAR, 132),
					new CharacterTransform(ACCENT_CIRCUMFLEX_CHAR, 133),
					new CharacterTransform(ACCENT_TILDE_CHAR, 134),
					new CharacterTransform(UMLAUT_CHAR, 135) }),
			new SpecialCharacter('s',
					new CharacterTransform[] { new CharacterTransform(UMLAUT_CHAR,108) }),					
			new SpecialCharacter('u', new CharacterTransform[] {
					new CharacterTransform(ACCENT_GRAVE_CHAR, 132),
					new CharacterTransform(ACCENT_ACUTE_CHAR, 133),
					new CharacterTransform(ACCENT_CIRCUMFLEX_CHAR, 134),
					new CharacterTransform(UMLAUT_CHAR, 135) }),
			new SpecialCharacter('y', new CharacterTransform[] {
					new CharacterTransform(ACCENT_ACUTE_CHAR, 132),
					new CharacterTransform(UMLAUT_CHAR, 134, false) }) };

	private final static CharacterReplacement[] CHARACTER_REPLACEMENTS = {
	// more language characters from alphabet Latin-1
			new CharacterReplacement("\\ss", 223),
			new CharacterReplacement("\\ua", 229),
			new CharacterReplacement("\\uA", 197),
			new CharacterReplacement("\\ae", 230),
			new CharacterReplacement("\\AE", 198),
			new CharacterReplacement("\\cc", 231),
			new CharacterReplacement("\\cC", 199),
			new CharacterReplacement("\\o", 248),
			new CharacterReplacement("\\O", 216),
			new CharacterReplacement("\\oe", 240),
			new CharacterReplacement("\\OE", 208),
			new CharacterReplacement("\\l", 254),
			new CharacterReplacement("\\L", 222),
			new CharacterReplacement("?'", 191), 
			new CharacterReplacement("!'", 161),
			
			// mathematical characters
			new CharacterReplacement("\\pm", 177),
			new CharacterReplacement("\\mp", 0x2213),
//			new CharacterReplacement("\\ldots", 0x2026), // font causes problems with this glyph
			new CharacterReplacement("\\cdot", 0x2219),
			new CharacterReplacement("\\nabla", 0x2207),
			new CharacterReplacement("\\partial", 0x2202),
			new CharacterReplacement("\\forall", 0x2200),
			new CharacterReplacement("\\exists", 0x2203),
			new CharacterReplacement("\\in", 0x2208),
//		new CharacterReplacement("\\not\\in", 0x220C),
			new CharacterReplacement("\\empty", 0x2205),
			new CharacterReplacement("\\infty", 0x221E),
			new CharacterReplacement("\\perp", 0x22A5),
			new CharacterReplacement("\\sim", 0x223C),
			new CharacterReplacement("\\simeq", 0x2243),
			new CharacterReplacement("\\equiv", 0x2261),
			new CharacterReplacement("\\approx", 0x2248),
			new CharacterReplacement("\\ne", 0x2260),
			new CharacterReplacement("\\cong", 0x2245),
			new CharacterReplacement("\\le", 0x2264),
			new CharacterReplacement("\\ge", 0x2265),
			
			//characters from alphabet Mathematical Symbols/Arrows
			new CharacterReplacement("\\rightarrow", 0x2192),
			new CharacterReplacement("\\Rightarrow", 0x21D2),
			new CharacterReplacement("\\leftarrow", 0x2190),
			new CharacterReplacement("\\Leftarrow", 0x21D0),
			new CharacterReplacement("\\leftrightarrow", 0x2194),
			new CharacterReplacement("\\Leftrightarrow", 0x21D4),
			new CharacterReplacement("\\middot", 0x22C5),
			new CharacterReplacement("\\quad", 0x00A0),
			new CharacterReplacement("\\qquad", 0x2001),
			
			// greek characters from alphabet Latin-1
			new CharacterReplacement("\\alpha", 0x03B1),
			new CharacterReplacement("\\Alpha", 0x0391),
			new CharacterReplacement("\\beta", 0x03B2),
			new CharacterReplacement("\\Beta", 0x0392),
			new CharacterReplacement("\\gamma", 0x03B3),
			new CharacterReplacement("\\Gamma", 0x0393),
			new CharacterReplacement("\\delta", 0x03B4),
			new CharacterReplacement("\\Delta", 0x0394),
			new CharacterReplacement("\\epsilon", 0x03B5),
			new CharacterReplacement("\\Epsilon", 0x0395),
			new CharacterReplacement("\\zeta", 0x03B6),
			new CharacterReplacement("\\Zeta", 0x0396),
			new CharacterReplacement("\\eta", 0x03B7),
			new CharacterReplacement("\\Eta", 0x0397),
			new CharacterReplacement("\\theta", 0x03B8),
			new CharacterReplacement("\\Theta", 0x0398),
			new CharacterReplacement("\\iota", 0x03B9),
			new CharacterReplacement("\\Iota", 0x0399),
			new CharacterReplacement("\\kappa", 0x03BA),
			new CharacterReplacement("\\Kappa", 0x039A),
			new CharacterReplacement("\\lambda", 0x03BB),
			new CharacterReplacement("\\Lambda", 0x039B),
			new CharacterReplacement("\\mu", 0x03BC),
			new CharacterReplacement("\\Mu", 0x039C),
			new CharacterReplacement("\\nu", 0x03BD),
			new CharacterReplacement("\\Nu", 0x039D),
			new CharacterReplacement("\\xi", 0x03BE),
			new CharacterReplacement("\\Xi", 0x039E),
			new CharacterReplacement("\\omicron", 0x03BF),
			new CharacterReplacement("\\Omicron", 0x039F),
			new CharacterReplacement("\\pi", 0x03C0),
			new CharacterReplacement("\\Pi", 0x03A0),
			new CharacterReplacement("\\rho", 0x03C1),
			new CharacterReplacement("\\Rho", 0x03A1),
//		new CharacterReplacement("\\", 0x03C2), // no name ?
//		new CharacterReplacement("\\", 0x03A2), // does not exist !
			new CharacterReplacement("\\sigma", 0x03C3),
			new CharacterReplacement("\\Sigma", 0x03A3),
			new CharacterReplacement("\\tau", 0x03C4),
			new CharacterReplacement("\\Tau", 0x03A4),
			new CharacterReplacement("\\upsilon", 0x03C5),
			new CharacterReplacement("\\Upsilon", 0x03A5),
			new CharacterReplacement("\\phi", 0x03C6),
			new CharacterReplacement("\\Phi", 0x03A6),
			new CharacterReplacement("\\chi", 0x03C7),
			new CharacterReplacement("\\Chi", 0x03A7),
			new CharacterReplacement("\\psi", 0x03C8),
			new CharacterReplacement("\\Psi", 0x03A8),
			new CharacterReplacement("\\omega", 0x03C9),
			new CharacterReplacement("\\Omega", 0x03A9),
			
			new CharacterReplacement("\\R", 0x211D),
			new CharacterReplacement("\\Z", 0x2124),
			new CharacterReplacement("\\N", 0x2115),
			new CharacterReplacement("\\C", 0x2102),
			new CharacterReplacement("\\Q", 0x211A)
		};

	
	/**
	 * Returns a copy of the given text where all unnecessary white spaces and line breaks are removed. 
	 */
	public static String trim(String text) {
		StringBuffer result = new StringBuffer();
		boolean spaceAdded = false; // flag indicating if a "space" was added as last character
		for(int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			// remove space characters
			if(c == ' ') {
				if(spaceAdded)
					continue;
				spaceAdded = true;
			}
			// remove line feeds
			else if(c == '\n') {
				// replace line feed with space character (if necessary)
				if(!spaceAdded) {
					result.append(' ');
					spaceAdded = true;
				}
				continue;
			}
			// remove windows line feeds
			else if(c == '\r')
				continue;
			// clear flag for all other characters
			else
				spaceAdded = false;
			result.append(c);
		}
		return result.toString();
	}
	
	/**
	 * Returns a copy of the given text where all special characters in the given text are replaced.
	 * @param text a text containing special characters
	 */
	public static String toUnicode(String text) {
		String result = new String(text);
		for(int i = 0; i < SPECIAL_CHARACTERS.length; i++) {
			for(int t = 0; t < SPECIAL_CHARACTERS[i].transforms.length; t++) {
				result = replaceAll(result, SPECIAL_CHARACTERS[i].baseChar, SPECIAL_CHARACTERS[i].transforms[t]);
			}
		}
		for(int i = 0; i < CHARACTER_REPLACEMENTS.length; i++) {
			result = replaceAll(result, CHARACTER_REPLACEMENTS[i]);
		}
		return result;
	}
	
	/*
	 * Replaces all occurrences of the specified CharacterReplacement in the given text.
	 */
	private static String replaceAll(String text, CharacterReplacement cr) {
		StringBuffer sb = new StringBuffer(text);
		int index = 0;
		while((index = sb.indexOf(cr.pattern, index)) != -1) {
			int afterPos = index + cr.pattern.length();
			if(afterPos < sb.length()) {
				// test if this pattern is contained in a longer pattern with same beginning
				if(Character.isLetterOrDigit(sb.charAt(afterPos))) {
					index = afterPos;
					continue;
				}
			}
			log("Pre-Parse: replacing \"" + sb.substring(index, afterPos) + "\"");
			sb.replace(index, afterPos, Character.toString(cr.replacement));
		}
		return sb.toString();
	}
	
	/*
	 * Replaces all occurrences of the specified CharacterTransform for the specified base character in the given text.
	 */
	private static String replaceAll(String text, char baseChar, CharacterTransform ct) {
		char replacement = (char) (baseChar + ct.characterCode);
		// replace small letters
		text = replaceAll(text, baseChar, ct.specialCharacter, replacement);
		// replace capital letters
		if(ct.uppercase) {
			baseChar -= (char) 32;
			replacement -= (char) 32;
			text = replaceAll(text, baseChar, ct.specialCharacter, replacement);
		}
		return text;
	}
	
	/*
	 * Replaces all occurrences of the specified special and base characters in the given text.
	 */
	private static String replaceAll(String text, char baseChar, char specialCharacter, char replacement) {
		StringBuffer sb = new StringBuffer(text);
		StringBuffer result = new StringBuffer();
		while(sb.length() > 0) {
			char c = sb.charAt(0);
			if(c == specialCharacter && sb.length() > 1 && sb.charAt(1) == baseChar) {
				sb.delete(0, 2);
				result.append(replacement);
			} else {
				sb.deleteCharAt(0);
				result.append(c);
			}
		}
		return result.toString();
	}
	
	public static void printSupportedCommands() {
		for(int i = 0; i < SPECIAL_CHARACTERS.length; i++) {
			char baseChar = SPECIAL_CHARACTERS[i].baseChar;
			System.out.println("Base character: " + baseChar);
			for(int t = 0; t < SPECIAL_CHARACTERS[i].transforms.length; t++) {
				CharacterTransform ct = SPECIAL_CHARACTERS[i].transforms[t];
				// print small letters
				System.out.print((char) (baseChar + ct.characterCode));
				System.out.print(' ');
				// print capital letters
				if(ct.uppercase) {
					System.out.print((char) (baseChar + ct.characterCode - 32));
					System.out.print(' ');
				}
			}
			System.out.print("\n\n");
		}
		System.out.println("\nReplacements:");
		for(int i = 0; i < CHARACTER_REPLACEMENTS.length; i++) {
			System.out.print(CHARACTER_REPLACEMENTS[i].replacement);
			System.out.print(' ');
			if(i == 14 || i == 39)
				System.out.print('\n');
		}
	}
	
	private static void log(String text) {
//		System.out.println(text);
	}
}

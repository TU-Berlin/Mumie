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

package net.mumie.mathletfactory.util.xml;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class can be used to encode and decode base64 characters.
 * 
 * @author gronau
 */
public class Base64Codec {

	/** Field containing all base64 characters. */
	public final static byte[] ENCODING_CHARACTERS = {
		'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
		'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
		'0','1','2','3','4','5','6','7','8','9',
		'+',
		'/'
	};
	
	/** Field containing all encodable characters. */
	public final static byte[] DECODING_CHARACTERS = new byte[0x100];
	static {
		// initialize all entries to <code>-1</code>
		for(int i = 0; i < DECODING_CHARACTERS.length; i++) {
			DECODING_CHARACTERS[i] = -1;
		}
		for (byte i=0; i < ENCODING_CHARACTERS.length; i++){
			DECODING_CHARACTERS[ENCODING_CHARACTERS[i]] = i;
		}
		DECODING_CHARACTERS[' '] = -2;
		DECODING_CHARACTERS['\n'] = -2;
		DECODING_CHARACTERS['\r'] = -2;
		DECODING_CHARACTERS['\t'] = -2;
		DECODING_CHARACTERS['\f'] = -2;
		DECODING_CHARACTERS['='] = -3;
	}
	
	/** Encodes the given bytes to base64. */
	public static byte[] encode(byte[] input) {
		byte[] result = null;
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(input);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			encode(in, out);
			result = out.toByteArray();
			in = null;
			out.close();
			out = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/** Decodes the given base64 bytes. */
	public static byte[] decode(byte[] input) {
		byte[] result = null;
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(input);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			decode(in, out);
			result = out.toByteArray();
			in = null;
			out.close();
			out = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/** Encodes the characters in the given input stream to base64 characters to the given output stream. */
	public static void encode(InputStream in, OutputStream out) throws IOException {
		int[] inputChars = new int[3];
		int charCounter = 0;
		boolean endOfStream = false;
		
		// read 1st byte
		while (!endOfStream && ((inputChars[0] = in.read()) != -1)) {
			// read 2nd byte
			inputChars[1] = in.read();
			// read 3th byte
			inputChars[2] = in.read();
			
			// read left six bits of 1st byte
			out.write(encode(inputChars[0] >> 2));
			// check if 1st byte was last byte in stream
			if(inputChars[1] != -1) {
				out.write(encode( ((inputChars[0] << 4 ) & 0x30) | (inputChars[1] >> 4) ));
				// check if 2nd byte was last byte in stream
				if(inputChars[2] != -1) {
					out.write(encode( ((inputChars[1] << 2) & 0x3c) | (inputChars[2] >> 6) ));
					out.write(encode( inputChars[2] & 0x3F ));
				} else { // 3rd byte is missing
					out.write(encode( (inputChars[1] << 2) & 0x3c ));
					out.write('=');
					endOfStream = true;
				}
			} else { // 2nd byte is missing
				out.write(encode( (( inputChars[0] << 4 ) & 0x30) ));
				out.write('=');
				out.write('=');
				endOfStream = true;
			}
			// insert line break
			charCounter += 4;
			if (charCounter >= 76){
				out.write('\n');
				charCounter = 0;
			}
		} // end while
		// write buffered data
		out.flush();
	}
	
	/** Decodes the base64 characters in the given input stream to the given output stream. */
	public static void decode(InputStream in, OutputStream out) throws IOException {
		int[] inputChars = new int[4];
		boolean endOfStream = false;
		
		// read 1st and 2nd byte
		while (!endOfStream && (inputChars[0] = getNextCharacter(in)) != -1
				&& (inputChars[1] = getNextCharacter(in)) != -1) {
			// read 3rd byte
			inputChars[2] = getNextCharacter(in);
			// read 4th byte
			inputChars[3] = getNextCharacter(in);
				
				out.write(inputChars[0] << 2 | inputChars[1] >> 4);
				// check if 2nd byte was last byte in stream
				if (inputChars[2] != -1) {
					out.write(inputChars[1] << 4 | inputChars[2] >> 2);
					// check if 3rd byte was last byte in stream
					if (inputChars[3] != -1) {
						out.write(inputChars[2] << 6 | inputChars[3]);
					} else { // 4th byte is missing
						endOfStream = true;
					}
				} else { // 3rd byte is missing
					endOfStream = true;
				}
			}
			out.flush();
	}
	
	/**
	 * Returns the next available base64 character from the stream or <code>-1</code>
	 * if the end of the stream is reached.
	 */
	private static int getNextCharacter(InputStream in) throws IOException {
		int character;
		int padding = 0;
		do {
			character = in.read();
			if (character == -1)
				return -1;
			character = decode(character);
			if(padding > 0 && character > -1)
				throw new IllegalArgumentException("Not a valid base64 character: " + character);
			if (character == -3) {
				padding++;
			}
		} while (character <= -1);
		return character;

	}
	
	/** Encodes a single byte to a base64 character. */
	public static byte encode(int character) {
		return ENCODING_CHARACTERS[character];
	}
	
	/** Decodes a single base64 byte to a character. */
	public static byte decode(int character) {
		return DECODING_CHARACTERS[character];
	}
}

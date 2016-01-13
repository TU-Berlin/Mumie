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

package net.mumie.mathletfactory.util.property;

public class PropertyChangeException extends RuntimeException {

	public final static int VALUE_CHANGE_ATTEMPT = 0;
	
	public final static int PROPERTY_REMOVE_ATTEMPT = 1;
	
	PropertyChangeException(Property property, int attemptType) {
		super(getMessage(attemptType) + "\"" + property.getName() + "\" !");
	}
	
	private static String getMessage(int attemptType) {
		switch(attemptType) {
		case VALUE_CHANGE_ATTEMPT:
			return "Cannot change the value of the read-only property ";
		case PROPERTY_REMOVE_ATTEMPT:
			return "Cannot remove the read-only property ";
		default:
			return "Cannot change the read-only property ";
		}
	}
}

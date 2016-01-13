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

package net.mumie.mathletfactory.util.extension;

public class UnsupportedExtensionException extends ExtensionException {

	public final static int UNKNOWN_ERROR = -1;
	
	public final static int INVALID_CLASS_REFERENCE_ERROR = 0;
	
	public final static int NEWER_JVM_VERSION_NEEDED_ERROR = 1;
	
	private final int m_causeType;
	
	public UnsupportedExtensionException(int causeType) {
		super();
		m_causeType = causeType;
	}
	
	public UnsupportedExtensionException(Throwable cause) {
		this(cause, null);
	}
	
	public UnsupportedExtensionException(Throwable cause, String extensionName) {
		super(cause, extensionName);
		m_causeType = getCauseType(cause);
	}
	
	public int getCauseType() {
		return m_causeType;
	}
	
	public int getCauseType(Throwable cause) {
		if(cause instanceof NoClassDefFoundError)
			return INVALID_CLASS_REFERENCE_ERROR;
		if(cause instanceof UnsupportedClassVersionError)
			return NEWER_JVM_VERSION_NEEDED_ERROR;
		return UNKNOWN_ERROR;
	}
	
	public String getLocalizedMessage() {
		return getMessage();
	}
	
	public String getMessage() {
		String extensionName = getExtensionName();
		if(extensionName == null)
			extensionName = "<unknown>";
		switch(getCauseType()) {
		case INVALID_CLASS_REFERENCE_ERROR:
			return "Extension \"" + extensionName + "\" contains an invalid class reference";
		case NEWER_JVM_VERSION_NEEDED_ERROR:
			return "Extension \"" + extensionName + "\" needs a newer Java VM version";
		default: // also error "UNKNOWN_ERROR"
			return "Extension \"" + extensionName + "\" produced an unknown error";
		}
	}
}

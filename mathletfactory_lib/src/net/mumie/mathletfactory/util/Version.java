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

package net.mumie.mathletfactory.util;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to parse and compare version strings, e.g. JVM, CVS or custom versions.
 * 
 * @author gronau
 * @mm.docstatus finished
 */public class Version {

	public final static int JVM_VERSION_TYPE = 0;
	
	public final static int CVS_VERSION_TYPE = 1;
	
	public final static int SVN_VERSION_TYPE = 2;
	
	public final static int CUSTOM_VERSION_TYPE = 10;
		
	private int[] m_mainVersionDigits;
	private int m_subVersion = -1;
	private int m_builtVersion = -1;
  private String m_versionSupplement = null;
	
	public Version(String versionString) {
		parseVersion(versionString);
	}
	
	public Version(int type, String version) {
		parseVersion(version);
	}
	
	protected void parseVersion(String versionString) {
    if(versionString == null)
      throw new NullPointerException("Version must not be null");
    // group for main version (e.g. "1.6.0"): one or more digits followed by a dot
    String pattern1 = "([\\d\\.]+)";
    // group for sub version (e.g. "-01" or "_100"), once or not at all
    String pattern2 = "([-|_](\\d+))?";
    // group for build version (e.g. "-b01" or "_b100"), once or not at all
    String pattern3 = "([-|_]b(\\d+))?";
    // group for version supplement (e.g. "-oem" or "_rc"), once or not at all
    String pattern4 = "([-|_]([a-zA-Z]+))?";
    Pattern pattern = Pattern.compile(pattern1 + pattern2 + pattern3 + pattern4);
    Matcher matcher = pattern.matcher(versionString);
    if(matcher.find()) {
      parseMainVersion(matcher.group(1));
      m_subVersion = (matcher.group(2) == null ? -1 : Integer.parseInt(matcher.group(3)));
      m_builtVersion = (matcher.group(4) == null ? -1 : Integer.parseInt(matcher.group(5)));
      m_versionSupplement = (matcher.group(6) == null ? null : matcher.group(7));
    } else
      throw new IllegalArgumentException("Version cannot be parsed: " + versionString);
	}
	
	protected void parseMainVersion(String versionString) {
		StringTokenizer st = new StringTokenizer(versionString, ".");
		int nrOfDigits = st.countTokens();
		m_mainVersionDigits = new int[nrOfDigits];
		for(int i = 1; i <= nrOfDigits; i++) {
			int pos = 2 * i - 2;
			String numberString = st.nextToken();
			if(numberString.equals("*"))
				m_mainVersionDigits[i-1] = -1;
			else
				m_mainVersionDigits[i-1] = Integer.parseInt(numberString);
		}
	}
	
	/**
	 * Returns if <code>this</code> Version instance is bigger than the given <code>otherVersion</code>.
	 */
	public boolean bigger(Version otherVersion) {
		return this.isMainVersionBigger(otherVersion);
	}
	
	/**
	 * Synonym for {@link #bigger(Version)}.
	 */
	public boolean newer(Version otherVersion) {
		return this.bigger(otherVersion);
	}
	
	/**
	 * Returns if <code>this</code> Version instance is smaller than the given <code>otherVersion</code>.
	 */
	public boolean smaller(Version otherVersion) {
		return this.isMainVersionSmaller(otherVersion);
	}
	
	/**
	 * Synonym for {@link #smaller(Version)}.
	 */
	public boolean older(Version otherVersion) {
		return this.smaller(otherVersion);
	}
	
	/**
	 * Returns if <code>this</code> Version instance is inside the given Versions <code>min</code> and <code>max</code>.
	 */
	public boolean inside(Version min, Version max) {
		return (this.bigger(min) && this.smaller(max)) || this.equals(min) || this.equals(max);
	}
	
	/**
	 * Returns if <code>this</code> Version instance is included in the given Version array (list) of Versions.
	 * To perform the equality check, the method {@link #equals(Object) Version.equals(Object)} is used.
	 */
	public boolean included(Version[] list) {
		for(int i = 0; i < list.length; i++) {
			if(this.equals(list[i]))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns if <code>this</code> Version instance is equal to the given Version <code>o</code>.
	 */
	public boolean equals(Object o) {
		if(o instanceof Version)
			return this.isMainVersionEqual((Version) o);
		else
			return false;
	}
	
	/**
	 * Returns if <code>this</code> Version instance contains information about the built version.
	 */
	public boolean hasBuiltVersion() {
		return m_builtVersion != -1;
	}
	
	/**
	 * Returns if <code>this</code> Version instance contains information about the sub version.
	 */
	public boolean hasSubVersion() {
		return m_subVersion != -1;
	}
	
  /**
   * Returns if <code>this</code> Version instance contains information about the version supplement.
   */
  public boolean hasVersionSupplement() {
    return m_versionSupplement != null;
  }
  
	private boolean isMainVersionEqual(Version otherVersion) {
		int maxIndex = Math.max(this.getMainVersionDigitsCount(), otherVersion.getMainVersionDigitsCount());
		for(int i = 0; i < maxIndex; i++) {
			int thisDigit = this.getMainVersionDigit(i);
			int otherDigit = otherVersion.getMainVersionDigit(i);
			if(thisDigit == -1 || otherDigit == -1)
				continue;
			if(thisDigit != otherDigit)
				return false;
		}
		return true;
	}
	
	private boolean isMainVersionBigger(Version otherVersion) {
		int maxIndex = Math.max(this.getMainVersionDigitsCount(), otherVersion.getMainVersionDigitsCount());
		for(int i = 0; i < maxIndex; i++) {
			int thisDigit = this.getMainVersionDigit(i);
			int otherDigit = otherVersion.getMainVersionDigit(i);
			if(thisDigit == -1 || otherDigit == -1)
				return true;
			if(thisDigit > otherDigit)
				return true;
			if(thisDigit < otherDigit)
				return false;
		}
		return false;
	}
	
	private boolean isMainVersionSmaller(Version otherVersion) {
		int maxIndex = Math.max(this.getMainVersionDigitsCount(), otherVersion.getMainVersionDigitsCount());
		for(int i = 0; i < maxIndex; i++) {
			int thisDigit = this.getMainVersionDigit(i);
			int otherDigit = otherVersion.getMainVersionDigit(i);
			if(thisDigit == -1 || otherDigit == -1)
				return true;
			if(thisDigit < otherDigit)
				return true;
			if(thisDigit > otherDigit)
				return false;
		}
		return false;
	}
	
	/**
	 * Returns the number if digits of <code>this</code> Version instance.
	 */
	public int getMainVersionDigitsCount() {
		return m_mainVersionDigits.length;
	}
	
	/**
	 * Returns the indexed digit of <code>this</code> Version instance.
	 */
	public int getMainVersionDigit(int index) {
		if(index < m_mainVersionDigits.length)
			return m_mainVersionDigits[index];
		else if(m_mainVersionDigits[m_mainVersionDigits.length-1] == -1)
			return -1;
		else
			return 0;
	}
	
	/**
	 * Returns the sub version of <code>this</code> Version instance or <code>-1</code> if not set.
	 */
	public int getSubVersion() {
		return m_subVersion;
	}
	
	/**
	 * Returns the built version of <code>this</code> Version instance or <code>-1</code> if not set.
	 */
	public int getBuiltVersion() {
		return m_builtVersion;
	}
	
  /**
   * Returns the version supplement of <code>this</code> Version instance or <code>null</code> if not set.
   */
  public String getVersionSupplement() {
    return m_versionSupplement;
  }
  
	private String getLeadingZero(int number, int nrOfLeadingZeros) {
		String result = number + "";
		while(result.length() < nrOfLeadingZeros) {
			result = "0" + result;
		}
		return result;
	}
	public String toString() {
		return toString(true, true);
	}
	
	public String toString(boolean showSubVersion, boolean showBuiltVersion) {
		String vstring = "";
		for(int i = 0; i < getMainVersionDigitsCount(); i++) {
			int digit = getMainVersionDigit(i);
			if(digit > -1)
				vstring += digit;
			else
				vstring += "*";
			if(i < getMainVersionDigitsCount()-1)
				vstring += ".";
		}
		if(showSubVersion && hasSubVersion()) {
			vstring += "_" + getLeadingZero(getSubVersion(), 2);
		}
		if(showBuiltVersion && hasBuiltVersion()) {
			vstring += "-b" + getLeadingZero(getBuiltVersion(), 2);
		}
    if(hasVersionSupplement())
      vstring += "-" + getVersionSupplement();
		return vstring;
	}
}

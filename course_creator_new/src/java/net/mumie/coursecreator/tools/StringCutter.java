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

package net.mumie.coursecreator.tools;

import java.awt.FontMetrics;

/**
 * StringCutter is a tool, which has methodes to convert strings
 * @author vrichter <a href="mailto:vrichter@math.tu-berlin.de">Verena Richter</a>
 *
 */
public class StringCutter  {
	StringCutter(){}
	
	
	/**
	 * creates a new String which is truncated from origin String.<br>
	 * if s is like this <br>  
	 * any seperator arbitrary seperator string<br>
	 * then it returns an shorter string which is the longest string <br>
	 * which is shorter then size (in double) an truncated at seperator <p>
	 * 
	 * if startstring not null, than this string is foreward-spaced<br>
	 * if addSeperatorFirst is true, the seperator is inserted between startstring and truncated String
	 * 
	 * @param s origin String
	 * @param seperator
	 * @param size 
	 * @param fontMetrics the font the string use
	 * @param startString 
	 * @param addSeperatorFirst
	 * 
	 * @return 
	 */
	public static String cut(String s, String seperator, double size, FontMetrics fontMetrics,String startString,boolean addSeperatorFirst){
		
		String origin = s;
		if (s==null) return s;
		if (s.equals("")) return s;
		if (addSeperatorFirst) size = size - fontMetrics.stringWidth(seperator); 
		
		while (fontMetrics.stringWidth(s+startString)>size && s.contains(seperator)){
			s=s.substring(s.indexOf(seperator)+1, s.length());
		}
		if (s.equals(origin)) return s;
		if (addSeperatorFirst) s=seperator +s;
		s=startString+s;

		return s;
		
	}

	/**
	 * if a String starts with spaces or newlines this truncates them
	 * @param s
	 * @return
	 */
	public static String cutSpaces(String s){
		String res = s;
		
		while ((res.startsWith(" ")||(res.startsWith("\n")))&& res.length()>0) {
			res=res.substring(1);
		}
		return res;
	}
	
	
	
	
	/**
	 * cuts origin String at seperator in substrings which have less the max chars<br>
	 * replaces last seperator by cutter <br>
	 * adds start and end String
	 * @param origin
	 * @param separator
	 * @param max
	 * @param cutter
	 * @param start
	 * @param end
	 * @return
	 */public static String cutEquidistant(String origin, String separator,int max,String cutter,String start, String end){
		String s1 = new String();
		if (origin==null) return null;
		if (origin.length() < max) return origin;
		
		String trnc = origin;
		while (trnc.length()+start.length()>max){
			String part = trnc.substring(0, Math.min(max,trnc.length()));
			if (part.substring(1,part.length()).contains(separator))
				part = part.substring(0, part.lastIndexOf(separator));
			
			trnc = trnc.substring(part.length(),trnc.length());
			s1 = s1.concat(part).concat(cutter);
		}
		return start+s1.concat(trnc)+end;
	} 
	
}

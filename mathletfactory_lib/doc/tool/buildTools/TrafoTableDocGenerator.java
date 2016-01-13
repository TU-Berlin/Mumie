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

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * @author gronau
 */
public class TrafoTableDocGenerator extends Task {
	
	class SortingHelper {
		private Hashtable m_hash = new Hashtable();
		private String m_lastEntry;
		private int m_level;
		
		private SortingHelper(int level) {
			m_level = level;
		}
		
		void addEntry(String[] entries) {
			if(entries.length == 1) {
				m_lastEntry = entries[0];
				return;
			}
			if(m_hash.containsKey(entries[0])) {
				SortingHelper h = (SortingHelper) m_hash.get(entries[0]);
				h.addEntry(getSmallerArray(entries));
				
			} else {
				SortingHelper h = new SortingHelper(m_level+1);
				h.addEntry(getSmallerArray(entries));
				m_hash.put(entries[0], h);
			}
		}
		
		int getRowCount() {
			int rowCount = 0;
			Object[] names = m_hash.keySet().toArray();
			if(names.length == 0) {
				return 1;
			}
			for(int i = 0; i < names.length; i++) {
				SortingHelper h = (SortingHelper) m_hash.get(names[i]);
				rowCount += h.getRowCount();
			}
			return rowCount;
		}
		
		String getHTMLContent() {
			String s = new String();
			Object[] names = m_hash.keySet().toArray();
			if(names.length == 0) {
				return "<td><a href=\"" + PATH_TO_APIDOC + File.separator + getPathFromPackage(m_lastEntry) + "\">" + m_lastEntry + "</a></td></tr>";
			}
			Arrays.sort(names);
			for(int i = 0; i < names.length; i++) {
				SortingHelper h = (SortingHelper) m_hash.get(names[i]);
				if(m_level == 0) {
					s += "<tr border=\"0\"><td colspan=\"4\">&nbsp;</td></tr>";
					s += "\n<tr><td colspan=\"4\"><b><a href=\"" + PATH_TO_APIDOC + File.separator + getPathFromPackage(names[i].toString()) + "\">" + names[i].toString() + "</a></b></td></tr>";
					s += "\n<tr>";
				}
				else {
					int rowSpan = h.getRowCount(); 
					String name = names[i].toString().length() > 35 ? "<font size=\"-1\">" + names[i].toString() + "</font>" : names[i].toString();
					s += "<td rowspan=\"" + rowSpan + "\" valign=\"top\">" + name + "</td>";
				}
				s += h.getHTMLContent();
			}
			return s;
		}
		
		String getPathFromPackage(String s) {
			String r = s.replace('.', '/');
			r += ".html";
			return r;
		}
		
		String[] getSmallerArray(String[] a) {
			String[] b = new String[a.length-1];
			System.arraycopy(a, 1, b, 0, b.length);
			return b;
		}
	}
	
	public final static String PATH_TO_APIDOC = "apidoc";
	
	private String WORKING_DIR;
	private String DOC_DIR;
	private SortingHelper m_helper = new SortingHelper(0);

  public void execute() throws BuildException {
  	String html = "<html>\n<head><title>Transformer Properties</title></head>\n<body>\n<center><h1>Transformer Properties</h1><center>\n<table border=\"0\" cellspacing=\"20\">";
  	html += m_helper.getHTMLContent();
  	html += "</table>\n</body>\n</html>";
  	try {
	  	File outFile = new File(WORKING_DIR + File.separator + DOC_DIR + File.separator + "TransformerProperties.html");
//	  	File path = new File(WORKING_DIR + File.separator + DOC_DIR);
////	  	log(path.toString());
//			if (!path.exists()) {
//				path.mkdirs();
//			}
			BufferedWriter out =
				new BufferedWriter(
					new FileWriter(outFile));
			out.write(html);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
  
  public void setTrafoFiles(String files) {
		WORKING_DIR = getProject().getBaseDir().getAbsolutePath();
		DOC_DIR = getProject().getProperty("doc.out.dir");
		log(DOC_DIR);
		Properties props = new Properties();
		String[] filesA = files.split(",");
		for(int f = 0; f < filesA.length; f++) {
			try {
	//			log(WORKING_DIR + File.separator + file);
				File trafoFile = new File(WORKING_DIR + File.separator + filesA[f].trim());
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(trafoFile));
				props.load(bis);
				Enumeration keys = props.keys();
				while(keys.hasMoreElements()) {
					String key = (String) keys.nextElement();
					int first = key.indexOf("#");
					int second = key.indexOf("#", first+1);
					String[] propArray = new String[4];
					propArray[0] = key.substring(second+1, key.length());
					propArray[1] = key.substring(first+1, second);
					propArray[2] = key.substring(0, first);
					propArray[3] = props.getProperty(key);
					m_helper.addEntry(propArray);
				}
			} catch (IOException e) {
				log(e.getLocalizedMessage());
			}
		}
  }
}

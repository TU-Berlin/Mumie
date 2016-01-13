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
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * @author Markus Gronau
 * 
 * This class is used to create HTML-pages including the sample applets of the
 * AppletFactory.
 *  
 */
public class SamplePageTool extends Task{

	private String PROPERTY_FILE = "settings.properties";
	
	private String WORKING_DIR;

	private Properties m_props = new Properties();

	private ArrayList m_applets = new ArrayList();
	private ArrayList m_dirs = new ArrayList();
	private JavaTreeNode m_tree;
	private Project m_project;


	private static boolean m_verboseMode = true;
	
	  public void init()
	    throws BuildException
	  {
	    m_project = getProject();
	  }

	  public void execute() throws BuildException
	  {
/*	    if (m_destDir == null)
	      throw new BuildException("destdir attribute must be set!");*/
	  }

	public SamplePageTool() {
		WORKING_DIR = getClass().getResource("").getPath();
		
		m_props.put("ROOT_PACKAGE", "net.mumie.mathletfactory");
		m_props.put("INPUT_FILE", "");
		String url = getClass().getResource("").getPath();
		m_props.put("OUTPUT_DIR", "");
		m_props.put("JARS_SOURCE_DIR", "");

		m_props.put("IMG_DIR", "images");
		m_props.put("JARS_DIR", "jars");
		m_props.put("ARCHIVE_FILES", "appfac-all.jar,layout.zip");

		m_props.put("APPLET_WIDTH_HEIGHT", "120,30");
		m_props.put("SEPARATE_WINDOW", "true");

		m_props.put("FILE_IMG", "file.gif");
		m_props.put("DIR_IMG", "dir.gif");
		m_props.put("NEXT_IMG", "next.gif");
		m_props.put("PREV_IMG", "prev.gif");
		m_props.put("UP_IMG", "up.gif");

		m_props.put("verbose", "true");

		readProps();

		System.out.println(
				"Working directory is " + WORKING_DIR);
		System.out.println(
			"Output directory is " + m_props.getProperty("OUTPUT_DIR"));
		System.out.println(
			"Root package is " + m_props.getProperty("ROOT_PACKAGE"));
		System.out.println();
		try {
			// reading applets...
			BufferedReader in =
				new BufferedReader(
					new FileReader(m_props.getProperty("INPUT_FILE")));
			String line = null;
			while (true) {
				line = in.readLine();
				if (line != null)
					addApplet(line);
				else
					break;
			}
			in.close();
			//			m_applets.add()

			// constructing the tree..
			m_tree = new JavaTreeNode();
			for (int i = 0; i < m_applets.size(); i++) {
				JavaTreeNode lastNode = m_tree;
				StringTokenizer tok =
					new StringTokenizer((String) m_applets.get(i), "/");
				while (tok.hasMoreTokens()) {
					lastNode = lastNode.addChild(tok.nextToken());
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage() + "\nProgram exited.\n");
			System.exit(2);
		}
	}

	private void copyImages() {
		String inImgDirLoc =
			WORKING_DIR
				+ File.separator
				+ m_props.getProperty("IMG_DIR")
				+ File.separator;
		String outImgDirLoc =
			m_props.getProperty("OUTPUT_DIR")
				+ File.separator
				+ m_props.getProperty("IMG_DIR")
				+ File.separator;
		vprintln("Creating image directory " + outImgDirLoc);
		File outImgDirFile = new File(outImgDirLoc);
		outImgDirFile.mkdir();
		vprintln("Copying image files");
		copyFile(
			inImgDirLoc + m_props.getProperty("FILE_IMG"),
			outImgDirLoc + m_props.getProperty("FILE_IMG"));
		copyFile(
			inImgDirLoc + m_props.getProperty("DIR_IMG"),
			outImgDirLoc + m_props.getProperty("DIR_IMG"));
		copyFile(
			inImgDirLoc + m_props.getProperty("NEXT_IMG"),
			outImgDirLoc + m_props.getProperty("NEXT_IMG"));
		copyFile(
			inImgDirLoc + m_props.getProperty("PREV_IMG"),
			outImgDirLoc + m_props.getProperty("PREV_IMG"));
	}

	private void copyJars() {
		String inJarDirLoc =
//			WORKING_DIR
//				+ File.separator
				 m_props.getProperty("JARS_SOURCE_DIR")
				+ File.separator;
		String outJarDirLoc =
			m_props.getProperty("OUTPUT_DIR")
				+ File.separator
				+ m_props.getProperty("JARS_DIR")
				+ File.separator;
		vprintln("Creating jar file directory " + outJarDirLoc);
		File outJarDirFile = new File(outJarDirLoc);
		outJarDirFile.mkdir();
		StringTokenizer tok =
			new StringTokenizer(m_props.getProperty("ARCHIVE_FILES"), ",");
		while (tok.hasMoreTokens()) {
			String archiv = tok.nextToken();
			vprintln("Copying " + archiv);
			copyFile(inJarDirLoc + archiv, outJarDirLoc + archiv);
		}
	}


	private void writeFiles() {
		for (int i = 0; i < m_applets.size(); i++) {
			writeFile((String) m_applets.get(i));
		}
		for (int i = 0; i < m_dirs.size(); i++) {
			writeFile((String) m_dirs.get(i));
		}

	}

	private void writeFile(String applet) {
		try {
			JavaTreeNode target = m_tree.getNode(applet);
			HTMLContentCreator creator = new HTMLContentCreator(target);
			File f =
				new File(
					m_props.getProperty("OUTPUT_DIR")
						+ File.separator
						+ creator.getHTMLFileLocation());

			if (!f.exists()) {
				vprintln(
					"\tCreating directory " + creator.getHTMLFileLocation());
				f.mkdirs();
			}

			vprintln("\tCreating file " + creator.getHTMLFileName());
			BufferedWriter out =
				new BufferedWriter(
					new FileWriter(
						m_props.getProperty("OUTPUT_DIR")
							+ File.separator
							+ creator.getHTMLFileName()));
			out.write(creator.getHTMLContent());
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addApplet(String line) {
		if (line.indexOf("$") != -1
			|| line.length() < m_props.getProperty("ROOT_PACKAGE").length())
			// || line.indexOf(".class") ==
			// -1
			return;
		if (line.indexOf(".class") != -1)
			line =
				line.substring(
					m_props.getProperty("ROOT_PACKAGE").length() + 1,
					line.indexOf(".class"));
		else {
			//			return;
			try {
				line =
					line.substring(
						m_props.getProperty("ROOT_PACKAGE").length() + 1,
						line.length() - 1);
				m_dirs.add(line);
				return;
			} catch (IndexOutOfBoundsException e) {
				System.err.println(line);
				return;
			}
		}
		m_applets.add(line);
	}

	private void readProps() {
		try {
			File propsFile =
				new File(
					WORKING_DIR
						+ File.separator
						+ PROPERTY_FILE);
			if (!propsFile.exists()) {
				//propsFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(propsFile);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				m_props.store(bos, "Properties for SamplePageTool");
				bos.close();
				System.out.println(
					"Property file " + propsFile.getAbsoluteFile() +" not found!\nA new one with default settings has been created.\nProgram exited.\n");
				System.exit(1);
				return;
			}
			FileInputStream fis = new FileInputStream(propsFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			m_props.load(bis);
			bis.close();
			if (m_props.getProperty("verbose").equals("true"))
				m_verboseMode = true;
			else
				m_verboseMode = false;
		} catch (IOException ioex) {
			System.err.println(ioex.getMessage());
		}
	}


	public static void main(String[] args) {
		SamplePageTool spc = new SamplePageTool();
		spc.writeFiles();
		spc.copyJars();
		spc.copyImages();
		System.out.println("Finished");
	}

	//***************************************
	//          Helper class
	//***************************************
	class JavaTreeNode {

		private JavaTreeNode m_parent;
		private String m_label;
		private ArrayList m_children = new ArrayList();
		private int m_depth = 0; // 0: root, 1: first
													// element

		/**
		 * Creates a root node.
		 */
		JavaTreeNode() {
			m_label = "ROOT";
			m_parent = this;
		}

		JavaTreeNode(JavaTreeNode parent, String label) {
			m_parent = parent;
			m_label = label;
			m_depth = parent.getDepth() + 1;
		}

		JavaTreeNode addChild(String label) {
			for (int i = 0; i < m_children.size(); i++) {
				if (((JavaTreeNode) m_children.get(i))
					.getLabel()
					.equals(label)) {
					return (JavaTreeNode) m_children.get(i);
				}
			}
			JavaTreeNode child = new JavaTreeNode(this, label);
			m_children.add(child);
			return child;
		}

		JavaTreeNode getChild(String label) {
			for (int i = 0; i < m_children.size(); i++) {
				if (((JavaTreeNode) m_children.get(i))
					.getLabel()
					.equals(label))
					return (JavaTreeNode) m_children.get(i);
			}
			return null;
		}

		/**
		 * Should only be called on an tree root.
		 */
		JavaTreeNode getNode(String name) {
			StringTokenizer tok = new StringTokenizer(name, "/");
			JavaTreeNode curr = this;
			while (tok.hasMoreTokens()) {
				if (curr == null) {
					break;
				}
				curr = curr.getChild(tok.nextToken());
			}
			if (curr == null) {
				System.err.println("Resource \"" + name + "\" not found!");
				return null;
			}
			return curr;
		}

		String getAsHTMLContent(JavaTreeNode target) {
			String res = "<li>";
			if (!this.getName().equals(target.getName())) {
				res += "<a href=\""
					+ target.getRelativePathTo(this)
					+ this.getFileName()
					+ "\">"
					+ this.getLabel()
					+ "</a>";
			} else
				res += this.getLabel();

			if (this.hasChildren()
				&& !this.getName().equals(target.getName())) {
				res += "\n<ul>\n";
				for (int i = 0; i < m_children.size(); i++) {
					JavaTreeNode child = (JavaTreeNode) m_children.get(i);
					if (target.getName().indexOf(child.getName()) == 0) {
						res += child.getAsHTMLContent(target);
					} else {
						res += "<li><a href=\""
							+ target.getRelativePathTo(child)
							+ child.getFileName()
							+ "\">"
							+ child.getLabel()
							+ "</a></li>\n";
					}
				}
				res += "</ul>\n";
			}
			res += "</li>\n";
			return res;
		}

		String getRelativePathTo(JavaTreeNode target) {
			String res = "";
			String[] thisName = getPathNameAsArray();
			String[] targetName = target.getPathNameAsArray();

			int i = 0;
			while (i < thisName.length && i < targetName.length) {
				if (!thisName[i].equals(targetName[i])) {
					break;
				}
				i++;
			}
			for (int k = 0; k < thisName.length - i; k++)
				res += "../";
			for (int j = i; j < targetName.length; j++)
				res += targetName[j] + "/";

			return res;
		}

		String getRelativePathPlusNameTo(JavaTreeNode target) {
			return getRelativePathTo(target) + target.getFileName();
		}

		JavaTreeNode getParent() {
			return m_parent;
		}

		int getDepth() {
			return m_depth;
		}

		boolean hasChildren() {
			return (m_children.size() == 0) ? false : true;
		}

		String getLabel() {
			return m_label;
		}

		String getName() {
			if (getDepth() <= 1)
				return m_label;
			else
				return m_parent.getName() + "." + m_label;
		}

		String getJavaName() {
			return m_props.getProperty("ROOT_PACKAGE") + "." + getName();
		}

		String[] getPathNameAsArray() {
			StringTokenizer tok = new StringTokenizer(getName(), ".");
			String[] array =
				new String[tok.countTokens() - (hasChildren() ? 0 : 1)];
			int i = 0;
			while (tok.hasMoreTokens()) {
				try {
					array[i] = tok.nextToken();
				} catch (IndexOutOfBoundsException e) {
					return array;
				}
				i++;
			}
			return array;
		}

		String getFileName() {
			if (hasChildren())
				return "index.html";
			else
				return m_label + ".html";
		}

		public String toString() {
			return "TreeNode for " + m_label;
		}

		//		String getFilePath() {
		//			if(getDepth() <= 1)
		//				return "";
		//			else
		//				return m_parent.getFilePath() + File.separator +
		//		}
	}

	class HTMLContentCreator {
		private String m_content = new String();
		private JavaTreeNode m_currentNode;
		private String m_htmlFileLoc = new String();
		private String m_imgDirLoc;
		private String m_jarsDirLoc;

		private String m_nextLink, m_prevLink;

		HTMLContentCreator(JavaTreeNode gotoNode) {
			m_currentNode = gotoNode;
			m_imgDirLoc = "";
			m_jarsDirLoc = "";
			for (int i = 0; i < m_currentNode.getDepth()- (m_currentNode.hasChildren() ? 0 : 1);i++) {
				m_imgDirLoc += "../";
				m_jarsDirLoc += "../";
			}
			m_jarsDirLoc += m_props.getProperty("JARS_DIR");
			m_imgDirLoc += m_props.getProperty("IMG_DIR") + "/";
			calcPrevNext();
		}

		void echo(String s) {
			m_content += s + "\n";
		}

		String getHTMLContent() {
			writeHead();
			writeLeftNavBar();
			writePackageContent();
			writeAppletContext();
			writeFoot();
			return m_content;
		}

		void writeHead() {
			echo(
				"<html>\n<head>\n<title>"
					+ ((m_currentNode.hasChildren()) ? "Package " : "Applet ")
					+ m_currentNode.getLabel()
					+ "</title>\n");
			echo(
				"<style type=\"text/css\">\n"
					+ "<!--\n"
					+ "a:link { text-decoration: none; color: blue}\n"
					+ "a:visited { text-decoration: none; color: #8000FF }\n"
					+ "a:active { text-decoration: none; color: #FF0000 }\n"
					+ "a:hover {  text-decoration:underline; color: #FF0000 }\n"
					+ "-->\n</style>\n\n</head>\n");
			echo("<body bgcolor=\"#FAFAFA\" topmargin=0 leftmargin=0>");
			echo("<table border=0 width=\"100%\" height=\"85%\" cellspacing=0 cellpadding=0>");
			echo("<tr><td width=\"40%\"height=\"100\" valign=\"top\">");
			echo(
				"<center>\n"
					+ "<font color=\"#CC3333\" size=\"+3\">AppletFactory</font><br>\n"
					+ "<font size=\"+1\">Development Applets</font><hr noshade width=150>\n"
					+ "</center>\n");
		}

		void writeLeftNavBar() {
			echo("<div style=\"background-color:#F0F0F0; overflow:auto; padding-top:10px; padding-left:10px; border:1px solid black\">");
			echo(m_tree.getNode("sample").getAsHTMLContent(m_currentNode));
			echo("</div></td><td valign=\"top\"><br><center><br>");

		}

		void writePackageContent() {
			echo(
				"<h3>"
					+ (m_currentNode.hasChildren() ? "Package " : "Applet ")
					+ "<i>"
					+ m_currentNode.getLabel()
					+ "</i></h3>");
			if (m_currentNode.hasChildren()) {
				echo("<table border=0>\n<tr>\n<td>\n");
				echo("<ul>\n");
				for (int i = 0; i < m_currentNode.m_children.size(); i++) {
					JavaTreeNode curr =
						(JavaTreeNode) m_currentNode.m_children.get(i);
					echo(
						"<li><a href=\""
							+ (curr.hasChildren()
								? curr.getLabel() + "/" + curr.getFileName()
								: curr.getFileName())
							+ "\"><img border=\"0\" align=\"middle\" src=\""
							+ m_imgDirLoc
							+ (curr.hasChildren()
								? m_props.getProperty("DIR_IMG")
								: m_props.getProperty("FILE_IMG"))
							+ "\"> "
							+ curr.getLabel()
							+ "</a></li>\n");
				}
				echo("</ul>\n");
				echo("</td>\n</tr>\n</table>");

			}
		}

		void writeAppletContext() {
			if (!m_currentNode.hasChildren()) {
				StringTokenizer tok =
					new StringTokenizer(
						m_props.getProperty("APPLET_WIDTH_HEIGHT"),
						",");
				echo(
					"<applet code=\""
						+ m_currentNode.getJavaName()
						+ ".class\" codebase=\""
						+ m_jarsDirLoc
						+ "\" archive=\""
						+ m_props.getProperty("ARCHIVE_FILES")
						+ "\""
						+ " width="
						+ tok.nextToken()
						+ " height="
						+ tok.nextToken()
						+ ">\n"
						+ (m_props.getProperty("SEPARATE_WINDOW").equals("true")
							? "<param name=\"separateWindow\" value=\"true\">"
							: "")
						+ "\n</applet>");
			}
		}

		void writeFoot() {
			echo("</td>\n</tr>\n</table>\n<center>\n<hr width=\"80%\" noshade>\n");
			echo("<table width=\"80%\">\n<tr>\n<td width=\"50\">");
			if (hasPrev())
				echo(
					"<a href=\""
						+ m_prevLink
						+ "\"><img border=0 src=\""
						+ m_imgDirLoc
						+ "prev.gif\" alt=\"N&auml;chstes Applet\"></a>");
			Calendar c = Calendar.getInstance();
			String date =
				"Version vom "
					+ c.get(Calendar.DAY_OF_MONTH)
					+ "."
					+ (c.get(Calendar.MONTH) + 1)
					+ "."
					+ c.get(Calendar.YEAR);
			//					+ ", Uhr "
			//					+ c.get(Calendar.HOUR_OF_DAY)
			//					+ ":"
			//					+ c.get(Calendar.MINUTE);
			echo("</td><td align=\"middle\">" + date);
			echo("</td>\n<td width=\"50\" align=\"right\">");
			if (hasNext())
				echo(
					"<a href=\""
						+ m_nextLink
						+ "\"><img border=0 src=\""
						+ m_imgDirLoc
						+ "next.gif\" alt=\"Vorheriges Applet\"></a>");
			echo("</td>\n</tr>\n</table>\n</center>\n</body>\n</html>\n");
		}

		String getHTMLFileName() {
			String res = new String();
			StringTokenizer tok =
				new StringTokenizer(m_currentNode.getName(), ".");
			while (tok.hasMoreTokens()) {
				String s = tok.nextToken();
				if (s.equals(m_currentNode.getLabel())) {
					if (m_currentNode.hasChildren()) {
						res += s + File.separator + "index.html";
						m_htmlFileLoc += s;
					} else
						res += s + ".html";
				} else {
					res += s + File.separator;
					m_htmlFileLoc += s + File.separator;
				}
			}
			return res;
		}

		String getHTMLFileLocation() {
			if (m_htmlFileLoc.length() == 0)
				getHTMLFileName();
			return m_htmlFileLoc;
		}

		void calcPrevNext() {
			ArrayList children = m_currentNode.getParent().m_children;
			for (int i = 0; i < children.size(); i++) {
				if ((JavaTreeNode) children.get(i) == m_currentNode) {
					if (i >= 1)
						m_prevLink =
							m_currentNode.getRelativePathPlusNameTo(
								(JavaTreeNode) children.get(i - 1));
					if (i < children.size() - 1) {
						m_nextLink =
							m_currentNode.getRelativePathPlusNameTo(
								(JavaTreeNode) children.get(i + 1));
					}
				}
			}

		}

		boolean hasNext() {
			return (m_nextLink == null ? false : true);
		}

		boolean hasPrev() {
			return (m_prevLink == null ? false : true);
		}

		public String toString() {
			return "HTMLContentCreator for TreeNode" + m_currentNode.getLabel();
		}
	}
	
	   public static void copyFile(String source, String dest)
	   {
	     FileReader fr;
	     BufferedReader br = null;
	     FileWriter fw;
	     BufferedWriter bw = null;
	     vprint("Copying file " + source + " to " + dest + " ... ");

	     try
	       {
		 fr = new FileReader(source);
		 br = new BufferedReader(fr);
		 fw = new FileWriter(dest);
		 bw = new BufferedWriter(fw);
		 int buffer = br.read();
		 while (buffer != -1)
		   {
		     bw.write(buffer);
		     buffer = br.read();
		   }
	       }
	     catch (FileNotFoundException fnfex)
	       {
		 System.err.println(fnfex.getMessage());
	       }
	     catch (IOException ioex)
	       {
		 System.err.println(ioex.getMessage());
	       }
	     finally
	       {
		 try
		   {
		     if (br != null)
		       br.close();
		     if (bw != null)
		       bw.close();
		     vprintln("ok");
		   }
		 catch (IOException ioex)
		   {
		     System.err.println("Error occured while closing streams to file!" + dest);
		   }
	       }
	   }
	   
	   static void vprintln(String s)
	   {
	     if (m_verboseMode)
	       System.out.println(s);
	   }
		
	   static void vprint(String s)
	   {
	     if (m_verboseMode)
	       System.out.print(s);
	   }

}

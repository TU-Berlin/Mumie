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

package net.mumie.cindy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.mumie.util.io.IOUtil;
import org.htmlparser.Node;
import org.htmlparser.Tag;
import org.htmlparser.lexer.InputStreamSource;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.lexer.Source;
import net.mumie.cdk.CdkHelper;

/**
 * Provides static utilities for Cinderelly visualizations.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CindyUtil.java,v 1.9 2008/09/12 14:08:33 rassy Exp $</code>
 */

public class CindyUtil
{
  /**
   * Name of the Cindy script file in the jar.
   */

  public static final String SCRIPT_NAME = "data.cdy";

  /**
   * Default description
   */

  public static final String DEFAULT_DESCRIPTION = "- insert description here -";

  /**
   * Default copyright
   */

  public static final String DEFAULT_COPYRIGHT = "- insert copyright note here -";

  /**
   * Extracts the Cinderella visualizations from the HTML page provided by the specified
   * source.
   */

  public static CindyData[] extract (Source source)
    throws Exception
  {
    CindyData cindyData = null;
    List<CindyData> cindyDataList = new ArrayList<CindyData>();
    Page page = new Page(source);
    Lexer lexer = new Lexer(page);
    Node node;
    while ( (node = lexer.nextNode()) != null )
      {
        if ( node instanceof Tag )
          {
            Tag tag = (Tag)node;
            String tagName = tag.getTagName();
            if ( tagName.equals("APPLET") )
              {
                if ( cindyData != null && tag.isEndTag() )
                  {
                    cindyDataList.add(cindyData);
                    cindyData = null;
                  }
                else if ( tag.getAttribute("code").equals("de.cinderella.CindyApplet") )
                  {
                    cindyData = new CindyData();
                    cindyData.setWidth(tag.getAttribute("width"));
                    cindyData.setHeight(tag.getAttribute("height"));
                                              }
              }
            else if ( cindyData != null && tagName.equals("PARAM") )
              {
                String name = tag.getAttribute("name");
                String value = tag.getAttribute("value");
                if ( name.equals("filename") )
                  cindyData.setScriptLocation(value);
                else
                  cindyData.setParam(name, value);
              }
          }
      }
    return cindyDataList.toArray(new CindyData[cindyDataList.size()]);
  }

  /**
   * Extracts the Cinderella visualizations from the HTML page provided by the specified
   * input stream.
   */

  public static CindyData[] extract (InputStream in)
    throws Exception
  {
    return extract(new InputStreamSource(in));
  }

  /**
   * Extracts the Cinderella visualizations from the HTML page provided by the specified
   * file.
   */

  public static CindyData[] extract (File file)
    throws Exception
  {
    return extract(new FileInputStream(file));
  }

  /**
   * Extracts the Cinderella visualizations from the HTML page provided by the specified
   * URL.
   */

  public static CindyData[] extract (URL url)
    throws Exception
  {
    InputStream in = url.openStream();
    CindyData[] result = extract(in);
    in.close();
    return result;
  }

  /**
   * Stores the data read from the specified input stream in the specified output stream as
   * a jar entry with the specified name.
   */

  public static void jar (InputStream in, OutputStream out, String entryName)
    throws Exception
  {
    JarOutputStream jar = new JarOutputStream(out);
    jar.putNextEntry(new JarEntry(entryName));
    IOUtil.redirect(in, jar, true, false);
    jar.closeEntry();
    jar.finish();
  }

  /**
   * Creates the content file of a Cinderella applet.
   */

  public static void createContentFile (InputStream in, File contentFile)
    throws Exception
  {
    jar(in, new FileOutputStream(contentFile), SCRIPT_NAME);
  }

  /**
   * Creates the content file of a Cinderella applet.
   */

  public static void createContentFile (File scriptFile, File contentFile)
    throws Exception
  {
    createContentFile(new FileInputStream(scriptFile), contentFile);
  }

  /**
   * Creates a Cinderella document content file.
   */

  public static void createContentFile (URL scriptURL, File contentFile)
    throws Exception
  {
    createContentFile(scriptURL.openStream(), contentFile);
  }

  /**
   * Creates a Cinderella document master file.
   */

  public static void createMasterFile (String name,
                                       int width,
                                       int height,
                                       String infoPagePath,
                                       File masterFile)
    throws Exception
  {
    writeFile
      (masterFile,
       "<?xml version=\"1.0\" encoding=\"ASCII\"?>",
       "<mumie:applet",
       "  xmlns:mumie=\"http://www.mumie.net/xml-namespace/document/metainfo\">",
       "  <mumie:name>" + name + "</mumie:name>",
       "  <mumie:qualified_name>de.cinderella.CindyApplet</mumie:qualified_name>",
       "  <mumie:description>",
       "    " + DEFAULT_DESCRIPTION,
       "  </mumie:description>",
       "  <mumie:info_page path=\"" + infoPagePath + "\"/>",
       "  <mumie:copyright>",
       "    " + DEFAULT_COPYRIGHT,
       "  </mumie:copyright>",
       "  <mumie:width value=\"" + width + "\"/>",
       "  <mumie:height value=\"" + height + "\"/>",
       "  <mumie:components>",
       "    <mumie:jar path=\"system/libraries/jar_cindyrun.meta.xml\"/>",
       "  </mumie:components>",
       "  <mumie:content_type type=\"application\" subtype=\"x-java-archive\"/>",
       "  <mumie:source_type type=\"text\" subtype=\"java\"/>",
       "</mumie:applet>");
  }

  /**
   * Creates the master file of the info page of a Cinderella document.
   */

  public static void createInfoPageMasterFile (String name,
                                               String appletPath,
                                               String genDocPath,
                                               File infoPageMasterFile)
    throws Exception
  {
    writeFile
      (infoPageMasterFile,
       "<?xml version=\"1.0\" encoding=\"ASCII\"?>",
       "<mumie:page xmlns:mumie=\"http://www.mumie.net/xml-namespace/document/metainfo\">",
       "  <mumie:name>" + name + "</mumie:name>",
       "  <mumie:description>",
       "    " + DEFAULT_DESCRIPTION,
       "  </mumie:description>",
       "  <mumie:copyright>",
       "    " + DEFAULT_COPYRIGHT,
       "  </mumie:copyright>",
       "  <mumie:content_type type=\"text\" subtype=\"xml\"/>",
       "  <mumie:components>",
       "    <mumie:applet lid=\"applet\" path=\"" + appletPath + "\"/>",
       "  </mumie:components>",
       "  <mumie:gdim_entries>",
       "    <mumie:gdim_entry generic_doc_path=\"" + genDocPath + "\"/>",
       "  </mumie:gdim_entries>",
       "</mumie:page>");
  }

  /**
   * Creates the content file of the info page of a Cinderella document.
   */

  public static void createInfoPageContentFile (String name,
                                                Map<String,String> params,
                                                File infoPageContentFile)
    throws Exception
  {
    PrintStream out = new PrintStream(new FileOutputStream(infoPageContentFile));
    out.println("<?xml version=\"1.0\" encoding=\"ASCII\"?>");
    out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\"");
    out.println("      xmlns:mmx=\"http://www.mumie.net/xml-namespace/xhtml-extention\">");
    out.println("  <head>");
    out.println("    <title>MUMIE: " + name + "</title>");
    out.println("    <mmx:css-stylesheet/>");
    out.println("  </head>");
    out.println("  <body>");
    out.println("    <mmx:top-bar/>");
    out.println("    <div class=\"main-indented\">");
    out.println("      <h1>Applet:" + name + "</h1>");
    out.println("      <p>");
    out.println("      </p>");
    out.println("      <mmx:applet lid=\"applet\">");
    out.println("        <param name=\"filename\" value=\"" + SCRIPT_NAME + "\"/>");
    for (Map.Entry<String,String> entry : params.entrySet())
      {
        out.println
          ("        <param name=\"" +
           entry.getKey() + "\" value=\"" +
           CdkHelper.quoteXML(entry.getValue()) + "\"/>");
      }
    out.println("      </mmx:applet>");
    out.println("    </div>");
    out.println("  </body>");
    out.println("</html>");
    out.flush();
  }

  /**
   * Creates the master file of the generic info page of a Cinderella document.
   */

  public static void createGenInfoPageMasterFile (String name,
                                                  File genInfoPageMasterFile)
    throws Exception
  {
    writeFile
      (genInfoPageMasterFile,
       "<?xml version=\"1.0\" encoding=\"ASCII\"?>",
       "<mumie:generic_page xmlns:mumie=\"http://www.mumie.net/xml-namespace/document/metainfo\">",
       "  <mumie:name>" + name + "</mumie:name>",
       "  <mumie:description>",
       "    " + DEFAULT_DESCRIPTION,
       "  </mumie:description>",
       "</mumie:generic_page>");
  }

  /**
   * 
   */

  protected static void writeFile (File file, String... lines)
    throws Exception
  {
    PrintStream out = new PrintStream(new FileOutputStream(file));
    for (String line : lines) out.println(line);
    out.flush();
    out.close();
  }

}

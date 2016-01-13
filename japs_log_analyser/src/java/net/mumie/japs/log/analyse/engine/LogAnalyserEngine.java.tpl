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

package net.mumie.japs.log.analyse.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import net.mumie.japs.log.analyse.LogAnalyserComponent;
import net.mumie.japs.log.analyse.LogOutputHandler;
import net.mumie.japs.log.analyse.LogOutputProcessingException;
import net.mumie.japs.log.analyse.LogOutputProducer;
import net.mumie.japs.log.analyse.parse.LogOutputParser;
import net.mumie.japs.log.analyse.source.FileLogOutputSource;
import net.mumie.japs.log.analyse.source.LogOutputSource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Main entry point. This class configures and runs a log analyzing process.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: LogAnalyserEngine.java.tpl,v 1.3 2007/06/10 00:43:40 rassy Exp $</code>
 */

public class LogAnalyserEngine
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The sources.
   */

  protected List sources = new ArrayList();

  /**
   * The parser of the pipeline.
   */

  protected LogOutputParser parser = null;

  /**
   * Maps component keys to component class names.
   */

  protected Map componentClassNames = new HashMap();

  /**
   * The SAX parser for parsing configuration and control files.
   */

  protected SAXParser saxParser = null;

  /**
   * Wether the engine should print status messages to stdout. Default is
   * <code>false</code>. 
   */

  protected boolean verbose = false;

  // --------------------------------------------------------------------------------
  // SAX handler classes
  // --------------------------------------------------------------------------------

  /**
   * Handles the SAX events when parsing component configuration XML files.
   */

  protected class ConfigSAXHandler extends DefaultHandler 
  {
    /**
     * Handles the start of an element.
     */

    public void startElement (String namespaceURI,
                              String localName,
                              String qualifiedName,
                              Attributes attributes)
      throws SAXException 
    {
      LogAnalyserEngine engine = LogAnalyserEngine.this;
      engine.msg("Found element: " + localName);
      if ( localName.equals("parser") ||
           localName.equals("filter") ||
           localName.equals("serializer") )
        {
          engine.defineComponent
            (localName, 
             attributes.getValue("name"),
             attributes.getValue("class"));
        }
    }
  }

  /**
   * Handles the SAX events when parsing setup XML files.
   */

  protected class SetupSAXHandler extends DefaultHandler
  {
    /**
     * The pipeline component that is currently set-up.
     */

    protected LogAnalyserComponent component = null;

    /**
     * The parameter that is currently parsed.
     */

    protected String paramName = null;

    /**
     * Collects the text of subsequent {@link #characters characters} calls.
     */

    protected StringBuffer text = null;

    /**
     * Handles the start of an element.
     */

    public void startElement (String namespaceURI,
                              String localName,
                              String qualifiedName,
                              Attributes attributes)
      throws SAXException 
    {
      try
        {
          LogAnalyserEngine engine = LogAnalyserEngine.this;
          engine.msg("Found element: " + localName);
          if ( localName.equals("file") )
            {
              this.text = new StringBuffer();
            }
          else if ( localName.equals("param") )
            {
              this.paramName = attributes.getValue("name");
              this.text = new StringBuffer();
            }
          else if ( localName.equals("parser") ||
                    localName.equals("filter") ||
                    localName.equals("serializer") )
            {
              LogAnalyserComponent nextComponent =
                engine.createComponent(localName, attributes.getValue("name"));
              if ( localName.equals("parser") )
                engine.parser = (LogOutputParser)nextComponent;
              else
                ((LogOutputProducer)this.component).setHandler((LogOutputHandler)nextComponent);
              this.component = nextComponent;
            }
        }
      catch (Exception exception)
        {
          throw new SAXException(exception);
        }
    }

    /**
     * Handles character data.
     */

    public void characters (char[] chars, int start, int length)
      throws SAXException
    {
      if ( this.text != null )
        this.text.append(new String(chars, start, length));
    }

    /**
     * Handles the end of an element.
     */

    public void endElement (String namespaceURI,
                            String localName,
                            String qualifiedName)
      throws SAXException
    {
      LogAnalyserEngine engine = LogAnalyserEngine.this;
      try
        {
          if ( localName.equals("file") )
            {
              String filename = this.text.toString().trim();
              if ( filename.length() > 0 )
                engine.addSource(new FileLogOutputSource(filename));
              this.text = null;
            }
          else if ( localName.equals("param") )
            {
              engine.setComponentParameter
                (this.component, this.paramName, this.text.toString());
              this.paramName = null;
              this.text = null;
            }
        }
      catch (Exception exception)
        {
          throw new SAXException(exception);
        }
    }
  }

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>LogAnalyserEngine</code>.
   */

  public LogAnalyserEngine ()
    throws LogOutputProcessingException
  {
      try
        {
          SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
          saxParserFactory.setValidating(true);
          saxParserFactory.setNamespaceAware(true);
          this.saxParser = saxParserFactory.newSAXParser();
        }
      catch (Exception exception)
        {
          throw new LogOutputProcessingException(exception);
        }
  }

  // --------------------------------------------------------------------------------
  // Sources
  // --------------------------------------------------------------------------------

  /**
   * Adds a source to the list of sources.
   */

  protected void addSource (LogOutputSource source)
  {
    this.msg("Adding source: name = " + source.getName() + ", source = " +source);
    this.sources.add(source);
  }

  // --------------------------------------------------------------------------------
  // Component definition and creation
  // --------------------------------------------------------------------------------

  /**
   * Creates the key by which the class name of the component with the specified type and
   * name can be looked-up in the map {@link #componentClassNames componentClassNames}.
   */

  protected static final String createKey (String type, String name)
  {
    return type + ": " + name;
  }

  /**
   * Defines a component with the specified type, name, and class name.
   */

  protected void defineComponent (String type, String name, String className)
  {
    this.msg
      ("Defining component:" +
       " type = " + type +
       ", name = " + name + 
       ", className = " + className);
    this.componentClassNames.put(createKey(type, name), className);
  }

  /**
   * Reads component definitions from the specified XML file.
   */

  public void defineComponents (File file)
    throws LogOutputProcessingException
  {
    this.msg("Reading components definition file: " + file);
    try
      {
        this.saxParser.parse(file, new ConfigSAXHandler());
      }
    catch(Exception exception)
      {
        throw new LogOutputProcessingException(exception);
      }
  }

  /**
   * Returns the class for the component with the specified type and name.
   */

  protected Class getComponentClass (String type, String name)
    throws LogOutputProcessingException
  {
    String className = (String)this.componentClassNames.get(createKey(type, name));
    if ( className == null )
      throw new LogOutputProcessingException
        ("No class found for component: " + type + " " + name);
    try
      {
        return Class.forName(className);
      }
    catch(Exception exception)
      {
        throw new LogOutputProcessingException
          ("Failed to load class for component: " + type + " " + name, exception);
      }
  }

  /**
   * Creates a component with the specified type and name.
   */

  protected LogAnalyserComponent createComponent (String type, String name)
    throws LogOutputProcessingException
  {
    try
      {
        this.msg("Creating component: type = " + type + ", name = " + name);
        return
          (LogAnalyserComponent)(this.getComponentClass(type, name).newInstance());
      }
    catch(Exception exception)
      {
        throw new LogOutputProcessingException(exception);
      }
  }

  /**
   * Sets a paramater of the specified component.
   */

  protected void setComponentParameter (LogAnalyserComponent component,
                                        String paramName,
                                        String paramValue)
    throws LogOutputProcessingException
  {
    this.msg
      ("Setting component parameter:" +
       " componentName = " + this.getComponentName(component) +
       ", paramName = " + paramName +
       ", paramValue = " + paramValue);
    component.setParameter(paramName, paramValue);
  }

  /**
   * Returns the name of the specified component.
   */

  protected String getComponentName (LogAnalyserComponent component)
  {
    String className = component.getClass().getName();
    Iterator iterator = this.componentClassNames.entrySet().iterator();
    String name = null;
    while ( name == null && iterator.hasNext() )
      {
        Map.Entry entry = (Map.Entry)iterator.next();
        if ( ((String)entry.getValue()).equals(className) )
          name = (String)entry.getKey();
      }
    return name;
  }

  // --------------------------------------------------------------------------------
  // Setup
  // --------------------------------------------------------------------------------

  /**
   * Reads the specified setup file.
   */

  public void setup (File file)
    throws LogOutputProcessingException
  {
    try
      {
        this.msg("Reading setup file: " + file);
        this.saxParser.parse(file, new SetupSAXHandler());
      }
    catch(Exception exception)
      {
        throw new LogOutputProcessingException(exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Running
  // --------------------------------------------------------------------------------

  /**
   * Executes an analyse run.
   */

  public void analyse ()
    throws LogOutputProcessingException
  {
    this.msg("Analysing");
    this.parser.notifyStart();
    Iterator iterator = this.sources.iterator();
    while ( iterator.hasNext() )
      this.parser.parse((LogOutputSource)iterator.next());
    this.parser.notifyEnd();
    this.msg("Done");
  }

  // --------------------------------------------------------------------------------
  // Status messages
  // --------------------------------------------------------------------------------

  /**
   * Prints the specified string to stdout provided {@link #verbose verbose} is
   * <code>true</code>.
   */

  protected void msg (String message)
  {
    if ( this.verbose )
      System.out.println(message);
  }

  /**
   * Sets wether the engine should print status messages to stdout. Default is
   * <code>false</code>.
   */

  public void setVerbose (boolean verbose)
  {
    this.verbose = verbose;
  }

  // --------------------------------------------------------------------------------
  // Execution from the command line.
  // --------------------------------------------------------------------------------

  /**
   * Main method.
   */

  public static void main (String[] params)
  {
    try
      {
        // Task specifiers:
        final int ANALYSE = 0;
        final int SHOW_HELP = 1;
        final int SHOW_VERSION =2;
        int task = ANALYSE;

        // The configuration directory:
        String configDirName =
          getSystemProperty("configDir", "@basedir@" + File.separator + "@etc.dir@");

        // Defaults for the component definition files:
        File componentsFile = new File (configDirName, "components.xml");
        File localComponentsFile = new File(configDirName, "components.local.xml");
        File addComponentsFile = null;

        // Default for the setup file:
        File setupFile = new File("analyse.xml");

        // Default for the verbose flag:
        boolean verbose = false;

        // Processing the command line:
        for (int i = 0; i < params.length; i++)
          {
            String param = params[i];
            // --help, -h
            if ( param.equals("--help") || param.equals("-h") )
              task = SHOW_HELP;
            // --version, -v
            else if ( param.equals("--version") || param.equals("-v") )
              task = SHOW_VERSION;
            // --components=FILE, -c FILE
            else if ( param.equals("--components") || param.equals("-c") )
              {
                if ( (i+1) >= params.length )
                  throw new IllegalArgumentException("Missing value after " + param);
                i++;
                addComponentsFile = new File(params[i]);
              }
            else if ( param.startsWith("--components=") )
              addComponentsFile = new File(params[i].substring("--components=".length()));
            else if ( param.startsWith("-c=") )
              addComponentsFile = new File(params[i].substring("-c=".length()));
            // --setup=FILE, -s FILE
            else if ( param.equals("--setup") || param.equals("-s") )
              {
                if ( (i+1) >= params.length )
                  throw new IllegalArgumentException("Missing value after " + param);
                i++;
                setupFile = new File(params[i]);
              }
            else if ( param.startsWith("--setup=") )
              setupFile = new File(params[i].substring("--setup=".length()));
            else if ( param.startsWith("-s=") )
              setupFile = new File(params[i].substring("-s=".length()));
            else if ( param.equals("--verbose") )
              verbose = true;
            else
              throw new IllegalArgumentException("Illegal parameter: " + param);
          }
        
        // Executing task
        switch ( task )
          {
          case ANALYSE:

            // Get an engine:
            LogAnalyserEngine engine = new LogAnalyserEngine();
            engine.setVerbose(verbose);

            // Read global components file:
            if ( !componentsFile.exists() )
              throw new LogOutputProcessingException
                ("Can not find global components file: " + componentsFile);
            engine.defineComponents(componentsFile);

            // Read local components file (if existing):
            if ( localComponentsFile.exists() )
              engine.defineComponents(localComponentsFile);

            // Read user-specified components file (if existing):
            if ( addComponentsFile != null && addComponentsFile.exists() )
              engine.defineComponents(addComponentsFile);

            // Read setup file:
            if ( !setupFile.exists() )
              throw new LogOutputProcessingException
                ("Can not find setup file: " + setupFile);
            engine.setup(setupFile);

            // Analyse:
            engine.analyse();

            break;

          case SHOW_HELP:
            String programName = System.getProperty
              (LogAnalyserEngine.class.getName() + ".programName", "mmlogan");
            System.out.print
              ("Usage:\n" +
               "  " + programName + " [OPTIONS]\n" +
               "Description:\n" +
               "  Analyses Cocoon log files.\n" +
               "Options:\n" +
               "  --components=FILE | -c FILE\n" +
               "      Read additional component definitions from FILE.\n" +
               "  --setup=FILE | -s FILE\n" +
               "      Use FILE as the setup file, instead of the default \"./analyse.xml\".\n" +
               "  --help | -h\n" +
               "      Print this help text and exit.\n" +
               "  --version | -v\n" +
               "      Print version information and exit\n" +
               "  --verbose\n" +
               "      Turn verbose mode on\n");
            break;

          case SHOW_VERSION:
            System.out.println("$Version$");
          }
      }
    catch (Throwable throwable)
      {
        if ( getSystemProperty("debug", "false").equals("true") )
          throwable.printStackTrace();
        else
          {
            String message = unwrappThrowable(throwable).getMessage();
            System.err.println
              ("ERROR: " + (message != null ? message : throwable.toString()));
          }
      }
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Returns a system property. The property name is
   * <pre>LogAnalyserEngine.class.getName() + "." + lastNamePart</pre>.
   * If the property is not set, the specified default value is returned.
   */

  protected static String getSystemProperty (String lastNamePart, String defaultValue)
  {
    return System.getProperty
      (LogAnalyserEngine.class.getName() + "." + lastNamePart, defaultValue);
  }

  /**
   * Returns the "root" of <code>throwable</code>. This is the Throwable that
   * initially caused the problem.
   */

  protected static Throwable unwrappThrowable (Throwable throwable)
  {
    Throwable cause;
    while ( true )
      {
	cause = throwable.getCause();
	if ( cause == null ) break;
	throwable = cause;
      }
    return throwable;
  }
}


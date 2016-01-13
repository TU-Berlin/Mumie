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

package net.mumie.xslt;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Hashtable;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import net.mumie.util.Util;

/**
 * <p>
 *   Frontend to TrAX complient XSLT processors.
 * <p>
 *
 * @author Tilman Rassy
 *
 * @version <span class="file">$Id: MmXSL.java,v 1.10 2003/08/20 12:59:43 rassy Exp $</span>
 */


public class MmXSL
{
  // ------------------------------------------------------------------------------------------
  // Static constants
  // ------------------------------------------------------------------------------------------

  /**
   * The version of this class
   */

  public static final String VERSION = "$Revision: 1.10 $";

  /**
   * Possible task specifier value.
   */

  public static final int UNDEFINED = -1;

  /**
   * Possible task specifier value.
   */

  public static final int TRANSFORM = 0;

  /**
   * Possible task specifier value.
   */

  public static final int SHOW_VERSION = 1;

  /**
   * Possible task specifier value.
   */

  public static final int SHOW_HELP = 2;

  /**
   * Possible task specifier value.
   */

  public static final int SHOW_SETTINGS = 3;
    
  /**
   * Default for the expected suffix of source system ids.
   */

  public static final String DEFAULT_SOURCE_SUFFIX = ".xml";

  /**
   * Default for the suffix of result system ids.
   */

  public static final String DEFAULT_RESULT_SUFFIX = ".xhtml";

  // ------------------------------------------------------------------------------------------
  // Global vriables
  // ------------------------------------------------------------------------------------------

  /**
   * The application name.
   */

  protected String applicationName = "java net.mumie.xslt.MmXSL";

  /**
   * The <code>TransformerFactory</code> used by this processor.
   */

  protected TransformerFactory transformerFactory;

  /**
   * The stylesheet.
   */

  protected XSLStylesheet stylesheet = null;

  /**
   * The sources.
   */

  protected List sourceSystemIds = new ArrayList();

  /**
   * The result. Used only if exactly one source is specified
   */

  protected String resultSystemId = null;;

  /**
   * The suffix of a source system id that is replaced by {@link #resultSuffix} to optain
   * the result system id. Default is {@link #DEFAULT_SOURCE_SUFFIX}.
   */

  protected String sourceSuffix = DEFAULT_SOURCE_SUFFIX;

  /**
   * The suffix that is given the result system ids. Default is
   * {@link #DEFAULT_RESULT_SUFFIX}. 
   */

  protected String resultSuffix = DEFAULT_RESULT_SUFFIX;

  /**
   * Whether status messages should be suppressed or not.
   */

  protected boolean quiet = false;

  // ------------------------------------------------------------------------------------------
  // get/set methods and the like
  // ------------------------------------------------------------------------------------------

  /**
   * Set the application name.
   */

  public void setApplicationName (String applicationName)
  {
    this.applicationName = applicationName;
  }

  /**
   * Returns the application name.
   */

  public String getApplicationName ()
  {
    return this.applicationName;
  }

  /**
   * Returns the transformer factory used by this class.
   */

  public TransformerFactory getTransformerFactory ()
  {
    return this.transformerFactory;
  }

  /**
   * Sets the stylesheet
   */

  public void setStylesheet (XSLStylesheet stylesheet)
  {
    this.stylesheet = stylesheet;
  }

  /**
   * Sets the stylesheet
   */

  public void setStylesheet (String systemId)
    throws TransformerConfigurationException
  {
    this.setStylesheet(new XSLStylesheet(this.getTransformerFactory(), systemId));
  }

  /**
   * Returns the stylesheet.
   */

  public XSLStylesheet getStylesheet()
  {
    return this.stylesheet;
  }

  /**
   * Adds a source
   */

  public void addSource (String sourceSystemId)
  {
    this.sourceSystemIds.add(sourceSystemId);
  }

  /**
   * Returns the source system ids.
   */

  public String[] getSourceSystemIds ()
  {
    return (String[])(this.sourceSystemIds.toArray(new String[this.sourceSystemIds.size()]));
  }

  /**
   * Returns the number of sources.
   */

  public int getNumberOfSources ()
  {
    return this.sourceSystemIds.size();
  }

  /**
   * Sets the result.
   */

  public void setResult (String resultSystemId)
  {
    this.resultSystemId = resultSystemId;
  }

  /**
   * Returns the system id of the result.
   */

  public String getResultSystemId ()
  {
    return this.resultSystemId;
  }

  /**
   * <p>
   *   Sets the source replacement suffix. This is the suffix of a source system id that is
   *   replaced by {@link #resultSuffix} to optain the result system id.
   * </p>
   * <p>
   *   If <code>sourceSuffix</code> is not <code>null</code>, the source replacement suffix
   *   is set to <code>sourceSuffix</code>, otherwise to {@link #DEFAULT_SOURCE_SUFFIX}. 
   * </p>
   */

  public void setSourceSuffix (String sourceSuffix)
  {
    this.sourceSuffix = ( sourceSuffix != null ? sourceSuffix : DEFAULT_SOURCE_SUFFIX );
  }

  /**
   * Returns the source replacement suffix.
   */

  public String getSourceSuffix ()
  {
    return this.sourceSuffix;
  }

  /**
   * <p>
   *   Sets the result suffix. This is the suffix given to the result system ids unless
   *   {@link #resultSystemId} is specified. 
   * </p>
   * <p>
   *   If <code>resultSuffix</code> is not <code>null</code>, the result suffix is set to
   *   <code>resultSuffix</code>, otherwise to {@link #DEFAULT_RESULT_SUFFIX}.  
   * </p>
   */

  public void setResultSuffix (String resultSuffix)
  {
    this.resultSuffix = ( resultSuffix != null ? resultSuffix : DEFAULT_RESULT_SUFFIX );
  }

  /**
   * Returns the result suffix.
   */

  public String getResultSuffix ()
  {
    return this.resultSuffix;
  }

  /**
   * Sets whether status messages are to be suppressed or not.
   */

  public void setQuiet (boolean quiet)
  {
    this.quiet = quiet;
  }

  /**
   * Sets whether status messages are to be suppressed or not.
   */

  public void setQuiet (String value)
  {
    this.setQuiet(Util.getBoolean(value));
  }

  /**
   * Returns whether status messages are to be suppressed or not.
   */

  public boolean getQuiet ()
  {
    return this.quiet;
  }

  // ------------------------------------------------------------------------------------------
  // Methods to execute a task, including auxiliaries
  // ------------------------------------------------------------------------------------------

  /**
   * Prints a status message.
   */

  public void message (String message)
  {
    if ( ! this.getQuiet() )
      System.out.print(message);
  }

  /**
   * <p>
   *   Composes a result system id from a source system id. Works as follows:
   * </p>
   * <p>
   *   If <code>sourceSystemId</code> ends with {@link #sourceSuffix}, this suffix is
   *   replaced by {@link #resultSuffix}.
   * </p>
   * <p>
   *   Otherwise, {@link #resultSuffix} is appended to <code>sourceSystemId</code>.
   * </p>
   */

  protected String composeResultSystemId (String sourceSystemId)
  {
    String systemId = new String(sourceSystemId);
    if ( systemId.endsWith(this.getSourceSuffix()) )
      systemId = Util.replaceSuffix(systemId, this.getSourceSuffix(), this.getResultSuffix());
    else
      systemId = systemId.concat(this.getResultSuffix());
    return systemId;
  }

  /**
   * Checks if everything is ok for a transformation.
   */

  public void check ()
    throws IllegalArgumentException
  {
    if ( this.getStylesheet() == null )
      throw new IllegalArgumentException("No stylesheet specified");
    if ( ( this.getResultSystemId() != null ) && ( this.getNumberOfSources() > 1 ) )
      throw new IllegalArgumentException
	("Specifying an output file is not allowed with multiple input files");
  }

  /**
   * <p>
   *   Performs a single transformation. Works as follows:
   * </p>
   * <p>
   *   If <code>sourceSystemId</code> is <code>null</code>, and <code>resultSystemId</code>
   *   is <code>null</code> or <span class="string">"-"</span>, transforms from stdin to
   *   stdout.
   * </p>
   * <p>
   *   If <code>sourceSystemId</code> is <code>null</code>, and <code>resultSystemId</code>
   *   is not <code>null</code> and not <span class="string">"-"</span>, transforms from
   *   stdin to <code>resultSystemId</code>.
   * </p>
   * <p>
   *   If <code>sourceSystemId</code> is not <code>null</code>, and <code>resultSystemId</code>
   *   is <code>null</code>, transforms from <code>sourceSystemId</code> to the system id
   *   returned by {@link #composeResultSystemId} applied to <code>sourceSystemId</code>.
   * </p>
   * <p>
   *   If <code>sourceSystemId</code> is not <code>null</code>, and
   *   <code>resultSystemId</code> is <span class="string">"-"</span>, transforms from 
   *   <code>sourceSystemId</code> to stdout.
   * </p>
   * <p>
   *   If <code>sourceSystemId</code> is not <code>null</code>, and
   *   <code>resultSystemId</code> is is not <code>null</code> and not <span
   *   class="string">"-"</span>, transforms from <code>sourceSystemId</code> to
   *   <code>resultSystemId</code>.
   * </p>
   */

  protected void transform (String sourceSystemId, String resultSystemId)
    throws TransformerException
  {
    Source source;
    Result result;

    // No source specified:
    if ( sourceSystemId == null )
      {
	// Result not specified or "-":
	if ( ( resultSystemId == null ) || ( resultSystemId.equals("-") ) )
	  {
	    message("Transforming stdin -> stdout ...\n");
	    source = new StreamSource(System.in);
	    result = new StreamResult(System.out);
	  }
	// Result specified and not "-":
	else
	  {
	    message("Transforming stdin -> " + resultSystemId + " ...\n");
	    source = new StreamSource(System.in);
	    result = new StreamResult(resultSystemId);
	  }
      }
    // Source specified:
    else
      {
	// No result specified:
	if ( resultSystemId == null )
	  {
	    String composedResultSystemId = this.composeResultSystemId(sourceSystemId);
	    message("Transforming " + sourceSystemId + " -> " + composedResultSystemId + "\n");
	    source = new StreamSource(sourceSystemId);
	    result = new StreamResult(composedResultSystemId);
	  }
	// Result specified and "-":
	else if ( resultSystemId.equals("-" ) )
	  {
	    message("Transforming " + sourceSystemId + " -> stdout ... ");
	    source = new StreamSource(sourceSystemId);
	    result = new StreamResult(System.out);
	  }
	// Result specified and not "-":
	else
	  {
	    message("Transforming " + sourceSystemId + " -> " + resultSystemId + "\n");
	    source = new StreamSource(sourceSystemId);
	    result = new StreamResult(resultSystemId);
	  }
      }

    this.getStylesheet().transform(source, result);
    message("Done\n");
  }
	      
  /**
   * Transforms the source(s).
   */

  public void transform ()
    throws TransformerException
  {
    this.check();

    String[] sourceSystemIds = this.getSourceSystemIds();

    if ( sourceSystemIds.length == 0 )
      {
	this.transform(null, this.getResultSystemId());
      }
    else if ( sourceSystemIds.length == 1 )
      {
	this.transform(sourceSystemIds[0], this.getResultSystemId());
      }
    else
      {
	for (int i = 0; i < sourceSystemIds.length; i++)
	  {
	    this.transform(sourceSystemIds[i], null);
	  }
      }
  }

  /**
   * Auxiliary method for {@link #showSettings}.
   */

  protected static void printVariable (String name, Object value)
  {
    System.out.println(name + ": " + (value != null ? value.toString() : "-not specified-"));
  }

  /**
   * Auxiliary method for {@link #showSettings}.
   */

  protected static void printVariable (String name, boolean value)
  {
    System.out.println(name + ": " + (value ? "on" : "off"));
  }

  /**
   * Prints out settings to stdout.
   */

  public void showSettings ()
  {
    System.out.println(
                "Application name : " + Util.getNotation(this.getApplicationName()) + "\n" +
                "Stylesheet       : " + Util.getNotation(this.getStylesheet()));
    String [] sourceSystemIds = this.getSourceSystemIds();
    if ( sourceSystemIds.length == 0 )
      System.out.println(
                "Source(s)        : -none-");
    else 
      {
	System.out.println(
                "Source(s)        : " + sourceSystemIds[0]);
	for (int i = 1; i < sourceSystemIds.length; i++)
	  System.out.println(
                "                   " + sourceSystemIds[i]);
      }
    System.out.println(
                "Result system id : " + Util.getNotation(this.getResultSystemId()) + "\n" +
                "Source suffix    : " + Util.getNotation(this.getSourceSuffix()) + "\n" +
                "Result suffix    : " + Util.getNotation(this.getResultSuffix()));
    if ( this.getStylesheet() != null )
      {
	String[] parameterNames = this.getStylesheet().getParameterNames();
	if ( parameterNames.length == 0 )
	  System.out.println(
                "Parameter(s)     : -none-");
	else 
	  {
	    System.out.println(
                "Parameter(s)     : " + parameterNames[0] + " = "
		+ this.getStylesheet().getParameter(parameterNames[0]));
	    for (int i = 1; i < parameterNames.length; i++)
	      System.out.println(
                "                   " + parameterNames[i] + " = "
		+ this.getStylesheet().getParameter(parameterNames[i]));
	  }
      }

    System.out.println(
                "Quiet            : " + Util.getNotation(this.getQuiet()));
  }

  /**
   * Prints the help text.
   */

  public void showHelp()
  {
    System.out.println
      (
       "Usage:\n" +
       " " + this.applicationName + " [OPTIONS] [ SOURCE1 SOURCE2 ... ]\n" +
       "Arguments:\n" +
       "  SOURCE1 SOURCE2 ...   Name of the XML source files to transform. If none is\n" +
       "                        given, the source is read from stdin.\n" +
       "Options:\n" +
       "  --fail-on-error, -f\n" +
       "      Throw an exception if an error occurs. Otherwise, the error is reported\n" +
       "      to stderr, but the main method does not throw an exception.\n" +
       "  --help, -h\n" +
       "      Print this help text and exit.\n" +
       "  --name=APP_NAME, -n APP_MAME\n" +
       "      Set the application name. This may be usefull for wrapper scripts, which\n" +
       "      cat set their own name here.\n" +
       "  --output=OUTPUT_URL, -o OUTPUT_URL\n" +
       "      Write output to OUTPUT_URL. This options is allowed only in the case of a\n" +
       "      single source. If OUTPUT_URL is \"-\", or if the source is read from stdin\n" +
       "      and this option is not set, output goes to stdout.\n" +
       "  --output-suffix=OUTPUT_SUFFIX, -u OUTPUT_SUFFIX\n" +
       "      Use OUTPUT_SUFFIX as the suffix SOURCE_SUFFIX is replaced with when\n" +
       "      composing the output URL. Defaluts to \"" + DEFAULT_RESULT_SUFFIX + "\".\n" +
       "  --param NAME=VALUE, -p NAME=VALUE\n" +
       "      Set a stylesheet parameter. NAME and VALUE should be self-explanatory.\n" +
       "      This option may occur several times to set several parameters.\n" +
       "  --show-settings, -e\n" +
       "      Print the values of the most important variables and exit.\n" +
       "  --quiet, -q\n" +
       "      Suppress status messages.\n" +
       "  --source-suffix=SOURCE_SUFFIX, -U SOURCE_SUFFIX\n" +
       "      Use SOURCE_SUFFIX as the suffix to replace when composing the output URL.\n" +
       "      Defaults to \"" + DEFAULT_SOURCE_SUFFIX + "\".\n" +
       "  --stylesheet=STYLESHEET, -s STYLESHEET\n" +
       "      Use XSL stylesheet STYLESHEET. Must be an URL.\n" +
       "  --version, -v\n" +
       "      Print version information and exit.\n"
      );
  }

  /**
   * Prints the version.
   */

  public static void showVersion()
  {
    System.out.println(VERSION);
  }

  // ------------------------------------------------------------------------------------------
  // Auxiliaries (e.g., methods to process the command line)
  // ------------------------------------------------------------------------------------------

  /**
   * Auxiliary method to parse the command line. Checks if at position <code>pos</code> of
   * <code>params</code> there is a value parameter. I.e., checks if
   * <code>params[pos]</code> exists and does not start with a hyphen.
   */

  protected static boolean hasValueAt (String[] params, int pos)
  {
    return ( ( pos < params.length ) &&
	     ! ( ( params[pos].length() > 1 ) && ( params[pos].startsWith("-") ) ) );
  }

  /**
   * Sets up this <code>SimpleXSLTTransformer</code> by command line parameters.
   */

  protected int setupByCmdlineParams (String[] params)
    throws TransformerConfigurationException
  {
    int task = TRANSFORM;
    String stylesheetSystemId = null;
    Map stylesheetParameters = new Hashtable();

    for (int i = 0; i < params.length; i++)
      {
	// --help, -h:
	if ( ( params[i].equals("--help") ) || ( params[i].equals("-h") ) )
	  {
	    task = SHOW_HELP;
	  }

	// --name=APP_NAME, -n APP_MAME:
	else if ( ( params[i].equals("--name") ) || ( params[i].equals("-n") ) )
	  {
	    if ( ! hasValueAt(params, i+1) )
	      throw new IllegalArgumentException("Missing value after " + params[i]);
	    i++;
	    this.setApplicationName(params[i]);
	  }
	else if ( params[i].startsWith("--name=") )
	  {
	    this.setApplicationName(params[i].substring("--name=".length()));
	  }
	else if ( params[i].startsWith("-u=") )
	  {
	    this.setApplicationName(params[i].substring("-n=".length()));
	  }

	// --output=OUTPUT_URL, -o OUTPUT_URL:
	else if ( ( params[i].equals("--output") ) || ( params[i].equals("-o") ) )
	  {
	    if ( ! hasValueAt(params, i+1) )
	      throw new IllegalArgumentException("Missing value after " + params[i]);
	    i++;
	    this.setResult(params[i]);
	  }
	else if ( params[i].startsWith("--output=") )
	  {
	    this.setResult(params[i].substring("--output=".length()));
	  }
	else if ( params[i].startsWith("-o=") )
	  {
	    this.setResult(params[i].substring("-o=".length()));
	  }

	// --output-suffix=OUTPUT_SUFFIX, -u OUTPUT_SUFFIX:
	else if ( ( params[i].equals("--output-suffix") ) || ( params[i].equals("-u") ) )
	  {
	    if ( ! hasValueAt(params, i+1) )
	      throw new IllegalArgumentException("Missing value after " + params[i]);
	    i++;
	    this.setResultSuffix(params[i]);
	  }
	else if ( params[i].startsWith("--output-suffix=") )
	  {
	    this.setResultSuffix(params[i].substring("--output-suffix=".length()));
	  }
	else if ( params[i].startsWith("-u=") )
	  {
	    this.setResultSuffix(params[i].substring("-s=".length()));
	  }

	// --param NAME=VALUE, -p NAME=VALUE:
	else if ( ( params[i].equals("--param") ) || ( params[i].equals("-p") ) )
	  {
	    if ( ! hasValueAt(params, i+1) )
	      throw new IllegalArgumentException("Missing value after " + params[i]);
	    i++;
	    Util.put(stylesheetParameters, params[i]);
	  }

	// --show-settings, -e:
	else if ( ( params[i].equals("--show-settings") ) || ( params[i].equals("-e") ) )
	  {
	    task = SHOW_SETTINGS;
	  }

	// --quiet, -q:
	else if ( ( params[i].equals("--quiet") ) || ( params[i].equals("-q") ) )
	  {
	    this.setQuiet(true);
	  }

	// --source-suffix=SOURCE_SUFFIX, -U SOURCE_SUFFIX:
	else if ( ( params[i].equals("--source-suffix") ) || ( params[i].equals("-U") ) )
	  {
	    if ( ! hasValueAt(params, i+1) )
	      throw new IllegalArgumentException("Missing value after " + params[i]);
	    i++;
	    this.setSourceSuffix(params[i]);
	  }
	else if ( params[i].startsWith("--source-suffix=") )
	  {
	    this.setSourceSuffix(params[i].substring("--source-suffix=".length()));
	  }
	else if ( params[i].startsWith("-u=") )
	  {
	    this.setSourceSuffix(params[i].substring("-s=".length()));
	  }

	// --stylesheet=STYLESHEET, -s STYLESHEET:
	else if ( ( params[i].equals("--stylesheet") ) || ( params[i].equals("-s") ) )
	  {
	    if ( ! hasValueAt(params, i+1) )
	      throw new IllegalArgumentException("Missing value after " + params[i]);
	    i++;
	    stylesheetSystemId = params[i];
	  }
	else if ( params[i].startsWith("--stylesheet=") )
	  {
	    stylesheetSystemId = params[i].substring("--stylesheet=".length());
	  }
	else if ( params[i].startsWith("-s=") )
	  {
	    stylesheetSystemId = params[i].substring("-s=".length());
	  }

	// --version, -v:
	else if ( ( params[i].equals("--version") ) || ( params[i].equals("-v") ) )
	  {
	    task = SHOW_VERSION;
	  }

	// SOURCE:
	else if ( ! params[i].startsWith("-") )
	  {
	    this.addSource(params[i]);
	  }

	// Unknown parameter:
	else
	  {
	    throw new IllegalArgumentException
	      ("Unknown parameter: " + params[i]);
	  }
      }

    if ( ( task == TRANSFORM ) && ( stylesheetSystemId != null ) )
      {
	message("Loading stylesheet " + stylesheetSystemId + "\n");
	this.setStylesheet(stylesheetSystemId);
	this.getStylesheet().setParameters(stylesheetParameters);
	message("Done\n");
      }

    return task;
  }

  // ------------------------------------------------------------------------------------------
  // Main method
  // ------------------------------------------------------------------------------------------

  /**
   * The main method.
   */

  public static void main (String[] params)
    throws MmXSLException
  {
    MmXSL mmXSL = null;
    try
      {
	mmXSL = new MmXSL();
	int task = mmXSL.setupByCmdlineParams(params);
	switch ( task )
	  {
	  case TRANSFORM :
	    mmXSL.transform(); break;
	  case SHOW_SETTINGS :
	    mmXSL.showSettings(); break;
	  case SHOW_HELP :
	    mmXSL.showHelp(); break;
	  case SHOW_VERSION :
	    showVersion(); break;
	  }
      }
    catch (Exception exception)
      {
	if ( exception instanceof TransformerException )
	  System.err.print
	    (SimpleErrorListener.composeMessage((TransformerException)exception));
	else
	  System.err.print(exception.toString());
	throw new MmXSLException("Aborted due to error");
      }
  }

  // ------------------------------------------------------------------------------------------
  // Constructors
  // ------------------------------------------------------------------------------------------

  /**
   * Creates a new <code>SimpleXSLProcessor</code> object.
   */

  public MmXSL ()
    throws TransformerFactoryConfigurationError
  {
    this.transformerFactory = TransformerFactory.newInstance();
  }

  /**
   * Creates a new <code>SimpleXSLProcessor</code> object.
   */

  public MmXSL (String applicationName)
    throws TransformerFactoryConfigurationError
  {
    this.transformerFactory = TransformerFactory.newInstance();
    this.setApplicationName(applicationName);
  }
}

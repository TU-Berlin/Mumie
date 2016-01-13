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

package net.mumie.jvmd.cmdlib;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.util.CmdlineParamHelper;
import net.mumie.util.StringUtil;
import net.mumie.util.xml.AbortingXSLErrorListener;
import net.mumie.util.xml.CachingTransformerProvider;

/**
 * Applies XSL stylesheets to XML sources.
 *
 *   <h4>Usage:</h4>
 *   <pre>  mmxtr [<var>OPTIONS</var>] [<var>SOURCES</var>]</pre>
 *   <h4>Description:</h4>
 *     <p>Applies XSL stylesheets to XML sources.</p>
 *   <h4>Options:</h4>
 *   <dl>
 *     <dt><code>--stylesheet=<var>XSLFILE</var>, -s <var>XSLFILE</var></code></dt>
 *     <dd>
 *       Specifies the XSL stylesheet to use. If not specifed, mmxtr tries to
 *       obtain the stylesheet from the &lt;?xml-stylesheet ...?&gt; processing
 *       instruction in the source.
 *     </dd>
 *     <dt><code>--input=<var>SOURCE</var>, -i <var>SOURCE</var></code></dt>
 *     <dd>
 *       Specifies the source file. May occur several times to specify more than
 *       one source file. If not specified, the source is read from stdin.
 *     </dd>
 *     <dt><code>--output=<var>OUTPUT</var>, -o <var>OUTPUT</var></code></dt>
 *     <dd>
 *       Specifies the file the output is written to. If omitted, the filename is
 *       automatically created by replacing the suffix <var>INSUFF</var> of the source file
 *       by <var>OUTSUFF</var> (s.b.). If <code>"-"</code>, output goes to stdout. If multiple
 *       sources are specifed, it is not allowed to specify a file by this option; i.e., the
 *       option must either be omitted or <code>"-"</code> in that case.
 *     </dd>
 *     <dt><code>--input-suffix=<var>INSUFF</var>, -I <var>INSUFF</var></code></dt>
 *     <dd>
 *       Specifies the input suffix. This is the suffix which is replaced by
 *       <var>OUTSUFF</var> in automatic output filename creation. Default is <code>"xml"</code>.
 *     </dd>
 *     <dt><code>--output-suffix=<var>OUTSUFF</var>, -I <var>OUTSUFF</var></code></dt>
 *     <dd>
 *       Specifies the output suffix. This is the suffix which replaces <var>INSUFF</var> in
 *       automatic output filename creation.  Default is <code>"xhtml"</code>.
 *     </dd>
 *     <dt><code>--param <var>NAME</var>=<var>VALUE</var>,
 *         -p <var>NAME</var>=<var>VALUE</var></code></dt>
 *     <dd>
 *       Specifies a stylesheet parameter
 *     </dd>
 *     <dt><code>--version , -v</code></dt>
 *     <dd>
 *       Prints version information and exists
 *     </dd>
 *     <dt><code>--verbose , -b</code></dt>
 *     <dd>
 *       Enables printing of status messages to stdout
 *     </dd>
 *     <dt><code>--help, -h</code></dt>
 *     <dd>
 *       Prints this help text and exists
 *     </dd>
 *   </dl>
 *   <h4>Examples:</h4>
 *   <dl>
 *     <dt><code>$ mmxtr -s transf.xsl -o output.xml input.xml</code></dt>
 *     <dd>
 *       Transforms input.xml to output.xml by means of transf.xsl
 *     </dd>
 *     <dt><code>$ mmxtr foo.xml</code></dt>
 *     <dd>
 *       Transforms foo.xml to foo.xhtml using the XSL stylesheet specified in the
 *       &lt;?xml-stylesheet ...?&gt; processing instruction in foo.xml.
 *     </dd>
 *     <dt><code>$ mmxtr foo.xdat -I xdat -O txt</code></dt>
 *     <dd>
 *       Transforms foo.xdat to foo.txt using the XSL stylesheet specified in the
 *       &lt;?xml-stylesheet ...?&gt; processing instruction in foo.xdat.
 *     </dd>
 *     <dt><code>$ mmxtr -s transf.xsl foo1.xml foo2.xml foo3.xml</code></dt>
 *     <dd>
 *       Transforms foo1.xml, foo2.xml, and foo3.xml to foo1.xhtml, foo2.xhtml,
 *       and foo3.xhtml, respectivly, using the stylesheet transf.xsl in each
 *       transformation.
 *     </dd>
 *     <dt><code>$ mmxtr foo1.xml foo2.xml foo3.xml</code></dt>
 *     <dd>
 *       Transforms foo1.xml to foo1.xhtml using the stylesheet specified in the
 *       &lt;?xml-stylesheet ...?&gt; processing instruction in foo1.xml, foo2.xml to
 *       foo2.xhtml using the stylesheet specified in the &lt;?xml-stylesheet ...?&gt;
 *       processing instruction in foo2.xml, and so on.
 *     </dd>
 *     <dt><code>$ mmxtr -s transf.xsl -o - input.xml</code></dt>
 *     <dd>
 *       Transforms input.xml by means of transf.xsl and writes the output to
 *       stdout.
 *     </dd>
 *     <dt><code>$ mmxtr -s transf.xsl -o output.xml</code></dt>
 *     <dd>
 *       Reads input from stdin and transforms it to output.xml by means of
 *       transf.xsl
 *     </dd>
 *   </dl>
 * 
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Mmxtr.java,v 1.4 2007/07/16 10:49:30 grudzin Exp $</code>
 */

public class Mmxtr extends AbstractCommand 
{
  /**
   * The command name (<code>"mmxtr"</code>).
   */

  public static final String COMMAND_NAME = "mmxtr";

  /**
   * A short description of the command
   */

  public static final String COMMAND_DESCRIPTION =
    "Applies XSL stylesheets to XML sources";

  /**
   * The default source suffix. Value is <code>"xml"</code>.
   */

  protected static String DEFAULT_SOURCE_SUFFIX = "xml";

  /**
   * The default result suffix. Value is <code>"xhtml"</code>.
   */

  protected static String DEFAULT_RESULT_SUFFIX = "xhtml";

  /**
   * Wether messages are printed to stdout. Default is <code>false</code>.
   */

  protected static boolean verbose = false;

  /**
   * The error listener for the XSL transformations.
   */

  protected AbortingXSLErrorListener errorListener = new AbortingXSLErrorListener();

  /**
   * Returns the command name (<code>"mmxtr"</code>).
   */

  public String getName ()
  {
    return COMMAND_NAME;
  }

  /**
   * Returns a short description of the command
   */

  public String getDescription ()
  {
    return COMMAND_DESCRIPTION;
  }

  /**
   * Prints a message to stdout provided {@link #verbose} is <code>true</code>.
   */

  protected void message (String message)
  {
    if ( verbose )
      this.out.println("mmxtr: " + message);
  }

  /**
   * Auxiliary method. Returns a string representation of the specified result object. If
   * the latter is a {@link StreamResult} and the corresponding output stream equals
   * <code>this.out</code>, the returned string is <code>"stdout"</code>. Otherwise, the
   * returned string is obtained by <code>result.getSystemId()</code>.
   */

  protected String toString (Result result)
  {
    if ( result instanceof StreamResult &&
         ((StreamResult)result).getOutputStream() == this.out )
      return "stdout";
    else
      return result.getSystemId();
  }

  /**
   * Auxiliary method. Returns a string representation of the specified source object. If
   * the latter is a {@link StreamSource} and the corresponding input stream equals
   * <code>this.in</code>, the returned string is <code>"stdin"</code>. Otherwise, the
   * returned string is obtained by <code>source.getSystemId()</code>.
   */

  protected String toString (Source source)
  {
    if ( source instanceof StreamSource &&
         ((StreamSource)source).getInputStream() == this.in )
      return "stdin";
    else
      return source.getSystemId();
  }

  /**
   * Auxiliary method. Passes the specified parameters to the specified transformer.
   */

  protected static void setTransfomerParams (Transformer transformer, Map transformerParams)
    throws Exception
  {
    Iterator paramsIterator = transformerParams.entrySet().iterator();
    while ( paramsIterator.hasNext() )
      {
        Map.Entry entry = (Map.Entry)paramsIterator.next();
        transformer.setParameter((String)entry.getKey(), entry.getValue());
      }
  }

  /**
   * Auxiliary method. Creates a {@link Result Result} object by replacing the specifed
   * source suffix by the specified result suffix in the specified source filename.
   */

  protected Result composeResult (String sourceFilename,
                                  String sourceSuffix,
                                  String resultSuffix)
    throws Exception
  {
    String resultFilename =
      StringUtil.replaceSuffix(sourceFilename, sourceSuffix, resultSuffix);
    return new StreamResult(this.makeAbsoluteFile(resultFilename));
  }

  /**
   * Carries out the actual transformation.
   */

  protected void transform (URI transformerURI,
                            List sourceFilenames,
                            String resultFilename,
                            String sourceSuffix,
                            String resultSuffix,
                            Map transformerParams)
    throws Exception
  {
    if ( transformerURI == null && sourceFilenames.size() == 0 )
      throw new IllegalArgumentException("Neither source nor stylesheet specified");

    if ( resultFilename != null && !resultFilename.equals("-") && sourceFilenames.size() > 1 )
      throw new IllegalArgumentException
	("Specifying an output file is not allowed with multiple input files");

    // Result as specified by the user:
    Result specifiedResult =
      (resultFilename != null
       ? (resultFilename.equals("-")
          ? new StreamResult(this.out)
          : new StreamResult(new File(resultFilename)))
       : null);

    // Get the transformerProvider:
    CachingTransformerProvider transformerProvider =
      CachingTransformerProvider.getSharedInstance();

    if ( sourceFilenames.size() == 0 ) // No source specified
      {
        // Get source from stdin:
        Source source = new StreamSource(this.in);

        // Get transformer:
        Transformer transformer = transformerProvider.getTransformer(transformerURI);

        // Set error listener:
        transformer.setErrorListener(this.errorListener);

        // Set parameters:
        setTransfomerParams(transformer, transformerParams);

        // Transform:
        transformer.transform(source, specifiedResult);
      }
    else // one or more source(s) specified
      {
        Iterator sourceIterator = sourceFilenames.iterator();
        while ( sourceIterator.hasNext() )
          {
            // Get input source:
            String sourceFilename = (String)sourceIterator.next();
            Source source = new StreamSource(this.makeAbsoluteFile(sourceFilename));

            // Get result:
            Result result =
              (specifiedResult != null
               ? specifiedResult
               : this.composeResult(sourceFilename, sourceSuffix, resultSuffix));

            // Get the transformer:
            Transformer transformer =
              (transformerURI != null
               ? transformerProvider.getTransformer(transformerURI)
               : transformerProvider.getAssociatedTransformer(source, null, null, null));

            // Set error listener:
            transformer.setErrorListener(this.errorListener);

            // Set parameters:
            setTransfomerParams(transformer, transformerParams);

            // Transform:
            this.message
              ("Transforming: " + this.toString(source) + " -> " + this.toString(result));
            transformer.transform(source, result);
          }
      }
  }

  /**
   * Displayes a help text.
   */

  public void showHelp ()
    throws Exception
  {
    this.out.print
      (
       "Usage:\n" +
       "  mmxtr [OPTIONS] [SOURCES]\n" +
       "Description:\n" +
       "  Applies XSL stylesheets to XML sources.\n" +
       "Options:\n" +
       "  --stylesheet=XSLFILE, -s XSLFILE\n" +
       "      Specifies the XSL stylesheet to use. If not specifed, mmxtr tries to\n" +
       "      obtain the stylesheet from the <?xml-stylesheet ...?> processing\n" +
       "      instruction in the source.\n" +
       "  --input=SOURCE, -i SOURCE\n" +
       "      Specifies the source file. May occur several times to specify more than\n" +
       "      one source file. If not specified, the source is read from stdin.\n" +
       "  --output=OUTPUT, -o OUTPUT\n" +
       "      Specifies the file the output is written to. If omitted, the filename is\n" +
       "      automatically created by replacing the suffix INSUFF of the source file\n" +
       "      by OUTSUFF (s.b.). If \"-\", output goes to stdout. If multiple sources are\n" +
       "      specifed, it is not allowed to specify a file by this option; i.e., the\n" +
       "      option must either be omitted or \"-\" in that case.\n" +
       "  --input-suffix=INSUFF, -I INSUFF\n" +
       "      Specifies the input suffix. This is the suffix which is replaced by\n" +
       "      OUTSUFF in automatic output filename creation. Default is \"xml\".\n" +
       "  --output-suffix=OUTSUFF, -I OUTSUFF\n" +
       "      Specifies the output suffix. This is the suffix which replaces INSUFF in\n" +
       "      automatic output filename creation.  Default is \"xhtml\".\n" +
       "  --param NAME=VALUE, -p NAME=VALUE\n" +
       "      Specifies a stylesheet parameter\n" +
       "  --version , -v\n" +
       "      Prints version information and exists\n" +
       "  --verbose , -b\n" +
       "      Enables printing of status messages to stdout\n" +
       "  --help, -h\n" +
       "      Prints this help text and exists\n" +
       "Examples:\n" +
       "  $ mmxtr -s transf.xsl -o output.xml input.xml\n" +
       "      Transforms input.xml to output.xml by means of transf.xsl\n" +
       "  $ mmxtr foo.xml\n" +
       "      Transforms foo.xml to foo.xhtml using the XSL stylesheet specified in the\n" +
       "      <?xml-stylesheet ...?> processing instruction in foo.xml.\n" +
       "  $ mmxtr foo.xdat -I xdat -O txt\n" +
       "      Transforms foo.xdat to foo.txt using the XSL stylesheet specified in the\n" +
       "      <?xml-stylesheet ...?> processing instruction in foo.xdat.\n" +
       "  $ mmxtr -s transf.xsl foo1.xml foo2.xml foo3.xml\n" +
       "      Transforms foo1.xml, foo2.xml, and foo3.xml to foo1.xhtml, foo2.xhtml,\n" +
       "      and foo3.xhtml, respectivly, using the stylesheet transf.xsl in each\n" +
       "      transformation.\n" +
       "  $ mmxtr foo1.xml foo2.xml foo3.xml\n" +
       "      Transforms foo1.xml to foo1.xhtml using the stylesheet specified in the\n" +
       "      <?xml-stylesheet ...?> processing instruction in foo1.xml, foo2.xml to\n" +
       "      foo2.xhtml using the stylesheet specified in the <?xml-stylesheet ...?>\n" +
       "      processing instruction in foo2.xml, and so on.\n" +
       "  $ mmxtr -s transf.xsl -o - input.xml\n" +
       "      Transforms input.xml by means of transf.xsl and writes the output to\n" +
       "      stdout.\n" +
       "  $ mmxtr -s transf.xsl -o output.xml\n" +
       "      Reads input from stdin and transforms it to output.xml by means of\n" +
       "      transf.xsl\n"
       );
  }

  /**
   * Displayes version information.
   */

  public void showVersion ()
    throws Exception
  {
    this.out.println("$Revision: 1.4 $");
  }

  /**
   * Execute method. Processes the parameters, then executes one of the following tasks: doing
   * the transformation, printing a help text, printing version information.
   */

  public int execute ()
    throws CommandExecutionException 
  {
    try
      {
        final int TRANSFORM = 0;
        final int SHOW_HELP = 1;
        final int SHOW_VERSION = 2;
        int task = TRANSFORM;

        URI transformerURI = null;
        List sourceFilenames = new ArrayList();
        String resultFilename = null;
        String sourceSuffix = DEFAULT_SOURCE_SUFFIX;
        String resultSuffix = DEFAULT_RESULT_SUFFIX;
        Map transformerParams = new HashMap();

        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            if ( paramHelper.checkOptionWithValue("--output", "-o", false) )
              resultFilename = paramHelper.getValue();
            else if ( paramHelper.checkOptionWithValue("--stylesheet", "-s") )
              transformerURI = this.makeAbsoluteFile(paramHelper.getValue()).toURI();
            else if ( paramHelper.checkOptionWithKeyValuePair("--param", "-p") )
              paramHelper.copyKeyValuePair(transformerParams);
            else if ( paramHelper.checkParam("--help", "-h") )
              task = SHOW_HELP;
            else if ( paramHelper.checkParam("--version", "-v") )
              task = SHOW_VERSION;
            else if ( paramHelper.checkOptionWithValue("--input-suffix", "-I") )
              sourceSuffix = paramHelper.getValue();
            else if ( paramHelper.checkOptionWithValue("--output-suffix", "-O") )
              resultSuffix = paramHelper.getValue();
            else if ( paramHelper.checkParam("--verbose", "-b") )
              verbose = true;
            else if ( paramHelper.checkOptionWithValue("--input", "-i", false) )
              sourceFilenames.add(paramHelper.getValue());
            else if ( paramHelper.checkArgument() )
              sourceFilenames.add(paramHelper.getParam());
            else
              throw new IllegalArgumentException
                ("Unknown parameter: " + paramHelper.getParam());
          }

        switch ( task )
          {
          case TRANSFORM:
            this.transform
              (transformerURI, sourceFilenames, resultFilename, sourceSuffix,
               resultSuffix, transformerParams);
            break;
          case SHOW_HELP:
            this.showHelp();
            break;
          case SHOW_VERSION:
            this.showVersion();
            break;
          }

        return 0;
      }
    catch (Exception exception)
      {
        throw new CommandExecutionException(exception);
      }
  }
}

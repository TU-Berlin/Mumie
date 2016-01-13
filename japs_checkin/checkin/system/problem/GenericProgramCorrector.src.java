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

package net.mumie.corrector;

import java.io.File;
import java.io.StringReader;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.mumie.cocoon.util.CocoonEnabledDataSheet;
import net.mumie.cocoon.util.ProblemCorrectionException;
import net.mumie.cocoon.util.ProblemCorrector;
import net.mumie.cocoon.util.ProcessResult;
import net.mumie.cocoon.util.ProcessUtil;
import net.mumie.cocoon.util.UniqueFilenameCreator;
import net.mumie.util.io.IOUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import java.util.List;
import java.util.ArrayList;

/**
 * Generic corrector for programming problems.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: GenericProgramCorrector.src.java,v 1.7 2009/08/05 11:48:46 linges Exp $</code>
 *
 * @mm.type java_class
 * @mm.category corrector
 * @mm.section system/problem
 * @mm.copyright Copyright (c) 2008, Technische Universitaet Berlin
 * @mm.buildClasspath mumie-util.jar
 * @mm.buildClasspath mumie-japs-for-mmcdk.jar
 * @mm.buildClasspath mumie-japs-datasheet.jar
 * @mm.buildClasspath excalibur-xmlutil-2.1.jar
 * @mm.description Generischer Korrektor fuer Programmieraufgaben
 * @mm.changelog Erste Version
 */

public class GenericProgramCorrector
  implements ProblemCorrector
{
  /**
   * Time limit for compiling (in milliseconds).
   */

  public static final long COMPILE_TIME_LIMIT = 15000;

  /**
   * Time limit for runing the wrapper (in milliseconds).
   */

  public static final long RUN_TIME_LIMIT = 15000;

  /**
   * Pattern to substitute <code>@...@</code> placeholders in the wrapper code.
   */

  protected static final Pattern pattern = Pattern.compile("(@USER_ANSWER@)|(@KEY@)");

  /**
   * Pattern to find the path of the wrapper file (Wrapper.java).
   */

  protected static final Pattern wrapperPathPattern = Pattern.compile("\\S+/Wrapper.java");

  /**
   * Quotes XML special characters and characters above 127.
   */

  public static String quoteXML (String string)
  {
    StringBuilder buffer = new StringBuilder();
    for (char c : string.toCharArray())
      {
        if ( c == '<' )
          buffer.append("&lt;");
        else if ( c == '>' )
          buffer.append("&gt;");
        else if ( c == '&' )
          buffer.append("&amp;");
        else if ( c < 128 )
          buffer.append(c);
        else
          buffer.append("&#").append((int)c).append(";");
      }
    return buffer.toString();
  }

  /**
   * Creates and returns the code of the <code>EvaluatorExecutor</code> class.
   */

  protected static final String createEvaluatorExecutorCode ()
  {
    String newline = System.getProperty("line.separator");
    return
      "import net.mumie.cocoon.corrutil.ProgramSecurityManager;" + newline +
      "public class EvaluatorExecutor" + newline +
      "{" + newline +
      "  public static void main (String[] params)" + newline +
      "  {" + newline +
      "    System.setSecurityManager(new ProgramSecurityManager());" + newline +
      "    Evaluator.main(params);" + newline +
      "  }" + newline +
      "}" + newline;
  }

  /**
   * Creates and returns a classpath value from the specified files. The value contains the
   * absolute filenames separated by <code>':'</code>.
   */

  protected static final String makeClassPath (File... files)
  {
    StringBuilder buffer = new StringBuilder(files[0].getAbsolutePath());
    for (int i = 1; i < files.length; i++)
      buffer.append(':').append(files[i].getAbsolutePath());
    return buffer.toString();
  }

  /**
   * 
   */

  protected static String processStderr (String stdout)
  {
    return wrapperPathPattern.matcher(stdout).replaceAll("Wrapper.java");
  }

  /**
   * Creates the marking XML code.
   */

  protected static String createMarking (String userCode,
                                         String explanation,
                                         String stdout, String stderr,
                                         String solution,
                                         Integer counter)
  {
    stderr = processStderr(stderr);
    userCode = addLineNumbers(userCode, counter);
    StringBuilder marking = new StringBuilder();
    marking
      .append("<marking xmlns=\"http://www.mumie.net/xml-namespace/marking\">")
      .append("<answer><codeblock>")
      .append(quoteXML(userCode))
      .append("</codeblock></answer>")
      .append("<explanation><par>")
      .append(quoteXML(explanation))
      .append("</par><par>");

    if ( stdout.length() > 0 )
      marking
        .append("Ausgaben nach stdout:</par><output>")
        .append(quoteXML(stdout))
        .append("</output>");
    else
      marking
        .append("Keine Ausgaben nach stdout</par>");

    marking
      .append("<par>");

    if ( stderr.length() > 0 )
      marking
        .append("Ausgaben nach stderr:</par><output class=\"error\">")
        .append(quoteXML(stderr))
        .append("</output>");
    else
      marking
        .append("Keine Ausgaben nach stderr</par>");

    marking
      .append("</explanation><solution><codeblock>")
      .append(quoteXML(solution))
      .append("</codeblock></solution></marking>");

    return marking.toString();
  }

  /**
   * 
   */

  protected static Node stringToNode (String string)
    throws Exception
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(new InputSource(new StringReader(string)));
    return document.getDocumentElement();
  }

  /**
   * Corrects the user answer. The user answer and all required data are provieded by
   * <code>input</code>. The result of the correction is stored in <code>output</code>
   * 
   */

  public void correct(CocoonEnabledDataSheet input,
                      CocoonEnabledDataSheet output)
    throws ProblemCorrectionException
  {
    try
      {
        // Directories:
        String baseDirName = input.getAsString("common/problem/tempdir");
        File workingDir = new File(baseDirName, UniqueFilenameCreator.newFilename());
        File codeDir = new File(workingDir, "code");
        File toolsDir = new File(baseDirName, "tools");

        try
          {
            IOUtil.createDir(workingDir, true);
            IOUtil.createDir(codeDir, true);

            // Create code:
            String wrapperCode = input.getAsString("common/problem/wrapper");
            String userCode = input.getAsString("user/answer/code");

            Integer counter = whichLineIs(wrapperCode, "@USER_ANSWER@"); //get line were user code will be insert

            wrapperCode = wrapperCode.replace("@USER_ANSWER@", userCode);
            String evaluatorCode = input.getAsString("common/problem/evaluator");
            String evaluatorExecutorCode = createEvaluatorExecutorCode();

            // Write code to files:
            File wrapperCodeFile = new File(codeDir, "Wrapper.java");
            File evaluatorCodeFile = new File(codeDir, "Evaluator.java");
            File evaluatorExecutorCodeFile = new File(codeDir, "EvaluatorExecutor.java");
            IOUtil.writeFile(wrapperCodeFile, wrapperCode);
            IOUtil.writeFile(evaluatorCodeFile, evaluatorCode);
            IOUtil.writeFile(evaluatorExecutorCodeFile, evaluatorExecutorCode);

            // If necessary, create the user class source file:
            File userCodeFile = null;
            String userClassName = input.getAsString("common/problem/class-name");
            if ( userClassName != null )
              {
                userCodeFile = new File(codeDir, userClassName + ".java");
                IOUtil.writeFile(userCodeFile, userCode);
              }

            // Utility jar files:
            File utilJarFile = new File(toolsDir, "mumie-japs-corrutil.jar");
            File localUtilJarFile = new File(toolsDir, "mumie-japs-local-corrutil.jar");

            // Class path:
            String classpath = makeClassPath(codeDir, utilJarFile, localUtilJarFile);

            // Init variable parts of the marking:
            String explanation;
            String stdout;
            String stderr;

            // Init score:
            float score;

            // Compile:
            ProcessResult compileResult = ProcessUtil.exec
              (workingDir, COMPILE_TIME_LIMIT,
               filterNull
               ("javac",
                "-classpath", classpath,
                (userCodeFile != null ? userCodeFile.getAbsolutePath() : null),
                wrapperCodeFile.getAbsolutePath(),
                evaluatorCodeFile.getAbsolutePath(),
                evaluatorExecutorCodeFile.getAbsolutePath()));

            if ( compileResult.getExitValue() != 0 || compileResult.timeLimitExceeded() )
              {
                explanation =
                  (compileResult.timeLimitExceeded()
                   ? "Die Kompilierung Ihres Codes wurde wegen Zeit\u00FCberschreitung abgebrochen"
                   : "Ihr Code hat einen Kompilierfehler verursacht");
                stdout = compileResult.getStdout();
                stderr = compileResult.getStderr();
                score = 0;
              }
            else
              {
                // Run:
                ProcessResult runResult = ProcessUtil.exec
                  (workingDir, RUN_TIME_LIMIT,
                   "java",
                   "-classpath", classpath,
                   "EvaluatorExecutor");

                int exitValue = runResult.getExitValue();
                explanation =
                  (runResult.timeLimitExceeded()
                   ? "Die Ausf\u00FChrung Ihres Codes wurde wegen Zeit\u00FCberschreitung abgebrochen"
                   : input.getAsString("common/problem/explanation/status-" + exitValue));
                stdout = runResult.getStdout();
                stderr = runResult.getStderr();
                score =
                  (runResult.timeLimitExceeded()
                   ? 0
                   : input.getAsFloat("common/problem/score/status-" + exitValue));
              }

            // Create marking:
            String marking = createMarking
              (userCode, explanation, stdout, stderr,
               input.getAsString("common/problem/solution"), counter);

            // Add marking to output datasheet:
            Node markingNode = stringToNode(marking);
            markingNode = output.getDocument().importNode(markingNode, true);
            output.put("user/marking", markingNode);

            // Add score to outputDataSheet:
            output.put("user/meta/score", score);
          }
        finally
          {
            if ( workingDir.exists() ) IOUtil.deleteDir(workingDir);
          }
      }
    catch (Exception exception)
      {
        throw new ProblemCorrectionException(exception);
      }
  }

  /**
   * Returns a copy of the specified array with the elements that are null removed.
   */

  protected static String[] filterNull (String... inputStrings)
  {
    List<String> outputStrings = new ArrayList<String>(inputStrings.length);
    for (String str : inputStrings)
      {
        if ( str != null ) outputStrings.add(str);
      }
    return outputStrings.toArray(new String[outputStrings.size()]);
  }

  /**
   * Adds Linenumber after each newline
   */

    public static String addLineNumbers(String code, Integer start)
  {
    String newline = System.getProperty("line.separator");
    String array[] = code.split(newline);
    String erg = "";
    for (int i = 0; i < array.length; i ++ )
    {
      
      array[i] = start + ": \t" + array[i] + newline;
      erg += array[i];
      start++;
    }
    
    return erg;
  }


  /**
   * Returns Linenumber of the searched String 
   */

  public static Integer whichLineIs(String code, String search)
  {
    int index = code.indexOf(search);
    int counter = 0;
    char[] countArray = code.toCharArray(); 
    for (int i = 0; i < index; i ++ )
    {
      if( '\n' == countArray[i])
        counter++;
      
    }
    
    return counter + 1 ;
    
  }
}

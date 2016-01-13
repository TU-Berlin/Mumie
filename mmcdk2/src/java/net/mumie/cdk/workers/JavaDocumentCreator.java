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

package net.mumie.cdk.workers;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.mumie.cdk.CdkConfigParam;
import net.mumie.cdk.CdkInitException;
import net.mumie.cdk.io.CdkFile;
import net.mumie.cdk.util.JavaMetatag;
import net.mumie.cdk.util.JavaMetatagParser;
import net.mumie.cdk.util.JavaMetatags;
import net.mumie.cocoon.checkin.Master;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.FileRole;
import net.mumie.cocoon.notions.Nature;
import net.mumie.cocoon.notions.XMLNamespace;
import net.mumie.util.io.IOUtil;
import net.mumie.util.io.PipeThread;

/**
 * Creates master and content files from Java source files.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: JavaDocumentCreator.java,v 1.25 2009/10/07 22:36:53 rassy Exp $</code>
 */

public class JavaDocumentCreator extends CdkWorker
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The metatag parser of this worker.
   */

  protected JavaMetatagParser metatagParser = new JavaMetatagParser();

  /**
   * The external process startet by this worker (javac, jar, jarsigner), or
   * <code>null</code> if none is currently running.
   */

  protected Process process = null;

  /**
   * The temporary directory used for compiling und building the jar.
   */

  protected File tempDir = null;

  /**
   * A file filter excluding CVS administrative directories. This are directories the name
   * of which is <code>"CVS"</code>.
   */

  protected FileFilter noCVSFiles = new FileFilter ()
    {
      public boolean accept (File file)
      {
        return !file.getName().equals("CVS");
      }
    };

  // --------------------------------------------------------------------------------
  // Calling external commands
  // --------------------------------------------------------------------------------

  /**
   * Executes the specified command line in the specified directory.
   */

  protected void exec (String... cmdline)
    throws Exception
  {
    // Launch process:
    this.process = Runtime.getRuntime().exec(cmdline, null, this.directory);
    
    // Redirect stdout and stderr:
    PipeThread outThread = new PipeThread(this.process.getInputStream(), this.out);
    PipeThread errThread = new PipeThread(this.process.getErrorStream(), this.err);
    
    // Wait for the process to finish:
    this.process.waitFor();

    // Wait for the PipeThreads to finish:
    outThread.join();
    errThread.join();

    // Check if errors occurred in the PipeThreads:
    outThread.checkError();
    errThread.checkError();

    // Check process exit value:
    int exitValue = this.process.exitValue();
    if ( exitValue != 0 )
      throw new JavaDocumentCreationException
        (cmdline[0] = " returned with code " + exitValue);
  }

  // --------------------------------------------------------------------------------
  // Building the class path
  // --------------------------------------------------------------------------------

  /**
   * Recursively adds all jars and applets referenced as components in the specified master
   * file to the specified list. The jars and applets are added as their content files given
   * as {@link CdkFile CdkFile} objects. "Recursively" means that the jar and/or applet
   * components ot the components are added as well, and so forth.
   */

  protected static void scanClasspath (CdkFile masterFile, List<File> classpath)
    throws Exception
  {
    for (Master component : masterFile.getMaster().getComponents())
      {
        int nature = component.getNature();
        int type = component.getType();
        if ( nature == Nature.DOCUMENT && ( type == DocType.APPLET || type == DocType.JAR ) )
          {
            CdkFile componentMasterFile = new CdkFile(component.getPath());
            CdkFile componentContentFile = componentMasterFile.getContentFile();
            if ( !classpath.contains(componentContentFile) ) 
              classpath.add(componentContentFile);
            scanClasspath(componentMasterFile, classpath);
          }
      }
  }

  /**
   * Adds all build classpath items found in the specified matainfos to the specified
   * list. Build classpath items are declared by the
   * {@link JavaMetatag#BUILD_CLASSPATH BUILD_CLASSPATH} javadoc tag.
   */

  protected static void scanClasspath (JavaMetatags metatags, List<File> classpath)
    throws Exception
  {
    String javaLibDir = null;
    for (String path : metatags.getAsList(JavaMetatag.BUILD_CLASSPATH))
      {
        if ( javaLibDir == null )
          javaLibDir =
            IOUtil.concatPaths(getConfigParam(CdkConfigParam.INSTALL_PREFIX), "lib", "java");
        File file = new File(javaLibDir, path);
        if ( !classpath.contains(file) )
          classpath.add(file);
      }
  }

  /**
   * Builds a classpath string form the specified list. This is done by concatenating
   * the entries of the list and separate them by
   * {@link File#pathSeparator File.pathSeparator}.
   */

  protected static String classpathToString (List<File> classpath)
  {
    StringBuffer buffer = new StringBuffer();
    boolean first = true;
    for (File file : classpath)
      {
        if ( !first )
          buffer.append(File.pathSeparator);
        buffer.append(file.getPath());
        first = false;
      }
    return buffer.toString();
  }

  // --------------------------------------------------------------------------------
  // Creating meta and content files
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Creates the master and content files for the specifies source file. If the
   *   <code>force</code> flag is true, the target files are created even if they exist and 
   *   are up-to-date. Otherwise, the are created only if inexistent or outdated. If the
   *   <code>deleteTmp</code> flag is true, the temporary directory where the compiler places
   *   its output is deleted after the target files have been created. Otherwise, it is kept.
   * </p>
   * <p>
   *   Implementation note: The method first checks the document type stated in the source
   *   file. If the type is "applet" or "java_class", the task of creating the target files
   *   is delegated to the type-specific methods
   *     {@link #createAppletDocument createAppletDocument}
   *   and
   *     {@link #createJavaClassDocument createJavaClassDocument},
   *   respectively. For any other document type, an exception is thrown.
   * </p>
   *
   * @throws JavaDocumentCreationException if something goes wrong. All exceptions which may
   *   occur are wrapped by this.
   */

  public void createDocument (CdkFile sourceFile, boolean force, boolean deleteTmp)
    throws JavaDocumentCreationException
  {
    final String APPLET = DocType.nameFor(DocType.APPLET);
    final String JAVA_CLASS = DocType.nameFor(DocType.JAVA_CLASS);
    try
      {
        this.msgln(sourceFile + ": ");

        // Parse source file for metatags:
        JavaMetatags metatags =  this.metatagParser.parse(sourceFile);

        String typeName = metatags.getAsString(JavaMetatag.TYPE);
        if ( typeName.equals(APPLET) )
          this.createAppletDocument(sourceFile, metatags, force, deleteTmp);
        else if ( typeName.equals(JAVA_CLASS) )
          this.createJavaClassDocument(sourceFile, metatags, force, deleteTmp);
        else
          throw new JavaDocumentCreationException
            ("Inapproriate document type: " + typeName +
             " (can only process " + APPLET + " and " + JAVA_CLASS + ")");
      }
    catch (Exception exception)
      {
        throw new JavaDocumentCreationException(exception);
      }
  }

  /**
   * <p>
   *   Creates the master and content files for several source files. For of the specified
   *   files, the following rules apply: 
   *   <ul>
   *     <li>
   *       If the file is a Java source file, the corresponding master and content files are
   *       created.
   *     </li>
   *     <li>
   *       Otherwise, if the file is a directory and <code>recursive</code> or
   *       <code>firstLevel</code> is true, the method is applied to the files in the
   *       directory. All arguments except <code>files</code> and <code>firstLevel</code>
   *       are inherited. The latter is set to false.
   *     </li>
   *     <li>
   *       Otherwise, if <code>firstLevel</code> is true, an exception is thrown.
   *     </li>
   *     <li>
   *       Otherwise the file is ignored.
   *     </li>
   *   </ul>
   *   The parameters <code>force</code> and <code>deleteTmp</code> have the same meaning as
   *   with {@link #createDocument createDocument}. 
   * </p>
   * <p>
   *   The main purpose of this method is to implement the public method with the same name,
   *   {@link #createDocuments(CdkFile[],boolean,boolean,boolean) createDocuments}.
   *   The latter is defined as
   *
   *   <pre>  createDocuments(files, force, deleteTmp, recursive, true)</pre>
   *
   *   By the rules above, it is guaranteed that the public method can be applied to any
   *   files and behaves in an intuitive way: for Java source files, the corresponding
   *   targets are created; for directories, all Java source files in the directory are
   *   processed; subdirectories are processed recursivly if the <code>recursivly</code>
   *   flag is set.
   * </p>
   */

  protected void createDocuments (CdkFile[] files,
                                  boolean force,
                                  boolean deleteTmp,
                                  boolean recursive,
                                  boolean firstLevel)
    throws Exception
  {
    for (CdkFile file : files)
      {
        if ( file.isSourceFile() && file.getTypeSuffix().equals("java") )
          this.createDocument(file, force, deleteTmp);
        else if ( file.isDirectory() && ( recursive || firstLevel ) )
          this.createDocuments
            (file.listCdkFiles(), force, deleteTmp, recursive, false);
        else if ( firstLevel )
          throw new IllegalArgumentException("Not a Java source file: " + file);
      }
  }

  /**
   * Creates the master and content files for several source files. The specified files are
   * treated as follows: For Java source files files, the corresponding targets are created;
   * for directories, all master files in the directory are processed. Subdirectories are
   * processed recursivly if the <code>recursivly</code> flag is set. The parameters
   * <code>force</code> and <code>deleteTmp</code> have the same meaning as with
   * {@link #createDocument createDocument}.
   *
   * @see #createDocuments(CdkFile[],boolean,boolean,boolean,boolean)
   *   createDocuments(files, force, deleteTmp recursive, firstLevel)
   */

  public void createDocuments (CdkFile[] files,
                               boolean force,
                               boolean deleteTmp,
                               boolean recursive)
    throws Exception
  {
    this.createDocuments(files, force, deleteTmp, recursive, true);
  }


  /**
   * Creates a document of type {@link DocType#APPLET APPLET} from the specified
   * source file.
   */

  public void createAppletDocument (CdkFile sourceFile,
                                    JavaMetatags metatags,
                                    boolean force,
                                    boolean deleteTmp)
    throws Exception
  {
    final String CONTENT_SUFFIX = FileRole.CONTENT_SUFFIX + ".jar";
    final String FILES_SUFFIX = "files";

    CdkFile masterFile = null;
    CdkFile contentFile = null;
    CdkFile tmpDir = null;

    try
      {
        // File objects needed:
        masterFile = sourceFile.getMasterFile();
        contentFile = masterFile.replaceSuffix(CONTENT_SUFFIX);
        tmpDir = sourceFile.replaceSuffix("tmp");
        File buildDir = new File(tmpDir, "build");
        File filesDir = sourceFile.replaceSuffix(FILES_SUFFIX);
        File javaFile = new File(tmpDir, createJavaFileName(sourceFile));
        File filesTargetDir = new File
          (buildDir, qualifiedNameToPath(metatags.getAsString(JavaMetatag.QUALIFIED_NAME)));

        // (Re)build master and content files if necessary:
        if ( force || checkIfOutdated(sourceFile, filesDir, masterFile, contentFile) )
          {
            // Create master file:
            this.msgln("Creating master ...");
            this.createAppletMasterFile(metatags, masterFile);

            // Create classpath for compiling:
            List<File> classpath = new ArrayList<File>();
            scanClasspath(masterFile, classpath);
            scanClasspath(metatags, classpath);

            // Prepare temporary dir:
            IOUtil.deleteDir(tmpDir);
            IOUtil.createDir(tmpDir, false);
            IOUtil.createDir(buildDir, false);
            IOUtil.copyFile(sourceFile, javaFile, false);

            // Compile:
            this.msgln("Compiling ...");
            this.exec
              (getConfigParam(CdkConfigParam.JAVAC_CMD, "javac"),
               "-classpath", classpathToString(classpath),
               "-g:lines",
               "-d", buildDir.getPath(),
               javaFile.getPath());

            if ( filesDir.exists() )
              {
                // Copy additional files:
                this.msgln("Copying additional files ...");
                IOUtil.copyFile(filesDir, filesTargetDir, true, this.noCVSFiles);
              }

            // Jar:
            this.msgln("Jaring ...");
            this.exec
              (composeCmdline
               (getConfigParam(CdkConfigParam.JAR_CMD, "jar"),
                "cf", contentFile.getPath(),
                scanJarEntries(buildDir, true)));

            // Sign if necessary:
            if ( metatags.getAsBoolean(JavaMetatag.SIGN, false) )
              {
                this.msgln("Signing ...");
                this.exec
                  (getConfigParam(CdkConfigParam.JARSIGNER_CMD, "jarsigner"),
                   "-keystore", getConfigParam(CdkConfigParam.JARSIGN_KEYSTORE, getDefaultKeystore()),
                   "-storepass", getConfigParam(CdkConfigParam.JARSIGN_KEYSTORE_PASSWORD),
                   contentFile.getPath(),
                   getConfigParam(CdkConfigParam.JARSIGN_KEY_ALIAS));
              }

            this.msgln("Done");
          }
        else
          this.msgln("Up-to-date");

        // Remove temp dir if necessary:
        if ( deleteTmp )
          IOUtil.deleteDir(tmpDir);
      }
    finally
      {
        if ( masterFile.exists() && !contentFile.exists() )
          IOUtil.deleteFile(masterFile);
        if ( deleteTmp && tmpDir.exists() )
          IOUtil.deleteDir(tmpDir);
      }
  }

  /**
   * Creates a document of type {@link DocType#JAVA_CLASS JAVA_CLASS} from the specified
   * source file.
   */

  public void createJavaClassDocument (CdkFile sourceFile,
                                       JavaMetatags metatags,
                                       boolean force,
                                       boolean deleteTmp)
    throws Exception
  {
    final String CONTENT_SUFFIX = FileRole.CONTENT_SUFFIX + ".class";

    CdkFile masterFile = null;
    CdkFile contentFile = null;
    CdkFile tmpDir = null;

    try
      {
        // File objects needed:
        masterFile = sourceFile.getMasterFile();
        contentFile = masterFile.replaceSuffix(CONTENT_SUFFIX);
        tmpDir = sourceFile.replaceSuffix("tmp");
        File buildDir = new File(tmpDir, "build");
        File javaFile = new File(tmpDir, createJavaFileName(sourceFile));

        // (Re)build master and content files if necessary:
        if ( force
             || IOUtil.checkIfOutdated(masterFile, sourceFile)
             || IOUtil.checkIfOutdated(contentFile, sourceFile) )
          {
            // Create master file:
            this.msgln("Creating master ...");
            this.createJavaClassMasterFile(metatags, masterFile);

            // Create classpath for compiling:
            List<File> classpath = new ArrayList<File>();
            scanClasspath(metatags, classpath);

            // Prepare temporary dir:
            IOUtil.deleteDir(tmpDir);
            IOUtil.createDir(tmpDir, false);
            IOUtil.createDir(buildDir, false);
            IOUtil.copyFile(sourceFile, javaFile, false);

            // Compile:
            this.msgln("Compiling ...");
            this.exec
              (getConfigParam(CdkConfigParam.JAVAC_CMD, "javac"),
               "-classpath", classpathToString(classpath),
               "-g:lines",
               "-d", buildDir.getPath(),
               javaFile.getPath());

            // Get class file:
            List<File> classFiles = scanClassFiles(buildDir);
            if ( classFiles.size() == 0 )
              throw new JavaDocumentCreationException
                ("No class file found after compilation");
            if ( classFiles.size() > 1 )
              throw new JavaDocumentCreationException
                ("Multiple class files found after compilation");

            // Copy class file to content file:
            IOUtil.copyFile(classFiles.get(0), contentFile);

            this.msgln("Done");
          }
        else
          this.msgln("Up-to-date");

        // Remove temp dir if necessary:
        if ( deleteTmp )
          {
            IOUtil.deleteDir(tmpDir);
          }
      }
    finally
      {
        if ( masterFile.exists() && !contentFile.exists() )
          {
            IOUtil.deleteFile(masterFile);
            if ( tmpDir.exists() )
              IOUtil.deleteDir(tmpDir);
          }
      }
  }

  /**
   * Creates the master file of a document of type "applet".
   */

  protected void createAppletMasterFile (JavaMetatags metatags, CdkFile masterFile)
    throws Exception
  {
    // Get data:
    String fallbackName = metatags.getAsString(JavaMetatag.FALLBACK_NAME);
    String name = metatags.getAsString(JavaMetatag.NAME, fallbackName);
    String qualifiedName = metatags.getAsString(JavaMetatag.QUALIFIED_NAME);
    String description = metatags.getAsString(JavaMetatag.DESCRIPTION, "");
    String copyright = metatags.getAsString(JavaMetatag.COPYRIGHT, "");
    String infoPagePath = metatags.getAsString(JavaMetatag.INFO_PAGE, null);
    String thumbnailPath = metatags.getAsString(JavaMetatag.THUMBNAIL, null);
    String changelog = metatags.getAsString(JavaMetatag.CHANGELOG, "");
    int width = metatags.getAsInt(JavaMetatag.WIDTH, true, 400);
    int height = metatags.getAsInt(JavaMetatag.HEIGHT, true, 400);
    List<String> requiredJars = metatags.getAsList(JavaMetatag.REQUIRE_JAR, false); 
    List<String> requiredApplets = metatags.getAsList(JavaMetatag.REQUIRE_APPLET, false);

    // Writing the master file:
    PrintStream masterOut = new PrintStream(new FileOutputStream(masterFile));
    masterOut.println("<?xml version=\"1.0\" encoding=\"ASCII\"?>");
    masterOut.println("<mumie:applet");
    masterOut.println("  xmlns:mumie=\"http://www.mumie.net/xml-namespace/document/metainfo\">");
    masterOut.println("  <mumie:name>" + name + "</mumie:name>");
    masterOut.println("  <mumie:qualified_name>" + qualifiedName + "</mumie:qualified_name>");
    masterOut.println("  <mumie:description>");
    masterOut.println("    " + description + "");
    masterOut.println("  </mumie:description>");
    if ( infoPagePath != null )
      masterOut.println("  <mumie:info_page path=\"" + infoPagePath + "\"/>");
    if ( thumbnailPath != null )
      masterOut.println("  <mumie:thumbnail path=\"" + thumbnailPath + "\"/>");
    masterOut.println("  <mumie:changelog>");
    masterOut.println("    " + changelog + "");
    masterOut.println("  </mumie:changelog>");
    masterOut.println("  <mumie:copyright>");
    masterOut.println("    " + copyright + "");
    masterOut.println("  </mumie:copyright>");
    masterOut.println("  <mumie:width value=\"" + width + "\"/>");
    masterOut.println("  <mumie:height value=\"" + height + "\"/>");
    masterOut.println("  <mumie:components>");
    if ( requiredJars != null )
      {
        for (String path : requiredJars)
          masterOut.println("    <mumie:jar path=\"" + path + "\"/>");
      }
    if ( requiredApplets != null )
      {
        for (String path : requiredApplets)
          masterOut.println("    <mumie:applet path=\"" + path + "\"/>");
      }
    masterOut.println("  </mumie:components>");
    masterOut.println("  <mumie:content_type type=\"application\" subtype=\"x-java-archive\"/>");
    masterOut.println("  <mumie:source_type type=\"text\" subtype=\"java\"/>");
    masterOut.println("</mumie:applet>");
    masterOut.flush();
    masterOut.close();
  }

  /**
   * Creates the master file of a document of type "java_class".
   */

  public void createJavaClassMasterFile (JavaMetatags metatags, CdkFile masterFile)
    throws Exception
  {
    // Get metainfos:
    String fallbackName = metatags.getAsString(JavaMetatag.FALLBACK_NAME);
    String name = metatags.getAsString(JavaMetatag.NAME, fallbackName);
    String qualifiedName = metatags.getAsString(JavaMetatag.QUALIFIED_NAME);
    String category = metatags.getAsString(JavaMetatag.CATEGORY);
    String description = metatags.getAsString(JavaMetatag.DESCRIPTION, "");
    String copyright = metatags.getAsString(JavaMetatag.COPYRIGHT, "");
    String changelog = metatags.getAsString(JavaMetatag.CHANGELOG, "");

    // Checking:
    if ( Category.codeFor(category) == Category.UNDEFINED )
      throw new JavaDocumentCreationException("Unkown category keyword: " + category);

    // Writing the master file:
    PrintStream masterOut = new PrintStream(new FileOutputStream(masterFile));
    masterOut.println("<?xml version=\"1.0\" encoding=\"ASCII\"?>");
    masterOut.println("<mumie:java_class");
    masterOut.println("  xmlns:mumie=\"http://www.mumie.net/xml-namespace/document/metainfo\">");
    masterOut.println("  <mumie:name>" + name + "</mumie:name>");
    masterOut.println("  <mumie:qualified_name>" + qualifiedName + "</mumie:qualified_name>");
    masterOut.println("  <mumie:description>");
    masterOut.println("    " + description + "");
    masterOut.println("  </mumie:description>");
    masterOut.println("  <mumie:changelog>");
    masterOut.println("    " + changelog + "");
    masterOut.println("  </mumie:changelog>");
    masterOut.println("  <mumie:copyright>");
    masterOut.println("    " + copyright + "");
    masterOut.println("  </mumie:copyright>");
    masterOut.println("  <mumie:category name=\"" + category + "\"/>");
    masterOut.println("  <mumie:content_type type=\"application\" subtype=\"x-java-vm\"/>");
    masterOut.println("  <mumie:source_type type=\"text\" subtype=\"java\"/>");
    masterOut.println("</mumie:java_class>");
    masterOut.flush();
    masterOut.close();
  }

  // --------------------------------------------------------------------------------
  // Stopping
  // --------------------------------------------------------------------------------

  /**
   * Sets the {@link #stop stop} flag to <code>true</code> and stops the external process if
   * currently running.
   */

  public synchronized void stop ()
  {
    this.stop = true;
    if ( this.process != null )
      {
        try
          {
            this.process.destroy();
          }
        catch (Exception exception)
          {
            // Ignored
          }
      }
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Returns the Java source filename for the specified cdk file (e.g., a master file).
   */

  protected static String createJavaFileName (CdkFile file)
  {
    return file.getPureName() + ".java";
  }

  /**
   * Returns the entries of the specified directory as filenames without directory path.
   */

  protected static String[] getDirEntries (File dir)
  {
    File[] files = dir.listFiles();
    String[] entries = new String[files.length];
    for (int i = 0; i < files.length; i++)
      entries[i] = files[i].getName();
    return entries;
  }

  /**
   * Scans the specified directory recursivley for Java class files and stores them in the  
   * specified list.
   */

  protected static void scanClassFiles (File dir, List<File> classFiles)
  {
    for (File file : dir.listFiles())
      {
        if ( file.isDirectory() )
          scanClassFiles(file, classFiles);
        else if ( file.getName().endsWith(".class") )
          classFiles.add(file);
      }
  }

  /**
   * Scans the specified directory recursivley for Java class files and returns them as a
   * list.
   */

  protected static List<File> scanClassFiles (File dir)
  {
    List<File> classFiles = new ArrayList<File>();
    scanClassFiles(dir, classFiles);
    return classFiles;
  }

  /**
   * Scans the specified directory recursivley for files and stores them in the  
   * specified list.
   */

  protected static void scanFiles (File dir, List<File> classFiles)
  {
    for (File file : dir.listFiles())
      {
        if ( file.isDirectory() )
          scanFiles(file, classFiles);
        else
          classFiles.add(file);
      }
  }

  /**
   * <p>
   *   Auxiliary method. Scans the specified directory recursivly for files and returns them
   *   in a form suitable for adding them to the command line of the jar program. For each
   *   file found, the parameters <code>"-C" <var>prefix</var> <var>rel_path</var></code> are
   *   added to the list returned afterwards, where <code><var>prefix</var></code> is the path
   *   of the directory and <var>rel_path</var> the path of the file relative to the
   *   directory.
   * </p>
   * <p>
   *   If the boolean flag <code>mustExist</code> is true, an exception is thrown if the
   *   directory doeas not exist. It it is false, an empty string list is returned.
   * </p>
   */

  protected static List<String> scanJarEntries (File dir, boolean mustExist)
  {
    if ( !dir.exists() )
      {
        if ( mustExist )
          throw new IllegalStateException(dir + " does not exists");
        else
          return new ArrayList<String>();
      }

    // Get files:
    List<File> files = new ArrayList<File>();
    scanFiles(dir, files);

    // Setup filename prefix:
    String prefix = dir.getPath();
    if ( !prefix.endsWith(File.separator) ) prefix += File.separator;
    int offset = prefix.length();

    // Create jar entry parameters:
    List<String> entries = new ArrayList<String>();
    for (File file : files)
      {
        String filename = file.getPath();
        if ( filename.startsWith(prefix) && filename.length() > offset )
          {
            entries.add("-C");
            entries.add(prefix);
            entries.add(filename.substring(offset));
          }
        else
          throw new IllegalArgumentException
            ("sacnJarEntries: " + filename + " is not a descendant of " + prefix);
      }

    return entries;
  }

  /**
   * Returns the specified configuration parameter. The parameter is simply looked up in the
   * system properties. If the parameter is not set (i.e., the system propertie is not set),
   * an exception is thrown.
   */

  protected static final String getConfigParam (String name)
    throws CdkInitException
  {
    String value = System.getProperty(name);
    if ( value == null )
      throw new CdkInitException("System property not set: " + name);
    return value;
  }

  /**
   * Returns the specified configuration parameter. The parameter is simply looked up in the
   * system properties. If the parameter is not set (i.e., the system propertie is not set),
   * the sprecified default value is returned.
   * an exception is thrown.
   */

  protected static final String getConfigParam (String name, String defaultValue)
    throws CdkInitException
  {
    return System.getProperty(name, defaultValue);
  }

  /**
   * Auxiliary method; returns an String array containing the specified Strings and the
   * Strings in the specified lists.
   */


  protected static String[] composeCmdline (String item0,
                                            String item1,
                                            String item2,
                                            List<String> restItems)
  {
    String[] cmdline = new String[3+restItems.size()];
    cmdline[0] = item0;
    cmdline[1] = item1;
    cmdline[2] = item2;
    int i = 2;
    for (String item : restItems)
      cmdline[++i] = item;
    return cmdline;
  }

  /**
   * Special method for applets to check if the master and content files are outdated.
   */

  protected static boolean checkIfOutdated (File sourceFile,
                                            File filesDir,
                                            File masterFile,
                                            File contentFile)
  {
    if ( !masterFile.exists() || !contentFile.exists() )
      return true;

    long sourceLastModified = sourceFile.lastModified();
    long masterLastModified = masterFile.lastModified();
    long contentLastModified = contentFile.lastModified();

    if ( masterLastModified <= sourceLastModified ||
         contentLastModified <= sourceLastModified )
      return true;
    else if ( !filesDir.exists() )
      return false;
    else
      {
        long filesDirLastModified = filesDir.lastModified();
        if ( masterLastModified <= filesDirLastModified ||
             contentLastModified <= filesDirLastModified )
          return true;
        else
          return checkIfOutdated(masterLastModified, contentLastModified, filesDir);
      }
  }

  /**
   * Auxiliary method to implement
   * {@link #checkIfOutdated(File,File,File,File) checkIfOutdated(File,File,File,File)}.
   */

  protected static boolean checkIfOutdated (long masterLastModified,
                                            long contentLastModified,
                                            File dir)
  {
    for (File file : dir.listFiles())
      {
        long fileLastModified = file.lastModified();
        if ( masterLastModified <= fileLastModified || 
             contentLastModified <= fileLastModified )
          return true;
        if ( file.isDirectory() &&
             checkIfOutdated(masterLastModified, contentLastModified, file) )
          return true;
      }
    return false;
  }

  /**
   * 
   */

  protected static String qualifiedNameToPath (String qualifiedName)
  {
    int lastDot = qualifiedName.lastIndexOf('.');
    if ( lastDot == -1 ) return null;
    char[] chars = qualifiedName.substring(0, lastDot).toCharArray();
    for (int i = 0; i < chars.length; i++)
      {
        if ( chars[i] == '.' ) chars[i] = File.separatorChar;
      }
    return new String(chars);
  }

  /**
   * Returns the absolute path of the default keystore
   */

  protected static String getDefaultKeystore ()
  {
    return (new File(System.getProperty("user.home"), ".keystore")).getAbsolutePath();
  }
}

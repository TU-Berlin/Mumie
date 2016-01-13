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

package net.mumie.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Stack;
import java.util.StringTokenizer;
import java.io.FileFilter;
import java.util.List;
import java.util.ArrayList;

/**
 * Static i/o utilities.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: IOUtil.java,v 1.18 2009/01/28 10:52:32 rassy Exp $</code>
 */

public class IOUtil
{
  // --------------------------------------------------------------------------------
  // Reading files
  // --------------------------------------------------------------------------------

  /**
   * Returns the content of the specified file as a string.
   */

  public static String readFile (File file)
    throws IOException
  {
    FileReader fileReader = new FileReader(file);
    char[] ioBuffer = new char[1024];
    StringBuilder content = new StringBuilder();
    int length = -1;
    while ( (length = fileReader.read(ioBuffer)) != -1 )
      content.append(ioBuffer, 0, length);
    return content.toString();
  }

  /**
   * Returns the content of the file with the specified name as a string.
   */

  public static String readFile (String filename)
    throws IOException
  {
    return readFile(new File(filename));
  }

  // --------------------------------------------------------------------------------
  // Writing files
  // --------------------------------------------------------------------------------

  /**
   * Writes the specified string to the specified file.
   */

  public static void writeFile (File file, String string)
    throws IOException
  {
    FileWriter writer = new FileWriter(file);
    writer.write(string);
    writer.flush();
    writer.close();
  }

  /**
   * Writes the specified string to the file with the specified name.
   *
   * @param filename the name of the file
   * @param string the string to write
   */

  public static void writeFile (String filename, String string)
    throws IOException
  {
    writeFile(new File(filename), string);
  }

  // --------------------------------------------------------------------------------
  // Reading and writing streams
  // --------------------------------------------------------------------------------

  /**
   * Reads all bytes from the specified input stream and writes them to the specified output
   * stream.
   */

  public static void redirect (InputStream in, OutputStream out,
                               boolean closeIn, boolean closeOut)
    throws IOException
  {
    byte[] ioBuffer = new byte[1024];
    int number = -1;
    while ( (number = in.read(ioBuffer)) != -1 )
      out.write(ioBuffer, 0, number);
    out.flush();
    if ( closeIn ) in.close();
    if ( closeOut ) out.close();
  }

  /**
   * Reads all bytes from the specified input stream and discards them. Closes the stream
   * after the last byte was read.
   */

  public static void discard (InputStream in)
    throws IOException
  {
    byte[] ioBuffer = new byte[1024];
    while ( in.read(ioBuffer) != -1 );
    in.close();
  }

  /**
   * Reads all bytes from the specified input stream, discards them, and returns the total
   * number of bytes read. Closes the stream after the last byte was read.
   */

  public static long discardAndGetLength (InputStream in)
    throws IOException
  {
    long totalCount = 0;
    int count = 0;
    byte[] ioBuffer = new byte[1024];
    while ( (count = in.read(ioBuffer)) != -1 )
      totalCount += count;
    in.close();
    return totalCount;
  }

  // --------------------------------------------------------------------------------
  // Copying files
  // --------------------------------------------------------------------------------

  /**
   * <p>
   *   Copies <code>source</code> to <code>target</code>. If <code>target</code>
   *   is a directory, <code>source</code> is copied to a new file inside that directory;
   *   otherwise, <code>target</code> becomes the copy of <code>source</code>. If
   *   <code>createParents</code> is <code>true</code>, parent directories of
   *   <code>target</code> are created if necessary.
   * </p>
   * <p>
   *   If <code>source</code> is a directory, it is copied recursively. In that case, The
   *   files to be copied can be restricted by <code>filter</code>. If <code>filter</code>
   *   is null, all files are copied. Please note: If a directory is excluded by
   *   <code>filter</code>, all its descendent files are not copied, either (this is
   *   slightly different to the behaviour of the {@link #listFiles IOUtil.listFiles}
   *   method).
   * </p>
   */

  public static void copyFile (File source, File target,
                               boolean createParents,
                               FileFilter filter)
    throws IOException
  {
    // At source name to target if we are copying to a directory:
    if ( target.isDirectory() )
      {
	String path = target.getPath();
	String name = source.getName();
	target = new File
	  (path.endsWith(File.separator) ? path + name : path + File.separator + name);
      }

    // Checking parents of target file:
    File parent = target.getParentFile();
    if ( parent != null && !parent.exists() )
      {
	if ( createParents )
          createDir(parent, false);
	else
	  throw new IOException("copyFile: Parent directory does not exist: " + target);
      }

    if ( source.isDirectory() )
      {
        createDir(target, false);
        for (File file : source.listFiles(filter))
          copyFile(file, target, createParents, filter);
      }
    else
      {
        // Input stream for the source:
        FileInputStream in = new FileInputStream(source);

        // Output stream for the target:
        FileOutputStream out = new FileOutputStream(target);

        // Copying:
        byte[] ioBuffer = new byte[1024];
        int number = -1;
        while ( (number = in.read(ioBuffer)) != -1 )
          out.write(ioBuffer, 0, number);
        out.flush();

        // Cleanup:
        in.close();
        out.close();
      }
  }

  /**
   * <p>
   *   Copies <code>source</code> to <code>target</code>. If <code>target</code>
   *   is a directory, <code>source</code> is copied to a new file inside that directory;
   *   otherwise, <code>target</code> becomes the copy of <code>source</code>. If
   *   <code>createParents</code> is <code>true</code>, parent directories of
   *   <code>target</code> are created if necessary. If <code>source</code> is a directory,
   *   it is copied recursively.
   * </p>
   */

  public static void copyFile (File source, File target,
                               boolean createParents)
    throws IOException
  {
    copyFile(source, target, createParents, null);
  }

  /**
   * <p>
   *   Copies <code>source</code> to <code>destination</code>. If <code>destination</code>
   *   is a directory, <code>source</code> is copied to a new file inside that directory;
   *   otherwise, <code>destination</code> becomes the copy of <code>source</code>.
   * </p>
   * <p>
   *   Same as {@link #copyFile(File,File,boolean) copyFile(source, destination, false)}.
   * </p>
   */

  public static void copyFile (File source, File destination)
    throws IOException
  {
    copyFile(source, destination, false);
  }

  /**
   * <p>
   *   Copies file <code>sourceName</code> (the source) to <code>destinationName</code>. If
   *   the latter names a directory, the source is copied to a new file with the same local
   *   name inside that directory; otherwise is is copied to a new file named
   *   <code>destinationName</code>.
   * </p>
   * <p>
   *   If <code>createParents</code> is <code>true</code>, parent directories of
   *   <code>destination</code> are created if necessary.
   * </p>
   *
   * @param sourceName name of the source file
   * @param destinationName name of the destination file
   * @param createParents whether parent directories should be created if necessary
   */

  public static void copyFile (String sourceName, String destinationName, boolean createParents)
    throws IOException
  {
    copyFile(new File(sourceName), new File(destinationName), createParents);
  }

  /**
   * <p>
   *   Copies file <code>sourceName</code> (the source) to <code>destinationName</code>. If
   *   the latter names a directory, the source is copied to a new file with the same local
   *   name inside that directory; otherwise is is copied to a new file named
   *   <code>destinationName</code>.
   * </p>
   * <p>
   *   Same as
   *{@link #copyFile(String,String,boolean) copyFile(sourceName, destinationName, false)}.
   * </p>
   */

  public static void copyFile (String sourceName, String destinationName)
    throws IOException
  {
    copyFile(sourceName, destinationName, false);
  }

  /**
   * Resolves <code>".."</code> and <code>"."</code> parts in <code>filename</code> and
   * returns the result. Example:
   * <pre>
   *   Util.normalizeFilename("foo/bar/../bazz")
   * </pre>
   * yields <code>"foo/bazz"</code>.
   */

  public static String normalizeFilename (String filename)
  {
    try
      {
	Stack parts = new Stack();
	StringTokenizer tokenizer = new StringTokenizer(filename, File.separator);
	while ( tokenizer.hasMoreTokens() )
	  {
	    String part = tokenizer.nextToken();
	    if ( part.equals("..") && !parts.empty() )
	      parts.pop();
	    else if ( part.equals(".") )
	      /* do nothing */ ;
	    else
	      parts.push(part);
	  }
	StringBuffer normalizedFilename = new StringBuffer();
	Iterator iterator = parts.iterator();
	boolean first = true;
	while ( iterator.hasNext() )
	  {
	    if ( !first || filename.startsWith(File.separator) )
	      normalizedFilename.append(File.separator);
	    normalizedFilename.append((String)iterator.next());
	    first = false;
	  }
	return normalizedFilename.toString();
      }
    catch (Exception exception)
      {
	throw new IllegalArgumentException
	  ("Can not normalize filename: " + filename + ": " + exception.toString());
      }
  }

  /**
   * <p>
   *   Concatenates two path fragments, taking care of leading/trailing path separators.
   *   <code>path1</code> and <code>path2</code> are the first resp. second fragment.
   *   <code>path1</code> may or may not end with a path separator. <code>path2</code>
   *   may or may not start with a path separator. In any case, the resulting string contains
   *   exactly one path separator between the parts specified by <code>path1</code> and
   *   <code>path2</code>. E.g., on Unix systems, regardless whether <code>path1</code> is
   * </p>
   * <quote>
   *     <code>"foo"</code>  or  <code>"foo/"</code>
   * </quote>
   * <p>
   *   and <code>path2</code> is
   * </p>
   * <quote>
   *     <code>"bar"</code>  or  <code>"/bar"</code>,
   * </quote>
   * <p>
   *   the returned string is always
   * <quote>
   *    <code>"foo/bar"</code>
   * </quote>
   */

  public static String concatenatePaths (String path1, String path2)
  {
    if ( ( path1 == null ) || ( path1.length() == 0 ) )
      return path2;
    else if ( path1.endsWith(File.separator) )
      {
	if ( path2.startsWith(File.separator) )
	  return path1.concat(path2.substring(1));
	else
	  return path1.concat(path2);
      }
    else
      {
	if ( path2.startsWith(File.separator) )
	  return path1.concat(path2);
	else
	  return path1.concat(File.separator).concat(path2);
      }
  }

  // --------------------------------------------------------------------------------
  // Creating directories
  // --------------------------------------------------------------------------------

  /**
   * Creates the specified directory. Necessary subdirectories are created, too, if not
   * existing. If <code>failIfExists</code> is true, an exception is thrown if the directory
   * already exists. If <code>failIfExists</code> is false, the method simply returns if the
   * directory already exists. If a file with the same name as the directory exists, but is
   * not a directory, an exception is thrown in any case. Unlike {@link File#mkdirs mkdirs}
   * in class {@link File File}, this method throws an exception if the creation failed,
   * instead of returning false.
   *
   * @throws IOException if the creation of the directory failed, <code>failIfExists</code>
   * is true and the directory already exists, or a non-directory file with the same name
   * exists.
   */

  public static void createDir (File dir, boolean failIfExists)
    throws IOException
  {
    if ( dir.exists() )
      {
        if ( failIfExists )
          throw new IOException
            ("Can not create directory: Directory already exists: " + dir);
        if ( !dir.isDirectory() )
          throw new IOException
            ("Can not create directory: File exists, but is not a directory: " + dir);
      }
    else
      {
        if ( !dir.mkdirs() )
          throw new IOException("Failed to create directory: " + dir);
      }
  }

  /**
   * <p>
   *   Creates the specified directory. Necessary subdirectories are created, too, if not
   *   existing. If <code>failIfExists</code> is true, an exception is thrown if the directory
   *   already exists. If <code>failIfExists</code> is false, the method simply returns if the
   *   directory already exists. If a file with the same name as the directory exists, but is
   *   not a directory, an exception is thrown in any case. Unlike {@link File#mkdirs mkdirs}
   *   in class {@link File File}, this method throws an exception if the creation failed,
   *   instead of returning false.
   * </p>
   * <p>
   *   Same as {@link #createDir(File,boolean) createDir(new File(dirName), failIfExists)}.
   * </p>
   *
   * @throws IOException if the creation of the directory failed, <code>failIfExists</code>
   * is true and the directory already exists, or a non-directory file with the same name
   * exists.
   */

  public static void createDir (String dirName, boolean failIfExists)
    throws IOException
  {
    createDir(new File(dirName), failIfExists);
  }

  // --------------------------------------------------------------------------------
  // Deleting files and directories
  // --------------------------------------------------------------------------------

  /**
   * Deletes the specified file. Unlike {@link File#delete delete} in class
   * {@link File File}, this method throws an exception if the deletion failed instead of
   * returning false.
   *
   * @throws IOException if the file could not be deleted
   */

  public static void deleteFile (File file)
    throws IOException
  {
    if ( !file.delete() )
      throw new IOException("Could not delete file: " + file);
  }

  /**
   * Recursively deletes the specified directory.
   *
   * @throws IllegalArgumentException if the specified file is not a directory
   * @throws IOException if the directory could not be deleted
   */

  public static void deleteDir (File dir)
    throws IOException
  {
    if ( !dir.exists() )
      return;
    if ( !dir.isDirectory() )
      throw new IllegalArgumentException("Not a directory: " + dir);
    for (File file : dir.listFiles())
      {
        if ( file.isDirectory() )
          deleteDir(file);
        else
          deleteFile(file);
      }
    deleteFile(dir);
  }

  // --------------------------------------------------------------------------------
  // Directroy contents
  // --------------------------------------------------------------------------------

  /**
   * Auxiliary method to implement {@link #listFiles listFiles}.
   */

  protected static void scanFiles (File dir,
                                   FileFilter filter,
                                   boolean recursive,
                                   List<File> files)
  {
    if ( !dir.isDirectory() )
      throw new IllegalArgumentException("scanFiles: not a directory: " + dir);
    for (File file : dir.listFiles())
      {
        if ( filter == null || filter.accept(file) )
          files.add(file);
        if ( recursive && file.isDirectory() )
          scanFiles(file, filter, recursive, files);
      }
  }

  /**
   * Returns all files in the specified directory accepted by the specified filter. If
   * <code>recursive</code> is true, subdirectories are processed recursivly; i.e., files in
   * descendant directories are returned, too.
   */

  public static File[] listFiles (File dir, FileFilter filter, boolean recursive)
  {
    List<File> files = new ArrayList<File>();
    scanFiles(dir, filter, recursive, files);
    return files.toArray(new File[files.size()]);
  }

  // --------------------------------------------------------------------------------
  // Concatenating paths
  // --------------------------------------------------------------------------------

  /**
   * Concatenates the specified paths, separated by {@link File#separator File.separator}.
   */

  public static String concatPaths (String... paths)
  {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < paths.length; i++)
      {
        String path = paths[i];

        // Ignore some cases:
        if ( path == null || path.equals("") || path.equals(File.separator) )
          continue;

        // If not the first item, remove a leading file separator:
        if ( i != 0 && path.startsWith(File.separator)
             && path.length() > File.separator.length() )
          path = path.substring(File.separator.length());

        // If not the last item, remove a trailing file separator:
        if ( i != paths.length && path.endsWith(File.separator)
             && path.length() > File.separator.length() )
          path = path.substring(0, path.length()-File.separator.length());

        // Add the item:
        if ( i > 0 ) buffer.append(File.separator);
        buffer.append(path);
      }
    return buffer.toString();
  }

  // --------------------------------------------------------------------------------
  // Comparing file times
  // --------------------------------------------------------------------------------

  /**
   * Checks if the specified "target" file is outdated with respect of the specified
   * "source" file. 
   */

  public static boolean checkIfOutdated (File target, File source)
  {
    if ( source == null )
      throw new NullPointerException("checkIfOutdated: source is null");

    long sourceLastModified = source.lastModified();

    if ( target == null )
      return true;
    else if ( target.isDirectory() )
      return checkIfOutdated(sourceLastModified, target);
    else 
      return ( !target.exists() || target.lastModified() <= sourceLastModified );

    // return ( target == null || target.lastModified() <= source.lastModified() );
  }

  /**
   * Checks if the specified "target" file is outdated with respect of the specified
   * "source" file. 
   */

  public static boolean checkIfOutdated (String targetFilename, String sourceFilename)
  {
    return checkIfOutdated(new File(targetFilename), new File(sourceFilename));
  }

  /**
   * 
   */

  protected static boolean checkIfOutdated (long sourceLastModified, File dir)
  {
    for (File target : dir.listFiles())
      {
        if ( target.lastModified() <= sourceLastModified )
          return true;
        if ( target.isDirectory() && checkIfOutdated(sourceLastModified, target) )
          return true;
      }
    return false;
  }

  // --------------------------------------------------------------------------------
  // Disabled constructor
  // --------------------------------------------------------------------------------

  /**
   * Throws an exception to indicate that this class must not be instanciated.
   */

  private IOUtil ()
  {
    throw new RuntimeException("Class must not be instanciated: " + IOUtil.class.getName());
  }
}

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

package net.mumie.cocoon.corrutil;

import java.io.File;
import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ProgramSecurityManager extends SecurityManager
{
  // ------------------------------------------------------------------------------
  // h1: Global variables and constants
  // ------------------------------------------------------------------------------

  /**
   * List of files (and/or directories) where reading is allowed.
   */

  protected final List<String> readAllowedFiles;

  /**
   * List of files (and/or directories) where writing is allowed.
   */

  protected final List<String> writeAllowedFiles;

  /**
   * List of files (and/or directories) where deleting is allowed.
   */

  protected List<String> deleteAllowedFiles;

  /**
   * The root of the Java runtime environment, terminated by a path separator.
   */

  protected String javaHome;

  // ------------------------------------------------------------------------------
  // h1: Constructor
  // ------------------------------------------------------------------------------

  /**
   * Creates a new instance with lists of files for which reading/writing/deleting is
   * allowed.
   */

  public ProgramSecurityManager (List<String> readAllowedFiles,
                                 List<String> writeAllowedFiles,
                                 List<String> deleteAllowedFiles)
  {
    this.javaHome = getJavaHome();
    this.readAllowedFiles = readAllowedFiles;
    this.writeAllowedFiles = writeAllowedFiles;
    this.deleteAllowedFiles = deleteAllowedFiles;
  }

  /**
   * Creates a new instance.
   */

  public ProgramSecurityManager ()
  {
    this(null, null, null);
  }

  // ------------------------------------------------------------------------------
  // h1: checkXxx methods.
  // ------------------------------------------------------------------------------

  public void checkAccept (String host, int port)
  {
    throw new SecurityException
      ("Program tried to accept socket connection (" + host + ":" + port + ")");
  }

  public void checkConnect (String host, int port)
  {
    throw new SecurityException
      ("Program tried to open socket connection (" + host + ":" + port + ")");
  }

  public void checkConnect (String host, int port, Object context)
  {
    throw new SecurityException
      ("Program tried to open socket connection (" + host + ":" + port + ")");
  }

  public void checkCreateClassLoader ()
  {
    throw new SecurityException("Program tried to create a class loader");
  }

  public void checkDelete (String filename) 
  {
    if ( !this.deleteAllowedFiles.contains(filename) )
      throw new SecurityException("Program tried to delete a file (" + filename + ")");
  }

  public void checkExec (String cmd)
  {
    throw new SecurityException("Program tried to execute an external command (" + cmd + ")");
  }

  public void checkExit (int exitValue) 
  {
    if ( calledFromWrapper() )
      throw new SecurityException("Program tried to call System.exit(int)");
  }

  public void checkLink (String filename) 
  {
    throw new SecurityException("Program tried to link library (" + filename + ")");
  }

  public void checkListen (int port)
  {
    throw new SecurityException("Program tried to listen on a port (" + port + ")");
  }

  public void checkPrintJobAccess ()
  {
    throw new SecurityException("Program tried to initiate a print job request");
  }

  public void checkPropertiesAccess ()
  {
    throw new SecurityException("Program tried to access system properties");
  }

  //    public void checkPropertyAccess (String name)
  //    {
  //      super.checkPropertyAccess(name);
  //      throw new SecurityException("Program tried to access a system property (" + name + ")");
  //    }

  public void checkRead (FileDescriptor descriptor)
  {
    throw new SecurityException
      ("Program tried to read from file descriptor (" + descriptor + ")");
  }

  public void checkRead (String filename)
  {
    if ( calledFromWrapper() && !this.readAllowedFiles.contains(filename) )
      throw new SecurityException("Program tried to read file \"" + filename + "\"");
  }

  public void checkRead (String filename, Object context)
  {
    if ( calledFromWrapper() && !this.readAllowedFiles.contains(filename) )
      throw new SecurityException("Program tried to read file \"" + filename + "\"");
  }

  public void checkSetFactory ()
  {
    throw new SecurityException("Program tried to set a factory");
  }

  public void checkWrite (FileDescriptor descriptor)
  {
    throw new SecurityException
      ("Program tried to write to a file descriptor (" + descriptor + ")");
  }

  public void checkWrite (String filename)
  {
    if ( calledFromWrapper() && !this.writeAllowedFiles.contains(filename) )
      throw new SecurityException
        ("Program tried to write to a file (" + filename + ")");
    
  }

  // ------------------------------------------------------------------------------
  // h1: Auxiliaries
  // ------------------------------------------------------------------------------

  /**
   * Returns the root of the Java runtime environment, terminated by a path separator.
   */

  protected static final String getJavaHome ()
  {
    return System.getProperty("java.home") + System.getProperty("file.separator");
  }

  /**
   * 
   */

  protected static boolean calledFromWrapper ()
  {
    Throwable throwable = new Throwable();
    for (StackTraceElement frame : throwable.getStackTrace())
      {
        if ( frame.getClassName().equals("Wrapper") ) return true;
      }
    return false;
  }

  /**
   * 
   */

  protected boolean allowsRead (String filename)
  {
    if ( filename.startsWith(this.javaHome) )
      return true;

    if ( this.readAllowedFiles.contains(filename) )
      return true;

    return false;
  }

}

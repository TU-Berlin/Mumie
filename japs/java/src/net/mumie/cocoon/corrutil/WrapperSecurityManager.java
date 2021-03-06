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

public class WrapperSecurityManager extends SecurityManager
{
  // ------------------------------------------------------------------------------
  // h1: Global variables and constants
  // ------------------------------------------------------------------------------

  /**
   * The key needed to change the settings of this manager.
   */

  private long key;

  /**
   * Whether a call to {@link System#exit(int) System.exit(int)} is allowed.
   */

  protected boolean systemExitAllowed = false;

  /**
   * List of files (and/or directories) where reading is allowed.
   */

  protected List<String> readAllowedFiles = new ArrayList<String>();

  /**
   * List of files (and/or directories) where writing is allowed.
   */

  protected List<String> writeAllowedFiles = new ArrayList<String>();

  /**
   * List of files (and/or directories) where deleting is allowed.
   */

  protected List<String> deleteAllowedFiles = new ArrayList<String>();

  /**
   * The root of the Java runtime environment, terminated by a path separator.
   */

  protected String javaHome;

  // ------------------------------------------------------------------------------
  // h1: Constructor
  // ------------------------------------------------------------------------------

  /**
   * Creates a new instance with the specified key.
   */

  public WrapperSecurityManager (long key)
  {
    this.key = key;
    this.javaHome = System.getProperty("java.home");
  }

  // ------------------------------------------------------------------------------
  // h1: Controlling whitch is allowed
  // ------------------------------------------------------------------------------

  /**
   * Checks if the passed key is correct.
   */

  protected final void validateKey (long key)
  {
    if ( this.key == 0 )
      throw new SecurityException("Key disabled");

    if ( key != this.key )
      throw new SecurityException("Wrong key");
  }

  /**
   * Allows/disallows calls to {@link System#exit(int) System.exit(int)}
   */

  public void allowSystemExit (long key, boolean systemExitAllowed)
  {
    this.validateKey(key);
    this.systemExitAllowed = systemExitAllowed;
  }

  /**
   * Allows the specified file to be read.
   */

  public void allowReadFile (long key, File file)
  {
    try
      {
        this.validateKey(key);
        String filename = file.getCanonicalPath();
        if ( !this.readAllowedFiles.contains(filename) )
          this.readAllowedFiles.add(filename);
      }
    catch (Exception exception)
      {
        throw new RuntimeException(exception);
      }
  }

  /**
   * Allows the specified file to be written.
   */

  public void allowWriteFile (long key, File file)
  {
    try
      {
        this.validateKey(key);
        String filename = file.getCanonicalPath();
        if ( !this.writeAllowedFiles.contains(filename) )
          this.writeAllowedFiles.add(filename);
      }
    catch (Exception exception)
      {
        throw new RuntimeException(exception);
      }
  }

  /**
   * Allows the specified file to be deleted.
   */

  public void allowDeleteFile (long key, File file)
  {
    try
      {
        this.validateKey(key);
        String filename = file.getCanonicalPath();
        if ( !this.deleteAllowedFiles.contains(filename) )
          this.deleteAllowedFiles.add(filename);
      }
    catch (Exception exception)
      {
        throw new RuntimeException(exception);
      }
  }

  /**
   * Disallows the specified file to be read.
   */

  public void disallowReadFile (long key, File file)
  {
    try
      {
        this.validateKey(key);
        String filename = file.getCanonicalPath();
        if ( this.readAllowedFiles.contains(filename) )
          this.readAllowedFiles.remove(filename);
      }
    catch (Exception exception)
      {
        throw new RuntimeException(exception);
      }
  }

  /**
   * Disallows the specified file to be written.
   */

  public void disallowWriteFile (long key, File file)
  {
    try
      {
        this.validateKey(key);
        String filename = file.getCanonicalPath();
        if ( this.writeAllowedFiles.contains(filename) )
          this.writeAllowedFiles.remove(filename);
      }
    catch (Exception exception)
      {
        throw new RuntimeException(exception);
      }
  }

  // ------------------------------------------------------------------------------
  // h1: checkXxx methods.
  // ------------------------------------------------------------------------------

  public void checkAccept (String host, int port)
  {
    super.checkAccept(host, port);
    throw new SecurityException
      ("Program tried to accept socket connection (" + host + ":" + port + ")");
  }

  public void checkConnect (String host, int port)
  {
    super.checkConnect(host, port);
    throw new SecurityException
      ("Program tried to open socket connection (" + host + ":" + port + ")");
  }

  public void checkConnect (String host, int port, Object context)
  {
    super.checkConnect(host, port, context);
    throw new SecurityException
      ("Program tried to open socket connection (" + host + ":" + port + ")");
  }

  public void checkCreateClassLoader ()
  {
    super.checkCreateClassLoader();
    throw new SecurityException("Program tried to create a class loader");
  }

  public void checkDelete (String filename) 
  {
    super.checkDelete(filename);
    if ( !this.deleteAllowedFiles.contains(filename) )
      throw new SecurityException("Program tried to delete a file (" + filename + ")");
  }

  public void checkExec (String cmd)
  {
    super.checkExec(cmd);
    throw new SecurityException("Program tried to execute an external command (" + cmd + ")");
  }

  public void checkExit (int exitValue) 
  {
    super.checkExit(exitValue);
    if ( this.systemExitAllowed )
      throw new SecurityException("Program tried to call System.exit(int)");
  }

  public void checkLink (String filename) 
  {
    super.checkLink(filename);
    throw new SecurityException("Program tried to link library (" + filename + ")");
  }

  public void checkListen (int port)
  {
    super.checkListen(port);
    throw new SecurityException("Program tried to listen on a port (" + port + ")");
  }

  public void checkPrintJobAccess ()
  {
    super.checkPrintJobAccess();
    throw new SecurityException("Program tried to initiate a print job request");
  }

  public void checkPropertiesAccess ()
  {
    super.checkPropertiesAccess();
    throw new SecurityException("Program tried to access system properties");
  }

  //    public void checkPropertyAccess (String name)
  //    {
  //      super.checkPropertyAccess(name);
  //      throw new SecurityException("Program tried to access a system property (" + name + ")");
  //    }

  public void checkRead (FileDescriptor descriptor)
  {
    super.checkRead(descriptor);
    throw new SecurityException
      ("Program tried to read from file descriptor (" + descriptor + ")");
  }

  public void checkRead (String filename)
  {
    super.checkRead(filename);
    if ( !this.checkIfReadAllowed(filename) )
      throw new SecurityException("Program tried to read file \"" + filename + "\"");
  }

  public void checkRead (String filename, Object context)
  {
    super.checkRead(filename, context);
    if ( !this.checkIfReadAllowed(filename) )
      throw new SecurityException("Program tried to read file \"" + filename + "\"");
  }

  public void checkSetFactory ()
  {
    super.checkSetFactory();
    throw new SecurityException("Program tried to set a factory");
  }

  public void checkWrite (FileDescriptor descriptor)
  {
    super.checkWrite(descriptor);
    throw new SecurityException
      ("Program tried to write to a file descriptor (" + descriptor + ")");
  }

  public void checkWrite (String filename)
  {
    super.checkWrite(filename);
    if ( !this.writeAllowedFiles.contains(filename) )
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

  protected static boolean calledForClassLoading ()
  {
    Throwable throwable = new Throwable();
    for (StackTraceElement frame : throwable.getStackTrace())
      {
        if ( frame.getClassName().equals("java.lang.ClassLoader") &&
             frame.getMethodName().equals("loadClass") )
          return true;
      }
    return false;
  }

  /**
   * 
   */

  protected boolean checkIfReadAllowed (String filename)
  {
    if ( filename.startsWith(this.javaHome) )
      return true;

    if ( this.readAllowedFiles.contains(filename) )
      return true;

    if ( calledForClassLoading() )
      return true;

    return false;
  }

}

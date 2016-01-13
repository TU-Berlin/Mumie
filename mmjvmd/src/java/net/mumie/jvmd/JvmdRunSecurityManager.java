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

package net.mumie.jvmd;

import java.security.Permission;

/**
 * A security manager that prevents the Java VM from being halted by the
 * {@link System#exit(int) System.exit(int)} method, but allows all other operations.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: JvmdRunSecurityManager.java,v 1.2 2007/07/16 10:49:28 grudzin Exp $</code>
 */

public class JvmdRunSecurityManager extends SecurityManager
{
  /**
   * Throws an exception because System exit is not allowed.
   */

  public void checkExit (int status)
    throws SecurityException
  {
    throw new SecurityException("System exit is not allowed");
  }

  /**
   * Simply returns to allow all operations except system exit.
   */

  public void checkPermission(Permission permission, Object context)
    throws SecurityException
  {
  }

  /**
   * Simply returns to allow all operations except system exit.
   */

  public void checkPermission(Permission permission)
    throws SecurityException
  {
  }
}

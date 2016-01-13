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

package net.mumie.japs.build;

import net.mumie.cocoon.util.PasswordEncryptor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import net.mumie.cocoon.util.PasswordEncryptionException;

public class PasswordEncryptorWrapper
{
  /**
   * The password encryptor wrapped by this <code>PasswordEncryptorWrapper</code>.
   */

  protected static PasswordEncryptor encryptor = null;

  /**
   * Sets {@link #encryptor} to an instance of the class spwcified by the system property
   * <code>password.encryptor.class</code>.
   */

  public static void init ()
    throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
           InvocationTargetException 
  {
    String encryptorClassName = System.getProperty("password.encryptor.class");
    if ( encryptorClassName == null )
      throw new IllegalStateException("No encryptor class specified");
    Class encryptorClass = Class.forName(encryptorClassName);
    Method createInstanceMethod =
      encryptorClass.getMethod("createOfflineInstance", new Class[0]);
    encryptor = (PasswordEncryptor)createInstanceMethod.invoke(null, new Object[0]);
  }

  /**
   * Encrypts the specified password by the wrapped encryptor.
   */

  public static String encrypt (String password)
    throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
           InvocationTargetException, PasswordEncryptionException
  {
    if ( encryptor == null ) init();
    return encryptor.encrypt(password);
  }
}

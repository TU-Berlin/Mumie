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

package net.mumie.cocoon.util;

import java.security.MessageDigest;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.avalon.framework.logger.NullLogger;

/**
 * <p>
 *   Encrypts passwords the way MOSES does.
 * </p>
 * <p>
 *   NOTE: The algorithm to convert the hash to a string is questionable, so use this
 *   encryptor only if communication with MOSES is required.
 * </p>
 */

public class MosesPasswordEncryptor extends AbstractLogEnabled 
  implements ThreadSafe, PasswordEncryptor
{
  /**
   * Encrypts <code>password</code> and returns the result.
   */

  public String encrypt (String password)
    throws PasswordEncryptionException
  {
    final String METHOD_NAME = "encrypt";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    try
      {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        String salt =
          (password.length() >= 2
           ? salt=password.substring(0,2)
           : password);
        digest.update(salt.getBytes("ASCII"));
        digest.update(password.getBytes());
        byte[] hash = digest.digest();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < hash.length; i++)
          buffer.append(hash[i]);
        this.getLogger().debug(METHOD_NAME + " 2/2: Done");
        return buffer.toString();
      }
    catch (Exception exception)
      {
        throw new PasswordEncryptionException(exception);
      }
  }

  /**
   * Creates and returns an instance that is suitable to work offline.
   */

  public static MosesPasswordEncryptor createOfflineInstance ()
  {
    MosesPasswordEncryptor encryptor = new MosesPasswordEncryptor();
    encryptor.enableLogging(new NullLogger());
    return encryptor;
  }
}
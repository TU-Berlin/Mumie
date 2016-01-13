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
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class UserUtil
{
  /**
   * Path of the section where users are created.
   */

  public static final String USERS_SECTION_PATH = "org/users";

  /**
   * Minimum length of passwords
   */

  public static final int PASSWORD_MIN_LENGTH = 6;

  /**
   * Pattern for login names.
   */

  protected static final Pattern loginNamePattern = Pattern.compile("[A-Za-z0-9._-]+");

  /**
   * Pattern for pure names.
   */

  protected static final Pattern pureNamePattern = Pattern.compile("usr_[A-Za-z0-9_]+");

  /**
   * Pattern for suggesting pure names.
   */

  protected static final Pattern suggestPureNamePattern = Pattern.compile("[.-]");

  /**
   * Checks if the specified login name contains only the allowed characters.
   */

  public static boolean checkLoginNameChars (String loginName)
  {
    return loginNamePattern.matcher(loginName).matches();
  }

  /**
   * Checks if the specified password contains only the allowed characters.
   */

  public static boolean checkPasswordChars (char[] password)
  {
    boolean ok = true;
    for (int i = 0; ok && i < password.length; i++)
      {
        char c = password[i];
        ok = (  32 <= c && c <= 126 );
      }
    return ok;
  }

  /**
   * Checks if the specified password contains only the allowed characters.
   */

  public static boolean checkPasswordChars (String password)
  {
    return checkPasswordChars(password.toCharArray());
  }

  /**
   * Checks if the specified pure name meets the conventions.
   */

  public static boolean checkPureName (String pureName)
  {
    return pureNamePattern.matcher(pureName).matches();
  }

  /**
   * 
   */

  public static String suggestPureName (String string)
  {
    Matcher matcher = suggestPureNamePattern.matcher(string);
    return "usr_" + matcher.replaceAll("_");
  }
}
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

package net.mumie.srv.notions;

public class MediaType
{
  // --------------------------------------------------------------------------------
  // Global variabled and constants, except autocoded 
  // --------------------------------------------------------------------------------

  /**
   * The undefined media type.
   */

  public static final int UNDEFINED = -1;

  // --------------------------------------------------------------------------------
  // Auxiliaries 
  // --------------------------------------------------------------------------------

  /**
   * Returns the array index of the specified media type code.
   */

  protected static final int indexOf (int code)
  {
    int index = -1;
    for (int i = 0; i < mediaTypes.length && index == -1; i++)
      {
        if ( mediaTypes[i] == code )
          index = i;
      }
    if ( index == -1 )
      throw new IllegalArgumentException("Invalid media type code: " + code);
    return index;
  }

  /**
   * Returns the array index of the media type with the specified name.
   */

  protected static final int indexOf (String name)
  {
    int index = -1;
    for (int i = 0; i < names.length && index == -1; i++)
      {
        if ( names[i].equals(name) )
          index = i;
      }
    if ( index == -1 )
      throw new IllegalArgumentException("Invalid media type name: " + name);
    return index;
  }

  // --------------------------------------------------------------------------------
  // Getting codes for names and vice versa
  // --------------------------------------------------------------------------------

  /**
   * Returns the name for the (pseudo-)document type represented by the specified numerical
   * code.
   */

  public static final String nameFor (int code)
  {
    return names[indexOf(code)];
  }

  /**
   * Returns the numerical code for the specified (pseudo-)document type name.
   */

  public static final int codeFor (String name)
  {
    return mediaTypes[indexOf(name)];
  }

  // --------------------------------------------------------------------------------
  // Suffixes
  // --------------------------------------------------------------------------------

  /**
   * Returns the suffix for the media type represented by the specified numerical
   * code.
   */

  public static final String suffixOf (int code)
  {
    return suffixes[indexOf(code)];
  } 

  /**
   * Returns the suffix for the specified media type name.
   */

  public static final String suffixOf (String name)
  {
    return suffixes[indexOf(name)];
  } 

  // --------------------------------------------------------------------------------
  // Checking whether a code or name exists as a media type
  // --------------------------------------------------------------------------------

  /**
   * Returns true if the specified integer exists as a media type code;
   * otherwise returns false.
   */

  public static final boolean exists (int code)
  {
    for (int i = 0; i < mediaTypes.length; i++)
      {
        if ( mediaTypes[i] == code )
          return true;
      }
    return false;
  }

  /**
   * Returns true if the specified string exists as a media type name;
   * otherwise returns false.
   */

  public static final boolean exists (String name)
  {
    for (int i = 0; i < names.length; i++)
      {
        if ( names[i].equals(name) )
          return true;
      }
    return false;
  }

  // --------------------------------------------------------------------------------
  // Getting all mediaTypes
  // --------------------------------------------------------------------------------

  /**
   * Returns an array containing all media types. The array is is not backed by any
   * variables of this class, so changes to the array do not effect this class. 
   */

  public static final int[] allMediaTypes ()
  {
    int[] mediaTypesCopy = new int[mediaTypes.length];
    System.arraycopy(mediaTypes, 0, mediaTypesCopy, 0, mediaTypes.length);
    return mediaTypesCopy;
  }

  // ================================================================================
  // Autocoded start
  // ================================================================================

  
  /**
   * A plain text document
   */

  public static final int TEXT_PLAIN = 101;

  /**
   * An XML document
   */

  public static final int TEXT_XML = 102;

  /**
   * A HTML document
   */

  public static final int TEXT_HTML = 103;

  /**
   * A CSS stylesheet
   */

  public static final int TEXT_CSS = 104;

  /**
   * A TeX document (TeX, LaTeX, MmTeX, ...)
   */

  public static final int TEXT_TEX = 105;

  /**
   * A Java source document
   */

  public static final int TEXT_JAVA = 106;

  /**
   * A GIF image
   */

  public static final int IMAGE_GIF = 201;

  /**
   * A JPEG image
   */

  public static final int IMAGE_JPEG = 202;

  /**
   * A PNG image
   */

  public static final int IMAGE_PNG = 203;

  /**
   * A jar archive
   */

  public static final int APPLICATION_X_JAVA_ARCHIVE = 301;

  /**
   * A Java class
   */

  public static final int APPLICATION_X_JAVA_VM = 302;

  /**
   * JavaScript
   */

  public static final int APPLICATION_X_JAVASCRIPT = 303;

  /**
   * A SWF Flash document
   */

  public static final int APPLICATION_X_SHOCKWAVE_FLASH = 304;

  /**
   * A zip archive
   */

  public static final int APPLICATION_ZIP = 305;

  /**
   * An MPEG audio document
   */

  public static final int AUDIO_MPEG = 401;

  /**
   * A WAV audio document
   */

  public static final int AUDIO_WAV = 402;

  /**
   * An array containing all media type codes.
   */

  private static final int[] mediaTypes =
  {
    101,
    102,
    103,
    104,
    105,
    106,
    201,
    202,
    203,
    301,
    302,
    303,
    304,
    305,
    401,
    402,
  };

  /**
   * An array containing all media type names.
   */

  private static final String[] names =
  {
    "text/plain",
    "text/xml",
    "text/html",
    "text/css",
    "text/tex",
    "text/java",
    "image/gif",
    "image/jpeg",
    "image/png",
    "application/x-java-archive",
    "application/x-java-vm",
    "application/x-javascript",
    "application/x-shockwave-flash",
    "application/zip",
    "audio/mpeg",
    "audio/wav",
  };

  /**
   * An array containing all media type suffixes.
   */

  private static final String[] suffixes =
  {
    "txt",
    "xml",
    "html",
    "css",
    "tex",
    "java",
    "gif",
    "jpg",
    "png",
    "jar",
    "class",
    "js",
    "swf",
    "zip",
    "mpg",
    "wav",
  };


  // ================================================================================
  // Autocoded end
  // ================================================================================

  /**
   * Disabled constructor.
   */

  private MediaType ()
    throws IllegalAccessException
  {
    throw new IllegalAccessException("MediaType must not be instanciated");
  }
}
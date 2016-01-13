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

import java.io.File;
import java.net.URI;
import net.mumie.cocoon.notions.FileRole;
import net.mumie.cocoon.notions.MediaType;

/**
 * <p>
 *   Represents a filename in the Japs standard for checkin files. Checkin files are master,
 *   content, or preview files. Each checkin file corresponds to a document or pseudo-document.
 * </p>
 * <p>
 *   A Japs filename has the following structure (see below for an exception):
 * </p>
 * <pre>
 * <var>typePrefix</var>.<var>pureName</var>.<var>roleSuffix</var>.<var>mediaTypeSuffix</var></pre>
 * <p>
 *   The <var>typePrefix</var> is the string name of the (pseudo-)document type. The
 *   <var>pureName</var> is the "actual" name of the (pseudo-)document. The
 *   <var>roleSuffix</var> indicates the role of the file (see
 *   {@link FileRole FileRole}. The <var>mediaTypeSuffix</var> indicates the media type of
 *   the file content (see {@link MediaType MediaType}).
 * </p>
 * <p>
 *   There is one exception from the above filename structure: section master filenames are
 *   always
 *   <pre>
 *   .meta.xml</pre>
 *   Thus, a master filename does not contain the <var>typePrefix</var> and the
 *   <var>pureName</var>.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: JapsFile.java,v 1.5 2007/07/11 15:38:53 grudzin Exp $</code>
 */

public class JapsFile extends File
{
  // --------------------------------------------------------------------------------
  // Global constants and variables
  // --------------------------------------------------------------------------------

  /**
   * The (full) suffix of master files. "Full" means it comprises both the role and the type
   * suffix.
   */

  public static final String MASTER_SUFFIX =
    FileRole.suffixOf[FileRole.MASTER] + "." + MediaType.suffixOf[MediaType.TEXT_XML];

  /**
   * The pure name of this file.
   */

  protected String pureName = null;

  /**
   * The role suffix of this file.
   */

  protected String roleSuffix = null;

  /**
   * The media type suffix of this file.
   */

  protected String mediaTypeSuffix = null;

  /**
   * The (pseudo-)document type prefix of this file
   */

  protected String typePrefix = null;

  // --------------------------------------------------------------------------------
  // Constructors
  // --------------------------------------------------------------------------------

  /**
   * Creates a new <code>JapsFile</code> from the specified filename.
   */

  public JapsFile (String filename)
    throws JapsFileException
  {
    super(filename);
    this.init();
  }

  /**
   * Creates a new <code>JapsFile</code> from the specified file.
   */

  public JapsFile (File file)
    throws JapsFileException
  {
    this(file.getPath());
  }

  /**
   * Creates a new <code>JapsFile</code> from the specified parent and child.
   */

  public JapsFile (File parent, String child)
    throws JapsFileException
  {
    super(parent, child);
    this.init();
  }

  /**
   * Creates a new <code>JapsFile</code> from the specified parent and child.
   */

  public JapsFile (String parent, String child)
    throws JapsFileException
  {
    super(parent, child);
    this.init();
  }

  /**
   * Creates a new <code>JapsFile</code> from the specified URI.
   */

  public JapsFile (URI uri)
    throws JapsFileException
  {
    super(uri);
    this.init();
  }

  // --------------------------------------------------------------------------------
  // Getting file parts (pure name, suffixes, prefix)
  // --------------------------------------------------------------------------------

  /**
   * Returns the role suffix of this <code>JapsFile</code>.
   */

  public String getRoleSuffix ()
  {
    return this.roleSuffix;
  }

  /**
   * Returns the media type suffix of this <code>JapsFile</code>.
   */

  public String getMediaTypeSuffix ()
  {
    return this.mediaTypeSuffix;
  }

  /**
   * Returns the full suffix of this <code>JapsFile</code>.
   */

  public String getFullSuffix ()
  {
    return this.roleSuffix + "." + this.mediaTypeSuffix;
  }

  /**
   * Returns the pure name of this <code>JapsFile</code>.
   */

  public String getPureName ()
  {
    return this.pureName;
  }

  /**
   * Returns the type prefix of this <code>JapsFile</code>.
   */

  public String getTypePrefix ()
  {
    return this.typePrefix;
  }

  // --------------------------------------------------------------------------------
  // Getting file role
  // --------------------------------------------------------------------------------

  /**
   * Returns the role of this <code>JapsFile</code>. The role is returned as the numerical
   * code defined in {@link FileRole}. If the role can not be determined, returns
   * {@link FileRole#UNDEFINED FileRole.UNDEFINED}.
   */

  public int getRole ()
  {
    return FileRole.codeForSuffix(roleSuffix);
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Sets {@link #pureName}, {@link #roleSuffix}, {@link #mediaTypeSuffix}, and
   * {@link #typePrefix}.
   */

  protected void init ()
    throws JapsFileException
  {
    String localName = this.getName();

    if ( localName.equals("." + MASTER_SUFFIX) )
      {
        // Specaial case: section master file
        this.pureName = "";
        this.typePrefix = "";
        this.roleSuffix = FileRole.suffixOf[FileRole.MASTER];
        this.mediaTypeSuffix = MediaType.suffixOf[MediaType.TEXT_XML];
      }
    else
      {
        // Get the positions of the dots:
        int firstDot = localName.indexOf('.');
        int lastDot = localName.lastIndexOf('.');
        int nextToLastDot =
          (lastDot > 0
           ? localName.lastIndexOf('.', lastDot - 1)
           : -1);

        // Check:
        if ( ! ( firstDot != -1 && lastDot != -1 && nextToLastDot != -1 &&
                 firstDot < nextToLastDot && nextToLastDot < lastDot ) )
          throw new JapsFileException("Invalid japs filename: \"" + localName + "\"");

        // Set name parts:
        this.pureName = localName.substring(firstDot+1, nextToLastDot);
        this.roleSuffix = localName.substring(nextToLastDot+1, lastDot);
        this.typePrefix = localName.substring(0, firstDot);
        this.mediaTypeSuffix = localName.substring(lastDot+1);
      }
  }
}

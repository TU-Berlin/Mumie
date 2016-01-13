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

import net.mumie.cdk.io.CdkFile;
import net.mumie.cocoon.notions.FileRole;
import net.mumie.cocoon.notions.MediaType;
import java.io.File;
import net.mumie.cdk.CdkConfigParam;
import java.util.regex.Pattern;
import java.util.Arrays;

/**
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: SectionMasterFileChecker.java,v 1.1 2009/05/15 23:15:12 rassy Exp $</code>
 */

public class SectionMasterFileChecker extends CdkWorker
{
	/**
	 * Directories with a name matching this regular expression are not considered
	 * sections by default.
	 */

	public static final String DEFAULT_NON_SEC_DIR_NAME_REGEX = "^CVS$|\\.files$";

	/**
	 * Pattern to check whether a directory name is to be ignored.
	 */

	protected Pattern excludedDirNamePattern = null;

	/**
	 * Filename of section masters.
	 */

	public static final String SEC_MASTER_FILENAME =
		"." + FileRole.MASTER_SUFFIX + "." + MediaType.TEXT_XML_SUFFIX;

	/**
	 * 
	 */

	protected int checkSectionMasterFiles (CdkFile[] dirs, boolean recursive, boolean firstLevel)
    throws Exception
  {
    int warnCount = 0;
    for (CdkFile dir : dirs)
      {
				this.checkStop();
        if ( dir.isDirectory() )
					{
						if ( !this.excludedDirNamePattern.matcher(dir.getName()).find() )
							{
								this.msg(dir + ": ");
								CdkFile masterFile = new CdkFile(new File(dir, SEC_MASTER_FILENAME));
								if ( masterFile.exists() )
									this.msgln("ok");
								else
									{
										warnCount++;
										this.msgln("failed");
										this.warn("WARNING: No " + SEC_MASTER_FILENAME + " file in " + dir);
									}
								if ( recursive )
									warnCount += this.checkSectionMasterFiles(dir.listCdkFiles(), recursive, false);
							}
					}
				else if ( firstLevel )
					throw new IllegalArgumentException("Not a directory: " + dir);
      }
    return warnCount;
  }

  /**
	 *
   */

  public int checkSectionMasterFiles (CdkFile[] dirs, boolean recursive)
    throws Exception
  {
		this.excludedDirNamePattern = Pattern.compile
			(System.getProperty(CdkConfigParam.NON_SEC_DIR_NAME_REGEX, DEFAULT_NON_SEC_DIR_NAME_REGEX));
    int warnCount = this.checkSectionMasterFiles(dirs, recursive, true);
    this.warn
      (warnCount == 0
       ? "No warnings"
       : warnCount + (warnCount == 1 ? " warning" : " warnings"));
    return warnCount;
  }
}

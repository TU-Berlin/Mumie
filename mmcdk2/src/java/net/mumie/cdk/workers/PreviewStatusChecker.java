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
import java.util.Map;
import java.util.HashMap;
import net.mumie.util.SimpleStringTable;
import net.mumie.util.StatusPrinter;
import net.mumie.util.VT100Esc;
import java.util.Arrays;
import net.mumie.cocoon.checkin.Master;
import net.mumie.cocoon.notions.Nature;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.DataFormat;

/**
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: PreviewStatusChecker.java,v 1.3 2007/11/01 00:25:32 rassy Exp $</code>
 */

public class PreviewStatusChecker extends CdkWorker
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * Status code indicating that a preview is up-to-date
   */

  public static final int UP_TO_DATE = 0;

  /**
   * Status code indicating that a preview is outdated
   */

  public static final int OUTDATED = 1;

  /**
   * Status code indicating that a preview does not exist
   */

  public static final int INEXISTENT = 2;

  /**
   * Status code indicating that a preview status is not clear
   */

  public static final int UNCLARIFIED = 3;

  /**
   * Array mapping the status codes to human-readable names. Will be initialized in the
   * constructor.
   */

  protected String[] statusNames;

  /**
   * Array mapping the status codes to VT 100 escape sequences. Will be initialized in the
   * constructor.
   */

  protected String[] statusVT100escs;

  /**
   * Headline of the table column containing the document filenames.
   */

  protected String headlineFilename;

  /**
   * Headline of the table column containing the status.
   */

  protected String headlineStatus;

  // --------------------------------------------------------------------------------
  // Checking the preview status
  // --------------------------------------------------------------------------------

  /**
   * Returns the preview status for the specified master file.
   */

  protected int getPreviewStatus (CdkFile masterFile)
    throws Exception
  {
    CdkFile contentFile = masterFile.getContentFile();
    CdkFile previewFile = masterFile.getPreviewFile();

    if ( !previewFile.exists() )
      return INEXISTENT;
    else if ( !contentFile.exists() )
      return UNCLARIFIED;
    else
      {
        long previewLastModified = previewFile.lastModified();
        if ( previewLastModified > masterFile.lastModified() &&
             previewLastModified > contentFile.lastModified() )
          return UP_TO_DATE;
        else
          return OUTDATED;
      }
  }

  /**
   * <p>
   *   Checks the preview statuses of the specified files and outputs the results by means
   *   of the specified status printer. The specified files may be of any type; files not
   *   needed for the check are silently ignored. If the <code>recursive</code> flag is
   *   true, directories are processed recursively by applying this method recursively to
   *   the contents of the directories. If the <code>firstLevel</code> flag is true, the
   *   method is applied to the contents of each directory in <code>files</code>, but unless
   *   the <code>recursive</code> flag is true, this is not done recursively.
   * </p>
   * <p>
   *   This method is an auxiliary means for implementing
   *   {@link #getPeviewStatuses(CdkFile[],int,boolean,boolean)}. The purpose of the
   *   <code>firstLevel</code> flag is to make it possible to specify directories as input
   *   for the <code>PreviewStatusChecker</code>, which are processed by processing the
   *   files they contain.
   * </p>
   */

  protected void checkPeviewStatuses (CdkFile[] files,
                                      StatusPrinter statusPrinter,
                                      boolean recursive,
                                      boolean firstLevel)
    throws Exception
  {
    for (CdkFile file : files)
      {
        this.checkStop();
        if ( isRelevantMasterFile(file) )
          this.printStatus(statusPrinter, file, this.getPreviewStatus(file));
        else if ( file.isPreviewFile() && !file.getMasterFile().exists() )
          this.printStatus(statusPrinter, file, UNCLARIFIED);
        else if ( file.isDirectory() && ( recursive || firstLevel ) )
          this.checkPeviewStatuses(file.listCdkFiles(), statusPrinter, recursive, false);
      }
  }

  /**
   * Checks the preview statuses of the specified files and prints the results to
   * stdout. The specified files may be of any type; files not needed for the check are
   * silently ignored. Printing is done by means of a {@link StatusPrinter StatusPrinter}
   * instance which is created in the method. The <code>outputWidth</code> parameter
   * specified the total width of the output (in characters). If the
   * <code>colorEnabled</code> flag is true, the statuses are printed in color using VT 100
   * escape sequences (see {@link #statusVT100escs statusVT100escs}. If the
   * <code>recursive</code> flag is true, directories are processed recursively.
   */

  public void checkPeviewStatuses (CdkFile[] files,
                                   int outputWidth,
                                   boolean colorEnabled,
                                   boolean recursive)
    throws Exception
  {
    StatusPrinter statusPrinter = new StatusPrinter
      (this.out,
       outputWidth,
       getMaxWidth(this.statusNames),
       StatusPrinter.LEFT,
       colorEnabled); 
    this.checkPeviewStatuses(files, statusPrinter, recursive, true);
  }

  /**
   * <p>
   *   Checks the preview statuses of the specified files, formats the result as a table,
   *   and prints it to stdout. If the <code>colorEnabled</code> flag is true, the status
   *   keywords are distinguished by colors.
   * </p>
   */

  public void printPeviewStatusTable (CdkFile[] files, boolean colorEnabled)
    throws Exception
  {
    // Get the status of each file:
    Map<String,Integer> statuses = new HashMap<String,Integer>();
    for (CdkFile file : files)
      {
        this.checkStop();
        if ( isRelevantMasterFile(file) )
          statuses.put(file.getPureName(), new Integer(this.getPreviewStatus(file)));
        else if ( file.isPreviewFile() && !file.getMasterFile().exists() )
          statuses.put(file.getPureName(), new Integer(UNCLARIFIED));
      }

    // Get a sorted array of all pure names:
    String[] pureNames = statuses.keySet().toArray(new String[statuses.size()]);
    Arrays.sort(pureNames);

    // Create the table:
    SimpleStringTable table = new SimpleStringTable(1+statuses.size(), 2);
    table.set(0, 0, this.headlineFilename);
    table.set(0, 1, this.headlineStatus);
    for (int i = 0; i < pureNames.length; i++)
      {
        this.checkStop();
        int status = statuses.get(pureNames[i]).intValue();
        String statusName = this.statusNames[status];
        String statusVT100esc = this.statusVT100escs[status];
        table.set(i+1, 0, pureNames[i]);
        table.set(i+1, 1, statusName, colorEnabled ? statusVT100esc : null);
      }
    table.setColumnBorders(true);
    table.setRowBorder(0, true);

    // Output table:
    table.printTo(this.out);
  }

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance.
   */

  public PreviewStatusChecker ()
  {
    this.statusNames = new String[4];
    this.statusNames[UP_TO_DATE] = "up-to-date";
    this.statusNames[OUTDATED] = "outdated";
    this.statusNames[INEXISTENT] = "inexistent";
    this.statusNames[UNCLARIFIED] = "unclarified";

    this.statusVT100escs = new String[4];
    this.statusVT100escs[UP_TO_DATE] = VT100Esc.FG_GREEN;
    this.statusVT100escs[OUTDATED] = VT100Esc.FG_RED;
    this.statusVT100escs[INEXISTENT] = VT100Esc.FG_MAGENTA;
    this.statusVT100escs[UNCLARIFIED] = VT100Esc.FG_CYAN;

    this.headlineFilename = "Document";
    this.headlineStatus = "Preview";
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Returns true if the specified file is the master file of a non-generic document with
   * non-binary content.
   */

  protected static boolean isRelevantMasterFile (CdkFile file)
    throws Exception
  {
    if ( !file.isMasterFile() )
      return false;
    Master master = file.getMaster();
    if ( master.getNature() != Nature.DOCUMENT )
      return false;
    int type = master.getType();
    return ( !DocType.isGeneric(type) && DataFormat.ofDocType(type) == DataFormat.TEXT );
  }

  /**
   * Outputs the specified status as the status of the specified file, using the specified
   * status printer. 
   */

  protected void printStatus (StatusPrinter statusPrinter, CdkFile file, int status)
  {
    statusPrinter.printKey(file.getCdkPurePath());
    statusPrinter.printStatus
      (this.statusNames[status], this.statusVT100escs[status]);
  }

  /**
   * 
   */

  protected static int getMaxWidth (String[] strings)
  {
    int maxWidth = 0;
    for (String string : strings)
      {
        int width = string.length();
        if ( width > maxWidth ) maxWidth = width;
      }
    return maxWidth;
  }
}

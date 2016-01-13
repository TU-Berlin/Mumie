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

import java.io.PrintStream;
import net.mumie.cdk.CdkHelper;
import net.mumie.cdk.io.CdkFile;
import net.mumie.cocoon.checkin.Master;
import net.mumie.cocoon.checkin.GDIMEntry;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.DocType;

/**
 * Checks references in master files.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ReferenceChecker.java,v 1.8 2008/08/30 23:32:08 rassy Exp $</code>
 */

public class ReferenceChecker extends CdkWorker
{
  /**
   * Checks the components and links in the specified master file. Auxiliary method; used by
   * {@link #checkReferences(CdkFile) checkReferences(CdkFile)}.
   *
   * @param masterFile master file of the document to check
   * @param warnCount number of warnings occurred so far
   * @return new number of warnings
   */

  protected int checkComponentsAndLinks (CdkFile masterFile, int warnCount)
    throws Exception
  {
    String contextPath = masterFile.getCdkPath();
    Master master = masterFile.getMaster();
    CdkHelper cdkHelper = CdkHelper.getSharedInstance();

    for (Master target : concatArrays(master.getComponents(), master.getLinks()))
      {
        this.checkStop();
        String path = target.getPath();
        if ( path == null )
          {
            warnCount++;
            if ( warnCount == 1 ) this.msgln("failed");
            this.warn("WARNING: In " + masterFile + ", a reference occurs without a path.");
          }
        else
          {
            path = cdkHelper.resolvePathPrefix(path, contextPath);
            CdkFile targetMasterFile = new CdkFile(path);
            if ( !targetMasterFile.exists() )
              {
                warnCount++;
                if ( warnCount == 1 ) this.msgln("failed");
                this.warn
                  ("WARNING: In " + masterFile + ", the following file is referenced" +
                   " but does not exist: " + targetMasterFile);
              }
            else
              {
                String typeNameOfTarget = target.getTypeName();
                String typeNameOfTargetByFile = targetMasterFile.getMaster().getTypeName();
                if ( !typeNameOfTarget.equals(typeNameOfTargetByFile) )
                  {
                    warnCount++;
                    if ( warnCount == 1 ) this.msgln("failed");
                    this.warn
                      ("WARNING: In " + masterFile + ", the following file is referenced" +
                       " as a document of type \"" + typeNameOfTarget + "\", but it is of type" +
                       " \"" + typeNameOfTargetByFile + "\": " + targetMasterFile);
                  }
              }
          }
      }

    return warnCount;
  }

  /**
   * Checks the GDIM entries in the specified master. Auxiliary method; used by
   * {@link #checkReferences(CdkFile) checkReferences(CdkFile)}.
   *
   * @param masterFile master file of the document to check
   * @param warnCount number of warnings occurred so far
   * @return new number of warnings
   */

  protected int checkGDIMEntries (CdkFile masterFile, int warnCount)
    throws Exception
  {
    String contextPath = masterFile.getCdkPath();
    Master master = masterFile.getMaster();
    CdkHelper cdkHelper = CdkHelper.getSharedInstance();

    int type = master.getType();
    String typeNameOfGeneric =
      (DocType.hasGeneric(type) ? DocType.nameFor(DocType.genericOf(type)) : null);

    for (GDIMEntry gdimEntry : master.getGDIMEntries())
      {
        this.checkStop();
        String path = gdimEntry.getGenericDocPath();
        if ( path == null )
          {
            warnCount++;
            if ( warnCount == 1 ) this.msgln("failed");
            this.warn
              ("WARNING: In " + masterFile + ", a generic document occurs without a path.");
          }
        else
          {
            path = cdkHelper.resolvePathPrefix(path, contextPath);
            CdkFile targetMasterFile = new CdkFile(path);
            if ( !targetMasterFile.exists() )
              {
                warnCount++;
                if ( warnCount == 1 ) this.msgln("failed");
                this.warn
                  ("WARNING: In " + masterFile + ", the following file is referenced" +
                   " but does not exist: " + targetMasterFile);
              }
            else
              {
                if ( typeNameOfGeneric == null )
                  {
                    warnCount++;
                    if ( warnCount == 1 ) this.msgln("failed");
                    this.warn
                      ("WARNING: The document " + masterFile + " is assigned to a" +
                       " generic document, but the document type has no generic counterpart.");
                  }
                else
                  {
                    String typeNameOfTargetByFile = targetMasterFile.getMaster().getTypeName();
                    if ( !typeNameOfGeneric.equals(typeNameOfTargetByFile) )
                      {
                        warnCount++;
                        if ( warnCount == 1 ) this.msgln("failed");
                        this.warn
                          ("WARNING: The document " + masterFile + " is assigned to the" +
                           " generic document " + targetMasterFile + ", which is of type" +
                           " \"" + typeNameOfTargetByFile + "\" but must be of type" +
                           " \"" + typeNameOfGeneric + "\".");
                      }
                  }
              }
          }
      }

    return warnCount;
  }

  /**
   * Checks the corrector reference in the specified master. Auxiliary method; used by
   * {@link #checkReferences(CdkFile) checkReferences(CdkFile)}.
   *
   * @param masterFile master file of the document to check
   * @param warnCount number of warnings occurred so far
   * @return new number of warnings
   */

  protected int checkCorrector (CdkFile masterFile, int warnCount)
    throws Exception
  {
    this.checkStop();

    Master master = masterFile.getMaster();
    CdkHelper cdkHelper = CdkHelper.getSharedInstance();

    String path = master.getCorrectorPath();
    if ( path != null )
      {
        CdkFile targetMasterFile = new CdkFile(path);
        if ( !targetMasterFile.exists() )
          {
            warnCount++;
            if ( warnCount == 1 ) this.msgln("failed");
            this.warn
              ("WARNING: In " + masterFile + ", the following file is referenced" +
               " as a corrector but does not exist: " + targetMasterFile);
          }
        else
          {
            final String JAVA_CLASS = DocType.nameFor(DocType.JAVA_CLASS);
            String typeNameOfTargetByFile = targetMasterFile.getMaster().getTypeName();
            if ( !typeNameOfTargetByFile.equals(JAVA_CLASS) )
              {
                warnCount++;
                if ( warnCount == 1 ) this.msgln("failed");
                this.warn
                  ("WARNING: In " + masterFile + ", the following file is referenced" +
                   " as a corrector, but its type is \"" +  typeNameOfTargetByFile +
                   "\" (should be \"" + JAVA_CLASS + "\"): " + targetMasterFile);
              }
          }
      }

    return warnCount;
  }

  /**
   * Checks the summary reference in the specified master. Auxiliary method; used by
   * {@link #checkReferences(CdkFile) checkReferences(CdkFile)}.
   *
   * @param masterFile master file of the document to check
   * @param warnCount number of warnings occurred so far
   * @return new number of warnings
   */

  protected int checkSummary (CdkFile masterFile, int warnCount)
    throws Exception
  {
    this.checkStop();
    Master master = masterFile.getMaster();
    CdkHelper cdkHelper = CdkHelper.getSharedInstance();

    String path = master.getSummaryPath();

    if ( path != null )
      {
        CdkFile targetMasterFile = new CdkFile(path);
        if ( !targetMasterFile.exists() )
          {
            warnCount++;
            if ( warnCount == 1 ) this.msgln("failed");
            this.warn
              ("WARNING: In " + masterFile + ", the following file is referenced" +
               " as a summary but does not exist: " + targetMasterFile);
          }
        else
          {
            final String GENERIC_SUMMARY = DocType.nameFor(DocType.GENERIC_SUMMARY);
            String typeNameOfTargetByFile = targetMasterFile.getMaster().getTypeName();
            if ( !typeNameOfTargetByFile.equals(GENERIC_SUMMARY) )
              {
                warnCount++;
                if ( warnCount == 1 ) this.msgln("failed");
                this.warn
                  ("WARNING: In " + masterFile + ", the following file is referenced" +
                   " as a summary, but its type is \"" +  typeNameOfTargetByFile +
                   "\" (should be \"" + GENERIC_SUMMARY + "\"): " + targetMasterFile);
              }
          }
      }

    return warnCount;
  }

  /**
   * Checks the info page reference in the specified master. Auxiliary method; used by
   * {@link #checkReferences(CdkFile) checkReferences(CdkFile)}.
   *
   * @param masterFile master file of the document to check
   * @param warnCount number of warnings occurred so far
   * @return new number of warnings
   */

  protected int checkInfoPage (CdkFile masterFile, int warnCount)
    throws Exception
  {
    this.checkStop();
    Master master = masterFile.getMaster();
    CdkHelper cdkHelper = CdkHelper.getSharedInstance();

    String path = master.getInfoPagePath();

    if ( path != null )
      {
        CdkFile targetMasterFile = new CdkFile(path);
        if ( !targetMasterFile.exists() )
          {
            warnCount++;
            if ( warnCount == 1 ) this.msgln("failed");
            this.warn
              ("WARNING: In " + masterFile + ", the following file is referenced" +
               " as an info page but does not exist: " + targetMasterFile);
          }
        else
          {
            final String GENERIC_PAGE = DocType.nameFor(DocType.GENERIC_PAGE);
            String typeNameOfTargetByFile = targetMasterFile.getMaster().getTypeName();
            if ( !typeNameOfTargetByFile.equals(GENERIC_PAGE) )
              {
                warnCount++;
                if ( warnCount == 1 ) this.msgln("failed");
                this.warn
                  ("WARNING: In " + masterFile + ", the following file is referenced" +
                   " as an info page, but its type is \"" +  typeNameOfTargetByFile +
                   "\" (should be \"" + GENERIC_PAGE + "\"): " + targetMasterFile);
              }
          }
      }

    return warnCount;
  }

  /**
   * Checks the thumbnail reference in the specified master. Auxiliary method; used by
   * {@link #checkReferences(CdkFile) checkReferences(CdkFile)}.
   *
   * @param masterFile master file of the document to check
   * @param warnCount number of warnings occurred so far
   * @return new number of warnings
   */

  protected int checkThumbnail (CdkFile masterFile, int warnCount)
    throws Exception
  {
    this.checkStop();
    Master master = masterFile.getMaster();
    CdkHelper cdkHelper = CdkHelper.getSharedInstance();

    String path = master.getThumbnailPath();

    if ( path != null )
      {
        CdkFile targetMasterFile = new CdkFile(path);
        if ( !targetMasterFile.exists() )
          {
            warnCount++;
            if ( warnCount == 1 ) this.msgln("failed");
            this.warn
              ("WARNING: In " + masterFile + ", the following file is referenced" +
               " as a thumbnail but does not exist: " + targetMasterFile);
          }
        else
          {
            final String GENERIC_IMAGE = DocType.nameFor(DocType.GENERIC_IMAGE);
            String typeNameOfTargetByFile = targetMasterFile.getMaster().getTypeName();
            if ( !typeNameOfTargetByFile.equals(GENERIC_IMAGE) )
              {
                warnCount++;
                if ( warnCount == 1 ) this.msgln("failed");
                this.warn
                  ("WARNING: In " + masterFile + ", the following file is referenced" +
                   " as a thumbnail, but its type is \"" +  typeNameOfTargetByFile +
                   "\" (should be \"" + GENERIC_IMAGE + "\"): " + targetMasterFile);
              }
          }
      }

    return warnCount;
  }

  /**
   * Checks the references in the specified master file. A warning is printed in any of the
   * following cases:
   * <ul>
   *   <li>
   *     A file is referenced but does not exist.
   *   </li>
   *   <li>
   *     A file is referenced as a component or link and the document type declared for the
   *     component or link differs from the document type specified in the referenced file.
   *   </li>
   *   <li>
   *     A file is referenced as a generic document in the GDIM section but the document
   *     type specified in the referenced file is not the generic counterpart of the type of
   *     the referencing file.
   *   </li>
   *   <li>
   *     A file is referenced as a generic document in the GDIM section but the type of the
   *     referencing file has no generic counterpart.
   *   </li>
   *   <li>
   *     A file is referenced as a corrector and the document type specified in the referenced
   *     file is not <code>java_class</code>.
   *   </li>
   *   <li>
   *     A file is referenced as a summary and the document type specified in the referenced
   *     file is not <code>generic_summary</code>.
   *   </li>
   *   <li>
   *     A file is referenced as a thumbnail and the document type specified in the referenced
   *     file is not <code>generic_iamge</code>.
   *   </li>
   *   <li>
   *     A file is referenced as an info page and the document type specified in the referenced
   *     file is not <code>generic_page</code>.
   *   </li>
   *   <li>
   *     A component, link or GDIM reference with no path specified.
   *   </li>
   * </ul>
   */

  public int checkReferences (CdkFile masterFile)
    throws Exception
  {
    if ( !masterFile.isMasterFile() )
      throw new IllegalArgumentException("Not a master file: " + masterFile);
    this.msg(masterFile + ": ");
    int warnCount = 0;

    // Check components and links:
    warnCount = this.checkComponentsAndLinks(masterFile, warnCount);

    // Check GDIM entries:
    warnCount = this.checkGDIMEntries(masterFile, warnCount);

    // Check corrector:
    warnCount = this.checkCorrector(masterFile, warnCount);

    // Check summary:
    warnCount = this.checkSummary(masterFile, warnCount);

    // Check info page:
    warnCount = this.checkInfoPage(masterFile, warnCount);

    // Check thumbnail:
    warnCount = this.checkThumbnail(masterFile, warnCount);

    if ( warnCount == 0 )
      this.msgln("ok");
    return warnCount;
  }

  /**
   * <p>
   *   Checks the references of several files. Iterates through the specified array of
   *   <code>CdkFile</code>s and applies {@link #checkReferences(CdkFile)} to each master
   *   file found therein. If the <code>recursive</code> flag is true, directories are
   *   processed recursively by applying this method recursively to the contents of the
   *   directories. If the <code>firstLevel</code> flag is true, the method is applied to
   *   the contents of each directory in <code>files</code>, but unless the
   *   <code>recursive</code> flag is true, this is not done recursively.
   * </p>
   * <p>
   *   Files that are neither master files nor directories are silently ignored.
   * </p>
   * <p>
   *   This method is an auxiliary means for implementing
   *   {@link #checkReferences(CdkFile[],boolean)}. The purpose of the
   *   <code>firstLevel</code> flag is to make it possible to specify directories as input
   *   for the <code>ReferenceChecker</code>, which are processed by processing the
   *   files they contain.
   * </p>
   */

  protected int checkReferences (CdkFile[] files, boolean recursive, boolean firstLevel)
    throws Exception
  {
    int warnCount = 0;
    for (int i = 0; i < files.length; i++)
      {
        CdkFile file = files[i];
        if ( file.isMasterFile() )
          warnCount += this.checkReferences(file);
        else if ( file.isDirectory() && ( recursive || firstLevel ) )
          warnCount += this.checkReferences(file.listCdkFiles(), recursive, false);
      }
    return warnCount;
  }

  /**
   * Checks the references in the master files amoung the files in the specified array. If
   * the <code>recursive</code> flag is true, directories are processed recursively. Files
   * that are neither master files nor directories are silently ignored. Returns the number
   * of warnings.
   */

  public int checkReferences (CdkFile[] files, boolean recursive)
    throws Exception
  {
    int warnCount = this.checkReferences(files, recursive, true);
    this.warn
      (warnCount == 0
       ? "No warnings"
       : warnCount + (warnCount == 1 ? " warning" : " warnings"));
    return warnCount;
  }

  /**
   * Auxiliary method, concatenates the specified arrays.
   */

  protected static Master[] concatArrays (Master[] array1, Master[] array2)
  {
    Master[] array = new Master[array1.length + array2.length];
    System.arraycopy(array1, 0, array, 0, array1.length);
    System.arraycopy(array2, 0, array, array1.length, array2.length);
    return array;
  }
}

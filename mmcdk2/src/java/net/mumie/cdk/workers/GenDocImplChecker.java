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
import net.mumie.cocoon.notions.LangCode;
import net.mumie.cocoon.notions.Nature;
import java.util.List;
import java.io.File;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: GenDocImplChecker.java,v 1.3 2008/09/01 09:51:50 rassy Exp $</code>
 */

public class GenDocImplChecker extends CdkWorker
{
  /**
   * <p>
   *   Finds all generic document master files amoung the specified files and stores their
   *   cdk paths in the specified list. If the <code>recursive</code> flag is true,
   *   subdirectories are processed recursively. If the <code>firstLevel</code> flag is
   *   true, the method is applied to the contents of each directory in <code>files</code>,
   *   but unless the <code>recursive</code> flag is true, this is not done recursively.
   * </p>
   * <p>
   *   Files that are generic document neither master files nor directories are silently
   *   ignored.
   * </p>
   * <p>
   *   This method is an auxiliary means for implementing
   *   {@link #findAllGenDocs(CdkFile[],boolean)}. The purpose of the
   *   <code>firstLevel</code> flag is to make it possible to specify directories as input
   *   for the <code>GenDocImplChecker</code>, which are processed by processing the
   *   files they contain.
   * </p>
   */

  protected void findAllGenDocs (CdkFile[] files,
                                 List<String> genDocPaths,
                                 boolean recursive,
                                 boolean firstLevel)
    throws Exception
  {
    for (CdkFile file : files)
      {
        if ( file.isMasterOfGeneric() )
          genDocPaths.add(file.getCdkPath());
        else if ( file.isDirectory() && ( recursive || firstLevel ) )
          this.findAllGenDocs(file.listCdkFiles(), genDocPaths, recursive, false);
      }
  }

  /**
   * Finds all generic document master files amoung the specified files and returns their
   * cdk paths as a list. If the <code>recursive</code> flag is true, subdirectories are
   * processed recursively.
   */

  public List<String> findAllGenDocs (CdkFile[] files, boolean recursive)
    throws Exception
  {
    List<String> genDocPaths = new ArrayList<String>();
    this.findAllGenDocs(files, genDocPaths, recursive, true);
    return genDocPaths;
  }

  /**
   * Checks if implementations exist for the generic documents with the specified paths.
   */

  public int checkGenDocImpl (List<String> genDocPaths)
    throws Exception
  {
    this.checkStop();

    // Create a map which stores the implementations of each generic document
    Map<String,List<String>> genDocImpls = new HashMap<String,List<String>>();
    for (String path : genDocPaths)
      genDocImpls.put(path, new ArrayList<String>());

    CdkHelper cdkHelper = CdkHelper.getSharedInstance();

    // Do the actual check:
    this.msg("Checking (this may take a while) ... ");
    Iterator iterator = cdkHelper.getCheckinFiles().iterator();
    while ( iterator.hasNext() )
      {
        this.checkStop();
        CdkFile file = new CdkFile((File)iterator.next());

        if ( file.isMasterFile() )
          {
            Master master = cdkHelper.getMasterCache().getMaster(file);
            if ( master.getNature() == Nature.DOCUMENT &&
                 !DocType.isGeneric(master.getType()) )
              {
                for (GDIMEntry gdimEntry : master.getGDIMEntries())
                  {
                    String genDocPath = gdimEntry.getGenericDocPath();
                    if ( genDocPaths.contains(genDocPath) )
                      {
                        String langCode = cdkHelper.normalizeLangCode(gdimEntry.getLangCode());
                        String themePath = cdkHelper.normalizeThemePath(gdimEntry.getThemePath());
                        if ( ( langCode.equals(LangCode.DEFAULT) ||
                               langCode.equals(LangCode.NEUTRAL) ) &&
                             themePath.equals(CdkHelper.DEFAULT_THEME_PATH) )
                          {
                            genDocImpls.get(genDocPath).add(file.getCdkPath());
                          }
                      }
                  }
              }
          }
      }
    this.msgln("done");

    int warnCount = 0;

    // Output result:
    for (String path : genDocPaths)
      {
        List<String> impls = genDocImpls.get(path);
        int numImpls = impls.size();
        this.msgln(path + ": " + (numImpls == 1 ? "ok" : "failed"));
        if ( numImpls == 0 )
          {
            this.warn
              ("WARNING: No implementation for " + path + " in the default theme " +
               "and either the default or neutral language");
            warnCount++;
          }
        else if ( numImpls > 1 )
          {
            this.warn
              ("WARNING: Multiple implementations for " + path + " in the default theme " +
               "and either the default or neutral language (" + toString(impls) + ")");
            warnCount++;
          }
      }
    if ( warnCount == 0 )
      this.warn("No warnings");
    else
      this.warn(warnCount + (warnCount == 1 ? " warning" : " warnings"));

    return warnCount;
  }

  /**
   * Checks if implementations for generic documents exist in the default theme and either
   * the default or neutral language. Works as follows: Iterates through the specified
   * files. If the file is the master file of a generic document, checks if an
   * implementation exists in the above sense. If the file is a directory, processes the
   * files in the directory. If the <code>recursive</code> flag is true, subdirectories are
   * processed recursively.
   */

  public int checkGenDocImpl (CdkFile[] files, boolean recursive)
    throws Exception
  {
    return this.checkGenDocImpl(this.findAllGenDocs(files, recursive));
  }

  /**
   * 
   */

  protected static String toString (List<String> impls)
  {
    StringBuilder buffer = new StringBuilder();
    boolean addSep = false;
    for (String impl : impls)
      {
        if ( addSep ) buffer.append(", ");
        buffer.append(impl);
        addSep = true;
      }
    return buffer.toString();
  }

}

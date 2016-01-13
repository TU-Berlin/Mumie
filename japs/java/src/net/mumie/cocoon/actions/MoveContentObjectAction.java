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

package net.mumie.cocoon.actions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.notions.Nature;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.pseudodocs.SessionUser;
import net.mumie.cocoon.pseudodocs.User;
import net.mumie.cocoon.util.ParamUtil;
import net.mumie.cocoon.util.PathTokenizer;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;

/**
 * <p>
 *   Moves a document or pseudo-document. Recognizes the following parameters:
 * </p>
 * <table class="genuine indented" style="width:60em">
 *   <thead>
 *     <tr>
 *       <td>Name</td>
 *       <td>Description</td>
 *       <td>Required</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td><code>origin</code></td>
 *       <td>The path of the document or pseudo-document to move</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>target</code></td>
 *       <td>Path where the (pseudo-)document si to be moved to</td>
 *       <td>Yes</td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   The actions can do one or both of the following:
 * </p>
 * <ol>
 *   <li>Changing the (pseudo-)document's section</li>
 *   <li>Changing the (pseudo-)document's pure name</li>
 * </ol>
 * <p>
 *   If <var>target</var> is the path of a section, the (pseudo-)document's section is
 *   changed to that, and the (pseudo-)document's pure name remains unchanged. Otherwise,
 *   <var>target</var> is decomposed into a section path <var>sec</var> and a pure name
 *   <var>pname</var>. If the (pseudo-)document's sections differs from <var>sec</var>, its
 *   section is changed to <var>sec</var>. If its pure name differs from <var>pname</var>,
 *   its pure name is changed to <var>pname</var>. It is an error if the section
 *   <var>sec</var> does not exist, or if a (pseudo-)document with the pure name
 *   <var>pname</var> already exists in <var>sec</var> (i.e., the action can not create
 *   sections and can not overwrite existing (pseudo-)documents.
 * </p>
 * <p>
 *   Provided no exception occurs, the {@link #act act} method returns an empty unmodifyable
 *   map ({@link #EMPTY_MAP EMPTY_MAP}).
 * </p>
 * 
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: MoveContentObjectAction.java,v 1.5 2007/12/20 16:41:38 grudzin Exp $</code>
 */

public class MoveContentObjectAction extends ServiceableAction
{
  /**
   * The path tokenizer of this instance.
   */

  protected PathTokenizer pathTokenizer = new PathTokenizer();

  /**
   * See class description.
   */

  public Map act (Redirector redirector,
                  SourceResolver resolver,
                  Map objectModel,
                  String source, 
                  Parameters parameters)
    throws ProcessingException 
  {
    final String METHOD_NAME = "act";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");

    DbHelper dbHelper = null;
    User user = null;    

    try
      {
        // Init services:
        dbHelper = (DbHelper)this.manager.lookup(DbHelper.ROLE);
        user = (User)this.manager.lookup(SessionUser.ROLE);

        // User id:
        int userId = user.getId();

        // Get input data from parameters:
        String originPath = ParamUtil.getAsString(parameters, "origin");
        String targetPath = ParamUtil.getAsString(parameters, "target");

        // Get origin data:
        ResultSet originData = dbHelper.queryContentObjectData
          (originPath, new String[] {DbColumn.DOC_TYPE, DbColumn.ID, DbColumn.CONTAINED_IN}, true);
        if ( !originData.next() ) throw new IllegalArgumentException
            ("Can not find (pseudo-)document with path: " + originPath);
        String originTypeName = originData.getString(DbColumn.DOC_TYPE);
        int originId = originData.getInt(DbColumn.ID);
        int originSectionId = originData.getInt(DbColumn.CONTAINED_IN);

        // Get origin nature and type:
        int originNature = -1;
        int originType = DocType.codeFor(originTypeName);
        if ( originType == DocType.UNDEFINED )
          {
            originType = PseudoDocType.codeFor(originTypeName);
            if ( originType == PseudoDocType.UNDEFINED )
              throw new IllegalArgumentException
                ("Unknwon (pseudo-)document type: " + originTypeName);
            originNature = Nature.PSEUDO_DOCUMENT;
          }
        else
          originNature = Nature.DOCUMENT;
        
        // Variables for new section id and new pure name:
        int newSectionId = Id.UNDEFINED;
        String newPureName = null;

        // Get target section path and target pure name:
        this.pathTokenizer.tokenize(targetPath);
        String targetSectionPath = this.pathTokenizer.getSectionPath();
        String targetPureName = this.pathTokenizer.getPureName();

        // Check if target exists, and if it is a section:
        ResultSet targetData = dbHelper.queryContentObjectData
          (targetSectionPath, targetPureName,
           new String[] {DbColumn.DOC_TYPE, DbColumn.ID, DbColumn.CONTAINED_IN}, true);
        if ( targetData.next() )
          {
            // Target exists
            String targetTypeName = targetData.getString(DbColumn.DOC_TYPE);
            if ( targetTypeName.equals(PseudoDocType.nameFor(PseudoDocType.SECTION)) )
              // Target is a section
              newSectionId = targetData.getInt(DbColumn.ID);
            else
              throw new IllegalArgumentException("Target exists: " + targetPath);
          }
        else
          {
            // Target does not exist
            int targetSectionId = dbHelper.getSectionIdForPath(targetSectionPath);
            if ( targetSectionId == -1 )
              throw new IllegalArgumentException
                ("Can not find section with path: " + targetSectionPath);
            if ( targetSectionId != originSectionId ) newSectionId = targetSectionId;
            newPureName = targetPureName;
          }

        // Check permissions:
        switch ( originNature )
          {
          case Nature.DOCUMENT:
            if ( !dbHelper.checkWritePermissionById(userId, originType, originId) )
              throw new SQLException("No write permission for " + originPath);
            break;
          case Nature.PSEUDO_DOCUMENT:
            if ( !dbHelper.checkPseudoDocWritePermission(userId, originType, originId) )
              throw new SQLException("No write permission for " + originPath);
            break;
          }
        if ( !dbHelper.checkPseudoDocWritePermission(userId, PseudoDocType.SECTION, newSectionId) )
          throw new SQLException("No write permission for " + targetPath);

        // New data as map:
        Map newData = new HashMap();
        if ( newSectionId != Id.UNDEFINED)
          newData.put(DbColumn.CONTAINED_IN, new Integer(newSectionId));
        if ( newPureName != null )
          newData.put(DbColumn.PURE_NAME, targetPureName);

        // Move:
        boolean suppress = newData.isEmpty();
        if ( !suppress )
          dbHelper.updateData(originType, originId, newData);

        this.getLogger().debug
          (METHOD_NAME + " 2/2: Done." +
           " originPath = " + originPath +
           ", targetPath = " + targetPath +
           ", newPureName = " + newPureName +
           ", newSectionId = " + newSectionId +
           ", suppress = " + suppress);
        
        return EMPTY_MAP;
      }
    catch  (Exception exception)
      {
	throw new ProcessingException(exception);
      }
    finally
      {
        if ( user != null )
          this.manager.release(user);

        if ( dbHelper != null )
          this.manager.release(dbHelper);
      }
  }

}

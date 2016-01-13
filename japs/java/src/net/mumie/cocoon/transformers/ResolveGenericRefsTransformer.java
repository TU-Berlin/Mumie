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

package net.mumie.cocoon.transformers;



import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.mumie.cocoon.db.DbHelper;
import net.mumie.cocoon.documents.Document;
import net.mumie.cocoon.notions.DbColumn;
import net.mumie.cocoon.notions.DbTable;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.RefType;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.util.GenericDocumentResolver;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.TimeStampValidity;
import org.xml.sax.SAXException;

/**
 * <p>
 *   Resolves references of a given document to generic documents. Recognizes the following
 *   parameters: 
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
 *       <td><code>type</code></td>
 *       <td>The type of the document which is the reference origin, as numerical code</td>
 *       <td rowspan="2">One of the both</td>
 *     </tr>
 *     <tr>
 *       <td><code>type-name</code></td>
 *       <td>The type of the document which is the reference origin, as as string name</td>
 *     </tr>
 *     <tr>
 *       <td><code>id</code></td>
 *       <td>The id of the document which is the reference origin</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>language</code></td>
 *       <td>The id of the language</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>theme</code></td>
 *       <td>The id of the theme</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr>
 *       <td><code>ref-type</code></td>
 *       <td>The type of the references. Allowed values are <code>component</code> and
 *         <code>link</code></td>
 *       <td>No. Default is <code>component</code></td>
 *     </tr>
 *   </tbody>
 * </table>
 * <p>
 *   For each reference of the specified type to a generic document, the corresponding
 *   real document is determined and written to the dynamic data section.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ResolveGenericRefsTransformer.java,v 1.9 2008/08/21 11:57:24 rassy Exp $</code>
 */

public class ResolveGenericRefsTransformer extends AbstractResolveGenericRefsTransformer
{

  // --------------------------------------------------------------------------------
  // Lifecycle related methods
  // --------------------------------------------------------------------------------

  /**
   * 
   */

  public void setup (SourceResolver sourceResolver,
                     Map objectModel,
                     String source,
                     Parameters parameters)
    throws ProcessingException
  {
    final String METHOD_NAME = "setup";
    this.getLogger().debug(METHOD_NAME + " 1/3: Started");

    GenericDocumentResolver resolver = null;

    try
      {
        super.setup(sourceResolver, objectModel, source, parameters);

	// Get document type and id:
        int docType = ParamUtil.getAsDocType(parameters, "type", "type-name");
        int docId = ParamUtil.getAsId(parameters, "id");

        // Get language and theme
        int languageId = ParamUtil.getAsId(parameters, "language");
        int themeId = ParamUtil.getAsId(parameters, "theme");

        // Get ref type:
        String refTypeStr = ParamUtil.getAsString(parameters, "ref-type", "component");
        int refType = RefType.UNDEFINED;
        if ( refTypeStr.equals("component") )
          refType = RefType.COMPONENT;
        else if( refTypeStr.equals("link") )
          refType = RefType.LINK;
        else
          throw new ParameterException("Invalid ref-type value: " + refTypeStr);

        // Get use mode:
        this.useMode = ParamUtil.getAsUseMode
          (parameters, "use-mode", "use-mode-name",
           (refType == RefType.COMPONENT ? UseMode.COMPONENT : UseMode.LINK));

        this.getLogger().debug
          (METHOD_NAME + " 2/3:" +
           " docType = " + docType +
           ", docId = " + docId +
           ", languageId = " + languageId +
           ", themeId = " + themeId +
           ", useMode = " + this.useMode);

        // Store name:
        this.storeName = 
	  "resolved-generic-" + (refType == RefType.COMPONENT ? "components" : "links");

	// Compose caching key:
	this.cachingKey =
	  docType + "-" +
	  docId + "-" +
	  languageId + "-" +
	  themeId + "-" +
	  refType + "-" +
	  this.useMode;

        // Init services:
        this.dbHelper = (DbHelper)this.manager.lookup(DbHelper.ROLE);
        resolver = (GenericDocumentResolver)this.manager.lookup(GenericDocumentResolver.ROLE);

        // Init ref tables:
        this.refTables = new HashMap<Integer,RefTable>();

        for (int genericDocType : genericDocTypes)
          {
            if ( DbTable.REF[docType][genericDocType] != null )
              {
                // Get generic doc ids:
                int[] genericDocIds =
		  this.getReferencedDocIds(docType, docId, genericDocType, refType);

                if ( genericDocIds.length > 0 )
		  {
		    // Get corresponding real doc ids:
		    int[] realDocIds = new int[genericDocIds.length];
		    ResultSet gdimData = this.dbHelper.queryGDIM
		      (genericDocType, genericDocIds, themeId, languageId, true);
		    for (int i = 0; i < genericDocIds.length; i++)
		      realDocIds[i] = resolver.resolve
			(genericDocType, genericDocIds[i], languageId, themeId, gdimData);

		    // Create ref table:
		    this.refTables.put
		      (new Integer(genericDocType), new RefTable(genericDocIds, realDocIds));
		  }
              }
          }

        this.getLogger().debug(METHOD_NAME + " 3/3: Done");
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
    finally
      {
        if ( resolver != null )
          this.manager.release(resolver);
      }
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Returns the ids of refernced documents.
   * @param docType type of the reference origins
   * @param docId id of the reference origins
   * @param toDocType type of the reference targets
   * @param refType reference type
   */

  protected int[] getReferencedDocIds (int docType,
				       int docId,
				       int toDocType,
				       int refType)
    throws Exception
  {
    ResultSet resultSet =
      this.dbHelper.queryReferencedDocs(docType, docId, toDocType, refType);

    List<Integer> toDocIds = new ArrayList<Integer>();
    while ( resultSet.next() )
      toDocIds.add(new Integer(resultSet.getInt(DbColumn.TO_DOC)));

    return toIntArray(toDocIds);
  }
}
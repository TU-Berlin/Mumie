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

public class ResolveGenericSectionEntriesTransformer extends AbstractResolveGenericRefsTransformer
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

	// Get section id:
        int sectionId = ParamUtil.getAsId(parameters, "id");

        // Get language and theme
        int languageId = ParamUtil.getAsId(parameters, "language");
        int themeId = ParamUtil.getAsId(parameters, "theme");

        // Get use mode:
        this.useMode = ParamUtil.getAsUseMode
          (parameters, "use-mode", "use-mode-name", UseMode.COMPONENT);

        this.getLogger().debug
          (METHOD_NAME + " 2/3:" +
           "  sectionId = " + sectionId +
           ", languageId = " + languageId +
           ", themeId = " + themeId +
           ", useMode = " + this.useMode);

        // Store name:
        this.storeName = "resolved-generic-section-entries";

	// Compose caching key:
	this.cachingKey =
	  sectionId + "-" +
	  languageId + "-" +
	  themeId + "-" +
	  this.useMode;

        // Init services:
        this.dbHelper = (DbHelper)this.manager.lookup(DbHelper.ROLE);
        resolver = (GenericDocumentResolver)this.manager.lookup(GenericDocumentResolver.ROLE);

        // Init ref tables:
        this.refTables = new HashMap<Integer,RefTable>();

        for (int genericDocType : genericDocTypes)
          {
            // Get generic doc ids:
            int[] genericDocIds =
              this.getContainedDocIds(sectionId, genericDocType);

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
   * Returns the ids of the documents with the specified type in the specified section.
   * @param sectionId id of the section
   * @param docType type of documents
   */

  protected int[] getContainedDocIds (int sectionId,
                                      int docType)
    throws Exception
  {
    ResultSet resultSet =
      this.dbHelper.queryDocumentsInSection
      (sectionId, docType, new String[] {DbColumn.ID}, false);

    List<Integer> toDocIds = new ArrayList<Integer>();
    while ( resultSet.next() )
      toDocIds.add(new Integer(resultSet.getInt(DbColumn.ID)));

    return toIntArray(toDocIds);
  }
}
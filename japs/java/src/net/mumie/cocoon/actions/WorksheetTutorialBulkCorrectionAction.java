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

import java.util.HashMap;
import java.util.Map;
import net.mumie.cocoon.grade.WorksheetTutorialBulkCorrector;
import net.mumie.cocoon.grade.PlainBulkCorrectionResultHandler; 
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;

/**
 * Invoke the bulk correction for a worksheet.
 * Uses {@link net.mumie.cocoon.grade.PlainBulkCorrectionResultHandler} as ContentHandler.
 * 
 * @author Marek Grudzinski <a href="mailto:grudzin@math.tu-berlin.de">grudzin@math.tu-berlin.de</a>
 * @version <code>$Id: WorksheetTutorialBulkCorrectionAction.java,v 1.2 2008/02/11 15:25:50 grudzin Exp $</code>
 */

public class WorksheetTutorialBulkCorrectionAction extends ServiceableAction 
{
	 public Map act (Redirector redirector,
			 						 SourceResolver resolver,
			 						 Map objectModel,
			 						 String source, 
			 						 Parameters parameters)
	 	throws ProcessingException 
	{
		 final String METHOD_NAME = "act";
		 this.getLogger().debug(METHOD_NAME + " 1/2: Started");

		 WorksheetTutorialBulkCorrector bulkCorrector = null;
		 PlainBulkCorrectionResultHandler contentHandler = null;

	   try
	   {
	  	 // Init bulk corrector:
	  	 bulkCorrector =
	  		 (WorksheetTutorialBulkCorrector)this.manager.lookup(WorksheetTutorialBulkCorrector.ROLE);
	  	 
	  	 contentHandler = new PlainBulkCorrectionResultHandler(); 

	  	 // Get tutorial and worksheet id:
	  	 int tutorialId = ParamUtil.getAsId(parameters, "tutorial");
	  	 int worksheetId = ParamUtil.getAsId(parameters, "worksheet");

	  	 // Get "force" flag:
	  	 boolean force = ParamUtil.getAsBoolean(parameters, "force", false);

	  	 // Get store name:
	  	 String storeName = ParamUtil.getAsString(parameters, "store-name", "bulk-correction");

	  	 this.getLogger().debug
	  	 (METHOD_NAME + " 2/3: " +
	  			 ", tutorialId = " + tutorialId +
	  			 ", worksheetId = " + worksheetId +
	  			 ", force = " + force +
	  			 ", storeName = " + storeName);
	  	 
	  	 bulkCorrector.bulkCorrect(worksheetId, tutorialId, force, contentHandler, false);
	  	 
	  	 //	Return sitemap parameters:
       Map sitemapParameters = new HashMap();
       sitemapParameters.put("numCorrections", contentHandler.getNumCorrections());
       sitemapParameters.put("numSuccessfulCorrections", contentHandler.getNumSuccessfulCorrections());
       

       this.getLogger().debug(METHOD_NAME + " 3/3: Done");
       this.getLogger().debug("MAREK: numCorrections=" + contentHandler.getNumCorrections() + " / " +
                              "numSuccessfulCorrections=" + contentHandler.getNumSuccessfulCorrections());
        
       return sitemapParameters;	  	 
	   }
	   catch (Exception exception)
	   {
	  	 throw new ProcessingException(exception);
	   }
	   finally
	   {
	  	 if ( bulkCorrector != null )
	  		 this.manager.release(bulkCorrector);
	   }	   
	}
}

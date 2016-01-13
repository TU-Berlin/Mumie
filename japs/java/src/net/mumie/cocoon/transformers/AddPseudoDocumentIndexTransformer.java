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

import java.util.Map;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.UseMode;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.pseudodocs.PseudoDocumentIndex;
import net.mumie.cocoon.util.ParamUtil;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.SourceResolver;
import org.xml.sax.SAXException;

/**
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: AddPseudoDocumentIndexTransformer.java,v 1.2 2008/12/15 12:11:11 rassy Exp $</code>
 */

public class AddPseudoDocumentIndexTransformer extends AbstractAddDynamicDataTransformer
{
  /**
   * The pseudo-document index.
   */

  protected PseudoDocumentIndex index = null;

  /**
   * Name of the {@link XMLElement#STORE STORE} element that wrappes the user.
   */

  protected String storeName = null;

  /**
   * Releases {@link #index} if not <code>null</code>.
   */

  protected void releaseIndex ()
  {
    if ( this.index == null )
      {
        this.manager.release(this.index);
        this.index = null;
      }
  }

  /**
   * Recycles this transformer for re-use. Calls the superclass recycle method and, if
   * {@link #index} is not <code>null</code>, releases {@link #index} and sets it to
   * <code>null</code>. 
   */

  public void recycle ()
  {
    final String METHOD_NAME = "recycle()";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    this.releaseIndex();
    this.storeName = null;
    super.recycle();
    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Disposes this transformer. Calls the superclass dispose method and, if
   * {@link #index} is not <code>null</code>, releases {@link #index} and sets it to
   * <code>null</code>. 
   */

  public void dispose ()
  {
    final String METHOD_NAME = "dispose()";
    this.getLogger().debug(METHOD_NAME + " 1/2: Started");
    this.releaseIndex();
    this.storeName = null;
    super.dispose();
    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Calls the superclass <code>setup</code> method and processes the parameters.
   */

  public void setup (SourceResolver resolver,
                     Map objectModel,
                     String source,
                     Parameters parameters)
    throws ProcessingException
  {
    final String METHOD_NAME = "setup";
    this.getLogger().debug(METHOD_NAME + " 1/3: Started");
    try
      {
        super.setup(resolver, objectModel, source, parameters);

        int type = ParamUtil.getAsPseudoDocType(parameters, "type", "type-name");
        int useMode = ParamUtil.getAsUseMode
          (parameters, "use-mode", "use-mode-name", UseMode.COMPONENT);

        this.storeName = ParamUtil.getAsString
          (parameters, "store-name", PseudoDocType.nameFor(type) + "_index");

        this.getLogger().debug
          (METHOD_NAME + " 2/3:" +
           " type = " + type +
           ", useMode = " + useMode +
           ", storeName = " + this.storeName);

        // Preparing index:
        this.index =
          (PseudoDocumentIndex)this.manager.lookup(PseudoDocumentIndex.ROLE);
        this.index.setType(type);
        this.index.setUseMode(useMode);

        this.getLogger().debug(METHOD_NAME + " 3/3: Done");
      }
    catch (Exception exception)
      {
	throw new ProcessingException(exception);
      }
  }

  /**
   * Sends the content of the dynamic data element to SAX.
   */

  public void dynamicDataElementContentToSAX ()
    throws SAXException
  {
    final String METHOD_NAME = "dynamicDataElementContentToSAX";
    this.getLogger().debug(METHOD_NAME + " 1/2: started");

    // Start STORE element:
    this.metaXMLElement.reset();
    this.metaXMLElement.setLocalName(XMLElement.STORE);
    this.metaXMLElement.addAttribute(XMLAttribute.NAME, this.storeName);
    this.metaXMLElement.startToSAX(this.contentHandler);

    // Sent index to SAX:
    this.index.toSAX(this.contentHandler, false);

    // Close STORE element:
    this.metaXMLElement.reset();
    this.metaXMLElement.setLocalName(XMLElement.STORE);
    this.metaXMLElement.endToSAX(this.contentHandler);

    this.getLogger().debug(METHOD_NAME + " 2/2: finished");
  }
}

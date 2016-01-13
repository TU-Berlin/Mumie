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

import java.io.PrintStream;
import net.mumie.cocoon.service.ServiceInstanceStatus;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.japs.datasheet.DataSheet;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.cocoon.xml.dom.DOMStreamer;
import org.apache.excalibur.xml.dom.DOMParser;
import org.apache.excalibur.xml.sax.XMLizable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import net.mumie.cocoon.service.LookupNotifyable;

/**
 * Default implementation of {@link CocoonEnabledDataSheet}.
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DefaultCocoonEnabledDataSheet.java,v 1.12 2007/07/11 15:38:52 grudzin Exp $</code>
 */

public class DefaultCocoonEnabledDataSheet extends DataSheet
  implements CocoonEnabledDataSheet, Recyclable, Disposable, LookupNotifyable
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(DefaultCocoonEnabledDataSheet.class);

  /**
   * The status object of this instance.
   */

  private ServiceInstanceStatus instanceStatus = null;

  /**
   * The DOM streamer to generate SAX events from this data sheet.
   */

  protected DOMStreamer domStreamer = null;

  /**
   * <p>
   *   Sets the document node to a newly created, empty data sheet document node. The node
   *   contains only the root element (no unit or data elements). It is created by
   *   <code>parser</code>.
   * </p>
   * <p>
   *   By means of this method the <code>DataSheet</code> object can be reused for another
   *   source. 
   * </p>
   * @throws NullPointerException if <code>parser</code> is <code>null</code>.
   * @throws SAXException if the parser fails to create the document.
   */

  public void setEmptyDocument (DOMParser parser)
    throws SAXException
  {
    if ( parser == null )
      throw new NullPointerException("Parser null");
    Document document = parser.createDocument();
    Element rootElement = document.createElementNS(NAMESPACE, ROOT_ELEMENT);
    rootElement.setAttribute("xmlns", NAMESPACE);
    document.appendChild(rootElement);
    this.setRootElement(rootElement);
  }

  /**
   * <p>
   *   Merges this data sheet with another data sheet.
   * </p>
   * <p>
   *   All data of the other data sheet is imported into this one. The value of
   *   <code>mode</code> controls what happens if both data sheets contain entries with the
   *   same path:
   * </p>
   * <ul>
   *   <li>{@link #REPLACE}:&nbsp; the data of this data sheet is removed before,</li>
   *   <li>{@link #PROTECT}:&nbsp; nothing is done (so the data of this data sheet is
   *   preserved),</li> 
   *   <li>{@link #APPEND}:&nbsp; the data of <code>dataSheet</code>is appended to the data
   *   of this data sheet.</li> 
   * </ul>
   */

  public void merge (CocoonEnabledDataSheet dataSheet, int mode)
  {
    String[] paths = dataSheet.getAllDataPaths();
    for (int i = 0; i < paths.length; i++)
      this.put(paths[i], this.importNodes(dataSheet.getAsNodeList(paths[i])), mode);
  }

  /**
   * Notifies this instance that it has been looked-up.
   */

  public void notifyLookup (String ownerLabel)
  {
    this.instanceStatus.notifyLookup(ownerLabel);
  }

  /**
   * Notifies this instance that it has been released.
   */

  public void notifyRelease (String ignored)
  {
    this.instanceStatus.notifyRelease();
  }

  /**
   * Recycles this object for reuse. Sets {@link #rootElement rootElement} to
   * <code>null</code> and recycles {@link #domStreamer domStreamer}.
   */

  public void recycle ()
  {
    this.rootElement = null;
    if ( this.domStreamer != null ) this.domStreamer.recycle();
    this.instanceStatus.notifyRecycle();
  }

  /**
   * Disposes this object. Does the same as {@link #recycle recycle}.
   */

  public void dispose ()
  {
    this.rootElement = null;
    if ( this.domStreamer != null ) this.domStreamer.recycle();
    this.instanceStatus.notifyDispose();
  }

  /**
   * <p>
   *   Sends this data sheet as SAX events to <code>contentHandler</code>.
   * </p>
   * <p>
   *   This is done by {@link #domStreamer domStreamer}, which is instanciated before if
   *   necessary. The normalizeNamespaces flag is set to <code>false</code>.
   * </p>
   * <p>
   *   NOTE: This method my issue <code>startDocument</code> and <code>endDocument</code>
   *   events.
   * </p>
   */

  public void toSAX (ContentHandler contentHandler)
    throws SAXException
  {
    if ( this.domStreamer == null )
      {
        this.domStreamer = new DOMStreamer();
        this.domStreamer.setNormalizeNamespaces(false);
      }
    this.domStreamer.recycle();
    this.domStreamer.setContentHandler(contentHandler);
    // this.domStreamer.stream(this.getDocument());
    this.domStreamer.stream(this.getRootElement());
  }

  /**
   * <p>
   *   Returns a string thast identifies this <code>CocoonEnabledDataSheet</code>. It has
   *   the form
   *   <pre>  "DefaultCocoonEnabledDataSheet#" + <var>instanceId</var></pre>
   *   where <var>instanceId</var> is the instance id of this
   *   <code>CocoonEnabledDataSheet</code>.
   * </p>
   */

  public String getIdentification ()
  {
    return
      "DefaultCocoonEnabledDataSheet" +
      '#' + this.instanceStatus.getInstanceId();
  }

  /**
   * <p>
   *   Creates a new, void data sheet object.
   * </p>
   * <p>
   *   NOTE: You <em>must</em> call {@link #setDocument setDocument},
   *   {@link #setEmptyDocument setEmptyDocument}, or {@link #setRootElement setRootElement}
   *   before you can work with a data sheet object created by this constructor.
   * </p>
   */

  public DefaultCocoonEnabledDataSheet ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }
}

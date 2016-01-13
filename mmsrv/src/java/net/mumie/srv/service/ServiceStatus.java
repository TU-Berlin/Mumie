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

package net.mumie.srv.service;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.mumie.srv.notions.TimeFormat;
import net.mumie.srv.notions.XMLAttribute;
import net.mumie.srv.notions.XMLElement;
import net.mumie.srv.notions.XMLNamespace;
import net.mumie.srv.util.LogUtil;
import net.mumie.srv.xml.GeneralXMLElement;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Startable;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * <p>
 *   Represents the status of a class providing a service in the Avalon/Excalibur sense.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ServiceStatus.java,v 1.2 2008/10/15 23:27:45 rassy Exp $</code>
 */

public class ServiceStatus
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * Counts the instances created so far.
   */

  protected long numberOfCreatedInstances = 0;

  /**
   * Counts the started instances. This are the instances for which the <code>start</code>
   * method has been called.
   */

  protected long numberOfStartedInstances = 0;

  /**
   * Counts the stopped instances. This are the instances for which the <code>stop</code>
   * method has been called.
   */

  protected long numberOfStoppedInstances = 0;

  /**
   * Counts the number of disposed instances.
   */

  protected long numberOfDisposedInstances = 0;

  /**
   * Counts the total number of recycles of all instances.
   */

  protected long numberOfRecycledInstances = 0;

  /**
   * Counts the total number of lookups of all instances.
   */

  protected long numberOfLookedupInstances = 0;

  /**
   * Counts the total number of releases of all instances.
   */

  protected long numberOfReleasedInstances = 0;

  /**
   * Represents the instances of the service class. Each list element should be an instance
   * status.
   */

  protected List serviceInstances = new ArrayList ();

  /**
   * The service class.
   */

  protected Class serviceClass = null;

  /**
   * Whether the tracked class is startable.
   */

  protected boolean startable = false;

  /**
   * Whether the tracked class is recyclable.
   */

  protected boolean recyclable = false;

  /**
   * Whether the tracked class is disposable
   */

  protected boolean disposable = false;

  /**
   * Whether the tracked class is lookup notifyable
   */

  protected boolean lookupNotifyable = false;

  /**
   * Stores all service statuses created so far. The keys of this map are the names of the
   * service classes, the values the <code>ServiceStatus</code> instances.
   */

  protected static Map serviceStatuses = new HashMap();

  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a new service status and sets the service class. The auxiliary variables
   * {@link #startable}, {@link #recyclable}, and {@link #disposable} are initialized. The
   * service class is registered in the {@link #serviceStatuses} map.
   */

  public ServiceStatus (Class serviceClass)
  {
    this.serviceClass = serviceClass;
    this.startable = ( Startable.class.isAssignableFrom(this.serviceClass) );
    this.recyclable = ( Recyclable.class.isAssignableFrom(this.serviceClass) );
    this.disposable = ( Disposable.class.isAssignableFrom(this.serviceClass) );
    this.lookupNotifyable = ( LookupNotifyable.class.isAssignableFrom(this.serviceClass) );
    serviceStatuses.put(serviceClass.getName(), this);
  }

  // --------------------------------------------------------------------------------
  // Increasing / decreasing the counters
  // --------------------------------------------------------------------------------

  /**
   * Increases the number of instances counter by 1 and returns the the new counter
   * value. The return value may be used as an instance id.
   */

  public synchronized long increaseNumberOfCreatedInstances ()
  {
    return ++this.numberOfCreatedInstances;
  }

  /**
   * Increases the number of started instances by 1.
   */

  public synchronized void increaseNumberOfStartedInstances ()
  {
    this.numberOfStartedInstances++;
  }

  /**
   * Increases the number of stopped instances by 1.
   */

  public synchronized void increaseNumberOfStoppedInstances ()
  {
    this.numberOfStoppedInstances++;
  }

  /**
   * Increases the number of recycled instances by 1.
   */

  public synchronized void increaseNumberOfRecycledInstances ()
  {
    this.numberOfRecycledInstances++;
  }

  /**
   * Increases the number of disposed instances by 1.
   */

  public synchronized void increaseNumberOfDisposedInstances ()
  {
    this.numberOfDisposedInstances++;
  }

  /**
   * Increases the number of looked up instances by 1.
   */

  public synchronized void increaseNumberOfLookedupInstances ()
  {
    this.numberOfLookedupInstances++;
  }

  /**
   * Increases the number of released instances by 1.
   */

  public synchronized void increaseNumberOfReleasedInstances ()
  {
    this.numberOfReleasedInstances++;
  }

  // --------------------------------------------------------------------------------
  // Registering a new instance
  // --------------------------------------------------------------------------------

  /**
   * Adds the specified instance status to the list representing the instances of the
   * tracked class.
   */

  public synchronized void register (ServiceInstanceStatus instanceStatus)
  {
    this.serviceInstances.add(instanceStatus);
  }

  // --------------------------------------------------------------------------------
  // Get methods
  // --------------------------------------------------------------------------------

  /**
   * Returns whether the tracked class is startable.
   */

  public boolean getStartable ()
  {
    return this.startable;
  }

  /**
   * Returns whether the tracked class is recyclable.
   */

  public boolean getRecyclable ()
  {
    return this.recyclable;
  }

  /**
   * Returns whether the tracked class is disposable.
   */

  public boolean getDisposable ()
  {
    return this.disposable;
  }

  /**
   * Returns whether the tracked class is lookup notifyable.
   */

  public boolean getLookupNotifyable ()
  {
    return this.lookupNotifyable;
  }

  // --------------------------------------------------------------------------------
  // Output methods
  // --------------------------------------------------------------------------------

  /**
   * Writes this status to the specified stream.
   */

  public void toStream (PrintStream out)
  {
    final int FIELD_WIDTH = 10;
    final int TIME_FIELD_WIDTH = TimeFormat.PRECISE.length();

    // Print Headline:
    out.println
      ("Class: " + this.serviceClass.getName() + "\n");

    // Print counters:
    out.println
      ("numberOfCreatedInstances:  " +
       LogUtil.numberToStringFlushLeft(this.numberOfCreatedInstances, FIELD_WIDTH));
    if ( this.startable )
      out.println
        ("numberOfRunningInstances:  " +
         LogUtil.numberToStringFlushLeft
         (this.numberOfStartedInstances - this.numberOfStoppedInstances, FIELD_WIDTH));
    if ( this.disposable )
      out.println
        ("numberOfActiveInstances:   " +
         LogUtil.numberToStringFlushLeft
         (this.numberOfCreatedInstances - this.numberOfDisposedInstances, FIELD_WIDTH));
    if ( this.recyclable )
      out.println
        ("numberOfRecycledInstances: " +
         LogUtil.numberToStringFlushLeft(this.numberOfRecycledInstances, FIELD_WIDTH));
    if ( this.lookupNotifyable )
      out.println
        ("numberOfLookedupInstances:  " +
         LogUtil.numberToStringFlushLeft(this.numberOfLookedupInstances, FIELD_WIDTH));
    if ( this.lookupNotifyable )
      out.println
        ("numberOfUsedInstances:  " +
         LogUtil.numberToStringFlushLeft
         (this.numberOfLookedupInstances - this.numberOfReleasedInstances, FIELD_WIDTH));
    out.println();

    // Print instance table header:
    out.print
      (LogUtil.flushLeft("Id", FIELD_WIDTH));
    out.print
      (" " + LogUtil.flushLeft("Lifecycle", FIELD_WIDTH));
    if ( this.startable )
      out.print
        (" " + LogUtil.flushLeft("Starts", FIELD_WIDTH));
    if ( this.recyclable )
      out.print
        (" " + LogUtil.flushLeft("Recycles", FIELD_WIDTH));
    if ( this.lookupNotifyable )
      out.print
        (" " + LogUtil.flushLeft("Lookups", FIELD_WIDTH));
    if ( this.lookupNotifyable )
      out.print
        (" " + LogUtil.flushLeft("Releases", FIELD_WIDTH));
    if ( this.startable )
      out.print
        (" " + LogUtil.flushLeft("Stops", FIELD_WIDTH));
    if ( this.lookupNotifyable )
      out.print
        (" " + LogUtil.flushLeft("Owner", FIELD_WIDTH));
    if ( this.startable )
      out.print
        ("  " + LogUtil.flushLeft("First Start", TIME_FIELD_WIDTH));
    if ( this.recyclable )
      out.print
        ("  " + LogUtil.flushLeft("Last Recycle", TIME_FIELD_WIDTH));
    if ( this.lookupNotifyable )
      out.print
        ("  " + LogUtil.flushLeft("Last Lookup", TIME_FIELD_WIDTH));
    if ( this.lookupNotifyable )
      out.print
        ("  " + LogUtil.flushLeft("Last Release", TIME_FIELD_WIDTH));
    if ( this.startable )
      out.print
        ("  " + LogUtil.flushLeft("Last Stop", TIME_FIELD_WIDTH));
    if ( this.disposable )
      out.print
        ("  " + LogUtil.flushLeft("Dispose", TIME_FIELD_WIDTH));
    out.print("\n\n");

    // Print instance table body:
    Collections.sort(this.serviceInstances);
    Iterator iterator = this.serviceInstances.iterator();
    while ( iterator.hasNext() )
      {
        ServiceInstanceStatus instanceStatus = (ServiceInstanceStatus)iterator.next();
        // Id
        out.print
          (LogUtil.numberToStringFlushLeft(instanceStatus.getInstanceId(), FIELD_WIDTH));
        // Lifecycle
        out.print
          (" " + LogUtil.flushLeft(instanceStatus.getLifecycleStatusName(), FIELD_WIDTH));
        // Starts
        if ( this.startable )
          out.print
            (" " + LogUtil.numberToStringFlushLeft(instanceStatus.getNumberOfStarts(), FIELD_WIDTH));
        // Recycles
        if ( this.recyclable )
          out.print
            (" " + LogUtil.numberToStringFlushLeft(instanceStatus.getNumberOfRecycles(), FIELD_WIDTH));
        // Lookups
        if ( this.lookupNotifyable )
          out.print
            (" " + LogUtil.numberToStringFlushLeft(instanceStatus.getNumberOfLookups(), FIELD_WIDTH));
        // Releases
        if ( this.lookupNotifyable )
          out.print
            (" " + LogUtil.numberToStringFlushLeft(instanceStatus.getNumberOfReleases(), FIELD_WIDTH));
        // Stops
        if ( this.startable )
          out.print
            (" " + LogUtil.numberToStringFlushLeft(instanceStatus.getNumberOfStops(), FIELD_WIDTH));
        // Owner
        if ( this.lookupNotifyable )
          out.print
            (" " + LogUtil.flushLeft(instanceStatus.getOwnerLabel(), FIELD_WIDTH));
        // First Start
        if ( this.startable )
          timeToStream(out,instanceStatus.getFirstStartTime(), TIME_FIELD_WIDTH);
        // Last Recycle
        if ( this.recyclable )
          timeToStream(out,instanceStatus.getLastRecycleTime(), TIME_FIELD_WIDTH);
        // Last Lookup
        if ( this.lookupNotifyable )
          timeToStream(out,instanceStatus.getLastLookupTime(), TIME_FIELD_WIDTH);
        // Last Release
        if ( this.lookupNotifyable )
          timeToStream(out,instanceStatus.getLastReleaseTime(), TIME_FIELD_WIDTH);
        // Last Stop
        if ( this.startable )
          timeToStream(out,instanceStatus.getLastStopTime(), TIME_FIELD_WIDTH);
        // Dispose
        if ( this.disposable )
          timeToStream(out,instanceStatus.getDisposeTime(), TIME_FIELD_WIDTH);
        out.println();
      }
  }

  /**
   * Auxiliary method used in {@link #toStream toStream}. Writes a value that is a time.
   */

  protected static void timeToStream (PrintStream out, long time, final int TIME_FIELD_WIDTH)
  {
    out.print
      ("  " +
       (time != -1
        ? LogUtil.timeToString(time)
        : LogUtil.fill(' ', TIME_FIELD_WIDTH)));
  }

  /**
   * Sends an XML representation of this status as SAX events to the specified content
   * handler. If <code>ownDocument</code> is <code>true</code>, the 
   * <code>startDocument</code> and <code>endDocument</code> methods (of the content
   * handler) are called at the beginning resp. end of the document; otherwise, they are not
   * called. If <code>detailed</code> is <code>true</code>, status information concerning
   * each instance are included in the XML; otherwise not.
   */

  public void toSAX (ContentHandler contentHandler, boolean ownDocument, boolean detailed)
    throws SAXException
  {
    GeneralXMLElement element = new GeneralXMLElement(XMLNamespace.STATUS, null);
    element.setDateFormat(TimeFormat.PRECISE);

    // Start document if necessary:
    if ( ownDocument )
      contentHandler.startDocument();

    // Start the root element:
    element.reset();
    element.setLocalName(XMLElement.SERVICE_STATUS);
    element.startToSAX(contentHandler);

    // Write the service_class element:
    element.reset();
    element.setLocalName(XMLElement.SERVICE_CLASS);
    element.addAttribute(XMLAttribute.NAME, this.serviceClass.getName());
    element.addAttribute(XMLAttribute.LOOKUP_NOTIFYABLE, this.lookupNotifyable);
    element.addAttribute(XMLAttribute.RECYCLABLE, this.recyclable);
    element.addAttribute(XMLAttribute.DISPOSABLE, this.disposable);
    element.addAttribute(XMLAttribute.STARTABLE, this.startable);
    element.toSAX(contentHandler);

    // counters:
    element.reset();
    element.setLocalName(XMLElement.NUMBER_OF_CREATED_INSTANCES);
    element.addAttribute(XMLAttribute.VALUE, this.numberOfCreatedInstances);
    element.toSAX(contentHandler);
    if ( this.startable )
      {
        element.reset();
        element.setLocalName(XMLElement.NUMBER_OF_RUNNING_INSTANCES);
        element.addAttribute
          (XMLAttribute.VALUE, this.numberOfStartedInstances - this.numberOfStoppedInstances);
        element.toSAX(contentHandler);
      }
    if ( this.disposable )
      {
        element.reset();
        element.setLocalName(XMLElement.NUMBER_OF_ACTIVE_INSTANCES);
        element.addAttribute
          (XMLAttribute.VALUE, this.numberOfCreatedInstances - this.numberOfDisposedInstances);
        element.toSAX(contentHandler);
      }
    if ( this.recyclable )
      {
        element.reset();
        element.setLocalName(XMLElement.NUMBER_OF_RECYCLED_INSTANCES);
        element.addAttribute
          (XMLAttribute.VALUE, this.numberOfRecycledInstances);
        element.toSAX(contentHandler);
      }
    if ( this.lookupNotifyable )
      {
        element.reset();
        element.setLocalName(XMLElement.NUMBER_OF_LOOKEDUP_INSTANCES);
        element.addAttribute
          (XMLAttribute.VALUE, this.numberOfLookedupInstances);
        element.toSAX(contentHandler);
      }
    if ( this.lookupNotifyable )
      {
        element.reset();
        element.setLocalName(XMLElement.NUMBER_OF_USED_INSTANCES);
        element.addAttribute
          (XMLAttribute.VALUE, this.numberOfLookedupInstances - this.numberOfReleasedInstances);
        element.toSAX(contentHandler);
      }

    if ( detailed )
      {
        // Start instance table:
        element.reset();
        element.setLocalName(XMLElement.INSTANCE_STATUSES);
        element.startToSAX(contentHandler);

        // Instance table body:
        Iterator iterator = this.serviceInstances.iterator();
        while ( iterator.hasNext() )
          {
            ServiceInstanceStatus instanceStatus = (ServiceInstanceStatus)iterator.next();

            // Start instance status element:
            element.reset();
            element.setLocalName(XMLElement.INSTANCE_STATUS);
            element.addAttribute(XMLAttribute.ID, instanceStatus.getInstanceId());
            element.startToSAX(contentHandler);

            // Lifecycle keyword:
            element.reset();
            element.setLocalName(XMLElement.LIFECYCLE);
            element.addAttribute(XMLAttribute.NAME, instanceStatus.getLifecycleStatusName());
            element.toSAX(contentHandler);

            if ( this.startable )
              {
                // Number of starts:
                element.reset();
                element.setLocalName(XMLElement.NUMBER_OF_STARTS);
                element.addAttribute(XMLAttribute.VALUE, instanceStatus.getNumberOfStarts());
                element.toSAX(contentHandler);
              }

            if ( this.recyclable )
              {
                // Number of recycles:
                element.reset();
                element.setLocalName(XMLElement.NUMBER_OF_RECYCLES);
                element.addAttribute(XMLAttribute.VALUE, instanceStatus.getNumberOfRecycles());
                element.toSAX(contentHandler);
              }

            if ( this.lookupNotifyable )
              {
                // Number of lookups:
                element.reset();
                element.setLocalName(XMLElement.NUMBER_OF_LOOKUPS);
                element.addAttribute(XMLAttribute.VALUE, instanceStatus.getNumberOfLookups());
                element.toSAX(contentHandler);
              }

            if ( this.lookupNotifyable )
              {
                // Number of releases:
                element.reset();
                element.setLocalName(XMLElement.NUMBER_OF_RELEASES);
                element.addAttribute(XMLAttribute.VALUE, instanceStatus.getNumberOfReleases());
                element.toSAX(contentHandler);
              }

            if ( this.startable )
              {
                // Number of stops:
                element.reset();
                element.setLocalName(XMLElement.NUMBER_OF_STOPS);
                element.addAttribute(XMLAttribute.VALUE, instanceStatus.getNumberOfStops());
                element.toSAX(contentHandler);
              }

            if ( this.lookupNotifyable )
              {
                // Owner label:
                element.reset();
                element.setLocalName(XMLElement.OWNER);
                element.addAttribute(XMLAttribute.VALUE, instanceStatus.getOwnerLabel());
                element.toSAX(contentHandler);
              }

            if ( this.startable && instanceStatus.getFirstStartTime() != -1 )
              {
                // First start time:
                element.reset();
                element.setLocalName(XMLElement.FIRST_STARTED);
                element.addAttribute(XMLAttribute.VALUE, new Date(instanceStatus.getFirstStartTime()));
                element.toSAX(contentHandler);
              }

            if ( this.recyclable && instanceStatus.getLastRecycleTime() != -1 )
              {
                // Last recycle time:
                element.reset();
                element.setLocalName(XMLElement.LAST_RECYCLED);
                element.addAttribute(XMLAttribute.VALUE, new Date(instanceStatus.getLastRecycleTime()));
                element.addAttribute(XMLAttribute.RAW, instanceStatus.getLastRecycleTime());
                element.toSAX(contentHandler);
              }

            if ( this.lookupNotifyable && instanceStatus.getLastLookupTime() != -1 )
              {
                // Last lookup time:
                element.reset();
                element.setLocalName(XMLElement.LAST_LOOKUP);
                element.addAttribute(XMLAttribute.VALUE, new Date(instanceStatus.getLastLookupTime()));
                element.addAttribute(XMLAttribute.RAW, instanceStatus.getLastLookupTime());
                element.toSAX(contentHandler);
              }

            if ( this.lookupNotifyable  && instanceStatus.getLastReleaseTime() != -1 )
              {
                // Last release time:
                element.reset();
                element.setLocalName(XMLElement.LAST_RELEASE);
                element.addAttribute(XMLAttribute.VALUE, new Date(instanceStatus.getLastReleaseTime()));
                element.addAttribute(XMLAttribute.RAW, instanceStatus.getLastReleaseTime());
                element.toSAX(contentHandler);
              }

            if ( this.startable && instanceStatus.getLastStopTime() != -1 )
              {
                // Last stop time:
                element.reset();
                element.setLocalName(XMLElement.LAST_STOPPED);
                element.addAttribute(XMLAttribute.VALUE, new Date(instanceStatus.getLastStopTime()));
                element.addAttribute(XMLAttribute.RAW, instanceStatus.getLastStopTime());
                element.toSAX(contentHandler);
              }

            if ( this.disposable && instanceStatus.getDisposeTime() != -1 )
              {
                // Dispose time:
                element.reset();
                element.setLocalName(XMLElement.DISPOSED);
                element.addAttribute(XMLAttribute.VALUE, new Date(instanceStatus.getDisposeTime()));
                element.addAttribute(XMLAttribute.RAW, instanceStatus.getDisposeTime());
                element.toSAX(contentHandler);
              }

            // Close instance status element:
            element.reset();
            element.setLocalName(XMLElement.INSTANCE_STATUS);
            element.endToSAX(contentHandler);
          }

        // Close instance table:
        element.reset();
        element.setLocalName(XMLElement.INSTANCE_STATUSES);
        element.endToSAX(contentHandler);
      }

    // Close the root element:
    element.reset();
    element.setLocalName(XMLElement.SERVICE_STATUS);
    element.endToSAX(contentHandler);

    // Close document if necessary:
    if ( ownDocument )
      contentHandler.endDocument();
  }

  /**
   * Writes a XML document, saying that status information are not available, as SAX events
   * to the specified content handler. If <code>ownDocument</code> is <code>true</code>, the 
   * <code>startDocument</code> and <code>endDocument</code> methods (of the content
   * handler) are called at the beginning resp. end of the document; otherwise, they are not
   * called.
   */

  public static void notifyNotAvailable (String reason,
                                         ContentHandler contentHandler,
                                         boolean ownDocument)
    throws SAXException
  {
    GeneralXMLElement element = new GeneralXMLElement(XMLNamespace.STATUS, null);

    // Start document if necessary:
    if ( ownDocument )
      contentHandler.startDocument();

    // Start the root element:
    element.reset();
    element.setLocalName(XMLElement.SERVICE_STATUS);
    element.startToSAX(contentHandler);

    // Write the "not_available" element:
    element.reset();
    element.setLocalName(XMLElement.NOT_AVAILABLE);
    element.addAttribute(XMLAttribute.REASON, reason);
    element.toSAX(contentHandler);

    // Close the root element:
    element.reset();
    element.setLocalName(XMLElement.SERVICE_STATUS);
    element.endToSAX(contentHandler);

    // Close document if necessary:
    if ( ownDocument )
      contentHandler.endDocument();
  }

  /**
   * Sends an XML representation of this status as SAX events to the specified content
   * handler. If <code>ownDocument</code> is <code>true</code>, the 
   * <code>startDocument</code> and <code>endDocument</code> methods (of the content
   * handler) are called at the beginning resp. end of the document; otherwise, they are not
   * called. Status information concerning each instance are included in the XML; thus, this
   * method is the same as
   * {@link #toSAX(ContentHandler,boolean,boolean toSAX(contentHandler, ownDocument, true}.
   */

  public void toSAX (ContentHandler contentHandler, boolean ownDocument)
    throws SAXException
  {
    this.toSAX(contentHandler, ownDocument, true);
  }

  /**
   * Writes the status of the service class with the specified name to the specified stream.
   */

  public static void statusToStream (String serviceClassName, PrintStream out)
  {
    ServiceStatus serviceStatus = getServiceStatus(serviceClassName);
    if ( serviceStatus == null )
      out.println("No status found for class: " + serviceClassName);
    else
      serviceStatus.toStream(out);
  }

  /**
   * Sends an XML representation of the status of the service class with the specified name
   * as SAX events to the specified content handler. If <code>ownDocument</code> is
   * <code>true</code>, the <code>startDocument</code> and <code>endDocument</code> methods
   * (of the content handler) are called at the beginning resp. end of the document;
   * otherwise, they are not called.
   */

  public static void statusToSAX (String serviceClassName,
                                  ContentHandler contentHandler,
                                  boolean ownDocument)
    throws SAXException
  {
    ServiceStatus serviceStatus = getServiceStatus(serviceClassName);
    if ( serviceStatus == null )
      notifyNotAvailable
        ("No status found for class: " + serviceClassName,
         contentHandler, ownDocument);
    else
      serviceStatus.toSAX(contentHandler, ownDocument);
  }

  /**
   * Sends an status overview comprising all services as SAX events to the specified content
   * handler. If <code>ownDocument</code> is <code>true</code>, the
   * <code>startDocument</code> and <code>endDocument</code> methods (of the content
   * handler) are called at the beginning resp. end of the document; otherwise, they are not
   * called.
   */

  public static void overviewToSAX (ContentHandler contentHandler, boolean ownDocument)
    throws SAXException
  {
    GeneralXMLElement element = new GeneralXMLElement(XMLNamespace.STATUS, null);

    // Start document if necessary:
    if ( ownDocument )
      contentHandler.startDocument();

    // Start the root element:
    element.reset();
    element.setLocalName(XMLElement.SERVICE_STATUS_OVERVIEW);
    element.startToSAX(contentHandler);

    // Write the statuses:
    Iterator iterator = serviceStatuses.values().iterator();
    while ( iterator.hasNext() )
      ((ServiceStatus)iterator.next()).toSAX(contentHandler, false, false);

    // Close the root element:
    element.reset();
    element.setLocalName(XMLElement.SERVICE_STATUS_OVERVIEW);
    element.endToSAX(contentHandler);

    // Close document if necessary:
    if ( ownDocument )
      contentHandler.endDocument();
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Returns the <code>ServiceStatus</code> object for the service class with the specified
   * name, or <code>null</code> if no such object exists. The <code>ServiceStatus</code>
   * object is looked up in the {@link #serviceStatuses} map.
   */

  protected static synchronized ServiceStatus getServiceStatus (String serviceClassName)
  {
    return (ServiceStatus)serviceStatuses.get(serviceClassName);
  }
}

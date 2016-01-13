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

package net.mumie.mathletfactory.util.error;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import net.mumie.mathletfactory.appletskeleton.MathletContext;
import net.mumie.mathletfactory.util.exercise.ExerciseConstants;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class creates an error report based on a {@link java.lang.Throwable throwable} and the client system.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class QualityFeedbackReport {
	
	private final static String INDENT = "  ";
	public final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss S";
	
	private Throwable m_error;
	private Element m_rootNode;
	
	private String m_time, m_appletClass, m_javaVersion, m_javaVendor, m_osName, m_osVersion, m_osArch;
	private String m_throwableClass, m_throwableMessage, m_causeClass, m_causeMessage, m_userID, m_problemRef, m_urlPrefix;
	private long m_rawTime;
	private String[] m_errorStacktrace, m_causeStacktrace;
	private String m_userDescription, m_userMailAddress; // will be set after initialisation

	/** 
	 * Creates a new error report based on the given error and the client system.
	 */
	public QualityFeedbackReport(MathletContext applet, Throwable error) {
		 m_error = error;
		
	   Date now = new Date();
	   SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	   m_time = sdf.format(now);
		 m_rawTime = now.getTime();
		 m_appletClass = applet.getClass().getName();
		 m_javaVersion = System.getProperty("java.version");
		 m_javaVendor = System.getProperty("java.vendor");
		 m_osName = System.getProperty("os.name");
		 m_osVersion = System.getProperty("os.version");
		 m_osArch = System.getProperty("os.arch");
		 m_throwableClass = error.getClass().getName();
		 m_throwableMessage = error.getMessage() != null ? error.getMessage() : "";
		 if(error.getCause() != null) {
			 m_causeClass = error.getCause().getClass().getName(); 
			 m_causeMessage = error.getCause().getMessage() != null ? error.getCause().getMessage() : "";
		 }
		 m_userID = applet.getParameter("userID", "???");// for debug purposes in test environment where ID not available
		 m_problemRef = applet.getParameter(ExerciseConstants.PROBLEM_REF_PARAM, "???");// dito
     m_urlPrefix = applet.getParameter(ExerciseConstants.URL_PREFIX_PARAM, "???");// dito
		 m_errorStacktrace = getStackTrace(error);
		 m_causeStacktrace = getStackTrace(error.getCause());
	}
	
	/*
	 * Returns the stacktrace of the given throwable as a string array.
	 */
	private String[] getStackTrace(Throwable error) {
		if(error == null)
			return null;
    StackTraceElement[] stackTrace = error.getStackTrace();
    String[] stackFrames = new String[stackTrace.length + 1];
    stackFrames[0] = error.toString();
    for (int i = 0; i < stackTrace.length; i++) {
  		stackFrames[i + 1] = stackTrace[i].getClassName() + "."
        + stackTrace[i].getMethodName() + " ( line "
        + stackTrace[i].getLineNumber() + " )";
    }
		return stackFrames;
	}
	
	public void setUserDescription(String description) {
		m_userDescription = description;
	}
	
	public String getUserDescription() {
		return m_userDescription;
	}
	
	public void setUserMailAddress(String mailAddress) {
		m_userMailAddress = mailAddress;
	}
	
	public String getUserMailAddress() {
		return m_userMailAddress;
	}
	
//
// these methods returns the collected data as single values
//
	
	public String[] getErrorStackTrace() {
		return m_errorStacktrace;
	}
	
	public String[] getCauseStackTrace() {
		return m_causeStacktrace;
	}
	
	public String getUserID() {
		return m_userID;
	}
	
	public String getProblemRef() {
		return m_problemRef;
	}
  
  public String getURLPrefix() {
    return m_urlPrefix;
  }
	
	public String getTimeString() {
		return m_time;
	}
	
	public String getTimeFormat() {
		return DATE_FORMAT;
	}
	
	public long getRawTime() {
		return m_rawTime;
	}
	
	public String getAppletClass() {
		return m_appletClass;
	}
	
	public String getJavaVersion() {
		return m_javaVersion;
	}
	
	public String getJavaVendor() {
		return m_javaVendor;
	}
	
	public String getOSName() {
		return m_osName;
	}
	
	public String getOSVersion() {
		return m_osVersion;
	}
	
	public String getOSArch() {
		return m_osArch;
	}
	
	public String getThrowableClass() {
		return m_throwableClass;
	}
	
	public String getThrowableMessage() {
		return m_throwableMessage;
	}
	
	public String getCauseClass() {
		return m_causeClass;
	}
	
	public String getCauseMessage() {
		return m_causeMessage;
	}
	
	/*
	 * Creates the xml-document that will be available under the field "m_rootNode".
	 */
	private void createXML() {
    try {
			 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		   dbf.setNamespaceAware(true);
		   DocumentBuilder db = dbf.newDocumentBuilder();
		   Document doc = db.getDOMImplementation().createDocument("http://www.mumie.net/xml-namespace/quality-feedback", "applet_quality_feedback", null);
		
		   doc.getDocumentElement().appendChild(doc.createTextNode("\n" + INDENT));
		   
		   Element timeNode = doc.createElement("time");
		   timeNode.setAttribute("value", getTimeString());
		   timeNode.setAttribute("format", getTimeFormat());
		   timeNode.setAttribute("raw", getRawTime() + "");
		   doc.getDocumentElement().appendChild(timeNode);
		   
		   doc.getDocumentElement().appendChild(doc.createTextNode("\n" + INDENT));
		   
		   Element userNode = doc.createElement("user");
		   userNode.setAttribute("id", getUserID());
		   doc.getDocumentElement().appendChild(userNode);
		
		   doc.getDocumentElement().appendChild(doc.createTextNode("\n" + INDENT));
		   
		   Element problemNode = doc.createElement("problem");
		   problemNode.setAttribute("ref_id", getProblemRef());
		   doc.getDocumentElement().appendChild(problemNode);
		
		   doc.getDocumentElement().appendChild(doc.createTextNode("\n" + INDENT));
		   
       Element serverNode = doc.createElement("server");
       serverNode.setAttribute("url_prefix", getURLPrefix());
       doc.getDocumentElement().appendChild(serverNode);
    
       doc.getDocumentElement().appendChild(doc.createTextNode("\n" + INDENT));
       
		   Element appletNode = doc.createElement("applet");
		   appletNode.setAttribute("class", getAppletClass());
		   doc.getDocumentElement().appendChild(appletNode);
		
		   doc.getDocumentElement().appendChild(doc.createTextNode("\n" + INDENT));
		   
		   Element javaNode = doc.createElement("java");
		   javaNode.setAttribute("version", getJavaVersion());
		   javaNode.setAttribute("vendor", getJavaVendor());
		   doc.getDocumentElement().appendChild(javaNode);
		
		   doc.getDocumentElement().appendChild(doc.createTextNode("\n" + INDENT));
		   
		   Element systemNode = doc.createElement("system");
		   systemNode.setAttribute("name", getOSName());
		   systemNode.setAttribute("version", getOSVersion());
		   systemNode.setAttribute("arch", getOSArch());
		   doc.getDocumentElement().appendChild(systemNode);
		   
		//   doc.getDocumentElement().appendChild(doc.createTextNode("\n" + INDENT));
		
		   addThrowableNode(m_error, doc.getDocumentElement(), INDENT);
		//   doc.getDocumentElement().appendChild(doc.createTextNode("\n" + INDENT));
		   
		   Element userCommentNode = doc.createElement("user_comment");
		   if(getUserDescription() != null)
		  	 userCommentNode.appendChild(doc.createTextNode(getUserDescription()));
		   doc.getDocumentElement().appendChild(userCommentNode);
		   
		   Element userEMailNode = doc.createElement("user_e-mail");
		   if(getUserMailAddress() != null)
		  	 userEMailNode.appendChild(doc.createTextNode(getUserMailAddress()));
		   doc.getDocumentElement().appendChild(userEMailNode);
		   
		   doc.getDocumentElement().appendChild(doc.createTextNode("\n"));
		   
		   m_rootNode = doc.getDocumentElement();
    } catch (FactoryConfigurationError e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    }
	}
	
	/*
	 * Adds a "throwable"-node to the parent node
	 */
	private void addThrowableNode(Throwable t, Element parent, String indent) {
		Document doc = parent.getOwnerDocument();
    Element throwableNode = doc.createElement("throwable");
    throwableNode.setAttribute("class", getThrowableClass());
    parent.appendChild(doc.createTextNode("\n" + indent));
    parent.appendChild(throwableNode);
    throwableNode.appendChild(doc.createTextNode("\n" + indent + INDENT));
  		Element messageNode = doc.createElement("message");
  		messageNode.appendChild(doc.createTextNode(getThrowableMessage()));
  		throwableNode.appendChild(messageNode);
    throwableNode.appendChild(doc.createTextNode("\n" + indent + INDENT));
  		Element stackTraceNode = doc.createElement("stacktrace");
  		
  		addStacktrace(stackTraceNode, t, indent + INDENT);

//  		throwableNode.appendChild(doc.createTextNode(INDENT + INDENT));
  		throwableNode.appendChild(stackTraceNode);
//    throwableNode.appendChild(doc.createTextNode("\n" + INDENT));
    
		Element causeNode = doc.createElement("cause");
		if(t.getCause() != null) {
			addThrowableNode(t.getCause(), causeNode, indent + INDENT + INDENT);
			//	stackTraceNode.appendChild(doc.createTextNode("\n" + INDENT + INDENT + INDENT + "caused by: "));
//			addStacktrace(causeNode, t.getCause(), INDENT + INDENT + INDENT + INDENT);
		}	  		
		throwableNode.appendChild(doc.createTextNode("\n" + indent + INDENT));
		throwableNode.appendChild(causeNode);
		throwableNode.appendChild(doc.createTextNode("\n" + indent));
    
    parent.appendChild(doc.createTextNode("\n" + indent));
	}
	
	/*
	 * Adds a "stacktrace"-node to the parent node
	 */
	private void addStacktrace(Node parent, Throwable t, String indent) {
    StackTraceElement[] stackTrace = t.getStackTrace();
    String[] stackFrames = new String[stackTrace.length + 1];
    stackFrames[0] = t.toString();
    
    parent.appendChild(parent.getOwnerDocument().createTextNode("\n" + indent + INDENT + stackFrames[0]));
    for (int i = 0; i < stackTrace.length; i++) {
    		stackFrames[i + 1] = stackTrace[i].getClassName() + "."
          + stackTrace[i].getMethodName() + " ( line "
          + stackTrace[i].getLineNumber() + " )";
    		parent.appendChild(parent.getOwnerDocument().createTextNode("\n" + indent + INDENT + stackFrames[i + 1]));
    }
    parent.appendChild(parent.getOwnerDocument().createTextNode("\n" + indent));
	}
	
	/**
	 * Returns the XML content containing all data nodes of this error report.
	 */
	public Element getXMLContent() {
		if(m_rootNode == null)
			createXML();
		return m_rootNode;
	}
	
	/**
	 * Returns the XML content containing all data nodes of this error report as a string.
	 */
	public String toXMLString() {
		return XMLUtils.nodeToString(getXMLContent());
	}
}

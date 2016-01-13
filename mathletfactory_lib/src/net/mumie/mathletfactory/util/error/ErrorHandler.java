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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import net.mumie.japs.client.AppletJapsClient;
import net.mumie.japs.client.JapsClient;
import net.mumie.japs.client.JapsPath;
import net.mumie.japs.client.JapsRequestParam;
import net.mumie.mathletfactory.appletskeleton.MathletContext;
import net.mumie.mathletfactory.appletskeleton.system.SystemDescriptor;
import net.mumie.mathletfactory.appletskeleton.util.dialog.DialogIDIF;
import net.mumie.mathletfactory.util.exercise.NoDataSheetException;
import net.mumie.mathletfactory.util.extension.UnsupportedExtensionException;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;

/**
 * This class handles all occuring errors by showing a message dialog along with a problem description and the 
 * possibility to report the error to a server.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class ErrorHandler {
	
	private Vector m_errors = new Vector();
	private MathletContext m_applet;
	private boolean m_firstErrorHandled = false;
	
  private static MumieLogger m_logger = MumieLogger.getLogger(ErrorHandler.class);
  private final static LogCategory HANDLE = m_logger.getCategory("error.handle");
  private final static LogCategory REPORT = m_logger.getCategory("error.report");

  /**
	 * Creates a new error handle for an applet. Every applet should not have more than one error handler.
	 */
	public ErrorHandler(MathletContext applet) {
		m_applet = applet;
	}
	
	/**
	 * Sends the given error report to a server.
	 */
	public void sendReport(QualityFeedbackReport report) {
    try {
	    JapsClient client = new AppletJapsClient(m_applet
	        .getParameter("urlPrefix"), m_applet.getJApplet()) {
	      		protected void log (String message) {
	      			m_logger.log(REPORT, "AppletJapsClient: " + message);
	      		}
	      	};
	    Map params = new Hashtable();
	    String subject = report.getProblemRef()+"/"+report.getAppletClass()+": "+ report.getThrowableClass()+": "+report.getThrowableMessage();
	    params.put(JapsRequestParam.SUBJECT, subject);
	    params.put(JapsRequestParam.REPORT, report.toXMLString());
			HttpURLConnection connection = client.post(JapsPath.QF_APPLET, params);
      if (connection != null) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
            connection.getInputStream()));
        String line;
        String message = new String();
        while ((line = reader.readLine()) != null) {
          message += line + "\n";
        }
        reader.close();
  			m_applet.showDialog(DialogIDIF.QUALITY_FEEDBACK_SUBMIT_OK, new String[] { message });
     } else {
  			m_applet.showDialog(DialogIDIF.QUALITY_FEEDBACK_SUBMIT_CANCELED, null);
      }
		} catch (Throwable t) {
			m_applet.showDialog(DialogIDIF.QUALITY_FEEDBACK_SUBMIT_ERROR, new Throwable[] { t });
		}
	}
	
	/**
	 * Adds an error to the internal list of errors and informs the handler to react on it (usually by showing an error dialog).
	 */
	public void addError(Throwable error) {
		m_logger.log(HANDLE, "error caught: " + error.getClass().getName());
		m_errors.add(error);
		if(!hasErrors() || m_firstErrorHandled)
			error.printStackTrace();
	}
	
	public void handleFirstError() {
		if(!hasErrors() || m_firstErrorHandled)
			return;
		Throwable error = getFirstError();
		if(error instanceof NoDataSheetException)
			return;
		m_logger.log(HANDLE, "handle error: " + error.getClass().getName());
		if(error instanceof UnsupportedExtensionException && ((UnsupportedExtensionException)error).getCauseType() == UnsupportedExtensionException.NEWER_JVM_VERSION_NEEDED_ERROR) {
			m_applet.showDialog(DialogIDIF.NEWER_JAVA_VERSION_NEEDED_DIALOG, new String[] { 
					SystemDescriptor.getJREVersion().toString(false, false),
					m_applet.getMathletRuntime().getMinJVMVersion().toString(false, false) });
		} else {
			error.printStackTrace();
			ErrorCaughtDialog dialog = new ErrorCaughtDialog(m_applet, getReport(error));
			dialog.showDialog();
		}
//		m_errors.remove(0);
		m_firstErrorHandled = true;
	}
	
	/**
	 * Returns if errors have been reported until now. 
	 */
	public boolean hasErrors() {
		return m_errors.size() > 0;
	}
	
	/**
	 * Returns the first error (i.e. throwable) that occured or null if no error was added.
	 */
	public Throwable getFirstError() {
		if(m_errors.size() == 0)
			return null;
		else
			return (Throwable) m_errors.get(0);
	}
	
	/**
	 * Returns the error report of the first error (i.e. throwable) that occured or null if no error was added..
	 */
	public QualityFeedbackReport getReport(Throwable error) {
		return new QualityFeedbackReport(m_applet, error);
	}
}

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

package net.mumie.mathletfactory.util.exercise;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;

import net.mumie.cocoon.util.ProblemCorrector;
import net.mumie.japs.client.AppletJapsClient;
import net.mumie.japs.client.JapsClient;
import net.mumie.japs.client.JapsClientException;
import net.mumie.japs.client.JapsClientRejectedAfterLoginExcpetion;
import net.mumie.japs.client.JapsPath;
import net.mumie.japs.client.JapsRequestParam;
import net.mumie.japs.client.JapsResponseHeader;
import net.mumie.japs.datasheet.DataSheet;
import net.mumie.japs.datasheet.DataSheetException;
import net.mumie.mathletfactory.appletskeleton.MathletContext;
import net.mumie.mathletfactory.display.util.StyledTextButton;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.util.ResourceManager;
import net.mumie.mathletfactory.util.exercise.receipt.Receipt;
import net.mumie.mathletfactory.util.exercise.receipt.ReceiptViewer;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;
import net.mumie.mathletfactory.util.xml.DatasheetRenderer;
import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;
import net.mumie.mathletfactory.util.xml.MathMLSerializable;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class represents an exercise in the Mumie exercise framework.
 * It needs to work for an applet (a {@link net.mumie.mathletfactory.appletskeleton.BaseApplet} more precisely), even
 * if the only constructor officially declares that it only needs the specified
 * interface implementation. The implementing applet must be therefore an instance
 * of BaseApplet with at least 1 implementation of the 3 exercise interfaces.
 *
 * @see net.mumie.mathletfactory.util.exercise.MumieExerciseIF
 * @see net.mumie.mathletfactory.util.exercise.MultipleTasksIF
 * @see net.mumie.mathletfactory.util.exercise.SelectableDataIF
 *
 * @author gronau
 * @mm.docstatus finished
 */
public class MumieExercise implements ExerciseConstants {

	/* 
	 * Constants for messages displayed in dialogs.
	 */
	
	protected final static String DEFAULT_FILE_NAME_QUERY_MESSAGE = ResourceManager.getMessage("exercise.DEFAULT_FILE_NAME_QUERY_MESSAGE");
  
	protected final static String SAVING_SUCCESSFUL_QUERY_MESSAGE = ResourceManager.getMessage("exercise.SAVING_SUCCESSFUL_QUERY_MESSAGE");
  
	protected final static String SAVING_SUCCESSFUL_NO_RIGHTS_MESSAGE = ResourceManager.getMessage("exercise.SAVING_SUCCESSFUL_NO_RIGHTS_MESSAGE");

	protected final static String SAVING_CANCELED_QUERY_MESSAGE = ResourceManager.getMessage("exercise.SAVING_CANCELED_QUERY_MESSAGE");
  
	protected final static String SAVING_ERROR_QUERY_MESSAGE = ResourceManager.getMessage("exercise.SAVING_ERROR_QUERY_MESSAGE");
    
	protected final static String RETRY_SAVING = ResourceManager.getMessage("exercise.RETRY_SAVING");
  
	protected final static String RETRY = ResourceManager.getMessage("exercise.RETRY");
  
	protected final static String REPORT_ERROR = ResourceManager.getMessage("exercise.REPORT_ERROR");
  
	protected final static String SAVING_RECEIPT_ERROR = ResourceManager.getMessage("exercise.SAVING_RECEIPT_ERROR");
  
	protected final static String RECEIVING_RECEIPT_ERROR = ResourceManager.getMessage("exercise.RECEIVING_RECEIPT_ERROR");
  
	protected final static String FILE_EXISTS_QUERY_MESSAGE = ResourceManager.getMessage("exercise.FILE_EXISTS_QUERY_MESSAGE");

	protected final static String NO_RIGHTS_QUERY_MESSAGE = ResourceManager.getMessage("exercise.NO_RIGHTS_QUERY_MESSAGE");
  
	protected final static String NO_CONNECTION_TO_HOST = ResourceManager.getMessage("exercise.NO_CONNECTION_TO_HOST");
  
	protected final static String TRY_AGAIN_LATER = ResourceManager.getMessage("exercise.TRY_AGAIN_LATER");
  
	protected final static String CANCELED_BY_USER = ResourceManager.getMessage("exercise.CANCELED_BY_USER");
  
  /*
   * Constants for sending and saving.
   */
  
  /** Constant for a POST request. */
  public final static int POST_REQUEST = 0;
  
  /** Constant for a GET request. */
  public final static int GET_REQUEST = 1;

  /** Constant for an interactive saving with status messages diplayed to the user. */
  public final static int FEEDBACK_SAVING = 0;
  
  /** Constant for a quiet saving without any status messages and saving of a receipt. */
  public final static int QUIET_SAVING = 1;
  
  /** Constant for the maximum number of tries for connection requests. */
  public final static int MAX_NR_OF_TRIES = 2;

  /*
   * Fields.
   */
  private int m_subtask = 1;
  
  private int m_exerciseType = 0;
  
  private String m_oldReceiptPath;
  
  private Class m_numberClass;

  private int m_dim = -1;
  
  private boolean m_isEditable;

  private ExerciseButtonPanel m_buttonPanel = new ExerciseButtonPanel();

  protected DataSheet m_questionSheet;

  protected DataSheet m_temporarySheet;
  
  protected DataSheet m_localSheet;
  
	private GenericSelectionHelper m_genSelectionHelper;

  private MumieExerciseIF m_mumieExerciseApplet;
  
  private MathletContext m_mathletContext;
  
  private static MumieLogger m_logger = MumieLogger.getLogger(MumieExercise.class);
  private final static LogCategory LOAD = m_logger.getCategory("exercise.load");
  private final static LogCategory SAVE = m_logger.getCategory("exercise.save");
  private final static LogCategory CONNECT = m_logger.getCategory("exercise.connect");
  private final static LogCategory RECEIPT = m_logger.getCategory("exercise.receipt");
  
  /*
   * Methods.
   */
  
  /**
   * Initializes a new MumieExercise for the mathlet <code>mathlet</code>
   * @param mathlet must be an instance of {@link net.mumie.mathletfactory.appletskeleton.BaseApplet} implementing {@link MumieExerciseIF} or its extending interfaces.
   */
  public MumieExercise(MumieExerciseIF mathlet) {
    m_exerciseType = EXERCISE_DEFAULT_TYPE;
  	setMathletContext(mathlet);
  }
  
  /**
   * Initializes a new exercise with given type for the mathlet <code>mathlet</code>
   * @param exerciseType the type as a constant
   * @param mathlet must be an instance of {@link net.mumie.mathletfactory.appletskeleton.BaseApplet} implementing {@link MumieExerciseIF} or its extending interfaces.
   * @see ExerciseConstants#EXERCISE_DEFAULT_TYPE
   * @see ExerciseConstants#EXERCISE_MC_TYPE
   * @see ExerciseConstants#EXERCISE_TEXT_TYPE
   */
  public MumieExercise(int exerciseType, MumieExerciseIF mathlet) {
    m_exerciseType = exerciseType;
    setMathletContext(mathlet);
  }
  
  /**
   * Initializes a new MumieExercise for the mathlet <code>mathlet</code>
   * @param mathlet must be an instance of {@link net.mumie.mathletfactory.appletskeleton.BaseApplet} implementing {@link MumieExerciseIF} or its extending interfaces.
   */
  public MumieExercise(MathletContext mathlet) {
    m_exerciseType = EXERCISE_DEFAULT_TYPE;
  	if(mathlet instanceof MumieExerciseIF)
  		setMathletContext((MumieExerciseIF) mathlet);
  	else
  		throw new IllegalArgumentException("Mathlet must implement an exercise interface!");
  }

  protected void setMathletContext(MumieExerciseIF mathlet) {
    mathlet.getExerciseSupport().setExercise(this);
    m_mathletContext = mathlet;
    m_mumieExerciseApplet = mathlet;
    m_genSelectionHelper = new GenericSelectionHelper();
    
    initializeQuestionSheet();
    
    // add debug button in debug mode
    if(m_mathletContext.isDebugMode()) {
    	m_mathletContext.addControl(new SubtaskSelector(), MathletContext.BOTTOM_PANE);
    	SelectButton selectButton = new SelectButton();
    	m_mathletContext.addControl(selectButton, MathletContext.BOTTOM_PANE);
    }
  }
  
  /**
   * Returns the type of this exercise instance.
   */
  public int getExerciseType() {
    return m_exerciseType;
  }
  
  /**
   * Returns the type name of this exercise instance.
   */
  public String getExerciseTypeName() {
    switch(getExerciseType()) {
      case EXERCISE_DEFAULT_TYPE:
        return EXERCISE_DEFAULT_TYPE_NAME;
      case EXERCISE_MC_TYPE:
        return EXERCISE_MC_TYPE_NAME;
      case EXERCISE_TEXT_TYPE:
        return EXERCISE_TEXT_TYPE_NAME;
      default:
        throw new RuntimeException("Wrong exercise type: " + getExerciseType());
    }
  }
  
  protected String getParameter(String name) {
  	return m_mathletContext.getParameter(name);
  }
    
  protected void debugCorrector(DataSheet answerSheet) throws Throwable {
  	ProblemCorrector corrector = m_mathletContext.getExerciseSupport().loadCorrector();
  	new CorrectorDebugger(m_mathletContext, corrector, answerSheet);
  }
  
  private void initializeQuestionSheet() {
    try {
      m_questionSheet = loadQuestionSheet();
      if (m_questionSheet == null)
        throw new NoDataSheetException("Unknown cause");
      String editableParam = getParameter(HOMEWORK_EDITABLE_PARAM);
      m_isEditable = (editableParam == null ? true : Boolean.valueOf(editableParam).booleanValue());
      m_numberClass = XMLUtils.getNumberClassFromField(m_questionSheet.getAsString(FIELD_PATH, true));
      Integer dim = m_questionSheet.getAsInteger(DIMENSION_PATH);
      if (dim != null)
        m_dim = dim.intValue();
      m_logger.log(LOAD, "Download successful!\n");
    } catch (DataSheetException e) {
    	m_mathletContext.reportError(e);
    }
  }
  
  private DataSheet loadQuestionSheet() {
    try {
    	// download debug data sheet from local file
    	if(m_mathletContext.isDebugMode()) {
    		// check if datasheet should be selected manually at start
    		if(getParameter(ExerciseConstants.INPUT_DATASHEET_PARAM).equalsIgnoreCase("none")) {
    			JFileChooser chooser = new JFileChooser();
    			chooser.setDialogTitle("Select input data sheet...");
    			chooser.setAcceptAllFileFilterUsed(true);
    			chooser.setMultiSelectionEnabled(false);
    			chooser.setFileFilter(new FileFilter() {
						public boolean accept(File f) {
							return f.isDirectory() || (f.isFile() && f.getName().endsWith(".datasheet.xml"));
						}
						
						public String getDescription() {
							return "Data-Sheet Files (*.datasheet.xml)";
						}
    			});
    			int choice = chooser.showOpenDialog(getMathletContext().getAppletContentPane());
    			System.out.println("MumieExercise: loading datasheet from " + chooser.getSelectedFile());
    			if(choice == JFileChooser.APPROVE_OPTION)
    				return loadLocalQuestionSheet(chooser.getSelectedFile());
    			else
    				throw new NoDataSheetException("No datasheet selected");
    		}
    		return loadLocalQuestionSheet(getParameter(ExerciseConstants.INPUT_DATASHEET_PARAM));
    	} 
    	// download preview data sheet from local file
    	else if(m_mathletContext.isPreviewMode()) {
    		return loadLocalQuestionSheet(getParameter(ExerciseConstants.INPUT_DATASHEET_PARAM));
    	}
    	// download server data sheet from remote file
    	else if(m_mathletContext.isOnlineMode()) {
    		return loadRemoteQuestionSheet();
    	} else
        throw new RuntimeException("Mathlet context does not run in exercise mode");
    } catch (NoDataSheetException noDsEx) {
    	// catch and forward No-DataSheet-Exceptions -> avoid duplicate No-DS-Exceptions 
    	throw noDsEx;
    } catch (Throwable e) {
    	m_mathletContext.reportError(e);
    	// stop applet loading
    	throw new NoDataSheetException("Abort applet loading");
    }
  }
    
  private DataSheet loadLocalQuestionSheet(String inputDatasheetPath) throws Throwable {
  	URI uri = new URI(inputDatasheetPath);
  	if(uri.getScheme() == null) {
  		File inputFile = new File(getParameter(ExerciseConstants.INPUT_DATASHEET_PARAM));
  		if(inputFile.exists() && inputFile.canRead())
  			return loadLocalQuestionSheet(inputFile);
  		else
  			return loadLocalQuestionSheet(new URL(getParameter(ExerciseConstants.INPUT_DATASHEET_PARAM)));
  	}
  	else if(uri.getScheme().equals("file"))
  		return loadLocalQuestionSheet(new File(uri.getPath()));
  	else if(uri.getScheme().equals("http"))
  		return loadLocalQuestionSheet(uri.toURL());
  	throw new IllegalArgumentException("Unknown scheme of datasheet path: " + uri.getScheme());
  }
  
  private DataSheet loadLocalQuestionSheet(File inputDatasheetFile) throws Throwable {
  	m_logger.log(LOAD, "Loading local data sheet from file " + inputDatasheetFile.getAbsolutePath() + " ...");
    return XMLUtils.loadDataSheetFromFile(inputDatasheetFile.getAbsolutePath());
  }
  
  private DataSheet loadLocalQuestionSheet(URL inputDatasheetURL) throws Throwable {
  	m_logger.log(LOAD, "Loading local data sheet from url " + inputDatasheetURL + " ...");
    return XMLUtils.loadDataSheetFromURL(inputDatasheetURL);
  }

  private DataSheet loadRemoteQuestionSheet() throws Throwable {
    m_logger.log(LOAD, "Downloading server data sheet...");
    m_logger.log(LOAD, "Parameter \"urlPrefix\"=" + getParameter(ExerciseConstants.URL_PREFIX_PARAM));
    m_logger.log(LOAD, "Parameter \"problemRef\"=" + getParameter(ExerciseConstants.PROBLEM_REF_PARAM));

  	// retrieve request parameters
    Map params = new Hashtable();
    params.put(JapsRequestParam.PROBLEM_REF, getParameter(ExerciseConstants.PROBLEM_REF_PARAM));
    params.put(JapsRequestParam.COURSE, getParameter(ExerciseConstants.COURSE_ID_PARAM));
    params.put(JapsRequestParam.WORKSHEET, getParameter(ExerciseConstants.WORKSHEET_ID_PARAM));
    params.put(JapsRequestParam.PROBLEM, getParameter(ExerciseConstants.PROBLEM_ID_PARAM));
    
    // send request to server
    HttpURLConnection connection = createDownloadConnection(params);
    // abort if user canceled downloading
    if (connection == null)
    	throw new NoDataSheetException("Canceled by user");
    Document document = XMLUtils.createDocumentBuilder().parse(connection.getInputStream());
    m_logger.log(LOAD, "Loading of input data sheet from server successful!");
    return new DataSheet(document);
  }

  private void clearSubtask() {
    try {
    	m_mumieExerciseApplet.clearSubtask();
    } catch (Throwable e) {
    	m_mathletContext.reportError(e);
    }
  }
  
  public void save() {
  	processAnswerSheet(FEEDBACK_SAVING);
  }
  
  public void sendAnswers() {
  	processAnswerSheet(FEEDBACK_SAVING);
  }
  
  public void sendAnswersQuietly() {
  	processAnswerSheet(QUIET_SAVING);
  }

  private void processAnswerSheet(int savingType) {
    try {
    	// clearing data sheet for "fresh" applet values
      clearTemporarySheet();
      
      // collecting data from applet
      m_logger.log(SAVE, "collecting answers into temporary sheet");
      if(!m_mumieExerciseApplet.collectAnswers())
      	return;
      
      // creating answer sheet with new and old answers
      DataSheet answerSheet = createAnswerSheet();
    	
    	// debug mode
      if (m_mathletContext.isDebugMode()) {
      	m_logger.log(SAVE, "start saving in debug mode...");
        updateQuestionSheet(answerSheet);
        // open corrector debugger
        debugCorrector(answerSheet);
      }
    	// preview mode
      else if(m_mathletContext.isPreviewMode()) {
      	m_logger.log(SAVE, "start saving in preview mode...");
        updateQuestionSheet(answerSheet);
        // save to file
      	saveAnswerSheet(answerSheet);
      }
      // server mode
      else if(m_mathletContext.isOnlineMode()) {
        m_logger.log(SAVE, "start saving in remote japs mode...");
      	sendAnswerSheet(answerSheet, savingType);
      }
    } catch (Throwable e) {
    	m_mathletContext.reportError(e);
    }
  }
  
  private void saveAnswerSheet(DataSheet answerSheet) throws Throwable {
  	URI uri = new File(getParameter(ExerciseConstants.OUTPUT_DATASHEET_PARAM)).toURI();
  	if(uri.getScheme() == null) {
  		File outputFile = new File(getParameter(ExerciseConstants.OUTPUT_DATASHEET_PARAM));
  		if(outputFile.canWrite())
  			XMLUtils.saveDataSheetToFile(answerSheet, outputFile);
  		else
  			throw new IllegalArgumentException("Cannot write to output datasheet!");
  	}
  	else if(uri.getScheme().equals("file"))
    	XMLUtils.saveDataSheetToFile(answerSheet, new File(uri.getPath()));
  	else if(uri.getScheme().equals("http"))
  		throw new IllegalArgumentException("The output Datasheet cannot be an URL!");
  	m_mathletContext.showMessageDialog(SAVING_SUCCESSFUL_QUERY_MESSAGE, "errors.SAVING_SUCCESSFUL_QUERY_MESSAGE"); 
  }
  
  private void sendAnswerSheet(DataSheet answerSheet, int savingType) throws Throwable {
  	m_logger.log(SAVE, "Sending answer data sheet to server...");
  	// retrieve request parameters
    Map params = new Hashtable();
    params.put(JapsRequestParam.PROBLEM_REF, getParameter(ExerciseConstants.PROBLEM_REF_PARAM));
    params.put(JapsRequestParam.CONTENT, answerSheet.toXMLCode());
    if(savingType == QUIET_SAVING) {// optional, only send if no receipt should be received
    	params.put(JapsRequestParam.SEND_RECEIPT, "false");
    }
    params.put(JapsRequestParam.COURSE, getParameter(ExerciseConstants.COURSE_ID_PARAM));
    params.put(JapsRequestParam.WORKSHEET, getParameter(ExerciseConstants.WORKSHEET_ID_PARAM));
    params.put(JapsRequestParam.PROBLEM, getParameter(ExerciseConstants.PROBLEM_ID_PARAM));
    
    // send request to server
    HttpURLConnection connection = createUploadConnection(params);
    if (connection != null) {
    	m_logger.log(SAVE, "Sending of answer data sheet to server successful!");
    	
      // Check the X-Mumie-Status header:
      String header = connection.getHeaderField(JapsResponseHeader.STATUS);
      if(header.equals("OK. Issued receipt")) {
      	
      	m_logger.log(SAVE, "issued receipt");

      	// update question sheet with newly saved answers
      	updateQuestionSheet(answerSheet);
      	
      	// save receipt as temporary file --> used for receipt viewer and for copying to user folder
    		File tempReceiptFile = null;
    		try {
    			tempReceiptFile = File.createTempFile("receipt_", null);
        	saveReceipt(connection.getInputStream(), tempReceiptFile);
    		} catch(SecurityException se) {
        	m_logger.log(SAVE, "applet has no rights to create a temporary file");
        	m_mathletContext.showMessageDialog(SAVING_SUCCESSFUL_NO_RIGHTS_MESSAGE, "errors.SAVING_SUCCESSFUL_NO_RIGHTS_MESSAGE"); 
      		return;
    		} catch(Throwable t) {
	       	m_logger.log(SAVE, "saving temporary receipt error: " + t);
	  			String text = SAVING_RECEIPT_ERROR + "<font size=\"-1\">" + t + "</font><br><br>";
	  			m_mathletContext.showErrorDialog(text);
	  			return;
    		}
      	
      	// check if system property "user.home" is readable
      	// true if applet is signed and certificate was accepted by the user
      	try {
      		System.getSecurityManager().checkPropertyAccess("user.home");
      	} catch(SecurityException ace) { // not readable
        	m_logger.log(SAVE, "applet has no rights to save to user account");
        	m_mathletContext.showMessageDialog(SAVING_SUCCESSFUL_NO_RIGHTS_MESSAGE, "errors.SAVING_SUCCESSFUL_NO_RIGHTS_MESSAGE"); 
      		return;
      	}

      	String path = null;
      	if(m_oldReceiptPath != null) {
      		path = m_oldReceiptPath;
        } else {
        	path = System.getProperty("user.home") + File.separator + "TUMULT";
        	m_oldReceiptPath = path;
      	}
    		String name = connection.getHeaderField(JapsResponseHeader.FILENAME);
      	SaveDialog dialog = new SaveDialog(path, name, tempReceiptFile);
      } else if(header.equals("OK")) {
      	m_logger.log(SAVE, "saving successful");
      	// update question sheet with newly saved answers
      	updateQuestionSheet(answerSheet);
      	if(savingType == FEEDBACK_SAVING)
      		m_mathletContext.showMessageDialog(SAVING_SUCCESSFUL_QUERY_MESSAGE, "errors.SAVING_SUCCESSFUL_QUERY_MESSAGE"); 
      } else { //if (header.startsWith("ERROR: ")) {
      	m_logger.log(SAVE, "error while sending");
        // error while sending -> retry?
      	if(savingType == QUIET_SAVING) // do nothing on quiet saving
      		return;
      	String text = SAVING_ERROR_QUERY_MESSAGE + "<br>(" + header + ")" + RETRY_SAVING;
      	if(m_mathletContext.showConfirmDialog(text, ResourceManager.getMessage("error"))) {
      		processAnswerSheet(savingType);
      	}
      }
    } else {
    	m_logger.log(SAVE, "Login canceled by user");
    	m_mathletContext.showMessageDialog(CANCELED_BY_USER);
    }
  }
  
  /**
   * Reads in a receipt file from an input stream and stores its content to the given destination file.
   */
  private void saveReceipt(InputStream in, File destFile) throws IOException {
    FileOutputStream out = new FileOutputStream(destFile);
    byte[] buffer = new byte[1024];
    int length;
    while ( (length = in.read(buffer)) != -1 ) {
      out.write(buffer, 0, length);
    }
    out.close();
  }
  
	/**
	 * Copies all elements below <code>path</code> from the <code>source</code> datasheet to the <code>target</code> datasheet.
	 */
  protected void copyData(DataSheet sourceDS, String path, DataSheet targetDS) throws DataSheetException {
    String[] problemPaths = sourceDS.getAllDataPaths();
    for(int i = 0; i < problemPaths.length; i++) {
    	if(problemPaths[i].startsWith(path.endsWith(PATH_SEPARATOR) ? path : path + PATH_SEPARATOR)) {
    		Element e = sourceDS.getDataElement(problemPaths[i]);
    		if(e.getChildNodes().getLength() == 1 && e.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE)
    			targetDS.put(problemPaths[i], e.getChildNodes().item(0).getNodeValue().trim());
    		else
    			targetDS.put(problemPaths[i], targetDS.getDocument().importNode(sourceDS.getAsElement(problemPaths[i]), true));
    	}
    }
  }
  
  /**
   *  Sends a HTTP request to the server and returns the established connection on success.
   */
  private HttpURLConnection sendRequest(int requestType, String url, String path, Map params) throws Throwable {
		HttpURLConnection connection = null;
		Throwable error = null;
    int nrOfTries = 0;
    do {
    	nrOfTries++;
    	m_logger.log(CONNECT, "Sending request, " + nrOfTries + ". try...");
    	try {
      	JapsClient client = new AppletJapsClient(url, m_mathletContext.getJApplet()) {
      		protected void log (String message) {
      			m_logger.log(CONNECT, "AppletJapsClient: " + message);
      		}
      	};
      	checkSessionCookie(client, path);
      	if(requestType == POST_REQUEST)
      		connection = client.post(path, params);
      	else if(requestType == GET_REQUEST)
      		connection = client.get(path, params);
      	m_logger.log(CONNECT, "Connection is " + (connection == null ? "null!" : "valid"));
      	return connection; // null -> canceled by user, no error
      } catch (NoDataSheetException noDsEx) {
      	// catch and forward No-DataSheet-Exceptions
      	throw noDsEx;
      } catch (JapsClientRejectedAfterLoginExcpetion japsException) {
      	// catch JapsClient-Rejected error and generate No-DataSheet-Exception from it
      	throw new NoDataSheetException("rejected after successful login", japsException);
    	} catch(Throwable t) {
      	m_logger.log(CONNECT, "Error caught while sending request: " + t);
    		error = t;
    	}
    	// this block can only be reached after errors
    	if(nrOfTries == MAX_NR_OF_TRIES) {
    		StringBuffer ts = new StringBuffer("<font size=\"-1\">" + error.toString() + "</font>");
    		if(error instanceof JapsClientException && ts.indexOf("(URL") > -1) {// insert line break for japs errors
    			ts.insert(ts.indexOf("(URL"), "<br>"); // -> show URL in new line
    		}
    		String text = NO_CONNECTION_TO_HOST + ts + TRY_AGAIN_LATER;
    		int choice = JOptionPane.showOptionDialog(m_mathletContext.getAppletContentPane(), text, ResourceManager.getMessage("error"), JOptionPane.NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[] {RETRY, REPORT_ERROR}, null);
    		if(choice == 0) { // retry option
    			nrOfTries = 0; // reset counter
    			continue;
    		} else { // cancel dialog or report error -> throw exception here and catch it in calling method
    			throw error;
    		}
    	}
		} while(nrOfTries < MAX_NR_OF_TRIES);
    // should never be reached
  	return null;
  }
  
  /**
   * This method checks if a session cookie can be retrieved for a single URL defined by
   * the given JapsClient instance and sub-path. If no such cookie is found, a {@link NoDataSheetException}
   * will be thrown and can be used to stop the further applet execution.
   * 
   * Note that this method is a workaround for "Login rejected after successful login" failures which
   * occur for connections which are established after the applet was "terminated" by the browser plugin.
   * It uses the <code>java.net.CookieHandler</code> class (which is part of Java since the version 1.5)
   * in an anonymous way, to preserve the compatibility with 1.4 and thus only works in 1.5 plugins or higher.
   * 
   * @param client the current instance of a JapsClient
   * @param path a sub-path on the server
   * @throws NoDataSheetException indicates that no session cookie is available
   */
  private void checkSessionCookie(JapsClient client, String path) throws NoDataSheetException {
		try {
			// generate URL on server
			URL url = client.composeURL(path);
			// get system's CookieHandler instance
			Class handlerClass = Class.forName("java.net.CookieHandler");
			Object handler = handlerClass.getMethod("getDefault", new Class[0]).invoke(null, new Object[0]);
			if (handler != null) {
				// get cookies for URL
				Method getMethod = handlerClass.getMethod("get", new Class[] {URI.class, Map.class});
				Map headers = (Map) getMethod.invoke(handler, new Object[] {new URI(url.toExternalForm()), new HashMap()});
	      List values = (List) headers.get("Cookie");
	      // values is "null" if no cookies at all exist
	      if(values == null)
	        throw new NoDataSheetException("No (session) cookie found, check failed");
	  	  // search for session cookie among all cookies for this url (typically 1 cookie)
	      for (Iterator i=values.iterator(); i.hasNext(); ) {
          String cookie = (String) i.next();
    	  	if(cookie != null && cookie.startsWith("JSESSIONID")) {
    				m_logger.log(CONNECT, "Session cookie found (" + cookie + "), check successful");
    	  		return;
    	  	}
	      }
	      // can only be reached if no session cookie was found
	      throw new NoDataSheetException("No session cookie found, check failed");
			} else
				m_logger.log(CONNECT, "No system Cookie-Handler defined, omitting check of session cookie");
		} catch (ClassNotFoundException e) {
			m_logger.log(CONNECT, "No Cookie-Handler class available (" + e.getMessage() + "), omitting check of session cookie");
		} catch (SecurityException e) {
			m_logger.log(CONNECT, "No sufficient security rights available for checking session cookie (" + e.getMessage() + "), omitting check");
    } catch (NoDataSheetException nods) {
    	// print out notification of workaround
    	System.out.println("No session cookie found, stopping applet execution");
    	// catch and forward No-DataSheet-Exceptions -> see "throws" clause in this method definition!
    	throw nods;
		} catch (Throwable t) {
			m_logger.log(CONNECT, "Error occurred during check of session cookie (" + t + "), omitting check");
		}
  }
  
	/**
   * Sends a POST request to the server and returns the newly established http connection.
   * @param params a {@link Map} containing the parameters for the request.
   */
  private HttpURLConnection createUploadConnection(Map params) throws Throwable {
  	String url = getParameter(URL_PREFIX_PARAM);
  	String path = getParameter(SAVE_ANSWERS_PATH_PARAM);
  	if(path == null) // check if param is set
  		path = JapsPath.STORE_PROBLEM_ANSWERS; // if not use static path
  	// send request
  	return sendRequest(POST_REQUEST, url, path, params);
  }
  
  /**
   * Sends a GET request to the server and returns the newly established http connection.
   * @param params a {@link Map} containing the parameters for the request.
   */
  private HttpURLConnection createDownloadConnection(Map params) throws Throwable {
  	String url = getParameter(URL_PREFIX_PARAM);
  	String path = JapsPath.PROBLEM_DATA;
  	// send request
  	return sendRequest(GET_REQUEST, url, path, params);
  }
  
  /**
   * Returns if this exercise should be editable or not, i.e. if the related exercise parameter was set to "true".
   * The default value is <code>true</code>.
   * 
   * @see ExerciseConstants#HOMEWORK_EDITABLE_PARAM
   */
  public boolean isEditable() {
  	return m_isEditable;
  }
    
	/**
	 * This class handles the saving of the student's answer receipt.
	 */
	class SaveDialog extends JDialog {
		
		private String m_fileName;
		private JTextField m_fileNameField;
		private File m_tempFile;
		private JDialog m_receiptDialog;
		
		SaveDialog(String path, String name, File tempFile) {
			super(m_mathletContext.getAppletFrame(), true);
			
			JPanel contentPane = new JPanel(new BorderLayout());
			JPanel centerPane = new JPanel(new BorderLayout());
			centerPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
			
			centerPane.add(new JLabel(DEFAULT_FILE_NAME_QUERY_MESSAGE), BorderLayout.CENTER);
			
	    m_fileName = path + File.separator + name;
	    m_tempFile = tempFile;
			JPanel fileNamePanel = new JPanel(new BorderLayout());
			m_fileNameField = new JTextField(new File(m_fileName).getAbsolutePath(), 30);
			m_fileNameField.setEditable(true);
			m_fileNameField.setBackground(Color.WHITE);
			m_fileNameField.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
						cancel();
					if(e.getKeyCode() == KeyEvent.VK_ENTER)
						ok();
				}
			});
			fileNamePanel.add(m_fileNameField, BorderLayout.CENTER);
			JButton browseButton = new StyledTextButton(ResourceManager.getMessage("exercise.browse"));
			browseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
			    JFileChooser outFileChooser = new JFileChooser();
			    outFileChooser.setFileFilter(new FileFilter() {
			    	public boolean accept(File f) {
			    		return f.getName().endsWith(".zip") || f.isDirectory();
			    	}
			    	
			    	public String getDescription() {
			    		return ResourceManager.getMessage("exercise.zip_file");
			    	}
			    });
			    outFileChooser.setSelectedFile(new File(m_fileName));
			    int resultType = outFileChooser.showSaveDialog(m_mathletContext.getAppletFrame());
			    if(resultType == JFileChooser.APPROVE_OPTION) {
			    	setFileName(outFileChooser.getSelectedFile().getAbsolutePath());
			    }
				}
			});
			fileNamePanel.add(browseButton, BorderLayout.EAST);
			centerPane.add(fileNamePanel, BorderLayout.SOUTH);
			
			JPanel bottomPane = new JPanel();
			JButton okButton = new StyledTextButton(ResourceManager.getMessage("Save"));
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					ok();
				}
			});
			bottomPane.add(okButton);
			JButton cancelButton = new StyledTextButton(ResourceManager.getMessage("exercise.cancel"));
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					cancel();
				}
			});
			bottomPane.add(cancelButton);
			final JButton viewButton = new StyledTextButton(ResourceManager.getMessage("exercise.view_receipt"));
			viewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					if(m_receiptDialog == null)
						m_receiptDialog = ReceiptViewer.createAppletDialog(SaveDialog.this, new Receipt(m_tempFile));
					m_receiptDialog.setVisible(true);
				}
			});
			bottomPane.add(viewButton);
			contentPane.add(bottomPane, BorderLayout.SOUTH);

			contentPane.add(centerPane, BorderLayout.CENTER);

			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					cancel();
				}
			});
			
			getContentPane().add(contentPane);
			pack();
			setSize(600, 300);
			center();
			setVisible(true);
		}
		
		private void setFileName(String path) {
			m_fileName = path;
			m_fileNameField.setText(path);
		}
		
		/**
		 * Returns the selected filename or null if this dialog was canceled.
		 */
		String getFileName() {
			return m_fileName;
		}
		
		private void cancel() {
			if(m_mathletContext.showConfirmDialog(SAVING_CANCELED_QUERY_MESSAGE, ResourceManager.getMessage("exercise.ASK_SAVING"))) {
    		return;
    	}
			m_fileName = null;
			setVisible(false);
		}
		
		private void ok() {
	    try {
	    	m_fileName = m_fileNameField.getText();
				File outFile = new File(m_fileNameField.getText());
	    	File parentDir = outFile.getParentFile();
	    	if(parentDir != null && !parentDir.exists()) {
	    		parentDir.mkdirs();
					m_logger.log(RECEIPT, "Directory created: " + parentDir.getAbsolutePath());
	    	}
				if( !outFile.exists() && outFile.createNewFile()) {
					// copy temporary receipt to user folder
					saveReceipt(new FileInputStream(m_tempFile), outFile);
					m_logger.log(RECEIPT, "Receipt saved to: " + outFile);
					setVisible(false);
				} else {
		    	outFile = getNonOverwrittingFile(outFile);
					boolean takeNewName = m_mathletContext.showConfirmDialog(FILE_EXISTS_QUERY_MESSAGE + outFile.getAbsolutePath(), ResourceManager.getMessage("Question"));
					if(takeNewName) {
						// copy temporary receipt to user folder
						saveReceipt(new FileInputStream(m_tempFile), outFile);
						m_logger.log(RECEIPT, "Receipt saved to: " + outFile);
						setVisible(false);
					}
				}
			} catch (IOException e) {
				m_mathletContext.showErrorDialog(NO_RIGHTS_QUERY_MESSAGE);
			} catch (Throwable t) {
       	m_logger.log(SAVE, "saving receipt error: " + t);
  			String text = SAVING_RECEIPT_ERROR + "<font size=\"-1\">" + t + "</font><br><br>";
  			m_mathletContext.showErrorDialog(text);
			}
		}
		
		/*
		 * Returns a file name based on the given file. The new file must not exist and its
		 * name contains the prefix of the given file name, an index and the suffix of the given file name.
		 * If the given file does not exist, it is simply returned.
		 */
		private File getNonOverwrittingFile(File f) {
			if(f.exists()) {
				String orgName = f.getName();
				String prefix = orgName.substring(0, orgName.lastIndexOf("."));
				String suffix = orgName.substring(orgName.lastIndexOf("."));
				int i = 0;
				File newFile = null;
				do {
					i++;
					newFile = new File(f.getParent(), prefix + "_" + i + suffix);
				} while(newFile.exists());
				return newFile;
			} else
				return f;
		}
		
	  /** Centers this frame on the screen. */
	  public void center() {
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (screenSize.width - getWidth())/2;
	    int y = (screenSize.height - getHeight())/2;
	    setLocation(x,y);
	  }
	}
	
  private void clearTemporarySheet() {
    m_temporarySheet = XMLUtils.createDataSheet();
  }
    
  public void setCurrentSubtask(int subtask) {
  	if(subtask <= 0)
  		return;
  	m_logger.log(LOAD.and(SAVE), "Changing subtask to " + subtask);
  	m_subtask = subtask;
  }
  
  public int getCurrentSubtask() {
  	return m_subtask;
  }
  
  public MathletContext getMathletContext() {
  	return m_mathletContext;
  }
  
  public MathletExerciseSupport getExerciseSupport() {
  	if(m_mathletContext == null)
  		return null;
  	return m_mathletContext.getExerciseSupport();
  }

  /**
   * Returns the incoming datasheet with the exercise data.
   */
  public DataSheet getQuestionSheet() {
    return m_questionSheet;
  }
    
  /**
   * Sets the local datasheet which can be used to store data directly into the answer sheet 
   * during the {@link MumieExerciseIF#collectAnswers()} call. Existing data with the same path
   * will be overwritten in the answer sheet.
   */
  public void setLocalSheet(DataSheet ds) {
  	m_localSheet = ds;
  }

  /**
   * Creates and returns the outgoing datasheet with the user answers
   * and the data from the local data sheet.
   */
  public DataSheet createAnswerSheet() throws Exception {
  	DataSheet answerSheet = XMLUtils.createDataSheet();
  	
  	// handle multiple choice differently
  	if(MultipleChoiceExerciseIF.class.isAssignableFrom(m_mathletContext.getAppletClass())) {
    	// copy data from local data sheet
    	if(m_localSheet != null) {
    		copyAll(m_localSheet, answerSheet);
    	}
  		return answerSheet;
  	}
  	
  	// copy problem elements only in debug mode (necessary for initializing objects)
    if (m_mathletContext.isDebugMode() || m_mathletContext.isPreviewMode()) {
    	copyData(m_questionSheet, "common/problem/", answerSheet);
    	copyData(m_questionSheet, "user/problem/", answerSheet);
    }
    
    copyGenericAttributes(answerSheet);
    
    // copy old answers to answer sheet
    m_logger.log(SAVE, "Checking question sheet: ");
    copyAnswers(m_questionSheet, answerSheet, false, getCurrentSubtask());
    copyGenericAnswers(m_questionSheet, answerSheet, false, getCurrentSubtask());
 	
    // copy new answers to answer sheet
    m_logger.log(SAVE, "Checking temporary sheet: ");
  	copyAnswers(m_temporarySheet, answerSheet, true, getCurrentSubtask());
  	copyGenericAnswers(m_temporarySheet, answerSheet, true, getCurrentSubtask());
  	
  	// copy data from local data sheet
  	if(m_localSheet != null) {
  		copyAll(m_localSheet, answerSheet);
  	}
  	  	
    return answerSheet;
  }
  
  protected void copyGenericAttributes(DataSheet answerSheet) {
    // save exercise type name
    answerSheet.put(EXERCISE_TYPE_PATH, getExerciseTypeName());      
    if(getExerciseType() == EXERCISE_DEFAULT_TYPE) {
      // store current subtask index
      m_logger.log(SAVE, "Current subtask: " + getCurrentSubtask());
      answerSheet.put(CURRENT_SUBTASK_PATH, getCurrentSubtask());
    }
  }
  
  protected void copyAnswers(DataSheet source, DataSheet target, boolean mustMatch, int subtask) throws Exception {
  	String[] subtaskPaths = source.getMatchingUnitPaths(SUBTASK_PATH + "*");
  	m_logger.log(SAVE, subtaskPaths.length + " path(s) found matching " + SUBTASK_PATH + "*");
  	for(int i = 0; i < subtaskPaths.length; i++) {
  		// format: user/answer/subtask_X
  		String subtaskString = subtaskPaths[i].substring(subtaskPaths[i].indexOf("_") + 1);
  		int parsedSubtask = Integer.parseInt(subtaskString);
  		if(matches(mustMatch, parsedSubtask, subtask)) {
  			m_logger.log(SAVE, "  copying elements below " + subtaskPaths[i] + " to answer sheet");
      	copyData(source, subtaskPaths[i], target);
  		}
  	}
  }
  
  protected void copyGenericAnswers(DataSheet source, DataSheet target, boolean mustMatch, int subtask) throws Exception {
  	String[] genericSubtaskPaths = source.getMatchingUnitPaths(GENERIC_USER_ANSWER_PATH + PATH_SEPARATOR + SUBTASK_PREFIX + "*");
  	m_logger.log(SAVE, genericSubtaskPaths.length + " path(s) found matching " + GENERIC_USER_ANSWER_PATH + PATH_SEPARATOR + SUBTASK_PREFIX + "*");
  	for(int i = 0; i < genericSubtaskPaths.length; i++) {
  		// format: user/answer/generic/subtask_X
  		String subtaskString = genericSubtaskPaths[i].substring(genericSubtaskPaths[i].indexOf("_") + 1);
  		int parsedSubtask = Integer.parseInt(subtaskString);
  		if(matches(mustMatch, parsedSubtask, subtask)) {
  			m_logger.log(SAVE, "  copying elements below " + genericSubtaskPaths[i] + " to answer sheet");
      	copyData(source, genericSubtaskPaths[i], target);
  		}
  	}
  }
  
  private static boolean matches(boolean mustMatch, int a, int b) {
  	return mustMatch ? a == b : a != b;
  }
  
  protected void copyAll(DataSheet source, DataSheet target) throws Exception {
  	target.merge(source, DataSheet.REPLACE);
  }
  
  /**
   * Updates the question sheet with the answers in the given data sheet. 
   * Note: Only data below the path {@link #SUBTASK_PATH} will be changed in the question sheet.
   * @param answerSheet data sheet containing user answers
   */
  private void updateQuestionSheet(DataSheet answerSheet) throws Exception {
  	DataSheet tempSheet = XMLUtils.createDataSheet();
		tempSheet.merge(answerSheet, DataSheet.REPLACE);
  	if(m_mathletContext.isDebugMode()) {
  		// do nothing, "problem" elements are already in answer sheet
  	} else {
    	copyData(m_questionSheet, "common/problem/", tempSheet);
    	copyData(m_questionSheet, "user/problem/", tempSheet);
  	}
  	m_questionSheet = tempSheet;
  	m_logger.log(SAVE, "saving successful!\n");
  }

  /**
   * Returns the parsed <code>common/problem/field</code> as a class.
   * Returns null if this data is not found.
   */
  public Class getNumberClass() {
    if (m_numberClass == null)
    	System.err.println("No value set in datasheet for " + FIELD_PATH);
    return m_numberClass;
  }

  /**
   * Returns the parsed <code>common/problem/dimension</code> as an integer.
   * Return -1 if this data is not found.
   */
  public int getDimension() {
    if (m_dim == -1)
    	System.err.println("No value set in datasheet for "
          + DIMENSION_PATH);
    return m_dim;
  }

  /**
   * Saves the answer string to the answer sheet for the indexed subtask and the
   * given path under <code>[path]</code>.
   *
   * @param path
   *          a path inside the answer sheet
   * @param answer
   *          the answer string
   */
  protected void setAnswer(String path, String answer) {
  	m_temporarySheet.put(path, answer);
  }

  /**
   * Saves the answer node to the answer sheet for the indexed subtask and the
   * given path under <code>[path]</code>.
   *
   * @param path
   *          a path inside the answer sheet
   * @param answerNode
   *          the answer as a DOM Node
   */
  protected void setAnswer(String path, Node answerNode) {
    Node n = m_temporarySheet.getDocument().importNode(answerNode, true);
    m_temporarySheet.put(path, n);
  }

  /**
   * Saves the answer string to the answer sheet for the indexed subtask and the
   * given path under <code>/user/answer/subtask_[subtaskIndex]/[path]</code>.
   * If the path starts with "selected_" and the answer string is a regular path
   * inside the question sheet, the generic selection helper will be asked to perform the task. 
   * Must only be called within the {@link MumieExerciseIF#collectAnswers()} method.
   *
   * @param subtaskIndex
   *          between 1 and number of subtasks
   * @param path
   *          a path under <code>/user/answer/subtask_[subtaskIndex]</code>
   * @param answer
   *          the answer string (may be a datasheet path)
   */
  public void setAnswer(int subtaskIndex, String path, String answer) {
		if(m_genSelectionHelper.isDataElement(answer))
			m_genSelectionHelper.setSelectionAnswer(subtaskIndex, path, answer);
		else
			throw new IllegalArgumentException("Only datasheet paths can be saved via strings !");
//			setAnswer(USER_ANSWER_PATH + PATH_SEPARATOR + SUBTASK_PREFIX
//					+ subtaskIndex + PATH_SEPARATOR + path, answer);
  }

  /**
   * Saves the object's value as answer for the indexed subtask and the
   * given path under <code>/user/answer/subtask_[subtaskIndex]/[path]</code>.
   * Must only be called within the {@link MumieExerciseIF#collectAnswers()} method.
   *
   * @param subtaskIndex
   *          between 1 and number of subtasks
   * @param path
   *          a path under <code>/user/answer/subtask_[subtaskIndex]</code>
   * @param answerObject
   *          an object containing the answer
   */
  public void setAnswer(int subtaskIndex, String path, MathMLSerializable answerObject) {
  	setAnswer(subtaskIndex, path, answerObject.getMathMLNode(m_temporarySheet.getDocument()));
  }
  
  /**
   * Saves the answer node to the answer sheet for the indexed subtask and the
   * given path under <code>/user/answer/subtask_[subtaskIndex]/[path]</code>.
   * Must only be called within the {@link MumieExerciseIF#collectAnswers()} method.
   *
   * @param subtaskIndex
   *          between 1 and number of subtasks
   * @param path
   *          a path under <code>/user/answer/subtask_[subtaskIndex]</code>
   * @param answerNode
   *          the answer as a DOM Node
   */
  public void setAnswer(int subtaskIndex, String path, Node answerNode) {
  	Node n = m_temporarySheet.getDocument().importNode(answerNode, true);
    setAnswer(USER_ANSWER_PATH + PATH_SEPARATOR + SUBTASK_PREFIX
        + subtaskIndex + PATH_SEPARATOR + path, n);
  }
  
  /**
   * Adds the referenced content of the given selected path to the generic answer node inside the answer sheet
   * and saves the answer string to the answer sheet for the indexed subtask and the
   * given path under <code>/user/answer/subtask_[subtaskIndex]/selected_[i]</code>.
   * 
   * @param subtaskIndex
   *          between 1 and number of subtasks
   * @param selectedPath a referencing path
   */
	public void setSelectionAnswer(int subtaskIndex, String selectedPath) {
		m_genSelectionHelper.setSelectionAnswer(subtaskIndex, selectedPath);
	}
	
  /**
   * Adds the referenced content of the given selected path to the generic answer node inside the answer sheet
   * under the given path and saves the answer string to the answer sheet for the indexed subtask and the
   * given path.
   * 
   * @param subtaskIndex
   *          between 1 and number of subtasks
   * @param path 
   *          a path under <code>/user/answer/subtask_[subtaskIndex]</code>
   * @param selectedPath a referencing path
   */
	public void setSelectionAnswer(int subtaskIndex, String path, String selectedPath) {
		m_genSelectionHelper.setSelectionAnswer(subtaskIndex, path, selectedPath);
	}
	
  /**
   * Adds the referenced content of the given selected paths to the generic answer node inside the answer sheet
   * and saves the answer strings to the answer sheet for the indexed subtask and the
   * given path under <code>/user/answer/subtask_[subtaskIndex]/selected_[i]</code>.
   * 
   * @param subtaskIndex
   *          between 1 and number of subtasks
   * @param selectedPaths an array of referencing paths
   */
	public void setSelectionAnswers(int subtaskIndex, String[] selectedPaths) {
		m_genSelectionHelper.setSelectionAnswers(subtaskIndex, selectedPaths);
	}
  
	/**
	 * Sets the runtime and number class for selected objects referenced by a path inside the
	 * question sheet. This method must be used BEFORE the mathlet's {@link MumieExerciseIF#collectAnswers()}
	 * method is called.
	 * 
	 * @param objectClass the M- or MM-class of selected objects
	 * @param numberClass the number class of selected objects
	 */
	public void setGenericSelectionAttributes(Class objectClass, Class numberClass) {
		m_genSelectionHelper.setGenericSelectionAttributes(objectClass, numberClass);
	}
	
	/**
	 * Sets the exercise object type and field for selected objects referenced by a path inside the
	 * question sheet. This method must be used BEFORE the mathlet's {@link MumieExerciseIF#collectAnswers()}
	 * method is called.
	 * 
	 * @param type the type of selected objects
	 * @param field the field (i.e. number class) of selected objects
	 */
	public void setGenericSelectionAttributes(String type, String field) {
		m_genSelectionHelper.setGenericSelectionAttributes(type, field);
	}
	
  /**
   * Loads the element with the specified path from the question sheet and saves
   * the node into the given MMObject.
   *
   * @param path
   *          a path in the question datasheet
   */
  public void loadElement(String path, MathMLSerializable mmobject) {
  	loadElement(path, mmobject, false);
  }
  
  /**
   * Loads the element with the specified path from the question sheet, saves
   * the node into the given MMObject and renders 
   * (if <code>render</code> is true) the object.
   *
   * @param path
   *          a path in the question datasheet
   */
  public void loadElement(String path, MathMLSerializable mmobject, boolean render) {
    try {
      Element e = getQuestionSheet().getAsElement(path);
      if (e != null) {
        mmobject.setMathMLNode(e);
        if(render && mmobject instanceof MMObjectIF)
        	((MMObjectIF)mmobject).render();
      }
      else
        throw new DataSheetException("No such element: " + path);
    } catch (DataSheetException e) {
    	System.err.println("Loading datasheet-element \"" + path
          + "\" failed!\n"+ e);
    }
  }

  /**
   * Loads the user answer element with the specified path from the question
   * sheet and saves the node into the given MMObject.
   *
   * @param path
   *          a path under "user/answer/subtask_[subtaskIndex]"
   */
  public void loadUserElement(int subtaskIndex, String path,
      MathMLSerializable mmobject) {
  	loadUserElement(subtaskIndex, path, mmobject, false);
  }
  
  /**
   * Loads the user answer element with the specified path from the question
   * sheet, saves the node into the given MMObject and renders 
   * (if <code>render</code> is true) the object.
   *
   * @param path
   *          a path under "user/answer/subtask_[subtaskIndex]"
   */
  public void loadUserElement(int subtaskIndex, String path,
      MathMLSerializable mmobject, boolean render) {
    loadElement(USER_ANSWER_PATH + PATH_SEPARATOR + SUBTASK_PREFIX
        + subtaskIndex + PATH_SEPARATOR + path, mmobject, render);
  }

  /**
   * Loads the user answer element with the specified path from the question
   * sheet and saves the node into the given MMObject.
   *
   * @param path
   *          a path inside the question sheet
   */
  public void loadProblemElement(String path, MathMLSerializable mmobject) {
  	loadProblemElement(path, mmobject, false);
  }

  /**
   * Loads the user answer element with the specified path from the question
   * sheet, saves the node into the given MMObject and renders 
   * (if <code>render</code> is true) the object.
   *
   * @param path
   *          a path inside the question sheet
   */
  public void loadProblemElement(String path, MathMLSerializable mmobject, boolean render) {
    loadElement(COMMON_PROBLEM_PATH + PATH_SEPARATOR + path, mmobject, render);
  }

  /**
   * Returns the element with the specified path from the question sheet.
   */
  public Element getElement(String path) {
    try {
      return getQuestionSheet().getAsElement(path);
    } catch (DataSheetException e) {
      System.err.println("Loading datasheet-element \"" + path
          + "\" failed: " + e);
      return null;
    }
  }

  public Element getUserElement(int subtaskIndex, String path) {
    return getElement(USER_ANSWER_PATH + PATH_SEPARATOR + SUBTASK_PREFIX
        + subtaskIndex + PATH_SEPARATOR + path);
  }

  public Element getProblemElement(String path) {
    return getElement(COMMON_PROBLEM_PATH + PATH_SEPARATOR + path);
  }

  /**
   * Returns the content of a data element with the specified path as a String.
   * Returns null if no such data element is found.
   */
  public String getString(String path) {
    try {
      return getQuestionSheet().getAsString(path);
    } catch (DataSheetException e) {
      return null;
    }
  }

  public String getUserString(int subtaskIndex, String path) {
    return getString(USER_ANSWER_PATH + PATH_SEPARATOR + SUBTASK_PREFIX
        + subtaskIndex + PATH_SEPARATOR + path);
  }

  public String getProblemString(String path) {
    return getString(COMMON_PROBLEM_PATH + PATH_SEPARATOR + path);
  }

  /**
   * Checks if the user element with the specified subpath exists for the given
   * subtask in the question sheet.
   */
  public boolean userElementExists(int subtaskIndex, String path) {
    return elementExists(USER_ANSWER_PATH + PATH_SEPARATOR + SUBTASK_PREFIX
        + subtaskIndex + PATH_SEPARATOR + path);
  }

  /**
   * Checks if the problem element with the specified subpath exists for the
   * given subtask in the question sheet.
   */
  public boolean problemElementExists(String path) {
    return elementExists(COMMON_PROBLEM_PATH + PATH_SEPARATOR + path);
  }

  /**
   * Checks if the element with the specified path exists in the question sheet.
   */
  public boolean elementExists(String path) {
    Element e = getQuestionSheet().getDataElement(path);
    return e != null ? true : false;
  }

  /**
   * Returns the complete button panel with the "send" and the "clear" button.
   */
  public ExerciseButtonPanel getButtonPanel() {
    return m_buttonPanel;
  }

  /**
   * Sets if the "clear" button should be visible.
   */
  public void setClearButtonVisible(boolean visible) {
    m_buttonPanel.m_clearButton.setVisible(visible);
  }
  
  /**
   * Sets the text that will be displayed on the "clear" button.
   */
  public void setClearButtonText(String text) {
  	getClearButton().setText(text);
  }

  /**
   * Returns if the "clear" button is visible.
   */
  public boolean isClearButtonVisible() {
    return m_buttonPanel.m_clearButton.isVisible();
  }

  /**
   * Returns the "send" button.
   */
  public JButton getSendButton() {
    return m_buttonPanel.m_sendButton;
  }

  /**
   * Returns the "clear" button.
   */
  public JButton getClearButton() {
    return m_buttonPanel.m_clearButton;
  }
  
	/**
	 * This class acts as a bridge between the selected paths and their corresponding XML exercise objet attributes.
	 * Its basic feature is to read the MathML content of the referenced paths, to add exercise object attributes 
	 * to them and to put the resulting XML node into a generic user answer section of the question sheet. 
	 */
	class GenericSelectionHelper {
		
		private String m_typeName, m_fieldName;
		
		/**
		 * Sets the runtime and number class for selected objects referenced by a path inside the
		 * question sheet. This method must be used BEFORE the mathlet's {@link MumieExerciseIF#collectAnswers()}
		 * method is called.
		 * 
		 * @param objectClass the M- or MM-class of selected objects
		 * @param numberClass the number class of selected objects
		 */
		void setGenericSelectionAttributes(Class objectClass, Class numberClass) {
			m_fieldName = ExerciseObjectFactory.getFieldName(numberClass);
			if(m_fieldName == null)
				throw new IllegalArgumentException("Exercise object has no registered number class: " + numberClass.getName());
			m_typeName = ExerciseObjectFactory.getTypeName(objectClass, m_fieldName);
			if(m_typeName == null)
				throw new IllegalArgumentException("Exercise object has no registered type name: " + objectClass.getName());
		}
		
		/**
		 * Sets the exercise object type and field for selected objects referenced by a path inside the
		 * question sheet. This method must be used BEFORE the mathlet's {@link MumieExerciseIF#collectAnswers()}
		 * method is called.
		 * 
		 * @param type the type of selected objects
		 * @param field the field (i.e. number class) of selected objects
		 */
		void setGenericSelectionAttributes(String type, String field) {
			m_typeName = type;
			if(m_typeName == null)
				throw new IllegalArgumentException("Type name must not be null !");
			m_fieldName = field;
			if(m_fieldName == null)
				throw new IllegalArgumentException("Field name must not be null !");
		}
		
		/**
		 * Returns the index of the next selection answer.
		 */
		private int getNextSelectionIndex(int subtaskIndex) {
			int i = 0;
			String path;
			do {
				i++;
				path = GENERIC_USER_ANSWER_PATH + PATH_SEPARATOR + SUBTASK_PREFIX + subtaskIndex 
								+ PATH_SEPARATOR + SELECTION_PREFIX + i ;
			} while(XMLUtils.isDataElement(m_temporarySheet, path));
			return i;
		}
		
	  /**
	   * Adds the referenced content of the given selected path to the generic answer node inside the answer sheet
	   * and saves the answer string to the answer sheet for the indexed subtask and the
	   * given path under <code>/user/answer/subtask_[subtaskIndex]/selected_[i]</code>.
	   * 
	   * @param subtaskIndex
	   *          between 1 and number of subtasks
	   * @param selectedPath a referencing path
	   */
		void setSelectionAnswer(int subtaskIndex, String selectedPath) {
			if(!isDataElement(selectedPath))
				throw new IllegalArgumentException("The path does not reference to a regular data element: " + selectedPath);				
			int selectionIndex = getNextSelectionIndex(subtaskIndex);
			try {
				Element exObjNode = generateExerciseObjectNode(selectedPath);
				setAnswer(GENERIC_USER_ANSWER_PATH + PATH_SEPARATOR + SUBTASK_PREFIX + subtaskIndex 
									+ PATH_SEPARATOR + SELECTION_PREFIX + selectionIndex, exObjNode);
			} catch(Exception e) {
				m_mathletContext.reportError(e);
			}
			setAnswer(USER_ANSWER_PATH + PATH_SEPARATOR + SUBTASK_PREFIX
					+ subtaskIndex + PATH_SEPARATOR + SELECTION_PREFIX + selectionIndex, selectedPath);
		}
		
	  /**
	   * Adds the referenced content of the given selected path to the generic answer node inside the answer sheet
	   * under the given path and saves the answer string to the answer sheet for the indexed subtask and the
	   * given path.
	   * 
	   * @param subtaskIndex
	   *          between 1 and number of subtasks
	   * @param path 
	   *          a path under <code>/user/answer/subtask_[subtaskIndex]</code>
	   * @param selectedPath a referencing path
	   */
		void setSelectionAnswer(int subtaskIndex, String path, String selectedPath) {
			if(!isDataElement(selectedPath))
				throw new IllegalArgumentException("The path does not reference to a regular data element: " + selectedPath);				
			try {
				Element exObjNode = generateExerciseObjectNode(selectedPath);
				setAnswer(GENERIC_USER_ANSWER_PATH + PATH_SEPARATOR + SUBTASK_PREFIX + subtaskIndex 
									+ PATH_SEPARATOR + path, exObjNode);
			} catch(Exception e) {
				// do nothing, error will be reported in receipt viewer
			}
			setAnswer(USER_ANSWER_PATH + PATH_SEPARATOR + SUBTASK_PREFIX + subtaskIndex
					 			+ PATH_SEPARATOR + path, selectedPath);
		}
	  /**
	   * Adds the referenced content of the given selected paths to the generic answer node inside the answer sheet
	   * and saves the answer strings to the answer sheet for the indexed subtask and the
	   * given path under <code>/user/answer/subtask_[subtaskIndex]/selected_[i]</code>.
	   * 
	   * @param subtaskIndex
	   *          between 1 and number of subtasks
	   * @param selectedPaths an array of referencing paths
	   */
		void setSelectionAnswers(int subtaskIndex, String[] selectedPaths) {
			for(int i = 0; i < selectedPaths.length; i++) {
				setSelectionAnswer(subtaskIndex, selectedPaths[i]);
			}
		}
		
		/**
		 * Reads the MathML content of the referenced path, adds exercise object attributes to it
		 * and returns it.
		 * @throws DataSheetException thrown if the path does not describe a data element inside the question sheet
		 * @throws NullPointerException thrown if the generic selection attributes were not set
		 */
		Element generateExerciseObjectNode(String path) throws DataSheetException {
			if(m_typeName == null || m_fieldName == null)
				throw new NullPointerException("Generic selection attributes were not set !");
			Element mathMLNode = getQuestionSheet().getAsElement(path);
			ExerciseObjectFactory.exportExerciseAttributes(mathMLNode, m_typeName, m_fieldName);
			return mathMLNode;
		}
		
		/**
		 * Returns if the given string references to a <i>data</i> element in the question sheet.
		 */
		boolean isDataElement(String answer) {
			return XMLUtils.isDataElement(getQuestionSheet(), answer);
		}
	}


  class SelectButton extends JButton {
  	SelectButton() {
  	super("Select...");
    this.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        final JSpinner taskInt = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        final JTextField pathField = new JTextField("common/problem/", 30);
        JButton doSubtask = new JButton("Change Subtask");
        JButton doData = new JButton("Add Element");
        JFrame frame = new JFrame("Select ...");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p1.add(new JLabel("<html><b>Select Subtask:"));
        p1.add(taskInt);
//            p1.add(Box.createRigidArea(new Dimension(150, 10)));
        p1.add(doSubtask);
        frame.getContentPane().add(p1);
        JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p2.add(new JLabel("<html><b>Select Data:"));
        p2.add(pathField);
        p2.add(doData);
        frame.getContentPane().add(p2);
        JPanel p3 = new JPanel(new BorderLayout());
        final DatasheetRenderer renderer = new DatasheetRenderer(getQuestionSheet());
        renderer.getTree().addTreeSelectionListener(new TreeSelectionListener() {
          public void valueChanged(TreeSelectionEvent e) {
          	pathField.setText(renderer.getDatasheetPath((DefaultMutableTreeNode) e.getPath().getLastPathComponent()));
          }
        });
        p3.add(renderer, BorderLayout.CENTER);
        frame.getContentPane().add(p3);
        doSubtask.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e1) {
          	int subtask = Integer.parseInt(taskInt.getValue().toString());
            ((MultipleTasksIF)m_mumieExerciseApplet).selectTask(subtask);
        		setCurrentSubtask(subtask);
          }
        });
        doData.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e1) {
          	((SelectableDataIF)m_mumieExerciseApplet).selectElement(pathField.getText());
          }
        });
        if(!(m_mumieExerciseApplet instanceof SelectableDataIF))
        	doData.setEnabled(false);
        if(!(m_mumieExerciseApplet instanceof MultipleTasksIF))
        	doSubtask.setEnabled(false);

        frame.pack();
        frame.setSize(600, 500);
        frame.setVisible(true);
      }
    });
  	}
  }
  
  class SubtaskSelector extends JSpinner {
  	SubtaskSelector() {
  		setModel(new SpinnerNumberModel(1, 1, 10, 1));
  		addChangeListener(new ChangeListener() {
  			public void stateChanged(ChangeEvent e) {
        	int subtask = Integer.parseInt(getValue().toString());
          ((MultipleTasksIF)m_mumieExerciseApplet).selectTask(subtask);
      		setCurrentSubtask(subtask);
  			}
  		});
  	}
  }
  
  class ExerciseButtonPanel extends JPanel {

    private JButton m_sendButton = new StyledTextButton(ResourceManager.getMessage("exercise.save"));
    
    private JButton m_clearButton = new StyledTextButton(ResourceManager.getMessage("exercise.clear"));

    public ExerciseButtonPanel() {
      add(m_sendButton);
      add(m_clearButton);

      m_sendButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          processAnswerSheet(FEEDBACK_SAVING);
        }
      });

      m_clearButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
        	clearSubtask();
        }
      });
    }
  }
}
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

package net.mumie.mathletfactory.util.exercise.receipt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.zip.ZipFile;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import net.mumie.japs.datasheet.DataSheet;
import net.mumie.japs.datasheet.DataSheetException;
import net.mumie.mathletfactory.appletskeleton.util.ControlPanel;
import net.mumie.mathletfactory.display.noc.MMPanel;
import net.mumie.mathletfactory.util.ResourceManager;
import net.mumie.mathletfactory.util.exercise.ExerciseConstants;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;
import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is used to parse (i.e. read) receipts from datasheets, which can either be loaded
 * from a zip file or be given directly.
 * The loaded answer elements are directy written to an answer panel, which can be used to
 * display the receipt's content.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public class Receipt implements ExerciseConstants {
	
	private final static String[] ABC = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", };
	private final static Font m_boldFont = new Font("Dialog", Font.BOLD, 14);
//	private final static Font m_plainFont = new Font("Dialog", Font.PLAIN, 14);
	private final static Font m_bigFont = new Font("Dialog", Font.PLAIN, 16);
	public final static String INPUT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss S";
	public final static String OUTPUT_DATE_FORMAT = "dd.MM.yyyy HH:mm";
	public final static String EMPTY_FIELD_CONTENT = "???";
	
	private DataSheet m_datasheet;
	private String m_courseName, m_worksheetName, m_problemName, 
			m_userFirstName, m_userSurname, m_fileName, m_time;
	private ControlPanel m_answerPanel;
	private int m_lastSubtask = -1;
	private boolean m_lastSubtaskIsEmpty = false;

  private static MumieLogger m_logger = MumieLogger.getLogger(Receipt.class);
  private final static LogCategory RECEIPT = m_logger.getCategory("exercise.receipt");
  
  /**
   * Constructs a new receipt from a zip file.
   * @param file a zip file
   */
	public Receipt(String file) {
		this(new File(file));
	}
	
  /**
   * Constructs a new receipt from a zip file.
   * @param file a zip file
   */
	public Receipt(File file) {
		m_fileName = file.getAbsolutePath();
		m_datasheet = loadDatasheetFromZipFile(file);
		if(m_datasheet == null) {
			System.err.println("An error occurred while reading datasheet!");
			return;
		}
		loadValues();
		loadGenericInfo();
		loadAnswers();
	}
	
  /**
   * Constructs a new receipt from a datasheet.
   * @param ds a datasheet with an answer block.
   */
	public Receipt(DataSheet ds) {
		m_datasheet = ds;
		loadValues();
		loadGenericInfo();
		loadAnswers();
	}
	
	/**
	 * Loads the student's data from the receipt.
	 */
	private void loadValues() {
		m_courseName = loadValue(m_datasheet, "user/receipt/course-name");
		m_worksheetName = loadValue(m_datasheet, "user/receipt/worksheet-name");
		m_problemName = loadValue(m_datasheet, "user/receipt/problem-name");
		m_userFirstName = loadValue(m_datasheet, "user/receipt/user-first-name");
		m_userSurname = loadValue(m_datasheet, "user/receipt/user-surname");
		m_time = loadValue(m_datasheet, "user/receipt/time");
	}
	
	/**
	 * Loads the number/index of the last saved subtask from the receipt.
	 */
	private void loadGenericInfo() {
		try {
			m_lastSubtask = m_datasheet.getAsInteger(CURRENT_SUBTASK_PATH, -1);
		} catch (DataSheetException e) {
			e.printStackTrace();
		}
		m_logger.log(RECEIPT, "last saved subtask: " + m_lastSubtask);
	}
	
	/**
	 * Loads a datasheet from a zip-file which must be a receipt file with a DATA entry.
	 */
	private DataSheet loadDatasheetFromZipFile(File file) {
		try {
			ZipFile zipFile = new ZipFile(file);
			InputStream in = zipFile.getInputStream(zipFile.getEntry("DATA"));
			return XMLUtils.loadDataSheetFromStream(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Loads a value from the datasheet.
	 */
	private String loadValue(DataSheet datasheet, String path) {
		if(datasheet.getDataElement(path) == null)
			return EMPTY_FIELD_CONTENT;
		return datasheet.getDataElement(path).getChildNodes().item(0).getNodeValue();
	}
	
	/**
	 * Loads the student's answers from the receipt.
	 */
	private void loadAnswers() {
		Vector subtasks = new Vector();
		NodeList userAnswerNodes = m_datasheet.getUnitElement(USER_ANSWER_PATH).getChildNodes();
		for(int i = 0; i < userAnswerNodes.getLength(); i++) {
			Node n = userAnswerNodes.item(i);
			if(n.getNodeType() == Node.TEXT_NODE || !getNameClassAttribute(n).startsWith(SUBTASK_PREFIX))
				continue;
			subtasks.add(n);
		}
		m_logger.log(RECEIPT, subtasks.size() + " saved subtasks found");
		m_answerPanel = new ControlPanel();
		if(m_lastSubtask != -1) {
			m_answerPanel.addText("<u>Zuletzt gespeicherte Antworten</u>", m_bigFont);
			m_answerPanel.insertLineBreak();
			loadSubtaskAnswers(subtasks, true);
		} else {
			m_answerPanel.addText("Zuletzt bearbeitet Aufgabe unbekannt !");
			m_answerPanel.insertLineBreak();
		}
		if(m_lastSubtask == -1 || m_lastSubtaskIsEmpty && subtasks.size() > 0 || !m_lastSubtaskIsEmpty && subtasks.size() > 1) {
			m_answerPanel.addText("-------------------------------------------");
			m_answerPanel.insertLineBreak();
			m_answerPanel.addText("<u>Zuvor gespeicherte Antworten</u>", m_bigFont);
			m_answerPanel.insertLineBreak();
			loadSubtaskAnswers(subtasks, false);
		}
	}
	
	/**
	 * Loads the answers for a single subtask from the receipt.
	 * @param subtaskNodes a list with all subtasks nodes
	 * @param loadCurrent flag if the current subtask should be loaded
	 */
	private void loadSubtaskAnswers(Vector subtaskNodes, boolean loadCurrent) {
//		if(subtaskNodes.size() == 0 && loadCurrent) {
//			m_answerPanel.addText("Antwort zur Teilaufgabe " + ABC[m_lastSubtask- 1] + ")", m_bigFont);
//			m_answerPanel.insertLineBreak();
//			m_answerPanel.addText("Keine Antworten gespeichert!", m_bigFont);
//			m_answerPanel.insertLineBreak();
//		}
		boolean currentWasFound = !loadCurrent;
		for(int i = 0; i < subtaskNodes.size(); i++) {
			Node subtaskNode = (Node) subtaskNodes.get(i);
			String subtaskName = getNameClassAttribute(subtaskNode);
			int pos = subtaskName.lastIndexOf('_') + 1;
			int subtaskNr = Integer.parseInt(subtaskName.substring(pos));
			if( (loadCurrent && subtaskNr != m_lastSubtask) || (!loadCurrent && subtaskNr == m_lastSubtask) )
				continue;
			if(loadCurrent && subtaskNr == m_lastSubtask)
				currentWasFound = true;
			m_answerPanel.addText("Antwort zur Teilaufgabe " + ABC[subtaskNr - 1] + ")", m_bigFont);
			m_answerPanel.insertLineBreak();
			try {
				addAnswers("subtask_" + subtaskNr, subtaskNode);
			} catch (Exception e) {
				e.printStackTrace();
			}
			m_answerPanel.insertLineBreaks(2);
		}
		if(currentWasFound == false) {
			m_lastSubtaskIsEmpty = true;
			m_answerPanel.addText("Antwort zur Teilaufgabe " + ABC[m_lastSubtask- 1] + ")", m_bigFont);
			m_answerPanel.insertLineBreak();
			m_answerPanel.addText("Keine Antworten gespeichert!", m_boldFont);
			m_answerPanel.insertLineBreak();
		}
	}
	
	/**
	 * Loads and adds the answers below the given path to the answer panel.
	 * @param parentPath the path containing the answers
	 * @param parentNode the node containing the answers
	 */
	private void addAnswers(String parentPath, Node parentNode) throws Exception {
		NodeList childNodes = parentNode.getChildNodes();
		for(int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if(childNode.getNodeType() == Node.TEXT_NODE)
				continue;
			String childName = getNameClassAttribute(childNode);
			String baseAnswerPath = USER_ANSWER_PATH;
			boolean showLabel = true;
			// check if node is a selection node --> read from generic answer block instead
			// referenced node must be a data element !
			if(isSelectionNode(parentPath, childName)) {
				baseAnswerPath = GENERIC_USER_ANSWER_PATH;
				showLabel = false;
			}
			String fullDataPath = baseAnswerPath + PATH_SEPARATOR + parentPath + PATH_SEPARATOR + childName;
			// data or unit node?
			if(XMLUtils.isUnitElement(m_datasheet, fullDataPath)) {
				// add all subnodes
				addAnswers(parentPath + PATH_SEPARATOR + childName, childNode);
				continue;
			}
			// path is valid?
			if(!XMLUtils.isDataElement(m_datasheet, fullDataPath)) {
				m_answerPanel.add(new LoadingFailedPanel(ResourceManager.getMessage("exercise.receipt.element_loading_error"), null, new SelectionAnswerLoadingException(fullDataPath)));
				m_answerPanel.insertLineBreak();
				continue;
			}
			addAnswer(fullDataPath, showLabel);
		}
	}
	
	/**
	 * Loads and adds the answer with the given full path to the answer panel.
	 * @param fullDataPath the full path to an answer element
	 * @param showLabel flag is the label should be shown
	 */
	private void addAnswer(String fullDataPath, boolean showLabel) throws Exception {
		m_logger.log(RECEIPT, "Loading data element from path " + fullDataPath);
		Node mathMLNode = m_datasheet.getAsElement(fullDataPath);
		JComponent mmpanel = createMMPanel(mathMLNode, showLabel);
		if(mmpanel != null) {
			m_answerPanel.add(mmpanel);
			m_answerPanel.insertLineBreak();
		}
	}
	
	/**
	 * Returns whether the element with the given path and name is a selection node whose value
	 * is a reference to another concrete element in the datasheet.
	 * @param nodePath the path to the node
	 * @param nodeName the name of the node
	 */
	private boolean isSelectionNode(String nodePath, String nodeName) {
		return XMLUtils.isDataElement(m_datasheet, GENERIC_USER_ANSWER_PATH + PATH_SEPARATOR + nodePath + PATH_SEPARATOR + nodeName);
	}
	
	/**
	 * Returns the name attribute of a data element in the means of the {@link DataSheet} path architecture.
	 */
	private String getNameClassAttribute(Node n) {
		return n.getAttributes().getNamedItem("name").getNodeValue();
	}
	
	/**
	 * Creates and returns a new MM-panel drawable for the given MathML node.
	 * Returns a {@link LoadingFailedPanel} if the object cannot be instantiated.
	 * @param mathmlNode a XML node containing exercise attributes and the MathML content
	 * @param showLabel flag is the label should be shown
	 * @return a drawable or a dummy panel
	 */
	private JComponent createMMPanel(Node mathmlNode, boolean showLabel) {
		try {
			if(Boolean.valueOf(
					ExerciseObjectFactory.getAttribute(
							ExerciseObjectFactory.HIDDEN_ATTRIBUTE, mathmlNode)).booleanValue())
				return null;
			JComponent panel = ExerciseObjectFactory.createViewer(mathmlNode);
			if(panel instanceof MMPanel && showLabel == false)
				((MMPanel) panel).setLabelVisible(false);
			return panel;
		} catch (Exception e) {
			return new LoadingFailedPanel(ResourceManager.getMessage("exercise.receipt.element_loading_error"), mathmlNode, e);
		}
	}
	
	/**
	 * Returns the answer panel containing the drawables built from the answers.
	 */
	public JComponent getAnswerPanel() {
		return m_answerPanel;
	}
	
	/**
	 * Returns the <i>course-name</i> property.
	 */
	public String getCourseName() {
		return m_courseName;
	}
	
	/**
	 * Returns the <i>worksheet-name</i> property.
	 */
	public String getWorksheetName() {
		return m_worksheetName;
	}
	
	/**
	 * Returns the <i>problem-name</i> property.
	 */
	public String getProblemName() {
		return m_problemName;
	}
	
	/**
	 * Returns the <i>user-first-name</i> property.
	 */
	public String getUserFirstName() {
		return m_userFirstName;
	}
	
	/**
	 * Returns the <i>user-surname</i> property.
	 */
	public String getUserSurname() {
		return m_userSurname;
	}
	
	/**
	 * Returns the receipt's file name if it was loaded from a zip file, <code>null</code> otherwise.
	 */
	public String getFileName() {
		return m_fileName;
	}
	
	/**
	 * Returns the datasheet containing the receipt's data.
	 */
	public DataSheet getDatasheet() {
		return m_datasheet;
	}
	
	/**
	 * Returns the <i>time</i> property in the current locale specific format.
	 */
	public String getTime() {
		if(m_time.equals(EMPTY_FIELD_CONTENT))
			return EMPTY_FIELD_CONTENT;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(INPUT_DATE_FORMAT);
			Date date = sdf.parse(m_time);
			sdf = new SimpleDateFormat(OUTPUT_DATE_FORMAT);
			return sdf.format(date);
		} catch (ParseException e) {
			System.err.println(e);
			return EMPTY_FIELD_CONTENT;
		}
	}
	
	/**
	 * This class is used to display non-loadable answers along with an error message and stacktrace tooltip.
	 */
	class LoadingFailedPanel extends JLabel {
		
		LoadingFailedPanel(String message, final Node mathmlNode, final Exception error) {
			super(message);
			int margin = 5;
			setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(Color.BLACK), 
					BorderFactory.createEmptyBorder(margin, margin, margin, margin)));
			if(error != null) {
				final String message2 = "<html><b>Die Antwort kann aufgrund eines Fehlers nicht dargestellt werden.<br>Meldung:</b> " + error.getLocalizedMessage();
				addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						new LoadingAnswerErrorFrame(message2, mathmlNode, error);
					}
				});
				setToolTipText(message2);
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
		}
	}
	
	/**
	 * This class is used to notify the loading system of a failed loading.
	 */
	class SelectionAnswerLoadingException extends RuntimeException {
		SelectionAnswerLoadingException(String path) {
			super("Data element not found with path " + path);
		}
	}
	
	class LoadingAnswerErrorFrame extends JFrame {
		
		LoadingAnswerErrorFrame(String message, Node mathmlNode, Exception error) {
			super("Antwort-Daten");
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			JPanel contentPane = new JPanel(new BorderLayout());
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			
			JLabel errorMessageLabel = new JLabel(message);
			errorMessageLabel.setHorizontalTextPosition(JLabel.LEFT);
			errorMessageLabel.setFont(errorMessageLabel.getFont().deriveFont(Font.PLAIN));
			contentPane.add(errorMessageLabel, BorderLayout.NORTH);
			
			JTabbedPane tabPane = new JTabbedPane();
			contentPane.add(tabPane, BorderLayout.CENTER);
			if(mathmlNode != null) {
				JEditorPane xmlText = new JEditorPane("text/plain", XMLUtils.nodeToString(mathmlNode, true));
				xmlText.setBackground(Color.WHITE);
				JScrollPane xmlScroller = new JScrollPane(xmlText);			
				tabPane.addTab("Roh-Daten", xmlScroller);
				xmlText.scrollRectToVisible(new Rectangle(0, -100, 10, 10));
			}
			JEditorPane stackTraceText = new JEditorPane("text/html", createStacktraceText(error));
			stackTraceText.setBackground(Color.WHITE);
			JScrollPane stackTraceScroller = new JScrollPane(stackTraceText);
//			stackTraceText.scrollRectToVisible(new Rectangle(0, 0, 10, 10));
//			stackTraceScroller.scrollRectToVisible(new Rectangle(0, 0, 10, 10));
			tabPane.addTab("Fehler-Daten", stackTraceScroller);

			getContentPane().add(contentPane, BorderLayout.CENTER);
			pack();
			setSize(640, 300);
			center();
			setVisible(true);
		}
		
		private String createStacktraceText(Exception e) {
			String result = "<html>";
			result += e.getClass() + ": <br><b>" + e.getMessage() + "</b><br>";
			Throwable t = e;
			if(e.getCause() != null) {
				t = e.getCause();
				result += "Caused by: " + t.getClass() + ": <br><b>" + e.getMessage() + "</b><br>";
			}
			for(int i = 0; i < t.getStackTrace().length; i++) {
				StackTraceElement el = t.getStackTrace()[i];
				result += formatStackTraceElement(el);
			}
			return result;
		}
		
		private String formatStackTraceElement(StackTraceElement e) {
			return "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + e.getClassName() + "." + (e.getMethodName().equals("<init>") ? "&lt;init&gt;" : e.getMethodName()) + "( " + e.getFileName() + ":" + e.getLineNumber() + " )<br>";
		}
	  
		/** Centers this frame on the screen. */
	  public void center() {
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (screenSize.width - getWidth())/2;
	    int y = (screenSize.height - getHeight())/2;
	    setLocation(x,y);
	  }
	}
}

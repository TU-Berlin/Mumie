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

package net.mumie.coursecreator.gui;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.io.InputStream;
import java.net.HttpURLConnection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


import org.apache.commons.lang.ObjectUtils;

import net.mumie.coursecreator.CCController;
import net.mumie.coursecreator.CommandConstants;
import net.mumie.coursecreator.treeview.TreeConstants;
import net.mumie.coursecreator.xml.ELClassListWrapper;
import net.mumie.coursecreator.xml.XMLConstants;
import net.mumie.japs.client.JapsClient;
/**
 * die Klasse um die Lehrveranstaltung auszuwaehlen.
 * @author <a href="mailto:vrichter@math.tu-berlin.de">Verena Richter</a>
 * @version $Id: ClassChooser.java,v 1.6 2009/03/30 12:40:20 vrichter Exp $
 */
public class ClassChooser extends JDialog {
	
	static final long serialVersionUID=0;
	private JapsClient japsclient=null;
	
	private JComboBox classBox;
	
	private Vector classList;
	private MetaInfoFieldController controller;
	private String headlineLabelText;
	/**
	 * The window listener.
	 */

	private WindowListener windowListener =
		new WindowAdapter ()
	{
		public void windowClosing (WindowEvent event)
		{
			ClassChooser dialog = ClassChooser.this;
			dialog.stop();
		}
	};
	
	public ClassChooser(MetaInfoFieldController c, JapsClient jc) {
		super(CCController.frame,"");
		this.controller = c;
		this.japsclient = jc;
		this.classList = getClassList();
		
		this.buildLayout();
	}

	/**
	 * Vector of all Classes
	 * @return
	 */
	private Vector getClassList(){
		Vector list = new Vector();
		
		Map classes = this.fetchELClassIndex();
		if (classes.size() == 0) return list;
		
		for (Iterator iter = classes.keySet().iterator(); iter.hasNext();) {
			String path = (String)iter.next();
			ELClassWrapper el =new ELClassWrapper(ObjectUtils.toString(classes.get(path)),path);	
			list.add(el);
		}
		return list;
	}
	
	private void buildLayout(){
//		 Dimensions of the Dialog		
		int width = 10;
	    int height = 160;
	    int buttonWidth = 100;
	    int buttonHeight = 30;
	    
	    // sets the Layout
		this.getContentPane().setLayout(new GridBagLayout());
		
		// the Button and Label Text
		String okButtonText = "Zuweisen";
	    String cancelButtonText = "Cancel";
	    headlineLabelText = "";
	    	    	    
	    // the Fonts
	    Font font = new Font("SansSerif", Font.PLAIN, 14);
	    Font headlineLabelFont = new Font("SansSerif", Font.PLAIN, 12);
	    Font textFieldFont = new Font("Monospaced", Font.PLAIN, 10);
	    
        // GridBagContraints for rootPanel (s.b.):
	    GridBagConstraints rootPanelStyle = 
	    	createGridBagContrains(GridBagConstraints.CENTER,4,4,4,4,0,0);
	    
	    // GridBagContraints for headlineLabel:
	    GridBagConstraints headlineLabelStyle = 
	    	createGridBagContrains(GridBagConstraints.CENTER, 6,6,6,6,0,0);
	    
	    // GridBagConstraints for textFieldPanel (s.b.):
	    GridBagConstraints textFieldPanelStyle = 
	    	createGridBagContrains(GridBagConstraints.CENTER,4,4,4,4,0,1);
	    
	    // GridBagConstraints for buttonPanel (s.b.):
	    GridBagConstraints buttonPanelStyle =
	    	createGridBagContrains(GridBagConstraints.CENTER,4,4,4,4,0,2);
   
	    // GridBagConstraints for serverTextField:
	    GridBagConstraints serverTextFieldStyle = 
	    	createGridBagContrains(GridBagConstraints.WEST, 6,6,6,6,1,0);
	    
	    // GridBagContraints for okButton (s.b.):
	    GridBagConstraints okButtonStyle = 
	    	createGridBagContrains(GridBagConstraints.CENTER, 6,6,6,6,0,0);

	    // GridBagContraints for cancelButton (s.b.):
	    GridBagConstraints cancelButtonStyle = 
	    	createGridBagContrains(GridBagConstraints.CENTER, 6,6,6,6,1,0);
	    
	    // Creating rootPanel (contains all components)
	    JPanel rootPanel = new JPanel(new GridBagLayout());
	    rootPanel.setFont(font);
	    
		// Creating headlineLabel:
	    JLabel headlineLabel = new JLabel(headlineLabelText);
	    headlineLabel.setFont(headlineLabelFont);

	    // Creating textFieldPanel:
	    JPanel textFieldPanel = new JPanel(new GridBagLayout());
	    textFieldPanel.setFont(font);

	    // Creating classTextField:
	    classBox = new JComboBox(this.classList);
	    classBox.setFont(textFieldFont);
	    
	    String classPath = this.controller.getMetaInfoField().getMetaInfos().getClassPath();
	    String className = this.controller.getMetaInfoField().getMetaInfos().getClassName();
	    
	    
	    if (setSelectedClass(classPath, className) == -1) headlineLabel.setForeground(Color.RED);
	    else headlineLabel.setForeground(Color.BLACK);
	    
	    headlineLabel.setText(this.headlineLabelText);
	    
	    // Creating buttonPanel:
	    JPanel buttonPanel = new JPanel(new GridBagLayout());
	    buttonPanel.setFont(font);

	    // okButton:
	    JButton okButton = new JButton(okButtonText);
	    okButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
	    okButton.setActionCommand(CommandConstants.META_INFO_FIELD_OK);
	    okButton.addActionListener(this.controller);
	    
	    // cancelButton:
	    JButton cancelButton = new JButton(cancelButtonText);
	    cancelButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
	    cancelButton.setActionCommand(CommandConstants.META_INFO_FIELD_CANCEL);
	    cancelButton.addActionListener(this.controller);
	    
	    // Composing the GUI:
	    this.getContentPane().setLayout(new GridBagLayout());
	    this.getContentPane().add(rootPanel, rootPanelStyle);
	    rootPanel.add(headlineLabel, headlineLabelStyle);
	    rootPanel.add(textFieldPanel, textFieldPanelStyle);
	    textFieldPanel.add(classBox, serverTextFieldStyle);
	    rootPanel.add(buttonPanel, buttonPanelStyle);
	    buttonPanel.add(okButton, okButtonStyle);
	    buttonPanel.add(cancelButton, cancelButtonStyle);

	    this.addWindowListener(this.windowListener);
	    
	    
	    int maxString =this.headlineLabelText.length();
	    for (int i=0;i<this.classList.size();i++){
	    	maxString = Math.max(((String)this.classList.get(i).toString()).length(),maxString);
	    }
	    
	    width= Math.max(width,maxString*8);
	    this.setSize(width, height);	
	}

	
	/**
	 *  
	 * @param classPath
	 * @param className
	 * @return 1 if the classPath and Name exists and set<br> 
	 * 	0 if before no calsspath was set <br>
	 * -1 if the classpath doesnt exists on that DB
 	 */
	private int setSelectedClass(String classPath, String className) {
		if (!(classPath.equals(XMLConstants.PATH_UNDEFINED))){// a valid path is set	
			
			int nrItems = this.classBox.getItemCount();
//			int nrItems = this.classPane.getLineCount();
			ELClassWrapperComparator elcomp = new ELClassWrapperComparator(false); 
			for (int i=0; i<nrItems; i++){
				if (0== elcomp.compare(this.classBox.getItemAt(i), new ELClassWrapper(className,classPath))){
					this.classBox.setSelectedIndex(i);
					this.headlineLabelText = "Lehrveranstaltung vom Server";
					return 1;
				}
			}
			
			this.headlineLabelText = "WARNING: die vorher gewählte Lehrveranstaltung existiert nicht";
			return -1;
		
		} 
		this.headlineLabelText = "Lehrveranstaltung vom Server";
		return 0;
	}

	/**
	 * Creates style
	 * @param anchor GridBagConstraints.anchor
	 * @param insetsTop GridBagConstraints.insets.top
	 * @param insetsRight GridBagConstraints.insets.right
	 * @param insetsBottom GridBagConstraints.insets.bottom
	 * @param insetsleft GridBagConstraints.insets.left
	 * @param x GridBagConstraints.gridx
	 * @param y GridBagConstraints.gridy
	 * @return new GridBagConstraints
	 */
	private GridBagConstraints createGridBagContrains(
							int anchor,
							int insetsTop,
							int insetsRight,
							int insetsBottom,
							int insetsleft,
							int x,
							int y) {
		GridBagConstraints compStyle = new GridBagConstraints();
	    compStyle.anchor = anchor;
	    compStyle.insets.top = insetsTop;
	    compStyle.insets.right = insetsRight;
	    compStyle.insets.bottom = insetsBottom;
	    compStyle.insets.left = insetsleft;
	    compStyle.gridx = x;
	    compStyle.gridy = y;
		return compStyle;
	}

	/**
     * Fetches an index of existing e-learning classes
     * ("Lehrveranstaltungen") from the JAPS server and turns that index
     * into a hash map. In that hash map, the e-learning class IDs are used
     * as keys, the values being the names of each e-learning class.
     *
     * @return a <code>Map</code>, as described above; the
     * <code>Map</code> may be <code>null</code> if the server could not be
     * connected to, or the response from the server could not be parsed.
     * @see ELClassListWrapper#getELClasses()
     */
	protected Map fetchELClassIndex(){
		HttpURLConnection conn = null;
		InputStream inst = null;
		
		try {
			
			conn = this.japsclient.get(TreeConstants.URL_EL_CLASS_LIST);
			inst = conn.getInputStream(); 
			
		} catch (Exception ex) {
			
			CCController.dialogErrorOccured(
					"ClassChooser Exception",
					"<html>Exception fetchELClassIndex() 1/2<p>"+ex+"</html>",
					JOptionPane.ERROR_MESSAGE);
			
			return null;
		} // end of try-catch

		ELClassListWrapper elcList; 
		try {
			elcList = ELClassListWrapper.getInstance(); 
			
			elcList.parse(inst); 
			
		} catch (Exception se) {
			
			CCController.dialogErrorOccured(
					"ClassChooser Exception",
					"<html>Exception fetchELClassIndex() 2/2<p>"+se+"</html>",
					JOptionPane.ERROR_MESSAGE);
			
			return null;
		} // end of try-catch

		return elcList.getELClasses();
	}		
	
	/** sets this Dialog non-visible */
	public void stop(){
		this.setVisible(false);
	}
	
	/**
	 * returns the path of the selected Class
	 * @return the path of the selected Class
	 */
	public String getClassPath(){
		if ((ELClassWrapper)this.classBox.getSelectedItem()==null) return "";
		return ((ELClassWrapper)this.classBox.getSelectedItem()).getPath();
	}
	
	/**
	 * returns the name of the selected Class
	 * @return the name of the selected Class
	 */
	public String getClassName(){
		if ((ELClassWrapper)this.classBox.getSelectedItem()==null) return "";
		return ((ELClassWrapper)this.classBox.getSelectedItem()).getName();
	}
	
	 /**
     * Inner class needed to access e-learning classes by their names
     * and/or IDs.
     * 
     * @see ELClassListWrapper
     */
    class ELClassWrapper {
    	String name;
    	String path;
    	
    	/**
    	 * Creates a new <code>ELClassWrapper</code> instance.
    	 *
    	 */
    	public ELClassWrapper(String theName, String thePath) {
    		name = theName;
    		this.path = thePath;
    	}
    	
    	/**
    	 * Get the Name value.
    	 * @return the Name value.
    	 */
    	public String getName() {return name;}

    	/**
    	 * Set the Name value.
    	 * @param newName The new Name value.
    	 */
    	public void setName(String newName) {name = newName;}

    	

    	/**
    	 * Get the full path value.
    	 * @returns the path value appended by the masterfile suffix.
    	 */
    	public String getPath() {return this.path;}

    	/**
    	 * Set the Id value.
    	 * @param newId The new Id value.
    	 */
    	public void setPath(String thePath) {this.path = thePath;}
    	public String toString() {return name;}
    } // end of inner class ELClassWrapper
    
    class ELClassWrapperComparator implements Comparator {

    	boolean compareOnlyByPath;

    	public ELClassWrapperComparator() {
    		compareOnlyByPath = false;
    	}

    	public ELClassWrapperComparator(boolean byId) {
    		compareOnlyByPath = byId;
    	}


    	// Implementation of java.util.Comparator

    	/**
    	 * Describe <code>compare</code> method here.
    	 *
    	 * @param object an <code>Object</code> value
    	 * @param object1 an <code>Object</code> value
    	 * @return 0 if 
    	 * <ul>
    	 * <li> both <code>object</code> and <code>object1</code> are
    	 * <code>ELClassWrapper</code>s</li> 
    	 * <li>both <code>object</code> and <code>object1</code> have the same Path</li> 
    	 * <li>both <code>object</code> and <code>object1</code> have the same name.</li>
    	 * </ul>
    	 * Otherwise, <code>compare</code> will return 42. 
    	 */
    	public int compare(Object object, Object object1) {
    		if ((object instanceof ELClassWrapper) 
    				&& (object1 instanceof ELClassWrapper)) 
    		{
    			ELClassWrapper ecw = (ELClassWrapper)object;
    			ELClassWrapper ecw1 = (ELClassWrapper)object1;
    			if (compareOnlyByPath) {
    				return ((ecw.getPath().equals(ecw1.getPath())) ? 0 : 42);
    			} // end of if (byIdOnly)
    			else {
    				return ((ecw.getPath().equals(ecw1.getPath()))
    						&& ecw.getName().equals(ecw1.getName()) 
    						? 0 : 42);
    			} // end of if (byIdOnly) else

    		} // end of if ( ... instanceof ELClassWrapper)
    		else {
    			return 42; 
    		} // end of if ( ... instanceof ELClassWrapper) else

    	}

    } // end of inner class ELClassWrapperComparator
	
}

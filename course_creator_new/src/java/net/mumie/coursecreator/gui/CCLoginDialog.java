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


import java.util.Vector;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


import net.mumie.coursecreator.CourseCreator;
import net.mumie.coursecreator.CCController;
import net.mumie.japs.client.ApplicationJapsClient;
import net.mumie.japs.client.JapsClient;
import net.mumie.japs.client.JapsClientException;


/**
 * This Class is changing the actual Server-Path in order to use several Japs-Servers with one 
 * CC instance. The whole CC gets the JapsClient for communicating with the Japs only from this class.
 * 
 * @author <a href="mailto:binder@math.tu-berlin.de">Jens Binder</a>
 *  * @author <a href="mailto:vrichter@math.tu-berlin.de">Verena Richter</a>
 * @version $Id: CCLoginDialog.java,v 1.26 2009/03/31 12:11:41 vrichter Exp $
 */
public class CCLoginDialog extends JDialog implements ComponentListener{
	static final long serialVersionUID=0;

	/** Indicates that the dialog has been successfully completed. */
	public static final int OK = 0;

	/** Indicates that the dialog has been aborted. */
	public static final int CANCELED = 1;
	
	/** Indicates that the user works offline */
	public static final int OFFLINE = 2;
	
	/** Indicates an undefined status. */
	public static final int UNDEFINED = -1;

	/** Command name indicating that the user wants to submit the data. */
	public static final String CMD_OK = "login_ok";

	/** Command name indicating that the user wants to abort the dialog without submitting the
	 * data. */
	public static final String CMD_CANCEL = "login_cancel";

	
	public static final String CMD_DELETE = "delete_server";
	/** Command name indicating that the user doesn't want to use a server */
	public static final String CMD_NO_SERVER = "no_server";
	
	/** The current status. */
	private int status = UNDEFINED;
	
	private static CourseCreator cc;
	private static CCController parent;
	private JComboBox serverTextField;
	private JapsClient japsclient;
	private boolean connected; 
	private JPanel textFieldPanel;
	/**
	 * The window listener.
	 */

	private WindowListener windowListener =
		new WindowAdapter ()
	{
		public void windowClosing (WindowEvent event)
		{
			CCLoginDialog dialog = CCLoginDialog.this;
			dialog.stop(CCLoginDialog.CANCELED);
		}
	};
	
	/**
	 * Constructor for a blank LoginDialog
	 * The User has to type in the Server including logindata in order to log into the
	 * JAPS-Server
	 * @param owner - the owner of this dialog gets activated after the OK-Button is pressed (when not already activated)
	 * @throws HeadlessException
	 */
	public CCLoginDialog(CCController owner) throws HeadlessException {
		super(CCController.frame,"Mumie-Japs Server");
		cc = CCController.frame;
		parent = owner;	
		
		this.buildLayout(parent.getConfig().getServerList());
		this.addComponentListener(this);
	}
	
	/** build the Layout */
	private void buildLayout(Vector<String> serverURL){
		
		// Dimensions of the Dialog		
		int width = 450;
	    int height = 160;
	    int buttonWidth = 80;
	    int buttonHeight = 30;
	    
	    // sets the Layout
		this.getContentPane().setLayout(new GridBagLayout());
		
		// the Button and Label Text
		String okButtonText = "Ok";
	    String cancelButtonText = "Cancel";
	    String noserverButtonText = "no Server";
	    String headlineLabelText = "Mumie Login - Server";
	    String serverLabelText = "Japs-Server:";
	    	    	    
	    // the Fonts
	    Font font = new Font("SansSerif", Font.PLAIN, 14);
	    Font headlineLabelFont = new Font("SansSerif", Font.PLAIN, 16);
	    Font textFieldFont = new Font("Monospaced", Font.PLAIN, 14);
	    
        // GridBagContraints for rootPanel (s.b.):
	    GridBagConstraints rootPanelStyle = new GridBagConstraints();
	    rootPanelStyle.anchor = GridBagConstraints.CENTER;
	    rootPanelStyle.insets.top = 4;
	    rootPanelStyle.insets.right = 4;
	    rootPanelStyle.insets.bottom = 4;
	    rootPanelStyle.insets.left = 4;
	    rootPanelStyle.gridx = 0;
	    rootPanelStyle.gridy = 0;
	    
	    // GridBagContraints for headlineLabel:
	    GridBagConstraints headlineLabelStyle = new GridBagConstraints();
	    headlineLabelStyle.anchor = GridBagConstraints.CENTER;
	    headlineLabelStyle.insets.top = 6;
	    headlineLabelStyle.insets.right = 6;
	    headlineLabelStyle.insets.bottom = 6;
	    headlineLabelStyle.insets.left = 6;
	    headlineLabelStyle.gridx = 0;
	    headlineLabelStyle.gridy = 0;
	    
	    // GridBagConstraints for textFieldPanel (s.b.):
	    GridBagConstraints textFieldPanelStyle = new GridBagConstraints();
	    textFieldPanelStyle.anchor = GridBagConstraints.CENTER;
	    textFieldPanelStyle.insets.top = 4;
	    textFieldPanelStyle.insets.right = 4;
	    textFieldPanelStyle.insets.bottom = 4;
	    textFieldPanelStyle.insets.left = 4;
	    textFieldPanelStyle.gridx = 0;
	    textFieldPanelStyle.gridy = 1;
	    
	    // GridBagConstraints for buttonPanel (s.b.):
	    GridBagConstraints buttonPanelStyle = new GridBagConstraints();
	    buttonPanelStyle.anchor = GridBagConstraints.CENTER;
	    buttonPanelStyle.insets.top = 4;
	    buttonPanelStyle.insets.right = 4;
	    buttonPanelStyle.insets.bottom = 4;
	    buttonPanelStyle.insets.left = 4;
	    buttonPanelStyle.gridx = 0;
	    buttonPanelStyle.gridy = 2;
	    
	    // GridBagConstraints for serverLabel:
	    GridBagConstraints serverLabelStyle = new GridBagConstraints();
	    serverLabelStyle.anchor = GridBagConstraints.WEST;
	    serverLabelStyle.insets.top = 6;
	    serverLabelStyle.insets.right = 6;
	    serverLabelStyle.insets.bottom = 6;
	    serverLabelStyle.insets.left = 6;
	    serverLabelStyle.gridx = 0;
	    serverLabelStyle.gridy = 0;

	    // GridBagConstraints for serverTextField:
	    GridBagConstraints serverTextFieldStyle = new GridBagConstraints();
	    serverTextFieldStyle.anchor = GridBagConstraints.WEST;
	    serverTextFieldStyle.insets.top = 6;
	    serverTextFieldStyle.insets.right = 6;
	    serverTextFieldStyle.insets.bottom = 6;
	    serverTextFieldStyle.insets.left = 6;
	    serverTextFieldStyle.gridx = 1;
	    serverTextFieldStyle.gridy = 0;
	    
	    // GridBagContraints for okButton (s.b.):
	    GridBagConstraints okButtonStyle = new GridBagConstraints();
	    okButtonStyle.anchor = GridBagConstraints.CENTER;
	    okButtonStyle.insets.top = 6;
	    okButtonStyle.insets.right = 6;
	    okButtonStyle.insets.bottom = 6;
	    okButtonStyle.insets.left = 6;
	    okButtonStyle.gridx = 0;
	    okButtonStyle.gridy = 0;

	    // GridBagContraints for cancelButton (s.b.):
	    GridBagConstraints cancelButtonStyle = new GridBagConstraints();
	    cancelButtonStyle.anchor = GridBagConstraints.CENTER;
	    cancelButtonStyle.insets.top = 6;
	    cancelButtonStyle.insets.right = 6;
	    cancelButtonStyle.insets.bottom = 6;
	    cancelButtonStyle.insets.left = 6;
	    cancelButtonStyle.gridx = 2;
	    cancelButtonStyle.gridy = 0;
	    
//	  GridBagContraints for noServerButton (s.b.):
	    GridBagConstraints noServerButtonStyle = new GridBagConstraints();
	    noServerButtonStyle.anchor = GridBagConstraints.CENTER;
	    noServerButtonStyle.insets.top = 6;
	    noServerButtonStyle.insets.right = 6;
	    noServerButtonStyle.insets.bottom = 6;
	    noServerButtonStyle.insets.left = 6;
	    noServerButtonStyle.gridx = 1;
	    noServerButtonStyle.gridy = 0;

	    
	    // GridBagContraints for cancelButton (s.b.):
	    GridBagConstraints deleteButtonStyle = new GridBagConstraints();
	    deleteButtonStyle.anchor = GridBagConstraints.CENTER;
	    deleteButtonStyle.insets.top = 6;
	    deleteButtonStyle.insets.right = 6;
	    deleteButtonStyle.insets.bottom = 6;
	    deleteButtonStyle.insets.left = 6;
	    deleteButtonStyle.gridx = 4;
	    deleteButtonStyle.gridy = 0;
	    // Creating rootPanel (contains all components)
	    JPanel rootPanel = new JPanel(new GridBagLayout());
	    rootPanel.setFont(font);
	    
		// Creating headlineLabel:
	    JLabel headlineLabel = new JLabel(headlineLabelText);
	    headlineLabel.setFont(headlineLabelFont);

	    // Creating textFieldPanel:
	    textFieldPanel = new JPanel(new GridBagLayout());
	    textFieldPanel.setFont(font);

	    // Creating serverLabel:
	    JLabel serverLabel = new JLabel(serverLabelText);

	    // Creating serverTextField:
	    serverTextField = new JComboBox(serverURL);
	    serverTextField.setEditable(true);
	    serverTextField.setFont(textFieldFont);
	    
		// Creating buttonPanel:
	    JPanel buttonPanel = new JPanel(new GridBagLayout());
	    buttonPanel.setFont(font);

	    // okButton:
	    JButton okButton = new JButton(okButtonText);
	    okButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
	    okButton.setActionCommand(CMD_OK);
	    okButton.addActionListener(parent);

	    // cancelButton:
	    JButton cancelButton = new JButton(cancelButtonText);
	    cancelButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
	    cancelButton.setActionCommand(CMD_CANCEL);
	    cancelButton.addActionListener(parent);
	    
	    // no_server Button
	    JButton noServerButton = new JButton(noserverButtonText);
	    noServerButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
	    noServerButton.setActionCommand(CMD_NO_SERVER);
	    noServerButton.addActionListener(parent);

	    JButton deleteButton = new JButton("delete Entry");
	    deleteButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
	    deleteButton.setActionCommand(CMD_DELETE);
	    deleteButton.addActionListener(parent);
	    
	    // Composing the GUI:
	    this.getContentPane().setLayout(new GridBagLayout());
	    this.getContentPane().add(rootPanel, rootPanelStyle);
	    rootPanel.add(headlineLabel, headlineLabelStyle);
	    rootPanel.add(textFieldPanel, textFieldPanelStyle);
	    textFieldPanel.add(serverLabel, serverLabelStyle);
	    textFieldPanel.add(serverTextField, serverTextFieldStyle);
	   
	    rootPanel.add(buttonPanel, buttonPanelStyle);
	    buttonPanel.add(okButton, okButtonStyle);
	    buttonPanel.add(noServerButton, noServerButtonStyle);
	    buttonPanel.add(cancelButton, cancelButtonStyle);
	    buttonPanel.add(deleteButton, deleteButtonStyle);
	    this.addWindowListener(this.windowListener);
	    
	    
	    int serverMax =0 ;
	    for (int i=0;i<serverURL.size();i++){
	    	serverMax = Math.max(((String)serverURL.get(i)).length(),serverMax);
	    }
	    width= Math.max(width,serverMax*12);//FIXME schoener waere getFontWidth...
	    this.setSize(width, height);	
	    this.setLocation(parent.getConfig().getPOSITION_LOGIN());
	} 
	
	
	
	public void stop(int status)
	{
		this.status = status;
		if ((status==OK)||(status==OFFLINE)) this.setVisible(false);
		if (status==OK) {
			CCController.frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			this.updateJapsClient();
			if (this.getConnected()){
				cc.setTitle("CourseCreator - "+this.getURLPrefix());
				CourseCreator.printStatus("changed Japs-Server",null);
				JOptionPane.showMessageDialog(CCController.frame, "<html>Eingeloggt auf dem Server:<br>"+ 
						this.getURLPrefix()+"</html>","Server", JOptionPane.INFORMATION_MESSAGE);
				CCController.frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}CCController.frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
		if (status==OFFLINE) {
			cc.setTitle("CourseCreator - OFFLINE");
			CourseCreator.printStatus("offline - Mode",null);
			JOptionPane.showMessageDialog(CCController.frame, "OFFLINE-Mode","OFFLINE-Mode", JOptionPane.INFORMATION_MESSAGE);
		}
		if (status==CANCELED){cc.getController().exitDialog();}
		if ((status==OK)||(status==OFFLINE)) if (!cc.isVisible()) cc.setVisible(true);
	}
	
	public int getStatus()	{
		return this.status;
	}
	
	public String getURLPrefix()
	{
		return ((String)this.serverTextField.getSelectedItem());
	}
	

	
	public boolean getConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	private void updateJapsClient(){
		
		String urlPrefix = this.getURLPrefix();
		babble("create new JapsClient with "+urlPrefix);
		this.japsclient = new ApplicationJapsClient(urlPrefix,cc);
		this.japsclient.setMaxLoginTries(3); 
 // just a try to login befor reading anything ...
		try {
			
			int ret = this.japsclient.login();
			if(ret==JapsClient.LOGIN_CANCELED){
				this.setConnected(false);
			}
			if(ret==JapsClient.LOGIN_FAILED){
				
				CCController.dialogErrorOccured(
						"CCLoginDialog: LoginFailed", 
						"CCLoginDialog: LoginFailed, Programm wird beendet", 
						JOptionPane.ERROR_MESSAGE);
				if (parent.dialogCloseCauseNotLogin())
				System.exit(0);
				else{
					this.setConnected(false);
				}
				
			}
			if (ret==JapsClient.LOGIN_SUCCESSFUL){
				parent.getConfig().addServer(urlPrefix,true,true);
				this.setConnected(true);
			}
		}
		catch(JapsClientException jce){
			CCController.dialogErrorOccured(
					"CCLoginDialog: LoginFailed", 
					"CCLoginDialog JapsClientException while login: " + jce, 
					JOptionPane.ERROR_MESSAGE);
			
			this.setConnected(false);
			parent.loginFailed(jce.getMessage());

		}
	}
	
	public JapsClient getJapsClient()
	{	
		return this.japsclient;
	}
	public void componentHidden(ComponentEvent e) {}

	public void componentMoved(ComponentEvent e) {
		parent.getConfig().setPOSITION_LOGIN(e.getComponent().getLocation());
		
	}

	public void componentResized(ComponentEvent e) {}

	public void componentShown(ComponentEvent e) {}
	void babble(String bab){
		if (parent.getConfig().getDEBUG()) System.out.println("CCLogindialog:"+bab);
	}
	
	/** loescht ersten eintrag aus Combobox */
	public void updateCombobox(){
		this.serverTextField.remove(0);

		if (this.serverTextField.getItemCount()!=0)
			this.serverTextField.setSelectedIndex(0);
		else this.serverTextField.setSelectedIndex(-1);
		this.serverTextField.setVisible(true);
		this.serverTextField.repaint();
		this.serverTextField.validate();
		
	}
}

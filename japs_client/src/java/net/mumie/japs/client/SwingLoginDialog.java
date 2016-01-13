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

package net.mumie.japs.client;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Frame;
import java.awt.Dimension;
import java.awt.Component;

/**
 * <p>
 *   Swing dialog window to request acoount and password.
 * </p>
 * <p>
 *   The window exists in two variants which behave identically but look differently, i.e.:
 * </p>
 * <table style="margin-left:25px;margin-top:5px;margin-bottom:5px;">
 *   <tr>
 *     <td style="vertical-align:top;">
 *       <img src="doc-files/SwingLoginDialog.png"/>
 *     </td>
 *     <td style="width:100px;text-align:center">and</td>
 *     <td style="vertical-align:top">
 *       <img src="doc-files/SwingLoginDialog_afterFailure.png"/>
 *     </td>
 *   </tr>
 * </table>
 * <p>
 *   The left variant is the default, while the right one should be used if the dialog
 *   immediately follows a failed dialog.
 * </p>
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Revision: 1.7 $</code>
 */

public class SwingLoginDialog extends JDialog
{
  /**
   * Indicates that the dialog has been successfully completed.
   */

  public static final int OK = 0;

  /**
   * Indicates that the dialog has been aborted.
   */

  public static final int CANCELED = 1;

  /**
   * Indicates an undefined status.
   */

  public static final int UNDEFINED = -1;

  /**
   * Command name indicating that the user wants to submit the data.
   */

  protected static final String CMD_OK = "ok";

  /**
   * Command name indicating that the user wants to abort the dialog without submitting the
   * data.
   */

  protected static final String CMD_CANCEL = "cancel";

  /**
   * Command name indicating that the user has finished the account input.
   */

  private static final String CMD_ACCOUNT_OK = "account_ok";

  /**
   * Text field to enter the account name.
   */

  protected JTextField accountTextField;

  /**
   * Text field to enter the password.
   */

  private JPasswordField passwordField;

  /**
   * The current status.
   */

  private int status = UNDEFINED;
  /**
   * Common action listener of all components.
   */

  protected ActionListener actionListener =
    new ActionListener ()
    {
      public void actionPerformed (ActionEvent event)
      {
        SwingLoginDialog dialog = SwingLoginDialog.this;
        String command = event.getActionCommand();
        if ( command.equals(SwingLoginDialog.CMD_OK) )
          dialog.stop(SwingLoginDialog.OK);
        else if ( command.equals(SwingLoginDialog.CMD_CANCEL) )
          dialog.stop(SwingLoginDialog.CANCELED);
        else
          throw new IllegalArgumentException("Unknown command: " + command);
      }
    };

  /**
   * The window listener.
   */

  protected WindowListener windowListener =
    new WindowAdapter ()
    {
      public void windowClosing (WindowEvent event)
      {
        SwingLoginDialog dialog = SwingLoginDialog.this;
        dialog.stop(SwingLoginDialog.CANCELED);
      }
    };

  /**
   * Returns the result of this dialog.
   */

  public LoginDialogResult getResult ()
  {
    LoginDialogResult result = null;
    switch ( this.status )
      {
      case OK:
        result = new LoginDialogResult
          (this.accountTextField.getText(), this.passwordField.getPassword());
        break;
      case CANCELED:
        break;
      default:
        throw new IllegalStateException
          ("Can not compose result: Inappropriate status: " + this.status);
      }
    return result;
  }

  /**
   * Creates a new login dialog.
   */

  public SwingLoginDialog (Frame owner,
                           String title,
                           String headline,
                           String defaultAccount,
                           boolean accountEditable,
                           boolean afterFailure)
  {
    super(owner, title, true);

    // Sizes:
    int width = 290;
    int height = (afterFailure ? 240 : 220);
    int buttonWidth = 80;
    int buttonHeight = 30;

    // Fonts:
    Font font = new Font("SansSerif", Font.PLAIN, 14);
    Font headlineLabelFont = new Font("SansSerif", Font.PLAIN, 16);
    Font errorLabelFont = new Font("SansSerif", Font.PLAIN, 14);
    Font textFieldFont = new Font("Monospaced", Font.PLAIN, 14);

    // Texts (labels and buttons):
    String headlineLabelText =
      (headline != null ? headline : (title != null ? title : "Mumie Login"));
    String errorLabelText = "Login failed - please try again";
    String accountLabelText = "Account:";
    String passwordLabelText = "Password:";
    String okButtonText = "Ok";
    String cancelButtonText = "Cancel";

    // Text field lengths:
    int accountTextFieldSize = 15;
    int passwordFieldSize = 15;

    // Colors:
    Color errorLabelForegroundColor = Color.RED;

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

    // GridBagConstraints for errorLabel:
    GridBagConstraints errorLabelStyle = new GridBagConstraints();
    errorLabelStyle.anchor = GridBagConstraints.CENTER;
    errorLabelStyle.insets.top = 6;
    errorLabelStyle.insets.right = 6;
    errorLabelStyle.insets.bottom = 6;
    errorLabelStyle.insets.left = 6;
    errorLabelStyle.gridx = 0;
    errorLabelStyle.gridy = 1;

    // GridBagConstraints for textFieldPanel (s.b.):
    GridBagConstraints textFieldPanelStyle = new GridBagConstraints();
    textFieldPanelStyle.anchor = GridBagConstraints.CENTER;
    textFieldPanelStyle.insets.top = 4;
    textFieldPanelStyle.insets.right = 4;
    textFieldPanelStyle.insets.bottom = 4;
    textFieldPanelStyle.insets.left = 4;
    textFieldPanelStyle.gridx = 0;
    textFieldPanelStyle.gridy = (afterFailure ? 2 : 1);

    // GridBagConstraints for buttonPanel (s.b.):
    GridBagConstraints buttonPanelStyle = new GridBagConstraints();
    buttonPanelStyle.anchor = GridBagConstraints.CENTER;
    buttonPanelStyle.insets.top = 4;
    buttonPanelStyle.insets.right = 4;
    buttonPanelStyle.insets.bottom = 4;
    buttonPanelStyle.insets.left = 4;
    buttonPanelStyle.gridx = 0;
    buttonPanelStyle.gridy = (afterFailure ? 3 : 2);

    // GridBagConstraints for accountLabel:
    GridBagConstraints accountLabelStyle = new GridBagConstraints();
    accountLabelStyle.anchor = GridBagConstraints.WEST;
    accountLabelStyle.insets.top = 6;
    accountLabelStyle.insets.right = 6;
    accountLabelStyle.insets.bottom = 6;
    accountLabelStyle.insets.left = 6;
    accountLabelStyle.gridx = 0;
    accountLabelStyle.gridy = 0;

    // GridBagConstraints for accountTextField:
    GridBagConstraints accountTextFieldStyle = new GridBagConstraints();
    accountTextFieldStyle.anchor = GridBagConstraints.WEST;
    accountTextFieldStyle.insets.top = 6;
    accountTextFieldStyle.insets.right = 6;
    accountTextFieldStyle.insets.bottom = 6;
    accountTextFieldStyle.insets.left = 6;
    accountTextFieldStyle.gridx = 1;
    accountTextFieldStyle.gridy = 0;

    // GridBagConstraints for passwordLabel:
    GridBagConstraints passwordLabelStyle = new GridBagConstraints();
    passwordLabelStyle.anchor = GridBagConstraints.WEST;
    passwordLabelStyle.insets.top = 6;
    passwordLabelStyle.insets.right = 6;
    passwordLabelStyle.insets.bottom = 6;
    passwordLabelStyle.insets.left = 6;
    passwordLabelStyle.gridx = 0;
    passwordLabelStyle.gridy = 1;

    // GridBagConstraints for passwordField:
    GridBagConstraints passwordFieldStyle = new GridBagConstraints();
    passwordFieldStyle.anchor = GridBagConstraints.WEST;
    passwordFieldStyle.insets.top = 6;
    passwordFieldStyle.insets.right = 6;
    passwordFieldStyle.insets.bottom = 6;
    passwordFieldStyle.insets.left = 6;
    passwordFieldStyle.gridx = 1;
    passwordFieldStyle.gridy = 1;

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
    cancelButtonStyle.gridx = 1;
    cancelButtonStyle.gridy = 0;

    // Creating rootPanel (contains all components)
    JPanel rootPanel = new JPanel(new GridBagLayout());
    rootPanel.setFont(font);

    // Creating headlineLabel:
    JLabel headlineLabel = new JLabel(headlineLabelText);
    headlineLabel.setFont(headlineLabelFont);

    // Creating errorLabel:
    JLabel errorLabel = new JLabel(errorLabelText);
    errorLabel.setFont(errorLabelFont);
    errorLabel.setForeground(errorLabelForegroundColor);

    // Creating textFieldPanel:
    JPanel textFieldPanel = new JPanel(new GridBagLayout());
    textFieldPanel.setFont(font);

    // Creating accountLabel:
    JLabel accountLabel = new JLabel(accountLabelText);

    // Creating accountTextField:
    this.accountTextField = new JTextField(accountTextFieldSize);
    if ( defaultAccount != null ) accountTextField.setText(defaultAccount);
    this.accountTextField.setFont(textFieldFont);
    this.accountTextField.setActionCommand(CMD_OK);
    this.accountTextField.addActionListener(this.actionListener);
    this.accountTextField.setEditable(accountEditable);

    // Creating passwordLabel:
    JLabel passwordLabel = new JLabel(passwordLabelText);

    // Creating passwordField:
    this.passwordField = new JPasswordField (passwordFieldSize);
    this.passwordField.setFont(textFieldFont);
    this.passwordField.setActionCommand(CMD_OK);
    this.passwordField.addActionListener(this.actionListener);

    // Creating buttonPanel:
    JPanel buttonPanel = new JPanel(new GridBagLayout());
    buttonPanel.setFont(font);

    // okButton:
    JButton okButton = new JButton(okButtonText);
    okButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
    okButton.setActionCommand(CMD_OK);
    okButton.addActionListener(this.actionListener);

    // cancelButton:
    JButton cancelButton = new JButton(cancelButtonText);
    cancelButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
    cancelButton.setActionCommand(CMD_CANCEL);
    cancelButton.addActionListener(this.actionListener);

    // Composing the GUI:
    this.getContentPane().setLayout(new GridBagLayout());
    this.getContentPane().add(rootPanel, rootPanelStyle);
    rootPanel.add(headlineLabel, headlineLabelStyle);
    if ( afterFailure ) rootPanel.add(errorLabel, errorLabelStyle);
    rootPanel.add(textFieldPanel, textFieldPanelStyle);
    textFieldPanel.add(accountLabel, accountLabelStyle);
    textFieldPanel.add(accountTextField, accountTextFieldStyle);
    textFieldPanel.add(passwordLabel, passwordLabelStyle);
    textFieldPanel.add(passwordField, passwordFieldStyle);
    rootPanel.add(buttonPanel, buttonPanelStyle);
    buttonPanel.add(okButton, okButtonStyle);
    buttonPanel.add(cancelButton, cancelButtonStyle);

    this.addWindowListener(this.windowListener);
    this.setSize(width, height);
    this.setVisible(true);
    
    // (defaultAccount != null ? this.passwordField : this.accountTextField).requestFocus();
  }

  /**
   * Creates a new login dialog.
   */

  public SwingLoginDialog (Frame owner, String title, String defaultAccount, boolean afterFailure)
  {
    this(owner, title, "Mumie Login", defaultAccount, true, afterFailure);
  }

  /**
   * Creates a new login dialog.
   */

  public SwingLoginDialog (Frame owner, String defaultAccount, boolean afterFailure)
  {
    this(owner, "Mumie Login", defaultAccount, afterFailure);
  }

  /**
   * Creates a new login dialog.
   */

  public SwingLoginDialog (Frame owner, boolean afterFailure)
  {
    this(owner, null, afterFailure);
  }

  /**
   * Stops the dialog.
   */

  protected void stop (int status)
  {
    this.status = status;
    this.setVisible(false);
    this.dispose();
  }
}

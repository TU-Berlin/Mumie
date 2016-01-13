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

package net.mumie.japs.client.test;

import javax.swing.JApplet;
import net.mumie.japs.client.SwingLoginDialog;
import net.mumie.japs.client.LoginDialogResult;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.Frame;

public class LoginDialogTest extends JApplet
{
  protected ActionListener actionListener =
    new ActionListener ()
    {
      public void actionPerformed (ActionEvent event)
      {
        System.out.println("DEBUG: actionPerformed(): started");
        LoginDialogTest.this.performTest();
        System.out.println("DEBUG: actionPerformed(): finished");
      }
    };

  protected void performTest ()
  {
    Frame frame = JOptionPane.getFrameForComponent(this);
    String afterFailureParam = this.getParameter("afterFailure");
    boolean afterFailure = ( afterFailureParam != null && afterFailureParam.equals("true") );
    String defaultAccount = this.getParameter("defaultAccount");
    SwingLoginDialog dialog = new SwingLoginDialog (frame, defaultAccount, afterFailure);
    LoginDialogResult result = dialog.getResult();
    if ( result == null )
      System.out.println("TEST: performTest (): result == null");
    else
      {
        System.out.println("TEST: performTest (): account == " + result.getAccount() + ", " +
                           "password == " + result.getPasswordAsString());
        
      }
  }

  protected void createGUI ()
  {
    System.out.println("DEBUG: createGUI(): started");
    JButton button = new JButton("Login Dialog Test");
    button.addActionListener(this.actionListener);
    this.getContentPane().add(button, BorderLayout.CENTER);
    System.out.println("DEBUG: createGUI(): finished");
  }

  public void init ()
  {
    System.out.println("DEBUG: init(): started");
    this.createGUI();
    System.out.println("DEBUG: init(): finished");
  }

}

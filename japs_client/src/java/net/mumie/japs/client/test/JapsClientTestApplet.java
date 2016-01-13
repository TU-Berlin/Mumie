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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import net.mumie.japs.client.AppletJapsClient;

public class JapsClientTestApplet extends JApplet
{
  static final int ROWS = 40;

  static final int COLUMNS = 80;

  protected AppletJapsClient client =
    new AppletJapsClient("http://habnix.math.tu-berlin.de/cocoon", this);

  protected JTextField urlTextField = null;

  protected JTextArea textArea = null;

  static final String SEPARATOR =
    "===========================================================================";

  protected void fetchURL ()
  {
    try
      {
        System.out.println("DEBUG: fetchURL(): started");
        String path = this.urlTextField.getText();
        HttpURLConnection connection = this.client.get(path, null, true);
        if ( connection != null )
          {
            BufferedReader reader =
              new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ( (line = reader.readLine()) != null )
              this.textArea.append(line + "\n");
            reader.close();
          }
        this.textArea.append(SEPARATOR + "\n");
        System.out.println("DEBUG: fetchURL(): finished");
      }
    catch (Exception exception)
      {
        exception.printStackTrace();
      }
  }

  protected ActionListener testActionListener =
    new ActionListener ()
    {
      public void actionPerformed (ActionEvent event)
      {
        JapsClientTestApplet applet = JapsClientTestApplet.this;
        System.out.println("DEBUG: actionPerformed(): started");
        applet.fetchURL();
        System.out.println("DEBUG: actionPerformed(): finished");
      }
    };

  protected void createAndRun ()
  {
    System.out.println("DEBUG: createAndRun(): started");

    Font textFont = new Font("Monospaced", Font.PLAIN, 14);

    this.urlTextField = new JTextField(COLUMNS);
    this.urlTextField.setFont(textFont);
    this.urlTextField.addActionListener(this.testActionListener);
    this.urlTextField.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

    JPanel urlTextFieldPanel = new JPanel(new BorderLayout());
    urlTextFieldPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    urlTextFieldPanel.add(this.urlTextField, BorderLayout.CENTER);
    
    this.textArea = new JTextArea(ROWS, COLUMNS);
    this.textArea.setFont(textFont);
    this.textArea.setEditable(false);

    JPanel textAreaPanel = new JPanel(new BorderLayout());
    textAreaPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    textAreaPanel.add(this.textArea, BorderLayout.CENTER);

    this.getContentPane().add(urlTextFieldPanel, BorderLayout.PAGE_START);
    this.getContentPane().add(new JScrollPane(textAreaPanel), BorderLayout.CENTER);
    ((BorderLayout)this.getContentPane().getLayout()).setVgap(2);

    System.out.println("DEBUG: createAndRun(): finished");
  }

  public void init ()
  {
    System.out.println("DEBUG: init(): started");
    this.createAndRun();
    System.out.println("DEBUG: init(): finished");
  }
}

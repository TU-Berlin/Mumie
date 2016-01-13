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

package net.mumie.scratch;

import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * @mm.type applet
 * @mm.section samples
 * @mm.sign no
 * @mm.description Dummy problem applet
 * @mm.width 200
 * @mm.height 50
 */

public class ProblemApplet extends JApplet 
{
  public void init()
  {
    JLabel label = new JLabel("Ok");
    label.setHorizontalAlignment(SwingConstants.CENTER);
    this.getContentPane().add(label);
  }
  
  public void selectData (String path)
  {
    System.out.println("selectData: path = " + path);
  }
  
  public void selectSubtask (int subtask)
  {
    System.out.println("selectSubtask: subtask = " + subtask);
  }
  
  public void showAppletFrame ()
  {
    System.out.println("showAppletFrame");
  }
  
  public void saveAnswers ()
  {
    System.out.println("saveAnswers");
  }
  
  public void setControl (String name, String value)
  {
    System.out.println("setControl: name = " + name + ", value = " + value);
  }
  
  public void unsetControl (String name)
  {
    System.out.println("unsetControl: name = " + name);
  }
	
  public void toggleControl (String name, String value)
  {
    System.out.println("toggleControl: name = " + name + ", value = " + value);
  }

  public void ping ()
  {
    System.out.println("ping");
  }
}

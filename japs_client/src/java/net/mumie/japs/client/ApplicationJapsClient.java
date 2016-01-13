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

import javax.swing.JFrame;
import java.awt.Frame;
import javax.swing.JOptionPane;

/**
 * A special Japs client for applets.
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @author Jens Binder <a href="mailto:binder@math.tu-berlin.de">binder@math.tu-berlin.de</a>
 * @version <code>$Revision: 1.3 $</code>
 */

public class ApplicationJapsClient extends CookieEnabledJapsClient
{
  /**
   * The Frame that is the "owner" of this client.
   */

  protected JFrame frame = null;

  /**
   * Performs a login dialog via a {@link SwingLoginDialog} window.
   */

  public LoginDialogResult performLoginDialog (boolean afterFailure)
  {
    Frame frame = JOptionPane.getFrameForComponent(this.frame);
    SwingLoginDialog dialog = new SwingLoginDialog(frame, afterFailure);
    return dialog.getResult();
  }

  /**
   * Creates a new applet japs client with <code>applet</code> as owner.
   */

  public ApplicationJapsClient (String urlPrefix, JFrame frame)
  {
    super(urlPrefix);
    if ( frame == null )
      throw new IllegalArgumentException("Application reference null");
    this.frame = frame;
  }

  /**
   * <p>
   *   Writes a log message. Simply prints
   * </p>
   * <pre>
   *   "JapsClient: " + message</pre>
   * <p>
   *   to <code>System.out</code>. A newline is automatically added.
   * </p>
   */

  protected void log (String message)
  {
    System.out.println("JapsClient: " + message);
  }
}

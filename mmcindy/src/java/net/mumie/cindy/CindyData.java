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

package net.mumie.cindy;

import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Comprises the data of a Cinderelly visualization.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: CindyData.java,v 1.4 2008/09/10 15:53:55 rassy Exp $</code>
 */

public class CindyData
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * Width of the cindy applet.
   */

  protected int width;

  /**
   * Height of the cindy applet.
   */

  protected int height;

  /**
   * Location of the Cindy script
   */

  protected String scriptLocation;

  /**
   * Parameters of the cindy applet.
   */

  protected Map<String,String> params = new HashMap<String,String>();

  // --------------------------------------------------------------------------------
  // Set methods
  // --------------------------------------------------------------------------------

  /**
   * Sets the width of the cindy applet.
   */

  public void setWidth (int width)
  {
    this.width = width;
  }

  /**
   * Sets the width of the cindy applet.
   */

  public void setWidth (String width)
  {
    this.setWidth(Integer.parseInt(width));
  }

  /**
   * Sets the height of the cindy applet.
   */

  public void setHeight (int height)
  {
    this.height = height;
  }

  /**
   * Sets the height of the cindy applet.
   */

  public void setHeight (String height)
  {
    this.setHeight(Integer.parseInt(height));
  }

  /**
   * Sets the location of the cindy script
   */

  public void setScriptLocation (String scriptLocation)
  {
    this.scriptLocation = scriptLocation;
  }

  /**
   * Sets a parameter for the Cinderelly applet.
   */

  public void setParam (String name, String value)
  {
    this.params.put(name, value);
  }

  // --------------------------------------------------------------------------------
  // Get methods
  // --------------------------------------------------------------------------------

  /**
   * Returns the width of the cindy applet.
   */

  public final int getWidth ()
  {
    return this.width;
  }

  /**
   * Returns the height of the cindy applet.
   */

  public final int getHeight ()
  {
    return this.height;
  }

  /**
   * Returns the location of the cindy script.
   */

  public final String getScriptLocation ()
  {
    return this.scriptLocation;
  }

  /**
   * Returns the value of the specified parameter.
   */

  public final String getParam (String name)
  {
    return this.params.get(name);
  }

  /**
   * Returns all parameters as a map.
   */

  public Map<String,String> getParams ()
  {
    return Collections.unmodifiableMap(this.params);
  }

  // --------------------------------------------------------------------------------
  // Utilities
  // --------------------------------------------------------------------------------

  /**
   * Prints this data to the specified print stream.
   */

  public void dump (PrintStream out)
  {
    out.println("width          : " + this.width);
    out.println("height         : " + this.height);
    out.println("scriptLocation : " + this.scriptLocation);
    out.println("params         : ");
    for (Map.Entry<String,String> entry : this.params.entrySet())
      out.println("  " + entry.getKey() + " = " + entry.getValue());
  }

}

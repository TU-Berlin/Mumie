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

import java.io.File;
import org.w3c.dom.Document;
import org.apache.excalibur.xml.dom.DOMParser;
import org.xml.sax.InputSource;
import net.mumie.japs.datasheet.DataSheet;
import net.mumie.cocoon.util.CocoonEnabledDataSheet;
import net.mumie.cocoon.util.DefaultCocoonEnabledDataSheet;

public class SampleMergeCocoon
{
  protected static int getMode (String name)
  {
    if ( name.equalsIgnoreCase("replace") )
      return DataSheet.REPLACE;
    else if ( name.equalsIgnoreCase("protect") )
      return DataSheet.PROTECT;
    else if ( name.equalsIgnoreCase("append") )
      return DataSheet.APPEND;
    else
      throw new IllegalArgumentException("Unknwon mode: " + name);
  }

  public static void main (String[] params)
    throws Exception
  {
    DOMParser parser = DOMParserFactory.newParser(params.length >= 1 ? params[0] : "jaxp");

    int mode = getMode(params.length >= 2 ? params[1] : "replace");

    Document document1 = parser.parseDocument(new InputSource("SampleMerge1.xml"));
    CocoonEnabledDataSheet dataSheet1 = new DefaultCocoonEnabledDataSheet();
    dataSheet1.setDocument(document1);

    Document document2 = parser.parseDocument(new InputSource("SampleMerge2.xml"));
    CocoonEnabledDataSheet dataSheet2 = new DefaultCocoonEnabledDataSheet();
    dataSheet2.setDocument(document2);
    
    dataSheet1.merge(dataSheet2, mode);

    dataSheet1.indent();
    dataSheet1.toXMLCode(System.out);
    System.out.print("\n");
  }
}

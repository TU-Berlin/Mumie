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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.excalibur.xml.dom.DOMParser;
import org.xml.sax.InputSource;
import net.mumie.cocoon.util.CocoonEnabledDataSheet;
import net.mumie.cocoon.util.DefaultCocoonEnabledDataSheet;

public class SampleReadCocoon
{
  public static void main (String[] params)
    throws Exception
  {
    DOMParser parser = DOMParserFactory.newParser(params.length != 0 ? params[0] : "jaxp");
    String filename = "SampleRead.xml";
    Document document = parser.parseDocument(new InputSource(filename));

    CocoonEnabledDataSheet dataSheet = new DefaultCocoonEnabledDataSheet();
    dataSheet.setDocument(document);
    String string_1_1 = dataSheet.getAsString("unit_1/data_1_1");
    boolean boolean_1_2 = dataSheet.getAsBoolean("unit_1/data_1_2", false);
    Element element_1_3 = dataSheet.getAsElement("unit_1/data_1_3");
    int int_2_1 = dataSheet.getAsInteger("unit_2/data_2_1", -1);
    System.out.println
      (
       "string_1_1  = " + string_1_1 + "\n" +
       "boolean_1_2 = " + boolean_1_2 + "\n" +
       "element_1_3 = " + element_1_3.getTagName() + "\n" +
       "int_2_1     = " + int_2_1 + "\n"
      );
  }
}

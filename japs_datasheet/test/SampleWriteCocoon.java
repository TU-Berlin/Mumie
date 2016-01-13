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

import org.apache.excalibur.xml.dom.DOMParser;
import net.mumie.cocoon.util.CocoonEnabledDataSheet;
import net.mumie.cocoon.util.DefaultCocoonEnabledDataSheet;

public class SampleWriteCocoon
{
  public static void main (String[] params)
    throws Exception
  {
    DOMParser parser = DOMParserFactory.newParser(params.length != 0 ? params[0] : "jaxp");
    CocoonEnabledDataSheet dataSheet = new DefaultCocoonEnabledDataSheet();
    dataSheet.setEmptyDocument(parser);

    String string_1_1 = "Hello World!";
    boolean boolean_1_2 = true;
    int int_2_1 = 12;

    dataSheet.put("unit_1/data_1_1", string_1_1);
    dataSheet.put("unit_1/data_1_2", boolean_1_2);
    dataSheet.put("unit_2/data_2_1", int_2_1);

    dataSheet.indent();

    dataSheet.toXMLCode(System.out);
    System.out.print("\n");
  }
}







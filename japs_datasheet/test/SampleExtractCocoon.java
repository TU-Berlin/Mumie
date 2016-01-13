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
import org.xml.sax.InputSource;
import org.apache.excalibur.xml.dom.DOMParser;
import net.mumie.cocoon.util.CocoonEnabledDataSheet;
import net.mumie.cocoon.util.DefaultCocoonEnabledDataSheet;
import net.mumie.japs.datasheet.DataSheet;

public class SampleExtractCocoon
{
  static final String PARSER_KEY = "jaxp";

  public static void main (String[] params)
    throws Exception
  {
    String filename =
      (params.length != 0 ? params[0] : "SampleExtract.xml");

    DOMParser parser = DOMParserFactory.newParser(PARSER_KEY);
    Document sourceDocument = parser.parseDocument(new InputSource(filename));

    CocoonEnabledDataSheet dataSheet = new DefaultCocoonEnabledDataSheet();
    dataSheet.setEmptyDocument(parser);
    dataSheet.extract(sourceDocument, DataSheet.REPLACE);

    dataSheet.indent();

    dataSheet.toXMLCode(System.out);
    System.out.print("\n");
  }
}







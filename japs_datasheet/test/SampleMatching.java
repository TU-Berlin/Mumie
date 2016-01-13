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

import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.DocumentBuilder; 
import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import net.mumie.japs.datasheet.DataSheet;

public class SampleMatching
{
  static void printArray (String[] strings)
  {
    for (int i = 0; i < strings.length; i++)
      System.out.print("  " + strings[i] + "\n");
  }

  public static void main (String[] params)
    throws Exception
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(new File("SampleMatching.xml"));

    DataSheet dataSheet = new DataSheet(document);
    String[] matchingDataPaths =
      dataSheet.getMatchingDataPaths("common/problem/vectors/vector_*");
    String[] matchingUnitPaths =
      dataSheet.getMatchingUnitPaths("common/problem/subtask_?");
    System.out.println("matchingDataPaths = ");
    printArray(matchingDataPaths);
    System.out.println("matchingUnitPaths = ");
    printArray(matchingUnitPaths);
  }
}
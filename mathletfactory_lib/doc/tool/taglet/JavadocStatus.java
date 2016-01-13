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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Pattern;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;

class ClassStatus {
  private String pack;
  private String name;
  private String author;
  private String status;
  public ClassStatus(String pack, String name, String author, String status){
    this.pack = pack;
    this.name = name;
    this.author = author;
    this.status = status;
  }
  public String getPackage() { return pack; }
  public String getName() { return name; } 
  public String getAuthor() { return author; }
  public String getStatus() { return status; }
}

public class JavadocStatus {
  public static final String INTRO = 
    "<html><head><title>Javadoc todo list</title></head><body bgcolor=\"#FEFFEC\"><center><h1>Status of API documentation</h1> ("+new Date()+")<br><br>" +
    "<br><br><body><center><table bgcolor=gray border=0 cellpadding=3 cellspacing=1><tr>" +
    "<th bgcolor=white>Package</th bgcolor=white><th bgcolor=white>Class</th bgcolor=white><th bgcolor=white>Author</th bgcolor=white><th bgcolor=white>Status</th bgcolor=white></tr>";
  
  public static final String END = "</center></body></html>";
  
  public static String getTag(ClassDoc doc, String tagName, String defaultValue) {
    StringBuffer result = new StringBuffer();
    Tag[] tags  = doc.tags(tagName);
    if (tags.length == 0) {
      result.append(defaultValue);
    } else {
      for (int i = 0; i < tags.length; i++) {
        result.append(tags[i].text());
        if (i < tags.length - 1)
          result.append(", ");
      }
    }
    return result.toString();
  }
  
  public static boolean start(RootDoc root) {
    int none = 0, outstanding = 0, finished = 0, processing = 0;
    ClassDoc[] classes = root.classes();
    ArrayList list = new ArrayList();
    Comparator comp = getComparator(root.options());
    Pattern authorRE = Pattern.compile(getOptionString(root.options(), "-author"));
    Pattern statusRE =  Pattern.compile(getOptionString(root.options(), "-status"));
    Pattern packageRE = Pattern.compile(getOptionString(root.options(), "-pack"));
    String linkTo = getOptionString(root.options(), "-link");
    String output = getOptionString(root.options(), "-destdir");
    if(output.equals(".*"))
      output = ".";
    
    for (int i = 0; i < classes.length; ++i) {
      //System.out.println("checking "+classes[i].containingPackage().toString());
      String packageName = classes[i].containingPackage().toString(); 
      if(packageName.matches(".*\\.test(\\..*|)$") || packageName.matches(".*\\.applet(_.*|)\\..*") ||
         classes[i].containingClass() != null){
        System.out.println("omitting "+classes[i]);      
        continue;
      }
      ClassStatus status = new ClassStatus(
          classes[i].containingPackage().name(),
          classes[i].name(),
          getTag(classes[i], "author", "nobody"),
          getTag(classes[i], "mm.docstatus", "none"));
      if (authorRE.matcher(status.getAuthor()).matches() &&
          statusRE.matcher(status.getStatus()).matches() &&
          packageRE.matcher(status.getPackage()).matches())
        list.add(status);
    }
    if (comp != null)
      Collections.sort(list, comp);
  
    try {
      OutputStreamWriter out = 
          new OutputStreamWriter(
          new BufferedOutputStream(
          new FileOutputStream(output+"/doc_todo.html")));
      out.write(INTRO);
      for (int i = 0; i < list.size(); ++i) {
        ClassStatus status = ((ClassStatus)list.get(i));
        String color = "white";
        if(status.getStatus().equals("finished")){        
          color = "#ccffcc";
          finished++;
        }
        if(status.getStatus().equals("outstanding")){        
          color = "#ffcccc";
          outstanding++;
        }
        if(status.getStatus().equals("processing")){        
          color = "#ffffcc";
          processing++;
        }
        if(status.getStatus().equals("none")){        
          none++;
        }
        out.write("<tr><td bgcolor="+color+">");
        out.write(status.getPackage());
        out.write("</td><td bgcolor="+color+">");
        if (linkTo.length() > 0) {
          String pName = status.getPackage();
          pName = pName.replaceAll("\\.", "/");
          out.write("<a href=" + linkTo + "/" + pName + "/" + 
              status.getName() + ".html>");
        }
        out.write(status.getName());
        if (linkTo.length() > 0) {
          out.write("</a>");
        }
        out.write("</td><td bgcolor="+color+">");
        out.write(status.getAuthor());
        out.write("</td><td bgcolor="+color+">");
        out.write(status.getStatus());
        out.write("</td></tr>");
      }
      int total = finished+processing+outstanding+none;
      out.write("</table><br> Docstatus: "+finished+" finished, "+processing
                +" processing, "+outstanding+" outstanding, "+none+" none. (Total: "
                +(total)+" => "+new DecimalFormat().format(100.0*finished/(1.0*total))+"% finished)");
      out.write(END);
      out.flush();
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return true;
  }

  static class DocComparator implements Comparator {
    private String[] sortOrder;
    public DocComparator(String sortString) {
      String[] parts = sortString.split(",");
      for (int i = 0; i < parts.length; i++) {
        if (parts[i].startsWith("+") || parts[i].startsWith("-"))
          continue;
        parts[i] = "+" + parts[i];
      }
      sortOrder = parts;
    }
    
    private int compareField(ClassStatus o1, ClassStatus o2, int fieldIndex) {
      String current = sortOrder[fieldIndex];
      int faktor = current.startsWith("-") ? -1 : 1;
      int result = 0;
      switch (current.charAt(1)) {
        case 'p':
          return faktor * o1.getPackage().compareTo(o2.getPackage());
        case 'n':
          return faktor * o1.getName().compareTo(o2.getName());
        case 'a':
          return faktor * o1.getAuthor().compareTo(o2.getAuthor());
        case 's':
          return faktor * o1.getStatus().compareTo(o2.getStatus());
      }
      return 0;
    }
    
    public int compare(Object o1, Object o2) {
      int result = 0;
      int fieldIndex = 0;
      ClassStatus c1 = (ClassStatus)o1;
      ClassStatus c2 = (ClassStatus)o2;
      while ((fieldIndex < sortOrder.length) && (result == 0)) {
        result = compareField(c1, c2, fieldIndex);
        fieldIndex++;
      }
      return result;
    }
        
    public boolean equals(Object obj) {
      return super.equals(obj);
    }
    
  }
  
  public static Comparator getComparator(String[][] options) {
    for (int i = 0; i < options.length; i++) {
      String[] opt = options[i];
      if (opt[0].equals("-sort")) {
        return new DocComparator(opt[1]);
      }
    }
    return null;
  }

  private static String getOptionString(String[][] options, String optionName) {
    for (int i = 0; i < options.length; i++) {
      String[] opt = options[i];
      if (opt[0].equals(optionName)) {
        return opt[1];
      }
    }
    return ".*";
  }
  
  public static int optionLength(String option) {
    if (option.equals("-sort"))
      return 2;
    if (option.equals("-author"))
      return 2;
    if (option.equals("-status"))
      return 2;
    if (option.equals("-pack"))
      return 2;
    if (option.equals("-link"))
      return 2;
    if (option.equals("-destdir"))
      return 2;
    return 0;
  }
}

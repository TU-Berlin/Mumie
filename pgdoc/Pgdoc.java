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

// package pgdoc;

import org.postgresql.jdbc2.*;
import java.sql.*;

public class Pgdoc
{
  public static void main(String[] args)
  {
    if ( args.length != 4 ){
      System.out.println("usage: gib 4 Argumente an:");
      System.out.println("1. den Namen der Rechners, auf dem der DB-Server laeuft");
      System.out.println("2. den Namen der Datenbank");
      System.out.println("3. deinen Usernamen bei der Datenbank");
      System.out.println("4. dein Passwort");
    }
    else try
      {
	Class.forName("org.postgresql.Driver");
	String url = "jdbc:postgresql://"+args[0]+"/"+args[1];
	String user = args[2];
	String database = "pgdocsample";
	String query = null;
	String query1 = null;
	String query2 = null;
	String tname = null;
  char ttype;
	String colname = null;
	String attrelid = null;
	String[] coltypes =  new String[2000];

	// initialisieren der Postgres-Datentypen
	for (int i=0;i<2000;i++) coltypes[i] = (new Integer(i)).toString();

	boolean isresult = true;
	java.sql.Connection con, con1, con2;
	java.sql.Statement st, st1, st2;
	java.sql.ResultSet rs, rs1, rs2;

	con = DriverManager.getConnection(url,user,args[3]);
	st = (java.sql.Statement)con.createStatement();
	con1 = DriverManager.getConnection(url,user,args[3]);
	st1 = (java.sql.Statement)con.createStatement();
	con2 = DriverManager.getConnection(url,user,args[3]);
	st2 = (java.sql.Statement)con.createStatement();
//Die Verbindungen zum DB-Server sollte jetzt stehen

	// initialisieren der Postgres-Datentypen
	//String tmp = null;
  String uglychar = "\"char\"";
	java.sql.PreparedStatement pst =
    con.prepareStatement("select format_type(?,NULL)");
	for (int i=0;i<2000;i++) {
	  //tmp = (new Integer(i)).toString();
    pst.setInt(1,i);
	  rs = pst.executeQuery();
	  if ( rs.next() ) {
      coltypes[i] = rs.getString(1).replace('"',' ');
      if ( coltypes[i].equals(uglychar) ) coltypes[i] = "char";
    }
	  else coltypes[i] = null;
	  rs.close();
	  st.close();
	} // end for (i...

  // Schreibe einen header

  System.out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
  System.out.println("<?xml-stylesheet type=\"text/xsl\" href=\"table.xsl\"?>");

  // Schreibe ein Root tag

  System.out.println("<dbtables>");
  System.out.println("  <dbcontent>");

	// hole alle Tabellen ( im weiteren Sinn ) in rs und beginne Schleife
	//  durch alle diese Tabellen

	query = "select relname,relfilenode,relkind from pg_class where relname !~ '^pg_' and relkind !='S' and relkind !='i' order by relkind";
	rs = st.executeQuery(query);
	while ( rs.next()) {
	  tname = rs.getString(1).replace('"',' ');
	  attrelid = rs.getString(2);
    ttype = rs.getString(3).charAt(0);
	  System.out.println("    <table relid=\""+attrelid+"\" type=\""
      +classType(ttype)+"\" name=\""+tname+"\">");
	  //System.out.println("      <tname>"+tname+"</tname>");
    System.out.println("      <columns>");
	  // hole alle Spalten der aktuellen Tabelle in rs1 und beginne Schleife
	  //  durch alle diese Spalten
	  query1 = "select attname,atttypid,attnotnull,attnum from pg_attribute where attrelid = "+ rs.getString(2)+" and attname !~ 'ctid' and attname !~ 'oid' and attname !~ 'xmin' and attname !~ 'cmin' and attname !~ 'xmax' and attname !~ 'ctid' and attname !~ 'cmax' and attname !~ 'tabloid' and attname !~ 'ctid'";
	  rs1 = st1.executeQuery(query1);
	  while ( rs1.next() ) {
	    System.out.print("        <column type=\""+coltypes[rs1.getInt(2)]+"\"");
	    if (rs1.getBoolean(3)) System.out.print(" null=\"not null\"");
	    //System.out.println(">");
	    colname = rs1.getString(1).replace('"',' ');
	    System.out.print(" name=\""+colname+"\"");
	    query2 = "select adsrc from pg_attrdef where adrelid = " + attrelid + " and adnum = " + rs1.getInt(4);
	    rs2 = st2.executeQuery(query2);
	    if ( rs2.next() )
	      System.out.print(" default=\""+rs2.getString(1).replace('"',' ').replace('<','*').replace('>','*')+"\"");
	    rs2.close();
	    System.out.println("/>");
	  } // end while ( rs1 ... ) die Schleife durch die Spalten einer Tabelle
	  rs1.close();
    System.out.println("      </columns>");

	  // finde die Referenzen von der aktuellen Tabelle zu anderen (in rs1)
	  //  und von anderen Tabellen auf diese (in rs2),
	  //  vorausgesetzt, es gibt den View "my_references"

	  try{
	    System.out.println("      <references>");
	    query1 = "select * from my_references where \"FromOID\" = " + attrelid;
	    rs1 = st1.executeQuery(query1);
	    while ( rs1.next() ) {
	    System.out.print("        <reference ");
	    System.out.print("fromcolumn=\"" + rs1.getString(3).replace('"',' '));
	    if (rs1.getString(4) != null) // hier sollte != null oder wenigstens !="" stehen koennen. Dazu Aenderungen in Funktion chunk ?! - TODO !
	      System.out.print(","+rs1.getString(4).replace('"',' '));
	    System.out.print("\" totable=\"" + rs1.getString(5).replace('"',' ') + "\" ");
	    System.out.print("tocolumn=\"" + rs1.getString(6).replace('"',' '));
	    if (rs1.getString(7) != null)
	      System.out.print(","+rs1.getString(7).replace('"',' '));
	    System.out.println("\"/>");
	    //System.out.println("      </reference>");

	    } // end while (rs1 ... ) die Schleife durch
	       //  die Referenzen VON dieser Tabelle zu anderen
	    // jetzt die Referenzen von anderen Tabellen zu dieser
	    query2 = "select * from my_references where \"ToOID\" = " + attrelid;
	    rs2 = st2.executeQuery(query2);
	    while ( rs2.next() ) {
	    System.out.print("        <referenced ");
	    System.out.print("fromtable=\"" + rs2.getString(2).replace('"',' ') + "\" ");
	    System.out.print("fromcolumn=\"" + rs2.getString(3).replace('"',' '));
	    if (rs2.getString(4) != null)
	      System.out.print(","+rs2.getString(4).replace('"',' '));
	    System.out.print("\" tocolumn=\"" + rs2.getString(6).replace('"',' '));
	    if (rs2.getString(7) != null)
	      System.out.print(","+rs2.getString(7).replace('"',' '));
	    System.out.println("\"/>");
	    //System.out.println("      </referenced>");

	    } // end while (rs2 ... ) die Schleife durch
	       //  die Referenzen VON anderen Tabelle zu dieser
	  }catch (SQLException e){}
	  System.out.println("      </references>");
	  System.out.println("    </table>");
	} // end while ( rs ... ) die Schleife durch die Tabellen
	rs.close();
	st2.close();
	st1.close();
	st.close();
	con2.close();
	con1.close();
	con.close();
  // Schliesse die Root tags

	System.out.println("  </dbcontent>");
	System.out.println("</dbtables>");

      } // end try
    catch (Throwable t)
      {
	t.printStackTrace();
      }

  } // end main

  private static String classType(char ttype)
  {
    switch (ttype) {
    case 'i': return "Index";
    case 'r': return "Table";
    case 'v': return "View";
    case 'S': return "Sequence";
    case 't': return "toast";
    case 's': return "system";
    default: return "unknown";
    }
  }
} // end class Pgdoc

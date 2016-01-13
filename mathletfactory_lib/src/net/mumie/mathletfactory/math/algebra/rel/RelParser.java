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

package net.mumie.mathletfactory.math.algebra.rel;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import net.mumie.mathletfactory.math.algebra.op.SyntaxErrorException;
import net.mumie.mathletfactory.math.algebra.rel.node.AllRel;
import net.mumie.mathletfactory.math.algebra.rel.node.AndRel;
import net.mumie.mathletfactory.math.algebra.rel.node.NotRel;
import net.mumie.mathletfactory.math.algebra.rel.node.OrRel;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;
import net.mumie.mathletfactory.math.algebra.rel.node.SimpleRel;
import net.mumie.mathletfactory.math.number.MDouble;


/**
 *  This class parses the user input and constructs a Relation Tree of 
 *  {@link net.mumie.mathletfactory.algebra.rel.node.RelNode}. This is
 *  done by using the following context free grammar (in EBNF:)
 <pre>
 G(T,N,s,R)

 T:      ALL, NULL, SIMP, NOT, AND, OR, NOT
 N:      rel, cla, sub, prim 
 s:      rel
 R:
 (1) rel       ->      sub { OR sub }.
 (2) sub       ->      cla { AND cla }.
 (3) cla       ->      NOT prim | prim.
 (4) prim      ->      SIMP | ALL | NULL | '[' rel ']'.
 </pre>
 
 * @author Paehler
 * @mm.docstatus finished
 */

public class RelParser {
  
  private static Logger logger = Logger.getLogger(RelParser.class.getName());
  
  private static class Debug {
    private static void out(String str) {
      //System.out.println(str);
    }
  }
  
  private StringTokenizer st;
  private Class m_numberClass;
  
  /** Type code for logical connectives. */
  protected static final int TC_CON = 1 ;
  
  /** Type code for parentheses. */
  protected static final int TC_PAR = 2;
  
  /** Type code for a simple relation. */
  protected static final int TC_SIMP = 3;
  
  /** The string constant for AND. */ 
  protected final static String AND  = "\u0700";
  /** The string constant for OR */
  protected final static String OR  = "\u0701";
  /** The string constant for NOT*/
  protected final static String NOT  = "\u0702";
    
  /** Whether a token has been consumed by expr, term ... etc. */
  protected boolean m_consumed = true;
  
  /** The root node of the Operational Tree. */
  protected RelNode m_root;
  
  /** The register for the current token values. */
  protected String m_token;
  
  /** The register for the current type code. */
  protected int m_typeCode;
  
  /** 
   * Returns the relation specified by the given number class, the string 
   * representation and the value of the normalization flag.
   */
  public static Relation getRelation(Class entryClass, String relStr, int normalize){
    return new Relation(getRelationTree(entryClass, relStr), normalize);
  }
  
  /** 
   * Returns the relation tree specified by the given number class, the string 
   * representation and the value of the normalization flag.
   */
  public static RelNode getRelationTree(Class numberClass, String relStr){
    RelParser parse = null;
    if(relStr.equals(""))
      return new AllRel(numberClass);
    
    try {
      parse = new RelParser(numberClass, relStr);
    } catch(SyntaxErrorException e){
      //e.printStackTrace();
      return null;
    }
    RelNode rootNode = parse.getRelationTree(); 
    return rootNode;
  }
  
  /**
   *  By constructing the object, <code>expr</code> is parsed and the
   *  Operation Tree is constructed. It then can be extracted by
   *  {@link #getRelationTree}.
   */
  public RelParser(Class numberClass, String relStr) throws SyntaxErrorException {
    
    m_numberClass = numberClass;
    relStr = preparse(relStr);
    st = new StringTokenizer(relStr, "[]"+AND+OR+NOT,true);
    
    // recursively construct Operation Tree
    m_root = rel();
    if( logger.isLoggable(java.util.logging.Level.FINE) )
      logger.fine("relStr is "+m_root.toString());
  }
  
  /**
   *  Lower cases input and replaces functions by their corresponding constants.
   */
  protected String preparse(String relStr){
    relStr = relStr.replaceAll("\\s*",""); // remove whitespace
    relStr = relStr.toLowerCase();
    relStr = relStr.replaceAll("or", OR);
    relStr = relStr.replaceAll("not", NOT);
    relStr = relStr.replaceAll("and", AND);
    relStr = relStr.replaceAll("([^<>="+OR+AND+NOT+"]+)([<>=]+)([^<>="+OR+AND+NOT+"]+)([<>=]+)([^<>="+OR+AND+NOT+"]+)", "$1 $2 $3 "+AND+" $3 $4 $5");


    if( logger.isLoggable(java.util.logging.Level.FINE) )
      logger.fine("preparsed to "+relStr);
    return relStr;
  }
 
  /** Returns the constructed Relation Tree by its root node. */
  public RelNode getRelationTree() {
    return m_root;
  }
  
  /** Returns the constructed Relation by its root node. */
  public Relation getRelation(){
    return new Relation(m_root, Relation.NORMALIZE_OP);
  }
  
  /**
   *  This method implements the Lexer: It produces a token and stores it in the
   *  global register <code>m_token</code>. It returns false if finished.
   */
  protected boolean getToken() throws SyntaxErrorException {
    if(!m_consumed){
      Debug.out("Token has not been consumed");
      return true;
    }
    m_consumed = false;
    
    try {
      m_token = st.nextToken();
    } catch (NoSuchElementException e) {
      return false;
    }
    
    Debug.out("token is: '" + m_token+"'");
    
    // parse a conjunction
    if(m_token.equals(AND) || m_token.equals(OR)
       || m_token.equals(NOT)){
      Debug.out("Parsing Conjunction ...");
      m_typeCode = TC_CON;
      return true;
    }
    
    if(m_token.equals("[") || m_token.equals("]")){
      m_typeCode = TC_PAR;
      return true;
    }
      
    // must be a simple relation
    m_typeCode = TC_SIMP;
    return true;
  }
  
  
  /**
   *  Implements the production
   <pre>
    rel       ->      cla { OR cla }.
   </pre>
   */
  protected RelNode rel() throws SyntaxErrorException {
    
    Debug.out("<rel>");
    
    RelNode retVal = null;
    RelNode firstClause = sub();
    List clauses = new ArrayList();
    
    while(getToken() && m_typeCode == TC_CON && m_token.equals(OR)) {
      String con = m_token;
      m_consumed = true;
      
      Debug.out("OR conjunction ...");
      
      // have we already performed an or conjunction in the while-loop? If no, insert
      // the first clause into the clause list
      if(clauses.size() == 0)
        clauses.add(firstClause);
      
      RelNode newClause = sub();
      // take care of subtraction
      
      clauses.add(newClause);
      // connect the children in the Operation Tree to the return Value
    }
    
    // if there is no addition or subtraction, simply return the first Term
    if(clauses.size() == 0)
      retVal = firstClause;
    else
      retVal = new OrRel((RelNode[])clauses.toArray(new RelNode[clauses.size()]));
    Debug.out("leaving <rel>");
    return retVal;
  }
  
  /**
   *  Implements the production
   <pre>
    sub       ->      cla { AND cla }.
   </pre>
   */
  protected RelNode sub() throws SyntaxErrorException {
    
    Debug.out("<sub>");
    
    RelNode retVal = null;
    RelNode firstSub = cla();
    List subs = new ArrayList();
    
    while(getToken() && m_typeCode == TC_CON && m_token.equals(AND)) {

      m_consumed = true;
      
      Debug.out("AND conjunction ...");
      
      // have we already performed an AND conjunction in the while-loop? If no, insert
      // the first subclause into the subs list
      if(subs.size() == 0)
        subs.add(firstSub);
      
      RelNode newSub = cla();
      subs.add(newSub);
      // connect the children in the Relation Tree to the return value
    }
    
    // if there is no multiplication or division, simply return the first fact
    if(subs.size() == 0)
      retVal = firstSub;
    else
      retVal = new AndRel((RelNode[])subs.toArray(new RelNode[subs.size()]));
    Debug.out("leaving <sub>");
    return retVal;
  }
  
  /**
   *  Implements the production
   <pre>
    (3) cla    ->    NOT prim | prim.
   </pre>
   */
  protected RelNode cla() throws SyntaxErrorException {
    Debug.out("<cla>");
    // check for a NOT
    if(getToken() && m_typeCode == TC_CON && m_token.equals(NOT)){
      Debug.out("setting NOT ...");
      m_consumed = true;
      Debug.out("leaving <cla>");
      return new NotRel(prim());
    }
    Debug.out("leaving <cla>");
    return prim();
  }
  
  /**
   *  Implements the production
   <pre>
    prim      ->      SIMP | '[' rel ']'.
   </pre>
   */
  protected RelNode prim() throws SyntaxErrorException{
    RelNode retVal = null;
    Debug.out("<prim>");
    
    // check for simple relation
    if(m_typeCode == TC_SIMP){
      Debug.out("SimpleRelation "+m_token);
      m_consumed = true;
      
      retVal = new SimpleRel(m_numberClass, m_token, Relation.NORMALIZE_NONE); 
      Debug.out("leaving <prim>");
      return retVal;
    }
    
    if(m_typeCode == TC_PAR && m_token.equals("[")) {
      m_consumed = true;
      retVal = rel();
      
      if(!getToken() || m_typeCode != TC_PAR || !m_token.equals("]"))
        throw new SyntaxErrorException();
      else
        m_consumed = true;
      Debug.out("leaving <prim>");
      return retVal;
    }
    
    if( logger.isLoggable(java.util.logging.Level.SEVERE) )
      logger.severe("leaving <prim> unexpectedly!!!!!");
    return retVal;
    
  }
  
  /** For testing purposes */
  public static void main(String[] args) {
    Logger.getLogger("").setLevel(java.util.logging.Level.FINE);

    try {
      RelParser e = new RelParser(MDouble.class, "3<=x<=x^2 AND 0<x<1");
      RelNode res = e.getRelationTree();
      System.out.println(res.toString());
      //System.out.println("Result is: "+res.getResult());
    } catch (Exception e) { e.printStackTrace(); }
    
  }
  
}


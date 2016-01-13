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

package net.mumie.mathletfactory.math.algebra.test;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.set.Interval;

/**  
 *  This class contains tests for the functionality of 
 *  {@link net.mumie.mathletfactory.algebra.rel.Relation}.
 *  @see  <a href="http://junit.sourceforge.net">JUnit</a>
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class RelationTest extends TestCase {


static {
  ConsoleHandler consoleHandler = new ConsoleHandler();
  consoleHandler.setLevel(java.util.logging.Level.OFF);
  Logger.getLogger("").addHandler(consoleHandler);
  Logger.getLogger("").setLevel(java.util.logging.Level.FINE);
}

	String[] eqn = new String[4];
	String[] solution = new String[4];
	
  public RelationTest(String name){
		super(name);
	}
  
	public void setUp(){
		eqn[0]  = " x+y != 2*y ";
		solution[0] = "x!=y";
		eqn[1]  = " x+2*y^2 = y^2";
		solution[1] = "x=-y^2";
		eqn[2]  = " x + 2*y^3 + 3*y^2 = y^3 + y^3 + y^2 +2*y^2";
		solution[2] = "x=0";
    eqn[3]  = " x*y != 2*y ";
    solution[3] = "x!=2";
	  }

  /**
   * Tests 
   *  {@link net.mumie.mathletfactory.algebra.rel.node.RelNode#separateFor}.
   */	  
	public void testSolve(){
	  for(int i=0;i<eqn.length;i++){
	    //System.out.println("solving "+eqn[i]);
	    Relation eq = new Relation(MDouble.class, eqn[i], Relation.NORMALIZE_OP);
	    eq.separateFor("x");
	    //System.out.println("solution is "+eq);
	    Assert.assertEquals(solution[i], eq.toString());
	  }
	}
	
  /**
   * Tests the creation of complex relations.
   */
	public void testCreate(){
	  Relation rel = new Relation(MDouble.class, "x+y > 0", Relation.NORMALIZE_OP);
	  //System.out.println(rel);
	  rel.addAndComposite("x^2>0");
	  rel.addOrComposite("x!=0");
	  //System.out.println(rel);
	  Assert.assertEquals(rel.toString(),"[x+y>0 AND x^2>0] OR x!=0");
	}
	
  /**
   * Tests 
   *  {@link net.mumie.mathletfactory.algebra.rel.Relation#normalize}.
   */

  public void testNormalize(){
    Relation rel = new Relation(MDouble.class, "x+y=0 AND [x=0 AND x!=1]", Relation.NORMALIZE_OP);
    Assert.assertEquals("x+y=0 AND x=0 AND x!=1", rel.toString());
    
    rel = new Relation(MDouble.class, "x+y=0 AND [x=0 OR x=1]", Relation.NORMALIZE_OP);
    
    Assert.assertEquals("[x+y=0 AND x=0] OR [x+y=0 AND x=1]", rel.toString());
    
    //System.out.println(rel); 
  }

  /**
   * Tests 
   *  {@link net.mumie.mathletfactory.algebra.rel.Relation#getTrueForSet}.
   */	
  public void testGetTrueForSet(){
    double eps = 1e-4;
    Interval interval = new Interval(MDouble.class, "(-3;3)");
    Relation rel = new Operation(MDouble.class, "tan(x)",true).getDefinedRelation();
    rel.setNormalForm(Relation.NORMALIZE_OP_SEPARATED);
    //System.out.println(rel+" is true for "+rel.getTrueForSet(eps, interval));
    rel = new Operation(MDouble.class, "1/sin(2*x+2*pi)",true).getDefinedRelation();
    rel.setNormalForm(Relation.NORMALIZE_OP_SEPARATED);
    //System.out.println(rel+" is true for "+rel.getTrueForSet(eps, interval));
    rel = new Relation(MDouble.class, "3x+3 = 3", Relation.NORMALIZE_OP_SEPARATED);
    //System.out.println(rel+" is false for "+rel.getFalseForSet(eps, interval));
    Assert.assertEquals(rel.getFalseForSet(eps, interval).toString(),"{(-Infinity;0), (0;Infinity)}");
    rel = new Relation(MDouble.class, "2x+3 > 4", Relation.NORMALIZE_OP_SEPARATED);
    //System.out.println(rel+" is false for "+rel.getFalseForSet(eps, interval));
    Assert.assertEquals(rel.getTrueForSet(eps, interval).toString(),"{(0.5;Infinity)}");
  }
  
  
  /** Running the test case. */  
	public static void main(String[] args) { 
		RelationTest test = new RelationTest("test");
    test.setUp();
		test.testGetTrueForSet();
	}
}

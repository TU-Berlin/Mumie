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

package net.mumie.mathletfactory.math.algebra.op;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import net.mumie.mathletfactory.math.algebra.op.node.AbsOp;
import net.mumie.mathletfactory.math.algebra.op.node.AcosOp;
import net.mumie.mathletfactory.math.algebra.op.node.AcoshOp;
import net.mumie.mathletfactory.math.algebra.op.node.AcotOp;
import net.mumie.mathletfactory.math.algebra.op.node.AcothOp;
import net.mumie.mathletfactory.math.algebra.op.node.AddOp;
import net.mumie.mathletfactory.math.algebra.op.node.AsinOp;
import net.mumie.mathletfactory.math.algebra.op.node.AsinhOp;
import net.mumie.mathletfactory.math.algebra.op.node.AtanOp;
import net.mumie.mathletfactory.math.algebra.op.node.AtanhOp;
import net.mumie.mathletfactory.math.algebra.op.node.ConjOp;
import net.mumie.mathletfactory.math.algebra.op.node.CosOp;
import net.mumie.mathletfactory.math.algebra.op.node.CoshOp;
import net.mumie.mathletfactory.math.algebra.op.node.CotOp;
import net.mumie.mathletfactory.math.algebra.op.node.CothOp;
import net.mumie.mathletfactory.math.algebra.op.node.ExpOp;
import net.mumie.mathletfactory.math.algebra.op.node.FacOp;
import net.mumie.mathletfactory.math.algebra.op.node.FloorOp;
import net.mumie.mathletfactory.math.algebra.op.node.ImOp;
import net.mumie.mathletfactory.math.algebra.op.node.LnOp;
import net.mumie.mathletfactory.math.algebra.op.node.MultOp;
import net.mumie.mathletfactory.math.algebra.op.node.NrtOp;
import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.algebra.op.node.OpNode;
import net.mumie.mathletfactory.math.algebra.op.node.PiecewiseConstOp;
import net.mumie.mathletfactory.math.algebra.op.node.PowerOp;
import net.mumie.mathletfactory.math.algebra.op.node.ReOp;
import net.mumie.mathletfactory.math.algebra.op.node.SinOp;
import net.mumie.mathletfactory.math.algebra.op.node.SinhOp;
import net.mumie.mathletfactory.math.algebra.op.node.TanOp;
import net.mumie.mathletfactory.math.algebra.op.node.TanhOp;
import net.mumie.mathletfactory.math.algebra.op.node.VariableOp;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;


/**
 * This class parses the user input and constructs the Operation Tree of
 * {@link net.mumie.mathletfactory.algebra.op.node.OpNode}. This is done by
 * using the following context free grammar (in EBNF:)
 * 
 * <pre>
 *                                                           G(T,N,s,R)
 *                                                          
 *                                                           T:      NUM, FUN, '!', '+', '-', '*', '/', '&circ;', '#', '(', ')', '|'
 *                                                           N:      expr, term, fac, pot, prim
 *                                                           s:      expr
 *                                                           R:
 *                                                           (1) expr      -&gt;      term { '+' term } | term { '-' term } .
 *                                                           (2) term      -&gt;      fact { '*' fact } | fact { '/' fact } .
 *                                                           (3) fact      -&gt;      FUN pot | pot '!' | pot .
 *                                                           (4) pot       -&gt;      prim {'&circ;' pot} | prim {'#' pot} .
 *                                                           (5) prim      -&gt;      VAR | NUM | '(' expr ')' | '|' expr '|' .
 * </pre>
 * 
 * @author Paehler
 * @mm.docstatus finished
 */

public class OpParser implements CharSetIF {

	private static Logger logger = Logger.getLogger( OpParser.class.getName() );

	private static class Debug {

		private static void out( String str ) {
		// System.out.println(str);
		}
	}

	private StringTokenizer st;

	/** The expression to be parsed. */
	protected String m_expression;

	/** The number class used. */
	protected Class m_numberClass;

	/** Whether a token has been consumed by expr, term ... etc. */
	protected boolean m_consumed = true;

	/** The root node of the Operational Tree */
	protected OpNode m_root;

	/** The global register for the current token values. */
	protected String m_token;

	// the typecode describes the type of the current token
	/** Type code for variables */
	protected static final int TC_VAR = 1;
	/** Type code for numerals */
	protected static final int TC_NUM = 2;
	/** Type code for operation symbols (+,-,*,/,^) */
	protected static final int TC_SYM = 3;
	/** Type code for functions (sin, cos, exp,...) */
	protected static final int TC_FUN = 4;

	/** The global register for the type code. */
	protected int m_typeCode;

	// identifiers for functions and constants

	/** The string value for sin. */
	protected final static String SIN = "\u0600";
	/** The string value for cos. */
	protected final static String COS = "\u0601";
	/** The string value for tan. */
	protected final static String TAN = "\u0602";
	/** The string value for cot. */
	protected final static String COT = "\u0603";

	/** The string value for sinh. */
	protected final static String SINH = "\u0606";
	/** The string value for cosh. */
	protected final static String COSH = "\u0607";
	/** The string value for tanh. */
	protected final static String TANH = "\u0608";
	/** The string value for coth. */
	protected final static String COTH = "\u0609";

	/** The string value for asin. */
	protected final static String ASIN = "\u060a";
	/** The string value for acos. */
	protected final static String ACOS = "\u060b";
	/** The string value for atan. */
	protected final static String ATAN = "\u060c";
	/** The string value for acot. */
	protected final static String ACOT = "\u0612";

	/** The string value for asin. */
	protected final static String ASINH = "\u060f";
	/** The string value for acos. */
	protected final static String ACOSH = "\u0610";
	/** The string value for atan. */
	protected final static String ATANH = "\u0611";
	/** The string value for acot. */
	protected final static String ACOTH = "\u060d";

	/** The string value for exp. */
	protected final static String EXP = "\u0604";
	/** The string value for ln. */
	protected final static String LN = "\u0605";

	/** The string value for sqrt. */
	protected final static String SQRT = "\u0613";

	/** The string value for abs. */
	protected final static String ABS = "\u0614";

	/** The string value for floor. */
	protected final static String FLOOR = "\u0615";

	/** The string value for faculty. */
	protected final static String FAC = "\u0616";

	/** The string value for Re(z). */
	protected final static String RE = "\u0617";
	/** The string value for Im(z). */
	protected final static String IM = "\u0618";

	/** The string value for Conj(z). */
	protected final static String CONJ = "\u0619";

	/** The string value for cbrt. */
	protected final static String CBRT = "\u061a";

	/** The string value for sign(x), the sign function. */
	protected final static String SIGN = "\u061b";

	/** The string value for NaN (not a number). */
	protected final static String NAN = "\u061c";

	protected final static String THETA_FUNC = "\u061d";

	/** The string value for a '+' inside braces */
	public final static String _PLUS = "\u0611";
	/** The string value for a '-' inside braces */
	public final static String _MINUS = "\u0612";

	/** The string value for infinity. */
	protected final static String INFINITY = "\u221e";

	/**
	 * Returns an operation for the given number class, expression string and
	 * the value of the <code>normalize</code> flag.
	 */
	public static Operation getOperation( Class numberClass, String expr, boolean normalize ) {
		if ( expr == null || expr.equals( "" ) ) expr = "0";
		OpNode newOp = getOperationTree( numberClass, expr, normalize );
		if ( newOp != null )
			return new Operation( newOp, normalize ); // jweber, corrected
		else
			return null;
	}

	/**
	 * Returns an operation node for the given number class, expression string
	 * and the value of the <code>normalize</code> flag.
	 */
	public static OpNode getOperationTree( Class numberClass, String expr, boolean normalize ) {
		OpParser parsed = null;

		try {
			parsed = new OpParser( numberClass, expr );
		} catch ( SyntaxErrorException e ) {
			System.out.println( "OpParser: Unable to parse: " + expr );
			// e.printStackTrace();
			return null;
		}
		OpNode rootNode = parsed.getOperationTree();
		if ( normalize )
			return OpTransform.normalize( rootNode );
		else
			return rootNode;
	}

	/**
	 * By constructing the object, <code>expr</code> is parsed and the
	 * Operation Tree is constructed. It then can be extracted by
	 * {@link #getOperationTree}.
	 */
	public OpParser( Class numberClass, String expr ) throws SyntaxErrorException {
		m_expression = expr;
		m_numberClass = numberClass;

		if ( !this.checkForAlphabets( expr ) ) {
			throw new SyntaxErrorException();
		}

		expr = preparse( expr );
		st = new StringTokenizer( expr, "!^#+-*/()|\t" + SIN + COS + TAN + COT + SINH + COSH + TANH + COTH + ASIN + ACOS + ATAN + ACOT + ASINH + ACOSH + ATANH + ACOTH + EXP + LN + SQRT + ABS + FLOOR
				+ FAC + RE + IM + CONJ + CBRT + SIGN + THETA_FUNC, true );

		// recursively construct Operation Tree
		m_root = expr();
		if ( logger.isLoggable( java.util.logging.Level.FINE ) ) logger.fine( "expr is " + m_root.toDebugString() );
	}

	public boolean checkForAlphabets( String expr ) {
		char currentChar;
		for ( int i = 0; i < expr.length(); i++ ) {
			currentChar = expr.charAt( i );
			if ( FULL_CHAR_SET.indexOf( currentChar ) == -1 ) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Lower cases input and replaces functions by their corresponding
	 * constants.
	 */
	protected String preparse( String expr ) {
		expr = replaceAll( expr, "\\s*", "" ); // remove whitespace
		expr = replaceAll( expr, ",", "." );

		expr = replaceAll( expr, "infinity", INFINITY );
		expr = replaceAll( expr, "unendlich", INFINITY );

		expr = expr.replaceAll( "EPSILON", EPSILON_UPPER );
		expr = replaceAll( expr, "epsilon", EPSILON_LOWER );

		expr = expr.replaceAll( "OMICRON", OMICRON_UPPER );
		expr = replaceAll( expr, "omicron", OMICRON_LOWER );

		expr = expr.replaceAll( "UPSILON", UPSILON_UPPER );
		expr = replaceAll( expr, "upsilon", UPSILON_LOWER );

		expr = expr.replaceAll( "LAMBDA", LAMBDA_UPPER );
		expr = replaceAll( expr, "lambda", LAMBDA_LOWER );

		expr = expr.replaceAll( "ALPHA", ALPHA_UPPER );
		expr = replaceAll( expr, "alpha", ALPHA_LOWER );

		expr = expr.replaceAll( "delta", DELTA_UPPER );
		expr = replaceAll( expr, "delta", DELTA_LOWER );

		expr = expr.replaceAll( "GAMMA", GAMMA_UPPER );
		expr = replaceAll( expr, "gamma", GAMMA_LOWER );

		expr = expr.replaceAll( "THETA", THETA_UPPER );
		expr = replaceAll( expr, "theta", THETA_LOWER );

		expr = expr.replaceAll( "KAPPA", KAPPA_UPPER );
		expr = replaceAll( expr, "kappa", KAPPA_LOWER );

		expr = expr.replaceAll( "SIGMA", SIGMA_UPPER );
		expr = replaceAll( expr, "sigma", SIGMA_LOWER );

		expr = expr.replaceAll( "OMEGA", OMEGA_UPPER );
		expr = replaceAll( expr, "omega", OMEGA_LOWER );

		expr = expr.replaceAll( "BETA", BETA_UPPER );
		expr = replaceAll( expr, "beta", BETA_LOWER );

		expr = expr.replaceAll( "ZETA", ZETA_UPPER );
		expr = replaceAll( expr, "zeta", ZETA_LOWER );

		expr = expr.replaceAll( "IOTA", IOTA_UPPER );
		expr = replaceAll( expr, "iota", IOTA_LOWER );

		expr = expr.replaceAll( "EPS", EPSILON_UPPER );
		expr = replaceAll( expr, "eps", EPSILON_LOWER );

		expr = expr.replaceAll( "ETA", ETA_UPPER );
		expr = replaceAll( expr, "eta", ETA_LOWER );

		expr = expr.replaceAll( "RHO", RHO_UPPER );
		expr = replaceAll( expr, "rho", RHO_LOWER );

		expr = expr.replaceAll( "TAU", TAU_UPPER );
		expr = replaceAll( expr, "tau", TAU_LOWER );

		expr = expr.replaceAll( "PHI", PHI_UPPER );
		expr = replaceAll( expr, "phi", PHI_LOWER );

		expr = expr.replaceAll( "CHI", CHI_UPPER );
		expr = replaceAll( expr, "chi", CHI_LOWER );

		expr = expr.replaceAll( "PSI", PSI_UPPER );
		expr = replaceAll( expr, "psi", PSI_LOWER );

		expr = expr.replaceAll( "PI", PI_UPPER );
		expr = replaceAll( expr, "pi", PI_LOWER );

		expr = expr.replaceAll( "MU", MU_UPPER );
		expr = replaceAll( expr, "mu", MU_LOWER );

		expr = expr.replaceAll( "NU", NU_UPPER );
		expr = replaceAll( expr, "nu", NU_LOWER );

		expr = expr.replaceAll( "XI", XI_UPPER );
		expr = replaceAll( expr, "xi", XI_LOWER );

		expr = replaceAll( expr, "arcsin", ASIN );
		expr = replaceAll( expr, "arccos", ACOS );
		expr = replaceAll( expr, "arctan", ATAN );
		expr = replaceAll( expr, "arccot", ACOT );

		expr = replaceAll( expr, "arsinh", ASINH );
		expr = replaceAll( expr, "arcosh", ACOSH );
		expr = replaceAll( expr, "artanh", ATANH );
		expr = replaceAll( expr, "arcoth", ACOTH );

		expr = replaceAll( expr, "asinh", ASINH );
		expr = replaceAll( expr, "acosh", ACOSH );
		expr = replaceAll( expr, "atanh", ATANH );
		expr = replaceAll( expr, "acoth", ACOTH );

		expr = replaceAll( expr, "asin", ASIN );
		expr = replaceAll( expr, "acos", ACOS );
		expr = replaceAll( expr, "atan", ATAN );
		expr = replaceAll( expr, "acot", ACOT );

		expr = replaceAll( expr, "sinh", SINH );
		expr = replaceAll( expr, "cosh", COSH );
		expr = replaceAll( expr, "tanh", TANH );
		expr = replaceAll( expr, "coth", COTH );

		expr = replaceAll( expr, "sin", SIN );
		expr = replaceAll( expr, "cos", COS );
		expr = replaceAll( expr, "tan", TAN );
		expr = replaceAll( expr, "cot", COT );

		expr = replaceAll( expr, "exp", EXP );

		expr = replaceAll( expr, "log", LN );
		expr = replaceAll( expr, "ln", LN );

		expr = replaceAll( expr, "sqrt", SQRT );

		expr = replaceAll( expr, "abs", ABS );

		expr = replaceAll( expr, "floor", FLOOR );
		expr = replaceAll( expr, "fl", FLOOR );

		expr = replaceAll( expr, "fac", FAC );

		expr = replaceAll( expr, "im", IM );
		expr = replaceAll( expr, "re", RE );

		expr = replaceAll( expr, "conj", CONJ );

		expr = replaceAll( expr, "cbrt", CBRT );

		expr = replaceAll( expr, "sgn", SIGN );
		expr = replaceAll( expr, "sign", SIGN );

		expr = replaceAll( expr, "nan", NAN );

		expr = replaceAll( expr, "\u00B2", "^2" );
		expr = replaceAll( expr, "\u00B3", "^3" );

		expr = replaceAll( expr, "(^[\\+]+)(.)", "$2" );
		expr = replaceAll( expr, "([(])([\\+]+)", "(" );
		expr = replaceAll( expr, "([" + SIN + COS + TAN + COT + SINH + COSH + TANH + COTH + ASIN + ACOS + ATAN + ACOT + ASINH + ACOSH + ATANH + ACOTH + EXP + LN + SQRT + ABS + FLOOR + FAC + RE + IM
				+ CONJ + CBRT + SIGN + THETA_FUNC + "])([\\+]+)", "$1" );

		// [..]^(a/b) -> [..]^a#b
//		expr = replaceAll( expr, "([^\\^]*)\\^\\(([^)]*)/([^)]*)\\)", "(($1)^($2))#($3)" );

		// mask '+' and '-' inside braces (used for indexing e.g. in recursive
		// sequences)
		expr = replaceAll( expr, "(\\{[^\\+])*\\+([^\\}]*\\})", "$1" + _PLUS + "$2" );
		expr = replaceAll( expr, "(\\{[^\\-])*\\-([^\\}]*\\})", "$1" + _MINUS + "$2" );

		expr = replaceAll( expr, THETA_LOWER + "\\(", THETA_FUNC + "\\(" );

		expr = replaceAll( expr, "\\)\\(", ")*(" );
		expr = replaceAll( expr, "([)])([" + VARIABLES_CHAR_SET + DIGITAL + SIN + COS + TAN + COT + SINH + COSH + TANH + COTH + ASIN + ACOS + ATAN + ACOT + ASINH + ACOSH + ATANH + ACOTH + EXP + LN
				+ SQRT + ABS + FLOOR + FAC + RE + IM + CONJ + CBRT + SIGN + THETA_FUNC + "])", "$1\\*$2" );

		expr = replaceAll( expr, "([" + VARIABLES_CHAR_SET + DIGITAL + "])([(])", "$1\\*$2" );

		expr = replaceAll( expr, "([" + VARIABLES_CHAR_SET + "])([" + VARIABLES_CHAR_SET + "])", "$1\\*$2" );
		expr = replaceAll( expr, "([" + VARIABLES_CHAR_SET + "])([" + VARIABLES_CHAR_SET + "])", "$1\\*$2" );
		expr = replaceAll( expr, "([" + VARIABLES_CHAR_SET + "])([" + SIN + COS + TAN + COT + SINH + COSH + TANH + COTH + ASIN + ACOS + ATAN + ACOT + ASINH + ACOSH + ATANH + ACOTH + EXP + LN + SQRT
				+ ABS + FLOOR + FAC + RE + IM + CONJ + CBRT + SIGN + THETA_FUNC + "])", "$1\\*$2" );

		expr = replaceAll( expr, "([0-9.])([" + VARIABLES_CHAR_SET + SIN + COS + TAN + COT + SINH + COSH + TANH + COTH + ASIN + ACOS + ATAN + ACOT + ASINH + ACOSH + ATANH + ACOTH + EXP + LN + SQRT
				+ ABS + FLOOR + FAC + RE + IM + CONJ + CBRT + SIGN + THETA_FUNC + "])", "$1\\*$2" );

		if ( logger.isLoggable( java.util.logging.Level.FINE ) ) logger.fine( "preparsed to " + expr );
		return expr;
	}

	private String replaceAll( String expr, String regex, String replacement ) {
		expr = Pattern.compile( regex, Pattern.CASE_INSENSITIVE ).matcher( expr ).replaceAll( replacement );
		return expr;
	}

	/** Returns the constructed Operation Tree by its root node. */
	public OpNode getOperationTree() {
		return m_root;
	}

	/** Returns the constructed Operation. */
	public Operation getOperation() {
		return new Operation( m_root );
	}

	/**
	 * This method implements the Lexer: It produces a token and stores it in
	 * the global register <code>m_token</code>. It returns false if
	 * finished.
	 */
	protected boolean getToken() throws SyntaxErrorException {
		if ( !m_consumed ) {
			Debug.out( "Token has not been consumed" );
			return true;
		}
		m_consumed = false;

		try {
			do
				m_token = st.nextToken();
			while ( m_token.equals( " " ) || m_token.equals( "\t" ) );
		} catch ( NoSuchElementException e ) {
			return false;
		}

		Debug.out( "token is: '" + m_token + "'" );

		// parse infinity and NaN
		if ( m_token.equals( INFINITY ) || m_token.equals( NAN ) ) {
			m_typeCode = TC_NUM;
			return true;
		}

		// parse a numeral
		if ( Character.isDigit( m_token.charAt( 0 ) ) ) {
			Debug.out( "Parsing Numeral ..." );

			int i;
			for ( i = 0; i < m_token.length() && ( Character.isDigit( m_token.charAt( i ) ) || m_token.charAt( i ) == '.' ); i++ );

			if ( i != m_token.length() ) throw new SyntaxErrorException();
			m_typeCode = TC_NUM;
			return true;
		}

		// parse a variable or constant
		if ( VARIABLES_CHAR_SET.indexOf( m_token.toCharArray()[0] ) >= 0 || m_token.equals( OpNode.REPLACEMENT_IDENTIFIER ) ) {
			Debug.out( "Parsing Variable ..." );
			m_typeCode = TC_VAR;
			return true;
		}

		// parse a function
		if ( m_token.equals( SIN ) || m_token.equals( COS ) || m_token.equals( TAN ) || m_token.equals( COT ) || m_token.equals( SINH ) || m_token.equals( COSH ) || m_token.equals( TANH )
				|| m_token.equals( COTH ) || m_token.equals( ASIN ) || m_token.equals( ACOS ) || m_token.equals( ATAN ) || m_token.equals( ACOT ) || m_token.equals( ASINH ) || m_token.equals( ACOSH )
				|| m_token.equals( ATANH ) || m_token.equals( ACOTH ) || m_token.equals( EXP ) || m_token.equals( LN ) || m_token.equals( SQRT ) || m_token.equals( ABS ) || m_token.equals( FLOOR )
				|| m_token.equals( FAC ) || m_token.equals( IM ) || m_token.equals( RE ) || m_token.equals( CONJ ) || m_token.equals( CBRT ) || m_token.equals( SIGN ) || m_token.equals( THETA_FUNC ) ) {
			Debug.out( "Parsing Function ..." );
			m_typeCode = TC_FUN;
			return true;
		}

		// must be a symbol
		m_typeCode = TC_SYM;
		return true;
	}

	/**
	 * Implements the production
	 * 
	 * <pre>
	 *                                                          	 expr  -&gt;  term { '+' term } | term { '-' term } .
	 *                                                          	
	 * </pre>
	 */
	protected OpNode expr() throws SyntaxErrorException {

		Debug.out( "<expr>" );

		OpNode retVal = null;
		OpNode firstTerm = term();
		List terms = new ArrayList();

		while ( getToken() && m_typeCode == TC_SYM && ( m_token.equals( "+" ) || m_token.equals( "-" ) ) ) {
			String op = m_token;
			m_consumed = true;

			Debug.out( "Addition ..." );

			// have we already performed an addition in the while-loop? If no,
			// insert
			// the first term into the term list
			if ( terms.size() == 0 ) terms.add( firstTerm );

			OpNode newTerm = term();
			// take care of subtraction
			if ( op.equals( "-" ) ) newTerm.negateFactor();

			terms.add( newTerm );
			// connect the children in the Operation Tree to the return Value
		}

		// if there is no addition or subtraction, simply return the first Term
		if ( terms.size() == 0 )
			retVal = firstTerm;
		else
			retVal = new AddOp( ( OpNode[] ) terms.toArray( new OpNode[terms.size()] ) );
		Debug.out( "leaving <expr>" );
		return retVal;
	}

	/**
	 * Implements the production
	 * 
	 * <pre>
	 *                                                          	 term      -&gt;      fact { '*' fact } | fact { '/' fact } .
	 *                                                          	
	 * </pre>
	 */
	protected OpNode term() throws SyntaxErrorException {

		Debug.out( "<term>" );

		OpNode retVal = null;
		OpNode firstFact = fact();
		List facts = new ArrayList();

		while ( getToken() && m_typeCode == TC_SYM && ( m_token.equals( "*" ) || m_token.equals( "/" ) ) ) {
			String op = m_token;
			m_consumed = true;

			Debug.out( "Multiplication ..." );

			// have we already performed an multiplication in the while-loop? If
			// no, insert
			// the first fact into the fact list
			if ( facts.size() == 0 ) facts.add( firstFact );

			OpNode newFact = fact();
			// take care of subtraction
			if ( op.equals( "/" ) ) newFact.negateExponent();

			facts.add( newFact );
			// connect the children in the Operation Tree to the return Value
		}

		// if there is no multiplication or division, simply return the first
		// fact
		if ( facts.size() == 0 )
			retVal = firstFact;
		else
			retVal = new MultOp( ( OpNode[] ) facts.toArray( new OpNode[facts.size()] ) );
		Debug.out( "leaving <expr>" );
		return retVal;
	}

	/**
	 * Implements the production
	 * 
	 * <pre>
	 *                                                          	 fact      -&gt;      FUN pot | pot '!' | pot.
	 *                                                          	
	 * </pre>
	 */
	protected OpNode fact() throws SyntaxErrorException {
		OpNode retVal = null;
		Debug.out( "<fact>" );
		if ( getToken() && m_typeCode == TC_FUN ) {
			m_consumed = true;

			// all the functions need only 1 argument and therefore
			// differ only in Calculation (i.e. which BaseOp to choose)
			if ( m_token.equals( SIN ) ) retVal = new SinOp( m_numberClass );
			if ( m_token.equals( COS ) ) retVal = new CosOp( m_numberClass );
			if ( m_token.equals( TAN ) ) retVal = new TanOp( m_numberClass );
			if ( m_token.equals( COT ) ) retVal = new CotOp( m_numberClass );

			if ( m_token.equals( SINH ) ) retVal = new SinhOp( m_numberClass );
			if ( m_token.equals( COSH ) ) retVal = new CoshOp( m_numberClass );
			if ( m_token.equals( TANH ) ) retVal = new TanhOp( m_numberClass );
			if ( m_token.equals( COTH ) ) retVal = new CothOp( m_numberClass );

			if ( m_token.equals( ASIN ) ) retVal = new AsinOp( m_numberClass );
			if ( m_token.equals( ACOS ) ) retVal = new AcosOp( m_numberClass );
			if ( m_token.equals( ATAN ) ) retVal = new AtanOp( m_numberClass );
			if ( m_token.equals( ACOT ) ) retVal = new AcotOp( m_numberClass );

			if ( m_token.equals( ASINH ) ) retVal = new AsinhOp( m_numberClass );
			if ( m_token.equals( ACOSH ) ) retVal = new AcoshOp( m_numberClass );
			if ( m_token.equals( ATANH ) ) retVal = new AtanhOp( m_numberClass );
			if ( m_token.equals( ACOTH ) ) retVal = new AcothOp( m_numberClass );

			if ( m_token.equals( EXP ) ) retVal = new ExpOp( m_numberClass );
			if ( m_token.equals( LN ) ) retVal = new LnOp( m_numberClass );
			if ( m_token.equals( SQRT ) ) retVal = new NrtOp( m_numberClass );
			if ( m_token.equals( ABS ) ) retVal = new AbsOp( m_numberClass );
			if ( m_token.equals( FLOOR ) ) retVal = new FloorOp( m_numberClass );
			if ( m_token.equals( FAC ) ) retVal = new FacOp( m_numberClass );
			if ( m_token.equals( IM ) ) retVal = new ImOp( m_numberClass );
			if ( m_token.equals( RE ) ) retVal = new ReOp( m_numberClass );
			if ( m_token.equals( CONJ ) ) retVal = new ConjOp( m_numberClass );
			if ( m_token.equals( CBRT ) ) retVal = new NrtOp( m_numberClass, 3 );
			if ( m_token.equals( SIGN ) ) retVal = new PiecewiseConstOp( m_numberClass, new double[] { 0 }, new double[] { -1, 1 }, "sign" );
			if ( m_token.equals( THETA_FUNC ) ) retVal = new PiecewiseConstOp( m_numberClass, new double[] { 0 }, new double[] { 0, 1 }, "theta" );

			// check for a following '(', because we wish to interpret sin(x)^2
			// as (sin(x))^2 not as sin x^2, which the grammar suggests
			if ( getToken() && m_typeCode == TC_SYM && m_token.equals( "(" ) ) {
				m_consumed = true;
				retVal.setChildren( new OpNode[] { expr() } );
				if ( !getToken() || m_typeCode != TC_SYM || !m_token.equals( ")" ) ) throw new SyntaxErrorException();
				m_consumed = true;
				// check for additional exponent after the closing parenthesis
				if ( getToken() && m_token.equals( "^" ) ) { // wrap the
					// function node
					// in a power
					// node
					m_consumed = true;
					retVal = new PowerOp( retVal, prim() );
				}
			} else {// argument without parentheses
				retVal.setChildren( new OpNode[] { pot() } );
			}
		} else
			retVal = pot();

		// handle postfix "!" operator
		if ( getToken() && m_token.equals( "!" ) ) {
			m_consumed = true;
			FacOp fac = new FacOp( m_numberClass );
			fac.setChildren( new OpNode[] { retVal } );
			retVal = fac;
		}
		Debug.out( "leaving <fact>" );
		return retVal;
	}

	/**
	 * Implements the production
	 * 
	 * <pre>
	 *                                                          	 pot       -&gt;      prim {'&circ;' pot} | prim {'#' pot}.
	 *                                                          	
	 * </pre>
	 */
	protected OpNode pot() throws SyntaxErrorException {
		Debug.out( "<pot>" );

		OpNode retVal = null;
		OpNode potnode = prim();

		if ( getToken() && m_typeCode == TC_SYM && m_token.equals( "^" ) ) {
			m_consumed = true;
			Debug.out( "Power ..." );

			retVal = new PowerOp( m_numberClass );
			OpNode potnode2 = pot();
			retVal.setChildren( new OpNode[] { potnode, potnode2 } );
		} else if ( getToken() && m_typeCode == TC_SYM && m_token.equals( "#" ) ) {
			m_consumed = true;
			Debug.out( "Nth root ..." );

			OpNode n = pot();
			if ( n.getContainedVariables().length != 0 ) {
				// node n contains variables, so make it a potnode^(1/n)
				// operation:
				MultOp one_div_n = new MultOp( m_numberClass );
				n.setExponent( -1 );
				one_div_n.setChildren( new OpNode[] { new NumberOp( m_numberClass, 1 ), n } );
				retVal = new PowerOp( potnode, one_div_n );
			} else { // simply take the n-th root operation
				retVal = new NrtOp( m_numberClass );
				retVal.setChildren( new OpNode[] { potnode } );
				( ( NrtOp ) retVal ).setN( ( int ) n.getResultDouble() );
			}
		} else
			// pot -> prim:
			retVal = potnode;
		if ( retVal == null ) throw new SyntaxErrorException();

		Debug.out( "leaving <pot>" );
		return retVal;

	}

	/**
	 * Implements the production
	 * 
	 * <pre>
	 *                                                          	 prim      -&gt;      VAR | NUM | '(' expr ')' | '|' expr '|'.
	 *                                                          	
	 * </pre>
	 */
	protected OpNode prim() throws SyntaxErrorException {
		OpNode retVal = null;
		Debug.out( "<prim>" );

		// check for an unary minus
		if ( getToken() && m_typeCode == TC_SYM && m_token.equals( "-" ) ) {
			Debug.out( "setting minus ..." );
			m_consumed = true;
			retVal = fact();
			retVal.negateFactor();
			Debug.out( "leaving <prim>" );
			return retVal;
		}

		// check for numeral
		if ( m_typeCode == TC_NUM ) {
			Debug.out( "numeral is: " + m_token );
			m_consumed = true;
			MNumber numeral = NumberFactory.newInstance( m_numberClass );
			if ( m_token.equals( INFINITY ) )
				numeral.setInfinity();
			else if ( m_token.equals( NAN ) )
				numeral.setNaN();
			else
				numeral.setDouble( Double.valueOf( m_token ).doubleValue() );
			retVal = new NumberOp( numeral );
			Debug.out( "leaving <prim>" );
			return retVal;
		}

		// check for variables
		if ( m_typeCode == TC_VAR ) {
			Debug.out( "Variable " + m_token );
			m_consumed = true;
			retVal = new VariableOp( m_numberClass, m_token );
			Debug.out( "leaving <prim>" );
			return retVal;
		}

		if ( m_typeCode == TC_SYM && m_token.equals( "(" ) ) {
			m_consumed = true;
			retVal = expr();

			if ( !getToken() || m_typeCode != TC_SYM || !m_token.equals( ")" ) )
				throw new SyntaxErrorException();
			else
				m_consumed = true;
			Debug.out( "leaving <prim>" );
			return retVal;
		}

		if ( m_typeCode == TC_SYM && m_token.equals( "|" ) ) {
			m_consumed = true;
			retVal = expr();

			if ( !getToken() || m_typeCode != TC_SYM || !m_token.equals( "|" ) )
				throw new SyntaxErrorException();
			else
				m_consumed = true;
			Debug.out( "leaving <prim>" );
			return new AbsOp( retVal );
		}

		if ( logger.isLoggable( java.util.logging.Level.SEVERE ) ) logger.severe( "leaving <prim> unexpectedly!!!!!" );
		throw new SyntaxErrorException();

	}

	/** For testing purposes */
	public static void main( String[] args ) {
		try {
			OpNode res = OpParser.getOperationTree( MDouble.class, "++1", false );
			Operation op = new Operation( res, false );
			// op.normalize();
			System.out.println( op.toDebugString() );
			// System.out.println("Result is: "+res.getResult());
		} catch ( Exception e ) {
			e.printStackTrace();
		}

	}

}

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


public interface CharSetIF {

	public final static String ALPHA_UPPER = "\u0391";
	public final static String BETA_UPPER = "\u0392";
	public final static String GAMMA_UPPER = "\u0393";
	public final static String DELTA_UPPER = "\u0394";
	public final static String EPSILON_UPPER = "\u0395";
	public final static String ZETA_UPPER = "\u0396";
	public final static String ETA_UPPER = "\u0397";
	public final static String THETA_UPPER = "\u0398";
	public final static String IOTA_UPPER = "\u0399";
	public final static String KAPPA_UPPER = "\u039A";
	public final static String LAMBDA_UPPER = "\u039B";
	public final static String MU_UPPER = "\u039C";
	public final static String NU_UPPER = "\u039D";
	public final static String XI_UPPER = "\u039E";
	public final static String OMICRON_UPPER = "\u039F";
	public final static String PI_UPPER = "\u03A0";
	public final static String RHO_UPPER = "\u03A1";
	public final static String SIGMA_UPPER = "\u03A3";
	public final static String TAU_UPPER = "\u03A4";
	public final static String UPSILON_UPPER = "\u03A5";
	public final static String PHI_UPPER = "\u03A6";
	public final static String CHI_UPPER = "\u03A7";
	public final static String PSI_UPPER = "\u03A8";
	public final static String OMEGA_UPPER = "\u03A9";

	public final static String ALPHA_LOWER = "\u03B1";
	public final static String BETA_LOWER = "\u03B2";
	public final static String GAMMA_LOWER = "\u03B3";
	public final static String DELTA_LOWER = "\u03B4";
	public final static String EPSILON_LOWER = "\u03B5";
	public final static String ZETA_LOWER = "\u03B6";
	public final static String ETA_LOWER = "\u03B7";
	public final static String THETA_LOWER = "\u03B8";
	public final static String IOTA_LOWER = "\u03B9";
	public final static String KAPPA_LOWER = "\u03BA";
	public final static String LAMBDA_LOWER = "\u03BB";
	public final static String MU_LOWER = "\u03BC";
	public final static String NU_LOWER = "\u03BD";
	public final static String XI_LOWER = "\u03BE";
	public final static String OMICRON_LOWER = "\u03BF";
	public final static String PI_LOWER = "\u03C0";
	public final static String RHO_LOWER = "\u03C1";
	public final static String SIGMA_LOWER = "\u03C3";
	public final static String TAU_LOWER = "\u03C4";
	public final static String UPSILON_LOWER = "\u03C5";
	public final static String PHI_LOWER = "\u03C6";
	public final static String CHI_LOWER = "\u03C7";
	public final static String PSI_LOWER = "\u03C8";
	public final static String OMEGA_LOWER = "\u03C9";

	public final static String PI = PI_LOWER;

	public final static String GREEK_ALPHABET_LOWER = ALPHA_LOWER + BETA_LOWER + GAMMA_LOWER + DELTA_LOWER + EPSILON_LOWER + ZETA_LOWER + ETA_LOWER + THETA_LOWER + IOTA_LOWER + KAPPA_LOWER
			+ LAMBDA_LOWER + MU_LOWER + NU_LOWER + XI_LOWER + OMICRON_LOWER + PI_LOWER + SIGMA_LOWER + TAU_LOWER + UPSILON_LOWER + PHI_LOWER + CHI_LOWER + PSI_LOWER + OMEGA_LOWER;

	public final static String GREEK_ALPHABET_UPPER = ALPHA_UPPER + BETA_UPPER + GAMMA_UPPER + DELTA_UPPER + EPSILON_UPPER + ZETA_UPPER + ETA_UPPER + THETA_UPPER + IOTA_UPPER + KAPPA_UPPER
			+ LAMBDA_UPPER + MU_UPPER + NU_UPPER + XI_UPPER + OMICRON_UPPER + PI_UPPER + SIGMA_UPPER + TAU_UPPER + UPSILON_UPPER + PHI_UPPER + CHI_UPPER + PSI_UPPER + OMEGA_UPPER;

	public final static String GREEK_ALPHABET = GREEK_ALPHABET_LOWER + GREEK_ALPHABET_UPPER;

	public final static String GERMAN_AE_UPPER = "\u00C4";
	public final static String GERMAN_AE_LOWER = "\u00E4";

	public final static String GERMAN_OE_UPPER = "\u00D6";
	public final static String GERMAN_OE_LOWER = "\u00F6";

	public final static String GERMAN_UE_UPPER = "\u00DC";
	public final static String GERMAN_UE_LOWER = "\u00FC";

	public final static String GERMAN_ALPHABET_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + GERMAN_AE_UPPER + GERMAN_OE_UPPER + GERMAN_UE_UPPER;
	public final static String GERMAN_ALPHABET_LOWER = "abcdefghijklmnopqrstuvwxyz" + GERMAN_AE_LOWER + GERMAN_OE_LOWER + GERMAN_UE_LOWER;
	public final static String GERMAN_ALPHABET = GERMAN_ALPHABET_LOWER + GERMAN_ALPHABET_UPPER;

	public final static String DIGITAL = "0123456789";

	public final static String OPERATORS = "+-*/|,.!#^()[]{}";

	public final static String UNICODE = "\u00B2 + \u00B2";

	public final static String VARIABLES_CHAR_SET = GERMAN_ALPHABET + GREEK_ALPHABET;

	public final static String FULL_CHAR_SET = GERMAN_ALPHABET + GREEK_ALPHABET + DIGITAL + OPERATORS + UNICODE;
}

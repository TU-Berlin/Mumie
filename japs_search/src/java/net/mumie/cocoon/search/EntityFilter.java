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

package net.mumie.cocoon.search;

import java.io.IOException;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

/**
 * Normalizes some important entities to unicode.
 * @version <span class="file">$Id: EntityFilter.java,v 1.3 2006/11/03 10:25:19 rassy Exp $</span>
 * @author vieritz
 */
public class EntityFilter extends TokenFilter {
	
	/**
	 * @param in
	 */
	public EntityFilter(TokenStream in) {
		super(in);
	}

	/** Normalizes some entities to unicode.
	 * @see org.apache.lucene.analysis.TokenStream#next()
	 */
	public Token next() throws IOException {
		Token t = input.next();
		if (t != null) {
			t.termText().replaceAll("&Auml;","Ae");
			t.termText().replaceAll("&auml;","ae");
			t.termText().replaceAll("&Ouml;","Oe");
			t.termText().replaceAll("&ouml;","oe");
			t.termText().replaceAll("&Uuml;","Ue");
			t.termText().replaceAll("&uuml;","ue");
			t.termText().replaceAll("&szlig;","ss");
		}
		return t;
	}

}

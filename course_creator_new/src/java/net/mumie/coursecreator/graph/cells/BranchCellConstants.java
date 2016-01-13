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

package net.mumie.coursecreator.graph.cells;

import java.awt.Dimension;

/**
 * Constants for Branchcell
 * @author <a href="mailto:binder@math.tu-berlin.de>Jens Binder</a>
 * @author <a href="mailto:vrichter@math.tu-berlin.de>Verena Richter</a>
 * @version $Id: BranchCellConstants.java,v 1.3 2009/03/30 12:40:29 vrichter Exp $
 *
 */
public class BranchCellConstants{

    public static String ANDSYM = "and";
    public static String ORSYM = "or";

    /**
     * gives the category as String its:
     * 0 for ''And'' or
	 * 1 for ''Or''
     * @return
     */
    public static String getCategoryAsString(int cat){
    	if (cat==0) return ANDSYM;
    	if (cat==1) return ORSYM;
    	return "";
    	
    }
    
    /**
     * Returns the integer value of the given category
     * @param cat category given as String
     */
    public static int getCategoryAsInt(String cat) {
		if(cat.equals(ANDSYM)) return 0;
		return 1;
		
    }
    
    ///////////////////////////////////////
    // dimension
    ///////////////////////////////////////
    public static Dimension NODEDIM = new Dimension(25,25);

}


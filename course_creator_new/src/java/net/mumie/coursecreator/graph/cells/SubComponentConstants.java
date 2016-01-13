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

import java.awt.Color;
import java.awt.Dimension;

import net.mumie.coursecreator.graph.MetaInfos;

import org.apache.commons.lang.ArrayUtils;

/**
 * Defines a lot of Constants for SubComponents
 * 
 * how to add new categories?
 * 1. define a new variable public static final String CAT_newName
 * 2. add CAT_newName to categories array
 * 3. define a shortname in shorts-array at same position in categories array
 * 4. add in aligns-array an position for the new category
 * 4. check if isSubElementCategory und isSubSectionCategory will work 
 * 5. define a Color in sub_col-array
 * 
 * @author <a href="mailto:binder@math.tu-berlin.de>Jens Binder</a>
 * @author <a href="mailto:vrichter@math.tu-berlin.de>Verena Richter</a>
 * @version $Id: SubComponentConstants.java,v 1.23 2009/03/30 12:40:22 vrichter Exp $
 *
 */
public class SubComponentConstants{

	//doctypes
	public static final int DOCTYPE_SUBSECTION =1;
	public static final int DOCTYPE_SUBELEMENT =2;
	
	//categories for element
    public static final String CAT_MOTIVATION = "motivation";
    public static final String CAT_PROOF = "proof";
    public static final String CAT_VISUALIZATION = "visualization";
    public static final String CAT_DEDUCTION = "deduction";
    public static final String CAT_REMARK = "remark";
    public static final String CAT_HISTORY = "history";
    public static final String CAT_EXAMPLE = "example";
    public static final String CAT_TEST = "test";
    
    //categories for section
    public static final String CAT_PRELEARN = "prelearn";
    public static final String CAT_HOMEWORK = "homework";
    public static final String CAT_SELFTEST = "selftest";
    
    // the subsection-Category
    public static final String CAT_WORKSHEET = "worksheet";
    
    ///////////////////////////////////////////////
    // Categories
    ///////////////////////////////////////////////
    
    /**
     * Array containing the category-names of Subcomponents
     */
    public static String[] categories = { 
    				  CAT_MOTIVATION,
    				  CAT_PROOF,
					  CAT_VISUALIZATION,
					  CAT_DEDUCTION,
					  CAT_REMARK,
					  CAT_HISTORY,
					  CAT_EXAMPLE,
					  CAT_TEST,
					  
					  CAT_PRELEARN,
					  CAT_HOMEWORK,
					  CAT_SELFTEST
					  ,CAT_WORKSHEET
    };

    public static String[] shorts = {
    	 			"mot", "bew", "vis",
    	 			"her", "bem", "his",
    	 			"bsp", "tst", "prl","hom","prt"
    	 			
    	 			, "wsh" // FIXME:.. well worksheet is not a category ..
    	 			};

    /**
     * 
     * @return category as string
     * or null if category is invalid 
     */
	public static String getCategoryAsString(int cat){
		if ((cat > categories.length)||(cat<0)) return null;
		return categories[cat]; 
	}

    /**
     * Returns the integer value of the given category
     * @param cat category given as String
     */
    public static int getCategoryAsInt(String cat) {
		for(int i = 0; i < categories.length; i++) {
		    if(cat.equals(categories[i])) return i;
		}
		return -1;
    }

    
    public static int getDefaultCategory(int graphType, int align){
    	
    	if (graphType==MetaInfos.GRAPHTYPE_SECTION) 
		switch (align){
	    	case 2: return getCategoryAsInt(CAT_PRELEARN);
	    	default: return getCategoryAsInt(CAT_HOMEWORK);

		}

    	switch (align){
	    	case 0: return getCategoryAsInt(CAT_DEDUCTION);
	    	case 1: return getCategoryAsInt(CAT_PROOF);
	    	case 2: return getCategoryAsInt(CAT_MOTIVATION);
	    		
	    	default: return getCategoryAsInt(CAT_VISUALIZATION);
    	}
    }
	
///////////////////////////////////////////////
//	 Aligns
///////////////////////////////////////////////

    
    // mapping between category and position
    // positioning:   0   1
    //                  x
    //                2   3
    private static int[] aligns = {
					 2, 1, 3,
					 0, 2, 2,
					 3, 3, 2, 3, 3
					 , 0 // FIXME:.. well worksheet is not a category ..
					 };
    
	/**
	 * the Strings for align
	 */
	private static String[] alignStrings = {
		"topleft", 
		"topright", 
		"bottomleft", 
		"bottomright"}; 


    /**
     * gives this align as int defined by category
     *  0   1
     *    x
     *  2   3
     *  or -1 if category is invalid
     * @param cat
     * @return
     */
	public static int getAlignAsInt(int cat){
    	if ((cat > categories.length)||(cat<0)) return -1;
    	
    	return aligns[cat];
		
	}
	
	/**
	 * @param align as String possible: "topleft", "topright", "bottomleft", "bottomright"
	 * @return the position in the alignStrings-array or -1 if s.th. wrong
	 */
	public static int getAlignAsInt(String align){
    	for (int i=0;i<alignStrings.length;i++)
    		if (alignStrings[i].equals(align)) return i;
    	return -1;
		
	}
    
    /**
     * gives this align as String defined by category
     * @return topleft, topright, bottomleft or bottomright    
     * or null if category is invalid
     */
	public static String getAlignAsString(int cat){
		if ((cat > categories.length)||(cat<0)) return null;
		return alignStrings[getAlignAsInt(cat)]; 
	}
	
	
    
	public boolean isSubElementCategory(int cat){
		int lower = ArrayUtils.indexOf(categories,CAT_TEST);
    	
    	return (cat <= lower); 
	}
	
	public static boolean isSubSectionCategory(int cat){
		int lower = ArrayUtils.indexOf(categories,CAT_PRELEARN);
    	
    	return (cat >= lower); 
	}
	
///////////////////////////////////////////////
//	 DocTypes
///////////////////////////////////////////////

	private static final String[] COMPONENT_TYPE = {"worksheet","generic_subelement"};
	/**
	 * gives the String for DocType. is need for XML
	 * @param docType
	 * @return
	 */
	public static String getComponentType(int docType){
		if (docType==DOCTYPE_SUBSECTION) return COMPONENT_TYPE[0];
		if (docType==DOCTYPE_SUBELEMENT) return COMPONENT_TYPE[1];
		return null;
	}
	
///////////////////////////////////////////////
// Colors
///////////////////////////////////////////////
    	
    public static Color[] sub_cols = { 
    	   new Color(0x99,0xCC,0xCC),
	       new Color(0xCC,0xCC,0x99),
	       new Color(0xCC,0x99,0x99),
	       new Color(0x99,0x99,0x99),
	       new Color(0x66,0xCC,0xCC),
	       new Color(0x99,0x99,0xCC),
	       new Color(0xCC,0xCC,0xCC),
	       new Color(0x99,0x99,0xCC),
	       Color.WHITE,
	       Color.WHITE,
	       Color.WHITE
	       ,Color.WHITE// FIXME:.. well worksheet is not a category ..
	};
    
    /**
     * returns the color of s subelement
     * @param cat  category of the subelement
     */
    public static Color getSubColor(int cat) {
    	if (cat < 0 || cat >= sub_cols.length) return null; 
    	return sub_cols[cat];
    }
    
    ///////////////////////////////////////
    // dimension
    ///////////////////////////////////////
    public static Dimension SUBELDIM = new Dimension(25,15);
    
}
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
 * Constants for Maincomponents
 * @author <a href="mailto:vrichter@math.tu-berlin.de">Verena Richter</a>
 * @author <a href="mailto:binder@math.tu-berlin.de">Jens Binder</a>
 * @version $Id: MainComponentConstants.java,v 1.17 2009/03/30 12:40:24 vrichter Exp $
 *
 */

public class MainComponentConstants{

	/**
	 * DOCTYPES define which documenttype is allowed
	 */
	public static final int DOCTYPE_SECTION = 0;
	public static final int DOCTYPE_ELEMENT = 1;
	public static final int DOCTYPE_PROBLEM = 2;
	
	/////////////////////////////////////////////
	//	 Categories
	/////////////////////////////////////////////
	/** how to add new categories?
	 * 1. define a new variable public static final String CAT_newName
	 * 2. add CAT_newName to categories array
	 * 3. define a shortname in ahorts-array at same position in categories array
	 * 4. check isProblemCategory, isElementCategory, isSectionCategory if they will work 
	 * 5. define a Color in main_col-array
	 */
	//Categories for elements
    public static final String CAT_DEFINITION = "definition";
    public static final String CAT_THEOREM = "theorem";
    public static final String CAT_LEMMA = "lemma";
    public static final String CAT_APPLICATION = "application";
    public static final String CAT_MOTIVATION = "motivation";
    public static final String CAT_ALGORITHM = "algorithm";
    
    // Categories for problem
    public static final String CAT_APPLET = "applet";
    public static final String CAT_MCHOICE = "mchoice";

    // Categories for sections
    public static final String CAT_SECTION = "course_section";
    
    /**
     * Array containing the category-names of (Sub)Elements
     */
    private static String[] categories = {
    	  CAT_DEFINITION,
		  CAT_THEOREM,
		  CAT_LEMMA,
		  CAT_APPLICATION,
		  CAT_MOTIVATION,
		  CAT_ALGORITHM,
		  
		  CAT_APPLET,
		  CAT_MCHOICE,
		  
		  CAT_SECTION
    };
    
    public static String[] shorts = {
    	"D", "T", "L", "A", "M", "Al", 
    	"A", "M", 
    	"S"};

    /**
     * returns if a category belongs to a Element
     * @param cat
     * @return true category is a elementCategory
     * false category is no elementCategory
     */
    
    
    public static boolean isElementCategory(int cat){
    	int lower = ArrayUtils.indexOf(categories,CAT_DEFINITION);
    	int upper = ArrayUtils.indexOf(categories,CAT_ALGORITHM);
    	return (cat >= lower && cat <= upper); 
    }
    
    /**
     * returns if a category belongs to a Problem
     * @param cat
     * @return true category is a problemCategory
     * false category is no problemCategory
     */    
    public static boolean isProblemCategory(int cat){
    	return (cat==ArrayUtils.indexOf(categories, CAT_APPLET)||
    			cat==ArrayUtils.indexOf(categories, CAT_MCHOICE));
    }
    /**
     * 
     * @param cat
     * @return
     */
    public static boolean isSectionCategory(int cat){
    	return (cat==ArrayUtils.indexOf(categories,CAT_SECTION));
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
    
    /**
     * Returns the string value of the given category
     * @param cat category given as int
     */
    public static String getCategoryAsString(int cat) {
		if (cat>-1&& cat <categories.length)
		    return categories[cat];
		
		return null;
    }
    
    public static int getDefaultCategory(int docType){
    	if (docType==DOCTYPE_SECTION) return getCategoryAsInt(CAT_SECTION);
    	else if (docType==DOCTYPE_ELEMENT) return getCategoryAsInt(CAT_DEFINITION);
    	return getCategoryAsInt(CAT_MCHOICE);
    }

    ///////////////////////////////////////////////
	//	 DocTypes
	///////////////////////////////////////////////
    
    private static final String[] COMPONENT_TYPE = 
    		{"course_section","generic_element","generic_problem"};
	
    public static String getComponentType(int docType){
    	if (docType==MetaInfos.GRAPHTYPE_SECTION)
    		return COMPONENT_TYPE[MetaInfos.GRAPHTYPE_SECTION]; 
    	if (docType==MetaInfos.GRAPHTYPE_ELEMENT) 
    		return COMPONENT_TYPE[MetaInfos.GRAPHTYPE_ELEMENT];
    	return COMPONENT_TYPE[2];
    }
	/////////////////////////////////////////////
	//	 colors
	/////////////////////////////////////////////	
    public static Color[] main_cols = { 
	   	new Color(0x33,0x99,0x99),//def
		new Color(0xCC,0xCC,0x66),//theorem
		new Color(0xCC,0xCC,0x66),//lemma
		new Color(0xCC,0x99,0x99),//application
		new Color(0x33,0x99,0x66),//mot
		new Color(0xCC,0x66,0x66),//Algo
		Color.PINK,//applet 
		new Color(130,180,130),
//		Color.pink,//mchoice
		Color.WHITE//Section
	};
   /**
    * returns the color for mainComponent
    * @param cat  category of the mainComponent
    * @return the color, or <code>null</code>, if the value of the
    * <code>cat</code> parameter lies outside the bounds of the 
    * {@link #main_cols} array.
    */
   public static Color getMainColor(int cat) {
	   if (cat < 0 && cat >= main_cols.length) return null;
	    return main_cols[cat];
   }
   
   ///////////////////////////////////////
   // dimension
   ///////////////////////////////////////
    public static Dimension ELDIM = new Dimension(25,25);
}
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

package net.mumie.coursecreator.graph;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.ArrayUtils;

import net.mumie.coursecreator.CCController;
import net.mumie.coursecreator.graph.cells.SubComponentConstants;
import net.mumie.coursecreator.xml.XMLConstants;

/**
 * This class holds all the Meta Informations of a Graph. It is provifiding setter and getter methods for nearly 
 * all necessary informations
 * 
 * @author <a href="mailto:binder@math.tu-berlin.de>Jens Binder</a>
 * @author <a href="mailto:vrichter@math.tu-berlin.de>Verena Richter</a>
 * @version $Id: MetaInfos.java,v 1.31 2009/03/19 15:53:33 vrichter Exp $
 */

public class MetaInfos {
	
	public CCController controller;
	/**
	 * different GraphTypes
	 */
	public static final int GRAPHTYPE_UNDEFINED =-1;
	public static final int GRAPHTYPE_SECTION =0;
	public static final int GRAPHTYPE_ELEMENT =1;
	public static final int GRAPHTYPE_PRELEARN =2;
	public static final int GRAPHTYPE_HOMEWORK =3;
	public static final int GRAPHTYPE_SELFTEST =4;
	
	/**
	 * different status levels for a graph
	 */
	public static final int ST_UNDEFINED = -1;
	public static final int ST_PRE = 0;
    public static final int ST_DEVEL_OK = 1;
    public static final int ST_CONTENT_OK = 2;
    public static final int ST_CONTENT_COMPLETE = 3;
    public static final int ST_PUBLISHABLE = 4;
    public static final int ST_FINAL = 5;

    /**
     * the names for the defined levels
     */
    public static final String[] statusNames =  {
    	"pre",
    	"devel_ok",
    	"content_ok",
    	"content_complete",
    	"ok_for_publication",
    	"final",
    };
    
	/**
	 * Describe variable <code>DEFAULT_NAME</code> and <code>DEFAULT_DESCRIPTION</code> here.
	 * they are displayed when nothing was written into the fields yet
	 */
	public static final String DEFAULT_NAME = "- no name -";
	public static final String DEFAULT_DESCRIPTION = "- no description -";
	public static final String DEFAULT_CLASSNAME = "keine Lehrveranstaltung gesetzt";
	public static String DEFAULT_COPYRIGHT;

    static {
	DEFAULT_COPYRIGHT = "(c) " + Calendar.getInstance().get(Calendar.YEAR) 
	    + " The MUMIE Project, TU Berlin";
    }
	
	private String name; // name of the graph
	private String descr; // description of the graph
	
	private int graphType;  
	private String classPath;
	private String className;
	
	private int status;
	private String copyright;
	
    private Calendar dateStartCal = null;
    private Calendar dateEndCal = null;
    
    private int[] authors;
    
    // a link to the summary of this graph
    private String summaryPath = "";
    private String saveName = null;
    private String saveDir = null;
	
	/**
	 * Create a new MetaInfos instance.
	 * Using default settings.
	 */
	public MetaInfos(CCController c) {
		
		this.controller = c;
		this.name = DEFAULT_NAME;
		this.descr = DEFAULT_DESCRIPTION;
		this.copyright = DEFAULT_COPYRIGHT;
		this.graphType= GRAPHTYPE_UNDEFINED;
		this.authors = ArrayUtils.EMPTY_INT_ARRAY;
		this.classPath = XMLConstants.PATH_UNDEFINED;
		this.className = DEFAULT_CLASSNAME;
		this.status = ST_UNDEFINED;
		
	}
	
	/**
	 * returns the Type of this Graph as an <code>int</code> defined earlier
	 * @param type
	 * @return int - the GraphType
	 */
	public static int getGraphTypeForXML(int type){
		if ((type==GRAPHTYPE_PRELEARN)||(type==GRAPHTYPE_HOMEWORK) ||(type==GRAPHTYPE_SELFTEST)) 
			return Math.min(GRAPHTYPE_PRELEARN,Math.min(GRAPHTYPE_HOMEWORK,GRAPHTYPE_SELFTEST));
		return type;
	}
	
	
	/**
	 * get the category-value
	 * @return an <code>int</code> value
	 */
	public int getGraphType() {
		return graphType;
	}
	
	/**
	 * Set the Category value.
	 * @param newCategory The new Category value.
	 */
	public void setGraphType(int newGraphType) {
		this.graphType = newGraphType;
	}    
	
	 public boolean isProblemGraph(){
	    if (this.getGraphType()==MetaInfos.GRAPHTYPE_HOMEWORK || 
	    			this.getGraphType()==MetaInfos.GRAPHTYPE_PRELEARN ||
	    			this.getGraphType()==MetaInfos.GRAPHTYPE_SELFTEST) return true;
	    return false;
	 } 
	
	 public boolean isSectionGraph(){
		 if (this.getGraphType()==MetaInfos.GRAPHTYPE_SECTION) return true;
	    return false;
	 } 
	 
	/**
	 * gives the String for GRAPHType. is need for XML
	 * @param graphType GRAPHTYPE_SECTION, GRAPHTYPE_ELEMENT, 
	 * 					 GRAPHTYPE_PRELEARN, GRAPHTYPE_HOMEWORK or GRAPHTYPE_SELFTEST
	 * @return course_section, element, prelearn, homework or selftest
	 */
	public static String getGraphtypeAsString(int graphType){
		if (graphType==GRAPHTYPE_SECTION) return "course_section";
		if (graphType==GRAPHTYPE_ELEMENT) return "element";
		if (graphType==GRAPHTYPE_PRELEARN) return SubComponentConstants.CAT_PRELEARN;
		if (graphType==GRAPHTYPE_HOMEWORK) return SubComponentConstants.CAT_HOMEWORK;
		if (graphType==GRAPHTYPE_SELFTEST) return SubComponentConstants.CAT_SELFTEST;
		if (graphType==GRAPHTYPE_UNDEFINED) return "undefined";
		return null;
	}
	
	/**
	 * returns the given GraphType as an <code>int</code> 
	 * @param graphType
	 * @return
	 */
	public static int getGraphTypeAsInt(String graphType){
		if (graphType.equals("course_section")) return GRAPHTYPE_SECTION;
		if (graphType.equals("element")) return GRAPHTYPE_ELEMENT;
		if (graphType.equals(SubComponentConstants.CAT_PRELEARN)) return GRAPHTYPE_PRELEARN;
		if (graphType.equals(SubComponentConstants.CAT_HOMEWORK)) return GRAPHTYPE_HOMEWORK;
		if (graphType.equals(SubComponentConstants.CAT_SELFTEST)) return GRAPHTYPE_SELFTEST;
		 //default:
		return GRAPHTYPE_UNDEFINED;
	}
	
	/**
	 * tests if a graphtype is an ProblemGraphtype
	 * @param type
	 * @return
	 */
	public static boolean isProblemGraphType(int type){
		if (type == GRAPHTYPE_HOMEWORK || type==GRAPHTYPE_PRELEARN || type==GRAPHTYPE_SELFTEST)
			return true;
		return false;
		
	}
	
	/**
	 * some getter and setter methods
	 * @return
	 */
	public String getDescription() { return descr;	}
	public void setDescr(String desc) { this.descr = desc;}
	
	public String getName() { return this.name; }
	public void setName(String name){ this.name = name; }
	
	public int[] getAuthors() { return this.authors; }
	public void setAuthors(int[] auth) {this.authors = auth;}
	public void addAuthor(int id) { this.authors = ArrayUtils.add(this.authors,id); }
	
	/**
     * Set the copyright.
     * @param copyright String
     */
    public void setCopyright(String copyright) { this.copyright = copyright; }
    
    /**
     * Returns the copyright.
     * @return a <code>String</code> value
     */
    public String getCopyright() { return this.copyright; }
    
    /** sets classPath*/
    public void setClassPath(String path){this.classPath=path;}
    
    /** returns classpath @return classpath*/
    public String getClassPath(){return this.classPath;}
    
    /** sets className*/
    public void setClassName(String name){this.className=name;}
    
    /** returns classname @return classname*/
    public String getClassName(){return this.className;}
    
    /** sets the summarypath*/
    public void setSummary(String path){this.summaryPath = path;}
    
    /** returns the summarypath*/
    public String getSummaryPath(){return this.summaryPath;}
    
    /** Sets the save name of the graph */
    public void setSaveName(String saveName) {this.saveName = saveName;}
    
    /**
     * Returns the save name of the graph.
     * @return saveName is the filename of this graph <b> without '.meta.xml' </b>
     */
    public String getSaveName() { return this.saveName; }
    
    /** Sets the save directory of the graph */
    public void setSaveDir(String dir) {this.saveDir = dir;}
    
    /**
     * Returns the save name of the graph.
     * @return saveName is the filename of this graph <b> without '.meta.xml' </b>
     */
    public String getSaveDir() { return this.saveDir; }
    
    /**
     * Sets default values for empty strings.
     */
    public void setDefaults() {
    	if(name.equals("")) name = DEFAULT_NAME;
    	if(descr.equals("")) descr = DEFAULT_DESCRIPTION;
    	if(copyright.equals("")) copyright = DEFAULT_COPYRIGHT;
    }
   
    /**
     * Returns the status of the graph.
     * @return an <code>int</code> value
     */
    public int getStatus() { return this.status; }
    
    /**
     * Sets the status of the graph.
     * @param status int, the status of the graph
     */
    public void setStatus(int status) { this.status = status; }

    public void setStatus(String s) { 
    	this.status = 0;
    	for (int i = 0; i<statusNames.length; i++){ 
    		if (s.equals(statusNames[i])){
    			this.status = i;
    			return;
    		}
    	}
    }

    public static String getStatusAsString(int s) {if ((statusNames.length>=s)||(s<0))
    	return "";
    	return statusNames[s];}

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public Calendar getDateStartCal() {
		
		if (dateStartCal ==null){
			dateStartCal = GregorianCalendar.getInstance();
			dateStartCal.setLenient(true);
			dateStartCal.roll(Calendar.HOUR_OF_DAY,true);
			dateStartCal.set(Calendar.MINUTE,0);
		}
		
		return  this.dateStartCal; }
    
	/**
	 * Describe <code>setDateStartCal</code> method here.
	 *
	 * @param sdate a <code>Calendar</code> value
	 */
	public void setDateStartCal(Calendar sdate) {
		
		this.dateStartCal = sdate;
	}
	
	
	/**
	 * Describe <code>setDateEndCal</code> method here.
	 *
	 * @param sdate a <code>Calendar</code> value
	 */
	public void setDateEndCal(Calendar sdate) {
		this.dateEndCal = sdate;
	}
	
	
	/**
	 * Describe <code>getDateEndCal</code> method here.
	 *
	 * @return a <code>Calendar</code> value
	 */
	public Calendar getDateEndCal() {

		if (dateEndCal ==null){
			dateEndCal = GregorianCalendar.getInstance();
			dateEndCal.setLenient(true);
			dateEndCal.roll(Calendar.HOUR_OF_DAY,true);
			dateEndCal.set(Calendar.MINUTE,0);
		}
		return this.dateEndCal; 
	}
	

    public boolean isSameDate(Calendar date1, Date date2)
			throws NumberFormatException {
		return (date1 != null && date2 != null && date1.getTime().equals(date2));
	}
	
	
	public static Calendar newCalendar(long millis){
		Calendar cal = Calendar.getInstance(); 
		cal.setLenient(true); 
		cal.setTimeInMillis(millis);
		return cal; 
	}
	
	public String getFormattedDate(Calendar cal) {
		
		StringBuffer datesb = new StringBuffer(); 
		FieldPosition field = 
			new FieldPosition(DateFormat.Field.ofCalendarField(Calendar.DATE));
		
	
		datesb = DATE_FORMAT.format(cal.getTime(), datesb, field);	    
		return datesb.toString(); 
	}
	
	public String getRawDate(Calendar cal){
		return Long.toString(cal.getTimeInMillis());
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
	
		return "Name "+this.name +
				" Desc "+this.descr+
				" Status "+this.status+
				" graphtype "+this.graphType+
				" classpath "+this.classPath +
				" className "+this.className ;
		 
	}

	void babble(String bab){
		if (this.controller.getConfig().getDEBUG_META_INFOS()&& this.controller.getConfig().getDEBUG())
			System.out.println("MetaInfos:"+bab);
	}
}

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

package net.mumie.coursecreator.xml;

import net.mumie.coursecreator.flatgraph.FlatGraph;
import net.mumie.coursecreator.graph.*;

/***
 * XMLConstants.java
 *
 * Created: Tue Mar 22 18:12:13 2005
 **/
/**
 * Some constants useful in connection with reading and writing Course XML 
 *
 * @author <a href="mailto:sinha@math.tu-berlin.de">Uwe Sinha</a>
 * @version $Revision: 1.12 $ ($Date: 2009/03/30 12:34:22 $)
 */

public class XMLConstants {

    public static final int UNDEFINED_ID = -1;
    public static final String PATH_UNDEFINED = "";
    

    public static final String NS_META 
	= "http://www.mumie.net/xml-namespace/document/metainfo";

    private static final String[] NS_STRUCT =
    {
	"http://www.mumie.net/xml-namespace/document/content/course",
	"http://www.mumie.net/xml-namespace/document/content/course_section",
	"http://www.mumie.net/xml-namespace/document/content/worksheet",
	"http://www.mumie.net/xml-namespace/document/content/course/abstract"
    };

    public static final String PREFIX_META = "mumie:";
    
    private static final String[] PREFIX = {"crs:", "csec:", "csub:","cabs:"};
    private static final String[] PREFIXNAME = {"crs", "csec", "csub","cabs"};

    public static final String[] ROOT_TAG = {"course","course_section","worksheet"};
      
    /**
     * gives PREFIX "crs:", "csec:", "csub:","cabs:" for an graphtype
     * for abstract use Graphtype -1
     * @param GraphType 
     * @return
     */
    public static String getPrefix(int graphType){
//    	if (graphType==-1) return PREFIX[3];
//    	if (graphType==MetaInfos.GRAPHTYPE_SECTION) return PREFIX[0];
//    	if (graphType==MetaInfos.GRAPHTYPE_ELEMENT) return PREFIX[1];
//    	return PREFIX[2];
    	
    	if (graphType==MetaInfos.GRAPHTYPE_SECTION||
			graphType==MetaInfos.GRAPHTYPE_ELEMENT||
			graphType==MetaInfos.GRAPHTYPE_HOMEWORK||
			graphType==MetaInfos.GRAPHTYPE_PRELEARN||
			graphType==MetaInfos.GRAPHTYPE_SELFTEST ) return PREFIX[0];

    	return PREFIX[3];
    	
    	
    }
    
    /**
     * gives PREFIXNAME "crs", "csec", "csub","cabs" for an graphtype
     * for abstract use Graphtype -1
     * @param GraphType 
     * @return
     */
    public static String getPrefixName(int graphType){

    	if (graphType==MetaInfos.GRAPHTYPE_SECTION||
			graphType==MetaInfos.GRAPHTYPE_ELEMENT||
			graphType==MetaInfos.GRAPHTYPE_HOMEWORK||
			graphType==MetaInfos.GRAPHTYPE_PRELEARN||
			graphType==MetaInfos.GRAPHTYPE_SELFTEST ) return PREFIXNAME[0];

    	return PREFIXNAME[3];
    	
    }
    
    /**
     * gives NSStruct 
     * 
     * 	"http://www.mumie.net/xml-namespace/document/content/course",
     * 	"http://www.mumie.net/xml-namespace/document/content/course_section",
     * 	"http://www.mumie.net/xml-namespace/document/content/worksheet",
     * 	"http://www.mumie.net/xml-namespace/document/content/course/abstract"
     * 
     *  for an graphtype
     * for abstract use Graphtype -1
     * @param GraphType 
     * @return
     */
    public static String getNS_STRUCT(int graphType){
    	
    	if (graphType==MetaInfos.GRAPHTYPE_SECTION||
			graphType==MetaInfos.GRAPHTYPE_ELEMENT||
			graphType==MetaInfos.GRAPHTYPE_HOMEWORK||
			graphType==MetaInfos.GRAPHTYPE_PRELEARN||
			graphType==MetaInfos.GRAPHTYPE_SELFTEST ) return NS_STRUCT[0];

       	return NS_STRUCT[3];
    	
    }
    
    public static String getRootTag(NavGraph nav){ 
    	if (nav.isSectionGraph()) return ROOT_TAG[0];
    	if (nav.isElementGraph()) return ROOT_TAG[1];
    	else return ROOT_TAG[2];
    	
    }
    public static String getRootTag(FlatGraph g){ 
    	if (g.isSectionGraph()) return ROOT_TAG[0];
    	if (g.isElementGraph()) return ROOT_TAG[1];
    	else return ROOT_TAG[2];
    	
    }
    

}// XMLConstants

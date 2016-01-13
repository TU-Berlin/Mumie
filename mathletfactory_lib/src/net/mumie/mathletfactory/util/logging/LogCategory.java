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

package net.mumie.mathletfactory.util.logging;

import java.util.ArrayList;

/**
 * This class represents a logging category in the MathletFactory Logging Framework.
 * It consists of a <i>name</i> and a <i>scope</i> which declares the valid classes and packages
 * in which it can be used to identify log messages. 
 * 
 * @author gronau
 * @mm.docstatus outstanding
 */
public class LogCategory {
	
	private String m_name;
	
//	private String m_scope;
	
//	public static LogCategory getTarget(Class loggingClass, String name) {
//		return LoggingManager.getLoggingManager().getCategory(loggingClass, name);
//	}
	
	LogCategory(String name) {
		m_name = name;
	}

//	LogCategory(String scope, String name) {
//		m_scope = scope;
//		m_name = name;
//	}
	
	/**
	 * Returns the name of this category.
	 */
	public String getName() {
		return m_name;
	}
	
//	public String getScope() {
//		return m_scope;
//	}
	
	/**
	 * Returns a new logging category list containing <code>this</code> category and <code>anotherCategory</code>.
	 */
	public LogCategoryList and(LogCategory anotherCategory) {
		return new LogCategoryList(this, anotherCategory);
	}
	
	public String toString() {
		return getName();
	}
}

/**
 * This class represents a list for logging categories.
 */
class LogCategoryList {
	private ArrayList m_list = new ArrayList();
	
	LogCategoryList() {}
	
	LogCategoryList(LogCategory c1, LogCategory c2) {
		add(c1);
		add(c2);
	}
	
	void clear() {
		m_list.clear();
	}
	
	void add(LogCategory category) {
		if(m_list.contains(category) == false)
			m_list.add(category);
	}
	
	void addAll(LogCategoryList list) {
		if(list == null)
			return;
		LogCategory[] categories = list.getCategories();
		addAll(categories);
	}
	
	void addAll(LogCategory[] categories) {
		if(categories == null)
			return;
		for(int i = 0; i < categories.length; i++)
			add(categories[i]);
	}
	
	LogCategory get(int index) {
		return (LogCategory) m_list.get(index);
	}
	
	int size() {
		return m_list.size();
	}
	
	boolean contains(LogCategory category) {
		return m_list.contains(category);
	}
	
	void remove(LogCategory category) {
		m_list.remove(category);
	}
	
	/**
	 * Returns an array containg all categories in this list.
	 */
	public LogCategory[] getCategories() {
		LogCategory[] result = new LogCategory[m_list.size()];
		m_list.toArray(result);
		return result;
	}
	
	/**
	 * Returns this category list and adds <code>anotherCategory</code> to it.
	 */
	public LogCategoryList and(LogCategory anotherCategory) {
		add(anotherCategory);
		return this;
	}
}

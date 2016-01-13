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
 * This class represents a logger used for logging a single class (or package).
 * The creation of such a logger is the first step in using the MathletFactory Logging Framework.
 * This logger can be used to gather "logging categories" for which events (e.g. text messages)
 * must be logged with.
 * 
 * @author gronau
 * @mm.docstatus outstanding
 */
public class MumieLogger {

	private LogCategoryList m_loggableCategories;
	
	private LogCategoryList m_activeCategories;
	
	private MumieLogger m_parent;
	
	private LoggerList m_children;
	
	private String m_shortName;
	
	private String m_fullName;
	
	// used to cache result of method "isActive" for performance reasons
	private boolean m_isActive = false;
	
	private static boolean isRoot(String fullName) {
		return fullName.indexOf('.') == -1;
	}
	
	private static String getParentName(String fullName) {
		if(isRoot(fullName))
			return null;
		else
			return fullName.substring(0, fullName.lastIndexOf('.'));
	}
	
	/**
	 * Returns the logger for a given class.
	 * Note that multiple calls to this method with the same argument
	 * always return the same logger instance.
	 * 
	 * @see {@link LoggingManager#getLogger(String)}.
	 */
	public static MumieLogger getLogger(Class loggingClass) {
		return LoggingManager.getLoggingManager().getLogger(loggingClass.getName());
	}
	
	MumieLogger(String fullName) {
		m_parent = LoggingManager.getLoggingManager().getLogger(getParentName(fullName), false);
		if(m_parent != null) // root
			m_parent.addChild(this);
		m_shortName = isRoot(fullName) ? fullName : fullName.substring(fullName.lastIndexOf('.') + 1);
		m_fullName = fullName;
	}
	
	MumieLogger(MumieLogger parent, String shortName) {
		m_parent = parent;
		if(parent != null) // root
			m_parent.addChild(this);
		m_shortName = shortName;
		m_fullName = m_parent == null ? shortName : m_parent.getFullName() + "." + shortName;
	}
	
	private boolean containsCategories(LogCategory[] categories) {
		for(int i = 0; i < categories.length; i++) {
			if(containsCategory(categories[i]))
				return true;
		}
		return false;
	}
	
	private boolean containsCategory(LogCategory category) {
		if(m_activeCategories == null) {
			if(isRoot())
				return false;
			return getParent().containsCategory(category);
		}
		for(int i = 0; i < m_activeCategories.size(); i++) {
			if(m_activeCategories.get(i) == category) // reference comparision
				return true;
		}
		return false;
	}
	
	/**
	 * Returns a named category from this logger. 
	 * Note that this category must be added formerly in the LoggingManager
	 * by a declaration in the "loggable categories" file
	 * in order to be returned by this method.
	 * 
	 * @param categoryName name of a formerly registered category
	 * @return a named category or null if such category was not registered
	 */
	public LogCategory getCategory(String categoryName) {
		if(m_loggableCategories == null) {
			if(isRoot())
				return null;
			return getParent().getCategory(categoryName);
		}
		for(int i = 0; i < m_loggableCategories.size(); i++) {
			if(m_loggableCategories.get(i).getName().equals(categoryName))
				return m_loggableCategories.get(i);
		}
		System.err.println("No logging category found for name \"" + categoryName + "\" in " + m_shortName);
		return null;
	}

	String getFullName() {
		return m_fullName;
	}
	
	String getShortName() {
		return m_shortName;
	}
	
	MumieLogger getParent() {
		return m_parent;
	}
	
	boolean isRoot() {
		return getParent() == null;
	}
	
	MumieLogger addChild(String shortName) {
		MumieLogger child = getChild(shortName);
		if(child == null) {
			child = new MumieLogger(this, shortName);
			addChild(child);
		}
		return child;
	}
	
	void addChild(MumieLogger child) {
		if(m_children == null)
			m_children = new LoggerList();
		m_children.add(child);
	}
	
	int getChildCount() {
		return m_children == null ? 0 : m_children.size();
	}
	
	MumieLogger getChild(int index) {
		return m_children.getLogger(index);
	}
	
	MumieLogger getChild(String shortName) {
		for(int i = 0; i < m_children.size(); i++) {
			MumieLogger child = getChild(i);
			if(child.getShortName().equals(shortName))
				return child;
		}
		return null;
	}
	
	void addLoggableCategory(LogCategory lc) {
		if(m_loggableCategories == null)
			m_loggableCategories = new LogCategoryList();
		m_loggableCategories.add(lc);
	}
	
	LogCategory[] getLoggableCategories() {
		LogCategoryList list = new LogCategoryList();
		list.addAll(m_loggableCategories);
		if( !isRoot())
			list.addAll(getParent().getLoggableCategories());
		return list.getCategories();
	}
	
	boolean isActive(LogCategory category) {
		MumieLogger logger = getActiveLoggerFor(category);
		boolean isContained = false;
		if(m_activeCategories != null) {
			isContained = m_activeCategories.contains(category);
		}
		if(isContained)
			return true;
		return isActiveInParent(category);
	}
	
	/** 
	 * Returns whether the given log category is "active" (i.e. enabled) for this logger.
	 * If <code>true</code> messages/actions should be logged for this category.
	 */
	public boolean isActiveCategory(LogCategory category) {
		return m_isActive && isActive(category);
	}
	
	LogCategory[] getOwnActiveCategories() {
		if(m_activeCategories == null)
			return new LogCategory[0];
		else
			return m_activeCategories.getCategories();
	}
	
	boolean isActiveInThis(LogCategory category) {
		return getActiveLoggerFor(category) == this;
	}

	
	boolean isActiveInParent(LogCategory category) {
		if(isRoot() == false)
			return getParent().isActive(category);
		else
			return false;
	}
	
	MumieLogger getActiveLoggerFor(LogCategory category) {
		boolean isContained = false;
		if(m_activeCategories != null) {
			isContained = m_activeCategories.contains(category);
		}
		if(isContained)
			return this;
		if(isRoot() == false)
			return getParent().getActiveLoggerFor(category);
		else
			return null;
	}
	
	void addActiveCategory(LogCategory lc) {
		if(m_activeCategories == null)
			m_activeCategories = new LogCategoryList();
		m_activeCategories.add(lc);
	}
	
	void removeActiveCategory(LogCategory lc) {
		if(m_activeCategories == null)
			return;
		m_activeCategories.remove(lc);
	}
	
	void clearActiveCategories() {
		if(m_activeCategories != null) {
			m_activeCategories.clear();
			m_activeCategories = null;
		}
	}
	
	boolean isActive() {
		if(m_activeCategories == null)
			return isRoot() ? false : getParent().isActive();
		else
			return true;
	}
	
	void setActive(boolean flag) {
		m_isActive = isActive();
		for(int i = 0; i < getChildCount(); i++) {
			getChild(i).setActive(flag);
		}
	}
	
	/**
	 * Logs a text message for multiple logging categories.
	 * One of these categories must be turned on for this logger in order
	 * to log the message.
	 * 
	 * @param categories a list of logging categories; must not be null
	 * @param message an arbitrary plain text message
	 */
	public void log(LogCategoryList categories, String message) {
		if(m_isActive && categories != null && containsCategories(categories.getCategories()))
			System.out.println(m_shortName + ": " + message);
	}
	
	/**
	 * Logs a text message for a single logging category.
	 * This category must be turned on for this logger in order
	 * to log the message.
	 * 
	 * @param category a logging category; must not be null
	 * @param message an arbitrary plain text message
	 */
	public void log(LogCategory category, String message) {
		if(m_isActive && category != null && containsCategory(category))
			System.out.println(m_shortName + ": " + message);
	}
	
	public void info(String message) {
		if(m_isActive)
			System.out.println(m_shortName + ": " + message);
	}
}

class LoggerList extends ArrayList {
	MumieLogger getLogger(int index) {
		return (MumieLogger) get(index);
	}
}

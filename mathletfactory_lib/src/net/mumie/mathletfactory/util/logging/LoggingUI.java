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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.mumie.mathletfactory.util.BasicApplicationFrame;

/**
 * This class represents an user interface for the MathletFactory Logging Framework and
 * can be used to configure the logging environment.
 * 
 * @author gronau
 * @mm.docstatus outstanding
 */
public class LoggingUI extends JPanel {

	private JTree m_classTree;
	private JList m_categoriesList;
	private MumieLogger m_currentLogger;
	private boolean m_isEnabled;
	
	LoggingUI() {
		super(new BorderLayout());
		
		m_classTree = new JTree();
		m_classTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				updateList(e.getNewLeadSelectionPath());
			}
		});
		JScrollPane treeScroller = new JScrollPane(m_classTree);
		treeScroller.setBorder(new TitledBorder("Class Tree"));
		
		m_categoriesList = new JList();
		m_categoriesList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				changeSelectedCategory();
			}
		});
		m_categoriesList.setCellRenderer(new LogCategoryRenderer());
		JScrollPane listScroller = new JScrollPane(m_categoriesList);
		listScroller.setBorder(new TitledBorder("Available Log Categories"));

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroller, listScroller);
		splitPane.setDividerLocation(400);
		add(splitPane, BorderLayout.CENTER);
		
		JPanel bottomPane = new JPanel();
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LoggingManager.getLoggingManager().save();
			}
		});
		JButton closeButton = new JButton("Quit");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		bottomPane.add(saveButton);
		bottomPane.add(closeButton);
		add(bottomPane, BorderLayout.SOUTH);
		
		boolean loggingIsEnabled = LoggingManager.getLoggingManager().isActive();
		final JCheckBox enableLoggingBox = new JCheckBox("Enable logging", loggingIsEnabled);
		enableLoggingBox.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				enableAll(enableLoggingBox.isSelected());
				LoggingManager.getLoggingManager().setActive(enableLoggingBox.isSelected());
			}
		});
		JPanel topPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPane.add(enableLoggingBox);
		add(topPane, BorderLayout.NORTH);

		ClassFinder cf = new ClassFinder();
		m_classTree.setModel(cf.getClassTree());
		enableAll(loggingIsEnabled);
	}
	
	private void enableAll(boolean flag) {
		m_isEnabled = flag;
		m_classTree.setEnabled(flag);
		m_categoriesList.setEnabled(flag);
	}
	
	private void changeSelectedCategory() {
		if(m_isEnabled == false)
			return;
		LogCategory category = (LogCategory) m_categoriesList.getSelectedValue();
		if(category == null)
			return;
		if(m_currentLogger.isActive(category)) {
			m_currentLogger.removeActiveCategory(category);
		} else {
			m_currentLogger.addActiveCategory(category);
		}
		m_categoriesList.repaint();
	}
	
	public static void main(String[] args) {
		LoggingUI panel = new LoggingUI();
		BasicApplicationFrame frame = new BasicApplicationFrame(panel, 800, 600);
		frame.setTitle("Mumie Logging User Interface");
		frame.show();
	}
	
	private void updateList(TreePath treePath) {
		m_categoriesList.removeAll();
//		System.out.println(getFullJavaName(treePath));
		m_currentLogger = LoggingManager.getLoggingManager().getLogger(getFullJavaName(treePath));
		Vector listData = new Vector();
		LogCategory[] categories = m_currentLogger.getLoggableCategories();
		for(int i = 0; i < categories.length; i++) {
			listData.add(categories[i]);
		}
		m_categoriesList.setListData(listData);
		m_categoriesList.repaint();
	}
	
	private String getFullJavaName(TreePath treePath) {
		if(treePath == null)
			return "";
		String result = "";
		for(int i = 1; i < treePath.getPathCount(); i++) {
			result += treePath.getPathComponent(i);
			if(i < treePath.getPathCount() - 1)
				result += ".";
		}
		return result;
	}

	class LogCategoryRenderer extends JCheckBox implements ListCellRenderer {
		
		LogCategoryRenderer() {
		}
	
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			LogCategory category = (LogCategory) value;
			if(isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			MumieLogger activeLogger = m_currentLogger.getActiveLoggerFor(category);
			if(activeLogger == null) {
				setSelected(false);
				setText(category.getName());
				setEnabled(m_isEnabled);
			}
			else if(activeLogger != m_currentLogger) {
				setSelected(true);
				setText(category.getName() + " (enabled in " + activeLogger.getFullName() + ")");
				setEnabled(false);
			}
			else if(activeLogger == m_currentLogger) {
				setSelected(true);
				setText(category.getName());
				setEnabled(m_isEnabled);
			}
			return this;
		}
	}
}

class ClassFinder {
	
	final static String BASE_CLASS_NAME = "LoggingUI";
	final static String REAL_CLASS_NAME = BASE_CLASS_NAME + ".class";
	
	private DefaultTreeModel m_treeModel;
	private FileFilter m_classFileFilter = new FileFilter() {
		public boolean accept(File pathname) {
			String name = pathname.getName();
			return name.endsWith(".class") && name.indexOf('$') == -1;
		}
	};
	private FileFilter m_directoryFilter = new FileFilter() {
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	};

	ClassFinder() {
		File basePath = getBaseURL();
		System.out.println("Started from " + (basePath.isDirectory() ? "directory " : "jar file ") + basePath.getAbsolutePath() + "\n");
		
		MutableTreeNode rootNode = new DefaultMutableTreeNode("(default package)");
		m_treeModel = new DefaultTreeModel(rootNode);
		try {
			if(basePath.isDirectory()) {
				readDirectory(basePath, rootNode);
			} else {
				readJarFile(basePath, rootNode);
			}
		} catch(Exception e) {
			System.err.println("Cannot read class tree: " );
			e.printStackTrace();
		}
	}
	
	private void readDirectory(File dir, MutableTreeNode parent) throws Exception {
		File[] files = dir.listFiles(m_classFileFilter);
		for(int i = 0; i < files.length; i++) {
			m_treeModel.insertNodeInto(new DefaultMutableTreeNode(getClassName(files[i].getName())), parent, 0);
		}
		File[] dirs = dir.listFiles(m_directoryFilter);
		for(int i = 0; i < dirs.length; i++) {
			MutableTreeNode dirNode = new DefaultMutableTreeNode(dirs[i].getName());
			m_treeModel.insertNodeInto(dirNode, parent, 0);
			readDirectory(dirs[i], dirNode);
		}
	}
	
//	private static String getJavaName(File file, File basePath) {
//		return file.getAbsolutePath();
//	}
	
	private void readJarFile(File file, MutableTreeNode parent) throws Exception {
		JarFile jarFile = new JarFile(file);
		Enumeration en = jarFile.entries();
		for( ; en.hasMoreElements(); ) {
			JarEntry entry = (JarEntry) en.nextElement();
			if(entry.getName().endsWith(".class") == false || entry.getName().indexOf('$') > -1)
				continue;
			getNode(entry.getName());
		}
	}
	
	private MutableTreeNode getNode(String fullName) {
		String tName = fullName;
		MutableTreeNode parentNode = (MutableTreeNode) m_treeModel.getRoot();
		while(tName.indexOf('/') > -1) {
			String nodeName = tName.substring(0, tName.indexOf('/'));
			parentNode = getNode(nodeName, parentNode, true);
			tName = tName.substring(nodeName.length()+1, tName.length());
		}
		return getNode(tName, parentNode, true);
	}
	
	private MutableTreeNode getNode(String nodeName, MutableTreeNode parent, boolean create) {
		MutableTreeNode result = null;
		for(int i = 0; i < m_treeModel.getChildCount(parent); i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) m_treeModel.getChild(parent, i);
			if(node.getUserObject().equals(nodeName)) {
				result = node;
			}
		}
		if(create && result == null) {
			result = new DefaultMutableTreeNode(getClassName(nodeName));
			m_treeModel.insertNodeInto(result, parent, 0);
		}
		return result;
	}
	
	TreeModel getClassTree() {
		return m_treeModel;
	}
	
	private String getClassName(String nameWithClassEnding) {
		if(nameWithClassEnding.endsWith(".class"))
			return nameWithClassEnding.substring(0, nameWithClassEnding.length() - ".class".length());
		else
			return nameWithClassEnding;
	}
	
	private File getBaseURL() {
		String pathFromPackage = "/" + LoggingUI.class.getName().replace('.', '/') + ".class";
//		System.out.println("Path from package: " + pathFromPackage);
		String pathToThisClass = LoggingUI.class.getResource(REAL_CLASS_NAME).getFile();
		if(pathToThisClass.startsWith("file:"))
			pathToThisClass = pathToThisClass.substring("file:".length());
//		System.out.println("Path to this class: " + pathToThisClass);
		if(pathToThisClass.endsWith(pathFromPackage) == false)
			exit("Cannot parse source path! Exiting.");
		int packageLength = pathFromPackage.length();
		if(pathToThisClass.indexOf('!') > -1)
			packageLength++;
		String basePath = pathToThisClass.substring(0, pathToThisClass.length() - packageLength);
//		System.out.println("Base path: " + basePath);
		return new File(basePath);
	}
	
	private static void exit(String errorMessage) {
		System.err.println(errorMessage);
		System.exit(-1);
	}
}
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

package net.mumie.mathletfactory.appletskeleton.util;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Gronau
 *
 * This class represents a tabbed pane where no tab is visible if only
 * 1 component is added.
 *
 */
public class TabbedPanel extends JPanel {

	//private final static String HIGHLIGHT_STRING = "<html><b>";
	private final static String HIGHLIGHT_STRING = "";
	
	private Component m_singleComp;
	private Component m_tabDummyPanel;
	private JTabbedPane m_tabbedPane;
	private int m_tabPlacement;

	private int m_tabComponentsCount = 0;

	/**
	 * Constructs a new TabbedPanel with bottom-tab's placement.
	 *
	 */
	public TabbedPanel() {
		this(SwingConstants.BOTTOM);
	}

	/**
	 * Constructs a new TabbedPanel with the given tab's placement.
	 * @see SwingConstants#TOP
	 * @see SwingConstants#BOTTOM
	 * @see SwingConstants#LEFT
	 * @see SwingConstants#RIGHT
	 */
	public TabbedPanel(int tabPlacement) {
		setLayout(new BorderLayout());
		m_tabPlacement = tabPlacement;
		m_tabDummyPanel = new JPanel();

		m_tabbedPane = new JTabbedPane(m_tabPlacement);
		m_tabbedPane.add(m_tabDummyPanel);
		getTabbedPane().getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				highlightCurrentTab();
			}
		});
	}
	
	private void highlightCurrentTab() {
		for(int i = 0; i < getTabbedPane().getTabCount(); i++) {
			String title = getTitleAt(i);
			if(i == getTabbedPane().getModel().getSelectedIndex()) {
				if(!title.startsWith(HIGHLIGHT_STRING))
					setTitleAt(i, HIGHLIGHT_STRING + title);
			} else {
				if(title.startsWith(HIGHLIGHT_STRING))
					setTitleAt(i, title.substring(HIGHLIGHT_STRING.length()));
			}
		}
	}

	/**
	 * Adds a new tab with <code>tabTitle</code> as title and <code>component</code> as content. 
	 */
	public void addTab(String tabTitle, Component tabComponent) {
		addComponent(tabComponent, tabTitle, -1);
	}

	public void addTab(String tabTitle, Component tabComponent, int tabIndex) {
		addComponent(tabComponent, tabTitle, (tabIndex == -1 || tabIndex > getTabComponentsCount()) ? -1 : tabIndex);
	}

  /**
   * Sets the title for the tab at the specified index.
   */
	public void setTitleAt(int tabIndex, String title) {
		m_tabbedPane.setTitleAt(tabIndex, title);
	}

	public String getTitleAt(int index) {
		return m_tabbedPane.getTitleAt(index);
	}

	public void setSelectedIndex(int index) {
		m_tabbedPane.setSelectedIndex(index);
	}

	public int getSelectedIndex() {
		return m_tabbedPane.getSelectedIndex();
	}

	public Component add(Component comp) {
		return addComponent(comp, "", -1);
	}

	public Component add(Component comp, int index) {
		return addComponent(comp, "", index);
	}

	public void add(Component comp, Object constraints) {
		addComponent(comp, "", -1);
	}

	public void add(Component comp, Object constraints, int index) {
		addComponent(comp, "", index);
	}

	public Component add(String title, Component comp) {
		return addComponent(comp, title, -1);
	}

	/*
	 * Adds a tab component with given title and position.
	 * Note: the position <code>-1</code> will append the tab behind the last tab.
	 */
	private Component addComponent(Component tabComponent, String tabTitle, int tabIndex) {
		if(m_tabComponentsCount == 0) { // still empty, adding first comp
			m_singleComp = tabComponent;
			m_tabbedPane.setTitleAt(0, HIGHLIGHT_STRING + tabTitle);
			m_tabComponentsCount++;
			super.add(m_singleComp, BorderLayout.CENTER);
		} else if(m_tabComponentsCount == 1) { // adding second comp
      switchToTabMode();
      insertTab(tabComponent, tabTitle, tabIndex);
			m_tabComponentsCount++;
		} else {
      insertTab(tabComponent, tabTitle, tabIndex);
			m_tabComponentsCount++;
		}
		highlightCurrentTab();
		return tabComponent;
	}
	
	private void insertTab(Component tabComponent, String tabTitle, int tabIndex) {
		if(tabIndex == -1)
			m_tabbedPane.addTab(tabTitle, tabComponent);
		else
			m_tabbedPane.insertTab(tabTitle, null, tabComponent, null, tabIndex);
	}

	/**
	 * Removes the specified component.
	 */
	public void remove(Component comp) {
		for (int i = 0; i < m_tabbedPane.getComponentCount(); i++) {
			if(m_tabbedPane.getComponent(i) == comp) {
        m_tabbedPane.remove(i);
        m_tabComponentsCount--;
        if(getTabComponentsCount() == 1)
          switchFromTabMode();
				return;
			}
		}
		if(m_singleComp != null && m_singleComp == comp) {
      switchToEmptyMode();
    }
		highlightCurrentTab();
	}
	
//	/**
//	 * Returns the component contained in the tab with the specified index.
//	 */
//	public Component getTab(int index) {
//		return m_tabbedPane.getTabComponentAt(index);
//	}

	/**
	 * Removes the component contained in the tab with the specified index.
	 */
	public void remove(int index) {
		if(index < 0 || index > getTabComponentsCount())
			throw new IllegalArgumentException("Index is out of range!");
		
		if(getTabComponentsCount() > 0) {
			m_tabbedPane.remove(index);
			m_tabComponentsCount--;
			if(getTabComponentsCount() == 1) {
        switchFromTabMode();
			}
		}
		else {
      switchToEmptyMode();
    }
		highlightCurrentTab();
	}

  /* Removes last component from jpanel and show empty/default jpanel. */
  private void switchToEmptyMode() {
    super.remove(0);
    m_singleComp = null;
    m_tabComponentsCount = 0;
  }

  /* Moves last single component from tab panel to jpanel. */
  private void switchFromTabMode() {
    m_singleComp = m_tabbedPane.getComponent(0);
    m_tabbedPane.add(m_tabDummyPanel, m_tabbedPane.getTitleAt(0));
    m_tabbedPane.remove(0);
    remove(m_tabbedPane);
    super.add(m_singleComp, BorderLayout.CENTER);
  }

  /* Moves single component from jpanel to tabbed panel. */
  private void switchToTabMode() {
    if(super.getComponentCount() > 0)
      super.remove(0);
    super.add(m_tabbedPane, BorderLayout.CENTER);
    m_tabbedPane.insertTab(m_tabbedPane.getTitleAt(0), null, m_singleComp, null, 0);
    m_singleComp = null;
    m_tabbedPane.remove(m_tabDummyPanel);
  }

  /** Removes all tabs from this panel. */
	public void removeAll() {
		m_tabbedPane.removeAll();
//		m_tabComponentsCount = 0;
		m_singleComp = new JPanel();
		super.add(m_singleComp);
    switchToEmptyMode();
	}

	/**
	 * Returns the number of added components.
	 * Despite its name, this method has not the target to override getComponentCount()
	 */
	public int getTabComponentsCount() {
		return m_tabComponentsCount;
	}
	
	/**
	 * Returns if this TabbedPanel is in the single component view or in the tab view.
	 */
	public boolean hasMultipleTabs() {
		return getTabComponentsCount() > 1;
	}

	/**
	 * Returns the "real" tabbed pane. Can be null.
	 */
	public JTabbedPane getTabbedPane() {
		return m_tabbedPane;
	}
}

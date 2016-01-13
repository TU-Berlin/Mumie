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

package net.mumie.mathletfactory.util.exercise;

import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.mumie.mathletfactory.appletskeleton.util.ControlPanel;

/**
 * This class helps to manage selected elements and to perform validations.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public	class SelectionHelper {
		
//		private final String[] ABC = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"};
		private boolean m_singleSelectionInSubtask, m_singleSelectionInTask;
		private Vector selectedTaskPaths[];
		private int m_maxEntries[], m_minEntries[];
		private JPanel m_subtaskPane;
		private ControlPanel[] m_subtaskPanels;
		private int m_currentSubtask = 1;
		private int m_nrOfSubtasks;
		
		/**
		 * Creates a new instance with the given number of subtasks and settings for element selection.
		 * 
		 * @param nrOfSubtasks the number of subtasks of this exercise
		 * @param singleSelectionInSubtask flag indicating that an element can only be added once in a subtask
		 * @param singleSelectionInTask flag indicating that an element can only be added once in this exercise
		 */
		public SelectionHelper(int nrOfSubtasks, boolean singleSelectionInSubtask, boolean singleSelectionInTask) {
			m_nrOfSubtasks = nrOfSubtasks;
			selectedTaskPaths = new Vector[nrOfSubtasks];
			m_maxEntries = new int[m_nrOfSubtasks];
			m_minEntries = new int[m_nrOfSubtasks];
			m_subtaskPanels = new ControlPanel[nrOfSubtasks];
			m_subtaskPane = new JPanel();
			for(int i = 0; i < nrOfSubtasks; i++) {
				selectedTaskPaths[i] = new Vector();
				m_maxEntries[i] = -1;
				m_minEntries[i] = -1;
				m_subtaskPanels[i] = new ControlPanel();
				m_subtaskPane.add(m_subtaskPanels[i]);
				m_subtaskPanels[i].setVisible(false);
			}
			m_subtaskPanels[0].setVisible(true);
			m_singleSelectionInSubtask = singleSelectionInSubtask;
			m_singleSelectionInTask = singleSelectionInTask;
//	    m_subtaskPane.getTabbedPane().addMouseListener(new MouseAdapter() {
//	      public void mouseClicked(MouseEvent e) {
//	      	m_currentSubtask = m_subtaskPane.getTabbedPane().getSelectedIndex()+1;
//	      }
//	    });
		}
		
		/**
		 * Returns if the given path is already added to given subtask.
		 */
		public boolean containsPath(int subtask, String path) {
			for(int i = 0; i < selectedTaskPaths[subtask-1].size(); i++) {
				if(selectedTaskPaths[subtask-1].get(i).equals(path)) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * Loads the stored user answers beginning with "selected_".
		 * This method uses the methods {@link MultipleTasksIF#selectTask(int)} and 
		 * {@link SelectableDataIF#selectElement(String)} which must be implemented in the applet.
		 */
		public void loadUserAnswers(MumieExerciseIF mathlet, MumieExercise exercise) {
			MultipleTasksIF mm = null;
			SelectableDataIF sm = null;
	    if(mathlet instanceof MultipleTasksIF)
	      mm = (MultipleTasksIF) mathlet;
	    if(mathlet instanceof SelectableDataIF)
	      sm = (SelectableDataIF) mathlet;
	    if(mm == null || sm == null)
	    	throw new IllegalArgumentException("Mathlet must implement the interfaces MultipleTasksIF and SelectableDataIF !");

			for(int t = 1; t <= m_nrOfSubtasks; t++) {
				mm.selectTask(t);
				int i = 1;
				while(exercise.userElementExists(t, "selected_" + i)) {
					sm.selectElement(exercise.getUserString(t, "selected_" + i));
					i++;
				}
			}
			mm.selectTask(1);
		}
		
		/**
		 * Clears all selected paths in the given subtask (indexing starts with 1).
		 * @param subtask index of subtask (indexing starts with 1)
		 */
		public void clearSubtask(int subtask) {
			selectedTaskPaths[subtask-1].clear();
		}
		
		/**
		 * Clears all selected paths in the current subtask.
		 */
		public void clearCurrentSubtask() {
			selectedTaskPaths[getCurrentSubtask()-1].clear();
		}
		
		/**
		 * Sets the maximum amount of entries that can be selected.
		 * Default is <code>-1</code> (no maximum).
		 * @param subtask index of subtask (indexing starts with 1)
		 */
		public void setMaximumEntries(int subtask, int maxEntries) {
			m_maxEntries[subtask-1] = maxEntries;
		}
		
		/**
		 * Sets the minimum amount of entries that can be selected.
		 * Default is <code>-1</code> (no minimum).
		 * @param subtask index of subtask (indexing starts with 1)
		 */
		public void setMinimumEntries(int subtask, int minEntries) {
			m_minEntries[subtask-1] = minEntries;
		}
		
		/**
		 * Returns the selected paths for the given subtask.
		 * @param subtask index of subtask (indexing starts with 1)
		 */
		public String[] getSelectedPaths(int subtask) {
			String[] paths = new String[selectedTaskPaths[subtask-1].size()];
			for(int i = 0; i < selectedTaskPaths[subtask-1].size(); i++) {
				paths[i] = (String) selectedTaskPaths[subtask-1].get(i);
			}
			return paths;
		}
		
		/**
		 * Returns the main panel with tabs for every subtask.
		 * @see #getSubtaskPanel(int)
		 */
		public JPanel getMainPanel() {
			return m_subtaskPane;
		}
		
		/**
		 * Returns the (Control-)Panel for the given subtask.
		 * @param subtask index of subtask (indexing starts with 1)
		 */
		public ControlPanel getSubtaskPanel(int subtask) {
			return m_subtaskPanels[subtask-1];
		}
		
		/**
		 * Returns if the answers should be saved even if not enough elements were added.
		 * In this case the user will be asked for saving.
		 */
		public boolean sendIncomplete() {
			boolean complete = true;
			for(int i = 1; i <= m_nrOfSubtasks; i++) {
				if(!hasMinimumEntries(i))
					complete = false;
			}
			if(!complete) {
				int answer = JOptionPane.showOptionDialog(null, "Es sind nicht gen�gend Element hinzugef�gt worden.\nTrotzdem speichern?", "Unvollst�ndige Antworten", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				if(answer == JOptionPane.YES_OPTION)
					return true;
				else
					return false;
			}
			return true;
		}
		
		public void showFailureMessage() {
			JOptionPane.showMessageDialog(null, "<html><b>Dieses Element wurde bereits hinzugef&uuml;gt!");
		}
		
		/**
		 * Checks and returns if the given path can be added and, if so, adds it.
		 */
		public boolean selectElement(String path) {
			if(!checkSelection(path) || hasMaximumEntries(getCurrentSubtask()))
				return false;
			selectedTaskPaths[m_currentSubtask-1].add(path);
			return true;
		}
		
		/**
		 * Returns if this path is valid and can be added.
		 */
		private boolean checkSelection(String path) {
			if(m_singleSelectionInTask) { // strongest of both attributes, check all subtasks
				for(int i = 1; i <= m_nrOfSubtasks; i++) {
					if(containsPath(i, path))
						return false;
				}
			} else if(m_singleSelectionInSubtask) { // check only current subtask
				if(containsPath(m_currentSubtask, path))
					return false;
			}
			return true;
		}
		
		/**
		 * Returns if the maximum amount of entries is reached.
		 * @param subtask index of subtask (indexing starts with 1)
		 */
		public boolean hasMaximumEntries(int subtask) {
			if(m_maxEntries[subtask-1] == -1)
				return false;
			if(selectedTaskPaths[subtask-1].size() >= m_maxEntries[subtask-1])
					return true;
			return false;
		}
		
		/**
		 * Returns if the minimum amount of entries is reached.
		 * @param subtask index of subtask (indexing starts with 1)
		 */
		public boolean hasMinimumEntries(int subtask) {
			if(m_minEntries[subtask-1] == -1)
				return true;
			if(selectedTaskPaths[subtask-1].size() >= m_minEntries[subtask-1])
					return true;
			return false;
		}
		
		/**
		 * Checks if the given subtask index is valid.
		 */
		private boolean checkSubtask(int subtask) {
			return subtask > 0 && subtask <= m_nrOfSubtasks;
		}
		
		/**
		 * Checks and returns if the given subtask can be chooses and, if so, does it.
		 * @param subtask index of subtask (indexing starts with 1)
		 */
		public boolean selectSubtask(int subtask) {
			if(!checkSubtask(subtask))
				return false;
			m_subtaskPanels[m_currentSubtask-1].setVisible(false);
			m_currentSubtask = subtask;
			m_subtaskPanels[m_currentSubtask-1].setVisible(true);
//			m_subtaskPane.setSelectedIndex(m_currentSubtask-1);
			return true;
		}
		
		/**
		 * Returns the index of the current subtask (indexing starts with 1)
		 */
		public int getCurrentSubtask() {
			return m_currentSubtask;
		}
	}

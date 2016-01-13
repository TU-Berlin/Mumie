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

package net.mumie.mathletfactory.util.animation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.display.layout.MatrixLayout;
import net.mumie.mathletfactory.util.ResourceManager;

/**
 * This class is used to generate an animation. An animation is constructed
 * through {@link net.mumie.mathletfactory.util.animation.Step Steps}. 
 * @author Gronau
 * @mm.docstatus finished
 */
public class Animation {

	private final static String START_BUTTON_ICON_SRC =
		"/resource/icon/animation/Play16.GIF";

	private final static String STOP_BUTTON_ICON_SRC =
		"/resource/icon/animation/Stop16.GIF";

	private final static String PAUSE_BUTTON_ICON_SRC =
		"/resource/icon/animation/Pause16.GIF";

	private final static String BACK_BUTTON_ICON_SRC =
		"/resource/icon/animation/StepBack16.GIF";

	private final static String FORWARD_BUTTON_ICON_SRC =
		"/resource/icon/animation/StepForward16.GIF";

	private final static Icon START_BUTTON_ICON =
		new ImageIcon(Animation.class.getResource(START_BUTTON_ICON_SRC));

//	private final static Icon STOP_BUTTON_ICON =
//		new ImageIcon(Animation.class.getResource(STOP_BUTTON_ICON_SRC));

	private final static Icon PAUSE_BUTTON_ICON =
		new ImageIcon(Animation.class.getResource(PAUSE_BUTTON_ICON_SRC));

//	private final static Icon BACK_BUTTON_ICON =
//		new ImageIcon(Animation.class.getResource(BACK_BUTTON_ICON_SRC));

//	private final static Icon FORWARD_BUTTON_ICON =
//		new ImageIcon(Animation.class.getResource(FORWARD_BUTTON_ICON_SRC));

	protected Vector m_componentsToRedraw = new Vector();

	protected Vector m_steps = new Vector();

	protected JPanel m_animationPanel, m_buttonPanel, m_statusLabelPanel;

	protected JPanel m_optionsPanel;

	protected JTabbedPane m_tabbedPane;

	protected JButton m_startPauseButton,
		m_stopButton,
		m_backButton,
		m_forwardButton;

	protected JLabel m_descriptionLabel, m_statusLabel;

	protected JRadioButton m_automaticButton, m_manuellButton;

	protected ButtonGroup m_optionsGroup;

	protected JSlider m_speedSlider;

	protected long m_startTime, m_timePassed;

	protected Thread m_animationThread;

	protected boolean m_isRunning = false;
	
	protected boolean m_isPausing = false;

	protected boolean m_isStarted = false;

	protected boolean m_isFinished = false;

	protected boolean m_isFirstStart = true;

//	protected boolean m_first = true;
	
	protected boolean m_showBackForwardButtons = false;

	protected int m_currentStepIndex = 0;

	protected boolean m_isAutomatic = true;

	protected Progress m_progress = new Progress(0, 0);

	protected Object m_lock = new Object();

	public final static double USER_CLICK_TOLERANCE = 0.10;

	protected double m_speed = 1;

	class AnimationThread extends Thread {
	  
		public void run() {
		  int m_nrOfCalls = 0;
			while (m_isRunning) {
				while(!m_isPausing) {
					synchronized (m_lock) {
						if (m_isRunning) {
							long time = System.currentTimeMillis();
							m_timePassed = time - m_startTime;
							if(getCurrentStep().getCallCount() > 0) {
							  if(m_timePassed < m_nrOfCalls * getCurrentStep().getDuration()/getCurrentStep().getCallCount())
							    continue;
							}
							m_nrOfCalls++;
							m_progress.setProgressRate(
								(double)m_timePassed
									/ (getCurrentStep().getDuration() * getSpeed()));
							m_progress.setCallCount(m_nrOfCalls);
							setStatusLabel(m_currentStepIndex + 1, m_progress.getProgressRate());
							if (m_progress.getProgressRate() >= 1) {
								// step finished
								if (m_isAutomatic) {
									// automatic
									getStep(m_currentStepIndex).end();
									redrawComponents();
									if (isAtLastStep()) {
										//                                      step finished, last step, automatic
										m_isFinished = true;
										m_nrOfCalls = 0;
										Animation.this.finish();
//										return;
									} else {
										// step finished, not last step, automatic
									  m_nrOfCalls = 0;
										stepForward();
									}
								} else {
									// manuell
									pause();
									getCurrentStep().end();
									redrawComponents();
								}
							} else {
								// step not finished
//								getCurrentStep().proceed(m_progress, m_first);
							  getCurrentStep().updateDependencies(m_progress);
								getCurrentStep().proceed(m_progress);
								redrawComponents();
								m_progress.setFirstCall(false);
							}
						}
					}
				}
			}
			log("Thread end");
		}
	}

	/**
	 * Creates a new animation.
	 */
	public Animation() {
		this(new JComponent[] {});
	}

	/**
	 * Creates a new animation with a redraw component.
	 */
	public Animation(JComponent component) {
		this(new JComponent[] { component });
	}

	/**
	 * Creates a new animation with an array of redraw components.
	 */
	public Animation(JComponent[] components) {
		for (int i = 0; i < components.length; i++) {
			m_componentsToRedraw.add(components[i]);
		}
		createAnimationPanel();
		setShowBackForwardButtons(m_showBackForwardButtons);
		m_descriptionLabel.setText("<html><br><br>");
	}

	/**
	 * Adds a redraw component.
	 */
	public void addRedrawComponent(JComponent c) {
		m_componentsToRedraw.add(c);
	}

	/**
	 * Removes the specified redraw component.
	 */
	public void removeRedrawComponent(JComponent c) {
		removeRedrawComponent(m_componentsToRedraw.indexOf(c));
	}

	/**
	 * Removes the redraw component with the specified index.
	 */
	public void removeRedrawComponent(int index) {
		m_componentsToRedraw.remove(index);
	}

	/**
	 * Redraws the added components.
	 */
	public void redrawComponents() {
		for (int i = 0; i < m_componentsToRedraw.size(); i++) {
			if (m_componentsToRedraw.get(i) instanceof MMCanvas) {
				MMCanvas canvas = (MMCanvas)m_componentsToRedraw.get(i);
				canvas.renderScene();
				canvas.repaint();
			} else {
				JComponent comp = (JComponent)m_componentsToRedraw.get(i);
				comp.revalidate();
			}
		}
	}

	private void createAnimationPanel() {
		m_startPauseButton =
			new JButton(
				new ImageIcon(Animation.class.getResource(START_BUTTON_ICON_SRC)));
		m_startPauseButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (m_isRunning && m_isPausing)
					resume();
				else if(m_isRunning)
					pause();
				else
					start();
			}
		});
		m_stopButton =
			new JButton(
				new ImageIcon(Animation.class.getResource(STOP_BUTTON_ICON_SRC)));
		m_stopButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});
		m_backButton =
			new JButton(
				new ImageIcon(Animation.class.getResource(BACK_BUTTON_ICON_SRC)));
		m_backButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				stepBack();
			}
		});
		m_forwardButton =
			new JButton(
				new ImageIcon(Animation.class.getResource(FORWARD_BUTTON_ICON_SRC)));
		m_forwardButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				stepForward();
			}
		});
		m_buttonPanel = new JPanel();
		m_buttonPanel.add(m_backButton);
		m_buttonPanel.add(m_startPauseButton);
		m_buttonPanel.add(m_stopButton);
		m_buttonPanel.add(m_forwardButton);
		Box cbPanel = new Box(BoxLayout.X_AXIS);
		m_automaticButton =
			new JRadioButton(
				ResourceManager.getMessage("animation.automatic"),
				true);
		m_manuellButton =
			new JRadioButton(
				ResourceManager.getMessage("animation.manuell"),
				false);
		ActionListener radioListener = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == m_automaticButton)
					setAutomatic(true);
				else
					setAutomatic(false);
			}
		};
		m_automaticButton.addActionListener(radioListener);
		m_manuellButton.addActionListener(radioListener);
		m_optionsGroup = new ButtonGroup();
		m_optionsGroup.add(m_automaticButton);
		m_optionsGroup.add(m_manuellButton);
		cbPanel.add(m_automaticButton);
		cbPanel.add(m_manuellButton);
		m_speedSlider = new JSlider(JSlider.HORIZONTAL, -5, 5, 0);
		Hashtable sliderTable = new Hashtable();
		sliderTable.put(
			new Integer(-3),
			new JLabel(ResourceManager.getMessage("animation.slower")));
		sliderTable.put(
			new Integer(3),
			new JLabel(ResourceManager.getMessage("animation.faster")));
		m_speedSlider.setLabelTable(sliderTable);
		m_speedSlider.setPaintLabels(true);
		m_speedSlider.setMinorTickSpacing(1);
		m_speedSlider.setMajorTickSpacing(5);
		m_speedSlider.setPaintTicks(true);
		m_speedSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				synchronized (m_lock) {
					if (m_speedSlider.getValue() < 0) {
						m_speed = Math.abs(m_speedSlider.getValue());
					} else if (m_speedSlider.getValue() > 0)
						m_speed = 1 / (double)m_speedSlider.getValue();
					else
						m_speed = 1;

					long timePassed =
						(long) (m_progress.getProgressRate() * getCurrentStep().getDuration() * getSpeed());
					long now = System.currentTimeMillis();
					m_startTime = now - timePassed;
				}
			}
		});
		m_optionsPanel = new JPanel(new MatrixLayout(2, 2));
		m_optionsPanel.setBorder(BorderFactory.createEtchedBorder());
		m_optionsPanel.add(
			new JLabel(ResourceManager.getMessage("animation.playmode")));
		m_optionsPanel.add(cbPanel);
		m_optionsPanel.add(
			new JLabel(ResourceManager.getMessage("animation.speed")));
		m_optionsPanel.add(m_speedSlider);
		m_descriptionLabel = new JLabel();
		m_animationPanel = new JPanel();
//		m_descriptionLabel.setBorder(BorderFactory.createEtchedBorder());
		m_animationPanel.setBorder(BorderFactory.createEtchedBorder());
		BoxLayout box = new BoxLayout(m_animationPanel, BoxLayout.Y_AXIS);
		m_animationPanel.setLayout(box);
		m_tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		m_tabbedPane.add(
			ResourceManager.getMessage("animation.description"),
			m_descriptionLabel);
		m_tabbedPane.add(
			ResourceManager.getMessage("animation.options"),
			m_optionsPanel);
		m_statusLabelPanel = new JPanel();
		m_statusLabel =
			new JLabel(
				ResourceManager.getMessage("animation.description.initial"));
		m_statusLabelPanel.add(m_statusLabel);
		m_animationPanel.add(m_tabbedPane); //m_textPanel
		m_animationPanel.add(m_statusLabelPanel);
		m_animationPanel.add(m_buttonPanel);
		m_animationThread = new AnimationThread();
	}

	private void setButtons() {
		if (m_steps.isEmpty()) {
			// if no step was added, all buttons are disabled
			m_startPauseButton.setEnabled(false);
			m_stopButton.setEnabled(false);
			m_backButton.setEnabled(false);
			m_forwardButton.setEnabled(false);
			return;
		}
		// m_isRunning is always true if m_isStarted is true!
		//
		// play/pause button's icon depending on playing or not (i.e.
		// pausing or stopped)
		if (m_isRunning)
			m_startPauseButton.setIcon(PAUSE_BUTTON_ICON);
		else
			m_startPauseButton.setIcon(START_BUTTON_ICON);
		// play/pause button's state if animation at the end
		m_startPauseButton.setEnabled(!hasAnimationFinished());
		// stop button's state
		m_stopButton.setEnabled(m_isStarted || hasAnimationFinished());
		// forward button's state
		m_forwardButton.setEnabled(!isAtLastStep());
		// back button's state
		m_backButton.setEnabled(true); //!isAtFirstStep()
	}

	/**
	 * Starts the animation.
	 */
	public void start() {
		synchronized (m_lock) {
			if (m_isRunning && m_isPausing) {
				resume();
			} else {
				log("start called");
				// first start of animation
				//                m_currentStepIndex = 0;
			  m_progress.setFirstCall(true);
				m_isStarted = true;
				m_isRunning = true;
				m_isPausing = false;
				getCurrentStep().setDependenciesEnabled(true);
				m_startTime = System.currentTimeMillis();
//				if (m_isFirstStart) {
//					System.out.println("First start");
					m_isFirstStart = false;
					m_animationThread.start();
//				}
			}
			setButtons();
			m_backButton.setEnabled(true);
		}
	}
	
	/**
	 * Resumes the animation.
	 */
	public void resume() {
		synchronized (m_lock) {
			if(!(m_isRunning && m_isPausing))
				return;
			log("resume called");
			if (m_progress.isFirstCall())
				m_startTime = System.currentTimeMillis();
			else
				m_startTime = System.currentTimeMillis() - m_timePassed;
			m_isPausing = false;
			log("pausing ended");
		}
	}

	/**
	 * Pauses the animation.
	 */
	public void pause() {
		synchronized (m_lock) {
			log("pause called");
			m_isPausing = !m_isPausing;
			setButtons();
		}
	}
	
	protected void finish() {
		synchronized (m_lock) {
			m_isRunning = false;
			m_isPausing = true;
			m_isStarted = false;
			setButtons();
			m_isFinished = false;
		}	  
	}

	/**
	 * Stops immediatly the animation and restores its initial state by calling
	 * backwards {@link Step#prepareForPreviousStep()} on each step.
	 * Does nothing if the animation is not playing or pausing.
	 *  
	 */
	public void stop() {
		if( !(isPlaying() || isPausing()))
			return;
		synchronized (m_lock) {
			log("stop called");
			m_isRunning = false;
			m_isPausing = true; //needed to stop thread
			m_isStarted = false;
			m_animationThread = null;
//			if (hasAnimationFinished()) {
//				setButtons();
//				m_isFinished = false;
//				return;
//			}
			m_isFinished = false;
			m_progress.setFirstCall(true);
			m_progress.setProgressRate(1);
			m_descriptionLabel.setText(null);
			m_statusLabel.setText(
				ResourceManager.getMessage("animation.description.initial"));
			for (int i = m_currentStepIndex; i >= 0; i--) {
				((Step)m_steps.get(i)).prepareForPreviousStep();
			}
			((Step)m_steps.get(0)).begin();
			redrawComponents();
			m_currentStepIndex = 0;
			m_animationThread = new AnimationThread();
			log("Thread created");
			setButtons();
		}
	}

	/**
	 * Moves the animation to the previous step (if it exists, i.e. if this step
	 * is not the first one).
	 */
	public void stepBack() {
		synchronized (m_lock) {
			log("step back called");
			if ((m_progress.isFirstCall() || m_progress.getProgressRate() <= USER_CLICK_TOLERANCE)
				&& m_currentStepIndex > 0) {
				getCurrentStep().prepareForPreviousStep();
				getCurrentStep().setDependenciesEnabled(false);
				m_currentStepIndex--;
				getCurrentStep().setDependenciesEnabled(true);
				getCurrentStep().begin();
			} else {
			  m_progress.setFirstCall(true);
				m_startTime = System.currentTimeMillis();
				m_progress.setProgressRate(0);
				getCurrentStep().prepareForPreviousStep();
				getCurrentStep().begin();
			}
			redrawComponents();
			setButtons();
		}
	}

	/**
	 * Moves the animation to the next step (if it exists, i.e. if this step is
	 * not the last one).
	 */
	public void stepForward() {
		synchronized (m_lock) {
			log("step forward called");
			if (isAtLastStep())
				return;
			if (!isAutomatic()) {
				if (hasCurrentStepFinished()) {
					// manuell, step finished
					getCurrentStep().prepareForNextStep();
					getCurrentStep().setDependenciesEnabled(false);
					m_currentStepIndex++;
					m_progress.setProgressRate(0);
					setButtons();
					m_progress.setFirstCall(true);
					getCurrentStep().setDependenciesEnabled(true);
					getCurrentStep().begin();
					redrawComponents();
					setStatusLabel(m_currentStepIndex + 1, m_progress.getProgressRate());
				} else {
					//                  manuell, step not yet finished
					pause();
					m_progress.setProgressRate(1);
					getCurrentStep().proceed(m_progress);
					getCurrentStep().end();
					redrawComponents();
					setStatusLabel(m_currentStepIndex + 1, m_progress.getProgressRate());
				}
			} else {
				// automatic
				if (!hasCurrentStepFinished()) {
					getCurrentStep().proceed(new Progress(1, getCurrentStep().getCallCount()));
					redrawComponents();
					getCurrentStep().end();
					redrawComponents();
				}
				getCurrentStep().prepareForNextStep();
				getCurrentStep().setDependenciesEnabled(false);
				m_currentStepIndex++;
				m_progress.setFirstCall(true);
				getCurrentStep().setDependenciesEnabled(true);
				getCurrentStep().begin();
				redrawComponents();
				m_startTime = System.currentTimeMillis();
				setButtons();
			}
		}
	}

	private void setStatusLabel(int step, double progress) {
		setStatusText(
			ResourceManager.getMessage("animation.progress")
				+ (int) (progress * 100)
				+ "%  --  "
				+ ResourceManager.getMessage("animation.step")
				+ ""
				+ (step)
				+ "/"
				+ m_steps.size());
	}

	/**
	 * Adds a step to the end of the step's list.
	 */
	public void addStep(Step step) {
		m_steps.add(step);
	}

	/**
	 * Builds the animation. Must be called after all adding and removing of
	 * steps has been done.
	 */
	public void initialize() {
		setButtons();
		if (m_steps.isEmpty())
			return;
		getStep(0).begin();
		//		getStep(0).proceed(0, true);
		redrawComponents();
	}
	
	/**
	 * Returns the lock object used to synchronize actions with the
	 * animation thread.
	 */
	public Object getLockObject() {
		return m_lock;
	}

	/**
	 * Sets the description text in the animation panel. HTML commands are
	 * allowed.
	 */
	public void setCurrentDescription(String text) {
		if (text.indexOf("<html>") > -1)
			m_descriptionLabel.setText(text);
		else
			m_descriptionLabel.setText("<html>" + text);
	}

	/**
	 * Sets the status text for the animation.
	 */
	public void setStatusText(String text) {
		m_statusLabel.setText(text);
	}
	
	public void setEnabled(boolean enabled) {
		m_statusLabel.setEnabled(enabled);
		m_startPauseButton.setEnabled(enabled);
		m_stopButton.setEnabled(enabled);
		m_backButton.setEnabled(enabled);
		m_forwardButton.setEnabled(enabled);
	}

	/**
	 * Returns the number of steps.
	 */
	public int getStepsCount() {
		return m_steps.size();
	}

	public JComponent getAnimationPanel() {
		return m_animationPanel;
	}

	/**
	 * Returns the panel with the animation's control buttons.
	 */
	public JComponent getButtonPanel() {
		return m_buttonPanel;
	}
	
	/**
	 * Returns the label with the current description text.
	 */
	public JLabel getDescriptionLabel() {
		return m_descriptionLabel;
	}
	
	/**
	 * Returns the panel with the animation's options.
	 */
	public JComponent getOptionsPanel() {
		return m_optionsPanel;
	}

	/**
	 * Returns the speed (control) slider.
	 */
	public JSlider getSpeedSlider() {
		return m_speedSlider;
	}
	
	/**
	 * Returns the step with index <code>index</index> of this animation.
	 */
	public Step getStep(int index) {
		if (m_steps.size() > 0 && index >= 0 && index < m_steps.size())
			return (Step)m_steps.get(index);
		else
			return null;
	}

	/**
	 * Sets the step with index <code>index</index> to <code>step</code>.
	 * Note that this method does not behave like {@link #addStep(Step)}!
	 * {@link #initialize()} must be called to rebuild the animation.
	 */
	public void setStep(int index, Step step) {
		m_steps.set(index, step);
	}

	/**
	 * Removes the step with index <code>index</index> from this animation.
	 * {@link #initialize()} must be called to rebuild the animation.
	 */
	public void removeStep(int index) {
		m_steps.remove(index);
	}

	/**
	 * Removes all steps from this animation. {@link #initialize()}must be
	 * called to rebuild the animation.
	 */
	public void removeAllSteps() {
		m_steps.removeAllElements();
	}

	/**
	 * Returns the current step.
	 */
	public Step getCurrentStep() {
		return getStep(m_currentStepIndex);
	}

	/**
	 * Returns if the animation is playing.
	 */
	public boolean isPlaying() {
		return m_isRunning;
	}

	/**
	 * Returns if the animation is pausing.
	 */
	public boolean isPausing() {
		return m_isPausing;
	}

	/**
	 * Returns if the animation is stopped.
	 */
	public boolean isStopped() {
		return !m_isStarted;
	}

	/**
	 * Returns the first step.
	 */
	public Step getFirstStep() {
		return getStep(0);
	}

	/**
	 * Returns the last step.
	 */
	public Step getLastStep() {
		return getStep(m_steps.size() - 1);
	}

	/**
	 * Returns if the animation is currently at the first step.
	 */
	public boolean isAtFirstStep() {
		return getCurrentStep() == getFirstStep();
	}

	/**
	 * Returns if the animation is currently at the last step.
	 */
	public boolean isAtLastStep() {
		return getCurrentStep() == getLastStep();
	}

	/**
	 * Returns the current progress containing informations about elapsed time and steps.
	 */
	public Progress getProgress() {
		return m_progress;
	}

	/**
	 * Returns if the current step has been finished.
	 */
	public boolean hasCurrentStepFinished() {
		return getProgress().getProgressRate() >= 1;
	}

	/**
	 * Returns if the complete animation (i.e. all steps) has finished.
	 */
	public boolean hasAnimationFinished() {
		return m_isFinished;
	}

	/**
	 * Returns if the next step should automatically start after finishing the last step.
	 */
	public boolean isAutomatic() {
		return m_isAutomatic;
	}

	/**
	 * Sets if the next step should automatically start after finishing the last step.
	 */
	public void setAutomatic(boolean isAutomatic) {
		m_isAutomatic = isAutomatic;
	}

	/**
	 * Returns the current speed factor.
	 */
	public double getSpeed() {
		return m_speed;
	}
	/**
	 * Returns true if the Back- and Forward-buttons are visible.
	 */
	public boolean isShowBackForwardButtons() {
		return m_showBackForwardButtons;
	}

	/**
	 * Sets the visibility of the Back- and Forward-button.
	 */
	public void setShowBackForwardButtons(boolean show) {
		m_showBackForwardButtons = show;
		m_backButton.setVisible(show);
		m_forwardButton.setVisible(show);
	}

	private void log(String message) {
//		System.out.println(message);
	}
}

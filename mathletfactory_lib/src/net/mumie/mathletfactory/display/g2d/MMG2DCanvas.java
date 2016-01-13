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

package net.mumie.mathletfactory.display.g2d;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.mumie.mathletfactory.action.handler.global.GlobalMousePopupHandler;
import net.mumie.mathletfactory.action.handler.global.g2d.GlobalMouseDisplay2DCoordinatesHandler;
import net.mumie.mathletfactory.action.handler.global.g2d.GlobalMouseWheelZoomHandler;
import net.mumie.mathletfactory.action.handler.global.g2d.GlobalMouseZoomInHandler;
import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.display.DrawingBoardIF;
import net.mumie.mathletfactory.display.MM2DCanvas;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.transformer.GeneralTransformer;
import net.mumie.mathletfactory.util.CanvasMessage;
import net.mumie.mathletfactory.util.Graphics2DUtils;
import net.mumie.mathletfactory.util.ResourceManager;

/**
 * This class is used to render 2 dimensional
 * {@link net.mumie.mathletfactory.mmobject MMObjectIF MMObject}s on a
 * {@link java.awt.Canvas} using the Java 2D API.
 *
 * @author Amsel, vossbeck
 * @mm.docstatus finished
 */
public class MMG2DCanvas extends MM2DCanvas {

  private Graphics2D m_currentGraphic = null;

	private JPanel m_drawingBoard;

	protected CanvasMessage m_fpsMessage;

	protected boolean m_enableAntialiasing = true;
	protected boolean m_enableFastRendering = false;

  public MMG2DCanvas() {
  	this(NO_TOOLBAR); // by default, toolbar should not be visible!
  }

	public MMG2DCanvas(int toolFlags) {
		super();
		if((toolFlags & NO_TOOLBAR) != 0
				&& MumieTheme.getThemeProperty("Canvas.toolbar.visible", "false").equals("true")) {
			if( MumieTheme.getThemeProperty("Canvas.toolbar.alignment", "horizontal").equals("horizontal"))
				toolFlags |= HORIZONTAL_TOOLBAR;
			else if( MumieTheme.getThemeProperty("Canvas.toolbar.alignment", "horizontal").equals("vertical"))
				toolFlags |= VERTICAL_TOOLBAR;
		}
		else
			toolFlags |= NO_TOOLBAR;


		m_toolFlags = toolFlags | SCROLL_BUTTONS;
		createComponents();
		layoutComponents();
		new GlobalMouseDisplay2DCoordinatesHandler(this);
		new GlobalMousePopupHandler(this);
		new GlobalMouseZoomInHandler(this);
		new GlobalMouseWheelZoomHandler(this);
		m_fpsMessage = new CanvasMessage(this);
		addMessage(m_fpsMessage);
	}

	protected void createComponents() {
		m_toolBar.setFloatable(false);
		m_toolBar.setFocusable(false);
    m_toolBar.setVisible((m_toolFlags &  (HORIZONTAL_TOOLBAR | VERTICAL_TOOLBAR)) != 0);
    		//&& (m_toolFlags & (HORIZONTAL_TOOLBAR | VERTICAL_TOOLBAR)) != 0);
    if (Graphics2DUtils.isJMFAvailable() && (m_toolFlags & VERTICAL_TOOLBAR) == 0 && false) {
      try {
        Class clazz = Class.forName("net.mumie.mathletfactory.util.JMFHelper");
        Method method = clazz.getDeclaredMethod("createRecordAction", new Class[] { Component.class });
        Action action = (Action)method.invoke(null, new Object[] {getDrawingBoard()});
        JButton record = createToolbarButton(action, action.isEnabled());
        method = clazz.getDeclaredMethod("createStopAction", new Class[] { Action.class });
        action = (Action)method.invoke(null, new Object[] {action});
        JButton stop = createToolbarButton(action, action.isEnabled());
        m_toolBar.addSeparator();
      } catch (Exception e) {
        Logger.getLogger("").severe(e.getMessage());
        //doesn't matter
      }
    }
    if ((m_toolFlags & VERTICAL_TOOLBAR) == 0) {
      final JButton btnPrev =
        createToolbarButton(new AbstractAction(
          "",
          new ImageIcon(getClass().getResource("/resource/icon/canvas/Back24.gif"))) {
        public void actionPerformed(ActionEvent e) {
          getWorld2Screen().getHistory().prev();
          getWorld2Screen().setFromHistory();
          updateCanvas();
        }
      }, true);
      btnPrev.setToolTipText(ResourceManager.getMessage("hist_backward"));
      final JButton btnNext =
        createToolbarButton(new AbstractAction(
          "",
          new ImageIcon(getClass().getResource("/resource/icon/canvas/Forward24.gif"))) {
        public void actionPerformed(ActionEvent e) {
          getWorld2Screen().getHistory().next();
          getWorld2Screen().setFromHistory();
          updateCanvas();
        }
      }, true);
      btnNext.setToolTipText(ResourceManager.getMessage("hist_forward"));
      getWorld2Screen().getHistory().addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          btnPrev.setEnabled(!getWorld2Screen().getHistory().isFirst());
          btnNext.setEnabled(!getWorld2Screen().getHistory().isLast());
        }
      });

      m_toolBar.addSeparator();
    }
		final JButton btnZoomIn = createToolbarButton(new AbstractAction(
				"", new ImageIcon(getClass().getResource("/resource/icon/canvas/ZoomIn24.gif"))) {
			public void actionPerformed(ActionEvent e) {
				getWorld2Screen().rightScale(2);
        updateCanvas();
				getWorld2Screen().addSnapshotToHistory();
			}
		}, true);
    btnZoomIn.setToolTipText(ResourceManager.getMessage("zoom_in"));
    final JButton btnZoomInH = createToolbarButton(new AbstractAction(
        "",new ImageIcon(getClass().getResource("/resource/icon/canvas/ZoomIn24h.gif"))) {
      public void actionPerformed(ActionEvent e) {
        getWorld2Screen().rightScale(2, 1);
        updateCanvas();
        getWorld2Screen().addSnapshotToHistory();
      }
    }, true);
    btnZoomInH.setToolTipText(ResourceManager.getMessage("zoom_in_h"));
    final JButton btnZoomInV = createToolbarButton(new AbstractAction(
        "",new ImageIcon(getClass().getResource("/resource/icon/canvas/ZoomIn24v.gif"))) {
      public void actionPerformed(ActionEvent e) {
        getWorld2Screen().rightScale(1, 2);
        updateCanvas();
        getWorld2Screen().addSnapshotToHistory();
      }
    }, true);
    btnZoomInV.setToolTipText(ResourceManager.getMessage("zoom_in_v"));
    if ((m_toolFlags & VERTICAL_TOOLBAR) == 0)
      m_toolBar.addSeparator();

    final JButton btnZoomOut = createToolbarButton(new AbstractAction(
				"",new ImageIcon(getClass().getResource("/resource/icon/canvas/ZoomOut24.gif"))) {
			public void actionPerformed(ActionEvent e) {
				getWorld2Screen().rightScale(0.5);
        updateCanvas();
				getWorld2Screen().addSnapshotToHistory();
			}
		}, true);
    btnZoomOut.setToolTipText(ResourceManager.getMessage("zoom_out"));
    final JButton btnZoomOutH = createToolbarButton(new AbstractAction(
        "",new ImageIcon(getClass().getResource("/resource/icon/canvas/ZoomOut24h.gif"))) {
      public void actionPerformed(ActionEvent e) {
        getWorld2Screen().rightScale(0.5, 1);
        updateCanvas();
        getWorld2Screen().addSnapshotToHistory();
      }
    }, true);
    btnZoomOutH.setToolTipText(ResourceManager.getMessage("zoom_out_h"));
    final JButton btnZoomOutV = createToolbarButton(new AbstractAction(
        "",new ImageIcon(getClass().getResource("/resource/icon/canvas/ZoomOut24v.gif"))) {
      public void actionPerformed(ActionEvent e) {
        getWorld2Screen().rightScale(1, 0.5);
        updateCanvas();
        getWorld2Screen().addSnapshotToHistory();
      }
    }, true);
    btnZoomOutV.setToolTipText(ResourceManager.getMessage("zoom_out_v"));
    m_toolBar.addSeparator();
    if ((m_toolFlags & ROTATE_BUTTONS) != 0) {
      final JButton btnRotate = createToolbarButton(new AbstractAction(
        "",
        new ImageIcon(getClass().getResource("/resource/icon/canvas/rotate.png"))) {
        public void actionPerformed(ActionEvent e) {
          getWorld2Screen().leftRotate(Math.PI / 12.0);
          updateCanvas();
          getWorld2Screen().addSnapshotToHistory();
        }
      }, true);
      btnRotate.setToolTipText(ResourceManager.getMessage("rotate"));
      final JButton btnAntiRotate = createToolbarButton(new AbstractAction(
        "",
        new ImageIcon(getClass().getResource("/resource/icon/canvas/antirotate.png"))) {
        public void actionPerformed(ActionEvent e) {
          getWorld2Screen().leftRotate(-Math.PI / 12.0);
          updateCanvas();
          getWorld2Screen().addSnapshotToHistory();
        }
      }, true);
      btnAntiRotate.setToolTipText(ResourceManager.getMessage("rotate"));
    }
		m_btnScrollUp = createScrollButton(new AbstractAction(
        "", new ImageIcon(getClass().getResource("/resource/icon/canvas/up.png"))) {
      public void actionPerformed(ActionEvent e) {
        int inc = ((ExtendedButtonModel) m_btnScrollUp.getModel()).getIncrement();
        getWorld2Screen().leftTranslateY(5*inc);
        updateCanvas();
      }
    }, new Insets(5, 0, 0, 0), (m_toolFlags & SCROLL_BUTTONS) != 0);
    //m_btnScrollUp.setToolTipText(ResourceManager.getMessage("scroll_up"));
    m_btnScrollDown = createScrollButton(new AbstractAction(
        "", new ImageIcon(getClass().getResource("/resource/icon/canvas/down.png"))) {
      public void actionPerformed(ActionEvent e) {
        int inc = ((ExtendedButtonModel) m_btnScrollUp.getModel()).getIncrement();
        getWorld2Screen().leftTranslateY(-5*inc);
        updateCanvas();
      }
    }, new Insets(0, 0, 5, 0), (m_toolFlags & SCROLL_BUTTONS) != 0);
    //m_btnScrollDown.setToolTipText(ResourceManager.getMessage("scroll_down"));
    m_btnScrollLeft = createScrollButton(new AbstractAction(
        "", new ImageIcon(getClass().getResource("/resource/icon/canvas/left.png"))) {
      public void actionPerformed(ActionEvent e) {
        int inc = ((ExtendedButtonModel) m_btnScrollUp.getModel()).getIncrement();
        getWorld2Screen().leftTranslateX(5*inc);
        updateCanvas();
      }
    }, new Insets(0, 5, 0, 0), (m_toolFlags & SCROLL_BUTTONS) != 0);
    //m_btnScrollLeft.setToolTipText(ResourceManager.getMessage("scroll_left"));
    m_btnScrollRight = createScrollButton(new AbstractAction(
        "", new ImageIcon(getClass().getResource("/resource/icon/canvas/right.png"))) {
      public void actionPerformed(ActionEvent e) {
        int inc = ((ExtendedButtonModel) m_btnScrollUp.getModel()).getIncrement();
        getWorld2Screen().leftTranslateX(-5*inc);
        updateCanvas();
      }
    }, new Insets(0, 0, 0, 5), (m_toolFlags & SCROLL_BUTTONS) != 0);
    //m_btnScrollRight.setToolTipText(ResourceManager.getMessage("scroll_right"));
	}

	protected void layoutComponents() {
    setLayout(new BorderLayout());
    JPanel centerPane = new JPanel(new BorderLayout());
    centerPane.add(m_btnScrollUp, BorderLayout.NORTH);
    centerPane.add(m_btnScrollDown, BorderLayout.SOUTH);
    centerPane.add(m_btnScrollLeft, BorderLayout.WEST);
    centerPane.add(m_btnScrollRight, BorderLayout.EAST);
    centerPane.add(getDrawingBoard(), BorderLayout.CENTER);
    Box allBox, toolBox;
    if((m_toolFlags & VERTICAL_TOOLBAR) != 0){
      allBox = Box.createHorizontalBox();
      toolBox = Box.createVerticalBox();
      toolBox.add(m_toolBar);
      toolBox.add(Box.createVerticalGlue());
      m_toolBar.setOrientation(JToolBar.VERTICAL);
    } else { // per default toolBar is horizontal
      allBox = Box.createVerticalBox();
      toolBox = Box.createHorizontalBox();
      toolBox.add(m_toolBar);
      toolBox.add(Box.createHorizontalGlue());
    }
		allBox.add(toolBox);
    allBox.add(centerPane);
    add(allBox, BorderLayout.CENTER);
	}

	public Graphics2D getGraphics2D() {
		return m_currentGraphic;
	}

	protected void setGraphics2D(Graphics2D gr) {
		m_currentGraphic = gr;
		if (m_currentGraphic != null) {
		    Object value;
		    if(m_enableAntialiasing)
		        value = RenderingHints.VALUE_ANTIALIAS_ON;
		    else
		        value = RenderingHints.VALUE_ANTIALIAS_OFF;
		    m_currentGraphic.setRenderingHint(
		            RenderingHints.KEY_ANTIALIASING, value);
		    if(m_enableFastRendering)
		        value = RenderingHints.VALUE_RENDER_SPEED;
		    else
		        value = RenderingHints.VALUE_RENDER_QUALITY;
		    m_currentGraphic.setRenderingHint(
		            RenderingHints.KEY_RENDERING, value);
		}
	}

	public final int getScreenType() {
		return GeneralTransformer.ST_GRAPHICS2D;
	}

	/**
	 * For the Graphics2DCanvas the JPanel itself as the component on which
	 * the geom objects will be drawn is returned.
	 */
	public Component getDrawingBoard() {
		if (m_drawingBoard == null) {
			m_drawingBoard = new DrawingBoard();
		}
		return m_drawingBoard;
	}

	private class DrawingBoard extends JPanel implements DrawingBoardIF {

		public DrawingBoard() {
			super();
			setFocusTraversalKeysEnabled(false);
			setFocusable(true);
		}
		public void paintComponent(Graphics gr) {
			((Graphics2D) gr).setComposite(
				AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f));
			super.paintComponent(gr);
			gr.setColor(getBackground());
			gr.fillRect(0, 0, getWidth(), getHeight());
			gr.setColor(getForeground());

			if (m_calculateFPS)
				startFrameRateCounter();

			//if(getGraphics2D() == null)
			    setGraphics2D((Graphics2D) gr);

			if (!isSceneRendered())
				renderScene();
			drawScene();

			if (m_calculateFPS) {
				stopFrameRateCounter();
				m_fpsMessage.setMessage((int)getFrameRate() + " fps");
			}

		}
		
		public void drawFocus(boolean focus) {
			if (focus)
				setBorder(MMCanvas.FOCUSED_BORDER);
			else
				setBorder(MMCanvas.UNFOCUSED_BORDER);
		}
		
		public void updateUI() {
			super.updateUI();
			setBackground(MumieTheme.getCanvasBackground());
		}
	}

  public void addSnapShotToHistory() {
    getWorld2Screen().addSnapshotToHistory();
  }

	public void enableCalculateFPS(boolean aBoolean) {
		if(!aBoolean && isCalculateFPSEnabled())
			m_fpsMessage.setMessage(null);
		super.enableCalculateFPS(aBoolean);
	}

	public void enableAntialiasing(boolean enable) {
	    m_enableAntialiasing = enable;
	}

	public boolean isAntialiasingEnabled() {
	    return m_enableAntialiasing;
	}

	public void enableFastRendering(boolean enable) {
	    m_enableFastRendering = enable;
	}
}



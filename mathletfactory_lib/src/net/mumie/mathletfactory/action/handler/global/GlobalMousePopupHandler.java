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

package net.mumie.mathletfactory.action.handler.global;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;
import net.mumie.mathletfactory.util.ResourceManager;

/**
 * @author Markus Gronau
 *
 * This class is used to ...
 * 
 */
public class GlobalMousePopupHandler extends GlobalHandler {

	
	private JCheckBoxMenuItem m_setToolbarVisible = new JCheckBoxMenuItem(ResourceManager.getMessage("show_toolbar"));
	private JCheckBoxMenuItem m_setScrollButtonsVisible = new JCheckBoxMenuItem(ResourceManager.getMessage("show_scroll_Buttons"));
	private JCheckBoxMenuItem m_setShowFPS = new JCheckBoxMenuItem(ResourceManager.getMessage("show_frames_per_sec"));
	private JCheckBoxMenuItem m_setShowCoords = new JCheckBoxMenuItem(ResourceManager.getMessage("show_mouse_coords"));
	private JCheckBoxMenuItem m_setAntialiasing = new JCheckBoxMenuItem("Antialiasing");
	private JRadioButtonMenuItem m_setFastRendering = new JRadioButtonMenuItem(ResourceManager.getMessage("optimized_for_speed"));
	private JRadioButtonMenuItem m_setGoodRendering = new JRadioButtonMenuItem(ResourceManager.getMessage("optimized_for_quality"), true);
	private ButtonGroup m_renderQualityGroup = new ButtonGroup();
	private JMenu m_renderingQualityMenu = new JMenu(ResourceManager.getMessage("rendering_quality"));
	private JMenuItem m_repaintCanvas = new JMenuItem(ResourceManager.getMessage("repaint"));
	private JMenuItem m_autoscaleCanvas = new JMenuItem(ResourceManager.getMessage("autoscale"));
	private JPopupMenu m_popupMenu = new JPopupMenu();
	private MMG2DCanvas m_g2dCanvas;
	
	public GlobalMousePopupHandler(MMCanvas canvas) {
		super(canvas);
		if(canvas instanceof MMG2DCanvas)
		    m_g2dCanvas = (MMG2DCanvas)canvas;
		
		m_repaintCanvas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getCanvas().renderScene();
				getCanvas().repaint();
			}
		});
		
		m_autoscaleCanvas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_g2dCanvas.autoScale(true);
				getCanvas().repaint();
			}
		});
		
		m_setToolbarVisible.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getCanvas().setToolbarVisible(m_setToolbarVisible.isSelected());
			}
		});
		
		m_setScrollButtonsVisible.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getCanvas().setScrollButtonsVisible(m_setScrollButtonsVisible.isSelected());
			}
		});
		
		m_setShowFPS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getCanvas().enableCalculateFPS(m_setShowFPS.isSelected());
			}
		});
		
		m_setShowCoords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		    m_canvas.setShowMouseCoordinates(m_setShowCoords.isSelected());
				  
			}
		});
		
		m_setAntialiasing.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
						if(m_g2dCanvas != null) {
						    m_g2dCanvas.enableAntialiasing(m_setAntialiasing.isSelected());
						}
				}
			});
		
		m_setGoodRendering.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
						if(m_g2dCanvas != null) {
						    m_g2dCanvas.enableFastRendering(m_setFastRendering.isSelected());
						}
				}
			});
		
		m_setFastRendering.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
						if(m_g2dCanvas != null) {
						    m_g2dCanvas.enableFastRendering(m_setFastRendering.isSelected());
						}
				}
			});
		
		m_popupMenu.add(m_setToolbarVisible);
		m_popupMenu.add(m_setScrollButtonsVisible);
		m_popupMenu.add(m_setShowFPS);
		m_popupMenu.add(m_setShowCoords);
		m_popupMenu.add(m_renderingQualityMenu);
		m_renderingQualityMenu.add(m_setAntialiasing);
		m_renderingQualityMenu.addSeparator();
		m_renderingQualityMenu.add(m_setGoodRendering);
		m_renderingQualityMenu.add(m_setFastRendering);
		m_renderQualityGroup.add(m_setGoodRendering);
		m_renderQualityGroup.add(m_setFastRendering);
		m_popupMenu.addSeparator();
		m_popupMenu.add(m_repaintCanvas);
		m_popupMenu.add(m_autoscaleCanvas);
	}
	
	/* 
	 * @see net.mumie.mathletfactory.action.handler.global.GlobalHandler#userDefinedDealsWith(net.mumie.mathletfactory.action.MMEvent)
	 */
	protected boolean userDefinedDealsWith(MMEvent event) {
		boolean resp = event.getEventType() == MouseEvent.MOUSE_PRESSED&&
		event.getMouseButton() == InputEvent.BUTTON3_MASK;
		
		if( resp )
			return true;
		else
			return false;
	}

	/* 
	 * @see net.mumie.mathletfactory.action.handler.global.GlobalHandler#doAction(net.mumie.mathletfactory.action.MMEvent)
	 */
	public boolean doAction(MMEvent event) {
		m_setToolbarVisible.setSelected(getCanvas().isToolbarVisible());
		m_setScrollButtonsVisible.setSelected(getCanvas().isScrollButtonsVisible());
		m_setShowFPS.setSelected(getCanvas().isCalculateFPSEnabled());
		if(m_g2dCanvas != null) {
		    m_setAntialiasing.setSelected(m_g2dCanvas.isAntialiasingEnabled());
		    
		}
		m_setShowCoords.setSelected(m_canvas.getShowMouseCoordinates());
		m_popupMenu.show(getCanvas().getDrawingBoard(), event.getX(), event.getY());
		return true;
	}

	/* 
	 * @see net.mumie.mathletfactory.action.handler.global.GlobalHandler#finish()
	 */
	public void finish() {}

}

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

package net.mumie.coursecreator.treeview;

import java.awt.PopupMenu;
import java.awt.MenuItem;
import java.awt.event.*;
import net.mumie.coursecreator.CommandConstants;

/** PopUp for Treenavigation Frame
 * @author <a href="mailto:vrichter@math.tu-berlin.de">Verena Richter</a>
 */
public class TreePopupMenu extends PopupMenu {

	static final long serialVersionUID=0;
	private TreeNavigationFrame treeNavigationFrame;
		
	public TreePopupMenu(TreeNavigationFrame tnf) {
		this.treeNavigationFrame = tnf;
		createPopupMenu(this.treeNavigationFrame);
	   
	}
	
	/**
	 * just rebuild the PopupMenu Layout
	 */
	public void update(){
		this.createPopupMenu(treeNavigationFrame);
	}
	
	/**
	 * creates the PopupMenu for the treeview
	 * @param listener
	 */
	private void createPopupMenu(ActionListener listener) {
	
		if (this.getItemCount()>0){// remove items!
			this.removeAll();
		}
		
		MenuItem mi;
		if (this.treeNavigationFrame.isEnabledPreviewButton()) {
			mi = new MenuItem("Preview");
			mi.addActionListener(listener);
			mi.setActionCommand(CommandConstants.TREEVIEW_PREVIEW);
			add(mi);
		}
		if (this.treeNavigationFrame.isEnabledMetainfoButton()) {
			mi = new MenuItem("MetaInformationen");
			mi.addActionListener(listener);
			mi.setActionCommand(CommandConstants.TREEVIEW_METAINFOS);
			add(mi);
		}
		if (this.treeNavigationFrame.isEnabledSetButton()) {
			mi = new MenuItem("Set");
			mi.addActionListener(listener);
			mi.setActionCommand(CommandConstants.TREEVIEW_SET);
			add(mi);
		}
		if (this.getItemCount()==0){
			
		}
	}

	/**
	 * returns if the Menu would have Items, <br>
	 * cause it doesn't make sense to show an empty Menu
	 * @return true if the menu would have items
	 */
	public boolean hasItems() { 
//		return true;}
	return (!(this.getItemCount()==0));}
	
	
}
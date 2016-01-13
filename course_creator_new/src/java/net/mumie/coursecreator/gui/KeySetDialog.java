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

package net.mumie.coursecreator.gui;

/**
 * this is the dialog for the shortcuts
 */

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

import javax.swing.BoxLayout;

import javax.swing.JFormattedTextField;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import net.mumie.coursecreator.CCController;
import net.mumie.coursecreator.CommandConstants;

public class KeySetDialog extends CCDialog implements KeyListener{
	static final long serialVersionUID=0;
	
	private CCController cccontroller;
	
	private JFormattedTextField key_new;
	private JFormattedTextField key_open;
	private JFormattedTextField key_red;
	private JFormattedTextField key_save;
	private JFormattedTextField key_checkred;
	private JFormattedTextField key_exists;
	private JFormattedTextField key_grid;
	private JFormattedTextField key_connect;
	private JFormattedTextField key_edgedir;
	private JFormattedTextField key_swap;
	private JFormattedTextField key_delete;
	private JFormattedTextField key_up; 
	private JFormattedTextField key_down;
	private JFormattedTextField key_left;
	private JFormattedTextField key_right;
	private JFormattedTextField key_movein;
	private JFormattedTextField key_moveout;
	private JFormattedTextField key_zoomin;
	private JFormattedTextField key_zoomout;
	
	private LinkedList<String> keys;
	
	private int compHeight = 15;
	private int width = 350;
	private int height = 580;
    
	public KeySetDialog(CCController c){
	
		
		super(CCController.frame,c,"Tastenk\u00fcrzel");
		this.cccontroller = c;
		this.buildLayout();
		this.addComponentListener(this);
		this.keys = new LinkedList<String>();
		this.setSettings();
		
	}
	/**
	 * layout
	 */
	private void buildLayout(){
		this.setResizable(false);
		
		setSize(this.width,this.height);
		this.setLocation(this.cccontroller.getConfig().getPOSITION_KEY());
		this.setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));
		
		this.add(this.createFile());
		this.add(this.createGraphManipulation());
		this.add(this.createNavigation());
		this.add(this.createDefaultNCloseButton());
		
	}

	/**
	 * file panel
	 * @return
	 */	
	private JPanel createFile(){
		GridBagLayout gbl = new GridBagLayout();
		
		JPanel filePanel	= new JPanel();
		filePanel.setLayout(gbl);
		filePanel.setBorder(new TitledBorder(new EtchedBorder(),"Datei"));
		filePanel.setSize(this.width, this.compHeight*3);

		key_open 	= createTextfield("Datei \u00f6ffnen",gbl, 0,filePanel,this.compHeight);
		key_new 	= createTextfield("neue Datei",gbl, 1,filePanel,this.compHeight);
		key_save	= createTextfield("Speichern",gbl, 2,filePanel,this.compHeight);

		
		filePanel.add(key_open);
		filePanel.add(key_new);
		filePanel.add(key_save);
		
		return filePanel;
		
	}
	
	/**
	 * graph manipulation panel
	 * @return
	 */	
	private JPanel createGraphManipulation(){
		GridBagLayout gbl = new GridBagLayout();
		
		JPanel graphPanel	= new JPanel();
		graphPanel.setLayout(gbl);
		graphPanel.setBorder(new TitledBorder(new EtchedBorder(),"Graph bearbeiten"));
		graphPanel.setSize(this.width, this.compHeight*10);

		key_connect = createTextfield("verbinden",gbl, 0,graphPanel,this.compHeight);
		key_swap	= createTextfield("Komponenten vertauschen",gbl, 1,graphPanel,this.compHeight);
		key_delete	= createTextfield("L\u00f6schen",gbl, 2,graphPanel,this.compHeight);
		key_grid	= createTextfield("am Gitter ausrichten",gbl, 3,graphPanel,this.compHeight);
		key_checkred= createTextfield("roten Faden pr\u00fcfen",gbl, 4,graphPanel,this.compHeight);
		
		key_edgedir	= createTextfield("Kanten Richtung",gbl, 5,graphPanel,this.compHeight);
		key_exists	= createTextfield("schwarze Kante",gbl, 6,graphPanel,this.compHeight);
		key_red		= createTextfield("rote Kante",gbl, 7,graphPanel,this.compHeight);

		key_movein	= createTextfield("Nach innen",gbl, 8,graphPanel,this.compHeight);
		key_moveout	= createTextfield("Nach au\u00DFen",gbl, 9,graphPanel,this.compHeight);

		
		graphPanel.add(key_connect);
		graphPanel.add(key_swap);
		graphPanel.add(key_delete);
		graphPanel.add(key_grid);
		graphPanel.add(key_checkred);
		graphPanel.add(key_edgedir);
		graphPanel.add(key_exists);
		graphPanel.add(key_red);
		graphPanel.add(key_movein);
		graphPanel.add(key_moveout);
		
		
		return graphPanel;
	}
	

	 /**
	  * navigation panel
	  * @return
	  */ 
	private JPanel createNavigation(){
		GridBagLayout gbl = new GridBagLayout();
		
		JPanel filePanel	= new JPanel();
		filePanel.setLayout(gbl);
		filePanel.setBorder(new TitledBorder(new EtchedBorder(),"Navigation"));
		filePanel.setSize(this.width, this.compHeight*6);

		
		key_zoomout = createTextfield("Rauszoomen",gbl, 0,filePanel,this.compHeight);
		key_zoomin	= createTextfield("Reinzoomen",gbl, 1,filePanel,this.compHeight);
		
		key_down	= createTextfield("runter",gbl, 2,filePanel,this.compHeight);
		key_left	= createTextfield("links",gbl, 3,filePanel,this.compHeight);
		key_right	= createTextfield("rechts",gbl, 4,filePanel,this.compHeight);
		key_up		= createTextfield("hoch",gbl, 5,filePanel,this.compHeight);

		
		filePanel.add(key_zoomout);
		filePanel.add(key_zoomin);
		filePanel.add(key_down);
		filePanel.add(key_left);
		filePanel.add(key_right);
		filePanel.add(key_up);
		
		return filePanel;
		
	}
		
	/**
	 * sets to all this.key_ ..s the value of the configclass
	 *
	 */
	private void setSettings(){
		this.key_new.setText(cccontroller.getConfig().getKey_new());
		this.key_checkred.setText(cccontroller.getConfig().getKey_checkRed());
		
		this.key_connect.setText(cccontroller.getConfig().getKey_connect());
		this.key_delete.setText(cccontroller.getConfig().getKey_delete());
		this.key_down.setText(cccontroller.getConfig().getKey_down());
		this.key_edgedir.setText(cccontroller.getConfig().getKey_edgeDir());
		this.key_exists.setText(cccontroller.getConfig().getKey_exists());
		this.key_grid.setText(cccontroller.getConfig().getKey_grid());
		this.key_left.setText(cccontroller.getConfig().getKey_left());
		this.key_movein.setText(cccontroller.getConfig().getKey_movein());
		this.key_moveout.setText(cccontroller.getConfig().getKey_moveout());
		this.key_open.setText(cccontroller.getConfig().getKey_open());
		this.key_red.setText(cccontroller.getConfig().getKey_red());
		this.key_right.setText(cccontroller.getConfig().getKey_right());
		this.key_save.setText(cccontroller.getConfig().getKey_save());
		this.key_swap.setText(cccontroller.getConfig().getKey_swap());
		this.key_up.setText(cccontroller.getConfig().getKey_up());
		this.key_zoomin.setText(cccontroller.getConfig().getKey_zoomin());
		this.key_zoomout.setText(cccontroller.getConfig().getKey_zoomout());
		
		this.keys.clear();
		
		this.keys.addLast(cccontroller.getConfig().getKey_new());
		this.keys.addLast(cccontroller.getConfig().getKey_checkRed());
		this.keys.addLast(cccontroller.getConfig().getKey_connect());
		this.keys.addLast(cccontroller.getConfig().getKey_delete());
		this.keys.addLast(cccontroller.getConfig().getKey_down());
		this.keys.addLast(cccontroller.getConfig().getKey_edgeDir());
		this.keys.addLast(cccontroller.getConfig().getKey_exists());
		this.keys.addLast(cccontroller.getConfig().getKey_grid());
		this.keys.addLast(cccontroller.getConfig().getKey_left());
		this.keys.addLast(cccontroller.getConfig().getKey_movein());
		this.keys.addLast(cccontroller.getConfig().getKey_moveout());
		this.keys.addLast(cccontroller.getConfig().getKey_open());
		this.keys.addLast(cccontroller.getConfig().getKey_red());
		this.keys.addLast(cccontroller.getConfig().getKey_right());
		this.keys.addLast(cccontroller.getConfig().getKey_save());
		this.keys.addLast(cccontroller.getConfig().getKey_swap());
		this.keys.addLast(cccontroller.getConfig().getKey_up());
		this.keys.addLast(cccontroller.getConfig().getKey_zoomin());
		this.keys.addLast(cccontroller.getConfig().getKey_zoomout());
		
	}

	/**
	 * a key exists allready. user decides if he want to retry or to set the key and delete the other
	 * @return true iff user wants to retry<br>
	 *          false iff user wants to set the key and delete the other 
	 */
	private boolean dialogKeyExistsRepeat(){
    	Object[] options = {"erneut versuchen", "das andere l\u00f6schen"};
    	return (JOptionPane.OK_OPTION == JOptionPane.showOptionDialog(this, 
    			"Die Tastenkombination existiert bereits",
    			"doppelte Tastenkombination", 
    			JOptionPane.YES_NO_OPTION,
    			JOptionPane.INFORMATION_MESSAGE,
    			null,
    			options,
    			options[1]));
	}
	
	/** (non-Javadoc)
	 * @see net.mumie.coursecreator.gui.CCDialog#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		
		JFormattedTextField source = (JFormattedTextField)e.getSource();
		int code = e.getKeyCode();
		if ((code==16)
				||(code==17)
				||(code==18)
				||(code==20) //Caps lock
				)return;
		
		
		String s="";
		
		if ((code>=65)&&code<=90) // letter
			s = String.valueOf(e.getKeyChar()).toLowerCase();
		else if ((code>=49)&&(code<=57)){ // numbers
			s= String.valueOf(code-48);
			
		}
		else if ((code>=112)&&(code<=124)) // "F"-keys
			s= CommandConstants.stringF+String.valueOf(code-111);
		
		// other staff
		else if (code==8) s= CommandConstants.stringBACKSPACE;
		else if (code==10) s= CommandConstants.stringENTER;
		else if (code==19) s= CommandConstants.stringPAUSE;
		else if (code==27) s= CommandConstants.stringESC;
		else if (code==32) s= CommandConstants.stringSPACE;
		else if (code==33) s= CommandConstants.stringPAGEUP;
		else if (code==34) s= CommandConstants.stringPAGEDOWN;
		else if (code==35) s= CommandConstants.stringEND;
		else if (code==36) s= CommandConstants.stringHOME;
		else if (code==37) s= CommandConstants.stringLEFT;
		else if (code==38) s= CommandConstants.stringUP;
		else if (code==39) s= CommandConstants.stringRIGHT;
		else if (code==40) s= CommandConstants.stringDOWN;
		else if (code==45) s= CommandConstants.stringMINUS;
		else if (code==107) s= CommandConstants.stringPLUS;
		else if (code==127) s= CommandConstants.stringDelete;
		else if (code==155) s= CommandConstants.stringINSERT;
		else {
			JOptionPane.showMessageDialog(
					CCController.frame,"Eingabefehler: Taste "+code+" nicht moeglich",
					"Taste nicht moeglich",
    			JOptionPane.INFORMATION_MESSAGE);
			source.setText(this.getConfigValue((JFormattedTextField)e.getSource()));
			source.setEditable(true);
	   return;
	   }
		
		// get modifiers
		if (e.isAltDown()) s = CommandConstants.stringALT + " + "+s;
		if (e.isControlDown())s = CommandConstants.stringCTRL + " + "+s; 
		if (e.isMetaDown()) s = CommandConstants.stringMETA+" + "+s;
		if (e.isShiftDown()) s = CommandConstants.stringSHIFT+" + "+s;
			
		if (this.keys.contains(s)&& (!this.getConfigValue(source).equals(s))) {
			if (this.dialogKeyExistsRepeat()){// user wants to retry
				source.setText(this.getConfigValue((JFormattedTextField)e.getSource()));
				
				return;
			}
			else {//delete the other
				JFormattedTextField other =getConfigValue(s); 
				other.setText(CommandConstants.stringEmpty);
				this.setKeyValue(other, CommandConstants.stringEmpty);
				
			}
			
		}else{
			this.keys.add(s);	
		}
		source.setText(s);
		
		
		this.keys.remove(this.getConfigValue(source));
		this.setKeyValue(source, s);
		this.cccontroller.createActionInputMap();
		source.setEnabled(false);
		source.setEnabled(true);
				
	}
	
	/**
	 * returns the JFormattedTextField which contains the String s
	 * @param s a String
	 * @return the JFormattedTextField which contains the String s
	 */
	private JFormattedTextField getConfigValue(String s){
		if (s.equals(this.cccontroller.getConfig().getKey_checkRed())) return this.key_checkred;
		else if (s.equals(this.cccontroller.getConfig().getKey_connect())) return this.key_connect;
		else if (s.equals(this.cccontroller.getConfig().getKey_delete())) return this.key_delete;
		else if (s.equals(this.cccontroller.getConfig().getKey_down())) return this.key_down;
		else if (s.equals(this.cccontroller.getConfig().getKey_edgeDir())) return this.key_edgedir;
		else if (s.equals(this.cccontroller.getConfig().getKey_exists())) return this.key_exists;
		else if (s.equals(this.cccontroller.getConfig().getKey_grid())) return this.key_grid;
		else if (s.equals(this.cccontroller.getConfig().getKey_left())) return this.key_left;
		else if (s.equals(this.cccontroller.getConfig().getKey_movein())) return this.key_movein;
		else if (s.equals(this.cccontroller.getConfig().getKey_moveout())) return this.key_moveout;
		else if (s.equals(this.cccontroller.getConfig().getKey_new())) return this.key_new;
		else if (s.equals(this.cccontroller.getConfig().getKey_open())) return this.key_open;
		else if (s.equals(this.cccontroller.getConfig().getKey_red())) return this.key_red;
		else if (s.equals(this.cccontroller.getConfig().getKey_right())) return this.key_right;
		else if (s.equals(this.cccontroller.getConfig().getKey_save())) return this.key_save;
		else if (s.equals(this.cccontroller.getConfig().getKey_swap())) return this.key_swap;
		else if (s.equals(this.cccontroller.getConfig().getKey_up())) return this.key_up;
		else if (s.equals(this.cccontroller.getConfig().getKey_zoomin())) return this.key_zoomin;
		else if (s.equals(this.cccontroller.getConfig().getKey_zoomout())) return this.key_zoomout;
		
		else return null;
	}
	
	/**
	 * returns the String of the membervariable for a JFormattedTextField keyField
	 * @param keyField
	 * @return
	 */
	private String getConfigValue(JFormattedTextField keyField){
		if (keyField.equals(this.key_checkred)) return this.cccontroller.getConfig().getKey_checkRed();
		else if (keyField.equals(this.key_connect)) return this.cccontroller.getConfig().getKey_connect();
		else if (keyField.equals(this.key_delete)) return this.cccontroller.getConfig().getKey_delete();
		else if (keyField.equals(this.key_down)) return this.cccontroller.getConfig().getKey_down();
		else if (keyField.equals(this.key_edgedir)) return this.cccontroller.getConfig().getKey_edgeDir();
		else if (keyField.equals(this.key_exists)) return this.cccontroller.getConfig().getKey_exists();
		else if (keyField.equals(this.key_grid)) return this.cccontroller.getConfig().getKey_grid();
		else if (keyField.equals(this.key_left)) return this.cccontroller.getConfig().getKey_left();
		else if (keyField.equals(this.key_movein)) return this.cccontroller.getConfig().getKey_movein();
		else if (keyField.equals(this.key_moveout)) return this.cccontroller.getConfig().getKey_moveout();
		else if (keyField.equals(this.key_new)) return this.cccontroller.getConfig().getKey_new();
		else if (keyField.equals(this.key_open)) return this.cccontroller.getConfig().getKey_open();
		else if (keyField.equals(this.key_red)) return this.cccontroller.getConfig().getKey_red();
		else if (keyField.equals(this.key_right)) return this.cccontroller.getConfig().getKey_right();
		else if (keyField.equals(this.key_save)) return this.cccontroller.getConfig().getKey_save();
		else if (keyField.equals(this.key_swap)) return this.cccontroller.getConfig().getKey_swap();
		else if (keyField.equals(this.key_up)) return this.cccontroller.getConfig().getKey_up();
		else if (keyField.equals(this.key_zoomin)) return this.cccontroller.getConfig().getKey_zoomin();
		else if (keyField.equals(this.key_zoomout)) return this.cccontroller.getConfig().getKey_zoomout();
		else return null;
	}
	
	/**
	 * sets to a JFormattedTextField keyField the String key
	 * @param keyField
	 * @param key
	 */
	private void setKeyValue(JFormattedTextField keyField,String key){
		if (keyField.equals(this.key_checkred)) this.cccontroller.getConfig().setKey_checkRed(key);
		else if (keyField.equals(this.key_connect)) this.cccontroller.getConfig().setKey_connect(key);
		else if (keyField.equals(this.key_delete)) this.cccontroller.getConfig().setKey_delete(key);
		else if (keyField.equals(this.key_down)) this.cccontroller.getConfig().setKey_down(key);
		else if (keyField.equals(this.key_edgedir)) this.cccontroller.getConfig().setKey_edgeDir(key);
		else if (keyField.equals(this.key_exists)) this.cccontroller.getConfig().setKey_exists(key);
		else if (keyField.equals(this.key_grid)) this.cccontroller.getConfig().setKey_grid(key);
		else if (keyField.equals(this.key_left)) this.cccontroller.getConfig().setKey_left(key);
		else if (keyField.equals(this.key_movein)) this.cccontroller.getConfig().setKey_movein(key);
		else if (keyField.equals(this.key_moveout)) this.cccontroller.getConfig().setKey_moveout(key);
		else if (keyField.equals(this.key_new)) this.cccontroller.getConfig().setKey_new(key);
		else if (keyField.equals(this.key_open)) this.cccontroller.getConfig().setKey_open(key);
		else if (keyField.equals(this.key_red)) this.cccontroller.getConfig().setKey_red(key);
		else if (keyField.equals(this.key_right)) this.cccontroller.getConfig().setKey_right(key);
		else if (keyField.equals(this.key_save)) this.cccontroller.getConfig().setKey_save(key);
		else if (keyField.equals(this.key_swap)) this.cccontroller.getConfig().setKey_swap(key);
		else if (keyField.equals(this.key_up)) this.cccontroller.getConfig().setKey_up(key);
		else if (keyField.equals(this.key_zoomin)) this.cccontroller.getConfig().setKey_zoomin(key);
		else if (keyField.equals(this.key_zoomout)) this.cccontroller.getConfig().setKey_zoomout(key);
		
	}

	@Override
	public void focusLost(FocusEvent e) {
		
		JFormattedTextField field = (JFormattedTextField)e.getSource();
		field.setBackground(this.bgColor);

	}
	
	@Override
	public void focusGained(FocusEvent e) {
		
		if (e.getOppositeComponent()!=null) e.getOppositeComponent().setEnabled(true);
		
		JFormattedTextField comp = (JFormattedTextField)e.getSource();
		
		JFormattedTextField field = (JFormattedTextField)e.getSource();
		field.setBackground(Color.white);
		field.setSelectionStart(0);
		field.setSelectionEnd(comp.getText().length());
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		 if (e.getSource().equals(this.defaultButton)){
             Object[] options = {"ja", "nein"};
             if (JOptionPane.OK_OPTION != JOptionPane.showOptionDialog(this,
                     "Defaultwerte setzen?",
                     "Defaultwerte setzen",
                     JOptionPane.YES_NO_OPTION,
                     JOptionPane.INFORMATION_MESSAGE,
                     null,
                     options,
                     options[1])) return;
             this.cccontroller.getConfig().setDefaultKeys();
             this.cccontroller.createActionInputMap();
             this.setSettings();
		 }else if (e.getSource().equals(this.closeButton)){
			 this.stop();
		 }
     }
}

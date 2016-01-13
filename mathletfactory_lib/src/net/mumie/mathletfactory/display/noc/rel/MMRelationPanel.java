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

package net.mumie.mathletfactory.display.noc.rel;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;

import net.mumie.mathletfactory.display.noc.MMEditablePanel;
import net.mumie.mathletfactory.display.noc.rel.node.RelationComponent;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.PropertyHandlerIF;
import net.mumie.mathletfactory.mmobject.algebra.MMRelation;
import net.mumie.mathletfactory.transformer.noc.RelationTransformer;


/**
 *  This class implements the interactive presentation of a
 *  {@link net.mumie.mathletfactory.mmobject.algebra.MMRelation}. 
 *  It displays it in a {@link RelationDisplay} and allows the editing (if
 *  the corresponding flag is set) in a popup-menu. 
 * 
 *  @author Paehler, Gronau
 *  @mm.docstatus finished
 */
public class MMRelationPanel extends MMEditablePanel {

	private RelationDisplay m_display;
  
  private MMRelation m_master;
  
  /**
   * Constructs the relation panel from a master 
   *  {@link net.mumie.mathletfactory.mmobject.algebra.MMRelation} object.
   * @see net.mumie.mathletfactory.display.noc.JPanelDrawable#JPanelDrawable(MMObjectIF)
   */
  public MMRelationPanel(MMObjectIF masterObject, RelationTransformer transformer) {
    super(masterObject, transformer, new RelationDisplay(masterObject, transformer));
    m_display = (RelationDisplay) getValueViewer();

    MMRelation masterRelation = (MMRelation)masterObject;
    m_display.setOpaque(true);
    addPropertyChangeListener(masterRelation);
    setRelation(masterRelation);
  }

  protected void applyContent(String content) {
  	firePropertyChange(PropertyHandlerIF.RELATION, m_master, 
      new Relation(m_master.getNumberClass(), content, m_master.getNormalForm()));
  	setRelation(m_master);
	}

	protected boolean checkContent(String content) {
		try {
			// try to parse new relation
			new Relation(m_master.getNumberClass(), content, m_master.getNormalForm());
			return true;
		} catch(Exception e) {
			// error while parsing...
			return false;
		}
	}

	protected String getEditingContent() {
		return m_master.toString(m_display.getDecimalFormat());
	}
   
  /**
   *  Sets the master 
   *  {@link net.mumie.mathletfactory.mmobject.algebra.MMRelation} object, 
   *  the model of this view.
   */
  public void setRelation(MMRelation master) {
    m_master = master;
    m_display.setRelation(m_master);
    repaintAll();
  }
  
  /**
    *  Sets the font to be used in the {@link RelationDisplay}.
    *
    * @see java.awt.Component#setFont(Font)
    */
   public void setFont(Font font) {
     if (m_display != null)
       m_display.setFont(font);
     super.setFont(font);
     repaintAll();
   }

  /**
   * Sets the color of the font.
   */
  public void setForeground(Color color){
    super.setForeground(color);
    if(m_display != null)
      m_display.setForeground(color);
  }
   
  /**
   * Returns the rows of the rendered relation. Needed by
   * {@link RelationTransformStepPanel}.
   */
  public RelationComponent[] getRelationRows(){
    return m_display.getRelationRows();
  }
    
  /** 
   * Sets the given display format for decimal (i.e. MDouble) numbers. 
   * Otherwise the format using {@link net.mumie.mathletfactory.display.noc.op.node.NumberView#DEFAULT_PATTERN} 
   * is used.
   */
  public void setDecimalFormat(DecimalFormat format){
    m_display.setDecimalFormat(format);
  }
}

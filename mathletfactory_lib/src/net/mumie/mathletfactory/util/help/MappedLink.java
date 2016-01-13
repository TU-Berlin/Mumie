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

package net.mumie.mathletfactory.util.help;
import javax.swing.JLabel;
import javax.swing.text.View;
import javax.help.*;
import javax.help.Map.ID;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import com.sun.java.help.impl.ViewAwareComponent;
 
/**
 * Class MappedLink is a view-aware lightweight component to be used
 * in JavaHelp for linking between topics across the merged helpsets.<br>
 * <p>
 * By applying this class for linking you create a hyperlink which works
 * similar to the context sensitive help enabled on a GUI widget.<br>
 * The targed is defined by a map id which is resolved via the JavaHelp map
 * file (.jhm) to a target URL. This URL is opened when clicking on the
 * "hyperlink".
 * </p>
 * <p>
 * To create such a "hyperlink" inside your help topic's content, use
 * the object tag as shown in the example below:<br>
 * <pre>
 * <object CLASSID="java:your.own.package.MappedLink">
 *    <param name="target" value="Map_id_of_target">
 *    <param name="linkText" value="Link to other topic">
 * </object>
  * </pre>
 * </p>
 * 
 * @author unknown (copied from http://forum.java.sun.com/thread.jspa?forumID=42&threadID=590201, changed by gronau)
 */
public class MappedLink extends JLabel implements ViewAwareComponent {
 
    private View myView;
    private HelpSet hs;
    private Map.ID target;
    private final static Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private JHelpContentViewer contentViewer;
    private Cursor origCursor;
 
 
    public MappedLink() {
        super();
 
        origCursor = getCursor();
        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }
 
            public void mouseEntered(MouseEvent e) {
                setCursor(handCursor);
            }
 
            public void mouseExited(MouseEvent e) {
                setCursor(origCursor);
            }
 
            public void mousePressed(MouseEvent e) {
                jump();
            }
 
            public void mouseReleased(MouseEvent e) {
            }
        });
    }
 
    /**
     * This method actually does the jump to the per Map.ID linked topic
     */
    private void jump() {
        try {
            if (contentViewer != null && target != null) {
                contentViewer.setCurrentID(target);
            }
        }
        catch (InvalidHelpSetContextException ex) {
            System.err.println("HelpSetContext EXCEPTION IN JUMP" + ex.getMessage());
            ex.printStackTrace();
        }
    }
 
    /**
     * Sets data obtained from the View
     */
    public void setViewData(View v) {
        myView = v;
 
        // Loop through and find the JHelpContentViewer
        Component c =  myView.getContainer();
        while (c != null) {
            if (c instanceof JHelpContentViewer) {
                contentViewer = (JHelpContentViewer) c;
                break;
            }
            c = c.getParent();
        }
 
        // Get the helpset if JHelpContentViewer was found
        if (contentViewer !=null) {
            TextHelpModel thm = contentViewer.getModel();
            if (thm != null) {
                hs = thm.getHelpSet();
            }
            // set Link font according to viewer's
            setForeground(Color.BLUE);
            Font usedFont = contentViewer.getFont();
            Font newFont = usedFont.deriveFont(Font.BOLD);
//            newFont = usedFont.deriveFont(14f);
            setFont(newFont);
        }
    }
 
 
    /**
     * sets the map id for the lookup
     * @param mapID The id for lookup in the map file
     */
    public void setTarget(String mapID) {
        try {
            this.target = ID.create(mapID, hs);
        } catch (BadIDException ex) {
            System.err.println("Bad Map ID: " + mapID);
        }
    }
 
    /**
     * Sets the text shown as hyperlink
     * @param text The hyperlinked text
     */
    public void setLinkText(String text) {
        this.setText("<html><u>" + text + "</u></html>");
    }
 
}

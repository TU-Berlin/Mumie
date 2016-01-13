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

package net.mumie.cdk.util.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.mumie.cdk.util.editor.editors.AbstractTypeEditor;
import net.mumie.cocoon.checkin.DOMMaster;
import net.mumie.cocoon.checkin.Master;
import net.mumie.cocoon.checkin.MasterException;

import org.w3c.dom.Document;

/**
 * This is the main class for executing the Meta-Info-Editor on any master file.
 * 
 * @author Markus Gronau <a
 *         href="mailto:gronau@math.tu-berlin.de">gronau@math.tu-berlin.de</a>
 * @version <code>$Id$</code>
 */
public class MasterEditor {

  public MasterEditor() {

  }

  public Master editMaster(File masterFile) throws MasterException {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);

      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(masterFile);
      Master master = new DOMMaster(doc);
      return editMaster(master);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public Master editMaster(Master master) throws MasterException {
    return new EditorFrame(master).getMaster();
  }

  public static void main(String[] args) throws MasterException {
    MasterEditor editor = new MasterEditor();
    if (args == null || args.length == 0) {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setFileFilter(new FileFilter() {
        public boolean accept(File f) {
          return f.isDirectory() || f.getName().endsWith(".meta.xml");
        }

        public String getDescription() {
          return "Meta-Info-Files (*.meta.xml)";
        }

      });
      int choice = fileChooser.showOpenDialog(null);
      if (choice == JFileChooser.APPROVE_OPTION) {
        editor.editMaster(fileChooser.getSelectedFile());
      }
    } else {
      for (int i = 0; i < args.length; i++) {
        File file = new File(args[i]);
        if (file.exists() && file.canRead() && file.canWrite())
          editor.editMaster(file);
        else
          System.err.println("Cannot read/write file argument #" + (i + 1)
              + ": " + args[i]);
      }
    }
  }

  class EditorFrame extends JFrame {

    private AbstractTypeEditor m_editor;

    EditorFrame(Master master) throws MasterException {
      super("Meta-Info-Editor");
      setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

      m_editor = AbstractTypeEditor.createTypeEditor(master);
      getContentPane().add(m_editor.getContentPane(), BorderLayout.CENTER);

      JButton okButton = new JButton("OK");
      okButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          closeEditor();
        }
      });
      JPanel bottomPanel = new JPanel();
      bottomPanel.add(okButton);
      getContentPane().add(bottomPanel, BorderLayout.SOUTH);

      addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          int choice = JOptionPane.showConfirmDialog(getContentPane(), m_editor
              .getMessage("editor.confirm-closing"), null,
              JOptionPane.YES_NO_OPTION);
          if (choice == JOptionPane.YES_OPTION)
            closeEditor();
        }
      });

      pack();
      setSize(640, 480);
      center();
      setVisible(true);
    }

    void closeEditor() {
      try {
        m_editor.save();
        dispose();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    Master getMaster() {
      return m_editor.getMaster();
    }

    /** Centers this frame on the screen. */
    public void center() {
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      int x = (screenSize.width - getWidth()) / 2;
      int y = (screenSize.height - getHeight()) / 2;
      setLocation(x, y);
    }
  }
}

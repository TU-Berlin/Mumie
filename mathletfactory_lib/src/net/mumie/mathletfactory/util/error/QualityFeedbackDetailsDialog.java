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

package net.mumie.mathletfactory.util.error;

import java.awt.Component;
import java.awt.Dialog;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.mumie.mathletfactory.appletskeleton.MathletContext;
import net.mumie.mathletfactory.appletskeleton.util.dialog.DefaultDialog;
import net.mumie.mathletfactory.appletskeleton.util.dialog.DialogAction;

/**
 * This dialog shows informations about the error and the client machine the given mathlet is running on.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class QualityFeedbackDetailsDialog extends DefaultDialog {

	public final static int DIALOG_ID = 22;
	
	public QualityFeedbackDetailsDialog(MathletContext mathlet, Dialog owner, QualityFeedbackReport report) {
		super(mathlet, owner, null);
		
		setDialogID(DIALOG_ID);
		setTitle(mathlet.getMessage("errorhandler.details.title.bar"));
		setContentTitle(mathlet.getMessage("errorhandler.details.title.content"));
		
		addText(mathlet.getMessage("errorhandler.details.text"));
		insertLineBreak();
		
		TooltipTreeNode throwableClassNode = new TooltipTreeNode(mathlet.getMessage("errorhandler.report.error.class")+report.getThrowableClass(), mathlet.getMessage("errorhandler.report.error.class.tooltip"));
		TooltipTreeNode throwableDescrNode = new TooltipTreeNode(mathlet.getMessage("errorhandler.report.error.message")+report.getThrowableMessage(), mathlet.getMessage("errorhandler.report.error.message.tooltip"));
		TooltipTreeNode causeClassNode = new TooltipTreeNode(mathlet.getMessage("errorhandler.report.cause.class")+report.getCauseClass(), mathlet.getMessage("errorhandler.report.cause.class.tooltip"));
		TooltipTreeNode causeDescrNode = new TooltipTreeNode(mathlet.getMessage("errorhandler.report.cause.message")+report.getCauseMessage(), mathlet.getMessage("errorhandler.report.cause.message.tooltip"));
		
		TooltipTreeNode appletNode = new TooltipTreeNode(mathlet.getMessage("errorhandler.report.applet.name")+report.getAppletClass(), mathlet.getMessage("errorhandler.report.applet.name.tooltip"));
		TooltipTreeNode userIDNode = new TooltipTreeNode(mathlet.getMessage("errorhandler.report.user.id")+report.getUserID(), mathlet.getMessage("errorhandler.report.user.id.tooltip"));
		TooltipTreeNode problemRefNode = new TooltipTreeNode(mathlet.getMessage("errorhandler.report.problem.ref")+report.getProblemRef(), mathlet.getMessage("errorhandler.report.problem.ref.tooltip"));
    TooltipTreeNode urlPrefixNode = new TooltipTreeNode(mathlet.getMessage("errorhandler.report.server.url")+report.getURLPrefix(), mathlet.getMessage("errorhandler.report.server.url.tooltip"));

		TooltipTreeNode timeNode = new TooltipTreeNode(mathlet.getMessage("errorhandler.report.time"), mathlet.getMessage("errorhandler.report.time.tooltip"));
		timeNode.add(new DefaultMutableTreeNode("<html><b>String</b> = "+report.getTimeString()));
		timeNode.add(new DefaultMutableTreeNode("<html><b>Format</b> = "+report.getTimeFormat()));
		timeNode.add(new DefaultMutableTreeNode("<html><b>Raw</b> = "+report.getRawTime()));

		TooltipTreeNode systemNode = new TooltipTreeNode(mathlet.getMessage("errorhandler.report.os"), mathlet.getMessage("errorhandler.report.os.tooltip"));
		systemNode.add(new DefaultMutableTreeNode(mathlet.getMessage("errorhandler.report.os.name")+report.getOSName()));
		systemNode.add(new DefaultMutableTreeNode(mathlet.getMessage("errorhandler.report.os.version")+report.getOSVersion()));
		systemNode.add(new DefaultMutableTreeNode(mathlet.getMessage("errorhandler.report.os.arch")+report.getOSArch()));

		TooltipTreeNode javaNode = new TooltipTreeNode(mathlet.getMessage("errorhandler.report.java"), mathlet.getMessage("errorhandler.report.java.tooltip"));
		javaNode.add(new DefaultMutableTreeNode(mathlet.getMessage("errorhandler.report.java.version")+report.getJavaVersion()));
		javaNode.add(new DefaultMutableTreeNode(mathlet.getMessage("errorhandler.report.java.vendor")+report.getJavaVendor()));

		TooltipTreeNode stacktraceNode = new TooltipTreeNode(mathlet.getMessage("errorhandler.report.stacktrace"), mathlet.getMessage("errorhandler.report.stacktrace.tooltip"));
		DefaultMutableTreeNode stacktraceExceptionNode = new DefaultMutableTreeNode("<html>"+report.getErrorStackTrace()[0]);
		stacktraceNode.add(stacktraceExceptionNode);
		for(int i = 1; i < report.getErrorStackTrace().length; i++) {
			stacktraceExceptionNode.add(new DefaultMutableTreeNode("<html>"+report.getErrorStackTrace()[i]));
		}
		if(report.getCauseStackTrace() != null) {
			DefaultMutableTreeNode stacktraceCauseNode = new DefaultMutableTreeNode("<html>caused by: "+report.getCauseStackTrace()[0]);
			stacktraceNode.add(stacktraceCauseNode);		
			for(int i = 1; i < report.getCauseStackTrace().length; i++) {
				stacktraceCauseNode.add(new DefaultMutableTreeNode("<html>"+report.getCauseStackTrace()[i]));
			}
		}

		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
		rootNode.add(appletNode);
		rootNode.add(throwableClassNode);
		rootNode.add(throwableDescrNode);
		rootNode.add(causeClassNode);
		rootNode.add(causeDescrNode);
		rootNode.add(problemRefNode);
    rootNode.add(urlPrefixNode);
		rootNode.add(userIDNode);
		rootNode.add(javaNode);
		rootNode.add(systemNode);
		rootNode.add(timeNode);
		rootNode.add(stacktraceNode);
		
		JTree tree = new JTree(rootNode);
		ToolTipManager.sharedInstance().registerComponent(tree);
		tree.setCellRenderer(new TooltipTreeCellRenderer());
		tree.setRootVisible(false);
		tree.setBorder(new CompoundBorder(new CompoundBorder(new EmptyBorder(1, 20, 20, 20), BorderFactory.createLoweredBevelBorder()), new EmptyBorder(20, 20, 20, 20)));
		tree.setToggleClickCount(1);
		JScrollPane treeScroller = new JScrollPane(tree);
		treeScroller.setBorder(null);
		addControl(treeScroller);
		
		insertLineBreaks(1);
		
		addAction(DialogAction.CLOSE_ACTION, mathlet.getMessage("dialog.action.close"));
		
		pack();
	}
	
	class TooltipTreeCellRenderer extends DefaultTreeCellRenderer {
		
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
					hasFocus);
			if(value instanceof TooltipTreeNode) {
				setToolTipText(((TooltipTreeNode) value).getTooltipText());
			} else {
				setToolTipText(null);
			}
			return this;
		}
	}
	
	class TooltipTreeNode extends DefaultMutableTreeNode {
		
		private String m_tooltipText;

		TooltipTreeNode(String text, String tooltipText) {
			super(text);
			m_tooltipText = tooltipText;
		}
		
		String getTooltipText() {
			return m_tooltipText;
		}
	}
}

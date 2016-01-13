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

package net.mumie.coursecreator.threads;

/**
 * klasse mit der ein Thread gestartet werden kann, der die Dateien<br>
 * CCController.dummyfile.meta.xml und CCController.dummyfile.content,xml<br>
 * im Systemeditor anzeigt.
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JOptionPane;

import net.mumie.coursecreator.CCController;

public class ShowSourceInEditor implements Runnable{

	private CCController controller;
	
	public ShowSourceInEditor(CCController c) {
		this.controller = c;
	}

	public void run() {
		
		String meta = CCController.dummyfile + ".meta.xml";
		String content = CCController.dummyfile +".content.xml";
		
		String cmd = System.getenv("EDITOR");
		if (cmd ==null) cmd = "xemacs";
		String cmdStringMeta = cmd+" " + meta;
		String cmdStringContent = cmd + " " + content;
		
		// neuen thread starten
		Process pMeta=null;
		Process pContent=null;
		try {				
			pMeta = Runtime.getRuntime().exec(cmdStringMeta);
			pContent = Runtime.getRuntime().exec(cmdStringContent);
			
		} catch (IOException e2) {
		}
	
		int exitValue = Integer.MAX_VALUE;
		while (exitValue==Integer.MAX_VALUE){
			try {
				exitValue=Math.min(pMeta.exitValue(),pContent.exitValue());
				
			} catch (RuntimeException e1) {
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e2) {
					System.err.println(" Sleepexception  "+e2.getMessage());
				}
			}
		}
		
		InputStream errorMeta = pMeta.getErrorStream();
		InputStream errorContent = pContent.getErrorStream();
		String er = generateError(errorMeta, errorContent);
		
		if (er.length()>0)
		CCController.dialogErrorOccured(
				"Exception: ",
				"<html>IOException<p>"+er+"</html>",
				JOptionPane.ERROR_MESSAGE);
		
			if (CCController.dummyfile.equals(this.controller.getModel().DUMMYFILE)){
	
				File fMeta = new File(meta);
				 if (fMeta.exists()) fMeta.delete();
	
				 File fContent = new File(content);
				 if (fContent.exists()) fContent.delete();
	
			}
	
		}

	/**
	 * @param pMeta
	 * @param pContent
	 * @param er
	 * @return
	 */
	private String generateError(InputStream errorMeta, InputStream errorContent ) {
		String er = "";
		
		int errorMetaAvailable=0;
		try {
			errorMetaAvailable = errorMeta.available();
		} catch (IOException e1) {
			er = er.concat(e1.getMessage());
		}
		
		if (errorMetaAvailable>10){
			byte[] e=null;
			try {
				e = new byte[ (int) errorMeta.available() ]; 
				errorMeta.read(e);
				errorMeta.close();
			} catch (IOException e2) {
				
				e2.printStackTrace();
			}
			er= er.concat(new String(e));
		}
		
		int errorContentAvailable=0;
		try {
			errorContentAvailable = errorContent.available();
		} catch (IOException e1) {
			er = er.concat(e1.getMessage());
		}
		if (errorContentAvailable>10){
			byte[] e=null;
			try {
				e = new byte[ (int) errorMeta.available() ]; 
				errorMeta.read(e);
				errorMeta.close();
			} catch (IOException e2) {
			}
			er= er.concat(new String(e));
		}
		return er;
	}

}

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
 * klasse , mit der ein Thread gestartet wird um eine .tex datei<br>
 *  mit mmtex und mmxalan zu uebersetzen.
 */
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JOptionPane;

import net.mumie.coursecreator.CCController;

/**
 * this class compiles a tex file to a xhtmlfile
 * @author vrichter
 *
 */
public class HelpCompiler implements Runnable{

	private String file;
	
	/**
	 * 
	 * @param f die zu uebersetzende tex datei<br>
	 * <b>ohne Endung</b>, also <br>
	 * new HelpCompiler("foo");
	 *   statt<br> 
	 * new HelpCompiler("foo.tex");
	 */
	public HelpCompiler(String f) {
		this.file = f;
	}

	/**
	 * starts the process cmd input
	 * @param cmd
	 * @param input
	 * @return true if everything is fine
	 */
	private boolean startprocess(String cmd , String input){
		
		String cmdString = cmd+" " + input;
		Process p=null;
		try {				
			p = Runtime.getRuntime().exec(cmdString);
			
		} catch (IOException e2) {
			JOptionPane.showMessageDialog(
	    			CCController.frame,"<html>IOException<p>"+e2+"</html>", "Exception: ",JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		int exitValue = Integer.MAX_VALUE;
		while (exitValue==Integer.MAX_VALUE){
			try {
				exitValue=p.exitValue();
				
			} catch (RuntimeException e1) {
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e2) {
					System.err.println(" Sleepexception  "+e2.getMessage());
				}
			}
		}
		
		InputStream errorMeta = p.getErrorStream();
		String er = generateError(errorMeta);
		
		if (er.length()>0){
			CCController.dialogErrorOccured(
					"Exception: ",
					"<html>IOException<p>"+er+"</html>",
					JOptionPane.ERROR_MESSAGE);
		
			return false;
		}
		return true;
	}
	
	
	/**
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		String tex = this.file.concat(".tex");
		String cmdtex = "mmtex";
		String xalan = this.file.concat(".xml");
		String cmdxalan = "mmxalan";
		
		if (this.startprocess(cmdtex, tex))
		 this.startprocess(cmdxalan, xalan);
	}	

	/**
	 * generates the error from process
	 * @param errorMeta
	 * @return
	 */
	private String generateError(InputStream errorMeta) {
		String er = "";
		int errorMetaAvailable=0;
		try {
			errorMetaAvailable = errorMeta.available();
		} catch (IOException e1) {
			System.out.println("sth went wrong");
			er = er.concat(e1.getMessage());
			return er;
		}
		
		if (errorMetaAvailable>0){
			byte[] e=null;
			try {
				e = new byte[ (int) errorMeta.available() ]; 
				errorMeta.read(e);
				errorMeta.close();
			} catch (IOException e2) {
				System.out.println("sth went wrong");
				e2.printStackTrace();
				return e2.toString();
			}
			er= er.concat(new String(e));
		}
		
		return er;
	}

}

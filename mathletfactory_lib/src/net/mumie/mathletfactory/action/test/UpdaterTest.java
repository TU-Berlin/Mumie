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

package net.mumie.mathletfactory.action.test;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.action.updater.DependencyAdapter;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.number.MMDouble;

public class UpdaterTest extends JFrame {

	DependencyAdapter dep1, dep2;

	UpdaterTest() {
		
		dep1 = new DependencyAdapter() {
			public void doUpdate(MMObjectIF dependant, MMObjectIF free) {
				System.out.println("update A");
			}
		};
		dep2 = new DependencyAdapter() {
			public void doUpdate(MMObjectIF dependant, MMObjectIF free) {
				System.out.println("update B");
			}
		};
		test1();
		test2();
		test3();
	}
	
	void test1() {
		MMDouble A = new MMDouble(0);
		MMDouble B = new MMDouble(1);

		System.out.println();
		System.out.println("Starting test 1...");

		System.out.println("Adding updater to B...");
		A.dependsOn(B, dep1);
		System.out.println();

		System.out.println("Invoking updaters on B...");
		B.invokeUpdaters();
		System.out.println();

		System.out.println("Invoking updaters on A...");
		A.invokeUpdaters();
		System.out.println();

		System.out.println("Performing action cycle from B...");
		ActionManager.performActionCycleFromObject(B);
		System.out.println();

		System.out.println("Exiting test 1...");
	}

	void test2() {
		MMDouble A = new MMDouble(0);
		MMDouble B = new MMDouble(1);

		System.out.println();
		System.out.println("Starting test 2...");

		System.out.println("Adding updater to B...");
		A.dependsOn(B, dep1);
		System.out.println();

		System.out.println("Adding updater to A...");
		B.dependsOn(A, dep2);
		System.out.println();

		System.out.println("Invoking updaters on B...");
		B.invokeUpdaters();
		System.out.println();

		System.out.println("Invoking updaters on A...");
		A.invokeUpdaters();
		System.out.println();

		System.out.println("Invoking updaters on B...");
		B.invokeUpdaters();
		System.out.println();

		System.out.println("Invoking updaters on A...");
		A.invokeUpdaters();
		System.out.println();

		System.out.println("Performing action cycle from B...");
		ActionManager.performActionCycleFromObject(B);
		System.out.println();
	
		System.out.println("Performing action cycle from A...");
		ActionManager.performActionCycleFromObject(A);
		System.out.println();

		System.out.println("Performing action cycle from B...");
		ActionManager.performActionCycleFromObject(B);
		System.out.println();
	
		System.out.println("Exiting test 2...");
	}
	
	void test3() {
		MMDouble A = new MMDouble(0);
		A.setEditable(true);
		MMDouble B = new MMDouble(1);
		B.setEditable(true);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel p = new JPanel();
		p.add(new JLabel("A="));
		p.add(A.getAsContainerContent());
		p.add(new JLabel(" , B="));
		p.add(B.getAsContainerContent());
		frame.getContentPane().add(p);
		frame.pack();
		frame.setSize(200, 100);
		frame.setVisible(true);
		
		System.out.println();
		System.out.println("Starting test 3...");

		System.out.println("Adding updater to B...");
		A.dependsOn(B, dep1);
		System.out.println();
		
		System.out.println("Invoking updaters on B...");
		B.invokeUpdaters();
		System.out.println();

		System.out.println("Exiting test 3...");
	}

	public static void main(String[] args) {
		new UpdaterTest();
	}

}

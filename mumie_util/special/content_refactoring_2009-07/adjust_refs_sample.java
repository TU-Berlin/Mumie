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

package net.mumie.mathlet.linalg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.mumie.mathletfactory.action.updater.DependencyAdapter;
import net.mumie.mathletfactory.action.updater.DependencyUpdater;
import net.mumie.mathletfactory.appletskeleton.NoCanvasApplet;
import net.mumie.mathletfactory.appletskeleton.util.ControlPanel;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MOpNumber;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMNumberMatrix;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMNumberTuple;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLineSegment;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;
import net.mumie.mathletfactory.mmobject.number.MMDouble;
import net.mumie.mathletfactory.mmobject.number.MMInteger;
import net.mumie.mathletfactory.mmobject.util.MMString;
import net.mumie.mathletfactory.util.CanvasMessage;
import net.mumie.mathletfactory.util.exercise.MultipleTasksIF;
import net.mumie.mathletfactory.util.exercise.MumieExercise;
/**
 * exercise for markov matrices
 * 
 * @author Michael Heimann <heimann@math.tu-berlin.de>
 * @mm.type applet
 * @mm.section content/lineare_algebra/media/applets/1_ohne_geometrische_struktur/1_2_lineare_abbildungen_und_matrizen
 * @mm.thumbnail content/lineare_algebra/media/applets/1_ohne_geometrische_struktur/1_2_lineare_abbildungen_und_matrizen/thumbnails/g_tmb_MarkovMatrix.meta.xml
 * @mm.copyright Copyright (c) 2007 by Technische Universitaet Berlin
 * @mm.requireJar system/libraries/jar_mathlet_factory.meta.xml
 * @mm.sign true
 * @mm.docstatus finished
 * @mm.status devel_ok
 * @mm.description Uebung zu Markovmatrizen
 * @mm.changelog none
 * @mm.width 750
 * @mm.height 750
 */
public class MarkovMatrix extends NoCanvasApplet implements MultipleTasksIF {

	private ExercisePanel exercise;
	private MumieExercise mumieExercise;
	private int Nr=1;
	private final Class numberClass = MOpNumber.class;
	//exercise stuff
	public MMString exerciseLabel;
	public ControlPanel task1Panel;
	public ControlPanel task2Panel,task3Panel;
	public JPanel taskPanelCenter;
	public MMNumberMatrix matrixP;
	public MMNumberTuple x0,x1,x2;
	private MMNumberTuple xk,xk1;
	public VectorComponents x0_canvas,x1_canvas,x2_canvas,xk_canvas,xk1_canvas;
	public MMInteger k;
	private JButton forward,backward;
	private MMNumberTuple x0_sub3;
	private ControlPanel displayWarning_sub3,displayWarning_sub2;
	private final int maxValue=10000;
	private boolean matrixPsaved;
	
	//work-around wegen canvas-problematik!!! ->maxValue, diplayWarning_sub2 und diplayWarning_sub3, VectorComponents.displayed
	
	public void init(){
		CVS_REVISION = "$Revision: 1.1 $";
		CVS_DATE = "$Date: 2009/07/15 22:43:59 $";
		try{	
			super.init();
			setTitle("<html>Markov-Matrix</html>");
			if(isHomeworkMode()){
				mumieExercise = new MumieExercise(this);
				exercise = new ExercisePanel();
				loadAnswersAndElements();
				getControlTabbedPanel().addTab(((ExercisePanel) exercise).TITLE,exercise);
			}
			getControlTabbedPanel().getTabbedPane().remove(0);
			addDynamicResetButton();
		}catch(Throwable t){reportError(t);}
	}
	
	class VectorComponents extends MMG2DCanvas{
		private MMAffine2DLineSegment[] lines;
		private MMAffine2DPoint[] points;
		private MMAffine2DLineSegment dummy, xaxes;
		int dim;
		double height=3;
		private String xLabel="";
		private boolean scale=true;
		private boolean displayed=false;
		public VectorComponents(NumberTuple nt,String xlab, boolean autoscale){
			xLabel=xlab;
			init(nt);
		}
		public VectorComponents(NumberTuple nt, boolean autoscale){
			scale=autoscale;
			init(nt);
		}
		private void init(NumberTuple nt){
			dim=nt.getRowCount();
			getW2STransformationHandler().setWorldDim(dim+1,height);
			getW2STransformationHandler().setWorldCentreCoordinates((dim+1)/2.0,0);
			lines=new MMAffine2DLineSegment[dim];
			points=new MMAffine2DPoint[dim];
			dummy = new MMAffine2DLineSegment(new MMAffine2DPoint(MDouble.class,0.5,-height/2.0),new MMAffine2DPoint(MDouble.class,dim+1,height/2.0));
			dummy.getDisplayProperties().setTransparency(0.0001);
			xaxes = new MMAffine2DLineSegment(new MMAffine2DPoint(MDouble.class,0.5,0),new MMAffine2DPoint(MDouble.class,dim+0.5,0));
			xaxes.getDisplayProperties().setTransparency(0.4);
			addObject(dummy);
			addObject(xaxes);

			CanvasMessage cm = new CanvasMessage(this,xLabel,
	  				0, CanvasMessage.CENTERED, CanvasMessage.PLAIN_MESSAGE);
			addMessage(cm);
			for(int i=0;i<dim;i++){
				lines[i]=new MMAffine2DLineSegment(new MMAffine2DPoint(MDouble.class,i+1,0),new MMAffine2DPoint(MDouble.class,i+1,nt.getEntry(i+1).getDouble()));
				points[i]=new MMAffine2DPoint(MDouble.class,i+1,0);
				lines[i].setObjectColor(Color.GREEN.darker());
				lines[i].getDisplayProperties().setTransparency(0.3);
				points[i].setObjectColor(Color.GREEN.darker());
				points[i].setLabel(String.valueOf(i+1));
				addObject(lines[i]);
				addObject(points[i]);
				if(nt.isEdited(i+1)&&Math.abs(nt.getEntry(i+1).abs().getDouble())<maxValue){
					if(displayed==false)displayed=true;
					lines[i].setVisible(true);
					points[i].setVisible(true);
				}else{
					lines[i].setVisible(false);
					points[i].setVisible(false);
				}
			}
		}
		public void updateVector(NumberTuple nt){
			double max=height/2.0;
			displayed=false;
			for(int i=0;i<dim;i++){
				if(nt.isEdited(i+1)&&Math.abs(nt.getEntry(i+1).getDouble())<=maxValue){
					if(displayed==false)displayed=true;
					lines[i].setEndPoint(i+1,nt.getEntry(i+1).getDouble());
					if(lines[i].isVisible()==false)lines[i].setVisible(true);
					lines[i].render();
					points[i].setXY(i+1,nt.getEntry(i+1).getDouble());
					if(points[i].isVisible()==false)points[i].setVisible(true);
					points[i].render();
					if(scale)max=Math.max(max,Math.abs(nt.getEntry(i+1).getDouble()));
				}else{
					if(lines[i].isVisible()==true)lines[i].setVisible(false);
					lines[i].render();
					if(points[i].isVisible()==true)points[i].setVisible(false);
					points[i].render();
				}
			}
		}
		public void scale(double max){
			if(displayed){
				if(max>maxValue)max=maxValue;
				dummy.setInitialPoint(0.5,-max-max/2.0);
				dummy.setEndPoint(dim+1,max+max/2.0);
				dummy.render();
				autoScale(false);
			}
		}
	}
	
	class ExercisePanel extends JPanel {
		public final String TITLE = "Aufgabe";
		private final int dim = 4;
		private ControlPanel cp2_1,cp2_2,cp2_3;//panel fuer vektoren in subtask 2
		private ControlPanel cp3_01,cp3_02,cp3_1,cp3_2;//panel fuer vektoren in subtask 3
		private MMString task3Label,task2Label;
		
		public ExercisePanel() {
			createObjects();
			defineDependencies();
			layoutObjects();
//			cheat();
			add(BorderLayout.NORTH,exerciseLabel.getAsContainerContent());
			JPanel  jp=new JPanel();
			jp.add(task1Panel);
			jp.add(task2Panel);
			jp.add(task3Panel);
			add(BorderLayout.CENTER,jp);
			ControlPanel cp1=new ControlPanel();
			cp1.add(mumieExercise.getSendButton());
			add(BorderLayout.SOUTH,cp1);
		}
		private void createObjects(){
			displayWarning_sub3=new ControlPanel();
			displayWarning_sub3.addText("Hinweis: Vektorkomponenten mit Betrag > "+String.valueOf(maxValue)+" werden nicht visualisiert.", Color.red);
			displayWarning_sub3.setVisible(false);
			displayWarning_sub2=new ControlPanel();
			displayWarning_sub2.addText("Hinweis: Vektorkomponenten mit Betrag > "+String.valueOf(maxValue)+" werden nicht visualisiert.", Color.red);
			displayWarning_sub2.setVisible(false);
			matrixPsaved=false;
			
			exerciseLabel=new MMString("<html><b>Aufgabe a)");
			task1Panel=new ControlPanel();
			task2Panel=new ControlPanel();
			task3Panel=new ControlPanel();
			
			matrixP=new MMNumberMatrix(numberClass,dim);
			matrixP.setEditable(true);
			matrixP.setEdited(false);
			x0=new MMNumberTuple(numberClass,dim);
			x1=new MMNumberTuple(numberClass,dim);
			x2=new MMNumberTuple(numberClass,dim);
			x0.setEditable(false);
			x1.setEditable(false);
			x2.setEditable(false);
			x0.setEdited(false);
			x1.setEdited(false);
			x2.setEdited(false);
			cp2_1=new ControlPanel();
			cp2_2=new ControlPanel();
			cp2_3=new ControlPanel();
	
			matrixP.setLabel("P = ");
			x0.setLabel("\\vec{x_0} = ");
			x1.setLabel("\\vec{x_1} = ");
			x2.setLabel("\\vec{x_2} = ");
			x0.getDisplayProperties().setLabelDisplayed(false);
			x1.getDisplayProperties().setLabelDisplayed(false);
			x2.getDisplayProperties().setLabelDisplayed(false);
			
			x0_canvas=new VectorComponents(x0,"x_0",false);
			x1_canvas=new VectorComponents(x1,"x_1",false);
			x2_canvas=new VectorComponents(x2,"x_2",false);
			
			x0_sub3 = new MMNumberTuple(numberClass,dim);
			cp3_01=new ControlPanel();
			cp3_02=new ControlPanel();
			cp3_1=new ControlPanel();
			cp3_2=new ControlPanel();
			xk=new MMNumberTuple(numberClass,dim);
			xk1=new MMNumberTuple(numberClass,dim);
			xk.setEdited(false);
			xk1.setEdited(false);
			xk_canvas=new VectorComponents(xk,"x_k",false);
			xk1_canvas=new VectorComponents(xk1,"x_k+1",false);
			task3Label=new MMString("<html>Geben Sie zuerst die Matrix P in Aufgabe a) vollst&auml;ndig an und speichern sie diese.</html>");
			task2Label=new MMString("<html>Geben Sie zuerst die Matrix P in Aufgabe a) vollst&auml;ndig an und speichern sie diese.</html>");
			k=new MMInteger();
			k.setEdited(false);
			k.setLabel("k = ");
			forward = new JButton(">>");
			backward = new JButton("<<");
			forward.setEnabled(false);
			backward.setEnabled(false);
			ActionListener button1Listener = new ActionListener(){
			    public void actionPerformed( ActionEvent e ){
			    	k.setInteger(k.getIntValue()+1);
			    	DependencyUpdater.performActionCycleFromObject(k);
			    }
	  	  	};
	  	  	ActionListener button2Listener = new ActionListener(){
			    public void actionPerformed( ActionEvent e ){
			    	if(k.getIntValue()>0){
			    		k.setInteger(k.getIntValue()-1);
			    		DependencyUpdater.performActionCycleFromObject(k);
			    	}
			    }
	  	  	};
	  	  	forward.addActionListener(button1Listener);
	  	  	backward.addActionListener(button2Listener);
		}
		private void layoutObjects(){
			setLayout(new BorderLayout());
			//subtask 1
			task1Panel.insertLineBreaks(3);
			task1Panel.add(matrixP.getAsContainerContent());
			
			//subtask 2
			cp2_1.addText("\\vec{x_0} =");
			cp2_1.add(x0.getAsContainerContent());
			cp2_2.addText("\\vec{x_1} =");
			cp2_2.add(x1.getAsContainerContent());
			cp2_3.addText("\\vec{x_2} =");
			cp2_3.add(x2.getAsContainerContent());
			ControlPanel task2PanelSouth = new ControlPanel();
			task2PanelSouth.add(task2Label.getAsContainerContent());
			task2PanelSouth.insertLineBreak();
			task2PanelSouth.add(displayWarning_sub2);
			JPanel task2PanelNorth = new JPanel();
			GridBagLayout gbl=new GridBagLayout();
			task2PanelNorth.setLayout(gbl);
			GridBagConstraints gbc=new GridBagConstraints();
			gbc.insets = new Insets(5,5,5,5);
			gbc.fill=GridBagConstraints.NONE;
			gbc.anchor=GridBagConstraints.EAST;
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridheight = 1;
			gbc.gridwidth = 1;
			gbl.columnWidths=new int[]{100,400};
			gbl.rowHeights=new int[]{100,100,100};
			gbl.setConstraints(cp2_1, gbc);
			task2PanelNorth.add(cp2_1);
			gbc.gridy = 1;
			gbl.setConstraints(cp2_2, gbc);
			task2PanelNorth.add(cp2_2);
			gbc.gridy = 2;
			gbl.setConstraints(cp2_3, gbc);
			task2PanelNorth.add(cp2_3);	
			gbc.fill=GridBagConstraints.BOTH;
			gbc.weightx=100;
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbl.setConstraints(x0_canvas, gbc);
			task2PanelNorth.add(x0_canvas);
			gbc.gridy = 1;
			gbl.setConstraints(x1_canvas, gbc);
			task2PanelNorth.add(x1_canvas);
			gbc.gridy = 2;
			gbl.setConstraints(x2_canvas, gbc);
			task2PanelNorth.add(x2_canvas);
			task2Panel.add(task2PanelNorth);
			task2Panel.insertLineBreak();
			task2Panel.add(task2PanelSouth);
			
			//subtask 3
			cp3_01.setLeftAlignment();
			cp3_02.setLeftAlignment();
			cp3_1.setLeftAlignment();
			cp3_2.setLeftAlignment();
			cp3_02.insertTab();
			cp3_01.addText("\\vec{x_0} =");
			cp3_01.add(x0_sub3.getAsContainerContent());
			cp3_02.addText("\\vec{x_{k+1}}:= P\\vec{x_k},");
			cp3_02.insertTab();
			cp3_02.addText("P\\vec{x_k} = P_k\\vec{x_0}");
			cp3_1.addText("\\vec{x_k} =");
			cp3_1.add(xk.getAsContainerContent());
			cp3_2.addText("\\vec{x_{k+1}} =");
			cp3_2.add(xk1.getAsContainerContent());

			ControlPanel task3PanelSouth = new ControlPanel();
			task3PanelSouth.insertLineBreak();
			task3PanelSouth.add(task3Label.getAsContainerContent());
			task3PanelSouth.insertLineBreak();
			task3PanelSouth.add(displayWarning_sub3);
			task3PanelSouth.insertLineBreak();
			task3PanelSouth.add(backward);
			task3PanelSouth.add(forward);
			task3PanelSouth.insertHSpace(30);
			task3PanelSouth.add(k.getAsContainerContent());
			JPanel task3PanelNorth = new JPanel();
			GridBagLayout gbl2=new GridBagLayout();
			task3PanelNorth.setLayout(gbl2);
			GridBagConstraints gbc2=new GridBagConstraints();
			gbc2.insets = new Insets(5,5,5,5);
			gbc2.fill=GridBagConstraints.NONE;
			gbc2.anchor=GridBagConstraints.EAST;
			gbc2.gridx = 0;
			gbc2.gridy = 0;
			gbc2.gridheight = 1;
			gbc2.gridwidth = 1;
			gbl2.columnWidths=new int[]{100,400};
			gbl2.rowHeights=new int[]{130,130,130};
			gbl2.setConstraints(cp3_01, gbc2);
			task3PanelNorth.add(cp3_01);
			gbc2.gridy = 1;
			gbl2.setConstraints(cp3_1, gbc2);
			task3PanelNorth.add(cp3_1);
			gbc2.gridy = 2;
			gbl2.setConstraints(cp3_2, gbc2);
			task3PanelNorth.add(cp3_2);	
			gbc2.fill=GridBagConstraints.BOTH;
			gbc2.weightx=100;
			gbc2.gridx = 1;
			gbc2.gridy = 0;
			gbl2.setConstraints(cp3_02, gbc2);
			task3PanelNorth.add(cp3_02);
			gbc2.gridy = 1;
			gbl2.setConstraints(xk_canvas, gbc2);
			task3PanelNorth.add(xk_canvas);
			gbc2.gridy = 2;
			gbl2.setConstraints(xk1_canvas, gbc2);
			task3PanelNorth.add(xk1_canvas);
			task3Panel.add(task3PanelNorth);
			task3Panel.insertLineBreak();
			task3Panel.add(task3PanelSouth);
			
			task1Panel.setVisible(true);
			task2Panel.setVisible(false);
			task3Panel.setVisible(false);
		}
		private void defineDependencies(){
			new MMDouble().dependsOn(new MMObjectIF[] {x0,x1,x2}, new DependencyAdapter(){
				 public void doUpdate() {
					 x0_canvas.updateVector(x0);
					 x1_canvas.updateVector(x1);
					 x2_canvas.updateVector(x2);
					 displayUpdate();
				 }  
			});
			new MMDouble().dependsOn(new MMObjectIF[] {k,x0_sub3,matrixP}, new DependencyAdapter(){
				 public void doUpdate() {
					 if(matrixP.isCompletelyEdited()&&matrixPsaved){
						 task3Label.setValue("");
						 task3Label.render();
						 task2Label.setValue("");
						 task2Label.render();
						 if(getMax(2)>maxValue&&displayWarning_sub2.isVisible()==false)displayWarning_sub2.setVisible(true);
						 if(getMax(3)>maxValue&&displayWarning_sub3.isVisible()==false)displayWarning_sub3.setVisible(true);
						 if(x0.isEditable()==false){
							 x0.setEditable(true);
							 x0.render();
						 }
						 if(x1.isEditable()==false){
							 x1.setEditable(true);
							 x1.render();
						 }
						 if(x2.isEditable()==false){
							 x2.setEditable(true);
							 x2.render();
						 }
						 if(k.isEditable()==false){
							 k.setEditable(true);
							 k.render();
						 }
						 if(k.isEdited())forward.setEnabled(true);
						 else forward.setEnabled(false);
						 if(k.getIntValue()>0&&k.isEdited())backward.setEnabled(true);
						 else backward.setEnabled(false);
						 if(k.getIntValue()<0&&k.isEdited())k.setInteger(0);
					 }else{
						 task3Label.setValue("<html>Geben Sie zuerst die Matrix P in Aufgabe a) vollst&auml;ndig an und speichern sie diese.</html>");
						 task3Label.render();
						 task2Label.setValue("<html>Geben Sie zuerst die Matrix P in Aufgabe a) vollst&auml;ndig an und speichern sie diese.</html>");
						 task2Label.render();
						 if(x0.isEditable()==true){
							 x0.setEditable(false);
							 x0.render();
						 }
						 if(x1.isEditable()==true){
							 x1.setEditable(false);
							 x1.render();
						 }
						 if(x2.isEditable()==true){
							 x2.setEditable(false);
							 x2.render();
						 }
						 if(displayWarning_sub2.isVisible()==true)displayWarning_sub2.setVisible(false);
						 if(displayWarning_sub3.isVisible()==true)displayWarning_sub3.setVisible(false);
						 if(k.isEditable()==true){
							 k.setEditable(false);
						 	 k.setEdited(false);
						 	 k.render();
						 	 forward.setEnabled(false);
						 	 backward.setEnabled(false);
						 	 xk.setEdited(false);
						 	 xk1.setEdited(false);
						 	 xk.render();
						 	 xk1.render();
						 	 xk_canvas.updateVector(xk);
						 	 xk1_canvas.updateVector(xk1);
						 }
					 }
					 if(matrixP.isCompletelyEdited()&&k.isEdited()&&k.getIntValue()>=0){
						 fastmultWithVector(k.getIntValue());
						 double max = getMax(3);
						 double maxdisplayed = getMaxDisplayed(3);
						 xk_canvas.scale(maxdisplayed);
						 xk1_canvas.scale(maxdisplayed);
						 if(max>maxValue&&displayWarning_sub3.isVisible()==false)displayWarning_sub3.setVisible(true);
						 if(max<=maxValue&&displayWarning_sub3.isVisible()==true)displayWarning_sub3.setVisible(false);
					 }
				 }  
			});
		}
		public void displayUpdate(){
			double max = getMax(2);
			double maxdisplayed = getMaxDisplayed(2);
			if(x0.isEdited())x0_canvas.scale(maxdisplayed);
			if(x1.isEdited())x1_canvas.scale(maxdisplayed);
			if(x2.isEdited())x2_canvas.scale(maxdisplayed);
			if(max>maxValue&&displayWarning_sub2.isVisible()==false)displayWarning_sub2.setVisible(true);
			if(max<=maxValue&&displayWarning_sub2.isVisible()==true)displayWarning_sub2.setVisible(false);
		}
		private double getMax(int n){
			//n ist die Nummer des Subtask
			double max=0;
			if(n==2){
				if(x0.isEdited()||x1.isEdited()||x2.isEdited()){
					for(int i=1;i<=dim;i++)if(x0.isEdited(i))max=Math.max(max,Math.abs(x0.getEntry(i).getDouble()));					
					for(int i=1;i<=dim;i++)if(x1.isEdited(i))max=Math.max(max,Math.abs(x1.getEntry(i).getDouble()));
					for(int i=1;i<=dim;i++)if(x2.isEdited(i))max=Math.max(max,Math.abs(x2.getEntry(i).getDouble()));
				}else max=1.5;
			}
			if(n==3){
				if(k.isEdited()){
					for(int i=1;i<=dim;i++)max=Math.max(max,Math.abs(xk.getEntry(i).getDouble()));					
					for(int i=1;i<=dim;i++)max=Math.max(max,Math.abs(xk1.getEntry(i).getDouble()));
				}else max=1.5;
			}
			return max;
		}
		private double getMaxDisplayed(int n){
			//n ist die Nummer des Subtask
			double max=0;
			if(n==2){
				if(x0.isEdited()||x1.isEdited()||x2.isEdited()){
					for(int i=1;i<=dim;i++)if(x0.isEdited(i)&&Math.abs(x0.getEntry(i).getDouble())<=maxValue)max=Math.max(max,Math.abs(x0.getEntry(i).getDouble()));					
					for(int i=1;i<=dim;i++)if(x1.isEdited(i)&&Math.abs(x1.getEntry(i).getDouble())<=maxValue)max=Math.max(max,Math.abs(x1.getEntry(i).getDouble()));
					for(int i=1;i<=dim;i++)if(x2.isEdited(i)&&Math.abs(x2.getEntry(i).getDouble())<=maxValue)max=Math.max(max,Math.abs(x2.getEntry(i).getDouble()));
				}else max=1.5;
			}
			if(n==3){
				if(k.isEdited()){
					for(int i=1;i<=dim;i++)if(Math.abs(xk.getEntry(i).getDouble())<=maxValue)max=Math.max(max,Math.abs(xk.getEntry(i).getDouble()));					
					for(int i=1;i<=dim;i++)if(Math.abs(xk1.getEntry(i).getDouble())<=maxValue)max=Math.max(max,Math.abs(xk1.getEntry(i).getDouble()));
				}else max=1.5;
			}
			return max;
		}
/*		private void cheat(){
			matrixP.setEntry(1,1,0.4);
			matrixP.setEntry(1,2,0.2);
			matrixP.setEntry(1,3,0.2);
			matrixP.setEntry(1,4,0.2);
			matrixP.setEntry(2,1,0.2);
			matrixP.setEntry(2,2,0.4);
			matrixP.setEntry(2,3,0.2);
			matrixP.setEntry(2,4,0.2);
			matrixP.setEntry(3,1,0.2);
			matrixP.setEntry(3,2,0.2);
			matrixP.setEntry(3,3,0.4);
			matrixP.setEntry(3,4,0.2);
			matrixP.setEntry(4,1,0.2);
			matrixP.setEntry(4,2,0.2);
			matrixP.setEntry(4,3,0.2);
			matrixP.setEntry(4,4,0.4);
			matrixP.setEdited(true);
			x0.setEntry(1,1);
			x0.setEdited(true);
			matrixP.render();
			x0.render();
			x1.setEntry(1, 0.4);
			x1.setEntry(2, 0.2);
			x1.setEntry(3, 0.2);
			x1.setEntry(4, 0.2);
			x1.setEdited(true);
			x1.render();
			x2.setEntry(1, 0.28);
			x2.setEntry(2, 0.24);
			x2.setEntry(3, 0.24);
			x2.setEntry(4, 0.24);
			x2.setEdited(true);
			x2.render();
			DependencyUpdater.performActionCycleFromObject(matrixP);
		}*/

		private void fastmultWithVector(int exp){
			//x0_sub3 und matrixP ausserhalb der Methode definiert
			//Seiteneffekte auf xk und xk1
			double[] matrix = matrixP.toDoubleArray();
			double[] result = new double[dim];
			double[] result2 = new double[dim];
			//i fuer Zeilen, n fuer die Zahl der Multiplikationen
			if(exp==0)for(int m=0;m<dim;m++)result[m]=x0_sub3.getEntry(m+1).getDouble();
			for(int n=1;n<=exp+1;n++){
				double[] temp = new double[dim];
				if(n>1)for(int m=0;m<dim;m++)temp[m]=result[m];
				else for(int m=0;m<dim;m++)temp[m]=x0_sub3.getEntry(m+1).getDouble();
				for(int i=0;i<dim;i++)
					for(int l=0;l<dim;l++){
						if(n<=exp){
							if(l==0)result[i]=temp[l]*matrix[dim*i+l];
							else result[i]=result[i]+temp[l]*matrix[dim*i+l];
						}else{
							if(l==0)result2[i]=temp[l]*matrix[dim*i+l];
							else result2[i]=result2[i]+temp[l]*matrix[dim*i+l];
						}
					}
			}
			for(int i=0;i<dim;i++){
				xk.setEntry(i+1,result[i]);
				xk1.setEntry(i+1,result2[i]);
				xk.setEdited(true);
				xk1.setEdited(true);
				xk.render();
				xk1.render();
			}
			 xk_canvas.updateVector(xk);
			 xk1_canvas.updateVector(xk1);
		}
	}

//	public void resetDemo(){
//		((DemoPanel)demo).reset();
//	}
//	
//	public void resetTraining(){
//		((TrainPanel)train).reset();
//	}
	
	public void reset() {
	}

	public void selectTask(int taskNr) {
		if(taskNr==1){
			Nr = 1;
			exerciseLabel.setValue("<html><b>Aufgabe a)");
			exerciseLabel.render();
			task1Panel.setVisible(true);
			task2Panel.setVisible(false);
			task3Panel.setVisible(false);
		}
		if(taskNr==2){
			Nr = 2;
			exerciseLabel.setValue("<html><b>Aufgabe b)");
			exerciseLabel.render();
			task1Panel.setVisible(false);
			task2Panel.setVisible(true);
			task3Panel.setVisible(false);
		}
		if(taskNr==3){
			Nr = 3;
			exerciseLabel.setValue("<html><b>Aufgabe c)");
			exerciseLabel.render();
			task1Panel.setVisible(false);
			task2Panel.setVisible(false);
			task3Panel.setVisible(true);
		}
	}

	public boolean collectAnswers() {
		mumieExercise.setAnswer(1, "matrixP", matrixP);
		mumieExercise.setAnswer(2, "x0", x0);
		mumieExercise.setAnswer(2, "x1", x1);
		mumieExercise.setAnswer(2, "x2", x2);
		mumieExercise.setAnswer(3, "k", k);
		if(Nr==1&&matrixPsaved==false){
			if(matrixP.isCompletelyEdited()){
				matrixPsaved=true;
				DependencyUpdater.performActionCycleFromObject(matrixP);
			}
		}
		return true;
	}

	public void loadAnswersAndElements(){
		if(mumieExercise.problemElementExists("vector_x0")){
			mumieExercise.loadProblemElement("vector_x0",x0_sub3);
			x0_sub3.render();
		}
		if(mumieExercise.userElementExists(1,"matrixP")){
			mumieExercise.loadUserElement(1,"matrixP",matrixP);
			matrixP.render();
			matrixPsaved=true;
			DependencyUpdater.performActionCycleFromObject(matrixP);
		}
		if(mumieExercise.userElementExists(2,"x0")){
			mumieExercise.loadUserElement(2,"x0",x0);
			mumieExercise.loadUserElement(2,"x1",x1);
			mumieExercise.loadUserElement(2,"x2",x2);
			x0.render();
			x1.render();
			x2.render();
			DependencyUpdater.performActionCycleFromObject(x0);
		}
		if(mumieExercise.userElementExists(3,"k")){
			mumieExercise.loadUserElement(3,"k",k);
			k.render();
			DependencyUpdater.performActionCycleFromObject(k);
		}
	}
	
	public void clearSubtask() {
		if(Nr==1){
			matrixP.setEdited(false);
			matrixP.render();
	    	DependencyUpdater.performActionCycleFromObject(matrixP);
		}
		if(Nr==2){
			displayWarning_sub2.setVisible(false);
			x0.setEdited(false);
			x1.setEdited(false);
			x2.setEdited(false);
			x0.render();
			x1.render();
			x2.render();
			x0_canvas.updateVector(x0);
			x1_canvas.updateVector(x1);
			x2_canvas.updateVector(x2);
	    	DependencyUpdater.performActionCycleFromObject(x0);
		}
		if(Nr==3){
			displayWarning_sub3.setVisible(false);
			if(!x0.isCompletelyEdited()&&!matrixP.isCompletelyEdited())k.setEditable(false);
			k.setEdited(false);
			forward.setEnabled(false);
			backward.setEnabled(false);
			xk.setEdited(false);
			xk1.setEdited(false);
			xk_canvas.updateVector(xk);
			xk1_canvas.updateVector(xk1);
			k.render();
			xk.render();
			xk1.render();
	    	DependencyUpdater.performActionCycleFromObject(k);
		}
	}

}

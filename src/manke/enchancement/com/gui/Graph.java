package manke.enchancement.com.gui;

import java.awt.BasicStroke;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Graph extends JPanel{

	private List<List<Double>> points = new ArrayList<List<Double>>();
	private Color[] listColors = {new Color(0,0,205), new Color(180,0,0), new Color(34,139,34), new Color(128,128,0), new Color(0,206,209), Color.orange};
	
	private int sizex = 500, sizey = 800, lineThickness = 2, middleLineThickness = 3;
	
	double largestActiveValue = 0, smallestActiveValue = 0, scaley = 10;
	
	JLabel scaleLabel;
	List<JPanel> listNames = new ArrayList<JPanel>();
	GridBagConstraints gc = new GridBagConstraints();
	List<Boolean> isListActive = new ArrayList<Boolean>();
	
	public Graph(int listSize) {
		initialize();
		setListSizes(new double[] {listSize});
	}
	public Graph(double[] listSizes) {
		initialize();
		setListSizes(listSizes);
	}
	public Graph(double[] listSizes, String[] listNames) {
		initialize();
		setListSizes(listSizes);
		setListNames(listNames);
	}
	
	private void setListSizes(double[] listSizes) {
		for(int i = 0; i < listSizes.length; i++) {
			points.add(new ArrayList<Double>());
			System.out.println("Adding list");
			for(int j = 0; j < listSizes[i]; j++) {
				points.get(i).add(0.0);
			}
		}
		updateSwitchList();
	}
	
	private void setListNames(String[] names) {
		for(JPanel panel : listNames) {
			remove(panel);
		}
		for(int i = 0; i < names.length; i++) {
			JLabel newLabel = new JLabel(names[i]);
			JPanel newPanel = new JPanel();
			JButton newButton = new JButton("Switch");
			
			final int newi = i;
			
			newButton.addActionListener(e -> {switchList(newi);});

			newPanel.setLayout(new FlowLayout());
			newLabel.setForeground(listColors[i % listColors.length]);
			
			
			
			listNames.add(newPanel);
			newPanel.add(newLabel, FlowLayout.LEFT);
			newPanel.add(newButton, FlowLayout.LEFT);
			
			gc.gridx = 0;
		    gc.gridy = GridBagConstraints.RELATIVE;
		    gc.insets = new Insets(0, 4, 0, 0);
		    gc.anchor = GridBagConstraints.FIRST_LINE_START;
		    if(i == names.length - 1) {
			    gc.weightx = 1;
		        gc.weighty = 1;
		    }
		    else {
			    gc.weightx = 0;
		        gc.weighty = 0;
		    }

			add(newPanel, gc);//, FlowLayout.RIGHT);
		}
		updateSwitchList();
	}
	
	private void updateSwitchList() {
		while(isListActive.size() > listNames.size() && isListActive.size() > points.size()) {
			isListActive.remove(isListActive.size() - 1);
		}
		while(isListActive.size() < listNames.size() || isListActive.size() < points.size()) {
			isListActive.add(true);
		}
	}
	
	private void switchList(int listIndex) {
		isListActive.set(listIndex, !isListActive.get(listIndex).booleanValue());
		updateLargestValue();
		updateSmallestValue();
	}
	
	private void initialize() {
		setLayout(new GridBagLayout());
		gc.gridx = 0;
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
        gc.insets = new Insets(2, 4, 0, 0);
        gc.weightx = 0;
        gc.weighty = 0;
        
		resize(sizex, sizey);
		scaleLabel = new JLabel("Scale: " + scaley + "\t");
		scaleLabel.setAlignmentX(0);
		add(scaleLabel, gc);//, FlowLayout.LEFRIGHTT);
		updateScale();
		
	}
	
	public void resize(int sizex, int sizey) {
		this.sizex = sizex;
		this.sizey = sizey;
		setPreferredSize(new Dimension(sizex, sizey));
	}

	public void pushPoint(double point) {
		pushPoint(point, 0);
	}
	public void pushPoint(double point, int listIndex) {
		if(points.size() > listIndex) {
			double removed = points.get(listIndex).remove(0);
			if(isListActive.get(listIndex)) {
				if(largestActiveValue < point) setLargestValue(point);
				else if(removed >= largestActiveValue) updateLargestValue();
				
				if(smallestActiveValue > point) setSmallestValue(point);
				else if(removed <= smallestActiveValue) updateSmallestValue();
			}
			
			points.get(listIndex).add(point);
		}
	}
	
	private void setLargestValue(double newValue) {
		largestActiveValue = newValue;
		updateScale();
	}
	private void updateLargestValue() {
		largestActiveValue = Double.NEGATIVE_INFINITY;
		for(int i = 0; i < points.size(); i++) {
			if(isListActive.get(i)){
				for(double val : points.get(i)) {
					if( val > largestActiveValue) largestActiveValue = val;
				}
			}
		}
		updateScale();
	}

	private void setSmallestValue(double newValue) {
		smallestActiveValue = newValue;
		updateScale();
	}
	private void updateSmallestValue() {
		smallestActiveValue = Double.POSITIVE_INFINITY;
		for(List<Double> list : points) {
			for(double val : list) {
				if( val < smallestActiveValue) smallestActiveValue = val;
			}
		}
		updateScale();
	}
	private void updateScale() {
		double prevScale = scaley;
		
		if(largestActiveValue != 0 && smallestActiveValue != 0) {
			scaley = Math.max(
					Math.pow(Math.E, Math.ceil(Math.log(Math.abs(largestActiveValue)))),
					Math.pow(Math.E, Math.ceil(Math.log(Math.abs(smallestActiveValue))))
					);
		}
		else if(largestActiveValue != 0) {
			scaley = Math.pow(Math.E, Math.ceil(Math.log(Math.abs(largestActiveValue))));
		}
		else if(smallestActiveValue != 0) {
			scaley = Math.pow(Math.E, Math.ceil(Math.log(Math.abs(smallestActiveValue))));
		}
		else {
			System.out.println("Dont change scale:");
		}
		
		if(prevScale != scaley)	scaleLabel.setText("Scale: " + scaley + "\t");
		
	}
	
	private void drawList(Graphics2D g2, int listIndex){
		drawList(g2, points.get(listIndex), listColors[listIndex % listColors.length], lineThickness);
	}
	private void drawList(Graphics2D g2, List<Double> list, Color c, int thickness) {

		int px1, px2, py1, py2;
		int listSize = list.size();
		
		g2.setColor(c);
		g2.setStroke(new BasicStroke(thickness));
		
		for(int i = 0; i < listSize - 1; i++) {
			px1 = (int) (sizex / (listSize + 1) * (i + 0.5));
			py1 = (int) (sizey * 0.5 - list.get(i) / scaley * sizey * 0.5);
			px2 = (int) (sizex / (listSize + 1) * (i + 1.5));
			py2 = (int) (sizey  * 0.5 - list.get(i + 1) / scaley * sizey * 0.5);
			
			//if(i < 5 || i > 95)
			//	System.out.println("For i " + i + ", val1: " + list.get(i) + " val2: " +list.get(i + 1) + "    px1:" + px1 + " py1:" + py1 + " px2:" + px2 + " py2:" + py2);
			
			g2.drawLine(px1, py1, px2, py2);
		}
	}
	
	private void drawMiddleLine(Graphics2D g2) {

		g2.setColor(Color.black);
		g2.setStroke(new BasicStroke(middleLineThickness));
		
		g2.drawLine(0, sizey / 2, sizex, sizey / 2);
	}
	private void drawGrid(Graphics2D g2) {
		g2.setColor(Color.gray);
		g2.setStroke(new BasicStroke(2));
		
		for(int i = 0; i < 9; i++) {
			g2.drawLine(0, i * (sizey - 2) / 8 + 1, sizex, i * (sizey - 2) / 8 + 1);
		}
		g2.setColor(Color.black);
		g2.setStroke(new BasicStroke(middleLineThickness));
		g2.drawLine(0, 0, 0, sizey);
	}
	
	/*private void drawPoint(int posx1, double posy1, int posx2, double posy2, double scaley, Graphics2D g2) {
		int px1 = (int) (sizex / (dataPoints + 1) * (posx1 + 0.5));
		int py1 = (int)(scaley / posy1);
		int px2 = (int) (sizex / (dataPoints + 1) * (posx2 + 0.5));
		int py2 = (int)(scaley / posy2);
		
		g2.drawLine(px1, py1, px2, py2);
	}*/
	
	public void paintComponent( Graphics g ) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        drawGrid(g2);
        drawMiddleLine(g2);
        for(int i = points.size() - 1; i >= 0; i--) {
        	if(isListActive.get(i)) {
        		drawList(g2, i);	
        	}
        }
        
        
        /*Line2D line = new Line2D.Double(10, 10, 40, 40);
        g2.setColor(Color.blue);
        g2.setStroke(new BasicStroke(10));
        g2.draw(line);*/
     }
	
}

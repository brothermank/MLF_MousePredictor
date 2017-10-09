package manke.enchancement.com.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

import manke.enchancement.com.hardwareCapture.MouseEventListener;
import manke.enchancement.com.neural.NeuralLayer;
import manke.enchancement.com.neural.NeuralNetwork;
import manke.enchancement.com.neural.Neuron;
import manke.enchancement.com.util.PointD;

public class NetworkIllustrator extends JPanel {

	private static final long serialVersionUID = 1L;
	
	final int spacingX = 200, spacingY = 20;
	
	int sizex, sizey, neuronR = 10, connectionT = 2, maxLayerSize = 0;
	
	NeuralNetwork network;
	MouseEventListener mouseListener;
	
	public NetworkIllustrator(NeuralNetwork network) {
		mouseListener = new MouseEventListener(this);
		addMouseListener(mouseListener);
		this.network = network;
		updateSize();
	}
	
	public void setNeuralNetwork(NeuralNetwork network){
		this.network = network;
		updateSize();
	}
	
	
	private void updateSize() {
		if(network != null) {
			sizex = network.getLayerCount() * spacingX + spacingY;
			maxLayerSize = 0;
			for(int i = 0; i < network.getLayerCount(); i++) {
				if(network.getNeuronsInLayer(i).size() > maxLayerSize) maxLayerSize = network.getNeuronsInLayer(i).size();
			}
			sizey = maxLayerSize * spacingY + spacingY;
			this.setPreferredSize(new Dimension(sizex, sizey));
			
			
		}
		System.out.println("Size set to: " + getSize().getWidth() + "," + getSize().getHeight());
	}
	
	public void paintComponent( Graphics g ) {
        super.paintComponent(g);
        if(network != null) {
	        Graphics2D g2 = (Graphics2D)g;
	
	        
	        Neuron neuron;
	        
    		drawAllConnections(network.getNeuron(sx, sy), g2);
    		
	        for(int i = 0; i < network.getLayerCount(); i++) {
	        	for(int j = 0; j < network.getNeuronsInLayer(i).size(); j++	) {
	        		PointD pos = getPosOfNeuron(i, j);
	        		/*
	        		if(sx == i && sy == j) {
	        			drawAllConnections(network.getNeuron(i, j), g2);
		        		for(int k = 0; k < neuron.getOutputs().size(); k++) {
		        			drawConnection(i, j, neuron.getOutputs().get(k).getOut(), neuron.getOutputs().get(k).getWeight(), g2);
		        		}
	        		}*/
	        		drawNeuron(i, j, network.getNeuron(i, j).getValue(), g2);
	        	}
	        }
	        

	        
	        
	       
        }
        /*Line2D line = new Line2D.Double(10, 10, 40, 40);
        g2.setColor(Color.blue);
        g2.setStroke(new BasicStroke(10));
        g2.draw(line);*/
     }
	
	int sx, sy;
	
	public void getNeuronAtPos(int x, int y) {
		int layerIndex = (int)(x / spacingX);
		int dLayerSize = maxLayerSize - network.getNeuronsInLayer(layerIndex).size();
		int neuronIndex = (int)((y - dLayerSize * 0.5 * spacingY) / spacingY - 0.5);
		sx = layerIndex;
		sy = neuronIndex;
	}
	
	private PointD getPosOfNeuron(int layerIndex, int neuronIndex) {
		int dLayerSize = maxLayerSize - network.getNeuronsInLayer(layerIndex).size();
		return new PointD(spacingX * 0.5 + layerIndex * spacingX, spacingY * 0.5 + neuronIndex * spacingY + dLayerSize * 0.5 * spacingY);
	}
	private PointD getPosOfNeuron(Neuron n) {
		int layerIndex = network.getLayerIndexByID(n.getLayerID());
		int neuronIndex = network.getLayer(layerIndex).getNeuronIndexByID(n.getID());
		
		return getPosOfNeuron(layerIndex, neuronIndex);
	}
	private void drawNeuron(int layerIndex, int neuronIndex, double neuronStrength, Graphics2D g2) {
		PointD pos = getPosOfNeuron(layerIndex, neuronIndex);
		
		
		setNeuronColor(neuronStrength, g2);
		
		g2.fillOval((int)pos.getX(), (int)pos.getY(), neuronR, neuronR);    
	}
	
	private void drawConnection(int layerIndexN1, int neuronIndexN1, Neuron n2, double connectionStrength, Graphics2D g2) {
		PointD pos1 = getPosOfNeuron(layerIndexN1, neuronIndexN1);
		PointD pos2 = getPosOfNeuron(n2);
		
		Line2D line = new Line2D.Double(pos1.getX() + neuronR * 0.5, pos1.getY() + neuronR * 0.5, pos2.getX() + neuronR * 0.5, pos2.getY() + neuronR * 0.5);
		setConnectionColor(connectionStrength, g2);
		g2.setStroke(new BasicStroke(connectionT));
        g2.draw(line);
	}
	private void drawAllConnections(Neuron n, Graphics2D g2) {
		Neuron neuron;
		for(int i = 0; i < network.getLayerCount(); i++) {
        	for(int j = 0; j < network.getNeuronsInLayer(i).size(); j++	) {
    			neuron = network.getNeuron(i, j);
        		for(int k = 0; k < neuron.getOutputs().size(); k++) {
        			if(neuron.getOutputs().get(k).getOut() == n || neuron.getOutputs().get(k).getIn() == n) {
	        			drawConnection(i, j, neuron.getOutputs().get(k).getOut(), neuron.getOutputs().get(k).getWeight(), g2);
        			}
        		}
        	}
        }
	}
	
	
	private void setConnectionColor(double val, Graphics2D g2) {
		double mod = 1;
		if(val >= 0) {
			Color color = new Color(0, (int) (255 * (1 - Math.exp(-val * mod))), 0);
			g2.setColor(color);
		}else {
			Color color = new Color((int) (255 * (1 - Math.exp(val * mod))), 0, 0);
			g2.setColor(color);
		}
	}
	private void setNeuronColor(double val, Graphics2D g2) {
		double mod = 1;
		if(val >= 0) {
			Color color = new Color(0, 0, (int) (255 * (1 - Math.exp(-val * mod))));
			g2.setColor(color);
		}else {
			//Color color = new Color((int) (255 * (1 - Math.exp(val * mod))), (int) (255 * (1 - Math.exp(-val * mod))), 0);
			//g2.setColor(color);
		}
	}

	
	
}

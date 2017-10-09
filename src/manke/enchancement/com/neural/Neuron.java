package manke.enchancement.com.neural;

import java.util.ArrayList;
import java.util.List;

import manke.enchancement.com.util.Rand;

public class Neuron {
	
	protected double thresh = 0;
	private double value = 0;
	protected double nextValue = 0;
	private double input = 0;
	private final int ID, LayerID;
	
	private List<NeuronConnection> outputs = new ArrayList<NeuronConnection>();
	
	public Neuron(int ID, int LayerID) {
		this.ID = ID;
		this.LayerID = LayerID;
	}
	
	public void setValue(double value) {
		nextValue = value;
		updateOutput(false, false);
	}
	public double getValue() {
		return value;
	}
	public double getInput() {
		return input;
	}
	public List<NeuronConnection> getOutputs(){
		return outputs;
	}
	public void setThresh(double newThresh) {
		thresh = newThresh;
	}

	public int getID() {
		return ID;
	}
	public int getLayerID() {
		return LayerID;
	}
	
	public List<NeuronConnection> connectLayer(NeuralLayer l) {
		List<NeuronConnection> newConnections = new ArrayList<NeuronConnection>();
		for(Neuron n : l.getNeurons()) {
			outputs.add(new NeuronConnection(this, n));
			newConnections.add(outputs.get(outputs.size() - 1));
		}
		return newConnections;
	}
	public NeuronConnection connectNeuron(Neuron n) {
		outputs.add(new NeuronConnection(this, n));
		return outputs.get(outputs.size() - 1);
	}
	public NeuronConnection connectNeuron(Neuron n, double weight) {
		outputs.add(new NeuronConnection(this, n, weight));
		return outputs.get(outputs.size() - 1);
	}
	
	public void updateInput(NeuronConnection c, boolean cascade) {
		input -= c.oldValue;
		input += c.newValue;
		if(cascade) updateOutput(cascade, true);
	}
	
	public void updateOutput(boolean cascade, boolean sigmoidMask) {
		if(sigmoidMask) {
			// if(input < thresh) nextValue = 1;
			if(sigmoidMask) nextValue = sigmoidMask(nextValue);
		}
		if(nextValue != value) {
			value = nextValue;
			for(NeuronConnection c : outputs) {
				c.updateValue(cascade);
			}
		}
	}
	public void updateConnection(boolean cascade, NeuronConnection c) {
		for(NeuronConnection cout : outputs) {
			if(c == cout) cout.updateValue(cascade);
		}
	}
	
	public void randomiseThreshold() {
		thresh = Rand.randomDouble(-outputs.size() * 0.25, outputs.size() * 0.25);
	}
	
	public String getCharacteristicString() {
		return "ID:" + ID + ";Thr:" + thresh + ";\n";
	}
	public String getInputListString() {
		String output = "IDI:" + ID + ";";
		for(NeuronConnection i : outputs) {
			output += "" + i.getOut().getID() + ";" + i.getWeight() + ";";
		}
		output += "\n";
		return output;
	}
	
	private double sigmoidMask(double input) {
		return 1 / (1 + Math.exp(-input));
	}
	
	
	
}

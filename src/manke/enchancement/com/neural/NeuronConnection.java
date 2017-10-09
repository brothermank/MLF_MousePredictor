package manke.enchancement.com.neural;

import javax.tools.DocumentationTool.DocumentationTask;

import manke.enchancement.com.util.Rand;

public class NeuronConnection {

	private Neuron in, out;
	private double weight;
	double oldValue, newValue;
	
	public NeuronConnection(Neuron inputNeuron, Neuron outputNeuron) {
		in = inputNeuron;
		out = outputNeuron;
		weight = Rand.randomDouble() * 2 - 1;
	}
	public NeuronConnection(Neuron inputNeuron, Neuron outputNeuron, double weight) {
		in = inputNeuron;
		out = outputNeuron;
		this.weight = weight;
	}

	public Neuron getOut() {
		return out;
	}
	public Neuron getIn() {
		return in;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public void updateValue(boolean cascade) {
		newValue = in.getValue() * weight;
		out.updateInput(this, cascade);
		oldValue = newValue;
	}
	
	public void mutate(double mutStr) {
		weight += Rand.randomDouble(-mutStr, mutStr);
	}
	
}

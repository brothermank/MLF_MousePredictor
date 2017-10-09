package manke.enchancement.com.neural;

import java.util.ArrayList;
import java.util.List;

import manke.enchancement.com.util.Indexer;

public class NeuralLayer {
	
	private List<Neuron> neurons = new ArrayList<Neuron>();
	private final int ID;
	
	Indexer ix;

	public NeuralLayer(Indexer networkIndexer, int ID) {
		ix = networkIndexer;
		this.ID = ID;
	}
	public NeuralLayer(int neuronCount, Indexer networkIndexer, int ID) {
		ix = networkIndexer;
		this.ID = ID;
		for(int i = 0; i < neuronCount; i++) {
			neurons.add(new Neuron(ix.next(), ID));
		}
	}
	
	public List<NeuronConnection> connectLayer(NeuralLayer l, boolean randomiseNeronThresh) {
		List<NeuronConnection> newConnections = new ArrayList<NeuronConnection>();
		for(Neuron ni : neurons) {
			newConnections.addAll(ni.connectLayer(l));
			if(randomiseNeronThresh) ni.randomiseThreshold();
			
		}
		return newConnections;
	}
	
	public List<Neuron> getNeurons(){
		return neurons;
	}
	public Neuron getNeuron(int neuronIndex) {
		return neurons.get(neuronIndex);
	}
	public Neuron getNeuronByID(int ID) {
		for(Neuron n : neurons) {
			if(n.getID() == ID) return n;
		}
		return null;
	}
	public int getNeuronIndexByID(int ID) {
		for(int i = 0; i < neurons.size(); i++) {
			if(neurons.get(i).getID() == ID) return i;
		}
		return -1;
	}
	
	public void addNeuron(Neuron n) {
		neurons.add(n);
	}
	public int getID() {
		return ID;
	}
	
	public void updateNeurons() {
		for(Neuron n : neurons) {
			n.updateOutput(false, true);
		}
	}
	
	public List<String> getCharacteristicStrings(){
		List<String> strings = new ArrayList<String>();
		strings.add("IDLC:" + ID + "{\n");
		for(Neuron n : neurons) {
			strings.add(n.getCharacteristicString());
		}
		strings.add("}\n");
		return strings;
		
	}
	public List<String> getInputListStrings(){
		List<String> strings = new ArrayList<String>();
		strings.add("IDLI:" + ID + "{\n");
		for(Neuron n : neurons) {
			strings.add(n.getInputListString());
		}
		strings.add("}\n");
		return strings;
	}
	
}

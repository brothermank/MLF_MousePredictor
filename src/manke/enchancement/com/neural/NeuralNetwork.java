package manke.enchancement.com.neural;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import manke.enchancement.com.util.Indexer;
import manke.enchancement.com.util.PointD;
import manke.enchancement.com.util.Rand;

public class NeuralNetwork {

	
	
	List<NeuralLayer> layers = new ArrayList<NeuralLayer>();
	List<NeuronConnection> allConnections = new ArrayList<NeuronConnection>();
	
	Indexer ix = new Indexer();
	
	public double mutStr = 0.001;
	
	private double lastPerformanceRating = 0;
	private PointD lastPredicted = new PointD();
	private PointD lastTarget = new PointD();
	private PointD target = new PointD();
	
	public NeuralNetwork() {
		
	}
	
	public void addLayer(int layerNeuronCount) {
		layers.add(new NeuralLayer(layerNeuronCount, ix, ix.next()));
	}
	public void addLayer(NeuralLayer layer) {
		layers.add(layer);
	}
	
	public void linkLayers(int inputLayerIndex, int outputLayerIndedx) {
		allConnections.addAll(layers.get(inputLayerIndex).connectLayer(layers.get(outputLayerIndedx), true));
	}
	public void linkLayers(NeuralLayer inputLayer, NeuralLayer outputLayer) {
		allConnections.addAll(inputLayer.connectLayer(outputLayer, false));
	}
	
	public void updateLayers() {
		for(int i = 1; i < layers.size(); i++) {
			layers.get(i).updateNeurons();
		}
	}
	
	public double getPerformance() {
		return lastPerformanceRating;
	}
	public PointD getPrediction() {
		return lastPredicted;
	}
	public void setPrediction(PointD newPrediction){
		lastPredicted = newPrediction;
	}
	public void updatePrediction() {
		List<Neuron> outNeurons = layers.get(layers.size() - 1).getNeurons();
		lastPredicted.setPos(outNeurons.get(0).getInput(), outNeurons.get(1).getInput());
	}
	public PointD getTarget() {
		return lastTarget;
	}
	public void setTarget(PointD newTarget) {
		lastTarget = target;
		target = newTarget;
	}
	
	public NeuralLayer getLayerByID(int ID) {
		for(NeuralLayer layer : layers) {
			if(layer.getID() == ID) return layer;
		}
		return null;
	}
	public int getLayerIndexByID(int ID) {
		for(int i = 0; i < layers.size(); i++) {
			if(layers.get(i).getID() == ID) return i;
		}
		return -1;
	}
	public int getLayerCount() {
		return layers.size();
	}
	public NeuralLayer getLayer(int index) {
		return layers.get(index);
	}
	
	public List<Neuron> getNeuronsInLayer(int layerIndex){
		return layers.get(layerIndex).getNeurons();
	}
	public Neuron getNeuron(int layerIndex, int neuronIndex) {
		return layers.get(layerIndex).getNeuron(neuronIndex);
	}
	public Neuron getNeuronByID(int ID) {
		for(NeuralLayer layer : layers) {
			Neuron n = null;
			if((n = layer.getNeuronByID(ID)) != null) return n;
		}
		return null;
	}
	public void registerConnection(NeuronConnection c) {
		allConnections.add(c);
	}
	public NeuronConnection getRandomConnection() {
		return allConnections.get(Rand.randomInt(0, allConnections.size() - 1));
	}
	
	public double getAdjustedError() {
		double rawError = getRawError();
		return rawError * 0.25 + rawError * getPredictionError();
	}
	public double getPredictionError() {
		return Math.abs(getOutput(2) - getRawError());
	}
	public double getRawError() {
		return Math.sqrt(Math.pow(getOutput(0) - target.getX(), 2) + Math.pow(getOutput(1) - target.getY(), 2));
	}
	public double errorPrediction() {
		return getOutputLayer().get(2).getInput();
	}
	private List<Neuron> getOutputLayer(){
		return layers.get(layers.size() - 1).getNeurons();
	}
	private double getOutput(int outputIndex) {
		return getOutputLayer().get(outputIndex).getInput();
	}
	private double[] getOutputs() {
		List<Neuron> outNeurons = layers.get(layers.size() - 1).getNeurons();
		double[] outputs = new double[outNeurons.size()];
		for(int i = 0; i < outNeurons.size(); i++) {
			outputs[i] = outNeurons.get(i).getInput();
		}
		return outputs;
	}

	public void trainRandomNeuron(double[] expectedOutputs) {
		//Note current performance
		double prevError = getAdjustedError();
		double[] prevOutputs = getOutputs();
		
		//Mutate random connection
		NeuronConnection mutationTarget = getRandomConnection();
		double prevWeight = mutateConnection(mutationTarget);
		
		//Note new performance
		double newError = getAdjustedError();
		
		//Choose better performance
		if(newError > prevError) {
			//Revert to previous state
			revertMutation(mutationTarget, prevWeight);
			setPrediction(new PointD(prevOutputs[0], prevOutputs[1]));
			lastPerformanceRating = prevError;
		}
		else {
			//Keep new state
			updatePrediction();
			lastPerformanceRating = newError;
		}
	}
	
	private double mutateConnection(NeuronConnection mutTarget) {
		double prevWeight = mutTarget.getWeight();
		mutTarget.mutate(mutStr);
		mutTarget.updateValue(true);
		return prevWeight;
	}
	private void revertMutation(NeuronConnection mutTarget, double prevWeight) {
		mutTarget.setWeight(prevWeight);
		mutTarget.updateValue(true);
	}
	
	//################ SAVING ################
	
	public List<String> getNetworkString() {
		List<String> strings = new ArrayList<String>();
		strings.add("IXI:" + ix.current() + ";\n");
		for(NeuralLayer l : layers) {
			strings.addAll(l.getCharacteristicStrings());
		}
		for(NeuralLayer l : layers) {
			strings.addAll(l.getInputListStrings());
		}
		return strings;
	}
	
	public void saveNeuralNetwork(String name) {
		BufferedWriter bw = null;
		FileWriter fw = null;

		try {

			List<String> lines = getNetworkString();
			
			fw = new FileWriter(name + ".nnw");
			bw = new BufferedWriter(fw);
			for(int i = 0; i < lines.size(); i++) {
				bw.write(lines.get(i));
			}

			System.out.println("Done");

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}
	}
	
	public static NeuralNetwork loadNeuralNetwork(String path){
		NeuralNetwork nn = new NeuralNetwork();
	    String line;
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			line = br.readLine();
			int a = 0, b = 0;
		    
		    a = line.indexOf("IXI:") + 4;
		    b = line.indexOf(";", a);
		    
		    nn.ix = new Indexer(Integer.parseInt(line.substring(a, b)));
	        line = br.readLine();
		    
		    while (line != null) {
		    	//Initialize layers
		    	while(line != null && (a = line.indexOf("IDLC:") + 5) != 4) { //Find location of ID for next layer, if it exists. Otherwise proceed to make connections.
				    b = line.indexOf("{", a);
			    	NeuralLayer l = new NeuralLayer(nn.ix, Integer.parseInt(line.substring(a, b))); //Make layer
			        line = br.readLine();

		        	//Make neurons for layer
			        while(line != null && (a = line.indexOf("ID:") + 3) != 2) {
					    b = line.indexOf(";", a);
					    Neuron neuron = new Neuron(Integer.parseInt(line.substring(a, b)), l.getID());
					    a = line.indexOf("Thr:", b) + 4;
					    b = line.indexOf(";", a);
					    neuron.setThresh(Double.parseDouble(line.substring(a, b)));  
					    l.addNeuron(neuron);
					    line = br.readLine();
			        }
			        nn.addLayer(l);
			        line = br.readLine();
		    	}
		    	
		    	//Make connections
		    	while(line != null && (a = line.indexOf("IDLI:") + 5) != 4) { //Find location of ID for next layer, if it exists, otherwise exit
				    b = line.indexOf("{", a);
		    		//Make connections for layer with this id
		    		NeuralLayer layer = nn.getLayerByID(Integer.parseInt(line.substring(a, b)));
			        line = br.readLine();
			        
			        
			        while(line != null && (a = line.indexOf("IDI:") + 4) != 3) { 
					    b = line.indexOf(";", a);
					    //Connect neuron with this id
					    System.out.println("Neurons in layer:" + layer.getNeurons().size());
					    Neuron neuron = layer.getNeuronByID(Integer.parseInt(line.substring(a, b)));
					   
					    a = b + 1;
					    while((b = line.indexOf(";", a)) != -1) {
					    	//Connect to this neuron
					    	Neuron cNeuron = nn.getNeuronByID(Integer.parseInt(line.substring(a, b)));
					    	System.out.println(line.substring(a, b));
					    	b += 1;
					    	a = line.indexOf(";", b);
					    	double thresh = Double.parseDouble(line.substring(b, a));
					    	nn.registerConnection(neuron.connectNeuron(cNeuron, thresh));
					    	a += 1;
					    	
					    }
					    
					    //Go to next neuron
					    line = br.readLine();
			        }
			        
			        //Go to next layer
			        line = br.readLine();
		    	}
		    }
		    br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failed to load Neural Network at \"" + path + "\"");
			e.printStackTrace();
		}
		return nn;
		   
	}
	
	
}

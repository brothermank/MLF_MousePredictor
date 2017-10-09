package manke.enchancement.com.mousepredictor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import manke.enchancement.com.hardwareCapture.MouseMonitor;
import manke.enchancement.com.neural.NeuralNetwork;
import manke.enchancement.com.neural.Neuron;
import manke.enchancement.com.util.PointD;

public class TrainingEnvironment implements Runnable {
	
	final int resx, resy;
	int points = 20, forwardPoints = 5, outputs = 3, currentSetIndex = 0, minimumSetSize = 500, maxTrainingCycles = 1000,
			targetedTrainingCycles = 500;
	
	List<Double> rx = new ArrayList<Double>(), ry = new ArrayList<Double>();
	
	private double trainerPerformance = 0;
	private double preMutPerformance = 0, postMutPerformance = 0, preMutRawError = 0, preMutPredictionError = 0, 
			preMutErrorPrediction = 0, maxPerformanceIncrease = 0.95, minPerformanceIncrease = 0.98;
	
	private NeuralNetwork nnwrk;
	MouseMonitor mm = new MouseMonitor();
	Thread monitorMouse;
	
	Point mousePosition = new Point();
	PointD mouseInput = new PointD();
	PointD target = new PointD();
	List<Queue<PointD>> fp = new ArrayList<Queue<PointD>>();
	
	List<Queue<PointD>> trainingSets = new ArrayList<Queue<PointD>>();
	List<Queue<PointD>> lastPoints = new ArrayList<Queue<PointD>>();
	
	
	double[] expectedOutputs = new double[2];
	
	private volatile boolean stopTraining = false;
	public volatile boolean completedTrainingPoint = false;
	
	public TrainingEnvironment() {
		resx = 1366; resy = 768;
	}
	
	public void initializeNewNetwork() {
		System.out.println("Initializing new network in training environment...");
		nnwrk = new NeuralNetwork();
		
		nnwrk.addLayer(points * 2);
		nnwrk.addLayer(points);
		nnwrk.addLayer(outputs);
		nnwrk.linkLayers(0, 1);
		nnwrk.linkLayers(1, 2);
		System.out.println("Network initialized");
	}
	
	/*public NeuralNetwork getNetwork() {
		return nnwrk;
	}*/
	public void setNetwork(NeuralNetwork nnwrk) {
		this.nnwrk = nnwrk;
	}
	public double getPostMutPerformance() {
		return postMutPerformance;
	}
	public double getPreMutPerformance() {
		return preMutPerformance;
	}
	public double getPreMutPredictionError() {
		return preMutPredictionError;
	}
	public double getPreMutRawError() {
		return preMutRawError;
	}
	public double getTrainerPerformance() {
		return trainerPerformance;
	}
	public double getErrorPrediction() {
		return preMutErrorPrediction;
	}
	public PointD getPrediction() {
		PointD raw = nnwrk.getPrediction();
		try {
			return new PointD(raw.getX() + rx.get(currentSetIndex)
					, raw.getY() + rx.get(currentSetIndex));
		} catch (IndexOutOfBoundsException e) {
			return new PointD(0,0);
		}
	}
	public PointD getTarget() {
		try {
			return new PointD(target.getX() + rx.get(currentSetIndex)
					, target.getY() + ry.get(currentSetIndex));
		} catch (IndexOutOfBoundsException e) {
			return new PointD(0,0);
		}
		
	}
	public double getMutationStrength() {
		return nnwrk.mutStr;
	}
	public boolean hasNetwork() {
		return nnwrk != null;
	}
	public NeuralNetwork getNeuralNetwork() {
		return nnwrk;
	}
	
	@Override
	public void run() { //Starts training active NeuralNetwork
		System.out.println("Initializing training session...");
		stopTraining = false;
		System.out.println("Training session initialized");
		trainOnSets();
		//trainOnCurrentData()
		System.out.println("Training session stopped");
	}
	
	private void trainOnLocalInformation() {
		stopTraining = false;
		monitorMouse = new Thread(mm);
		monitorMouse.start();
		
		waitForNewMouseInput();
		mm.inputUpdate = false;
		initializeInputs();
		while(!stopTraining) {
			updateInputs();
			mm.inputUpdate = false;
			trainOnCurrentData(true);
			waitForNewMouseInput();
		}
	}
	private void trainOnSets() {
		System.out.println("training");
		while(!stopTraining) {
			for(int i = 0; i < trainingSets.size(); i++) {
				PointD nextPoint;
				if(currentSetIndex == i || trainingSets.get(i).size() < minimumSetSize) {
					if(currentSetIndex != i) {
						changeSet(i, true);
					}
					while((nextPoint = trainingSets.get(i).poll()) != null) {
						updateInputs(nextPoint, currentSetIndex);
						trainOnCurrentData(false);
					}
				}
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void trainOnCurrentData(boolean mmInterrupt) {
		int trainings = 0;
		if(mouseIsMoving()) {
			//Set up Network
			nnwrk.setTarget(target);
			nnwrk.trainRandomNeuron(expectedOutputs);
			
			//Not pre training performance
			preMutPerformance = nnwrk.getPerformance();
			preMutPredictionError = nnwrk.getPredictionError();
			preMutRawError = nnwrk.getRawError();
			preMutErrorPrediction = nnwrk.errorPrediction();
			
			//Train network until maximum accepted improvement, maximum cycles or mouse monitor interrupts
			while((nnwrk.getPerformance() / preMutPerformance) > maxPerformanceIncrease && ((trainings < maxTrainingCycles) || mmInterrupt)
					&& !(mm.inputUpdate && mmInterrupt)) {
				nnwrk.trainRandomNeuron(expectedOutputs);
				trainings++;
			}
			postMutPerformance = nnwrk.getPerformance();
			
			fitMutStrength(trainings);
			completedTrainingPoint = true;
		}

		if(trainings != 0) {
			trainerPerformance = trainings;
		}
		
	}
	
	private void waitForNewMouseInput() {
		while(!mm.inputUpdate) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public int addSet() {
		System.out.println("Added set");
		fp.add(new LinkedList<PointD>());
		lastPoints.add(new LinkedList<PointD>());
		trainingSets.add(new LinkedList<PointD>());
		rx.add(new Double(0));
		ry.add(new Double(0));
		return trainingSets.size() - 1;
	}
	public void addDataToSet(int setIndex, PointD[] data) {
		for(int i = 0; i < data.length; i++) {
			trainingSets.get(setIndex).add(data[i]);	
		}
	}
	
	public void fitMutStrength(int trainingCycles) {
		if(postMutPerformance / preMutPerformance < 0.95 && nnwrk.mutStr > 0.00001) {
			nnwrk.mutStr *= 0.98;
		}
		else if(postMutPerformance / preMutPerformance > 0.995 && nnwrk.mutStr < 0.0001) { //At some point improvement slows down, and mutation strength will skyrocket, resulting in ridiculous weights.
			nnwrk.mutStr *= 1.01;
		}
		
		if(trainingCycles > targetedTrainingCycles && nnwrk.mutStr > 0.00001) {
			nnwrk.mutStr *= 0.98;
		}
		else if (nnwrk.mutStr < 0.0005){
			nnwrk.mutStr *= 1.01;	
		}
	}
	
	public void stopThread() {
		System.out.println("Stopping training session...");
		stopTraining = true;
		mm.stopThread();
	}
	
	private boolean mouseIsMoving() {
		List<Neuron> il = nnwrk.getNeuronsInLayer(0);
		boolean isMoving =  Math.abs(il.get(0).getValue() - il.get(2).getValue()) > 0.004 || //il.get(0).getValue() == il.get(4).getValue() &&
				//il.get(0).getValue() == il.get(6).getValue() && il.get(0).getValue() == il.get(8).getValue() &&
				Math.abs(il.get(1).getValue() - il.get(3).getValue()) > 0.004;// && il.get(1).getValue() == il.get(5).getValue());
				//&& il.get(1).getValue() == il.get(7).getValue() && il.get(1).getValue() == il.get(9).getValue());
		
		/*if(isMoving)System.out.println("ismovingdata: " + il.get(0).getValue() +"," + il.get(2).getValue() +"," + 
		il.get(4).getValue() +"," + il.get(6).getValue() +","  + il.get(8).getValue() + "    " +
		il.get(1).getValue() +"," + il.get(3).getValue() +"," + il.get(5).getValue() +"," + il.get(7).getValue() +","  
		+ il.get(9).getValue()); */ 
		
		if(isMoving) {
			System.out.println("ismovingdata: " + il.get(0).getValue() +"," + il.get(2).getValue() +"," + 
					il.get(1).getValue() +"," + il.get(3).getValue());
		}
		
		return isMoving;
	}
	
	private void initializeInputs() {
		System.out.println("Initializing inputs...");
		for(int i = 0; i < points * 2; i++) {
			if(i % 2 == 0) 	nnwrk.getNeuron(0, i).setValue(mouseInput.getX());
			else 			nnwrk.getNeuron(0, i).setValue(mouseInput.getY());
		}
		for(int i = 0; i < forwardPoints; i++) {
			fp.get(0).add(mouseInput.copy());
		}
		System.out.println("Inputs initialized");
		
	}
	

	private void updateInputs(PointD newestInput, int setIndex) {
		fp.get(setIndex).add(newestInput);
		
		double dy = nnwrk.getNeuron(0, 39).getValue() - nnwrk.getNeuron(0, 37).getValue();
		double dx = nnwrk.getNeuron(0, 38).getValue() - nnwrk.getNeuron(0, 36).getValue();

		rx.set(currentSetIndex, rx.get(currentSetIndex) - dx);
		ry.set(currentSetIndex, ry.get(currentSetIndex) - dy);	
		
		for(int i = 39; i >= 2; i--) {
			if(i % 2 == 1) {
				nnwrk.getNeuron(0, i).setValue(nnwrk.getNeuron(0, i - 2).getValue() + dy);
			}else {
				nnwrk.getNeuron(0, i).setValue(nnwrk.getNeuron(0, i - 2).getValue() + dx);
			}
		}
		PointD nextPoint = fp.get(setIndex).poll();
		nnwrk.getNeuron(0, 0).setValue(nextPoint.getX() - rx.get(currentSetIndex));
		nnwrk.getNeuron(0, 1).setValue(nextPoint.getY() - ry.get(currentSetIndex));
		expectedOutputs[0] = newestInput.getX() - rx.get(currentSetIndex);
		expectedOutputs[1] = newestInput.getY() - ry.get(currentSetIndex);
		
		for(int i = 0; i < expectedOutputs.length; i++) {
			if(i % 2 == 0) {
				expectedOutputs[i] = expectedOutputs[i] + dx;
			}else {
				expectedOutputs[i] = expectedOutputs[i] + dy;
			}
		}
		
		target = new PointD(nextPoint.getX() - rx.get(currentSetIndex), nextPoint.getY() - ry.get(currentSetIndex));
		//System.out.println("Newest neuron input: " + (nnwrk.getNeuron(0, 0).getValue() + rx) + "," + (nnwrk.getNeuron(0, 1).getValue() + ry));
		//System.out.println("R: " + rx + "," + ry + "   D: " + dx + "," + dy);
		//System.out.println(x);
	
	}
	private void updateInputs() {
		updateMousePosition();
		updateInputs(mouseInput, 0);
		//System.out.println("Newest mouse position: " + mouseInput.getX() + "," + mouseInput.getY());
		/*double dy = nnwrk.getNeuron(0, 39).getValue() - nnwrk.getNeuron(0, 37).getValue();
		double dx = nnwrk.getNeuron(0, 38).getValue() - nnwrk.getNeuron(0, 36).getValue();
		
		rx -= dx;
		ry -= dy;
		
		for(int i = 39; i >= 2; i--) {
			if(i % 2 == 1) {
				nnwrk.getNeuron(0, i).setValue(nnwrk.getNeuron(0, i - 2).getValue() + dy);
			}else {
				nnwrk.getNeuron(0, i).setValue(nnwrk.getNeuron(0, i - 2).getValue() + dx);
			}
		}
		PointD nextPoint = fp.poll();
		nextPoint.getX();
		nnwrk.getNeuron(0, 0).setValue(nextPoint.getX() - rx);
		nnwrk.getNeuron(0, 1).setValue(nextPoint.getY() - ry);
		expectedOutputs[0] = mouseInput.getX() - rx;
		expectedOutputs[1] = mouseInput.getY() - ry;
		
		for(int i = 0; i < expectedOutputs.length; i++) {
			if(i % 2 == 0) {
				expectedOutputs[i] = expectedOutputs[i] + dx;
			}else {
				expectedOutputs[i] = expectedOutputs[i] + dy;
			}
		}
		
		target = new PointD(nextPoint.getX() - rx, nextPoint.getY() - ry);
		//System.out.println("Newest neuron input: " + (nnwrk.getNeuron(0, 0).getValue() + rx) + "," + (nnwrk.getNeuron(0, 1).getValue() + ry));
		//System.out.println("R: " + rx + "," + ry + "   D: " + dx + "," + dy);
		//System.out.println(x);*/
	}

	private void changeSet(int setIndex, boolean setBroken) {
		List<Neuron> inputLayer = nnwrk.getNeuronsInLayer(0);
		PointD p;
		if(!setBroken) {
			//Prepare current set for re-initialization
			try {
				for(int i = 0; i < points; i++) {
					lastPoints.get(currentSetIndex).add(new PointD(inputLayer.get(0 + i * 2).getValue(), inputLayer.get(1 + i * 2).getValue()));
				}
			} catch (IndexOutOfBoundsException e) {
				while(lastPoints.get(currentSetIndex).size() < points) {
					lastPoints.get(currentSetIndex).add(new PointD(inputLayer.get(inputLayer.size() - 2).getValue(), 
							inputLayer.get(inputLayer.size() - 2).getValue()));
				}
				System.out.println("Wrong Neural Network input layer size");
				e.printStackTrace();
			}
			//Initialize next set
			for(int i = 0; i < points; i++) {
				p = lastPoints.get(setIndex).poll();
				inputLayer.get(i * 2).setValue(p.getX());
				inputLayer.get(1 + i * 2).setValue(p.getY());
			}
		}
		else {
			p = trainingSets.get(setIndex).poll();
			fp.set(setIndex, new LinkedList<PointD>());
			
			for(int i = 0; i < forwardPoints; i++) {
				fp.get(setIndex).add(p);
			}
			for(Neuron n : inputLayer) {
				n.setValue(0);
			}

			rx.set(setIndex, p.getX());
			ry.set(setIndex, p.getY());
			
			for(int i = 0; i <forwardPoints; i++) {
				updateInputs(trainingSets.get(setIndex).poll(), setIndex);
			}
		}
		
		currentSetIndex = setIndex;
	}
	
	private void updateMousePosition() {
		mousePosition.setLocation(mm.mousePosition);
		mouseInput.setPos(mm.mouseInput);
		/*int size = fp.size();
		System.out.print("FP contents: ");
		for(int i = 0; i < size; i++) {
			PointD entry = fp.poll();
			//System.out.print(" " + entry.getX() + " ");
			fp.add(entry);
		}
		System.out.println("");*/
	}
	

	
	
	

}

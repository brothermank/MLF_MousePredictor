package manke.enchancement.com.gui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import manke.enchancement.com.mousepredictor.TrainingEnvironment;
import manke.enchancement.com.neural.NeuralNetwork;
import manke.enchancement.com.util.PointD;

public class NeuralNetworkMonitor extends JPanel {
	
	TrainingEnvironment te = new TrainingEnvironment();
	Thread trainingThread;

	JLabel mutStrengthLabel, postMutPerformanceEntry, preMutPerformanceEntry, lastXPrediction, lastYPrediction, mousePosX, mousePosY,
			rawError, predictionError, errorPrediction;
	
	JButton startTrainingSession, stopTrainingSession, genNeuralNetwork, saveNetworkButton, loadNetworkButton;
	
	NetworkIllustrator illustrator;
	Graph performanceGrapher;
	
	MonitorActionListener actionListener;

	public JPanel containerLeft, labelPanel;
	
	JFrame frame;
	
	
	public NeuralNetworkMonitor(JFrame frame) {
		this.frame = frame;
		setLayout(new FlowLayout(FlowLayout.LEFT));

		//Item setups
        //Buttons
		startTrainingSession = new JButton("Start Training");
		stopTrainingSession = new JButton("Stop Training");
		genNeuralNetwork = new JButton("Generate new Neural Network");
		saveNetworkButton = new JButton("Save Current Neural Network");
		loadNetworkButton = new JButton("Load new Neural Network");
		
		startTrainingSession.addActionListener(e -> {startTrainingSession();});
		stopTrainingSession.addActionListener(e -> {stopTrainingSession();});
		genNeuralNetwork.addActionListener(e -> {genNewNetwork();});
		saveNetworkButton.addActionListener(e -> {saveNeuralNetwork();});
		loadNetworkButton.addActionListener(e -> {loadNeuralNetwork();});
		
		startTrainingSession.setEnabled(false);
		stopTrainingSession.setEnabled(false);
		
		//Labels
		postMutPerformanceEntry = new JLabel("Post mutation performance: 0");
		preMutPerformanceEntry = new JLabel("Pre mutation performance: 0");
		lastXPrediction = new JLabel("Last X Prediction: ");
		lastYPrediction = new JLabel("Last Y Prediction: ");
		mousePosX = new JLabel("Mouse X Position: ");
		mousePosY = new JLabel("Mouse Y Position: ");
		mutStrengthLabel = new JLabel("Mutation Strength");
		rawError = new JLabel("Raw error: ");
		predictionError = new JLabel("Predicted error:");
		errorPrediction = new JLabel("Predicted raw error: ");
		
		//Topology Monitor
		illustrator = new NetworkIllustrator(te.getNeuralNetwork());
		
		//Performance Grapher
		double[] listSizes = {100, 100, 100, 100, 100};
		performanceGrapher = new Graph(listSizes, new String[] {"Pre Mutation Performance", "Post Mutation performance", "Pre Mutation Prediction Error", 
				"Pre Mutation Raw Error", "Predicted Raw Error"});
		
		//Left side (anything but topology monitor)
		containerLeft = new JPanel();
		labelPanel = new JPanel();
		JPanel emptyLabelPanel = new JPanel();
		
		emptyLabelPanel.setLayout(new GridBagLayout());
		GridBagConstraints gcLeft = new GridBagConstraints();
		containerLeft.setLayout(new GridBagLayout());
		labelPanel.setLayout(new GridBagLayout());
		
		gcLeft.gridx = 0;
		gcLeft.gridy = 0;
		gcLeft.anchor = GridBagConstraints.FIRST_LINE_START;
        gcLeft.insets = new Insets(2, 4, 0, 0);
        gcLeft.weightx = 0;
        gcLeft.weighty = 0;

        labelPanel.add(mutStrengthLabel, gcLeft);
		gcLeft.gridy++;
		labelPanel.add(lastXPrediction, gcLeft);
		gcLeft.gridy++;
		labelPanel.add(lastYPrediction, gcLeft);
		gcLeft.gridy++;
		labelPanel.add(mousePosX, gcLeft);
		gcLeft.gridy++;
		labelPanel.add(mousePosY, gcLeft);
		gcLeft.gridy++;
		labelPanel.add(preMutPerformanceEntry, gcLeft);
		gcLeft.gridy++;
		labelPanel.add(postMutPerformanceEntry, gcLeft); 
		gcLeft.gridy++;
		labelPanel.add(rawError, gcLeft);
		gcLeft.gridy++;
		labelPanel.add(errorPrediction, gcLeft);
		gcLeft.gridy++;
		labelPanel.add(predictionError, gcLeft);
		gcLeft.gridy++;
		labelPanel.add(startTrainingSession, gcLeft);
		gcLeft.gridy++;
		labelPanel.add(stopTrainingSession, gcLeft);
		gcLeft.gridy++;
		labelPanel.add(genNeuralNetwork, gcLeft);
		gcLeft.gridy++;
		labelPanel.add(saveNetworkButton, gcLeft);
		gcLeft.gridy++;
		labelPanel.add(loadNetworkButton, gcLeft);
		gcLeft.gridy++;
		gcLeft.gridx++;
		gcLeft.weightx = 1;
		gcLeft.weighty = 1;
		labelPanel.add(emptyLabelPanel, gcLeft);
		
		gcLeft.gridx = 0;
		gcLeft.gridy = 1;
		containerLeft.add(performanceGrapher, gcLeft);
		gcLeft.gridy = 0;
		gcLeft.weightx = 0;
		gcLeft.weighty = 0;
		containerLeft.add(labelPanel, gcLeft);
		
		add(containerLeft);
		add(illustrator);
		
		System.out.println("Added network monitor");
	}
	

	public TrainingEnvironment getTrainingEnvironment() {
		return te;
	}
	
	private void genNewNetwork() {
		stopTrainingSession();
		te.initializeNewNetwork();
		illustrator.setNeuralNetwork(te.getNeuralNetwork());
		//frame.pack();
	}
	private void stopTrainingSession() {
		te.stopThread();
		stopTrainingSession.setEnabled(false);
		startTrainingSession.setEnabled(true);
	}
	private void startTrainingSession() {
		trainingThread = new Thread(te);
		trainingThread.start();
		stopTrainingSession.setEnabled(true);
		startTrainingSession.setEnabled(false);
	}
	private void saveNeuralNetwork() {
		NetworkSaver.saveNetwork(te.getNeuralNetwork());
	}
	
	public void autosave() {
		if(te.hasNetwork())	te.getNeuralNetwork().saveNeuralNetwork("Autosave");
		System.out.println("Autosaving");
	}
	
	
	private void loadNeuralNetwork() {
		stopTrainingSession();
		JFileChooser fc = new JFileChooser();
		try {
			fc.setCurrentDirectory(new File(".").getCanonicalFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int returnVal = fc.showOpenDialog(this);
		System.out.println("Return:" + fc.getSelectedFile().getAbsolutePath());
		NeuralNetwork newNetwork = NeuralNetwork.loadNeuralNetwork(fc.getSelectedFile().getAbsolutePath());
		te.setNetwork(newNetwork);
		illustrator.setNeuralNetwork(newNetwork);

		//frame.pack();
	}
	
	public void update() {
		if(te.hasNetwork()) {
			PointD lastPrediction = te.getPrediction();
			//System.out.println("Prediction: " + te.getNetwork().getPrediction().getX() + "," + te.getNetwork().getPrediction().getY());
			PointD mousePos = te.getTarget();
			postMutPerformanceEntry.setText("Post mutation performance: " + te.getPostMutPerformance());
			preMutPerformanceEntry.setText("Pre mutation performance: " + te.getPreMutPerformance());
			lastXPrediction.setText("Last X Prediction: " + lastPrediction.getX());
			lastYPrediction.setText("Last Y Prediction: " + lastPrediction.getY());
			mousePosX.setText("Mouse X Position: " + mousePos.getX());
			mousePosY.setText("Mouse Y Position: " + mousePos.getY());
			mutStrengthLabel.setText("Mutation Strength: " + te.getMutationStrength());
			predictionError.setText("Prediction Error: " + te.getPreMutPredictionError());
			rawError.setText("Raw Error: " + te.getPreMutRawError());
			errorPrediction.setText("Predicted raw error: " + te.getErrorPrediction());
			
			if(te.completedTrainingPoint) {
				addCurrentDataToGraph();
				te.completedTrainingPoint = false;
			}
		
		}
		illustrator.repaint();
		performanceGrapher.repaint();
	}
	
	public void addCurrentDataToGraph() {
		performanceGrapher.pushPoint(te.getPreMutPerformance(), 0);
		performanceGrapher.pushPoint(te.getPostMutPerformance(), 1);
		performanceGrapher.pushPoint(te.getPreMutPredictionError(), 2);
		performanceGrapher.pushPoint(te.getPreMutRawError(), 3);
		performanceGrapher.pushPoint(te.getErrorPrediction(), 4);
	}
	
	
}

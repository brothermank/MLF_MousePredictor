package manke.enchancement.com.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import manke.enchancement.com.mousepredictor.TrainingEnvironment;
import manke.enchancement.com.neural.NeuralNetwork;

public class NeuralTrainerMain extends JFrame {

	private final int sizex = 1366, sizey = 768;
	
	boolean hasMonitor = false;
	protected NeuralNetworkMonitor monitor;
	
	JButton repack;
	
	public NeuralTrainerMain() { 
		setDefaultParameters();
		addMonitor();
	}
	

	public TrainingEnvironment getTrainingEnvironment() {
		return monitor.getTrainingEnvironment();
	}
	
	private void setDefaultParameters() {
		JFrame frame = new JFrame();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0,0,500, 500);
		
		setVisible(true);
		setLocationRelativeTo(null);
		
		repack = new JButton("Repack");
		repack.addActionListener(e -> {pack();});
		//pack();
		
		
		System.out.println("Done setting up gui");
	}
	
	public void addMonitor() {
		monitor = new NeuralNetworkMonitor(this);
		

		GridBagConstraints gcLeft = new GridBagConstraints();
		
		gcLeft.gridx = 1;
		gcLeft.gridy = 1;
		gcLeft.anchor = GridBagConstraints.FIRST_LINE_END;
        gcLeft.insets = new Insets(2, 4, 4, 2);
        gcLeft.weightx = 0;
        gcLeft.weighty = 0;
        
		monitor.labelPanel.add(repack, gcLeft);
		
		getContentPane().add(monitor);
		
		pack();
		
		hasMonitor = true;
	}
	
	public void autosave() {
		monitor.autosave();
	}
	
	public void update() {
		if(hasMonitor) {
			monitor.update();
		}
	}
	
}

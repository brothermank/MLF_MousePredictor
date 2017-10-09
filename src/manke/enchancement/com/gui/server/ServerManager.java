package manke.enchancement.com.gui.server;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;

import manke.enchancement.com.gui.NeuralTrainerMain;
import manke.enchancement.com.networking.server.DataServer;

public class ServerManager extends NeuralTrainerMain {
	
	DataServer server;
	
	JButton startListeningB;
	
	public ServerManager() {
		super();
		server = new DataServer(getTrainingEnvironment());
		addDefaultParameters();
		pack();
	}
	
	private void addDefaultParameters() {
		startListeningB = new JButton("Start listening");
		startListeningB.addActionListener(e -> {startListeningForConnections();});
		
		GridBagConstraints gcLeft = new GridBagConstraints();
		
		gcLeft.gridx = 1;
		gcLeft.gridy = 0;
		gcLeft.anchor = GridBagConstraints.FIRST_LINE_END;
        gcLeft.insets = new Insets(2, 4, 4, 2);
        gcLeft.weightx = 0;
        gcLeft.weighty = 0;
        
		monitor.labelPanel.add(startListeningB, gcLeft);
	}
	
	private void startListeningForConnections() {
		server.startServer();
		startListeningB.setEnabled(false);
	}
}

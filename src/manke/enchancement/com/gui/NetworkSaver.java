package manke.enchancement.com.gui;

import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import manke.enchancement.com.neural.NeuralNetwork;

public class NetworkSaver extends JDialog {
	
	private NeuralNetwork network;
	private JTextField nameEntry;
	private JLabel nameLabel;
	
	public NetworkSaver(NeuralNetwork network) {
		this.network = network;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(0,0,500, 500);
		setVisible(true);
		setLocationRelativeTo(null);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));

		nameEntry = new JTextField();
		nameLabel = new JLabel("Save Name: ");
		nameEntry.setSize(100, 40);
		
		panel.add(nameLabel);
		panel.add(nameEntry);
		
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(e -> {saveNeuralNetwork(this.network);});

		panel.add(saveButton);
		
		add(panel);
		
		pack();
	}

	public static void saveNetwork(NeuralNetwork network) {
		NetworkSaver saveDialogue = new NetworkSaver(network);
		
	}
	
	private void saveNeuralNetwork(NeuralNetwork network) {
		if(!nameEntry.getText().equals("")) {
			network.saveNeuralNetwork(nameEntry.getText());
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}
}

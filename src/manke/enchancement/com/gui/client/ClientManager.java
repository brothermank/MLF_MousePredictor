package manke.enchancement.com.gui.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import manke.enchancement.com.networking.client.DataClient;

public class ClientManager extends JFrame{

	JButton startRecording;
	JButton stopRecording;
	JButton startStreaming;
	JButton stopStreaming;
	JButton connect;
	
	JPanel ipAddressP = new JPanel();
	JPanel portP = new JPanel();
	
	JTextField ipAddressF = new JTextField("localhost");
	JTextField portF = new JTextField("22222");
	
	JLabel ipAddressL = new JLabel("IP Address");
	JLabel portL = new JLabel("Port");
	JLabel streamingL = new JLabel("Streaming");
	JLabel recordingL = new JLabel("Recording");
	
	JTextArea output = new JTextArea();
	
	DataClient client;

	public ClientManager() {
		client = new DataClient();
		setDefaultParameters();
	}
	
	private void setDefaultParameters() {
		JFrame frame = new JFrame();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0,0,500, 500);
		
		setVisible(true);
		setLocationRelativeTo(null);
		
		JPanel containerPanel = new JPanel();
		JPanel emptyPanel = new JPanel();	
		GridBagConstraints gcParent = new GridBagConstraints();
		containerPanel.setLayout(new GridBagLayout());

		gcParent.gridx = 0;
		gcParent.gridy = 0;
		gcParent.anchor = GridBagConstraints.FIRST_LINE_START;
        gcParent.insets = new Insets(2, 4, 0, 0);
        gcParent.weightx = 0;
        gcParent.weighty = 0;
        
        //Buttons
		GridBagConstraints gcButtons = new GridBagConstraints();
		gcButtons.gridx = 0;
		gcButtons.gridy = 0;
		gcButtons.anchor = GridBagConstraints.WEST;
		gcButtons.insets = new Insets(2, 4, 0, 0);
		gcButtons.weightx = 0.5;
		gcButtons.weighty = 1;
        
        JPanel startStopRecordingP = new JPanel();
        JPanel startStopStreamingP = new JPanel();
        startStopRecordingP.setLayout(new GridBagLayout());
        startStopRecordingP.setPreferredSize(new Dimension(300, 30));
        startStopStreamingP.setLayout(new GridBagLayout());
        startStopStreamingP.setPreferredSize(new Dimension(300, 30));
        startRecording = new JButton("Start");
		stopRecording = new JButton("Stop");
		startStreaming = new JButton("Start");
		stopStreaming = new JButton("Stop");
		
		connect = new JButton("Connect");
		
		startStopRecordingP.add(recordingL, gcButtons);
		startStopStreamingP.add(streamingL, gcButtons);
		gcButtons.gridx++;
		gcButtons.weightx = 0;
		startStopRecordingP.add(startRecording, gcButtons);
		startStopStreamingP.add(startStreaming, gcButtons);
		gcButtons.gridx++;
		gcButtons.weightx = 1;
		startStopRecordingP.add(stopRecording, gcButtons);
		startStopStreamingP.add(stopStreaming, gcButtons);
		

		connect.addActionListener(e -> {connect();});
		startRecording.addActionListener(e -> {startRecording();});
		stopRecording.addActionListener(e -> {stopRecording();});
		stopRecording.setEnabled(false);
		startStreaming.addActionListener(e -> {startStreaming();});
		startStreaming.setEnabled(false);
		stopStreaming.addActionListener(e -> {stopStreaming();});
		stopStreaming.setEnabled(false);
		
		ipAddressP.setLayout(new FlowLayout());
		portP.setLayout(new FlowLayout());

		ipAddressF.setPreferredSize(new Dimension(100,20));
		portF.setPreferredSize(new Dimension(100,20));

		ipAddressL.setPreferredSize(new Dimension(70,20));
		portL.setPreferredSize(new Dimension(70,20));

		ipAddressP.add(ipAddressL);
		ipAddressP.add(ipAddressF);		
		portP.add(portL);
		portP.add(portF);

		
		output.setEditable(false);
		output.setPreferredSize(new Dimension(300, 100));
		output.setBackground(Color.LIGHT_GRAY);
		
		//Add components to main panel
		containerPanel.add(ipAddressP, gcParent);
		gcParent.gridy++;
		containerPanel.add(portP, gcParent);
		gcParent.gridy++;
		containerPanel.add(startStopRecordingP, gcParent);
		gcParent.gridy++;
		containerPanel.add(startStopStreamingP, gcParent);
		gcParent.gridy++;
		gcParent.anchor = GridBagConstraints.CENTER;
		containerPanel.add(connect, gcParent);
		gcParent.anchor = GridBagConstraints.FIRST_LINE_START;
		gcParent.gridy++;
		containerPanel.add(output, gcParent);
		
		gcParent.gridy++;
		gcParent.gridx++;
		gcParent.weighty = 1;
		gcParent.weightx = 1;
		containerPanel.add(emptyPanel, gcParent);
		
		
		add(containerPanel);
		
		pack();
		
		autoConnect();
		//repack = new JButton("Repack");
		//repack.addActionListener(e -> {pack();});
		//pack();
		
		
		System.out.println("Done setting up gui");
	}
	
	
	private boolean connect() {
		connect.setEnabled(false);
		if(client.tryConnect()) {
			println("Connection successful");
			startStreaming.setEnabled(true);
			return true;
		}
		else {
			connect.setEnabled(true);
			println("Connection unsuccessful");
			return false;
		}
	}
	private void startRecording() {
		startRecording.setEnabled(false);
		client.startRecording();
		stopRecording.setEnabled(true);
	}
	private void stopRecording() {
		stopRecording.setEnabled(false);
		client.stopRecording();
		startRecording.setEnabled(true);
	}
	private void startStreaming() {
		startStreaming.setEnabled(false);
		client.startStreaming();
		stopStreaming.setEnabled(true);
		
	}
	private void stopStreaming() {
		stopStreaming.setEnabled(false);
		client.stopStreaming();
		startStreaming.setEnabled(true);
	}
	
	public void println(String text) {
		output.append(text + "\n");
	
	}
	
	private void fetchIPandPort() {
		URL location;
		try {
			location = new URL("https://docs.google.com/document/d/e/2PACX-1vSEFgpLJLUX40Zbf1FOtQxnz9QyMhnqkgLRDJ7BBgZ9eEPHcgepSMMoaupNZjH6bmlcU5DaOLf4Tut4/pub");

	        URLConnection yc = location.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(
	                yc.getInputStream(), "UTF-8"));
	        String inputLine;
	        StringBuilder a = new StringBuilder();
	        while ((inputLine = in.readLine()) != null)
	            a.append(inputLine);
	        in.close();
	        
	        int b = a.indexOf("IP:");
	        int c = a.indexOf(";END;", b);
	        ipAddressF.setText(a.substring(b + 3, c));
	        
	        b = a.indexOf("PORT:");
	        c = a.indexOf(";END;", b);
	        portF.setText(a.substring(b + 5, c));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void autoConnect() {
		fetchIPandPort();
		while(1 < 2)
		if(connect()) {
			startStreaming();
			return;
		} else {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

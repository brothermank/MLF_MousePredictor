package manke.enchancement.com;

import manke.enchancement.com.gui.NeuralTrainerMain;
import manke.enchancement.com.gui.client.ClientManager;
import manke.enchancement.com.gui.server.ServerManager;
import manke.enchancement.com.mousepredictor.TrainingEnvironment;
import manke.enchancement.com.networking.client.DataClient;
import manke.enchancement.com.networking.server.DataServer;
import manke.enchancement.com.neural.NeuralNetwork;
import manke.enchancement.com.util.Rand;

public class Entry {

	public static void main(String[] args) {
		init();
		
		//testClient();
		testServer();
		//testNetworking();
		//testGUI();
		//testLoading();
		//testNetworkGeneration();
		System.out.println("Quitting");
	}
	
	public static void init() {
		Rand.init();
	}
	
	static final long autosaveTime = 300000;
	static long autosaveCounter = 0;
	
	public static void testClient() {
		ClientManager gui = new ClientManager();
		
		/*DataClient client = new DataClient();
		
		client.tryConnect();

		for(int i = 0; i < 1000; i++) {		
			client.getCollector().addInput(new PointD(i,i));
		}
		client.startStreaming();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	public static void testServer() {
		ServerManager server = new ServerManager();
		//server.addMonitor(); Now done in constructor
		
		System.out.println("Setup done");
		
		//testClient();
		while(0 < 1) {
			try {
				server.update();
				Thread.sleep(100);
				autosaveCounter += 100;
				if(autosaveCounter >= autosaveTime) {
					server.autosave();
					autosaveCounter = 0;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void testNetworking() {
		ClientManager gui = new ClientManager();
		ServerManager server = new ServerManager();
		//server.addMonitor(); Now done in constructor
		
		System.out.println("Setup done");
		
		//testClient();
		while(0 < 1) {
			try {
				server.update();
				Thread.sleep(100);
				autosaveCounter += 100;
				if(autosaveCounter >= autosaveTime) {
					server.autosave();
					autosaveCounter = 0;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void testGUI() {
		NeuralTrainerMain g = new NeuralTrainerMain();
		
		g.addMonitor();
		
		while(0 < 1) {
			try {
				g.update();
				Thread.sleep(100);
				autosaveCounter += 100;
				if(autosaveCounter >= autosaveTime) {
					g.autosave();
					autosaveCounter = 0;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void testLoading() {
		NeuralNetwork nn = NeuralNetwork.loadNeuralNetwork("TestNetwork1");
		nn.saveNeuralNetwork("TestNetworkLoading");
	}
	
	public void testTrainingEnvironment() {
		TrainingEnvironment te = new TrainingEnvironment();
		te.initializeNewNetwork();
		te.run();
	}
	
	public static void testNetworkGeneration() {
		NeuralNetwork nnwrk = new NeuralNetwork();
		nnwrk.addLayer(5);
		nnwrk.addLayer(7);
		nnwrk.addLayer(10);
		nnwrk.linkLayers(0, 1);
		nnwrk.linkLayers(1, 2);
		
		nnwrk.saveNeuralNetwork("TestNetworkGeneration");
	}
	
}

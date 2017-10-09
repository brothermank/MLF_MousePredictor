package manke.enchancement.com.networking.server;

import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import manke.enchancement.com.mousepredictor.TrainingEnvironment;

public class ServerCommunicationHandler {

	List<ServerConnection> connections = new ArrayList<ServerConnection>();
	List<Thread> connectionThreads = new ArrayList<Thread>();
	
	public void addConnection(Socket connectionSocket, ObjectInputStream inputStream, DataOutputStream outputStream, TrainingEnvironment te) {
		connections.add(new ServerConnection(connectionSocket, inputStream, outputStream, te));
		connectionThreads.add(new Thread(connections.get(connections.size() - 1)));
	}
	
	public void listenToConnection(int connectionIndex) {
		connectionThreads.get(connectionIndex).run();
	}
	public void listenToNewestConnection() {
		connectionThreads.get(connectionThreads.size() - 1).start();
	}
}

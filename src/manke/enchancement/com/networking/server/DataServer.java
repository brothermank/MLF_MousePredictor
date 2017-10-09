package manke.enchancement.com.networking.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import manke.enchancement.com.mousepredictor.TrainingEnvironment;

public class DataServer {
	
	String ip = "localhost";
	int port = 22222;
	
	ServerSocket serverSocket;
	Thread connectThread;
	
	ServerCommunicationHandler communicator = new ServerCommunicationHandler();
	
	boolean listeningForConnections = false;
	
	TrainingEnvironment te;
	
	public DataServer(TrainingEnvironment te) {
		this.te = te;
	}
	
	public void startServer() {
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server started");
		} catch (IOException e) {
			System.out.println("Failed to create server on port: " + port);
			e.printStackTrace();
		}
		startListeningForConnections();
	}
	
	/*private void makeNewConnection() {
		try {
			connections.add(new Socket(ip, port));
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}*/

	Runnable connectionListener = new Runnable() {
		public void run() {
			if(serverSocket != null) {
				System.out.println("Server started listening for connections");
				while(listeningForConnections) {
					try {
						Socket socket = serverSocket.accept();
						ObjectInputStream dis = new ObjectInputStream(socket.getInputStream());
						DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
						
						communicator.addConnection(socket, dis, dos, te);
						communicator.listenToNewestConnection();
						
						System.out.println("New connection established");
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println("Server stopped listening for connections");
			}
			else {
				System.out.println("Attempting to listen for connections on non-established server");
			}
		}
	};
	public void startListeningForConnections() {
		listeningForConnections = true;
		connectThread = new Thread(connectionListener);
		//connectThread.setPriority(Thread.MIN_PRIORITY);
		connectThread.start();
	}
	
	public void stopListeningForConnections() {
		listeningForConnections = false;
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}

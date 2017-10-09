package manke.enchancement.com.networking.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class DataClient {

	
	ClientConnection connection;
	DataCollector collector;
	Thread stream, monitor;
	
	
	public DataClient() {
		connection = new ClientConnection();
		collector = new DataCollector(connection);
	}
	
	public boolean tryConnect() {
		return connection.tryConnect();
	}
	
	public ClientConnection getConnection() {
		return connection;
	}
	public DataCollector getCollector() {
		return collector;
	}
	
	public void startStreaming() {
		stream = new Thread(connection);
		stream.start();
	}
	public void stopStreaming() {
		connection.stopStreaming();
	}
	
	public void startRecording() {
		monitor = new Thread(collector);
		monitor.start();
	}
	public void stopRecording() {
		collector.stopRecording();
	}
	
	
}

package manke.enchancement.com.networking.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import manke.enchancement.com.mousepredictor.TrainingEnvironment;
import manke.enchancement.com.networking.PointDTransaction;

public class ServerConnection implements Runnable {

	int expectedTransactionID = Integer.MIN_VALUE, trainerIndex = 0;
	
	TrainingEnvironment te;
	Socket connectionSocket;
	ObjectInputStream inputStream;
	DataOutputStream outputStream;
	
	private volatile Queue<PointDTransaction> inputs = new LinkedList<PointDTransaction>();
	
	boolean closeCommunication = false;
	
	public ServerConnection(Socket connectionSocket, ObjectInputStream inputStream, DataOutputStream outputStream, TrainingEnvironment te) {
		this.connectionSocket = connectionSocket;
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.te = te;
		trainerIndex = te.addSet();
		try {
			outputStream.writeInt(expectedTransactionID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			PointDTransaction input;
			outputStream.writeInt(expectedTransactionID);
			while(!closeCommunication) {
				input = (PointDTransaction)(inputStream.readObject());
				if(input.getTransactionIndex() == expectedTransactionID) {
					//queueTransaction(input);
					te.addDataToSet(trainerIndex, input.getPoints());
					System.out.println("Recieved transaction with expected index " + input.getTransactionIndex() + 
							" and coordinates: " + input.poll().getX());// + "," + input.getPoint().getY());
					nextTransaction();
				}
				else {
					requestTransactionResend();
					//System.out.println("Recieved transaction with unexpected index " + input.getTransactionIndex());
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		System.out.println("Communication closed");
	}
	
	private void queueTransaction(PointDTransaction transaction) {
		inputs.add(transaction);
	}
	
	private void nextTransaction() {
		expectedTransactionID++;
		try {
			outputStream.writeInt(expectedTransactionID);
			//System.out.println("Expected transaction id: " + expectedTransactionID);
		} catch (IOException e) {
			System.out.println("Failed to proceede to next transaction");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void requestTransactionResend() {

		try {
			outputStream.writeInt(expectedTransactionID);
		} catch (IOException e) {
			System.out.println("Failed to request transaction resend");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean hasPoints() {
		return inputs.size() > 0;
	}
	
	public PointDTransaction poll() {
		return inputs.poll();
	}
	
	
}

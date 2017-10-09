package manke.enchancement.com.networking.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import manke.enchancement.com.networking.PointDTransaction;

public class ClientConnection implements Runnable {

	String ip = "localhost";
	int port = 22222;
	
	Socket connection;
	ObjectOutputStream outputStream;
	DataInputStream inputStream;
	
	boolean connected = false;
	
	private Queue<PointDTransaction> transactionQueue = new LinkedList<PointDTransaction>();

	private int ongoingTransactionIndex;
	private int expectedTransactionIndex;
	private PointDTransaction ongoingTransaction;
	
	private int transactionInterval = 1;
	
	private boolean stopStreaming = false;
	
	@Override
	public void run() {
		stopStreaming = false;
		while(connection != null && !stopStreaming) {
			if(transactionQueue.size() > 0) {
				performTransaction();
			}
			try {
				Thread.sleep(transactionInterval);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean tryConnect() {
		try {
			connection = new Socket(ip, port);
			inputStream = new DataInputStream(connection.getInputStream());
			outputStream = new ObjectOutputStream(connection.getOutputStream());
		} catch (IOException e) {
			connected = false;
			System.out.println("Connection failed");
			return false;
		}
		
		connected = true;
		return true;
	}
	

	public void performTransaction() {
		if(updateExpectedTransactionIndex()) {
			
			//If index too high (Stream broken by client, re-synchronize)
			if(ongoingTransactionIndex > expectedTransactionIndex) {
				ongoingTransaction = transactionQueue.poll();
				ongoingTransactionIndex = expectedTransactionIndex;
				ongoingTransaction.setStreamBroken();
			}
			//If index too low (Stream broken by host, skip to expected data point)
			else if(ongoingTransactionIndex < expectedTransactionIndex) {
				while(ongoingTransactionIndex < expectedTransactionIndex) {
					if(transactionQueue.size() > 0) {
						ongoingTransaction = transactionQueue.poll();
						ongoingTransactionIndex++;
					}
					else {
						//Idle while waiting for more inputs
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();	
						}
					}
				}	
				ongoingTransaction.setStreamBroken();
			}
		}
		
		try {
			
			ongoingTransaction.setTransactionIndex(ongoingTransactionIndex);
			outputStream.writeObject((Serializable)(ongoingTransaction));
			//System.out.println("Client completed transaction");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Client failed transaction");
			e.printStackTrace();
		}
		
	}
	
	private boolean updateExpectedTransactionIndex(){
		try {
			int nextInt = inputStream.readInt();
			if(nextInt != expectedTransactionIndex) {
				expectedTransactionIndex = nextInt;
				//System.out.println("Client expected transactionID is now " + expectedTransactionIndex);
				return true;
			}
			else {
				//System.out.println("Client didnt update transactionID");
				return false;
			}
		} catch (IOException e) {
			System.out.println("Failed to update transaction index");
			e.printStackTrace();
		}
		return false;
	}
	
	public void queueTransaction(PointDTransaction transaction) {
		transactionQueue.add(transaction);
	}
	
	public void stopStreaming() {
		stopStreaming = true;
	}
	
	
}

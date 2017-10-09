package manke.enchancement.com.networking.client;

import manke.enchancement.com.hardwareCapture.MouseMonitor;
import manke.enchancement.com.networking.PointDTransaction;
import manke.enchancement.com.util.PointD;

public class DataCollector implements Runnable {
	
	private int dataSize = 20;
	
	private PointDTransaction transaction;
	
	private ClientConnection outputConnection;

	private MouseMonitor monitor = new MouseMonitor();
	
	private boolean stopRecording = false;
	private PointD lastPoint = new PointD();
	private int repeatPoints = 0;
	
	public DataCollector(ClientConnection connection) {
		outputConnection = connection;
		transaction = new PointDTransaction();
	}
	
	@Override
	public void run() {
		Thread monitorThread = new Thread(monitor);
		monitorThread.start();
		
		stopRecording = false;
		while(!stopRecording) {
			waitForInput();
			readInput();
		}
		
		
	}
	
	private void waitForInput() {
		while(!monitor.inputUpdate) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		monitor.inputUpdate = false;
	}
	
	private void readInput() {
		
		if(Math.abs(lastPoint.getX() - monitor.mouseInput.getX()) < 0.001 && Math.abs(lastPoint.getY() - monitor.mouseInput.getY()) < 0.001) {
			if(repeatPoints < dataSize) {
				repeatPoints++;
			}
		}
		else {
			repeatPoints = 0;
		}
		
		if(repeatPoints < dataSize) {
			if(transaction.spaceLeft()) {
				transaction.addPoint(new PointD(monitor.mouseInput));
			}
			else {
				System.out.println("Added to queue");
				outputConnection.queueTransaction(transaction);
				transaction = new PointDTransaction();
				transaction.addPoint(new PointD(monitor.mouseInput));
			}
		}
		else {
			//System.out.println("Repeat limit");
		}
		
		lastPoint.setPos(monitor.mouseInput);
		
	}
	
	public void addInput(PointD input) {
		if(transaction.spaceLeft()) transaction.addPoint(new PointD(input));
		else {
			outputConnection.queueTransaction(transaction);
			transaction = new PointDTransaction();
			transaction.addPoint(new PointD(input));
		}
	}
	
	public void stopRecording() {
		stopRecording = true;
	}
	
	
	
}

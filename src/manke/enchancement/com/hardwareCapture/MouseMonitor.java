package manke.enchancement.com.hardwareCapture;

import java.awt.MouseInfo;
import java.awt.Point;

import manke.enchancement.com.util.PointD;

public class MouseMonitor implements Runnable {

	final int resx = 1366, resy = 768;
	
	public volatile Point mousePosition;
	public volatile PointD mouseInput = new PointD();
	public volatile boolean inputUpdate = false;
	private volatile boolean stop = false;
	
	@Override
	public void run() {
		System.out.println("Mouse monitoring started");
		stop = false;
		while(!stop) {
			updateMousePosition();
			inputUpdate = true;
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Mouse monitoring stopped");
	}
	
	public void stopThread() {
		System.out.println("Mouse monitoring stopping");
		stop = true;
	}


	public void updateMousePosition() {
		mousePosition = MouseInfo.getPointerInfo().getLocation();
		mouseInput.setPos((double)(mousePosition.getX()) / (double)(resx), (double)(mousePosition.getY()) / (double)(resy));
		
	}
	
}

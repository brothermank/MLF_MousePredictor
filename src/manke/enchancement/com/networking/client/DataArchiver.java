package manke.enchancement.com.networking.client;

import java.util.ArrayList;
import java.util.List;

import manke.enchancement.com.hardwareCapture.MouseMonitor;
import manke.enchancement.com.util.PointD;

public class DataArchiver {

	private MouseMonitor mouseListener =  new MouseMonitor();
	private Thread mouseCaptureThread; 
	
	List<PointD> pointsToSend = new ArrayList<PointD>();
	
	
	public DataArchiver() {
		mouseCaptureThread = new Thread(mouseListener);
	}
	
	
	
}

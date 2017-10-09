package manke.enchancement.com.networking;

import java.io.Serializable;

import manke.enchancement.com.util.PointD;

public class PointDTransaction implements Serializable{
	
	public transient final static int size = 100;
	
	PointD[] points = new PointD[size];
	private int transactionIndex;
	private boolean streamBroken, isFull;
	
	transient int accessIndex = 0;
	
	public PointDTransaction() {
		this.transactionIndex = 0;
		streamBroken = false;
	}
	public PointDTransaction(int transactionID) { 
		this.transactionIndex = transactionID;
		streamBroken = false;
	}
	public PointDTransaction(int transactionID, boolean streamBroken) { 
		this.transactionIndex = transactionID;
		this.streamBroken = streamBroken;
	}
	
	
	public int getTransactionIndex() {
		return transactionIndex;
	}
	public void setTransactionIndex(int newIndex) {
		transactionIndex = newIndex;
	}
	public PointD[] getPoints() {
		return points;
	}
	public PointD poll() {
		if(!isFull) {
			accessIndex++;
			return points[accessIndex - 1];
		}
		throw new IndexOutOfBoundsException();
	}
	public void addPoint(PointD p) {
		if(!isFull) {
			accessIndex++;
			points[accessIndex - 1] = p;
		}
	}
	public void setStreamBroken() {
		streamBroken = true;
	}
	public boolean spaceLeft() {
		return accessIndex < size;
	}
}

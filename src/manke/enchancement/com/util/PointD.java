package manke.enchancement.com.util;

import java.awt.Point;
import java.io.Serializable;

public class PointD implements Serializable{
	
	private double x = 0, y = 0;

	public PointD() {
		
	}
	public PointD(double x, double y) {
		this.x = x; this.y = y;
	}
	public PointD(PointD p) {
		x = p.getX(); p.getY();
	}
	
	public void setPos(double x, double y) {
		this.x = x; this.y = y;
	}
	public void setPos(PointD p) {
		this.x = p.getX(); this.y = p.getY();
	}
	
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public PointD copy() {
		return new PointD(x, y);
	}
}

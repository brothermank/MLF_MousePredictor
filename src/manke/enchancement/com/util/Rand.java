package manke.enchancement.com.util;

import java.util.Random;

public class Rand {

	private static Random r;
	public static void init() {
		r = new Random();
	}
	
	public static double randomDouble() {
		return r.nextDouble();
	}
	public static double randomDouble(double min, double max) {
		return (r.nextDouble() * (max - min)) + min;
	}
	
	public static int randomInt() {
		return r.nextInt();
	}
	public static int randomInt(int min, int max) {
		double d = r.nextDouble();
		int i = (int)(d * (max - min));
		return i + min;
	}
	
}

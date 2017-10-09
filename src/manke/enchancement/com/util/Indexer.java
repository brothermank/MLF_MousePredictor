package manke.enchancement.com.util;

public class Indexer {

	private int currentID = 0;
	
	public Indexer() {}
	public Indexer(int startID) {
		currentID = startID;
	}
	
	public int next() {
		currentID++;
		return currentID;
	}
	public int current() {
		return  currentID;
	}

	
}

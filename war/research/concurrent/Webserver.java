/**
 * Webserver.java
 * Concurrent webserver-style application where many reader threads take
 * numbers out of a hash table, while a writer thread puts them in.
 * 
 * Example copied from "Comprehensive Synchronization Elimination for Java,"
 * by Jonathan Aldriche et al., which appears in Science of Computer
 * Programming 47 (2003) 91-120. Modified to use generics.
 */

import java.util.Random;

class Pair <F,S> {
	private F first;
	private S second;

	public Pair(F f, S s) {
		first = f;
		second = s;
	}

	public synchronized F getFirst() {
		return first;
	}

	public synchronized S getSecond() {
		return second;
	}

	public synchronized void setFirst(F f) {
		first = f;
	}

	public synchronized void setSecond(S s) {
		second = s;
	}
}

class Table<K,V> {
	private final List<Pair<K,V>> entries[];
	private final int capacity;

	public Table() {
		capacity = 13587;
		entries = (List<Pair<K,V>>[])new List[capacity];
		for(int i = 0; i < capacity; ++i)
			entries[i] = new List<Pair<K,V>>();
	}

	public synchronized V get(K key) {
		return getEntry(key).getSecond();		
	}

	public synchronized void put(K key, V value) {
		Pair<K,V> entry = getEntry(key);
		entry.setSecond(value);
	}
	
	private synchronized Pair<K,V> getEntry(K key) {
		int index = key.hashCode() % capacity;
		List<Pair<K,V>> l = entries[index];
		l.reset();
		while(l.hasMore()) {
			Pair<K,V> p = l.getNext();
			if(p.getFirst().equals(key))
				return p;
		}
		Pair<K,V> p = new Pair(key, null);
		l.add(p);
		return p;
	}
}

class List<E> {
	private Pair<E,Pair> first;
	private Pair<E,Pair> current;
	
	public synchronized E getNext() {
		if( current != null ) {
			E value = current.getFirst();
			current = (Pair<E,Pair>)current.getSecond();
			return value;
		}
		else return null;
	}
	
	public synchronized void reset() {
		current = first;
	}
	
	public synchronized boolean hasMore() {
		return current != null;
	}
	
	public synchronized void add(E entry) {
		first = new Pair<E,Pair>(entry, first);
	}
}

class WriterThread extends Thread {
	public void run() {
		int myMaxNumber = 1000;
		while(myMaxNumber < 10000) {
			for(int i = 0; i < 100; ++i) {
				Webserver.dataTable.put(
						new Integer(myMaxNumber),
						String.valueOf(myMaxNumber));
				myMaxNumber++;
			}
			synchronized(Webserver.maxNumberLock) {
				Webserver.maxNumber = myMaxNumber;
			}
		}
		System.out.println("Writer complete.");
	}
}

class ReaderThread extends Thread {
	public void run() {
		int myMaxNumber;
		Random rand = new Random();
		for(int i = 0; i < 1000; ++i) {
			synchronized(Webserver.maxNumberLock) {
				myMaxNumber = Webserver.maxNumber;
			}
			for(int j = 0; j < 100; ++j) {
				int index = Math.abs(
						rand.nextInt()) % myMaxNumber;
				Webserver.dataTable.get(
						new Integer(index));
			}
		}
		System.out.println("Reader complete");
	}
}

public class Webserver {
	
	public static final Table<Integer,String> dataTable = new Table<Integer,String>();
	public static int maxNumber;
	public static final Object maxNumberLock = new Object();
	
	public static void main(String args[]) {
		/* Set up data table */
		for(maxNumber = 0; maxNumber < 100; ++maxNumber) {
			dataTable.put(
					new Integer(maxNumber),
					String.valueOf(maxNumber));
		}
		for(int threadNum = 0; threadNum < 8; ++threadNum) {
			new ReaderThread().start();
		}
		new WriterThread().start();
	}
}
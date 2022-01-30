package testat3;

import java.net.DatagramPacket;
import java.util.LinkedList;

/**
 * @author Mathis Neunzig
 * @author Nathalie Möck
 */
public class DatagramPaketQueue {
	
	// LinkedList als FIFO-Queue
	private LinkedList<DatagramPacket> queue = new LinkedList<>();
	
	/**
	 * Fügt ein DatagramPacket der Queue hinzu
	 * @param p Eingehendes DatagramPacket
	 */
	synchronized void add(DatagramPacket p) {
		
		queue.add(p);
		// Dem ersten Thread sagen, dass er einen Eintrag in der Queue finden kann
		this.notify();
		
	}
	
	/**
	 * Nimmt ein DatagramPacket aus der Queue. Ist die Queue leer, wird an dieser gewartet, bis ein Element sich in der Queue befindet
	 * @return Ausgehendes DatagramPacket
	 */
	synchronized DatagramPacket remove() {
		
		while(queue.size() == 0) {
			try {
				// Warten, wenn Queue leer ist
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return queue.pop();
		
	}

}

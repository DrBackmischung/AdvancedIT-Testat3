package testat3;

import java.net.DatagramPacket;
import java.util.LinkedList;

public class DatagramPaketQueue {
	
	private LinkedList<DatagramPacket> queue = new LinkedList<>();
	
	synchronized void add(DatagramPacket p) {
		
		queue.add(p);
		this.notify();
		
	}
	
	synchronized DatagramPacket remove() {
		
		while(queue.size() == 0) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return queue.pop();
		
	}

}

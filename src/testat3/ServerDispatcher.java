package testat3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class ServerDispatcher {
	
	private static final String PATH = System.getProperty("user.home") + "/Desktop/Files/";

    private static DatagramSocket s = null;
    private static ServerWorker[] workers;
    private static DatagramPaketQueue queue;
    private static FileHandler f;
	private static Map<String, FileMonitor> monitor = new HashMap<>();
    
    public static void main(String[] args) {

    	workers = new ServerWorker[5];
    	queue = new DatagramPaketQueue();
    	f = new FileHandler(PATH);
    	
    	try {
    		
    		s = new DatagramSocket(5999);
    		System.out.println("Server auf Port 5999 gestartet!");
    		for(int i = 0; i<workers.length; i++) {
    			workers[i] = new ServerWorker(i, s, queue, f, monitor);
    			workers[i].start();
                System.out.println("["+i+"] Worker gestartet");
    		}
    		
    		while(true) {
    			DatagramPacket p = new DatagramPacket(new byte[65535], 65535);
    			s.receive(p);
    			queue.add(p);
    		}
    		
    	} catch(SocketException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }

}

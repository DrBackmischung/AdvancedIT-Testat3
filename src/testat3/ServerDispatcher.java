package testat3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @author Mathis Neunzig
 * @author Nathalie Möck
 */
public class ServerDispatcher {
	
	// Die Dateien befinden sich im Ordner /Desktop/Files, dieser muss manuell angelegt werden
	private static final String PATH = System.getProperty("user.home") + "/Desktop/Files/";

    private static DatagramSocket s = null;
    // Worker-Pool
    private static ServerWorker[] workers;
    private static DatagramPaketQueue queue;
    private static FileHandler f;
    
    /**
	 * Hauptmethode zur Ausführung
	 * @param args Argumente zum Starten des TestClients
	 * @throws SocketException Wird geworfen, wenn der Socket nicht erstellt werden kann
	 * @throws IOException Wird geworfen, wenn das Empfangen nicht klappt
     */
    public static void main(String[] args) {

    	// Zur Demo haben wir 4 Worker
    	workers = new ServerWorker[4];
    	queue = new DatagramPaketQueue();
    	f = new FileHandler(PATH);
    	
    	try {
    		
    		s = new DatagramSocket(5999);
    		System.out.println("Server auf Port 5999 gestartet!");
    		// Worker starten
    		for(int i = 0; i<workers.length; i++) {
    			workers[i] = new ServerWorker(i, s, queue, f);
    			workers[i].start();
                System.out.println("["+i+"] Worker gestartet");
    		}
    		
    		// DatagramPackages empfangen und zur Queue hinzufügen
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

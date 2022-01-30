package testat3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author Mathis Neunzig
 * @author Nathalie Möck
 */
public class ServerWorker extends Thread {
	
	private int id;
	private DatagramSocket s;
	private DatagramPaketQueue queue;
	private FileHandler f;

	public ServerWorker(int i, DatagramSocket s, DatagramPaketQueue queue, FileHandler f) {
		
		this.id = i;
		this.s = s;
		this.queue = queue;
		this.f = f;
		
	}
	
    /**
     * Methode, die beim Thread-Start ausgeführt wird
     * @throws IOException Wird geworfen, wenn es Probleme gibt, das DatagramPacket zu verschicken
     */
    @Override
    public void run() {
        while(true) {
        	// DatagramPacket wird aus Queue entnommen
            DatagramPacket request = queue.remove();
            System.out.println("["+id+"] Worker startet mit der Bearbeitung einer Request");
            DatagramPacket response = createResponse(request);
            // Antwort wird zum Client zurückgeschickt
            try {
                s.send(response);
            } catch (IOException e) {
            	e.printStackTrace();
            }
            System.out.println("["+id+"] Worker hat die Bearbeitung abgeschlossen");
        }
    }
    
    /**
     * Erstellt für ein eingehendes DatagramPacket eine Antwort
     * @param request Zu bearbeitendes DatagramPacket
     * @return DatagramPacket, was zum Client zurückgesendet wird
     */
    public DatagramPacket createResponse(DatagramPacket request) {
    	
    	// Daten aus der Request entnehmen
    	int l = request.getLength();
    	InetAddress i = request.getAddress();
    	int port = request.getPort();
    	byte[] data = request.getData();
    	String cmd = new String(data, 0, l);
    	String answer = "Status 500: Internal Server Error";
    	
    	// Befehl aufteilen
    	String[] args = cmd.split(" ", 2);
    	// Befehl muss aus zwei Teilen bestehen
    	if(args.length != 2) {
    		
    		answer = "Status 406: Not Acceptable (Wrong Syntax, use 'READ <fileName>,<lineNumber>' or 'WRITE <fileName>,<lineNumber>,<text>')";
    		
    	} else {
    		
    		// Lese-Zugriff
    		if(args[0].equals("READ")) {
    			
    			String[] params = args[1].split(",");
    			// Lese-Zugriff braucht 2 Parameter
    			if(params.length != 2) {
    				
    				answer = "Status 406: Not Acceptable (READ needs two parameters: <fileName>,<lineNumber>)";
    				
    			} else {
    				
    				try {
        				
        				int line = Integer.parseInt(params[1].trim());
        				
        				// Nur positive, ganze Zeilennummern >0 sind erlaubt
        				if(line < 1) {
        					answer = "Status 406: Not acceptable (LineNumber has to be a positive number >= 1)";
        				} else {

        		            System.out.println("["+id+"] Worker bearbeitet: "+cmd);
        		            // Aus Datei lesen
        					answer = f.read(id, params[0], line);
        					
        				}
    					
    				} catch (IllegalArgumentException e) {
        				// Nur positive, ganze Zeilennummern >0 vom Typ "Integer" sind erlaubt
    					answer = "Status 406: Not acceptable (LineNumber has to be an integer with a positive value greater then 1)";
    				}
    				
    			}

    		// Schreib-Zugriff
    		} else if(args[0].equals("WRITE")) {
    			
    			String[] params = args[1].split(",");
    			// Schreib-Zugriff braucht 2 Parameter
    			if(params.length != 3) {
    				
    				answer = "Status 406: Not Acceptable (WRITE needs three parameters: <fileName>,<lineNumber>,<text>)";
    				
    			} else {
    				
    				try {
        				
        				int line = Integer.parseInt(params[1].trim());

        				// Nur positive, ganze Zeilennummern >0 sind erlaubt
        				if(line < 1) {
        					answer = "Status 406: Not acceptable (LineNumber has to be a positive number >= 1)";
        				} else {

        		            System.out.println("["+id+"] Worker bearbeitet: "+cmd);
        		            // In Datei schreiben
        					answer = f.write(id, params[0], line, params[2]);
        					
        				}
    					
    				} catch (IllegalArgumentException e) {
        				// Nur positive, ganze Zeilennummern >0 vom Typ "Integer" sind erlaubt
    					answer = "Status 406: Not acceptable (LineNumber has to be an integer with a positive value greater then 1)";
    				}
    				
    			}
    			
    		// Nur READ und WRITE wird unterstützt
    		} else {
    			
    			answer = "Status 406: Not Acceptable (Only 'READ <fileName>,<lineNumber>' or 'WRITE <fileName>,<lineNumber>,<text>') allowed";
    			
    		}
    		
    	}
    	
    	// Antwort in DatagramPacket zurückgeben
    	byte[] responseData = answer.getBytes();
    	DatagramPacket response = new DatagramPacket(responseData, responseData.length, i, port);
    	return response;
    	
    }

}

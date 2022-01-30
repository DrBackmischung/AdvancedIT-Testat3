package testat3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

public class ServerWorker extends Thread {
	
	private int id;
	private DatagramSocket s;
	private DatagramPaketQueue queue;
	private FileHandler f;
	Map<String, FileMonitor> monitor;

	public ServerWorker(int i, DatagramSocket s, DatagramPaketQueue queue, FileHandler f, Map<String, FileMonitor> monitor) {
		
		this.id = i;
		this.s = s;
		this.queue = queue;
		this.f = f;
		this.monitor = monitor;
		
	}
	
    @Override
    public void run() {
        while(true) {
            DatagramPacket request = queue.remove();
            System.out.println("["+id+"] Worker startet mit der Bearbeitung einer Request");
            DatagramPacket response = createResponse(request);
            try {
                s.send(response);
            } catch (IOException e) {
            	e.printStackTrace();
            }
            System.out.println("["+id+"] Worker hat die Bearbeitung abgeschlossen");
            try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            System.out.println("["+id+"] Worker ist bereit für neuen Auftrag");
        }
    }
    
    public DatagramPacket createResponse(DatagramPacket request) {
    	
    	int l = request.getLength();
    	InetAddress i = request.getAddress();
    	int port = request.getPort();
    	byte[] data = request.getData();
    	String cmd = new String(data, 0, l);
    	String answer = "Status 500: Internal Server Error";
    	
    	String[] args = cmd.split(" ", 2);
    	if(args.length != 2) {
    		
    		answer = "Status 406: Not Acceptable (Wrong Syntax, use 'READ <fileName>,<lineNumber>' or 'WRITE <fileName>,<lineNumber>,<text>')";
    		
    	} else {
    		
    		if(args[0].equals("READ")) {
    			
    			String[] params = args[1].split(",");
    			if(params.length != 2) {
    				
    				answer = "Status 406: Not Acceptable (READ needs two parameters: <fileName>,<lineNumber>";
    				
    			} else {
    				
    				try {
        				
        				int line = Integer.parseInt(params[1].trim());
        				
        				if(line < 1) {
        					answer = "Status 406: Not acceptable (LineNumber has to be a positive number >= 1)";
        				} else {

        		            System.out.println("["+id+"] Worker bearbeitet: "+cmd);
        					answer = f.read(id, params[0], line, monitor);
        					
        				}
    					
    				} catch (IllegalArgumentException e) {
    					answer = "Status 406: Not acceptable (LineNumber has to be an integer with a positive value greater then 1)";
    				}
    				
    			}
    			
    		} else if(args[0].equals("WRITE")) {
    			
    			String[] params = args[1].split(",");
    			if(params.length != 3) {
    				
    				answer = "Status 406: Not Acceptable (WRITE needs three parameters: <fileName>,<lineNumber>,<text>)";
    				
    			} else {
    				
    				try {
        				
        				int line = Integer.parseInt(params[1].trim());
        				
        				if(line < 1) {
        					answer = "Status 406: Not acceptable (LineNumber has to be a positive number >= 1)";
        				} else {

        		            System.out.println("["+id+"] Worker bearbeitet: "+cmd);
        					answer = f.write(id, params[0], line, params[2], monitor);
        					
        				}
    					
    				} catch (IllegalArgumentException e) {
    					answer = "Status 406: Not acceptable (LineNumber has to be an integer with a positive value greater then 1)";
    				}
    				
    			}
    			
    		} else {
    			
    			answer = "Status 406: Not Acceptable (Only 'READ <fileName>,<lineNumber>' or 'WRITE <fileName>,<lineNumber>,<text>') allowed";
    			
    		}
    		
    	}
    	
    	byte[] responseData = answer.getBytes();
    	DatagramPacket response = new DatagramPacket(responseData, responseData.length, i, port);
    	return response;
    	
    }

}

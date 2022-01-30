package testat3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author Mathis Neunzig
 * @author Nathalie Möck
 */
public class Client {
	
	/**
	 * Hauptmethode zur Ausführung
	 * @param args Argumente zum Starten des TestClients
	 * @throws SocketException Wird geworfen, wenn der Socket nicht erstellt werden kann
	 * @throws UnknownHostException Wird geworfen, wenn localhost nicht gefunden werden kann
	 * @throws IOException Wird geworfen, es ein Problem beim verschicken gibt
	 * @throws Exception Wird geworfen, wenn sonstige Fehler auftreten
	 */
	public static void main(String[] args) {
		
		final int MAX = 65535;
		
		try(DatagramSocket s = new DatagramSocket(); BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
			InetAddress host = InetAddress.getLocalHost();
			while(true) {
				// Eingabe einlesen
				System.out.print("> ");
				String msg = in.readLine();
				// Abbrechen, wenn user nichts abschickt oder ein "."
				if(msg == null || msg == ".") break;
				int l = msg.length();
				// Wenn die Nachricht zu groß ist, wird sie zurückgeworden
				if(l > MAX)
					throw new Exception("Zu größer Datensatz!");
				// Ein DatagramPacket wird erstellt und abgeschickt
				DatagramPacket p = new DatagramPacket(msg.getBytes(), l, host, 5999);
				s.send(p);
				// Ankommende DatagramPackete werden empfangen und ausgegeben
				byte[] puffer = new byte[MAX];
				DatagramPacket i = new DatagramPacket(puffer, puffer.length);
				s.receive(i);
				String response = new String(i.getData(), 0, i.getLength());
				System.out.println(response);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
		
	}

}

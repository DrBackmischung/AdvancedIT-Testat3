package testat3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author Mathis Neunzig
 * @author Nathalie Möck
 */
public class TestClient {
	
	static DatagramSocket s = null;
	final int MAX = 65535;
	
	/**
	 * Hauptmethode zur Ausführung
	 * @param args Argumente zum Starten des TestClients
	 * @throws SocketException Wird geworfen, wenn der Socket nicht erstellt werden kann
	 * @throws Exception Wird geworfen, wenn sonstige Fehler auftreten
	 */
	public static void main(String[] args) {
		
		try {
			s = new DatagramSocket();
			// Testfall ausführen
//			test1();
//			test2();
//			test3();
//			test4();
//			test5();
//			test6();
//			test7();
//			test8();
//			test9();
//			test10();
//			test11();
//			test12();
//			test13();
//			test14();
//			test15();
//			test16();
//			test17();
//			test18();
			// DatagramPackets empfangen und ausgeben
			while(true) {
				byte[] puffer = new byte[65535];
				DatagramPacket i = new DatagramPacket(puffer, puffer.length);
				s.receive(i);
				String response = new String(i.getData(), 0, i.getLength());
				System.out.println(response);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			s.close();
		}
		
	}
	
	/**
	 * Sendet den Befehl an den Server
	 * @param Befehl, der an den Server geschickt wird
	 * @throws UnknownHostException Wird geworfen, wenn localhost nicht gefunden werden kann
	 * @throws IOException Wird geworfen, wenn es Probleme gibt, DatagramPackete zu verschicken
	 */
	public static void send(String cmd) {
		
		// DatagramPacket erstellen und verschicken
		try {
			DatagramPacket p = new DatagramPacket(cmd.getBytes(), cmd.getBytes().length, InetAddress.getLocalHost(), 5999);
			s.send(p);
			System.out.println(cmd);
			//
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	// Testmethoden zur automatischen, parallelen Ausführung
	
	public static void test1() {
		send("READ veg.txt,1");
		send("READ veg.txt,2");
		send("READ veg.txt,3");
		send("READ veg.txt,4");
	}
	
	public static void test2() {
		send("WRITE veg.txt,1,Sellerie");
		send("WRITE veg.txt,2,Tomate");
		send("WRITE veg.txt,3,Kartoffel");
		send("WRITE veg.txt,4,Brokkoli");
	}
	
	public static void test3() {
		send("WRITE veg.txt,2,Ruccula");
		send("READ veg.txt,1");
		send("WRITE veg.txt,1,Steckrübe");
		send("READ veg.txt,1");
	}
	
	public static void test4() {
		send("READ veg.txt,1");
		send("READ veg.txt,2");
		send("READ fruit.txt,3");
		send("READ fruit.txt,4");
	}
	
	public static void test5() {
		send("WRITE fruit.txt,1,Birne");
		send("WRITE fruit.txt,2,Orange");
		send("WRITE fruit.txt,1,Kaki");
		send("WRITE veg.txt,4,Pok Choi");
	}
	
	public static void test6() {
		send("READ fruit.txt,1");
		send("WRITE veg.txt,1,Rotkohl");
		send("READ veg.txt,1");
		send("WRITE veg.txt,2,Rosenkohl");
	}
	
	public static void test7() {
		send("READ beer.txt,1");
	}
	
	public static void test8() {
		send("WRITE beer.txt,1,Astra Rakete");
	}
	
	public static void test9() {
		send("READ veg.txt,6");
	}
	
	public static void test10() {
		send("WRITE fruit.txt,6,Pampelmuse");
	}
	
	public static void test11() {
		send("READ veg.txt");
	}
	
	public static void test12() {
		send("WRITE fruit.txt,1");
		send("WRITE fruit.txt");
	}
	
	public static void test13() {
		send("READ");
		send("WRITE");
	}
	
	public static void test14() {
		send("READ veg.txt,Marmelade");
	}
	
	public static void test15() {
		send("WRITE veg.txt,Birnenmilchshake,Kürbis");
	}
	
	public static void test16() {
		send("READ veg.txt,-3");
	}
	
	public static void test17() {
		send("WRITE veg.txt,-4,Kürbis");
	}
	
	public static void test18() {
		send("KÜRBISKUCHEN");
		send("котенок");
	}
	
	public static void test19() {
		send(".");
	}

}

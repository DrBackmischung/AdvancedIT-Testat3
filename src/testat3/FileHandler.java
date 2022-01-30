package testat3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mathis Neunzig
 * @author Nathalie Möck
 */
public class FileHandler {
	
	private String path;
	// Für jede Datei wird ein eigener Monitor gepflegt
	Map<String, FileMonitor> monitors = new HashMap<>();
	
	public FileHandler(String path) {
		this.path = path;
		File d = new File(path);
		File[] files = d.listFiles();
		// Alle Monitore erstellen
		for(File f : files) {
			monitors.put(f.getName(), new FileMonitor());
		}
	}

	/**
	 * Liest die Datei
	 * @param id Worker ID
	 * @param file Datei, die gelesen wird
	 * @param line Zu lesende Zeilennummer
	 * @return Antwort an den Client
	 * @throws FileNotFoundException Wird geworfen, wenn Datei nicht gefunden wird
	 * @throws IOException Wird geworden, wenn es Dateifehler gibt
	 * @throws InterruptedException Wird geworfen, wenn Thread.sleep() Probleme macht
	 */
	public String read(int id, String file, int line) {
		
		// Monitor raussuchen
		FileMonitor m = monitors.get(file);
		if(m == null)
			return "Status 404: Not found (File not found)";
		
		// Monitor betreten
		m.startReading();
		
		try {

			// Zur Simulation "schlafen"
			Thread.sleep(2000);
			// Datei lesen
			BufferedReader br = new BufferedReader(new FileReader(path+file));
			String s = "";
			
			// Alle nicht wichtigen Zeilen überschreiben
			for(int i = 0; i < line; i++) {
				s = br.readLine();
			}
			
			// Schließen
			br.close();
			
			// Wenn die Zeile null ist und nicht gefunden wird, existiert diese nicht
			if(s == null) {
				return "Status 404: Not found (Line "+line+" was not found)";
			}
			
			return s;
			
		} catch(FileNotFoundException e) {
			
			m.endWriting();
			return "Status 404: Not found (File not found)";
			
		} catch (IOException e) {
			
			m.endWriting();
			return "Status 500: Internal Server Error";
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			return "Status 500: Internal Server Error";
		} finally {
			// Monitor verlassen
			m.endReading();
		}
		
	}

	/**
	 * Schreibt in eine Datei
	 * @param id Worker ID
	 * @param file Datei, die beschrieben wird
	 * @param line Zu schreibende Zeilennummer
	 * @param data Zu schreibender Inhalt
	 * @return Antwort an den Client
	 * @throws FileNotFoundException Wird geworfen, wenn Datei nicht gefunden wird
	 * @throws IOException Wird geworden, wenn es Dateifehler gibt
	 * @throws InterruptedException Wird geworfen, wenn Thread.sleep() Probleme macht
	 */
	public String write(int id, String file, int line, String data) {
		
		// Monitor raussuchen
		FileMonitor m = monitors.get(file);
		if(m == null)
			return "Status 404: Not found (File not found)";

		// Monitor betreten
		m.startWriting();
		
		try {

			// Zur Simulation "schlafen"
			Thread.sleep(2000);
			// Originaldatei, aus der gelesen wird
			File f = new File(path+file);
			// Cache, in den geschrieben wird
			File temp = new File(path+file + ".new");
			String s = "";

			// Reader/Writer initialisieren
			BufferedReader br = new BufferedReader(new FileReader(f));
			PrintWriter pw = new PrintWriter(temp);
			
			int i = 1;
			
			// Solange es Zeilen gibt oder die zu schreibende Zeile nicht erreicht ist
			while(((s = br.readLine()) != null) || i <= line) {
				
				// Zeile ersetzen
				if(i == line) {
					pw.println(data);
				} else {
					// Leere Zeilen mit leerem String füllen (nicht mit s, dann wird "null" reingeschrieben), sonst mit alter Zeile befüllen
					if(s == null) {
						pw.println("");
					} else {
						pw.println(s);
					}
				}
				i++;
			}

			// Speichern
			pw.flush();

			// Schließen
			br.close();
			pw.close();
			// Originaldatei löschen
			f.delete();
			// Cache abspeichern unter Dateinamen der Originaldatei
			temp.renameTo(f); 

			// Monitor verlassen
			m.endWriting();
			return "Status 200: WRITE executed ("+data+")";
			
		} catch(FileNotFoundException e) {
			
			m.endWriting();
			return "Status 404: Not found (File not found)";
			
		} catch (IOException e) {
			
			m.endWriting();
			return "Status 500: Internal Server Error";
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			return "Status 500: Internal Server Error";
		}
		
	}

}

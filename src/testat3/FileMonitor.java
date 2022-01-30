package testat3;

/**
 * @author Mathis Neunzig
 * @author Nathalie Möck
 */
public class FileMonitor {
	
	// Zähler für die Anzahl an Workern, die sich im k.A. befinden
	private int reader = 0;
	private int writer = 0;
	// Nur ein Writer darf aktiv sein, ist isWriting = true, ist ein Writer am Arbeiten und andere müssen warten
	private boolean isWriting = false;
	
	/**
	 * Betreten des k.A. "Lesen"
	 */
	public synchronized void startReading() {
		
		try {
			
			// Es darf nur gelesen werden, wenn es keine Writer gibt
			while(writer > 0) {
				wait();
			}
			
			reader ++;
			notifyAll();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Verlassen des k.A. "Lesen"
	 */
	public synchronized void endReading() {
		
		reader --;
		notifyAll();
		
	}

	/**
	 * Betreten des k.A. "Schreiben"
	 */
	public synchronized void startWriting() {
		
		try {
			
			// Schreiberpriorität
			writer ++;
			
			// Falls noch Lese-Zugriffe aktiv sind, muss gewartet werden
			while(reader > 0 || isWriting) {
				wait();
			}
			
			// Writer ist aktiv
			isWriting = true;
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Verlassen des k.A. "Schreiben"
	 */
	public synchronized void endWriting() {

		writer --;
		isWriting = false;
		notifyAll();
		
	}

}

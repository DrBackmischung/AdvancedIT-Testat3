package testat3;

public class FileMonitor {
	
	@SuppressWarnings("unused")
	private int reader = 0;
	private int writer = 0;
	private boolean isWriting = false;
	
	public synchronized void startReading() {
		
		try {
			
			while(writer > 0) {
				this.wait();
			}
			
			reader ++;
			this.notifyAll();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public synchronized void endReading() {
		
		reader --;
		this.notifyAll();
		
	}
	
	public synchronized void startWriting() {
		
		try {
			
			writer ++;
			
			while(writer > 0 || isWriting) {
				this.wait();
			}
			
			isWriting = true;
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public synchronized void endWriting() {
		
		writer --;
		isWriting = false;
		this.notifyAll();
		
	}

}

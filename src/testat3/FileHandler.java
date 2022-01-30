package testat3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class FileHandler {
	
	private String path;
	private FileMonitor m;
	
	public FileHandler(String path) {
		this.path = path;
	}

	public String read(int id, String file, int line, Map<String, FileMonitor> monitor) {
		
		if(monitor.keySet().contains(file)) {
			m = monitor.get(file);
		} else {
			m = new FileMonitor();
			monitor.put(file, m);
		}
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(path+file));
			String s = "";
			
			for(int i = 0; i < line; i++) {
				s = br.readLine();
			}
			
			br.close();
			
			if(s == null) {
				return "Status 404: Not found (Line "+line+" was not found)";
			}
			
			return s;
			
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			
			return "Status 500: Internal Server Error";
		}
		
		return null;
		
	}

	public String write(int id, String file, int line, String data, Map<String, FileMonitor> monitor) {
		
		try {
			
			File f = new File(path+file);
			File temp = new File(path+file + ".new");
			String s = "";
			
			BufferedReader br = new BufferedReader(new FileReader(f));
			
			PrintWriter pw = new PrintWriter(temp);
			
			int i = 1;
			
			while(((s = br.readLine()) != null) || i <= line) {
				
				if(i == line) {
					pw.println(data);
				}else {
					if(s == null) {
						pw.println("");
					}else {
						pw.println(s);
					}
				}
				i++;
			}
			
			pw.flush();
			
			br.close();
			pw.close();
			f.delete();
			temp.renameTo(f); 
			return "Status 200: WRITE executed";
			
		} catch(FileNotFoundException e) {
			
			return "Status 404: Not found (File not found)";
			
		} catch (IOException e) {
			
			return "Status 500: Internal Server Error";
			
		}
		
	}

}

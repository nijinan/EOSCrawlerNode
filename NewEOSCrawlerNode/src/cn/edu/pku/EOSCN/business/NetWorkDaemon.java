package cn.edu.pku.EOSCN.business;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;

public class NetWorkDaemon {
	public static boolean isok = true;
	static {
		NetWorkDaemon.init();
	}
	public static void init() {
		// TODO Auto-generated constructor stub
		Thread thread = new Thread(){
			public void run(){
				while (true){
					try {
						isok = isConnect();
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					FileUtil.clear();
				}
				
			}
		};
		//thread.setDaemon(false);
		thread.setDaemon(true);
		thread.start();
	}
	
	public static boolean isConnect() { 
        Runtime runtime = Runtime.getRuntime(); 
        try { 
            Process process = runtime.exec("ping " + "14.215.177.37"); 
            InputStream is = process.getInputStream(); 
            InputStreamReader isr = new InputStreamReader(is); 
            BufferedReader br = new BufferedReader(isr); 
            String line = null; 
            StringBuffer sb = new StringBuffer(); 
            while ((line = br.readLine()) != null) { 
                sb.append(line); 
                // System.out.println("����ֵΪ:"+line);  
            } 
            is.close(); 
            isr.close(); 
            br.close(); 
 
            if (null != sb && !sb.toString().equals("")) { 
                String logString = ""; 
                if (sb.toString().indexOf("TTL") > 0) { 
                    logString = "Connected" + getCurrentTime(); 
                    System.out.println(logString);
                    return true;	                    
                } else { 
                    logString = "Disconnected " + getCurrentTime(); 
                    System.out.println(logString);  
                    return false;	                  
                } 
             
            } 
            return false;
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
		return false; 
    } 
    public static String getCurrentTime() { 
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        String time = sdf.format(new Date()); 
        return time; 
    } 
}

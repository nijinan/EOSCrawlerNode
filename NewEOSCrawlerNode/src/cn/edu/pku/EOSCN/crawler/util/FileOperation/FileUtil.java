package cn.edu.pku.EOSCN.crawler.util.FileOperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.Path;

public class FileUtil {
	private static Map<String,Map<String,Boolean>> logCache = new ConcurrentHashMap <String,Map<String,Boolean>>();	
	private static Map<String,Map<String,Object>> writeBackList = new ConcurrentHashMap <String,Map<String,Object>>();
	static {
		FileUtil.init();
	}
	public static void init() {
		// TODO Auto-generated constructor stub
		Thread thread = new Thread(){
			public void run(){
				while (true){
					try {
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
		thread.start();
	}
	

	public static boolean deleteFolder(String sPath) {  
	   boolean flag = false;  
	   File file = new File(sPath);  
	    // 判断目录或文件是否存在  
	    if (!file.exists()) {  // 不存在返回 false  
	        return flag;  
	    } else {  
	        // 判断是否为文件  
	        if (file.isFile()) {  // 为文件时调用删除文件方法  
	            return deleteFile(sPath);  
	        } else {  // 为目录时调用删除目录方法  
	            return deleteDirectory(sPath);  
	        }  
	    }  
	}  
	
	public static boolean deleteFile(String sPath) {  
	    boolean flag = false;  
	    File file = new File(sPath);  
	    // 路径为文件且不为空则进行删除  
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	        flag = true;  
	    }  
	    return flag;  
	}  
	public static boolean deleteDirectory(String sPath) {  
	    //如果sPath不以文件分隔符结尾，自动添加文件分隔符  
	    if (!sPath.endsWith(File.separator)) {  
	        sPath = sPath + File.separator;  
	    }  
	    File dirFile = new File(sPath);  
	    //如果dir对应的文件不存在，或者不是一个目录，则退出  
	    if (!dirFile.exists() || !dirFile.isDirectory()) {  
	        return false;  
	    }  
	    boolean flag = true;  
	    //删除文件夹下的所有文件(包括子目录)  
	    File[] files = dirFile.listFiles();  
	    for (int i = 0; i < files.length; i++) {  
	        //删除子文件  
	        if (files[i].isFile()) {  
	            flag = deleteFile(files[i].getAbsolutePath());  
	            if (!flag) break;  
	        } //删除子目录  
	        else {  
	            flag = deleteDirectory(files[i].getAbsolutePath());  
	            if (!flag) break;  
	        }  
	    }  
	    if (!flag) return false;  
	    //删除当前目录  
	    if (dirFile.delete()) {  
	        return true;  
	    } else {  
	        return false;  
	    }  
	}  
	public static void clear(){
		for (String str : writeBackList.keySet()){
			FileUtil.saveLog(str);
		}

	}
	
	public static File createPath(String path){
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String filePath = 
				String.format("%s%c%s", 
						path,
						Path.SEPARATOR,
						"log.txt");
		File file = new File(filePath);	
		try {
			if (!file.exists()){
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dir;
	}
	
	public static File createFile(String path, String fileName) {
		createPath(path);
		String filePath = 
				String.format("%s%c%s", 
						path,
						Path.SEPARATOR,
						fileName);
		File file = new File(filePath);	
		try {
			if (!file.exists()){
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public static File createFile(String fullPath){
		String fileName = fullPath.substring(fullPath.lastIndexOf(Path.SEPARATOR)+1);
		String path = fullPath.substring(0,fullPath.lastIndexOf(Path.SEPARATOR));
		return createFile(path,fileName);
	}
	
	public static boolean exist(String path, String fileName){
		createPath(path);
		String filePath = 
				String.format("%s%c%s", 
						path,
						Path.SEPARATOR,
						fileName);
		File file = new File(filePath);	
		return file.exists();
	}
	
	public static boolean exist(String fullPath){
		String fileName = fullPath.substring(fullPath.lastIndexOf(Path.SEPARATOR)+1);
		String path = fullPath.substring(0,fullPath.lastIndexOf(Path.SEPARATOR));
		return exist(path,fileName);
	}
	
	public static void loadLog(String path, Map<String,Boolean> set){
		String loggerPath =
				String.format("%s%c%s",
						path,
						Path.SEPARATOR,
						"log.txt");
		createPath(path);
		File file = new File(loggerPath);
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufr = new BufferedReader(isr);
			String inputStr = null;
			while ((inputStr = bufr.readLine()) != null){
				set.put(inputStr, true);
			}
			bufr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveLog(String path){
		Map<String,Object> set = FileUtil.writeBackList.get(path);
		if (set == null) return;
		String loggerPath =
				String.format("%s%c%s",
						path,
						Path.SEPARATOR,
						"log.txt");
		File file = new File(loggerPath);
		FileWriter fw;
		try {
			fw = new FileWriter(file,true);
			if (fw != null) {
				for (String fileName : set.keySet()){
					//if (!set.get(fileName)){
						fw.append(fileName + "\n");
						set.put(fileName, true);
					//}
				}
				fw.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		set.clear();
	}
	
	public static boolean loggedByRegex(String path, String fileName, String regex){
		Map<String,Boolean> set;
		if (FileUtil.logCache.containsKey(path)){
			set = FileUtil.logCache.get(path);
		}else{
			set = new ConcurrentHashMap<String,Boolean>();
			loadLog(path,set);
			FileUtil.logCache.put(path, set);
		}
		for (String s : set.keySet()){
			if (s.matches(regex)) return true;
		}
		return false;
	}
	
	public static boolean logged(String path, String fileName){
		Map<String,Boolean> set;
		if (FileUtil.logCache.containsKey(path)){
			set = FileUtil.logCache.get(path);
		}else{
			set = new ConcurrentHashMap<String,Boolean>();
			loadLog(path,set);
			FileUtil.logCache.put(path, set);
		}
		return set.containsKey(fileName);
	}
	
	public static boolean loggedByRegex(String fullPath, String regex){
		String fileName = fullPath.substring(fullPath.lastIndexOf(Path.SEPARATOR)+1);
		String path = fullPath.substring(0,fullPath.lastIndexOf(Path.SEPARATOR));
		return loggedByRegex(path,fileName,regex);	
	}
	
	public static boolean logged(String fullPath){
		try{
			String fileName = fullPath.substring(fullPath.lastIndexOf(Path.SEPARATOR)+1);
			String path = fullPath.substring(0,fullPath.lastIndexOf(Path.SEPARATOR));
			return logged(path,fileName);
		}catch (Exception e){
			return false;
		}
	}
	
	public static void logging(String path, String fileName){
		Map<String,Boolean> set;
		if (FileUtil.logCache.containsKey(path)){
			set = FileUtil.logCache.get(path);
		}else {
			set = new ConcurrentHashMap<String,Boolean>();
			loadLog(path,set);
			FileUtil.logCache.put(path, set);
		}
		
		if (!set.containsKey(fileName)){
			set.put(fileName, false);
			writeBack(path,fileName);
		}
	}
	
	public static void writeBack(String path, String filename){
		Map<String,Object> set;
		if (FileUtil.writeBackList.containsKey(path)){
			set = FileUtil.writeBackList.get(path);
		}else{
			set = new ConcurrentHashMap<String,Object>();
			FileUtil.writeBackList.put(path, set);
		}
		set.put(filename, new Object());
		if (set.size() > 20){
			saveLog(path);
		}
	}
	
	public static void logging(String fullPath){
		try{
			if (!exist(fullPath)) return;
			String fileName = fullPath.substring(fullPath.lastIndexOf(Path.SEPARATOR)+1);
			String path = fullPath.substring(0,fullPath.lastIndexOf(Path.SEPARATOR));
			logging(path,fileName);		
		}catch (Exception e){
			return ;
		}
	}
	
	public static String read(String fullPath){
		StringBuffer ret = new StringBuffer("");
		try {
		File file = new File(fullPath);
		FileReader fr;
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			int tot = 0;
			while ((line = br.readLine()) != null){
				tot++;
				ret.append(line + "\n");
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return ret.toString();
	}
	
	public static void write(String fullPath, String content){
		try {
			File file = FileUtil.createFile(fullPath);
			FileOutputStream baos = new FileOutputStream(file);  
			FileWriter fw;
			//fw = new FileWriter(file);
			
			baos.write(content.getBytes("ISO-8859-1"));
			baos.close();
			//fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package cn.edu.pku.EOSCN.crawler.util.FileOperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.Path;

public class FileUtil {

	public FileUtil() {
		// TODO Auto-generated constructor stub
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
	
	public static boolean logged(String path, String fileName) throws Exception{
		String loggerPath =
				String.format("%s%c%s",
						path,
						Path.SEPARATOR,
						fileName);
		File file = new File(loggerPath);
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader bufr = new BufferedReader(isr);
		String inputStr = null;
		while ((inputStr = bufr.readLine()) != null){
			if (inputStr.equals(fileName)){
				bufr.close();
				return true;
			}
		}
		bufr.close();
		return false;
	}
	
	public static boolean logged(String fullPath) throws Exception{
		String fileName = fullPath.substring(fullPath.lastIndexOf(Path.SEPARATOR)+1);
		String path = fullPath.substring(0,fullPath.lastIndexOf(Path.SEPARATOR));
		return logged(path,fileName);
	}
	
	public static void logging(String path, String fileName) throws Exception{
		String loggerPath =
				String.format("%s%c%s",
						path,
						Path.SEPARATOR,
						fileName);
		File file = new File(loggerPath);
		FileWriter fw =  new FileWriter(file);
		if (fw != null) {
			fw.append("\n" + fileName);
			fw.close();
		}
	}
	
	public static void logging(String fullPath) throws Exception{
		String fileName = fullPath.substring(fullPath.lastIndexOf(Path.SEPARATOR)+1);
		String path = fullPath.substring(0,fullPath.lastIndexOf(Path.SEPARATOR));
		logging(path,fileName);		
	}
}

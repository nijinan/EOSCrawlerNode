package cn.edu.pku.EOSCN.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

import cn.edu.pku.EOSCN.TestUtil;
import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.entity.Project;
import cn.edu.pku.EOSCN.entity.ResourceMetaData;
/**
 * 和文件存储有关的工具类
 * @author 灵箫
 *
 */
public class StorageUtil {

	public static final int SUCCESS=1;
	public static final int FAIL=0;
		
	
	/**
	 * 根据项目得到版本库存储地址，其他存储地址的方法可以仿写
	 * @author 张灵箫
	 * @throws MalformedURLException 
	 * @throws SmbException 
	 */
	public static String getSourceCodeFilePath(Project project) throws MalformedURLException, SmbException {
		String dirString = Config.getEOSDir() + project.getUuid() + "/" +
				ResourceMetaData.CODE_TYPE +
				"/";
		SmbFile dir = new SmbFile(dirString);
		if (!dir.exists()) {
			dir.mkdirs();
			System.out.println("making dir at " + dirString + "...");
		}
		return dirString;
	}
	
	public static String getRelativeWebFilePath(Project project) throws MalformedURLException, SmbException {
		String dirString = Config.getEOSDir() + project.getUuid() + "/" +
				ResourceMetaData.RELATIVEWEB_TYPE +
				"/";
		SmbFile dir = new SmbFile(dirString);
		if (!dir.exists()) {
			dir.mkdirs();
			System.out.println("making dir at " + dirString + "...");
		}
		return dirString;
	}
	
	public static String getDocumentationsFilePath(Project project) throws MalformedURLException, SmbException {
		String dirString = Config.getEOSDir() + project.getUuid() + "/" +
				ResourceMetaData.DOC_TYPE +
				"/";
		SmbFile dir = new SmbFile(dirString);
		if (!dir.exists()) {
			dir.mkdirs();
			System.out.println("making dir at " + dirString + "...");
		}
		return dirString;
	}
	
	public static String getMailingListFilePath(Project project) throws MalformedURLException, SmbException {
		String dirString = Config.getEOSDir() + project.getUuid() + "/" +
				ResourceMetaData.MAIL_TYPE +
				"/";
		SmbFile dir = new SmbFile(dirString);
		if (!dir.exists()) {
			dir.mkdirs();
			System.out.println("making dir at " + dirString + "...");
		}
		return dirString;
	}
	
	public static String getIssueTrackerFilePath(Project project) throws MalformedURLException, SmbException {
		String dirString = Config.getEOSDir() + project.getUuid() + "/" +
				ResourceMetaData.BUG_TYPE +
				"/";
		SmbFile dir = new SmbFile(dirString);
		if (!dir.exists()) {
			dir.mkdirs();
			System.out.println("making dir at " + dirString + "...");
		}
		return dirString;
	}
	
	@Deprecated
	public static int storeTextFileLocal(String content, String filePath)
	{
		
		BufferedWriter bw;
		try {
			//System.out.println(filePath);
			bw = new BufferedWriter(new FileWriter(filePath, false));
			bw.write(content);
			bw.flush();
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return FAIL;
		}
		return SUCCESS;
		
	}
	
	public static int storeTextFileRemote(String content, String remoteUrl)
	{
		
	    OutputStream out = null;       
	    try {       
	       
	              
	      //  System.out.println("remoteUrl="+remoteUrl);
	        SmbFile remoteFile = new SmbFile(remoteUrl);       
	        
	        out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile));     
	        
	        byte[] buffer = content.getBytes();     
	        out.write(buffer);       
	        out.flush();       
	        out.close();       
	        return SUCCESS;
	    } catch (Exception e) {       
	        e.printStackTrace();       
	    } finally {       
	             
	    }    
	    return FAIL;
		
	}
	
	public static void main(String[] args) throws MalformedURLException, SmbException {
		Project luceneProject;
		luceneProject = TestUtil.getLuceneProject();
		String dirString = getSourceCodeFilePath(luceneProject);
		System.out.println(dirString);
		getMailingListFilePath(luceneProject);
		getDocumentationsFilePath(luceneProject);
		System.out.println(getProjectXmlFile(luceneProject.getUuid()));
	}

	public static String getProjectXmlFile(String uuid) {
		return Config.getEOSDir() + uuid + "/" + uuid + ".xml";
	}
}

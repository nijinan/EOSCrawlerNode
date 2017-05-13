package cn.edu.pku.EOSCN.crawler.util;

import java.io.File;
import java.sql.SQLException;

import org.json.JSONObject;

import cn.edu.pku.EOSCN.DAO.CrawlerTaskDao;
import cn.edu.pku.EOSCN.DAO.DAOUtils;
import cn.edu.pku.EOSCN.business.InitBusiness;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.entity.CrawlerTask;
import cn.edu.pku.EOSCN.entity.Project;

public class Statics2 {
    private static long getTotalSizeOfFilesInDir(final File file) {
        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (final File child : children) 
            	if(!child.getName().contains("log.txt") && 
            			!child.getName().contains("index") && 
            			!child.getName().endsWith("GIT"))
                total += getTotalSizeOfFilesInDir(child);
        return total;
    }
    private static long getTotalNumOfFilesInDir(final File file) {
        if (file.isFile())
            return 1;
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (final File child : children) 
            	if(!child.getName().contains("log.txt") && 
            			!child.getName().contains("index") && 
            			!child.getName().endsWith("GIT"))
                total += getTotalNumOfFilesInDir(child);
        return total;
    }
    
    public static void work(CrawlerTask ct){
		int fileNum = 0;
		int fileSize = 0;
		try {
			if (CrawlerTaskDao.hasKey(ct)) return;
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String [] s = ct.getDownload().split(";");
		if (ct.getResourceType().equals("JiraIssue")){
			for (String fs : s){
				if (fs.length() < 2) continue;
				File file = new File(fs);
				if (file == null) return;
				for (File f : file.listFiles()){
					fileNum += f.listFiles().length - 1;
					fileSize += getTotalSizeOfFilesInDir(f);
				}
			}
		}
		//content.append("\",");
		if (ct.getResourceType().equals("Mbox")){
			for (String fs : s){
				File file = new File(fs);
				if (file == null) return;
				for (File f : file.listFiles()){
					fileNum += getTotalNumOfFilesInDir(f);
					fileSize += getTotalSizeOfFilesInDir(f);
				}
			}
		}
		//content.append("\",");
		if (ct.getResourceType().equals("Git")){
			for (String fs : s){
				File file = new File(fs);
				if (file == null) return;
				for (File f : file.listFiles()){
					fileNum += getTotalNumOfFilesInDir(f);
					fileSize += getTotalSizeOfFilesInDir(f);
				}
			}
		}
		if (ct.getResourceType().equals("MainSite")){
			return;
//			for (String fs : s){
//				File file = new File(fs);
//				for (File f : file.listFiles()){
//					fileNum += getTotalNumOfFilesInDir(f);
//					fileSize += getTotalSizeOfFilesInDir(f);
//				}
//			}
		}
		//content.append("\"\n");
		try {
			DAOUtils.update("INSERT INTO "+ct.getResourceType()+" (uuid, fileNum, fileSize) VALUES (?,?,?)",
			        ct.getUuid(), fileNum, fileSize);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(fileNum + "   " + fileSize);
    }
    
	public static void  main(String args[]){
		InitBusiness.initEOS();
		try {
			for (CrawlerTask ct : CrawlerTaskDao.getAllCrawlerTask()){
				System.out.println(ct.getEntrys() + "  " + ct.getResourceType() +  "   "+ct.getProjectUuid());
				work(ct);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

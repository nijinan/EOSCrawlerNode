package cn.edu.pku.EOSCN.crawler.util;

import java.io.File;
import java.sql.SQLException;

import org.json.JSONObject;

import cn.edu.pku.EOSCN.DAO.CrawlerTaskDao;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.entity.CrawlerTask;
import cn.edu.pku.EOSCN.entity.Project;

public class Statics {
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
                total += getTotalSizeOfFilesInDir(child);
        return total;
    }
    
    public static void work(JSONObject obj,Project project){
		String key = obj.getString("name");
		key = key.replaceAll("(Incubating)", "");
		key = key.trim().toLowerCase();
		System.out.println(key);
		
		int fileNum = 0;
		int fileSize = 0;
		//content.append(key + ",");
		if (obj.optString("bug-database").contains("jira")){
			String content = "";
			CrawlerTask ct = new CrawlerTask(project,"JiraIssue");
			ct.setEntrys(obj.optString("bug-database"));
			String Path = "D://CrawlData//Apache//cn.edu.pku.EOSCN.crawler.JiraIssueCrawler";
			File file = new File(Path);
			//content.append("Issue : \n");
			//content.append("\"");
			for (File f : file.listFiles()){
				if (f.getName().toLowerCase().contains(key) && f.isDirectory()){
					content += (f.getAbsolutePath() + ";");
					//fileNum += f.listFiles().length - 1;
					//fileNum += getTotalSizeOfFilesInDir(f);
				}
			}
			ct.setDownload(content);
			try {
				CrawlerTaskDao.insertCrawlerTask(ct);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//content.append("\",");
		if (obj.has("mailing-list")){
			String content = "";
			CrawlerTask ct = new CrawlerTask(project,"Mbox");
			String Path = "D://CrawlData//Apache//cn.edu.pku.EOSCN.crawler.MboxCrawler";
			File file = new File(Path);
			//content.append("Email : \n");
			//content.append("\"");
			for (File f : file.listFiles()){
				if (key.toLowerCase().contains(f.getName().toLowerCase())&& f.isDirectory()){
					content += (f.getAbsolutePath() + ";");
				}
			}			
			if (content.length() > 2) {
				ct.setDownload(content);
				ct.setEntrys(obj.optString("mailing-list"));
				try {
					CrawlerTaskDao.insertCrawlerTask(ct);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//content.append("\",");
		if (true){
			CrawlerTask ct = new CrawlerTask(project,"Git");
			String Path = "D://CrawlData//Apache//cn.edu.pku.EOSCN.crawler.GitCrawler";
			File file = new File(Path);
			String content = "";
			//content.append("GitCommit : \n");
			//content.append("\"");
			for (File f : file.listFiles()){
				if (f.getName().toLowerCase().contains(key)&& f.isDirectory()){
					content += (f.getAbsolutePath() + ";");
				}
			}
			if (content.length() > 2) {
				ct.setDownload(content);
				ct.setEntrys(obj.optString("repository"));
				try {
					CrawlerTaskDao.insertCrawlerTask(ct);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (true){
			CrawlerTask ct = new CrawlerTask(project,"MainSite");
			String Path = "D://CrawlData//Apache//cn.edu.pku.EOSCN.crawler.MainSiteCrawler";
			File file = new File(Path);
			String content = "";
			//content.append("GitCommit : \n");
			//content.append("\"");
			for (File f : file.listFiles()){
				if (f.getName().contains(key.replaceAll("apache", "").trim())&& f.isDirectory()){
					content += (f.getAbsolutePath() + ";");
				}
			}
			if (content.length() > 2) {
				ct.setDownload(content);
				ct.setEntrys(obj.optString("homepage"));
				try {
					CrawlerTaskDao.insertCrawlerTask(ct);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//content.append("\"\n");

    }
    
	public static void  main(String args[]){
		String html = FileUtil.read("D:\\CrawlData\\Apache\\ProjectsList.json");
		JSONObject jsobj = new JSONObject(html);
		StringBuffer content = new StringBuffer("");
		for (String name : jsobj.keySet()){
			JSONObject obj = jsobj.getJSONObject(name);
			//work(obj);
		}
		FileUtil.write("D://CrawlData//Apache//Jira.txt", content.toString());;
	}
}

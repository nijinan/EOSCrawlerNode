package cn.edu.pku.EOSCN.crawler.util;

import java.io.File;

import org.json.JSONObject;

import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;

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
	public static void  main(String args[]){
		String html = FileUtil.read("D:\\CrawlData\\Apache\\ProjectsList.json");
		JSONObject jsobj = new JSONObject(html);
		StringBuffer content = new StringBuffer("");
		for (String name : jsobj.keySet()){
			JSONObject obj = jsobj.getJSONObject(name);
			String key = obj.getString("name");
			key = key.replaceAll("(Incubating)", "");
			key = key.trim();
			System.out.println(key);
			content.append("Project : " + key + "\n");
			String Path = "D://CrawlData//Apache//cn.edu.pku.EOSCN.crawler.JiraIssueCrawler";
			File file = new File(Path);
			content.append("Issue : \n");
			for (File f : file.listFiles()){
				if (f.getName().contains(key) && f.isDirectory()){
					content.append(f.getAbsolutePath() + " " + (f.listFiles().length - 1) + " " + getTotalSizeOfFilesInDir(f) + "\n");
				}
			}
			Path = "D://CrawlData//Apache//cn.edu.pku.EOSCN.crawler.MboxCrawler";
			file = new File(Path);
			content.append("Email : \n");
			for (File f : file.listFiles()){
				if (key.toLowerCase().contains(f.getName().toLowerCase())&& f.isDirectory()){
					content.append(f.getAbsolutePath() + " " + (f.listFiles().length - 1) + " " + getTotalSizeOfFilesInDir(f) + "\n");
				}
			}			
			Path = "D://CrawlData//Apache//cn.edu.pku.EOSCN.crawler.GitCrawler";
			file = new File(Path);
			content.append("GitCommit : \n");
			for (File f : file.listFiles()){
				if (f.getName().contains(key)&& f.isDirectory()){
					content.append(f.getAbsolutePath() + " " + getTotalNumOfFilesInDir(f) + " " + getTotalSizeOfFilesInDir(f) + "\n");
				}
			}
			
		}
		FileUtil.write("D://CrawlData//Apache//Jira.txt", content.toString());;
	}
}

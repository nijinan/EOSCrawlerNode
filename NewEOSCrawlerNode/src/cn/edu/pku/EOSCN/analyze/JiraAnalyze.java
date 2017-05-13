package cn.edu.pku.EOSCN.analyze;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import cn.edu.pku.EOSCN.business.ThreadManager;
import cn.edu.pku.EOSCN.crawler.Crawler;
import cn.edu.pku.EOSCN.crawler.JiraIssueCrawler;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.entity.Project;

public class JiraAnalyze {
	public static void analyzeApache(){
		File file = new File("D:\\CrawlData\\Apache\\cn.edu.pku.EOSCN.crawler.JiraIssueCrawler");
		for (File dir : file.listFiles()){
			if (dir.isDirectory()){
				for (File issueDir : dir.listFiles()){
					if (issueDir.isDirectory()&& issueDir.getName().matches("[0-9]*")){
						String html = FileUtil.read(issueDir.getAbsolutePath() + File.separatorChar + issueDir.getName()+".json");
						if (html.length() < 3) continue;
						JSONObject jsobj = new JSONObject(html);
						JSONObject obj = jsobj.getJSONObject("fields");
						String date = obj.getString("created").substring(0, 7);
						//System.out.println(date);
						File newFile = new File(dir.getAbsolutePath()+File.separatorChar+date);
						if (!newFile.exists()){
							newFile.mkdir();
						}
						try {
							FileUtils.moveDirectoryToDirectory(issueDir, newFile, false);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
					}
				}
			}
		}
	}
	public static void moveto(File from,File to){
		
	}
	public static void main(String args[]){
		analyzeApache();

		
		
	}
}

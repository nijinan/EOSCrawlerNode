package cn.edu.pku.EOSCN.analyze;

import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.Path;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BugAnalyze {
	public static void analyzeApache(){
		File file = new File("E:\\CrawlData\\Apache\\cn.edu.pku.EOSCN.crawler.JiraIssueCrawler");
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
	public static void analyzeUser(){
		File file = new File("E:\\CrawlData\\Apache\\cn.edu.pku.EOSCN.crawler.JiraIssueCrawler");
		if (!file.exists()) return;
		Arrays.stream(file.listFiles()).filter(project->project.isDirectory()).forEach(project->{
			Set<String> assigneeSet = new HashSet<>();
			Set<String> reporterSet = new HashSet<>();
			//System.out.println(project.getName());
			Arrays.stream(project.listFiles()).filter(dateDir->dateDir.isDirectory() && dateDir.getName().matches("[0-9]{4}-[0-9]{2}")).forEach(dateDir->{
				Set<String> assigneeDateSet = new HashSet<>();
				Set<String> reporterDateSet = new HashSet<>();
				//System.out.println(dateDir.getName());
				Arrays.stream(dateDir.listFiles()).filter(issueDir->issueDir.isDirectory() && issueDir.getName().matches("[0-9]*")).forEach(issueDir->{
					String html = FileUtil.read(issueDir.getAbsolutePath() + Path.SEPARATOR + issueDir.getName()+".json");
					if (html.length() >= 3){
						JSONObject jsobj = new JSONObject(html);
						if (jsobj.getJSONObject("fields").optJSONObject("assignee") != null) {
							String assigneeName = jsobj.getJSONObject("fields").getJSONObject("assignee").getString("emailAddress");
							//System.out.println(assigneeName);
							assigneeDateSet.add(assigneeName);
						}
						if (jsobj.getJSONObject("fields").optJSONObject("reporter") != null) {
							String reporterName = jsobj.getJSONObject("fields").getJSONObject("reporter").getString("emailAddress");
							//System.out.println(reporterName);
							reporterDateSet.add(reporterName);
						}
					}

				});
				setToFile(assigneeDateSet,dateDir.getAbsolutePath() + Path.SEPARATOR + "assignee.txt");
				setToFile(reporterDateSet,dateDir.getAbsolutePath() + Path.SEPARATOR + "reporter.txt");
				assigneeSet.addAll(assigneeDateSet);
				reporterSet.addAll(reporterDateSet);
			});
			setToFile(assigneeSet,project.getAbsolutePath() + Path.SEPARATOR + "assignee.txt");
			setToFile(reporterSet,project.getAbsolutePath() + Path.SEPARATOR + "reporter.txt");
		});
	}

	public static void setToFile(Set<String> set, String filename){
		File file = new File(filename);
		if (!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileUtils.write(file,""+set.size()+"\n");
			FileUtils.writeLines(file,set,true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Map<String,Integer> countUser(String projectName, String query){
		File project = new File("E:\\CrawlData\\Apache\\cn.edu.pku.EOSCN.crawler.JiraIssueCrawler\\"+projectName);
		Map<String, Integer> cnt = new HashMap<>();
		//System.out.println(project.getName());
		Arrays.stream(project.listFiles()).filter(dateDir->dateDir.isDirectory() && dateDir.getName().matches("[0-9]{4}-[0-9]{2}")).forEach(dateDir->{
			Set<String> assigneeDateSet = new HashSet<>();
			Arrays.stream(dateDir.listFiles()).filter(f->f.getName().startsWith(query)).forEach(f->{
				try {
					cnt.put(dateDir.getName(), Integer.valueOf(FileUtils.readLines(dateDir).get(0)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

		});
		return cnt;
	}



	public static void main(String args[]){
		//analyzeApache();
		//analyzeUser();
	}
}

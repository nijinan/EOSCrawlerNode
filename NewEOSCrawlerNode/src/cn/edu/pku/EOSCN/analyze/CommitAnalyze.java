package cn.edu.pku.EOSCN.analyze;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.Path;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommitAnalyze {

	public static void analyzeApache(){
		File file = new File("E:\\CrawlData\\Apache\\cn.edu.pku.EOSCN.crawler.GitCrawler");
		Arrays.stream(file.listFiles()).filter(project->project.isDirectory()).forEach(project->{
			Arrays.stream(project.listFiles()).forEach(mlist->{
				System.out.println(project.getName());

				if (mlist.isDirectory()) {
					JSONArray jsarr = new JSONArray();
					Arrays.stream(mlist.listFiles()).
							filter(mboxfile -> mboxfile.getName().startsWith("commit")).
							forEach(mboxfile -> {
								GitCommit commit = new GitCommit(mboxfile);
								JSONObject jsobj = new JSONObject();
								//jsobj.put("date", commit.createDate);
								File newFile = new File(mlist.getAbsolutePath()+File.separatorChar+commit.createDate);
								if (!newFile.exists()){
									newFile.mkdir();
//									File sta = new File(newFile.getAbsolutePath() + Path.SEPARATOR + "statics.json");
//									try {
//										sta.createNewFile();
//									} catch (IOException e) {
//										e.printStackTrace();
//									}
								}
								try {
									FileUtils.moveFileToDirectory(mboxfile,newFile,false);
								} catch (IOException e) {
									e.printStackTrace();
								}
								//FileUtils.moveDirectoryToDirectory(issueDir, newFile, false);
//								jsobj.put("emailsCnt", parser.emailsCnt);
//								jsobj.put("emailsUses", parser.emails.size());
//								jsarr.put(jsobj);
							});
//					try {
//						FileUtils.write(sta, jsarr.toString());
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
				}
			});
		});
	}

	public static void CountUser(){
		File file = new File("E:\\CrawlData\\Apache\\cn.edu.pku.EOSCN.crawler.GitCrawler");

		Arrays.stream(file.listFiles()).filter(project->project.isDirectory()).forEach(project->{
			Arrays.stream(project.listFiles()).forEach(mlist->{
				Set<String> CommitterSet = new HashSet<>();
				Set<String> CommitSet = new HashSet<>();
				System.out.println(mlist.getName());
				JSONArray jsarr = new JSONArray();
				if (mlist.isDirectory()) {
					File sta = new File(mlist.getAbsolutePath() + Path.SEPARATOR + "statics.json");
					if (!sta.exists()) try {
						sta.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
					Arrays.stream(mlist.listFiles()).filter(dateDir->dateDir.isDirectory() && dateDir.getName().matches("[0-9]{4}-[0-9]{2}")).forEach(dateDir->{
						Set<String> CommitterDateSet = new HashSet<>();
						Set<String> CommitDateSet = new HashSet<>();


						Arrays.stream(dateDir.listFiles()).
								filter(mboxfile -> mboxfile.getName().startsWith("commit")).
								forEach(mboxfile -> {
									GitCommit commit = new GitCommit(mboxfile);
									CommitterDateSet.add(commit.authorName);
									CommitDateSet.add(commit.UUID);
								});
						setToFile(CommitterDateSet,dateDir.getAbsolutePath() + Path.SEPARATOR + "reporter.txt");
						setToFile(CommitDateSet,dateDir.getAbsolutePath() + Path.SEPARATOR + "commit.txt");
						CommitterSet.addAll(CommitterDateSet);
						CommitSet.addAll(CommitDateSet);
						JSONObject jsobj = new JSONObject();
						jsobj.put("date", dateDir.getName());
						jsobj.put("commitsCnt", CommitDateSet.size());
						jsobj.put("coomitsUsers", CommitterDateSet.size());
						jsarr.put(jsobj);
					});
					setToFile(CommitterSet,mlist.getAbsolutePath() + Path.SEPARATOR + "reporter.txt");
					setToFile(CommitSet,mlist.getAbsolutePath() + Path.SEPARATOR + "commit.txt");
					try {
						FileUtils.write(sta, jsarr.toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
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




	public static void main(String args[]){
		CountUser();
		//analyzeUser();
		String x = "Mon Mar 02 13:57:49 CST 2015";



	}


	public static class GitCommit{
		public String UUID = "";
		public String version = "";
		public String createDate = "";
		public String logMessage = "";
		public String parentUUID = "";
		public String commitSvnUrl = "";
		public String authorName = "";
		static SimpleDateFormat sdf1 = new SimpleDateFormat ("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
		public static Map m = new HashMap<String,String>();
		public GitCommit(File commitFile){
			try {
				BufferedReader reader = new BufferedReader(new FileReader(commitFile));
				reader.readLine(); // filter the first line of the file;

				UUID = reader.readLine().split(" ")[1]; // get the commit uuid

				//get the commit author
				authorName = reader.readLine();

				//get the commit create time
				createDate = reader.readLine();


				Date date= null;
				try {
					date = sdf1.parse(createDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				String sDate=sdf.format(date);
				createDate = sDate.substring(0,7);
				//get the commit log message
				logMessage = reader.readLine();

				String temp;
				//get commit svn url
				do{
					temp = reader.readLine();
					if(temp.indexOf("git-svn-id:") == 0){
						temp = temp.replace("git-svn-id:" , "").trim();
						commitSvnUrl = temp.split(" ")[0];
						version = commitSvnUrl.substring(commitSvnUrl.lastIndexOf("@") + 1);
					}
				}while(temp.indexOf("----------------------------") != 0);

				parentUUID = reader.readLine();
				if(parentUUID.indexOf("Parents : ") == 0) {
					parentUUID = parentUUID.replace("Parents : ", "").trim();
				}
				reader.close();
			}catch(Exception e){
				System.out.print("parsing commit file meta info filed , commit file:" + commitFile.getAbsolutePath());
				System.out.print(e.getMessage());
			}
		}
	}
}

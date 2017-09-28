package cn.edu.pku.EOSCN.analyze;

import cn.edu.pku.EOSCN.analyze.util.MboxParser;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.Path;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

public class MailAnalyze {
	public static void analyzeApache(){
		File file = new File("E:\\CrawlData\\Apache\\cn.edu.pku.EOSCN.crawler.MboxCrawler");

		Arrays.stream(file.listFiles()).filter(project->project.isDirectory()).forEach(project->{
			Arrays.stream(project.listFiles()).forEach(mlist->{
			    System.out.println(project.getName());
			    File sta = new File(mlist.getAbsolutePath() + Path.SEPARATOR + "statics.json");
			    if (!sta.exists() && mlist.isDirectory()) {
                    JSONArray jsarr = new JSONArray();
                    Arrays.stream(mlist.listFiles()).
                            filter(mboxfile -> mboxfile.getName().startsWith("http")).
                            forEach(mboxfile -> {
                        String date = mboxfile.getName().substring(mboxfile.getName().length() - 11, mboxfile.getName().length() - 5);
                        MboxParser parser = new MboxParser();
                        parser.parse(mboxfile);
                        JSONObject jsobj = new JSONObject();
                        jsobj.put("date", date);
                        jsobj.put("emailsCnt", parser.emailsCnt);
                        jsobj.put("emailsUses", parser.emails.size());
                        jsarr.put(jsobj);
                    });
                    try {
                        FileUtils.write(sta, jsarr.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
		});
	}

	public static void setToFile(Set<String> set, String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileUtils.write(file, "" + set.size() + "\n");
			FileUtils.writeLines(file, set, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	public static void main(String args[]){
		analyzeApache();
		//analyzeUser();
	}
}

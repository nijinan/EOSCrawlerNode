package cn.edu.pku.EOSCN.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.Path;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.URLReader;
import cn.edu.pku.EOSCN.entity.Project;

public class GitTagsCrawler extends GitCrawler {

	private List<String> tagsJsonPaths;
	
	public GitTagsCrawler() {
		// TODO Auto-generated constructor stub
		super();
	}

	public void crawl_url() throws Exception{
		String tagsUrl = 
				String.format("%s/%s",this.getApiBaseUrl(),"tags");
		int page = 0;
		tagsJsonPaths = new LinkedList<String>();
		while (true){
			page ++;
			String storagePath = 
					String.format("%s%c%s%d%s", 
							this.getStorageBasePath(),Path.SEPARATOR,
							"tags",page,".json");
			if (Crawler.needLog){
				if (FileUtil.logged(storagePath))
					this.tagsJsonPaths.add(storagePath);
					continue;
			}
			String url = String.format("%s?page=%d&%s", tagsUrl,page,GitCrawler.gitToken);
			String content = URLReader.getHtmlStringFromUrl(url);
			if (content.length() < 20) break;
			this.tagsJsonPaths.add(storagePath);
			File file = FileUtil.createFile(storagePath);
			FileWriter fw = new FileWriter(file);
			fw.write(content);
			fw.close();
		}
	}
	@Override
	public void crawl_data() throws Exception {
		// TODO Auto-generated method stub
		for (String tagsJsonPath : this.tagsJsonPaths){
			File file = new File(tagsJsonPath);
			StringBuilder sb = new StringBuilder("");
			BufferedReader br =new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null)
				sb.append(line+"\r\n");
			br.close();
			JSONArray ja = new JSONArray(sb.toString());
			for (int i = 0; i < ja.length(); i++){
				JSONObject jo = (JSONObject)ja.get(i);
				String name = (String) jo.get("name");
				String downloadUrl = (String) jo.get("zipball_url") + "?" + GitCrawler.gitToken; 
				String storagePath = 
						String.format("%s%c%s%c%s.zip", 
								this.getStorageBasePath(),Path.SEPARATOR,
								"tagsRelease",Path.SEPARATOR,
								name);
				FileUtil.createFile(storagePath);
				URLReader.downloadFromUrl(downloadUrl, storagePath);
			}
		}
	}
	public static void main(String args[]){
		Crawler crawl = new GitTagsCrawler();
		Project project = new Project();
		project.setOrgName("google");
		project.setProjectName("gson");
		project.setName(String.format("%s-%s",project.getOrgName(), project.getProjectName()));
		crawl.setProject(project);
		try {
			crawl.Crawl();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

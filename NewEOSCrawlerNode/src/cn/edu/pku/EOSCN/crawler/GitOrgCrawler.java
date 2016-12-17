package cn.edu.pku.EOSCN.crawler;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.Path;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.edu.pku.EOSCN.business.ThreadManager;
import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.GitApiDownloader;
import cn.edu.pku.EOSCN.entity.Project;

/** 
  * @author Jinan Ni E-mail: nijinan@pku.edu.cn
  * @date 2016年12月17日 下午4:29:46 
  * @version 1.0   */
public class GitOrgCrawler extends Crawler {
	private List<String> projectsJsonPaths = new LinkedList<String>();
	protected static final String gitApiBaseUrl = 
			"https://api.github.com/orgs/%s/repos";
	private String storageBasePath;	
	private String apiBaseUrl;
	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		storageBasePath = String.format("%s%c%s%c%s", 
				Config.getTempDir(),
				Path.SEPARATOR,
				this.getProject().getOrgName(),
				Path.SEPARATOR,
				this.getClass().getName());
	
		apiBaseUrl = String.format(gitApiBaseUrl, 
				this.getProject().getOrgName());
	}
	
	@Override
	public void crawl_url() throws Exception{
		int page = 1;
		while (true){
			String storagePath = 
					String.format("%s%c%s%d%s", 
							this.storageBasePath,Path.SEPARATOR,
							"project_index",page,".json");
			if (this.needLog){
				if (FileUtil.logged(storagePath)){
					this.projectsJsonPaths.add(storagePath);
					page++;
					continue;
				}
			}
			String url = String.format("%s?page=%d&", apiBaseUrl,page);
			//String url = String.format("%s?page=%d", commitsUrl,page);
			String content = GitApiDownloader.downloadOrin(url,null);
			if (content.length() == 0) continue;
			if (content.length() < 10) break; 
			this.projectsJsonPaths.add(storagePath);
			if (this.needLog){
				FileUtil.logging(storagePath);
			}
			FileUtil.write(storagePath, content);
			page ++;
		}
		for (String projectsJsonPath : this.projectsJsonPaths){
			String s = FileUtil.read(projectsJsonPath);
			JSONArray ja = new JSONArray(s);
			for (int i = 0; i < ja.length(); i++){
				JSONObject jo = (JSONObject)ja.get(i);
				String sha = jo.getString("name");
				Crawler crawl = new GitCommitsCrawler();
				Project project = new Project();
				project.setOrgName(this.getProject().getOrgName());
				project.setProjectName(sha);
				project.setName(String.format("%s%c%s",project.getOrgName(), Path.SEPARATOR,project.getProjectName()));
				crawl.setProject(project);
				crawl.needLog = true;
				crawl.crawlerType = Crawler.MAIN;
				ThreadManager.addCrawlerTask(crawl);
			}
		}
	}

	@Override
	public void crawl_middle(int id, Crawler crawler) {
		// TODO Auto-generated method stub
	}

	@Override
	public void crawl_data() {
		// TODO Auto-generated method stub

	}
	public static void main(String args[]) throws InterruptedException {
		Crawler crawl = new GitOrgCrawler();
		Project project = new Project();
		ThreadManager.initCrawlerTaskManager();
		project.setOrgName("Eclipse");
		project.setProjectName("eclipse");
		project.setName(String.format("%s%c%s",project.getOrgName(),Path.SEPARATOR, project.getProjectName()));
		crawl.setProject(project);
		crawl.needLog = true;
		crawl.crawlerType = Crawler.FULL;
		ThreadManager.addCrawlerTask(crawl);
		crawl.join();
		ThreadManager.finishCrawlerTaskManager();
		System.out.println("ok1");
	}

}

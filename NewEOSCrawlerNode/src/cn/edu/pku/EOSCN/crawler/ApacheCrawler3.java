package cn.edu.pku.EOSCN.crawler;

import java.util.List;

import org.json.JSONObject;

import cn.edu.pku.EOSCN.business.ThreadManager;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.HtmlDownloader;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.URLExtractor;
import cn.edu.pku.EOSCN.entity.CrawlerURL;
import cn.edu.pku.EOSCN.entity.Project;

public class ApacheCrawler3 extends Crawler {
	private String projectListUrl = "";
	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		projectListUrl = this.getEntrys();
	}

	@Override
	public void crawl_url() throws Exception {
		// TODO Auto-generated method stub
		String html = FileUtil.read("D:\\CrawlData\\Apache\\ProjectsList.json");
		JSONObject jsobj = new JSONObject(html);
		//List<CrawlerURL> urls = URLExtractor.getAllUrls(html, projectListUrl, "");
		boolean flag = false;
		for (String name : jsobj.keySet()){
			JSONObject obj = jsobj.getJSONObject(name);
			if (!obj.has("homepage")) continue;
			MainSiteCrawler crawl = new MainSiteCrawler();
			
			Project project = new Project();
			project.setOrgName("Apache");
			project.setProjectName(obj.getString("name"));
			project.setName(obj.getString("name"));
			project.setHostUrl(obj.getString("homepage"));
			if (obj.getString("homepage").toLowerCase().contains("openwebbeans.apache.org")) {
				flag = true;
				//continue;
			}
			
			if (!flag) continue;
			//CrawlerTaskManager.createCrawlerTask(project, "Bugzilla");
			crawl.setProject(project);
			crawl.needLog = true;
			crawl.subCrawlerRun = 6;
			crawl.subCrawlerNum = 6;
			
			crawl.crawlerType = Crawler.MAIN;
			
			
			crawl.setEntrys(obj.getString("homepage"));
			crawl.hostwating = true;
			ThreadManager.addCrawlerTask(crawl);
			sleep(10000);
			crawl.join();
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
	public static void main(String args[]){
		ApacheCrawler3 crawl = new ApacheCrawler3();
		Project project = new Project();
		ThreadManager.initCrawlerTaskManager();
		//InitBusiness.initEOS();
		//JDBCPool.initPool();
		project.setOrgName("Apache");
		project.setProjectName("");
		project.setName("Apache");
		//CrawlerTaskManager.createCrawlerTask(project, "Bugzilla");
		crawl.setProject(project);
		crawl.needLog = true;
		crawl.crawlerType = Crawler.FULL;
		
		
		crawl.setEntrys("http://mail-archives.apache.org/mod_mbox/");
		ThreadManager.addCrawlerTask(crawl);
	}
}

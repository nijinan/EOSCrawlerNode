package cn.edu.pku.EOSCN.crawler;

import java.util.List;

import cn.edu.pku.EOSCN.business.ThreadManager;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.HtmlDownloader;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.URLExtractor;
import cn.edu.pku.EOSCN.entity.CrawlerURL;
import cn.edu.pku.EOSCN.entity.Project;

public class ApacheCrawler extends Crawler {
	private String projectListUrl = "";
	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		projectListUrl = this.getEntrys();
	}

	@Override
	public void crawl_url() throws Exception {
		// TODO Auto-generated method stub
		String html = HtmlDownloader.downloadOrin(projectListUrl,null,null);
		List<CrawlerURL> urls = URLExtractor.getAllUrls(html, projectListUrl, "");
		for (CrawlerURL url : urls){
			String s = url.getUrl();
			s = s.substring(projectListUrl.length());
			if (s.contains("incubator")) continue;
			if (!s.contains("general")) continue;
			MboxCrawler crawl = new MboxCrawler();
			Project project = new Project();
			project.setOrgName("Apache");
			project.setProjectName(s.substring(0, s.indexOf("-")));
			project.setName(s.substring(s.indexOf("-")+1,s.length() - 1));
			//CrawlerTaskManager.createCrawlerTask(project, "Bugzilla");
			crawl.setProject(project);
			crawl.needLog = true;
			crawl.subCrawlerRun = 6;
			crawl.subCrawlerNum = 6;
			crawl.crawlerType = Crawler.MAIN;
			
			
			crawl.setEntrys(url.getUrl());
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
		ApacheCrawler crawl = new ApacheCrawler();
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

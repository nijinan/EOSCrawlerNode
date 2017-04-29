package cn.edu.pku.EOSCN.crawler;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.edu.pku.EOSCN.business.ThreadManager;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.HtmlDownloader;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.URLExtractor;
import cn.edu.pku.EOSCN.entity.CrawlerURL;
import cn.edu.pku.EOSCN.entity.Project;

public class ApacheCrawler5 extends Crawler {
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
		html = HtmlDownloader.downloadOrin("https://git-wip-us.apache.org/repos/asf", null, null);
		Document doc = Jsoup.parse(html);
		doc.getElementsByClass("dark");
		for (Element ele : doc.getElementsByClass("dark")){
			String s1 = (ele.getElementsByClass("list").get(0).html());
			//String s2 = (ele.getElementsByClass("list").get(1).html());
			String s2 = ele.getElementsByClass("list").get(1).attr("title");
			boolean flag = false;
			if (s2.contains("")) flag = true;
			//if (!flag) continue;
			go(s1,s2);
		}
		for (Element ele : doc.getElementsByClass("light")){
			String s1 = (ele.getElementsByClass("list").get(0).html());
			//String s2 = (ele.getElementsByClass("list").get(1).html());
			String s2 = ele.getElementsByClass("list").get(1).attr("title");
			boolean flag = false;
			if (s2.contains("")) flag = true;
			//if (!flag) continue;
			go(s1,s2);
		}
		//List<CrawlerURL> urls = URLExtractor.getAllUrls(html, projectListUrl, "");

	}
	public void go(String s2, String s1){
		Project project = new Project();
		project.setOrgName("Apache");
		project.setProjectName(s1);
		project.setName(s1); 
		GitCrawler crawl = new GitCrawler();
		String ss = "https://git-wip-us.apache.org/repos/asf/" + s2;
		String gitname = s2.substring(0, s2.length()-4);
		//if (!gitname.toLowerCase().contains("lucene")) return;
		project.setName(gitname);
		crawl.setProject(project);
		crawl.needLog = true;
		crawl.subCrawlerRun = 1;
		crawl.subCrawlerNum = 1;
		crawl.crawlerType = Crawler.MAIN;
		crawl.setEntrys(ss);
		crawl.hostwating = true;
		ThreadManager.addCrawlerTask(crawl);
		try {
			sleep(1000);
			crawl.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		ApacheCrawler5 crawl = new ApacheCrawler5();
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

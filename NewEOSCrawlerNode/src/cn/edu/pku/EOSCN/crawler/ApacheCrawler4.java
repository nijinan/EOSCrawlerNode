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

public class ApacheCrawler4 extends Crawler {
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
			System.out.println(ele.getElementsByClass("list").get(0).html());
			System.out.println(ele.getElementsByClass("list").get(1).html());
		}
		//List<CrawlerURL> urls = URLExtractor.getAllUrls(html, projectListUrl, "");
		boolean flag = false;
		for (String name : jsobj.keySet()){
			JSONObject obj = jsobj.getJSONObject(name);
			
			
			
			if (!obj.has("repository")) continue;
			JSONArray jarr = obj.getJSONArray("repository");
			
			for (Object s : jarr){
				String ss = s.toString();
				if (ss.contains("git-wip-us.apache.org/repos/asf")){
					Project project = new Project();
					project.setOrgName("Apache");
					project.setProjectName(obj.getString("name"));
					project.setName(obj.getString("name"));
					if (project.getName().contains("Stratos")) flag = true;
					if (project.getName().contains("incubator")) continue;
					if (!flag) continue; 
					GitCrawler crawl = new GitCrawler();
					ss = ss.substring(ss.indexOf("git-wip-us.apache.org/repos/asf"));
					ss = "https://" + ss;
					String gitname = ss.substring(ss.lastIndexOf("/")+1);
					gitname = gitname.substring(0,gitname.length()-4);
					if (gitname.contains("?")) continue;
					project.setName(gitname);
					crawl.setProject(project);
					crawl.needLog = true;
					crawl.subCrawlerRun = 1;
					crawl.subCrawlerNum = 1;
					crawl.crawlerType = Crawler.MAIN;
					crawl.setEntrys(ss);
					crawl.hostwating = true;
					ThreadManager.addCrawlerTask(crawl);
					sleep(100);
					crawl.join();
				}
				
			}
			//CrawlerTaskManager.createCrawlerTask(project, "Bugzilla");

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
		ApacheCrawler4 crawl = new ApacheCrawler4();
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

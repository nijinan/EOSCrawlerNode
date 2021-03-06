package cn.edu.pku.EOSCN.crawler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.eclipse.core.runtime.Path;

import cn.edu.pku.EOSCN.business.InitBusiness;
import cn.edu.pku.EOSCN.business.ThreadManager;
import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.HtmlDownloader;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.URLExtractor;
import cn.edu.pku.EOSCN.detect.Detector;
import cn.edu.pku.EOSCN.detect.Smeller;
import cn.edu.pku.EOSCN.entity.CrawlerURL;
import cn.edu.pku.EOSCN.entity.Project;

public class MainSiteCrawler extends Crawler {
	private static int maxdepth = 100000;
	private String storageBasePath;
	private String webUrl;
	private Queue<CrawlerURL> urlQueue = new LinkedList<CrawlerURL>();
	private HashSet<String> visitURLSet=new HashSet<String>();
	public int runningNum = 0;
	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		storageBasePath = String.format("%s%c%s%c%s", 
				Config.getTempDir(),
				Path.SEPARATOR,
				this.getProject().getOrgName(),
				Path.SEPARATOR,
				this.getClass().getName());
	
		webUrl = this.getEntrys();
		/*TODO get the webUrl*/
	}

	public HashSet<String> getVisitURLSet() {
		return visitURLSet;
	}

	
	@Override
	public void crawl_url() throws Exception {
		/* get the webUrl from google or do nothing */
		CrawlerURL crawlerUrl = new CrawlerURL();
		crawlerUrl.setUrl(webUrl);
		crawlerUrl.setDepth(0);
		crawlerUrl.setDocName("root");
		urlQueue.add(crawlerUrl);
	}
	public void crawl_middle(int id, Crawler crawler){

	}
	@Override
	public void crawl_data(){
		// TODO Auto-generated method stub
		int tt = 0;
		Queue<CrawlerURL> q = ((MainSiteCrawler)father).getUrlQueue();
		boolean first = true;
		while (true){
			CrawlerURL url;
			synchronized (q){
				if (first) ((MainSiteCrawler)father).runningNum++;
				first = false;
				url = this.fetchUrl();
				if (url == null) {
					break;
				}
			}
			tt = (tt + 1)%10;
			if (tt == 0) System.out.println("Thread" + this.subid + "  " + url.getUrl());
			String storagePath = 
					String.format("%s%c%s", 
							this.getStorageBasePath(),Path.SEPARATOR ,
							HtmlDownloader.url2path(url.getUrl()));
			String html = "";

			if (this.needLog){
				if (FileUtil.logged(storagePath) && FileUtil.exist(storagePath)){
					html = FileUtil.read(storagePath);
				}else {
					int times = 1;
					
					html = HtmlDownloader.downloadOrin(url.getUrl(),null,null);
					while (html.length() <= 0){
						times --;
						if (times < 0) break;
						html = HtmlDownloader.downloadOrin(url.getUrl(),null,null);
					}
					FileUtil.write(storagePath,html);
					FileUtil.logging(storagePath);
				}
			}else{
				html = HtmlDownloader.downloadOrin(url.getUrl(),null,null);
				FileUtil.write(storagePath,html);
			}
			if (html == null) continue;
			if (html.length() <= 0) continue;
			if (html.length() > 1000000) continue;
			if (url.getDepth() >= this.maxdepth) continue;
			if (Smeller.smell(html,url.getUrl(), project)){
				System.out.println("smell ++ :" + url.getUrl());
				continue;
			}
			List<CrawlerURL> urls = URLExtractor.getAllUrls(html, url.getUrl(), "");
			//System.out.println(html);
			for (CrawlerURL u : urls){
				if (u.getUrl().contains("@")) continue;
				if (!this.hasUrl(u)){
					u.setDocName(u.getUrl().replaceAll("[<>\\/:*?]", ""));
					u.setDepth(url.getDepth() + 1);
					if (Smeller.smell("",u.getUrl(), project)){
						System.out.println("smell ++ :" + u.getUrl());
						Detector detector = Smeller.smellEntry("",u.getUrl(), project);
						if (detector != null){
							continue;
						}
					}
					if (u.getUrl().contains("?")){
						u.setUrl(u.getUrl().substring(0, u.getUrl().indexOf("?")));
					}
					if (HtmlDownloader.getHost2(u.getUrl()).startsWith("ci")) continue;
					if (HtmlDownloader.url2path(u.getUrl()).contains("				")){
						System.out.println("");
					}
					if (u.getUrl().contains("deltaspike.apache.org"))
					{
						System.out.println("");
					}
					
					if (HtmlDownloader.getHost2(u.getUrl()).startsWith(HtmlDownloader.getHost2(project.getHostUrl())))
						this.addUrl(u);
				}
			}
		}		
	}

	
	public CrawlerURL fetchUrl(){
		Queue<CrawlerURL> q = ((MainSiteCrawler)father).getUrlQueue();
		CrawlerURL ret;
		//synchronized (q){
			while (q.isEmpty()){
				((MainSiteCrawler)father).runningNum --;
				if (((MainSiteCrawler)father).runningNum == 0) {
					q.notifyAll();
					return null;
				};
				try {
					q.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					q.notifyAll();
					return null;
				}
				((MainSiteCrawler)father).runningNum ++;
			}
			ret = q.poll();
		//}
		return ret;
	}
	
	public boolean hasUrl(CrawlerURL url){
		HashSet<String> set = ((MainSiteCrawler)father).getVisitURLSet();
		return set.contains(url.getUrl());
	}
	
	public void addUrl(CrawlerURL url){
		Queue<CrawlerURL> q = ((MainSiteCrawler)father).getUrlQueue();
		HashSet<String> set = ((MainSiteCrawler)father).getVisitURLSet();
		synchronized (q){
			if (!set.contains(url.getUrl())){
				q.add(url);
				set.add(url.getUrl());
				q.notify();
			}
		}
	} 
	
	public String getStorageBasePath() {
		return storageBasePath;
	}

	public void setStorageBasePath(String storageBasePath) {
		this.storageBasePath = storageBasePath;
	}
	public static void main(String[] args) throws Exception{
		Crawler crawl = new MainSiteCrawler();
		Project project = new Project();
		//InitBusiness.initEOS();
		ThreadManager.initCrawlerTaskManager();
		project.setOrgName("apache");
		project.setProjectName("eclipse");
		project.setName("eclipse");
		crawl.setProject(project);
		crawl.needLog = true;
		crawl.hostwating = true;
		((MainSiteCrawler)crawl).webUrl = "https://dev.eclipse.org/mailman/listinfo/egit-build";
		crawl.crawlerType = Crawler.MAIN;
		
		
		ThreadManager.addCrawlerTask(crawl);
		crawl.join();
		ThreadManager.finishCrawlerTaskManager();
		System.out.println("ok1");
	}

	public Queue<CrawlerURL> getUrlQueue() {
		return urlQueue;
	}


}

package cn.edu.pku.EOSCN.crawler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Path;

import cn.edu.pku.EOSCN.business.ThreadManager;
import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.HtmlDownloader;
import cn.edu.pku.EOSCN.entity.Project;

public class MboxCrawler extends Crawler {
	private String storageBasePath;
	private String projectMboxBaseUrl;
	public List<String> urlList = new ArrayList<String>();
	private String last = "";
	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		storageBasePath = String.format("%s%c%s%c%s%c%s%c%s", 
				Config.getTempDir(),
				Path.SEPARATOR,
				this.getProject().getOrgName(),
				Path.SEPARATOR,
				this.getClass().getName(),
				Path.SEPARATOR,
				this.getProject().getProjectName(),
				Path.SEPARATOR,
				this.getProject().getName());		
		this.projectMboxBaseUrl = this.getEntrys();
	}

	@Override
	public void crawl_url() throws Exception {
		// TODO Auto-generated method stub
		String htmlText = HtmlDownloader.downloadOrin(this.projectMboxBaseUrl,null,null);
		String patternString = "[0-9]{6}.mbox";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(htmlText);
		Set<String> set = new HashSet<String>();
		while(matcher.find()) {
			set.add(this.projectMboxBaseUrl+"/"+matcher.group());
			String s = matcher.group();
			if (last.compareTo(s) < 0) last = s;
		}
		last = this.projectMboxBaseUrl+"/"+last;
		urlList.addAll(set);
	}
	@Override
	public void crawl_middle(int id, Crawler crawler){
		MboxCrawler mboxCrawler = (MboxCrawler) crawler; 
		mboxCrawler.last = last;
		for (int i = 0; i < urlList.size(); i++){
			if (i % this.subCrawlerNum == id){
				mboxCrawler.urlList.add(this.urlList.get(i));
			}
		}
	} 
	@Override
	public void crawl_data() {
		// TODO Auto-generated method stub
		//int cnt = 0;
		for (String url : this.urlList){
		//	cnt++;
			//if (cnt % this.subCrawlerNum != this.subid) continue;
			String storagePath = this.storageBasePath + Path.SEPARATOR + url.replaceAll("[<>\\/:*?]", "");
			System.out.println("Thread" + this.subid + "  " + url);
			if (this.needLog){
				if (!url.contains(last) && FileUtil.logged(storagePath) && FileUtil.exist(storagePath)){
					continue;
				}else{
					if (url.contains(last)){
						System.out.println("");
					}
					String text = HtmlDownloader.downloadClient(url,null,null);
					int times = 1;
					while ((times > 0) && (text.length() == 0)){
						try {
							sleep(15000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						text = HtmlDownloader.downloadClient(url,null,null);
						times--;
					}
					if (text.length() > 0)FileUtil.write(storagePath, text);
					FileUtil.logging(storagePath);
				}
			}else{
				String text = HtmlDownloader.downloadOrin(url,null,null);
				FileUtil.write(storagePath, text);				
			}
		}
	}
	public static void main(String[] args) throws Exception{
		MboxCrawler crawl = new MboxCrawler();
		Project project = new Project();
		ThreadManager.initCrawlerTaskManager();
		project.setOrgName("apache");
		project.setProjectName("lucene");
		project.setName("lucenegeneral");
		crawl.setProject(project);
		crawl.setProjectMboxBaseUrl("http://mail-archives.apache.org/mod_mbox/lucene-general/");
		crawl.needLog = true;
		crawl.crawlerType = Crawler.MAIN;
		crawl.Crawl();
	}

	public String getProjectMboxBaseUrl() {
		return projectMboxBaseUrl;
	}

	public void setProjectMboxBaseUrl(String projectMboxBaseUrl) {
		this.projectMboxBaseUrl = projectMboxBaseUrl;
	}

	public List<String> getUrlList() {
		return urlList;
	}

	public void setUrlList(List<String> urlList) {
		this.urlList = urlList;
	}
}

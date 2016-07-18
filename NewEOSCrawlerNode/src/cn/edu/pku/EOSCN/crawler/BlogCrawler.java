package cn.edu.pku.EOSCN.crawler;

import java.io.IOException;
import java.util.List;

import org.apache.commons.httpclient.HttpException;

import cn.edu.pku.EOSCN.crawler.util.UrlOperation.URLReader;
import cn.edu.pku.EOSCN.entity.Project;

public class BlogCrawler extends Crawler {
	private static final String googleApiBase = 
			"https://www.google.com.hk/search?hl=en&tbm=blg&num=100&q="; 
	public BlogCrawler() {
		// TODO Auto-generated constructor stub
	}

	public BlogCrawler(Project project, List<String> urllist) {
		super(project, urllist);
		// TODO Auto-generated constructor stub
	}

	public BlogCrawler(String username, String pw, Project project, List<String> urllist) {
		super(username, pw, project, urllist);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void crawl_url() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void crawl_data() throws Exception {
		// TODO Auto-generated method stub

	}
	public static void main(String args[]) throws HttpException, IOException{
		String pageurl = "https://www.google.com/search?hl=en&q=lucene";
		String content = URLReader.getHtmlStringFromUrl(pageurl);
		System.out.println(content);
		
	}
}

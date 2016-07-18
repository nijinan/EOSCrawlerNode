package cn.edu.pku.EOSCN.crawler;

import java.util.List;

import cn.edu.pku.EOSCN.crawlerTask.CrawlerTaskManager;
import cn.edu.pku.EOSCN.entity.Project;

public class TestCrawler extends Crawler {
	
	public TestCrawler(Project project, List<String> urlList){
		super(project, urlList);
	}
	
	public TestCrawler() {
		super();
	}

	@Override
	public void Crawl() {
		int t = (int) (120);
		int j=1;
		for (int i = 1; i <= t; i++) {
			System.out.println(this.getTaskuuid() + " print " + i +"/"+ t);
			//CrawlerTaskManager.printTaskStatus();
			//t --;
			//j = 1/(t-i);
			//addPercentageBy(10);
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		setStatus(SUCCESS);
		finish();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}


}

/**
 * 
 */
package cn.edu.pku.EOSCN.crawler;

import java.util.LinkedList;
import java.util.List;

import cn.edu.pku.EOSCN.crawler.util.Doc.URLReader;
import cn.edu.pku.EOSCN.entity.Project;

/**
 * @author nijinan
 *
 */
public class GitCrawler extends Crawler {

	public GitCrawler() {
		super();
	}


	/* (non-Javadoc)
	 * @see cn.edu.pku.EhuyaOSCN.crawler.Crawler#init()
	 */
	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cn.edu.pku.EOSCN.crawler.Crawler#Crawl()
	 */
	@Override
	public void Crawl() throws Exception {
		// TODO Auto-generated method stub
		String gitApiUrl = "";
		List<String> urls = new LinkedList<String>();
		for (String url : urls){
			
		}
		URLReader.getHtmlStringFromUrl(gitApiUrl);
		
	}

}

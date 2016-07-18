package cn.edu.pku.EOSCN.crawler.util.mbox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




/**   
* @Title: MBoxURLExtractor.java
* @Package cn.edu.pku.EOS.crawler.util.mbox
* @Description: 从Apache的项目的邮件列表页面抽取出mbox文件的链接
* @author jinyong     jinyonghorse@hotmail.com  
* @date 2013-7-9 上午10:33:09
*/

public class MBoxURLExtractor {
	private String MailHomePageURL;
	
	public MBoxURLExtractor(String url) {
		this.MailHomePageURL = url;
	}
	
	public ArrayList<String> getMBoxURLList() {
		ArrayList<String> urlList = new ArrayList<String>();
		HashSet<String> urlSet = new HashSet<String>();
		String htmlText = HttpPage.getPageByHttpClient(this.MailHomePageURL);
		String patternString = "[0-9]{6}.mbox";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(htmlText);
		
		while(matcher.find()) {
			urlSet.add(this.MailHomePageURL+"/"+matcher.group());
		}
//		htmlText = HttpPage.getPageByHttpClient(urlList.get(0));
//		System.out.println(htmlText);
		urlList.addAll(urlSet);
		return urlList;
	}
	
	public static void main(String args[]) {
		
		String urlString = "http://mail-archives.apache.org/mod_mbox/lucene-lucene-net-commits/";
		ArrayList<String > url = new MBoxURLExtractor(urlString).getMBoxURLList();
		for(String s : url) {
			System.out.println(s);
		}
	}
}

package cn.edu.pku.EOSCN.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储开源项目和资源类型关系的类，对印的数据库表中存储了开源项目对因的资源类型，以及每种资源的爬取地址，以及爬虫类型等元信息
 * @author 张灵箫
 *
 */

public class ResourceMetaData {

	public static final String DOC_TYPE = "Documentation";
	public static final String CODE_TYPE = "SourceCode";
	public static final String MAIL_TYPE = "MailingList";
	public static final String BUG_TYPE = "IssueTracker";
	public static final String QA_TYPE = "FAQ";
	public static final String RELATIVEWEB_TYPE = "RelativeWeb";



	private String type;
	private String urlListString;
	private List<String> baseUrls = new ArrayList<String>();
	private String crawler;
	private int count;
	
	
	
	public ResourceMetaData(){}
	public ResourceMetaData(String type, String urls, String crawler) {
		this.type = type;
		this.urlListString = urls;
		this.crawler = crawler;
		getUrlsFromUrlString(urls);
	}
	
	public String getUrlListString() {
		return urlListString;
	}
	public void setUrlListString(String urlListsString) {
		this.urlListString = urlListsString;
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getCrawler() {
		return crawler;
	}
	public void setCrawler(String crawler) {
		this.crawler = crawler;
	}
	public List<String> getBaseUrls() {
		return baseUrls;
	}
	public void setBaseUrls(List<String> baseUrls) {
		this.baseUrls = baseUrls;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @author 张灵箫
	 * 数据库中urlliststring存了多个url地址，用分号;隔开，该方法将这些地址存入baseUrls中
	 * 注意：会清空原来的baseurl中的内容！
	 * @param urlListsString
	 */
	public void getUrlsFromUrlString(String urlListsString) {
		if (urlListString == null) {
			return;
		}
		String[] urlList = urlListsString.split(";");
		baseUrls.clear();
		for (String string : urlList) {
			baseUrls.add(string);
		}
	}
	
	public static void main(String[] args) {
		ResourceMetaData rData = new ResourceMetaData();
		rData.getUrlsFromUrlString("http://lucene.apache.org/core/4_3_0/index.html;http://lucene.apache.org/core/3_6_2/index.html");
		for (String string : rData.getBaseUrls()) {
			System.out.println(string);
		}
	}
}

package cn.edu.pku.EOSCN.crawler.util.htmlCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import cn.edu.pku.EOSCN.crawler.util.htmlCode.HtmlPage.Segment;

public class htmlCodeExtrator {
	
	public static String parseHtml(String html){
		Document root = Jsoup.parse(html);
		Element body = root.getElementsByTag("body").first();
		String ret = parseElement(body);
		return ret;
	}
	
	public static String parseElement(Node root){
		String ret = "";
		if (root.nodeName().equals("br")) return "\n";
		if (root.nodeName().equals("#text")){
			return root.toString();
		}
		if (root.nodeName().equals("div")) ret += "\n";
		if (root.nodeName().equals("tr")) 
			ret += "\n";
		for(Node child : root.childNodes()){
			ret += parseElement(child);
		}
		if (root.nodeName().equals("div")) ret += "\n";
		return ret;
	}
	
	public static List<String> getCode(String text){
		List<String> ret = new LinkedList<String>();
		return ret;
	}
	public static void main(String args[]) throws IOException{
		File file = new File("a.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String data = null;
		String htmlStr = "";
		while((data = br.readLine()) != null){
			htmlStr = htmlStr + data + "\n";
		}
		//String htmlStr = "<html><head></head><body>qwe<br>opaoiw</body></html>";
		
		String page = parseHtml(htmlStr);
		page = page.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&").
				replace("&quot;", "\"")
		.replace("&nbsp;", " ");
		HtmlPage html = new HtmlPage(page);
		html.process();
		for (Segment seg : html.segments){
			if (seg.getContentType() == Segment.CODE_CONTENT){
				System.out.println(seg.getContentText());
				System.out.println("***********");
			}
		}
		//System.out.println(page);
	}
}

package cn.edu.pku.EOSCN.crawler.util.htmlCode;

import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class htmlCodeExtrator {
	
	public static String parseHtml(String html){
		Document root = Jsoup.parse(html);
		Element body = root.getElementsByTag("body").first();
		return parseElement(body);
	}
	
	public static String parseElement(Node root){
		String ret = "";
		if (root.nodeName().equals("br")) return "\n";
		if (root.nodeName().equals("#text")){
			return root.toString();
		}
		if (root.nodeName().equals("div")) ret += "\n";
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
	public static void main(String args[]){
		
		System.out.println();
	}
}

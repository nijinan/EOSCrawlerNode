package cn.edu.pku.EOSCN.crawler.util.htmlCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
		if (root.nodeName().equals("span")){
			System.out.println("asd");
		}
		if (root.nodeName().equals("#text")){
			return root.toString().replaceAll("\n+", " ").replaceAll("\t+", " ").replaceAll(" +", " ");
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
	public static String process(String htmlStr) throws IOException{
		String ret = "";
//		String page = parseHtml(htmlStr);
//		page = page.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&").
//				replace("&quot;", "\"")
//		.replace("&nbsp;", " ");
		HtmlPage html = new HtmlPage(htmlStr);
		html.process();
		for (Segment seg : html.segments){
			if (seg.getContentType() == Segment.CODE_CONTENT){
				ret += seg.getContentText();
				ret += "***********\n";
			}
		}
		return ret;
	}
	public static void main(String args[])throws IOException{
		//File dir = new File("D:\\tmp\\get+similarity+between+two+documents+Lucene\\get+similarity+between+two+documents+Lucene");
		File dir = new File("D:\\tmp\\lucene\\get+similarity+between+two+documents+Lucene");
		for (File file : dir.listFiles()){
		try{
			if (file.isDirectory()) continue;
			if (file.getName().startsWith("_3h")){
				System.out.println("");
			}
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String data = null;
			String htmlStr = "";
			while((data = br.readLine()) != null){
				htmlStr = htmlStr + data + "\n";
			}
			String ret = process(htmlStr);
			File f = new File(dir.getAbsolutePath()+File.separator+"code"+File.separator+file.getName());
			FileWriter fw = new FileWriter(f);
			fw.write(ret);
			fw.close();
		}catch (Exception e){
			e.printStackTrace();
		}
		}
	}
}

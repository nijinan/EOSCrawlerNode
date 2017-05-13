package cn.edu.pku.EOSCN.detect;

import cn.edu.pku.EOSCN.entity.Project;

public class GitDetector extends Detector {

	public GitDetector() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean detect(String url, String content, Project project) throws Exception {
		// TODO Auto-generated method stub
		//if (url.contains("?")) return true;
		if (url.toLowerCase().contains("git") || url.toLowerCase().contains("tickets") || 
				url.toLowerCase().contains("issue") || url.toLowerCase().contains("bug")) {
			for (String s : url.split("/")){
				if (s.length() > 35) return true;
				if (s.toLowerCase().equals("git")) return true;
				if (s.toLowerCase().startsWith("tickets")) return true;
				if (s.toLowerCase().startsWith("issue")) return true;
				if (s.toLowerCase().startsWith("bug")) return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean detectEntry(String url, String content, Project project) throws Exception {
		// TODO Auto-generated method stub
		//if (url.contains("?")) return true;
		if (url.toLowerCase().contains("git") || url.toLowerCase().contains("tickets") || 
				url.toLowerCase().contains("issue") || url.toLowerCase().contains("bug")) {
			for (String s : url.split("/")){
				if (s.length() > 35) return true;
				if (s.toLowerCase().equals("git")) return true;
				if (s.toLowerCase().startsWith("tickets")) return true;
				if (s.toLowerCase().startsWith("issue")) return true;
				if (s.toLowerCase().startsWith("bug")) return true;
			}
		}
		return false;
	}
	
	@Override
	public void dispatch(String url, Project project) throws Exception {
		// TODO Auto-generated method stub
		
	}

}

package cn.edu.pku.EOSCN.detect;

import cn.edu.pku.EOSCN.entity.Project;

public class MHonArcDetector extends Detector {

	public MHonArcDetector() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean detect(String url, String content, Project project) throws Exception {
		// TODO Auto-generated method stub
		if (content.contains("Mail converted by") && content.toLowerCase().contains("mhonarc")){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean detectEntry(String url, String content, Project project) throws Exception {
		// TODO Auto-generated method stub
		if (content.contains("Mail converted by") && content.toLowerCase().contains("mhonarc")){
			if (url.contains("maillist.html"))
				return true;
		}
		return false;
	}
	
	@Override
	public void dispatch(String url, Project project) throws Exception {
		// TODO Auto-generated method stub
	}

}

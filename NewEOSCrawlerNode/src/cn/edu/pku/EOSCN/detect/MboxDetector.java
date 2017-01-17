package cn.edu.pku.EOSCN.detect;

import cn.edu.pku.EOSCN.entity.Project;

public class MboxDetector extends Detector {

	@Override
	public boolean detect(String url, String content, Project project) throws Exception {
		// TODO Auto-generated method stub
		if (url.toLowerCase().contains("mbox")) return true;
		return false;
	}

	@Override
	public boolean detectEntry(String url, String content, Project project) throws Exception {
		// TODO Auto-generated method stub
		if (url.toLowerCase().contains("mbox")) return true;
		return false;
	}

	@Override
	public void dispatch(String url, Project project) throws Exception {
		// TODO Auto-generated method stub

	}

}

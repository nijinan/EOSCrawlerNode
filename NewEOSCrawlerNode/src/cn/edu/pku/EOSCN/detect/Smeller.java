package cn.edu.pku.EOSCN.detect;

import cn.edu.pku.EOSCN.entity.Project;

public class Smeller {

	public Smeller() {
		// TODO Auto-generated constructor stub
	}

	public static boolean smellEntry(String page, String url, Project project) {
		boolean isEntry = false;
		Detector detector;
		try {
			detector = new MHonArcDetector(); 
			if (detector.detect(page,url, project)){
				detector.dispatch(url, project);
				isEntry = true;
			}
			detector = new BugzillaDetector(); 
			if (detector.detect(page,url, project)){
				detector.dispatch(url, project);
				isEntry = true;
			}			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isEntry;
	}
	
	public static boolean smell(String page, String url,Project project){
		boolean isothers = false;
		Detector detector;
		try {
			detector = new MHonArcDetector();
			
			return detector.detect(page,url, project);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
}

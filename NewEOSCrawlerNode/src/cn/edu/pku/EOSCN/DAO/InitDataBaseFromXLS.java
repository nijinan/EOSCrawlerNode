package cn.edu.pku.EOSCN.DAO;

import java.sql.SQLException;

import org.json.JSONObject;

import cn.edu.pku.EOSCN.business.InitBusiness;
import cn.edu.pku.EOSCN.crawler.util.Statics;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.entity.Project;

public class InitDataBaseFromXLS {
	public static void main(String args[]){
		InitBusiness.initEOS();
		String html = FileUtil.read("D:\\CrawlData\\Apache\\ProjectsList.json");
		JSONObject jsobj = new JSONObject(html);
		for (String name : jsobj.keySet()){
			JSONObject obj = jsobj.getJSONObject(name);
			if (!obj.has("name") || !obj.has("homepage")) continue;
			System.out.println(obj.get("name"));
//			Project project = new Project(obj.optString("name"),obj.optString("homepage"));
//			project.setProgrammingLanguage(obj.optString("programming-language"));
//			project.setHostUrl(obj.optString("homepage"));
//			project.setOrgName("Apahce");
//			project.setProjectName(obj.optString("name"));
//			project.setDescription(obj.optString("description"));
//			ProjectDAO.insertProject(project);
			Project project;
			try {
				project = ProjectDAO.getProjectByName(obj.optString("name"));
				System.out.println(project.getName());
				Statics.work(obj,project);
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}

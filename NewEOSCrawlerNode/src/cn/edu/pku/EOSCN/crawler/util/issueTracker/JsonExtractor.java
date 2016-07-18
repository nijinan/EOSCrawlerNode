package cn.edu.pku.EOSCN.crawler.util.issueTracker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.edu.pku.EOSCN.crawler.util.issueTracker.json.*;
import cn.edu.pku.EOSCN.entity.IssueTracker;

/**
 * @author Carrie
 *
 */
public class JsonExtractor {
	
	public List<IssueTracker> list=new ArrayList<IssueTracker>();
	public String projectUuid;	
	
	public JsonExtractor(String path,String projectUuid) throws IOException, JSONException, org.json.JSONException{
		this.projectUuid=projectUuid;
		StringBuilder sb=new StringBuilder("");
		BufferedReader br =new BufferedReader(new FileReader(path));
		String line=null;
		while ((line=br.readLine())!=null)
			sb.append(line+"\r\n");
		br.close();
				
		JSONObject jo=new JSONObject(sb.toString());
		JSONArray issues=(JSONArray) jo.get("issues");
		for (int i=0;i<issues.length();i++){
			JSONObject issue=issues.getJSONObject(i);
			JSONObject fields=issue.getJSONObject("fields");			
			JSONObject issuetype=fields.getJSONObject("issuetype");
			JSONArray versionArray = (JSONArray)fields.get("fixVersions");
			
			IssueTracker d = new IssueTracker(projectUuid);
			
			if(!issuetype.equals(null))
				d.setType(issuetype.get("name").toString());
			else d.setType("null");
			
			if(!issue.get("key").equals(null))
				d.setKeyname(issue.get("key").toString());
			else d.setKeyname("null");
			
			if(!fields.get("summary").equals(null))
				d.setSummary(fields.get("summary").toString());
			else d.setSummary("null");
			
//			if(fields.get("assignee").equals("null"))
			if(!fields.get("assignee").equals(null))
				d.setAssignee(fields.getJSONObject("assignee").get("name").toString());
			else d.setAssignee("null");
			
			if(!fields.get("reporter").equals(null))
				d.setReporter(fields.getJSONObject("reporter").get("name").toString());
			else d.setReporter("null");
			
			if(!fields.get("priority").equals(null))
				d.setPriority(fields.getJSONObject("priority").get("name").toString());
			else d.setPriority("null");
			
			if(!fields.get("status").equals(null))
				d.setStatus(fields.getJSONObject("status").get("name").toString());
			else d.setStatus("null");

			if(!fields.get("resolution").equals(null))
				d.setResolution(fields.getJSONObject("resolution").get("name").toString());
			else d.setResolution("null");
			
			if(!fields.get("created").equals(null))
				d.setCreated(fields.get("created").toString());
			else d.setCreated("null");
			
			if(!fields.get("updated").equals(null))
				d.setUpdated(fields.get("updated").toString());
			else d.setUpdated("null");
			
			//多个版本的情况
//			d.setVersion(fields.getJSONObject("fixVersions").get("name").toString());
			if(versionArray.length() > 0){
				d.setVersion(versionArray.getJSONObject(0).get("name").toString());
				for(int j = 1; j < versionArray.length(); j++){	
					d.setVersion(d.getVersion() + ";" + versionArray.getJSONObject(j).get("name").toString());
				}						
			}
			else d.setVersion("");
			
			if(!fields.get("description").equals(null))
				d.setDescription(fields.get("description").toString());
			else d.setDescription("null");
			
			list.add(d);
		}
	}

}


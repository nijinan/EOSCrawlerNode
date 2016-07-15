package cn.edu.pku.EOSCN.DAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;

import cn.edu.pku.EOSCN.entity.ResourceMetaData;

public class ResourceMetaDataDAO {

	public List<ResourceMetaData> getDataListByProjectUuid(String uuid) throws SQLException {
		List<ResourceMetaData> datas = DAOUtils.getResult(ResourceMetaData.class, "select * from resourceMetaData where projectuuid = ?", uuid);
		for (ResourceMetaData resourceMetaData : datas) {
			//System.out.println(resourceMetaData.getType());
			resourceMetaData.getUrlsFromUrlString(resourceMetaData.getUrlListString());
		}
		return datas;
	}
	
	public int insertResourceMetaData(String projectuuid, ResourceMetaData data) throws SQLException {
		int result = DAOUtils.update("INSERT INTO resourceMetaData(projectuuid, type, crawler, urlListString) " +
				"VALUES(?, ?, ?, ?)", projectuuid, data.getType(), data.getCrawler(), data.getUrlListString());
		return result;
	}

	public int deleteAllMetaDataOfProject(String uuid) throws SQLException {
		int result = DAOUtils.update("DELETE FROM resourceMetaData WHERE projectuuid = ?", uuid);
		return result;
	}

	public static int updateCount(String puuid, String type, int count) throws SQLException {
		int result = DAOUtils.update("UPDATE resourceMetaData SET count = ? where projectuuid = ? AND type = ?", count, puuid, type);
		return result;
	}
}

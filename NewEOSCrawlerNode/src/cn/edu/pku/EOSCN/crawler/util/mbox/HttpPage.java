package cn.edu.pku.EOSCN.crawler.util.mbox;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;


/**   
* @Title: HttpPage.java
* @Package cn.edu.pku.EOS.crawler.util.mbox
* @Description: 从url获取html文本
* @author jinyong     jinyonghorse@hotmail.com  
* @date 2013-7-9 上午10:49:50
*/

public class HttpPage {
	private final static int CONNECTION_TIMEOUT = 10000;
	private final static int SOCKET_TIMEOUT = 10000;

	public static String getPageByHttpClient(String url) {
		String page = null;
		HttpClient client = new HttpClient();
		client.getParams().setIntParameter("http.connection.timeout",
				CONNECTION_TIMEOUT);
		client.getParams().setIntParameter("http.socket.timeout",
				SOCKET_TIMEOUT);
		try {
			//System.out.println(url);
			HttpMethod method = new GetMethod(url);
			method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
	        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
	                new DefaultHttpMethodRetryHandler());

			InputStream input;
			BufferedReader reader;
			String line;
			StringBuffer buffer = new StringBuffer();
			client.executeMethod(method);
			input = method.getResponseBodyAsStream();
			reader = new BufferedReader(new InputStreamReader(input, "utf-8"));
			while ((line = reader.readLine()) != null) {
				buffer.append(line + "\r\n");
			}
			page = buffer.toString();
			
			reader.close();
			input.close();
			method.releaseConnection();
			Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return page;
	}
}

package cn.edu.pku.EOSCN.crawler.util.mbox;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.auxilii.msgparser.Message;

/**   
* @Title: DateHeader.java
* @Package cn.edu.pku.EOS.crawler.util.mbox
* @Description: 解析发送日期的类
* @author jinyong     jinyonghorse@hotmail.com  
* @date 2013-5-25 下午12:39:47
*/

public class DateHeader extends HeaderParser {

    public static final SimpleDateFormat date_format = new SimpleDateFormat(
    		"EEE, dd MMM yyyy HH:mm:ss ZZZZ", Locale.US);

    public DateHeader() {
    	super("Date");
    }

    @Override
    public void parse(Message message, String line) throws Exception {
    	message.setDate(date_format.parse(line));
    }
}

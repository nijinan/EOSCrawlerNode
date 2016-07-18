package cn.edu.pku.EOSCN.crawler.util.mbox;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;


import com.auxilii.msgparser.Message;


/**   
* @Title: MBoxParser.java
* @Package cn.edu.pku.EOS.crawler.util.mbox
* @Description: 解析MBox文件的类
* @author jinyong     jinyonghorse@hotmail.com  
* @date 2013-5-25 下午12:39:16
*/

public class MBoxParser extends AbstractParser {
	protected Logger logger = Logger.getLogger(MBoxParser.class.getName());
    public MBoxParser() {
	    headerParsers.add(new FromEmailHeader());
	    headerParsers.add(new ToEmailHeader());
	    headerParsers.add(new DateHeader());
	    headerParsers.add(new SubjectHeader());
    }

    public Message parse(File file) throws IOException, Exception {
        byte bytes[] = ReadFile.getBytesFromFile(file);
        String content = new String(bytes);
        return parse(content);
    }

    public Message parse(String content) throws Exception {
        Message msg = new Message();
        int idx_unix = content.indexOf("\n\n");
        int idx_win = content.indexOf("\r\n\r\n");
        String header = null;
        int start = 0;

        if (content.startsWith("From ")) {
            start = content.indexOf("\n");
        }

        int header_end = 0;

        if (idx_unix > 0) {
            header_end = idx_unix;
        } else if (idx_win > 0) {
            header_end = idx_win;
        }
//        logger.info("head_end is:"+header_end+"  and content length is :"+content.length());
        header = content.substring(start, header_end);
        msg.setHeaders(header);
        
        parseHeader(msg, header);
        parseBody(msg, content.substring(header_end + 1));
        
        return msg;
    }
    
}

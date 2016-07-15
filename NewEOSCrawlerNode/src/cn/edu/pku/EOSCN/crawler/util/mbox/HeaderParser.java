package cn.edu.pku.EOSCN.crawler.util.mbox;

import com.auxilii.msgparser.Message;

/**   
* @Title: HeaderParser.java
* @Package cn.edu.pku.EOS.crawler.util.mbox
* @Description: 解析邮件头的抽象类   其他例如ToEmailHeader等要继承该类
* @author jinyong     jinyonghorse@hotmail.com  
* @date 2013-5-25 下午12:37:21
*/

public abstract class HeaderParser {

    final String header;

    public HeaderParser(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public abstract void parse(Message message, String line) throws Exception;
}
package cn.edu.pku.EOSCN.crawler.util.mbox;

import com.auxilii.msgparser.Message;

/**   
* @Title: SubjectHeader.java
* @Package cn.edu.pku.EOS.crawler.util.mbox
* @Description: 解析Subject
* @author jinyong     jinyonghorse@hotmail.com  
* @date 2013-5-25 下午12:38:53
*/

public class SubjectHeader extends HeaderParser {

    public SubjectHeader() {
        super("Subject");
    }

    @Override
    public void parse(Message message, String line) {
        message.setSubject(line);
    }
}

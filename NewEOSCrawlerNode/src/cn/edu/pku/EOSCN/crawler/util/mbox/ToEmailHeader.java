package cn.edu.pku.EOSCN.crawler.util.mbox;

import java.util.List;

import com.auxilii.msgparser.Message;

/**   
* @Title: ToEmailHeader.java
* @Package cn.edu.pku.EOS.crawler.util.mbox
* @Description: 解析ToEmail地址和名称
* @author jinyong     jinyonghorse@hotmail.com  
* @date 2013-5-25 下午12:38:33
*/

public class ToEmailHeader extends EmailHeader {

    public ToEmailHeader() {
        super("To");
    }

    @Override
    public void assign(Message msg, List<MailAddress> emails) {
    	msg.setToEmail(emails.get(0).getEmail());
    	msg.setToName(emails.get(0).getDisplayName());
    }
}

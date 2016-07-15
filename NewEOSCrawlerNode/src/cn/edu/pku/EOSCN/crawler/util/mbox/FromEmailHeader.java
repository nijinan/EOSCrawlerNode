package cn.edu.pku.EOSCN.crawler.util.mbox;

import java.util.List;

import com.auxilii.msgparser.Message;

/**   
* @Title: FromEmailHeader.java
* @Package cn.edu.pku.EOS.crawler.util.mbox
* @Description: 解析FromMail地址和名称
* @author jinyong     jinyonghorse@hotmail.com  
* @date 2013-5-25 下午12:38:17
*/

public class FromEmailHeader extends EmailHeader {

    public FromEmailHeader() {
        super("From");
    }

    @Override
    public void assign(Message msg, List<MailAddress> emails) {
        msg.setFromEmail(emails.get(0).getEmail());
        msg.setFromName(emails.get(0).getDisplayName());
    }
}

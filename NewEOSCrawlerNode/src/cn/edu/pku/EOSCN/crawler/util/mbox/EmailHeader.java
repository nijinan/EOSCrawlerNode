package cn.edu.pku.EOSCN.crawler.util.mbox;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


import com.auxilii.msgparser.Message;

/**
 * 
 * @author martin
 */
public abstract class EmailHeader extends HeaderParser {
	protected final static Logger logger = Logger.getLogger(EmailHeader.class.getName());
    public EmailHeader(String header) {
        super(header);
    }

    @Override
    public void parse(Message msg, String line) {
        List<MailAddress> emails = splitAttendees(line);
      
        if (!emails.isEmpty())
                assign(msg, emails);
    }

    public abstract void assign(Message msg, List<MailAddress> emails);

    public static List<MailAddress> splitAttendees(String text) {
        List<MailAddress> addresses = new ArrayList<MailAddress>();
        MailAddress address = new MailAddress();
      
        int start = text.indexOf("<");
        int end = text.indexOf(">");
//        System.out.println("########################################################3");
//        System.out.println(text);
//        System.out.println("start : "+start);
//        System.out.println("end : "+end);
//        System.out.println("email: "+ text.substring(start+1,end));
//        System.out.println("name: "+ text.substring(0,start));
//        System.out.println("#######################################################4");
        if(start < 0 || end < 0) {
        	address.setEmail(text);
        	address.setDisplayName(text);
        	addresses.add(address);
        }
        else {
        	address.setEmail(text.substring(start+1,end));
        	address.setDisplayName(text.substring(0,start));
        	addresses.add(address);
        }
        
        
//        for (String part : parts) {
//        	
//            MailAddress addr = new MailAddress();
//            int start = part.indexOf("<");
//            int end = part.indexOf(">");
//            
//            if (start >= 0 && end >= 0) {
//                addr.setEmail(part.substring(start + 1, end));
//                addr.setDisplayName(StringUtils.strip(part.substring(0, start)
//                		.trim(), "\""));
//            } else {
//                addr.setEmail(part);
//            }
//            if (addr.getEmail().contains("@")) {
//                addresses.add(addr);
//            }
//        }
        return addresses;
    }
}
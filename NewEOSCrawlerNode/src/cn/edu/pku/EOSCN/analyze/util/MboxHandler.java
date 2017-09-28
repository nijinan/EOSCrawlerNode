package cn.edu.pku.EOSCN.analyze.util;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.parser.AbstractContentHandler;
import org.apache.james.mime4j.stream.BodyDescriptor;
import org.apache.james.mime4j.stream.Field;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MboxHandler extends AbstractContentHandler {

    private Map<String, String> mailMap = new HashMap();
    private Map<String, String> mailUserMap = new HashMap();
    private Map<String, String> mailReplyMap = new HashMap<String, String>();
    private Map<String, Set<String>> mailUserNameMap = new HashMap<String, Set<String>>();
    public MailInfo mailInfo = new MailInfo();


    @Override
    public void field(Field fieldData) throws MimeException {
        if (fieldData.toString().startsWith("Message-ID:") || fieldData.toString().startsWith("Message-Id:")) {
            mailInfo.id = fieldData.toString().substring(11).trim();
        } else if (fieldData.toString().startsWith("Subject:")) {
            mailInfo.subject = fieldData.toString().substring(8).trim();
        } else if (fieldData.toString().startsWith("In-Reply-To:")) {
            mailInfo.replyTo = fieldData.toString().substring(12).trim();
            if (mailInfo.replyTo.length() > 0)
                mailReplyMap.put(mailInfo.id, mailInfo.replyTo);
        } else if (fieldData.toString().startsWith("From:")) {
            mailInfo.from = fieldData.toString().substring(5).trim();
            Pair<String, String> senderPair = MailUtil.extractMailNameAndAddress(mailInfo.from);
            if (senderPair != null) {
                mailInfo.senderName = senderPair.getLeft();
                mailInfo.senderMail = senderPair.getRight();
            } else {// has no mail address, e.g., from="undisclosed-recipients:;"
                mailInfo.senderName = mailInfo.senderMail = mailInfo.from;
            }
        } else if (fieldData.toString().startsWith("To:")) {
            mailInfo.to = fieldData.toString().substring(3).trim();

            List<Pair<String, String>> mailPairs = MailUtil.extractMultiMailNameAndAddress(mailInfo.to);
            int mailNum = mailPairs.size();
            if (mailNum > 0) {
                mailInfo.receiverNames = new String[mailNum];
                mailInfo.receiverMails = new String[mailNum];

                for (int i = 0; i < mailNum; i++) {
                    Pair<String, String> mailPair = mailPairs.get(i);
                    mailInfo.receiverNames[i] = mailPair.getLeft();
                    mailInfo.receiverMails[i] = mailPair.getRight();
                }
            } else {// has no mail address
                mailInfo.receiverNames = new String[]{};
                mailInfo.receiverMails = new String[]{};
            }
        } else if (fieldData.toString().startsWith("Date:")) {
            mailInfo.date = fieldData.toString().substring(5).trim();
        }
    }

    @Override
    public void body(BodyDescriptor bd, InputStream is) throws MimeException,
            IOException {
        String r = "";
        byte[] buffer = new byte[200];
        String s;
        int len;
        try {
            while ((len = is.read(buffer)) != -1) {
                if (len != 200) {
                    byte buffer2[] = new byte[len];
                    System.arraycopy(buffer, 0, buffer2, 0, len);
                    s = new String(buffer2);
                } else {
                    s = new String(buffer);
                }
                if (s != null)
                    r += s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mailInfo.body = r;
        //System.out.println("body");
        //System.out.println(r);
    }

    @Override
    public void startMultipart(BodyDescriptor bd) throws MimeException {
    }

    @Override
    public void endMultipart() throws MimeException {
    }

    @Override
    public void epilogue(InputStream is) throws MimeException {
    }

    @Override
    public void preamble(InputStream is) throws MimeException {
    }

    @Override
    public void startHeader() throws MimeException {
    }

    @Override
    public void endHeader() throws MimeException {
    }

    @Override
    public void startBodyPart() throws MimeException {
    }

    @Override
    public void endBodyPart() throws MimeException {
    }

    @Override
    public void startMessage() throws MimeException {
        mailInfo = new MailInfo();
    }

    @Override
    public void endMessage() throws MimeException {


    }


    @Override
    public void raw(InputStream is) throws MimeException {
    }


    public Map<String, Set<String>> getMailUserNameMap() {
        return mailUserNameMap;
    }

}

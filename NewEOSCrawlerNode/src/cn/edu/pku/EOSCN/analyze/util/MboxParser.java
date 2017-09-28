package cn.edu.pku.EOSCN.analyze.util;

import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.stream.MimeConfig;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2017/5/23.
 */
public class MboxParser {
    private static Charset charset = Charset.forName("UTF-8");
    private final static CharsetDecoder DECODER = charset.newDecoder();
    MimeStreamParser parser = null;
    MboxHandler myHandler = null;
    public Set<String> emails = new HashSet();
    public int emailsCnt = 0;
    public void parse(File mboxFile){
        if (mboxFile.isDirectory()) {
            for (File f : mboxFile.listFiles())
                parse(f);
            return;
        }
        if (!mboxFile.getName().endsWith(".mbox"))
            return;
        MboxIterator iterator;
        try {
            parser = new MimeStreamParser(new MimeConfig());
            myHandler = new MboxHandler();
            parser.setContentHandler(myHandler);
            iterator = MboxIterator.fromFile(mboxFile).charset(DECODER.charset()).build();
            emails.clear();
            emailsCnt = 0;
        } catch (IOException e) {
            return;
        }
        for (CharBufferWrapper message : iterator) {
            if (message.toString().contains("Subject: svn commit"))
                continue;
            if (message.toString().contains("Subject: cvs commit"))
                continue;
            if (message.toString().contains("Subject: ["))
                continue;
            parse(message);
        }
    }
    public void parse(CharBufferWrapper message) {
        try {
            parser.parse(new ByteArrayInputStream(message.toString().trim().getBytes()));
            String email = myHandler.mailInfo.senderMail;
            emails.add(email);
            emailsCnt++;
        } catch (MimeException | IOException e) {
            e.printStackTrace();
        }
    }
}

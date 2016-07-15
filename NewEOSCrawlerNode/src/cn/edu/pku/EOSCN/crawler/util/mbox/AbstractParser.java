package cn.edu.pku.EOSCN.crawler.util.mbox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.auxilii.msgparser.Message;

public abstract class AbstractParser {
	protected List<HeaderParser> headerParsers = new ArrayList<HeaderParser>();


    public Message parse(File file) throws IOException, Exception {
            return null;
    }

    public Message parse(String content) throws Exception {
            return null;
    }
    /**
     * 解析header 读取header对应的串 冒号之后即为其内容
     */
    public void parseHeader(Message message, String header) throws Exception { 
    	String lines[] = header.split("\n"); for(int i = 0; i < lines.length; i++){
    		String line = lines[i];
    		for (HeaderParser parser : headerParsers) {
			String header_prefix = parser.getHeader() + ": ";
                StringBuilder sb = new StringBuilder(); 
                if (line.startsWith(header_prefix)) {
                	sb.append(line+"\n");
                	for (int j = i+1; j < lines.length; j++) {
						if(lines[j].indexOf(":")!=-1) break;
						else sb.append(lines[j]+"\n");
					}
                	
                    String header_content = sb.toString().substring(header_prefix.length());
                    parser.parse(message, header_content);
                }
    		}
    	}
    }

    public void parseBody(Message message, String body) {
        message.setBodyText(body);
        message.setBodyRTF("");
    }

    public List<HeaderParser> getHeaders() {
        return headerParsers;
    }
}

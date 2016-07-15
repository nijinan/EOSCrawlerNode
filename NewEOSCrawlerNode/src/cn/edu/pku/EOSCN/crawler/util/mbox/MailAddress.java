package cn.edu.pku.EOSCN.crawler.util.mbox;

/**   
* @Title: MailAddress.java
* @Package cn.edu.pku.EOS.crawler.util.mbox
* @Description: 解析邮件地址
* @author jinyong     jinyonghorse@hotmail.com  
* @date 2013-5-25 下午12:34:23
*/

public class MailAddress {

    String displayName;
    String email;

    public MailAddress(String display_name, String email) {
        this.displayName = display_name;
        this.email = email;
    }

    public MailAddress() {

    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        if (displayName == null) {
                return email;
        }
        return "\"" + displayName + "\"" + " <" + email + ">";
    }

    public void setDisplayName(String name) {
        displayName = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o.getClass() == this.getClass()) {
            MailAddress addr = (MailAddress) o;
            return addr.email.equals(email)
            		&& addr.displayName.equals(displayName);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59* hash+ (this.displayName != null ? this.displayName.hashCode() : 0);
        hash = 59 * hash + (this.email != null ? this.email.hashCode() : 0);
        return hash;
    }
}

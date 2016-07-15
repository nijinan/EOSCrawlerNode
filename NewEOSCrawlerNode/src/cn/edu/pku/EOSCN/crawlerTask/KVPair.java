package cn.edu.pku.EOSCN.crawlerTask;

public class KVPair {
	private String key;
	private String value;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public KVPair(String k, String v) {
		key = k;
		value = v;
	}
}

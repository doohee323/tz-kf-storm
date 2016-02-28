package tzkfstorm.example7;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;

import backtype.storm.tuple.Values;

public class LogBean {

	private static final Log log = LogFactory.getLog(LogBean.class);
	private static final Gson gson = new Gson();

	public static LogBean parse(String line) {
		try {
			return gson.fromJson(line, LogBean.class);
		} catch (Exception e) {
			log.error("LogBean==============================================" + line);
			return null;
		}
	}

	private String id;
	private String timestamp;
	private String hostname;
	private String client_ip;

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getClient_ip() {
		return client_ip;
	}

	public void setClient_ip(String client_ip) {
		this.client_ip = client_ip;
	}

	public static void main(String[] args) throws Exception {
		LogBean lb = LogBean.parse("{timestamp:1441411710347,hostname:ruleset33.xdn.com,client_ip:10.115.74.54}");
		new Values(lb);
		LogBean lb2 = LogBean.parse("{\"timestamp\":1441411710347,\"hostname\":\"ruleset33.xdn.com\",\"client_ip\":\"10.115.74.54\"}");
		new Values(lb2);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}

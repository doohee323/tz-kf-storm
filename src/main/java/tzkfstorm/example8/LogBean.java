package tzkfstorm.example8;

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
	private Long microsecs;
	private String hostname;
	private String query_type;
	private String ruleset_id;
	private String user_id;
	private String rule_id;
	private String client_ip;
	private String asn;
	private String geoip_country_code;
	private String response_type;
	private String destination_id;

	public static void main(String[] args) throws Exception {
		String test = "{\"timestamp\":1453424390,\"microsecs\":179898,\"hostname\":\"healthcheck.xdn.com\",\"query_type\":\"A\",\"ruleset_id\":\"4922\",\"user_id\":\"384\",\"rule_id\":\"24603\",";
		test += "\"client_ip\":\"23.249.49.34\",\"asn\":\"40934\",\"geoip_country_code\":\"US\",\"response_type\":\"A\",\"destination_id\":\"3207\"}";
		LogBean lb = LogBean.parse(test);
		new Values(lb);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public Long getMicrosecs() {
		return microsecs;
	}

	public void setMicrosecs(Long microsecs) {
		this.microsecs = microsecs;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getQuery_type() {
		return query_type;
	}

	public void setQuery_type(String query_type) {
		this.query_type = query_type;
	}

	public String getRuleset_id() {
		return ruleset_id;
	}

	public void setRuleset_id(String ruleset_id) {
		this.ruleset_id = ruleset_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getRule_id() {
		return rule_id;
	}

	public void setRule_id(String rule_id) {
		this.rule_id = rule_id;
	}

	public String getClient_ip() {
		return client_ip;
	}

	public void setClient_ip(String client_ip) {
		this.client_ip = client_ip;
	}

	public String getAsn() {
		return asn;
	}

	public void setAsn(String asn) {
		this.asn = asn;
	}

	public String getGeoip_country_code() {
		return geoip_country_code;
	}

	public void setGeoip_country_code(String geoip_country_code) {
		this.geoip_country_code = geoip_country_code;
	}

	public String getResponse_type() {
		return response_type;
	}

	public void setResponse_type(String response_type) {
		this.response_type = response_type;
	}

	public String getDestination_id() {
		return destination_id;
	}

	public void setDestination_id(String destination_id) {
		this.destination_id = destination_id;
	}
}

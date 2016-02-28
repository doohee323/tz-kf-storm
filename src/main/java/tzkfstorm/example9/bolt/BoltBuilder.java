package tzkfstorm.example9.bolt;

import java.util.Properties;

import tzkfstorm.example9.Keys;

/**
 */
public class BoltBuilder {

	public Properties configs = null;

	public BoltBuilder(Properties configs) {
		this.configs = configs;
	}

	public EsperBolt buildEsperBolt() {
		String SOLR_STREAM = "solr-stream";
		String topic = configs.getProperty(Keys.KAFKA_TOPIC);
		String kafa_spout = configs.getProperty(Keys.KAFKA_SPOUT_ID) + "_default";
		EsperBolt esperBolt = new EsperBolt.Builder().inputs().aliasComponent(Keys.ESPER_BOLT_ID)
				.toEventType(kafa_spout).outputs().outputs().onStream(SOLR_STREAM).emit("id", "timestamp", "client_ip")
				.statements().add("select * from " + kafa_spout + " where hostname = \"ruleset32.xdn.com\"").build();
		return esperBolt;
	}

	public SolrBolt buildSolrBolt() {
		String solrServerUlr = configs.getProperty(Keys.SOLR_SERVER);
		String collection = configs.getProperty(Keys.SOLR_COLLECTION);
		SolrBolt solrBolt = new SolrBolt(solrServerUlr + collection);
		return solrBolt;
	}

}

package tzkfstorm.case9.bolt;

import java.util.Properties;

/**
 */
public class BoltBuilder {

	public Properties configs = null;

	public BoltBuilder(Properties configs) {
		this.configs = configs;
	}

	public EsperBolt buildEsperBolt() {
		String SOLR_STREAM = "solr-stream";
		String topic = configs.getProperty("kafa.topic");
		String kafa_spout = configs.getProperty("kafka-spout") + "_default";
		EsperBolt esperBolt = new EsperBolt.Builder().inputs().aliasComponent("esper-bolt").toEventType(kafa_spout)
				.outputs().outputs().onStream(SOLR_STREAM).emit("id", "timestamp", "client_ip").statements()
				.add("select * from " + kafa_spout + " where hostname = \"ruleset32.xdn.com\"").build();
		return esperBolt;
	}

	public SolrBolt buildSolrBolt() {
		String solrServerUlr = configs.getProperty("solr.url");
		String collection = configs.getProperty("solr.collection");
		SolrBolt solrBolt = new SolrBolt(solrServerUlr + collection);
		return solrBolt;
	}

}

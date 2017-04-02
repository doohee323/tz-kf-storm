package tzkfstorm.case7.bolt;

import java.util.Properties;

/**
 */
public class BoltBuilder {

	public Properties configs = null;

	public BoltBuilder(Properties configs) {
		this.configs = configs;
	}

	public EsperBolt buildEsperBolt() {
		return new EsperBolt(configs);
	}

	public SolrBolt buildSolrBolt() {
		String solrServerUlr = configs.getProperty("solr.url");
		String collection = configs.getProperty("solr.collection");
		SolrBolt solrBolt = new SolrBolt(solrServerUlr + collection);
		return solrBolt;
	}

}

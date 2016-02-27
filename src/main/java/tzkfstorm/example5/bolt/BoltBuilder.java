package tzkfstorm.example5.bolt;

import java.util.Properties;

import tzkfstorm.example4.Keys;

/**
 */
public class BoltBuilder {

	public Properties configs = null;

	public BoltBuilder(Properties configs) {
		this.configs = configs;
	}

	public EsperBolt buildEsperBolt() {
		return new EsperBolt();
	}

	public SolrBolt buildSolrBolt() {
		String solrServerUlr = configs.getProperty(Keys.SOLR_SERVER);
		String collection = configs.getProperty(Keys.SOLR_COLLECTION);
		SolrBolt solrBolt = new SolrBolt(solrServerUlr + collection);
		return solrBolt;
	}

}

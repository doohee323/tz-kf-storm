package tzkfstorm.example4.bolt;

import java.util.Properties;

import tzkfstorm.example4.Keys;

/**
 */
public class BoltBuilder {

	public Properties configs = null;

	public BoltBuilder(Properties configs) {
		this.configs = configs;
	}

	public SplitLogBolt buildSplitLogBolt() {
		return new SplitLogBolt();
	}

	public CountBolt buildCountBolt() {
		return new CountBolt();
	}

	public ReportBolt buildReportBolt() {
		return new ReportBolt();
	}

	public SolrBolt buildSolrBolt() {
		String solrServerUlr = configs.getProperty(Keys.SOLR_SERVER);
		String collection = configs.getProperty(Keys.SOLR_COLLECTION);
		SolrBolt solrBolt = new SolrBolt(solrServerUlr + collection);
		return solrBolt;
	}

}
package tzkfstorm.example4.bolt;

import java.util.Properties;

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
		String solrServerUlr = configs.getProperty("solr.url");
		String collection = configs.getProperty("solr.collection");
		SolrBolt solrBolt = new SolrBolt(solrServerUlr + collection);
		return solrBolt;
	}

}

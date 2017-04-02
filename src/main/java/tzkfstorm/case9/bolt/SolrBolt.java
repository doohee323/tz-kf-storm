package tzkfstorm.case9.bolt;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

/**
 */
public class SolrBolt extends BaseRichBolt {

	private static final Log log = LogFactory.getLog(SolrBolt.class);
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	SolrClient solrClient;
	String solrAddress;

	/**
	 */
	public SolrBolt(String solrAddress) {
		this.solrAddress = solrAddress;
	}

	public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		this.solrClient = new HttpSolrClient(solrAddress);
	}

	public void execute(Tuple input) {
		try {
			SolrInputDocument document = getSolrInputDocumentForInput(input);
			if (!document.isEmpty()) {
				solrClient.add(document);
				solrClient.commit();
			}
			collector.ack(input);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 */
	public SolrInputDocument getSolrInputDocumentForInput(Tuple input) {
		String content = (String) input.getValueByField("content");
		System.out.println("Received in SOLR bolt->" + content);
		String[] parts = content.trim().split(" ");
		SolrInputDocument document = new SolrInputDocument();
		try {
			for (String part : parts) {
				String[] subParts = part.split(":");
				String fieldName = subParts[0];
				String value = subParts[1];
				document.addField(fieldName, value);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return document;
	}

	@Override
	public void cleanup() {
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
	}

}

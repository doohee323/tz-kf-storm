package tzkfstorm.case4.bolt;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import tzkfstorm.case4.Topology;

public class ReportBolt extends BaseRichBolt {

	private static final Log log = LogFactory.getLog(ReportBolt.class);
	private static final long serialVersionUID = 6102304822420418016L;

	private OutputCollector collector;

	@SuppressWarnings("rawtypes")
	public void prepare(Map stormConf, TopologyContext context, OutputCollector outCollector) {
		collector = outCollector;
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream(Topology.SOLR_STREAM, new Fields("reportBolt", "content"));
	}

	public void execute(Tuple tuple) {
		String id = tuple.getString(0);
		String hostname = tuple.getString(1);
		String count = Long.toString(tuple.getLong(2));
		String value = "id:" + id + " value:" + "{" + hostname + "|" + count + "}";
		collector.emit(Topology.SOLR_STREAM, new Values("solr", value));
		collector.ack(tuple);
		log.error("Emitted : " + hostname + "->" + count);
	}
}

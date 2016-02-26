package example4.tzkfstorm.bolt;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import example4.tzkfstorm.Topology;

/**
 * @author vishnu viswanath This class parses the incoming messages and decided
 *         which bolt the message has to be passed on to There are two cases in
 *         this example, first if of solr type and second is of hdfs type.
 */
public class SinkTypeBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;
	private OutputCollector collector;

	public void execute(Tuple tuple) {
		String value = tuple.getString(0);
		System.out.println("Received in SinkType bolt : " + value);
		int index = value.indexOf(" ");
		if (index == -1)
			return;
		String type = value.substring(0, index);
		System.out.println("Type : " + type);
		value = value.substring(index);
		if (type.equals("solr")) {
			collector.emit(Topology.SOLR_STREAM, new Values(type, value));
			System.out.println("Emitted : " + value);
		}
		collector.ack(tuple);
	}

	public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream(Topology.SOLR_STREAM, new Fields("sinkType", "content"));
	}

}

package tzkfstorm.example4.bolt;

import java.util.HashMap;
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
import tzkfstorm.example4.LogBean;

public class CountBolt extends BaseRichBolt {

	private static final long serialVersionUID = 6843364678084556655L;
	private static final Log log = LogFactory.getLog(CountBolt.class);
	private static final Long ONE = Long.valueOf(1);

	private OutputCollector collector;
	private Map<String, Long> countMap;

	@SuppressWarnings("rawtypes")
	public void prepare(Map stormConf, TopologyContext context, OutputCollector outCollector) {
		collector = outCollector;
		countMap = new HashMap<String, Long>();
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("id", "hostname", "count"));
	}

	public void execute(Tuple tuple) {
		LogBean logBean = (LogBean) tuple.getValue(0);
		String id = logBean.getId();
		Long cnt = countMap.get(logBean.getHostname());
		if (cnt == null) {
			cnt = ONE;
		} else {
			cnt++;
		}
		countMap.put(logBean.getHostname(), cnt);
		collector.emit(tuple, new Values(id, logBean.getHostname(), cnt));
		collector.ack(tuple);
	}
}

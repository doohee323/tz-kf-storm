package example4.tzstorm.bolt;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import example4.tzstorm.LogBean;

public class HostnameCountBolt extends BaseRichBolt {

    private static final long serialVersionUID = 6843364678084556655L;
    private static final Long ONE = Long.valueOf(1);

    private OutputCollector collector;
    private Map<String, Long> countMap;

    @SuppressWarnings("rawtypes")
    public void prepare(Map stormConf, TopologyContext context, OutputCollector outCollector) {
        collector = outCollector;
        countMap = new HashMap<String, Long>();
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("hostname", "count"));
    }

    public void execute(Tuple tuple) {
        LogBean logbean = (LogBean) tuple.getValue(0);
        Long cnt = countMap.get(logbean.getHostname());
        if (cnt == null) {
            cnt = ONE;
        } else {
            cnt++;
        }
        countMap.put(logbean.getHostname(), cnt);
        collector.emit(tuple, new Values(logbean.getHostname(), cnt));
        // collector.ack(tuple);
    }
}

package example4.tzstorm.bolt;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import example4.tzstorm.LogBean;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class SplitLogBolt extends BaseRichBolt {

    private static final long serialVersionUID = 3092938699134129356L;

    private OutputCollector collector;

    @SuppressWarnings("rawtypes")
    public void prepare(Map cfg, TopologyContext topologyCtx, OutputCollector outCollector) {
        collector = outCollector;
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("hostname"));
    }

    public void execute(Tuple tuple) {
        Object value = tuple.getValue(0);
        String Log = null;
        LogBean logBean = null;
        if (value instanceof String) {
            Log = (String) value;
        } else {
            // Kafka returns bytes
            byte[] bytes = (byte[]) value;
            try {
                Log = new String(bytes, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        logBean = LogBean.parse(Log);

        collector.emit(tuple, new Values(logBean));
        // collector.ack(tuple);
    }
}

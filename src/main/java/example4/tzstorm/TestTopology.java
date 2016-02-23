package example4.tzstorm;

import java.util.Arrays;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import example4.tzstorm.bolt.HostnameCountBolt;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;

public class TestTopology {

	private static final String zkHost = "localhost";
    private static final String zkHostPort = "localhost:2181";
//  private static final String stormNimbusHost = "127.0.0.1";
  private static final String TOPIC = "logs";

  private static final String SPOUT_ID = "testSpout4";
  private static final String SPLIT_BOLT_ID = "split-bolt";
  private static final String COUNT_BOLT_ID = "count-bolt";
  private static final String REPORT_BOLT_ID = "report-bolt";
  private static final String TOPOLOGY_ID = "TestTopology4";
  
	public static void main(String[] args) {

		String nimbusHost = "192.168.59.103";

		KafkaSpout kafkaSpout = buildKafkaSpout();

		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("eventsEmitter", kafkaSpout, 8);
		HostnameCountBolt countBolt = new HostnameCountBolt();
		builder.setBolt(COUNT_BOLT_ID, countBolt).fieldsGrouping(SPLIT_BOLT_ID, new Fields("hostname"));

		// More bolts stuffzz
		Config config = new Config();
		config.setMaxTaskParallelism(5);
		config.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 2);
		config.put(Config.NIMBUS_HOST, nimbusHost);
		config.put(Config.NIMBUS_THRIFT_PORT, 6627);
		config.put(Config.STORM_ZOOKEEPER_PORT, 2181);
		config.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(zkHost));

		try {
			StormSubmitter.submitTopology("my-topology", config, builder.createTopology());
		} catch (Exception e) {
			throw new IllegalStateException("Couldn't initialize the topology", e);
		}


	}
	
	private static KafkaSpout buildKafkaSpout() {
        String zkRoot = "/kafka-log-spout";
        String zkSpoutId = "log-spout";
        ZkHosts zkHosts = new ZkHosts(zkHostPort);

        SpoutConfig spoutCfg = new SpoutConfig(zkHosts, TOPIC, zkRoot, zkSpoutId);
        KafkaSpout kafkaSpout = new KafkaSpout(spoutCfg);
        return kafkaSpout;
    }	

}
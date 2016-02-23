package example4.tzstorm;

import java.util.Arrays;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import example4.tzstorm.bolt.HostnameCountBolt;
import example4.tzstorm.bolt.ReportBolt;
import example4.tzstorm.bolt.SplitLogBolt;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;

public class TestTopology4 {

	private static final String zkHost = "localhost";
	private static final String zkHostPort = "localhost:2181";
	private static final String stormNimbusHost = "127.0.0.1";
	private static final String TOPIC = "logs";

	private static final String SPOUT_ID = "testSpout4";
	private static final String SPLIT_BOLT_ID = "split-bolt";
	private static final String COUNT_BOLT_ID = "count-bolt";
	private static final String REPORT_BOLT_ID = "report-bolt";
	private static final String TOPOLOGY_ID = "TestTopology4";

	public static void main(String[] args) throws Exception {
		String runType = System.getProperty("runType", "local");

		KafkaSpout spout = buildKafkaSpout();
		SplitLogBolt splitBolt = new SplitLogBolt();
		HostnameCountBolt countBolt = new HostnameCountBolt();
		ReportBolt reportBolt = new ReportBolt();

		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout(SPOUT_ID, spout);
		builder.setBolt(SPLIT_BOLT_ID, splitBolt).shuffleGrouping(SPOUT_ID);
		builder.setBolt(COUNT_BOLT_ID, countBolt).fieldsGrouping(SPLIT_BOLT_ID, new Fields("hostname"));
		builder.setBolt(REPORT_BOLT_ID, reportBolt).globalGrouping(COUNT_BOLT_ID);

		Config cfg = new Config();
//		runType = "storm";
		if (runType.equals("local")) {
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(TOPOLOGY_ID, cfg, builder.createTopology());
			Utils.sleep(10 * 100000);
			cluster.killTopology(TOPOLOGY_ID);
			cluster.shutdown();
		} else if (runType.equals("storm")) {
			Config config = new Config();
			config.setMaxTaskParallelism(5);
			config.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 2);
			config.put(Config.NIMBUS_HOST, stormNimbusHost);
			config.put(Config.NIMBUS_THRIFT_PORT, 6627);
			config.put(Config.STORM_ZOOKEEPER_PORT, 2181);
			config.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(zkHost));

			try {
				StormSubmitter.submitTopology(TOPOLOGY_ID, cfg, builder.createTopology());
			} catch (Exception e) {
				throw new IllegalStateException("Couldn't initialize the topology", e);
			}
			// } else if (runType.equals("kafka")) {
			// Map<String, String> storm_conf = Utils.readStormConfig();
			// storm_conf.put("nimbus.host", stormNimbusHost);
			// Client client =
			// NimbusClient.getConfiguredClient(storm_conf).getClient();
			// String inputJar =
			// "/Users/dhong/Documents/workspace/java/kstorm/target/kstorm-1.0-SNAPSHOT-jar-with-dependencies.jar";
			// StormSubmitter.submitJar(storm_conf, inputJar);
			// String jsonConf = JSONValue.toJSONString(storm_conf);
			// client.submitTopology(TOPOLOGY_ID, inputJar, jsonConf,
			// builder.createTopology());
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

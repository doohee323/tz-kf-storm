package tzkfstorm.case9;

import java.util.Properties;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import storm.kafka.KafkaSpout;
import tzkfstorm.case9.bolt.BoltBuilder;
import tzkfstorm.case9.bolt.EsperBolt;
import tzkfstorm.case9.bolt.SolrBolt;
import tzkfstorm.case9.spout.SpoutBuilder;

/**
 */
public class Topology {

	private Properties configs;
	public BoltBuilder boltBuilder;
	public SpoutBuilder spoutBuilder;
	public static final String SOLR_STREAM = "solr-stream";

	public Topology(String configFile) throws Exception {
		configs = new Properties();
		try {
			configs.load(Topology.class.getResourceAsStream("/case9.properties"));
			boltBuilder = new BoltBuilder(configs);
			spoutBuilder = new SpoutBuilder(configs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}
	}

	private void submitTopology() throws Exception {
		TopologyBuilder builder = new TopologyBuilder();
		KafkaSpout kafkaSpout = spoutBuilder.buildKafkaSpout();
		EsperBolt esperBolt = boltBuilder.buildEsperBolt();
		SolrBolt solrBolt = boltBuilder.buildSolrBolt();

		int kafkaSpoutCount = Integer.parseInt(configs.getProperty("kafkaspout.count"));
		builder.setSpout(configs.getProperty("kafka-spout"), kafkaSpout, kafkaSpoutCount);

		int esperBoltCount = Integer.parseInt(configs.getProperty("esper-bolt.count"));
		builder.setBolt(configs.getProperty("esper-bolt"), esperBolt, esperBoltCount)
				.shuffleGrouping(configs.getProperty("kafka-spout"));

		int solrBoltCount = Integer.parseInt(configs.getProperty("solrbolt.count"));
		builder.setBolt(configs.getProperty("solr-bolt"), solrBolt, solrBoltCount)
				.shuffleGrouping(configs.getProperty("esper-bolt"), SOLR_STREAM);

		Config conf = new Config();
		conf.put("solr.zookeeper.hosts", configs.getProperty("solr.zookeeper.hosts"));
		String topologyName = configs.getProperty("topology");
		conf.setNumWorkers(1);

		try {
//			String runType = System.getProperty("runType", "local");
//			if (runType != null && runType.equals("local")) {
//				LocalCluster cluster = new LocalCluster();
//				cluster.submitTopology(topologyName, conf, builder.createTopology());
//				Utils.sleep(10 * 100000);
//				cluster.killTopology(topologyName);
//				cluster.shutdown();
//			} else {
				StormSubmitter.submitTopology(topologyName, conf, builder.createTopology());
//			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		String configFile;
		if (args.length == 0) {
			configFile = "case9.properties";
		} else {
			configFile = args[0];
		}
		Topology ingestionTopology = new Topology(configFile);
		ingestionTopology.submitTopology();
	}
}

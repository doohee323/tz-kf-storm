package tzkfstorm.example4;

import java.util.Properties;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import storm.kafka.KafkaSpout;
import tzkfstorm.example4.bolt.BoltBuilder;
import tzkfstorm.example4.bolt.CountBolt;
import tzkfstorm.example4.bolt.ReportBolt;
import tzkfstorm.example4.bolt.SolrBolt;
import tzkfstorm.example4.bolt.SplitLogBolt;
import tzkfstorm.example4.spout.SpoutBuilder;

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
			configs.load(Topology.class.getResourceAsStream("/example4.properties"));
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
		SplitLogBolt splitlogBolt = boltBuilder.buildSplitLogBolt();
		CountBolt countBolt = boltBuilder.buildCountBolt();
		ReportBolt reportBolt = boltBuilder.buildReportBolt();
		SolrBolt solrBolt = boltBuilder.buildSolrBolt();

		int kafkaSpoutCount = Integer.parseInt(configs.getProperty("kafkaspout.count"));
		builder.setSpout(configs.getProperty("kafka-spout"), kafkaSpout, kafkaSpoutCount);

		int splitBoltCount = Integer.parseInt(configs.getProperty("split-bolt.count"));
		builder.setBolt(configs.getProperty("split-bolt"), splitlogBolt, splitBoltCount)
				.shuffleGrouping(configs.getProperty("kafka-spout"));

		int countBoltCount = Integer.parseInt(configs.getProperty("count-bolt.count"));
		builder.setBolt(configs.getProperty("count-bolt"), countBolt, countBoltCount)
				.fieldsGrouping(configs.getProperty("split-bolt"), new Fields("hostname"));

		int reportBoltCount = Integer.parseInt(configs.getProperty("report-bolt.count"));
		builder.setBolt(configs.getProperty("report-bolt"), reportBolt, reportBoltCount)
				.globalGrouping(configs.getProperty("count-bolt"));

		int solrBoltCount = Integer.parseInt(configs.getProperty("solrbolt.count"));
		builder.setBolt(configs.getProperty("solr-bolt"), solrBolt, solrBoltCount)
				.shuffleGrouping(configs.getProperty("report-bolt"), SOLR_STREAM);

		Config conf = new Config();
		conf.put("solr.zookeeper.hosts", configs.getProperty("solr.zookeeper.hosts"));
		String topologyName = configs.getProperty("topology");
		conf.setNumWorkers(1);

		try {
			String runType = System.getProperty("runType", "local");
			if (runType != null && runType.equals("local")) {
				LocalCluster cluster = new LocalCluster();
				cluster.submitTopology(topologyName, conf, builder.createTopology());
				Utils.sleep(10 * 100000);
				cluster.killTopology(topologyName);
				cluster.shutdown();
			} else {
				StormSubmitter.submitTopology(topologyName, conf, builder.createTopology());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			StormSubmitter.submitTopology(topologyName, conf, builder.createTopology());
		}
	}

	public static void main(String[] args) throws Exception {
		String configFile;
		if (args.length == 0) {
			configFile = "example4.properties";
		} else {
			configFile = args[0];
		}
		Topology ingestionTopology = new Topology(configFile);
		ingestionTopology.submitTopology();
	}
}

package example4.tzkfstorm;

import java.util.Properties;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import example4.tzkfstorm.bolt.BoltBuilder;
import example4.tzkfstorm.bolt.CountBolt;
import example4.tzkfstorm.bolt.ReportBolt;
import example4.tzkfstorm.bolt.SolrBolt;
import example4.tzkfstorm.bolt.SplitLogBolt;
import example4.tzkfstorm.spout.SpoutBuilder;
import storm.kafka.KafkaSpout;

/**
 */
public class Topology {

	public Properties configs;
	public BoltBuilder boltBuilder;
	public SpoutBuilder spoutBuilder;
	public static final String SOLR_STREAM = "solr-stream";

	public Topology(String configFile) throws Exception {
		configs = new Properties();
		try {
			configs.load(Topology.class.getResourceAsStream("/default_config.properties"));
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

		int kafkaSpoutCount = Integer.parseInt(configs.getProperty(Keys.KAFKA_SPOUT_COUNT));
		builder.setSpout(configs.getProperty(Keys.KAFKA_SPOUT_ID), kafkaSpout, kafkaSpoutCount);

		int splitBoltCount = Integer.parseInt(configs.getProperty(Keys.SPLIT_BOLT_COUNT));
		builder.setBolt(configs.getProperty(Keys.SPLIT_BOLT_ID), splitlogBolt, splitBoltCount)
				.shuffleGrouping(configs.getProperty(Keys.KAFKA_SPOUT_ID));

		int countBoltCount = Integer.parseInt(configs.getProperty(Keys.COUNT_BOLT_COUNT));
		builder.setBolt(configs.getProperty(Keys.COUNT_BOLT_ID), countBolt, countBoltCount)
				.fieldsGrouping(configs.getProperty(Keys.SPLIT_BOLT_ID), new Fields("hostname"));

		int reportBoltCount = Integer.parseInt(configs.getProperty(Keys.REPORT_BOLT_COUNT));
		builder.setBolt(configs.getProperty(Keys.REPORT_BOLT_ID), reportBolt, reportBoltCount)
				.globalGrouping(configs.getProperty(Keys.COUNT_BOLT_ID));

		int solrBoltCount = Integer.parseInt(configs.getProperty(Keys.SOLR_BOLT_COUNT));
		builder.setBolt(configs.getProperty(Keys.SOLR_BOLT_ID), solrBolt, solrBoltCount)
				.shuffleGrouping(configs.getProperty(Keys.REPORT_BOLT_ID), SOLR_STREAM);

		Config conf = new Config();
		conf.put("solr.zookeeper.hosts", configs.getProperty(Keys.SOLR_ZOOKEEPER_HOSTS));
		String topologyName = configs.getProperty(Keys.TOPOLOGY_NAME);
		conf.setNumWorkers(1);

		try {
			// String runType = System.getProperty("runType", "local");
			// if (runType != null && runType.equals("local")) {
			// LocalCluster cluster = new LocalCluster();
			// cluster.submitTopology(topologyName, conf,
			// builder.createTopology());
			// Utils.sleep(10 * 100000);
			// cluster.killTopology(topologyName);
			// cluster.shutdown();
			// } else {
			StormSubmitter.submitTopology(topologyName, conf, builder.createTopology());
			// }
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}
	}

	public static void main(String[] args) throws Exception {
		String configFile;
		if (args.length == 0) {
			configFile = "default_config.properties";
		} else {
			configFile = args[0];
		}
		Topology ingestionTopology = new Topology(configFile);
		ingestionTopology.submitTopology();
	}
}

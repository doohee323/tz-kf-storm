package tzkfstorm.example7;

import java.util.Properties;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import storm.kafka.KafkaSpout;
import tzkfstorm.example7.bolt.BoltBuilder;
import tzkfstorm.example7.bolt.EsperBolt;
import tzkfstorm.example7.bolt.SolrBolt;
import tzkfstorm.example7.spout.SpoutBuilder;

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
			configs.load(Topology.class.getResourceAsStream("/example7.properties"));
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

		int kafkaSpoutCount = Integer.parseInt(configs.getProperty(Keys.KAFKA_SPOUT_COUNT));
		builder.setSpout(configs.getProperty(Keys.KAFKA_SPOUT_ID), kafkaSpout, kafkaSpoutCount);

		int esperBoltCount = Integer.parseInt(configs.getProperty(Keys.ESPER_BOLT_COUNT));
		builder.setBolt(configs.getProperty(Keys.ESPER_BOLT_ID), esperBolt, esperBoltCount)
				.shuffleGrouping(configs.getProperty(Keys.KAFKA_SPOUT_ID));

		int solrBoltCount = Integer.parseInt(configs.getProperty(Keys.SOLR_BOLT_COUNT));
		builder.setBolt(configs.getProperty(Keys.SOLR_BOLT_ID), solrBolt, solrBoltCount)
				.shuffleGrouping(configs.getProperty(Keys.ESPER_BOLT_ID), SOLR_STREAM);

		Config conf = new Config();
		conf.put("solr.zookeeper.hosts", configs.getProperty(Keys.SOLR_ZOOKEEPER_HOSTS));
		String topologyName = configs.getProperty(Keys.TOPOLOGY_NAME);
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
			configFile = "example7.properties";
		} else {
			configFile = args[0];
		}
		Topology ingestionTopology = new Topology(configFile);
		ingestionTopology.submitTopology();
	}
}

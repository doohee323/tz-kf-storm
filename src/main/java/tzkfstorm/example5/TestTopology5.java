package tzkfstorm.example5;

import java.util.Properties;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import storm.trident.TridentTopology;
import tzkfstorm.example4.Keys;
import tzkfstorm.example4.bolt.BoltBuilder;
import tzkfstorm.example4.bolt.SolrBolt;
import tzkfstorm.example4.spout.SpoutBuilder;
import tzkfstorm.example5.bolt.EsperBolt;
import tzkfstorm.example5.operator.DistinctStateFactory;
import tzkfstorm.example5.operator.DistinctStateUpdater;
import tzkfstorm.example5.spout.TridentSpout;

public class TestTopology5 {

	private static final String TOPOLOGY_ID = "TestTopology5";
	private Properties configs;
	public BoltBuilder boltBuilder;
	public SpoutBuilder spoutBuilder;
	public static final String SOLR_STREAM = "solr-stream";

	public TestTopology5(String configFile) throws Exception {
		configs = new Properties();
		try {
			configs.load(TestTopology5.class.getResourceAsStream("/example5.properties"));
			boltBuilder = new BoltBuilder(configs);
			spoutBuilder = new SpoutBuilder(configs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}
	}

	private void submitTopology() throws Exception {
		SolrBolt solrBolt = boltBuilder.buildSolrBolt();
		
		TopologyBuilder builder = new TopologyBuilder();
		TridentTopology topology = new TridentTopology();
		topology.newStream("log", new TridentSpout())
				.partitionPersist(new DistinctStateFactory(), new Fields("logstring"), new DistinctStateUpdater(),
						new Fields("logstring2"))
				.newValuesStream().each(new Fields("logstring2"), new EsperBolt(), new Fields("logstring3"));

		int solrBoltCount = Integer.parseInt(configs.getProperty(Keys.SOLR_BOLT_COUNT));
		builder.setBolt(configs.getProperty(Keys.SOLR_BOLT_ID), solrBolt, solrBoltCount)
				.shuffleGrouping(configs.getProperty(Keys.REPORT_BOLT_ID), SOLR_STREAM);

		StormTopology stormTopology = topology.build();

		Config conf = new Config();
		String runType = System.getProperty("runType", "local");
		if (runType.equals("local")) {
			conf.setDebug(true);
			LocalCluster cluster = new LocalCluster();

			cluster.submitTopology(TOPOLOGY_ID, conf, stormTopology);
			Utils.sleep(1000000);
			cluster.killTopology(TOPOLOGY_ID);
			cluster.shutdown();
		} else {
			conf.setNumWorkers(5);
			try {
				StormSubmitter.submitTopology(TOPOLOGY_ID, conf, builder.createTopology());
			} catch (Exception ae) {
				System.out.println(ae);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		String configFile;
		if (args.length == 0) {
			configFile = "example5.properties";
		} else {
			configFile = args[0];
		}
		TestTopology5 ingestionTopology = new TestTopology5(configFile);
		ingestionTopology.submitTopology();
	}
}

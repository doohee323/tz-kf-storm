package tzkfstorm.example5;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import storm.trident.TridentTopology;
import tzkfstorm.example5.bolt.EsperBolt;
import tzkfstorm.example5.operator.DistinctStateFactory;
import tzkfstorm.example5.operator.DistinctStateUpdater;
import tzkfstorm.example5.spout.TestSpout5;

public class TestTopology5 {

    private static final String TOPOLOGY_ID = "TestTopology5";

    public static void main(String args[]) {
        TridentTopology topology = new TridentTopology();
        topology.newStream("log", new TestSpout5())
                .partitionPersist(new DistinctStateFactory(), new Fields("logstring"), new DistinctStateUpdater(),
                        new Fields("logstring2")).newValuesStream()
                .each(new Fields("logstring2"), new EsperBolt(), new Fields("logstring3"));
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
//            	StormSubmitter.submitTopology(TOPOLOGY_ID, conf, builder.createTopology());
//                StormSubmitter.submitTopology(args[0], conf, stormTopology);
            } catch (Exception ae) {
                System.out.println(ae);
            }
        }
    }
}

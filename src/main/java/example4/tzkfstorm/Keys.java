package example4.tzkfstorm;

/**
 */
public class Keys {

	public static final String TOPOLOGY_NAME = "topology";

	// kafka spout
	public static final String KAFKA_SPOUT_ID = "kafka-spout";
	public static final String KAFKA_ZOOKEEPER = "kafka.zookeeper";
	public static final String KAFKA_TOPIC = "kafa.topic";
	public static final String KAFKA_ZKROOT = "kafka.zkRoot";
	public static final String KAFKA_CONSUMERGROUP = "kafka.consumer.group";
	public static final String KAFKA_SPOUT_COUNT = "kafkaspout.count";

	//sink bolt
	public static final String SINK_TYPE_BOLT_ID = "sink-type-bolt";
	public static final String SINK_BOLT_COUNT = "sinkbolt.count";
	
	// split-split
	public static final String SPLIT_BOLT_ID = "split-bolt";
	public static final String SPLIT_BOLT_COUNT = "split-bolt.count";
	// count-split
	public static final String COUNT_BOLT_ID = "count-bolt";
	public static final String COUNT_BOLT_COUNT = "count-bolt.count";
	// report-split
	public static final String REPORT_BOLT_ID = "report-bolt";
	public static final String REPORT_BOLT_COUNT = "report-bolt.count";

	// solr bolt
	public static final String SOLR_BOLT_ID = "solr-bolt";
	public static final String SOLR_BOLT_COUNT = "solrbolt.count";
	public static final String SOLR_COLLECTION = "solr.collection";
	public static final String SOLR_SERVER = "solr.url";
	public static final String SOLR_ZOOKEEPER_HOSTS = "solr.zookeeper.hosts";

}

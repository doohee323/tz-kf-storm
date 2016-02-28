package tzkfstorm.example4.spout;

import java.util.Properties;

import backtype.storm.spout.SchemeAsMultiScheme;
import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

/**
 *
 */
public class SpoutBuilder {

	public Properties configs = null;

	public SpoutBuilder(Properties configs) {
		this.configs = configs;
	}

	public KafkaSpout buildKafkaSpout() {
		BrokerHosts hosts = new ZkHosts(configs.getProperty("kafka.zookeeper"));
		String topic = configs.getProperty("kafa.topic");
		String zkRoot = configs.getProperty("kafka.zkRoot");
		String groupId = configs.getProperty("kafka.consumer.group");
		SpoutConfig spoutConfig = new SpoutConfig(hosts, topic, zkRoot, groupId);
		spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);
		return kafkaSpout;
	}
}

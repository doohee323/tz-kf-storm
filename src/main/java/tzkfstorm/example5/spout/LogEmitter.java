package tzkfstorm.example5.spout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.ITridentSpout.Emitter;
import storm.trident.topology.TransactionAttempt;

public class LogEmitter implements Emitter<Long> {

	private static final Logger log = LoggerFactory.getLogger(LogEmitter.class);
	public List<String> logData = new ArrayList<String>();

	private static final String TOPIC = "logs";
	private static final int NUM_THREADS = 20;
	private static final String ZOOKEEPER_HOST_PORT = "localhost:2181";

	ConsumerConnector consumer = null;
	Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = null;
	ExecutorService executor = null;

	public LogEmitter() {
		Properties props = new Properties();
		props.put("group.id", "sample_group");
		props.put("zookeeper.connect", ZOOKEEPER_HOST_PORT);

		props.put("zookeeper.session.timeout.ms", "6000");
		props.put("zookeeper.connectiontimeout.ms", "12000");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");
		props.put("auto.offset.reset", "smallest");

		ConsumerConfig consumerConfig = new ConsumerConfig(props);
		consumer = Consumer.createJavaConsumerConnector(consumerConfig);
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(TOPIC, NUM_THREADS);
		consumerMap = consumer.createMessageStreams(topicCountMap);
		executor = Executors.newFixedThreadPool(NUM_THREADS);
		// consumer.shutdown();
	}

	public void emitBatch(TransactionAttempt tx, Long coordinatorMeta, TridentCollector collector) {
		log.debug("Emitter.emitBatch({}, {}, collector)", tx, coordinatorMeta);
		this.prepareData();
		for (String log : logData) {
			List<Object> oneTuple = Arrays.<Object> asList(log);
			collector.emit(oneTuple);
		}
	}

	private void prepareData() {
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(TOPIC);
		for (final KafkaStream<byte[], byte[]> stream : streams) {
			executor.execute(new Runnable() {
				public void run() {
					for (MessageAndMetadata<byte[], byte[]> messageAndMetadata : stream) {
						System.out.println(new String(messageAndMetadata.message()));
						logData.add(new String(messageAndMetadata.message()));
					}
				}
			});
		}

	}

	public void success(TransactionAttempt tx) {
		log.debug("Emitter.success({})", tx);
	}

	public void close() {
		log.debug("Emitter.close()");
		consumer.shutdown();
		executor.shutdown();
	}

}
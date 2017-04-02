package tzkfstorm.case4.kafka;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import backtype.storm.utils.Utils;

public class ProducerTestKafka {

	private static final Log log = LogFactory.getLog(ProducerTestKafka.class);

//	private static final String KAFKA_HOST_PORT = "192.168.82.150:9092"; // to vagrant
	private static final String KAFKA_HOST_PORT = "localhost:9092";	// to local
	private static final String TOPIC = "logs";

	private static String LOG_FILENAME = "resources/data/a.txt";
	private List<Object> logData = new ArrayList<Object>();

	private Producer<Object, String> kproducer;

	public static void main(String[] args) {
		ProducerTestKafka source = new ProducerTestKafka();
		source.init();
		source.populate();
		source.close();
	}

	private void init() {
		Properties props = new Properties();
		props.put("metadata.broker.list", KAFKA_HOST_PORT);
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("request.required.acks", "1");

		ProducerConfig config = new ProducerConfig(props);
		kproducer = new Producer<Object, String>(config);

		this.prepareData();
	}

	private void populate() {
		for (int i = 0; i < this.logData.size(); i++) {
			String input = this.logData.get(i).toString();
			System.out.println("input" + i + "->" + input);
			kproducer.send(new KeyedMessage<Object, String>(TOPIC, input));
		}
		Utils.sleep(500);
	}

	private void close() {
		kproducer.close();
	}

	/**
	 * 
	 * @throws Exception
	 */
	private void prepareData() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(LOG_FILENAME));
			String oneLine = br.readLine();
			if (oneLine == null) {
				br.close();
				throw new Exception("!!!");
			}
			int totalLines = 0;
			while (oneLine != null) {
				totalLines++;
				logData.add(oneLine);
				oneLine = br.readLine();
			}
			log.error("Lines in ten second log: " + totalLines);
			br.close();
		} catch (Exception e) {
			e.getStackTrace();
		}
	}
}

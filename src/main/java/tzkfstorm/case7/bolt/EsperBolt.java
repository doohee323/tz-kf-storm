package tzkfstorm.case7.bolt;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.google.gson.Gson;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import tzkfstorm.case4.Topology;
import tzkfstorm.case7.LogBean;

public class EsperBolt extends BaseRichBolt {

	private static final long serialVersionUID = 6843364678084556655L;
	private static final Log log = LogFactory.getLog(EsperBolt.class);

	private Properties configs = null;
	private static final Gson gson = new Gson();
	private OutputCollector collector;

	public EsperBolt(Properties configs) {
		this.configs = configs;
	}

	private EPServiceProvider epService;

	public void prepare(Map stormConf, TopologyContext context, OutputCollector outCollector) {
		collector = outCollector;
		epService = setUpEsper(epService);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream(Topology.SOLR_STREAM, new Fields("reportBolt", "content"));
	}

	public void execute(Tuple tuple) {
		try {
			List<Object> values = tuple.getValues();
			LogBean logBean = gson.fromJson((String) values.get(0), LogBean.class);
			epService.getEPRuntime().sendEvent(logBean);
		} catch (Exception e) {
			log.error("esper execute:" + e);
		}
		collector.ack(tuple);
	}

	public EPServiceProvider setUpEsper(EPServiceProvider epService) {
		Configuration configuration = new Configuration();
		configuration.addEventType("Log", LogBean.class.getName());

		epService = EPServiceProviderManager.getDefaultProvider(configuration);
		epService.initialize();

		String qeury = configs.getProperty("esper-query1");
		EPStatement statement = epService.getEPAdministrator().createEPL(qeury);

		statement.addListener(new UpdateListener() {
			public void update(EventBean[] newEvents, EventBean[] oldEvents) {
				if (newEvents != null) {
					for (EventBean e : newEvents) {
						String id = e.get("id").toString();
						String hostname = e.get("hostname").toString();
						String count = "1";
						String value = "id:" + id + " value:" + "{" + hostname + "|" + count + "}";
						collector.emit(Topology.SOLR_STREAM, new Values("solr", value));
						log.error("esper emitted:" + value);
					}
				}
			}
		});

		String qeury2 = configs.getProperty("esper-query2");
		EPStatement statement2 = epService.getEPAdministrator().createEPL(qeury2);

		statement2.addListener(new UpdateListener() {
			public void update(EventBean[] newEvents, EventBean[] oldEvents) {
				if (newEvents != null) {
					for (EventBean e : newEvents) {
						String id = "COUNT";
						String hostname = e.get("hostname").toString();
						String count = e.get("count").toString();
						String value = "id:" + id + " value:" + "{" + hostname + "|" + count + "}";
						collector.emit(Topology.SOLR_STREAM, new Values("solr", value));
						log.error("esper emitted:" + value);
					}
				}
			}
		});
		return epService;
	}
}

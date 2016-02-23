package example5.tzstorm.bolt;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.Function;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;
import backtype.storm.task.OutputCollector;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.google.gson.Gson;

import example5.tzstorm.LogBean;

@SuppressWarnings("rawtypes")
public class TestBolt5 implements Function {

    private static final long serialVersionUID = 1L;
    static final Logger log = LoggerFactory.getLogger(TestBolt5.class);
    private int partitionIndex;
    
    private static final Gson gson = new Gson();

    private EPServiceProvider epService;

    public void prepare(Map conf, TridentOperationContext context) {
        log.info("CountSumFunction.prepare(): partition[{}/{}]", context.getPartitionIndex(), context.numPartitions());
        partitionIndex = context.getPartitionIndex();
        this.setUpEsper();
    }

    public void cleanup() {
    }

    public void execute(TridentTuple tuple, TridentCollector collector) {
        List<Object> values = tuple.getValues();
        LogBean logBean = gson.fromJson((String)values.get(0), LogBean.class);
        epService.getEPRuntime().sendEvent(logBean);
    }

    private void setUpEsper() {
        Configuration configuration = new Configuration();
        configuration.addEventType("Log", LogBean.class.getName());

        epService = EPServiceProviderManager.getDefaultProvider(configuration);
        epService.initialize();

        StringBuffer qeury = new StringBuffer();
        // qeury.append("SELECT COUNT(Log.hostname) AS total, ");
        // qeury.append("Log.hostname AS hostname ");
        // qeury.append("FROM Log.win:time(10 SECOND) ");
        // // qeury.append("WHERE Log.hostname = \"ruleset32.xdn.com\" ");
        // qeury.append("GROUP BY Log.hostname ");
        // qeury.append("OUTPUT SNAPSHOT EVERY 2 SEC ");

        qeury.append("select * from Log t where t.hostname = \"ruleset32.xdn.com\" ");
        EPStatement statement = epService.getEPAdministrator().createEPL(qeury.toString());

        statement.addListener(new UpdateListener() {
            public void update(EventBean[] arg0, EventBean[] arg1) {
                if (arg0 != null) {
                    for (EventBean e : arg0) {
                        // log.error("log count for each " + e.get("hostname")
                        // + ": " + e.get("total"));
                        log.error("log -> " + e.get("timestamp") + ": " + e.get("hostname"));
                    }
                }
            }
        });
    }

}

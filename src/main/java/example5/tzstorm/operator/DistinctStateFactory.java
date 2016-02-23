package example5.tzstorm.operator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import storm.trident.state.State;
import storm.trident.state.StateFactory;
import backtype.storm.task.IMetricsContext;

public class DistinctStateFactory implements StateFactory {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("rawtypes")
    public State makeState(Map conf, IMetricsContext metrics, int partitionIndex, int numPartitions) {
        return new DistinctState();
    }
}

class DistinctState implements State {
    private Map<String, String> map = new ConcurrentHashMap<String, String>();
    private Map<String, String> temp = new ConcurrentHashMap<String, String>();

    public void beginCommit(Long txid) {
        temp.clear();
    }

    public void commit(Long txid) {
        for (Map.Entry<String, String> entry : temp.entrySet())
            map.put(entry.getKey(), entry.getValue());
    }

    public boolean hasKey(String key) {
        return map.containsKey(key) || temp.containsKey(key);
    }

    public void put(String key, String value) {
        temp.put(key, value);
    }
}

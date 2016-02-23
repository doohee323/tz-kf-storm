package example5.tzstorm.spout;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.TridentCollector;
import storm.trident.spout.ITridentSpout.Emitter;
import storm.trident.topology.TransactionAttempt;

public class LogEmitter implements Emitter<Long> {
    private static final Logger log = LoggerFactory.getLogger(LogEmitter.class);

    private static String LOG_FILENAME = "data/a.txt";
    private List<String> logData = new ArrayList<String>();

    public void emitBatch(TransactionAttempt tx, Long coordinatorMeta, TridentCollector collector) {
        log.debug("Emitter.emitBatch({}, {}, collector)", tx, coordinatorMeta);
        this.prepareData();
        for (String log : logData) {
            List<Object> oneTuple = Arrays.<Object> asList(log);
            collector.emit(oneTuple);
        }
    }

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
            log.debug("Lines in ten second log: " + totalLines);
            br.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void success(TransactionAttempt tx) {
        log.debug("Emitter.success({})", tx);
    }

    public void close() {
        log.debug("Emitter.close()");
    }

}
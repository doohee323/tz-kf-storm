package tzkfstorm.example5.spout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.spout.ITridentSpout.BatchCoordinator;

public class LogBatchCoordinator implements BatchCoordinator<Long> {
    private static final Logger log = LoggerFactory.getLogger(LogBatchCoordinator.class);

    public Long initializeTransaction(long txid, Long prevMetadata, Long currMetadata) {
        log.info("BatchCoordinator.initializeTransaction({}, {}, {})",
                new Object[] { txid, prevMetadata, currMetadata });
        if (prevMetadata == null) {
            return 1L;
        }
        return prevMetadata + 1L;
    }

    public void success(long txid) {
        log.info("BatchCoordinator.success(txid={})", txid);
    }

    public boolean isReady(long txid) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        log.info("BatchCoordinator.isReady({})", new Object[] { txid });
        return true;
    }

    public void close() {
        log.info("BatchCoordinator.close()");
    }
}
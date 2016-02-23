package example5.tzstorm.operator;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.tuple.TridentTuple;

@SuppressWarnings("serial")
public class DistinctStateUpdater extends BaseStateUpdater<DistinctState> {
    public void updateState(DistinctState state, List<TridentTuple> tuples, TridentCollector collector) {
        List<TridentTuple> newEntries = new ArrayList<TridentTuple>();
        for (TridentTuple t : tuples) {
            String logstr = (String) t.get(0);
            String key = hash(logstr);
            if (!state.hasKey(key)) {
                state.put(key, logstr);
                newEntries.add(t);
            }
        }
        for (TridentTuple t : newEntries) {
            collector.emit(Arrays.asList(t.get(0)));
        }
    }

    public static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(16);

            while (md5.length() < 32) {
                md5 = "0" + md5;
            }

            return md5;
        } catch (NoSuchAlgorithmException e) {

        }
        return null;
    }
}

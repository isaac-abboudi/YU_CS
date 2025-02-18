import edu.yu.introtoalgs.BTKeysAtSameLevel2;

import java.lang.reflect.Constructor;
import java.util.concurrent.*;

public class BigOItTree {

    /** Constructor
     */
    public BigOItTree(){
    }

    private double[] results = new double[300];
    double resultsBeforeAverage = 0;
    int n = 250;
    int iteration = 0;
    ExecutorService es = Executors.newSingleThreadExecutor();

    public double doublingRatio(String tree, long timeOutInMs) {
        try{
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    double prev = timeOfExecution(n, tree);
                    while (iteration < 25){
                        double trialTime = timeOfExecution(n, tree);
                        resultsBeforeAverage += (trialTime/prev);
                        prev = trialTime;
                        iteration++;
                        n = n*2;
                        if (n < 0){
                            break;
                        }
                    }
                }
            };
            Future<?> f = es.submit(r);
            f.get(timeOutInMs, TimeUnit.MILLISECONDS);
        } catch (Exception e){
            if (iteration < 2){
                return Double.NaN;
            }
            return resultsBeforeAverage/iteration;
        }
        return resultsBeforeAverage/iteration;
    }

    private double timeOfExecution(int n, String tree){
        BTKeysAtSameLevel2 btKey = new BTKeysAtSameLevel2();
        long start = System.nanoTime();
        btKey.compute(tree);
        long end = System.nanoTime();
        return (double)(end-start);
    }
}
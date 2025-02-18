package edu.yu.introtoalgs;

import java.lang.reflect.Constructor;
import java.util.concurrent.*;

public class BigOIt extends BigOItBase{

    /** Constructor
     */
    public BigOIt(){
        for (int i = 0; i < 100000; i++) { // i think this is warming up the JVM
            BigOMeasurable temp = new BigOMeasurable() {
                @Override
                public void execute() {
                }
            };
        }
    }
    private double[] results = new double[300];
    double resultsBeforeAverage = 0;
    int n = 250;
    int iteration = 0;
    ExecutorService es = Executors.newSingleThreadExecutor();

    @Override
    public double doublingRatio(String bigOMeasurable, long timeOutInMs) {
        try{
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    double prev = timeOfExecution(n, bigOMeasurable);
                    while (iteration < 25){
                        double trialTime = timeOfExecution(n, bigOMeasurable);
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

    private double timeOfExecution(int n, String className){
        BigOMeasurable bom = null;
        try { // init object
            Class clazz = Class.forName(className);
            Constructor c = clazz.getConstructor();
            bom = (BigOMeasurable) c.newInstance();
        }catch(Exception e){
            e.printStackTrace();
        }
        bom.setup(n);
        long start = System.nanoTime();
        bom.execute();
        long end = System.nanoTime();
        return (double)(end-start);
    }
}

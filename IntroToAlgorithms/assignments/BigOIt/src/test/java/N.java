import edu.yu.introtoalgs.BigOMeasurable;

public class N extends BigOMeasurable {
    public N() {
    }
    @Override
    public void execute() {
        double[] data = new double[this.n];
        for (int i = 0; i < data.length; i++){
            data[i] = Math.random();
        }
    }

}

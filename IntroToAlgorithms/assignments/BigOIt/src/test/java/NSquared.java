import edu.yu.introtoalgs.BigOMeasurable;

public class NSquared extends BigOMeasurable {
    public NSquared(){
    }
    @Override
    public void execute() {
        double[][] data = new double[this.n][this.n];
        for (int i = 0; i< data.length; i++){
            for (int j = 0; j< data[0].length; j++){
                data[i][j] = 9;
            }
        }
    }
}

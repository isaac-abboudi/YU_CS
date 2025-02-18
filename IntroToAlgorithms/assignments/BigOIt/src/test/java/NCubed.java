import edu.yu.introtoalgs.BigOMeasurable;

public class NCubed extends BigOMeasurable {

    public NCubed(){

    }

    @Override
    public void execute() {
        double[][][] data = new double[this.n][this.n][this.n];
        for (int i = 0; i < this.n; i++){
            for (int j = 0; j < this.n; j++){
                for (int k = 0; k < this.n; k++){
                    data[i][j][k] = 9;
                }
            }
        }
    }
}

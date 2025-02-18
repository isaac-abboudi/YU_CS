import edu.yu.da.ArithmeticPuzzle;
import edu.yu.da.ArithmeticPuzzleBase;
import edu.yu.da.GeneticAlgorithmConfig;
import org.junit.Test;


public class ArithmeticPuzzleExam {

    @Test
    public void simpleTest(){

        GeneticAlgorithmConfig g = new GeneticAlgorithmConfig(100, 8,
                GeneticAlgorithmConfig.SelectionType.ROULETTE, .1, .8);
        ArithmeticPuzzle a = new ArithmeticPuzzle("ABC", "DEF", "GHI");
        ArithmeticPuzzleBase.SolutionI solution = a.solveIt(g);
        for (int i = 0; i < solution.solution().size(); i++){
            System.out.println("index: " + i +   ", char: "+ solution.solution().get(i));
        }
    }

//    @Test
//    public void demo(){
//
//            final String augend = "Q";
//            final String addend = "R";
//            final String sum = "AB";
//            final double MUTATION_PROBABILITY = 0.2;
//            final double CROSSOVER_PROBABILITY = 0.7;
//            final int POPULATION_SIZE = 1000;
//            GeneticAlgorithmConfig.SelectionType selectionType = TOURNAMENT ;
//            final GeneticAlgorithmConfig gac = new
//                    GeneticAlgorithmConfig
//                    ( POPULATION_SIZE , MAX_GENERATIONS ,
//                            selectionType , mutationProbability ,
//                            crossoverProbability );
//            final ArithmeticPuzzleBase apb = new
//                    ArithmeticPuzzle ( augend , addend , sum ) ;
//            final SolutionI solution = apb . solveIt ( gac ) ;
//    }




}

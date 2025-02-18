package edu.yu.da;

import java.util.*;
import edu.yu.da.*;

public class ArithmeticPuzzle extends ArithmeticPuzzleBase {


    public class Solution implements SolutionI{
        public List<Character> sol;

        public Solution(List<Character> sol){
            this.sol = sol;
        }


        @Override
        public List<Character> solution() {
            return sol;
        }

        @Override
        public String getAugend() {
            return augend;
        }

        @Override
        public String getAddend() {
            return addend;
        }

        @Override
        public String getSum() {
            return sum;
        }

        @Override
        public int nGenerations() {
            return maxGenerations;
        }
    }
    /**
     * Constructor.  Specifies the arithmetic puzzle to be solved in terms of an
     * augend, addend, and sum.
     * <p>
     * Representation: all characters are in the range A-Z, with each letter
     * represents a unique digit.  The puzzle to be solved specifies that the
     * augend and addend (each representing a number in base 10) sum to the
     * specified sum (also a number in base 10).  Each of these numbers is
     * represented with the most significant letter (digit) in position 0, next
     * most significant letter (digit) in position 1, and so on.  The numbers
     * need not be the same length: an "empty" digit is represented by the
     * "space" character.
     * <p>
     * Addition: Augend + Addend = Sum
     *
     * @param augend
     * @param addend
     * @param sum
     */
    public ArithmeticPuzzle(final String augend,
                            final String addend,
                            final String sum){
        super(augend, addend, sum);

        this.augend = augend;
        this.addend = addend;
        this.sum = sum;
        this.scalingFactor = getScalingFactor(augend, addend, sum);

        this.uniqueChars = getUniqueChars(augend, addend, sum);
        this.masterMap = generateMappings(uniqueChars);
        this.fitnessMap = new HashMap<>();

    }

    private List<Character> getUniqueChars(String str1, String str2, String str3){
        HashSet<Character> uniqueChars = new HashSet<>();
        for (int i = 0; i < str1.length(); i++){
            uniqueChars.add(str1.charAt(i));
        }
        for (int i = 0; i < str2.length(); i++){
            uniqueChars.add(str2.charAt(i));
        }
        for (int i = 0; i < str3.length(); i++){
            uniqueChars.add(str3.charAt(i));
        }
        return new ArrayList<>(uniqueChars);
    }
    private HashMap<Integer, HashMap<Character, Integer>> generateMappings(List<Character> list){
        HashMap<Integer, HashMap<Character, Integer>> map = new HashMap<>();
        for (int i = 0; i < initialPopulation; i++){
            map.put(i, generateMap(list));
        }
        return map;
    }
    private HashMap<Character, Integer> generateMap(List<Character> list){
        HashMap<Character, Integer> m = new HashMap<>();
        for (Character c : list){
            m.put(c, (int)(Math.random()*10));
        }
        return m;
    }
    private int nextKey(){
        newChild+=1;
        return newChild;
    }
    private int getScalingFactor(String str1, String str2, String str3){
        return (int) Math.pow(10, Math.max(Math.max(str1.length(), str2.length()), str3.length())
                - Math.min(Math.min(str1.length(), str2.length()), str3.length()));
    }
    private int stringValue(String str, HashMap<Character, Integer> map){
        int value = 0;
        for (int i = 0; i < str.length(); i++) {
            int digit = map.get(str.charAt(i));
            value = value * 10 + digit;
        }
        return value;
    }
    private double evaluateFitness(HashMap<Character, Integer> map){
        // find value for each string
        int aug = stringValue(augend, map);
        int add = stringValue(addend, map);
        int s = stringValue(sum, map);
        int absoluteDiff = Math.abs((aug + add) - s);
        double scaledFitness = (double) absoluteDiff / scalingFactor;
        // the better the score, the closer it is to 100. 100 == perfectScore

        return (1/(1+scaledFitness)*100);
    }

    private HashSet<Integer> rouletteWheel(HashMap<Integer, Double> map, double totalFitness){
        HashSet<Integer> selected = new HashSet<>();
        HashSet<Integer> allMappings = new HashSet<>(map.keySet());
        double cumulativeSum = 0;
        double random = Math.random() * (int)(totalFitness+1);
        while(selected.size() < elite){
            for (Integer i: allMappings){
                cumulativeSum += map.get(i);
                if (cumulativeSum >= random){
                    selected.add(i);
                    allMappings.remove(i);
                }
            }
        }
        return selected;
    }
    private HashSet<Integer> roulette(){
        HashMap<Integer, Double> relFitness = new HashMap<>();
        double totalFitness = 0;
        for (Integer i : fitnessMap.keySet()){
            totalFitness += fitnessMap.get(i);
        }
        // normalize fitness scores
        for (Integer i : fitnessMap.keySet()){
            double relFit = fitnessMap.get(i)/totalFitness;
            relFitness.put(i, relFit);
        }
        return rouletteWheel(relFitness, totalFitness);
    }
    private HashSet<Integer> tournament(){
        HashSet<Integer> selected = new HashSet<>();
        HashSet<Integer> allMappings = new HashSet<>(masterMap.keySet());
        HashSet<Integer> bracket = new HashSet<>();
        while (selected.size() < elite){
            for (int i = 0; i < bracketSize; i++){
                int randomIndex = (int) (Math.random() * initialPopulation);
                bracket.add(randomIndex);
                allMappings.remove(randomIndex);
            }
            double max = Double.MIN_VALUE;
            int index = -1;
            for (Integer i : bracket){
                double fitness = fitnessMap.get(i);
                if (max < fitness){
                    max = fitness;
                    index = i;
                }
            }
            selected.add(index);
        }
        return selected;
    }
    private HashSet<HashMap<Character, Integer>> selectForCrossover(HashSet<Integer> set){
        HashSet<Integer> selected = new HashSet<>();
        for (Integer i : set){
            if (Math.random() < crossoverProb){
                selected.add(i);
            }
        }
        return crossover(set);
    }
    private HashSet<HashMap<Character, Integer>> crossover(HashSet<Integer> set){
        HashSet<HashMap<Character, Integer>> offsprings = new HashSet<>();
        ArrayList<Integer> parents = new ArrayList<>(set);
        int crossoverPoint = (int)(Math.random() * uniqueChars.size());
        int counter = 0;
        int crossoverPool = parents.size();
        if (crossoverPool % 2 != 0){
            crossoverPool-=1;
        }
        for (int i = 0; i < crossoverPool; i+=2){
            HashMap<Character, Integer> child = new HashMap<>();
            for (Character c : uniqueChars){
                Integer val = masterMap.get(parents.get(i)).get(c);
                if (val != null){
                    child.put(c, val);
                    counter++;
                }
                if (counter == crossoverPoint){
                    break;
                }
            }
            for (Character c : uniqueChars){
                if (child.containsKey(c)){
                    continue;
                }
                Integer val = masterMap.get(parents.get(i+1)).get(c);
                if (val != null){
                    child.put(c, val);
                    counter++;
                }
                if (counter == uniqueChars.size()){
                    break;
                }
            }
            offsprings.add(child);
        }
        return offsprings;
    }
    private HashSet<HashMap<Character, Integer>> mutation(){
        HashSet<HashMap<Character, Integer>> mutatedOffsprings = new HashSet<>();
        for (Integer map : masterMap.keySet()){
            HashMap<Character, Integer> mutatedChild = null;
            if (Math.random() < mutationProb){
                mutatedChild = new HashMap<>(masterMap.get(map));
                int mutation = (int)(Math.random() * 10);
                int randomIndex = (int) (Math.random() * uniqueChars.size());
                char key = uniqueChars.get(randomIndex);
                mutatedChild.put(key, mutation);
            }
            mutatedOffsprings.add(mutatedChild);
        }
        return mutatedOffsprings;
    }
    private void purge(int amountToPurge, HashSet<Integer> elites){
        for (Integer mapping : masterMap.keySet()){
            if (!elites.contains(mapping)){
                if ((Math.random() * 100) < fitnessMap.get(mapping)){
                    masterMap.remove(mapping);
                    fitnessMap.remove(mapping);
                    amountToPurge--;
                    if (amountToPurge <= 0){
                        break;
                    }
                }
            }
        }
    }
    private HashMap<Character, Integer> getBestAnswer(){
        double max = Double.MIN_VALUE;
        int index = -1;
        for (Integer i : fitnessMap.keySet()){
            double fitness = fitnessMap.get(i);
            if (max < fitness){
                max = fitness;
                index = i;
            }
        }
        return masterMap.get(index);
    }
    private SolutionI convertToSolution(HashMap<Character, Integer> answer){
        List<Character> list = new ArrayList<>(10);
        for (Character c : answer.keySet()){
            list.add(answer.get(c), c);
        }
        return new Solution(list);
    }



    @Override
    public SolutionI solveIt(GeneticAlgorithmConfig gac) {

        //SolutionI solution = new Solution();

//        this.uniqueChars = getUniqueChars(augend, addend, sum);
//        this.masterMap = generateMappings(uniqueChars);
//        this.fitnessMap = new HashMap<>();



        this.initialPopulation = gac.getInitialPopulationSize();
        this.elite = initialPopulation/4;
        this.bracketSize = 4;
        this.newChild = initialPopulation;
        this.crossoverProb = gac.getCrossoverProbability();
        this.mutationProb = gac.getMutationProbability();
        boolean roulette = gac.getSelectionType() == GeneticAlgorithmConfig.SelectionType.ROULETTE;
        int currentG = 0;
        this.maxGenerations = gac.getMaxGenerations();
        while(currentG <= maxGenerations){
            System.out.println("iteration: " + currentG);
            for (Integer map : masterMap.keySet()){
                double fitnessScore = evaluateFitness(masterMap.get(map));
                if (fitnessScore == 100){
                    solutionMap = masterMap.get(map);
                    break;
                }
                fitnessMap.put(map, fitnessScore);
            }
            HashSet<Integer> elites = null;
            if (roulette){
                elites = roulette();
            } else {
                elites = tournament();
            }

            HashSet<HashMap<Character, Integer>> offsprings = selectForCrossover(elites);
            HashSet<HashMap<Character, Integer>> mutations = mutation();

            purge(offsprings.size() + mutations.size(), elites);

            for (HashMap<Character, Integer> map : offsprings){
                masterMap.put(nextKey(), map);
            }
            for (HashMap<Character, Integer> map : mutations){
                masterMap.put(nextKey(), map);
            }

            currentG++;
        }
        if (solutionMap == null){
            solutionMap = getBestAnswer();
        }
        return convertToSolution(solutionMap);
    }

    private int elite;
    private int bracketSize;
    private int newChild;
    private double crossoverProb;
    private double mutationProb;
    private int maxGenerations;
    private int scalingFactor;
    private HashMap<Character, Integer> solutionMap;
    private String augend, addend, sum;
    private List<Character> uniqueChars;
    private HashMap<Integer, HashMap<Character, Integer>> masterMap;
    private HashMap<Integer, Double> fitnessMap;

    private int initialPopulation;

}

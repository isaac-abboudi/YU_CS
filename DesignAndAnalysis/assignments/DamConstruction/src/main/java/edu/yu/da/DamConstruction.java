package edu.yu.da;

import java.util.Arrays;

/** Defines the API for specifying and solving the DamConstruction problem (see
 * the requirements document).
 *
 * Students MAY NOT change the public API of this class, nor may they add ANY
 * constructor.
 *
 * @author Avraham Leff
 */

public class DamConstruction {

    private int[] yArray;
    private int[][] memo;
    private int n;
    private int riverEnd;

    /**
     * Constructor
     *
     * @param Y        y-positions specifying dam locations, sorted by ascending
     *                 y-values.  Client maintains ownership of this parameter.  Y must contain
     *                 at least one element.
     * @param riverEnd the y-position of the river's end (a dam was previously
     *                 constructed both at this position and at position 0 and no evaluation will be
     *                 made of their construction cost): all values in Y are both greater than 0
     *                 and less than riverEnd.
     * @note students need not verify correctness of either parameter.  On the
     * other hand, for your own sake, I suggest that you add these (easy to do)
     * "sanity checks".
     */
    public DamConstruction(final int Y[], final int riverEnd) {
        // fill me in to taste
        this.yArray = Y;

        this.riverEnd = riverEnd;
        this.n = yArray.length;
        this.memo = new int[n][n];
    } // constructor

    /**
     * Solves the DamConstruction problem, returning the minimum possible cost
     * of evaluating the environmental impact of dam construction over all
     * possible construction sequences.
     *
     * @return the minimum possible evaluation cost.
     */
    public int solve() {

        for (int i = 0; i < n; i++) {
            Arrays.fill(memo[i], Integer.MAX_VALUE);
        }
        //first dam
        memo[0][0] = yArray[0];

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {

                int cost = (yArray[i] - yArray[j]) * (riverEnd - yArray[i]); // compute cost of evaluating dam at position i
                for (int k = 0; k < i; k++) {
                    if ((memo[k][j] + cost + yArray[i] - yArray[k]) >= 0){
                        memo[i][j] = Math.min(memo[i][j], memo[k][j] + cost + yArray[i] - yArray[k]);
                    }
                }
            }

            memo[i][i] = memo[i - 1][i - 1] + (yArray[i] - yArray[i - 1]) * (riverEnd - yArray[i]); // evaluate dam at position i as the last dam
        } // loop

        //mincost
        int minCost = Integer.MAX_VALUE;
        for (int j = 0; j < n; j++) {
            minCost = Math.min(minCost, memo[n - 1][j]);
        }
        return minCost;
    }

    /** Returns the cost of applying the dam evaluation decisions in the
     * specified order against the dam locations and river end state supplied to
     * the constructor.
     *
     * @param evaluationSequence elements of the Y parameter supplied in the
     * constructor, possibly rearranged such that the ith element represents the
     * y-position that is to be the ith dam evaluated for the WPA.  Thus: if Y =
     * {2, 4, 6}, damDecisions may be {4, 6, 2}: this method will return the cost
     * of evaluating the entire set of y-positions when dam evaluation is done
     * first for position "4", then for position "6", finally for position "2".
     * @return the cost of dam evaluation for the entire sequence of dam
     * positions when performed in the specified order.
     * @fixme This method is conceptually a static method because it doesn't
     * depend on the optimal solution produced by solve().  OTOH: the
     * implementation does require access to both the Y array and "river end"
     * information supplied to the constructor.
     * @note the implementation of this method is (almost certainly) not the
     * dynamic programming algorithm used in solve().  This method is part of the
     * API to stimulate your thinking as you work through this assignment and to
     * exercise your software engineering muscles.
     * @notetoself is this assignment too easy without an API for returning the
     * "optimal evaluation sequence"?
     */
    public int cost(final int[] evaluationSequence) {
        return -1;
    }
} // class
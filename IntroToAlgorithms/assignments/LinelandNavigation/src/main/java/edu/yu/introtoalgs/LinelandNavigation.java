package edu.yu.introtoalgs;

/** Defines the API for the LinelandNavigation assignment: see the requirements
 * document for more information.
 *
 * Students MAY NOT change the constructor signature.  My test code will only
 * invoke the API defined below.
 *
 * @author Avraham Leff
 */

public class LinelandNavigation {

    /** Even though Lineland extends forward (and backwards) infinitely, for
     * purposes of this problem, the navigation goal cannot exceed this value
     */
    public final static int MAX_FORWARD = 1_000_000;
    private int g, m;
    private int p;
    private int[] line;
    private boolean impossible;


    /** Constructor.  When the constructor completes successfully, the navigator
     * is positioned at position 0.
     *
     * @param g a positive value indicating the minimim valued position for a
     * successful navigation (a successful navigation can result in a position
     * that is greater than g).  The value of this parameter ranges from 1 to
     * MAX_FORWARD (inclusive).
     * @param m a positive integer indicating the exact number of positions that
     * must always be taken in a forward move. The value of this parameter cannot
     * exceed MAX_FORWARD.
     * @throws IllegalArgumentException if the parameter values violate the
     * specified semantics.
     */
    public LinelandNavigation(final int g, final int m) {
        // fill me in!
        this.g = g;
        this.m = m;
        this.p = m;
        this.impossible = false;
        this.line = new int[MAX_FORWARD];
    }

    /** Adds a mined line segment to Lineland (an optional operation).
     *
     * NOTE: to simplify computation, assume that no two mined line segments can
     * overlap with one another, even at their end-points.  You need not test for
     * this (although if it's easy to do so, consider sprinkling asserts in your
     * code).
     *
     * @param start a positive integer representing the start (inclusive)
     * position of the line segment
     * @param end a positive integer represending the end (inclusive) position of
     * the line segment, must be greater or equal to start, and less than
     * MAX_FORWARD.
     */
    public void addMinedLineSegment(final int start, final int end) {
        // fill me in!
        if (end < start || end >= MAX_FORWARD){
            return;
        }
        if (end - start >= m){
            // if a mined segment is >= the jump dist than getting to g is impossible.
            impossible = true;
        }
        if (p >= start && p <= end){ //first jump is impossible
            impossible = true;
        }
        for (int i = start; i <= end; i++){
            line[i] = 1;
        }
    }


    /** Determines the minimum number of moves needed to navigate from position 0
     * to position g or greater, where forward navigation must exactly m
     * positions at a time and backwards navigation can be any number of
     * positions.
     *
     * @return the minimum number of navigation moves necessary, or "0" if no
     * navigation is possible under the specified constraints.
     */
    public final int solveIt() {
        if (impossible){
            return 0;
        }
        int totalMoves = 1;
        boolean reachedG = false;
        int backwardCushion = 0;
        while (!reachedG){
            if (isSafeSpot(p+m)){ //can move forward -> move forward
                p += m;
                totalMoves++;
                if (p >= g){
                    reachedG = true;
                }
            } else { //have to move back
                // how much can i move back
                int distRequired = distToGoBack(p+m);
                if (distRequired > backwardCushion){
                    backwardCushion = cushionSize(p);
                    totalMoves++;
                }
                p -= distRequired;
                if (p < 0){
                    return 0;
                }
            }
        }
        return totalMoves;
    }


    private boolean isSafeSpot(int spot){
        return line[spot] == 0;
    }

    private int distToGoBack(int spot){
        int dist = 0;
        for (int i = spot; i > 1; i--){
            if (line[i] == 0){
                return dist;
            }
            dist++;
        }
        return -1;
    }

    private int cushionSize(int p){
        int cushion = 0;
        for (int i = p; i > 1; i--){
            if (line[i] != 0){
                return cushion;
            } else if (cushion == m){
                return cushion;
            }
            cushion++;
        }
        return cushion;
    }

} // LinelandNavigation
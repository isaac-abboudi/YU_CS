package edu.yu.da;

import java.util.Arrays;
import java.util.List;

public class WeAreAllConnected extends WeAreAllConnectedBase {

    public static class Segment extends SegmentBase {
        public final int x, y, duration;

        /** Constructor.
         *
         * @param x one end of a communication segment, specified by a city id
         * (0..n-1)
         * @param y one end of a communication segment, specified by a city id
         * (0..n-1).  You may assume that "x" differs from "y".
         * @param duration unit-less amount of time required for a message to
         * travel from either end of the segment to the other.  You may assume that
         * duration is non-negative.
         */
        public Segment(final int x, final int y, final int duration) {
            super(x, y, duration);
            this.x = x;
            this.y = y;
            this.duration = duration;
        }
    } // static inner Segment class

    /** Constructor.  No-op implementation.
     */
    public WeAreAllConnected() {
        // any additional function to be provided by the subclassed implementation
    }

    /** Given a list of the current communication system's segments and a list of
     * possible segments that can be added to the current system, select exactly
     * one segment from the set of possibilities to be added to the current
     * communication system.  You may assume that all segments supplied by the
     * client satisfy Segment semantics.
     *
     * @param n the ids of all cities referenced by SegmentBase instances lie in
     * the range 0..n-1 (inclusive).
     * @param current the current communication system defined as a list of
     * segments.  The client maintains ownership of this parameter.
     * @param possibilities one possible segment will be selected from this list
     * to be added to the current communication system.  The client maintains
     * ownership of this parameter.
     * @return the segment from the set of possibilities that provides the best
     * improvement in the total duration of the current system.  Total duration
     * is defined as the sum of the durations between all pairs of cities x and y
     * in the current system.  If more than one segment qualifies, return any of
     * those possibilities.
     */
    public SegmentBase findBest(int n, List<SegmentBase> current, List<SegmentBase> possibilities) {
        SegmentBase segToReturn = null;
        int[][] adjMatrixOG = initDistMatrix(n, current);
        floydWarshallAbboudi(n, adjMatrixOG);
        int durationOG = sumItAllUp(n, adjMatrixOG);
        int durationToBeat = durationOG;

        // for each poissibility
            // save oldXY and oldYX
            // add new edge to matrix
            // run FW and record duration
            // save duration if < min
            // replace oldXY and oldYX
        for (SegmentBase newSegment : possibilities){

            //save old vals
            int oldXY = adjMatrixOG[newSegment.x][newSegment.y];
            // check if new duration even helps
            if (oldXY <= newSegment.duration){
                continue;
            }
            int oldYX = adjMatrixOG[newSegment.y][newSegment.x];
            adjMatrixOG[newSegment.x][newSegment.y] = newSegment.duration;
            adjMatrixOG[newSegment.y][newSegment.x] = newSegment.duration;

            floydWarshallAbboudi(n, adjMatrixOG);

            int newDuration = sumItAllUp(n, adjMatrixOG);
            if (newDuration <= durationToBeat){
                durationToBeat = newDuration;
                segToReturn = newSegment;
            }
            adjMatrixOG[newSegment.x][newSegment.y] = oldXY;
            adjMatrixOG[newSegment.y][newSegment.x] = oldYX;
        }

        return segToReturn;
    }

    private int[][] initDistMatrix(int n, List<SegmentBase> list){
        int[][] array = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(array[i], Integer.MAX_VALUE);
        }
        for (SegmentBase segment : list){
            array[segment.x][segment.y] = segment.duration;
            array[segment.y][segment.x] = segment.duration;
        }
        return array;
    }

    private void floydWarshallAbboudi(int n, int[][] dist) {
        int i, j, k;

        for (k = 0; k < n; k++) {
            for (i = 0; i < n; i++) {
                for (j = 0; j < n; j++) {
                    if (dist[i][k] == Integer.MAX_VALUE || dist[k][j] == Integer.MAX_VALUE){
                        continue;
                    }
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
    }

    private int sumItAllUp(int n, int[][] dist){
        int total = 0;
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                if (dist[i][j] == Integer.MAX_VALUE){
                    continue;
                }
                total += dist[i][j];
            }
        }
        return total;
    }
}

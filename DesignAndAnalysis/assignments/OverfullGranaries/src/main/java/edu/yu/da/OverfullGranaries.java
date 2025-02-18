package edu.yu.da;

/** Defines the API for specifying and solving the OverfullGranaries problem
 * (see the requirements document).
 *
 * Students MAY NOT change the public API of this class, nor may they add ANY
 * constructor.

 * @author Avraham Leff
 */


import java.util.*;

public class OverfullGranaries {

    /** Represents the 10_000 bushels of grain that must be moved from the
     * overfull granaries to the underfull granaries
     */
    public final static double BUSHELS_TO_MOVE = 10_000;

    private class Edge {
        private final int src;
        private final int dest;
        private final int capacity;
        private int flow;


        public Edge(int src, int dest, int capacity){
            this.src = src;
            this.capacity = capacity;
            this.dest = dest;
        }
        public int from(){
            return src;
        }
        public int to(){
            return dest;
        }
        public int capacity(){
            return capacity;
        }
        public int flow(){
            return flow;
        }
        public int other(int vertex){
            if(vertex == src)
                return dest;
            else if (vertex == dest)
                return src;
            else
                throw new IllegalArgumentException();
        }
        public int resCapTo(int vertex){
            if(vertex == src)
                return flow;
            else if (vertex == dest)
                return capacity - flow;
            else
                throw new IllegalArgumentException();
        }
        public void addResFlowTo(int vertex, int delta){
            if(vertex == src)
                flow -= delta;
            else if (vertex == dest)
                flow += delta;
            else
                throw new IllegalArgumentException();
        }
        @Override
        public boolean equals(final Object o) {
            if (o == this) return true;
            if (!(o instanceof Edge)) {
                return false;
            }

            final Edge that = (Edge) o;
            return that.to() == to() && that.from() == from() && flow() == that.flow() && capacity() == that.capacity();
        }
    } // Edge

    private class NetworkGraph{
        private int V;
        private ArrayList<Edge> [] adj;
        private Set<Edge> edges = new HashSet<>();

        public NetworkGraph(int V){
            this.V = V;
            adj = (ArrayList<Edge> []) new ArrayList[V];
            for (int v = 0; v < V; v++){
                adj[v] = new ArrayList<>();
            }
        }
        public void addEdge(Edge e){
            if (!edges.contains(e)) {
                edges.add(e);
                int v = e.from();
                int w = e.to();
                adj[v].add(e);
                adj[w].add(e);
            }
        }
        public List<Edge> adj(int v){
            return adj[v];
        }

        public int V() {
            return V;
        }
    } // NetworkGraph

    private class FordFulkerson{
        private boolean[] marked;
        private Edge[] edgeTo;
        private int value;

        public FordFulkerson(NetworkGraph G, int s, int t){
            value = 0;
            while(hasAugmentingPath(G,s,t)){
                int bottle = Integer.MAX_VALUE;
                for(int v = t; v != s; v = edgeTo[v].other(v)){
                    bottle = Math.min(bottle, edgeTo[v].resCapTo(v));
                }
                for(int v = t; v != s; v = edgeTo[v].other(v)){
                    edgeTo[v].addResFlowTo(v,bottle);
                }
                value += bottle;
            }
        }
        private boolean hasAugmentingPath(NetworkGraph G, int s, int t){
            edgeTo = new Edge[G.V()];
            marked = new boolean[G.V()];
            Queue<Integer> queue = new ArrayDeque<>();
            queue.add(s);
            marked[s] = true;
            while(!queue.isEmpty()){
                int v = queue.remove();
                for(Edge e : G.adj(v)){
                    int w = e.other(v);
                    if(e.resCapTo(w) > 0 && !marked[w]){
                        edgeTo[w] = e;
                        marked[w] = true;
                        queue.add(w);
                    }
                }
            }
            return marked[t];
        }
    } //FordFulkerson


    private String[] overfullGranaries;
    private String[] underfullGranaries;
    private HashMap<String, Integer> granaryID;
    private HashMap<Integer, String> answerKey;
    private int V;
    private ArrayList<Edge> aToB;
    private ArrayList<String> minCut;
    /** Constructor.
     *
     * @param X labelling of the overfull granaries, must contain at least one
     * element and no duplicates.  No element of X can be an element of Y.
     * @param Y labelling of the underfull granaries, must contain at least one
     * element and no duplicates.  No element of Y can be an element of X.
     */
    public OverfullGranaries(final String[] X, final String[] Y) {
        // init maps
        this.answerKey = new HashMap<>();
        this.overfullGranaries = X;
        this.granaryID = new HashMap<>();
        int i = 0;
        for (; i < overfullGranaries.length; i++){
            granaryID.put(overfullGranaries[i], i+1);
            answerKey.put(i+1, overfullGranaries[i]);
        }
        this.underfullGranaries = Y;
        for (int j = 0; j < underfullGranaries.length; j++){
            this.granaryID.put(underfullGranaries[j], i+1);
            answerKey.put(i+1, overfullGranaries[j]);
            i++;
        }
        this.aToB = new ArrayList<>();
        this.V = overfullGranaries.length + underfullGranaries.length + 2;
        this.minCut = new ArrayList<>();
    }

    /** Specifies that an edge exists from the specified src to the specified
     * dest of specified capacity.  It is legal to invoke edgeExists between
     * nodes in X, between nodes in Y, from a node in X to a node in Y, or for
     * src and dest to be hitherto unknown nodes.  The method cannot specify a
     * node in Y to be the src, nor can it specify a node in X to be the dest.
     *
     * @param src must contain at least one character
     * @param dest must contain at least one character, can't equal src
     * @param capacity must be greater than 0, and is specified implicitly to be
     * "bushels per hour"
     */
    public void edgeExists(final String src, final String dest, final int capacity) {
        int s = granaryID.get(src);
        int d = granaryID.get(dest);
        aToB.add(new Edge(s, d, capacity));
    }

    /** Solves the OverfullGranaries problem.
     *
     * @return the minimum number hours neeed to achieve the goal of moving
     * BUSHELS_TO_MOVE number of bushels from the X granaries to the Y granaries
     * along the specified road map.
     * @note clients may only invoke this method after all relevant edgeExists
     * calls have been successfully invoked.
     */
    public double solveIt() {

        NetworkGraph G = new NetworkGraph(V);
        HashSet<Edge> edgesFromSrc = new HashSet<>();
        int sink = G.V() - 1;
        int i = 1;
        for (; i < overfullGranaries.length; i++){
            Edge edgeFromSrc = new Edge(0, i, (int)BUSHELS_TO_MOVE);
            G.addEdge(edgeFromSrc);
            edgesFromSrc.add(edgeFromSrc);
        }
        for (Edge edge : aToB){
            G.addEdge((new Edge(edge.src, edge.dest, edge.capacity)));
        }
        for (; i < G.V - 1; i++){
            G.addEdge((new Edge(i, sink, (int)BUSHELS_TO_MOVE)));
        }
        // FF algorithm produces residual graph
        FordFulkerson maxFlow = new FordFulkerson(G,0,sink);
        HashSet<Edge> visitedEdges = new HashSet<>();
        //dfs(G.adj, 0, visitedNodes, minCutNodes, visitedEdges); pending...
        double residualCap = 0;
        for (Edge edge : edgesFromSrc){
            residualCap += edge.resCapTo(0);
            if (edge.resCapTo(0) > 0){
                minCut.add(answerKey.get(edge.dest));
            }
        }
        Collections.sort(minCut);
        double bushelsInMPH = BUSHELS_TO_MOVE / residualCap;
        return bushelsInMPH;
    }

    public void dfs(ArrayList<Edge>[] adj, int vertex, Set<Integer> visited, Set<Integer> connectedNodes, Set<Edge> visitedEdges) {
        visited.add(vertex);
        for (Edge edge : adj[vertex]) {
            int neighbor = edge.dest;
            if (edge.src == vertex && edge.resCapTo(edge.src) > 0) {
                connectedNodes.add(neighbor);
                visitedEdges.add(edge);
                if (!visited.contains(neighbor)) {
                    dfs(adj, neighbor, visited, connectedNodes, visitedEdges);
                }
            }
        }
    }

    /** Return the names of all vertices in the X side of the min-cut, sorted by
     * ascending lexicographical order.
     *
     * @return only the names of the vertices in the X side of the min-cut
     * @note clients may only invoke this method after solveIt has been
     * successfully invoked.  Else throw an ISE.
     */
    public List<String> minCut() {
        return minCut;
    }
} // OverfullGranaries



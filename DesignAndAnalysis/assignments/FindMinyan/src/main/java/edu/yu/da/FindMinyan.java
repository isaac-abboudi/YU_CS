package edu.yu.da;

/** Defines the API for specifying and solving the FindMinyan problem (see the
 * requirements document).  Also defines an inner interface, and uses it as
 * part of the ArithmeticPuzzleI API definition.
 *
 * Students MAY NOT change the public API of this class, nor may they add ANY
 * constructor.
 *
 * @author Avraham Leff
 */

import java.util.*;

public class FindMinyan {


    class DirectedEdge {
        int v;
        int w;
        int weight;

        public DirectedEdge(int v, int w, int weight){
            this.v = v;
            this.w = w;
            this.weight = weight;
        }
        public int from(){
            return this.v;
        }
        public int to(){
            return this.w;
        }
        public int weight() {
            return this.weight;
        }
    }
    public class Bag<Item> implements Iterable<Item> {
        private Node<Item> first;    // beginning of bag
        private int n;               // number of elements in bag

        // helper linked list class
        private class Node<Item> {
            private Item item;
            private Node<Item> next;
        }

        public Bag() {
            first = null;
            n = 0;
        }

        public boolean isEmpty() {
            return first == null;
        }

        public int size() {
            return n;
        }

        public void add(Item item) {
            Node<Item> oldfirst = first;
            first = new Node<Item>();
            first.item = item;
            first.next = oldfirst;
            n++;
        }

        public Iterator<Item> iterator() {
            return new LinkedIterator(first);
        }

        // an iterator, doesn't implement remove() since it's optional
        private class LinkedIterator implements Iterator<Item> {
            private Node<Item> current;

            public LinkedIterator(Node<Item> first) {
                current = first;
            }

            public boolean hasNext() {
                return current != null;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

            public Item next() {
                if (!hasNext()) throw new NoSuchElementException();
                Item item = current.item;
                current = current.next;
                return item;
            }
        }
    }

    public class EdgeWeightedDigraph{

        private int V;
        private int E;
        private Bag<DirectedEdge>[] adj;

        public EdgeWeightedDigraph(int V){
            this.V = V;
            this.E = 0;
            adj = (Bag<DirectedEdge>[]) new Bag[V];
            for (int i = 0; i < V; i++){
                adj[i] = new Bag<DirectedEdge>();
            }
        }

        public void addEdge(DirectedEdge e){
            adj[e.from()].add(e);
            E++;
        }
        public Iterable<DirectedEdge> adj(int v){
            return adj[v];
        }
        public Iterable<DirectedEdge> edges(){
            Bag<DirectedEdge> bag = new Bag<>();
            for (int i = 0; i < V; i++){
                for (DirectedEdge e : adj[i]){
                    bag.add(e);
                }
            }
            return bag;
        }
    }

    public class IndexMinPQ<Key extends Comparable<Key>> implements Iterable<Integer> {
        private int maxN;        // maximum number of elements on PQ
        private int n;           // number of elements on PQ
        private int[] pq;        // binary heap using 1-based indexing
        private int[] qp;        // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
        private Key[] keys;      // keys[i] = priority of i

        /**
         * Initializes an empty indexed priority queue with indices between {@code 0}
         * and {@code maxN - 1}.
         *
         * @param maxN the keys on this priority queue are index from {@code 0}
         *             {@code maxN - 1}
         * @throws IllegalArgumentException if {@code maxN < 0}
         */
        public IndexMinPQ(int maxN) {
            if (maxN < 0) throw new IllegalArgumentException();
            this.maxN = maxN;
            n = 0;
            keys = (Key[]) new Comparable[maxN + 1];    // make this of length maxN??
            pq = new int[maxN + 1];
            qp = new int[maxN + 1];                   // make this of length maxN??
            for (int i = 0; i <= maxN; i++)
                qp[i] = -1;
        }

        /**
         * Returns true if this priority queue is empty.
         *
         * @return {@code true} if this priority queue is empty;
         * {@code false} otherwise
         */
        public boolean isEmpty() {
            return n == 0;
        }

        /**
         * Is {@code i} an index on this priority queue?
         *
         * @param i an index
         * @return {@code true} if {@code i} is an index on this priority queue;
         * {@code false} otherwise
         * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
         */
        public boolean contains(int i) {
            validateIndex(i);
            return qp[i] != -1;
        }

        /**
         * Returns the number of keys on this priority queue.
         *
         * @return the number of keys on this priority queue
         */
        public int size() {
            return n;
        }

        /**
         * Associates key with index {@code i}.
         *
         * @param i   an index
         * @param key the key to associate with index {@code i}
         * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
         * @throws IllegalArgumentException if there already is an item associated
         *                                  with index {@code i}
         */
        public void insert(int i, Key key) {
            validateIndex(i);
            if (contains(i)) throw new IllegalArgumentException("index is already in the priority queue");
            n++;
            qp[i] = n;
            pq[n] = i;
            keys[i] = key;
            swim(n);
        }

        /**
         * Returns an index associated with a minimum key.
         *
         * @return an index associated with a minimum key
         * @throws NoSuchElementException if this priority queue is empty
         */
        public int minIndex() {
            if (n == 0) throw new NoSuchElementException("Priority queue underflow");
            return pq[1];
        }

        /**
         * Returns a minimum key.
         *
         * @return a minimum key
         * @throws NoSuchElementException if this priority queue is empty
         */
        public Key minKey() {
            if (n == 0) throw new NoSuchElementException("Priority queue underflow");
            return keys[pq[1]];
        }

        /**
         * Removes a minimum key and returns its associated index.
         *
         * @return an index associated with a minimum key
         * @throws NoSuchElementException if this priority queue is empty
         */
        public int delMin() {
            if (n == 0) throw new NoSuchElementException("Priority queue underflow");
            int min = pq[1];
            exch(1, n--);
            sink(1);
            assert min == pq[n + 1];
            qp[min] = -1;        // delete
            keys[min] = null;    // to help with garbage collection
            pq[n + 1] = -1;        // not needed
            return min;
        }

        /**
         * Returns the key associated with index {@code i}.
         *
         * @param i the index of the key to return
         * @return the key associated with index {@code i}
         * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
         * @throws NoSuchElementException   no key is associated with index {@code i}
         */
        public Key keyOf(int i) {
            validateIndex(i);
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            else return keys[i];
        }

        /**
         * Change the key associated with index {@code i} to the specified value.
         *
         * @param i   the index of the key to change
         * @param key change the key associated with index {@code i} to this key
         * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
         * @throws NoSuchElementException   no key is associated with index {@code i}
         */
        public void changeKey(int i, Key key) {
            validateIndex(i);
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            keys[i] = key;
            swim(qp[i]);
            sink(qp[i]);
        }

        /**
         * Change the key associated with index {@code i} to the specified value.
         *
         * @param i   the index of the key to change
         * @param key change the key associated with index {@code i} to this key
         * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
         * @deprecated Replaced by {@code changeKey(int, Key)}.
         */
        @Deprecated
        public void change(int i, Key key) {
            changeKey(i, key);
        }

        /**
         * Decrease the key associated with index {@code i} to the specified value.
         *
         * @param i   the index of the key to decrease
         * @param key decrease the key associated with index {@code i} to this key
         * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
         * @throws IllegalArgumentException if {@code key >= keyOf(i)}
         * @throws NoSuchElementException   no key is associated with index {@code i}
         */
        public void decreaseKey(int i, Key key) {
            validateIndex(i);
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            if (keys[i].compareTo(key) == 0)
                throw new IllegalArgumentException("Calling decreaseKey() with a key equal to the key in the priority queue");
            if (keys[i].compareTo(key) < 0)
                throw new IllegalArgumentException("Calling decreaseKey() with a key strictly greater than the key in the priority queue");
            keys[i] = key;
            swim(qp[i]);
        }

        public void increaseKey(int i, Key key) {
            validateIndex(i);
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            if (keys[i].compareTo(key) == 0)
                throw new IllegalArgumentException("Calling increaseKey() with a key equal to the key in the priority queue");
            if (keys[i].compareTo(key) > 0)
                throw new IllegalArgumentException("Calling increaseKey() with a key strictly less than the key in the priority queue");
            keys[i] = key;
            sink(qp[i]);
        }

        public void delete(int i) {
            validateIndex(i);
            if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
            int index = qp[i];
            exch(index, n--);
            swim(index);
            sink(index);
            keys[i] = null;
            qp[i] = -1;
        }
        // throw an IllegalArgumentException if i is an invalid index
        private void validateIndex(int i) {
            if (i < 0) throw new IllegalArgumentException("index is negative: " + i);
            if (i >= maxN) throw new IllegalArgumentException("index >= capacity: " + i);
        }
        private boolean greater(int i, int j) {
            return keys[pq[i]].compareTo(keys[pq[j]]) > 0;
        }
        private void exch(int i, int j) {
            int swap = pq[i];
            pq[i] = pq[j];
            pq[j] = swap;
            qp[pq[i]] = i;
            qp[pq[j]] = j;
        }
        private void swim(int k) {
            while (k > 1 && greater(k / 2, k)) {
                exch(k, k / 2);
                k = k / 2;
            }
        }

        private void sink(int k) {
            while (2 * k <= n) {
                int j = 2 * k;
                if (j < n && greater(j, j + 1)) j++;
                if (!greater(k, j)) break;
                exch(k, j);
                k = j;
            }
        }

        public Iterator<Integer> iterator() {
            return new HeapIterator();
        }

        private class HeapIterator implements Iterator<Integer> {
            // create a new pq
            private IndexMinPQ<Key> copy;

            // add all elements to copy of heap
            // takes linear time since already in heap order so no keys move
            public HeapIterator() {
                copy = new IndexMinPQ<Key>(pq.length - 1);
                for (int i = 1; i <= n; i++)
                    copy.insert(pq[i], keys[pq[i]]);
            }
            public boolean hasNext() {
                return !copy.isEmpty();
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
            public Integer next() {
                if (!hasNext()) throw new NoSuchElementException();
                return copy.delMin();
            }
        }
    }

    int totalCities;
    int start, goal;
    private DirectedEdge[] edgeTo;
    private int[] distTo;
    private IndexMinPQ<Integer> pq;
    private EdgeWeightedDigraph G;
    private HashMap<Integer, Integer> minyanimByDuration;
    private HashSet<Integer> citiesWithMinyan;
    int minDist = Integer.MAX_VALUE;
    int totalMinTrips = 0;
    boolean highwaysAdded = false;
    boolean solveItInvoked = false;

    /** Constructor: clients specify the number of cities involved in the
     * problem.  Cities are numbered 1..n, and for convenience, the "start" city
     * is labelled as "1", and the goal city is labelled as "n".
     *
     * @param nCities number of cities, must be greater than 1.
     */
    public FindMinyan(final int nCities) {
        // fill me in!
        if (nCities <= 1){
            throw new IllegalArgumentException();
        }
        this.totalCities = nCities;
        this.start = 1;
        this.goal = nCities;
        edgeTo = new DirectedEdge[nCities];
        distTo = new int[nCities];

        this.minyanimByDuration = new HashMap<>();
        this.citiesWithMinyan = new HashSet<>();

        pq = new IndexMinPQ<>(nCities);

        for (int i = 1; i < nCities; i++){
            distTo[i] = Integer.MAX_VALUE;
        }
        distTo[0] = 0;

        this.G = new EdgeWeightedDigraph(nCities);
    }



    /** Defines a highway leading (bi-directionally) between two cities, of
     * specified duration.
     *
     * @param city1 identifies a 1 <= city <= n, must differ from city2
     * @param city2 identifies a 1 <= city <= n, must differ from city1
     * @param duration the bi-directional duration of a trip between the two
     * cities on this highway, must be non-negative
     */
    public void addHighway(final int city1, final int duration, final int city2) {
        // fill me in!
        // adding edges
        if (city1 == city2){
            throw new IllegalArgumentException("city1 must differ from city2");
        }
        if (duration < 0){
            throw new IllegalArgumentException("duration must be non-negative");
        }
        DirectedEdge edge1 = new DirectedEdge(city1-1, city2-1, duration);
        DirectedEdge edge2 = new DirectedEdge(city2-1, city1-1, duration);
        G.addEdge(edge1);
        G.addEdge(edge2);

        highwaysAdded = true;
    }

    /** Specifies that a minyan can be found in the specified city.
     *
     * @param city identifies a 1 <= city <= n
     */
    public void hasMinyan(final int city) {
        if (city < 1 || city > totalCities){
            throw new IllegalArgumentException();
        }
        this.citiesWithMinyan.add(city-1);
    }

    /** Find a solution to the FindMinyan problem based on state specified by the
     * constructor, addHighway(), and hasMinyan() API.  Clients access the
     * solution through the shortestDuration() and nShortestDurationTrips() APIs.
     */
    public void solveIt() {
        if (!highwaysAdded){
            minDist = -1;
            totalMinTrips = -1;
            return;
        }
        // initial dijkstra call
        pq.insert(0, 0);
        while (!pq.isEmpty()){
            relax(G, pq.delMin());
        }

        for (int minyan : citiesWithMinyan){
            minyanimByDuration.put(minyan, distTo(minyan));
        }

        // dijkstra to find distance from minyan to goal
        for (int minyan : citiesWithMinyan){
            pq = new IndexMinPQ<>(totalCities);     //reset pq
            for (int i = 0; i < totalCities; i++){  //reset distTo[]
                distTo[i] = Integer.MAX_VALUE;
            }
            distTo[minyan] = 0;                     //source node distance set to 0

            pq.insert(minyan, 0);               //traverse graph
            while (!pq.isEmpty()){
                relax(G, pq.delMin());
            }
            minyanimByDuration.put(minyan, (minyanimByDuration.get(minyan)+distTo(goal-1)));
        }

        // iterate over minyanim distance map to find shortestDuration and nShortestTrips

        for (int distance : minyanimByDuration.values()){
            if (minDist > distance){
                minDist = distance;
            }
        }
        for (int distance : minyanimByDuration.values()){
            if (minDist == distance){
                totalMinTrips++;
            }
        }

        solveItInvoked = true;
    }

    private void relax(EdgeWeightedDigraph G, int v){
        for (DirectedEdge e : G.adj(v)){
            int w = e.to();
            if (distTo[w] > (distTo[v] + e.weight())){
                distTo[w] = distTo[v] + e.weight();
                edgeTo[w] = e;
                if (pq.contains(w)){
                    pq.changeKey(w, distTo[w]);
                } else {
                    pq.insert(w, distTo[w]);
                }
            }
        }
    }
    private int distTo(int v){
        return distTo[v];
    }
    private boolean hasPathTo(int v){
        return distTo[v] < Integer.MAX_VALUE;
    }

    /** Returns the duration of the shortest trip satisfying the FindMinyan
     * constraints.
     *
     * @return duration of the shortest trip, undefined if client hasn't
     * previously invoked solveIt().
     */
    public int shortestDuration() {
        if (!solveItInvoked){
            return -1;
        }
        return minDist;
    }

    /** Returns the number of distinct trips that satisfy the FindMinyan
     * constraints.
     *
     * @return number of shortest duration trips, undefined if client hasn't
     * previously invoked solveIt()..
     */
    public int numberOfShortestTrips() {
        if (!solveItInvoked){
            return -1;
        }
        return totalMinTrips;
    }

} // FindMinyan
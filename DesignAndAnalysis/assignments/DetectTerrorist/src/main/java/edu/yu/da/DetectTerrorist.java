package edu.yu.da;

import java.util.HashMap;

/** Defines the API for specifying and solving the DetectTerrorist problem (see
 * the requirements document).
 *
 * Students MAY NOT change the public API of this class, nor may they add ANY
 * constructor.
 *
 * @author Avraham Leff
 */

public class DetectTerrorist {

    private int terroristIndex;
    private int[] passengers;
    private int other;
    private int thirdVal = -1;
    private int thirdIndex = -1;
    private int fourthVal = -1;
    private int fourthIndex = -1;
    private boolean done;


    /** Constructor: represents passengers to be detected as an array in which
     * the ith value is the weight of the ith passenger.  After the constructor
     * completes, clients can invoke getTerrorist() for a O(1) lookup cost.
     *
     * @param passengers an array of passenger weights, indexed 0..n-1.  All
     * passengers that are not terrorists have the same weight: that weight is
     * greater than the weight of the terrorist.  Exactly one passenger is a
     * terrorist.
     */
    public DetectTerrorist(final int[] passengers) {
        // fill me in!
        this.passengers = passengers;
        this.done = false;

        divideAndFind(this.passengers, passengers.length-1, 0);

//        System.out.println("other: " + other + ", other value: " + passengers[other]);
//        System.out.println("ti: " + terroristIndex + ", ti val: " + passengers[terroristIndex]);
//        System.out.println("thirdIndex: " + thirdIndex + ", thirdval: " + thirdVal);


        if (passengers[other] < passengers[terroristIndex]){
            terroristIndex = other;
        } else if (thirdVal != -1 && thirdVal < passengers[terroristIndex]){
            terroristIndex = thirdIndex;
        }

    }   // constructor


    private void divideAndFind(int[] data, int hi, int low){
        boolean addToRight = false;
        int length = hi - low + 1;

        boolean oddLength = length % 2 != 0;
        if (oddLength && thirdVal == -1){
            thirdVal = passengers[hi];
            thirdIndex = hi;
            hi -= 1;
        } else if (oddLength){
            addToRight = true;
        }

        int mid = low + (hi - low) / 2;

        int left = constantWeigh(low, mid);
        int right = constantWeigh(mid+1, hi);
        if (addToRight){
            right += thirdVal;
            thirdVal = -1;
            thirdIndex = -1;
        }

        if (done){
            if (left > right){ //go right
                terroristIndex = mid+1;
                other = hi;
            } else {
                terroristIndex = low;
                other = mid;
            }
            return;
        }
        if (oddLength && !addToRight && left == right) {
            terroristIndex = thirdIndex;
            return;
        }

        if (left > right){                          //go right
            divideAndFind(data, hi, mid+1);
        } else {                                    //go left
            thirdVal = -1;
            thirdIndex = -1;
            divideAndFind(data, mid, low);
        }
    }

    private int constantWeigh(int start, int end){
        if (end-start == 1){
            done = true;
        }
        int weight = 0;
        for (int i = start; i <= end; i++){  //include passenger[end] so <=
            weight += passengers[i];
        }
        return weight;
    }

    /** Returns the index of the passenger who has been determined to be a
     * terrorist.
     *
     * @return the index of the terrorist element.
     */
    public int getTerrorist() {
        return terroristIndex;
    }

} // DetectTerrorist
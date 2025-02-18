package edu.yu.introtoalgs;

/** Defines the API for the BTKeysAtSameLevel assignment: see the requirements
 * document for more information.
 *
 * Students MAY NOT change the constructor signature.  My test code will only
 * invoke the API defined below.
 *
 * @author Avraham Leff
 */

import java.util.ArrayList;
import java.util.List;

public class BTKeysAtSameLevel {

    List<List<Integer>> masterList;

    /** Constructor
     */
    public BTKeysAtSameLevel() {
        // fill this in!
        this.masterList = new ArrayList<>();
    }

    /** Given a String representation of a binary tree whose keys are Integers,
     * computes a List whose elements are List of keys at the same level (or
     * depth) from the root.  The top-level List is ordered by increasing
     * distance from the root so that the first List element consists of the
     * root's key, followed by the keys of all the root's immediate children,
     * etc.  At a given level, the List is ordered from left to right.  See the
     * requirements doc for an example.
     *
     * The String representation of the binary tree is defined as follows.  Keys
     * consist of a single integer value, from 0 to 9.  The string consists only
     * of parentheses and single digit integers: it must begin with an integer
     * (the value of the root node) followed by zero, one or two pairs of
     * parentheses. A pair of parentheses represents a child binary tree with
     * (recursively) the same structure. If a given node only has one child, that
     * child will be the left child node of the parent.
     *
     * Note: the "empty" tree is represented by the empty string, and this method
     * should therefore return an empty List.
     *
     * @param treeInStringRepresentation a binary tree represented in the
     * notation defined above.
     * @return a List whose elements are Lists of the tree's (integer) key
     * values, ordered in increasing distance from the root.  Each List element
     * contains the keys at a given level, ordered from left to right.
     * @throws IllegalArgumentException if the String is null, or doesn't
     * correspond to a valid String representation of a binary tree as defined
     * above.
     */
    public List<List<Integer>> compute(final String treeInStringRepresentation) {
        if (treeInStringRepresentation.isBlank() || treeInStringRepresentation.isEmpty()){
            throw new IllegalArgumentException("Invalid input: String is empty or blank");
        }
        char[] treeStructure = treeInStringRepresentation.toCharArray(); // char[] of String to iterate over
        int depth = 0;
        int openParen = 0;
        int closeParen = 0;
        boolean repeatParen = false;
        List<Integer> temp;
        for (int i = 0; i < treeStructure.length; i++){
            char current = treeStructure[i];

            // if int -> add to list at index depth
            if (current >= 48 && current <= 57){
                if (!hasListAtIndex(masterList, depth)){ // check if list at index depth exists
                    masterList.add(depth, new ArrayList<>()); // and if not add a list there
                }
                temp = masterList.get(depth);
                temp.add(Character.getNumericValue(current));
                repeatParen = false;
            }

            // if '(' -> upcoming child node, increase depth
            else if (current == '('){
                if (repeatParen){ // checking for back-t0-back '(' -> means a tree level was skipped
                    throw new IllegalArgumentException("Invalid input: depth skipped in tree structure");
                }
                else {
                    repeatParen = true;
                    depth++;
                    openParen++;
                }
            }

            // if ')' -> done with child node, decrease depth/return to parent
            else if (current == ')'){
                depth--;
                closeParen++;
                repeatParen = false;
            }
        }
        if (openParen != closeParen){ // checking to see if all the levels of the tree were completed with parens
            throw new IllegalArgumentException("Invalid input: improper tree structure");
        }

        return masterList;
    } // compute

    private boolean hasListAtIndex(List<List<Integer>> lst, int index){ //does this make sense
        try {
            lst.get(index);
            return true;
        } catch (IndexOutOfBoundsException e){
            return false;
        }
    }
}   // class
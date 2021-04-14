package edu.neu.coe.info6205.symbolTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BSTSimpleDriver {
    private static final int maxNumberOfInsertions = 1024;
    private static final int maxNumberOfDeletions = 512;

    //get an instance of Random
    public static ThreadLocalRandom getRandom() {
        return ThreadLocalRandom.current();
    }

    /**
     * get a random int between [min, max]
     * @param min
     * @param max
     * @return
     */
    public static int getRandomInt(int min, int max) {
        return getRandom().nextInt(max-min+1) + min;
    }


    /**
     * get a random int between [0,max)
     * @param max
     * @return
     */
    public static int getRandomInt(int max) {
        return getRandom().nextInt(max);
    }

    public static boolean getResultForProbability(double p) {
        return getRandomInt(1, 100) <= 100 * p;
    }

    public static double getMeanDepth(List<Integer> depths) {
        double sum = 0;
        for (int d : depths) {
            sum += d;
        }
        return sum / depths.size();
    }

    public static int getMaxDepth(List<Integer> depths) {
        int max = 0;
        for (int d : depths) {
            max = Math.max(max, d);
        }
        return max;
    }

    public static List<Integer> getInsertionList() {
        List<Integer> list = new ArrayList<Integer>(){{
            for (int i = 1; i <= maxNumberOfInsertions; i++) {
                add(i);
            }
        }};
        Collections.shuffle(list);
        return list;
    }

    public static void insertionInBST(BSTSimple bst, List<Integer> insertionNumList, List<Integer> keyList) {
        if (insertionNumList.size() == 0) {
            return;
        }

        int indexOfInsertionNum = getRandomInt(insertionNumList.size());
        int insertionNum = insertionNumList.get(indexOfInsertionNum);
        bst.put(insertionNum, insertionNum);
        keyList.add(insertionNum);
        insertionNumList.remove(indexOfInsertionNum);
    }

    public static void deletionInBST(BSTSimple bst, List<Integer> keyList, boolean isHibbardDeletion) {
        if (keyList.size() == 0) {
            return;
        }

        int indexOfDeletionNum = getRandomInt(keyList.size());
        int deletionNum = keyList.get(indexOfDeletionNum);
        if (isHibbardDeletion) {
            bst.hibbardDelete(deletionNum);
        } else {
            bst.delete(deletionNum);
        }
        keyList.remove(indexOfDeletionNum);
    }

    // this method yields a BST with 1024 key additions randomly with 512 deletions (not do all the additions and then all the deletions).
    public static int yieldRequiredBST(boolean isHibbardDeletion) {
        BSTSimple bst = new BSTSimple();
        int insertions = 0;
        int deletions = 0;

        List<Integer> insertionNumList = getInsertionList();
        List<Integer> keyList = new ArrayList<>();

        // 1024 + 512
        for (int i = 0; i < maxNumberOfDeletions + maxNumberOfInsertions; i++) {
            if (keyList.size() == 0) {
                insertionInBST(bst, insertionNumList, keyList);
                insertions++;
                continue;
            }

            if (insertionNumList.size() == 0) {
                deletionInBST(bst, keyList, isHibbardDeletion);
                deletions++;
                continue;
            }

            int insertionPossibility = (maxNumberOfInsertions - insertions) /
                    (maxNumberOfInsertions + maxNumberOfDeletions - insertions - deletions);

            if (getResultForProbability(insertionPossibility)) {
                insertionInBST(bst, insertionNumList, keyList);
                insertions++;
            } else {
                deletionInBST(bst, keyList, isHibbardDeletion);
                deletions++;
            }
        }

        return bst.depth();
    }

    public static void main(String[] args) {
        List<Integer> depthsForRandomlyLeftRightDeletion = new ArrayList<>();
        List<Integer> depthsForHibbardDeletion = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            depthsForRandomlyLeftRightDeletion.add(yieldRequiredBST(false));
            depthsForHibbardDeletion.add(yieldRequiredBST(true));
        }

        System.out.println("Mean depth for 100 binary search trees which use randomly left-right deletion: " + getMeanDepth(depthsForRandomlyLeftRightDeletion));
        System.out.println("Max depth for 100 binary search trees which use randomly left-right deletion: " + getMaxDepth(depthsForRandomlyLeftRightDeletion));
        System.out.println("Mean depth for 100 binary search trees which use hibbard deletion: " + getMeanDepth(depthsForHibbardDeletion));
        System.out.println("Max depth for 100 binary search trees which use hibbard deletion: " + getMaxDepth(depthsForHibbardDeletion));
    }
}

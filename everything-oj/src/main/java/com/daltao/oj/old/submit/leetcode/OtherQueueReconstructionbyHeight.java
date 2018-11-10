package com.daltao.oj.old.submit.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by dalt on 2017/11/22.
 */
public class OtherQueueReconstructionbyHeight {
    public static class Compare implements Comparator<int[]> {
        public int compare(int[] a, int[] b){
            if(a[0] != b[0])
                return b[0] - a[0];
            return a[1] - b[1];
        }
    }
    public int[][] reconstructQueue(int[][] people) {
        Arrays.sort(people, new Compare());
        ArrayList<int[]> result = new ArrayList<>();
        for(int[] person: people){
            result.add(person[1], person);
        }

        for(int i=0;i<people.length;i++){
            people[i] = result.get(i);
        }
        return people;
    }
}

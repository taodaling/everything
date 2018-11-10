package com.daltao.oj.old.submit.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Definition for an interval.
 * public class Interval {
 * int start;
 * int end;
 * Interval() { start = 0; end = 0; }
 * Interval(int s, int e) { start = s; end = e; }
 * }
 */
class InsertInterval {

    public static void main(String[] args) {
        List<Interval> intervals = Arrays.asList(
                new Interval(1, 3), new Interval(4, 5)
        );

        new InsertInterval().insert(intervals, new Interval(2, 4));
    }

    public List<Interval> insert(List<Interval> intervals, Interval newInterval) {
        intervals = new ArrayList<>(intervals);

        //at first, find the insert pos
        int insertIndex = 0;
        for (int bound = intervals.size(); insertIndex < bound && intervals.get(insertIndex).start < newInterval.start; insertIndex++)
            ;
        intervals.add(insertIndex, newInterval);
        int right = insertIndex + 1;
        for (int bound = intervals.size(); right < bound && intervals.get(right).start <= newInterval.end; right++) {
            newInterval.end = Math.max(newInterval.end, intervals.get(right).end);
        }
        int left = insertIndex - 1;
        for (; left >= 0 && intervals.get(left).end >= newInterval.start; left--) {
            newInterval.start = intervals.get(left).start;
            newInterval.end = Math.max(newInterval.end, intervals.get(left).end);
        }
        intervals.set(left + 1, newInterval);

        List<Interval> result = new ArrayList<>();
        result.addAll(intervals.subList(0, left + 2));
        result.addAll(intervals.subList(right, intervals.size()));

        Pattern p = Pattern.compile("\\s*\\d+(\\.\\d+)?(e\\d+)?\\s*");
       // p.matcher("").matches();
        return result;

    }


    public static class Interval {
        int start;
        int end;

        Interval() {
            start = 0;
            end = 0;
        }

        Interval(int s, int e) {
            start = s;
            end = e;
        }

        @Override
        public String toString() {
            return start + "," + end;
        }
    }
}
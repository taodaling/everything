package com.daltao.oj.old.submit.leetcode;

import java.util.Arrays;
import java.util.Comparator;

public class TwoSum {
    public static void main(String[] arg) {
        System.out.println(Arrays.toString(
                new TwoSum().twoSum(new int[]{3, 3}, 6)
        ));
    }

    static class Element {
        final int num;
        final int index;

        public Element(int num, int index) {
            this.num = num;
            this.index = index;
        }

        @Override
        public int hashCode() {
            return num;
        }

        @Override
        public boolean equals(Object obj) {
            return num == ((Element) obj).num;
        }
    }

    static final Comparator<Element> ELEMENT_COMPARATOR = new Comparator<Element>() {
        @Override
        public int compare(Element o1, Element o2) {
            return o1.num > o2.num ? 1 : o1.num < o2.num ? -1 : 0;
        }
    };

    public int[] twoSum(int[] nums, int target) {
        Element[] elements = new Element[nums.length];
        for (int i = 0, bound = nums.length; i < bound; i++) {
            elements[i] = new Element(nums[i], i);
        }


        //Sort the array with ascending order
        Arrays.sort(elements, ELEMENT_COMPARATOR);

        //Quickly find solution with binarysearch
        for (int i = 0, bound = elements.length; i < bound; i++) {
            Element a = elements[i];
            Element b = new Element(target - a.num, -1);
            if (a.num == b.num) {
                if (i < elements.length - 1 && elements[i + 1].num == a.num) {
                    return new int[]{a.index, elements[i + 1].index};
                }
            }
            int bindex = Arrays.binarySearch(elements, b, ELEMENT_COMPARATOR);
            if (bindex >= 0) {
                return new int[]{a.index, elements[bindex].index};
            }
        }
        return null;
    }
}
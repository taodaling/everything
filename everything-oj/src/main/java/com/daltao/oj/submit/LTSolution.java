package com.daltao.oj.submit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LTSolution {

    public static void main(String[] args) {
        System.out.println(new Solution()
                .constructFromPrePost(
                        new int[]{1, 2, 4, 5, 3, 6, 7},
                        new int[]{4, 5, 2, 6, 7, 3, 1}
                ));
    }


    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    static class Solution {
        public TreeNode constructFromPrePost(int[] pre, int[] post) {
            Map<Integer, Integer> indexPostIndexByValue = inverseIndexOf(post);
            return buildTreeFrom(pre, 0, pre.length, post, 0, post.length, indexPostIndexByValue);
        }

        private TreeNode buildTreeFrom(int[] pre, int prebpos, int preepos, int[] post,
                                       int postbpos, int postepos, Map<Integer, Integer> inverseIndexOfPost) {
            if (preepos <= prebpos) {
                return null;
            }

            System.out.println(Arrays.toString(Arrays.copyOfRange(pre, prebpos, preepos)));
            System.out.println(Arrays.toString(Arrays.copyOfRange(post, postbpos, postepos)));

            TreeNode node = new TreeNode(pre[prebpos]);
            if (preepos <= prebpos + 1) {
                return node;
            }

            int index = inverseIndexOfPost.get(pre[prebpos + 1]);
            node.left = buildTreeFrom(pre, prebpos + 1, prebpos + 1 + (index - postbpos) + 1, post,
                    postbpos, index + 1, inverseIndexOfPost);
            node.right = buildTreeFrom(pre, prebpos + 1 + (index - postbpos) + 1, preepos, post, index + 1,
                    postepos - 1, inverseIndexOfPost);
            return node;
        }

        private Map<Integer, Integer> inverseIndexOf(int[] data) {
            int n = data.length;
            Map<Integer, Integer> map = new HashMap(n);
            for (int i = 0; i < n; i++) {
                map.put(data[i], i);
            }
            return map;
        }
    }

}

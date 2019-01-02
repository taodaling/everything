package com.daltao.oj.submit;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class LTSolution {

    public static void main(String[] args) {
        TreeNode root = new TreeNode(0);
        root.left = new TreeNode(1);
        root.right = new TreeNode(2);
        root.right.right = new TreeNode(3);

        System.out.println(new Solution().minCameraCover(root));
    }

     public static class TreeNode {
         int val;
         TreeNode left;
         TreeNode right;
         TreeNode(int x) { val = x; }

         @Override
         public String toString() {
             return "" + val
                     ;
         }
     }

    static class Solution {
        public int minCameraCover(TreeNode root) {
            if(root.left == null && root.right == null)
            {
                return 1;
            }

            int[] res = new int[1];
            dfs(root, res);

            return res[0];
        }

        public static boolean dfs(TreeNode root, int[] result)
        {
            if(root == null)
            {
                return true;
            }

            if(dfs(root.left, result) && dfs(root.right, result))
            {
                return false;
            }

            result[0]++;
            return true;
        }
    }

}

package com.daltao.oj.old.submit.leetcode;

import java.util.Arrays;

public class QueueReconstructionbyHeight {
    private static final int INF = -100000000;
    
    public int[][] reconstructQueue(int[][] people) {
        if(people.length == 0)
        {
            return people;
        }

        //Sort at first
        int[][] data = people.clone();
        Arrays.sort(data, (a, b) -> a[0] != b[0] ? a[0] - b[0] : b[1] - a[1]);
        
        //Then build segment tree
        int n = data.length;
        SegmentTree tree = SegmentTree.buildTree(0, n - 1, data);
        
        int[][] ret = new int[n][2];
        
        //Find the min segment node
        for(int i = 0; i < n; i++)
        {
            int index = tree.getMinValue();
            ret[i] = data[index];
            
            tree.sub(0, index - 1, 1);
            tree.sub(index, index, INF);
        }
        
        return ret;
    }
    
    public static class SegmentTree{
        
        private SegmentTree (){}
        
        private static class Node{
            int dirty;
            
            CacheObject cache;
            
            Node left;
            Node right;
            
            int leftBound;
            int rightBound;
            
            public CacheObject getCache()
            {
                CacheObject ret = (CacheObject)cache.clone();
                ret.cachedMinFront += dirty;
                return ret;
            }
            
            public void updateCache()
            {
                CacheObject leftCache = left.getCache();
                CacheObject rightCache = right.getCache();
                cache = leftCache.compareTo(rightCache) < 0 ? leftCache : rightCache;
            }
        }
        
        private Node root;
        
        private static class CacheObject implements Comparable<CacheObject>, Cloneable{
            int cachedMinHeight;
            int cachedMinFront;
            int cachedMinValue;
            
            public int compareTo(CacheObject obj)
            {
                int frontDiff = cachedMinFront - obj.cachedMinFront;
                if(frontDiff != 0)
                {
                    return frontDiff;
                }
                return cachedMinValue - obj.cachedMinValue;
            }
            
            public Object clone()
            {
                try{
                    return super.clone();
                }catch(Exception e){
                    return null;
                }
            }
        }
        
        private static Node buildNode(int leftBound, int rightBound, int[][] initData)
        {
            Node node = new Node();
            node.leftBound = leftBound;
            node.rightBound = rightBound;
            
            if(leftBound == rightBound)
            {
                CacheObject cache = new CacheObject();
                cache.cachedMinHeight = initData[leftBound][0];
                cache.cachedMinFront = initData[leftBound][1];
                cache.cachedMinValue = leftBound;
                
                node.cache = cache;
                return node;
            }
            
            int mid = (leftBound + rightBound) / 2;
            node.left = buildNode(leftBound, mid, initData);
            node.right = buildNode(mid + 1, rightBound, initData);
            
            node.updateCache();
            
            return node;
        }
        
        public static SegmentTree buildTree(int left, int right, int[][] initData)
        {
            SegmentTree ret = new SegmentTree();
            ret.root = buildNode(left, right, initData);
            return ret;
        }
        
        public void sub(int leftBound, int rightBound, int val)
        {
            sub(root, leftBound, rightBound, val);
        }
        
        private static void sub(Node root, int leftBound, int rightBound, int val)
        {
            if(root.leftBound > rightBound || root.rightBound < leftBound)
            {
                return;
            }
            if(root.leftBound >= leftBound && root.rightBound <= rightBound)
            {
                root.dirty -= val;
                return;
            }
            
            sub(root.left, leftBound, rightBound, val);
            sub(root.right, leftBound, rightBound, val);
            
            root.updateCache();
        }
        
        public int getMinValue()
        {
            return root.cache.cachedMinValue;  
        }
    }
}
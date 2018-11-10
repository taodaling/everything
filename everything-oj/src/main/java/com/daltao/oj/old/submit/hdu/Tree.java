package com.daltao.oj.old.submit.hdu;

/**
 * Created by dalt on 2018/1/8.
 */
public class Tree {

    public static class LCTNode {
        static LCTNode NIL = new LCTNode();
        LCTNode father;
        LCTNode preferChild;
        SplayNode<LCTNode> treeNode = new SplayNode<LCTNode>(this);

        public LCTNode access() {
            LCTNode x = this;
            x.treeNode.splay();
            //Split the prefer child of x
            x.treeNode.right.father = SplayNode.NIL;
            x.treeNode.right.getMin().val.father = x;
            x.treeNode.right = SplayNode.NIL;
            x.treeNode.update();
            LCTNode joint = x;
            while (true) {
                x = x.treeNode.getMin().val;
                LCTNode father = x.father;
                if (father == NIL) {
                    return joint;
                }
                joint = father;
                father.preferChild.switchToNormal();
                x.switchToPrefer();
                x = father;
            }
        }

        public LCTNode getRoot() {
            access();
            return treeNode.getMin().val;
        }

        public void cut() {
            access();

            //cut auxiliary tree
            treeNode.splay();
            treeNode.left.father = SplayNode.NIL;
            LCTNode oldFather = treeNode.left.getMax().val;
            treeNode.left = SplayNode.NIL;

            //cut the actual tree
            father = NIL;
            if (oldFather.preferChild == this) {
                oldFather.preferChild = NIL;
            }
        }

        public void join(LCTNode f) {
            LCTNode root = getRoot();
            root.father = f;
        }

        public LCTNode lca(LCTNode a, LCTNode b) {
            b.access();
            LCTNode lca = a.access();
            lca.switchToNormal();
            return lca;
        }

        //Change the prefer edge linked to father into normal edge
        public void switchToNormal() {
            treeNode.splay();
            treeNode.left.father = SplayNode.NIL;
            LCTNode f = treeNode.left.getMax().val;
            treeNode.left = SplayNode.NIL;
            treeNode.update();
            father = f;
            father.preferChild = NIL;
        }

        //Change the prefer edge linked to father into normal edge
        public void switchToPrefer() {
            treeNode.splay();
            father.treeNode.splay();
            father.treeNode.asRight(treeNode);
            father.treeNode.update();
            father.preferChild = this;
        }

        public void batchSet(LCTNode a, LCTNode b, int v) {
            LCTNode lca = lca(a, b);
            lca.treeNode.batchSet(v);
        }

        public void batchMod(LCTNode a, LCTNode b, int v) {
            LCTNode lca = lca(a, b);
            lca.treeNode.batchMod(v);
        }
    }

    public static class SplayNode<T> {
        static SplayNode NIL = new SplayNode(null);
        static int[] vals = new int[5];
        static int[] valCnts = new int[5];
        static int[] orderedVals = new int[5];
        static int[] orderedCnts = new int[5];
        SplayNode<T> left, right, father;
        T val;
        boolean batchSetValueFlag;
        int batchSetValue;
        boolean batchModValueFlag;
        int batchModValue;
        int weight;
        int theLargestWeight;
        int theLargestWeightCnt;
        int theSecondLargestWeight;
        int theSecondLargestWeightCnt;
        int size;

        public SplayNode(T val) {
            this.val = val;
        }

        static void countTheKthValue(int valCnt, int k) {
            int lastMax;
            int curMax = Integer.MAX_VALUE;
            int curCnt;
            for (int i = 0; i < k; i++) {
                lastMax = curMax;
                curMax = Integer.MIN_VALUE;
                curCnt = 0;
                for (int j = 0; j < valCnt; j++) {
                    if (vals[j] >= lastMax || valCnts[j] == 0) {
                        continue;
                    }
                    if (vals[j] > curMax) {
                        curMax = vals[j];
                        curCnt = 1;
                    } else if (vals[j] == curMax) {
                        curCnt++;
                    }
                }
                if (curMax == Integer.MIN_VALUE) {
                    for (int j = i; j < k; j++) {
                        orderedCnts[j] = 0;
                    }
                    return;
                }
                orderedVals[i] = curMax;
                orderedCnts[i] = curCnt;
            }
            return;
        }

        public static void zig(SplayNode x) {
            SplayNode y = x.father;
            SplayNode z = y.father;
            SplayNode b = x.right;

            x.asRight(y);
            y.asLeft(b);
            z.takeThePlaceOf(y, x);
            y.update();
        }

        public static void zag(SplayNode x) {
            SplayNode y = x.father;
            SplayNode z = y.father;
            SplayNode b = x.left;

            x.asLeft(y);
            y.asRight(b);
            z.takeThePlaceOf(y, x);
            y.update();
        }

        public void splay() {
            SplayNode y, z;
            while ((y = this.father) != NIL) {
                if ((z = y.father) == NIL) {
                    y.consume();
                    this.consume();
                    if (this == y.left) {
                        zig(this);
                    } else {
                        zag(this);
                    }
                } else {
                    z.consume();
                    y.consume();
                    this.consume();
                    if (this == y.left) {
                        if (y == z.left) {
                            zig(y);
                            zig(this);
                        } else {
                            zig(this);
                            zag(this);
                        }
                    } else {
                        if (y == z.left) {
                            zag(this);
                            zig(this);
                        } else {
                            zag(y);
                            zag(this);
                        }
                    }
                }
            }
            this.update();
        }

        public SplayNode<T> getMin() {
            SplayNode<T> x = this;
            while (x.left != NIL) {
                x = x.left;
            }
            x.splay();
            return x;
        }

        public SplayNode<T> getMax() {
            SplayNode<T> x = this;
            while (x.right != NIL) {
                x = x.right;
            }
            x.splay();
            return x;
        }

        public void asLeft(SplayNode x) {
            x.father = this;
            left = x;
        }

        public void asRight(SplayNode x) {
            x.father = this;
            right = x;
        }

        public void takeThePlaceOf(SplayNode oldOne, SplayNode newOne) {
            if (left == oldOne) {
                asLeft(newOne);
            } else {
                asRight(newOne);
            }
        }

        public void update() {
            size = left.size + right.size + 1;
            if (batchSetValueFlag) {
                theLargestWeight = batchSetValue;
                theLargestWeightCnt = size;
                theSecondLargestWeight = 0;
                theSecondLargestWeightCnt = 0;
            } else {
                int valCnt = 1;
                vals[0] = weight;
                valCnts[0] = 1;
                if (left != NIL) {
                    vals[valCnt] = left.theLargestWeight;
                    valCnts[valCnt] = left.theLargestWeightCnt;
                    valCnt++;
                    vals[valCnt] = left.theSecondLargestWeight;
                    valCnts[valCnt] = left.theSecondLargestWeightCnt;
                    valCnt++;
                }
                if (right != NIL) {
                    vals[valCnt] = right.theLargestWeight;
                    valCnts[valCnt] = right.theLargestWeightCnt;
                    valCnt++;
                    vals[valCnt] = right.theSecondLargestWeight;
                    valCnts[valCnt] = right.theSecondLargestWeightCnt;
                    valCnt++;
                }
                countTheKthValue(valCnt, 2);
                theLargestWeight = orderedVals[0];
                theLargestWeightCnt = orderedCnts[0];
                theSecondLargestWeight = orderedVals[1];
                theSecondLargestWeight = orderedCnts[1];
            }

            if (batchModValueFlag) {
                theLargestWeight += batchModValue;
                theSecondLargestWeight += batchModValue;
            }
        }

        public void batchSet(int val) {
            batchSetValue = val;
            batchSetValueFlag = true;
            batchModValueFlag = false;

        }

        public void batchMod(int val) {
            batchModValueFlag = true;
            batchModValue = val;

        }

        public void consume() {
            if (batchSetValueFlag) {
                batchSetValueFlag = false;
                left.batchSet(batchSetValue);
                right.batchSet(batchSetValue);
                weight = batchSetValue;
            }
            if (batchModValueFlag) {
                batchModValueFlag = false;
                left.batchMod(batchModValue);
                right.batchMod(batchModValue);
                weight += batchModValue;
            }
        }
    }
}
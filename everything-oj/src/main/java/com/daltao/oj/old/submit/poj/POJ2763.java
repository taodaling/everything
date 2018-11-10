package com.daltao.oj.old.submit.poj;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dalt on 2018/1/4.
 */
public class POJ2763 {
    public static BlockReader input;
    static int nodeNum;
    static int requestNum;
    static int questionCnt;
    static StringBuilder result = new StringBuilder();
    private static int LIMIT = 100001;
    static Question[] questions = new Question[LIMIT];
    static int[] weights = new int[LIMIT];
    static Node[] edgeSrc = new Node[LIMIT];
    static Node[] edgeDst = new Node[LIMIT];
    static Node[] nodes = new Node[LIMIT + 1];
    static Bit bit = new Bit(LIMIT);
    static int[][] requests = new int[LIMIT][3];

    static {
        for (int i = 0; i <= LIMIT; i++) {
            nodes[i] = new Node();
        }
        for (int i = 0; i < LIMIT; i++) {
            questions[i] = new Question();
        }
    }

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\POJ2763.in"));
        input = new BlockReader(System.in);

        while (input.hasMore()) {
            POJ2763 solution = new POJ2763();
            solution.init();
            System.out.print(solution.solve());
        }


    }

    public void init() {
        nodeNum = input.nextInteger();
        requestNum = input.nextInteger();
        Node pos = nodes[input.nextInteger()];
        for (int i = 1; i <= nodeNum; i++) {
            nodes[i].init(i);
        }
        bit.init(nodeNum);

        for (int i = 1; i < nodeNum; i++) {
            edgeSrc[i] = nodes[input.nextInteger()];
            edgeDst[i] = nodes[input.nextInteger()];
            weights[i] = input.nextInteger();

            edgeSrc[i].children.add(edgeDst[i]);
            edgeDst[i].children.add(edgeSrc[i]);
        }

        questionCnt = 0;
        for (int i = 0; i < requestNum; i++) {
            requests[i][0] = input.nextInteger();
            requests[i][1] = input.nextInteger();

            //Pick child
            if (requests[i][0] == 0) {
                questions[questionCnt].n1 = pos;
                questions[questionCnt].n2 = nodes[requests[i][1]];
                pos = questions[questionCnt].n2;
                questions[questionCnt].n1.questionList.add(questions[questionCnt]);
                questions[questionCnt].n2.questionList.add(questions[questionCnt]);
                requests[i][2] = questionCnt++;
            } else {
                requests[i][2] = input.nextInteger();
            }
        }

        result.setLength(0);
    }

    public String solve() {
        Node root = nodes[1];
        buildTree(root, null);
        lca(root);
        buildHeavyChain(root);

        for (int i = 0; i < requestNum; i++) {
            int[] request = requests[i];
            if (request[0] == 0) {
                Question question = questions[request[2]];
                result.append(sumOf(question.n1, question.n2, question.lca)).append('\n');
            } else {
                int changeVal = request[2] - weights[request[1]];
                weights[request[1]] = request[2];
                updateEdge(edgeSrc[request[1]], edgeDst[request[1]], changeVal);
            }
        }
        return result.toString();
    }

    public void buildTree(Node root, Node father) {
        root.father = father;
        root.children.remove(father);
        for (Node child : root.children) {
            buildTree(child, root);
        }
    }

    public void updateEdge(Node src, Node dst, int weight) {
        if (src.father == dst) {
            bit.update(src.heavyChainId, weight);
        } else {
            bit.update(dst.heavyChainId, weight);
        }
    }

    public void buildHeavyChain(Node root) {
        buildHeavyChain1(root);
        buildHeavyChain2(root, root, new int[]{1});
        for (int i = 1; i < nodeNum; i++) {
            Node src = edgeSrc[i];
            Node dst = edgeDst[i];
            int weight = weights[i];
            updateEdge(src, dst, weight);
        }
    }

    public int sumOf(Node a, Node b, Node lca) {
        return sumOf(a, lca) + sumOf(b, lca);
    }

    public int sumOf(Node a, Node lca) {
        int sum = 0;
        while (a.heavyChainRoot != lca.heavyChainRoot) {
            sum += bit.sum(a.heavyChainRoot.heavyChainId, a.heavyChainId);
            a = a.heavyChainRoot.father;
        }
        if (lca != a) {
            sum += bit.sum(lca.heavyChainFollower.heavyChainId, a.heavyChainId);
        }
        return sum;
    }

    public int buildHeavyChain1(Node root) {
        int sum = 1;
        Node maxChildren = root;
        int maxSize = 0;
        for (Node child : root.children) {
            int childSize = buildHeavyChain1(child);
            sum += childSize;
            if (maxSize < childSize) {
                maxChildren = child;
            }
        }

        root.heavyChainFollower = maxChildren;
        return sum;
    }

    public void buildHeavyChain2(Node root, Node heavyChainRoot, int[] idAllocator) {
        root.heavyChainId = idAllocator[0]++;
        root.heavyChainRoot = heavyChainRoot;
        if (root.heavyChainFollower != root) {
            buildHeavyChain2(root.heavyChainFollower, heavyChainRoot, idAllocator);
        }
        for (Node child : root.children) {
            if (child == root.heavyChainFollower) {
                continue;
            }
            buildHeavyChain2(child, child, idAllocator);
        }
    }

    public void lca(Node root) {
        root.getRepr().ancestor = root;
        for (Node child : root.children) {
            lca(child);
            Node.union(child, root);
            root.getRepr().ancestor = root;
        }

        root.flag = 1;
        for (Question question : root.questionList) {
            if (question.n1.flag == 0 || question.n2.flag == 0) {
                continue;
            }
            question.lca = (question.n1 == root ? question.n2 : question.n1).getRepr().ancestor;
        }
    }

    public static class Question {
        Node n1;
        Node n2;
        Node lca;
    }

    public static class Node {
        Node p;
        List<Node> children = new ArrayList();
        int rank;
        int flag;
        int id;
        List<Question> questionList = new ArrayList();
        Node father;
        Node ancestor;
        Node heavyChainFollower;
        Node heavyChainRoot;
        int heavyChainId;

        public static void union(Node a, Node b) {
            a = a.getRepr();
            b = b.getRepr();
            if (a.rank == b.rank) {
                if (a == b) {
                    return;
                }
                a.rank++;
            }
            if (a.rank > b.rank) {
                b.p = a;
            } else {
                a.p = b;
            }
        }

        public void init(int id) {
            p = this;
            children.clear();
            rank = 0;
            flag = 0;
            this.id = id;
            questionList.clear();
            father = null;
            ancestor = null;
            heavyChainFollower = null;
            heavyChainRoot = null;
            heavyChainId = 0;
        }

        public Node getRepr() {
            return p == p.p ? p : (p = p.getRepr());
        }
    }

    public static class Bit {
        int[] data;
        int cap;
        int left;

        public Bit(int cap) {
            this.cap = cap;
            data = new int[cap + 1];
        }

        public void update(int i, int v) {
            for (; i <= cap; i += i & -i) {
                data[i] += v;
            }
        }

        public int sum(int i) {
            int sum = 0;
            for (; i > 0; i -= i & -i) {
                sum += data[i];
            }
            return sum;
        }

        public int sum(int i, int j) {
            return sum(j) - sum(i - 1);
        }

        public void init(int toX) {
            Arrays.fill(data, 1, toX + 1, 0);
        }
    }

    public static class BlockReader {
        static final int EOF = -1;
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        StringBuilder builder = new StringBuilder();

        public BlockReader(InputStream is) {
            this(is, 8192);
        }

        public BlockReader(InputStream is, int bufSize) {
            this.is = is;
            dBuf = new byte[bufSize];
            next = nextByte();
        }

        public void skipBlank() {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
        }

        public String nextBlock() {
            builder.setLength(0);
            skipBlank();
            while (next != EOF && !Character.isWhitespace(next)) {
                builder.append((char) next);
                next = nextByte();
            }
            return builder.toString();
        }

        public int nextInteger() {
            skipBlank();
            int ret = 0;
            boolean rev = false;
            if (next == '+' || next == '-') {
                rev = next == '-';
                next = nextByte();
            }
            while (next >= '0' && next <= '9') {
                ret = (ret << 3) + (ret << 1) + next - '0';
                next = nextByte();
            }
            return rev ? -ret : ret;
        }

        public int nextBlock(char[] data, int offset) {
            skipBlank();
            int index = offset;
            int bound = data.length;
            while (next != EOF && index < bound && !Character.isWhitespace(next)) {
                data[index++] = (char) next;
                next = nextByte();
            }
            return index - offset;
        }

        public boolean hasMore() {
            skipBlank();
            return next != EOF;
        }

        public int nextByte() {
            while (dPos >= dSize) {
                if (dSize == -1) {
                    return EOF;
                }
                dPos = 0;
                try {
                    dSize = is.read(dBuf);
                } catch (IOException e) {
                }
            }
            return dBuf[dPos++];
        }
    }

}

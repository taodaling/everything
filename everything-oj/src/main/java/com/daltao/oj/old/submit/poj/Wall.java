package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by dalt on 2017/12/11.
 */
public class Wall {
    static BlockReader reader;

    public static void main(String[] args) throws Exception {
        System.setIn(new FileInputStream("D:\\test\\poj\\Wall.in"));

        reader = new BlockReader(System.in);
        while (reader.hasMore()) {
            Wall wall = new Wall();
            wall.init();
            System.out.println(wall.solve());
        }
    }

    List<Vector2I> castleLocationList;

    int n;
    int feetAllowed;

    public void init() {
        n = reader.nextInteger();
        feetAllowed = reader.nextInteger();
        castleLocationList = new ArrayList(n);
        for (int i = 0; i < n; i++) {
            castleLocationList.add(new Vector2I(
                    reader.nextInteger(), reader.nextInteger()
            ));
        }
    }

    public int solve() {
        Convex convex = Convex.makeConvex(castleLocationList);
        double shortestWallLength = 2 * Math.PI * feetAllowed;
        for (int i = 0, bound = convex.size(); i < bound; i++) {
            Vector2I v1 = convex.get(i);
            Vector2I v2 = convex.get((i + 1) % bound);
            shortestWallLength += Math.sqrt(GeomUtils.dist2(v1, v2));
        }

        return (int) (shortestWallLength + 0.5);
    }

    public static class Vector2I {
        final int x;
        final int y;

        public Vector2I(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Vector2I sub(Vector2I other) {
            return new Vector2I(x - other.x, y - other.y);
        }


        public String toString() {
            return String.format("(%d,%d)", x, y);
        }


        public boolean equals(Object obj) {
            Vector2I vec = (Vector2I) obj;
            return x == vec.x && y == vec.y;
        }
    }

    public static class BlockReader {
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        static final int EOF = -1;

        public void skipBlank() {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
        }

        StringBuilder builder = new StringBuilder();

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

        public BlockReader(InputStream is) {
            this(is, 1024);
        }

        public BlockReader(InputStream is, int bufSize) {
            this.is = is;
            dBuf = new byte[bufSize];
            next = nextByte();
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
                    throw new RuntimeException(e);
                }
            }
            return dBuf[dPos++];
        }
    }

    public static class GeomUtils {
        private GeomUtils() {
        }

        public static int dist2(Vector2I a, Vector2I b) {
            int x = a.x - b.x;
            int y = a.y - b.y;
            return x * x + y * y;
        }

        public static boolean sameLine(Vector2I a, Vector2I b, Vector2I c) {
            return cmul(b.sub(a), c.sub(a)) == 0;
        }

        public static int cmul(Vector2I a, Vector2I b) {
            return a.x * b.y - a.y * b.x;
        }
    }

    public static class Convex extends AbstractList<Vector2I> {
        private Vector2I[] vectors;
        private Vector2I bl;
        private Vector2I tr;

        public Vector2I getBottomLeftCorner() {
            return bl;
        }

        public Vector2I getTopRightCorner() {
            return tr;
        }

        @Override
        public Vector2I get(int index) {
            return vectors[index];
        }


        public int size() {
            return vectors.length;
        }

        public static Convex makeConvex(List<Vector2I> vector2IList) {
            Convex result = new Convex();
            if (vector2IList.size() == 0) {
                result.vectors = vector2IList.toArray(new Vector2I[0]);
                return result;
            }

            //If all points located on same line
            Vector2I v1 = vector2IList.get(0);
            Vector2I v2 = vector2IList.get(0);
            Vector2I bl = vector2IList.get(0);
            Vector2I tr = vector2IList.get(0);
            boolean sameLineFlag = true;
            for (Vector2I vertex : vector2IList) {
                if (!GeomUtils.sameLine(v1, v2, vertex)) {
                    sameLineFlag = false;
                }
                v2 = vertex;
                if (bl.y > vertex.y || (bl.y == vertex.y && bl.x > vertex.x)) {
                    bl = vertex;
                }
                if (tr.y < vertex.y || (tr.y == vertex.y && tr.x < vertex.x)) {
                    tr = vertex;
                }
            }

            result.bl = bl;
            result.tr = tr;
            if (sameLineFlag) {
                if (bl.equals(tr)) {
                    result.vectors = new Vector2I[]{bl};
                } else {
                    result.vectors = new Vector2I[]{bl, tr};
                }
                return result;
            }

            //Remove all inner vertex, make vectors contains points on outline of convex
            //At first, quickSort by angle of vector bl-v
            //v < u equals to that bl-u is on the anticlockwise of bl-v
            //So we can simplify the procession of calculation because of -cmul(bl-v, bl-u)=v.compareTo(b)
            final Vector2I finalBl = bl;
            Vector2I[] vector2IListArray = vector2IList.toArray(new Vector2I[vector2IList.size()]);
            vector2IList = Arrays.asList(vector2IListArray);
            Arrays.sort(vector2IListArray, new Comparator<Vector2I>() {

                public int compare(Vector2I a, Vector2I b) {
                    int res = -GeomUtils.cmul(a.sub(finalBl), b.sub(finalBl));
                    if (res == 0) {
                        if (a.equals(finalBl)) {
                            return -1;
                        }
                        if (b.equals(finalBl)) {
                            return 1;
                        }
                    }
                    return res;
                }
            });
            //Remove all the vertex has the same angle but retain the farthest one
            int newSize = 1;
            for (int i = 2, bound = vector2IList.size(); i < bound; i++) {
                Vector2I candidate = vector2IListArray[newSize];
                Vector2I scanOne = vector2IListArray[i];
                if (GeomUtils.sameLine(candidate, scanOne, bl)) {
                    //Retain the farthest one in the vertexes with same angle
                    //Replace the candidate
                    if (GeomUtils.dist2(bl, scanOne) > GeomUtils.dist2(bl, candidate)) {
                        vector2IListArray[newSize] = scanOne;
                    }
                } else {
                    //Add the candidate
                    newSize++;
                    vector2IListArray[newSize] = scanOne;
                }
            }
            vector2IList = vector2IList.subList(0, newSize + 1);
            //Graham's Scan
            LinkedList<Vector2I> stack = new LinkedList();
            for (int i = 0, bound = vector2IList.size(); i < bound; i++) {
                Vector2I vec = vector2IList.get(i);
                while (stack.size() >= 2) {
                    Vector2I top1 = stack.removeLast();
                    Vector2I top2 = stack.getLast();
                    if (GeomUtils.cmul(top1.sub(top2), vec.sub(top2)) > 0) {
                        stack.addLast(top1);
                        break;
                    }
                }
                stack.addLast(vec);
            }
            result.vectors = stack.toArray(new Vector2I[stack.size()]);
            return result;
        }
    }
}

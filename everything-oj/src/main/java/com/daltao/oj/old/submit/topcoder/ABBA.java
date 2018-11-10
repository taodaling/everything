package com.daltao.oj.old.submit.topcoder;

public class ABBA {

    public String canObtain(String initial, String target) {
        int n = initial.length();
        int m = target.length();

        int[] bCnts = new int[m + 1];
        for (int i = 0; i < m; i++) {
            bCnts[i + 1] = bCnts[i] + target.charAt(i) - 'A';
        }

        KMPAutomaton kam = new KMPAutomaton(n);
        for(int i = 0; i < n; i++)
        {
            kam.build(initial.charAt(i));
        }

        kam.beginMatch();
        for(int i = 0; i < m; i++)
        {
            kam.match(target.charAt(i));
            if(kam.matchLast == n)
            {
                //Total match, do test
                int lCnt = bCnts[i - n + 1];
                int rCnt = bCnts[m] - bCnts[i + 1];

                if(i >= n && target.charAt(i - n) == 'A'
                        || lCnt != rCnt)
                {
                    continue;
                }

                return "Possible";
            }
        }

        kam.beginMatch();
        for(int i = m - 1; i >= 0; i--)
        {
            kam.match(target.charAt(i));
            if(kam.matchLast == n)
            {
                //Total match, do test
                int lCnt = bCnts[i];
                int rCnt = bCnts[m] - bCnts[i + n];

                if(i + n < m && target.charAt(i + n) == 'A'
                        || lCnt + 1 != rCnt)
                {
                    continue;
                }

                return "Possible";
            }
        }

        return "Impossible";
    }

    public static class KMPAutomaton {
        char[] data;
        int[] fail;
        int buildLast;
        int matchLast = 0;

        public KMPAutomaton(int cap) {
            data = new char[cap + 2];
            fail = new int[cap + 2];
            fail[0] = -1;
            buildLast = 0;
        }

        public void beginMatch() {
            matchLast = 0;
        }

        public void match(char c) {
            matchLast = visit(c, matchLast) + 1;
        }

        public int visit(char c, int trace) {
            while (trace >= 0 && data[trace + 1] != c) {
                trace = fail[trace];
            }
            return trace;
        }

        public void build(char c) {
            buildLast++;
            fail[buildLast] = visit(c, fail[buildLast - 1]) + 1;
            data[buildLast] = c;
        }
    }

}

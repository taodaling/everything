package com.daltao.oj.template;

public class Counter {
        int[] cnts;
        int[] versions;
        int now;

        public Counter(int n) {
            cnts = new int[n];
            versions = new int[n];
        }

        public void nextVersion() {
            now++;
        }

        private void check(int i) {
            if (versions[i] != now) {
                versions[i] = now;
                cnts[i] = 0;
            }
        }

        public int get(int i) {
            check(i);
            return cnts[i];
        }

        public void mod(int i, int v) {
            check(i);
            cnts[i] += v;
        }
    }
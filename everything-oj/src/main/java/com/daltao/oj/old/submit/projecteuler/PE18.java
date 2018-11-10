package com.daltao.oj.old.submit.projecteuler;

import cn.dalt.oj.old.template.BlockReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dalt on 2018/4/2.
 */
public class PE18 {
    public static final int MOD = (int) (1e9 + 7);
    public static BlockReader input;
    public static PrintStream output;

    public static void main(String[] args) throws FileNotFoundException {
        init();

        solve();

        output.flush();
    }

    public static void init() throws FileNotFoundException {
        input = new BlockReader(new FileInputStream("D:\\DataBase\\TESTCASE\\pe\\PE18.in"));
        output = System.out;
    }

    public static void solve() {
        final int DP = 1;
        final int VAL = 0;

        List<int[][]> triangle = new ArrayList<>();
        for (int i = 1; input.hasMore(); i++) {
            int[][] data = new int[i][2];
            for (int j = 0; j < i; j++) {
                data[j][VAL] = input.nextInteger();
                data[j][DP] = -1;
            }
            triangle.add(data);
        }

        int[][] lastRow = triangle.get(triangle.size() - 1);
        for (int i = 0, until = lastRow.length; i < until; i++) {
            lastRow[i][DP] = lastRow[i][VAL];
        }

        for (int i = triangle.size() - 2; i >= 0; i--) {
            int[][] curRow = triangle.get(i);
            for (int j = 0, until = curRow.length; j < until; j++) {
                curRow[j][DP] = Math.max(lastRow[j][DP], lastRow[j + 1][DP]) + curRow[j][VAL];
            }
            lastRow = curRow;
        }

        output.println(lastRow[0][DP]);
    }
}

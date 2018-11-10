package com.daltao.oj.old.submit.bzoj;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Created by Administrator on 2018/2/21.
 */
public class BZOJ1061Data {
    public static void main(String[] args) throws IOException {
        StringBuilder builder = new StringBuilder();
        int n = 1000;
        int m = 1001;
        builder.append(n).append(' ').append(m).append('\n');
        for (int i = 1; i <= n; i++) {
            builder.append(i).append(' ');
        }
        builder.append('\n');

        for (int i = 1; i < n; i++) {
            builder.append(i).append(' ').append(i + 1).append(' ').append(1000).append('\n');
        }

        OutputStream os = new FileOutputStream("D:\\DataBase\\TESTCASE\\bzoj\\BZOJ1061.in");
        os.write(builder.toString().getBytes(Charset.forName("ascii")));
        os.close();
    }
}

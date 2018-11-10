package com.daltao.oj.old.submit.pe;

import java.util.Calendar;

/**
 * Created by dalt on 2018/4/5.
 */
public class PE19 {
    int month = 0;

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        int sunday = 0;
        calendar.clear();
        for (int i = 1901; i <= 2000; i++) {
            for (int j = 0; j < 12; j++) {
                calendar.set(i, j, 1);
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    sunday++;
                }
            }
        }

        System.out.println(sunday);
    }
}

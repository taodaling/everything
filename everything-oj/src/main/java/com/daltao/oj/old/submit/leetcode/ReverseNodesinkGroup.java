package com.daltao.oj.old.submit.leetcode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/6/20.
 */
public class ReverseNodesinkGroup {
    public static void main(String[] args)
    {
        System.out.println(new ReverseNodesinkGroup().reverseKGroup(new ListNode(Arrays.<Integer>asList(0, 1, 2, 3)), 3));
    }

    public ListNode reverseKGroup(ListNode head, int k) {
        if (head == null)
            return null;
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode trace = dummy;
        ListNode parent = dummy;
        while (trace.next != null) {
            int t = 0;
            while (t < k && trace.next != null) {
                trace = trace.next;
                t++;
            }
            if (t == k) {
                trace = parent.next;
                while (t > 1) {
                    t--;
                    ListNode next = trace.next;
                    trace.next = next.next;
                    next.next = parent.next;
                    parent.next = next;
                }
                parent = trace;
                trace = parent;
            }
        }
        return dummy.next;
    }
}

class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
        val = x;
    }

    ListNode(List<Integer> list) {
        val = list.get(0);
        if (list.size() > 1) {
            next = new ListNode(list.subList(1, list.size()));
        }
    }

    @Override
    public String toString() {
        return val + (next != null ? "," + next.toString() : "");
    }
}

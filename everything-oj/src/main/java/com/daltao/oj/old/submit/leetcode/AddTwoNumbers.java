package com.daltao.oj.old.submit.leetcode;


/**
 * Created by Administrator on 2017/6/4.
 */
public class AddTwoNumbers {

    public static void main(String[] args)
    {
        System.out.println(new AddTwoNumbers().addTwoNumbers(new ListNode(342), new ListNode(465)));
    }

    static class ListNode {
        ListNode(int x)
        {
            val = x % 10;
            int adv = x / 10;
            if(adv != 0)
            {
                next = new ListNode(adv);
            }
        }

        @Override
        public String toString() {
            return "" + val + (next != null ? "->" + next.toString() : "");
        }

        int val;
        ListNode next;
    }

    static class NodeList{
        ListNode tail;
        ListNode root;
        int advance = 0;
        public NodeList(int x)
        {
            root = new ListNode(x % 10);
            tail = root;
            advance = x / 10;
        }
        public void append(int x)
        {
            x += advance;
            ListNode temp = new ListNode(x % 10);
            advance = x / 10;

            tail.next = temp;
            tail = temp;
        }
        public ListNode getResult()
        {
            while(advance != 0)
            {
                append(0);
            }
            return root;
        }

        @Override
        public String toString() {
            return root.toString();
        }
    }

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        NodeList list = new NodeList(l1.val + l2.val);
        l1 = l1.next;
        l2 = l2.next;

        while(l1 != null && l2 != null)
        {
            list.append(l1.val + l2.val);
            l1 = l1.next;
            l2 = l2.next;
        }
        while(l1 != null)
        {
            list.append(l1.val);
            l1 = l1.next;
        }
        while(l2 != null)
        {
            list.append(l2.val);
            l2 = l2.next;
        }
        return list.getResult();
    }
}

package com.daltao.utils;


import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DiffUtilsTest {

    private static List<Character> getCharacterList(String s) {
        List<Character> result = new ArrayList<>(s.length());
        for (char c : s.toCharArray()) {
            result.add(c);
        }
        return result;
    }

    @Test
    public void longestCommonSubSequence() {
        List<Character> a = getCharacterList("wguqihgoyvazowtdotyewgrhzfxq");
        List<Character> b = getCharacterList("nunxfxdgfrzfnsrwrqrepspdfpej");
        List<Character> expect = getCharacterList("udgrzfq");

        Assertions.assertIterableEquals(expect, DiffUtils.longestCommonSubSequence(a, b));
    }

    @Test
    public void shortestEditScript() {
        List<Object> commands = new ArrayList<>();
        List<Character> a = getCharacterList("bcabwcd");
        List<Character> b = getCharacterList("ttaybioco");
        DiffUtils.shortestEditScript(a, b, new DiffUtils.EditScriptRecorder<Character>() {
            @Override
            public void add(Character character) {
                commands.add(new AddCommand(character));
            }

            @Override
            public void delete() {
                commands.add(new DeleteCommand());
            }

            @Override
            public void accept() {
                commands.add(new AcceptCommand());
            }
        });

        /**
         * - b
         * - c
         * + t
         * + t
         * = a
         * + y
         * = b
         * - w
         * + i
         * + o
         * = c
         * - d
         * + o
         */
        Assertions.assertEquals(13, commands.size());
        Iterator<Object> commandIter = commands.iterator();
        Assertions.assertEquals(new DeleteCommand(), commandIter.next());
        Assertions.assertEquals(new DeleteCommand(), commandIter.next());
        Assertions.assertEquals(new AddCommand('t'), commandIter.next());
        Assertions.assertEquals(new AddCommand('t'), commandIter.next());
        Assertions.assertEquals(new AcceptCommand(), commandIter.next());
        Assertions.assertEquals(new AddCommand('y'), commandIter.next());
        Assertions.assertEquals(new AcceptCommand(), commandIter.next());
        Assertions.assertEquals(new DeleteCommand(), commandIter.next());
        Assertions.assertEquals(new AddCommand('i'), commandIter.next());
        Assertions.assertEquals(new AddCommand('o'), commandIter.next());
        Assertions.assertEquals(new AcceptCommand(), commandIter.next());
        Assertions.assertEquals(new DeleteCommand(), commandIter.next());
        Assertions.assertEquals(new AddCommand('o'), commandIter.next());
    }

    @Data
    private static class AddCommand {
        private final char c;

        private AddCommand(char c) {
            this.c = c;
        }

        public String toString() {
            return "+ " + c;
        }
    }

    @Data
    private static class DeleteCommand {
        @Override
        public String toString() {
            return "- " + 1;
        }
    }

    @Data
    private static class AcceptCommand {
        @Override
        public String toString() {
            return "= " + 1;
        }
    }
}

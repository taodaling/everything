package com.daltao.script.lexer;

import com.daltao.script.token.AbstractLineToken;
import com.daltao.script.token.LineToken;
import com.daltao.script.token.Token;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexerImpl implements Lexer {
    public static void main(String[] args) {
        for (Iterator<Token> iterator = new LexerImpl(new InputStreamReader(System.in)); iterator.hasNext(); ) {
            Token token = iterator.next();
            System.out.println(token);
        }
    }

    private static final LineToken EOF = new AbstractLineToken(-1, "EOF") {
    };

    /**
     * blank: \s*
     * comment: //.*
     * int: [0-9]+
     * string: "(\\"|\\n|\\\\|[^"])"
     * variable: [A-Z_a-z][A-Z_a-z0-9]*
     * equal: ==
     * leq: <=
     * geq: >=
     * less: <
     * greater: >
     * and: &&
     * bit or: \|
     * or: \|\|
     * punctuation: \p{Punct}
     * plus: \+
     * subtract: -
     * multiply: \*
     * divide: /
     * left curly bracket: \{
     * right curly bracket: }
     * set: =
     */
    private static Pattern pattern = Pattern.compile(
            new StringBuilder()
                    .append("\\s*") // blank
                    .append("(")
                    .append("(")
                    .append("//.*") //0: comment
                    .append(")")
                    .append("|")
                    .append("(")
                    .append("[0-9]+") //1: int
                    .append(")")
                    .append("|")
                    .append("(")
                    .append("\"(\\\\\"|\\\\n|\\\\\\\\|[^\"])\"") //2: string
                    .append(")")
                    .append("|")
                    .append("(")
                    .append("[A-Z_a-z][A-Z_a-z0-9]*") //3: variable
                    .append(")")
                    .append("|")
                    .append("(")
                    .append("==") //4: equal
                    .append(")")
                    .append("|")
                    .append("(")
                    .append("<=") //5: leq
                    .append(")")
                    .append("|")
                    .append("(")
                    .append(">=") //6: geq
                    .append(")")
                    .append("|")
                    .append("(")
                    .append("<") //7: less
                    .append(")")
                    .append("|")
                    .append("(")
                    .append(">") //8: greater
                    .append(")")
                    .append("|")
                    .append("(")
                    .append("&&") //9: and
                    .append(")")
                    .append("|")
                    .append("(")
                    .append("\\|") //10: bit or
                    .append(")")
                    .append("|")
                    .append("(")
                    .append("\\|\\|") //11: or
                    .append(")")
                    .append("|")
                    .append("(")
                    .append("\\p{Punct}") //12: punctuation
                    .append(")")
                    .append("|")
                    .append("(")
                    .append("\\+") //13: plus: \+
                    .append(")")
                    .append("|")
                    .append("(")
                    .append("-") //14: subtract: -
                    .append(")")
                    .append("|")
                    .append("(")
                    .append("\\*") //15: multiply: \*
                    .append(")")
                    .append("|")
                    .append("(")
                    .append("/") //16: divide: /
                    .append(")")
                    .append("|")
                    .append("(")
                    .append("\\{") //17: left curly bracket: \{
                    .append(")")
                    .append("|")
                    .append("(")
                    .append("}") //18: right curly bracket: }
                    .append(")")
                    .append("|")
                    .append("(")
                    .append("=") //19: set: =
                    .append(")")
                    .append(")")
                    .toString()
    );

    private Deque<Token> tokenDeque = new ArrayDeque<>();

    private static class StringToken extends AbstractLineToken {
        public StringToken(int lineNum, String value) {
            super(lineNum, value);
        }
    }

    private boolean refill() throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(line);
        matcher.useTransparentBounds(true).useAnchoringBounds(false);
        int bpos = 0;
        int epos = line.length();
        while (bpos < epos) {
            matcher.region(bpos, epos);
            if (!matcher.lookingAt()) {
                break;
            }
            bpos = matcher.end(0);
            String m = matcher.group(1);
            if (m == null) {
                continue;
            }
            if (matcher.group(2) != null) {
                continue;
            }
            tokenDeque.add(new StringToken(reader.getLineNumber(), matcher.group(1)));
        }
        return true;
    }

    private boolean ensureNotEmpty() throws IOException {
        while (tokenDeque.isEmpty() && refill()) ;
        return !tokenDeque.isEmpty();
    }

    private LineNumberReader reader;

    public LexerImpl(Reader reader) {
        this.reader = new LineNumberReader(reader);
    }


    @Override
    public boolean hasNext() {
        try {
            return ensureNotEmpty();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Token next() {
        return hasNext() ? tokenDeque.removeFirst() : EOF;
    }
}

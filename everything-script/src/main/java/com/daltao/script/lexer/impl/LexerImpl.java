package com.daltao.script.lexer.impl;

import com.daltao.script.lexer.Lexer;
import com.daltao.script.token.Token;
import com.daltao.script.token.impl.TokenType;
import com.daltao.script.token.impl.TypedToken;
import com.daltao.utils.Precondition;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexerImpl implements Lexer {
    public static void main(String[] args) {
        for (Iterator<Token> iterator = new LexerImpl(new InputStreamReader(System.in)); iterator.hasNext(); ) {
            Token token = iterator.next();
            System.out.println(token);
        }
    }

    /**
     * blank: \s*
     * comment: //.*
     * int: [0-9]+
     * string: "(\\"|\\n|\\\\|[^"])*"
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
     * module: %
     */
    private static List<String> pieces = Lists.newArrayList();
    private static List<TokenType> tokenTypes = Lists.newArrayList();
    private static Pattern pattern;

    static {
        pieces.add(null);
        tokenTypes.add(null);

        pieces.add(null);
        tokenTypes.add(null);

        pieces.add("//.*");
        tokenTypes.add(TokenType.COMMENT);

        pieces.add("[0-9]+");
        tokenTypes.add(TokenType.INT);

        pieces.add("\"(\\\\\"|\\\\n|\\\\\\\\|[^\"])*\"");
        tokenTypes.add(TokenType.STRING);
        tokenTypes.add(null);

        pieces.add("[A-Z_a-z][A-Z_a-z0-9]*");
        tokenTypes.add(TokenType.IDENTIFIER);

        pieces.add("<");
        tokenTypes.add(TokenType.TWO_OPERAND_OPERATOR);

        pieces.add(">");
        tokenTypes.add(TokenType.TWO_OPERAND_OPERATOR);

        pieces.add("&&");
        tokenTypes.add(TokenType.TWO_OPERAND_OPERATOR);

        pieces.add("\\|\\|");
        tokenTypes.add(TokenType.TWO_OPERAND_OPERATOR);

        pieces.add(";");
        tokenTypes.add(TokenType.SEMICOLON);

        pieces.add("\\+");
        tokenTypes.add(TokenType.TWO_OPERAND_OPERATOR);

        pieces.add("-");
        tokenTypes.add(TokenType.TWO_OPERAND_OPERATOR);

        pieces.add("\\*");
        tokenTypes.add(TokenType.TWO_OPERAND_OPERATOR);

        pieces.add("/");
        tokenTypes.add(TokenType.TWO_OPERAND_OPERATOR);

        pieces.add("\\{");
        tokenTypes.add(TokenType.LEFT_CURLY_BRACKET);

        pieces.add("\\}");
        tokenTypes.add(TokenType.RIGHT_CURLY_BRACKET);

        pieces.add("=");
        tokenTypes.add(TokenType.TWO_OPERAND_OPERATOR);

        pieces.add("%");
        tokenTypes.add(TokenType.TWO_OPERAND_OPERATOR);

        StringBuilder builder = new StringBuilder("\\s*").append("(");
        for (int i = 2, until = pieces.size(); i < until; i++) {
            builder.append("(").append(pieces.get(i)).append(")|");
        }
        if (builder.charAt(builder.length() - 1) == '|') {
            builder.setLength(builder.length() - 1);
        }
        builder.append(')');
        pattern = Pattern.compile(builder.toString());
    }

    private Deque<Token> tokenDeque = new ArrayDeque<>();


    private boolean refill() {
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
            for (int i = 2, until = tokenTypes.size(); i < until; i++) {
                String group = null;
                if ((group = matcher.group(i)) != null && tokenTypes.get(i) != null) {
                    tokenDeque.add(new TypedToken(group, reader.getLineNumber(), tokenTypes.get(i)));
                    break;
                }
            }
        }

        tokenDeque.addLast(new TypedToken("\n", reader.getLineNumber(), TokenType.EOL));
        return true;
    }

    boolean eof;

    private boolean ensureNotEmpty() throws IOException {
        while (tokenDeque.isEmpty() && refill()) ;
        if (tokenDeque.isEmpty() && !eof) {
            eof = true;
            tokenDeque.addLast(new TypedToken("EOF", -1, TokenType.EOF));
        }
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
        Precondition.isTrue(hasNext());
        return tokenDeque.removeFirst();
    }
}

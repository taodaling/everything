package com.daltao.script.parser.impl;

import com.daltao.collection.PredictableIterator;
import com.daltao.script.ast.*;
import com.daltao.script.ast.impl.ASTContextImpl;
import com.daltao.script.bnf.AbstractRule;
import com.daltao.script.bnf.ForwardingRuleImpl;
import com.daltao.script.bnf.Rule;
import com.daltao.script.error.ParserException;
import com.daltao.script.lexer.Lexer;
import com.daltao.script.lexer.impl.LexerImpl;
import com.daltao.script.parser.Parser;
import com.daltao.script.token.Token;
import com.daltao.script.token.impl.TokenType;
import com.daltao.utils.IOUtils;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.daltao.script.util.BNFUtils.*;

public class ParserImpl implements Parser {
    private static Set<String> reservedNames = Sets.newHashSet(
            "while",
            "if"
    );

    private PredictableIterator<Token> iterator;

    @Override
    public boolean hasNext() {
        return rule.match(iterator);
    }

    @Override
    public ASTNode next() {
        return rule.parse(iterator);
    }

    private static OperatorPriorities priorities = new OperatorPriorities()
            .append(OperatorPriority.builder().operator("=").leftMost(false).order(3).build())
            .append(OperatorPriority.builder().operator("*").leftMost(true).order(2).build())
            .append(OperatorPriority.builder().operator("%").leftMost(true).order(2).build())
            .append(OperatorPriority.builder().operator("/").leftMost(true).order(2).build())
            .append(OperatorPriority.builder().operator("+").leftMost(true).order(1).build())
            .append(OperatorPriority.builder().operator("-").leftMost(true).order(1).build())
            .append(OperatorPriority.builder().operator("<").leftMost(true).order(0).build())
            .append(OperatorPriority.builder().operator(">").leftMost(true).order(0).build());

    public static void main(String[] args) throws IOException {
        Lexer lexer = new LexerImpl(new InputStreamReader(new FileInputStream("D:/TEMP/code.txt")));
        //System.out.println(Iterators.toString(new ParserImpl(new LexerImpl(new InputStreamReader(new FileInputStream("D:/TEMP/code.txt"))))));

        ASTContext context = new ASTContextImpl();

        ParserImpl impl = new ParserImpl(lexer);
        while (impl.hasNext()) {
            ASTNode node = impl.next();
            System.out.println(node);
            System.out.println("=> " + node.eval(context));
        }
    }

    private Rule rule;

    public ParserImpl(Lexer iterator) {
        this.iterator = new PredictableIterator<>(iterator, 1);

        Rule number = terminal(x -> x.peek(0).getType() == TokenType.INT);
        number.setFunction(LiteralNumberNode::new);
        number.setId("NUMBER");

        Rule string = terminal(x -> x.peek(0).getType() == TokenType.STRING);
        string.setFunction(Constants.FETCH_FIRST_ONE);
        string.setId("STRING");

        Rule identifier = terminal(x -> x.peek(0).getType() == TokenType.IDENTIFIER &&
                !reservedNames.contains(x.peek(0).getText()));
        identifier.setFunction(IdentifierNode::new);
        identifier.setId("IDENTIFIER");

        Rule op = terminal(x -> x.peek(0).getType() == TokenType.TWO_OPERAND_OPERATOR);
        op.setFunction(Constants.FETCH_FIRST_ONE);
        op.setId("OP");

        Rule eol = terminal(x -> x.peek(0).getType() == TokenType.EOL);
        eol.setFunction(Constants.FETCH_FIRST_ONE);
        eol.setId("EOL");

        ForwardingRuleImpl primary = new ForwardingRuleImpl();

        Rule factor = or(and(string("-"), primary), primary);
        factor.setId("factor");
        factor.setFunction(list -> list.childAt(0) != null ? new NegateNode((ASTList) list.childAt(0)) : list.childAt(1));

        Rule expr = and(factor, any(and(op, factor)));
        expr.setId("expr");
        expr.setFunction(list -> {
            List<ASTNode> result = new ArrayList<>();
            result.add(list.childAt(0));
            for (ASTNode child : (ASTList) list.childAt(1)) {
                //OP factor
                ASTList next = (ASTList) child;
                result.add(next.childAt(0));
                result.add(next.childAt(1));
            }
            return asExpression(result, BinaryOp::new, x -> ((ASTLeaf) x).getToken().getText(), priorities);
        });

        primary.setRule(or(and(string("\""), expr, string("\"")), number, identifier, string));
        primary.setId("primary");
        primary.setFunction(list -> {
            if (list.childAt(0) != null) {
                return list.listChildAt(1).childAt(1);
            }
            for (ASTNode child : list) {
                if (child != null) {
                    return child;
                }
            }
            throw new ParserException("", null);
        });

        ForwardingRuleImpl block = new ForwardingRuleImpl();

        Rule simple = and(expr);
        simple.setId("simple");
        simple.setFunction(Constants.FETCH_FIRST_ONE);

        Rule statement = or(
                and(string("if"), expr, block, maybe(and(string("else"), block))),
                and(string("while"), expr, block),
                simple
        );
        statement.setId("statement");
        statement.setFunction(list -> {
            if (list.childAt(0) != null) {
                return new IfNode(list.listChildAt(0));
            } else if (list.childAt(1) != null) {
                return new WhileNode(list.listChildAt(1));
            } else {
                return list.childAt(2);
            }
        });

        block.setRule(and(
                string("{"),
                maybe(statement),
                any(and(or(string(";"), eol), maybe(statement))),
                string("}")
        ));
        block.setId("block");
        block.setFunction(BlockNode::new);

       Rule program = or(
                and(statement, or(string(";"), eol)),
                or(string(";"), eol)
        );
        program.setId("program");
        program.setFunction(ProgramNode::new);

        this.rule = program;
    }
}

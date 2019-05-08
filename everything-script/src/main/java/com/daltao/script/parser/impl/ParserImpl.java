package com.daltao.script.parser.impl;

import com.daltao.collection.PredictableIterator;
import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ASTLeaf;
import com.daltao.script.ast.ASTList;
import com.daltao.script.ast.ASTNode;
import com.daltao.script.ast.Constants;
import com.daltao.script.ast.impl.NativeASTContextImpl;
import com.daltao.script.bnf.ForwardingRuleImpl;
import com.daltao.script.bnf.Rule;
import com.daltao.script.error.ParserException;
import com.daltao.script.lexer.Lexer;
import com.daltao.script.lexer.impl.LexerImpl;
import com.daltao.script.parser.Parser;
import com.daltao.script.token.Token;
import com.daltao.script.token.impl.TokenType;
import com.daltao.script.util.StdoutDebugPredictableIterator;
import com.google.common.collect.Sets;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.daltao.script.util.BNFUtils.*;

public class ParserImpl implements Parser {
    private static Set<String> reservedNames = Sets.newHashSet(
            "while",
            "if",
            "else",
            "def",
            "new",
            "class",
            "is",
            "nil",
            "extends"
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
            .append(OperatorPriority.builder().operator("=").leftMost(false).order(-1).build())
            .append(OperatorPriority.builder().operator("*").leftMost(true).order(2).build())
            .append(OperatorPriority.builder().operator("%").leftMost(true).order(2).build())
            .append(OperatorPriority.builder().operator("/").leftMost(true).order(2).build())
            .append(OperatorPriority.builder().operator("+").leftMost(true).order(1).build())
            .append(OperatorPriority.builder().operator("-").leftMost(true).order(1).build())
            .append(OperatorPriority.builder().operator("<").leftMost(true).order(0).build())
            .append(OperatorPriority.builder().operator(">").leftMost(true).order(0).build())
            .append(OperatorPriority.builder().operator("is").leftMost(true).order(0).build());

    public static void main(String[] args) throws IOException {
        Lexer lexer = new LexerImpl(new InputStreamReader(new FileInputStream("D:/TEMP/code.txt")));
        //System.out.println(Iterators.toString(new ParserImpl(new LexerImpl(new InputStreamReader(new FileInputStream("D:/TEMP/code.txt"))))));

        ASTContext context = new NativeASTContextImpl();

        ParserImpl impl = new ParserImpl(lexer);
        while (impl.hasNext()) {
            ASTNode node = impl.next();
            System.out.println(node);
            Object val = node.eval(context);
            System.out.println("=> " + val);
        }
    }

    private Rule rule;

    public ParserImpl(Lexer iterator) {
        this.iterator = new StdoutDebugPredictableIterator<>(iterator, 1);

        Rule number = terminal(x -> x.peek(0).getType() == TokenType.INT);
        number.setFunction(LiteralNumberNode::new);
        number.setId("NUMBER");

        Rule string = terminal(x -> x.peek(0).getType() == TokenType.STRING);
        string.setFunction(StringNode::new);
        string.setId("STRING");

        Rule identifier = terminal(x -> x.peek(0).getType() == TokenType.IDENTIFIER &&
                !reservedNames.contains(x.peek(0).getText()));
        identifier.setFunction(IdentifierNode::new);
        identifier.setId("IDENTIFIER");

        Rule nil = terminal(x -> x.peek(0).getType() == TokenType.IDENTIFIER &&
                x.peek(0).getText().equals("nil"));
        nil.setId("nil");
        nil.setFunction(x -> new NilNode());

        Rule eol = terminal(x -> x.peek(0).getType() == TokenType.EOL);
        eol.setFunction(Constants.FETCH_FIRST_ONE);
        eol.setId("EOL");

        Rule op = terminal(x -> x.peek(0).getType() == TokenType.TWO_OPERAND_OPERATOR
                || (x.peek(0).getType() == TokenType.IDENTIFIER && x.peek(0).getText().equals("is")));
        op.setFunction(Constants.FETCH_FIRST_ONE);
        op.setId("OP");

        ForwardingRuleImpl primary2 = new ForwardingRuleImpl();
        ForwardingRuleImpl classDef = new ForwardingRuleImpl();
        ForwardingRuleImpl newObject = new ForwardingRuleImpl();
        ForwardingRuleImpl params = new ForwardingRuleImpl();


        ForwardingRuleImpl index = new ForwardingRuleImpl();

        //factor : "-" primary3 | primary3
        Rule factor = or(and(string("-"), primary2), primary2);
        factor.setId("factor");
        factor.setFunction(list -> list.childAt(0) != null ? new NegateNode((ASTList) list.childAt(0)) : list.childAt(1));

        //expr : factor {op factor}
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

        ForwardingRuleImpl def = new ForwardingRuleImpl();

        //primary : "(" expr ")" | identifier | number | string | def | class | newObject | nil
        Rule primary = or(
                and(string("("), expr, string(")")),
                identifier,
                number,
                string,
                def,
                classDef,
                newObject,
                nil
        );
        primary.setId("primary");
        primary.setFunction(list -> {
            if (list.childAt(0) != null) {
                return list.listChildAt(0).childAt(1);
            }
            for (ASTNode child : list) {
                if (child != null) {
                    return child;
                }
            }
            throw new ParserException("", null);
        });

        //primary2 : primary {"("  params ")" | "." primary | index}
        primary2.setRule(and(primary, any(or(
                and(
                        string("("),
                        params,
                        string(")")
                ),
                and(string("."), primary),
                index
        ))));
        primary2.setId("primary2");
        primary2.setFunction(list ->
        {
            ASTNode leftOperand = list.childAt(0);
            for (ASTNode follow : list.listChildAt(1)) {
                ASTList or = (ASTList) follow;
                if (or.childAt(0) != null) {
                    leftOperand = new InvocationNode(leftOperand, or.listChildAt(0));
                } else if (or.childAt(1) != null) {
                    leftOperand = new DotNode(leftOperand, (SetValueAble) or.listChildAt(1).childAt(1));
                } else {
                    leftOperand = new ArrayValueNode(leftOperand, or.listChildAt(2).childAt(1));
                }
            }
            return leftOperand;
        });

        ForwardingRuleImpl block = new ForwardingRuleImpl();

        //separator : ";"
        Rule separator = string(";");
        separator.setId("separator");

        //simple : expr separator
        Rule simple = and(expr, separator);
        simple.setId("simple");
        simple.setFunction(Constants.FETCH_FIRST_ONE);

        //statement : "if" expr block ["else" block] | "while" expr block | simple | separator
        Rule statement = or(
                and(string("if"), expr, block, maybe(and(string("else"), block))),
                and(string("while"), expr, block),
                simple,
                separator
        );
        statement.setId("statement");
        statement.setFunction(list -> {
            if (list.childAt(0) != null) {
                return new IfNode(list.listChildAt(0));
            } else if (list.childAt(1) != null) {
                return new WhileNode(list.listChildAt(1));
            }
            for (ASTNode node : list) {
                if (node != null) {
                    return node;
                }
            }
            throw new ParserException("", null);
        });

        //block : "{" { statement } "}"
        block.setRule(and(
                string("{"),
                any(statement),
                string("}")
        ));
        block.setId("block");
        block.setFunction(BlockNode::new);

        //program : statement
        Rule program = or(
                and(statement),
                string(";")
        );
        program.setId("program");
        program.setFunction(ProgramNode::new);

        //def : "def" [identifier] "("  params ")" block
        def.setRule(
                and(
                        string("def"),
                        maybe(identifier),
                        string("("),
                        params,
                        string(")"),
                        block
                ));
        def.setId("def");
        def.setFunction(DefNode::new);

        //"class" identifier ["extends" identifier] block
        classDef.setRule(and(
                string("class"),
                identifier,
                maybe(and(string("extends"), identifier)),
                block
        ));
        classDef.setId("class");
        classDef.setFunction(ClassDefNode::new);

        //param-def : [identifier {"," identifier}]
        params.setRule(maybe(and(
                expr,
                any(
                        and(string(","), expr)
                )
                )
        ));
        params.setId("params");
        params.setFunction(ParamNode::new);

        //new : "new" identifier "("  params ")"
        newObject.setRule(
                and(
                        string("new"),
                        identifier,
                        string("("),
                        params,
                        string(")")
                )
        );
        newObject.setId("new");
        newObject.setFunction(NewObjectNode::new);

        //index : "[" expr "]"
        index.setRule(
                and(
                        string("["),
                        expr,
                        string("]")
                )
        );
        index.setId("index");

        this.rule = program;
    }
}

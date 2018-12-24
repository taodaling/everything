package com.daltao.script.util;

import com.daltao.collection.PredictableIterator;
import com.daltao.script.ast.ASTLeaf;
import com.daltao.script.ast.ASTList;
import com.daltao.script.ast.ASTNode;
import com.daltao.script.ast.Constants;
import com.daltao.script.bnf.AbstractRule;
import com.daltao.script.bnf.Rule;
import com.daltao.script.token.Token;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class BNFUtils {
    public static ASTNode asExpression(List<ASTNode> nodes, Function<ASTList, ASTNode> factory, Function<ASTNode, String> opFetcher, OperatorPriorities priorities) {
        Deque<ASTNode> operands = new ArrayDeque<>(nodes.size() / 2 + 1);
        Deque<OperatorPriority> priorityDeque = new ArrayDeque<>(nodes.size() / 2);
        Deque<ASTNode> operators = new ArrayDeque<>(nodes.size() / 2);
        boolean isOperand = false;
        for (ASTNode node : nodes) {
            isOperand = !isOperand;
            if (isOperand) {
                operands.add(node);
                continue;
            }
            OperatorPriority priority = priorities.getOrder(opFetcher.apply(node));
            while (!priorityDeque.isEmpty() && priorityDeque.getLast().compareTo(priority) > 0) {
                ASTNode right = operands.removeLast();
                ASTNode left = operands.removeLast();
                priorityDeque.removeLast();
                ASTNode operator = operators.removeLast();
                operands.addLast(factory.apply(new ASTList(left, operator, right)));
            }
            priorityDeque.addLast(priority);
            operators.addLast(node);
        }

        while (!operators.isEmpty()) {
            ASTNode right = operands.removeLast();
            ASTNode left = operands.removeLast();
            ASTNode operator = operators.removeLast();
            operands.addLast(factory.apply(new ASTList(left, operator, right)));
        }
        return operands.removeLast();
    }

    public static class OperatorPriorities {
        private Map<String, OperatorPriority> map = new HashMap<>();

        public OperatorPriority getOrder(String operator) {
            return map.get(operator);
        }

        public OperatorPriorities append(OperatorPriority operatorPriority) {
            map.put(operatorPriority.operator, operatorPriority);
            return this;
        }

        public OperatorPriorities append(String operator, int order, boolean leftMost) {
            return append(new OperatorPriority(operator, order, leftMost));
        }
    }

    @Builder
    @Getter
    public static class OperatorPriority implements Comparable<OperatorPriority> {
        private String operator;
        private int order;
        private boolean leftMost;

        @Override
        public int compareTo(OperatorPriority o) {
            if (order != o.order) {
                return order - o.order;
            }
            return leftMost ? 1 : -1;
        }
    }

    public static Rule and(Rule... rules) {
        AndRule rule = new AndRule();
        rule.setRules(Arrays.asList(rules));
        return rule;
    }

    public static Rule skip() {
        return new SkipRule();
    }

    public static Rule or(Rule... rules) {
        OrRule rule = new OrRule();
        rule.setRules(Arrays.asList(rules));
        return rule;
    }

    public static Rule maybe(Rule rule) {
        MaybeRule maybeRule = new MaybeRule();
        maybeRule.setRule(rule);
        maybeRule.setFunction(Constants.FETCH_FIRST_ONE);
        return maybeRule;
    }


    public static Rule any(Rule rule) {
        AnyRule anyRule = new AnyRule();
        anyRule.setRule(rule);
        return anyRule;
    }

    public static Rule string(String name) {
        StringRule rule = new StringRule();
        rule.setPattern(name);
        rule.setFunction(Constants.FETCH_FIRST_ONE);
        return rule;
    }

    public static Rule terminal(Predicate<PredictableIterator<Token>> predicate) {
        TerminalRule rule = new TerminalRule();
        rule.setPredicate(predicate);
        return rule;
    }

    @Setter
    private static class TerminalRule extends AbstractRule {
        private Predicate<PredictableIterator<Token>> predicate;

        @Override
        public ASTNode parse(PredictableIterator<Token> iterator) {
            return getFunction().apply(new ASTList(new ASTLeaf(iterator.next())));
        }

        @Override
        public boolean match(PredictableIterator<Token> iterator) {
            return predicate.test(iterator);
        }
    }

    private static class SkipRule extends AbstractRule {
        @Override
        public ASTNode parse(PredictableIterator<Token> iterator) {
            return null;
        }

        @Override
        public boolean match(PredictableIterator<Token> iterator) {
            return true;
        }
    }


    @Setter
    private static class StringRule extends AbstractRule {
        private String pattern;


        @Override
        public ASTNode parse(PredictableIterator<Token> iterator) {
            return getFunction().apply(new ASTList(new ASTLeaf(iterator.next())));
        }

        @Override
        public boolean match(PredictableIterator<Token> iterator) {
            return pattern.equals(iterator.peek(0).getText());
        }

        @Override
        public String toString() {
            return getId() + ":" + pattern;
        }
    }

    @Setter
    private static class AndRule extends AbstractRule {
        private List<Rule> rules;

        @Override
        public ASTNode parse(PredictableIterator<Token> iterator) {
            List<ASTNode> result = new ArrayList<>(rules.size());
            for (Rule rule : rules) {
                result.add(rule.parse(iterator));
            }
            return getFunction().apply(new ASTList(result));
        }

        @Override
        public boolean match(PredictableIterator<Token> iterator) {
            return rules.get(0).match(iterator);
        }
    }

    @Setter
    private static class AnyRule extends AbstractRule {
        private Rule rule;

        @Override
        public ASTNode parse(PredictableIterator<Token> iterator) {
            List<ASTNode> nodes = new ArrayList<>(1);
            while (rule.match(iterator)) {
                nodes.add(rule.parse(iterator));
            }
            return getFunction().apply(new ASTList(nodes));
        }

        @Override
        public boolean match(PredictableIterator<Token> iterator) {
            return true;
        }
    }

    @Setter
    private static class MaybeRule extends AbstractRule {
        private Rule rule;

        @Override
        public ASTNode parse(PredictableIterator<Token> iterator) {
            List<ASTNode> nodes = new ArrayList<>(1);
            if (rule.match(iterator)) {
                nodes.add(rule.parse(iterator));
            } else {
                nodes.add(null);
            }
            return getFunction().apply(new ASTList(nodes));
        }

        @Override
        public boolean match(PredictableIterator<Token> iterator) {
            return true;
        }
    }

    @Setter
    private static class OrRule extends AbstractRule {
        private List<Rule> rules;

        @Override
        public ASTNode parse(PredictableIterator<Token> iterator) {
            List<ASTNode> result = new ArrayList<>();
            boolean find = false;
            for (Rule rule : rules) {
                if (!find && rule.match(iterator)) {
                    result.add(rule.parse(iterator));
                    find = true;
                } else {
                    result.add(null);
                }
            }
            return getFunction().apply(new ASTList(result));
        }

        @Override
        public boolean match(PredictableIterator<Token> iterator) {
            for (Rule rule : rules) {
                if (rule.match(iterator)) {
                    return true;
                }
            }
            return false;
        }
    }
}

package com.daltao.script.bnf;

import lombok.Setter;

@Setter
public class ForwardingRuleImpl extends ForwardingRule {
    private Rule rule;

    @Override
    protected Rule delegate() {
        return rule;
    }
}

package com.daltao.script.parser;

import com.daltao.collection.PredictableIterator;
import com.daltao.collection.TransactionalRandomAccessListIterator;
import com.daltao.script.ast.ASTNode;
import com.daltao.script.lexer.Lexer;
import com.daltao.script.token.Token;

import java.util.Iterator;

public interface Parser extends Iterator<ASTNode> {

}

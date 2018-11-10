package com.daltao.collection.wrapper;

import java.util.Iterator;

public abstract class IteratorWrapper<V> implements Iterator<V> {
    private Iterator<V> inner;
	public IteratorWrapper(Iterator<V> a0){
		if(a0 == null){
			throw new NullPointerException();
		}
		this.inner = a0;
	}
	@Override
	public void remove(){
		inner.remove();
	}
	@Override
	public boolean hasNext(){
		return inner.hasNext();
	}
	@Override
	public V next(){
		return inner.next();
	}
}


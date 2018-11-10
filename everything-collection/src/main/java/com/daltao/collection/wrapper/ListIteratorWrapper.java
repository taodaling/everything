package com.daltao.collection.wrapper;

import java.util.ListIterator;

public abstract class ListIteratorWrapper implements ListIterator {
    protected ListIterator inner;
	public ListIteratorWrapper(ListIterator a0){
		if(a0 == null){
			throw new NullPointerException();
		}
		this.inner = a0;
	}
	@Override
	public void add(Object a0){
		inner.add(a0);
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
	public Object next(){
		return inner.next();
	}
	@Override
	public void set(Object a0){
		inner.set(a0);
	}
	@Override
	public boolean hasPrevious(){
		return inner.hasPrevious();
	}
	@Override
	public int nextIndex(){
		return inner.nextIndex();
	}
	@Override
	public Object previous(){
		return inner.previous();
	}
	@Override
	public int previousIndex(){
		return inner.previousIndex();
	}
}
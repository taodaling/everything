package com.daltao.collection.wrapper;

import java.util.*;
import java.util.function.UnaryOperator;

public abstract class ListWrapper implements List {
    protected List inner;
	public ListWrapper(List a0){
		if(a0 == null){
			throw new NullPointerException();
		}
		this.inner = a0;
	}
	@Override
	public boolean add(Object a0){
		return inner.add(a0);
	}
	@Override
	public void add(int a0, Object a1){
		inner.add(a0, a1);
	}
	@Override
	public Object remove(int a0){
		return inner.remove(a0);
	}
	@Override
	public boolean remove(Object a0){
		return inner.remove(a0);
	}
	@Override
	public Object get(int a0){
		return inner.get(a0);
	}
	@Override
	public boolean equals(Object a0){
		return inner.equals(a0);
	}
	@Override
	public int hashCode(){
		return inner.hashCode();
	}
	@Override
	public int indexOf(Object a0){
		return inner.indexOf(a0);
	}
	@Override
	public void clear(){
		inner.clear();
	}
	@Override
	public boolean contains(Object a0){
		return inner.contains(a0);
	}
	@Override
	public boolean isEmpty(){
		return inner.isEmpty();
	}
	@Override
	public Iterator iterator(){
		return inner.iterator();
	}
	@Override
	public int lastIndexOf(Object a0){
		return inner.lastIndexOf(a0);
	}
	@Override
	public void replaceAll(UnaryOperator a0){
		inner.replaceAll(a0);
	}
	@Override
	public int size(){
		return inner.size();
	}
	@Override
	public List subList(int a0, int a1){
		return inner.subList(a0, a1);
	}
	@Override
	public Object[] toArray(){
		return inner.toArray();
	}
	@Override
	public Object[] toArray(Object[] a0){
		return inner.toArray(a0);
	}
	@Override
	public Spliterator spliterator(){
		return inner.spliterator();
	}
	@Override
	public boolean addAll(Collection a0){
		return inner.addAll(a0);
	}
	@Override
	public boolean addAll(int a0, Collection a1){
		return inner.addAll(a0, a1);
	}
	@Override
	public Object set(int a0, Object a1){
		return inner.set(a0, a1);
	}
	@Override
	public boolean containsAll(Collection a0){
		return inner.containsAll(a0);
	}
	@Override
	public ListIterator listIterator(int a0){
		return inner.listIterator(a0);
	}
	@Override
	public ListIterator listIterator(){
		return inner.listIterator();
	}
	@Override
	public boolean removeAll(Collection a0){
		return inner.removeAll(a0);
	}
	@Override
	public boolean retainAll(Collection a0){
		return inner.retainAll(a0);
	}
	@Override
	public void sort(Comparator a0){
		inner.sort(a0);
	}
}

package com.leovandriel.reversible.proxy;

import java.util.Collection;
import java.util.Iterator;
import com.leovandriel.reversible.action.Action;
import com.leovandriel.reversible.action.ActionRunner;
import com.leovandriel.reversible.misc.Slot;

public class ProxySlot<T> implements Slot<T> {
	private Slot<T> target;
	private ActionRunner runner;

	public ProxySlot(Slot<T> target, ActionRunner runner) {
		this.target = target;
		this.runner = runner;
	}

	public T get() {
		return target.get();
	}

	public class Set implements Action<T> {
		private T value;
		private T previous;

		public Set(T value) {
			this.value = value;
		}

		public T run() {
			previous = target.get();
			return target.set(value);
		}

		public void unrun() {
			target.set(previous);
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + value + ')';
		}
	}

	public T set(T value) {
		return runner.run(new Set(value));
	}

	public boolean add(T e) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean contains(Object o) {
		return target.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return target.containsAll(c);
	}

	public boolean isEmpty() {
		return target.isEmpty();
	}

	/**
	 * warning
	 */
	public Iterator<T> iterator() {
		return target.iterator();
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return target.size();
	}

	public Object[] toArray() {
		return target.toArray();
	}

	public <U> U[] toArray(U[] a) {
		return target.toArray(a);
	}

	@Override
	public int hashCode() {
		return target.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return target.equals(o);
	}

	@Override
	public String toString() {
		return target.toString();
	}
}

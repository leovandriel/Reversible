package com.leovandriel.reversible.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import com.leovandriel.reversible.action.Action;
import com.leovandriel.reversible.action.ActionRunner;

public class ProxyCollection<T> implements Collection<T> {
	private Collection<T> target;
	private ActionRunner runner;

	public ProxyCollection(Collection<T> target, ActionRunner runner) {
		this.target = target;
		this.runner = runner;
	}

	public boolean add(T e) {
		throw new UnsupportedOperationException("Unable to undo this operation, collection implementation unknown.");
	}

	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException("Unable to undo this operation, collection implementation unknown.");
	}

	public class Clear implements Action<Void> {
		private List<T> backup;

		public Void run() {
			backup = new ArrayList<T>(target);
			target.clear();
			return null;
		}

		public void unrun() {
			target.addAll(backup);
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + ')';
		}
	}

	public void clear() {
		runner.run(new Clear());
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
		throw new UnsupportedOperationException("Unable to undo this operation, collection implementation unknown.");
	}

	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("Unable to undo this operation, collection implementation unknown.");
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("Unable to undo this operation, collection implementation unknown.");
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

package com.leovandriel.reversible.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import com.leovandriel.reversible.action.Action;
import com.leovandriel.reversible.action.AdvancedRunner;

public class DebugProxyCollection<T> implements Collection<T> {
	private Collection<T> target;
	private AdvancedRunner runner;

	public DebugProxyCollection(Collection<T> target, AdvancedRunner runner) {
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
			if (!target.isEmpty()) {
				throw new IllegalStateException("Target modified, should be empty");
			}
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

	public class IteratorImpl implements Iterator<T> {
		private Iterator<T> iterator = target.iterator();

		public boolean hasNext() {
			return iterator.hasNext();
		}

		public T next() {
			return iterator.next();
		}

		public void remove() {
			if (!runner.isRunningAction()) {
				throw new IllegalStateException("Modification should only happen within action runs");
			}
			iterator.remove();
		}
	}

	public Iterator<T> iterator() {
		return new IteratorImpl();
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

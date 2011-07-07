package com.leovandriel.reversible.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import com.leovandriel.reversible.action.Action;
import com.leovandriel.reversible.action.ActionRunner;

public class DebugProxyList<T> implements List<T> {
	private List<T> target;
	private ActionRunner runner;

	public DebugProxyList(List<T> target, ActionRunner runner) {
		this.target = target;
		this.runner = runner;
	}

	public class DebugAction {
		private int sizeBeforeRun;
		private int sizeAfterRun;

		public void preRun() {
			sizeBeforeRun = target.size();
		}

		public void postRun() {
			sizeAfterRun = target.size();
		}

		public void preUnrun() {
			if (target.size() != sizeAfterRun) {
				throw new IllegalStateException("Target modified, size should be " + sizeAfterRun + ", not "
						+ target.size());
			}
		}

		public void postUnrun() {
			if (target.size() != sizeBeforeRun) {
				throw new IllegalStateException("Target modified, size should be " + sizeBeforeRun + ", not "
						+ target.size());
			}
		}
	}

	public class Add extends DebugAction implements Action<Boolean> {
		private T value;

		public Add(T value) {
			this.value = value;
		}

		public Boolean run() {
			preRun();
			Boolean result = new Boolean(target.add(value));
			postRun();
			return result;
		}

		public void unrun() {
			preUnrun();
			target.remove(target.size() - 1);
			postUnrun();
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + value + ')';
		}
	}

	public boolean add(T e) {
		return runner.run(new Add(e)).booleanValue();
	}

	public class AddAt extends DebugAction implements Action<Void> {
		private int index;
		private T value;

		public AddAt(int index, T value) {
			this.index = index;
			this.value = value;
		}

		public Void run() {
			preRun();
			target.add(index, value);
			postRun();
			return null;
		}

		public void unrun() {
			preUnrun();
			target.remove(index);
			postUnrun();
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + index + ',' + value + ')';
		}
	}

	public void add(int index, T element) {
		runner.run(new AddAt(index, element));
	}

	public class AddAll extends DebugAction implements Action<Boolean> {
		private List<T> values;

		public AddAll(Collection<? extends T> values) {
			this.values = new ArrayList<T>(values);
		}

		public Boolean run() {
			preRun();
			Boolean result = new Boolean(target.addAll(values));
			postRun();
			return result;
		}

		public void unrun() {
			preUnrun();
			for (int i = target.size() - 1, end = i - values.size(); i > end; i--) {
				target.remove(i);
			}
			postUnrun();
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + values + ')';
		}
	}

	public boolean addAll(Collection<? extends T> c) {
		return runner.run(new AddAll(c)).booleanValue();
	}

	public class AddAllAt extends DebugAction implements Action<Boolean> {
		private int index;
		private List<T> values;

		public AddAllAt(int index, Collection<? extends T> values) {
			this.index = index;
			this.values = new ArrayList<T>(values);
		}

		public Boolean run() {
			preRun();
			Boolean result = new Boolean(target.addAll(index, values));
			postRun();
			return result;
		}

		public void unrun() {
			preUnrun();
			for (int i = 0; i < values.size(); i++) {
				target.remove(index);
			}
			postUnrun();
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + index + ',' + values + ')';
		}
	}

	public boolean addAll(int index, Collection<? extends T> c) {
		return runner.run(new AddAllAt(index, c)).booleanValue();
	}

	public void clear() {
		// runner.run(new ProxyCollection.Clear<T>(target));
	}

	public boolean contains(Object o) {
		return target.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return target.containsAll(c);
	}

	public T get(int index) {
		return target.get(index);
	}

	public int indexOf(Object o) {
		return target.indexOf(o);
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

	public int lastIndexOf(Object o) {
		return target.lastIndexOf(o);
	}

	public class ListIteratorImpl implements ListIterator<T> {
		private ListIterator<T> iterator;

		public ListIteratorImpl(ListIterator<T> iterator) {
			this.iterator = iterator;
		}

		public boolean hasNext() {
			return iterator.hasNext();
		}

		public T next() {
			return iterator.next();
		}

		public boolean hasPrevious() {
			return iterator.hasPrevious();
		}

		public T previous() {
			return iterator.previous();
		}

		public int nextIndex() {
			return iterator.nextIndex();
		}

		public int previousIndex() {
			return iterator.previousIndex();
		}

		public void remove() {
			throw new UnsupportedOperationException("Unable to undo this operation because iterator changes state.");
		}

		public void set(T e) {
			throw new UnsupportedOperationException("Unable to undo this operation because iterator changes state.");
		}

		public void add(T e) {
			throw new UnsupportedOperationException("Unable to undo this operation because iterator changes state.");
		}
	}

	public ListIterator<T> listIterator() {
		return target.listIterator();
	}

	public ListIterator<T> listIterator(int index) {
		return target.listIterator(index);
	}

	public class Remove extends DebugAction implements Action<Boolean> {
		private Object value;
		private int index;

		public Remove(Object value) {
			this.value = value;
		}

		public Boolean run() {
			preRun();
			index = target.indexOf(value);
			Boolean result = new Boolean(target.remove(value));
			postRun();
			return result;
		}

		@SuppressWarnings("unchecked")
		public void unrun() {
			preUnrun();
			if (index >= 0) {
				target.add(index, (T) value);
			}
			postUnrun();
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + value + ')';
		}
	}

	public boolean remove(Object o) {
		return runner.run(new Remove(o)).booleanValue();
	}

	public class RemoveAt extends DebugAction implements Action<T> {
		private T value;
		private int index;

		public RemoveAt(int index) {
			this.index = index;
		}

		public T run() {
			preRun();
			value = target.remove(index);
			postRun();
			return value;
		}

		public void unrun() {
			preUnrun();
			target.add(index, value);
			postUnrun();
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + index + ')';
		}
	}

	public T remove(int index) {
		return runner.run(new RemoveAt(index));
	}

	private static class IntObjectPair {
		private int i;
		private Object o;

		public IntObjectPair(int i, Object o) {
			this.i = i;
			this.o = o;
		}

		public int getInt() {
			return i;
		}

		public Object getObject() {
			return o;
		}
	}

	public class RemoveAll extends DebugAction implements Action<Boolean> {
		private List<Object> values;
		private List<IntObjectPair> removedOnRun;

		public RemoveAll(Collection<?> values) {
			this.values = new ArrayList<Object>(values);
		}

		public Boolean run() {
			preRun();
			int i = 0;
			for (Object o : values) {
				if (target.contains(o)) {
					removedOnRun.add(new IntObjectPair(i, o));
				}
				i++;
			}
			Boolean result = new Boolean(target.removeAll(values));
			postRun();
			return result;
		}

		@SuppressWarnings("unchecked")
		public void unrun() {
			preUnrun();
			for (IntObjectPair pair : removedOnRun) {
				target.add(pair.getInt(), (T) pair.getObject());
			}
			postUnrun();
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + values + ')';
		}
	}

	public boolean removeAll(Collection<?> c) {
		return runner.run(new RemoveAll(c)).booleanValue();
	}

	public class RetainAll extends DebugAction implements Action<Boolean> {
		private List<Object> values;
		private List<IntObjectPair> removedOnRun;

		public RetainAll(Collection<?> values) {
			this.values = new ArrayList<Object>(values);
		}

		public Boolean run() {
			preRun();
			int i = 0;
			for (T t : target) {
				if (!values.contains(t)) {
					removedOnRun.add(new IntObjectPair(i, t));
				}
				i++;
			}
			Boolean result = new Boolean(target.removeAll(values));
			postRun();
			return result;
		}

		@SuppressWarnings("unchecked")
		public void unrun() {
			preUnrun();
			for (IntObjectPair p : removedOnRun) {
				target.add(p.getInt(), (T) p.getObject());
			}
			postUnrun();
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + values + ')';
		}
	}

	public boolean retainAll(Collection<?> c) {
		return runner.run(new RetainAll(c)).booleanValue();
	}

	public class Set extends DebugAction implements Action<T> {
		private int index;
		private T value;
		private T backup;

		public Set(int index, T value) {
			this.index = index;
			this.value = value;
		}

		public T run() {
			preRun();
			backup = target.set(index, value);
			postRun();
			return backup;
		}

		public void unrun() {
			preUnrun();
			target.set(index, backup);
			postUnrun();
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + value + ',' + index + ')';
		}
	}

	public T set(int index, T element) {
		return runner.run(new Set(index, element));
	}

	public int size() {
		return target.size();
	}

	public List<T> subList(int fromIndex, int toIndex) {
		return new DebugProxyList<T>(target.subList(fromIndex, toIndex), runner);
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

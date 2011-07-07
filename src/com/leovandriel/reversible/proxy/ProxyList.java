package com.leovandriel.reversible.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import com.leovandriel.reversible.action.Action;
import com.leovandriel.reversible.action.ActionRunner;

public class ProxyList<T> implements List<T> {
	private List<T> target;
	private ActionRunner runner;

	public ProxyList(List<T> target, ActionRunner runner) {
		this.target = target;
		this.runner = runner;
	}

	public class Add implements Action<Boolean> {
		private T value;

		public Add(T value) {
			this.value = value;
		}

		public Boolean run() {
			return new Boolean(target.add(value));
		}

		public void unrun() {
			target.remove(target.size() - 1);
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + value + ')';
		}
	}

	public boolean add(T e) {
		return runner.run(new Add(e)).booleanValue();
	}

	public class AddAt implements Action<Void> {
		private int index;
		private T value;

		public AddAt(int index, T value) {
			this.index = index;
			this.value = value;
		}

		public Void run() {
			target.add(index, value);
			return null;
		}

		public void unrun() {
			target.remove(index);
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + index + ',' + value + ')';
		}
	}

	public void add(int index, T element) {
		runner.run(new AddAt(index, element));
	}

	public class AddAll implements Action<Boolean> {
		private List<T> values;

		public AddAll(Collection<? extends T> values) {
			this.values = new ArrayList<T>(values);
		}

		public Boolean run() {
			return new Boolean(target.addAll(values));
		}

		public void unrun() {
			for (int i = target.size() - 1, end = i - values.size(); i > end; i--) {
				target.remove(i);
			}
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + values + ')';
		}
	}

	public boolean addAll(Collection<? extends T> c) {
		return runner.run(new AddAll(c)).booleanValue();
	}

	public class AddAllAt implements Action<Boolean> {
		private int index;
		private List<T> values;

		public AddAllAt(int index, Collection<? extends T> values) {
			this.index = index;
			this.values = new ArrayList<T>(values);
		}

		public Boolean run() {
			return new Boolean(target.addAll(index, values));
		}

		public void unrun() {
			for (int i = 0; i < values.size(); i++) {
				target.remove(index);
			}
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
		runner.run(new ProxyCollection<T>(target, runner).new Clear());
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

	public Iterator<T> iterator() {
		assert(false); // TODO: implement
		return target.iterator();
	}

	public int lastIndexOf(Object o) {
		return target.lastIndexOf(o);
	}

	public ListIterator<T> listIterator() {
		return new ProxyListIterator(target.listIterator());
	}

	public ListIterator<T> listIterator(int index) {
		return new ProxyListIterator(target.listIterator(index));
	}

	public class ProxyListIterator implements ListIterator<T> {
		private ListIterator<T> iterator;
		private T current;

		public ProxyListIterator(ListIterator<T> iterator) {
			this.iterator = iterator;
		}

		@Override
		public void add(T element) {
			runner.run(new Add(element));
		}

		public class Add implements Action<Void> {
			private T value;

			public Add(T value) {
				this.value = value;
			}

			public Void run() {
				iterator.add(value);
				return null;
			}

			public void unrun() {
				iterator.remove();
			}

			@Override
			public String toString() {
				return this.getClass().getSimpleName() + '(' + value + ')';
			}
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}
		@Override
		public boolean hasPrevious() {
			return iterator.hasPrevious();
		}

		@Override
		public T next() {
			return runner.run(new Next());
		}

		public class Next implements Action<T> {
			private T previous;

			public T run() {
				previous = current;
				current = iterator.next();
				return current;
			}

			public void unrun() {
				current = previous;
				iterator.previous();
			}

			@Override
			public String toString() {
				return this.getClass().getSimpleName()+ '(' + previous + ')';
			}
		}

		@Override
		public int nextIndex() {
			return iterator.nextIndex();
		}

		@Override
		public T previous() {
			return iterator.previous();
		}

		public class Previous implements Action<T> {
			private T next;

			public T run() {
				next = current;
				current = iterator.previous();
				return current;
			}

			public void unrun() {
				current = next;
				iterator.next();
			}

			@Override
			public String toString() {
				return this.getClass().getSimpleName()+ '(' + next + ')';
			}
		}

		@Override
		public int previousIndex() {
			return iterator.previousIndex();
		}

		@Override
		public void remove() {
			runner.run(new Remove());
		}

		public class Remove implements Action<Void> {
			private T value;

			public Void run() {
				value = current;
				iterator.remove();
				return null;
			}

			public void unrun() {
				iterator.add(value);
			}

			@Override
			public String toString() {
				return this.getClass().getSimpleName() + '(' + value + ')';
			}
		}

		@Override
		public void set(T element) {
			runner.run(new Set(element));
		}

		public class Set implements Action<Void> {
			private T newValue;
			private T oldValue;

			public Set(T value) {
				newValue = value;
			}

			public Void run() {
				oldValue = current;
				iterator.set(newValue);
				return null;
			}

			public void unrun() {
				iterator.previous();
				iterator.next();
				iterator.set(oldValue);
			}

			@Override
			public String toString() {
				return this.getClass().getSimpleName() + '(' + oldValue + ',' + newValue + ')';
			}
		}

	}

	public class Remove implements Action<Boolean> {
		private Object value;
		private int index;

		public Remove(Object value) {
			this.value = value;
		}

		public Boolean run() {
			index = target.indexOf(value);
			return new Boolean(target.remove(value));
		}

		@SuppressWarnings("unchecked")
		public void unrun() {
			if (index >= 0) {
				target.add(index, (T) value);
			}
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + value + ')';
		}
	}

	public boolean remove(Object o) {
		return runner.run(new Remove(o)).booleanValue();
	}

	public class RemoveAt implements Action<T> {
		private T value;
		private int index;

		public RemoveAt(int index) {
			this.index = index;
		}

		public T run() {
			value = target.remove(index);
			return value;
		}

		public void unrun() {
			target.add(index, value);
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + index + ')';
		}
	}

	public T remove(int index) {
		return runner.run(new RemoveAt(index));
	}

	private class IntObjectPair {
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

	public class RemoveAll implements Action<Boolean> {
		private List<Object> values;
		private List<IntObjectPair> removedOnRun;

		public RemoveAll(Collection<?> values) {
			this.values = new ArrayList<Object>(values);
		}

		public Boolean run() {
			int i = 0;
			for (Object o : values) {
				if (target.contains(o)) {
					removedOnRun.add(new IntObjectPair(i, o));
				}
				i++;
			}
			return new Boolean(target.removeAll(values));
		}

		@SuppressWarnings("unchecked")
		public void unrun() {
			for (IntObjectPair pair : removedOnRun) {
				target.add(pair.getInt(), (T) pair.getObject());
			}
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + values + ')';
		}
	}

	public boolean removeAll(Collection<?> c) {
		return runner.run(new RemoveAll(c)).booleanValue();
	}

	public class RetainAll implements Action<Boolean> {
		private List<Object> values;
		private List<IntObjectPair> removedOnRun;

		public RetainAll(Collection<?> values) {
			this.values = new ArrayList<Object>(values);
		}

		public Boolean run() {
			int i = 0;
			for (T t : target) {
				if (!values.contains(t)) {
					removedOnRun.add(new IntObjectPair(i, t));
				}
				i++;
			}
			return new Boolean(target.removeAll(values));
		}

		@SuppressWarnings("unchecked")
		public void unrun() {
			for (IntObjectPair p : removedOnRun) {
				target.add(p.getInt(), (T) p.getObject());
			}
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + values + ')';
		}
	}

	public boolean retainAll(Collection<?> c) {
		return runner.run(new RetainAll(c)).booleanValue();
	}

	public class Set implements Action<T> {
		private int index;
		private T value;
		private T backup;

		public Set(int index, T value) {
			this.index = index;
			this.value = value;
		}

		public T run() {
			backup = target.set(index, value);
			return backup;
		}

		public void unrun() {
			target.set(index, backup);
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
		return new ProxyList<T>(target.subList(fromIndex, toIndex), runner);
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

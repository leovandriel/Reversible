package com.leovandriel.reversible.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.leovandriel.reversible.action.Action;
import com.leovandriel.reversible.action.ActionRunner;

public class ProxySet<T> implements Set<T> {
	private Set<T> target;
	private ActionRunner runner;

	public ProxySet(Set<T> target, ActionRunner runner) {
		this.target = target;
		this.runner = runner;
	}

	public class Add implements Action<Boolean> {
		private T value;
		private boolean addedOnRun;

		public Add(T value) {
			this.value = value;
		}

		public Boolean run() {
			addedOnRun = target.add(value);
			return new Boolean(addedOnRun);
		}

		public void unrun() {
			if (addedOnRun) {
				target.remove(value);
			}
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + value + ')';
		}
	}

	public boolean add(T e) {
		return runner.run(new Add(e)).booleanValue();
	}

	public class AddAll implements Action<Boolean> {
		private List<T> values;
		private Set<T> addedOnRun;

		public AddAll(Collection<? extends T> values) {
			this.values = new ArrayList<T>(values);
		}

		public Boolean run() {
			addedOnRun = new HashSet<T>();
			for (T t : values) {
				if (!target.contains(t)) {
					addedOnRun.add(t);
				}
			}
			return new Boolean(target.addAll(values));
		}

		public void unrun() {
			target.removeAll(addedOnRun);
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + values + ')';
		}
	}

	public boolean addAll(Collection<? extends T> c) {
		return runner.run(new AddAll(c)).booleanValue();
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

	public boolean isEmpty() {
		return target.isEmpty();
	}

	/**
	 * warning
	 */
	public Iterator<T> iterator() {
		return target.iterator();
	}

	public class Remove implements Action<Boolean> {
		private Object value;
		private boolean removedOnRun;

		public Remove(Object value) {
			this.value = value;
		}

		public Boolean run() {
			removedOnRun = target.remove(value);
			return new Boolean(removedOnRun);
		}

		@SuppressWarnings("unchecked")
		public void unrun() {
			if (removedOnRun) {
				target.add((T) value);
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

	public class RemoveAll implements Action<Boolean> {
		private List<Object> values;
		private List<T> removedOnRun;

		public RemoveAll(Collection<?> values) {
			this.values = new ArrayList<Object>(values);
		}

		@SuppressWarnings("unchecked")
		public Boolean run() {
			removedOnRun = new ArrayList<T>();
			for (Object o : values) {
				if (target.contains(o)) {
					removedOnRun.add((T) o);
				}
			}
			return new Boolean(target.removeAll(values));
		}

		public void unrun() {
			target.addAll(removedOnRun);
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
		private List<T> removedOnRun;

		public RetainAll(Collection<?> values) {
			this.values = new ArrayList<Object>(values);
		}

		public Boolean run() {
			removedOnRun = new ArrayList<T>();
			for (T t : target) {
				if (!values.contains(t)) {
					removedOnRun.add(t);
				}
			}
			return new Boolean(target.retainAll(values));
		}

		public void unrun() {
			target.addAll(removedOnRun);
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + values + ')';
		}
	}

	public boolean retainAll(Collection<?> c) {
		return runner.run(new RetainAll(c)).booleanValue();
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

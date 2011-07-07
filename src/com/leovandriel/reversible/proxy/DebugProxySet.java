package com.leovandriel.reversible.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.leovandriel.reversible.action.Action;
import com.leovandriel.reversible.action.AdvancedRunner;

public class DebugProxySet<T> implements Set<T> {
	private Set<T> target;
	private AdvancedRunner runner;

	public DebugProxySet(Set<T> target, AdvancedRunner runner) {
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
		private boolean addedOnRun;
		private int sizeBeforeRun;
		private int sizeAfterRun;

		public Add(T value) {
			this.value = value;
		}

		public Boolean run() {
			sizeBeforeRun = target.size();
			addedOnRun = target.add(value);
			sizeAfterRun = target.size();
			return new Boolean(addedOnRun);
		}

		public void unrun() {
			if (target.size() != sizeAfterRun) {
				throw new IllegalStateException("Target modified, size should be " + sizeAfterRun + ", not "
						+ target.size());
			}
			if (addedOnRun) {
				if (!target.remove(value)) {
					throw new IllegalStateException("Target modified, unable to remove element");
				}
			}
			if (target.size() != sizeBeforeRun) {
				throw new IllegalStateException("Target modified, size should be " + sizeBeforeRun + ", not "
						+ target.size());
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

	public class AddAll extends DebugAction implements Action<Boolean> {
		private List<T> values;
		private Set<T> addedOnRun;
		private int sizeBeforeRun;
		private int sizeAfterRun;

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
			sizeBeforeRun = target.size();
			boolean result = target.addAll(values);
			sizeAfterRun = target.size();
			return new Boolean(result);
		}

		public void unrun() {
			if (target.size() != sizeAfterRun) {
				throw new IllegalStateException("Target modified, size should be " + sizeAfterRun + ", not "
						+ target.size());
			}
			if (!target.containsAll(addedOnRun)) {
				throw new IllegalStateException("Target modified, expected to contain all " + addedOnRun.size()
						+ " elements");
			}
			if (!target.removeAll(addedOnRun)) {
				throw new IllegalStateException("Target modified, unable to remove elements");
			}
			if (target.size() != sizeBeforeRun) {
				throw new IllegalStateException("Target modified, size should be " + sizeBeforeRun + ", not "
						+ target.size());
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

	public class Clear extends DebugAction implements Action<Void> {
		private List<T> backup;
		private int sizeBeforeRun;
		private int sizeAfterRun;

		public Void run() {
			backup = new ArrayList<T>(target);
			sizeBeforeRun = target.size();
			target.clear();
			sizeAfterRun = target.size();
			return null;
		}

		public void unrun() {
			if (target.size() != sizeAfterRun) {
				throw new IllegalStateException("Target modified, size should be " + sizeAfterRun + ", not "
						+ target.size());
			}
			target.addAll(backup);
			if (target.size() != sizeBeforeRun) {
				throw new IllegalStateException("Target modified, size should be " + sizeBeforeRun + ", not "
						+ target.size());
			}
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

	public class IteratorImpl extends DebugAction implements Iterator<T> {
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

	public class Remove extends DebugAction implements Action<Boolean> {
		private Object value;
		private boolean removedOnRun;
		private int sizeBeforeRun;
		private int sizeAfterRun;

		public Remove(Object value) {
			this.value = value;
		}

		public Boolean run() {
			sizeBeforeRun = target.size();
			removedOnRun = target.remove(value);
			sizeAfterRun = target.size();
			return new Boolean(removedOnRun);
		}

		@SuppressWarnings("unchecked")
		public void unrun() {
			if (target.size() != sizeAfterRun) {
				throw new IllegalStateException("Target modified, size should be " + sizeAfterRun + ", not "
						+ target.size());
			}
			if (removedOnRun) {
				if (!target.add((T) value)) {
					throw new IllegalStateException("Target modified, should not contain element");
				}
			}
			if (target.size() != sizeBeforeRun) {
				throw new IllegalStateException("Target modified, size should be " + sizeBeforeRun + ", not "
						+ target.size());
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

	public class RemoveAll extends DebugAction implements Action<Boolean> {
		private List<Object> values;
		private List<T> removedOnRun;
		private int sizeBeforeRun;
		private int sizeAfterRun;

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
			sizeBeforeRun = target.size();
			boolean result = target.removeAll(values);
			sizeAfterRun = target.size();
			return new Boolean(result);
		}

		public void unrun() {
			if (target.size() != sizeAfterRun) {
				throw new IllegalStateException("Target modified, size should be " + sizeAfterRun + ", not "
						+ target.size());
			}
			for (T t : removedOnRun) {
				if (target.contains(t)) {
					throw new IllegalStateException("Target modified, not expected to contain any of elements");
				}
			}
			target.addAll(removedOnRun);
			if (target.size() != sizeBeforeRun) {
				throw new IllegalStateException("Target modified, size should be " + sizeBeforeRun + ", not "
						+ target.size());
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

	public class RetainAll extends DebugAction implements Action<Boolean> {
		private List<Object> values;
		private List<T> removedOnRun;
		private int sizeBeforeRun;
		private int sizeAfterRun;

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
			sizeBeforeRun = target.size();
			boolean result = target.retainAll(values);
			sizeAfterRun = target.size();
			return new Boolean(result);
		}

		public void unrun() {
			if (target.size() != sizeAfterRun) {
				throw new IllegalStateException("Target modified, size should be " + sizeAfterRun + ", not "
						+ target.size());
			}
			for (T t : removedOnRun) {
				if (target.contains(t)) {
					throw new IllegalStateException("Target modified, not expected to contain any of elements");
				}
			}
			target.addAll(removedOnRun);
			if (target.size() != sizeBeforeRun) {
				throw new IllegalStateException("Target modified, size should be " + sizeBeforeRun + ", not "
						+ target.size());
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

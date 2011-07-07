package com.leovandriel.reversible.misc;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SingleSlot<T> extends AbstractCollection<T> implements Slot<T> {
	private T value;

	public SingleSlot(T value) {
		this.value = value;
	}

	public T get() {
		return value;
	}

	public T set(T value) {
		T old = this.value;
		this.value = value;
		return old;
	}

	private class IteratorImpl implements Iterator<T> {
		boolean started;

		public boolean hasNext() {
			return !started;
		}

		public T next() {
			if (!started) {
				started = true;
				return value;
			}
			throw new NoSuchElementException();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public Iterator<T> iterator() {
		return new IteratorImpl();
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SingleSlot<?>)) {
			return false;
		}
		SingleSlot<?> other = (SingleSlot<?>) obj;
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		if (value != this) {
			builder.append(value);
		} else {
			builder.append("(this Container)");
		}
		return builder.append(']').toString();
	}
}

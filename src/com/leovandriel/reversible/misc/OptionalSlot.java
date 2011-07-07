package com.leovandriel.reversible.misc;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class OptionalSlot<T> extends AbstractCollection<T> implements Slot<T> {
	private T value;
	private boolean hasValue;

	public OptionalSlot() {
	}

	public OptionalSlot(T value) {
		this.value = value;
		hasValue = true;
	}

	public T get() {
		return hasValue ? this.value : null;
	}

	public T set(T value) {
		T result = get();
		this.value = value;
		return result;
	}

	private class IteratorImpl implements Iterator<T> {
		boolean started;

		public boolean hasNext() {
			return hasValue && !started;
		}

		public T next() {
			if (hasNext()) {
				started = true;
				return value;
			}
			throw new NoSuchElementException();
		}

		public void remove() {
			if (hasValue && started) {
				hasValue = false;
				value = null;
			} else {
				throw new IllegalStateException();
			}
		}
	}

	@Override
	public Iterator<T> iterator() {
		return new IteratorImpl();
	}

	@Override
	public int size() {
		return hasValue ? 1 : 0;
	}

	@Override
	public boolean add(T e) {
		if (hasValue) {
			throw new RuntimeException("Already has value");
		}
		value = e;
		hasValue = true;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (hasValue ? 1231 : 1237);
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
		if (!(obj instanceof OptionalSlot<?>)) {
			return false;
		}
		OptionalSlot<?> other = (OptionalSlot<?>) obj;
		if (hasValue != other.hasValue) {
			return false;
		}
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
		if (hasValue) {
			if (value != this) {
				builder.append(value);
			} else {
				builder.append("(this Container)");
			}
		}
		return builder.append(']').toString();
	}
}

package com.leovandriel.reversible.misc;

import java.util.Collection;

public interface Slot<T> extends Collection<T> {
	public T get();

	public T set(T value);
}

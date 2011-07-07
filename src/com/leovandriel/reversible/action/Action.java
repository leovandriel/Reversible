package com.leovandriel.reversible.action;

public interface Action<T> {
	public T run();

	public void unrun();
}

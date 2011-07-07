package com.leovandriel.reversible.action;

public interface ActionRunner {
	public <T> T run(Action<T> action);
}

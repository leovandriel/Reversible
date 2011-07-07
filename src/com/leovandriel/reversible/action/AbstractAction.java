package com.leovandriel.reversible.action;

public class AbstractAction<T> implements Action<T> {
	private boolean hasRun;

	public T run() {
		if (hasRun) {
			throw new RuntimeException("Unable to run already run activity");
		}
		hasRun = true;
		return null;
	}

	public void unrun() {
		if (!hasRun) {
			throw new RuntimeException("Unable to cancel unrun activity");
		}
		hasRun = false;
	}
}

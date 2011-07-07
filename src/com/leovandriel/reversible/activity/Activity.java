package com.leovandriel.reversible.activity;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.leovandriel.reversible.action.Action;

public class Activity {
	private List<Action<?>> actions = new LinkedList<Action<?>>();
	private boolean hasRun;

	public Activity() {
	}

	public <T> T run(Action<T> action) {
		actions.add(action);
		T result = action.run();
		hasRun = true;
		return result;
	}

	public int size() {
		return actions.size();
	}

	public void undo() {
		if (!hasRun) {
			throw new RuntimeException("Unable to undo an unrun activity");
		}
		ListIterator<Action<?>> iterator = actions.listIterator(actions.size());
		while (iterator.hasPrevious()) {
			iterator.previous().unrun();
		}
		hasRun = false;
	}

	public void redo() {
		if (hasRun) {
			throw new RuntimeException("Unable to redo an run activity");
		}
		for (Action<?> a : actions) {
			a.run();
		}
		hasRun = true;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + '(' + actions + ')';
	}
}

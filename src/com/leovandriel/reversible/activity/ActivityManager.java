package com.leovandriel.reversible.activity;

import java.util.LinkedList;

import com.leovandriel.reversible.action.Action;
import com.leovandriel.reversible.action.ActionRunner;

public class ActivityManager implements ActionRunner {
	private LinkedList<Activity> undoActivities = new LinkedList<Activity>();
	private LinkedList<Activity> redoActivities = new LinkedList<Activity>();
	private Activity current;

	/**
	 * Should only be called by {@link #run(Action)} to ensure synchronous
	 * unnested calls.
	 */
	public <T> T run(Action<T> action) {
		if (current == null) {
			redoActivities.clear();
			current = new Activity();
		}
		return current.run(action);
	}

	/**
	 * Marks the current state by starting a new activity and pushing the
	 * current into history.
	 */
	public void mark() {
		undoActivities.push(current);
		current = null;
	}

	public void undo() {
		if (current != null) {
			mark();
		}
		current = undoActivities.pop();
		redoActivities.push(current);
		if (current != null) {
			current.undo();
			current = null;
		}
	}

	public void redo() {
		current = redoActivities.pop();
		if (current != null) {
			current.redo();
		}
		mark();
	}

	public boolean canUndo() {
		return current != null || !undoActivities.isEmpty();
	}

	public boolean canRedo() {
		return !redoActivities.isEmpty();
	}

	public void trimUndo(int size) {
		while (undoActivities.size() > size) {
			undoActivities.removeLast();
		}
	}

	public void trimRedo(int size) {
		while (redoActivities.size() > size) {
			redoActivities.removeLast();
		}
	}

	public int getUndoSize() {
		return undoActivities.size() + (current != null ? 1 : 0);
	}

	public int getRedoSize() {
		return redoActivities.size();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + '(' + undoActivities.size()
				+ " <- " + current + " -> " + redoActivities.size() + ')';
	}
}

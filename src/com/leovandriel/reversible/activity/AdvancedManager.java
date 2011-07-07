package com.leovandriel.reversible.activity;

import com.leovandriel.reversible.action.Action;
import com.leovandriel.reversible.action.AdvancedRunner;

public class AdvancedManager extends ActivityManager implements AdvancedRunner {
	private boolean running;
	private int maxUndoSize = Integer.MAX_VALUE;
	private int maxRedoSize = Integer.MAX_VALUE;

	/**
	 * Runs the provided action in the current activity.
	 */
	@Override
	public synchronized <T> T run(Action<T> action) {
		if (running) {
			throw new RuntimeException("Cannot run an action within an action.");
		}
		running = true;
		T result = super.run(action);
		running = false;
		return result;
	}

	@Override
	public void mark() {
		super.mark();
		trimUndo(maxUndoSize);
	}

	@Override
	public void undo() {
		if (!canUndo()) {
			throw new RuntimeException(
					"Unable to undo because there are no done activities");
		}
		super.undo();
		trimRedo(maxRedoSize);
	}

	/**
	 * Repeatedly undoes without checking.
	 *
	 * @param count
	 *            <= {@link #getUndoSize()}
	 */
	public void undo(int count) {
		for (; count > 0; count--) {
			super.undo();
		}
		trimRedo(maxRedoSize);
	}

	public void undoAll() {
		undo(getUndoSize());
		trimRedo(maxRedoSize);
	}

	/**
	 * redoes the last undone activity, or throws an exception if there is no
	 * future, which can be testen using {@link #canRedo()}.
	 */
	@Override
	public void redo() {
		if (!canRedo()) {
			throw new RuntimeException(
					"Unable to redo because there is no undone activities");
		}
		super.redo();
		trimUndo(maxUndoSize);
	}

	/**
	 * Repeatedly redoes without checking.
	 *
	 * @param count
	 *            <= {@link #getRedoSize()}
	 */
	public void redo(int count) {
		for (; count > 0; count--) {
			super.redo();
		}
		trimUndo(maxUndoSize);
	}

	public void redoAll() {
		redo(getRedoSize());
		trimUndo(maxUndoSize);
	}

	public void setMaxUndoSize(int size) {
		this.maxUndoSize = size;
	}

	public void setMaxRedoSize(int size) {
		this.maxRedoSize = size;
	}

	public boolean isRunningAction() {
		return running;
	}

}

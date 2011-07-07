package com.leovandriel.reversible.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class GroupAction extends AbstractAction<Void> {
	private List<Action<?>> actions;

	public GroupAction() {
		this.actions = new LinkedList<Action<?>>();
	}

	public GroupAction(Collection<Action<?>> actions) {
		this.actions = new ArrayList<Action<?>>(actions);
	}

	public void addAction(Action<?> action) {
		actions.add(action);
	}

	public int size() {
		return actions.size();
	}

	@Override
	public Void run() {
		super.run();
		for (Action<?> a : actions) {
			a.run();
		}
		return null;
	}

	@Override
	public void unrun() {
		super.unrun();
		ListIterator<Action<?>> iterator = actions.listIterator(actions.size());
		while (iterator.hasPrevious()) {
			iterator.previous().unrun();
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + '(' + actions + ')';
	}
}

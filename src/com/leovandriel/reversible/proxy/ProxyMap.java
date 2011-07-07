package com.leovandriel.reversible.proxy;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.leovandriel.reversible.action.Action;
import com.leovandriel.reversible.action.ActionRunner;

public class ProxyMap<K, V> implements Map<K, V> {
	private Map<K, V> target;
	private ActionRunner runner;

	public ProxyMap(Map<K, V> target, ActionRunner runner) {
		this.target = target;
		this.runner = runner;
	}

	public class Clear implements Action<Void> {
		private Map<K, V> backup;

		public Void run() {
			backup = new HashMap<K, V>(target);
			target.clear();
			return null;
		}

		public void unrun() {
			target.putAll(backup);
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + ')';
		}
	}

	public void clear() {
		runner.run(new Clear());
	}

	public boolean containsKey(Object key) {
		return target.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return target.containsValue(value);
	}

	public Set<Entry<K, V>> entrySet() {
		return new ProxySet<Entry<K, V>>(target.entrySet(), runner);
	}

	public V get(Object key) {
		return target.get(key);
	}

	public boolean isEmpty() {
		return target.isEmpty();
	}

	public Set<K> keySet() {
		return new ProxySet<K>(target.keySet(), runner);
	}

	public class Put implements Action<V> {
		private K key;
		private V value;
		private V previous;
		private boolean addedOnRun;

		public Put(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public V run() {
			addedOnRun = !target.containsKey(key);
			previous = target.put(key, value);
			return previous;
		}

		public void unrun() {
			if (addedOnRun) {
				target.remove(key);
			} else {
				target.put(key, previous);
			}
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + key + ',' + value + ')';
		}
	}

	public V put(K key, V value) {
		return runner.run(new Put(key, value));
	}

	public class PutAll implements Action<Void> {
		private Map<K, V> toBePuts;
		private Map<K, V> modifiedOnRun;
		private Set<K> addedOnRun;

		public PutAll(Map<? extends K, ? extends V> toBePuts) {
			this.toBePuts = new HashMap<K, V>(toBePuts);
		}

		public Void run() {
			modifiedOnRun = new HashMap<K, V>();
			addedOnRun = new HashSet<K>();
			for (Entry<? extends K, ? extends V> e : toBePuts.entrySet()) {
				if (target.containsKey(e.getKey())) {
					modifiedOnRun.put(e.getKey(), e.getValue());
				} else {
					addedOnRun.add(e.getKey());
				}
			}
			target.putAll(toBePuts);
			return null;
		}

		public void unrun() {
			for (K key : addedOnRun) {
				target.remove(key);
			}
			target.putAll(modifiedOnRun);
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + toBePuts + ')';
		}
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		runner.run(new PutAll(m));
	}

	public class Remove implements Action<V> {
		private Object key;
		private V backup;
		private boolean removedOnRun;

		public Remove(Object key) {
			this.key = key;
		}

		public V run() {
			removedOnRun = target.containsKey(key);
			backup = target.remove(key);
			return backup;
		}

		@SuppressWarnings("unchecked")
		public void unrun() {
			if (removedOnRun) {
				target.put((K) key, backup);
			}
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + '(' + key + ')';
		}
	}

	public V remove(Object key) {
		return runner.run(new Remove(key));
	}

	public int size() {
		return target.size();
	}

	public Collection<V> values() {
		return new ProxyCollection<V>(target.values(), runner);
	}

	@Override
	public int hashCode() {
		return target.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return target.equals(o);
	}

	@Override
	public String toString() {
		return target.toString();
	}
}

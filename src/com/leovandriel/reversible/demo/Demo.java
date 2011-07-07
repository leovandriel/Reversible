package com.leovandriel.reversible.demo;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.leovandriel.reversible.activity.AdvancedManager;
import com.leovandriel.reversible.proxy.DebugProxySet;
import com.leovandriel.reversible.proxy.ProxyList;

public class Demo {
	public static void main(String[] args) {
		AdvancedManager manager = new AdvancedManager();
		new Demo().run(new ProxyList<String>(new LinkedList<String>(), manager), manager);
		new Demo().run(new DebugProxySet<String>(new HashSet<String>(), manager), manager);
//		new Demo().example();
	}

	public void run(Collection<String> names, AdvancedManager manager) {
		System.out.println("\nDemo Reversible using a " + names.getClass().getSimpleName());
		manager.undoAll();
		System.out.println("Empty list: " + names + "  " + manager);
		names.add("nid");
		System.out.println("Add name  : " + names + "  " + manager);
		names.add("sancy");
		System.out.println("Add name  : " + names + "  " + manager);
		manager.undo();
		System.out.println("Undo      : " + names + "  " + manager);
		manager.redo();
		System.out.println("Redo      : " + names + "  " + manager);
		names.add("toni");
		System.out.println("Add name  : " + names + "  " + manager);
		manager.mark();
		System.out.println("Mark      : " + names + "  " + manager);
		names.add("bob");
		System.out.println("Add name  : " + names + "  " + manager);
		manager.undo(2);
		System.out.println("Undo twice: " + names + "  " + manager);
		names.add("john");
		System.out.println("Add name  : " + names + "  " + manager);
		manager.undo();
		System.out.println("Undo      : " + names + "  " + manager);
	}

	public void example() {
		{
			System.out.println("\nDemo Reversible example without undo");
			List<String> names = new LinkedList<String>();
			names.add("Maurice");
			names.add("Roy");
			names.add("Jen");
			System.out.println("Unsorted: " + names);
			Collections.sort(names);
			System.out.println("Sorted  : " + names);
		}
		{
			System.out.println("\nDemo Reversible example with undo");
			AdvancedManager manager = new AdvancedManager();
			List<String> names = new ProxyList<String>(new LinkedList<String>(), manager);
			names.add("Maurice");
			names.add("Roy");
			names.add("Jen");
			System.out.println("Unsorted: " + names + "  " + manager);
			manager.mark();
			Collections.sort(names);
			System.out.println("Sorted  : " + names + "  " + manager);
			manager.undo(); // undoes sorting names
			System.out.println("Undo    : " + names + "  " + manager);
			manager.undo(); // undoes adding names
			System.out.println("Undo    : " + names + "  " + manager);
		}
	}
}

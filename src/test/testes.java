package test;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class testes {
	static Map<Integer,Set<Integer>> m=new TreeMap<Integer,Set<Integer>>();
	public static void main(String[] args) {
		m.put(1,new TreeSet<Integer>());
		m.get(1).add(2);
		for(Integer i:m.get(1)) {
			System.out.println(i);
		}
	}
}

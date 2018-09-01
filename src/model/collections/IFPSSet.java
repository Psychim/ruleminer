package model.collections;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
//IFPS规则集合
public class IFPSSet implements Iterable<PPairSet>{
	private Set<PPairSet> rules;
	public IFPSSet() {
		rules=new TreeSet<PPairSet>();
	}
	public synchronized void add(PPairSet pset) {
		// TODO Auto-generated method stub
		rules.add(pset);
	}
	@Override
	public Iterator<PPairSet> iterator() {
		return rules.iterator();
	}
	public int size() {
		// TODO Auto-generated method stub
		return rules.size();
	}

}

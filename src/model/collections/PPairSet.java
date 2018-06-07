package model.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import model.element.PPair;
import util.SubSetGenerator;
import util.Util;

public class PPairSet implements Iterable<PPair>, Comparable<PPairSet>{
	private Set<PPair> p;
	private Map<Integer,Integer> m;
	public PPairSet(List<PPair> list) {
		// TODO Auto-generated constructor stub
		p=new TreeSet<PPair>();
		m=new TreeMap<Integer,Integer>();
		p.addAll(list);
	}
	public int size() {
		return p.size();
	}
	public synchronized void addPairIntoGraph(int hv) {
		// TODO Auto-generated method stub
		if(m.containsKey(hv)) {
			m.put(hv,m.get(hv)+1);
		}
		else m.put(hv, 2);
	}
	public double divergence() {
		int edges=0;
		for(Integer k:m.keySet()) {
			edges+=m.get(k)-1;
		}
		return m.keySet().size()*1./edges;
	}
	public boolean hasSubSet(PPairSet ps){
		for(PPair pp:ps) {
			if(!this.p.contains(pp))
				return false;
		}
		return true;
	}
	public boolean bestInSubSet(Suite suite) {
		SubSetGenerator<PPair> ssg=new SubSetGenerator<PPair>(p);
		while(ssg.next()) {
			PPairSet ps=new PPairSet(ssg.get());
			if(suite.contains(ps)) {
				ps=suite.ceiling(ps);
				if(this.divergence()<=ps.divergence()&&this.compareTo(ps)!=0)
					return false;
			}
		}
		return true;
	}
	@Override
	public Iterator<PPair> iterator() {
		return p.iterator();
	}
	@Override
	public int compareTo(PPairSet o) {
		// TODO Auto-generated method stub
		return Util.compareSet(this.p, o.p);
	}
	public Set<PPair> getSet() {
		// TODO Auto-generated method stub
		return p;
	}
	
}

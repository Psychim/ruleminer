package model.collections;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import model.element.Property;
import util.Util;

public class PropertySet implements Comparable<PropertySet>,Iterable<Property>{
	Set<Property> ps;
	int cnt;
	public PropertySet() {
		ps=new TreeSet<Property>();
		cnt=0;
	}
	public PropertySet(Property p,int pcnt) {
		// TODO Auto-generated constructor stub
		this();
		ps.add(p);
		cnt=pcnt;
	}
	public PropertySet(PropertySet p, PropertySet q) {
		// TODO Auto-generated constructor stub
		this();
		ps.addAll(p.ps);
		ps.addAll(q.ps);
	}
	public PropertySet(Set<Property> ps2) {
		// TODO Auto-generated constructor stub
		this();
		ps.addAll(ps2);
	}
	public synchronized void addCount() {
		// TODO Auto-generated method stub
		cnt++;
	}
	public int getCount() {
		// TODO Auto-generated method stub
		return cnt;
	}
	@Override
	public int compareTo(PropertySet o) {
		// TODO Auto-generated method stub
		return Util.compareSet(ps, o.ps);
	}
	public boolean isSmallerAtLast(PropertySet o) {
		if(ps.size()!=o.ps.size()) return false;
		Iterator<Property> ite1=ps.iterator();
		Iterator<Property> ite2=o.ps.iterator();
		Property p1,p2;
		p1=ite1.next();
		p2=ite2.next();
		while(ite1.hasNext()&&ite2.hasNext()) {
			if(p1.compareTo(p2)!=0) return false;
			p1=ite1.next();
			p2=ite2.next();
		}
		return p1.compareTo(p2)<0;
	}
	public boolean allk_1SubSetsIn(Set<PropertySet> l) {
		// TODO Auto-generated method stub
		PropertySet subset=new PropertySet(ps);
		for(Property p:ps) {
			subset.remove(p);
			if(!l.contains(subset)) return false;
			subset.add(p);
		}
		return true;
	}
	private synchronized void add(Property p) {
		// TODO Auto-generated method stub
		this.ps.add(p);
	}
	private synchronized void remove(Property p) {
		// TODO Auto-generated method stub
		this.ps.remove(p);
	}
	@Override
	public Iterator<Property> iterator() {
		// TODO Auto-generated method stub
		return ps.iterator();
	}
	public int size() {
		// TODO Auto-generated method stub
		return ps.size();
	}
	public boolean isFromDifferentSource(PropertySet q) {
		// TODO Auto-generated method stub
		Property p1=ps.iterator().next();
		Property p2=q.ps.iterator().next();
		if(p1==null||p2==null) return true;
		return p1.getSource()!=p2.getSource();
	}
}

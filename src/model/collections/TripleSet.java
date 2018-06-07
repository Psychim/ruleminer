package model.collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import model.element.Instance;
import model.element.Property;
import model.element.Triple;

public class TripleSet implements Iterable<Triple>{
	private List<Triple> triples;
	public TripleSet() {
		triples=new LinkedList<Triple>();
	}
	@Override
	public Iterator<Triple> iterator() {
		return triples.iterator();
	}
	public TripleSet select(Instance e) {
		TripleSet res=new TripleSet();
		for(Triple t:triples) {
			if(t.getS().equals(e)) {
				res.add(t);
			}
		}
		return res;
	}
	public synchronized void add(Triple t) {
		triples.add(t);
	}
	public Set<Instance> getSubjects() {
		// TODO Auto-generated method stub
		Set<Instance> res=new TreeSet<Instance>();
		for(Triple t:triples) {
			res.add(t.getS());
		}
		return res;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((triples == null) ? 0 : triples.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TripleSet other = (TripleSet) obj;
		if (triples == null) {
			if (other.triples != null)
				return false;
		} else if (!triples.equals(other.triples))
			return false;
		return true;
	}
	public int size() {
		// TODO Auto-generated method stub
		return triples.size();
	}
	public Triple first() {
		// TODO Auto-generated method stub
		for(Triple t:triples)
			return t;
		return null;
	}
	public synchronized void addAll(TripleSet ts2) {
		// TODO Auto-generated method stub
		triples.addAll(ts2.triples);
	}
	public Set<Instance> valuesOf(Property p1) {
		// TODO Auto-generated method stub
		Set<Instance> res=new TreeSet<Instance>();
		for(Triple t:triples) {
			if(t.getP().equals(p1)) {
				res.add(t.getO());
			}
		}
		return res;
	}
}

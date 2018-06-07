package model.collections;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import model.element.Correspondence;
import model.element.Instance;

public class CorrespondenceSet implements Iterable<Correspondence> {
	private TreeSet<Correspondence> cores;
	public CorrespondenceSet() {
		cores=new TreeSet<Correspondence>();
	}
	public int size() {
		return cores.size();
	}
	public MatchSet select(double threshold) {
		MatchSet res=new MatchSet();
		for(Correspondence c:cores) {
			if(c.getConf()>threshold) {
				res.add(c.getMatch());
			}
		}
		return res;
	}

	@Override
	public Iterator<Correspondence> iterator() {
		return cores.iterator();
	}

	public synchronized void add(Instance e1, Instance e2, double conf) {
		// TODO Auto-generated method stub
		Correspondence c1=new Correspondence(e1,e2,conf),
				c2=cores.ceiling(c1);
		if(c1.equals(c2)) {
			cores.remove(c2);
			c1.setConf(c1.getConf()*c2.getConf()/(1-c1.getConf()-c2.getConf()+2*c1.getConf()*c2.getConf()));
		}
		cores.add(c1);
	}
	public MatchSet getMatchSet() {
		// TODO Auto-generated method stub
		MatchSet res=new MatchSet();
		for(Correspondence c:cores) {
			res.add(c.getMatch());
		}
		return res;
	}
}

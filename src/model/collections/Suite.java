package model.collections;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import model.element.PPair;

public class Suite implements Iterable<PPairSet>{
	private TreeSet<PPairSet> ppsset;
//	private Map<PPair,Set<PPairSet>> index;
	public Suite() {
		ppsset=new TreeSet<PPairSet>();
//		index=new TreeMap<PPair,Set<PPairSet>>();
	}
	public synchronized void add(PPairSet pps,int hv) {
		PPairSet pset2=ppsset.ceiling(pps);
		if(pset2!=null&&pset2.compareTo(pps)==0) {
			pset2.addPairIntoGraph(hv);
		}
		else{
			pps.addPairIntoGraph(hv);
			ppsset.add(pps);
		}
	}
	@Override
	public Iterator<PPairSet> iterator() {
		// TODO Auto-generated method stub
		return ppsset.iterator();
	}
	public PPairSet ceiling(PPairSet pps) {
		return ppsset.ceiling(pps);
	}
	public int size() {
		// TODO Auto-generated method stub
		return ppsset.size();
	}
	public boolean contains(PPairSet ps) {
		// TODO Auto-generated method stub
		return ppsset.contains(ps);
	}
}

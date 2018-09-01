package model.element;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import model.collections.PropertySet;
//事务，只记录属性
public class Transaction implements Iterable<Property>{
//	private Set<PVPair> items;
	private Set<Property> psofItems;	
	public Transaction() {
//		items=new TreeSet<PVPair>();
		psofItems=new TreeSet<Property>();
	}
	public synchronized void add(Property p, Instance o) {
		// TODO Auto-generated method stub
//		items.add(new PVPair(p,o));
		psofItems.add(p);
	}
	@Override
	public Iterator<Property> iterator() {
		// TODO Auto-generated method stub
		return psofItems.iterator();
	}
	public boolean contains(PropertySet ps) {
		// TODO Auto-generated method stub
		for(Property p:ps) {
			if(!psofItems.contains(p))return false;
		}
		return true;
	}
	public boolean contains(PPair pp) {
		// TODO Auto-generated method stub
		return psofItems.contains(pp.getP1())&&psofItems.contains(pp.getP2());
	}
//	@Override
//	public int compareTo(Transaction o) {
//		// TODO Auto-generated method stub
//		return Util.compareSet(items, o.items);
//	}
}

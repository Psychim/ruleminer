package algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import model.collections.PPairSet;
import model.collections.TripleSet;
import model.element.Instance;
import model.element.PPair;
import model.element.PVPair;
import model.element.Property;
import model.element.Triple;

public class PVSetProcessor {
	private final static int b=378551;
	private final static int a=63689;
	private int cura=a;
	private int hashvalue=1;
	private Map<Property,Set<Instance>> ep1,ep2;
	private PPairSet pset;
	public PVSetProcessor(Map<Property,Set<Instance>> ep1arg,Map<Property,Set<Instance>> ep2arg,PPairSet pset){
		ep1=ep1arg;
		ep2=ep2arg;
		this.pset=pset;
	}
	private boolean existEqualObject(Set<Instance> e1vset, Set<Instance> e2vset,PPair pp) {
		// TODO Auto-generated method stub
		for(Instance e1:e1vset) {
			for(Instance e2:e2vset) {
				if(e1.approxEqual(e2)) {
					hashvalue=hashvalue*cura+new PVPair(pp.getP1(),e1).hashCode()+new PVPair(pp.getP2(),e2).hashCode();
					cura*=b;
					return true;
				}
			}
		}
		return false;
	}
	public boolean PVPairSetsIsEqual() {
		// TODO Auto-generated method stub
		hashvalue=1;
		for(PPair pp:pset) {
			Set<Instance> o1=ep1.get(pp.getP1());
			Set<Instance> o2=ep2.get(pp.getP2());
			if(o1==null||o2==null) return false;
			if(!existEqualObject(o1,o2,pp)) return false;
		}
		return true;
	}
	public int hashValue() {
		// TODO Auto-generated method stub
		return hashvalue;
	}
}

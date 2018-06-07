package model.collections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import model.element.Instance;
import model.element.PPair;
import model.element.PVPair;
import model.element.PVPair2;
import model.element.Property;

public class RuleMatcher {
	private Map<PVPair2,Set<Instance>> candidates;
	private PPairSet rule;
	public RuleMatcher(PPairSet r) {
		candidates=new TreeMap<PVPair2,Set<Instance>>();
		rule=r;
	}
	public void add(Instance e,Map<Property,Set<Instance>> pvmap) {
		for(PPair pp:rule) {
			Set<Instance> values=pvmap.get(pp.getP1());
			for(Instance v:values) {
				PVPair2 pv=new PVPair2(pp.getP1(),v);
				if(!candidates.containsKey(pv))
					candidates.put(pv, new TreeSet<Instance>());
				candidates.get(pv).add(e);
			}
		}
	}
	public Set<Instance> match2(Map<Property,Set<Instance>> pvmap){
		Set<Instance> res=null;
		for(PPair pp:rule) {
			Set<Instance> tmp=new TreeSet<Instance>();
			Set<Instance> values=pvmap.get(pp.getP2());
			for(Instance v:values) {
				PVPair2 pv=new PVPair2(pp.getP1(),v);
				if(candidates.containsKey(pv))
					tmp.addAll(candidates.get(pv));
			}
			if(res==null) res=tmp;
			else res.retainAll(tmp);
		}
		return res;
	}
}
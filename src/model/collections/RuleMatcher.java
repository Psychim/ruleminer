package model.collections;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import model.element.Instance;
import model.element.PPair;
import model.element.PVPair2;
import model.element.Property;

public class RuleMatcher {
	private Map<PVPair2,Set<Instance>> candidates;	//按属性-值对索引实体
	private PPairSet rule;
	public RuleMatcher(PPairSet r) {
		candidates=new TreeMap<PVPair2,Set<Instance>>();
		rule=r;
	}
	public void add(Instance e,Map<Property,Set<Instance>> pvmap) {
		//将e加入pvmap中所有属性-值对的索引表
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
		//求pvmap所有属性-值对索引表的交集，即为匹配到的实体集合
		for(PPair pp:rule) {	//遍历规则的等价属性对
			Set<Instance> tmp=new TreeSet<Instance>();
			Set<Instance> values=pvmap.get(pp.getP2());	//pvmap中的属性来自源1
			for(Instance v:values) {	//遍历值，对同一个属性的所有值的索引表求并集（因为按eifps规则，同一个属性的值是或的关系）
				PVPair2 pv=new PVPair2(pp.getP1(),v);	//保存下的candidates中的属性来自源0
				if(candidates.containsKey(pv))
					tmp.addAll(candidates.get(pv));
			}
			//不同属性间则求交集
			if(res==null) res=tmp;
			else res.retainAll(tmp);
		}
		return res;
	}
}
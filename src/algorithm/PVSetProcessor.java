package algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import model.collections.PPairSet;
import model.collections.TripleSet;
import model.element.EPSKey;
import model.element.Instance;
import model.element.PPair;
import model.element.PVPair;
import model.element.Property;
import model.element.Triple;

public class PVSetProcessor {
	//哈希用的参数，现在用EPSKey替代哈希来保证不发生冲撞，时间空间的代价也不是很大）
	private final static int b=378551;
	private final static int a=63689;
	private int cura=a;
	private Set<EPSKey> values;	//EPSKey
	private Map<Property,Set<Instance>> ep1,ep2;	//待匹配实体对的属性值对集合
	private PPairSet pset;	//规则
	public PVSetProcessor(Map<Property,Set<Instance>> ep1arg,Map<Property,Set<Instance>> ep2arg,PPairSet pset){
		ep1=ep1arg;
		ep2=ep2arg;
		this.pset=pset;
	}
	//判断对等价属性对pp是否存在相等的值并更新EPSKey
	//若存在多个相等的值，为每个值和每个旧的EPSKey构造一个新的EPSKey
	//因此每个事务可能在CG图中引入多条边
	private boolean existEqualObject(Set<Instance> e1vset, Set<Instance> e2vset,PPair pp) {
		// TODO Auto-generated method stub
		Set<EPSKey> newves=new HashSet<EPSKey>();
		boolean flag=false;
		for(Instance e1:e1vset) {	//源0实体的值
			for(Instance e2:e2vset) {	//源1实体的值
				if(e1.approxEqual(e2)) {	//这里用approxEqual判断两个值是否等价，而equals主要用作判断两个Instance对象是否相等
					for(EPSKey value: values){
						EPSKey newv=new EPSKey(value);
						newv.add(e1.getValueIndex());
						newves.add(newv);
						flag=true;
					}
				}
			}
		}
		values=newves;
		cura*=b;
		return flag;
	}
	//判断属性值对集合是否等价
	public boolean PVPairSetsIsEqual() {
		// TODO Auto-generated method stub
		values=new HashSet<EPSKey>();
		values.add(new EPSKey());	//先加入空的EPSKey，便于后面的操作
		for(PPair pp:pset) {	//枚举等价属性对
			//取属性的值
			Set<Instance> o1=ep1.get(pp.getP1());
			Set<Instance> o2=ep2.get(pp.getP2());
			if(o1==null||o2==null) return false;
			if(!existEqualObject(o1,o2,pp)) return false;
		}
		return true;
	}
	public Set<EPSKey> getValues() {
		// TODO Auto-generated method stub
		return values;
	}
}

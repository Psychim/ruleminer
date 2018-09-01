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
	private Map<PVPair2,Set<Instance>> candidates;	//������-ֵ������ʵ��
	private PPairSet rule;
	public RuleMatcher(PPairSet r) {
		candidates=new TreeMap<PVPair2,Set<Instance>>();
		rule=r;
	}
	public void add(Instance e,Map<Property,Set<Instance>> pvmap) {
		//��e����pvmap����������-ֵ�Ե�������
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
		//��pvmap��������-ֵ��������Ľ�������Ϊƥ�䵽��ʵ�弯��
		for(PPair pp:rule) {	//��������ĵȼ����Զ�
			Set<Instance> tmp=new TreeSet<Instance>();
			Set<Instance> values=pvmap.get(pp.getP2());	//pvmap�е���������Դ1
			for(Instance v:values) {	//����ֵ����ͬһ�����Ե�����ֵ���������󲢼�����Ϊ��eifps����ͬһ�����Ե�ֵ�ǻ�Ĺ�ϵ��
				PVPair2 pv=new PVPair2(pp.getP1(),v);	//�����µ�candidates�е���������Դ0
				if(candidates.containsKey(pv))
					tmp.addAll(candidates.get(pv));
			}
			//��ͬ���Լ����󽻼�
			if(res==null) res=tmp;
			else res.retainAll(tmp);
		}
		return res;
	}
}
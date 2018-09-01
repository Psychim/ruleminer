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
	//��ϣ�õĲ�����������EPSKey�����ϣ����֤��������ײ��ʱ��ռ�Ĵ���Ҳ���Ǻܴ�
	private final static int b=378551;
	private final static int a=63689;
	private int cura=a;
	private Set<EPSKey> values;	//EPSKey
	private Map<Property,Set<Instance>> ep1,ep2;	//��ƥ��ʵ��Ե�����ֵ�Լ���
	private PPairSet pset;	//����
	public PVSetProcessor(Map<Property,Set<Instance>> ep1arg,Map<Property,Set<Instance>> ep2arg,PPairSet pset){
		ep1=ep1arg;
		ep2=ep2arg;
		this.pset=pset;
	}
	//�ж϶Եȼ����Զ�pp�Ƿ������ȵ�ֵ������EPSKey
	//�����ڶ����ȵ�ֵ��Ϊÿ��ֵ��ÿ���ɵ�EPSKey����һ���µ�EPSKey
	//���ÿ�����������CGͼ�����������
	private boolean existEqualObject(Set<Instance> e1vset, Set<Instance> e2vset,PPair pp) {
		// TODO Auto-generated method stub
		Set<EPSKey> newves=new HashSet<EPSKey>();
		boolean flag=false;
		for(Instance e1:e1vset) {	//Դ0ʵ���ֵ
			for(Instance e2:e2vset) {	//Դ1ʵ���ֵ
				if(e1.approxEqual(e2)) {	//������approxEqual�ж�����ֵ�Ƿ�ȼۣ���equals��Ҫ�����ж�����Instance�����Ƿ����
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
	//�ж�����ֵ�Լ����Ƿ�ȼ�
	public boolean PVPairSetsIsEqual() {
		// TODO Auto-generated method stub
		values=new HashSet<EPSKey>();
		values.add(new EPSKey());	//�ȼ���յ�EPSKey�����ں���Ĳ���
		for(PPair pp:pset) {	//ö�ٵȼ����Զ�
			//ȡ���Ե�ֵ
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

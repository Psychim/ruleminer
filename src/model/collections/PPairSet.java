package model.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import model.element.EPSKey;
import model.element.PPair;
import util.SubSetGenerator;
import util.Util;
//IFPS����
public class PPairSet implements Iterable<PPair>, Comparable<PPairSet>{
	private TreeSet<PPair> p;
	private Map<EPSKey,Integer> m;	//CGͼ��ֻ��Ҫ��¼ÿ����ͨ�����Ĵ�С���ɣ�����һ��EPSKey��Ӧһ����ͨ����
	private int support=0;
	private double div=-1;	//divergence��-1��ʾδ�������CGͼ�Ѿ����¹�����Ҫ���¸���m����divergence
	public PPairSet() {
		p=new TreeSet<PPair>();
		m=new HashMap<EPSKey,Integer>();
	}
	public PPairSet(Collection<PPair> list) {
		// TODO Auto-generated constructor stub
		this();
		p.addAll(list);
	}
	public PPairSet(PPair pp) {
		// TODO Auto-generated constructor stub
		this();
		p.add(pp);
	}
	public PPairSet(PPairSet pps) {
		// TODO Auto-generated constructor stub
		this();
		p.addAll(pps.p);
	}
	public void addPPair(PPair pp) {
		p.add(pp);
	}
	public int size() {
		return p.size();
	}
	//��һ������������±߼���CGͼ
	//Ŀǰ��Ϊһ�����񼴱������˶�����Ҳֻ����һ��֧�ֶ�
	public synchronized void addPairIntoGraph(Set<EPSKey> ves) {
		// TODO Auto-generated method stub
		div=-1;
		support++;
		for(EPSKey v:ves){
			if(m.containsKey(v)) {
				m.put(v,m.get(v)+1);
			}
			else m.put(v, 2);
		}
	}
	//����divergence
	public double divergence() {
		if(div>=0)
			return div;
		int edges=0;
		for(Entry<EPSKey,Integer> k:m.entrySet()) {
			edges+=k.getValue()-1;
		}
		if(edges==0) div=0;
		else div=m.keySet().size()*1./edges;
		return div;
	}
	//�Ƿ����Ӽ�ps
	public boolean hasSubSet(PPairSet ps){
		for(PPair pp:ps) {
			if(!this.p.contains(pp))
				return false;
		}
		return true;
	}
	@Override
	public Iterator<PPair> iterator() {
		return p.iterator();
	}
	@Override
	public int compareTo(PPairSet o) {
		// TODO Auto-generated method stub
		return Util.compareSet(this.p, o.p);
	}
	public Set<PPair> getSet() {
		// TODO Auto-generated method stub
		return p;
	}
	public PPair SmalleastPPair() {
		return p.first();
	}
	//�Ƿ���ifpss�к��е����е�ǰ��������Ӽ��о�����ߵ�divergence
	//���һ�����Ӽ���divergence�Ѿ�����candidates���Ŷȵ���ֵ����ô��ǰ����Ҳû�б�Ҫ�ټ���ifps���򼯺�
	public boolean bestInSubSet(IFPSSet ifpss, double matchThreshold) {
		// TODO Auto-generated method stub
		Iterator<PPairSet> ite=ifpss.iterator();
		while(ite.hasNext()){
			PPairSet pps=ite.next();
			if(this.hasSubSet(pps)&&(this.divergence()<=pps.divergence()||pps.divergence()>matchThreshold)&&this.compareTo(pps)!=0)
				return false;
		}
		return true;
	}
	public void printGraph() {
		// TODO Auto-generated method stub
		for(EPSKey k:m.keySet()) {
			System.out.println(k.hashCode()+"\t"+m.get(k));
		}
	}
	public boolean hasSupport(int i) {
		// TODO Auto-generated method stub
		return support>=i;
	}
	public int getSupport() {
		// TODO Auto-generated method stub
		return support;
	}
	public int getComponentNum(){
		return m.size();
	}
}

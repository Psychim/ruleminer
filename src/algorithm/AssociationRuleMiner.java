package algorithm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import model.collections.PropertySet;
import model.collections.TransactionTable;
import model.element.PPair;
import model.element.PVPair;
import model.element.Property;
import model.element.Transaction;
import util.Operator;
import util.SubSetGenerator;

public class AssociationRuleMiner {
	public static int MINSUP=10;	//��С֧�ֶ�
	public static double MINCONF=0.1;	//��С���Ŷ�
	private TreeMap<Property,List<Integer>> plist;	//�����������������
	private TransactionTable trans;	//�����
	private Integer counter;	
	public AssociationRuleMiner(TransactionTable tt) {
		trans=tt;
	}
	public Map<Property,List<Integer>> getPlist(){
		return plist;
	}
	//�ھ�ȼ����Զ�
	public Set<PPair> MineAssociationRules(){
		System.out.println("<AssociationRuleMiner>Mining large item sets...");
		System.out.println("<AssociationRuleMiner>get large 1 item sets...");
		//�ھ��СΪ1��Ƶ���
		plist=getLarge1Property();
		System.out.println("<AssociationRuleMiner> "+plist.size()+" large 1 item sets were found.");
		//�ھ��СΪ2��Ƶ���������ֵΪ(���Զ�->֧�ֶȣ�
		Map<PPair,Integer> l2=Apriori();
		System.out.println("<AssociationRuleMiner> "+l2.keySet().size()+" large 2 item sets were found.");
		Set<PPair> res=new TreeSet<PPair>();
//		int i=0;
//		List<Integer> pl;
		//�ھ�ȼ�����
		for(PPair ps:l2.keySet()) {
			if(l2.get(ps)>=MINCONF*plist.get(ps.getP1()).size()&&
					l2.get(ps)>=MINCONF*plist.get(ps.getP2()).size()) {
//				pl=Operator.biAnd(l1.get(ps.getP1()), l1.get(ps.getP2()));
				res.add(ps);
			}
		}
		return res;
	}
	//It's enough to look up sets of size 2 due to only one-to-one relationships are required
	public Map<PPair,Integer>  Apriori() {
		Map<PPair,Integer> res=new TreeMap<PPair,Integer>();
		System.out.println("<Apriori>Mining large 2 itemsets...");
		ExecutorService es=Executors.newFixedThreadPool(41);
		for(Property p2:plist.keySet()) {
			if(p2.getSource()==0) continue;	//ֻ�ھ�ͬ��Դ�����Զԣ�����������Դ0��
			es.execute(new Runnable() {
				private Property p=p2;
				private Map<PPair,Integer> resbuf=new TreeMap<PPair,Integer>();
				public void run() {
					// TODO Auto-generated method stub
					List<Integer> pl,t1,t2;
					for(Property p1:plist.keySet()) {
						if(p1.getSource()>=p.getSource()) break;	//������Դ1��
						t1=plist.get(p1);	//��p1�����񼯺�
						t2=plist.get(p2);	//��p2�����񼯺�
						pl=Operator.biAnd(t1,t2);	//�󽻼�
						if(pl.size()>=MINSUP) {
							resbuf.put(new PPair(p1,p), pl.size());
						}
					}
					synchronized(res) {
						res.putAll(resbuf);
					}
				}
			});
		}
		es.shutdown();
		try {
			while(!es.awaitTermination(5, TimeUnit.SECONDS)) {
				System.out.print(res.size()+"...");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println();
		return res;
	}
	private TreeMap<Property,List<Integer>> getLarge1Property() {
		// TODO Auto-generated method stub
		//System.out.println("<getLarge1Itemsets>");
		Map<Property,List<Integer>> res=new TreeMap<Property,List<Integer>>();
		int i=0;
		for(Transaction t:trans) {	//��������
			for(Property p:t) {		//���������е�����
				//������t����p����������
				if(!res.containsKey(p)) {
					res.put(p, new LinkedList<Integer>());
				}
				res.get(p).add(i);
			}
			i++;
			if(i%(trans.size()/100)==0) {
				System.out.print(i+"...");
			}
		}
		System.out.println();
		TreeMap<Property,List<Integer>> realres=new TreeMap<Property,List<Integer>>();
		int j=0;
		//ɸѡ�������㹻֧�ֶȵ�����
		for(Property p:res.keySet()) {
			if(res.get(p).size()>=MINSUP) {
				realres.put(p,res.get(p));
				j++;
				if(j%1000==0) {
					System.out.print(j+"...");
				}
			}
		}
		System.out.println();
		return realres;
	}
}

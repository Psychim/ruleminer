package algorithm;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import concurrent.CandidatesReader;
import concurrent.MatchSetReader;
import concurrent.TripleSetReader;
import model.collections.CorrespondenceSet;
import model.collections.IFPSSet;
import model.collections.MatchSet;
import model.collections.PPairSet;
import model.collections.RuleMatcher;
import model.collections.Suite;
import model.collections.TransactionTable;
import model.collections.TripleSet;
import model.element.Correspondence;
import model.element.EPSKey;
import model.element.Instance;
import model.element.Match;
import model.element.PPair;
import model.element.PVPair;
import model.element.Pair;
import model.element.Property;
import model.element.Transaction;
import model.element.Triple;
import util.Operator;
import util.SubSetGenerator;
import util.Util;

public class MatchingRuleMiner {
	//private TripleSet triples;
	private MatchSet seeds,matches;	//���Ӽ��ϡ�ƥ�伯��
	private IFPSSet ifpss;	//ifps���򼯺�
	private Map<Instance,TripleSet> triples1,triples2;	//��subject������Ԫ�飬triples1ΪԴ0��triples2ΪԴ1
	private Map<Instance,TripleSet> objectMap;	//��object������Ԫ�飬ֻ����Դ1����Ԫ��
	private Map<Property,TripleSet> propertyMap1,propertyMap2;	//������������Ԫ�飬�ֱ�ΪԴ0��Դ1
	private CorrespondenceSet candidates;
	private int iteration=1;	//��ǰ��������
	private double matchThreshold=0.98,	//candidates���Ŷ���ֵ
				divergenceThreshold=0.95;	//ifps����divergence��ֵ
	private RuleMatcher matcher=null;	//ƥ����������ΪԴ1�е�ʵ�尴������ifps����ƥ��Դ0�е�ʵ��
	private int ruleSupport=10;	//ifps������С֧�ֶ�
	private boolean largeEnough=false;	//largeEnough���
	public MatchingRuleMiner(File f1, File f2, File fseed, double threshold,int minSupport, int startIteration) throws InterruptedException, ExecutionException {
		// TODO Auto-generated constructor stub
		matchThreshold=threshold;	//candidates���Ŷ���ֵ
		iteration=startIteration;	//��ǰ��������
		ruleSupport=minSupport;	//��Сifps����֧�ֶ�
		ExecutorService es0=Executors.newCachedThreadPool();
		Future<Map<Instance,TripleSet>> fut_t1=es0.submit(new TripleSetReader(f1,0));	//��ȡԴ0��Ԫ��
		Future<Map<Instance,TripleSet>> fut_t2=es0.submit(new TripleSetReader(f2,1));	//��ȡԴ1��Ԫ��
		Future<MatchSet> fut_seeds=null;
		if(startIteration==1) {	//�ӵ�һ�ε�����Ҳ�����ʼ��ʼ����
			fut_seeds=es0.submit(new MatchSetReader(fseed));	//��ȡ����
			matches=new MatchSet();
			candidates=new CorrespondenceSet();
			es0.shutdown();
		}
		else {	//�ӵ�2�ε����Ժ�ʼ����
			fut_seeds=es0.submit(new MatchSetReader(new File("./seeds_iteration"+startIteration)));	//��ȡ�����µ������ļ�
			Future<MatchSet> fut_matches=es0.submit(new MatchSetReader(new File("./matches_iteration"+startIteration)));	//��ȡ�����ƥ���ļ�
			Future<CorrespondenceSet> fut_cand=es0.submit(new CandidatesReader(new File("./candidates_iteration"+startIteration)));	//��ȡ�����candidates�ļ�
			es0.shutdown();
			matches=fut_matches.get();
			candidates=fut_cand.get();
		}
		triples1=fut_t1.get();
		triples2=fut_t2.get();
		seeds=fut_seeds.get();
		System.out.println();
		System.out.println("<MatchingRuleMiner> "+triples1.keySet().size()+" subjects in source 0");
		System.out.println("<MatchingRuleMiner> "+triples2.keySet().size()+" subjects in source 1");
		System.out.println("<MatchingRuleMiner> "+seeds.size()+" matches in seeds");
		System.out.println("<MatchingRuleMiner> "+matches.size()+" matches found in previous iterations");
		System.out.println("<MatchingRuleMiner> "+candidates.size()+" candidates found in previous iterations");
		objectMap=new HashMap<Instance,TripleSet>();
		propertyMap1=new HashMap<Property,TripleSet>();
		propertyMap2=new HashMap<Property,TripleSet>();
		for(Instance e:triples2.keySet()) {
			//triples2.get(e).add(new Triple(e,new Property("label",1),new Instance(Util.getLabelname(e.getValue()),1)));
			for(Triple t:triples2.get(e)) {
				if(!objectMap.containsKey(t.getO())) {
					objectMap.put(t.getO(), new TripleSet());
				}
				objectMap.get(t.getO()).add(t);
				if(!propertyMap2.containsKey(t.getP())){
					propertyMap2.put(t.getP(),new TripleSet());
				}
				propertyMap2.get(t.getP()).add(t);
			}
		}
		for(Instance e:triples1.keySet()) {
			//triples1.get(e).add(new Triple(e,new Property("label",0),new Instance(Util.getLabelname(e.getValue()),0)));
			for(Triple t:triples1.get(e)) {
				if(!propertyMap1.containsKey(t.getP())){
					propertyMap1.put(t.getP(),new TripleSet());
				}
				propertyMap1.get(t.getP()).add(t);
			}
		}
	}
	private void MineIFPSs() throws InterruptedException, ExecutionException {
		//O(|seeds|*|triples|)
		//��ÿ�����ӵ�Դ0ʵ���滻��Ԫ�鼯������Ӧ��Դ1ʵ��
		System.out.println("<MineIFPSs>Replacing objects...");
		for(Match c:seeds) {
			TripleSet ts2=objectMap.get(c.getE(1));
			if(ts2==null) continue;
			else objectMap.remove(c.getE(1));
			//System.out.println("<MIneIFPSs>replace "+c.getE(1)+" with "+c.getE(0));
			for(Triple t:ts2) {
				t.setO(c.getE(0));
			}
			//Since triples are modified here, TripleSet are unordered;
		}
		System.out.println("<MineIFPSs>Constructing transactions...");
		//���������
		TransactionTable trans=new TransactionTable();
		MatchSet unionmatches=new MatchSet();
		unionmatches.append(seeds);
		for(Correspondence c:candidates) {
			unionmatches.add(c.getMatch());
		}
//		unionmatches.append(candidates.getMatchSet());
		//ÿ���������һ��ƥ�䣬�Լ���ƥ�������ʵ����е�����
		for(Match c:unionmatches) {
			Transaction tran=new Transaction();
			TripleSet e1triples=triples1.get(c.getE(0)),
					  e2triples=triples2.get(c.getE(1));
			if(e1triples!=null) {
				for(Triple t:e1triples) {
					tran.add(t.getP(),t.getO());
				}
			}
			if(e2triples!=null) {
				for(Triple t:e2triples) {
					tran.add(t.getP(),t.getO());
				}
			}
			trans.add(tran,c);
		}
		unionmatches=null;	//�������
		System.out.println("<MineIFPSs>Size of transaction table is "+trans.size());
		System.out.println("<MineIFPSs>Mining association rules...");
		//�ھ�ȼ����Զ�
		AssociationRuleMiner arm=new AssociationRuleMiner(trans);
		Set<PPair> pEquivalents=arm.MineAssociationRules();
		System.out.println("<MineIFPSs>"+pEquivalents.size()+" pairs in total.");
		System.out.println("<MineIFPSs>Mining suites...");
		//�ھ�ifps����
		MineIFPSDirectly(pEquivalents,arm.getPlist(),trans);
		System.out.println("<MineIFPSs> "+ifpss.size()+" rules were found.");
	}
	private List<Integer> ComputeDivergence(PPairSet pps,List<Integer> lm,TransactionTable trans){
		List<Integer> newlm=new LinkedList<Integer>();
		for(Integer index:lm) {	//�����������
			Match m=trans.getMatch(index);	//�����Ӧ��ƥ��
			TripleSet e1triples=triples1.get(m.getE(0));
			TripleSet e2triples=triples2.get(m.getE(1));
			if(e1triples==null||e2triples==null) continue;
			Map<Property,Set<Instance>> ep1=getPVMap(e1triples),ep2=getPVMap(e2triples);
			PVSetProcessor pvp=new PVSetProcessor(ep1,ep2,pps);	
			if(pvp.PVPairSetsIsEqual()) {	//�ж�����ֵ���Ƿ�ȼ�
				Set<EPSKey> ves=pvp.getValues();	//ÿ����ͨ�����Ķ�Ӧ�ļ�ֵ
				pps.addPairIntoGraph(ves); //����±�
				newlm.add(index);
			}
		}
		//�������������ifps����
		synchronized(ifpss){
			if(pps.divergence()>divergenceThreshold&&pps.hasSupport(ruleSupport)&&pps.bestInSubSet(ifpss,matchThreshold)) {
				ifpss.add(pps);
			}
		}
		return newlm;
	}
	//�����ϴ�С������˳���ھ�ifps����
	//�����ֱ�Ϊ�ȼ����Զԣ�(����->��������)�����������
	private void MineIFPSDirectly(Set<PPair> pEquivalents,Map<Property,List<Integer>> plist,TransactionTable trans) throws InterruptedException, ExecutionException {
		ifpss=new IFPSSet();	
		Map<PPair,List<Integer>> pplist=new TreeMap<PPair,List<Integer>>();	//���ȼ����Զ����������
		Map<PPairSet,List<Integer>> scur=new TreeMap<PPairSet,List<Integer>>();	//��Ҫ�����ھ�ĵȼ����ԶԼ��ϣ������ȼ����ԶԼ������������
		//���ɴ�СΪ1��PPairSet
		List<Future<Pair<PPairSet,List<Integer>>>> fut=new LinkedList<Future<Pair<PPairSet,List<Integer>>>>();
		ExecutorService es=Executors.newFixedThreadPool(41);
		for(PPair outpp:pEquivalents) {	//�����ȼ����Զ�
			fut.add(es.submit(new Callable<Pair<PPairSet,List<Integer>>>() {
				List<Integer> l1=plist.get(outpp.getP1()),l2=plist.get(outpp.getP2());
				PPair pp=outpp;
				@Override
				public Pair<PPairSet,List<Integer>> call() {
					// TODO Auto-generated method stub
					List<Integer> lm=Operator.biAnd(l1, l2);	//����p1��p2�����񽻼�
					PPairSet newpps=new PPairSet(pp);	//����PPairSet
					//����newpps��CGͼ�������˵��޷�����PPairSetƥ���Match
					List<Integer> newlm=ComputeDivergence(newpps,lm,trans);	
					return new Pair<PPairSet,List<Integer>>(newpps, newlm);
				}
			}));
		}
		es.shutdown();
		for(Future<Pair<PPairSet,List<Integer>>> fut_res:fut){
			Pair<PPairSet,List<Integer>> res=fut_res.get();
			pplist.put(res.first.SmalleastPPair(), res.second);	
			PPairSet pps=res.first;
			//divergence�Ѿ��ﵽcandidates���Ŷȵ���ֵ�ģ�û��Ҫ�����ھ�
			//֧�ֶ��Ѿ�����ruleSupport�ģ�Ҳû��Ҫ�����ھ�
			if(pps.divergence()<matchThreshold&&pps.hasSupport(ruleSupport))
				scur.put(pps, res.second);
		}
		while(scur.size()>0) {
			System.out.println("<MineIFPSDirectly> "+scur.size()+" candidates");
			Map<PPairSet,List<Integer>> snext=new TreeMap<PPairSet,List<Integer>>();	//��һ����Ҫ��һ���ھ�ĵȼ����ԶԼ���
			es=Executors.newFixedThreadPool(41);
			//��һ����չÿ�����ϣ�Ϊÿ���ȼ����ԶԼ��ϼ������һ�������Զ�
			for(PPairSet outpps:scur.keySet()) {
				for(PPair outpp:pEquivalents) {
					if(outpp.compareTo(outpps.SmalleastPPair())>=0) break;	//Ϊ�����ظ���ֻ�����С�����Զ�
					List<Integer> outl1=pplist.get(outpp),outl2=scur.get(outpps);
					es.execute(new Runnable() {
						List<Integer> l1=outl1,l2=outl2;
						PPairSet pps=outpps;
						PPair pp=outpp;
						@Override
						public void run() {
							// TODO Auto-generated method stub
							List<Integer> lm=Operator.biAnd(l1, l2);	//���¼��ϵ��������
							if(lm.size()==0) return;	
							PPairSet newpps=new PPairSet(pps);
							newpps.addPPair(pp);
							//�����¼��ϵ�CGͼ
							List<Integer> newlm=ComputeDivergence(newpps,lm,trans);
							//��������ͬ��
							if(newpps.divergence()<matchThreshold&&newpps.hasSupport(ruleSupport)){
								synchronized(snext){
									snext.put(newpps, newlm);
								}
							}
						}
					});
				}
			}
			es.shutdown();
			while(!es.awaitTermination(5, TimeUnit.SECONDS)){
				System.out.print(ifpss.size()+"...");
			}
			scur=snext;
			System.out.println();
			System.out.println("<MineIFPSDirectly>"+ifpss.size());
		}
	}
	//�ھ�candidates
	private void GetCorrespondences() {
		CorrespondenceSet iniCorrespondence=new CorrespondenceSet();
		System.out.println("<GetCorrespondence>Begin...");
		int i=1;	
		int pre=0;
		boolean tmple=true;	//��ʱ��ǣ�Ϊtrueʱ˵��candidates�㹻��
		for(PPairSet rule:ifpss) {	//����ifps����
			pre=iniCorrespondence.size();	//��¼ƥ��ǰ�Ĵ�С
			System.out.println("rule "+i++);
			Set<Instance> subt1=null,subt2=null;	//�ֱ�ΪԴ0��1�к��иù����������Ե�ʵ�弯��
			for(PPair pp:rule) {
				System.out.println(pp);
				if(subt1==null){
					subt1=new HashSet<Instance>(propertyMap1.get(pp.getP1()).getSubjects());
				}
				else subt1.retainAll(propertyMap1.get(pp.getP1()).getSubjects());
				if(subt2==null){
					subt2=new HashSet<Instance>(propertyMap2.get(pp.getP2()).getSubjects());
				}
				else subt2.retainAll(propertyMap2.get(pp.getP2()).getSubjects());
			}
			System.out.println(rule.getSupport()+"\t"+rule.divergence());
			//����ƥ����
			matcher=new RuleMatcher(rule);	
			System.out.println("<GetCorrespondence>Constructing rulematcher...");
			//����subt1������ʵ��
			for(Instance e:subt1) {
				Map<Property,Set<Instance>> p=getPVMap(triples1.get(e));
				matcher.add(e,p);
			}
			ExecutorService es=Executors.newFixedThreadPool(41);
			for(Instance e2:subt2) {	//����subt2
				es.execute(new Runnable() {
					private Instance e=e2;
					@Override
					public void run() {
							// TODO Auto-generated method stub
						Map<Property,Set<Instance>> p2v=getPVMap(triples2.get(e));
						Set<Instance> e2match=matcher.match2(p2v);	//ƥ��Դ0�е�ʵ��
						//���º�ѡƥ��(e1,e)����iniCorrespondence
						for(Instance e1:e2match) {
							iniCorrespondence.add(e1, e, rule.divergence());
						}
					}
				});
			}
			es.shutdown();
			try {
				while(!es.awaitTermination(5, TimeUnit.SECONDS)) {
					System.out.print(iniCorrespondence.size()+"...");
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}
			matcher=null;
			System.out.println();
			System.out.println("<GetCorrespondence>"+iniCorrespondence.size());
			int delta=iniCorrespondence.size()-pre;	//��ǰ����iniCorrespondence��С������
			if(delta>rule.getSupport()){	//�������ڸù����support˵��δ����
				tmple=false;
			}
		}
		largeEnough|=tmple;	//����largeEnough���
		candidates.update(iniCorrespondence);	//��iniCorrespondence�ϲ���candidates
	}
	public IFPSSet getIFPSRules() {
		return ifpss;
	}
	public MatchSet getMatches() {
		return matches;
	}
	//����Ԫ�鼯������ȡ(����->ֵ����)��������Ԫ�������ͬһ��subject
	private Map<Property,Set<Instance>> getPVMap(TripleSet triples){
		Map<Property,Set<Instance>> ep=new TreeMap<Property,Set<Instance>>();
		for(Triple t:triples) {
			Property p=t.getP();
			if(!ep.containsKey(p)) ep.put(p, new TreeSet<Instance>());
			ep.get(p).add(t.getO());
		}
		return ep;
	}
	public void mine() throws InterruptedException, ExecutionException{
		MatchSet newmatch=null;
		int msize=0;
		int csize=0;
		largeEnough=false;
		do {
			System.out.println("<MatchingRuleMiner>Iteration "+iteration);
			System.out.println("<MatchingRuleMiner>Mining ifps rules...");
//			Set<PPair> pEquivalents=MineIFPSs(seeds,candidates,0.95);
			MineIFPSs();
			System.out.println("<MatchingRuleMiner>Mining correspondences...");
			csize=candidates.size();
			GetCorrespondences();
//			candidates=GetCorrespondencesByEqualPPairs(pEquivalents);
			msize=matches.size();
			//if(iteration>2) largeEnough=true;
			if(largeEnough){
				newmatch=candidates.select(matchThreshold);
				seeds.append(newmatch);			//unnecessary
				matches.append(newmatch);
			}
			System.out.println("<MatchingRuleMiner> "+(matches.size()-msize)+" new matches found in iteration " +iteration);
			iteration++;
			System.out.println("<MatchingRuleMiner> save candidates before iteration "+iteration);
			saveCandidates();	//���浱ǰ��candidates
			System.out.println("<MatchingRuleMiner> save seeds before iteration "+iteration);
			saveSeeds();		//���浱ǰ������
			System.out.println("<MatchingRuleMiner> save matches before iteration "+iteration);
			saveMatches();		//���浱ǰ��ƥ��
		}while(!largeEnough||msize!=matches.size());
		
	}
	private void saveCandidates() {
		// TODO Auto-generated method stub
		File f=new File("./candidates_iteration"+iteration);
		BufferedWriter bw=null;
		try {
			bw=new BufferedWriter(new FileWriter(f));
			for(Correspondence c:candidates) {
				bw.write(c.getMatch().getE(0)+" "+c.getMatch().getE(1)+" "+c.getConf()+"\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if(bw!=null)
					bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void saveSeeds() {
		// TODO Auto-generated method stub
		File f=new File("./seeds_iteration"+iteration);
		BufferedWriter bw=null;
		try {
			bw=new BufferedWriter(new FileWriter(f));
			for(Match m:seeds) {
				bw.write(m.getE(0).getValue()+" "+m.getE(1).getValue()+"\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if(bw!=null)
					bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void saveMatches() {
		// TODO Auto-generated method stub
		File f=new File("./matches_iteration"+iteration);
		BufferedWriter bw=null;
		try {
			bw=new BufferedWriter(new FileWriter(f));
			for(Match m:matches) {
				bw.write(m.getE(0).getValue()+" "+m.getE(1).getValue()+"\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if(bw!=null)
					bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}

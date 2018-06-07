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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
import model.element.Instance;
import model.element.Match;
import model.element.PPair;
import model.element.PVPair;
import model.element.Property;
import model.element.Transaction;
import model.element.Triple;
import util.Operator;
import util.SubSetGenerator;

public class MatchingRuleMiner {
	//private TripleSet triples;
	private MatchSet seeds,matches;
	private IFPSSet ifpss;
	private Map<Instance,TripleSet> triples1,triples2;
	private Map<Instance,TripleSet> objectMap;
	private CorrespondenceSet candidates;
	private Suite suite;
	private int iteration=1;
	private double matchThreshold=0.98;
	private RuleMatcher matcher=null;
	public MatchingRuleMiner(File f1, File f2, File fseed, double threshold, int startIteration) throws InterruptedException, ExecutionException {
		// TODO Auto-generated constructor stub
		matchThreshold=threshold;
		iteration=startIteration;
		ExecutorService es0=Executors.newCachedThreadPool();
		Future<Map<Instance,TripleSet>> fut_t1=es0.submit(new TripleSetReader(f1,0));
		Future<Map<Instance,TripleSet>> fut_t2=es0.submit(new TripleSetReader(f2,1));
		Future<MatchSet> fut_seeds=null;
		if(startIteration==1) {
			fut_seeds=es0.submit(new MatchSetReader(fseed));
			matches=new MatchSet();
			candidates=new CorrespondenceSet();
			es0.shutdown();
		}
		else {
			fut_seeds=es0.submit(new MatchSetReader(new File("./seeds_iteration"+startIteration)));
			Future<MatchSet> fut_matches=es0.submit(new MatchSetReader(new File("./matches_iteration"+startIteration)));
			Future<CorrespondenceSet> fut_cand=es0.submit(new CandidatesReader(new File("./candidates_iteration"+startIteration)));
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
		for(Instance e:triples2.keySet()) {
			for(Triple t:triples2.get(e)) {
				if(!objectMap.containsKey(t.getO())) {
					objectMap.put(t.getO(), new TripleSet());
				}
				objectMap.get(t.getO()).add(t);
			}
		}
	}
	private void MineIFPSs(double threshold) {
		//O(|seeds|*|triples|)
		suite=null;
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
		TransactionTable trans=new TransactionTable();
		MatchSet unionmatches=new MatchSet();
		unionmatches.append(seeds);
		for(Correspondence c:candidates) {
			unionmatches.add(c.getMatch());
		}
//		unionmatches.append(candidates.getMatchSet());
		candidates=null;
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
		unionmatches=null;
		System.out.println("<MineIFPSs>Size of transaction table is "+trans.size());
		System.out.println("<MineIFPSs>Mining association rules...");
		AssociationRuleMiner arm=new AssociationRuleMiner(trans);
		Set<PPair> pEquivalents=arm.MineAssociationRules();
		System.out.println("<MineIFPSs>"+pEquivalents.size()+" pairs in total.");
		System.out.println("<MineIFPSs>Mining suites...");
		suite=MineSuites(pEquivalents,arm.getPlist(),trans);
		System.out.println("<MineIFPSs>"+suite.size()+" suites were found.");
		System.out.println("<MineIFPSs>Selecting ifps rules...");
		trans=null;
		arm=null;
		pEquivalents=null;
		ifpss=new IFPSSet();
		
		ExecutorService es=Executors.newFixedThreadPool(41);
		for(PPairSet pset:suite) {
			es.execute(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(pset.divergence()>threshold&&pset.bestInSubSet(suite)) {
						ifpss.add(pset);
					}
				}
			});
		}
		es.shutdown();
		try {
			while(!es.awaitTermination(5, TimeUnit.SECONDS)) {
				System.out.print(ifpss.size()+"...");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		suite=null;
		System.out.println();
		System.out.println("<MineIFPSs> "+ifpss.size()+" rules were found.");
	}
	private Suite MineSuites(Set<PPair> pEquivalents,Map<Property,List<Integer>> plist,TransactionTable trans){
		Suite suite=new Suite();
		Map<Integer,Set<PPair>> equalpvs=new TreeMap<Integer,Set<PPair>>();
		ExecutorService es=Executors.newFixedThreadPool(41);
		for(PPair pp:pEquivalents) {
			es.execute(new Runnable() {
				private PPair rpp=pp;
				private List<Integer> l=new LinkedList<Integer>();
				@Override
				public void run() {
					// TODO Auto-generated method stub
					List<Integer> lm=Operator.biAnd(plist.get(rpp.getP1()), plist.get(rpp.getP2()));
					for(Integer index:lm) {
						Match m=trans.getMatch(index);
						TripleSet e1triples=triples1.get(m.getE(0));
						TripleSet e2triples=triples2.get(m.getE(1));
						Set<Instance> v1=e1triples.valuesOf(rpp.getP1());
						Set<Instance> v2=e2triples.valuesOf(rpp.getP2());
						if(existEqualObject(v1,v2)) {
							l.add(index);
						}
					}
					synchronized(equalpvs) {
						for(Integer i:l) {
							if(!equalpvs.containsKey(i)) equalpvs.put(i, new TreeSet<PPair>());
							Set<PPair> pps=equalpvs.get(i);
							pps.add(rpp);
						}
					}
				}
			});
		}
		es.shutdown();
		try {
			while(!es.awaitTermination(5, TimeUnit.SECONDS)) {
				System.out.print(".");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println();
		es=Executors.newFixedThreadPool(41);
		for(Integer i:equalpvs.keySet()) {
			es.execute(new SuitesMiner(trans.getMatch(i),suite,equalpvs.get(i)));
		}
		es.shutdown();
		try {
			while(!es.awaitTermination(5, TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println();
		return suite;
	}
	private void GetCorrespondences() {
		candidates=new CorrespondenceSet();
		System.out.println("<GetCorrespondence>Begin...");
		int i=1;	
		for(PPairSet rule:ifpss) {
			System.out.println("rule "+i++);
			for(PPair pp:rule) {
				System.out.println(pp);
			}
			System.out.println(rule.divergence());
			matcher=new RuleMatcher(rule);
			System.out.println("<GetCorrespondence>Constructing rulematcher...");
			for(Instance e:triples1.keySet()) {
				Map<Property,Set<Instance>> p=getPVMap(triples1.get(e));
				if(containAllPPairs(p,rule,0)) {
					matcher.add(e,p);
				}
			}
			ExecutorService es=Executors.newFixedThreadPool(41);
			for(Instance e2:triples2.keySet()) {
				es.execute(new Runnable() {
					private Instance e=e2;
					@Override
					public void run() {
							// TODO Auto-generated method stub
						Map<Property,Set<Instance>> p2v=getPVMap(triples2.get(e));
						if(!containAllPPairs(p2v,rule,1)) return;
						Set<Instance> e2match=matcher.match2(p2v);
						for(Instance e1:e2match) {
							candidates.add(e1, e, rule.divergence());
						}
					}
				});
			}
			es.shutdown();
			try {
				while(!es.awaitTermination(5, TimeUnit.SECONDS)) {
					System.out.print(candidates.size()+"...");
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}
			matcher=null;
			System.out.println();
			System.out.println("<GetCorrespondence>"+candidates.size());
		}
	}
	public IFPSSet getIFPSRules() {
		return ifpss;
	}
	public MatchSet getMatches() {
		return matches;
	}
	private boolean containAllPPairs(Map<Property, Set<Instance>> ep, PPairSet rule,int index) {
		// TODO Auto-generated method stub
		for(PPair pp:rule) {
			if(!(index==0&&ep.containsKey(pp.getP1())||index==1&&ep.containsKey(pp.getP2())))
				return false;
		}
		return true;
	}
	private boolean existEqualObject(Set<Instance> e1vset, Set<Instance> e2vset) {
		// TODO Auto-generated method stub
		for(Instance e1:e1vset) {
			for(Instance e2:e2vset) {
				if(e1.approxEqual(e2))
					return true;
			}
		}
		return false;
	}
	private Map<Property,Set<Instance>> getPVMap(TripleSet triples){
		Map<Property,Set<Instance>> ep=new TreeMap<Property,Set<Instance>>();
		for(Triple t:triples) {
			Property p=t.getP();
			if(!ep.containsKey(p)) ep.put(p, new TreeSet<Instance>());
			ep.get(p).add(t.getO());
		}
		return ep;
	}
	private class SuitesMiner implements Runnable{
		private Match m;
		private Suite suite;
		private Set<PPair> pps;
		SuitesMiner(Match mp,Suite s,Set<PPair> ppsp){
			m=mp;
			suite=s;
			pps=ppsp;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			SubSetGenerator<PPair> g=new SubSetGenerator<PPair>(pps);
			TripleSet e1triples=triples1.get(m.getE(0));
			TripleSet e2triples=triples2.get(m.getE(1));
			if(e1triples==null||e2triples==null) return;
			Map<Property,Set<Instance>> ep1=getPVMap(e1triples),ep2=getPVMap(e2triples);
			while(g.next()) {
				PPairSet pset=new PPairSet(g.get());
				PVSetProcessor pvp=new PVSetProcessor(ep1,ep2,pset);
				if(pvp.PVPairSetsIsEqual()) {
					int hv=pvp.hashValue();
					suite.add(pset,hv);
				}
			}
		}
	}
	public void mine(){
		MatchSet newmatch=new MatchSet();
		int msize=0;
		do {
			System.out.println("<MatchingRuleMiner>Iteration "+iteration);
			System.out.println("<MatchingRuleMiner>Mining ifps rules...");
//			Set<PPair> pEquivalents=MineIFPSs(seeds,candidates,0.95);
			MineIFPSs(0.95);
			System.out.println("<MatchingRuleMiner>Mining correspondences...");
			GetCorrespondences();
//			candidates=GetCorrespondencesByEqualPPairs(pEquivalents);
			msize=matches.size();
			if(iteration>1){
				newmatch=candidates.select(matchThreshold);
				seeds.append(newmatch);			//unnecessary
				matches.append(newmatch);
				System.out.println("<MatchingRuleMiner> "+(matches.size()-msize)+" new matches found in iteration " +iteration);
			}
			iteration++;
			System.out.println("<MatchingRuleMiner> save candidates before iteration "+iteration);
			saveCandidates();
			System.out.println("<MatchingRuleMiner> save seeds before iteration "+iteration);
			saveSeeds();
			System.out.println("<MatchingRuleMiner> save matches before iteration "+iteration);
			saveMatches();
		}while(iteration<=2||msize!=matches.size());
		
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
				bw.write(m.getE(0)+" "+m.getE(1)+"\n");
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
				bw.write(m.getE(0)+" "+m.getE(1)+"\n");
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

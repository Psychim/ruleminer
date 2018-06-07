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
import model.element.Pair;
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
	private double matchThreshold=0.98,
				divergenceThreshold=0.95;
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
	private void MineIFPSs() throws InterruptedException, ExecutionException {
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
		MineIFPSDirectly(pEquivalents,arm.getPlist(),trans);
		System.out.println("<MineIFPSs> "+ifpss.size()+" rules were found.");
	}
	private List<Integer> ComputeDivergence(PPairSet pps,List<Integer> lm,TransactionTable trans){
		List<Integer> newlm=new LinkedList<Integer>();
		for(Integer index:lm) {
			Match m=trans.getMatch(index);
			TripleSet e1triples=triples1.get(m.getE(0));
			TripleSet e2triples=triples2.get(m.getE(1));
			if(e1triples==null||e2triples==null) continue;
			Map<Property,Set<Instance>> ep1=getPVMap(e1triples),ep2=getPVMap(e2triples);
			PVSetProcessor pvp=new PVSetProcessor(ep1,ep2,pps);
			if(pvp.PVPairSetsIsEqual()) {
				int hv=pvp.hashValue();
				pps.addPairIntoGraph(hv);
				newlm.add(index);
			}
		}
		if(pps.divergence()>divergenceThreshold&&pps.bestInSubSet(ifpss)) {
			ifpss.add(pps);
		}
		return newlm;
	}
	private void MineIFPSDirectly(Set<PPair> pEquivalents,Map<Property,List<Integer>> plist,TransactionTable trans) throws InterruptedException, ExecutionException {
		ifpss=new IFPSSet();
		Map<PPair,List<Integer>> pplist=new TreeMap<PPair,List<Integer>>();
		Map<PPairSet,List<Integer>> scur=new TreeMap<PPairSet,List<Integer>>();
		ExecutorService es=Executors.newFixedThreadPool(41);
		for(PPair pp:pEquivalents) {
			List<Integer> lm=Operator.biAnd(plist.get(pp.getP1()), plist.get(pp.getP2()));
			if(lm.size()==0) continue;
			PPairSet pps=new PPairSet(pp);
			List<Integer> newlm=ComputeDivergence(pps,lm,trans);
			pplist.put(pp, newlm);
			scur.put(pps, newlm);
		}
		while(scur.size()>0) {
			List<Future<Pair<PPairSet,List<Integer>>>> fut=new LinkedList<Future<Pair<PPairSet,List<Integer>>>>();
			for(PPairSet outpps:scur.keySet()) {
				for(PPair outpp:pEquivalents) {
					if(outpp.compareTo(outpps.SmalleastPPair())>=0) break;
					List<Integer> outlm=Operator.biAnd(pplist.get(outpp), scur.get(outpps));
					if(outlm.size()==0) continue;
					fut.add(es.submit(new Callable<Pair<PPairSet,List<Integer>>>() {
						List<Integer> lm=outlm;
						PPairSet pps=outpps;
						PPair pp=outpp;
						@Override
						public Pair<PPairSet,List<Integer>> call() {
							// TODO Auto-generated method stub
							PPairSet newpps=new PPairSet(pps);
							newpps.addPPair(pp);
							List<Integer> newlm=ComputeDivergence(newpps,lm,trans);
							return new Pair<PPairSet,List<Integer>>(newpps, newlm);
						}
					}));
				}
			}
			es.shutdown();
			scur=new TreeMap<PPairSet,List<Integer>>();
			for(Future<Pair<PPairSet,List<Integer>>> fut_res:fut) {
				Pair<PPairSet,List<Integer>> res=fut_res.get();
				if(res.second.size()==0) continue;
				scur.put(res.first, res.second);
			}
			System.out.println("<MineIFPSDirectly>"+ifpss.size());
		}
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
		MatchSet newmatch=new MatchSet();
		int msize=0;
		do {
			System.out.println("<MatchingRuleMiner>Iteration "+iteration);
			System.out.println("<MatchingRuleMiner>Mining ifps rules...");
//			Set<PPair> pEquivalents=MineIFPSs(seeds,candidates,0.95);
			MineIFPSs();
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

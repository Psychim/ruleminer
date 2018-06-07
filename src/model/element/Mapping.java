package model.element;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Mapping {
	private static Map<Instance,Integer> imp=new TreeMap<Instance,Integer>();
	private static Map<Property,Integer> pmp=new TreeMap<Property,Integer>();
//	private static Map<Match,Integer> mmp=new TreeMap<Match,Integer>();
	private static ArrayList<Instance> iimp=new ArrayList<Instance>();
	private static ArrayList<Property> pimp=new ArrayList<Property>();	
//	private static ArrayList<Match> mimp=new ArrayList<Match>();
	public synchronized static void mappingInstance(Match m) {
		Integer i1=getIndex(m.getE(1));
		iimp.set(i1, m.getE(0));
	}
	public synchronized static Integer getIndex(Instance ins) {
		if(imp.containsKey(ins))
			return imp.get(ins);
		else {
			iimp.add(ins);
			imp.put(ins, iimp.size()-1);
			return iimp.size()-1;
		}
	}
	public synchronized static Integer getIndex(Property p) {
		if(pmp.containsKey(p))
			return pmp.get(p);
		else {
			pimp.add(p);
			pmp.put(p, pimp.size()-1);
			return pimp.size()-1;
		}
	}
//	public synchronized static Integer getIndex(Match m) {
//		if(mmp.containsKey(m))
//			return mmp.get(m);
//		else {
//			mimp.add(m);
//			mmp.put(m, mimp.size()-1);
//			return mimp.size()-1;
//		}
//	}
	public static Instance getInstance(int i) {
		return iimp.get(i);
	}
	public static Property getProperty(int i) {
		return pimp.get(i);
	}
//	public static Match getMatch(int i) {
//		return mimp.get(i);
//	}
}

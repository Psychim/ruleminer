package model.collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import model.element.Match;
//∆•≈‰ºØ∫œ
public class MatchSet implements Iterable<Match>{
	private Set<Match> matches;
	public MatchSet() {
		matches=new TreeSet<Match>();
	}
	public MatchSet(MatchSet ms) {
		this();
		append(ms);
	}
	public boolean isEmpty() {
		return matches.isEmpty();
	}
	public synchronized void append(MatchSet newmatches) {
		// TODO Auto-generated method stub
		matches.addAll(newmatches.matches);
	}
	public synchronized void add(Match c) {
		matches.add(c);
	}
	@Override
	public Iterator<Match> iterator() {
		return matches.iterator();
	}
	public int size() {
		// TODO Auto-generated method stub
		return matches.size();
	}
}

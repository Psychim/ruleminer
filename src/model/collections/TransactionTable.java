package model.collections;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import model.element.Match;
import model.element.Transaction;
//事务表
public class TransactionTable implements Iterable<Transaction>{
	//index相同的事务与Match一一对应
	ArrayList<Transaction> transes;
	ArrayList<Match> matches;
	public TransactionTable() {
		transes=new ArrayList<Transaction>();
		matches=new ArrayList<Match>();
	}
	public synchronized void add(Transaction tran,Match c) {
		transes.add(tran);
		matches.add(c);
	}
	public Collection<Match> getMatches() {
		return matches;
	}
	@Override
	public Iterator<Transaction> iterator() {
		// TODO Auto-generated method stub
		return transes.iterator();
	}
	public int size() {
		// TODO Auto-generated method stub
		return transes.size();
	}
	public Match getMatch(int index) {
		// TODO Auto-generated method stub
		return matches.get(index);
	}
}

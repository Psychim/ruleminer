package test;

import model.collections.TripleSet;
import model.element.Instance;
import model.element.Property;
import model.element.Triple;

public class Test {
	public static void main(String[] args) {
		TripleSet t1=new TripleSet(),t2=new TripleSet();
//		Triple t=new Triple(new Instance("1"),new Property("2"),new Instance("3"));
//		Triple tp=new Triple(new Instance("1"),new Property("2"),new Instance("5"));
//		t1.add(t);
//		t1.add(tp);
//		t2.add(t);
//		t2.add(tp);
		for(Triple tr:t1) {
			System.out.println(tr.getO());
		}
		for(Triple tr:t2) {
//			tr.setO(new Instance("6"));
			break;
		}
		for(Triple tr:t1) {
			System.out.println(tr.getO());
		}
	}
}

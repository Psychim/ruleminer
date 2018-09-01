package model.element;

import java.util.Arrays;
//∆•≈‰
public class Match implements Comparable<Match>{
	private Instance[] e=new Instance[2];		//each from different data source
	public Match(Instance a,Instance b) {
		e[a.getSource()]=a;
		e[b.getSource()]=b;
	}
	public Instance getE(int source) {
		return e[source];
	}

	public synchronized void setE(int source,Instance o) {
		this.e[source] = o;
	}
	public boolean has(Instance o) {
		return e[0].equals(o)||e[1].equals(o);
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Match o=(Match)obj;
		return this.compareTo(o)==0;
	}
	@Override
	public int compareTo(Match o) {
		// TODO Auto-generated method stub
		int e1flag=e[0].compareTo(o.e[0]);
		if(e1flag!=0)return e1flag;
		return e[1].compareTo(o.e[1]);
	}
	@Override
	public String toString() {
		return "Match [e=" + Arrays.toString(e) + "]";
	}
}

package model.element;

public class Correspondence implements Comparable<Correspondence>{
	private Match m;
	private double conf;
	public Correspondence(Instance e1, Instance e2, double conf2) {
		// TODO Auto-generated constructor stub
		m=new Match(e1,e2);
		conf=conf2;
	}
	public Correspondence() {
		// TODO Auto-generated constructor stub
		m=null;
		conf=0;
	}
	public Match getMatch() {
		return m;
	}
	public double getConf() {
		return conf;
	}
	public synchronized void setConf(double conf) {
		this.conf = conf;
	}
	@Override
	public int compareTo(Correspondence o) {
		// TODO Auto-generated method stub
		return m.compareTo(o.m);
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(!(obj instanceof Correspondence)) return false;
		if(obj==this) return true;
		Correspondence o=(Correspondence)obj;
		return this.m.equals(o.m);
	}
}

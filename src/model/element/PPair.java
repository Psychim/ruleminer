package model.element;

public class PPair implements Comparable<PPair>{
	private Integer p1,p2;
	private int cnt;
	public PPair(Property property, Property property2) {
		// TODO Auto-generated constructor stub
		p1=Mapping.getIndex(property);
		p2=Mapping.getIndex(property2);
		cnt=0;
	}
	public synchronized void addCount() {
		cnt++;
	}
	public int getCount() {
		return cnt;
	}
	public boolean has(Property p) {
		// TODO Auto-generated method stub
		int pi=Mapping.getIndex(p);
		return p1.equals(pi)||p2.equals(pi);
	}	
	@Override
	public String toString() {
		return "PPair [p1=" + Mapping.getProperty(p1) + ", p2=" + Mapping.getProperty(p2) + "]";
	}
	@Override
	public int compareTo(PPair o) {
		// TODO Auto-generated method stub
		int p1flag=p1.compareTo(o.p1);
		if(p1flag!=0)return p1flag;
		return p2.compareTo(o.p2);
	}

	public Property getP1() {
		// TODO Auto-generated method stub
		return Mapping.getProperty(p1);
	}
	public Property getP2() {
		return Mapping.getProperty(p2);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((p1 == null) ? 0 : p1.hashCode());
		result = prime * result + ((p2 == null) ? 0 : p2.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PPair other = (PPair) obj;
		if (p1 == null) {
			if (other.p1 != null)
				return false;
		} else if (!p1.equals(other.p1))
			return false;
		if (p2 == null) {
			if (other.p2 != null)
				return false;
		} else if (!p2.equals(other.p2))
			return false;
		return true;
	}
}

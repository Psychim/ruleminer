package model.element;

public class PVPair implements Comparable<PVPair>{
	private Integer p;
	private Integer v;
	public PVPair(Property p2, Instance v2) {
		p=Mapping.getIndex(p2);
		v=Mapping.getIndex(v2);
	}
	@Override
	public int compareTo(PVPair o) {
		// TODO Auto-generated method stub
		int pflag=p.compareTo(o.p);
		if(pflag!=0)return pflag;
		return v.compareTo(o.v);
	}
	public Property getP() {
		return Mapping.getProperty(p);
	}
	public synchronized void setP(Property p) {
		this.p = Mapping.getIndex(p);
	}
	public Instance getV() {
		return Mapping.getInstance(v);
	}
	public synchronized void setV(Instance v) {
		this.v = Mapping.getIndex(v);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((p == null) ? 0 : p.hashCode());
		result = prime * result + ((v == null) ? 0 : v.hashCode());
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
		PVPair other = (PVPair) obj;
		if (p == null) {
			if (other.p != null)
				return false;
		} else if (!p.equals(other.p))
			return false;
		if (v == null) {
			if (other.v != null)
				return false;
		} else if (!v.equals(other.v))
			return false;
		return true;
	}	
}

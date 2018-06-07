package model.element;

public class PVPair2 implements Comparable<PVPair2>{
	public final Integer p;
	public final String v;
	public PVPair2(Property p2, Instance v2) {
		p=Mapping.getIndex(p2);
		v=v2.getValue();
	}
	@Override
	public int compareTo(PVPair2 o) {
		// TODO Auto-generated method stub
		int pflag=p.compareTo(o.p);
		if(pflag!=0)return pflag;
		return v.compareTo(o.v);
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
		PVPair2 other = (PVPair2) obj;
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

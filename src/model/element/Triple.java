package model.element;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Triple implements Comparable<Triple>{
	private Integer s,p,o;
	@Override
	public String toString() {
		return "Triple [s=" + s + ", o=" + o + ", p=" + p + "]";
	}
	public Triple(Instance e1, Property p2, Instance v) {
		// TODO Auto-generated constructor stub
		s=Mapping.getIndex(e1);
		p=Mapping.getIndex(p2);
		o=Mapping.getIndex(v);
	}
	
	public Instance getS() {
		return Mapping.getInstance(s);
	}
	public synchronized void setS(Instance s) {
		this.s = Mapping.getIndex(s);
	}
	public Instance getO() {
		return Mapping.getInstance(o);
	}
	public synchronized void setO(Instance o) {
		this.o = Mapping.getIndex(o);
	}
	public Property getP() {
		return Mapping.getProperty(p);
	}
	public synchronized void setP(Property p) {
		this.p = Mapping.getIndex(p);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((o == null) ? 0 : o.hashCode());
		result = prime * result + ((p == null) ? 0 : p.hashCode());
		result = prime * result + ((s == null) ? 0 : s.hashCode());
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
		Triple other = (Triple) obj;
		if (o == null) {
			if (other.o != null)
				return false;
		} else if (!o.equals(other.o))
			return false;
		if (p == null) {
			if (other.p != null)
				return false;
		} else if (!p.equals(other.p))
			return false;
		if (s == null) {
			if (other.s != null)
				return false;
		} else if (!s.equals(other.s))
			return false;
		return true;
	}
	@Override
	public int compareTo(Triple o) {
		// TODO Auto-generated method stub
		int flag=this.s.compareTo(o.s);
		if(flag!=0)return flag;
		flag=this.p.compareTo(o.p);
		if(flag!=0)return flag;
		flag=this.o.compareTo(o.o);
		return flag;
	}
	
}

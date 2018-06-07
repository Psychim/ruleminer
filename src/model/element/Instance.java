package model.element;

public class Instance implements Comparable<Instance>{
	String value;
	int source;
	public Instance(String string,int sc) {
		// TODO Auto-generated constructor stub
		value=string;
		source=sc;
	}
	public String getValue() {
		return value;
	}
	public int getSource() {
		return source;
	}
	@Override
	public String toString() {
		return value;
	}
	@Override
	public int compareTo(Instance o) {
		// TODO Auto-generated method stub
		if(source!=o.source)return source-o.source;
		return value.compareTo(o.value);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + source;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		Instance other = (Instance) obj;
		if (source != other.source)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	public int distanceWith(Instance v2) {
		// TODO Auto-generated method stub
		if(this.equals(v2))return 0;
		int[][] d=new int[value.length()+1][v2.value.length()+1];
		for(int i=0;i<value.length()+1;i++) {
			d[i][0]=i;
		}
		for(int i=0;i<v2.value.length()+1;i++) {
			d[0][i]=i;
		}
		for(int i=1;i<value.length()+1;i++) {
			for(int j=1;j<v2.value.length()+1;j++) {
				d[i][j]=d[i-1][j-1]+value.charAt(i-1)==v2.value.charAt(j-1)?0:1;
				d[i][j]=Math.min(d[i][j], d[i-1][j]+1);
				d[i][j]=Math.min(d[i][j], d[i][j-1]+1);
			}
		}
		return d[value.length()][v2.value.length()];
	}
	public boolean approxEqual(Instance e2) {
		// TODO Auto-generated method stub
//		return this.distanceWith(e2)<=0.6*(this.value.length()+e2.value.length());
		return this.value.equals(e2.value);
	}

	
}

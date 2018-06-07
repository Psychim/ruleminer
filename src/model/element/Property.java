package model.element;

public class Property implements Comparable<Property>{
	private String value;
	int source;
	public Property(String string,int sc) {
		// TODO Auto-generated constructor stub
		value=string;
		source=sc;
	}
	public int getSource() {
		return source;
	}
	public String getValue() {
		return value;
	}
	public int compareTo(Property p) {
		// TODO Auto-generated method stub
		if(source!=p.source) return source-p.source;
		return value.compareTo(p.value);
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
		Property other = (Property) obj;
		if (source != other.source)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return value;
	}
}

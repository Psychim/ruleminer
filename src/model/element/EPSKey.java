package model.element;

import java.util.LinkedList;
//用于替代CG图哈希值的类
public class EPSKey {
	private LinkedList<Integer> values=null;
	public EPSKey(){
		values=new LinkedList<Integer>();
	}
	public EPSKey(EPSKey value) {
		// TODO Auto-generated constructor stub
		values=new LinkedList<Integer>(value.values);
	}
	public void add(Integer i){
		values.add(i);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((values == null) ? 0 : values.hashCode());
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
		EPSKey other = (EPSKey) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}
}

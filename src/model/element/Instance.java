package model.element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.Util;
//实体，包括resource和字面值
public class Instance implements Comparable<Instance>{
	private int value;	//字符串的索引
	private int source;	//来自的源
	public Instance(String string,int sc) {
		// TODO Auto-generated constructor stub
		value=Mapping.getIndex(string);
		source=sc;
	}
	public String getValue() {
		return Mapping.getString(value);
	}
	public int getSource() {
		return source;
	}
	@Override
	public String toString() {
		return Mapping.getString(value);
	}
	@Override
	public int compareTo(Instance o) {
		// TODO Auto-generated method stub
		if(source!=o.source)return source-o.source;
		return value-o.value;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + source;
		result = prime * result + Mapping.getString(value).hashCode();
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
		if(value!=other.value)
			return false;
		return true;
	}
	//判断两instance是否等价
	//目前标准是属于同一个同义集合
	//但目前只有resource具有同义词，字面值没有
	public boolean approxEqual(Instance e2) {
		double t=0.2;
		// TODO Auto-generated method stub
//		return this.distanceWith(e2)<=0.6*(this.value.length()+e2.value.length());
//		String s1=Mapping.getString(value),s2=Mapping.getString(e2.value);
		if(Mapping.findSet(value)==Mapping.findSet(e2.value))
			return true;
		String s1=Mapping.getString(value),s2=Mapping.getString(e2.value);
		if(s1.length()==0||s2.length()==0) return false;
		if(s1.charAt(0)=='<') return false;
		if(s2.charAt(0)=='<') return false;
		Pattern p=Pattern.compile("^\\d+.*");
		Matcher m=p.matcher(s1);
		if(m.find()) return false;
		m=p.matcher(s2);
		if(m.find()) return false;
		if(Util.distance(s1, s2)*1./Math.max(s1.length(), s2.length())<t){
			Mapping.unionSet(value, e2.value);
			System.out.println(s1+"\t"+s2);
			return true;
		}
		return false;
//		Pattern r=Pattern.compile("\\d");
//		Matcher m=r.matcher(s1);
//		if(m.find()) return false;
//		if(s1.contains(s2)||s2.contains(s1)){
//			Mapping.unionSet(value, e2.value);
//			return true;
//		}
	}
	public Integer getValueIndex() {
		// TODO Auto-generated method stub
		return value;
	}

	
}

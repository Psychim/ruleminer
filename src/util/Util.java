package util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import model.element.Property;

public class Util{
	public static void swap(Object o1,Object o2) {
		Object tmp=o1;
		o1=o2;
		o2=tmp;
	}
	public static <T extends Comparable<T>> int compareSet(Set<T> s1,Set<T> s2){
		Iterator<T> ite1=s1.iterator();
		Iterator<T> ite2=s2.iterator();
		while(ite1.hasNext()&&ite2.hasNext()) {
			int flag=ite1.next().compareTo(ite2.next());
			if(flag!=0) return flag;
		}
		return s1.size()-s2.size();
	}
	public static String LongestCommonPrefix(String a,String b) {
		String res=new String();
		for(int i=0;i<a.length()&&i<b.length();i++) {
			if(a.charAt(i)==b.charAt(i))
				res+=a.charAt(i);
			else return res;
		}
		return res;
	}
	
}

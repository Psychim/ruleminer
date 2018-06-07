package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SubSetGenerator<T> {
	List<T> list;
	int[] mark;
	public SubSetGenerator(Set<T> set) {
		list=new ArrayList<T>();
		mark=new int[set.size()];
		for(T t:set) {
			list.add(t);
		}
		for(int i=0;i<list.size();i++)
			mark[i]=0;
	}
	public boolean next() {
		int c=1;
		for(int i=0;i<list.size();i++) {
			mark[i]+=c;
			c=mark[i]/2;
			mark[i]%=2;
		}
		return c==0;
	}
	public List<T> get(){
		List<T> res=new ArrayList<T>();
		for(int i=0;i<list.size();i++) {
			if(mark[i]==1)
				res.add(list.get(i));
		}
		return res;
	}
}

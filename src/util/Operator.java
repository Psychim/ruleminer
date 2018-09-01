package util;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Operator {
	//求升序列表l1和l2的交集
	public static <T extends Comparable<T>> List<T> biAnd(List<T> l1,List<T> l2){
		List<T> res=new LinkedList<T>();
		if(l1.size()==0||l2.size()==0) return res;
		Iterator<T> i1=l1.iterator(),i2=l2.iterator();
		T d1=i1.next(),d2=i2.next();
		while(d1!=null&&d2!=null) {
			int flag=d1.compareTo(d2);
			if(flag<0){
				d1=i1.hasNext()?i1.next():null;
			}
			else if(flag==0) {
				res.add(d1);
				d1=i1.hasNext()?i1.next():null;
				d2=i2.hasNext()?i2.next():null;
			}
			else d2=i2.hasNext()?i2.next():null;
		}
		return res;
	}
}

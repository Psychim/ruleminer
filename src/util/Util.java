package util;

import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util{
	//交换
	public static void swap(Object o1,Object o2) {
		Object tmp=o1;
		o1=o2;
		o2=tmp;
	}
	public static int distance(String s1,String s2){
		int[][] m=new int[s1.length()+1][s2.length()+1];
		for(int i=0;i<=s1.length();i++) m[i][0]=i;
		for(int i=0;i<=s2.length();i++) m[0][i]=i;
		for(int i=1;i<=s1.length();i++){
			for(int j=1;j<=s2.length();j++){
				int c=(s1.charAt(i-1)==s2.charAt(j-1))?0:1;
				m[i][j]=m[i-1][j]+1;
				m[i][j]=Math.min(m[i][j], m[i][j-1]+1);
				m[i][j]=Math.min(m[i][j], m[i-1][j-1]+c);
			}
		}
		return m[s1.length()][s2.length()];
	}
	//按字典序比较集合大小关系
	public static <T extends Comparable<T>> int compareSet(Set<T> s1,Set<T> s2){
		Iterator<T> ite1=s1.iterator();
		Iterator<T> ite2=s2.iterator();
		while(ite1.hasNext()&&ite2.hasNext()) {
			int flag=ite1.next().compareTo(ite2.next());
			if(flag!=0) return flag;
		}
		return s1.size()-s2.size();
	}
	//a和b的最长公共前缀
	public static String LongestCommonPrefix(String a,String b) {
		String res=new String();
		for(int i=0;i<a.length()&&i<b.length();i++) {
			if(a.charAt(i)==b.charAt(i))
				res+=a.charAt(i);
			else return res;
		}
		return res;
	}
	//标签名
	public static String getLabelname(String s){
		Pattern r=Pattern.compile(".*/resource/(.+)>$");
		Matcher m=r.matcher(s);
		if(!m.find()) return "";
		String val=m.group(1);
		return val;
	}
}

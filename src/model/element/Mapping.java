package model.element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import util.Util;
//用于索引字符串
public class Mapping {
	private static Map<String,Integer> smp=new TreeMap<String,Integer>();	//(字符串->id)
	private static ArrayList<String> simp=new ArrayList<String>();	//(id->字符串)
	private static ArrayList<Integer> father=new ArrayList<Integer>();	//并查集父结点，属于同一个集合的字符串同义
	static {
		simp.add("");
		smp.put("", 0);
		father.add(0);
	}
	//字符串s的索引
	public synchronized static Integer getIndex(String s) {
		if(smp.containsKey(s))
			return smp.get(s);
		else {
			simp.add(s);
			smp.put(s, simp.size()-1);
			father.add(simp.size()-1);
			return simp.size()-1;
		}
	}
	//索引i的字符串
	public static String getString(int i) {
		return simp.get(i);
	}
	//并查集-求u所属集合
	public static synchronized int findSet(int u){
		if(father.get(u)==u) return u;
		father.set(u, findSet(father.get(u)));
		return father.get(u);
	}
	//并查集-合并u，v集合
	public static synchronized void unionSet(int u,int v){
		u=findSet(u);
		v=findSet(v);
		father.set(u, v);
	}
	//读取同义词表
	public static void readSynonyms(File f) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br=new BufferedReader(new FileReader(f));
		while(true){
			String s=br.readLine();
			if(s==null) break;
			String[]e=s.split(" ");
			//System.out.println(e[0]);
			//抽取标签名
			String v1=Util.getLabelname(e[0]),v2=Util.getLabelname(e[1]);
			if(v1==null||v1.equals(""))continue;
			if(v2==null||v2.equals(""))continue;
			//v1、v2同义，合并三个源中v1、v2的集合
			unionSet(getIndex("<http://zhishi.me/zhwiki/resource/"+v1+">"),getIndex("<http://zhishi.me/zhwiki/resource/"+v2+">"));
			unionSet(getIndex("<http://zhishi.me/hudongbaike/resource/"+v1+">"),getIndex("<http://zhishi.me/hudongbaike/resource/"+v2+">"));
			unionSet(getIndex("<http://zhishi.me/baidubaike/resource/"+v1+">"),getIndex("<http://zhishi.me/baidubaike/resource/"+v2+">"));
		}
		br.close();
	}
}

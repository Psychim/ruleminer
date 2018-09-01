package model.element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import util.Util;
//���������ַ���
public class Mapping {
	private static Map<String,Integer> smp=new TreeMap<String,Integer>();	//(�ַ���->id)
	private static ArrayList<String> simp=new ArrayList<String>();	//(id->�ַ���)
	private static ArrayList<Integer> father=new ArrayList<Integer>();	//���鼯����㣬����ͬһ�����ϵ��ַ���ͬ��
	static {
		simp.add("");
		smp.put("", 0);
		father.add(0);
	}
	//�ַ���s������
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
	//����i���ַ���
	public static String getString(int i) {
		return simp.get(i);
	}
	//���鼯-��u��������
	public static synchronized int findSet(int u){
		if(father.get(u)==u) return u;
		father.set(u, findSet(father.get(u)));
		return father.get(u);
	}
	//���鼯-�ϲ�u��v����
	public static synchronized void unionSet(int u,int v){
		u=findSet(u);
		v=findSet(v);
		father.set(u, v);
	}
	//��ȡͬ��ʱ�
	public static void readSynonyms(File f) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br=new BufferedReader(new FileReader(f));
		while(true){
			String s=br.readLine();
			if(s==null) break;
			String[]e=s.split(" ");
			//System.out.println(e[0]);
			//��ȡ��ǩ��
			String v1=Util.getLabelname(e[0]),v2=Util.getLabelname(e[1]);
			if(v1==null||v1.equals(""))continue;
			if(v2==null||v2.equals(""))continue;
			//v1��v2ͬ�壬�ϲ�����Դ��v1��v2�ļ���
			unionSet(getIndex("<http://zhishi.me/zhwiki/resource/"+v1+">"),getIndex("<http://zhishi.me/zhwiki/resource/"+v2+">"));
			unionSet(getIndex("<http://zhishi.me/hudongbaike/resource/"+v1+">"),getIndex("<http://zhishi.me/hudongbaike/resource/"+v2+">"));
			unionSet(getIndex("<http://zhishi.me/baidubaike/resource/"+v1+">"),getIndex("<http://zhishi.me/baidubaike/resource/"+v2+">"));
		}
		br.close();
	}
}

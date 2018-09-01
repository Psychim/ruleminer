package algorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import concurrent.MatchSetReader;
import concurrent.TripleSetReader;
import model.collections.MatchSet;
import model.collections.TripleSet;
import model.element.Instance;
import model.element.Mapping;
import model.element.Match;
import model.element.Property;
import model.element.Triple;

public class Main {
	public static File f1,f2,fmatch;
	public static File[] rdFiles;
	public static double threshold;
	public static int startIteration=1;
	public static int minSupport=100;
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		initParameters(args[0]);		//��ȡ�����ļ�
		//��ȡͬ��ʲ���ͬ�����ϲ���ͬһ����
		for(File f:rdFiles){
			Mapping.readSynonyms(f);
		}
		//��ʼ�ھ�
		System.out.println("<main>Start mining...");
		MatchingRuleMiner arm=new MatchingRuleMiner(f1,f2,fmatch,threshold,minSupport,startIteration);
		arm.mine();
		System.out.println("<main>Done.");
		//����ƥ��
		MatchSet ms=arm.getMatches();
		File fout=new File("./out.txt");
		BufferedWriter bw=new BufferedWriter(new FileWriter(fout));
		for(Match m:ms) {
			//System.out.println(m.getE(0)+"\t"+m.getE(1));
			bw.write(m.getE(0).getValue()+" "+m.getE(1).getValue()+"\n");
		}
		bw.close();
	}
	private static void initParameters(String arg) throws IOException {
		// TODO Auto-generated method stub
		File fp=new File(arg);
		BufferedReader br=new BufferedReader(new FileReader(fp));
		//��һ�У���Ԫ��Դ1�ļ�
		String s=br.readLine();
		System.out.println("<init>Source 1: "+s);
		f1=new File(s);
		//�ڶ��У���Ԫ��Դ2�ļ�
		s=br.readLine();
		System.out.println("<init>Source 2: "+s);
		f2=new File(s);
		//�����У������ļ�
		s=br.readLine();
		System.out.println("<init>Seeds: "+s);
		fmatch=new File(s);
		//�����У�redirects�ļ����ÿո�ָ���ͬ�ļ�
		s=br.readLine();
		String[]names=s.split(" ");
		rdFiles=new File[names.length];
		for(int i=0;i<names.length;i++){
			rdFiles[i]=new File(names[i]);
			System.out.println("<init>redirects: "+names[i]);
		}
		//�����У�candidates��ֵ
		s=br.readLine();
		threshold=Double.parseDouble(s);
		System.out.println("<init>threshold: "+threshold);
		//�����У��ȼ�������С֧�ֶ�
		s=br.readLine();
		AssociationRuleMiner.MINSUP=Integer.parseInt(s);
		System.out.println("<init>AssociationRuleMiner MINSUP: "+AssociationRuleMiner.MINSUP);
		//�����У��ȼ�������С���Ŷ�
		s=br.readLine();
		AssociationRuleMiner.MINCONF=Double.parseDouble(s);
		System.out.println("<init>AssociationRuleMiner MINCONF: "+AssociationRuleMiner.MINCONF);
		//�ڰ��У�IFPS������С֧�ֶ�
		s=br.readLine();
		minSupport=Integer.parseInt(s);
		System.out.println("<init>min rule support: "+minSupport);
		if(minSupport>AssociationRuleMiner.MINSUP){
			AssociationRuleMiner.MINSUP=minSupport;
			System.out.println("<init>reset AssociationRuleMiner MINSUP: "+AssociationRuleMiner.MINSUP);
		}
		//�ھ��У��ӵڼ��ֵ�����ʼ����
		s=br.readLine();
		startIteration=Integer.parseInt(s);
		System.out.println("<init>Iteration start from "+startIteration);
		br.close();
	}
}

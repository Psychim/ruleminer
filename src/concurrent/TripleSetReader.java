package concurrent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import model.collections.TripleSet;
import model.element.Instance;
import model.element.Property;
import model.element.Triple;
//���ڶ�ȡ��Ԫ��
public class TripleSetReader implements Callable<Map<Instance,TripleSet>>{
	private int source;	//��Ԫ���ļ�������Դ
	private File f=null;
	private Map<Instance,TripleSet> triples;
	public TripleSetReader(File f,int s){
		this.f=f;
		source=s;
		triples=new HashMap<Instance,TripleSet>();
	}
	@Override
	public Map<Instance,TripleSet> call() {
		// TODO Auto-generated method stub
		BufferedReader br=null;
		ExecutorService es=Executors.newFixedThreadPool(41);
		try {
			System.out.println("<TripleSetReader "+source+">Reading file "+f.getName()+"...");
			br = new BufferedReader(new FileReader(f));
			while(true){
				String s0=br.readLine();
				if(s0==null) break;
				es.execute(new Runnable() {
					private String s=s0;
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String[] elements=s.split("\\s");
						Instance subject=new Instance(elements[0],source);
						Property property=new Property(elements[1],source);
						//����ֵ�п����пհ׷��������Ҫ��ʣ�µ����ַ���ȫ���ϲ���Ϊobject����ȥ����.��
						String objectStr="";
						for(int j=2;j<elements.length&&!elements[j].equals(".");j++)
							objectStr+=(j==2?"":" ")+elements[j];
						//��������ֵֻ�������ֺ���ĸ��ȥ������'zh'
						//���ﲻ��utf-8����
						if(objectStr.indexOf("http://zhishi.me")==-1) {
							objectStr=objectStr.replaceAll("[^0-9a-zA-Z]", "");
							objectStr=objectStr.replaceAll("zh$", "");
							//System.out.println(objectStr);
						}
						Instance object=new Instance(objectStr,source);
						synchronized(triples) {
							if(!triples.containsKey(subject)) {
								triples.put(subject, new TripleSet());
							}
							triples.get(subject).add(new Triple(subject,property,object));
						}
					}
				});
			}
			es.shutdown();
			while(!es.awaitTermination(5, TimeUnit.SECONDS));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(br!=null)
					br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return triples;
	}
}
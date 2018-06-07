package concurrent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Callable;

import model.collections.MatchSet;
import model.element.Instance;
import model.element.Match;

public class MatchSetReader implements Callable<MatchSet> {
	private MatchSet matches;
	private File fmatch;
	public MatchSetReader(File f){
		matches=new MatchSet();
		fmatch=f;
	}
	@Override
	public MatchSet call() {
		// TODO Auto-generated method stub
		System.out.println("<MatchSetReader>Reading "+fmatch.getName()+"...");
		BufferedReader br=null;
		try {
			br = new BufferedReader(new FileReader(fmatch));
			while(true) {
				String s=br.readLine();
				if(s==null) break;
				String[] e=s.split(" ");
				Instance e1=new Instance(e[1].substring(7,e[1].length()-1),0),e2=new Instance(e[3].substring(7,e[3].length()-1),1);
				matches.add(new Match(e1,e2));
			}
		} catch (FileNotFoundException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}finally{
			try {
				if(br!=null)
					br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return matches;
	}
}

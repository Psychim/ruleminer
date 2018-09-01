package concurrent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Callable;

import model.collections.CorrespondenceSet;
import model.element.Instance;
//用于读取保存下的candidates
public class CandidatesReader implements Callable<CorrespondenceSet> {
	private CorrespondenceSet candidates=new CorrespondenceSet();
	private File fcand;
	public CandidatesReader(File f) {
		fcand=f;
	}
	@Override
	public CorrespondenceSet call() {
		// TODO Auto-generated method stub
		BufferedReader br=null;
		try {
			br=new BufferedReader(new FileReader(fcand));
			while(true) {
				String s=br.readLine();
				if(s==null) break;
				String[] e=s.split(" ");
				candidates.add(new Instance(e[1].substring(7,e[1].length()-1),0), new Instance(e[3].substring(7,e[3].length()-1),1), Double.parseDouble(e[4]));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally {
			try {
				if(br!=null)
					br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return candidates;
	}

}

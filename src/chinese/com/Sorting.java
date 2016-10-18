package chinese.com;
import chinese.com.wordFreq;

import java.util.Comparator;


public class Sorting implements Comparator<chinese.com.wordFreq> {
	public int compare(chinese.com.wordFreq o1, chinese.com.wordFreq o2) {
		return Long.valueOf(o2.getNo()).compareTo(Long.valueOf((o1.getNo())));      
	}  
}

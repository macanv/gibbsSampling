package chinese.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chinese.com.FileUtil;
import chinese.com.Stopwords;

/**Class for corpus which consists of M documents
 * @author yangliu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yangliuyx@gmail.com
 */

public class Documents {
	
	ArrayList<Document> docs;  //文档array
	Map<String, Integer> termToIndexMap;//word-index map
	ArrayList<String> indexToTermMap;  //word-index array
	Map<String,Integer> termCountMap; //词频统计 map

	public ArrayList<Document> getDocs() {
		return docs;
	}

	public void setDocs(ArrayList<Document> docs) {
		this.docs = docs;
	}

	public Map<String, Integer> getTermToIndexMap() {
		return termToIndexMap;
	}

	public void setTermToIndexMap(Map<String, Integer> termToIndexMap) {
		this.termToIndexMap = termToIndexMap;
	}

	public ArrayList<String> getIndexToTermMap() {
		return indexToTermMap;
	}

	public void setIndexToTermMap(ArrayList<String> indexToTermMap) {
		this.indexToTermMap = indexToTermMap;
	}

	public Map<String, Integer> getTermCountMap() {
		return termCountMap;
	}

	public void setTermCountMap(Map<String, Integer> termCountMap) {
		this.termCountMap = termCountMap;
	}

	public Documents(){
		docs = new ArrayList<Document>();
		termToIndexMap = new HashMap<String, Integer>();
		indexToTermMap = new ArrayList<String>();
		termCountMap = new HashMap<String, Integer>();
	}
	
	public void readDocs(String docsPath){
		//遍历目录下的所有文档
		//String path = "data/20news-18828";
		File folds= new File(docsPath);

		String[] subfolds = folds.list();
		for (String filesName : subfolds){
			System.out.println(docsPath + filesName);
			for (File files : new File(docsPath + filesName).listFiles()){
				Document doc = new Document(files.getAbsolutePath(),
						termToIndexMap, indexToTermMap, termCountMap);
				docs.add(doc); //将文档加入到语料库中
			}
		}

	}


	/**
	 * 处理一篇文档的工具类
	 */
	public static class Document {
		public String getDocName() {
			return docName;
		}

		private String docName;

		public int[] getDocWords() {
			return docWords;
		}

		int[] docWords;

		/**
		 * 构造方法，处理本篇文档中的所有词。
		 * @param docName 文档路径
		 * @param termToIndexMap  word-index map
		 * @param indexToTermMap index-word map
		 * @param termCountMap word count
		 */
		public Document(String docName, Map<String, Integer> termToIndexMap, ArrayList<String> indexToTermMap, Map<String, Integer> termCountMap){
			this.docName = docName;
			//Read file and initialize word index array
			ArrayList<String> docLines = new ArrayList<String>();
			ArrayList<String> words = new ArrayList<String>();
			//读取数据
			FileUtil.readLines(docName, docLines);
			for(String line : docLines){
				//分解每一个行，得到单词，保存在words中
				FileUtil.tokenizeAndLowerCase(line, words);
			}
			//Remove stop words and noise words
			for(int i = 0; i < words.size(); i++){
				if(words.get(i).length() < 2){
					words.remove(i);
					i--;
				}
			}
			//Transfer word to index
			this.docWords = new int[words.size()];
			for(int i = 0; i < words.size(); i++){
				String word = words.get(i);
				//如果当前单词没有加入到map中，加入到map最后，否则
				if(!termToIndexMap.containsKey(word)){
					int newIndex = termToIndexMap.size();
					termToIndexMap.put(word, newIndex);
					indexToTermMap.add(word);
					termCountMap.put(word, new Integer(1));
					docWords[i] = newIndex;
				} else {
					docWords[i] = termToIndexMap.get(word);
					termCountMap.put(word, termCountMap.get(word) + 1);
				}
			}
			words.clear();
		}

		/**
		 * 处理噪声单词，只要不是正常的英文单词，都认为是噪声单词
		 * @param string 需要判别的string
		 * @return true:是噪声单词，false: 正常的单词
		 */
		public boolean isNoiseWord(String string) {
			// TODO Auto-generated method stub
			string = string.toLowerCase().trim();
			//匹配字符串
			Pattern MY_PATTERN = Pattern.compile(".*[a-zA-Z]+.*");
			Matcher m = MY_PATTERN.matcher(string);
			// filter @xxx and URL
			if(string.matches(".*www\\..*") || string.matches(".*\\.com.*") || 
					string.matches(".*http:.*") || string.matches("From:") || string.matches("to:") ||
					string.matches(">?"))
				return true;
			if (!m.matches()) {
				return true;
			} else
				return false;
		}

		/**
		 * 判断一行是否是From:开口的
		 * @param string
		 * @return
		 */
		public boolean isFrom(String string){
			return  false;
		}
		
	}
}

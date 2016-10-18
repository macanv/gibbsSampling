package com.macan.gibbsLDA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.text.AbstractDocument.BranchElement;


/**
 * doc set 文档集
 * @author macan
 *
 */
public class Corpus {

	List<int[]> documentList;
	Vocabulary vocabulary;
	
	public Corpus() {
		// TODO Auto-generated constructor stub
		documentList = new LinkedList<int[]>();
		vocabulary = new Vocabulary();
	}
	
//	public Map<String, Integer> addDocument(List<String> document) {
//		Map<String, Integer> doc = new HashMap<String, Integer>();
//		int i = 0;
//		for (String word : document) {
//			doc.put(word, vocabulary.getId(word, true));
//		}
//	}
	/**
	 * 添加文档到documentList
	 * @param document 一篇文档
	 * @return 该篇文档的所有词ID数组
	 */
	public int[] addDocument(List<String> document) {
		int[] doc = new int[document.size()];
		int i = 0;
		for (String word : document) {
			//将单词加入到词表中，vocabulary在这里set data
			doc[i++] = vocabulary.getId(word, true);
		}
		//添加到list 中
		documentList.add(doc);
		return doc;
	}
	
	/**
	 * 将list中的数组转化为二维数组 size :M*per_doc_word_nums
	 * @return 文档-词 array
	 */
	public int[][] toArray() {
		return documentList.toArray(new int[0][]);
	}
	
	/**
	 * 获取vocabulary 中word的size
	 * @return length of word
	 */
	public int getVocabularySize() {
		return vocabulary.wordSize();
	}
	
	/**
	 * 将磁盘文件导入
	 * @param path 文件目录
	 * @return 语料库
	 * @throws IOException
	 */
	public static Corpus load(String path) throws IOException {
		Corpus corpus = new Corpus();
		File folder = new File(path);
		//依次读取目录下的所有文件
		for (File file : folder.listFiles()) {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String line;
			List<String> wordList = new LinkedList<String>();
			//按行读取当前文件
			while ((line = reader.readLine()) != null) {
				//按照空格切割每一行的内容，将单词存放在words中
				String[] words = line.split(" "); 
				for (String word : words) {
					//如果单词的长度小于2，忽略该单词
					if (word.trim().length() < 2) {
						continue;
					}
					//将符合条件的单词加入到List中
					wordList.add(word);
				}
			}
			//读取完当前文件后，关闭bufferedReader
			reader.close();
			//将当前doc中所有词加入到语料库中
			corpus.addDocument(wordList);
		}
		//如果语料库中的单词为0，返回空
		if (corpus.getVocabularySize() == 0) {
			return null;
		}else {
			return corpus;
		}
	}
	
	/**
	 * 获取词表
	 * @return 词表
	 */
	public Vocabulary getVocabulary() {
		return vocabulary;
	}
	
	/**
	 * 获取文档矩阵 
	 * @return M*per_doc_word_nums 的矩阵
	 */
	public int[][] getDocument() {
		return toArray();
	}
	
	/**
	 * 添加一篇文章加入到训练语料库中
	 * @param path 文章的路径
	 * @param vocabulary 词表
	 * @return 返回该文章的词矩阵
	 * @throws IOException
	 */
	public static int[] loadDocument(String path, Vocabulary vocabulary) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(path));
		List<Integer> wordList = new LinkedList<Integer>();
		String line;
		while((line = reader.readLine()) != null){
			String[] words = line.split(" ");
			for (String word : words) {
				if (word.trim().length() < 2) {
					continue;
				}
				
				//添加单词到词表
				Integer id = vocabulary.getId(word);
				//如果该单词已经存在原语料库的词表中了，该单词有效，放入新的document 词 array中
				if (id != null) {
					wordList.add(id);
				}
			}
		}
		reader.close();
		//将wordlist中的id 复制到数组中，返回该文档的单词矩阵
		int[] result = new int[wordList.size()];	
		int i = 0;
		for (Integer integer : wordList) {
			result[i++] = integer;
		}
		return result;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		final StringBuilder sBuilder = new StringBuilder();
		for (int[] doc : documentList) {
			sBuilder.append(Arrays.toString(doc)).append("\n");
		}
		sBuilder.append(vocabulary);
		return super.toString();
	}
}

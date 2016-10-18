package com.macan.gibbsLDA;

import java.util.Map;
import java.util.TreeMap;


/**
 * word set 字典集合
 * @author macan
 *
 */
public class Vocabulary {
	
	/**
	 * word-id map 保存单词和单词的编号id  {“word”， id}
	 */
	Map<String, Integer> wordIdMap;
	
	/**
	 * id-word 单词数组，通过下标来索引单词
	 */
	String[] id2wordArray;
	
	
	//init
	public Vocabulary() {
		this.wordIdMap = new TreeMap<String, Integer>();
		this.id2wordArray = new String[1024];
	}
	
	/**
	 * 通过id 获取单词
	 * @param id : 单词的ID 
	 * @return id 对应的单词
	 */
	public String getWord(Integer id) {
		return id2wordArray[id];
	}
	
	
	/**
	 * 以 false的方式去查找单词id
	 * @param word
	 * @return 该单词对应的ID
	 */
	public Integer getId(String word) {
		return getId(word, false);
	}
	
	/**
	 * 根部单词获取在map中的单词ID，
	 * 如果该单词的ID不存在且create参数不为false，将该词加入到词表中
	 * @param word 目标单词
	 * @param create 是否在map中add
	 * @return 返回单词对应的ID
	 */
	public Integer getId(String word, boolean create) {
		//1,在map中查找该单词的ID
		Integer id = wordIdMap.get(word);
		//如果不需要新加该单词到map，那么直接返回ID
		if (!create) {
			return id;
		}
		//如果map中没有该单词
		if(id == null){
			id = wordIdMap.size(); //分配该单词的id 等于map的size + 1 （0 开始）
		}
		//将该单词加入到没map中
		wordIdMap.put(word, id);
		//如果单词array满了，那么调整array大小
		if (id2wordArray.length - 1 < id) {
			resize(wordIdMap.size() * 2); //调整大小为当前的两倍
		}
		//更新array,将单词和对应id 加入到数组中
		id2wordArray[id] = word;
		
		return id;
	}
	
	/**
	 * alloc id-word array size
	 * @param n 拓展的大小
	 */
	public void resize(int n) {
		String[] nArray = new String[n];
		System.arraycopy(id2wordArray, 0, nArray, 0, id2wordArray.length);
		id2wordArray = nArray; //copy 
	}
	
	/**
	 * 获取map的大小
	 * @return 当前map上所有词的数量
	 */
	public Integer wordSize() {
		return wordIdMap.size();
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		final StringBuilder sbBuilder = new StringBuilder();
		for (int i = 0; i < id2wordArray.length; i++) {
			if (id2wordArray[i] == null) {
				break;
			}
			sbBuilder.append(i).append("=").append(id2wordArray[i]).append("\n");
		}
		return super.toString();
	}
}

package com.macan.gibbsLDA;

//import java.util.Collections;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.Map;
//import java.util.TreeMap;
import java.util.*;

/**
 * 格式化输出矩阵
 * @author macan
 *
 */
public class LDAUtil {

	/**
	 * 每个主题下，每个单词：单词出现概率 mapArray K*V map长度为V，array长度为K
	 * @param phi LDA模型中的phi 矩阵，topic-word 矩阵，size ：K*V  ？
	 * @param vocabulary 词表，语料库中的所有词
	 * @param limit 一个topic中的最大词数量
	 * @return  phi矩阵
	 * 
	 */
	//@SuppressWarnings("unchecked")
	public static Map<String, Double>[] translate(double[][] phi, Vocabulary vocabulary, int limit) {
		//取topic 下最小的词数量
		limit = Math.min(limit, phi[0].length);
		Map<String, Double>[] result = new Map[phi.length];
		//遍历每一个topic向量
		for (int k = 0; k < phi.length; k++) {
			//
			Map<Double, String> rankMap = 
					new TreeMap<Double, String>(Collections.reverseOrder());
			//将当前主题下对应词的概率和和词表中对应的词存放在map中，
			for (int i = 0; i < phi[k].length; i++) {
				rankMap.put(phi[k][i], vocabulary.getWord(i));
			}
			//使用迭代器指向rankMap的起始地址
			Iterator<Map.Entry<Double, String>> iterator = rankMap.entrySet().iterator();
			//为每个result数组的map分配实体类型
			result[k] = new LinkedHashMap<String, Double>();
			for (int i = 0; i < limit; i++) {
				//循环获取rankMap中的数据
				Map.Entry<Double, String> entry = iterator.next();
				//将获取到的数据存放到result中
				result[k].put(entry.getValue(), entry.getKey());
			}
		}
		return result;
	}
	
	/**
	 * 获取最大的topic map
	 * @param tp 主题参数
	 * @param phi phi 矩阵
	 * @param vocabulary 词表
	 * @param limit 每个主题下最大的单词数量
	 * @return 最大topic的map 数据
	 */
	public static Map<String, Double> translate(double[] tp, 
			double[][] phi, Vocabulary vocabulary, int limit) {
		//调用上面方法，获取topic-word 矩阵
		Map<String, Double>[] topicMapArray = translate(phi, vocabulary, limit);
		int t = -1;
		double p = -1.0;
		//获取最大的topic
		for (int k = 0; k < tp.length; k++) {
			if (tp[k] > p) {
				p = tp[k];
				t = k;
			}
		}
		return topicMapArray[t];
	}
	
	/**
	 * 打印mapArray 数据
	 * @param resule
	 */
	public static void  explain(Map<String, Double>[] resule) {
		int i = 0;
		for (Map<String, Double> topicMap : resule) {
			System.out.printf("topic %d\n", i++);
			explain(topicMap);
			System.out.println();
		}
	}
	
	/**
	 * 打印map 中的数据集合
	 * @param topicMap
	 */
	public static void explain(Map<String, Double> topicMap) {
		for (Map.Entry<String,Double> entry : topicMap.entrySet()) {
			System.out.println(entry);
		}
	}
	
}

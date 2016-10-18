package com.macan.testGibbsLDA;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.macan.gibbsLDA.Corpus;
import com.macan.gibbsLDA.LDAGibbsSampler;
import com.macan.gibbsLDA.LDAUtil;


/**
 * 测试类
 * @author macan
 *
 */
public class TestGibbsSampling {

	@Test
	public void testGibbsSampling() throws IOException {
		//导入语料库
		Corpus corpus = Corpus.load("data/mini");
		System.out.println("总共训练的词数: " + corpus.getVocabularySize());
		//创建LDA抽词器
		LDAGibbsSampler ldaGibbsSampler = new LDAGibbsSampler(corpus.getDocument(),
				corpus.getVocabularySize());
		//开始吉布斯抽样训练超参数   传入topic 数量为10
		ldaGibbsSampler.gibbs(10);
		
		//获取训练后的phi 矩阵
		double[][] phi = ldaGibbsSampler.getPhi();
		
		//格式化输出
        //Map<String, Double>[] topicMap = LDAUtil.translate(phi, corpus.getVocabulary(), 10);
        //LDAUtil.explain(topicMap);
		
		
		
		
	}
	//@Test
	public void testGibbsSampling1() throws IOException {
		
        // 1. Load corpus from disk
        Corpus corpus = Corpus.load("data/mini");
        System.out.println("count of vocabulary: " + corpus.getVocabularySize());
        // 2. Create a LDA sampler
        LDAGibbsSampler ldaGibbsSampler = new LDAGibbsSampler(corpus.getDocument(), corpus.getVocabularySize());
       // System.out.println("LDAGS :" + ldaGibbsSampler );
        // 3. Train it
        ldaGibbsSampler.gibbs(10);
        // 4. The phi matrix is a LDA model, you can use LdaUtil to explain it.
        double[][] phi = ldaGibbsSampler.getPhi();
        System.out.println(phi.length);
        Map<String, Double>[] topicMap = LDAUtil.translate(phi, corpus.getVocabulary(), 10);
        LDAUtil.explain(topicMap);
	}

	@Test
	public void  testFile() throws IOException {
		String path = "data/20news-18828";

	}
}

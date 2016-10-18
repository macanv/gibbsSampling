package lda.generativeNewDoc;

/**
 * Created by macan on 2016/10/10.
 */

import lda.com.FileUtil;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 根据LDA模型，在已知theta和phi后，生成一篇文档
 */
public class GenerativeDoc {
    private double[] p_theta;
    private double[] p_phi;
    private int[] docIndex;
    private int newDoc;
    private Map<Integer, Integer> topicIndex;
    /**
     * 构造方法，模拟随机生成一篇文档的过程
     * @param theta doc-topic distribution
     * @param phi doc-word distribution
     * @param wordCount 一篇文章的总个数
     */
   public  GenerativeDoc(double[][] theta, double[][] phi, int wordCount){
       //init map
       topicIndex = new HashMap<Integer, Integer>();
       //1. 随机抽取一篇需要生成的文档
       int doc = (int)(Math.random() * theta.length);
       this.newDoc = doc;
       System.out.println("doc :" + doc);
       //theta中抽取该文档的主题分布的累加
       p_theta = new double[theta[doc].length];
       p_theta[0] = theta[doc][0];
       for (int j = 1; j <theta[doc].length; ++j){
           p_theta[j] = theta[doc][j] + p_theta[j - 1];
       }

       //模拟生成一篇又N个单词的文章
       docIndex = new int[wordCount];
       for (int n = 0; n < wordCount; ++n){
            //2.根据该篇文章的主题分布，抽取该z
            int z= sampling(p_theta);
           System.out.println("z = " + z);
            //3.根据z,在phi中抽取word
            p_phi = new double[phi[z].length];
            p_phi[0] = phi[z][0];
           for (int i = 1; i < phi[z].length; ++i){
               p_phi[i] += phi[z][i] + p_phi[i - 1];
           }
            //抽取word的index,保存在docIndex数组中
            docIndex[n] = sampling(p_phi);
           System.out.println("w = " + docIndex[n]);
       }

   }

    /**
     * 随机抽取
     * @param p 需要抽取的概率分布累加值
     * @return 抽到的结果
     */
    public int  sampling(double[] p) {
        double u = Math.random() * p[p.length - 1]; //p[] is unnormalised
        int z;
        for(z = 0; z < p.length; z++){
            if(u < p[z]){
                break;
            }
        }
        return  z;
    }


    /**
     * 获取该篇文章的单词index
     * @return
     */
    public int[] getDcoument(){
        return docIndex;
    }
    /**
     * 保存生成的文档到本地
     * @param path:保存文档的路径
     */
    public void saveGenerativeDoc(String path, ArrayList<String> indexToTermMap){
        ArrayList<String> doc = new ArrayList<String>();
        for (int i = 0; i < docIndex.length; ++i) {
            doc.add(indexToTermMap.get(docIndex[i]) + " ");
            if ((i % 10 == 0) && (i!= 0)) {
                doc.add("\n");
            }
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));
            for (String word: doc){
                writer.write(word);
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(doc);

    }

    public int getNewDoc(){
        return  newDoc;
    }


    public void printWord(int[] words, ArrayList<String> indexToTermMap){
        String path = "data/LdaResults/localDoc";
        ArrayList<String> doc = new ArrayList<String>();
        for (int i = 0; i < words.length; ++i) {
            doc.add(indexToTermMap.get(words[i]) + " ");
            if ((i % 10) == 0  && (i != 0)) {
                doc.add("\n");
            }
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));
            for (String word: doc){
                writer.write(word);
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void topicIndexAdd(Integer z){
        boolean temp = topicIndex.containsKey(z);
        //如果不存在z->value,将该value 加入到map中
        if (!temp){
            topicIndex.put(z, new Integer(1));
        }else{
            Integer count = topicIndex.get(z) + 1;
            topicIndex.put(z, count);
        }
    }

    public Map<Integer, Integer> getTopicIndex(){
        savaTopicIndex(topicIndex);
        return topicIndex;
    }

    public void  savaTopicIndex(Map<Integer, Integer> topicIndex){
        if (topicIndex == null){
            topicIndex = this.topicIndex;
        }
        String path = "data/LdaResults/topicIndex";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));
            Set<Map.Entry<Integer,Integer>> set = topicIndex.entrySet();
            for (Map.Entry<Integer, Integer> va:set){
                writer.write("topic" + va.getKey() + "   抽取次数:" + va.getValue() + "\n");
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

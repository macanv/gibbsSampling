package lda.application;

import lda.autoLabel.AutoLabel;
import lda.compteSimilaryText.SimilaryText;
import lda.conf.PathConfig;
import lda.generativeNewDoc.GenerativeDoc;
import lda.main.Documents;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;


/**
 * Created by macan on 2016/10/11.
 */
public class TestApp {

    public  static  void main(String[] args){

        String originalDocsPath = "data/english/long.dat";
        String resultPath = chinese.conf.PathConfig.LdaResultsPath;

        Documents docSet = new Documents();
        //读取文档数据
        docSet.readDocs(originalDocsPath);
        System.out.println("wordMap size " + docSet.getTermToIndexMap().size());

       Application application = new Application();
        System.out.println("end");

        System.out.println("generative a new document...");
        //生成一篇新的文档
        double[][] theta = toArray(application.getTheta());
        double[][] phi = toArray(application.getPhi());
        GenerativeDoc newDoc = new GenerativeDoc(theta, phi,200);
        //保存生成的文档到本地文件夹
        newDoc.saveGenerativeDoc(resultPath + "/newDoc", docSet.getIndexToTermMap());
        //读取原始文档
        int localdoc = newDoc.getNewDoc();
        int[] word = docSet.getDocs().get(localdoc).getDocWords();
        System.out.println(docSet.getDocs().get(localdoc).getDocName());
        newDoc.printWord(word, docSet.getIndexToTermMap());
        newDoc.savaTopicIndex(null);

        //计算相似文档
        System.out.println("compute the similarily of each document...");
        SimilaryText similaryText = new SimilaryText(theta);
        similaryText.sortList(resultPath + "/similary");

        System.out.println("文档自动打标签:");
        //文档自动打标签
        AutoLabel autoLabel = new AutoLabel(application.getTheta(), application.getPhi(), 5);
        autoLabel.LabelDocWithMaxProbolityWord(docSet.getIndexToTermMap(), resultPath + "/label");



    }

    /**
     * 将ArrayList<ArrayList<Double>> 转化为数组
     * @param theta 需要转化的数组
     * @return  转化成功的数组
     */
    public  static double[][] toArray(ArrayList<ArrayList<Double>> theta) {
        double[][] result = new double[theta.size()][];
        for (int i = 0; i < theta.size(); ++i) {
            result[i] = new double[theta.get(i).size()];
            for (int j = 0; j < theta.get(i).size(); ++j) {
                //System.out.println(theta.get(i).get(j));
                result[i][j] = theta.get(i).get(j).doubleValue();
            }
        }
        return result;
    }
}



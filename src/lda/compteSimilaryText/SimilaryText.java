package lda.compteSimilaryText;

/**
 * Created by macan on 2016/10/11.
 */

import lda.com.TextPairs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;



/**
 * 计算相似文档
 */
public class SimilaryText {

    double[][] distance;
    /**
     * 构造方法，计算所有文档的距离
     * @param theta
     */
    public  SimilaryText(double[][] theta){

        distance = new double[theta.length][theta.length];
        double temp = 0.0;
        for (int n = 0; n < theta.length; ++n){
            for (int i = n + 1; i < theta.length; ++i){
                for (int j = 0; j < theta[i].length; ++j){
                    //距离计算公式sigma(theta(i,j) - theta(f,i))^2
                    temp += Math.pow(Math.sqrt(theta[n][j]) - Math.sqrt(theta[i][j]), 2);
                    distance[n][i] = temp;
                    distance[i][n] = temp;
                    temp = 0.0;
                }
            }
        }


    }

    /**
     * 对计算距离后的数组进行排序
     */
    public void sortList(String path){
        //找出与每一篇文章最相近的文章
        int index = 0;
        double min = 0.0;
        String[] datas = new String[distance.length];
        for (int i = 0 ; i < distance.length; ++i){
            TextPairs textPairs =  similariaryWithIndex(i);
            String  data = "与文章" + (i + 1) + "最相似的文章是第：" + (textPairs.index + 1)  + "篇文章" + "他们之间的距离是:" + textPairs.min;
            datas[i] = data;
                    //System.out.println("与文章" + (i + 1) + "最相似的文章是第：" + (textPairs.index + 1)  + "篇文章" + "他们之间的距离是:" + textPairs.min);
        }
        write(datas,path);
    }

    public void write(String[] data, String path){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));
            for ( int i = 0; i < distance.length; ++i){
                writer.write(data[i].toString() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查找与给定文章最相似的文章。
     * @param doc 给定文章的编号
     * @return 最相思文章的编号
     */
    public TextPairs similariaryWithIndex(int doc){
        double min = 100;
        int minIndex = 0;
        for (int j = 0 ; j < distance.length; ++j){
            if (distance[doc][j] == 0){
                continue;
            }
            if (distance[doc][j] < min){
                min = distance[doc][j]; //更新最小值
                minIndex = j;
            }
        }
        TextPairs textPairs = new TextPairs();
        textPairs.index = minIndex;
        textPairs.min = min;
        return  textPairs;
    }

    /**
     * 插入法排序
     * @param is：待排序的数组
     */
    public static void insertSort(double[] is) {
        /**
         * 第0个是已经排好序的，所以从第一个开始插入
         */
        for (int i = 1; i < is.length; i++) {
            /**
             * 每一个元素与前边已经排好的元素做比较
             */
            int k;
            double temp = is[i];
            for (k = i - 1; k >= 0 && is[k] > temp; k--) {
                is[k + 1] = is[k];
            }
            is[k + 1] = temp;
        }
    }
}

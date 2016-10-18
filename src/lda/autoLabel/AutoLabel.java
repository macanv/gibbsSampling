package lda.autoLabel;

/**
 * Created by macan on 2016/10/11.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
 * 根据LDA训练后，得到的theta,phi文件，进行文档的自动打标签
 */
public class AutoLabel {
    /**
     * 最大标签词个数
     */
    private int wordCount;

    public int[][] getLabelIndex() {
        return labelIndex;
    }

    /**
     * 每篇文档的标签单词index
     */
    private  int[][] labelIndex;

    /**
     * 每个主题下，概率最大的单词
     */
    private int[][] maxWords;
    /**
     * 构造方法，进行数据处理
     * @param theta theta 数组
     * @param phi phi数组
     * @param  wordCount 最大标签词个数
     *
     */
    public AutoLabel(ArrayList<ArrayList<Double>> theta, ArrayList<ArrayList<Double>> phi, int wordCount){
        this.wordCount = wordCount;
        maxWords = new int[phi.size()][wordCount];
        //1. 根据theta 找出max topic index for each doc
        int[] maxTopicIndex = new int[theta.size()];
        for (int i = 0; i < theta.size(); ++i){
            //寻找每一篇文章的最大主题，保存在数组中，下表表示文章编号，value表示最大主题编号
           maxTopicIndex[i]  = maxTopicWithDocDistribution(theta.get(i));
        }

        //2.根据获取到的文章最大主题，找到该主题下，概率最大的几个单词，作为标记这篇文章的label.这里，我们取五个
        maxWordWithTopicIndex(wordCount, phi);
        labelIndex = new int[theta.size()][wordCount];
        for (int i = 0 ; i < theta.size(); ++i){
            //对于每一篇文档，都做同样的处理。
            labelIndex[i] = maxWords[maxTopicIndex[i]];
        }

        //根据单词的index, 找出实际对应的单词


    }

    public int maxTopicWithDocDistribution(ArrayList<Double> doc){
        Double max = 0.0;
        int index = 0;
        for (int i = 0 ; i < doc.size(); ++i){
            if (doc.get(i) > max){
                max = doc.get(i);
                index = i;
            }
        }
        return  index;
    }

    public void maxWordWithTopicIndex(int wordCount, ArrayList<ArrayList<Double>> phi){
        //找出每个主题下，概率最大的wordCount个单词
        for (int i = 0; i < phi.size(); ++i){
            int[] words = new int[wordCount];
            //获取最大主题的词分布
            ArrayList<Double> topicWord = phi.get(i);
            //在这个词分布中，找到最大的几个概率的单词的index
            for (int j = 0; j < wordCount ; ++j){
                Double max = 0.0;
                int index = 0;
                //每次循环找到最大的（这个算法效率低下，正确的做法应该是先排序，然后再做，不过，我们主要是找到前五个，总共的比较次数是N^5（近似），如果做排序，那么也应该只需要排前面五个就好了。这样的话，效率也就不低了）
                for (int k = 0 ; k < topicWord.size(); ++k){
                    if (topicWord.get(k) > max){
                        max = topicWord.get(k);
                        index = k;
                    }
                }
                words[j] = index;
                //移除该最大的，继续寻找剩余的最大的
                topicWord.remove(index);
            }
            maxWords[i] = words;
        }

        //System.out.println(phi.size() + "   " + phi.get(1).size());
    }

    /**
     * 根据单词index，获取相对于的实际单词
     * @param indexToTermMap 单词-index的字典
     * @return 成果，返回true, 其他，返回false
     */
    public boolean LabelDocWithMaxProbolityWord(ArrayList<String> indexToTermMap, String path){
        ArrayList<String[]> labels = new ArrayList<String[]>();
        for (int i = 0; i < labelIndex.length; ++i){
            String[] words = new String[wordCount];
            for (int j = 0; j < labelIndex[i].length; ++j){
                words[j] = indexToTermMap.get(labelIndex[i][j]) + "\t";
                //System.out.println(words[j]);
            }
            labels.add(words);
        }

        //讲数据写入到本地文件
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));
            for (String[] word : labels){
                for (int i = 0 ; i < word.length; ++i){
                    writer.write(word[i]);
                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

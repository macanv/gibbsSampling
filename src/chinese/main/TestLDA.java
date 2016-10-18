package chinese.main;

/**
 * Created by macan on 2016/10/11.
 */

import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * 读取LDA训练后的数据，来验证模型以及模型的应用
 */
public class TestLDA {


    @Test
    public void testReadFile() throws IOException {
        String theta_path = "data/LdaResults/lda_1000.theta";
        String phi_path = "data/LdaResults/lda_1000.phi";

        ArrayList<ArrayList<Double>> theta = new ArrayList<>();
        ArrayList<ArrayList<Double>> phi = new ArrayList<>();
        BufferedReader reader;
//        double[][] theta;
//        double[][] phi;
        //读取theta文件
        try {
            reader = new BufferedReader(new FileReader(new File(theta_path)));
            String line = "";
            while ((line = reader.readLine()) != null){
                StringTokenizer tokenizer = new StringTokenizer(line);
                ArrayList<Double> data = new ArrayList<Double>();
                while (tokenizer.hasMoreTokens()){
                    data.add(Double.parseDouble(tokenizer.nextToken()));
                }
                theta.add(data);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //读取phi文件
        try {
            reader = new BufferedReader(new FileReader(new File(phi_path)));
            String line = "";
            while ((line = reader.readLine()) != null){
                StringTokenizer tokenizer = new StringTokenizer(line);
                ArrayList<Double> data = new ArrayList<Double>();
                while (tokenizer.hasMoreTokens()){
                    data.add(Double.parseDouble(tokenizer.nextToken()));
                }
                phi.add(data);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

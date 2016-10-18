package chinese.application;

/**
 * Created by macan on 2016/10/11.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * LDA的一些应用
 */
public class Application {

    private ArrayList<ArrayList<Double>> theta;
    private ArrayList<ArrayList<Double>> phi;


    public  Application(){
        readThetaPhi();

    }

    public void readThetaPhi(){
        String theta_path = "data/LdaResults/lda_1000.theta";
        String phi_path = "data/LdaResults/lda_1000.phi";

        theta = new ArrayList<>();
        phi = new ArrayList<>();
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
        } catch (IOException e) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  ArrayList<ArrayList<Double>> getTheta(){
        return theta;
    }

    public  ArrayList<ArrayList<Double>> getPhi(){
        return  phi;
    }



}

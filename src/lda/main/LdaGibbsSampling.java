package lda.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import lda.com.FileUtil;
import lda.com.TextPairs;
import lda.compteSimilaryText.SimilaryText;
import lda.conf.ConstantConfig;
import lda.conf.PathConfig;
import lda.generativeNewDoc.GenerativeDoc;
import org.junit.Test;

/**Liu Yang's implementation of Gibbs Sampling of LDA
 * @author yangliu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yangliuyx@gmail.com
 */

public class LdaGibbsSampling {
	
	public static class modelparameters {
		float alpha = 0.5f; //usual value is 50 / K
		float beta = 0.1f;//usual value is 0.1
		int topicNum = 100;
		int iteration = 100;
		int saveStep = 10;
		int beginSaveIters = 50;
	}
	
	/**Get parameters from configuring file. If the 
	 * configuring file has value in it, use the value.
	 * Else the default value in program will be used
	 * @param ldaparameters
	 * @param parameterFile
	 * @return void
	 */
	private static void getParametersFromFile(modelparameters ldaparameters,
			String parameterFile) {
		// TODO Auto-generated method stub
		ArrayList<String> paramLines = new ArrayList<String>();
		FileUtil.readLines(parameterFile, paramLines);
		for(String line : paramLines){
			String[] lineParts = line.split("\t");
			switch(parameters.valueOf(lineParts[0])){
			case alpha:
				ldaparameters.alpha = Float.valueOf(lineParts[1]);
				break;
			case beta:
				ldaparameters.beta = Float.valueOf(lineParts[1]);
				break;
			case topicNum:
				ldaparameters.topicNum = Integer.valueOf(lineParts[1]);
				break;
			case iteration:
				ldaparameters.iteration = Integer.valueOf(lineParts[1]);
				break;
			case saveStep:
				ldaparameters.saveStep = Integer.valueOf(lineParts[1]);
				break;
			case beginSaveIters:
				ldaparameters.beginSaveIters = Integer.valueOf(lineParts[1]);
				break;
			}
		}
	}
	
	public enum parameters{
		alpha, beta, topicNum, iteration, saveStep, beginSaveIters;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String originalDocsPath = PathConfig.ldaDocsPath;
		String resultPath = PathConfig.LdaResultsPath;
		String parameterFile= ConstantConfig.LDAPARAMETERFILE;
		
		modelparameters ldaparameters = new modelparameters();
		getParametersFromFile(ldaparameters, parameterFile);
		Documents docSet = new Documents();
		//读取文档数据
		docSet.readDocs(originalDocsPath);
		System.out.println("wordMap size " + docSet.termToIndexMap.size());
		FileUtil.mkdir(new File(resultPath));
		//初始化LDA参数
		LdaModel model = new LdaModel(ldaparameters);
		System.out.println("1 Initialize the model ...");
		//初始化gibbs sampling
		model.initializeModel(docSet);
		System.out.println("2 Learning and Saving the model ...");
		//随机模拟gibbs sampling
		model.inferenceModel(docSet);
		System.out.println("3 Output the final model ...");
		//保存生成的数据到本地
		model.saveIteratedModel(ldaparameters.iteration, docSet);
		System.out.println("Done!");

		System.out.println("generative a new document...");
		//生成一篇新的文档
		GenerativeDoc newDoc = new GenerativeDoc(model.getTheta(), model.getPhi(),200);
		//保存生成的文档到本地文件夹
		newDoc.saveGenerativeDoc(resultPath + "/newDoc", docSet.indexToTermMap);

		//计算相似文档
		System.out.println("compute the similarily of each document...");
		SimilaryText similaryText = new SimilaryText(model.getTheta());
		similaryText.sortList(resultPath + "/similary");

		//
	}


//	@Test
//	public  void testReadDatFile(){
//		String path = "";
//		BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
//	}
}

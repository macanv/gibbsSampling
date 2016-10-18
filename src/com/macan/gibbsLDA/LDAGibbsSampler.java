package com.macan.gibbsLDA;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;
import javax.xml.stream.events.EndDocument;

/**
 * Gibbs Sampler 算法实现
 * 
 * @author macan
 *
 */
public class LDAGibbsSampler {

	/**
	 * vocabulary size 单词个数 （每篇文档的单词个数，每篇文档可以看成是独立的）
	 */
	int V;

	/**
	 * number of topic 主题个数
	 */
	int K;

	/**
	 * document set 文档集 第i篇文档的所有单词
	 */
	int[][] document;

	/**
	 * Dirichlet parameter (doc-topic associations) 文档主题超参数 (取多少队训练的结果影响较小，一般取
	 * 50/K)
	 */
	double alpha = 2.0;

	/**
	 * Dirichlet parameter (topic-word associations)
	 */
	double beta = 0.5;

	/**
	 * topic assignments for each word 
	 * 每个单词的主题分配Z[i][j] := 文档i，第j个单词的主题编号 
	 * the Serial number of topic of i_th doc,j_th word size : M* per_doc_word_nums
	 */
	int[][] z;

	/**
	 * nw[i][j] := 单词i 归入主题j的次数 j_th topic下i_th word 个数 size : V*K
	 */
	int[][] nw;

	/**
	 * nd[i][j] := 文档i 中归入主题j的词语个数 i_th document 下j_th topic 中单词的个数 size : M*K
	 */
	int[][] nd;

	/**
	 * nwsum[j] 归入主题j的词语个数 size: K
	 */
	int[] nwsum;

	/**
	 * ndsum[i] 文档i中全部词语数量 size M
	 */
	int[] ndsum;

	/**
	 * theta 累积量
	 */
	double[][] thetasum;

	/**
	 * phi 累积量
	 */
	double[][] phisum;

	/**
	 * 样本容量
	 */
	int numstats;

	/**
	 * 多久更新一次统计量
	 */
	private static int THIN_INTERVAL = 20;

	/**
	 * 收敛前的迭代量
	 */
	private static int BURN_IN = 20;

	/**
	 * 最大迭代次数
	 */
	private static int ITERATIONS = 1000;

	/**
	 * 最后的模型个数（取收敛后的N个迭代的参数做平均可以使得模型质量很高）
	 */
	private static int SAMPLE_LAG = 10;

	private static int dispcol = 0;

	/**
	 * 导入要采样的数据，并初始化
	 * 
	 * @param document
	 *            文档数组
	 * @param V
	 *            vocabulary 词表大小 单词总个数
	 */
	public LDAGibbsSampler(int[][] document, int V) {
		// TODO Auto-generated constructor stub
		this.document = document;
		this.V = V;
	}

	/**
	 * 随机初始化参数
	 * 
	 * @param k
	 *            主题个数
	 */
	public void initialState(int k) {
		int M = document.length; // 文档个数

		// 初始化计数器
		nw = new int[V][K];  // word-topic
		nd = new int[M][K];  // doc-topic
		nwsum = new int[K];  // k_th topic word num;
		ndsum = new int[M];  // m_th sum of word

		// 初始化z[][] z_i 表示马氏链的初始状态 1:k
		z = new int[M][];
		for (int m = 0; m < M; m++) {
			int N = document[m].length; // 每篇文字的词个数
			z[m] = new int[N];

			for (int n = 0; n < N; n++) {
				int topic = (int) (Math .random() * k); // 随机参数一个主题 0~1.0
				z[m][n] = topic; // 给第m篇文档的第n个单词随机赋予一个topic
				// topic主题下该单词的次数+1
				nw[document[m][n]][topic]++;
				// 该文档下topic 主题中的词语个数+1
				nd[m][topic]++;
				// 该topic 中的词语个数+1
				nwsum[topic]++;
			}
			// 更新第m篇文档中单词的个数
			ndsum[m] = N;
		}
	}

	/**
	 * gibbs 采样的封装
	 * 
	 * @param k
	 *            主题个数
	 */
	public void gibbs(int k) {
		gibbs(k, alpha, beta);
	}

	/**
	 * 吉布斯采样
	 * 
	 * @param k
	 *            主题个数
	 * @param alpha
	 *            超参数 alpha的值
	 * @param beta
	 *            超参数 beta 的值
	 */
	public void gibbs(int k, double alpha, double beta) {
		this.K = k;
		this.alpha = alpha;
		this.beta = beta;

		// 初始化采样器
		thetasum = new double[document.length][k];
		phisum = new double[K][V];
		numstats = 0;


		// 初始化马尔科夫链
		initialState(K);

		System.out.println("开始采样");
		// 开始迭代 进行吉布斯采样
		// 对每一篇文档的每一个单词都要计算p(z|w) 一共迭代ITERATIONS次，如果收敛。。。。
		for (int i = 0; i < ITERATIONS; i++) { // 迭代次数
			for (int m = 0; m < z.length; m++) { // 每一篇文档
				for (int n = 0; n < z[m].length; n++) { // 每一个词语
					// z_i = z[m][n]
					// 从p(z_i|z_-i, w) 抽样
					int topic = sampleFullConditional(m, n);
					z[m][n] = topic;
				} // end one word
			}// end one doc
			dispcol++;
			//why?
			if (dispcol >= 100) {
				System.out.println();
				dispcol = 0;
			}
		}// end iterator
		System.out.println();
	}

	/**
	 * 随机抽样器，抽样的数据满足w,z的联合概率分布 对应公式中，计算联合概率的公式
	 * p(z|w) -- p(z,w)/p(z_i = j |z_-i, w) =
	 * (n_-i,j(w_i) + beta)/(n_-i,j(.) + W * beta) * (n_-i,j(d_i) +alpha)/
	 * (n_-i,.(d_i) + K * alpha)
	 * 
	 * @param m
	 *            doc_th
	 * @param n
	 *            word_th
	 * @return 第m 篇文档的第n个单词的主题编号
	 */
	private int sampleFullConditional(int m, int n) {
		// 先将m_th doc 下的n_th word移除
		int topic = z[m][n]; // 获取单词的主题编号
		nw[document[m][n]][topic]--;
		nd[m][topic]--;
		nwsum[topic]--;
		ndsum[m]--;

		//
		double[] p = new double[K];
		// 计算在给定单词后，去掉这个单词计算该单词的topic概率
		// p(z_i|z_-i,w) k :=1 to K
		for (int k = 0; k < K; k++) {
			p[k] = (nw[document[m][n]][k] + beta) / (nwsum[k] + V * beta)
					* (nd[m][k] + alpha) / (ndsum[m] + K * alpha);
		}

		// 逐项累加概率
		for (int k = 1; k < p.length; k++) {
			p[k] += p[k - 1];
		}
		System.out.println("p[k-1] = " + p[K -1 ]);
		// 正则化，利用0-1均匀分布，来随机表示投掷该词的主题编号
		double u = Math.random() * p[K - 1];
		// 如果某个topic的概率大于随机模拟得到的概率，那么就接受这个概率，就得到了这个topic index
		for (topic = 0; topic < p.length; topic++) {
			if (u < p[topic]) {
				break;
			}
		}
		// 将重新估的z[m][n]词语加入到变量中
		nw[document[m][n]][topic]++;
		nd[m][topic]++;
		nwsum[topic]++;
		ndsum[m]++;

		return topic;
	}

	/**
	 * 更新训练参数 thetasum,phisum
	 */
	private void updateParams() {
		// update thetasum
		for (int m = 0; m < document.length; m++) {
			for (int k = 0; k < K; k++) {
				thetasum[m][k] += (nd[m][k] + alpha) / (ndsum[m] + K * alpha);
			}
		}
		// update phisum
		for (int k = 0; k < K; k++) {
			for (int w = 0; w < V; w++) {
				phisum[k][w] += (nw[w][k] + beta) / (nwsum[k] + V * beta);
			}
		}
		// 参数求和更新次数加1
		numstats++;
	}

	/**
	 * 获取doc-topic 矩阵
	 * 
	 * @return theta 矩阵
	 */
	public double[][] getTheta() {
		double[][] theta = new double[document.length][K]; // M*k矩阵

		// 最后的模型个数,没有收敛完成
		if (SAMPLE_LAG > 0) {
			for (int m = 0; m < document.length; m++) {
				for (int k = 0; k < K; k++) {
					// theta 矩阵是迭代过程中所有theta矩阵的平均值
					theta[m][k] = thetasum[m][k] / numstats;
				}
			}
		} else { // 完成采样，达到标准
			for (int m = 0; m < document.length; m++) {
				for (int k = 0; k < K; k++) {
					// 计算theta 矩阵
					theta[m][k] = (nd[m][k] + alpha) / (ndsum[m] + K * alpha);
				}
			}
		}
		return theta;
	}

	/**
	 * 获取phi 超参数矩阵
	 * 
	 * @return phi 矩阵 topic-word size : K*V
	 */
	public double[][] getPhi() {
		double[][] phi = new double[K][V];
		if (SAMPLE_LAG > 0) {
			for (int k = 0; k < K; k++) {
				for (int w = 0; w < V; w++) {
					phi[k][w] = phisum[k][w] / numstats;
				}
			}
		} else { // 根据期望计算phi 矩阵
			for (int k = 0; k < K; k++) {
				for (int w = 0; w < V; w++) {
					phi[k][w] = (nw[w][k] + beta) / (ndsum[k] + V * beta);
				}
			}
		}

		return phi;
	}

	public static void hist(double[] data, int fmax) {
		double[] hist = new double[data.length];

		double hmax = 0;
		// 取最大的。。。？？？？
		for (int i = 0; i < data.length; i++) {
			hmax = Math.max(data[i], hmax);
		}

		double shrink = fmax / hmax;
		for (int i = 0; i < data.length; i++) {
			hist[i] = shrink * data[i];
		}

		NumberFormat nf = new DecimalFormat("00");
		String scale = "";
		for (int i = 0; i < fmax / 10 + 1; i++) {
			scale += "    .    " + i % 10;
		}

		System.out.println("x" + nf.format(hmax / fmax) + "\t0" + scale);
		for (int i = 0; i < hist.length; i++) {
			System.out.println(i + "\t|");
			for (int j = 0; j < Math.round(hist[i]); j++) {
				if ((j + 1) % 10 == 0) {
					System.out.println("]");
				} else {
					System.out.println("|");
				}
			}
			System.out.println();
		}
	}

	/**
	 * 配置gibbs sampling 参数
	 * 
	 * @param iterations
	 *            总迭代次数
	 * @param buruIN
	 *            最大迭代收敛次数
	 * @param thinInterval
	 *            更新参数
	 * @param sampleLag
	 *            抽样时间间隔（-1 表示最后只抽样一次）
	 */
	public void configure(int iterations, int buruIN, int thinInterval,
			int sampleLag) {
		ITERATIONS = iterations;
		BURN_IN = buruIN;
		THIN_INTERVAL = thinInterval;
		SAMPLE_LAG = sampleLag;

	}

	/**
	 * 通过获取的训练参数，预测新的文档
	 * 
	 * @param alpha
	 * @param beta
	 * @param phi
	 *            以前的phi 矩阵
	 * @param doc
	 *            新文档
	 * @return
	 */
	public static double[] inference(double alpha, double beta, double[][] phi,
			int[] doc) {
		int K = phi.length; // 新闻的的主题个数与原训练样本相同
		int V = phi[0].length; // 训练的词语个数也与原训练样本相同

		// 初始化统计变量
		// K_th topic 下 v_th word的个数
		int[][] nw = new int[V][K];
		// 新文档中主题编号
		int[] nd = new int[K];
		// k主题下单词总个数
		int[] nwsum = new int[K];
		// 文档中单词个数初始化为0
		int ndsum = 0;

		// z_i :=1:k 表示马氏链的初始状态
		int N = doc.length;
		int[] z = new int[N];
		for (int n = 0; n < N; n++) {
			int topic = (int) (Math.random() * K);
			z[n] = topic; // 初始化
			// 初始化参数
			nw[doc[n]][topic]++;
			nd[topic]++;
			nwsum[topic]++;
		}
		ndsum = N;

		// 迭代
		for (int i = 0; i < ITERATIONS; i++) {
			for (int n = 0; n < z.length; n++) {
				// 从p(z_i|z_-i, w) 中抽样前，在计数器中移除该词
				int topic = z[n]; //随机获取topic 
				nw[doc[n]][topic]--;
				nd[topic]--;
				nwsum[topic]--;
				ndsum--;
				
				//抽样数据
				double[] p = new double[K];
				//计算p(zi|z_-i,w)的概率
				for (int k = 0; k < K; k++) {
					p[k] =  phi[k][doc[n]] * (nd[k] + alpha) / (ndsum + K * alpha);
				}
				
				for (int k = 1; k < p.length; k++) {
					p[k] += p[k - 1];
				}
				
				//正则化
				double u = Math.random() * p[K - 1];
				for ( topic = 0; topic < p.length; topic++) {
					if (u < p[topic]) {
						break;
					}
				}
			
				//将该词 加入到计数器中
				nw[doc[n]][topic]++;
				nd[topic]++;
				nwsum[topic]++;
				ndsum++;
				z[n] = topic; //topic 
			}//end for one iterators
		}//end all iterators
		
		double[] theta = new double[K];
		for (int k = 0; k < K; k++) {
			theta[k] = (nd[k] + alpha) / (ndsum + K * alpha);
		}
		return theta;
	}
	
	/**
	 * 获取预测文本的主题分布
	 * @param phi 
	 * @param doc
	 * @return
	 */
	public static double[] inference(double[][] phi, int[] doc) {
		return inference(2.0, 2.0, phi, doc);
	}
	
}

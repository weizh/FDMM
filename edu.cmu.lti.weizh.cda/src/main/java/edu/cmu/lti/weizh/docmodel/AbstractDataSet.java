package edu.cmu.lti.weizh.docmodel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.cmu.lti.weizh.eval.EVAL_CONSTS;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;


public abstract class AbstractDataSet implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected List<Document> documents;
	
	public List<Document> getDocuments() {
		return this.documents;
	}
	
	protected String evalType;
	
	public String getEvalType() {
		return evalType;
	}

	public void setEvalType(String evalType) {
		this.evalType = evalType;
	}

	public AbstractDataSet(int capacity, String evalType){
		this.evalType = evalType;
		documents = new ArrayList<Document>(capacity);
	}
	
	/**
	 * 
	 * The sentence is shattered into words.
	 * 
	 * if fold ==Double.positiveInfinity, then all data is training. Else, split
	 * it.
	 * 
	 * 
	 * @param fold
	 * @param output
	 * @throws Exception
	 */
	public void crossValidationRandomGenerator(double fold, String output)
			throws Exception {
		Random r = new Random();
		int ifold = (int) fold;
		BufferedWriter bw = new BufferedWriter(new FileWriter(output));
		for (Document doc : documents) {
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {

					for (Word word : sent.getWords()) {
						boolean[] rand = new boolean[ifold];
						for (int i = 0; i < ifold; i++) {
							double rn = r.nextDouble();
							if (fold == Double.POSITIVE_INFINITY)
								word.setBooleanFolds(new boolean[] { true });
							else {
								rand[i] = rn > 1 / (double) ifold ? true
										: false;
								bw.write((i == 0 ? "" : "\t")
										+ (rand[i] == true ? 1 : 0)
										+ (i == ifold - 1 ? "\n" : ""));
							}
						}
						word.setBooleanFolds(rand);
					}
				}
			}
		}
		bw.close();
	}

	/**
	 * Fill in CV folds for each word. 
	 * If folds are split according to sentence, still fill in each word. 
	 * The fold split decision is made in RandomGenerator and stored in CV fold file.
	 * @param string
	 * @throws Exception
	 */
	public void fillCVFolds(String string) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(string));
		String line = "";
		for (Document doc : documents) {
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {
					for (Word word : sent.getWords()) {
						line = br.readLine();
						String[] toks = line.split("\t");
						boolean[] b = new boolean[toks.length];
						for (int i = 0; i < b.length; i++)
							b[i] = Integer.parseInt(toks[i]) == 1 ? true
									: false;
						word.setBooleanFolds(b);
					}
				}
			}
		}
	}

	/** all the tokens within the same sentence are tagged with the same CV fold label for each fold. 
	 * 
	 * @param fold
	 * @param output
	 * @throws Exception
	 */
	public void sentenceCrossValidationRandomGenerator(double fold, String output)
			throws Exception {

		Random r = new Random();
		int ifold = (int) fold;
		BufferedWriter bw = new BufferedWriter(new FileWriter(output));
		for (Document doc : documents) {
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {

					ArrayList<Boolean> arr = new ArrayList<Boolean>();
					for (int i = 0; i < ifold; i++) {
						double rn = r.nextDouble();
						arr.add(rn > 1 / (double) ifold ? true : false);
					}

					for (Word word : sent.getWords()) {
						boolean[] rand = new boolean[ifold];

						if (fold == Double.POSITIVE_INFINITY)
							word.setBooleanFolds(new boolean[] { true });
						else {
							for (int i = 0; i < ifold; i++) {
								int writei = arr.get(i) ? 1 : 0;
								bw.write((i == 0 ? "" : "\t") + writei
										+ (i == ifold - 1 ? "\n" : ""));
							}
						}
						word.setBooleanFolds(rand);
					}
				}
			}
		}
		bw.close();

	}
	
	public void printTokenByTokenEvaluation(int foldId) throws Exception {

		TObjectIntHashMap<String> allTrainStat, allTestStat;
		allTrainStat = new TObjectIntHashMap<String>();
		allTestStat = new TObjectIntHashMap<String>();

		TObjectIntHashMap<String> correct, totalGold, totalPredict;
		correct = new TObjectIntHashMap<String>();
		totalGold = new TObjectIntHashMap<String>();
		totalPredict = new TObjectIntHashMap<String>();

		for (Document doc : documents) {
			System.out.println(doc.getDocId());
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {
					List<Word> words = sent.getWords();
					for (Word word : words) {
						if (word.getBooleanFolds()[foldId])
							allTrainStat.adjustOrPutValue(word.getEntityType(), 1, 1);
						else
							allTestStat.adjustOrPutValue(word.getEntityType(), 1, 1);
						if (word.getBooleanFolds()[foldId])
							continue;
						if (word.getPrediction() == null)
							System.err.println("Not predicted word!");

						String goldStandardToCompare = null;
						switch (evalType) {
						case EVAL_CONSTS.NER_TYPE:
							goldStandardToCompare = word.getEntityType();
							break;
						case EVAL_CONSTS.POS_TYPE:
							goldStandardToCompare = word.getPartOfSpeech();
							break;
						case EVAL_CONSTS.CHUNK_TYPE:
							goldStandardToCompare = word.getChunkType();
							break;
						default:
							break;
						}
						if (goldStandardToCompare.equals(word.getPrediction()))
							if (!correct.contains(goldStandardToCompare))
								correct.put(goldStandardToCompare, 1);
							else
								correct.increment(goldStandardToCompare);

						if (totalGold.contains(goldStandardToCompare))
							totalGold.increment(goldStandardToCompare);
						else
							totalGold.put(goldStandardToCompare, 1);

						if (totalPredict.contains(word.getPrediction()))
							totalPredict.increment(word.getPrediction());
						else
							totalPredict.put(word.getPrediction(), 1);
					}
				}
			}
		}
		TObjectIntIterator<String> ci = correct.iterator();

		double accf1 = 0;
		int n = 0;
		while (ci.hasNext()) {
			n++;
			ci.advance();
			String label = ci.key();
			int val = ci.value();
			int totalgold = totalGold.get(label);
			int totalpredict = totalPredict.get(label);
			double p = (double) val / (double) totalpredict;
			double r = (double) val / (double) totalgold;
			double f1 = 2 * p * r / (p + r);
			// System.out.println("Label class "+ label +"	: "+ " P " + p
			// +"	R "+r + "	F1 "+ f1);
			System.out.printf("Label class	%s	:	P:	%.4f	R:	%.4f	F1:	%.4f\n", label, p, r, f1);
			accf1 += f1;
		}
		System.out.printf("Average f1 of all class is: %.4f", accf1 / (double) n);
		TObjectIntIterator<String> trainiter = allTrainStat.iterator();
		TObjectIntIterator<String> testiter = allTestStat.iterator();
		int traintotal = 0, testtotal = 0;
		// System.out.println("\ntraining stat:");
		// for (int i = 0 ; trainiter.hasNext(); i++){
		// trainiter.advance();
		// System.out.println(trainiter.key()+ trainiter.value());
		// traintotal += trainiter.value();
		// }
		// System.out.println(traintotal);
		// System.out.println("\ntest stat:");
		//
		// for (int i = 0 ; testiter.hasNext(); i++){
		// testiter.advance();
		// System.out.println(testiter.key()+ testiter.value());
		// testtotal += testiter.value();
		// }
		// System.out.println(testtotal);
	}


	
}

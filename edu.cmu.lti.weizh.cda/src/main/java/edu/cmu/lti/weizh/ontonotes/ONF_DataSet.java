package edu.cmu.lti.weizh.ontonotes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import edu.cmu.lti.weizh.models.DataSet;
import edu.cmu.lti.weizh.models.Document;
import edu.cmu.lti.weizh.models.NamedEntity;
import edu.cmu.lti.weizh.models.Paragraph;
import edu.cmu.lti.weizh.models.Sentence;
import edu.cmu.lti.weizh.models.Word;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;

public class ONF_DataSet extends DataSet {

	List<Document> documents;

	public ONF_DataSet() {
		documents = new ArrayList<Document>();
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void predictionsStat() throws Exception {

		TObjectIntHashMap<String> correct, totalGold, totalPredict;
		correct = new TObjectIntHashMap<String>();
		totalGold = new TObjectIntHashMap<String>();
		totalPredict = new TObjectIntHashMap<String>();

		for (Document doc : documents) {
			// System.out.println(doc.getDocId());
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {
					List<NamedEntity> nes = sent.getNamedEntities();
					for (NamedEntity ne : nes) {
						if (ne.getPrediction() == null)
							System.err.println("Not predicted Named Entity!");
						
						if (ne.getEntityType().equals(ne.getPrediction()))
							if (!correct.contains(ne.getEntityType()))
								correct.put(ne.getEntityType(), 1);
							else
								correct.increment(ne.getEntityType());
						if (totalGold.contains(ne.getEntityType()))
							totalGold.increment(ne.getEntityType());
						else
							totalGold.put(ne.getEntityType(), 1);
						if (totalPredict.contains(ne.getPrediction()))
							totalPredict.increment(ne.getPrediction());
						else
							totalPredict.put(ne.getPrediction(), 1);
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
	}

	public void predictionsWordsStat( int foldId) throws Exception {
		
		TObjectIntHashMap<String> allTrainStat, allTestStat;
		allTrainStat = new TObjectIntHashMap<String>();
		allTestStat = new TObjectIntHashMap<String>();
		
		TObjectIntHashMap<String> correct, totalGold, totalPredict;
		correct = new TObjectIntHashMap<String>();
		totalGold = new TObjectIntHashMap<String>();
		totalPredict = new TObjectIntHashMap<String>();

		for (Document doc : documents) {
			// System.out.println(doc.getDocId());
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
						
						if (word.getEntityType().equals(word.getPrediction()))
							if (!correct.contains(word.getEntityType()))
								correct.put(word.getEntityType(), 1);
							else
								correct.increment(word.getEntityType());
						if (totalGold.contains(word.getEntityType()))
							totalGold.increment(word.getEntityType());
						else
							totalGold.put(word.getEntityType(), 1);
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
		int traintotal = 0 , testtotal = 0;
//		System.out.println("\ntraining stat:");
//		for (int i = 0 ; trainiter.hasNext(); i++){
//			 trainiter.advance();
//			 System.out.println(trainiter.key()+ trainiter.value());
//			 traintotal += trainiter.value();
//		}
//		System.out.println(traintotal);
//		System.out.println("\ntest stat:");
//
//		for (int i = 0 ; testiter.hasNext(); i++){
//			 testiter.advance();
//			 System.out.println(testiter.key()+ testiter.value());
//			 testtotal += testiter.value();
//		}
//		System.out.println(testtotal);
	}
	
public void predictionsSentWordsStat( int foldId) throws Exception {
		
		TObjectIntHashMap<String> allTrainStat, allTestStat;
		allTrainStat = new TObjectIntHashMap<String>();
		allTestStat = new TObjectIntHashMap<String>();
		
		TObjectIntHashMap<String> correct, totalGold, totalPredict;
		correct = new TObjectIntHashMap<String>();
		totalGold = new TObjectIntHashMap<String>();
		totalPredict = new TObjectIntHashMap<String>();

		for (Document doc : documents) {
			// System.out.println(doc.getDocId());
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {
					List<Word> words = sent.getWords();
					for (Word word : words) {
						if (sent.getFolds()[foldId])
							allTrainStat.adjustOrPutValue(word.getEntityType(), 1, 1);
						else
							allTestStat.adjustOrPutValue(word.getEntityType(), 1, 1);
						if (sent.getFolds()[foldId])
							continue;
						if (word.getPrediction() == null)
							System.err.println("Not predicted word!");
						
						if (word.getEntityType().equals(word.getPrediction()))
							if (!correct.contains(word.getEntityType()))
								correct.put(word.getEntityType(), 1);
							else
								correct.increment(word.getEntityType());
						if (totalGold.contains(word.getEntityType()))
							totalGold.increment(word.getEntityType());
						else
							totalGold.put(word.getEntityType(), 1);
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
		int traintotal = 0 , testtotal = 0;
		System.out.println("training stat:");
		for (int i = 0 ; trainiter.hasNext(); i++){
			 trainiter.advance();
			 System.out.println(trainiter.key()+ trainiter.value());
			 traintotal += trainiter.value();
		}
		System.out.println(traintotal);
		System.out.println("test stat:");

		for (int i = 0 ; testiter.hasNext(); i++){
			 testiter.advance();
			 System.out.println(testiter.key()+ testiter.value());
			 testtotal += testiter.value();
		}
		System.out.println(testtotal);
	}
	
public void predictionsWordsStatPOS( int foldId) throws Exception {
		
		TObjectIntHashMap<String> allTrainStat, allTestStat;
		allTrainStat = new TObjectIntHashMap<String>();
		allTestStat = new TObjectIntHashMap<String>();
		
		TObjectIntHashMap<String> correct, totalGold, totalPredict;
		correct = new TObjectIntHashMap<String>();
		totalGold = new TObjectIntHashMap<String>();
		totalPredict = new TObjectIntHashMap<String>();

		for (Document doc : documents) {
			// System.out.println(doc.getDocId());
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
						
						if (word.getPOS().equals(word.getPrediction()))
							if (!correct.contains(word.getPOS()))
								correct.put(word.getPOS(), 1);
							else
								correct.increment(word.getPOS());
						if (totalGold.contains(word.getPOS()))
							totalGold.increment(word.getPOS());
						else
							totalGold.put(word.getPOS(), 1);
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
		int traintotal = 0 , testtotal = 0;
//		System.out.println("training stat:");
//		for (int i = 0 ; trainiter.hasNext(); i++){
//			 trainiter.advance();
//			 System.out.println(trainiter.key()+ trainiter.value());
//			 traintotal += trainiter.value();
//		}
//		System.out.println(traintotal);
//		System.out.println("test stat:");
//
//		for (int i = 0 ; testiter.hasNext(); i++){
//			 testiter.advance();
//			 System.out.println(testiter.key()+ testiter.value());
//			 testtotal += testiter.value();
//		}
//		System.out.println(testtotal);
	}

	public void prettyPrint() throws Exception {
		for (Document doc : documents) {
			System.out.println(doc.getDocId());

			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {
					System.out.println(sent.getPlainSentence());
					int i = 0;
					for (Word word : sent.getWords()) {
						System.out.println((i++) + " " + word + " " + word.getPOS());
					}
					List<NamedEntity> nes = sent.getNamedEntities();
					System.out.println("-----------\nNamed entities: ");
					for (NamedEntity ne : nes) {
						System.out.println(ne.getStart() + "-" + ne.getEnd() + " " + ne.getEntityType());
					}
				}
			}
		}

	}

	/**
	 * if fold ==Double.positiveInfinity, then all data is training. Else, split
	 * it.
	 * 
	 * @param fold
	 * @param output
	 * @throws Exception
	 */
	public void crossValidationRandomGenerator(double fold, String output) throws Exception {
		Random r = new Random();
		int ifold = (int)fold;
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
								rand[i] = rn > 1 / (double)ifold ? true : false;
								bw.write((i == 0 ? "" : "\t") + (rand[i] == true ? 1 : 0) + (i == ifold - 1 ? "\n" : ""));
							}
						}
						word.setBooleanFolds(rand);
					}
				}
			}
		}
		bw.close();
	}

	public void fillCVFolds(String string) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(string));
		String line="";
		for (Document doc : documents) {
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {
					for (Word word : sent.getWords()) {
						line=br.readLine();
						String[] toks = line.split("\t");
						boolean[] b = new boolean[toks.length];
						for (int i = 0 ; i < b.length ; i++)
							b[i] = Integer.parseInt(toks[i])==1?true:false;
						word.setBooleanFolds(b);
					}
				}
			}
		}
	}

public void predictionsWordsStatSentPOS( int foldId) throws Exception {
		
		TObjectIntHashMap<String> allTrainStat, allTestStat;
		allTrainStat = new TObjectIntHashMap<String>();
		allTestStat = new TObjectIntHashMap<String>();
		
		TObjectIntHashMap<String> correct, totalGold, totalPredict;
		correct = new TObjectIntHashMap<String>();
		totalGold = new TObjectIntHashMap<String>();
		totalPredict = new TObjectIntHashMap<String>();

		for (Document doc : documents) {
			// System.out.println(doc.getDocId());
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {
					if (sent.getFolds()[foldId])
						continue;
					
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
						
						if (word.getPOS().equals(word.getPrediction()))
							if (!correct.contains(word.getPOS()))
								correct.put(word.getPOS(), 1);
							else
								correct.increment(word.getPOS());
						if (totalGold.contains(word.getPOS()))
							totalGold.increment(word.getPOS());
						else
							totalGold.put(word.getPOS(), 1);
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
		int traintotal = 0 , testtotal = 0;
		System.out.println("training stat:");
		for (int i = 0 ; trainiter.hasNext(); i++){
			 trainiter.advance();
			 System.out.println(trainiter.key()+ trainiter.value());
			 traintotal += trainiter.value();
		}
		System.out.println(traintotal);
		System.out.println("test stat:");

		for (int i = 0 ; testiter.hasNext(); i++){
			 testiter.advance();
			 System.out.println(testiter.key()+ testiter.value());
			 testtotal += testiter.value();
		}
		System.out.println(testtotal);
	}


}

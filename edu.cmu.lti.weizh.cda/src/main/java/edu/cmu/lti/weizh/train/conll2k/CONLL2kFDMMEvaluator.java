package edu.cmu.lti.weizh.train.conll2k;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import edu.cmu.lti.weizh.data.CONLLFormatDataSet;
import edu.cmu.lti.weizh.data.DataFactory;
import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.feature.FCONST;
import edu.cmu.lti.weizh.feature.Feature;
import edu.cmu.lti.weizh.feature.Theta;
import edu.cmu.lti.weizh.train.AbstractFDMMEvaluator;

public class CONLL2kFDMMEvaluator extends AbstractFDMMEvaluator<CONLLFormatDataSet,CONLL2kFDMMTrainer, CONLL2kFDMMEvaluator> {

	/**
	 * This is for de-serialization convenience
	 */
	private static final long serialVersionUID = 1L;

	double alpha = 1E-4;
	double beta = 1E-5;
	double gamma = 1E-5;
	static final String TRANS_F_NAME = FCONST.n(FCONST.F_CHUNK, 1);

	CONLL2kFDMMEvaluator(CONLL2kFDMMTrainer trainer) {
		super(trainer);
		Theta.setTHETA_HEADERS(trainer.getThetaHeaders());
		Theta.setHEADER_DELIMITER(trainer.getThetaHeaderDelimiter());
		Theta.setVALUE_DELIMITER(trainer.getThetaValueDelimiter());

		Feature.setHEADER_DELIMITER(trainer.getFeatureHeaderDelimiter());
		Feature.setVALUE_DELIMITER(trainer.getFeatureValueDelimiter());

	}

	@Override
	public void evaluate(CONLLFormatDataSet d) {

		try {
			for (Sentence s : d.getDocuments().get(0).getParagraphs().get(0).getSentences()) {
				List<Word> words = s.getWords();
				List<Theta<String>> thetas = new ArrayList<Theta<String>>(words.size());
				List<String> labels = new ArrayList<String>(words.size());
				List<List<Feature<String>>> features = new ArrayList<List<Feature<String>>>(words.size());
				List<Feature<String>> transitions = new ArrayList<Feature<String>>();

				// System.out.println(s.getWords());
				for (int i = 0; i < words.size(); i++) {
					Word w = words.get(i);
					Theta<String> theta = new Theta<String>(w);
					thetas.add(theta);
					labels.add(w.getChunkType());
					List<Feature<String>> feats = new ArrayList<Feature<String>>(this.trainer.getFeatureHeaders().length);

					for (String fheader : this.trainer.getFeatureHeaders()) {
						Feature<String> f = new Feature<String>(fheader, s, i);
						feats.add(f);
					}
					features.add(feats);

					transitions.add(new Feature<String>(TRANS_F_NAME, s, i));
				}
				// for (int i = 0 ; i < features.size() ; i++) {
				// System.out.println(thetas.get(i));
				// System.out.println(transitions.get(i));
				// for (Feature f2 : features.get(i))
				// System.out.println(f2);
				// System.out.println();
				// }

				String[] predictions = this.trainer.getModel().viterbiDecodeFeature(thetas, features, transitions, alpha, beta,
						gamma, true);

				setPredictions(words, predictions);
			}
			printResults(d);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void printResults(CONLLFormatDataSet d) throws Exception {
		HashMap<String, Double> predictions = new HashMap<String, Double>();
		HashMap<String, Double> correctPredictions = new HashMap<String, Double>();
		HashMap<String, Double> totalCorrect = new HashMap<String, Double>();

		for (Sentence s : d.getDocuments().get(0).getParagraphs().get(0).getSentences()) {
			List<Word> words = s.getWords();
			for (Word w : words) {
				String type = w.getChunkType();
				String prediction = w.getPrediction();
				putToMap(predictions, prediction);
				if (type.equals(prediction))
					putToMap(correctPredictions, type);
				putToMap(totalCorrect, type);
			}
		}
		printMaps(totalCorrect, predictions, correctPredictions);
		printF1(totalCorrect, predictions, correctPredictions);
	}

	private void printF1(HashMap<String, Double> totalCorrect, HashMap<String, Double> predictions,
			HashMap<String, Double> correctPredictions) {
		double totalPred = getTotal(predictions);
		double totalGold = getTotal(totalCorrect);
		double totalCPred = getTotal(correctPredictions);

		System.out.println("System total recall: " + totalCPred / totalGold);
		System.out.println("System total precision: " + totalCPred / totalPred);
	}

	private double getTotal(HashMap<String, Double> predictions) {
		double t = 0;
		for (double f : predictions.values()) {
			t += f;
		}
		return t;
	}

	private void printMaps(HashMap<String, Double> totalCorrect, HashMap<String, Double> predictions,
			HashMap<String, Double> correctPredictions) {

		Iterator<Entry<String, Double>> iter = totalCorrect.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Double> t = iter.next();
			String k = t.getKey();
			double tot = t.getValue();
			System.out.print("For label " + k +"\t");
			System.out.print("Precision is " + getProb(predictions, correctPredictions, k)+"\t");
			System.out.println("Recall is " + getProb(totalCorrect, correctPredictions, k));
		}
	}

	private double getProb(HashMap<String, Double> predictions, HashMap<String, Double> correctPredictions, String k) {
		if (predictions.containsKey(k))
			if (correctPredictions.containsKey(k)) {
				return correctPredictions.get(k) / predictions.get(k);
			}
		return 0;
	}

	private void putToMap(HashMap<String, Double> m, String p) {

		if (m.containsKey(p))
			m.put(p, m.get(p) + 1);
		else
			m.put(p, 1.0);

	}

	private void setPredictions(List<Word> words, String[] predictions) {
		int i = 0;
		for (Word w : words) {
			try {
				w.setPrediction(predictions[i++]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * System total recall: 0.9019988602064293 System total precision:
	 * 0.9019988602064293 
	 * 
For label I-NP	Precision is 0.9177728098365237	Recall is 0.9138146911519198
For label I-ADVP	Precision is 0.3739130434782609	Recall is 0.48314606741573035
For label I-SBAR	Precision is 0.15	Recall is 0.75
For label B-ADJP	Precision is 0.6013513513513513	Recall is 0.6095890410958904
For label I-VP	Precision is 0.882938026013772	Recall is 0.872260015117158
For label I-ADJP	Precision is 0.5238095238095238	Recall is 0.39520958083832336
For label O	Precision is 0.9338735818476499	Recall is 0.9323624595469255
For label B-LST	Precision is 0.0	Recall is 0.0
For label B-PP	Precision is 0.9205607476635514	Recall is 0.9418000415713988
For label B-NP	Precision is 0.9138126009693053	Recall is 0.9107229109644179
For label I-LST	Precision is 0.0	Recall is 0.0
For label I-CONJP	Precision is 0.6666666666666666	Recall is 0.7692307692307693
For label B-INTJ	Precision is 0.15384615384615385	Recall is 1.0
For label B-PRT	Precision is 0.5862068965517241	Recall is 0.6415094339622641
For label B-VP	Precision is 0.8970713073005093	Recall is 0.907471017604122
For label I-PP	Precision is 0.44642857142857145	Recall is 0.5208333333333334
For label B-SBAR	Precision is 0.7813852813852814	Recall is 0.6747663551401869
For label B-ADVP	Precision is 0.6882821387940842	Recall is 0.6986143187066974
For label B-CONJP	Precision is 0.35294117647058826	Recall is 0.6666666666666666

System total recall: 0.9019988602064293
System total precision: 0.9019988602064293
	 * 
	 * @param arg
	 */
	public static void main(String arg[]) {
		CONLLFormatDataSet test2kData = DataFactory.getCONLL2kTest();
		CONLL2kFDMMTrainer CONLLtrainer = new CONLL2kFDMMTrainer().load("CONLL2kFDMMTrainer");
		CONLL2kFDMMEvaluator eval = new CONLL2kFDMMEvaluator(CONLLtrainer);
		eval.evaluate(test2kData);
	}

	@Override
	protected CONLL2kFDMMEvaluator self() {
		// TODO Auto-generated method stub
		return this;
	}
}

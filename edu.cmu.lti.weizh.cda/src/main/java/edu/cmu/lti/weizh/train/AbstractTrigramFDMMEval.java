package edu.cmu.lti.weizh.train;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import edu.cmu.lti.weizh.docmodel.AbstractDataSet;
import edu.cmu.lti.weizh.docmodel.Document;
import edu.cmu.lti.weizh.docmodel.Paragraph;
import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.feature.Feature;
import edu.cmu.lti.weizh.feature.Theta;

public abstract class AbstractTrigramFDMMEval<
FVTYPE,
D extends AbstractDataSet,
T extends AbstractTigramFDMMTrain<FVTYPE,T,D>
>
implements Evaluatable<D> {

	/**
	 * 
	 */
	protected T trainer;

	protected AbstractTrigramFDMMEval(T trainer) {
		this.trainer = trainer;
		if (trainer == null)
			throw new UnsupportedOperationException("Trainer should not be NULL. Otherwise Evaluator not found.");
		
		Theta.setTHETA_HEADERS(trainer.getThetaHeaders());
		Theta.setHEADER_DELIMITER(trainer.getThetaHeaderDelimiter());
		Theta.setVALUE_DELIMITER(trainer.getThetaValueDelimiter());

		Feature.setHEADER_DELIMITER(trainer.getFeatureHeaderDelimiter());
		Feature.setVALUE_DELIMITER(trainer.getFeatureValueDelimiter());
	}

	@Override
	public void evaluate(D d) {
		try {
			for (Document doc : d.getDocuments())
				for (Paragraph para : doc.getParagraphs())
					for (Sentence s : para.getSentences()) {
						List<Word> words = s.getWords();

						List<Theta<String>> thetas = new ArrayList<Theta<String>>(words.size());
						List<List<Feature<String>>> features = new ArrayList<List<Feature<String>>>(words.size());
						String[] goldLabels = new String[words.size()];

						for (int i = 0; i < words.size(); i++) {
							Word w = words.get(i);
							Theta<String> theta = new Theta<String>(w);
							thetas.add(theta);
							List<Feature<String>> feats = new ArrayList<Feature<String>>(trainer.getFeatureHeaders().length);
							for (String fheader : trainer.getFeatureHeaders())
								feats.add(new Feature<String>(fheader, s, i));
							features.add(feats);
							goldLabels[i] = (trainer.getGoldLabel(w));
						}

						String[] predictions = trainer.getModel().predict(thetas, features);
						setPredictions(words, predictions);
					}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			printResults(d);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setPredictions(List<Word> words, String[] predictions) {
		int i = 0;
		for (Word w : words) {
			try {
				w.setPrediction(predictions[i++]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void printResults(D d) throws Exception {
		HashMap<String, Double> predictions = new HashMap<String, Double>();
		HashMap<String, Double> correctPredictions = new HashMap<String, Double>();
		HashMap<String, Double> totalCorrect = new HashMap<String, Double>();

		for (Document doc : d.getDocuments())
			for (Paragraph para : doc.getParagraphs())
				for (Sentence s : para.getSentences()) {
			List<Word> words = s.getWords();
			for (Word w : words) {
				String type = trainer.getGoldLabel(w);
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
	
	private void putToMap(HashMap<String, Double> m, String p) {

		if (m.containsKey(p))
			m.put(p, m.get(p) + 1);
		else
			m.put(p, 1.0);

	}
	private void printMaps(HashMap<String, Double> totalCorrect, HashMap<String, Double> predictions,
			HashMap<String, Double> correctPredictions) {

		Iterator<Entry<String, Double>> iter = totalCorrect.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Double> t = iter.next();
			String k = t.getKey();
			System.out.print("For label " + k);
			System.out.print("\tPrecision is\t" + getProb(predictions, correctPredictions, k));
			System.out.println("\tRecall is\t" + getProb(totalCorrect, correctPredictions, k));
		}
	}
	
	private void printF1(HashMap<String, Double> totalCorrect, HashMap<String, Double> predictions,
			HashMap<String, Double> correctPredictions) {

		double totalPred = getTotalNATExcluded(predictions);
		double totalGold = getTotalNATExcluded(totalCorrect);
		double totalCPred = getTotalNATExcluded(correctPredictions);
		System.out.println(totalPred + " " + totalGold + " " + totalCPred);
		System.out.println("System total recall: " + totalCPred / totalGold);
		System.out.println("System total precision: " + totalCPred / totalPred);
		System.out.println("System total F1: " + 2 * totalCPred / (totalGold + totalPred));
	}
	
	private double getProb(HashMap<String, Double> predictions, HashMap<String, Double> correctPredictions, String k) {
		if (predictions.containsKey(k))
			if (correctPredictions.containsKey(k)) {
				return correctPredictions.get(k) / predictions.get(k);
			}
		return 0;
	}
	
	private double getTotalNATExcluded(HashMap<String, Double> predictions) {
		double t = 0;
		for (Entry<String, Double> e : predictions.entrySet()) {
			if (e.getKey().equalsIgnoreCase("o") || e.getKey().equalsIgnoreCase("[o]")) {
				continue;
			}
			t += e.getValue();
		}
		return t;
	}


}

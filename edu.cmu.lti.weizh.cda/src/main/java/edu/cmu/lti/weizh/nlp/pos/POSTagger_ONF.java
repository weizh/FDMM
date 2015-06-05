package edu.cmu.lti.weizh.nlp.pos;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.eval.Prediction;
import edu.cmu.lti.weizh.feature.Feature;
import edu.cmu.lti.weizh.feature.Theta;
import edu.cmu.lti.weizh.nlp.Tagger;
import edu.cmu.lti.weizh.train.ontonotes.ON_PercTrain_POS;

public class POSTagger_ONF implements Tagger {

	protected ON_PercTrain_POS percTrain;
	double iter;

	public POSTagger_ONF(String model) {
		percTrain = new ON_PercTrain_POS().load(model);
		iter = percTrain.getIterationsUsed() * percTrain.getTotalSentProcessed();
	}

	@Override
	public Sentence tag(Sentence s) throws Exception {
		List<Word> words = s.getWords();

		List<Theta<String>> thetas = new ArrayList<Theta<String>>(words.size());
		List<List<Feature<String>>> features = new ArrayList<List<Feature<String>>>(words.size());

		for (int i = 0; i < words.size(); i++) {
			Word w = words.get(i);
			Theta<String> theta = new Theta<String>(w);
			thetas.add(theta);
			List<Feature<String>> feats = new ArrayList<Feature<String>>(percTrain.getFeatureHeaders().length);
			for (String fheader : percTrain.getFeatureHeaders())
				feats.add(new Feature<String>(fheader, s, i));
			features.add(feats);
		}

		String[] predictions = percTrain.getModel().predictWithAverageParam(thetas, features, iter);
		setPredictionsAsString(words, predictions);

		return s;
	}

	private void setPredictionsAsString(List<Word> words, String[] predictions) {
		int i = 0;
		for (Word w : words) {
			try {
				w.setPrediction(predictions[i++]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Sentence tagWithLabelDist(Sentence s) throws Exception {
		List<Word> words = s.getWords();

		List<Theta<String>> thetas = new ArrayList<Theta<String>>(words.size());
		List<List<Feature<String>>> features = new ArrayList<List<Feature<String>>>(words.size());

		for (int i = 0; i < words.size(); i++) {
			Word w = words.get(i);
			Theta<String> theta = new Theta<String>(w);
			thetas.add(theta);
			List<Feature<String>> feats = new ArrayList<Feature<String>>(percTrain.getFeatureHeaders().length);
			for (String fheader : percTrain.getFeatureHeaders())
				feats.add(new Feature<String>(fheader, s, i));
			features.add(feats);
		}

		Prediction[] predictions = percTrain.getModel().predictWithAverageParamLabelDist(thetas, features, iter);
		setPredictions(words, predictions);

		return s;
	}

	private List<Word> setPredictions(List<Word> words, Prediction[] predictions) {
		for (int i = 0 ; i < words.size(); i++){
			words.get(i).setPDist(predictions[i]);
		}
		return words;
	}
}

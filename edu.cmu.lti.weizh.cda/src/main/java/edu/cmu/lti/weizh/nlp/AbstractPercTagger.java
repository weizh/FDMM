package edu.cmu.lti.weizh.nlp;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.weizh.docmodel.DataSet;
import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.eval.Prediction;
import edu.cmu.lti.weizh.feature.Feature;
import edu.cmu.lti.weizh.feature.Theta;
import edu.cmu.lti.weizh.train.AbstractPercTrain;


public abstract class AbstractPercTagger<M extends AbstractPercTrain<String, M, DataSet>> implements Tagger{

	protected M percTrain;
	protected double iter;
	

	public AbstractPercTagger(String model) {
		percTrain = getTrainerModel().load(model);
		iter = percTrain.getIterationsUsed() * percTrain.getTotalSentProcessed();
	}
	
	public abstract M getTrainerModel();
	
	/**
	 * This method set word string features, such as using setPartOfSpeech() to set String POS field.
	 */
	@Override
	public Sentence tag(Sentence s) throws Exception {

		Theta.setTHETA_HEADERS(percTrain.getThetaHeaders());
		Theta.setHEADER_DELIMITER(percTrain.getFeatureHeaderDelimiter());
		Theta.setVALUE_DELIMITER(percTrain.getThetaValueDelimiter());
		Feature.setFEATURE_HEADERS(percTrain.getFeatureHeaders());
		Feature.setHEADER_DELIMITER(percTrain.getFeatureHeaderDelimiter());
		Feature.setVALUE_DELIMITER(percTrain.getFeatureValueDelimiter());
		
		
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

		String[] predictions = percTrain.getModel().viterbiDecodeAvgParam(thetas, features, iter);
		setPredictionsAsString(words, predictions);

		return s;
	}

	/**
	 * This method set word string label distribution, which can be accessed with getPOSLabelDist() or getNERLabelDist().
	 * However, the String fields, such as String pos, or String NE is not set though.
	 */
	public Sentence tagWithLabelDistribution(Sentence s) throws Exception {
		
		Theta.setTHETA_HEADERS(percTrain.getThetaHeaders());
		Theta.setHEADER_DELIMITER(percTrain.getFeatureHeaderDelimiter());
		Theta.setVALUE_DELIMITER(percTrain.getThetaValueDelimiter());
		Feature.setFEATURE_HEADERS(percTrain.getFeatureHeaders());
		Feature.setHEADER_DELIMITER(percTrain.getFeatureHeaderDelimiter());
		Feature.setVALUE_DELIMITER(percTrain.getFeatureValueDelimiter());
		
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

		Prediction[] predictions = percTrain.getModel().maxProductAvgParam(thetas, features, iter);
		setPredictions(words, predictions);

		return s;
	}

	private void setPredictionsAsString(List<Word> words, String[] predictions) {
		int i = 0;
		for (Word w : words) {
			try {
				setWordLabel(w,predictions[i++]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private List<Word> setPredictions(List<Word> words, Prediction[] predictions) {
		for (int i = 0 ; i < words.size(); i++){
			setWordLabelDist(words.get(i),predictions[i]);
		}
		return words;
	}

	protected abstract void setWordLabel(Word w, String string) ;


	protected abstract void setWordLabelDist(Word word, Prediction prediction);

}

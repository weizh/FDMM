package edu.cmu.lti.weizh.train.__deprecated__;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.cmu.lti.weizh.docmodel.AbstractDataSet;
import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.feature.Feature;
import edu.cmu.lti.weizh.feature.Theta;
import edu.cmu.lti.weizh.io.Storable;
import edu.cmu.lti.weizh.mlmodel.TrigramFDMM;
import edu.cmu.lti.weizh.train.Trainable;

public abstract class AbstractTigramFDMMTrain<FVTYPE, T extends Trainable<TrigramFDMM, D>, D extends AbstractDataSet> extends
		Storable<T> implements Trainable<TrigramFDMM, D> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String[] thetaHeaders;
	protected String thetaHeaderDelimiter;
	protected String thetaValueDelimiter;

	protected String[] featureHeaders;
	protected String featureHeaderDelimiter;
	protected String featureValueDelimiter;

	protected TrigramFDMM trfdmm;

	public AbstractTigramFDMMTrain(String[] thetaHeaders, String thetaHeaderDelimiter, String thetaValueDelimiter,
			String[] featureHeaders, String featureHeaderDelimiter, String featureValueDelimiter) {

		this.trfdmm = new TrigramFDMM();

		this.thetaHeaders = thetaHeaders;
		this.thetaHeaderDelimiter = thetaHeaderDelimiter;
		this.thetaValueDelimiter = thetaValueDelimiter;

		Theta.setTHETA_HEADERS(thetaHeaders);
		Theta.setHEADER_DELIMITER(thetaHeaderDelimiter);
		Theta.setVALUE_DELIMITER(thetaValueDelimiter);

		this.featureHeaders = featureHeaders;
		this.featureHeaderDelimiter = featureHeaderDelimiter;
		this.featureValueDelimiter = featureValueDelimiter;

		Feature.setHEADER_DELIMITER(featureHeaderDelimiter);
		Feature.setVALUE_DELIMITER(featureValueDelimiter);

	}

	public AbstractTigramFDMMTrain() {
	}

	public TrigramFDMM getModel() {
		return trfdmm;
	}

	public String[] getFeatureHeaders() {
		return featureHeaders;
	}

	public String getFeatureHeaderDelimiter() {
		return featureHeaderDelimiter;
	}

	public String getFeatureValueDelimiter() {
		return featureValueDelimiter;
	}

	public String[] getThetaHeaders() {
		return thetaHeaders;
	}

	public String getThetaHeaderDelimiter() {
		return thetaHeaderDelimiter;
	}

	public String getThetaValueDelimiter() {
		return thetaValueDelimiter;
	}

	public double THRESHOLD;
	public int TotalNoSent;
	public HashSet<String> LabelSet;

	protected void setThreshold(double th) {
		this.THRESHOLD = th;
	}

	/**
	 *
	 * Train data d, using t and th implicitly set. Set T back into real # of
	 * iterations if stopped early due to threshold.
	 *
	 */
	@Override
	public void train(D d) throws Exception {
		LabelSet = getLabelSet(getSentences(d));
		trfdmm.setLabelSet(LabelSet);
		int si = 0;

		Date date = new Date(System.currentTimeMillis());
		System.out.println(date);
		
		for (Sentence s : getSentences(d)) {
			if (si % 1000 == 0)
				System.err.println(" Sentence " + (si++));
			List<Word> words = s.getWords();
			List<Theta<String>> thetas = new ArrayList<Theta<String>>(words.size());
			List<List<Feature<String>>> features = new ArrayList<List<Feature<String>>>(words.size());
			String[] goldLabels = new String[words.size()];
			for (int i = 0; i < words.size(); i++) {
				Word w = words.get(i);
				Theta<String> theta = new Theta<String>(w);
				thetas.add(theta);
				List<Feature<String>> feats = new ArrayList<Feature<String>>(featureHeaders.length);
				for (String fheader : featureHeaders)
					feats.add(new Feature<String>(fheader, s, i));
				features.add(feats);
				goldLabels[i] = (getGoldLabel(w));
			}
			trfdmm.countLearn( goldLabels, thetas, features);
		}
	}

	/**
	 * return the possible labels for each sentence.
	 * 
	 * @param sentences
	 * @return
	 */
	public HashSet<String> getLabelSet(List<Sentence> sentences) {
		LabelSet = new HashSet<String>();
		for (Sentence s : sentences) {
			List<Word> w = s.getWords();
			for (Word wd : w)
				LabelSet.add(getGoldLabel(wd));
		}
		return LabelSet;
	}

	/**
	 * return the sentence list of document d.
	 * 
	 * @param d
	 * @return
	 */
	protected abstract List<Sentence> getSentences(D d);

	/**
	 * This function has to be re-written when moving to a new data label set.
	 * 
	 * @param w
	 * @return
	 */
	protected abstract String getGoldLabel(Word w);

}
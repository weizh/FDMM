package edu.cmu.lti.weizh.train;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.cmu.lti.weizh.docmodel.AbstractDataSet;
import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.feature.Feature;
import edu.cmu.lti.weizh.feature.Theta;
import edu.cmu.lti.weizh.io.Storable;
import edu.cmu.lti.weizh.mlmodel.PerceptronFDMM;

public abstract class AbstractPercTrain
<
FVTYPE,
T extends Trainable<PerceptronFDMM, D>, 
D extends AbstractDataSet
> 
extends Storable<T> 
implements Trainable<PerceptronFDMM, D> {

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

	protected PerceptronFDMM pfdmm;

	public AbstractPercTrain(String[] thetaHeaders, String thetaHeaderDelimiter, String thetaValueDelimiter,
			String[] featureHeaders, String featureHeaderDelimiter, String featureValueDelimiter) {

		this.pfdmm = new PerceptronFDMM();

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

	public AbstractPercTrain() {
	}

	public PerceptronFDMM getModel() {
		return pfdmm;
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

	public int T;
	public double THRESHOLD;
	public int TotalNoSent;
	public HashSet<String> LabelSet;

	protected void setT(int t) {
		T = t;
	}
	protected void setThreshold(double th){
		this.THRESHOLD = th;
	}
	/**
	 * Train data d with threshold th for stopping, or iteration t, which ever stops the training first.
	 * @param d
	 * @param t
	 * @param th
	 * @throws Exception 
	 */
	public void train(D d, int t, double th) throws Exception{
		setT(t);
		setThreshold(th);
		train(d);
	}
	
	/**
	 *
	 * Train data d, using t and th implicitly set. Set T back into real # of iterations if stopped early due to threshold.
	 *
	 */
	@Override
	public void train(D d) throws Exception {
		LabelSet = getLabelSet(getSentences(d));
		pfdmm.setLabelSet(LabelSet);
		TotalNoSent = getSentences(d).size();
		double prevError = Double.NEGATIVE_INFINITY;
		int si = 0, t = 0, totalSents = 0;
		for (; t < T; t++) {
			si = 0;
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date(System.currentTimeMillis());
			System.err.println("Iteration " + t +"\t" + dateFormat.format(date));
			double totalNumOfLabels = 0;
			double totalDiff = 0;
			for (Sentence s : getSentences(d)) {
				totalSents++;
				if (si % 1000 == 0)
					System.err.println(" Sentence " + si);
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
				String[] predictions = pfdmm.predictWithAverageParam(thetas, features, totalSents);
				totalDiff += getDiff(predictions, goldLabels);
				totalNumOfLabels += predictions.length;
				pfdmm.perceptronLearn(predictions, goldLabels, thetas, features);
				si++;
			}
			double currentError = totalDiff / totalNumOfLabels;
			System.err.println("Previous iteration error rate: "+prevError + "\t Current Iteration error rate:" + currentError);
			if (prevError == Double.NEGATIVE_INFINITY)
				prevError = currentError;
			else if (Math.abs(prevError - currentError) < this.THRESHOLD)
				break;
			else
				prevError = currentError;
		}

		System.out.println(t + " iterations, each iteration processed " + totalSents +" sentences.");

		// set the t to be the actual num. of iterations
		this.T = t;
	}
	
	public int getIterationsUsed() {
		return T;
	}
	
	public int getTotalSentProcessed()	{
		return TotalNoSent;
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

	private double getDiff(String[] predictions, String[] goldLabels) {
		double diff = 0.0;
		for (int i = 0; i < predictions.length; i++) {
			if (predictions[i].equals(goldLabels[i]) == false)
				diff += 1;
		}
		return diff;
	}

}
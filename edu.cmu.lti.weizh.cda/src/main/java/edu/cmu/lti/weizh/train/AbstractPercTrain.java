package edu.cmu.lti.weizh.train;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.cmu.lti.weizh.docmodel.AbstractDataSet;
import edu.cmu.lti.weizh.docmodel.Document;
import edu.cmu.lti.weizh.docmodel.Paragraph;
import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.feature.Feature;
import edu.cmu.lti.weizh.feature.Theta;
import edu.cmu.lti.weizh.io.Storable;
import edu.cmu.lti.weizh.mlmodel.PerceptronFDMM;

public abstract class AbstractPercTrain<FVTYPE, T extends Trainable<PerceptronFDMM, D>, D extends AbstractDataSet> extends
		Storable<T> implements Trainable<PerceptronFDMM, D> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String[] thetaHeaders;

	protected String[] featureHeaders;

	protected PerceptronFDMM pfdmm;

	public AbstractPercTrain(String[] thetaHeaders, String[] featureHeaders) {

		this.pfdmm = new PerceptronFDMM();

		this.thetaHeaders = thetaHeaders;
		this.featureHeaders = featureHeaders;
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
		return Feature.getHEADER_DELIMITER();
	}

	public String getFeatureValueDelimiter() {
		return Feature.getVALUE_DELIMITER();
	}

	public String[] getThetaHeaders() {
		return thetaHeaders;
	}

	public String getThetaHeaderDelimiter() {
		return Theta.getHEADER_DELIMITER();
	}

	public String getThetaValueDelimiter() {
		return Theta.getVALUE_DELIMITER();
	}

	public int T;
	public double THRESHOLD;
	public int TotalNoSent;
	public HashSet<String> LabelSet;

	protected void setT(int t) {
		T = t;
	}

	protected void setThreshold(double th) {
		this.THRESHOLD = th;
	}

	/**
	 * Train data d with threshold th for stopping, or iteration t, which ever
	 * stops the training first.
	 * 
	 * @param d Dataset
	 * @param t Iterations
	 * @param th threshold
	 * @throws Exception
	 */
	public void train(D d, int t, double th) throws Exception {
		setT(t);
		setThreshold(th);
		train(d);
	}

	/**
	 *
	 * Train data d, using t and th implicitly set. Set T back into real # of
	 * iterations if stopped early due to threshold.
	 *
	 */
	@Override
	public void train(D d) throws Exception {
		
		Theta.setTHETA_HEADERS(thetaHeaders);
		
		LabelSet = getLabelSet(getSentences(d));
		pfdmm.setLabelSet(LabelSet);
		TotalNoSent = getSentences(d).size();
		double prevError = Double.NEGATIVE_INFINITY;
		int si = 0, t = 0, totalSents = 0;
		for (; t < T; t++) {
			si = 0;
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date(System.currentTimeMillis());
			// System.err.println("Iteration " + t +"\t" +
			// dateFormat.format(date));
			double totalNumOfLabels = 0;
			double totalDiff = 0;
			for (Sentence s : getSentences(d)) {
				totalSents++;
				// if (si % 1000 == 0)
				// System.err.println(" Sentence " + si);
				List<Word> words = s.getWords();
				List<Theta<String>> thetas = new ArrayList<Theta<String>>(words.size());
				List<List<Feature<String>>> features = new ArrayList<List<Feature<String>>>(words.size());
				String[] goldLabels = new String[words.size()];
				for (int i = 0; i < words.size(); i++) {
					Word w = words.get(i);
					Theta<String> theta = new Theta<String>(w);
					thetas.add(theta);
					List<Feature<String>> feats = new ArrayList<Feature<String>>(getFeatureHeaders().length);
					for (String fheader : getFeatureHeaders())
						feats.add(new Feature<String>(fheader, s, i));
					features.add(feats);
					goldLabels[i] = (getGoldLabel(w));
				}
				String[] predictions = pfdmm.viterbiDecodeAvgParam(thetas, features, totalSents);
				totalDiff += getDiff(predictions, goldLabels);
				totalNumOfLabels += predictions.length;
				pfdmm.perceptronLearn(predictions, goldLabels, thetas, features);
				si++;
			}
			double currentError = totalDiff / totalNumOfLabels;
			// System.err.println("Previous iteration error rate: "+prevError +
			// "\t Current Iteration error rate:" + currentError);
			if (prevError == Double.NEGATIVE_INFINITY)
				prevError = currentError;
			else if (Math.abs(prevError - currentError) < this.THRESHOLD)
				break;
			else
				prevError = currentError;
		}

		System.err.println("Ran " + (t + 1) + " iterations, total processed " + totalSents + " sentences.");

		// set the t to be the actual num. of iterations
		this.T = t;
	}

	public int getIterationsUsed() {
		return T;
	}

	public int getTotalSentProcessed() {
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
	public List<Sentence> getSentences(D d) {
		ArrayList<Sentence> ss = new ArrayList<Sentence>();
		try {
			for (Document doc : d.getDocuments())
				for (Paragraph para : doc.getParagraphs())
					for (Sentence sent : para.getSentences()) {
						ss.add(sent);
					}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ss;
	}

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
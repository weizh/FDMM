package edu.cmu.lti.weizh.mlmodel;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.cmu.lti.weizh.feature.FCONST;
import edu.cmu.lti.weizh.feature.Feature;
import edu.cmu.lti.weizh.feature.Theta;

/**
 * FDMM trigram model. This model is adapted from the Perc Learner.
 * 
 * @author Wei Zhang
 *
 * @param <FEATURETYPE>
 *            Right now the feature type is just String. Note that the type
 *            conversion in add() will force to convert FEATURETYPE for feature
 *            value into STRING so that the feature value can be stored into FID
 *            to be indexed.
 */
public class TrigramFDMM extends MLModel<String> {

	String modelname;

	private static final long serialVersionUID = 1L;
	/*
	 * Store the Distribution of each entity over labels.
	 */
	// to store current value for theta.
	HashMap<String, double[]> Thetas = new HashMap<String, double[]>();
	
	HashMap<String, HashMap<String, double[]>> Phis = new HashMap<String, HashMap<String, double[]>>();
	
	// to store current value of transitions. Initialized when label size is
	// get.
	double[][][] Transition;


	private HashSet<String> labelSet;
	private HashMap<String, Integer> labelIndex;

	double denom;

	private HashMap<Integer, String> id2Label;

	double alpha, beta, gamma;

	/**
	 * set alpha (THeta), beta (Feature) Gamma (Transition) smoothing values
	 * before decoding.
	 */
	public void setDecodingParam(double a, double b, double g) {
		alpha = a;
		beta = b;
		gamma = g;
	};

	public TrigramFDMM() {

	}

	public void countLearn(String[] goldLabels, List<Theta<String>> thetas,
			List<List<Feature<String>>> features) {

		for (int i = 0; i < goldLabels.length; i++) {
			modifyTheta(thetas.get(i), goldLabels[i]);
			modifyFeature(features.get(i), goldLabels[i]);
		}
		modifyTrans(goldLabels);
	}

	// ****************************************** Transition.
	private void modifyTrans(String[] goldTrans) {
		String p = FCONST.SENTSTART;
		String n = FCONST.SENTEND;
		if (goldTrans.length == 0)
			return;
		if (goldTrans.length > 0) {
			addOrAdjustTrans(p, p, goldTrans[0], 1);
		}
		if (goldTrans.length > 1) {
			addOrAdjustTrans(p, goldTrans[0], goldTrans[1], 1);
		}

		for (int i = 2; i < goldTrans.length; i++) {
			addOrAdjustTrans(goldTrans[i - 2], goldTrans[i - 1], goldTrans[i], 1);
		}

		int i = goldTrans.length;
		if (i == 1) {
			addOrAdjustTrans(p, goldTrans[i - 1], n, 1);
			addOrAdjustTrans(goldTrans[i - 1], n, n, 1);
		} else { // case i>1, since i will never be 0.
			addOrAdjustTrans(goldTrans[i - 2], goldTrans[i - 1], n, 1);
			addOrAdjustTrans(goldTrans[i - 1], n, n, 1);
		}
	}

	private void addOrAdjustTrans(String p, String c, String n, int i) {
		int pi = this.getTransLabelIndex(p);
		int ci = this.getTransLabelIndex(c);
		int ni = this.getTransLabelIndex(n);
		Transition[pi][ci][ni] += i;
	}

	int getTransLabelIndex(String s) {
		if (s.equals(FCONST.SENTSTART))
			return this.labelSet.size();
		else if (s.equals(FCONST.SENTEND))
			return this.labelSet.size() + 1;
		else
			return this.getLabelIndex(s);
	}

	// ****************************************** Feature.
	void modifyFeature(List<Feature<String>> list, String goldlabel) {
		for (Feature<String> f : list) {
			addOrAdjustSingleFeatureValue(f, goldlabel, 1);
		}
	}

	void addOrAdjustSingleFeatureValue(Feature<String> f, String label, int i) {
		String fname = f.getName();
		String fval = f.getValue();
		if (Phis.containsKey(fname) == false)
			Phis.put(fname, new HashMap<String, double[]>());
		HashMap<String, double[]> phi = Phis.get(fname);
		if (phi.containsKey(fval) == false)
			phi.put(fval, this.getNewEmptyLabelArray());
		double[] fvals = phi.get(fval);
		fvals[this.getLabelIndex(label)] += i;
	}


	// ****************************************** Theta.
	void modifyTheta(Theta<String> t, String goldlabel) {
		List<String> values = t.getValue();
		for (int i = 0; i < values.size(); i++) {
			addOrAdjustSingleThetaValue(values.get(i), goldlabel, 1);
		}
	}

	void addOrAdjustSingleThetaValue(String name, String label, double i) {
		if (Thetas.get(name) == null) {
			Thetas.put(name, this.getNewEmptyLabelArray());
		}
		double[] labelDist = Thetas.get(name);
		labelDist[getLabelIndex(label)] += i;
	}

	public String[] predict(List<Theta<String>> thetas, List<List<Feature<String>>> features) throws Exception {
		double d=1E-20;
		setDecodingParam(d,d,d);
		return viterbiDecode(thetas, features);
	}

	/**
	 * 
	 * @param _theta
	 * @param _features
	 * @param _transition
	 * @param alpha
	 *            prior for the label dist.
	 * @param beta
	 *            prior for the feature dist.
	 * @param gamma
	 *            prior for the start transition smoother
	 * @param advanced
	 * @return
	 * @throws Exception
	 */
	public String[] viterbiDecode(List<Theta<String>> thetas, List<List<Feature<String>>> features) throws Exception {
		if (thetas.size() == 0)
			throw new UnsupportedOperationException("Zero Length sentence. No need to viterbiDecode.");
		int size = labelSet.size();
		double[][] valueGrid = new double[thetas.size()][size * size];
		int[][] backtrackGrid = new int[thetas.size()][size * size];

		for (int i = 0; i < thetas.size(); i++)
			for (int j = 0; j < size * size; j++) {
				valueGrid[i][j] = Double.NaN;
				backtrackGrid[i][j] = Integer.MIN_VALUE;
			}

		for (int i = 0; i < thetas.size(); i++) {
			Theta<String> theta = thetas.get(i);
			List<Feature<String>> feature = features.get(i);
			addToGrid(i, thetas.size(), valueGrid, backtrackGrid, theta, feature);
		}
		addToGrid(thetas.size(), thetas.size(), valueGrid, backtrackGrid, null, null);
		return bestSequence(valueGrid, backtrackGrid);
	}

	private String[] bestSequence(double[][] valueGrid, int[][] bg) {
		// printGrids(valueGrid, bg);
		int wLen = valueGrid.length;
		String[] label = new String[wLen];
		int[] labelIndex = new int[wLen];
		for (int i = 0; i < labelIndex.length; i++)
			labelIndex[i] = Integer.MIN_VALUE;
		double maxFinalVal = Double.NEGATIVE_INFINITY;
		int maxNodeId = Integer.MIN_VALUE;
		for (int i = 0; i < valueGrid[0].length; i++) {
			if (valueGrid[wLen - 1][i] > maxFinalVal) {
				maxFinalVal = valueGrid[wLen - 1][i];
				maxNodeId = i;
			}
		}
		labelIndex[wLen - 1] = maxNodeId;
		for (int i = wLen - 1; i > 0; i--) {
			int bp = bg[i][labelIndex[i]];
			labelIndex[i - 1] = bp;
		}
		label[0] = this.id2Label.get(labelIndex[0]);
		for (int i = 1; i < labelIndex.length; i++) {
			label[i] = this.id2Label.get(labelIndex[i] % this.labelSet.size());
		}
		// printLabels(labelIndex);

		return label;
	}

	private void addToGrid(int i, int size, double[][] vg, int[][] bg, Theta<String> theta, List<Feature<String>> feature) {
		int labelSize = this.labelSet.size();
		int iStart = labelSize, iEnd = labelSize + 1;
		// edge cases
		if (i >= size) {
			if (size == 1) {
				for (int p = 0; p < labelSize; p++) {
					if (vg[i - 1][p] == Double.NaN)
						continue;
					vg[i - 1][p] += getTransitionValues(iStart, p, iEnd) + getTransitionValues(p, iEnd, iEnd);
				}
			} else {
				for (int c = 0; c < labelSize; c++) {// current
					double endVal = getTransitionValues(c, iEnd, iEnd);
					for (int p = 0; p < labelSize; p++) {// previous
						vg[i - 1][p * labelSize + c] += getTransitionValues(p, c, iEnd) + endVal;
					}
				}
			}
			return;
		} else if (i == 0) {// elements in layer zero are at the top of list.
							// previous node index is set to -1.
			for (String l : this.labelSet) {
				int c = this.getLabelIndex(l);
				double emit = getThetaValues(theta, l) + getFeatureValues(feature, l);
				double trans = getTransitionValues(labelSize, labelSize, c);
				double sum = emit + trans;
				vg[0][c] = sum;
				bg[0][c] = -1;
			}
			return;
		}

		// main cases ( 0< i < sentence size)
		if (i == 1) {

			int pp = labelSize;
			// just process layer i=1 (#0 is the first layer).
			for (String l : this.labelSet) { // current
				double emit = getThetaValues(theta, l) + getFeatureValues(feature, l);
				int c = this.getLabelIndex(l);
				for (int p = 0; p < labelSize; p++) {
					double acc = vg[0][p];
					vg[1][p * labelSize + c] = acc + emit + getTransitionValues(pp, p, c);
					bg[1][p * labelSize + c] = p;
				}
			}
		} else { // size
			for (String l : this.labelSet) {
				double emit = getThetaValues(theta, l) + getFeatureValues(feature, l);
				int c = this.getLabelIndex(l);
				for (int p = 0; p < labelSize; p++) {
					int ci = p * labelSize + c;
					for (int pp = 0; pp < labelSize; pp++) {
						int pi = pp * labelSize + p;
						double acc = vg[i - 1][pi];
						double trans = getTransitionValues(pp, p, c);
						double sum = emit + acc + trans;
						if (Double.isNaN(vg[i][ci])) {
							vg[i][ci] = sum;
							bg[i][ci] = pi;
						} else {
							if (sum > vg[i][ci]) {
								vg[i][ci] = sum;
								bg[i][ci] = pi;
							}
						}

					}
				}
			}
		}
	}

	private double getTransitionValues(int pp, int p, int c) {
		double sum = 0;
		sum = getSum(Transition[pp][p]);
		double prob = (Transition[pp][p][c] + gamma) / (sum + (double) Transition[pp][p].length * gamma);
		return Math.log(prob);
	}

	private Double getFeatureValues(List<Feature<String>> feature, String label) {
		if (feature == null)
			return 0.0;
		Double sum = 0.0;
		HashMap<String, HashMap<String, double[]>> phi = Phis;
		for (Feature<String> f : feature) {
			String fname = f.getName();
			String fval = f.getValue();
			HashMap<String, double[]> wordDist = phi.get(fname);
			if (wordDist.containsKey(fval) == false)
				continue;
			double[] labelDist = wordDist.get(fval);
			double tsum = getSum(labelDist);
			tsum = tsum + (double) (labelDist.length) * beta;
			sum += Math.log((labelDist[this.getLabelIndex(label)] + beta) / tsum);
		}
		return sum;
	}

	private double getThetaValues(Theta<String> theta, String label) {
		double sum = 0;

		for (int i = 0; i < Theta.getTHETA_HEADERS().length; i++) {
			if (Thetas.containsKey(theta.getValue().get(i))){
				double[] tdist = Thetas.get(theta.getValue().get(i));
				double tsum = getSum(tdist);
				double tprob = (tdist[getLabelIndex(label)]+alpha)/(tsum+(double)tdist.length*alpha);
				sum+=Math.log(tprob);
//				break;
			}
		}
		return sum;
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private double getSum(double[] tdist) {
		double sum = 0;
		for (int i = 0 ; i < tdist.length ; i ++){
			sum +=tdist[i];
		}
		return sum;
	}

	@Override
	public void store(String file) throws IOException {

	}

	/**
	 * Set labelset, labelindex, and transition matrix as well.
	 * 
	 * @param labelSet
	 */
	public void setLabelSet(HashSet<String> labelSet) {
		this.labelSet = labelSet;
		this.labelIndex = new HashMap<String, Integer>(labelSet.size());
		this.id2Label = new HashMap<Integer, String>(labelSet.size());
		int i = 0;
		for (String s : labelSet) {
			id2Label.put(i, s);
			labelIndex.put(s, i++);
		}
		int s = labelSet.size() + 2;
		this.Transition = new double[s][s][s];
		// this.TransitionSum = new double[s][s][s];
		for (int j = 0; j < s; j++)
			for (int k = 0; k < s; k++)
				for (int l = 0; l < s; l++) {
					Transition[j][k][l] = 0.0d;
					// TransitionSum[j][k][l] = 0.0d;
				}

	}

	int getLabelIndex(String s) {
		return labelIndex.get(s);
	}

	double[] getNewEmptyLabelArray() {
		double[] d = new double[labelSet.size()];
		for (int i = 0; i < d.length; i++)
			d[i] = 0.0d;
		return d;
	}
}

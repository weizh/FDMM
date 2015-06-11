package edu.cmu.lti.weizh.mlmodel;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import edu.cmu.lti.weizh.eval.Prediction;
import edu.cmu.lti.weizh.feature.FCONST;
import edu.cmu.lti.weizh.feature.Feature;
import edu.cmu.lti.weizh.feature.Theta;

/**
 * FDMM model
 * 
 * @author Wei Zhang
 *
 * @param <FEATURETYPE>
 *            Right now the feature type is just String. Note that the type
 *            conversion in add() will force to convert FEATURETYPE for feature
 *            value into STRING so that the feature value can be stored into FID
 *            to be indexed.
 */
public class PerceptronFDMM extends MLModel<String> {

	String modelname;

	private static final long serialVersionUID = 1L;
	/*
	 * Store the Distribution of each entity over labels.
	 */
	// to store current value for theta.
	HashMap<String, double[]> Thetas = new HashMap<String, double[]>();
	// to store sum of thetas.
	HashMap<String, double[]> ThetaSum = new HashMap<String, double[]>();

	// to store current value for phis.
	// Map<FeatureName, Map<FeatureValue, Double[] labelVals>>.
	HashMap<String, HashMap<String, double[]>> Phis = new HashMap<String, HashMap<String, double[]>>();
	// to store sum of phis.
	HashMap<String, HashMap<String, double[]>> PhiSum = new HashMap<String, HashMap<String, double[]>>();

	// to store current value of transitions. Initialized when label size is
	// get.
	double[][][] Transition;
	// to store sum of transitions.Initialized when label size is get.
	double[][][] TransitionSum;

	private HashSet<String> labelSet;
	private HashMap<String, Integer> labelIndex;

	double denom;

	private HashMap<Integer, String> id2Label;

	public PerceptronFDMM() {

	}

	public void perceptronLearn(String[] predictions, String[] goldLabels, List<Theta<String>> thetas,
			List<List<Feature<String>>> features) {

		for (int i = 0; i < goldLabels.length; i++) {
			modifyTheta(thetas.get(i), predictions[i], goldLabels[i]);
			modifyFeature(features.get(i), predictions[i], goldLabels[i]);
		}
		modifyTrans(goldLabels, predictions);
		updateThetaEOL();
		updateFeatureEOL();
		updateTransSumAtEndOfLoop();
	}

	// ****************************************** Transition.
	private void modifyTrans(String[] goldTrans, String[] predTrans) {
		String p = FCONST.SENTSTART;
		String n = FCONST.SENTEND;
		if (goldTrans.length == 0)
			return;
		if (goldTrans.length > 0) {
			addOrAdjustTrans(p, p, goldTrans[0], 1);
			addOrAdjustTrans(p, p, predTrans[0], -1);
		}
		if (goldTrans.length > 1) {
			addOrAdjustTrans(p, goldTrans[0], goldTrans[1], 1);
			addOrAdjustTrans(p, predTrans[0], predTrans[1], -1);
		}

		for (int i = 2; i < goldTrans.length; i++) {
			addOrAdjustTrans(goldTrans[i - 2], goldTrans[i - 1], goldTrans[i], 1);
			addOrAdjustTrans(predTrans[i - 2], predTrans[i - 1], predTrans[i], -1);
		}

		int i = goldTrans.length;
		if (i == 1) {
			addOrAdjustTrans(p, goldTrans[i - 1], n, 1);
			addOrAdjustTrans(p, predTrans[i - 1], n, -1);
			addOrAdjustTrans(goldTrans[i - 1], n, n, 1);
			addOrAdjustTrans(predTrans[i - 1], n, n, -1);
		} else { // case i>1, since i will never be 0.
			addOrAdjustTrans(goldTrans[i - 2], goldTrans[i - 1], n, 1);
			addOrAdjustTrans(predTrans[i - 2], predTrans[i - 1], n, -1);
			addOrAdjustTrans(goldTrans[i - 1], n, n, 1);
			addOrAdjustTrans(predTrans[i - 1], n, n, -1);
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

	private void updateTransSumAtEndOfLoop() {
		int size = this.labelSet.size() + 2;
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				for (int k = 0; k < size; k++)
					TransitionSum[i][j][k] += Transition[i][j][k];
	}

	// ****************************************** Feature.
	void modifyFeature(List<Feature<String>> list, String predlabel, String goldlabel) {
		for (Feature<String> f : list) {
			addOrAdjustSingleFeatureValue(f, goldlabel, 1);
			addOrAdjustSingleFeatureValue(f, predlabel, -1);
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

	void updateFeatureEOL() {
		for (Entry<String, HashMap<String, double[]>> en : Phis.entrySet()) {
			String sname = en.getKey();
			HashMap<String, double[]> sdist = en.getValue();
			if (PhiSum.containsKey(sname) == false)
				PhiSum.put(sname, new HashMap<String, double[]>());
			HashMap<String, double[]> ddist = PhiSum.get(sname);
			for (Entry<String, double[]> nn : sdist.entrySet()) {
				String ssval = nn.getKey();
				double[] ssdist = nn.getValue();
				if (ddist.containsKey(ssval) == false) {
					double[] dist = new double[ssdist.length];
					System.arraycopy(ssdist, 0, dist, 0, ssdist.length);
					ddist.put(ssval, dist);
				} else {
					double[] dddist = ddist.get(ssval);
					for (int i = 0; i < ssdist.length; i++) {
						dddist[i] += ssdist[i];
					}
				}
			}
		}
	}

	// ****************************************** Theta.
	void modifyTheta(Theta<String> t, String predlabel, String goldlabel) {
		List<String> values = t.getValue();
		for (int i = 0; i < values.size(); i++) {
			addOrAdjustSingleThetaValue(values.get(i), goldlabel, 1);
			addOrAdjustSingleThetaValue(values.get(i), predlabel, -1);
		}
	}

	void addOrAdjustSingleThetaValue(String name, String label, double i) {
		if (Thetas.get(name) == null) {
			Thetas.put(name, this.getNewEmptyLabelArray());
		}
		double[] labelDist = Thetas.get(name);
		labelDist[getLabelIndex(label)] += i;
	}

	void updateThetaEOL() {
		for (Entry<String, double[]> e : Thetas.entrySet()) {
			String name = e.getKey();
			double[] labels = e.getValue();
			if (ThetaSum.containsKey(name) == false) {
				double[] dist = new double[labels.length];
				System.arraycopy(labels, 0, dist, 0, labels.length);
				ThetaSum.put(name, dist);
			} else {
				double[] dist = ThetaSum.get(name);
				for (int i = 0; i < dist.length; i++)
					dist[i] += labels[i];
			}
		}
	}

	public String[] predict(List<Theta<String>> thetas, List<List<Feature<String>>> features) throws Exception {

		return viterbiDecode(thetas, features, false);
	}

	public String[] viterbiDecodeAvgParam(List<Theta<String>> thetas, List<List<Feature<String>>> features, double denom)
			throws Exception {
		this.denom = denom;
		return viterbiDecode(thetas, features, true);
	}

	public iNode[] viterbiDecodeAvgParam(List<Theta<String>> thetas, List<List<Feature<String>>> features, int denom, int n)
			throws Exception {
		this.denom = denom;
		return viterbiDecodeNBest(thetas, features, true);
	}

	public Prediction[] maxProductAvgParam(List<Theta<String>> thetas, List<List<Feature<String>>> features, double denom) {
		this.denom = denom;
		return fowardBackward(thetas, features);
	}

	private Prediction[] fowardBackward(List<Theta<String>> thetas, List<List<Feature<String>>> features) {
		double[][] fTable = genForwardTable(thetas, features);
		double[][] bTable = genBackwardTable(thetas, features);

		Prediction p[] = new Prediction[thetas.size()];
		for (int i = 0; i < p.length; i++) {
			String[] names = new String[labelIndex.size()];
			double[] vals = new double[labelIndex.size()];

			double[] sum = new double[labelIndex.size()];
			for (Entry<String, Integer> label : labelIndex.entrySet()) {
				String ls = label.getKey();
				int li = label.getValue();
				names[li] = ls;
				double logSum = getLogHistSum(fTable, bTable, i, li);
				vals[li] = logSum;
				sum[li] = logSum;
			}
			double logDenom = logSumOfExponentials(sum);
			for (int j = 0; j < vals.length; j++)
				vals[j] = Math.exp(vals[j] - logDenom);

			p[i] = new Prediction(names, vals);
		}
		return p;
	}

	private double getLogHistSum(double[][] fTable, double[][] bTable, int windex, int offset) {
		double[] tempp = new double[labelIndex.size()];
		for (int i = 0; i < labelIndex.size(); i++) {
			int index = i * labelIndex.size() + offset;
			if (Double.isNaN(fTable[windex][index]) == false && Double.isNaN(bTable[windex][index]) == false)
				tempp[i] = (fTable[windex][index] + bTable[windex][index]);
		}
		return logSumOfExponentials(tempp);
	}

	private double[][] genBackwardTable(List<Theta<String>> thetas, List<List<Feature<String>>> features) {
		int size = labelSet.size();
		double[][] valueGrid = new double[thetas.size()][size * size];
		for (int i = 0; i < thetas.size(); i++)
			for (int j = 0; j < size * size; j++) {
				valueGrid[i][j] = Double.NaN;
			}
		// TODO:
		for (int i = thetas.size() - 1; i > -1; i--) {
			Theta<String> theta = thetas.get(i);
			List<Feature<String>> feature = features.get(i);
			beta(i, thetas.size(), valueGrid, theta, feature, true);
		}

		return valueGrid;
	}

	private double[][] genForwardTable(List<Theta<String>> thetas, List<List<Feature<String>>> features) {
		int size = labelSet.size();
		double[][] valueGrid = new double[thetas.size()][size * size];
		for (int i = 0; i < thetas.size(); i++)
			for (int j = 0; j < size * size; j++) {
				valueGrid[i][j] = Double.NaN;
			}
		// TODO:
		for (int i = 0; i < thetas.size(); i++) {
			Theta<String> theta = thetas.get(i);
			List<Feature<String>> feature = features.get(i);
			alpha(i, thetas.size(), valueGrid, theta, feature, true);
		}
		return valueGrid;
	}

	private void beta(int i, int size, double[][] vg, Theta<String> theta, List<Feature<String>> feature, boolean isAvg) {

		if (i >= size || i < 0)
			return; // should not process last layer
		int labelSize = this.labelSet.size();
		int iStart = labelSize, iEnd = labelSize + 1;
		// edge cases
		if (i == size - 1) {// elements in layer zero are at the top of list.
							// previous node index is set to -1.
			for (String l : this.labelSet) {
				int c = this.getLabelIndex(l);
				double emit = getThetaValues(theta, l, isAvg) + getFeatureValues(feature, l, isAvg);
				double tail = getTransitionValues(c, iEnd, iEnd, isAvg);

				for (String pl : this.labelSet) {
					int p = this.getLabelIndex(pl);
					double trans = getTransitionValues(p, c, iEnd, isAvg);
					double sum = emit + trans + tail;
					vg[i][p * labelSize + c] = sum;
				}
			}
			return;
		}

		// main cases ( 0< i < sentence size)
		if (i == 0) {

			int pp = labelSize;
			// just process layer i=1 (#0 is the first layer).
			for (String l : this.labelSet) { // current
				int c = this.getLabelIndex(l);
				double sum = 0;
				double temp[] = new double[labelSize];
				int loopi = 0;
				for (String ns : this.labelSet) {
					int n = getLabelIndex(ns);
					double acc = vg[1][c * labelSet.size() + n];
					double trans = getTransitionValues(labelSet.size(), c, n, isAvg);
					double emit = getThetaValues(theta, ns, isAvg) + getFeatureValues(feature, ns, isAvg);
					temp[loopi++] = (acc + trans + emit);
				}
				vg[0][c] = logSumOfExponentials(temp);
			}
		} else { // size
			for (String ps : this.labelSet) {
				int p = this.getLabelIndex(ps);
				for (String cs : this.labelSet) {
					int c = this.getLabelIndex(cs);
					int ci = p * labelSize + c;
					double[] temp = new double[labelSize];
					int loopi = 0;
					for (String ns : this.labelSet) {
						int n = this.getLabelIndex(ns);
						double emit = getThetaValues(theta, ns, isAvg) + getFeatureValues(feature, ns, isAvg);
						double acc = vg[i + 1][c * labelSet.size() + n];
						double trans = getTransitionValues(p, c, n, isAvg);
						double sum = emit + acc + trans;
						temp[loopi] = sum;
					}
					vg[i][ci] = logSumOfExponentials(temp);
				}
			}
			// printGrids(vg,bg);
		}

	}

	private void alpha(int i, int size, double[][] vg, Theta<String> theta, List<Feature<String>> feature, boolean isAvg) {

		if (i >= size)
			return; // should not process last layer
		int labelSize = this.labelSet.size();
		int iStart = labelSize, iEnd = labelSize + 1;
		// edge cases
		if (i == 0) {// elements in layer zero are at the top of list.
						// previous node index is set to -1.
			for (String l : this.labelSet) {
				int c = this.getLabelIndex(l);
				double emit = getThetaValues(theta, l, isAvg) + getFeatureValues(feature, l, isAvg);
				double trans = getTransitionValues(labelSize, labelSize, c, isAvg);
				double sum = emit + trans;
				vg[0][c] = sum;
			}
			return;
		}

		// main cases ( 0< i < sentence size)
		if (i == 1) {

			int pp = labelSize;
			// just process layer i=1 (#0 is the first layer).
			for (String l : this.labelSet) { // current
				double emit = getThetaValues(theta, l, isAvg) + getFeatureValues(feature, l, isAvg);
				int c = this.getLabelIndex(l);
				for (int p = 0; p < labelSize; p++) {
					double acc = vg[0][p];
					vg[1][p * labelSize + c] = acc + emit + getTransitionValues(pp, p, c, isAvg);
				}
			}
		} else { // size
			for (String l : this.labelSet) {
				double emit = getThetaValues(theta, l, isAvg) + getFeatureValues(feature, l, isAvg);
				int c = this.getLabelIndex(l);
				for (int p = 0; p < labelSize; p++) {
					int ci = p * labelSize + c;
					double[] temp = new double[labelSize];
					for (int pp = 0; pp < labelSize; pp++) {
						int pi = pp * labelSize + p;
						double acc = vg[i - 1][pi];
						double trans = getTransitionValues(pp, p, c, isAvg);
						double sum = emit + acc + trans;
						temp[pp] = sum;
					}
					vg[i][ci] = logSumOfExponentials(temp) + emit;
				}
			}
			// printGrids(vg,bg);
		}
	}

	/**
	 * From https://github.com/jmacglashan/generalResearch/blob/master/src/
	 * generativemodel/LogSumExp.java Find the max term, and use that instead.
	 * 
	 * @param exponentialTerms
	 * @return
	 */
	public static double logSumOfExponentials(double[] exponentialTerms) {

		if (exponentialTerms.length == 0) {
			return Double.NEGATIVE_INFINITY;
		}

		double maxTerm = Double.NEGATIVE_INFINITY;
		for (double d : exponentialTerms) {
			if (d > maxTerm) {
				maxTerm = d;
			}
		}

		if (maxTerm == Double.NEGATIVE_INFINITY) {
			return Double.NEGATIVE_INFINITY;
		}

		double sum = 0.;
		for (double d : exponentialTerms) {
			sum += Math.exp(d - maxTerm);
		}

		return maxTerm + Math.log(sum);
	}

	/**
	 * A node in decoding grid.
	 * 
	 * @author wei
	 *
	 */
	class Node {
		int index;
		double[] logProb;
		Node[] nbest;

		/**
		 * size is the size of the backTrack list.
		 * 
		 * @param size
		 */
		Node(int size, int index) {
			this.index = index;
			logProb = new double[size];
			nbest = new Node[size];
			for (int i = 0; i < size; i++) {
				logProb[i] = Double.NaN;
				nbest[i] = null;
			}
		}
	}

	public class iNode implements Comparable<iNode> {
		public double prob;
		public String[] sequence;

		iNode(double logProb, String[] seq) {
			this.prob = logProb;
			this.sequence = seq;
		}

		@Override
		public int compareTo(iNode arg0) {
			if (prob < arg0.prob)
				return 1;
			if (prob - arg0.prob < 1E-100)
				return 0;
			return -1;
		}

		public String[] getSequence() {
			if (sequence == null)
				throw new UnsupportedOperationException("sequence must be set before get.");
			return sequence;
		}

	}

	private iNode[] viterbiDecodeNBest(List<Theta<String>> thetas, List<List<Feature<String>>> features, boolean isAverage) {

		if (thetas.size() == 0)
			throw new UnsupportedOperationException("Zero Length sentence. No need to viterbiDecode.");
		int size = labelSet.size();
		double[][] valueGrid = new double[thetas.size()][size * size];
		double[][] secvalueGrid = new double[thetas.size()][size * size];

		int[][] backtrackGrid = new int[thetas.size()][size * size];
		int[][] secbacktrackGrid = new int[thetas.size()][size * size];
		for (int i = 0; i < thetas.size(); i++)
			for (int j = 0; j < size * size; j++) {
				valueGrid[i][j] = Double.NaN;
				secvalueGrid[i][j] = Double.NaN;
				backtrackGrid[i][j] = Integer.MIN_VALUE;
				secbacktrackGrid[i][j] = Integer.MIN_VALUE;
			}

		for (int i = 0; i < thetas.size(); i++) {
			Theta<String> theta = thetas.get(i);
			List<Feature<String>> feature = features.get(i);
			addToNBestGrid(i, thetas.size(), valueGrid, secvalueGrid, backtrackGrid, secbacktrackGrid, theta, feature, isAverage);
		}
		addToNBestGrid(thetas.size(), thetas.size(), valueGrid, secvalueGrid, backtrackGrid, secbacktrackGrid, null, null,
				isAverage);
//		 printGrids(valueGrid,backtrackGrid);
//		 printGrids(secvalueGrid,secbacktrackGrid);
		 
		return NbestSequence(valueGrid, secvalueGrid, backtrackGrid, secbacktrackGrid);

	}

	private void addToNBestGrid(int i, int size, double[][] vg, double[][] svg, int[][] bg, int[][] sbg, Theta<String> theta,
			List<Feature<String>> feature, boolean isAvg) {
		int labelSize = this.labelSet.size();
		int iStart = labelSize, iEnd = labelSize + 1;
		// edge cases
		if (i >= size) {
			if (size == 1) {
				for (int p = 0; p < labelSize; p++) {
					if (Double.isNaN(vg[i - 1][p]))
						continue;
					double val = getTransitionValues(iStart, p, iEnd, isAvg) + getTransitionValues(p, iEnd, iEnd, isAvg);
					vg[i - 1][p] += val;
					svg[i - 1][p] += val;
				}
			} else {
				for (int c = 0; c < labelSize; c++) {// current
					double endVal = getTransitionValues(c, iEnd, iEnd, isAvg);
					for (int p = 0; p < labelSize; p++) {// previous
						double val = getTransitionValues(p, c, iEnd, isAvg) + endVal;
						vg[i - 1][p * labelSize + c] += val;
						svg[i - 1][p * labelSize + c] += val;
					}
				}
			}
			return;
		} else if (i == 0) {// elements in layer zero are at the top of list.
							// previous node index is set to -1.
			for (String l : this.labelSet) {
				int c = this.getLabelIndex(l);
				double emit = getThetaValues(theta, l, isAvg) + getFeatureValues(feature, l, isAvg);
				double trans = getTransitionValues(labelSize, labelSize, c, isAvg);
				double sum = emit + trans;
				vg[0][c] = sum;
				bg[0][c] = -1;
				svg[0][c] = sum;
				sbg[0][c] = -1;
			}
			return;
		}

		// main cases ( 0< i < sentence size)
		if (i == 1) {

			int pp = labelSize;
			// just process layer i=1 (#0 is the first layer).
			for (String l : this.labelSet) { // current
				double emit = getThetaValues(theta, l, isAvg) + getFeatureValues(feature, l, isAvg);
				int c = this.getLabelIndex(l);
				for (int p = 0; p < labelSize; p++) {
					double trans = getTransitionValues(pp, p, c, isAvg);
					;

					vg[1][p * labelSize + c] = vg[0][p] + emit + trans;
					bg[1][p * labelSize + c] = p;

					svg[1][p * labelSize + c] = svg[0][p] + emit + trans;
					sbg[1][p * labelSize + c] = p;
				}
			}
		} else { // size
			for (String l : this.labelSet) {
				double emit = getThetaValues(theta, l, isAvg) + getFeatureValues(feature, l, isAvg);
				int c = this.getLabelIndex(l);
				for (int p = 0; p < labelSize; p++) {
					int ci = p * labelSize + c;
					double maxSum = Double.NEGATIVE_INFINITY, secSum = Double.NEGATIVE_INFINITY;
					int maxPi = Integer.MIN_VALUE, secPi = Integer.MIN_VALUE;

					for (int pp = 0; pp < labelSize; pp++) {
						int pi = pp * labelSize + p;
						double acc = vg[i - 1][pi];
						double trans = getTransitionValues(pp, p, c, isAvg);
						double sum = emit + acc + trans;
						if (sum >= maxSum){
							secSum = maxSum; secPi = maxPi;
							maxSum = sum; maxPi = pi;
						}else{
							if (sum >=secSum){
								secSum = sum;
								secPi = pi;
							}
						}

					}
					vg[i][ci] = maxSum;
					bg[i][ci] = maxPi;
					svg[i][ci] = secSum;
					sbg[i][ci] = secPi;
				}
			}
		}
	}

	private iNode[] NbestSequence(double[][] vg, double[][] svg, int[][] bg, int[][] sbg) {

		int wLen = vg.length;
		String[] label = new String[wLen];
		String[] seclabel = new String[wLen];
		int[] labelIndex = new int[wLen];
		int[] seclabelIndex = new int[wLen];
		for (int i = 0; i < labelIndex.length; i++) {
			labelIndex[i] = Integer.MIN_VALUE;
			seclabelIndex[i] = Integer.MIN_VALUE;
		}
		double maxFinalVal = Double.NEGATIVE_INFINITY, secFinalVal = Double.NEGATIVE_INFINITY;
		int maxNodeId = Integer.MIN_VALUE, secNodeId = Integer.MIN_VALUE;

		for (int i = 0; i < vg[0].length; i++) {
			if (vg[wLen - 1][i] > maxFinalVal) {
				maxFinalVal = vg[wLen - 1][i];
				maxNodeId = i;
			}
			if (svg[wLen - 1][i] > secFinalVal) {
				secFinalVal = vg[wLen - 1][i];
				secNodeId = i;
			}
		}
		
		labelIndex[wLen - 1] = maxNodeId;
		seclabelIndex[wLen - 1] = secNodeId;
		for (int i = wLen - 1; i > 0; i--) {
			int bp = bg[i][labelIndex[i]];
			labelIndex[i - 1] = bp;
//			System.out.println("i " + i + " secLabelIndex " + seclabelIndex[i]);
			int sbp = sbg[i][seclabelIndex[i]];
			seclabelIndex[i - 1] = sbp;
		}
		label[0] = this.id2Label.get(labelIndex[0]);
		seclabel[0] = this.id2Label.get(seclabelIndex[0]);
		for (int i = 1; i < labelIndex.length; i++) {
			label[i] = this.id2Label.get(labelIndex[i] % this.labelSet.size());
			seclabel[i] = this.id2Label.get(seclabelIndex[i] % this.labelSet.size());
		}
		double maxprob = Math.exp(maxFinalVal);
		double secprob = Math.exp(secFinalVal);
		double sum = maxprob+secprob;
		iNode[] nodes = new iNode[2];
		nodes[0] = new iNode(maxFinalVal/sum, label);
		nodes[1] = new iNode(secFinalVal/sum, seclabel);
		return nodes;
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
	public String[] viterbiDecode(List<Theta<String>> thetas, List<List<Feature<String>>> features, boolean isAverage)
			throws Exception {
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
			addToGrid(i, thetas.size(), valueGrid, backtrackGrid, theta, feature, isAverage);
		}
		addToGrid(thetas.size(), thetas.size(), valueGrid, backtrackGrid, null, null, isAverage);
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

	private void addToGrid(int i, int size, double[][] vg, int[][] bg, Theta<String> theta, List<Feature<String>> feature,
			boolean isAvg) {
		int labelSize = this.labelSet.size();
		int iStart = labelSize, iEnd = labelSize + 1;
		// edge cases
		if (i >= size) {
			if (size == 1) {
				for (int p = 0; p < labelSize; p++) {
					if (Double.isNaN(vg[i - 1][p]))
						continue;
					vg[i - 1][p] += getTransitionValues(iStart, p, iEnd, isAvg) + getTransitionValues(p, iEnd, iEnd, isAvg);
				}
			} else {
				for (int c = 0; c < labelSize; c++) {// current
					double endVal = getTransitionValues(c, iEnd, iEnd, isAvg);
					for (int p = 0; p < labelSize; p++) {// previous
						vg[i - 1][p * labelSize + c] += getTransitionValues(p, c, iEnd, isAvg) + endVal;
					}
				}
			}
			return;
		} else if (i == 0) {// elements in layer zero are at the top of list.
							// previous node index is set to -1.
			for (String l : this.labelSet) {
				int c = this.getLabelIndex(l);
				double emit = getThetaValues(theta, l, isAvg) + getFeatureValues(feature, l, isAvg);
				double trans = getTransitionValues(labelSize, labelSize, c, isAvg);
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
				double emit = getThetaValues(theta, l, isAvg) + getFeatureValues(feature, l, isAvg);
				int c = this.getLabelIndex(l);
				for (int p = 0; p < labelSize; p++) {
					double acc = vg[0][p];
					vg[1][p * labelSize + c] = acc + emit + getTransitionValues(pp, p, c, isAvg);
					bg[1][p * labelSize + c] = p;
				}
			}
		} else { // size
			for (String l : this.labelSet) {
				double emit = getThetaValues(theta, l, isAvg) + getFeatureValues(feature, l, isAvg);
				int c = this.getLabelIndex(l);
				for (int p = 0; p < labelSize; p++) {
					int ci = p * labelSize + c;
					for (int pp = 0; pp < labelSize; pp++) {
						int pi = pp * labelSize + p;
						double acc = vg[i - 1][pi];
						double trans = getTransitionValues(pp, p, c, isAvg);
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
			// printGrids(vg,bg);
		}
	}

	private double getTransitionValues(int pp, int p, int c, boolean isAvg) {
		if (isAvg)
			return TransitionSum[pp][p][c] / this.denom;
		return Transition[pp][p][c];
	}

	private Double getFeatureValues(List<Feature<String>> feature, String label, boolean isAvg) {
		if (feature == null)
			return 0.0;
		Double sum = 0.0;
		if (isAvg) {
			HashMap<String, HashMap<String, double[]>> phi = PhiSum;

			for (Feature<String> f : feature) {
				String fname = f.getName();
				String fval = f.getValue();
				if (phi.containsKey(fname) && phi.get(fname).containsKey(fval))
					sum += phi.get(fname).get(fval)[this.getLabelIndex(label)] / this.denom;
			}
		} else {
			HashMap<String, HashMap<String, double[]>> phi = Phis;
			for (Feature<String> f : feature) {
				String fname = f.getName();
				String fval = f.getValue();
				if (phi.containsKey(fname) && phi.get(fname).containsKey(fval))
					sum += phi.get(fname).get(fval)[this.getLabelIndex(label)];
			}
		}
		return sum;
	}

	private double getThetaValues(Theta<String> theta, String label, boolean isAverage) {
		double sum = 0;
		if (isAverage) {
			HashMap<String, double[]> target = this.ThetaSum;
			for (int i = 0; i < Theta.getTHETA_HEADERS().length; i++) {
				if (target.containsKey(theta.getValue().get(i)))
					sum += target.get(theta.getValue().get(i))[this.getLabelIndex(label)] / this.denom;
			}
		} else {
			HashMap<String, double[]> target = this.Thetas;
			for (int i = 0; i < Theta.getTHETA_HEADERS().length; i++) {
				if (target.containsKey(theta.getValue().get(i)))
					sum += target.get(theta.getValue().get(i))[this.getLabelIndex(label)];
			}
		}
		return sum;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
		this.TransitionSum = new double[s][s][s];
		for (int j = 0; j < s; j++)
			for (int k = 0; k < s; k++)
				for (int l = 0; l < s; l++) {
					Transition[j][k][l] = 0.0d;
					TransitionSum[j][k][l] = 0.0d;
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

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void store(String file) throws IOException {

	}

	private void printLabels(int[] labelIndex) {
		for (int i = 0; i < labelIndex.length; i++) {
			print(i + ":" + labelIndex[i] + "\t");
		}
		println();
	}

	private void printGrids(double[][] valueGrid, int[][] bg) {
		System.out.println(valueGrid.length);
		System.out.println(valueGrid[0].length);
		print("Values:	\n");

		for (int i = 0; i < valueGrid.length; i++) {
			print("\nword " + i + "\n");
			for (int j = 0; j < this.labelSet.size(); j++)
				for (int k = 0; k < this.labelSet.size(); k++)
					print(j + ":" + k + ":" + valueGrid[i][j * this.labelSet.size() + k] + "\t");
		}
		print("\n");
		 println("BackTracker:");
		 for (int i = 0; i < valueGrid.length; i++) {
		 println("\nword "+ i);
		 for (int j = 0; j < this.labelSet.size(); j++)
		 for (int k = 0; k < this.labelSet.size(); k++)
		 print(j+":"+k+":"+bg[i][j*this.labelSet.size()+k] + "\t");
		 }
		println();

	}

	private void print(Object key) {
		System.out.print(key);
	}

	private void println() {
		System.out.println();
	}

	private void println(String s) {
		System.out.println(s);
	}

}

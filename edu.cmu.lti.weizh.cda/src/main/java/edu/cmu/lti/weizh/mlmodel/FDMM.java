package edu.cmu.lti.weizh.mlmodel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import edu.cmu.lti.weizh.docmodel.Document;
import edu.cmu.lti.weizh.train.FeatureConstants;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

public class FDMM extends MLModel {

	String modelname;

	private static final long serialVersionUID = 1L;

	/*
	 * Store dictionary. String to Id (int) map. Dic also includes Feature
	 * values for each token. and phi type id. entity type id.
	 */
	TObjectIntHashMap<String> DID, FID, LID;

	int did, fid, lid;
	public final static String TOTAL = "[ROW_TOTAL]";
	TIntObjectHashMap<String> LID2LString;
	/*
	 * Store the Distribution of each entity over labels. <EntityID <LabelID,
	 * FREQ>>
	 */
	TIntObjectHashMap<TIntIntHashMap> theta;

	/*
	 * Define the feature distribution over <phi_iID:<labelID<dicID,Freq>>> Here
	 * we use a specific String for denoting the sum. This string should be:
	 * [PHI_I_SUM]. The put of this string is in first time filling out the
	 * value.
	 */
	TIntObjectHashMap<TIntObjectHashMap<TIntIntHashMap>> phis;

	public FDMM() {
		this.DID = new TObjectIntHashMap<String>();
		this.LID = new TObjectIntHashMap<String>();
		this.LID2LString = new TIntObjectHashMap<String>();
		did = fid = lid = 0;
		DID.put(TOTAL, did++);// dummy variable for storing the sum.
		LID.put(TOTAL, lid++);

		this.FID = new TObjectIntHashMap<String>();

		this.theta = new TIntObjectHashMap<TIntIntHashMap>();
		this.phis = new TIntObjectHashMap<TIntObjectHashMap<TIntIntHashMap>>();
	}

	public void print() {
		System.out.println("DID");
		System.out.println(DID);
		System.out.println("LID");
		System.out.println(LID);
		System.out.println("FID");
		System.out.println(FID);
		System.out.println("THETA");
		System.out.println(theta);
		System.out.println("PHI");
		System.out.println(phis);
	}

	// total is added to feature distribution in <INTINThashMap> of
	// phi[feature][label],
	// and to <INTINTHashMap> for theta[word].
	public void add(String word, String label, String featureName, String featureValue) {

		if (!DID.containsKey(word))
			DID.put(word, did++);

		if (!LID.containsKey(label)) {
			LID.put(label, lid);
			LID2LString.put(lid++, label);
		}

		if (!FID.containsKey(featureName))
			FID.put(featureName, fid++);

		if (!DID.containsKey(featureValue))
			DID.put(featureValue, did++);

		int c = DID.get(word);
		int etype = LID.get(label);
		int fheader = FID.get(featureName);
		int fval = DID.get(featureValue);

		int tot = DID.get(TOTAL);

		// fill out theta
		if (theta.containsKey(c)) {
			TIntIntHashMap dDistribution = theta.get(c);

			// increment total number.
			dDistribution.increment(tot);
			dDistribution.adjustOrPutValue(etype, 1, 1);

		} else {
			TIntIntHashMap dDistribution = new TIntIntHashMap();
			dDistribution.put(etype, 1);
			dDistribution.put(tot, 1);
			theta.put(c, dDistribution);
		}

		// fill out the feature
		if (phis.containsKey(fheader)) {
			TIntObjectHashMap<TIntIntHashMap> phi_i = phis.get(fheader);

			if (phi_i.containsKey(etype)) {
				TIntIntHashMap wordDist = phi_i.get(etype);

				wordDist.adjustOrPutValue(fval, 1, 1);
				wordDist.increment(tot);

			} else {
				TIntIntHashMap wordDist = new TIntIntHashMap();
				wordDist.put(tot, 1);
				wordDist.put(fval, 1);
				phi_i.put(etype, wordDist);
			}
		} else {
			TIntIntHashMap wordDist = new TIntIntHashMap();
			wordDist.put(tot, 1);
			wordDist.put(fval, 1);
			TIntObjectHashMap<TIntIntHashMap> phi_i = new TIntObjectHashMap<TIntIntHashMap>();
			phi_i.put(etype, wordDist);
			phis.put(fheader, phi_i);
		}
	}

	public static void main(String args[]) throws IOException, ClassNotFoundException {
		FDMM model = FDMM.load("en.FDA_MLModel");

		System.out.println();

	}

	public static FDMM load(String file) throws IOException, ClassNotFoundException {
		FileInputStream in = new FileInputStream(file);
		ObjectInputStream ins = new ObjectInputStream(in);
		FDMM copy = (FDMM) ins.readObject();
		copy.modelname = file;
		in.close();
		ins.close();
		return copy;
	}

	@Override
	public void store(String file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(this);
		out.close();
	}

	public String predict(String[] thetaIndex, double alpha, double beta, String... args) throws Exception {
		if ((args.length & 0x01) != 0)
			throw new Exception(" Number of arguments for features should not be odd.");

		// if (DID.containsKey(ctok))
		TIntIntHashMap entitytheta = theta.get(DID.get(thetaIndex[0]));
		int et = 1;
		while (entitytheta == null) {

			entitytheta = theta.get(DID.get(thetaIndex[et++]));
		}

		int tsize = entitytheta.size() - 1;

		TIntIntIterator ei = entitytheta.iterator();
		int maxlabel = -1;
		double maxval = Double.NEGATIVE_INFINITY;

		while (ei.hasNext()) {
			ei.advance();
			int label = ei.key();
			if (label == (LID.get(TOTAL))) {
				continue;
			}
			int tnum = ei.value();
			int tdenom = entitytheta.get(LID.get(TOTAL));

			double tempval = ((tnum + alpha) / (tdenom + tsize * alpha));
			for (int i = 0; i < args.length; i += 2) {
				TIntIntHashMap tempphi = phis.get(FID.get(args[i])).get(label);
				int num = tempphi.get(DID.get(args[i + 1]));
				int denom = tempphi.get(DID.get(TOTAL));
				tempval *= (num + beta) / (denom + beta * (tempphi.size() - 1));
			}

			if (tempval > maxval) {
				maxval = tempval;
				maxlabel = label;
			}
		}
		if (maxlabel == -1)
			System.out.println(-1);
		return LID2LString.get(maxlabel);
	}

	// This is the second viterbi algorithm, without transition probabilities.
	// It should generate the same result as token by token does.
	public String[] viterbiDecode2(ArrayList<String[]> _theta, ArrayList<String[]> featuresArray,
			ArrayList<String[]> transitionArray, double alpha, double beta) {

		double logProb[][] = new double[_theta.size()][LID2LString.size() + 1];//
		int backtrack[][] = new int[_theta.size()][LID2LString.size() + 1];//

		for (int i = 0; i < logProb.length; i++) {//
			for (int j = 0; j < logProb[0].length; j++) {//
				logProb[i][j] = Double.NEGATIVE_INFINITY;//
				backtrack[i][j] = Integer.MIN_VALUE;//
			}//
		}//

		for (int wordi = 0; wordi < _theta.size(); wordi++) {
			String[] thetaIndex = _theta.get(wordi);
			// if (DID.containsKey(ctok))
			TIntIntHashMap entitytheta = null;
			int et = 0;
			while (entitytheta == null) {
				entitytheta = theta.get(DID.get(thetaIndex[et++]));
			}

			TIntObjectIterator<String> ei = LID2LString.iterator();
			while (ei.hasNext()) { // each state for word i
				ei.advance();
				int label = ei.key();
				if (label == (LID.get(FeatureConstants.SENTSTART)))
					continue;

				// emit
				int tnum = entitytheta.contains(label) ? entitytheta.get(label) : 0;
				int tdenom = entitytheta.get(DID.get(TOTAL));
				double emit = ((tnum + alpha) / (tdenom + (LID2LString.size() - 1) * alpha));
				double logEmit = Math.log(emit);

				// feature emit
				double logAccfeat = 0;
				String[] features = featuresArray.get(wordi);
				for (int i = 0; i < features.length; i += 2) {
					String fname = features[i];
					String fval = features[i + 1];
					TIntIntHashMap tempphi = phis.get(FID.get(fname)).get(label);
					int num = tempphi.get(DID.get(fval));
					int denom = tempphi.get(DID.get(TOTAL));
					logAccfeat = logAccfeat + Math.log((num + beta) / (denom + beta * (tempphi.size() - 1)));
				}

				// transition
				if (wordi == 0) {
					// System.out.println(LID.get(Global.SENTSTART));
					TIntIntHashMap fvalDist = phis.get(FID.get(transitionArray.get(wordi)[0])).get(LID.get(FeatureConstants.SENTSTART));
					int transNum = fvalDist.get(DID.get(ei.value()));
					int transDenom = fvalDist.get(LID.get(TOTAL));
					double trans = (transNum + beta) / (transDenom + beta * (LID2LString.size() - 1));
					// double trans = 1; // set the transition prob. to 1 for
					// all the states.
					logProb[wordi][label] = logEmit + logAccfeat + Math.log(trans);
					backtrack[wordi][label] = Integer.MIN_VALUE;
					continue;

				} else {
					double maxAccumPlusTransLogProb = Double.NEGATIVE_INFINITY;
					TIntObjectIterator<String> pei = LID2LString.iterator();
					int prevLabel = -1;
					while (pei.hasNext()) { // each state for word i
						pei.advance();
						int plabel = pei.key();
						if (plabel == (LID.get(FeatureConstants.SENTSTART)))
							continue;
						// accumulative of state j for prev word
						double prevAccum = logProb[wordi - 1][plabel];
						int transLabel = FID.get(transitionArray.get(wordi - 1)[0]);
						TIntIntHashMap fvalDist = phis.get(FID.get(transitionArray.get(wordi - 1)[0])).get(plabel);
						int transNum = fvalDist.get(DID.get(pei.value())); 
						int transDenom = fvalDist.get(DID.get(TOTAL));
						double trans = (transNum + beta) / (transDenom + beta * (fvalDist.size() - 1));
						double accumTimesTransLogProb = Math.log(trans) + prevAccum;
						if (maxAccumPlusTransLogProb < accumTimesTransLogProb) {
							maxAccumPlusTransLogProb = accumTimesTransLogProb;
							prevLabel = plabel;
						}
					}
					// the max prev state is found, so update the logProb and
					// backtrack matrices.
					backtrack[wordi][label] = prevLabel;
					logProb[wordi][label] = logEmit + logAccfeat + maxAccumPlusTransLogProb;
				}
			}
		}
		print(logProb, backtrack);
		return bestSequence(logProb, backtrack);
	}

	/**
	 * 
	 * @param _theta
	 * @param _features
	 * @param _transition
	 * @param alpha prior for the label dist.
	 * @param beta prior for the feature dist.
	 * @param gamma  prior for the start transition smoother
	 * @param advanced 
	 * @return
	 * @throws Exception
	 */
	public String[] viterbiDecode(ArrayList<String[]> _theta, ArrayList<String[]> _features, ArrayList<String[]> _transition,
			double alpha, double beta, double gamma, boolean advanced) throws Exception {

		double logProb[][] = new double[_theta.size()][LID2LString.size() + 1];//
		int backtrack[][] = new int[_theta.size()][LID2LString.size() + 1];//
		for (int i = 0; i < logProb.length; i++)
			for (int j = 0; j < logProb[0].length; j++) {//
				logProb[i][j] = Double.NEGATIVE_INFINITY;//
				backtrack[i][j] = Integer.MIN_VALUE;//
			}//

		for (int wordi = 0; wordi < _theta.size(); wordi++) { // for every word
																// in sentence
			// get the label distribution of word i (backoff is used.)
			TIntIntHashMap cLabelDist = null;
			int tindex = 0;
			while (cLabelDist == null)
				cLabelDist = theta.get(DID.get(_theta.get(wordi)[tindex++]));
			String[] featureValues = _features.get(wordi);
			String[] transValues = _transition.get(wordi);
			TIntObjectIterator<String> citer = LID2LString.iterator();
			while (citer.hasNext()) { // for every state of the current word
				citer.advance();
				int cLabelId = citer.key();
				if (cLabelId == LID.get(FeatureConstants.SENTSTART))
					continue;

				// 1 logP(c label | c word)
				double probLabel = (cLabelDist.get(cLabelId) + alpha)
						/ (cLabelDist.get(DID.get(TOTAL)) + alpha * (LID2LString.size() - 1));
				double logProbLabel = Math.log(probLabel);

				// 2 logP( features | c label)
				double logProbFeatures = 0;
				for (int f = 0; f < featureValues.length; f += 2) {
					String fname = featureValues[f];
					String fval = featureValues[f + 1];
					TIntObjectHashMap<TIntIntHashMap> phiDist = phis.get(FID.get(fname));
					TIntIntHashMap phi4cLabel = phiDist.get(cLabelId);
					double prob4F = (phi4cLabel.get(DID.get(fval)) + beta)
							/ ((phi4cLabel.size() - 1) * beta + phi4cLabel.get(DID.get(TOTAL)));
					logProbFeatures += Math.log(prob4F);
				}

				if (wordi == 0) {
					// add the transition prob. from sentence start to state of
					// current word
					int featurename = FID.get(transValues[0]);
					int prevState = LID.get(FeatureConstants.SENTSTART);
					int currentState = DID.get(citer.value());
					TIntIntHashMap startphis = phis.get(featurename).get(prevState);
					double transFromStart = (startphis.get(currentState) + gamma)
							/ ((LID2LString.size() - 1) * gamma + startphis.get(DID.get(TOTAL)));

					logProb[wordi][cLabelId] = logProbFeatures + (advanced ? logProbLabel : 0.0) + Math.log(transFromStart);
					backtrack[wordi][cLabelId] = Integer.MIN_VALUE;
					continue;
				}

				// 3 P(trans) * P( Accumulate of prev)
				double maxLogProb = Double.NEGATIVE_INFINITY;
				int maxPrevLabel = -1;

				TIntObjectIterator<String> piter = LID2LString.iterator();
				while (piter.hasNext()) { // for every previous word
					piter.advance();
					int pLabelId = piter.key();
					if (pLabelId == LID.get(FeatureConstants.SENTSTART))
						continue;
					// previous slot logProb + P( cLabel | pLabel) indexed by
					// feature type.
					double prevAccumLogProb = logProb[wordi - 1][pLabelId];
					// Transition probability. This only works for 1 previous
					// state feature.
					// Expanding to p2 state transition needs to create another
					// loop within.
					int featurename = FID.get(transValues[0]);
					int currentState = DID.get(citer.value());
					TIntIntHashMap fvalDist = phis.get(featurename).get(pLabelId);
					double transProb = (fvalDist.get(currentState) + gamma)
							/ (fvalDist.get(DID.get(TOTAL)) + gamma * (LID2LString.size() - 1));

					double AccumLogProb = prevAccumLogProb + Math.log(transProb);

					if (AccumLogProb > maxLogProb) {
						maxLogProb = AccumLogProb;
						maxPrevLabel = pLabelId;
					}
				}
				if (maxPrevLabel == -1) {
					throw new Exception("can not select a maximum candidate from previous words!");
				} else {
					logProb[wordi][cLabelId] = (advanced ? logProbLabel : 0) + logProbFeatures + maxLogProb;
					backtrack[wordi][cLabelId] = maxPrevLabel;
				}
			}// end of c states
		}// end of words

		// fill the transition probability from last word to sentence end.
		// last column only adds a transition prob for the previous accumu
		// probs.

		int maxPrevLabel = -1;
		double maxAccumuPlusLastTrans = Double.NEGATIVE_INFINITY;
		TIntObjectIterator<String> lastiter = LID2LString.iterator();
		while (lastiter.hasNext()) { // for every previous word
			lastiter.advance();
			if (lastiter.key() == LID.get(FeatureConstants.SENTSTART))
				continue;
			// prev accumulate log prob
			double prevAccumProb = logProb[logProb.length - 1][lastiter.key()];
			TIntIntHashMap transValDist = phis.get(FID.get(_transition.get(0)[0])).get(lastiter.key());
			double trans2Last = (transValDist.get(DID.get(lastiter.value())) + gamma)
					/ (transValDist.get(DID.get(TOTAL)) + gamma * (transValDist.size() - 1));
			double logAccumPlusTrans = Math.log(trans2Last) + prevAccumProb;
			if (logAccumPlusTrans > maxAccumuPlusLastTrans) {
				maxAccumuPlusLastTrans = logAccumPlusTrans;
				maxPrevLabel = lastiter.key();
			}
		}
		// print(logProb,backtrack);
		return bestSequence2( backtrack, maxPrevLabel);
	}

	// elonged logProb and backtrack matrix. Last column is the best choice and best probability.
	private String[] bestSequence2( int[][] backtrack, int maxlabel) {
		
		int[] backpath = new int[backtrack.length];
		backpath[backtrack.length - 1] = maxlabel;

		for (int i = backtrack.length - 1; i > 0; i--) {
			// System.out.println(i);
			backpath[i - 1] = backtrack[i][maxlabel];
			maxlabel = backpath[i - 1];
		}

		String[] finalLabels = new String[backtrack.length];
		for (int i = 0; i < backtrack.length; i++) {
			finalLabels[i] = LID2LString.get(backpath[i]);
		}
		return finalLabels;
	}
	
	private void print(double[][] logProb, int[][] backtrack) {
		for (int i = 0; i < logProb.length; i++) {
			for (int j = 0; j < logProb[0].length; j++) {
				System.out.print(logProb[i][j] + "\t");
			}
			System.out.println();
		}

		for (int i = 0; i < backtrack.length; i++) {
			for (int j = 0; j < backtrack[0].length; j++) {
				System.out.print(backtrack[i][j] + "\t");
			}
			System.out.println();
		}

	}

	private String[] bestSequence(double[][] logProb, int[][] backtrack) {

		double max = Double.NEGATIVE_INFINITY;
		int maxlabel = -1;
		for (int i = 0; i < logProb[0].length; i++) {
			if (logProb[logProb.length - 1][i] > max)
				maxlabel = i;
		}
		int[] backpath = new int[backtrack.length];
		backpath[backtrack.length - 1] = maxlabel;

		for (int i = backtrack.length - 1; i > 0; i--) {
			// System.out.println(i);
			backpath[i - 1] = backtrack[i][maxlabel];
			maxlabel = backpath[i - 1];
		}

		String[] finalLabels = new String[logProb.length];
		for (int i = 0; i < logProb.length; i++) {
			finalLabels[i] = LID2LString.get(backpath[i]);
		}
		return finalLabels;
	}

	public String[] TBTRecursiveDecode(ArrayList<String[]> theta2, ArrayList<String[]> features, double d, double e, boolean b,
			boolean c) throws Exception {
		String[] predictions = new String[theta2.size()];
		for (int i = theta2.size() - 1; i > -1; i--) {
			String[] feat = features.get(i);
			if (i == theta2.size() - 1) {
				String[] newfeat = new String[feat.length + 2];
				for (int j = 0; j < feat.length; j++)
					newfeat[j] = feat[j];
				newfeat[newfeat.length - 2] = FeatureConstants.N1TYPE;
				newfeat[newfeat.length - 1] = "[n1type]";
				predictions[i] = predict(theta2.get(i), d, e, newfeat);
			} else if (i == theta2.size() - 2) {
				String[] newfeat = new String[feat.length + 4];
				for (int j = 0; j < feat.length; j++)
					newfeat[j] = feat[j];
				newfeat[newfeat.length - 2] = FeatureConstants.N2TYPE;
				newfeat[newfeat.length - 1] = "[n1type]";
				newfeat[newfeat.length - 4] = FeatureConstants.N1TYPE;
				newfeat[newfeat.length - 3] = predictions[i + 1];
				predictions[i] = predict(theta2.get(i), d, e, newfeat);
			} else if (i == theta2.size() - 3) {
				String[] newfeat = new String[feat.length + 6];
				for (int j = 0; j < feat.length; j++)
					newfeat[j] = feat[j];
				newfeat[newfeat.length - 2] = FeatureConstants.N3TYPE;
				newfeat[newfeat.length - 1] = "[n1type]";
				newfeat[newfeat.length - 4] = FeatureConstants.N2TYPE;
				newfeat[newfeat.length - 3] = predictions[i + 2];
				newfeat[newfeat.length - 6] = FeatureConstants.N1TYPE;
				newfeat[newfeat.length - 5] = predictions[i + 1];
				predictions[i] = predict(theta2.get(i), d, e, newfeat);
			} else if (i < theta2.size() - 3) {
				String[] newfeat = new String[feat.length + 6];
				for (int j = 0; j < feat.length; j++)
					newfeat[j] = feat[j];
				newfeat[newfeat.length - 2] = FeatureConstants.N3TYPE;
				newfeat[newfeat.length - 1] = predictions[i + 3];
				newfeat[newfeat.length - 4] = FeatureConstants.N2TYPE;
				newfeat[newfeat.length - 3] = predictions[i + 2];
				newfeat[newfeat.length - 6] = FeatureConstants.N1TYPE;
				newfeat[newfeat.length - 5] = predictions[i + 1];
				predictions[i] = predict(theta2.get(i), d, e, newfeat);
			}
		}
		return predictions;
	}

	public void train(Document startData) {
		// TODO Auto-generated method stub
		
	}
}

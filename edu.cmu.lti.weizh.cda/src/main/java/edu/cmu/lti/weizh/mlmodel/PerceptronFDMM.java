package edu.cmu.lti.weizh.mlmodel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.cmu.lti.weizh.feature.FCONST;
import edu.cmu.lti.weizh.feature.Feature;
import edu.cmu.lti.weizh.feature.Theta;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

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
	 * Store dictionary. String to Id (int) map. Dic also includes Feature
	 * values for each token. and phi type id. entity type id.
	 */
	TObjectIntHashMap<String> DID;
	TObjectIntHashMap<String> FID;
	TObjectIntHashMap<String> LID;

	int did, fid, lid;
	public final static String TOTAL = "[ROW_TOTAL]";
	TIntObjectHashMap<String> LID2LString;
	/*
	 * Store the Distribution of each entity over labels. <EntityID <LabelID,
	 * FREQ>>
	 */
	TIntObjectHashMap<TIntIntHashMap> theta;
	TIntObjectHashMap<TIntIntHashMap> thetaSum;
	/*
	 * Define the feature distribution over <phi_iID:<labelID<dicID,Freq>>> Here
	 * we use a specific String for denoting the sum. This string should be:
	 * [PHI_I_SUM]. The put of this string is in first time filling out the
	 * value.
	 */
	TIntObjectHashMap<TIntObjectHashMap<TIntIntHashMap>> phis;
	TIntObjectHashMap<TIntObjectHashMap<TIntIntHashMap>> phisSum;

	
	double alpha, beta, gamma;
	
	public PerceptronFDMM() {
		this.DID = new TObjectIntHashMap<String>();
		this.FID = new TObjectIntHashMap<String>();
		this.LID = new TObjectIntHashMap<String>();
		this.LID2LString = new TIntObjectHashMap<String>();
		did = fid = lid = 0;
		DID.put(TOTAL, did++);// dummy variable for storing the sum.
		LID.put(TOTAL, lid++);

		this.theta = new TIntObjectHashMap<TIntIntHashMap>();
		this.phis = new TIntObjectHashMap<TIntObjectHashMap<TIntIntHashMap>>();
		
		setParam(1E-4, 1E-5,1E-5);
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

		/**
		 * Strong conversion from featureType to String to enable DID to store
		 * the feature.
		 */
		if (!DID.containsKey(featureValue))
			DID.put((String) featureValue, did++);

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

	public void add(String word, String label, Feature<String> feat) {
		if (feat==null || word==null || label==null || feat.getName()==null || feat.getValue()==null) return;
		add(word, label, feat.getName(), feat.getValue());
	}

	public void add(Theta<String> theta, String label, Feature<String> f) {
		if(theta==null) return;
		List<String> v = theta.getValue();
		for (String val : v) {
			add(val, label, f);
		}
	}

	public void add(Theta<String> theta, String label, List<Feature<String>> features) {
		if(features==null) return;
		for (Feature<String> f: features){
			add(theta,label,f);
		}
	}

	public static void main(String args[]) throws IOException, ClassNotFoundException {
		PerceptronFDMM model = PerceptronFDMM.load("en.FDA_MLModel");

		System.out.println();

	}

	public static PerceptronFDMM load(String file) throws IOException, ClassNotFoundException {
		FileInputStream in = new FileInputStream(file);
		ObjectInputStream ins = new ObjectInputStream(in);
		PerceptronFDMM copy = (PerceptronFDMM) ins.readObject();
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

	/**
	 * This is an interface to viterbiDecode() function using newly defined feature classes.
	 * @param thetas
	 * @param features
	 * @param transitions
	 * @param alpha
	 * @param beta
	 * @param gamma
	 * @param b
	 * @return
	 * @throws Exception
	 */
	public String[] viterbiDecodeFeature(List<Theta<String>> thetas, List<List<Feature<String>>> features,
			List<Feature<String>> transitions, double alpha, double beta, double gamma, boolean b) throws Exception {

		ArrayList<String[]> _theta = new ArrayList<String[]>(thetas.size());
		for (Theta<String> t : thetas){
			_theta.add(t.getValue().toArray(new String[]{}));
		}
		
		ArrayList<String[]> _features = new ArrayList<String[]>(features.size());
		for (List<Feature<String>> f : features){
			String[] e=new String[f.size()*2];
			int i = 0 ;
			for (Feature<String> nv:f){
				e[i++]=nv.getName();
				e[i++] = nv.getValue();
			}
			_features.add(e);
		}
		
		ArrayList<String[]> _transition = new ArrayList<String[]>(transitions.size());
		for (Feature<String> t:transitions){
			_transition.add(new String[]{t.getName(),t.getValue()});
		}
		
		return viterbiDecode(_theta,_features,_transition,alpha,beta,gamma,b);
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
				if (cLabelId == LID.get(FCONST.SENTSTART))
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
					int prevState = LID.get(FCONST.SENTSTART);
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
					if (pLabelId == LID.get(FCONST.SENTSTART))
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
			if (lastiter.key() == LID.get(FCONST.SENTSTART))
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
		return bestSequence(backtrack, maxPrevLabel);
	}

	// elonged logProb and backtrack matrix. Last column is the best choice and
	// best probability.
	private String[] bestSequence(int[][] backtrack, int maxlabel) {

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

	public void perceptronLearn(List<String> predictions, List<String> goldLabels, List<Theta<String>> thetas,
			List<List<Feature<String>>> features, List<String[]> goldTrans) {
		HashMap<String, Integer> rightFeatures = new HashMap<String, Integer>();
		HashMap<String, Integer> wrongFeatures = new HashMap<String, Integer>();
		
		for (int i = 0 ; i < goldLabels.size(); i++){
			
		}
	}

	public void setParam(double alpha, double beta, double gamma) {
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = gamma;	
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
	public void printParams() {
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

	public List<String> predict(List<Theta<String>> thetas, List<List<Feature<String>>> features) {
		
		return null;
	}
}

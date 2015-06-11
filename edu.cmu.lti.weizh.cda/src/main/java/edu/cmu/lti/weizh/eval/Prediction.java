package edu.cmu.lti.weizh.eval;

import java.util.HashMap;
import java.util.Map.Entry;

public class Prediction {

	HashMap<String, Double> convMap;
	public Prediction(int size){
		convMap = new HashMap<String,Double>(size);
	}
	
	public HashMap<String, Double> getMap(){
		return convMap;
	}
	public String getBestMapCand(){
		double max = Double.NEGATIVE_INFINITY;
		String mstr=null;
		for(Entry<String, Double> e : convMap.entrySet()){
			if (e.getValue() > max){
				max = e.getValue();
				mstr = e.getKey();
			}
		}
		return mstr;
	}
	
	String[] predNames;
	public String[] getPredNames() {
		return predNames;
	}

	public void setPredNames(String[] predNames) {
		this.predNames = predNames;
	}

	public double[] getPredValues() {
		return predValues;
	}

	public void setPredValues(double[] predValues) {
		this.predValues = predValues;
	}

	double[] predValues;

	String bestCandidateName;
	double bestCandidateProb = Double.NEGATIVE_INFINITY;

	public Prediction(String[] pnames, double[] pvals) {
		predNames = pnames;
		predValues = pvals;
		if (pnames == null || pvals == null)
			throw new NullPointerException("Prediction parameters can not be null.");
		if (pnames.length != pvals.length)
			throw new UnsupportedOperationException("name and val in Prediction initialization should be the same!");
		if (pnames.length == 0)
			throw new UnsupportedOperationException("Empty Prediction param Not supported.");
		for (int i = 0; i < pnames.length; i++) {
			if (pvals[i] > bestCandidateProb) {
				bestCandidateName = pnames[i];
				bestCandidateProb = pvals[i];
			}
		}
	}

	/**
	 * Best candidate name may not align with maxProduct results.
	 * @param string
	 */
	public String getBestCandidateName() {
		return this.bestCandidateName;
	}

	public double getBestCandidateProb() {
		return this.bestCandidateProb;
	}

	public double getEntropy() {
		double e = 0;
		for (int i = 0; i < predValues.length; i++) {
			e = e - predValues[i] * Math.log(predValues[i]);
		}
		return e;
	}

	/**
	 * Best candidate name may not align with maxProduct results.
	 * If it's set, it will be viterbi result; if it's not, it will be max product result.
	 * @param string
	 */
	public void setBestCandidateName(String string) {
		this.bestCandidateName =string;
		if ( predNames==null || predValues==null ) return;
		for (int i = 0 ; i < predNames.length ;i ++){
			if (predNames[i].equals(string))
				bestCandidateProb = predValues[i];
		}
		
	}
	
	
}

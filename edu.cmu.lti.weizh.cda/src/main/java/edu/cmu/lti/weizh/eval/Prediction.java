package edu.cmu.lti.weizh.eval;

public class Prediction {

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
}

package edu.cmu.lti.weizh.eval;

import java.util.ArrayList;

public class Prediction {

	ArrayList<String> predNames;
	ArrayList<Double> predValues;

	String bestCandidateName;
	double bestCandidateProb;
	public Prediction(ArrayList<String> predNames2, ArrayList<Double> predValues2,String bestCandidateName,double bestCandidateProb) {
		// TODO Auto-generated constructor stub
		predNames = predNames2;
		predValues = predValues2;
		this.bestCandidateName = bestCandidateName;
		this.bestCandidateProb = bestCandidateProb;
	}
	
	public String getBestCandidateName() throws Exception{
		if (bestCandidateName ==null) throw new Exception("No predicted candidate assigned.");
		return this.bestCandidateName;
	}
	
	public double getBestCandidateProb(){
		return this.bestCandidateProb;
	}

	public double getEntropy() {
		double e = 0;
		for (int i = 0; i < predValues.size(); i++) {
			e = e- predValues.get(i)*Math.log(predValues.get(i));
		}
		return e;
	}
}

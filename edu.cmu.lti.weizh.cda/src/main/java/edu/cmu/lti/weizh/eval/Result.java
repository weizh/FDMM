package edu.cmu.lti.weizh.eval;

import java.io.Serializable;

public class Result implements Serializable {

	public double tprecision;
	public double trecall;
	public double tf1;
	public double lprecision;
	public double lrecall;
	public double lf1;
	public double oprecision;
	public double orecall;
	public double of1;
	
	public double totalPrecision;
	public double totalRecall;
	public double totalF1;
	
	public int seedTrainCount;
	public int addedTrainCount;
	public int testInstanceCount;
	
}

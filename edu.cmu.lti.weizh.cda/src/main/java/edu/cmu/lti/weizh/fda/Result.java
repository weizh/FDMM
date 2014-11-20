package edu.cmu.lti.weizh.fda;

import java.io.Serializable;

public class Result implements Serializable {

	double tprecision;
	double trecall;
	double tf1;
	double lprecision;
	double lrecall;
	double lf1;
	double oprecision;
	double orecall;
	double of1;
	
	double totalPrecision;
	double totalRecall;
	double totalF1;
	
	int seedTrainCount;
	int addedTrainCount;
	int testInstanceCount;
	
}

package edu.cmu.lti.weizh.train.ontonotes;


import edu.cmu.lti.weizh.data.DATA_PATHS;
import edu.cmu.lti.weizh.data.DataFactory;
import edu.cmu.lti.weizh.data.ontonotes.OntonotesDataSet;
import edu.cmu.lti.weizh.train.AbstractPercEval;

public class ON_PercEval_POS extends AbstractPercEval<String,OntonotesDataSet, ON_PercTrain > {

	ON_PercEval_POS(ON_PercTrain trainer) {
		super(trainer);
	}

	public static void main(String arg[]) {
		
		
		ON_PercEval_POS cnn_evaluator = new ON_PercEval_POS(new ON_PercTrain().load("trainedModels/ONF_CNN_PERC_100_NegInf_basic.trainer"));
		cnn_evaluator.evaluate(DataFactory.getONFDataSet(DATA_PATHS.ONF_CNN_TEST,true));

	}

}

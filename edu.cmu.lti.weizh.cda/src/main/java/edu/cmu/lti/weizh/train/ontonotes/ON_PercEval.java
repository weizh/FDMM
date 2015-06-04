package edu.cmu.lti.weizh.train.ontonotes;


import edu.cmu.lti.weizh.data.DATA_PATHS;
import edu.cmu.lti.weizh.data.DataFactory;
import edu.cmu.lti.weizh.data.ontonotes.OntonotesDataSet;
import edu.cmu.lti.weizh.train.AbstractPercEval;

public class ON_PercEval extends AbstractPercEval<String,OntonotesDataSet, ON_PercTrain > {

	ON_PercEval(ON_PercTrain trainer) {
		super(trainer);
	}

	public static void main(String arg[]) {
		
		ON_PercEval abc_evaluator = new ON_PercEval(new ON_PercTrain().load("trainedModels/ONF_ABC_PERC_100_NegInf_basic.trainer"));
		abc_evaluator.evaluate(DataFactory.getONFDataSet(DATA_PATHS.ONF_ABC_TEST,true));
//		
//		ON_PercEval mnb_evaluator = new ON_PercEval(new ON_PercTrain().load("trainedModels/ONF_MNB_PERC_100_NegInf_basic.trainer"));
//		mnb_evaluator.evaluate(DataFactory.getONFDataSet(DATA_PATHS.ONF_MNB_TEST,true));
		
//		ON_PercEval cnn_evaluator = new ON_PercEval(new ON_PercTrain().load("trainedModels/ONF_CNN_PERC_100_NegInf_basic.trainer"));
//		cnn_evaluator.evaluate(DataFactory.getONFDataSet(DATA_PATHS.ONF_CNN_TEST,true));
//		
//		ON_PercEval nbc_evaluator = new ON_PercEval(new ON_PercTrain().load("trainedModels/ONF_NBC_PERC_100_NegInf_basic.trainer"));
//		nbc_evaluator.evaluate(DataFactory.getONFDataSet(DATA_PATHS.ONF_NBC_TEST,true));
		
//		ON_PercEval pri_evaluator = new ON_PercEval(new ON_PercTrain().load("trainedModels/ONF_PRI_PERC_100_NegInf_basic.trainer"));
//		pri_evaluator.evaluate(DataFactory.getONFDataSet(DATA_PATHS.ONF_PRI_TEST,true));
//		
//		ON_PercEval voa_evaluator = new ON_PercEval(new ON_PercTrain().load("trainedModels/ONF_VOA_PERC_100_NegInf_basic.trainer"));
//		voa_evaluator.evaluate(DataFactory.getONFDataSet(DATA_PATHS.ONF_VOA_TEST,true));
//		
	}
	
/**
 * iter 100, th Neg Inf. Basic features.
 * 
 * 
ABC

For label GPE	Precision is	0.6043956043956044	Recall is	0.8461538461538461
For label PERSON	Precision is	0.7678571428571429	Recall is	0.8037383177570093
For label MISC	Precision is	0.8297872340425532	Recall is	0.693950177935943
For label O	Precision is	0.9718010915706489	Recall is	0.9843366093366094
For label ORG	Precision is	1.0	Recall is	0.5263157894736842
468.0 510.0 366.0
System total recall: 0.7176470588235294
System total precision: 0.782051282051282
System total F1: 0.7484662576687117


MNB
For label GPE	Precision is	0.3968253968253968	Recall is	0.6410256410256411
For label PERSON	Precision is	0.7475728155339806	Recall is	0.77
For label MISC	Precision is	0.9262295081967213	Recall is	0.5594059405940595
For label O	Precision is	0.9747395833333333	Recall is	0.9928381962864722
For label ORG	Precision is	0.6	Recall is	0.35714285714285715
313.0 383.0 230.0
System total recall: 0.6005221932114883
System total precision: 0.7348242811501597
System total F1: 0.6609195402298851


CNN


NBC
For label GPE	Precision is	0.6363636363636364	Recall is	0.7777777777777778
For label PERSON	Precision is	0.6461538461538462	Recall is	0.65625
For label MISC	Precision is	0.6638297872340425	Recall is	0.5931558935361216
For label O	Precision is	0.9574961360123647	Recall is	0.9706227967097533
For label ORG	Precision is	0.9166666666666666	Recall is	0.7051282051282052
415.0 450.0 288.0
System total recall: 0.64
System total precision: 0.6939759036144578
System total F1: 0.6658959537572254


PRI


VOA


 */

}

package edu.cmu.lti.weizh.train.nlpba;


import edu.cmu.lti.weizh.data.DataFactory;
import edu.cmu.lti.weizh.docmodel.DataSet;
import edu.cmu.lti.weizh.train.AbstractPercEval;

public class NLPBA_PercEval extends AbstractPercEval<String,DataSet, NLPBA_PercTrain > {

	NLPBA_PercEval(NLPBA_PercTrain trainer) {
		super(trainer);
	}

	public static void main(String arg[]) {
		
		NLPBA_PercEval tester = new NLPBA_PercEval(new NLPBA_PercTrain().load("trainedModels/ONF_NLPBA_PERC_100_NegInf_basic.trainer"));
		tester.evaluate(DataFactory.getNLPBASampleTest());
		String[] thetaHeaders = tester.trainer.getThetaHeaders();
		for (String s : thetaHeaders) System.out.println(s);
		String[] FeatureHeaders = tester.trainer.getFeatureHeaders();
		for (String s : FeatureHeaders)System.out.println(s);
		NLPBA_PercEval evaluator = new NLPBA_PercEval(new NLPBA_PercTrain().load("trainedModels/ONF_NLPBA_PERC_100_NegInf_basic.trainer"));
		evaluator.evaluate(DataFactory.getNLPBAEval());
		
	}

	/**
	 * Evaluation
	 * 
HT_WORD
HT_LEMMA
HT_WORDFORM-thd-HT_SUFFIX
HT_PREFIX-thd-HT_SUFFIX
HT_POS-thd-HT_SUFFIX
HT_POS
HT_WORDFORM
HT_SUFFIX

FT_POS@P2-fhd-FT_POS@P1
FT_POS@P1-fhd-FT_POS@N1
FT_POS@N1-fhd-FT_POS@N2
HT_WORDFORM@P2-fhd-HT_WORDFORM@P1
HT_WORDFORM@P1-fhd-HT_WORDFORM@N1
HT_WORDFORM@N1-fhd-HT_WORDFORM@N2
FT_LEMMA@P1
FT_LEMMA@P2
FT_LEMMA@N1
FT_LEMMA@N2

Evaluation with basic (without context POS, no POS actually. So POS features above does not count.)

For label I-cell_type	Precision is	0.7818181818181819	Recall is	0.5
For label B-cell_line	Precision is	0.15384615384615385	Recall is	1.0
For label I-RNA	Precision is	0.6666666666666666	Recall is	0.15384615384615385
For label I-DNA	Precision is	0.4090909090909091	Recall is	1.0
For label B-RNA	Precision is	0.0	Recall is	0.0
For label B-DNA	Precision is	0.3333333333333333	Recall is	0.5
For label B-cell_type	Precision is	0.6904761904761905	Recall is	0.5178571428571429
For label O	Precision is	0.9482612383375743	Recall is	0.9642087106511428
For label B-protein	Precision is	0.6	Recall is	0.8225806451612904
For label I-protein	Precision is	0.8282828282828283	Recall is	0.543046357615894
For label I-cell_line	Precision is	0.25	Recall is	0.8333333333333334
442.0 481.0 279.0
System total recall: 0.58004158004158
System total precision: 0.6312217194570136
System total F1: 0.6045503791982665

For label I-cell_type	Precision is	0.8608490566037735	Recall is	0.6101638248077565
For label B-cell_line	Precision is	0.36332958380202474	Recall is	0.646
For label I-RNA	Precision is	0.8347826086956521	Recall is	0.5133689839572193
For label I-DNA	Precision is	0.8169611307420495	Recall is	0.6461710452766909
For label B-RNA	Precision is	0.7473684210526316	Recall is	0.6016949152542372
For label B-DNA	Precision is	0.7424058323207776	Recall is	0.5785984848484849
For label B-cell_type	Precision is	0.76184122748499	Recall is	0.5944820406038521
For label O	Precision is	0.9580345382701595	Recall is	0.9621296557130085
For label B-protein	Precision is	0.6838171388840605	Recall is	0.7763962897177817
For label I-protein	Precision is	0.7178398058252428	Recall is	0.7434017595307918
For label I-cell_line	Precision is	0.4920863309352518	Recall is	0.6916076845298281
19043.0 19392.0 13391.0
System total recall: 0.6905424917491749
System total precision: 0.7031980255211889
System total F1: 0.6968128008325745





	 */
}

package edu.cmu.lti.weizh.train.ontonotes;


import edu.cmu.lti.weizh.data.DATA_PATHS;
import edu.cmu.lti.weizh.data.DataFactory;
import edu.cmu.lti.weizh.data.ontonotes.OntonotesDataSet;
import edu.cmu.lti.weizh.train.AbstractPercEval;

public class ON_PercEval_NER extends AbstractPercEval<String,OntonotesDataSet, ON_PercTrain_NER > {

	ON_PercEval_NER(ON_PercTrain_NER trainer) {
		super(trainer);
	}

	public static void main(String arg[]) {
		
//		ON_PercEval abc_evaluator = new ON_PercEval(new ON_PercTrain().load("trainedModels/ONF_ABC_PERC_100_NegInf_basic.trainer"));
//		abc_evaluator.evaluate(DataFactory.getONFDataSet(DATA_PATHS.ONF_ABC_TEST,true));
		
//		ON_PercEval mnb_evaluator = new ON_PercEval(new ON_PercTrain().load("trainedModels/ONF_MNB_PERC_100_NegInf_basic.trainer"));
//		mnb_evaluator.evaluate(DataFactory.getONFDataSet(DATA_PATHS.ONF_MNB_TEST,true));
		
//		ON_PercEval cnn_evaluator = new ON_PercEval(new ON_PercTrain().load("trainedModels/ONF_CNN_PERC_100_NegInf_basic.trainer"));
//		cnn_evaluator.evaluate(DataFactory.getONFDataSet(DATA_PATHS.ONF_CNN_TEST,true));
		
//		ON_PercEval nbc_evaluator = new ON_PercEval(new ON_PercTrain().load("trainedModels/ONF_NBC_PERC_100_NegInf_basic.trainer"));
//		nbc_evaluator.evaluate(DataFactory.getONFDataSet(DATA_PATHS.ONF_NBC_TEST,true));
		
//		ON_PercEval pri_evaluator = new ON_PercEval(new ON_PercTrain().load("trainedModels/ONF_PRI_PERC_100_NegInf_basic.trainer"));
//		pri_evaluator.evaluate(DataFactory.getONFDataSet(DATA_PATHS.ONF_PRI_TEST,true));
		
		ON_PercEval_NER voa_evaluator = new ON_PercEval_NER(new ON_PercTrain_NER().load("trainedModels/ONF_VOATrain_NER_PERC_100_NegInf_basic.trainer"));
		voa_evaluator.useMaxProductDecoding();
		voa_evaluator.evaluate(DataFactory.getONFDataSet(DATA_PATHS.ONF_VOA_TEST,true));
		
	}

	
	
// compare the following to Finkel et al 2009, 2010 and Tkachenko et al. '12.
	
/**
 * iter 100, th Neg Inf. 
 * first line:(Basic features minus wordforms).
 * second line: (basic features.
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

For label GPE	Precision is	0.6626506024096386	Recall is	0.8461538461538461
For label PERSON	Precision is	0.744	Recall is	0.8691588785046729
For label MISC	Precision is	0.8532110091743119	Recall is	0.6619217081850534
For label O	Precision is	0.9738839963559065	Recall is	0.98495085995086
For label ORG	Precision is	0.723404255319149	Recall is	0.5964912280701754
473.0 510.0 368.0
System total recall: 0.7215686274509804
System total precision: 0.7780126849894292
System total F1: 0.7487283825025433

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

For label GPE	Precision is	0.5918367346938775	Recall is	0.7435897435897436
For label PERSON	Precision is	0.6666666666666666	Recall is	0.84
For label MISC	Precision is	0.8714285714285714	Recall is	0.6039603960396039
For label O	Precision is	0.97854526425955	Recall is	0.9920424403183024
For label ORG	Precision is	0.6875	Recall is	0.2619047619047619
331.0 383.0 246.0
System total recall: 0.6422976501305483
System total precision: 0.743202416918429
System total F1: 0.6890756302521008

CNN
For label GPE	Precision is	0.896551724137931	Recall is	0.9105058365758755
For label PERSON	Precision is	0.7475149105367793	Recall is	0.8120950323974082
For label MISC	Precision is	0.7943585077343039	Recall is	0.7678100263852242
For label O	Precision is	0.9854253756637668	Recall is	0.9835917676910065
For label ORG	Precision is	0.6631299734748011	Recall is	0.7142857142857143
2240.0 2207.0 1733.0
System total recall: 0.7852288173991844
System total precision: 0.7736607142857143
System total F1: 0.7794018439397347

For label GPE	Precision is	0.8357142857142857	Recall is	0.9105058365758755
For label PERSON	Precision is	0.709349593495935	Recall is	0.7537796976241901
For label MISC	Precision is	0.7609075043630017	Recall is	0.7669305189094108
For label O	Precision is	0.985306583780729	Recall is	0.9830842965886665
For label ORG	Precision is	0.7082066869300911	Recall is	0.6657142857142857
2247.0 2207.0 1688.0
System total recall: 0.7648391481649298
System total precision: 0.7512238540275924
System total F1: 0.7579703637180063

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

For label GPE	Precision is	0.725	Recall is	0.6444444444444445
For label PERSON	Precision is	0.7115384615384616	Recall is	0.578125
For label MISC	Precision is	0.7740384615384616	Recall is	0.6121673003802282
For label O	Precision is	0.958047292143402	Recall is	0.9839404622013318
For label ORG	Precision is	0.7160493827160493	Recall is	0.7435897435897436
381.0 450.0 285.0
System total recall: 0.6333333333333333
System total precision: 0.7480314960629921
System total F1: 0.6859205776173285

PRI
For label GPE	Precision is	0.8450704225352113	Recall is	0.8780487804878049
For label PERSON	Precision is	0.8963963963963963	Recall is	0.8291666666666667
For label MISC	Precision is	0.8273684210526315	Recall is	0.7387218045112782
For label O	Precision is	0.9790794979079498	Recall is	0.9884180406049871
For label ORG	Precision is	0.75	Recall is	0.7358490566037735
1066.0 1136.0 889.0
System total recall: 0.7825704225352113
System total precision: 0.8339587242026266
System total F1: 0.8074477747502271

For label GPE	Precision is	0.8	Recall is	0.8780487804878049
For label PERSON	Precision is	0.8423423423423423	Recall is	0.7791666666666667
For label MISC	Precision is	0.8210735586481114	Recall is	0.7763157894736842
For label O	Precision is	0.9859749455337691	Recall is	0.9866466821092792
For label ORG	Precision is	0.7458563535911602	Recall is	0.8490566037735849
1131.0 1136.0 915.0
System total recall: 0.8054577464788732
System total precision: 0.8090185676392573
System total F1: 0.8072342302602559


VOA
For label GPE	Precision is	0.8323529411764706	Recall is	0.884375
For label PERSON	Precision is	0.8663793103448276	Recall is	0.7642585551330798
For label MISC	Precision is	0.8754325259515571	Recall is	0.863481228668942
For label O	Precision is	0.9781599781599781	Recall is	0.9820474167466082
For label ORG	Precision is	0.6681818181818182	Recall is	0.6504424778761062
1659.0 1688.0 1390.0
System total recall: 0.8234597156398105
System total precision: 0.8378541289933695
System total F1: 0.8305945622945922

For label GPE	Precision is	0.7857142857142857	Recall is	0.89375
For label PERSON	Precision is	0.871900826446281	Recall is	0.8022813688212928
For label MISC	Precision is	0.9014778325123153	Recall is	0.8327645051194539
For label O	Precision is	0.9743485342019544	Recall is	0.9838289708099219
For label ORG	Precision is	0.7286432160804021	Recall is	0.6415929203539823
1617.0 1688.0 1374.0
System total recall: 0.8139810426540285
System total precision: 0.849721706864564
System total F1: 0.8314674735249622

Max Product though:
For label GPE	Precision is	0.6992753623188406	Recall is	0.6050156739811913
For label PERSON	Precision is	0.5520833333333334	Recall is	0.6045627376425855
For label MISC	Precision is	0.7971391417425228	Recall is	0.7005714285714286
For label O	Precision is	0.9881808017888516	Recall is	0.8530263339307873
For label ORG	Precision is	0.15350223546944858	Recall is	0.911504424778761
2675.0 1683.0 1171.0
System total recall: 0.6957813428401664
System total precision: 0.43775700934579437
System total F1: 0.5374024782010096

 */

}

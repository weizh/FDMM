package edu.cmu.lti.weizh.train.conll2k;

import edu.cmu.lti.weizh.data.DataFactory;
import edu.cmu.lti.weizh.docmodel.DataSet;
import edu.cmu.lti.weizh.train.AbstractPercEval;

public class C2K_Perc_Eval extends
		AbstractPercEval<String, DataSet, C2K_Perc_Train> {


	C2K_Perc_Eval(C2K_Perc_Train trainer) {
		super(trainer);
	}


	/**
	 * FDMM trainer feature set as in CONLL2KFDMMTrainer.java.
	 * System total recall: 0.9019988602064293 System total precision:
	 * 0.9019988602064293 System total F1: 0.9019988602064293
	 * 
FDMMPerceptron trainer, iteration 2, with duplicate feature removed:
For label I-NP	Precision is	0.9518818149841717	Recall is	0.9412214802448525
For label I-ADVP	Precision is	0.4642857142857143	Recall is	0.43820224719101125
For label I-SBAR	Precision is	0.125	Recall is	0.5
For label B-ADJP	Precision is	0.6236323851203501	Recall is	0.6506849315068494
For label I-VP	Precision is	0.9195791055993987	Recall is	0.9247921390778534
For label I-ADJP	Precision is	0.6776859504132231	Recall is	0.49101796407185627
For label O	Precision is	0.9241542092840284	Recall is	0.9503236245954693
For label B-LST	Precision is	0.0	Recall is	0.0
For label B-PP	Precision is	0.9500924214417745	Recall is	0.9615464560382457
For label B-NP	Precision is	0.9548413344182262	Recall is	0.9446948961519884
For label I-LST	Precision is	0.0	Recall is	0.0
For label I-CONJP	Precision is	0.625	Recall is	0.7692307692307693
For label B-INTJ	Precision is	0.3333333333333333	Recall is	1.0
For label B-PRT	Precision is	0.75	Recall is	0.39622641509433965
For label B-VP	Precision is	0.9295952782462057	Recall is	0.9467582653499356
For label I-PP	Precision is	0.4788732394366197	Recall is	0.7083333333333334
For label B-SBAR	Precision is	0.8636363636363636	Recall is	0.7457943925233644
For label B-ADVP	Precision is	0.7106382978723405	Recall is	0.7713625866050808
For label B-CONJP	Precision is	0.45454545454545453	Recall is	0.5555555555555556
47377.0 47377.0 44190.0
System total recall: 0.9327310720391752
System total precision: 0.9327310720391752
System total F1: 0.9327310720391752

System total recall: 0.9300919969900721
System total precision: 0.9340597728048364
System total F1: 0.9320716622678457


FDMMPerceptron trainer, iteration 10:

For label I-NP	Precision is	0.9596174282678002	Recall is	0.9421953255425709
For label I-ADVP	Precision is	0.3493150684931507	Recall is	0.5730337078651685
For label I-SBAR	Precision is	0.16666666666666666	Recall is	0.75
For label B-ADJP	Precision is	0.7251908396946565	Recall is	0.6506849315068494
For label I-VP	Precision is	0.9511323003575686	Recall is	0.9047619047619048
For label I-ADJP	Precision is	0.6879432624113475	Recall is	0.5808383233532934
For label O	Precision is	0.9244665939884753	Recall is	0.9605177993527508
For label B-LST	Precision is	0.0	Recall is	0.0
For label B-PP	Precision is	0.9648938406558756	Recall is	0.9540636042402827
For label B-NP	Precision is	0.9563913431142093	Recall is	0.9498470455643213
For label I-LST	Precision is	0.0	Recall is	0.0
For label I-CONJP	Precision is	0.5	Recall is	0.23076923076923078
For label B-INTJ	Precision is	0.6666666666666666	Recall is	1.0
For label B-PRT	Precision is	0.6557377049180327	Recall is	0.7547169811320755
For label B-VP	Precision is	0.9212761549616739	Recall is	0.9547015886646629
For label I-PP	Precision is	0.5818181818181818	Recall is	0.6666666666666666
For label B-SBAR	Precision is	0.8349705304518664	Recall is	0.794392523364486
For label B-ADVP	Precision is	0.720558882235529	Recall is	0.8337182448036952
For label B-CONJP	Precision is	0.2857142857142857	Recall is	0.2222222222222222
47377.0 47377.0 44413.0
System total recall: 0.9374379973404817
System total precision: 0.9374379973404817
System total F1: 0.9374379973404817
O excluded:
System total recall: 0.9339757749350681
System total precision: 0.9394716280886806
System total F1: 0.9367156403296288

iteration 50:

For label I-NP	Precision is	0.9567642956764296	Recall is	0.9543683917640512
For label I-ADVP	Precision is	0.5263157894736842	Recall is	0.5617977528089888
For label I-SBAR	Precision is	0.15	Recall is	0.75
For label B-ADJP	Precision is	0.7469879518072289	Recall is	0.7077625570776256
For label I-VP	Precision is	0.939622641509434	Recall is	0.9410430839002267
For label I-ADJP	Precision is	0.7540983606557377	Recall is	0.5508982035928144
For label O	Precision is	0.9489533011272142	Recall is	0.9535598705501618
For label B-LST	Precision is	0.0	Recall is	0.0
For label B-PP	Precision is	0.9550653594771242	Recall is	0.9719393057576388
For label B-NP	Precision is	0.955625100304927	Recall is	0.9587023023667687
For label I-LST	Precision is	0.0	Recall is	0.0
For label I-CONJP	Precision is	0.6470588235294118	Recall is	0.8461538461538461
For label B-INTJ	Precision is	1.0	Recall is	1.0
For label B-PRT	Precision is	0.7475728155339806	Recall is	0.7264150943396226
For label B-VP	Precision is	0.9514248704663213	Recall is	0.9461142121082009
For label I-PP	Precision is	0.8529411764705882	Recall is	0.6041666666666666
For label B-SBAR	Precision is	0.8654618473895582	Recall is	0.805607476635514
For label B-ADVP	Precision is	0.7935409457900807	Recall is	0.7944572748267898
For label B-CONJP	Precision is	0.5	Recall is	0.6666666666666666
47377.0 47377.0 44794.0

System total recall: 0.9454798742005615
System total precision: 0.9454798742005615
System total F1: 0.9454798742005615

O excluded:
System total recall: 0.9442677864893074
System total precision: 0.9449559112881677
System total F1: 0.9446117235685494

iteration 70 (stopped 1E-5)
For label I-NP	Precision is	0.9550437075065908	Recall is	0.9575681691708403
For label I-ADVP	Precision is	0.5454545454545454	Recall is	0.5393258426966292
For label I-SBAR	Precision is	0.16666666666666666	Recall is	0.75
For label B-ADJP	Precision is	0.7531172069825436	Recall is	0.6894977168949772
For label I-VP	Precision is	0.941553544494721	Recall is	0.9436885865457294
For label I-ADJP	Precision is	0.7142857142857143	Recall is	0.5389221556886228
For label O	Precision is	0.9484668486113341	Recall is	0.9559870550161812
For label B-LST	Precision is	0.0	Recall is	0.0
For label B-PP	Precision is	0.9631469979296067	Recall is	0.9669507378923301
For label B-NP	Precision is	0.9576694028649606	Recall is	0.9579777813556594
For label I-LST	Precision is	0.0	Recall is	0.0
For label I-CONJP	Precision is	0.6666666666666666	Recall is	0.6153846153846154
For label B-INTJ	Precision is	1.0	Recall is	0.5
For label B-PRT	Precision is	0.7068965517241379	Recall is	0.7735849056603774
For label B-VP	Precision is	0.9461653492843409	Recall is	0.950837269214255
For label I-PP	Precision is	0.6458333333333334	Recall is	0.6458333333333334
For label B-SBAR	Precision is	0.8688172043010752	Recall is	0.7551401869158878
For label B-ADVP	Precision is	0.7972027972027972	Recall is	0.789838337182448
For label B-CONJP	Precision is	0.5	Recall is	0.4444444444444444
41148.0 41197.0 38901.0
System total recall: 0.9442677864893074
System total precision: 0.9453922426363371
System total F1: 0.9448296800048576
	 * 
	 * @param arg
	 */
	public static void main(String arg[]) {
		DataSet test2kData = DataFactory.getCONLL2kTest();
//		CONLLFormatDataSet test2kData = DataFactory.getCONLL2kTrain();
		C2K_Perc_Train trainer = new C2K_Perc_Train().load("trainedModels/CONLL2KTrain_POS_PERC_100_NegInf_basic.trainer");
		C2K_Perc_Eval evaluator = new C2K_Perc_Eval(trainer);
		evaluator.evaluate(test2kData);
	}
	
	
/**	CONLL2k POS
 * 
 * 
 * 
 * 
47377.0 47377.0 46174.0
System total recall: 0.9746079321189607
System total precision: 0.9746079321189607
System total F1: 0.9746079321189607

 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */


}

package edu.cmu.lti.weizh.train.ontonotes;


import edu.cmu.lti.weizh.data.DATA_PATHS;
import edu.cmu.lti.weizh.data.DataFactory;
import edu.cmu.lti.weizh.data.ontonotes.OntonotesDataSet;
import edu.cmu.lti.weizh.train.AbstractTrigramFDMMEval;

public class ON_TrigramFDMMEval extends AbstractTrigramFDMMEval<String,OntonotesDataSet, ON_TrigramFDMMTrain > {

	ON_TrigramFDMMEval(ON_TrigramFDMMTrain trainer) {
		super(trainer);
	}

	public static void main(String arg[]) {
		
		ON_TrigramFDMMEval abc_evaluator = new ON_TrigramFDMMEval(new ON_TrigramFDMMTrain().load("trainedModels/ONF_ABC_TrigramFDMM_basic-fine.trainer"));
		abc_evaluator.evaluate(DataFactory.getONFDataSet(DATA_PATHS.ONF_ABC_TEST,false));
//		
//		ON_PercEval mnb_evaluator = new ON_PercEval(new ON_PercTrain().load("trainedModels/ONF_MNB_PERC_7Iter-100-1E-10-advanced.trainer"));
//		mnb_evaluator.evaluate(DataFactory.getONFDataSet(DATA_PATHS.ONF_MNB_TEST));
		
//		ON_PercEval cnn_evaluator = new ON_PercEval(new ON_PercTrain().load("trainedModels/ONF_CNN_PERC_15Iter-100-1E-10-basic.trainer"));
//		cnn_evaluator.evaluate(DataFactory.getONFDataSet(DATA_PATHS.ONF_CNN_TEST));
		
//		ON_PercEval nbc_evaluator = new ON_PercEval(new ON_PercTrain().load("trainedModels/ONF_NBC_PERC_34Iter-100-1E-10-basic.trainer"));
//		nbc_evaluator.evaluate(DataFactory.getONFDataSet(DATA_PATHS.ONF_NBC_TEST));
//		
//		ON_PercEval pri_evaluator = new ON_PercEval(new ON_PercTrain().load("trainedModels/ONF_PRI_PERC_45Iter-100-1E-10-basic.trainer"));
//		pri_evaluator.evaluate(DataFactory.getONFDataSet(DATA_PATHS.ONF_PRI_TEST));
//		
//		ON_PercEval voa_evaluator = new ON_PercEval(new ON_PercTrain().load("trainedModels/ONF_VOA_PERC_35Iter-100-1E-10-basic.trainer"));
//		voa_evaluator.evaluate(DataFactory.getONFDataSet(DATA_PATHS.ONF_VOA_TEST));
		
	}
	
/**
 * iter 100, th 1E-10. Basic features.
 * 
 * 
ABC
For label TIME	Precision is	0.9166666666666666	Recall is	0.25
For label GPE	Precision is	0.7222222222222222	Recall is	0.8
For label NORP	Precision is	0.8695652173913043	Recall is	0.9090909090909091
For label PERCENT	Precision is	1.0	Recall is	1.0
For label FAC	Precision is	0.5185185185185185	Recall is	0.6363636363636364
For label PRODUCT	Precision is	0.9545454545454546	Recall is	0.875
For label ORDINAL	Precision is	1.0	Recall is	0.42857142857142855
For label LOC	Precision is	0.8333333333333334	Recall is	0.14285714285714285
For label PERSON	Precision is	0.7364341085271318	Recall is	0.8878504672897196
For label MONEY	Precision is	1.0	Recall is	1.0
For label EVENT	Precision is	0.0	Recall is	0.0
For label DATE	Precision is	0.6610169491525424	Recall is	0.5652173913043478
For label [O]	Precision is	0.9686085119227287	Recall is	0.9855651105651105
For label QUANTITY	Precision is	0.0	Recall is	0.0
For label CARDINAL	Precision is	0.42857142857142855	Recall is	0.6
For label ORG	Precision is	0.5740740740740741	Recall is	0.543859649122807
453.0 510.0 317.0
System total recall: 0.6215686274509804
System total precision: 0.6997792494481236
System total F1: 0.6583592938733126


For label TIME	Precision is	1.0	Recall is	0.36363636363636365
For label GPE	Precision is	0.5684210526315789	Recall is	0.8307692307692308
For label NORP	Precision is	0.9047619047619048	Recall is	0.8636363636363636
For label PERCENT	Precision is	0.0	Recall is	0.0
For label FAC	Precision is	1.0	Recall is	0.36363636363636365
For label PRODUCT	Precision is	1.0	Recall is	0.9166666666666666
For label ORDINAL	Precision is	0.8571428571428571	Recall is	0.8571428571428571
For label LOC	Precision is	0.75	Recall is	0.42857142857142855
For label PERSON	Precision is	0.7166666666666667	Recall is	0.8037383177570093
For label MONEY	Precision is	1.0	Recall is	1.0
For label EVENT	Precision is	0.0	Recall is	0.0
For label DATE	Precision is	0.6103896103896104	Recall is	0.6811594202898551
For label [O]	Precision is	0.9708649468892261	Recall is	0.9824938574938575
For label QUANTITY	Precision is	1.0	Recall is	0.11764705882352941
For label CARDINAL	Precision is	0.4827586206896552	Recall is	0.56
For label ORG	Precision is	0.868421052631579	Recall is	0.5789473684210527
advanced: 100 iter 
System total recall: 0.6490196078431373
System total precision: 0.70276008492569
System total F1: 0.6748216106014271

advanced(shrinked) feature set, collapsed tag
For label GPE	Precision is	0.6043956043956044	Recall is	0.8461538461538461
For label PERSON	Precision is	0.7678571428571429	Recall is	0.8037383177570093
For label MISC	Precision is	0.8297872340425532	Recall is	0.693950177935943
For label O	Precision is	0.9718010915706489	Recall is	0.9843366093366094
For label ORG	Precision is	1.0	Recall is	0.5263157894736842
468.0 510.0 366.0
System total recall: 0.7176470588235294
System total precision: 0.782051282051282
System total F1: 0.7484662576687117

advanced  feature set, collapsed tag
For label GPE	Precision is	0.6125	Recall is	0.7538461538461538
For label PERSON	Precision is	0.79	Recall is	0.7383177570093458
For label MISC	Precision is	0.8108108108108109	Recall is	0.6405693950177936
For label O	Precision is	0.9676737160120846	Recall is	0.9837223587223587
For label ORG	Precision is	0.5555555555555556	Recall is	0.5263157894736842
456.0 510.0 338.0
System total recall: 0.6627450980392157
System total precision: 0.7412280701754386
System total F1: 0.6997929606625258


MNB
For label TIME	Precision is	0.84	Recall is	0.45652173913043476
For label GPE	Precision is	0.53125	Recall is	0.8717948717948718
For label NORP	Precision is	1.0	Recall is	0.6363636363636364
For label LANGUAGE	Precision is	0.0	Recall is	0.0
For label FAC	Precision is	0.2727272727272727	Recall is	1.0
For label ORDINAL	Precision is	1.0	Recall is	0.75
For label LOC	Precision is	0.0	Recall is	0.0
For label PERSON	Precision is	0.65	Recall is	0.78
For label MONEY	Precision is	1.0	Recall is	1.0
For label WORK_OF_ART	Precision is	0.0	Recall is	0.0
For label DATE	Precision is	0.6428571428571429	Recall is	0.29508196721311475
For label [O]	Precision is	0.976271186440678	Recall is	0.993103448275862
For label QUANTITY	Precision is	1.0	Recall is	0.10526315789473684
For label ORG	Precision is	0.8888888888888888	Recall is	0.38095238095238093
For label CARDINAL	Precision is	0.5588235294117647	Recall is	0.8636363636363636
318.0 383.0 207.0
System total recall: 0.5404699738903395
System total precision: 0.6509433962264151
System total F1: 0.5905848787446505

CNN

For label TIME	Precision is	0.5670103092783505	Recall is	0.8148148148148148
For label NORP	Precision is	0.7610619469026548	Recall is	0.9885057471264368
For label GPE	Precision is	0.8185053380782918	Recall is	0.8949416342412452
For label LANGUAGE	Precision is	0.0	Recall is	0.0
For label PERCENT	Precision is	0.8648648648648649	Recall is	0.8421052631578947
For label FAC	Precision is	0.8333333333333334	Recall is	0.37735849056603776
For label PRODUCT	Precision is	0.25	Recall is	0.03571428571428571
For label ORDINAL	Precision is	0.96875	Recall is	0.6888888888888889
For label LOC	Precision is	0.3146067415730337	Recall is	0.3888888888888889
For label PERSON	Precision is	0.6655737704918033	Recall is	0.8768898488120951
For label MONEY	Precision is	0.8059701492537313	Recall is	0.8181818181818182
For label WORK_OF_ART	Precision is	0.6623376623376623	Recall is	0.5730337078651685
For label DATE	Precision is	0.5637707948243993	Recall is	0.8997050147492626
For label EVENT	Precision is	0.6666666666666666	Recall is	0.5714285714285714
For label [O]	Precision is	0.9923267755149138	Recall is	0.9698336622497885
For label QUANTITY	Precision is	0.4444444444444444	Recall is	0.3333333333333333
For label ORG	Precision is	0.6904761904761905	Recall is	0.6628571428571428
For label CARDINAL	Precision is	0.6962025316455697	Recall is	0.8661417322834646
2609.0 2207.0 1720.0
System total recall: 0.7793384685092887
System total precision: 0.6592564200843235
System total F1: 0.7142857142857143

NBC
For label TIME	Precision is	0.875	Recall is	0.5185185185185185
For label NORP	Precision is	1.0	Recall is	0.6875
For label GPE	Precision is	0.5294117647058824	Recall is	0.6
For label PERCENT	Precision is	0.0	Recall is	0.0
For label FAC	Precision is	0.0	Recall is	0.0
For label PRODUCT	Precision is	0.0	Recall is	0.0
For label ORDINAL	Precision is	0.6	Recall is	0.375
For label LOC	Precision is	1.0	Recall is	0.8
For label PERSON	Precision is	0.5428571428571428	Recall is	0.59375
For label MONEY	Precision is	0.6666666666666666	Recall is	0.75
For label WORK_OF_ART	Precision is	0.0	Recall is	0.0
For label EVENT	Precision is	0.15	Recall is	1.0
For label DATE	Precision is	0.5641025641025641	Recall is	0.4943820224719101
For label [O]	Precision is	0.9552814186584425	Recall is	0.9706227967097533
For label QUANTITY	Precision is	0.0	Recall is	0.0
For label CARDINAL	Precision is	0.625	Recall is	0.625
For label ORG	Precision is	0.59	Recall is	0.7564102564102564
409.0 450.0 234.0
System total recall: 0.52
System total precision: 0.5721271393643031
System total F1: 0.5448195576251456

PRI
For label TIME	Precision is	0.8571428571428571	Recall is	0.6
For label LAW	Precision is	0.0	Recall is	0.0
For label NORP	Precision is	0.944	Recall is	0.959349593495935
For label GPE	Precision is	0.8490566037735849	Recall is	0.8780487804878049
For label LANGUAGE	Precision is	0.0	Recall is	0.0
For label PERCENT	Precision is	1.0	Recall is	1.0
For label FAC	Precision is	0.0	Recall is	0.0
For label PRODUCT	Precision is	0.5714285714285714	Recall is	0.6666666666666666
For label ORDINAL	Precision is	1.0	Recall is	0.4444444444444444
For label LOC	Precision is	0.8636363636363636	Recall is	0.5428571428571428
For label PERSON	Precision is	0.9064039408866995	Recall is	0.7666666666666667
For label WORK_OF_ART	Precision is	1.0	Recall is	0.5
For label MONEY	Precision is	0.6666666666666666	Recall is	0.4
For label EVENT	Precision is	0.75	Recall is	1.0
For label DATE	Precision is	0.8333333333333334	Recall is	0.6666666666666666
For label [O]	Precision is	0.9832137538919724	Recall is	0.9896443657174002
For label QUANTITY	Precision is	0.0	Recall is	0.0
For label CARDINAL	Precision is	0.5321100917431193	Recall is	0.8405797101449275
For label ORG	Precision is	0.6407766990291263	Recall is	0.8301886792452831
1088.0 1136.0 863.0
System total recall: 0.7596830985915493
System total precision: 0.7931985294117647
System total F1: 0.7760791366906474

VOA
For label TIME	Precision is	0.8536585365853658	Recall is	0.5737704918032787
For label LAW	Precision is	1.0	Recall is	1.0
For label NORP	Precision is	0.916	Recall is	0.9346938775510204
For label GPE	Precision is	0.7796143250688705	Recall is	0.884375
For label LANGUAGE	Precision is	0.0	Recall is	0.0
For label PERCENT	Precision is	1.0	Recall is	0.8333333333333334
For label FAC	Precision is	0.5833333333333334	Recall is	0.4827586206896552
For label PRODUCT	Precision is	0.7058823529411765	Recall is	0.3333333333333333
For label ORDINAL	Precision is	0.7857142857142857	Recall is	0.7333333333333333
For label LOC	Precision is	0.4883720930232558	Recall is	0.5526315789473685
For label PERSON	Precision is	0.9026548672566371	Recall is	0.7756653992395437
For label MONEY	Precision is	0.625	Recall is	0.625
For label EVENT	Precision is	0.0	Recall is	0.0
For label DATE	Precision is	0.8549618320610687	Recall is	0.7859649122807018
For label [O]	Precision is	0.9749864056552474	Recall is	0.9828696724681376
For label QUANTITY	Precision is	0.0	Recall is	0.0
For label CARDINAL	Precision is	0.8761061946902655	Recall is	0.8319327731092437
For label ORG	Precision is	0.6172839506172839	Recall is	0.6637168141592921
1629.0 1688.0 1306.0
System total recall: 0.773696682464455
System total precision: 0.8017188459177409
System total F1: 0.7874585468797106

 */

}

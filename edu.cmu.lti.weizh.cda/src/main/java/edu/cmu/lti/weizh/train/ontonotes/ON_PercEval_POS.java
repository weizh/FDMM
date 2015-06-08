package edu.cmu.lti.weizh.train.ontonotes;


import edu.cmu.lti.weizh.data.DATA_PATHS;
import edu.cmu.lti.weizh.data.DataFactory;
import edu.cmu.lti.weizh.docmodel.DataSet;
import edu.cmu.lti.weizh.docmodel.OntonotesDataSet;
import edu.cmu.lti.weizh.train.AbstractPercEval;

public class ON_PercEval_POS extends AbstractPercEval<String,DataSet, ON_PercTrain_POS > {

	ON_PercEval_POS(ON_PercTrain_POS trainer) {
		super(trainer);
	}

	public static void main(String arg[]) {
		
		ON_PercEval_POS cnn_evaluator = new ON_PercEval_POS(new ON_PercTrain_POS().load("trainedModels/ONF_CNNTrain_POS_PERC_100_NegInf_basic.trainer"));
		cnn_evaluator.evaluate(DataFactory.getONFDataSet(DATA_PATHS.ONF_CNN_TRAIN,false));
	}

	/**
	 * Train with CNN test, test with CNN train.
	 * 
	 * 
For label -NONE-	Precision is	0.9971026490066225	Recall is	0.9993777224642191
For label HYPH	Precision is	0.9936102236421726	Recall is	1.0
For label JJ	Precision is	0.824724318049913	Recall is	0.7662442706929091
For label -RRB-	Precision is	1.0	Recall is	0.08
For label RB	Precision is	0.8353951890034365	Recall is	0.8313953488372093
For label DT	Precision is	0.9764157810452759	Recall is	0.9940714391581443
For label TO	Precision is	0.9005190311418685	Recall is	0.9923736892278361
For label RP	Precision is	0.6587301587301587	Recall is	0.5665529010238908
For label RBR	Precision is	0.4485294117647059	Recall is	0.6853932584269663
For label RBS	Precision is	0.0	Recall is	0.0
For label JJS	Precision is	0.7795698924731183	Recall is	0.9602649006622517
For label JJR	Precision is	0.6808510638297872	Recall is	0.6918918918918919
For label NN	Precision is	0.8885142484228845	Recall is	0.9033506579674887
For label NNPS	Precision is	0.9433962264150944	Recall is	0.5319148936170213
For label VBN	Precision is	0.9033505154639175	Recall is	0.8654320987654321
For label VB	Precision is	0.900830258302583	Recall is	0.8983440662373505
For label PDT	Precision is	0.6956521739130435	Recall is	0.34782608695652173
For label VBP	Precision is	0.9224855861627163	Recall is	0.8727272727272727
For label ``	Precision is	1.0	Recall is	0.9954337899543378
For label WP$	Precision is	1.0	Recall is	0.25
For label PRP	Precision is	0.995484543244182	Recall is	0.9875947622329428
For label SYM	Precision is	1.0	Recall is	0.46153846153846156
For label MD	Precision is	0.9569620253164557	Recall is	0.9921259842519685
For label WDT	Precision is	0.9384615384615385	Recall is	0.8714285714285714
For label VBZ	Precision is	0.8631143775838309	Recall is	0.9288185862580326
For label WP	Precision is	0.9837133550488599	Recall is	0.9837133550488599
For label IN	Precision is	0.9521562459037882	Recall is	0.9509098049482917
For label XX	Precision is	0.0	Recall is	0.0
For label $	Precision is	1.0	Recall is	1.0
For label ''	Precision is	1.0	Recall is	1.0
For label EX	Precision is	0.8121212121212121	Recall is	0.950354609929078
For label VBG	Precision is	0.9247894103489771	Recall is	0.9684940138626339
For label POS	Precision is	0.8761904761904762	Recall is	0.7301587301587301
For label VBD	Precision is	0.9015842534805569	Recall is	0.8812763960581886
For label -LRB-	Precision is	1.0	Recall is	0.04
For label .	Precision is	0.9937980649962789	Recall is	0.9982556690755046
For label ,	Precision is	0.9988363072148952	Recall is	0.9984490112446684
For label UH	Precision is	0.78125	Recall is	0.5208333333333334
For label PRP$	Precision is	0.9512485136741974	Recall is	0.9913258983890955
For label NNS	Precision is	0.9177325581395349	Recall is	0.8711368653421634
For label CC	Precision is	0.9922388059701492	Recall is	0.9805309734513274
For label NNP	Precision is	0.9147049831855701	Recall is	0.9662522202486679
For label CD	Precision is	0.9523411371237458	Recall is	0.9366776315789473
For label :	Precision is	0.9230769230769231	Recall is	0.9333333333333333
For label WRB	Precision is	0.992619926199262	Recall is	1.0
For label ADD	Precision is	0.0	Recall is	0.0
74468.0 74468.0 69157.0
System total recall: 0.9286807756351722
System total precision: 0.9286807756351722
System total F1: 0.9286807756351722
	 * 
	 */
}

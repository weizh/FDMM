package edu.cmu.lti.weizh.train.conll2k;



import edu.cmu.lti.weizh.data.CONLLFormatDataSet;
import edu.cmu.lti.weizh.data.DataFactory;
import edu.cmu.lti.weizh.feature.FCONST;
import edu.cmu.lti.weizh.train.AbstractFDMMEval;
@Deprecated
public class C2K_FDMMEval extends AbstractFDMMEval<String, CONLLFormatDataSet,C2K_FDMM_Train> {

	/**
	 * This is for de-serialization convenience
	 */
	private static final long serialVersionUID = 1L;

	double alpha = 1E-4;
	double beta = 1E-5;
	double gamma = 1E-5;
	static final String TRANS_F_NAME = FCONST.n(FCONST.F_CHUNK, 1);

	C2K_FDMMEval(C2K_FDMM_Train trainer) {
		super(trainer);
	}
	

	/**
	 * System total recall: 0.9019988602064293 System total precision:
	 * 0.9019988602064293 
	 * 
For label I-NP	Precision is 0.9177728098365237	Recall is 0.9138146911519198
For label I-ADVP	Precision is 0.3739130434782609	Recall is 0.48314606741573035
For label I-SBAR	Precision is 0.15	Recall is 0.75
For label B-ADJP	Precision is 0.6013513513513513	Recall is 0.6095890410958904
For label I-VP	Precision is 0.882938026013772	Recall is 0.872260015117158
For label I-ADJP	Precision is 0.5238095238095238	Recall is 0.39520958083832336
For label O	Precision is 0.9338735818476499	Recall is 0.9323624595469255
For label B-LST	Precision is 0.0	Recall is 0.0
For label B-PP	Precision is 0.9205607476635514	Recall is 0.9418000415713988
For label B-NP	Precision is 0.9138126009693053	Recall is 0.9107229109644179
For label I-LST	Precision is 0.0	Recall is 0.0
For label I-CONJP	Precision is 0.6666666666666666	Recall is 0.7692307692307693
For label B-INTJ	Precision is 0.15384615384615385	Recall is 1.0
For label B-PRT	Precision is 0.5862068965517241	Recall is 0.6415094339622641
For label B-VP	Precision is 0.8970713073005093	Recall is 0.907471017604122
For label I-PP	Precision is 0.44642857142857145	Recall is 0.5208333333333334
For label B-SBAR	Precision is 0.7813852813852814	Recall is 0.6747663551401869
For label B-ADVP	Precision is 0.6882821387940842	Recall is 0.6986143187066974
For label B-CONJP	Precision is 0.35294117647058826	Recall is 0.6666666666666666

System total recall: 0.9019988602064293
System total precision: 0.9019988602064293
	 * 
	 * @param arg
	 * @throws Exception 
	 */
	public static void main(String arg[]) throws Exception {
		CONLLFormatDataSet test2kData = DataFactory.getCONLL2kTest();
		C2K_FDMM_Train CONLLtrainer = new C2K_FDMM_Train().load("CONLL2kFDMMTrainer");
		C2K_FDMMEval eval = new C2K_FDMMEval(CONLLtrainer);
		eval.evaluate(test2kData);
	}

}

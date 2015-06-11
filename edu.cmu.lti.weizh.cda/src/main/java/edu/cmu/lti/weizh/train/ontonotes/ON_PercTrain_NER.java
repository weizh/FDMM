package edu.cmu.lti.weizh.train.ontonotes;


import edu.cmu.lti.weizh.data.DATA_PATHS;
import edu.cmu.lti.weizh.data.DataFactory;
import edu.cmu.lti.weizh.docmodel.DataSet;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.feature.Feature;
import edu.cmu.lti.weizh.feature.Theta;
import edu.cmu.lti.weizh.train.AbstractPercTrain;

public class ON_PercTrain_NER extends AbstractPercTrain<String, ON_PercTrain_NER, DataSet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ON_PercTrain_NER(String[] thetaHeaders, String[] featureHeaders) {
		super(thetaHeaders, featureHeaders);
	}
	
	public ON_PercTrain_NER() {
		super();
	}
	
	public static void main(String argv[]) throws Exception {

		
		int iter=100; double th = Double.NEGATIVE_INFINITY;
//		ON_PercTrain abctrainer = new ON_PercTrain(thetaHeaders, thd, tvd, featureHeaders, fhd, fvd);
//		ON_PercTrain mnbtrainer = new ON_PercTrain(thetaHeaders, thd, tvd, featureHeaders, fhd, fvd);
//		ON_PercTrain_NER cnntrainer = new ON_PercTrain_NER(thetaHeaders, thd, tvd, featureHeaders, fhd, fvd);
//		ON_PercTrain nbctrainer = new ON_PercTrain(thetaHeaders, thd, tvd, featureHeaders, fhd, fvd);
//		ON_PercTrain pritrainer = new ON_PercTrain(thetaHeaders, thd, tvd, featureHeaders, fhd, fvd);
		ON_PercTrain_NER voatrainer = new ON_PercTrain_NER(Theta.getOnfNerThetaHeaders(),Feature.getOnfNerFeatureHeaders());
//
//		abctrainer.train(DataFactory.getONFDataSet(DATA_PATHS.ONF_ABC_TRAIN,true), iter, th);
//		mnbtrainer.train(DataFactory.getONFDataSet(DATA_PATHS.ONF_MNB_TRAIN,true), iter, th);
//		cnntrainer.train(DataFactory.getONFDataSet(DATA_PATHS.ONF_CNN_TRAIN,true), iter, th);
//		nbctrainer.train(DataFactory.getONFDataSet(DATA_PATHS.ONF_NBC_TRAIN,true),  iter, th);
//		pritrainer.train(DataFactory.getONFDataSet(DATA_PATHS.ONF_PRI_TRAIN,true),  iter, th);
		voatrainer.train(DataFactory.getONFDataSet(DATA_PATHS.ONF_VOA_TRAIN,true), iter, th);
//
//		abctrainer.store("trainedModels/ONF_ABC_PERC_100_NegInf_basic.trainer");
//		mnbtrainer.store("trainedModels/ONF_MNB_PERC_100_NegInf_basic.trainer");		
//		cnntrainer.store("trainedModels/ONF_CNNTrain_NER_PERC_100_NegInf_basic.trainer");		
//		nbctrainer.store("trainedModels/ONF_NBC_PERC_100_NegInf_basic.trainer");
//		pritrainer.store("trainedModels/ONF_PRI_PERC_100_NegInf_basic.trainer");
		voatrainer.store("trainedModels/ONF_VOATrain_NER_PERC_100_NegInf_basic.trainer");
	}

	@Override
	public String getGoldLabel(Word w) {
		return w.getEntityType();
	}

	@Override
	protected ON_PercTrain_NER self() {
		return this;
	}

}

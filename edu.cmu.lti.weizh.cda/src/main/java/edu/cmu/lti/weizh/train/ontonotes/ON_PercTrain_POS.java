package edu.cmu.lti.weizh.train.ontonotes;

import edu.cmu.lti.weizh.data.DATA_PATHS;
import edu.cmu.lti.weizh.data.DataFactory;
import edu.cmu.lti.weizh.docmodel.DataSet;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.feature.Feature;
import edu.cmu.lti.weizh.feature.Theta;
import edu.cmu.lti.weizh.train.AbstractPercTrain;

public class ON_PercTrain_POS extends AbstractPercTrain<String, ON_PercTrain_POS, DataSet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ON_PercTrain_POS(String[] thetaHeaders,  String[] featureHeaders) {
		super(thetaHeaders, featureHeaders);
	}

	public ON_PercTrain_POS() {
		super();
	}

	public static void main(String argv[]) throws Exception {

		int iter = 100;
		double th = Double.NEGATIVE_INFINITY;

		ON_PercTrain_POS postagger = new ON_PercTrain_POS(Theta.getPOSthetaHeaders(), Feature.getPOSfeatureHeaders());
		DataSet onfAllTrain = DataFactory.getONFDataSet(DATA_PATHS.ONF_CNN_TEST, false);
		postagger.train(onfAllTrain, iter, th);
		postagger.store("trainedModels/ONF_CNNTrain_POS_PERC_100_NegInf_basic.trainer");
	}

	@Override
	public String getGoldLabel(Word w) {
		return w.getPartOfSpeech();
	}

	@Override
	protected ON_PercTrain_POS self() {
		return this;
	}

}

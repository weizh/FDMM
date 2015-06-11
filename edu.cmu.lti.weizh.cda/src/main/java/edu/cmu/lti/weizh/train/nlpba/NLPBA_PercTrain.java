package edu.cmu.lti.weizh.train.nlpba;

import edu.cmu.lti.weizh.data.DataFactory;
import edu.cmu.lti.weizh.docmodel.DataSet;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.feature.Feature;
import edu.cmu.lti.weizh.feature.Theta;
import edu.cmu.lti.weizh.train.AbstractPercTrain;

public class NLPBA_PercTrain extends AbstractPercTrain<String, NLPBA_PercTrain, DataSet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	NLPBA_PercTrain(String[] thetaHeaders, String[] featureHeaders) {
		super(thetaHeaders, featureHeaders);

	}

	public NLPBA_PercTrain() {
		super();
	}

	public static void main(String argv[]) throws Exception {

		int iter = 100;
		double th = Double.NEGATIVE_INFINITY;
		NLPBA_PercTrain abctrainer = new NLPBA_PercTrain(Theta.getNlpbaNerThetaHeaders(), Feature.getNlpbaNerFeatureHeaders());
		//
		abctrainer.train(DataFactory.getNLPBATrain(), iter, th);

		//
		abctrainer.store("trainedModels/ONF_NLPBA_PERC_100_NegInf_basic.trainer");

	}

	@Override
	public String getGoldLabel(Word w) {
		return w.getEntityType();
	}

	@Override
	protected NLPBA_PercTrain self() {
		return this;
	}

}

package edu.cmu.lti.weizh.train.conll2k;

import edu.cmu.lti.weizh.data.DataFactory;
import edu.cmu.lti.weizh.docmodel.DataSet;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.feature.Feature;
import edu.cmu.lti.weizh.feature.Theta;
import edu.cmu.lti.weizh.train.AbstractPercTrain;

public class C2K_Perc_Train extends AbstractPercTrain<String, C2K_Perc_Train, DataSet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	C2K_Perc_Train(String[] thetaHeaders, String thetaHeaderDelimiter, String thetaValueDelimiter, String[] featureHeaders,
			String featureHeaderDelimiter, String featureValueDelimiter) {
		super(thetaHeaders, thetaHeaderDelimiter, thetaValueDelimiter, featureHeaders, featureHeaderDelimiter,
				featureValueDelimiter);

	}

	public C2K_Perc_Train() {
		super();
	}

	public static void main(String argb[]) throws Exception {

		C2K_Perc_Train trainer = new C2K_Perc_Train(Theta.getConll2kChunkingthetaHeaders(), Theta.getThd(), Theta.getTvd(),
				Feature.getCONLL2KChunkingFeatureHeaders(), Feature.getFhd(), Feature.getFvd());
		DataSet train2kData = DataFactory.getCONLL2kTrain();
		trainer.train(train2kData, 100, 1E-10);
		trainer.store("CONLL2kFDMMPerceptron-" + trainer.getIterationsUsed() + ".trainer");

	}

	@Override
	protected C2K_Perc_Train self() {
		return this;
	}

	@Override
	protected String getGoldLabel(Word w) {
		return w.getChunkType();
	}

}

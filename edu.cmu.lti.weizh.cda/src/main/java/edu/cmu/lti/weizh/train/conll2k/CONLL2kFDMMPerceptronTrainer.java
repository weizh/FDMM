package edu.cmu.lti.weizh.train.conll2k;

import java.util.List;

import edu.cmu.lti.weizh.data.CONLLFormatDataSet;
import edu.cmu.lti.weizh.data.DataFactory;
import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.feature.FCONST;
import edu.cmu.lti.weizh.train.AbstractFDMMPerceptronTrainer;

public class CONLL2kFDMMPerceptronTrainer extends
		AbstractFDMMPerceptronTrainer<String, CONLL2kFDMMPerceptronTrainer, CONLLFormatDataSet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	CONLL2kFDMMPerceptronTrainer(String[] thetaHeaders, String thetaHeaderDelimiter, String thetaValueDelimiter,
			String[] featureHeaders, String featureHeaderDelimiter, String featureValueDelimiter) {
		super(thetaHeaders, thetaHeaderDelimiter, thetaValueDelimiter, featureHeaders, featureHeaderDelimiter,
				featureValueDelimiter);

	}

	public CONLL2kFDMMPerceptronTrainer() {
		super();
	}

	public static void main(String argb[]) {
		String thd = "-thd-";
		String tvd = "-tvd-";
		String[] thetaHeaders = new String[] { 
				FCONST.T_WORD, 
				FCONST.T_LEMMA, 
				FCONST.T_WORDFORM + thd + FCONST.T_SUFFIX,
				FCONST.T_POS + thd + FCONST.T_SUFFIX,
				FCONST.T_POS, 
				FCONST.T_WORDFORM,
				FCONST.T_SUFFIX
				};

		String[] featureHeaders = new String[] { 
				FCONST.p(FCONST.F_POS, 1), 
				FCONST.n(FCONST.F_POS, 1), 
				FCONST.p(FCONST.F_POS, 2),
				FCONST.n(FCONST.F_POS, 2),
				//
				FCONST.p(FCONST.F_LEMMA, 1), 
				FCONST.p(FCONST.F_LEMMA, 2), 
				FCONST.n(FCONST.F_LEMMA, 1),
				FCONST.n(FCONST.F_LEMMA, 2),

				FCONST.p(FCONST.F_SUFFIX, 1), 
				FCONST.n(FCONST.F_SUFFIX, 1), 
				FCONST.p(FCONST.F_PREFIX, 2),
				FCONST.n(FCONST.F_PREFIX, 2),

		};
		String fhd = "-fhd-";
		String fvd = "-fvd-";

		CONLL2kFDMMPerceptronTrainer trainer = new CONLL2kFDMMPerceptronTrainer(thetaHeaders, thd, tvd, featureHeaders, fhd, fvd);

		CONLLFormatDataSet train2kData = DataFactory.getCONLL2kTrain();

		try {
			int T = 100;
			trainer.setT(T);
			trainer.train(train2kData);
			trainer.store("CONLL2kFDMMPerceptron-"+T+".trainer");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected CONLL2kFDMMPerceptronTrainer self() {
		return this;
	}

	@Override
	protected String getGoldLabel(Word w) {
		return w.getChunkType();
	}

	@Override
	protected List<Sentence> getSentences(CONLLFormatDataSet d) {
		try {
			return d.getDocuments().get(0).getParagraphs().get(0).getSentences();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}

package edu.cmu.lti.weizh.train.conll2k;

import java.util.List;

import edu.cmu.lti.weizh.data.CONLLFormatDataSet;
import edu.cmu.lti.weizh.data.DataFactory;
import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.feature.FCONST;
import edu.cmu.lti.weizh.train.AbstractPercTrain;

public class C2K_Perc_Train extends AbstractPercTrain<String, C2K_Perc_Train, CONLLFormatDataSet> {

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
		String thd = "-thd-";
		String tvd = "-tvd-";
		String fhd = "-fhd-";
		String fvd = "-fvd-";

		String[] thetaHeaders = new String[] { FCONST.T_WORD, FCONST.T_LEMMA, FCONST.T_WORDFORM + thd + FCONST.T_SUFFIX,
				FCONST.T_POS + thd + FCONST.T_SUFFIX, FCONST.T_POS, FCONST.T_WORDFORM, FCONST.T_SUFFIX };

		String[] featureHeaders = new String[] { FCONST.p(FCONST.F_POS, 1), FCONST.n(FCONST.F_POS, 1),
				FCONST.p(FCONST.F_POS, 2),
				FCONST.n(FCONST.F_POS, 2),
				//
				FCONST.p(FCONST.F_LEMMA, 1), FCONST.p(FCONST.F_LEMMA, 2), FCONST.n(FCONST.F_LEMMA, 1),
				FCONST.n(FCONST.F_LEMMA, 2),

				FCONST.p(FCONST.F_SUFFIX, 1), FCONST.n(FCONST.F_SUFFIX, 1), FCONST.p(FCONST.F_PREFIX, 2),
				FCONST.n(FCONST.F_PREFIX, 2),

		};

		C2K_Perc_Train trainer = new C2K_Perc_Train(thetaHeaders, thd, tvd, featureHeaders, fhd, fvd);
		CONLLFormatDataSet train2kData = DataFactory.getCONLL2kTrain();
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

package edu.cmu.lti.weizh.train.ontonotes;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.weizh.data.DATA_PATHS;
import edu.cmu.lti.weizh.data.DataFactory;
import edu.cmu.lti.weizh.data.ontonotes.OntonotesDataSet;
import edu.cmu.lti.weizh.docmodel.Document;
import edu.cmu.lti.weizh.docmodel.Paragraph;
import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.feature.FCONST;
import edu.cmu.lti.weizh.train.AbstractPercTrain;

public class ON_PercTrain_POS extends AbstractPercTrain<String, ON_PercTrain_POS, OntonotesDataSet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ON_PercTrain_POS(String[] thetaHeaders, String thetaHeaderDelimiter, String thetaValueDelimiter, String[] featureHeaders,
			String featureHeaderDelimiter, String featureValueDelimiter) {
		super(thetaHeaders, thetaHeaderDelimiter, thetaValueDelimiter, featureHeaders, featureHeaderDelimiter,
				featureValueDelimiter);

	}
	
	public ON_PercTrain_POS() {
		super();
	}
	
	public static void main(String argv[]) throws Exception {
		String thd = "-thd-";
		String tvd = "-tvd-";
		String fhd = "-fhd-";
		String fvd = "-fvd-";
		
		String[] thetaHeaders = new String[] { 
				//basic
				FCONST.T_WORD, 
				FCONST.T_LEMMA, 
				FCONST.T_WORDFORM + thd + FCONST.T_SUFFIX,
				FCONST.T_PREFIX + thd + FCONST.T_SUFFIX,
				FCONST.T_WORDFORM,
				FCONST.T_SUFFIX
				};

		String[] featureHeaders = new String[] { 
				// basic

				FCONST.p(FCONST.F_SUFFIX, 2) + fhd + FCONST.p(FCONST.F_SUFFIX, 1), 
				FCONST.p(FCONST.F_SUFFIX, 1) + fhd + FCONST.n(FCONST.F_SUFFIX, 1),
				FCONST.n(FCONST.F_SUFFIX, 1) + fhd + FCONST.n(FCONST.F_SUFFIX, 2),
				
				FCONST.p(FCONST.F_WORDFORM, 2) + fhd + FCONST.p(FCONST.F_WORDFORM, 1), 
				FCONST.p(FCONST.F_WORDFORM, 1) + fhd + FCONST.n(FCONST.F_WORDFORM, 1),
				FCONST.n(FCONST.F_WORDFORM, 1) + fhd + FCONST.n(FCONST.F_WORDFORM, 2),
				
				FCONST.p(FCONST.F_LEMMA, 1), 
				FCONST.p(FCONST.F_LEMMA, 2), 
				FCONST.n(FCONST.F_LEMMA, 1),
				FCONST.n(FCONST.F_LEMMA, 2),
		};
		
		int iter=100; double th = Double.NEGATIVE_INFINITY;
		
		ON_PercTrain_POS postagger = new ON_PercTrain_POS(thetaHeaders, thd, tvd, featureHeaders, fhd, fvd);
		OntonotesDataSet onfAllTrain = DataFactory.getONFDataSet(DATA_PATHS.ONF_CNN_TEST, false);
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

	@Override
	public List<Sentence> getSentences(OntonotesDataSet d) {
		ArrayList<Sentence> ss = new ArrayList<Sentence>();
		try {
			for (Document doc : d.getDocuments())
				for (Paragraph para : doc.getParagraphs())
					for (Sentence sent : para.getSentences()) {
						ss.add(sent);
					}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ss;
	}

}

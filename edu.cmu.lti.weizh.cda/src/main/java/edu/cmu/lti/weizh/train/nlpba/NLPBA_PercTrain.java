package edu.cmu.lti.weizh.train.nlpba;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.weizh.data.CONLLFormatDataSet;
import edu.cmu.lti.weizh.data.DataFactory;
import edu.cmu.lti.weizh.docmodel.Document;
import edu.cmu.lti.weizh.docmodel.Paragraph;
import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.feature.FCONST;
import edu.cmu.lti.weizh.feature.Feature;
import edu.cmu.lti.weizh.train.AbstractPercTrain;

public class NLPBA_PercTrain extends AbstractPercTrain<String, NLPBA_PercTrain, CONLLFormatDataSet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	NLPBA_PercTrain(String[] thetaHeaders, String thetaHeaderDelimiter, String thetaValueDelimiter, String[] featureHeaders,
			String featureHeaderDelimiter, String featureValueDelimiter) {
		super(thetaHeaders, thetaHeaderDelimiter, thetaValueDelimiter, featureHeaders, featureHeaderDelimiter,
				featureValueDelimiter);

	}

	public NLPBA_PercTrain() {
		super();
	}

	public static void main(String argv[]) throws Exception {
		

		 String thd = Feature.getThd();
		 String tvd = Feature.getTvd();
		 String fhd = Feature.getFhd();
		 String fvd = Feature.getFvd();
		 
		 String[] nlpbaThetaHeaders = new String[] { 
			//basic
			FCONST.T_WORD, 
			FCONST.T_LEMMA, 
			FCONST.T_CAP,
			FCONST.T_WORDFORM,
			FCONST.T_SUFFIX,
			FCONST.T_PREFIX,
			FCONST.T_CAP +thd+FCONST.T_PREFIX,
			FCONST.T_CAP +thd + FCONST.T_WORDFORM,
			FCONST.T_CAP +thd + FCONST.T_SUFFIX,
			FCONST.T_WORDFORM + thd + FCONST.T_SUFFIX,
			FCONST.T_WORDFORM + thd + FCONST.T_PREFIX,
			FCONST.T_PREFIX + thd + FCONST.T_SUFFIX,

			};

String[] nlpaFeatureHeaders = new String[] { 
			// basic
		FCONST.p(FCONST.F_LEMMA, 1), 
		FCONST.p(FCONST.F_LEMMA, 2), 
		FCONST.n(FCONST.F_LEMMA, 1),
		FCONST.n(FCONST.F_LEMMA, 2),
		
		FCONST.p(FCONST.F_LEMMA, 2) + fhd + FCONST.p(FCONST.F_LEMMA, 1), 
		FCONST.p(FCONST.F_LEMMA, 1) + fhd + FCONST.n(FCONST.F_LEMMA, 1),
		FCONST.n(FCONST.F_LEMMA, 1) + fhd + FCONST.n(FCONST.F_LEMMA, 2),
		
		FCONST.p(FCONST.F_CAP, 1), 
		FCONST.p(FCONST.F_CAP, 2), 
		FCONST.n(FCONST.F_CAP, 1),
		FCONST.n(FCONST.F_CAP, 2),
		
		FCONST.p(FCONST.F_CAP, 2) + fhd + FCONST.p(FCONST.F_CAP, 1), 
		FCONST.p(FCONST.F_CAP, 1) + fhd + FCONST.n(FCONST.F_CAP, 1),
		FCONST.n(FCONST.F_CAP, 1) + fhd + FCONST.n(FCONST.F_CAP, 2),
		
		FCONST.p(FCONST.F_WORDFORM, 1), 
		FCONST.p(FCONST.F_WORDFORM, 2), 
		FCONST.n(FCONST.F_WORDFORM, 1),
		FCONST.n(FCONST.F_WORDFORM, 2),
		FCONST.p(FCONST.F_WORDFORM, 2) + fhd + FCONST.p(FCONST.F_WORDFORM, 1), 
		FCONST.p(FCONST.F_WORDFORM, 1) + fhd + FCONST.n(FCONST.F_WORDFORM, 1),
		FCONST.n(FCONST.F_WORDFORM, 1) + fhd + FCONST.n(FCONST.F_WORDFORM, 2),
		
		FCONST.p(FCONST.F_SUFFIX, 1), 
		FCONST.p(FCONST.F_SUFFIX, 2), 
		FCONST.n(FCONST.F_SUFFIX, 1),
		FCONST.n(FCONST.F_SUFFIX, 2),
		
		FCONST.p(FCONST.F_SUFFIX, 2) + fhd + FCONST.p(FCONST.F_SUFFIX, 1), 
		FCONST.p(FCONST.F_SUFFIX, 1) + fhd + FCONST.n(FCONST.F_SUFFIX, 1),
		FCONST.n(FCONST.F_SUFFIX, 1) + fhd + FCONST.n(FCONST.F_SUFFIX, 2),
		
		FCONST.p(FCONST.F_PREFIX, 1), 
		FCONST.p(FCONST.F_PREFIX, 2), 
		FCONST.n(FCONST.F_PREFIX, 1),
		FCONST.n(FCONST.F_PREFIX, 2),
		
		FCONST.p(FCONST.F_PREFIX, 2) + fhd + FCONST.p(FCONST.F_PREFIX, 1), 
		FCONST.p(FCONST.F_PREFIX, 1) + fhd + FCONST.n(FCONST.F_PREFIX, 1),
		FCONST.n(FCONST.F_PREFIX, 1) + fhd + FCONST.n(FCONST.F_PREFIX, 2),
			
	};
	
		int iter = 100;
		double th = Double.NEGATIVE_INFINITY;
		NLPBA_PercTrain abctrainer = new NLPBA_PercTrain(nlpbaThetaHeaders, Feature.getThd(), Feature.getTvd(),
				nlpaFeatureHeaders, Feature.getFhd(), Feature.getFvd());
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

	@Override
	public List<Sentence> getSentences(CONLLFormatDataSet d) {
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

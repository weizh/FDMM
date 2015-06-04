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

public class ON_PercTrain extends AbstractPercTrain<String, ON_PercTrain, OntonotesDataSet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ON_PercTrain(String[] thetaHeaders, String thetaHeaderDelimiter, String thetaValueDelimiter, String[] featureHeaders,
			String featureHeaderDelimiter, String featureValueDelimiter) {
		super(thetaHeaders, thetaHeaderDelimiter, thetaValueDelimiter, featureHeaders, featureHeaderDelimiter,
				featureValueDelimiter);

	}
	
	public ON_PercTrain() {
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
				FCONST.T_POS + thd + FCONST.T_SUFFIX,
				FCONST.T_POS, 
				FCONST.T_WORDFORM,
				FCONST.T_SUFFIX
				};

		String[] featureHeaders = new String[] { 
				// basic

				FCONST.p(FCONST.F_POS, 2) + fhd + FCONST.p(FCONST.F_POS, 1), 
				FCONST.p(FCONST.F_POS, 1) + fhd + FCONST.n(FCONST.F_POS, 1),
				FCONST.n(FCONST.F_POS, 1) + fhd + FCONST.n(FCONST.F_POS, 2),
				
				FCONST.p(FCONST.T_WORDFORM, 2) + fhd + FCONST.p(FCONST.T_WORDFORM, 1), 
				FCONST.p(FCONST.T_WORDFORM, 1) + fhd + FCONST.n(FCONST.T_WORDFORM, 1),
				FCONST.n(FCONST.T_WORDFORM, 1) + fhd + FCONST.n(FCONST.T_WORDFORM, 2),
				
				FCONST.p(FCONST.F_LEMMA, 1), 
				FCONST.p(FCONST.F_LEMMA, 2), 
				FCONST.n(FCONST.F_LEMMA, 1),
				FCONST.n(FCONST.F_LEMMA, 2),
		};
		
		int iter=200; double th = Double.NEGATIVE_INFINITY;
		ON_PercTrain abctrainer = new ON_PercTrain(thetaHeaders, thd, tvd, featureHeaders, fhd, fvd);
//		ON_PercTrain mnbtrainer = new ON_PercTrain(thetaHeaders, thd, tvd, featureHeaders, fhd, fvd);
//		ON_PercTrain cnntrainer = new ON_PercTrain(thetaHeaders, thd, tvd, featureHeaders, fhd, fvd);
//		ON_PercTrain nbctrainer = new ON_PercTrain(thetaHeaders, thd, tvd, featureHeaders, fhd, fvd);
//		ON_PercTrain pritrainer = new ON_PercTrain(thetaHeaders, thd, tvd, featureHeaders, fhd, fvd);
//		ON_PercTrain voatrainer = new ON_PercTrain(thetaHeaders, thd, tvd, featureHeaders, fhd, fvd);
//
		abctrainer.train(DataFactory.getONFDataSet(DATA_PATHS.ONF_ABC_TRAIN,true), iter, th);
//		mnbtrainer.train(DataFactory.getONFDataSet(DATA_PATHS.ONF_MNB_TRAIN), iter, th);
//		cnntrainer.train(DataFactory.getONFDataSet(DATA_PATHS.ONF_CNN_TRAIN), iter, th);
//		nbctrainer.train(DataFactory.getONFDataSet(DATA_PATHS.ONF_NBC_TRAIN),  iter, th);
//		pritrainer.train(DataFactory.getONFDataSet(DATA_PATHS.ONF_PRI_TRAIN),  iter, th);
//		voatrainer.train(DataFactory.getONFDataSet(DATA_PATHS.ONF_VOA_TRAIN), iter, th);
//
		abctrainer.store("trainedModels/ONF_ABC_PERC_"+abctrainer.getIterationsUsed()+"_100_advanced.trainer");
//		mnbtrainer.store("trainedModels/ONF_MNB_PERC_"+mnbtrainer.getIterationsUsed()+".trainer");		
//		cnntrainer.store("trainedModels/ONF_CNN_PERC_"+cnntrainer.getIterationsUsed()+".trainer");		
//		nbctrainer.store("trainedModels/ONF_NBC_PERC_"+nbctrainer.getIterationsUsed()+".trainer");
//		pritrainer.store("trainedModels/ONF_PRI_PERC_"+pritrainer.getIterationsUsed()+".trainer");
//		voatrainer.store("trainedModels/ONF_VOA_PERC_"+voatrainer.getIterationsUsed()+"advanced.trainer");
	}

	@Override
	public String getGoldLabel(Word w) {
		return w.getEntityType();
	}

	@Override
	protected ON_PercTrain self() {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ss;
	}

}

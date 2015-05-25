package edu.cmu.lti.weizh.train.conll2k;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.weizh.data.CONLLFormatDataSet;
import edu.cmu.lti.weizh.data.DataFactory;
import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.feature.FCONST;
import edu.cmu.lti.weizh.feature.Feature;
import edu.cmu.lti.weizh.feature.Theta;
import edu.cmu.lti.weizh.mlmodel.FDMM;
import edu.cmu.lti.weizh.mlmodel.PerceptronFDMM;
import edu.cmu.lti.weizh.train.AbstractFDMMPerceptronTrainer;
import edu.cmu.lti.weizh.train.AbstractFDMMTrainer;

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

	@Override
	public void train(CONLLFormatDataSet d) throws Exception {
		for (Sentence s : d.getDocuments().get(0).getParagraphs().get(0).getSentences()) {
			List<Word> words = s.getWords();

			// thetas include the sent start and sent end theta. Each Theta is
			// created, but the generalization in theta were never used.
			List<Theta<String>> thetas = new ArrayList<Theta<String>>(words.size());
			List<List<Feature<String>>> features = new ArrayList<List<Feature<String>>>(words.size());
			List<String[]> goldTrans = new ArrayList<String[]>();
			List<String> goldLabels = new ArrayList<String>(words.size());

			for (int i = 0; i < words.size(); i++) {
				Word w = words.get(i);
				Theta<String> theta = new Theta<String>(w);
				// theta
				thetas.add(theta);

				// features
				List<Feature<String>> feats = new ArrayList<Feature<String>>(featureHeaders.length);
				for (String fheader : featureHeaders) {
					Feature<String> f = new Feature<String>(fheader, s, i);
					feats.add(f);
				}
				features.add(feats);

				// trans
				goldTrans.add(getGoldTrans(goldTrans, s, FCONST.F_CHUNK, i));

				// gold labels
				goldLabels.add(w.getChunkType());
			}

			List<String> predictions = pfdmm.predict(thetas, features);

			pfdmm.perceptronLearn(predictions, goldLabels, thetas, features, goldTrans);
		}
	}

	/**
	 * At least one word exists, so loop of word will call this function.
	 * 
	 * @param goldTrans
	 * @param s
	 * @param fChunk
	 * @param i
	 */
	private String[] getGoldTrans(List<String[]> goldTrans, Sentence s, String fChunk, int i) {

		List<Word> words = s.getWords();
		int wlen = words.size();
		if (i < 0 || i > wlen - 1)
			throw new UnsupportedOperationException("add Transition index out of bound.");
		String plabel = FCONST.SENTSTART, clabel = null, nlabel = FCONST.SENTEND;
		if (i > 0)
			plabel = words.get(i - 1).getChunkType();
		if (i < wlen - 1)
			nlabel = words.get(i + 1).getChunkType();
		
		clabel = words.get(i).getChunkType();
		return new String[]{plabel,clabel,nlabel};
	}

	public static void main(String argb[]) {
		String thd = "-thd-";
		String tvd = "-tvd-";
		String[] thetaHeaders = new String[] { FCONST.T_WORD, FCONST.T_LEMMA + thd + FCONST.T_SUFFIX, FCONST.T_LEMMA,
				FCONST.T_WORDFORM + thd + FCONST.T_SUFFIX, FCONST.T_POS + thd + FCONST.T_SUFFIX, FCONST.T_POS, FCONST.DUMMY };

		String[] featureHeaders = new String[] { FCONST.F_WORD, FCONST.F_LEMMA, FCONST.F_WORDFORM, FCONST.F_POS, FCONST.F_SUFFIX,

		FCONST.pModif(FCONST.F_POS, 1), FCONST.nModif(FCONST.F_POS, 1),

		FCONST.pModif(FCONST.F_LEMMA, 1), FCONST.pModif(FCONST.F_LEMMA, 2), FCONST.nModif(FCONST.F_LEMMA, 1),
				FCONST.nModif(FCONST.F_LEMMA, 2),

				FCONST.pModif(FCONST.F_SUFFIX, 1), FCONST.nModif(FCONST.F_SUFFIX, 1),

				FCONST.pModif(FCONST.F_PREFIX, 1), FCONST.pModif(FCONST.F_PREFIX, 2),

		};
		String fhd = "-fhd-";
		String fvd = "-fvd-";

		CONLL2kFDMMPerceptronTrainer trainer = new CONLL2kFDMMPerceptronTrainer(thetaHeaders, thd, tvd, featureHeaders, fhd, fvd);

		CONLLFormatDataSet train2kData = DataFactory.getCONLL2kTrain();

		try {
			trainer.train(train2kData);
			trainer.store("CONLL2kFDMMTrainer");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected CONLL2kFDMMPerceptronTrainer self() {
		return this;
	}

}

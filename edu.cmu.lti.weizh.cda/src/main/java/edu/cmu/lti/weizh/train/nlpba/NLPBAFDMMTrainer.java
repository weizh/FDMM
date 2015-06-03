package edu.cmu.lti.weizh.train.nlpba;

import java.io.FileInputStream;
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
import edu.cmu.lti.weizh.train.AbstractFDMMTrainer;

public class NLPBAFDMMTrainer extends AbstractFDMMTrainer<String, CONLLFormatDataSet> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	NLPBAFDMMTrainer(String[] thetaHeaders, String thetaHeaderDelimiter, String thetaValueDelimiter, String[] featureHeaders,
			String featureHeaderDelimiter, String featureValueDelimiter) {
		super(thetaHeaders, thetaHeaderDelimiter, thetaValueDelimiter, featureHeaders, featureHeaderDelimiter,
				featureValueDelimiter);
		
	}

	@Override
	public void train(CONLLFormatDataSet d) throws Exception {
		for (Sentence s : d.getDocuments().get(0).getParagraphs().get(0).getSentences()) {
			List<Word> words = s.getWords();
			fdmm.add("<s>", FCONST.SENTSTART, new Feature<String>(FCONST.n(FCONST.F_NE, 1), s, -1));

			for (int i = 0; i < words.size(); i++) {
				
				Word w = words.get(i);
				String label = w.getEntityType();
				Theta<String> theta = new Theta<String>(w);
				
				List<Feature<String>> features = new ArrayList<Feature<String>>(featureHeaders.length);
				for (String fheader : featureHeaders) {
					Feature<String> f = new Feature<String>(fheader, s, i);
					features.add(f);
				}
				
				fdmm.add(theta, label, features);
				
				fdmm.add(theta, label, new Feature<String>(FCONST.n(FCONST.F_NE, 1), s, i));
				
				if (i == words.size() - 1) {
					fdmm.add(
							theta, 
							label, 
							new Feature<String>(FCONST.n(FCONST.F_NE, 1), s,s.getWords().size())
					);
				}
			}

		}
	}
	private void store(String string) {
		try{
			FileOutputStream fileOut = new FileOutputStream(string);
			ObjectOutputStream out = new ObjectOutputStream(fileOut	);
			out.writeObject(this);
			out.close();
			fileOut.close();
			System.out.println("Model serialized to "+ string);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public FDMM getFDMMModel() {
		return this.fdmm;
	}

	public static void main(String argb[]) {
		String thd = "-thd-";
		String tvd = "-tvd-";
		String[] thetaHeaders = new String[] { 
				FCONST.T_WORD, 
				FCONST.T_LEMMA + thd + FCONST.T_SUFFIX,
				FCONST.T_LEMMA, 
				FCONST.T_WORDFORM + thd + FCONST.T_SUFFIX,
//				FCONST.T_POS + thd + FCONST.T_SUFFIX,
//				FCONST.T_POS,
				FCONST.DUMMY
		};

		String[] featureHeaders = new String[] { 
				FCONST.F_WORD,
				FCONST.F_LEMMA,
				FCONST.F_WORDFORM,
//				FCONST.F_POS,
				FCONST.F_SUFFIX,
				
//				FCONST.pFeatNameModif(FCONST.F_POS, 1),
//				FCONST.nFeatNameModif(FCONST.F_POS, 1),

				FCONST.p(FCONST.F_LEMMA, 1),
				FCONST.p(FCONST.F_LEMMA, 2),
				FCONST.n(FCONST.F_LEMMA, 1),
				FCONST.n(FCONST.F_LEMMA, 2),
				
				FCONST.p(FCONST.F_SUFFIX, 1),
				FCONST.n(FCONST.F_SUFFIX, 1),
				
				FCONST.p(FCONST.F_PREFIX, 1),
				FCONST.p(FCONST.F_PREFIX, 2),
				
		};
		String fhd = "-fhd-";
		String fvd = "-fvd-";

		NLPBAFDMMTrainer trainer = new NLPBAFDMMTrainer(thetaHeaders, thd, tvd, featureHeaders, fhd,
				fvd);
		
		CONLLFormatDataSet nlpbadata = DataFactory.getNLPBATrain();
		
		try {
			trainer.train(nlpbadata);
			trainer.store("NLPBAFDMMTrainer");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static NLPBAFDMMTrainer load(String string)  {
		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(new FileInputStream(string));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NLPBAFDMMTrainer t=null;
		try {
			t = (NLPBAFDMMTrainer) is.readObject();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t;
	}

}

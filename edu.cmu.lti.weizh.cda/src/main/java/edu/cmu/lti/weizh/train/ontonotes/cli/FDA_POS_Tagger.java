package edu.cmu.lti.weizh.train.ontonotes.cli;

import java.io.IOException;
import java.util.ArrayList;

import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.feature.ONF_CONSTS;
import edu.cmu.lti.weizh.mlmodel.FDMM;
import edu.cmu.lti.weizh.nlp.Stemmer;
import edu.cmu.lti.weizh.nlp.Tagger;

public class FDA_POS_Tagger implements Tagger {

	static FDA_POS_Tagger postagger;
	private FDMM modeltagger;

	public FDA_POS_Tagger(String string) throws ClassNotFoundException, IOException {
		modeltagger = FDMM.load(string);
	}

	public static FDA_POS_Tagger getInstance() {
		if (postagger == null)
			try {
				postagger = new FDA_POS_Tagger("POS-rich-randomSent-fold10.0-NOV08.en.FDA_MLModel");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return postagger;
	}

	@Override
	public Sentence tag(Sentence sent) throws Exception {

		ArrayList<String[]> theta = new ArrayList<String[]>();
		ArrayList<String[]> features = new ArrayList<String[]>();
		ArrayList<String[]> transition = new ArrayList<String[]>();

		int position = 0;
		for (Word word : sent.getWords()) {
			int start = position++, end = start;

			// if (word.getWord().equalsIgnoreCase("there"))
			// System.out.println(word.getPOS());

			String p1tok = "[p1tok]", p2tok = "[p2tok]", p3tok = "[p3tok]";
			String n1tok = "[n1tok]", n2tok = "[n2tok]", n3tok = "[n3tok]";

			String p1cap = "[p1cap]", p2cap = "[p2cap]", p3cap = "[p3cap]";
			String n1cap = "[n1cap]", n2cap = "[n2cap]", n3cap = "[n3cap]";

			String p1suf = "[p1suf]", p2suf = "[p2suf]", p3suf = "[p3suf]";
			String n1suf = "[n1suf]", n2suf = "[n2suf]", n3suf = "[n3suf]";

			String p1form = "[p1form]", p2form = "[p2form]", p3form = "[p3form]";
			String n1form = "[n1form]", n2form = "[n2form]", n3form = "[n3form]";

			String n1type = "[n1type]";

			if (start > 0) {
				p1tok = sent.wordAt(start - 1).getTrimLowered();
				p1cap = sent.wordAt(start - 1).isCapitalizedFirst();
				p1suf = sent.wordAt(start - 1).getSuffix();
				p1form = sent.wordAt(start - 1).getWordForm();

				if (start > 1) {
					p2tok = sent.wordAt(start - 2).getTrimLowered();
					p2cap = sent.wordAt(start - 2).isCapitalizedFirst();
					p2suf = sent.wordAt(start - 2).getSuffix();
					p2form = sent.wordAt(start - 2).getWordForm();

					if (start > 2) {
						p3tok = sent.wordAt(start - 3).getTrimLowered();
						p3cap = sent.wordAt(start - 3).isCapitalizedFirst();
						p3suf = sent.wordAt(start - 3).getSuffix();
						p3form = sent.wordAt(start - 3).getWordForm();

					}
				}
			}
			String origintok = word.getWord();
			String ctok = word.getTrimLowered();
			String cunif = "[COLLAPSED]";
			String ccap = word.isCapitalizedFirst();
			String csuf = word.getSuffix();
			String cform = word.getWordForm();

			if (end < sent.size() - 1) {
				n1tok = sent.wordAt(end + 1).getTrimLowered();
				n1cap = sent.wordAt(end + 1).isCapitalizedFirst();
				n1suf = sent.wordAt(end + 1).getSuffix();
				n1form = sent.wordAt(end + 1).getWordForm();
				n1type = sent.wordAt(end + 1).getPartOfSpeech();
				if (end < sent.size() - 2) {
					n2tok = sent.wordAt(end + 2).getTrimLowered();
					n2cap = sent.wordAt(end + 2).isCapitalizedFirst();
					n2suf = sent.wordAt(end + 2).getSuffix();
					n2form = sent.wordAt(end + 2).getWordForm();
					if (end < sent.size() - 3) {
						n3tok = sent.wordAt(end + 3).getTrimLowered();
						n3cap = sent.wordAt(end + 3).isCapitalizedFirst();
						n3suf = sent.wordAt(end + 3).getSuffix();
						n3form = sent.wordAt(end + 3).getWordForm();
					}
				}
			}

			String[] thetaIndex = new String[] { origintok, ctok, Stemmer.stemTerm(ctok), csuf, cform, ccap, cunif };

			theta.add(thetaIndex);
			features.add(new String[] {
					// Global.CTOK,ctok,
					ONF_CONSTS.CSUF, csuf, ONF_CONSTS.CFORM, cform, ONF_CONSTS.CCAP, ccap,
					// Global.P1TOK,p1tok,
					// Global.P1CAP, p1cap, Global.N1CAP,n1cap, //
					// hurt.
					ONF_CONSTS.P1SUF, p1suf, ONF_CONSTS.N1SUF, n1suf
			// ,Global.P1FORM,p1form,Global.N1FORM, n1form
			});
			transition.add(new String[] { ONF_CONSTS.N1TYPE, n1type });
		}

		String[] labels = modeltagger.viterbiDecode(theta, features, transition, 1E-5, 1E-5, 1E-5, true);
		// String[] labels = fdamodel.viterbiDecode2(theta,
		// features, transition, 1E-5, 1E-5);
		for (int i = 0; i < sent.getWords().size(); i++) {
			sent.getWords().get(i).setPartOfSpeech(labels[i]);
		}

		return sent;
	}

}

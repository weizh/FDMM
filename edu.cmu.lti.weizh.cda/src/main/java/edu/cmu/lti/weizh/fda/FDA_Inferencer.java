package edu.cmu.lti.weizh.fda;

import java.awt.List;
import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import edu.cmu.lti.weizh.Interface.Inferencer;
import edu.cmu.lti.weizh.models.Global;
import edu.cmu.lti.weizh.models.NamedEntity;
import edu.cmu.lti.weizh.models.Sentence;
import edu.cmu.lti.weizh.models.Word;
import edu.cmu.lti.weizh.nlputils.BreakIterator_Java;
import edu.cmu.lti.weizh.nlputils.EuroLangTwokenizer;
import edu.cmu.lti.weizh.ontonotes.ONF_DataSet;
import edu.cmu.lti.weizh.utils.BKTree;
import edu.cmu.lti.weizh.utils.Dictionary;
import edu.cmu.lti.weizh.utils.Stemmer;
import edu.cmu.lti.weizh.utils.TreeNode;

public class FDA_Inferencer implements Inferencer<FDA_MLModel, ONF_DataSet> {

	public FDA_Inferencer(FDA_MLModel posSentModel, FDA_MLModel nerModel, BKTree bbt, Dictionary dict) {
		// TODO Auto-generated constructor stub
		this.posmodel = posSentModel;
		this.nermodel = nerModel;
		this.bbt = bbt;
		this.dict = dict;
	}

	public FDA_Inferencer() {
		// TODO Auto-generated constructor stub
	}

	public FDA_MLModel getPosmodel() {
		return posmodel;
	}

	public FDA_Inferencer setPosmodel(FDA_MLModel posmodel) {
		this.posmodel = posmodel;
		return this;
	}

	private FDA_MLModel posmodel;
	private FDA_MLModel nermodel;
	private Dictionary dict;
	private BKTree bbt;

	public static void main(String arv[]) throws Exception {

		/*
		 * load models : alltok-folds: word level NER SETN-alltok: sentence
		 * level NER POSModel-alltok: word level POS SENT-POSModel : sentence
		 * level POS.
		 */

		System.err.println("Loading NER Model:");

		FDA_MLModel nerModel = FDA_MLModel
				.load("src/main/resources/20140618_000850-alltok-fold10.0(p5-n5_pos-tok-cap)(ctok-cpos-cclps-ctype).en.FDA_MLModel");
		System.err.println("NER Done.");

		System.err.println("Loading POS Model:");
		int foldId = 0;
		FDA_MLModel posSentModel = FDA_MLModel.load("src/main/resources/POS-alltok-fold10." + foldId
				+ "(p5-n5_pos-tok-cap)(ctok-cpos-cclps-ctype).en.FDA_MLModel");
		System.err.println("POS Done.");

		System.err.println("Loading Dict :");
		Dictionary dict = new Dictionary("src/main/resources/en-dict.txt");
		System.err.println("Dict Done.");

		System.err.println("Loading Misspell Parser: (8 million in total.)");
		BKTree bbt = new BKTree();
		bbt.init("GeoNames/allCountries.txt");
		System.err.println("Misspell done.\n All Done. Try it out :) ");

		FDA_Inferencer inferencer = new FDA_Inferencer(posSentModel, nerModel, bbt, dict);

//		 inferencer.cmdlineInfer(System.in);

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		System.out.print(">");
		while ((line = br.readLine()) != null) {
			ArrayList<NamedEntity> entities = inferencer.infer(line);
			for (NamedEntity e : entities) {
				System.out.println(e);
			}
		}

	}

	public ArrayList<NamedEntity> infer(String line) {

		Sentence tokdsent = BreakIterator_Java.tokenize(new Sentence(line));
		Sentence posedsent = tagSentPOS(tokdsent);
		Sentence neredsent = tagSentNE(posedsent);
		for (Word w : neredsent.getWords()) {
			String sword = w.getLemma();
			if (!dict.contains(sword))
				if (w.getEntityType().equals("LOC") || w.getEntityType().equals("FAC") || w.getEntityType().equals("GPE"))
					if (!bbt.inGaz(w.getWord())) {
						TreeNode s = bbt.findBest(w.getWord());
						System.out
								.println((s == null ? w.getWord() + " (Not Corrected.)" : (Character.isUpperCase(w.getWord()
										.charAt(0)) ? w.getWord().charAt(0) + s.s.substring(1) + " (Corrected.)" : s.s
										+ " (Corrected.)"))
										+ "\t" + w.getPOS() + "\t" + w.getEntityType());
						if (s != null) {
							w.setCorrected((Character.isUpperCase(w.getWord().charAt(0)) ? w.getWord().charAt(0) + s.s.substring(1)
									: s.s));
						}

						continue;
					}
			System.out.println(w.getWord() + "\t" + w.getPOS() + "\t" + w.getEntityType());
		}

		ArrayList<NamedEntity> locs = new ArrayList<NamedEntity>();
		int startpos = -1, endpos = -1;
		String hist = null;
		for (int k = 0; k < neredsent.getWords().size(); k++) {
			String current = neredsent.getWords().get(k).getEntityType();
			if (k == 0) {
				hist = current;
				startpos = 0;
				endpos = 0;
			} else if (k == neredsent.getWords().size() - 1) {
				if (hist.equals(current)) {
					endpos = k;
					// create hist+current entity
				if(!hist.equals("[O]"))	locs.add(new NamedEntity(hist, startpos, endpos, neredsent));
				} else {
					// create hist entity
					if(!hist.equals("[O]")) locs.add(new NamedEntity(hist, startpos, endpos, neredsent));
					// create current single token entity
					if(!current.equals("[O]")) locs.add(new NamedEntity(current, k, k, neredsent));
				}
			} else {
				if (hist.equals(current)) {
					endpos = k;
				} else {
					// create start to end entity
					if(!hist.equals("[O]")) locs.add(new NamedEntity(hist, startpos, endpos, neredsent));

					startpos = k;
					endpos = k;
					hist = current;
				}
			}
		}
		
//		for (Word w : neredsent.getWords()) {
//			String sword = w.getLemma();
//			if (!dict.contains(sword))
//				if (w.getEntityType().equals("LOC") || w.getEntityType().equals("FAC") || w.getEntityType().equals("GPE"))
//					if (!bbt.inGaz(w.getWord())) {
//						TreeNode s = bbt.findBest(w.getWord());
//						System.out
//								.println((s == null ? w.getWord() + " (Not Corrected.)" : (Character.isUpperCase(w.getWord()
//										.charAt(0)) ? w.getWord().charAt(0) + s.s.substring(1) + " (Corrected.)" : s.s
//										+ " (Corrected.)"))
//										+ "\t" + w.getPOS() + "\t" + w.getEntityType());
//						if (s != null) {
//							w.setCorrected((Character.isUpperCase(w.getWord().charAt(0)) ? w.getWord().charAt(0) + s.s.substring(1)
//									: s.s));
//						}
//
//						continue;
//					}
//			System.out.println(w.getWord() + "\t" + w.getPOS() + "\t" + w.getEntityType());
//		}
		
		return locs;
	}

	private void cmdlineInfer(InputStream in) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line = null;
		System.out.print(">");
		while ((line = br.readLine()) != null) {
			Sentence tokdsent = EuroLangTwokenizer.tokenize(new Sentence(line));
			Sentence posedsent = tagSentPOS(tokdsent);
			Sentence neredsent = tagSentNE(posedsent);
			for (Word w : neredsent.getWords()) {
				String sword = w.getLemma();
				if (!dict.contains(sword))
					if (w.getEntityType().equals("LOC") || w.getEntityType().equals("FAC") || w.getEntityType().equals("GPE"))
						if (!bbt.inGaz(w.getWord())) {
							TreeNode s = bbt.findBest(w.getWord());
							System.out.println((s == null ? w.getWord() + " (Not Corrected.)" : (Character.isUpperCase(w
									.getWord().charAt(0)) ? w.getWord().charAt(0) + s.s.substring(1) + " (Corrected.)" : s.s
									+ " (Corrected.)"))
									+ "\t" + w.getPOS() + "\t" + w.getEntityType());
							continue;
						}
				System.out.println(w.getWord() + "\t" + w.getPOS() + "\t" + w.getEntityType());

			}
			System.out.print(">");
		}
	}

	public Sentence tagSentNE(Sentence sent) {

		int wordposition = 0;
		for (Word word : sent.getWords()) {

			int start = wordposition, end = wordposition++;

			// entity starts the sentence
			String p1tok = "[p1tok]", p2tok = "[p2tok]", p3tok = "[p3tok]";
			String n1tok = "[n1tok]", n2tok = "[n2tok]", n3tok = "[n3tok]";

			String p1pos = "[p1pos]", p2pos = "[p2pos]", p3pos = "[p3pos]";
			String n1pos = "[n1pos]", n2pos = "[n2pos]", n3pos = "[n3pos]";

			String p1cap = "[p1cap]", p2cap = "[p2cap]", p3cap = "[p3cap]";
			String n1cap = "[n1cap]", n2cap = "[n2cap]", n3cap = "[n3cap]";

			String p1suf = "[p1suf]", p2suf = "[p2suf]", p3suf = "[p3suf]";
			String n1suf = "[n1suf]", n2suf = "[n2suf]", n3suf = "[n3suf]";

			String p1pref = "[p1pref]", p2pref = "[p2pref]", p3pref = "[p3pref]";
			String n1pref = "[n1pref]", n2pref = "[n2pref]", n3pref = "[n3pref]";

			String p1form = "[p1form]", p2form = "[p2form]", p3form = "[p3form]";
			String n1form = "[n1form]", n2form = "[n2form]", n3form = "[n3form]";

			String p1type = "[p1type]", p2type = "[p2type]", p3type = "[p3type]";

			if (start > 0) {
				p1tok = sent.wordAt(start - 1).getLemma();
				p1pos = sent.wordAt(start - 1).getPOS();
				p1cap = sent.wordAt(start - 1).getCap();
				p1suf = sent.wordAt(start - 1).getSuffix();
				p1pref = sent.wordAt(start - 1).getPreffix();
				p1form = sent.wordAt(start - 1).getWordForm();
				p1type = sent.wordAt(start - 1).getPrediction();
				if (start > 1) {
					p2tok = sent.wordAt(start - 2).getLemma();
					p2pos = sent.wordAt(start - 2).getPOS();
					p2cap = sent.wordAt(start - 2).getCap();
					p2suf = sent.wordAt(start - 2).getSuffix();
					p2pref = sent.wordAt(start - 2).getPreffix();
					p2form = sent.wordAt(start - 2).getWordForm();
					p2type = sent.wordAt(start - 2).getPrediction();
					if (start > 2) {
						p3tok = sent.wordAt(start - 3).getLemma();
						p3pos = sent.wordAt(start - 3).getPOS();
						p3cap = sent.wordAt(start - 3).getCap();
						p3suf = sent.wordAt(start - 3).getSuffix();
						p3pref = sent.wordAt(start - 3).getPreffix();
						p3form = sent.wordAt(start - 3).getWordForm();
						p3type = sent.wordAt(start - 3).getPrediction();
					}
				}
			}

			String ctok = word.getLemma();
			String cpos = word.getPOS();
			String ccap = word.getCap();
			String csuf = word.getSuffix();
			String cpref = word.getPreffix();
			String cform = word.getWordForm();

			if (end < sent.size() - 1) {
				n1tok = sent.wordAt(end + 1).getLemma();
				n1pos = sent.wordAt(end + 1).getPOS();
				n1cap = sent.wordAt(end + 1).getCap();
				n1suf = sent.wordAt(end + 1).getSuffix();
				n1pref = sent.wordAt(end + 1).getPreffix();
				n1form = sent.wordAt(end + 1).getWordForm();
				if (end < sent.size() - 2) {
					n2tok = sent.wordAt(end + 2).getLemma();
					n2pos = sent.wordAt(end + 2).getPOS();
					n2cap = sent.wordAt(end + 2).getCap();
					n2suf = sent.wordAt(end + 2).getSuffix();
					n2pref = sent.wordAt(end + 2).getPreffix();
					n2form = sent.wordAt(end + 2).getWordForm();
					if (end < sent.size() - 3) {
						n3tok = sent.wordAt(end + 3).getLemma();
						n3pos = sent.wordAt(end + 3).getPOS();
						n3cap = sent.wordAt(end + 3).getCap();
						n3suf = sent.wordAt(end + 3).getSuffix();
						n3pref = sent.wordAt(end + 3).getPreffix();
						n3form = sent.wordAt(end + 3).getWordForm();
					}
				}
			}

			String[] thetaIndex = new String[] { Stemmer.stemTerm(ctok), cform + "_" + cpos, cpos };
			String label = null;
			try {
				label = this.nermodel.predict(thetaIndex, 1E-5, 1E-5, Global.CCAP, ccap, Global.CPOS, cpos, Global.CSUF, csuf,
						Global.CFORM, cform, Global.P1TYPE, p1type, Global.P2TYPE, p2type,
						// Global.P3TYPE, p3type,
						Global.P1TOK, Stemmer.stemTerm(p1tok), Global.N1TOK, Stemmer.stemTerm(n1tok), Global.P2TOK,
						Stemmer.stemTerm(p2tok), Global.N2TOK, Stemmer.stemTerm(n2tok),
						// Global.P1SUF, p1suf, Global.N1SUF, n1suf,

						// Global.P1FORM, p1form, Global.N1FORM, n1form,

						Global.P1CAP, p1cap, Global.N1CAP, n1cap

						// , Global.P1POS, p1pos
						// , Global.N1POS, n1pos

						, Global.P2CAP, p2cap
				// , Global.N2CAP, n2cap
				// , Global.P2POS, p2pos
				// , Global.N2POS, n2pos

						// , Global.P3TOK, Stemmer.stemTerm( p3tok)
						// , Global.N3TOK, Stemmer.stemTerm( n3tok)
						// , Global.P3CAP, p3cap
						// , Global.N3CAP, n3cap
						// , Global.P3POS, p3pos
						// , Global.N3POS, n3pos

						);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			word.setEntityType(label);
		}
		return sent;
	}

	public Sentence tagSentPOS(Sentence sent) {

		int position = 0;
		for (Word word : sent.getWords()) {
			int start = position++;
			int end = start;
			// if (word.getWord().equalsIgnoreCase("there"))
			// System.out.println(word.getPOS());

			// entity starts the sentence
			String p1cap = "[p1cap]", p2cap = "[p2cap]", p3cap = "[p3cap]";
			String n1cap = "[n1cap]", n2cap = "[n2cap]", n3cap = "[n3cap]";

			String p1suf = "[p1suf]", p2suf = "[p2suf]", p3suf = "[p3suf]";
			String n1suf = "[n1suf]", n2suf = "[n2suf]", n3suf = "[n3suf]";

			String p1form = "[p1form]", p2form = "[p2form]", p3form = "[p3form]";
			String n1form = "[n1form]", n2form = "[n2form]", n3form = "[n3form]";

			String p1type = "[p1type]", p2type = "[p2type]", p3type = "[p3type]";

			String p1end = "[p1end]", p3end = "[p3end]", p2end = "[p2end]";
			String n1end = "[n1end]", n3end = "[n3end]", n2end = "[n2end]";

			if (start > 0) {
				p1cap = sent.wordAt(start - 1).getCap();
				p1suf = sent.wordAt(start - 1).getSuffix();
				p1form = sent.wordAt(start - 1).getWordForm();
				p1type = sent.wordAt(start - 1).getPrediction();
				p1end = sent.wordAt(start - 1).getSuffix(1);

				if (start > 1) {
					p2cap = sent.wordAt(start - 2).getCap();
					p2suf = sent.wordAt(start - 2).getSuffix();
					p2form = sent.wordAt(start - 2).getWordForm();
					p2type = sent.wordAt(start - 2).getPrediction();
					p2end = sent.wordAt(start - 2).getSuffix(1);

					if (start > 2) {
						p3cap = sent.wordAt(start - 3).getCap();
						p3suf = sent.wordAt(start - 3).getSuffix();
						p3form = sent.wordAt(start - 3).getWordForm();
						p3type = sent.wordAt(start - 3).getPrediction();
						p3end = sent.wordAt(start - 3).getSuffix(1);

					}
				}
			}

			String ctok = word.getLemma();
			String cunif = "[COLLAPSED]";
			String ccap = word.getCap();
			String csuf = word.getSuffix();
			String cform = word.getWordForm();

			if (end < sent.size() - 1) {
				n1cap = sent.wordAt(end + 1).getCap();
				n1suf = sent.wordAt(end + 1).getSuffix();
				n1form = sent.wordAt(end + 1).getWordForm();
				n1end = sent.wordAt(end + 1).getSuffix(1);

				if (end < sent.size() - 2) {
					n2cap = sent.wordAt(end + 2).getCap();
					n2suf = sent.wordAt(end + 2).getSuffix();
					n2form = sent.wordAt(end + 2).getWordForm();
					n2end = sent.wordAt(end + 2).getSuffix(1);

					if (end < sent.size() - 3) {
						n3cap = sent.wordAt(end + 3).getCap();
						n3suf = sent.wordAt(end + 3).getSuffix();
						n3form = sent.wordAt(end + 3).getWordForm();
						n3end = sent.wordAt(end + 3).getSuffix(1);

					}
				}
			}

			String[] thetaIndex = new String[] { Stemmer.stemTerm(ctok), ccap, cunif };
			String label = null;
			try {
				label = this.posmodel.predict(thetaIndex, 1E-5, 1E-5, Global.CSUF, csuf
				// ,Global.P1END,p1end
				// ,Global.P1TYPE,p1type
				// ,Global.P2TYPE,p2type
				// ,Global.P3TYPE,p3type
				// ,Global.CFORM, cform
				// , Global.CCAP, ccap
				// , Global.P1CAP, p1cap, Global.N1CAP,n1cap
						, Global.P1SUF, p1suf, Global.N1SUF, n1suf, Global.P2SUF, p2suf, Global.N2SUF, n2suf

				);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			word.setPos(label);
		}
		return sent;
	}

	@Override
	public void infer(FDA_MLModel model, ONF_DataSet d) throws Exception {
		// TODO Auto-generated method stub

	}

}

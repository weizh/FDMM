package edu.cmu.lti.weizh.fda;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import edu.cmu.lti.weizh.Interface.Trainer;
import edu.cmu.lti.weizh.models.Document;
import edu.cmu.lti.weizh.models.Global;
import edu.cmu.lti.weizh.models.NamedEntity;
import edu.cmu.lti.weizh.models.Paragraph;
import edu.cmu.lti.weizh.models.Sentence;
import edu.cmu.lti.weizh.models.Word;
import edu.cmu.lti.weizh.ontonotes.ONFImporter;
import edu.cmu.lti.weizh.ontonotes.ONF_DataSet;
import edu.cmu.lti.weizh.utils.Stemmer;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;

public class FDA_Trainer implements Trainer<FDA_MLModel, ONF_DataSet> {
	TObjectIntHashMap<String> posRules;

	public static void main(String argv[]) throws Exception {

		FDA_Trainer fdatrainer = new FDA_Trainer();

		ONF_DataSet fdad = new ONF_DataSet();
		String path = "/home/wei/Documents/annotations";
		ONFImporter.fill(new File(path), fdad);

		String mode = "NER";
		double fold = 10;
		String date = "NOV08";

		String foldFile =  "10-randomSent-CVfolds-OCT25.txt";
		String modelFile = mode+"10-rich-randomSent-fold10.";

		// if (trainGran.equals("randomWord"))
		// fdad.crossValidationRandomGenerator((int)fold, foldFile);
		// else
		// fdad.sentenceCrossValidationRandomGenerator((int)fold, foldFile);

		fdad.fillCVFolds(foldFile);

		for (int i = 0; i < (int) fold; i++) {
			System.out.println("training " + mode + " fold :" + i);

			FDA_MLModel fdamodel = null;

			if (mode.equals("POS"))
				fdamodel = fdatrainer.trainPOS(fdad, fold, i);
			else if (mode.equals("NER"))
				fdamodel = fdatrainer.trainNERRich(fdad, fold, i);

			if (fdamodel != null)
				fdamodel.store(modelFile + i + "-" + date + ".en.FDA_MLModel");
			break;
		}
	}

	class PosRule {
		String rule;
		int val;

		public PosRule(String r, int v) {
			rule = r;
			val = v;
		}
	}

	private void storePosRules(String string) throws IOException {

		ArrayList<PosRule> pr = new ArrayList<PosRule>();
		TObjectIntIterator<String> iter = this.posRules.iterator();
		for (int i = 0; i < this.posRules.size(); i++) {
			iter.advance();
			pr.add(new PosRule(iter.key(), iter.value()));
		}

		Collections.sort(pr, new Comparator<PosRule>() {
			public int compare(PosRule p1, PosRule p2) {
				return p1.val - p2.val;
			}
		});
		BufferedWriter bw = new BufferedWriter(new FileWriter(string));
		for (PosRule rule : pr) {
			bw.write(rule.rule + "\t" + rule.val + "\n");
		}
		bw.close();
	}

	FDA_Trainer() {
		this.posRules = new TObjectIntHashMap<String>();
	}

	/**
	 * Train POS with random tokens.
	 * 
	 * @param fdadata
	 * @param fold
	 * @param ifold
	 * @return
	 * @throws Exception
	 */
	public FDA_MLModel trainPOS(ONF_DataSet fdadata, double fold, int ifold) throws Exception {

		FDA_MLModel fdamodel = new FDA_MLModel();
		/*
		 * populate the dataset into the model.
		 */
		for (Document doc : fdadata.getDocuments()) {
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {
					List<Word> words = sent.getWords();
					/*
					 * get the named entities.
					 */
					List<NamedEntity> nes = sent.getNamedEntities();

					int wordposition = 0;
					for (Word word : words) {

						int start = wordposition, end = wordposition++;

						if (ifold != Double.POSITIVE_INFINITY)
							if (word.getBooleanFolds()[ifold] == false)
								continue;

						// entity starts the sentence
						String p1tok = "[p1tok]", p2tok = "[p2tok]", p3tok = "[p3tok]";
						String n1tok = "[n1tok]", n2tok = "[n2tok]", n3tok = "[n3tok]";

						String p1cap = "[p1cap]", p2cap = "[p2cap]", p3cap = "[p3cap]";
						String n1cap = "[n1cap]", n2cap = "[n2cap]", n3cap = "[n3cap]";

						String p1suf = "[p1suf]", p2suf = "[p2suf]", p3suf = "[p3suf]";
						String n1suf = "[n1suf]", n2suf = "[n2suf]", n3suf = "[n3suf]";

						String p1form = "[p1form]", p2form = "[p2form]", p3form = "[p3form]";
						String n1form = "[n1form]", n2form = "[n2form]", n3form = "[n3form]";

						String p1type = "[p1type]", p2type = "[p2type]", p3type = "[p3type]";
						String n1type = "[p1type]", n2type = "[p2type]", n3type = "[p3type]";

						if (start > 0) {
							p1tok = sent.wordAt(start - 1).getLemma();
							p1cap = sent.wordAt(start - 1).getCap();
							p1suf = sent.wordAt(start - 1).getSuffix();
							p1form = sent.wordAt(start - 1).getWordForm();
							p1type = sent.wordAt(start - 1).getPOS();
							if (start > 1) {
								p2tok = sent.wordAt(start - 2).getLemma();
								p2cap = sent.wordAt(start - 2).getCap();
								p2suf = sent.wordAt(start - 2).getSuffix();
								p2form = sent.wordAt(start - 2).getWordForm();
								p2type = sent.wordAt(start - 2).getPOS();
								if (start > 2) {
									p3tok = sent.wordAt(start - 3).getLemma();
									p3cap = sent.wordAt(start - 3).getCap();
									p3suf = sent.wordAt(start - 3).getSuffix();
									p3form = sent.wordAt(start - 3).getWordForm();
									p3type = sent.wordAt(start - 3).getPOS();
								}
							}
						}

						String origintok = word.getWord();
						String ctok = word.getLemma();
						String cunif = "[COLLAPSED]";
						String ccap = word.getCap();
						String csuf = word.getSuffix();
						String cform = word.getWordForm();

						if (end < sent.size() - 1) {
							n1tok = sent.wordAt(end + 1).getLemma();
							n1cap = sent.wordAt(end + 1).getCap();
							n1suf = sent.wordAt(end + 1).getSuffix();
							n1form = sent.wordAt(end + 1).getWordForm();
							n1type = sent.wordAt(end + 1).getPOS();

							if (end < sent.size() - 2) {
								n2tok = sent.wordAt(end + 2).getLemma();
								n2cap = sent.wordAt(end + 2).getCap();
								n2suf = sent.wordAt(end + 2).getSuffix();
								n2form = sent.wordAt(end + 2).getWordForm();
								n2type = sent.wordAt(end + 2).getPOS();

								if (end < sent.size() - 3) {
									n3tok = sent.wordAt(end + 3).getLemma();
									n3cap = sent.wordAt(end + 3).getCap();
									n3suf = sent.wordAt(end + 3).getSuffix();
									n3form = sent.wordAt(end + 3).getWordForm();
									n3type = sent.wordAt(end + 3).getPOS();
								}
							}
						}
						// theta is for : stemmed token, token (lowered), DUMMY,
						// Capitalization, suffix, complex word form.
						for (String s : new String[] { origintok, Stemmer.stemTerm(ctok), ctok, cunif, ccap, csuf, cform }) {
							fdamodel.add(s, word.getPOS(), Global.P1TOK, Stemmer.stemTerm(p1tok));
							fdamodel.add(s, word.getPOS(), Global.P2TOK, Stemmer.stemTerm(p2tok));
							fdamodel.add(s, word.getPOS(), Global.P3TOK, Stemmer.stemTerm(p3tok));

							fdamodel.add(s, word.getPOS(), Global.N1TOK, Stemmer.stemTerm(n1tok));
							fdamodel.add(s, word.getPOS(), Global.N2TOK, Stemmer.stemTerm(n2tok));
							fdamodel.add(s, word.getPOS(), Global.N3TOK, Stemmer.stemTerm(n3tok));

							fdamodel.add(s, word.getPOS(), Global.P1CAP, p1cap);
							fdamodel.add(s, word.getPOS(), Global.P2CAP, p2cap);
							fdamodel.add(s, word.getPOS(), Global.P3CAP, p3cap);
							fdamodel.add(s, word.getPOS(), Global.N1CAP, n1cap);
							fdamodel.add(s, word.getPOS(), Global.N2CAP, n2cap);
							fdamodel.add(s, word.getPOS(), Global.N3CAP, n3cap);

							fdamodel.add(s, word.getPOS(), Global.P1SUF, p1suf);
							fdamodel.add(s, word.getPOS(), Global.P2SUF, p2suf);
							fdamodel.add(s, word.getPOS(), Global.P3SUF, p3suf);
							fdamodel.add(s, word.getPOS(), Global.N1SUF, n1suf);
							fdamodel.add(s, word.getPOS(), Global.N2SUF, n2suf);
							fdamodel.add(s, word.getPOS(), Global.N3SUF, n3suf);

							fdamodel.add(s, word.getPOS(), Global.P1FORM, p1form);
							fdamodel.add(s, word.getPOS(), Global.P2FORM, p2form);
							fdamodel.add(s, word.getPOS(), Global.P3FORM, p3form);
							fdamodel.add(s, word.getPOS(), Global.N1FORM, n1form);
							fdamodel.add(s, word.getPOS(), Global.N2FORM, n2form);
							fdamodel.add(s, word.getPOS(), Global.N3FORM, n3form);

							fdamodel.add(s, word.getPOS(), Global.ORIGIN_CTOK, origintok);
							fdamodel.add(s, word.getPOS(), Global.CTOK, ctok);
							fdamodel.add(s, word.getPOS(), Global.CTOK_STEMMED, Stemmer.stemTerm(ctok));
							fdamodel.add(s, word.getPOS(), Global.CCAP, ccap);
							fdamodel.add(s, word.getPOS(), Global.CSUF, csuf);
							fdamodel.add(s, word.getPOS(), Global.CFORM, cform);

							fdamodel.add(s, word.getPOS(), Global.P1TYPE, p1type);
							fdamodel.add(s, word.getPOS(), Global.P2TYPE, p2type);
							fdamodel.add(s, word.getPOS(), Global.P3TYPE, p3type);

							fdamodel.add(s, word.getPOS(), Global.N1TYPE, n1type);
							fdamodel.add(s, word.getPOS(), Global.N2TYPE, n2type);
							fdamodel.add(s, word.getPOS(), Global.N3TYPE, n3type);

						}

						/*
						 * Sent start is stored in the LID and LID2LString. sent
						 * end is stored in DID, which is a feature value. TOTAL
						 * is stored in both LID and DID, but not LID2String
						 * 
						 * So, in sum,
						 * 
						 * LID: Sent start, Label total; DID: sent end, feature
						 * total; LID2String: sent start.
						 */
						// Sentence start and end symbols are used as both
						// token, and type.
						if (start == 0) {// first word in the sentence, so add
											// the sentence begin.
							// only add transitional probability to feature.
							fdamodel.add("<s>", Global.SENTSTART, Global.N1TYPE, word.getPOS());
						}
						if (start == sent.getWords().size() - 1)
						{
							// only add transitional probability to feature.
							for (String s : new String[] { origintok, Stemmer.stemTerm(ctok), ctok, cunif, ccap, csuf, cform }) {
								fdamodel.add(s, word.getPOS(), Global.N1TYPE, Global.SENTEND);
							}

						}
					}
				}
			}
		}
		return fdamodel;

	}

	/**
	 * Train NER with random tokens. if fold ==infinity, all data is train data.
	 * if fold ==1, it means all data is test data.
	 */

	public FDA_MLModel trainNERRich(ONF_DataSet fdadata, double fold, int ifold) throws Exception {
		FDA_MLModel fdamodel = new FDA_MLModel();
		/*
		 * populate the dataset into the model.
		 */
		for (Document doc : fdadata.getDocuments()) {
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {
					List<Word> words = sent.getWords();
					/*
					 * get the named entities.
					 */
					List<NamedEntity> nes = sent.getNamedEntities();

					int wordposition = 0;
					for (Word word : words) {
						int start = wordposition, end = wordposition++;

						if (ifold != Double.POSITIVE_INFINITY)
							if (word.getBooleanFolds()[ifold] == false)
								continue;

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
						String n1type = "[n1type]", n2type = "[n2type]", n3type = "[n3type]";

						if (start > 0) {
							p1tok = sent.wordAt(start - 1).getLemma();
							p1pos = sent.wordAt(start - 1).getPOS();
							p1cap = sent.wordAt(start - 1).getCap();
							p1suf = sent.wordAt(start - 1).getSuffix();
							p1pref = sent.wordAt(start - 1).getPreffix();
							p1form = sent.wordAt(start - 1).getWordForm();
							p1type = sent.wordAt(start - 1).getEntityType();
							if (start > 1) {
								p2tok = sent.wordAt(start - 2).getLemma();
								p2pos = sent.wordAt(start - 2).getPOS();
								p2cap = sent.wordAt(start - 2).getCap();
								p2suf = sent.wordAt(start - 2).getSuffix();
								p2pref = sent.wordAt(start - 2).getPreffix();
								p2form = sent.wordAt(start - 2).getWordForm();
								p2type = sent.wordAt(start - 2).getEntityType();
								if (start > 2) {
									p3tok = sent.wordAt(start - 3).getLemma();
									p3pos = sent.wordAt(start - 3).getPOS();
									p3cap = sent.wordAt(start - 3).getCap();
									p3suf = sent.wordAt(start - 3).getSuffix();
									p3pref = sent.wordAt(start - 3).getPreffix();
									p3form = sent.wordAt(start - 3).getWordForm();
									p3type = sent.wordAt(start - 3).getEntityType();
								}
							}
						}

						String origintok = word.getWord();
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
							n1type = sent.wordAt(end+1).getEntityType();
							if (end < sent.size() - 2) {
								n2tok = sent.wordAt(end + 2).getLemma();
								n2pos = sent.wordAt(end + 2).getPOS();
								n2cap = sent.wordAt(end + 2).getCap();
								n2suf = sent.wordAt(end + 2).getSuffix();
								n2pref = sent.wordAt(end + 2).getPreffix();
								n2form = sent.wordAt(end + 2).getWordForm();
								n2type = sent.wordAt(end+2).getEntityType();

								if (end < sent.size() - 3) {
									n3tok = sent.wordAt(end + 3).getLemma();
									n3pos = sent.wordAt(end + 3).getPOS();
									n3cap = sent.wordAt(end + 3).getCap();
									n3suf = sent.wordAt(end + 3).getSuffix();
									n3pref = sent.wordAt(end + 3).getPreffix();
									n3form = sent.wordAt(end + 3).getWordForm();
									n3type = sent.wordAt(end+3).getEntityType();

								}
							}
						}
						String sp1tok = Stemmer.stemTerm(p1tok);
						String sp2tok = Stemmer.stemTerm(p2tok);
						String sp3tok = Stemmer.stemTerm(p3tok);
						String sn1tok = Stemmer.stemTerm(n1tok);
						String sn2tok = Stemmer.stemTerm(n2tok);
						String sn3tok = Stemmer.stemTerm(n3tok);
						String sctok = Stemmer.stemTerm(ctok);

						for (String s : new String[] { origintok, ctok, Stemmer.stemTerm(ctok), cpos + "_" + csuf, cpos,
								ccap + "_" + csuf, csuf, cpref, cform, ccap, "[DUMMY]" }) {
							fdamodel.add(s, word.getEntityType(), Global.P1TOK, sp1tok);
							fdamodel.add(s, word.getEntityType(), Global.P2TOK, sp2tok);
							fdamodel.add(s, word.getEntityType(), Global.P3TOK, sp3tok);

							fdamodel.add(s, word.getEntityType(), Global.N1TOK, sn1tok);
							fdamodel.add(s, word.getEntityType(), Global.N2TOK, sn2tok);
							fdamodel.add(s, word.getEntityType(), Global.N3TOK, sn3tok);

							fdamodel.add(s, word.getEntityType(), Global.P1POS, p1pos);
							fdamodel.add(s, word.getEntityType(), Global.P2POS, p2pos);
							fdamodel.add(s, word.getEntityType(), Global.P3POS, p3pos);
							fdamodel.add(s, word.getEntityType(), Global.N1POS, n1pos);
							fdamodel.add(s, word.getEntityType(), Global.N2POS, n2pos);
							fdamodel.add(s, word.getEntityType(), Global.N3POS, n3pos);

							fdamodel.add(s, word.getEntityType(), Global.P1CAP, p1cap);
							fdamodel.add(s, word.getEntityType(), Global.P2CAP, p2cap);
							fdamodel.add(s, word.getEntityType(), Global.P3CAP, p3cap);
							fdamodel.add(s, word.getEntityType(), Global.N1CAP, n1cap);
							fdamodel.add(s, word.getEntityType(), Global.N2CAP, n2cap);
							fdamodel.add(s, word.getEntityType(), Global.N3CAP, n3cap);

							fdamodel.add(s, word.getEntityType(), Global.P1SUF, p1suf);
							fdamodel.add(s, word.getEntityType(), Global.P2SUF, p2suf);
							fdamodel.add(s, word.getEntityType(), Global.P3SUF, p3suf);
							fdamodel.add(s, word.getEntityType(), Global.N1SUF, n1suf);
							fdamodel.add(s, word.getEntityType(), Global.N2SUF, n2suf);
							fdamodel.add(s, word.getEntityType(), Global.N3SUF, n3suf);

							fdamodel.add(s, word.getEntityType(), Global.P1PREF, p1pref);
							fdamodel.add(s, word.getEntityType(), Global.P2PREF, p2pref);
							fdamodel.add(s, word.getEntityType(), Global.P3PREF, p3pref);
							fdamodel.add(s, word.getEntityType(), Global.N1PREF, n1pref);
							fdamodel.add(s, word.getEntityType(), Global.N2PREF, n2pref);
							fdamodel.add(s, word.getEntityType(), Global.N3PREF, n3pref);

							fdamodel.add(s, word.getEntityType(), Global.P1FORM, p1form);
							fdamodel.add(s, word.getEntityType(), Global.P2FORM, p2form);
							fdamodel.add(s, word.getEntityType(), Global.P3FORM, p3form);
							fdamodel.add(s, word.getEntityType(), Global.N1FORM, n1form);
							fdamodel.add(s, word.getEntityType(), Global.N2FORM, n2form);
							fdamodel.add(s, word.getEntityType(), Global.N3FORM, n3form);

							fdamodel.add(s, word.getEntityType(), Global.CCAP, ccap);
							fdamodel.add(s, word.getEntityType(), Global.CPOS, cpos);
							fdamodel.add(s, word.getEntityType(), Global.CTOK, ctok);
							fdamodel.add(s, word.getEntityType(), Global.CTOK_STEMMED, sctok);
							fdamodel.add(s, word.getEntityType(), Global.ORIGIN_CTOK, origintok);
							fdamodel.add(s, word.getEntityType(), Global.CSUF, csuf);
							fdamodel.add(s, word.getEntityType(), Global.CPREF, cpref);
							fdamodel.add(s, word.getEntityType(), Global.CFORM, cform);

							// add previous label feature.
							fdamodel.add(s, word.getEntityType(), Global.P1TYPE, p1type);
							fdamodel.add(s, word.getEntityType(), Global.P2TYPE, p2type);
							fdamodel.add(s, word.getEntityType(), Global.P3TYPE, p3type);
							
							fdamodel.add(s, word.getEntityType(), Global.N1TYPE, n1type);
							fdamodel.add(s, word.getEntityType(), Global.N2TYPE, n2type);
							fdamodel.add(s, word.getEntityType(), Global.N3TYPE, n3type);
						}

						/*
						 * Sent start is stored in the LID and LID2LString. sent
						 * end is stored in DID, which is a feature value. TOTAL
						 * is stored in both LID and DID, but not LID2String
						 * 
						 * So, in sum,
						 * 
						 * LID: Sent start, Label total; DID: sent end, feature
						 * total; LID2String: sent start.
						 */
						// Sentence start and end symbols are used as both
						// token, and type.
						if (start == 0) {// first word in the sentence, so add
											// the sentence begin.
							// only add transitional probability to feature.
							fdamodel.add("<s>", Global.SENTSTART, Global.N1TYPE, word.getEntityType());
						}
						if (start == sent.getWords().size() - 1)
						{
							// only add transitional probability to feature.
							for (String s : new String[] { origintok, ctok, Stemmer.stemTerm(ctok), cpos + "_" + csuf, cpos,
									ccap + "_" + csuf, csuf, cpref, cform, ccap, "[DUMMY]" }) {
								fdamodel.add(s, word.getEntityType(), Global.N1TYPE, Global.SENTEND);
							}
						}
					}
				}
			}
		}
		return fdamodel;
	}

	@Override
	public FDA_MLModel train(ONF_DataSet d, double fold, int foldId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}

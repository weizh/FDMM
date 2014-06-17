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
		String path = "C:/Users/Wynn/Documents/wynnzh/data/LDC/data/english/annotations/";
		ONFImporter.fill(new File(path), fdad);

		boolean richfeature = true;
		String mode = "POS";
		double fold = 10;
		
		
		if (mode.equals("POS")) {
			fdad.fillCVFolds("10-POSMODEL-split.txt");
		} else if (mode.equals("NER")) {
			fdad.fillCVFolds("10-crf-split.txt");
		}

		// fdatrainer.storePosRules("posrules.txt");

		for (int i = 0; i < (int) fold; i++) {
			System.out.println("training " + mode + " fold :" + i);

			FDA_MLModel fdamodel;

			if (mode.equals("POS")) {
				fdamodel = fdatrainer.trainPOS(fdad, fold, i);
				fdamodel.store("POS-alltok-fold" + (int) fold + "." + i
						+ "(p5-n5_pos-tok-cap)(ctok-cpos-cclps-ctype).en.FDA_MLModel");
			} else if (mode.equals("NER")) {
				if (richfeature)
					fdamodel = fdatrainer.trainRich(fdad, fold, i);
				else
					fdamodel = fdatrainer.train(fdad, fold, i);

				fdamodel.store((richfeature ? "rich-" : "") + "alltok-fold" + (int) fold + "." + i
						+ "(p5-n5_pos-tok-cap)(ctok-cpos-cclps-ctype).en.FDA_MLModel");

			}

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

						String p1end = "[p1end]",p3end = "[p3end]",p2end = "[p2end]";
						String n1end = "[n1end]",n3end = "[n3end]",n2end = "[n2end]";
						
						if (start > 0) {
							p1tok = sent.wordAt(start - 1).getLemma();
							p1cap = sent.wordAt(start - 1).getCap();
							p1suf = sent.wordAt(start-1).getSuffix();
							p1form = sent.wordAt(start - 1).getWordForm();
							p1end = sent.wordAt(start-1).getSuffix(1);
							p1type = sent.wordAt(start-1).getPOS();
							if (start > 1) {
								p2tok = sent.wordAt(start - 2).getLemma();
								p2cap = sent.wordAt(start - 2).getCap();
								p2suf =sent.wordAt(start-2).getSuffix();
								p2form = sent.wordAt(start - 2).getWordForm();
								p2type = sent.wordAt(start-2).getPOS();
								p2end = sent.wordAt(start-2).getSuffix(1);
								if (start > 2) {
									p3tok = sent.wordAt(start - 3).getLemma();
									p3cap = sent.wordAt(start - 3).getCap();
									p3suf = sent.wordAt(start-3).getSuffix();
									p3form = sent.wordAt(start - 3).getWordForm();
									p3type = sent.wordAt(start-3).getPOS();
									p3end = sent.wordAt(start-3).getSuffix(1);
								}
							}
						}

						String ctok = word.getLemma();
						String cunif = "[COLLAPSED]";
						String ccap = word.getCap();
						String csuf = word.getSuffix();
						String cform = word.getWordForm();


						if (end < sent.size() - 1) {
							n1tok = sent.wordAt(end + 1).getLemma();
							n1cap = sent.wordAt(end + 1).getCap();
							n1suf = sent.wordAt(end+1).getSuffix();
							n1form = sent.wordAt(end + 1).getWordForm();
							n1end = sent.wordAt(end + 1).getSuffix(1);
							if (end < sent.size() - 2) {
								n2tok = sent.wordAt(end + 2).getLemma();
								n2cap = sent.wordAt(end + 2).getCap();
								n2suf = sent.wordAt(end+2).getSuffix();
								n2form = sent.wordAt(end + 2).getWordForm();
								n2end = sent.wordAt(end+2).getSuffix(1);
								if (end < sent.size() - 3) {
									n3tok = sent.wordAt(end + 3).getLemma();
									n3cap = sent.wordAt(end + 3).getCap();
									n3suf =sent.wordAt(end+3).getSuffix();
									n3form = sent.wordAt(end + 3).getWordForm();
									n3end = sent.wordAt(end+3).getSuffix(1);

								}
							}
						}
						for (String s : new String[] { Stemmer.stemTerm(ctok), cunif, ccap }) {
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
							
							fdamodel.add(s, word.getPOS(), Global.P1END, p1end);
							fdamodel.add(s, word.getPOS(), Global.P2END, p2end);
							fdamodel.add(s, word.getPOS(), Global.P3END, p3end);
							fdamodel.add(s, word.getPOS(), Global.N1END, n1end);
							fdamodel.add(s, word.getPOS(), Global.N2END, n2end);
							fdamodel.add(s, word.getPOS(), Global.N3END, n3end);
							
							
							fdamodel.add(s, word.getPOS(), Global.CCAP, ccap);
							fdamodel.add(s, word.getPOS(), Global.CTOK, Stemmer.stemTerm(ctok));
							fdamodel.add(s, word.getPOS(), Global.CSUF, csuf);
							fdamodel.add(s, word.getPOS(), Global.CFORM, cform);
							
							fdamodel.add(s, word.getPOS(), Global.P1TYPE, p1type);
							fdamodel.add(s, word.getPOS(), Global.P2TYPE, p2type);
							fdamodel.add(s, word.getPOS(), Global.P3TYPE, p3type);
							
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
	public FDA_MLModel train(ONF_DataSet fdadata, double fold, int ifold) throws Exception {
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
						if (ifold != Double.POSITIVE_INFINITY)
							if (word.getBooleanFolds()[ifold] == false)
								continue;

						// entity starts the sentence
						String p1tok = "[p1tok]", p2tok = "[p2tok]", p3tok = "[p3tok]", p4tok = "[p4tok]", p5tok = "[p5tok]";
						String n1tok = "[n1tok]", n2tok = "[n2tok]", n3tok = "[n3tok]", n4tok = "[n4tok]", n5tok = "[n5tok]";

						String p1pos = "[p1pos]", p2pos = "[p2pos]", p3pos = "[p3pos]", p4pos = "[p4pos]", p5pos = "[p5pos]";
						String n1pos = "[n1pos]", n2pos = "[n2pos]", n3pos = "[n3pos]", n4pos = "[n4pos]", n5pos = "[n5pos]";

						String p1cap = "[p1cap]", p2cap = "[p2cap]", p3cap = "[p3cap]", p4cap = "[p4cap]", p5cap = "[p5cap]";
						String n1cap = "[n1cap]", n2cap = "[n2cap]", n3cap = "[n3cap]", n4cap = "[n4cap]", n5cap = "[n5cap]";

						int start = wordposition, end = wordposition++;
						if (start > 0) {
							p1tok = sent.wordAt(start - 1).getLemma();
							p1pos = sent.wordAt(start - 1).getPOS();
							p1cap = sent.wordAt(start - 1).getCap();
							if (start > 1) {
								p2tok = sent.wordAt(start - 2).getLemma();
								p2pos = sent.wordAt(start - 2).getPOS();
								p2cap = sent.wordAt(start - 2).getCap();
								if (start > 2) {
									p3tok = sent.wordAt(start - 3).getLemma();
									p3pos = sent.wordAt(start - 3).getPOS();
									p3cap = sent.wordAt(start - 3).getCap();
									if (start > 3) {
										p4tok = sent.wordAt(start - 4).getLemma();
										p4pos = sent.wordAt(start - 4).getPOS();
										p4cap = sent.wordAt(start - 4).getCap();
										if (start > 4) {
											p5tok = sent.wordAt(start - 5).getLemma();
											p5pos = sent.wordAt(start - 5).getPOS();
											p5cap = sent.wordAt(start - 5).getCap();
										}
									}
								}
							}
						}

						String ctok = word.getLemma();
						String ctype = "[" + word.getEntityType() + "]";
						String cunif = "[COLLAPSED]";
						String cpos = word.getPOS();
						String ccap = word.getCap();
						// if (this.posRules.containsKey(cpos))
						// this.posRules.increment(cpos);
						// else this.posRules.put(cpos, 1);
						this.posRules.adjustOrPutValue(cpos, 1, 1);

						if (end < sent.size() - 1) {
							n1tok = sent.wordAt(end + 1).getLemma();
							n1pos = sent.wordAt(end + 1).getPOS();
							n1cap = sent.wordAt(end + 1).getCap();
							if (end < sent.size() - 2) {
								n2tok = sent.wordAt(end + 2).getLemma();
								n2pos = sent.wordAt(end + 2).getPOS();
								n2cap = sent.wordAt(end + 2).getCap();
								if (end < sent.size() - 3) {
									n3tok = sent.wordAt(end + 3).getLemma();
									n3pos = sent.wordAt(end + 3).getPOS();
									n3cap = sent.wordAt(end + 3).getCap();
									if (end < sent.size() - 4) {
										n4tok = sent.wordAt(end + 4).getLemma();
										n4pos = sent.wordAt(end + 4).getPOS();
										n4cap = sent.wordAt(end + 4).getCap();
										if (end < sent.size() - 5) {
											n5tok = sent.wordAt(end + 5).getLemma();
											n5pos = sent.wordAt(end + 5).getPOS();
											n5cap = sent.wordAt(end + 5).getCap();
										}
									}
								}
							}
						}
						for (String s : new String[] { Stemmer.stemTerm(ctok), ctype, cunif, cpos, ccap }) {
							fdamodel.add(s, word.getEntityType(), Global.P1TOK, Stemmer.stemTerm(p1tok));
							fdamodel.add(s, word.getEntityType(), Global.P2TOK, Stemmer.stemTerm(p2tok));
							fdamodel.add(s, word.getEntityType(), Global.P3TOK, Stemmer.stemTerm(p3tok));
							fdamodel.add(s, word.getEntityType(), Global.P4TOK, Stemmer.stemTerm(p4tok));
							fdamodel.add(s, word.getEntityType(), Global.P5TOK, Stemmer.stemTerm(p5tok));
							fdamodel.add(s, word.getEntityType(), Global.N1TOK, Stemmer.stemTerm(n1tok));
							fdamodel.add(s, word.getEntityType(), Global.N2TOK, Stemmer.stemTerm(n2tok));
							fdamodel.add(s, word.getEntityType(), Global.N3TOK, Stemmer.stemTerm(n3tok));
							fdamodel.add(s, word.getEntityType(), Global.N4TOK, Stemmer.stemTerm(n4tok));
							fdamodel.add(s, word.getEntityType(), Global.N5TOK, Stemmer.stemTerm(n5tok));
							fdamodel.add(s, word.getEntityType(), Global.P1POS, p1pos);
							fdamodel.add(s, word.getEntityType(), Global.P2POS, p2pos);
							fdamodel.add(s, word.getEntityType(), Global.P3POS, p3pos);
							fdamodel.add(s, word.getEntityType(), Global.P4POS, p4pos);
							fdamodel.add(s, word.getEntityType(), Global.P5POS, p5pos);
							fdamodel.add(s, word.getEntityType(), Global.N1POS, n1pos);
							fdamodel.add(s, word.getEntityType(), Global.N2POS, n2pos);
							fdamodel.add(s, word.getEntityType(), Global.N3POS, n3pos);
							fdamodel.add(s, word.getEntityType(), Global.N4POS, n4pos);
							fdamodel.add(s, word.getEntityType(), Global.N5POS, n5pos);
							fdamodel.add(s, word.getEntityType(), Global.P1CAP, p1cap);
							fdamodel.add(s, word.getEntityType(), Global.P2CAP, p2cap);
							fdamodel.add(s, word.getEntityType(), Global.P3CAP, p3cap);
							fdamodel.add(s, word.getEntityType(), Global.P4CAP, p4cap);
							fdamodel.add(s, word.getEntityType(), Global.P5CAP, p5cap);
							fdamodel.add(s, word.getEntityType(), Global.N1CAP, n1cap);
							fdamodel.add(s, word.getEntityType(), Global.N2CAP, n2cap);
							fdamodel.add(s, word.getEntityType(), Global.N3CAP, n3cap);
							fdamodel.add(s, word.getEntityType(), Global.N4CAP, n4cap);
							fdamodel.add(s, word.getEntityType(), Global.N5CAP, n5cap);
							fdamodel.add(s, word.getEntityType(), Global.CCAP, ccap);
							fdamodel.add(s, word.getEntityType(), Global.CPOS, cpos);
							fdamodel.add(s, word.getEntityType(), Global.CTOK, Stemmer.stemTerm(ctok));
						}
					}
				}
			}
		}
		return fdamodel;
	}

	public FDA_MLModel trainRich(ONF_DataSet fdadata, double fold, int ifold) throws Exception {
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
						String sp1tok = Stemmer.stemTerm(p1tok);
						String sp2tok = Stemmer.stemTerm(p2tok);
						String sp3tok = Stemmer.stemTerm(p3tok);
						String sn1tok = Stemmer.stemTerm(n1tok);
						String sn2tok = Stemmer.stemTerm(n2tok);
						String sn3tok = Stemmer.stemTerm(n3tok);
						String sctok = Stemmer.stemTerm(ctok);

						for (String s : new String[] { Stemmer.stemTerm(ctok), cform, cpos, ccap, cform+"_"+cpos }) {
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
							fdamodel.add(s, word.getEntityType(), Global.CTOK, sctok);
							fdamodel.add(s, word.getEntityType(), Global.CSUF, csuf);
							fdamodel.add(s, word.getEntityType(), Global.CPREF, cpref);
							fdamodel.add(s, word.getEntityType(), Global.CFORM, cform);

							// add previous label feature.
							fdamodel.add(s, word.getEntityType(), Global.P1TYPE, p1type);
							fdamodel.add(s, word.getEntityType(), Global.P2TYPE, p2type);
							fdamodel.add(s, word.getEntityType(), Global.P3TYPE, p3type);
						}
					}
				}
			}
		}
		return fdamodel;
	}

	/**
	 * Train POS with random sentences, not random tokens.
	 * 
	 * @param fdadata
	 * @param fold
	 * @param ifold
	 * @return
	 * @throws Exception
	 */
	public FDA_MLModel trainSentPOS(ONF_DataSet fdadata, double fold, int ifold) throws Exception {

		FDA_MLModel fdamodel = new FDA_MLModel();
		/*
		 * populate the dataset into the model.
		 */
		for (Document doc : fdadata.getDocuments()) {
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {
					List<Word> words = sent.getWords();

					if (ifold != Double.POSITIVE_INFINITY)
						if (sent.getFolds()[ifold] == false)
							continue;

					/*
					 * get the named entities.
					 */
					List<NamedEntity> nes = sent.getNamedEntities();

					int wordposition = 0;
					for (Word word : words) {

						int start = wordposition, end = wordposition++;

						// entity starts the sentence
						String p1tok = "[p1tok]", p2tok = "[p2tok]", p3tok = "[p3tok]", p4tok = "[p4tok]", p5tok = "[p5tok]";
						String n1tok = "[n1tok]", n2tok = "[n2tok]", n3tok = "[n3tok]", n4tok = "[n4tok]", n5tok = "[n5tok]";

						String p1pos = "[p1pos]", p2pos = "[p2pos]", p3pos = "[p3pos]", p4pos = "[p4pos]", p5pos = "[p5pos]";
						String n1pos = "[n1pos]", n2pos = "[n2pos]", n3pos = "[n3pos]", n4pos = "[n4pos]", n5pos = "[n5pos]";

						String p1cap = "[p1cap]", p2cap = "[p2cap]", p3cap = "[p3cap]", p4cap = "[p4cap]", p5cap = "[p5cap]";
						String n1cap = "[n1cap]", n2cap = "[n2cap]", n3cap = "[n3cap]", n4cap = "[n4cap]", n5cap = "[n5cap]";

						String p1suf = "[p1suf]", p2suf = "[p2suf]", p3suf = "[p3suf]", p4suf = "[p4suf]", p5suf = "[p5uf]";
						String n1suf = "[n1suf]", n2suf = "[n2suf]", n3suf = "[n3suf]", n4suf = "[n4suf]", n5suf = "[n5uf]";

						if (start > 0) {
							p1tok = sent.wordAt(start - 1).getLemma();
							p1pos = sent.wordAt(start - 1).getPOS();
							p1cap = sent.wordAt(start - 1).getCap();
							p1suf = p1tok.substring(p1tok.length() > 3 ? (p1tok.length() - 3) : 0);
							if (start > 1) {
								p2tok = sent.wordAt(start - 2).getLemma();
								p2pos = sent.wordAt(start - 2).getPOS();
								p2cap = sent.wordAt(start - 2).getCap();
								p2suf = p2tok.substring(p2tok.length() > 3 ? (p2tok.length() - 3) : 0);

								if (start > 2) {
									p3tok = sent.wordAt(start - 3).getLemma();
									p3pos = sent.wordAt(start - 3).getPOS();
									p3cap = sent.wordAt(start - 3).getCap();
									p3suf = p3tok.substring(p3tok.length() > 3 ? (p3tok.length() - 3) : 0);

									if (start > 3) {
										p4tok = sent.wordAt(start - 4).getLemma();
										p4pos = sent.wordAt(start - 4).getPOS();
										p4cap = sent.wordAt(start - 4).getCap();
										p4suf = p4tok.substring(p4tok.length() > 3 ? (p4tok.length() - 3) : 0);

										if (start > 4) {
											p5tok = sent.wordAt(start - 5).getLemma();
											p5pos = sent.wordAt(start - 5).getPOS();
											p5cap = sent.wordAt(start - 5).getCap();
											p5suf = p5tok.substring(p5tok.length() > 3 ? (p5tok.length() - 3) : 0);

										}
									}
								}
							}
						}

						String ctok = word.getLemma();
						String ctype = "[" + word.getEntityType() + "]";
						String cunif = "[COLLAPSED]";
						String cpos = word.getPOS();
						String ccap = word.getCap();
						String csuf = word.getLemma()
								.substring(word.getLemma().length() > 3 ? (word.getLemma().length() - 3) : 0);
						// if (this.posRules.containsKey(cpos))
						// this.posRules.increment(cpos);
						// else this.posRules.put(cpos, 1);
						this.posRules.adjustOrPutValue(cpos, 1, 1);

						if (end < sent.size() - 1) {
							n1tok = sent.wordAt(end + 1).getLemma();
							n1pos = sent.wordAt(end + 1).getPOS();
							n1cap = sent.wordAt(end + 1).getCap();
							n1suf = n1tok.substring(n1tok.length() > 3 ? (n1tok.length() - 3) : 0);

							if (end < sent.size() - 2) {
								n2tok = sent.wordAt(end + 2).getLemma();
								n2pos = sent.wordAt(end + 2).getPOS();
								n2cap = sent.wordAt(end + 2).getCap();
								n2suf = n2tok.substring(n2tok.length() > 3 ? (n2tok.length() - 3) : 0);

								if (end < sent.size() - 3) {
									n3tok = sent.wordAt(end + 3).getLemma();
									n3pos = sent.wordAt(end + 3).getPOS();
									n3cap = sent.wordAt(end + 3).getCap();
									n3suf = n3tok.substring(n3tok.length() > 3 ? (n3tok.length() - 3) : 0);

									if (end < sent.size() - 4) {
										n4tok = sent.wordAt(end + 4).getLemma();
										n4pos = sent.wordAt(end + 4).getPOS();
										n4cap = sent.wordAt(end + 4).getCap();
										n4suf = n4tok.substring(n4tok.length() > 3 ? (n4tok.length() - 3) : 0);

										if (end < sent.size() - 5) {
											n5tok = sent.wordAt(end + 5).getLemma();
											n5pos = sent.wordAt(end + 5).getPOS();
											n5cap = sent.wordAt(end + 5).getCap();
											n5suf = n5tok.substring(n5tok.length() > 3 ? (n5tok.length() - 3) : 0);

										}
									}
								}
							}
						}
						for (String s : new String[] { Stemmer.stemTerm(ctok), cunif, ccap }) {
							fdamodel.add(s, word.getPOS(), Global.P1TOK, Stemmer.stemTerm(p1tok));
							fdamodel.add(s, word.getPOS(), Global.P2TOK, Stemmer.stemTerm(p2tok));
							fdamodel.add(s, word.getPOS(), Global.P3TOK, Stemmer.stemTerm(p3tok));
							fdamodel.add(s, word.getPOS(), Global.P4TOK, Stemmer.stemTerm(p4tok));
							fdamodel.add(s, word.getPOS(), Global.P5TOK, Stemmer.stemTerm(p5tok));
							fdamodel.add(s, word.getPOS(), Global.N1TOK, Stemmer.stemTerm(n1tok));
							fdamodel.add(s, word.getPOS(), Global.N2TOK, Stemmer.stemTerm(n2tok));
							fdamodel.add(s, word.getPOS(), Global.N3TOK, Stemmer.stemTerm(n3tok));
							fdamodel.add(s, word.getPOS(), Global.N4TOK, Stemmer.stemTerm(n4tok));
							fdamodel.add(s, word.getPOS(), Global.N5TOK, Stemmer.stemTerm(n5tok));
							fdamodel.add(s, word.getPOS(), Global.P1POS, p1pos);
							fdamodel.add(s, word.getPOS(), Global.P2POS, p2pos);
							fdamodel.add(s, word.getPOS(), Global.P3POS, p3pos);
							fdamodel.add(s, word.getPOS(), Global.P4POS, p4pos);
							fdamodel.add(s, word.getPOS(), Global.P5POS, p5pos);
							fdamodel.add(s, word.getPOS(), Global.N1POS, n1pos);
							fdamodel.add(s, word.getPOS(), Global.N2POS, n2pos);
							fdamodel.add(s, word.getPOS(), Global.N3POS, n3pos);
							fdamodel.add(s, word.getPOS(), Global.N4POS, n4pos);
							fdamodel.add(s, word.getPOS(), Global.N5POS, n5pos);
							fdamodel.add(s, word.getPOS(), Global.P1CAP, p1cap);
							fdamodel.add(s, word.getPOS(), Global.P2CAP, p2cap);
							fdamodel.add(s, word.getPOS(), Global.P3CAP, p3cap);
							fdamodel.add(s, word.getPOS(), Global.P4CAP, p4cap);
							fdamodel.add(s, word.getPOS(), Global.P5CAP, p5cap);
							fdamodel.add(s, word.getPOS(), Global.N1CAP, n1cap);
							fdamodel.add(s, word.getPOS(), Global.N2CAP, n2cap);
							fdamodel.add(s, word.getPOS(), Global.N3CAP, n3cap);
							fdamodel.add(s, word.getPOS(), Global.N4CAP, n4cap);
							fdamodel.add(s, word.getPOS(), Global.N5CAP, n5cap);
							fdamodel.add(s, word.getPOS(), Global.P1SUF, p1suf);
							fdamodel.add(s, word.getPOS(), Global.P2SUF, p2suf);
							fdamodel.add(s, word.getPOS(), Global.P3SUF, p3suf);
							fdamodel.add(s, word.getPOS(), Global.P4SUF, p4suf);
							fdamodel.add(s, word.getPOS(), Global.P5SUF, p5suf);
							fdamodel.add(s, word.getPOS(), Global.N1SUF, n1suf);
							fdamodel.add(s, word.getPOS(), Global.N2SUF, n2suf);
							fdamodel.add(s, word.getPOS(), Global.N3SUF, n3suf);
							fdamodel.add(s, word.getPOS(), Global.N4SUF, n4suf);
							fdamodel.add(s, word.getPOS(), Global.N5SUF, n5suf);

							fdamodel.add(s, word.getPOS(), Global.CCAP, ccap);
							fdamodel.add(s, word.getPOS(), Global.CPOS, cpos);
							fdamodel.add(s, word.getPOS(), Global.CTOK, Stemmer.stemTerm(ctok));
							fdamodel.add(s, word.getPOS(), Global.CSUF, csuf);
						}
					}
				}
			}
		}
		return fdamodel;

	}
}

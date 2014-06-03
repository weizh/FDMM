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
import edu.cmu.lti.weizh.utils.Stemmer;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;

public class FDA_Trainer implements Trainer<FDA_MLModel, FDA_DataSet> {
	TObjectIntHashMap<String> posRules;

	public static void main(String argv[]) throws Exception {
		FDA_DataSet fdad = new FDA_DataSet();
		String path = "C:/Users/Wynn/Documents/wynnzh/data/LDC/data/english/annotations/";
		ONF2FDADImporter.fill(new File(path), fdad);
		double fold = 10;
		fdad.crossValidationRandomGenerator(fold, (int) fold + "-crf-split.txt");
		System.out.println(fdad.getDocuments().size());
		// fdad.prettyPrint();
		FDA_Trainer fdatrainer = new FDA_Trainer();

		// fdatrainer.storePosRules("posrules.txt");
		for (int i = 0; i < (int) fold; i++) {
			System.out.println("training fold :" + i);
			 FDA_MLModel fdamodel = fdatrainer.train(fdad, fold, i);
			 fdamodel.store("alltok-fold" + (int) fold + "." + i +
			 "(p5-n5_pos-tok-cap)(ctok-cpos-cclps-ctype).en.FDA_MLModel");
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
	 * if fold ==infinity, all data is train data. if fold ==1, it means all
	 * data is test data.
	 */
	public FDA_MLModel train(FDA_DataSet fdadata, double fold, int ifold) throws Exception {
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

}

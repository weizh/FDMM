package edu.cmu.lti.weizh.fda;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import java.util.HashSet;
import java.util.List;

import edu.cmu.lti.weizh.Interface.Inferencer;
import edu.cmu.lti.weizh.models.Document;
import edu.cmu.lti.weizh.models.Global;
import edu.cmu.lti.weizh.models.NamedEntity;
import edu.cmu.lti.weizh.models.Paragraph;
import edu.cmu.lti.weizh.models.Sentence;
import edu.cmu.lti.weizh.models.Word;
import edu.cmu.lti.weizh.ontonotes.ONFImporter;
import edu.cmu.lti.weizh.ontonotes.ONF_DataSet;
import edu.cmu.lti.weizh.utils.Stemmer;

public class DatasetEvaluator implements Inferencer<FDA_MLModel, ONF_DataSet> {

	private FDA_MLModel posmodel;
	private FDA_MLModel nermodel;

	public static void main(String arv[]) throws Exception {

		ONF_DataSet testSet = new ONF_DataSet();
		String path = "C:/Users/Wynn/Documents/wynnzh/data/LDC/data/english/annotations/";
		//
		System.out.println("loading data:");
		ONFImporter.fill(new File(path), testSet);
		//

		// generate labeling.txt
		// generateFiles(testSet);

		// generate train test files for crfsuite and crf++.
		// int ifold = 0;
		// generateCRFTrainTestFiles("NER-SentLevel-fold"+ifold, testSet, 10,
		// ifold);
		// generateCRFPPTrainTestFiles("NER-101suf", testSet, 10, 0);

		// if (true)
		// return;

		String mode = "NER";
		/*
		 * load models : alltok-folds: word level NER SETN-alltok: sentence
		 * level NER POSModel-alltok: word level POS SENT-POSModel : sentence
		 * level POS.
		 */
		if (mode.equals("NER")) {
			testSet.fillCVFolds("10-crf-split.txt");
			int foldId = 0;
			FDA_MLModel nerModel = FDA_MLModel.load("rich-alltok-fold10." + foldId
					+ "(p5-n5_pos-tok-cap)(ctok-cpos-cclps-ctype).en.FDA_MLModel");
//			nerModel.print();

			// sentence level NER
			// new DatasetEvaluator().inferSentWordLevel(model, testSet,
			// foldId);
			// testSet.predictionsSentWordsStat(foldId);

			// word level NER
			new DatasetEvaluator().inferWordLevel(nerModel, testSet, foldId);
			testSet.predictionsWordsStat(foldId);

			// named entity level
			// new DatasetEvaluator().infer(model, testSet);
			// testSet.predictionsStat();

		} else if (mode.equals("POS")) {

			
			testSet.fillCVFolds("10-POSMODEL-split.txt");
			int foldId = 0;
			FDA_MLModel posSentModel = FDA_MLModel.load("POS-alltok-fold10." + foldId
					+ "(p5-n5_pos-tok-cap)(ctok-cpos-cclps-ctype).en.FDA_MLModel");

			// Best combo feature: 0.8456
			// word level pos
			new DatasetEvaluator().inferWordLevelPOS(posSentModel, testSet, foldId);
			testSet.predictionsWordsStatPOS(foldId);

			// //sentence level pos
			// new DatasetEvaluator().inferWordLevelSentPOS(model, testSet,
			// foldId);
			// testSet.predictionsWordsStatSentPOS(foldId);
			//
		}

	}

	public void infer(FDA_MLModel fdamodel, ONF_DataSet fdadata) throws Exception {

		for (Document doc : fdadata.getDocuments()) {
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {

					List<NamedEntity> nes = sent.getNamedEntities();

					for (NamedEntity ne : nes) {
						int start = ne.getStart();
						int end = ne.getEnd();

						// entity starts the sentence
						String p1tok = "[p1tok]", p2tok = "[p2tok]", p3tok = "[p3tok]", p4tok = "[p4tok]", p5tok = "[p5tok]";
						String n1tok = "[n1tok]", n2tok = "[n2tok]", n3tok = "[n3tok]", n4tok = "[n4tok]", n5tok = "[n5tok]";

						String p1pos = "[p1pos]", p2pos = "[p2pos]", p3pos = "[p3pos]", p4pos = "[p4pos]", p5pos = "[p5pos]";
						String n1pos = "[n1pos]", n2pos = "[n2pos]", n3pos = "[n3pos]", n4pos = "[n4pos]", n5pos = "[n5pos]";

						String p1cap = "[p1cap]", p2cap = "[p2cap]", p3cap = "[p3cap]", p4cap = "[p4cap]", p5cap = "[p5cap]";
						String n1cap = "[n1cap]", n2cap = "[n2cap]", n3cap = "[n3cap]", n4cap = "[n4cap]", n5cap = "[n5cap]";

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

						String ctok = ne.getLemma();
						String ctype = "[" + ne.getEntityType() + "]";
						String cunif = "[COLLAPSED]";
						String cpos = ne.getPOS();

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
						String[] thetaIndex = new String[] { ctok, cpos, cunif };
						String label = fdamodel.predict(thetaIndex, 1E-5, 1E-5, Global.P1TOK, p1tok, Global.N1TOK, n1tok,
								Global.P2TOK, p2tok, Global.N2TOK, n2tok, Global.P3TOK, p3tok, Global.N3TOK, n3tok, Global.P4TOK,
								p4tok, Global.N4TOK, n4tok, Global.P5TOK, p5tok, Global.N5TOK, n5tok);
						ne.setPrediction(label);
					}
				}
			}
		}
	}

	public void inferWordLevel(FDA_MLModel fdamodel, ONF_DataSet fdadata, int foldId) throws Exception {

		for (Document doc : fdadata.getDocuments()) {
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {

					int wordposition = 0;
					for (Word word : sent.getWords()) {

						int start = wordposition, end = wordposition++;

						if (word.getBooleanFolds()[foldId])
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

						String p1type = "[p1type]", p2type = "[p2type]",p3type = "[p3type]";

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

						String[] thetaIndex = new String[] { Stemmer.stemTerm(ctok),  cform+"_"+cpos, cpos };
						String label = fdamodel.predict(thetaIndex, 1E-5, 1E-5, Global.CCAP, ccap, Global.CPOS, cpos,
								Global.CSUF, csuf, Global.CFORM, cform, Global.P1TYPE, p1type, 
								Global.P2TYPE, p2type,
//								Global.P3TYPE, p3type,
								Global.P1TOK, Stemmer.stemTerm(p1tok), Global.N1TOK, Stemmer.stemTerm(n1tok), Global.P2TOK,
								Stemmer.stemTerm(p2tok), Global.N2TOK, Stemmer.stemTerm(n2tok),
//								 Global.P1SUF, p1suf, Global.N1SUF, n1suf,

								// Global.P1FORM, p1form, Global.N1FORM, n1form,

								Global.P1CAP, p1cap, Global.N1CAP, n1cap

//						 , Global.P1POS, p1pos
						// , Global.N1POS, n1pos

								 , Global.P2CAP, p2cap
								// , Global.N2CAP, n2cap
								// , Global.P2POS, p2pos
								// , Global.N2POS, n2pos

//								 , Global.P3TOK, Stemmer.stemTerm( p3tok)
								// , Global.N3TOK, Stemmer.stemTerm( n3tok)
								// , Global.P3CAP, p3cap
								// , Global.N3CAP, n3cap
								// , Global.P3POS, p3pos
								// , Global.N3POS, n3pos

								);
						word.setPrediction(label);
					}
				}
			}
		}
	}

	public void inferSentWordLevel(FDA_MLModel fdamodel, ONF_DataSet fdadata, int foldId) throws Exception {

		for (Document doc : fdadata.getDocuments()) {
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {

					int position = 0;
					for (Word word : sent.getWords()) {
						int start = position++;
						int end = start;

						if (sent.getFolds()[foldId])
							continue;
						// entity starts the sentence
						String p1tok = "[p1tok]", p2tok = "[p2tok]", p3tok = "[p3tok]", p4tok = "[p4tok]", p5tok = "[p5tok]";
						String n1tok = "[n1tok]", n2tok = "[n2tok]", n3tok = "[n3tok]", n4tok = "[n4tok]", n5tok = "[n5tok]";

						String p1pos = "[p1pos]", p2pos = "[p2pos]", p3pos = "[p3pos]", p4pos = "[p4pos]", p5pos = "[p5pos]";
						String n1pos = "[n1pos]", n2pos = "[n2pos]", n3pos = "[n3pos]", n4pos = "[n4pos]", n5pos = "[n5pos]";

						String p1cap = "[p1cap]", p2cap = "[p2cap]", p3cap = "[p3cap]", p4cap = "[p4cap]", p5cap = "[p5cap]";
						String n1cap = "[n1cap]", n2cap = "[n2cap]", n3cap = "[n3cap]", n4cap = "[n4cap]", n5cap = "[n5cap]";

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
						String[] thetaIndex = new String[] { Stemmer.stemTerm(ctok), cpos };
						String label = fdamodel.predict(thetaIndex, 1E-5, 1E-5, Global.CCAP, ccap, Global.CPOS, cpos,

						Global.P1TOK, Stemmer.stemTerm(p1tok), Global.N1TOK, Stemmer.stemTerm(n1tok), Global.P1CAP, p1cap,
								Global.N1CAP, n1cap
								// , Global.P1POS, p1pos
								// , Global.N1POS, n1pos
								//
								, Global.P2TOK, Stemmer.stemTerm(p2tok), Global.N2TOK, Stemmer.stemTerm(n2tok)
						// , Global.P2CAP, p2cap
						// , Global.N2CAP, n2cap
						// , Global.P2POS, p2pos
						// , Global.N2POS, n2pos

								// , Global.P3TOK, Stemmer.stemTerm( p3tok)
								// , Global.N3TOK, Stemmer.stemTerm( n3tok)
								// , Global.P3CAP, p3cap
								// , Global.N3CAP, n3cap
								// , Global.P3POS, p3pos
								// , Global.N3POS, n3pos

								// ,Global.P4TOK, Stemmer.stemTerm(p4tok)
								// , Global.N4TOK, Stemmer.stemTerm(n4tok)
								// Global.P4CAP, p4cap, Global.N4CAP, n4cap,
								// Global.P4POS, p4pos, Global.N4POS, n4pos,

								// ,Global.P5TOK, Stemmer.stemTerm(p5tok)
								// , Global.N5TOK, Stemmer.stemTerm(n5tok)
								// Global.P5CAP, p5cap, Global.N5CAP, n5cap
								// Global.P5POS, p5pos, Global.N5POS, n5pos
								);
						word.setPrediction(label);
					}
				}
			}
		}
	}

	public void inferWordLevelPOS(FDA_MLModel fdamodel, ONF_DataSet fdadata, int foldId) throws Exception {

		for (Document doc : fdadata.getDocuments()) {
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {

					int position = 0;
					for (Word word : sent.getWords()) {
						int start = position++, end = start;

						// if (word.getWord().equalsIgnoreCase("there"))
						// System.out.println(word.getPOS());
						if (word.getBooleanFolds()[foldId])
							continue;

						// entity starts the sentence

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
							p1cap = sent.wordAt(start - 1).getCap();
							p1suf = sent.wordAt(start - 1).getSuffix();
							p1form = sent.wordAt(start - 1).getWordForm();
							p1type = sent.wordAt(start-1).getPrediction();
							p1end = sent.wordAt(start-1).getSuffix(1);

							if (start > 1) {
								p2cap = sent.wordAt(start - 2).getCap();
								p2suf = sent.wordAt(start - 2).getSuffix();
								p2form = sent.wordAt(start - 2).getWordForm();
								p2type = sent.wordAt(start-2).getPrediction();
								p2end = sent.wordAt(start-2).getSuffix(1);


								if (start > 2) {
									p3cap = sent.wordAt(start - 3).getCap();
									p3suf = sent.wordAt(start - 3).getSuffix();
									p3form = sent.wordAt(start - 3).getWordForm();
									p3type = sent.wordAt(start-3).getPrediction();
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
							n1cap = sent.wordAt(end + 1).getCap();
							n1suf = sent.wordAt(end + 1).getSuffix();
							n1form = sent.wordAt(end + 1).getWordForm();
							n1end = sent.wordAt(end + 1).getSuffix(1);

							if (end < sent.size() - 2) {
								n2cap = sent.wordAt(end + 2).getCap();
								n2suf = sent.wordAt(end + 2).getSuffix();
								n2form = sent.wordAt(end + 2).getWordForm();
								n2end = sent.wordAt(end+2).getSuffix(1);

								if (end < sent.size() - 3) {
									n3cap = sent.wordAt(end + 3).getCap();
									n3suf = sent.wordAt(end + 3).getSuffix();
									n3form = sent.wordAt(end + 3).getWordForm();
									n3end = sent.wordAt(end+3).getSuffix(1);

								}
							}
						}

						String[] thetaIndex = new String[] { Stemmer.stemTerm(ctok), ccap, cunif };
						String label = fdamodel.predict(thetaIndex, 1E-5, 1E-5, Global.CSUF, csuf
//								,Global.P1END,p1end
//								,Global.P1TYPE,p1type
//								,Global.P2TYPE,p2type
//								,Global.P3TYPE,p3type
						// ,Global.CFORM, cform 
						// , Global.CCAP, ccap
						// , Global.P1CAP, p1cap, Global.N1CAP,n1cap
								, Global.P1SUF, p1suf, Global.N1SUF, n1suf
						 , Global.P2SUF, p2suf
								, Global.N2SUF, n2suf

								);
						word.setPrediction(label);
					}
				}
			}
		}
	}

	public void inferWordLevelSentPOS(FDA_MLModel fdamodel, ONF_DataSet fdadata, int foldId) throws Exception {

		for (Document doc : fdadata.getDocuments()) {
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {

					if (sent.getFolds()[foldId])
						continue;
					int position = 0;
					for (Word word : sent.getWords()) {
						int start = position++;
						int end = start;
						// if (word.getWord().equalsIgnoreCase("there"))
						// System.out.println(word.getPOS());

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
						String[] thetaIndex = new String[] { Stemmer.stemTerm(ctok), ccap };
						String label = fdamodel.predict(thetaIndex, 1E-5, 1E-5, Global.CSUF, csuf
						// , Global.CCAP, ccap
						// , Global.P1TOK, p1tok, Global.N1TOK,n1tok
						// , Global.P1CAP, p1cap, Global.N1CAP,n1cap
								, Global.P1SUF, p1suf, Global.N1SUF, n1suf
						// , Global.P2SUF, p2suf, Global.N2SUF, n2suf
						// , Global.P2TOK, p2tok, Global.N2TOK,n2tok
								);
						word.setPrediction(label);
					}
				}
			}
		}
	}

	public static void generateMockupFiles(String fs, ONF_DataSet fdadata) throws Exception {

		BufferedWriter bw = new BufferedWriter(new FileWriter(fs + "-train.txt"));
		/*
		 * populate the dataset into the model.
		 */
		for (Document doc : fdadata.getDocuments()) {
			System.out.println("processing doc " + doc.getDocId());
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {
					List<Word> words = sent.getWords();
					/*
					 * get the named entities.
					 */
					List<NamedEntity> nes = sent.getNamedEntities();
				}
			}
		}
		bw.close();
	}

	public static void generateCRFTrainTestFiles(String fs, ONF_DataSet fdadata, double fold, int ifold) throws Exception {

		BufferedWriter bw = new BufferedWriter(new FileWriter(fs + "-train.txt"));
		BufferedWriter bwtest = new BufferedWriter(new FileWriter(fs + "-test.txt"));
		/*
		 * populate the dataset into the model.
		 */
		for (Document doc : fdadata.getDocuments()) {
			System.out.println("processing doc " + doc.getDocId());
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {
					List<Word> words = sent.getWords();
					/*
					 * get the named entities.
					 */
					List<NamedEntity> nes = sent.getNamedEntities();

					int wordposition = 0;
					for (Word word : words) {

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

						if (fold != Double.POSITIVE_INFINITY) {
							StringBuilder s = new StringBuilder();
							s.append(word.getEntityType());
							String[] ens = { Stemmer.stemTerm(ctok), Stemmer.stemTerm(p1tok), Stemmer.stemTerm(n1tok),
									Stemmer.stemTerm(p2tok), Stemmer.stemTerm(n2tok),
									// Stemmer.stemTerm(p4tok),Stemmer.stemTerm(n4tok),
									// Stemmer.stemTerm(p5tok),Stemmer.stemTerm(n5tok),
									cpos,
									// p4pos,n4pos,
									// p5pos,n5pos,
									ccap, p1cap, n1cap
							//
							};
							for (String e : ens) {
								s.append("\t").append(e);
							}
							s.append("\n");

							if (sent.getFolds()[ifold])
								// if (word.getBooleanFolds()[ifold])
								bw.write(s.toString());
							else
								bwtest.write(s.toString());
						}
					}
					if (sent.getFolds()[ifold])
						bw.write("\n");
					else
						bwtest.write("\n");
				}
			}
		}
		bw.close();
		bwtest.close();
	}

	public static void generateCRFPPTrainTestFiles(String fs, ONF_DataSet fdadata, double fold, int ifold) throws Exception {

		BufferedWriter bw = new BufferedWriter(new FileWriter(fs + "-train.txt"));
		BufferedWriter bwtest = new BufferedWriter(new FileWriter(fs + "-test.txt"));
		/*
		 * populate the dataset into the model.
		 */
		for (Document doc : fdadata.getDocuments()) {
			System.out.println("processing doc " + doc.getDocId());
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {
					List<Word> words = sent.getWords();
					int position = 0;
					for (Word word : sent.getWords()) {
						int start = position++;
						int end = start;

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

						if (fold != Double.POSITIVE_INFINITY) {
							StringBuilder s = new StringBuilder();

							s.append(word.getPOS()).append("\t").append(Stemmer.stemTerm(ctok)).append("\t").append(csuf)
							// .append("\t").append(Global.P1SUF +
							// p1suf).append("\t").append(Global.N1SUF + n1suf)
									.append("\n");

							// if (word.getBooleanFolds()[ifold])
							if (sent.getFolds()[ifold])
								bw.write(s.toString());
							else
								bwtest.write(s.toString());
						}
					}
				}
			}
		}
		bw.close();
		bwtest.close();
	}

	public static void generateFiles(ONF_DataSet fdadata) throws Exception {

		// BufferedWriter bw = new BufferedWriter(new
		// FileWriter("All-labeling.txt"));
		/*
		 * populate the data set into the model.
		 */
		BufferedWriter bw = null;
		for (Document doc : fdadata.getDocuments()) {

			String name = doc.getDocId().substring(40).split("\\\\")[6];

			// System.out.println("processing doc " + doc.getDocId());
			for (Paragraph para : doc.getParagraphs()) {
				int filecount = 0, sentcount = 0;

				int totalsent = 0;
				for (Sentence sent : para.getSentences()) {
					List<NamedEntity> nes = sent.getNamedEntities();

					if (nes.size() == 0)
						continue;

					boolean bool = false;
					for (NamedEntity ne : nes) {
						if (ne.getEntityType().equalsIgnoreCase("LOC") || ne.getEntityType().equalsIgnoreCase("GPE")
								|| ne.getEntityType().equalsIgnoreCase("FAC") || ne.getEntityType().equalsIgnoreCase("NORP")
								|| ne.getEntityType().equalsIgnoreCase("ORG")) {
							bool = true;
							break;
						}
					}

					if (bool == false)
						continue;

					totalsent++;
				}

				for (Sentence sent : para.getSentences()) {

					/*
					 * get the named entities.
					 */
					List<NamedEntity> nes = sent.getNamedEntities();

					boolean bool = false;
					for (NamedEntity ne : nes) {
						if (ne.getEntityType().equalsIgnoreCase("LOC") || ne.getEntityType().equalsIgnoreCase("GPE")
								|| ne.getEntityType().equalsIgnoreCase("FAC") || ne.getEntityType().equalsIgnoreCase("NORP")
								|| ne.getEntityType().equalsIgnoreCase("ORG")) {
							bool = true;
							break;
						}
					}

					if (bool == false)
						continue;

					sentcount++;
					if (sentcount == 1) {
						bw = new BufferedWriter(new FileWriter("labeling\\" + name + "_0.txt"));
					}

					List<Word> words = sent.getWords();

					HashSet<Integer> nepositions = new HashSet<Integer>();

					StringBuffer s = new StringBuffer(), s1 = new StringBuffer(), s2 = new StringBuffer();
					s.append(" \t ");
					s1.append("OntoNotes tags\t ");
					s2.append("Your tags\t ");
					int wordposition = 0;
					for (Word word : words) {
						if (word.getWord().equalsIgnoreCase("the")) {
							s.append("\t").append(word.getWord());
							s1.append("\t").append(" ");
							s2.append("\t").append(" ");
							continue;
						}

						if (word.getWord().startsWith("*"))
							continue;
						if (word.getWord().equals("\""))
							s.append("\t").append("'");
						else
							s.append("\t").append(word.getWord());

						if (word.getEntityType().equals("[O]"))
							s1.append("\t").append(" ");
						else if (word.getEntityType().equals("NORP"))
							s1.append("\t").append("GPE");
						else if (word.getEntityType().equals("GPE") || word.getEntityType().equals("LOC")
								|| word.getEntityType().equals("FAC") || word.getEntityType().equals("ORG"))
							s1.append("\t").append(word.getEntityType());
						else
							s1.append("\t").append(" ");

						s2.append("\t").append(" ");
					}

					s.append("\n");
					s1.append("\n");
					s2.append("\n");

					bw.write(s.toString());
					bw.write(s1.toString());
					bw.write(s2.toString());
					bw.write("\n");

					if (sentcount % 5 == 0) {
						bw.close();
						if (sentcount < totalsent)
							bw = new BufferedWriter(new FileWriter("labeling\\" + name + "_" + (++filecount) + ".txt"));
					}

				}
				bw.close();
			}
		}

	}
}

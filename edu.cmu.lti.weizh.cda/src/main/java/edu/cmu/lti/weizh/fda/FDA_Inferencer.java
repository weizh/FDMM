package edu.cmu.lti.weizh.fda;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import edu.cmu.lti.weizh.Interface.Inferencer;
import edu.cmu.lti.weizh.models.Document;
import edu.cmu.lti.weizh.models.Global;
import edu.cmu.lti.weizh.models.NamedEntity;
import edu.cmu.lti.weizh.models.Paragraph;
import edu.cmu.lti.weizh.models.Sentence;
import edu.cmu.lti.weizh.models.Word;
import edu.cmu.lti.weizh.utils.Stemmer;

public class FDA_Inferencer implements Inferencer<FDA_MLModel, FDA_DataSet> {

	public static void main(String arv[]) throws Exception {

		FDA_DataSet testSet = new FDA_DataSet();
		String path = "C:/Users/Wynn/Documents/wynnzh/data/LDC/data/english/annotations/";

		System.out.println("loading data:");
		ONF2FDADImporter.fill(new File(path), testSet);

		testSet.fillCVFolds("10-crf-split.txt");

		generateFiles(testSet);

		// generateCRFPPTrainTestFiles("token-bestComb", testSet, 10, 3);

		int i = 1;
		if (i == 1)
			return;
		//
		int foldId = 9;
		FDA_MLModel model = FDA_MLModel.load("alltok-fold10." + foldId
				+ "(p5-n5_pos-tok-cap)(ctok-cpos-cclps-ctype).en.FDA_MLModel");

		// This function is to combine the lower and upper cased context tokens
		// into one.
		// model.collapse();
		new FDA_Inferencer().inferWordLevel(model, testSet, foldId);
		// new FDA_Inferencer().infer(model, testSet);
		testSet.predictionsWordsStat(foldId);
		// testSet.predictionsStat();

	}

	public void infer(FDA_MLModel fdamodel, FDA_DataSet fdadata) throws Exception {

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

	public void inferWordLevel(FDA_MLModel fdamodel, FDA_DataSet fdadata, int foldId) throws Exception {

		for (Document doc : fdadata.getDocuments()) {
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {

					int position = 0;
					for (Word word : sent.getWords()) {
						int start = position++;
						int end = start;

						if (word.getBooleanFolds()[foldId])
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

	public static void generateMockupFiles(String fs, FDA_DataSet fdadata) throws Exception {

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

	public static void generateCRFTrainTestFiles(String fs, FDA_DataSet fdadata, double fold, int ifold) throws Exception {

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
									Stemmer.stemTerm(p2tok), Stemmer.stemTerm(n2tok), Stemmer.stemTerm(p3tok),
									Stemmer.stemTerm(n3tok),
									// Stemmer.stemTerm(p4tok),Stemmer.stemTerm(n4tok),
									// Stemmer.stemTerm(p5tok),Stemmer.stemTerm(n5tok),
									cpos, p1pos, n1pos, p2pos, n2pos, p3pos, n3pos,
									// p4pos,n4pos,
									// p5pos,n5pos,
									ccap, p1cap, n1cap, p2cap, n2cap, p3cap, n3cap,
							// p4cap,n4cap,
							// p5cap,n5cap
							//
							};
							for (String e : ens) {
								s.append("\t").append(e);
							}
							s.append("\n");
							if (word.getBooleanFolds()[ifold])
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

	public static void generateCRFPPTrainTestFiles(String fs, FDA_DataSet fdadata, double fold, int ifold) throws Exception {

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
							String[] ens = { Global.CTOK + "_" + Stemmer.stemTerm(ctok),
									Global.P1TOK + "_" + Stemmer.stemTerm(p1tok), Global.N1TOK + "_" + Stemmer.stemTerm(n1tok),
									Global.P2TOK + "_" + Stemmer.stemTerm(p2tok), Global.N2TOK + "_" + Stemmer.stemTerm(n2tok),
									// Stemmer.stemTerm(p3tok),
									// Stemmer.stemTerm(n3tok),
									// Stemmer.stemTerm(p4tok),Stemmer.stemTerm(n4tok),
									// Stemmer.stemTerm(p5tok),Stemmer.stemTerm(n5tok),
									Global.CPOS + "_" + cpos,
									// p1pos, n1pos, p2pos, n2pos, p3pos, n3pos,
									// p4pos,n4pos,
									// p5pos,n5pos,
									Global.CCAP + "_" + ccap, Global.P1CAP + "_" + p1cap, Global.N1CAP + "_" + n1cap
							// , p2cap, n2cap, p3cap, n3cap,
							// p4cap,n4cap,
							// p5cap,n5cap
							//
							};
							s.append(word.getEntityType());

							for (String e : ens) {
								s.append("\t").append(e);
							}
							s.append("\n");
							if (word.getBooleanFolds()[ifold])
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

	public static void generateFiles(FDA_DataSet fdadata) throws Exception {

		BufferedWriter bw = new BufferedWriter(new FileWriter("labeling.txt"));
		/*
		 * populate the data set into the model.
		 */
		for (Document doc : fdadata.getDocuments()) {
			// System.out.println("processing doc " + doc.getDocId());
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {
					List<Word> words = sent.getWords();
					/*
					 * get the named entities.
					 */
					List<NamedEntity> nes = sent.getNamedEntities();

					boolean bool = false;
					for (NamedEntity ne : nes) {
						if (ne.getEntityType().equalsIgnoreCase("FAC"))
							bool = true;
					}
					if (bool == false)
						continue;

					StringBuffer s = new StringBuffer(), s1 = new StringBuffer(), s2 = new StringBuffer();
					s.append(doc.getDocId().substring(40));
					s1.append("Sure tags");
					s2.append("unsure tags");
					int wordposition = 0;
					for (Word word : words) {
						if (word.getWord().startsWith("*"))
							continue;
						if (word.getWord().equals("\""))
							s.append("\t").append("'");
						else
							s.append("\t").append(word.getWord());
						s1.append("\t").append(" ");
						s2.append("\t").append(" ");
					}
					s.append("\n");
					s1.append("\n");
					s2.append("\n");

					bw.write(s.toString());
					bw.write(s1.toString());
					bw.write(s2.toString());

				}
			}
		}
		bw.close();

	}
}

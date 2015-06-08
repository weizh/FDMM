package edu.cmu.lti.weizh.train.ontonotes;

import java.io.File;
import java.util.ArrayList;

import edu.cmu.lti.weizh.data.ontonotes.OntoNotesDataFiller;
import edu.cmu.lti.weizh.data.ontonotes.OntonotesDataSet;
import edu.cmu.lti.weizh.docmodel.Document;
import edu.cmu.lti.weizh.docmodel.Paragraph;
import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.eval.EVAL_CONSTS;
import edu.cmu.lti.weizh.feature.ONF_CONSTS;
import edu.cmu.lti.weizh.mlmodel.FDMM;
import edu.cmu.lti.weizh.nlp.stemming.Stemmer;
import edu.cmu.lti.weizh.train.Evaluatable;

public class ON_FDMM_Eval implements Evaluatable< OntonotesDataSet> {

	public static void main(String arv[]) throws Exception {

		String mode = "NER";
		String type = mode.equals("NER")?EVAL_CONSTS.NER_TYPE:EVAL_CONSTS.POS_TYPE;
		
		String cvfile = "10-randomSent-CVfolds-OCT25.txt";
		int foldId = 0;
		
		String path = "/home/wei/Documents/annotations/";
		
		boolean TbT = true; // token by token or viterbi
		
		OntonotesDataSet dataset = new OntonotesDataSet(300000, type);
		
		System.out.println("loading data:");
		new OntoNotesDataFiller(dataset).fill(new File(path),false);
		dataset.fillCVFolds(cvfile);
		/*
		 * load models : alltok-folds: word level NER SETN-alltok: sentence
		 * level NER POSModel-alltok: word level POS SENT-POSModel : sentence
		 * level POS.
		 */
		if (type.equals(EVAL_CONSTS.NER_TYPE)) {
			new ON_FDMM_Eval().infer(mode, TbT, FDMM.load("NER10-rich-randomSent-fold10.0-NOV08.en.FDA_MLModel"),
					dataset, foldId);
			dataset.printTokenByTokenEvaluation(foldId);
			//
			// named entity level
			// new DatasetEvaluator().inferEbENER(mode, testSet);
			// dataset.predictionsStat();

		} else if (type.equals(EVAL_CONSTS.POS_TYPE)) {
			FDMM model = FDMM.load("POS-rich-randomSent-fold10.0-OCT25.en.FDA_MLModel");
			new ON_FDMM_Eval().infer(mode, TbT, model, dataset, foldId);
			dataset.printTokenByTokenEvaluation(foldId);
		}

		// /------------------- end of procedure --------------------------
		// generate labeling.txt
		// generateFiles(testSet);
		// generate train test files for crfsuite and crf++.
		// int ifold = 0;
		// generateCRFTrainTestFiles("NER-SentLevel-fold"+ifold, testSet, 10,
		// ifold);
		// generateCRFPPTrainTestFiles("NER-101suf", testSet, 10, 0);
	}

	private void infer(String mode, boolean TbT, FDMM model, OntonotesDataSet testSet, int foldId) throws Exception {

		if (mode.equals("NER"))
			if (TbT) {
				// token-by-token for NER: sentences.
//				inferTbTNER(model, testSet, foldId);
			} else {
				// veterbi decoding for NER: sentences
				inferViterbiNER(model, testSet, foldId);
			}
		if (mode.equals("POS")) {
			if (TbT) {
				// Best combo feature: token-by-token inference: 0.8459 for
				// 10-randomSent-CVfolds-OCT24.txt
				// With p1 transition feature: token-by-token: 0.8439 for
				// 10-randomSent-CVfolds-OCT24.txt
				// word level pos
//				inferTBTPOS(model, testSet, foldId);
				// inferTBTRecursivePOS(model, testSet, foldId);

			} else {
				// Best combo feature: Sent Level: for
				// 10-randomSent-CVfolds-OCT24.txt
				// //sentence level pos
				inferViterbiPOS(model, testSet, foldId);

			}
		}
	}

	
@Deprecated
	/**
	 * word by word classification of NE.
	 * 
	 * Label class MONEY : P: 0.8001 R: 0.9134 F1: 0.8530<br>
	 * Label class GPE : P: 0.7797 R: 0.8178 F1: 0.7983<br>
	 * Label class ORG : P: 0.6377 R: 0.8378 F1: 0.7242<br>
	 * Label class PRODUCT : P: 0.6205 R: 0.5550 F1: 0.5860<br>
	 * Label class DATE : P: 0.6758 R: 0.7985 F1: 0.7320<br>
	 * Label class [O] : P: 0.9857 R: 0.9645 F1: 0.9750<br>
	 * Label class EVENT : P: 0.5632 R: 0.6447 F1: 0.6012<br>
	 * Label class ORDINAL : P: 0.6115 R: 0.5822 F1: 0.5965<br>
	 * Label class FAC : P: 0.5578 R: 0.5452 F1: 0.5514<br>
	 * Label class WORK_OF_ART : P: 0.4960 R: 0.4478 F1: 0.4707<br>
	 * Label class LOC : P: 0.5118 R: 0.5729 F1: 0.5406<br>
	 * Label class NORP : P: 0.7873 R: 0.7937 F1: 0.7905<br>
	 * Label class PERSON : P: 0.6008 R: 0.8555 F1: 0.7059<br>
	 * Label class TIME : P: 0.4793 R: 0.5730 F1: 0.5220<br>
	 * Label class CARDINAL : P: 0.5698 R: 0.6983 F1: 0.6276<br>
	 * Label class LAW : P: 0.5729 R: 0.5352 F1: 0.5534<br>
	 * Label class PERCENT : P: 0.8118 R: 0.9026 F1: 0.8548<br>
	 * Label class LANGUAGE : P: 0.7273 R: 0.4211 F1: 0.5333<br>
	 * Label class QUANTITY : P: 0.5556 R: 0.6421 F1: 0.5957<br>
	 * Average f1 of all class is: 0.6638<br>
	 * 
	 * 
	 * @param fdamodel
	 * @param fdadata
	 * @param foldId
	 * @throws Exception
	 */

	/**
	 * Viterbi Decoding of sequence
	 * 
	 * Label class MONEY : P: 0.8104 R: 0.9204 F1: 0.8619 <br>
	 * Label class GPE : P: 0.7851 R: 0.8623 F1: 0.8219 <br>
	 * Label class ORG : P: 0.7282 R: 0.8420 F1: 0.7810<br>
	 * Label class PRODUCT : P: 0.6198 R: 0.5459 F1: 0.5805<br>
	 * Label class DATE : P: 0.7223 R: 0.8078 F1: 0.7626<br>
	 * Label class [O] : P: 0.9887 R: 0.9714 F1: 0.9799<br>
	 * Label class EVENT : P: 0.6068 R: 0.6164 F1: 0.6115<br>
	 * Label class ORDINAL : P: 0.5965 R: 0.8253 F1: 0.6925<br>
	 * Label class FAC : P: 0.5133 R: 0.5452 F1: 0.5288<br>
	 * Label class WORK_OF_ART : P: 0.5474 R: 0.4047 F1: 0.4654<br>
	 * Label class LOC : P: 0.5712 R: 0.6417 F1: 0.6044<br>
	 * Label class NORP : P: 0.7940 R: 0.8830 F1: 0.8362<br>
	 * Label class PERSON : P: 0.6484 R: 0.8875 F1: 0.7493<br>
	 * Label class TIME : P: 0.5621 R: 0.6202 F1: 0.5897<br>
	 * Label class CARDINAL : P: 0.5560 R: 0.7458 F1: 0.6371<br>
	 * Label class LAW : P: 0.6279 R: 0.5070 F1: 0.5610<br>
	 * Label class PERCENT : P: 0.7937 R: 0.9200 F1: 0.8522<br>
	 * Label class LANGUAGE : P: 0.5862 R: 0.4474 F1: 0.5075<br>
	 * Label class QUANTITY : P: 0.5762 R: 0.6612 F1: 0.6158<br>
	 * Average f1 of all class is: 0.6863<br>
	 * 
	 * @param fdamodel
	 * @param fdadata
	 * @param foldId
	 * @throws Exception
	 */
	public void inferViterbiNER(FDMM fdamodel, OntonotesDataSet fdadata, int foldId) throws Exception {

		for (Document doc : fdadata.getDocuments()) {
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {

					if (sent.getWords().get(0).getBooleanFolds()[foldId])
						continue;

					ArrayList<String[]> theta = new ArrayList<String[]>();
					ArrayList<String[]> features = new ArrayList<String[]>();
					ArrayList<String[]> transition = new ArrayList<String[]>();

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
						String n1type = "[n1type]", n2type = "[n2type]", n3type = "[n3type]";
						if (start > 0) {
							p1tok = sent.wordAt(start - 1).getTrimLowered();
							p1pos = sent.wordAt(start - 1).getPartOfSpeech();
							p1cap = sent.wordAt(start - 1).isCapitalizedFirst();
							p1suf = sent.wordAt(start - 1).getSuffix();
							p1pref = sent.wordAt(start - 1).getPreffix();
							p1form = sent.wordAt(start - 1).getWordForm();
							p1type = sent.wordAt(start - 1).getPrediction();
							if (start > 1) {
								p2tok = sent.wordAt(start - 2).getTrimLowered();
								p2pos = sent.wordAt(start - 2).getPartOfSpeech();
								p2cap = sent.wordAt(start - 2).isCapitalizedFirst();
								p2suf = sent.wordAt(start - 2).getSuffix();
								p2pref = sent.wordAt(start - 2).getPreffix();
								p2form = sent.wordAt(start - 2).getWordForm();
								p2type = sent.wordAt(start - 2).getPrediction();
								if (start > 2) {
									p3tok = sent.wordAt(start - 3).getTrimLowered();
									p3pos = sent.wordAt(start - 3).getPartOfSpeech();
									p3cap = sent.wordAt(start - 3).isCapitalizedFirst();
									p3suf = sent.wordAt(start - 3).getSuffix();
									p3pref = sent.wordAt(start - 3).getPreffix();
									p3form = sent.wordAt(start - 3).getWordForm();
									p3type = sent.wordAt(start - 3).getPrediction();
								}
							}
						}

						String origintok = word.getWord();
						String ctok = word.getTrimLowered();
						String cpos = word.getPartOfSpeech();
						String ccap = word.isCapitalizedFirst();
						String csuf = word.getSuffix();
						String cpref = word.getPreffix();
						String cform = word.getWordForm();

						if (end < sent.size() - 1) {
							n1tok = sent.wordAt(end + 1).getTrimLowered();
							n1pos = sent.wordAt(end + 1).getPartOfSpeech();
							n1cap = sent.wordAt(end + 1).isCapitalizedFirst();
							n1suf = sent.wordAt(end + 1).getSuffix();
							n1pref = sent.wordAt(end + 1).getPreffix();
							n1form = sent.wordAt(end + 1).getWordForm();
							n1type = sent.wordAt(end + 1).getEntityType();
							if (end < sent.size() - 2) {
								n2tok = sent.wordAt(end + 2).getTrimLowered();
								n2pos = sent.wordAt(end + 2).getPartOfSpeech();
								n2cap = sent.wordAt(end + 2).isCapitalizedFirst();
								n2suf = sent.wordAt(end + 2).getSuffix();
								n2pref = sent.wordAt(end + 2).getPreffix();
								n2form = sent.wordAt(end + 2).getWordForm();
								n2type = sent.wordAt(end + 2).getEntityType();
								if (end < sent.size() - 3) {
									n3tok = sent.wordAt(end + 3).getTrimLowered();
									n3pos = sent.wordAt(end + 3).getPartOfSpeech();
									n3cap = sent.wordAt(end + 3).isCapitalizedFirst();
									n3suf = sent.wordAt(end + 3).getSuffix();
									n3pref = sent.wordAt(end + 3).getPreffix();
									n3form = sent.wordAt(end + 3).getWordForm();
									n3type = sent.wordAt(end + 3).getEntityType();
								}
							}
						}

						String[] thetaIndex = new String[] { origintok, ctok, Stemmer.stemTerm(ctok), cpos + "_" + csuf, cpos,
								ccap + "_" + csuf, csuf, cpref, cform, ccap, "[DUMMY]" };
						theta.add(thetaIndex);

						String[] feature = new String[] { ONF_CONSTS.CCAP, ccap, ONF_CONSTS.CPOS, cpos, // help
								ONF_CONSTS.CSUF, csuf, // help
								// Global.CFORM,cform,
								ONF_CONSTS.CPREF, cpref, // help
								ONF_CONSTS.P1TOK, Stemmer.stemTerm(p1tok),// help
								ONF_CONSTS.N1TOK, Stemmer.stemTerm(n1tok),// help

								// Global.P1SUF, p1suf, Global.N1SUF, n1suf,
								ONF_CONSTS.P1CAP, p1cap, // help
								ONF_CONSTS.N1CAP, n1cap, // help

								ONF_CONSTS.P2CAP, p2cap, // help
								ONF_CONSTS.N2CAP, n2cap // help
						// , Global.P2POS, p2pos
						// , Global.N2POS, n2pos

						// , Global.P3TOK, Stemmer.stemTerm( p3tok) // hurt
						// , Global.N3TOK, Stemmer.stemTerm( n3tok) // hurt
						// , Global.P3CAP, p3cap
						// , Global.N3CAP, n3cap
						// , Global.P3POS, p3pos
						// , Global.N3POS, n3pos};
						};
						features.add(feature);

						transition.add(new String[] { ONF_CONSTS.N1TYPE, n1type });

					}

					String[] labels = fdamodel.viterbiDecode(theta, features, transition, 1E-5, 1E-5, 1E-4, true);
					// String[] labels = fdamodel.viterbiDecode2(theta,
					// features, transition, 1E-5, 1E-5);
					for (int i = 0; i < sent.getWords().size(); i++) {
						sent.getWords().get(i).setPrediction(labels[i]);
					}
				}
			}
		}
	}

	/**
	 * Exp1: N1 type only: 87.33. Exp2: N3-N1, P1-P3: 90.97. So we hope that
	 * viterbi will use as much information as is used by Exp2, in a different
	 * way of looking at whole sentence.
	 * 
	 * @param fdamodel
	 * @param fdadata
	 * @param foldId
	 * @throws Exception
	 */
	public void inferViterbiPOS(FDMM fdamodel, OntonotesDataSet fdadata, int foldId) throws Exception {

		for (Document doc : fdadata.getDocuments()) {
			System.out.println(doc.getDocId());
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {
					if (sent.getWords().get(0).getBooleanFolds()[0])
						continue;

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

						String p1type = "[p1type]", p2type = "[p2type]", p3type = "[p3type]";
						String n1type = "[n1type]", n2type = "[n2type]", n3type = "[n3type]";

						if (start > 0) {
							p1tok = sent.wordAt(start - 1).getTrimLowered();
							p1cap = sent.wordAt(start - 1).isCapitalizedFirst();
							p1suf = sent.wordAt(start - 1).getSuffix();
							p1form = sent.wordAt(start - 1).getWordForm();
							p1type = sent.wordAt(start - 1).getPrediction();

							if (start > 1) {
								p2tok = sent.wordAt(start - 2).getTrimLowered();
								p2cap = sent.wordAt(start - 2).isCapitalizedFirst();
								p2suf = sent.wordAt(start - 2).getSuffix();
								p2form = sent.wordAt(start - 2).getWordForm();
								p2type = sent.wordAt(start - 2).getPrediction();

								if (start > 2) {
									p3tok = sent.wordAt(start - 3).getTrimLowered();
									p3cap = sent.wordAt(start - 3).isCapitalizedFirst();
									p3suf = sent.wordAt(start - 3).getSuffix();
									p3form = sent.wordAt(start - 3).getWordForm();
									p3type = sent.wordAt(start - 3).getPrediction();

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
								n2type = sent.wordAt(end + 2).getPartOfSpeech();
								if (end < sent.size() - 3) {
									n3tok = sent.wordAt(end + 3).getTrimLowered();
									n3cap = sent.wordAt(end + 3).isCapitalizedFirst();
									n3suf = sent.wordAt(end + 3).getSuffix();
									n3form = sent.wordAt(end + 3).getWordForm();
									n3type = sent.wordAt(end + 3).getPartOfSpeech();
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

					String[] labels = fdamodel.viterbiDecode(theta, features, transition, 1E-5, 1E-5, 1E-5, true);
					// String[] labels = fdamodel.viterbiDecode2(theta,
					// features, transition, 1E-5, 1E-5);
					for (int i = 0; i < sent.getWords().size(); i++) {
						sent.getWords().get(i).setPrediction(labels[i]);
					}
					// return;
				}
			}
		}
	}


	public void evaluate(OntonotesDataSet d) throws Exception {
		// TODO Auto-generated method stub
		
	}
}

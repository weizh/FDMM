package edu.cmu.lti.weizh.train.fdmm.activelearning;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.mlmodel.FDMM;
import edu.cmu.lti.weizh.nlp.EuroLangTwokenizer;
import edu.cmu.lti.weizh.train.FeatureConstants;
import edu.cmu.lti.weizh.train.fdmm.cli.FDA_Inferencer;
import edu.cmu.lti.weizh.utils.Stemmer;

public class FDA_OnlineLearner {

	FDMM posModel;
	static String modelname, path;

	public FDA_OnlineLearner(FDMM posModel2) {
		posModel = posModel2;
	}

	public static void main(String argv[]) throws Exception {

		FDMM nerModel = new FDMM();

		System.err.println("This is the online learner. Input sentence, then tag it.");

		FDA_OnlineLearner onlineLearner = new FDA_OnlineLearner(FDMM.load("POS-rich-randomSent-fold10.0-NOV08.en.FDA_MLModel"));

		onlineLearner.onlineUpdate(nerModel, System.in);

	}

	private void onlineUpdate(FDMM fdamodel, InputStream in) throws Exception {

		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		System.out.print(">");
		String line = null;
		while ((line = br.readLine()) != null) {

			if (line.equalsIgnoreCase("quit")) {
				System.out.println("Do you want to store the model?Y/N");
				line = br.readLine();
				if (line.equalsIgnoreCase("y")) {
					String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
					fdamodel.store(path + timeStamp + "-" + modelname);
				}
				break;
			}

			Sentence rawsent = new Sentence(line);
			Sentence sent = EuroLangTwokenizer.tokenize(rawsent);
			sent = new FDA_Inferencer().setPosmodel(this.posModel).tagSentPOS(sent);
			List<Word> words = sent.getWords();

			for (int i = 0; i < sent.getWords().size(); i++) {
				System.out.println(i + "\t" + words.get(i).getWord() + "\t" + words.get(i).getPartOfSpeech());
				words.get(i).setEntityType("[O]");
			}
			System.out.println("Please tag the named entity with couple [(pos)  (tag)]. If there aren't any, just press D.");

			while ((line = br.readLine()) != null) {
				if (line.equalsIgnoreCase("D"))
					break;
				else {
					String[] toks = line.split("[ ]+");
					if (toks.length != 2) {
						System.err.println("Format rwong. Use (pos) (tag) as rule.");
						continue;
					}
					int index = -1;
					try {
						index = Integer.parseInt(toks[0]);
					} catch (Exception e) {
						System.err.println("Index not parsarble. Follow format (pos) (tag)");
						continue;
					}

					if (index >= words.size() || index < 0 || index == -1) {
						System.err.println("Index out of range. Type in again");
						continue;
					}

					if (FeatureConstants.EntityList.contains(toks[1]) == false) {
						System.err.println("Entity Type not in List. Entity should be in the list below:");
						System.err.println(FeatureConstants.EntityList);
						continue;
					}

					words.get(index).setEntityType(toks[1]);
					System.out.println("Entity Type is set for word " + words.get(index).getWord() + "\t"
							+ words.get(index).getEntityType());
					System.out.println("Are you sure? If not, press X to cancel type. Or just press return.");
					String cancel = null;
					while ((cancel = br.readLine()) != null) {
						if (cancel.equalsIgnoreCase("x")) {
							words.get(index).setEntityType(null);
							break;
						} else if (cancel.length() == 0)
							break;
						else {
							System.err.println("Again, press X to cancel tagging, or return to confirm.");
							continue;
						}
					}
				}
				System.out.println("Please tag the named entity with couple [(pos)  (tag)]. If there aren't any, just press D.");

			}

			int wordposition = 0;
			for (Word word : words) {
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
					p1pos = sent.wordAt(start - 1).getPartOfSpeech();
					p1cap = sent.wordAt(start - 1).isCapitalizedFirst();
					p1suf = sent.wordAt(start - 1).getSuffix();
					p1pref = sent.wordAt(start - 1).getPreffix();
					p1form = sent.wordAt(start - 1).getWordForm();
					p1type = sent.wordAt(start - 1).getEntityType();
					if (start > 1) {
						p2tok = sent.wordAt(start - 2).getLemma();
						p2pos = sent.wordAt(start - 2).getPartOfSpeech();
						p2cap = sent.wordAt(start - 2).isCapitalizedFirst();
						p2suf = sent.wordAt(start - 2).getSuffix();
						p2pref = sent.wordAt(start - 2).getPreffix();
						p2form = sent.wordAt(start - 2).getWordForm();
						p2type = sent.wordAt(start - 2).getEntityType();
						if (start > 2) {
							p3tok = sent.wordAt(start - 3).getLemma();
							p3pos = sent.wordAt(start - 3).getPartOfSpeech();
							p3cap = sent.wordAt(start - 3).isCapitalizedFirst();
							p3suf = sent.wordAt(start - 3).getSuffix();
							p3pref = sent.wordAt(start - 3).getPreffix();
							p3form = sent.wordAt(start - 3).getWordForm();
							p3type = sent.wordAt(start - 3).getEntityType();
						}
					}
				}

				String ctok = word.getLemma();
				String cpos = word.getPartOfSpeech();
				String ccap = word.isCapitalizedFirst();
				String csuf = word.getSuffix();
				String cpref = word.getPreffix();
				String cform = word.getWordForm();

				if (end < sent.size() - 1) {
					n1tok = sent.wordAt(end + 1).getLemma();
					n1pos = sent.wordAt(end + 1).getPartOfSpeech();
					n1cap = sent.wordAt(end + 1).isCapitalizedFirst();
					n1suf = sent.wordAt(end + 1).getSuffix();
					n1pref = sent.wordAt(end + 1).getPreffix();
					n1form = sent.wordAt(end + 1).getWordForm();
					if (end < sent.size() - 2) {
						n2tok = sent.wordAt(end + 2).getLemma();
						n2pos = sent.wordAt(end + 2).getPartOfSpeech();
						n2cap = sent.wordAt(end + 2).isCapitalizedFirst();
						n2suf = sent.wordAt(end + 2).getSuffix();
						n2pref = sent.wordAt(end + 2).getPreffix();
						n2form = sent.wordAt(end + 2).getWordForm();
						if (end < sent.size() - 3) {
							n3tok = sent.wordAt(end + 3).getLemma();
							n3pos = sent.wordAt(end + 3).getPartOfSpeech();
							n3cap = sent.wordAt(end + 3).isCapitalizedFirst();
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

				for (String s : new String[] { Stemmer.stemTerm(ctok), cform, cpos, ccap, cform + "_" + cpos }) {
					fdamodel.add(s, word.getEntityType(), FeatureConstants.P1TOK, sp1tok);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.P2TOK, sp2tok);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.P3TOK, sp3tok);

					fdamodel.add(s, word.getEntityType(), FeatureConstants.N1TOK, sn1tok);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.N2TOK, sn2tok);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.N3TOK, sn3tok);

					fdamodel.add(s, word.getEntityType(), FeatureConstants.P1POS, p1pos);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.P2POS, p2pos);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.P3POS, p3pos);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.N1POS, n1pos);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.N2POS, n2pos);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.N3POS, n3pos);

					fdamodel.add(s, word.getEntityType(), FeatureConstants.P1CAP, p1cap);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.P2CAP, p2cap);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.P3CAP, p3cap);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.N1CAP, n1cap);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.N2CAP, n2cap);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.N3CAP, n3cap);

					fdamodel.add(s, word.getEntityType(), FeatureConstants.P1SUF, p1suf);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.P2SUF, p2suf);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.P3SUF, p3suf);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.N1SUF, n1suf);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.N2SUF, n2suf);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.N3SUF, n3suf);

					fdamodel.add(s, word.getEntityType(), FeatureConstants.P1PREF, p1pref);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.P2PREF, p2pref);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.P3PREF, p3pref);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.N1PREF, n1pref);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.N2PREF, n2pref);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.N3PREF, n3pref);

					fdamodel.add(s, word.getEntityType(), FeatureConstants.P1FORM, p1form);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.P2FORM, p2form);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.P3FORM, p3form);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.N1FORM, n1form);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.N2FORM, n2form);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.N3FORM, n3form);

					fdamodel.add(s, word.getEntityType(), FeatureConstants.CCAP, ccap);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.CPOS, cpos);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.CTOK, sctok);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.CSUF, csuf);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.CPREF, cpref);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.CFORM, cform);

					// add previous label feature.
					fdamodel.add(s, word.getEntityType(), FeatureConstants.P1TYPE, p1type);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.P2TYPE, p2type);
					fdamodel.add(s, word.getEntityType(), FeatureConstants.P3TYPE, p3type);
				}
			}

			System.out.print(">");
		}
	}
}

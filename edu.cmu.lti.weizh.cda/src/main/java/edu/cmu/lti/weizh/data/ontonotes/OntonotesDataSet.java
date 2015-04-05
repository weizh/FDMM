package edu.cmu.lti.weizh.data.ontonotes;

import java.util.List;

import edu.cmu.lti.weizh.docmodel.AbstractDataSet;
import edu.cmu.lti.weizh.docmodel.Document;
import edu.cmu.lti.weizh.docmodel.NamedEntity;
import edu.cmu.lti.weizh.docmodel.Paragraph;
import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;

public class OntonotesDataSet extends AbstractDataSet {

	public OntonotesDataSet(int capacity, String type) {
		super(capacity, type);
	}



	public void predictionsNE_EntitybyEntity_Stat() throws Exception {

		TObjectIntHashMap<String> correct, totalGold, totalPredict;
		correct = new TObjectIntHashMap<String>();
		totalGold = new TObjectIntHashMap<String>();
		totalPredict = new TObjectIntHashMap<String>();

		for (Document doc : documents) {
			// System.out.println(doc.getDocId());
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {
					List<NamedEntity> nes = sent.getNamedEntities();
					for (NamedEntity ne : nes) {
						if (ne.getPrediction() == null)
							System.err.println("Not predicted Named Entity!");

						if (ne.getEntityType().equals(ne.getPrediction()))
							if (!correct.contains(ne.getEntityType()))
								correct.put(ne.getEntityType(), 1);
							else
								correct.increment(ne.getEntityType());
						if (totalGold.contains(ne.getEntityType()))
							totalGold.increment(ne.getEntityType());
						else
							totalGold.put(ne.getEntityType(), 1);
						if (totalPredict.contains(ne.getPrediction()))
							totalPredict.increment(ne.getPrediction());
						else
							totalPredict.put(ne.getPrediction(), 1);
					}
				}
			}
		}
		TObjectIntIterator<String> ci = correct.iterator();

		double accf1 = 0;
		int n = 0;
		while (ci.hasNext()) {
			n++;
			ci.advance();
			String label = ci.key();
			int val = ci.value();
			int totalgold = totalGold.get(label);
			int totalpredict = totalPredict.get(label);
			double p = (double) val / (double) totalpredict;
			double r = (double) val / (double) totalgold;
			double f1 = 2 * p * r / (p + r);
			// System.out.println("Label class "+ label +"	: "+ " P " + p
			// +"	R "+r + "	F1 "+ f1);
			System.out.printf("Label class	%s	:	P:	%.4f	R:	%.4f	F1:	%.4f\n", label, p, r, f1);
			accf1 += f1;
		}
		System.out.printf("Average f1 of all class is: %.4f", accf1 / (double) n);
	}

	public void prettyPrintNEs() throws Exception {
		for (Document doc : documents) {
			System.out.println(doc.getDocId());

			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {
					System.out.println(sent.getPlainSentence());
					int i = 0;
					for (Word word : sent.getWords()) {
						System.out.println((i++) + " " + word + " " + word.getPartOfSpeech());
					}
					List<NamedEntity> nes = sent.getNamedEntities();
					System.out.println("-----------\nNamed entities: ");
					for (NamedEntity ne : nes) {
						System.out.println(ne.getStart() + "-" + ne.getEnd() + " " + ne.getEntityType());
					}
				}
			}
		}

	}

}

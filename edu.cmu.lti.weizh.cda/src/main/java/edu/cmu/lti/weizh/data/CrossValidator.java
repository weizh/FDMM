package edu.cmu.lti.weizh.data;

import java.util.Random;

import edu.cmu.lti.weizh.data.ontonotes.OntonotesDataSet;
import edu.cmu.lti.weizh.docmodel.Document;
import edu.cmu.lti.weizh.docmodel.Paragraph;
import edu.cmu.lti.weizh.docmodel.Sentence;

public class CrossValidator {

	OntonotesDataSet total, train, test;
	double trainingProportion = 0.8;

	public CrossValidator(OntonotesDataSet onfDataSet, double trProp) {
		if (trProp < 1 && trProp > 0)
			trainingProportion = trProp;

		Random r = new Random();
		
		total = onfDataSet;
		train = new OntonotesDataSet(10000, total.getEvalType() + "train");
		test = new OntonotesDataSet(10000, total.getEvalType() + "test");

		Document trainDoc = new Document(null);
		Document testDoc = new Document(null);
		Paragraph trainPara = new Paragraph();
		Paragraph testPara = new Paragraph();
		
		for (Document d : onfDataSet.getDocuments())
			try {
				for (Paragraph p : d.getParagraphs())
					for (Sentence s : p.getSentences()) {
						if (r.nextDouble()>trainingProportion)
							testPara.addSentence(s);
						else
							trainPara.addSentence(s);
					}
				trainDoc.addParagraph(trainPara);
				testDoc.addParagraph(testPara);
				train.getDocuments().add(trainDoc);
				test.getDocuments().add(testDoc);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public OntonotesDataSet getTrain() {
		return train;
	}

	public OntonotesDataSet getTest() {
		return test;
	}
}

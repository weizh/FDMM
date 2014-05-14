package edu.cmu.lti.weizh.fda;

import java.util.List;

import edu.cmu.lti.weizh.Interface.Trainer;
import edu.cmu.lti.weizh.models.DataSet;
import edu.cmu.lti.weizh.models.Document;
import edu.cmu.lti.weizh.models.MLModel;
import edu.cmu.lti.weizh.models.NamedEntity;
import edu.cmu.lti.weizh.models.Paragraph;
import edu.cmu.lti.weizh.models.Sentence;

public class FDA_Trainer implements Trainer {

	
	public MLModel train(DataSet data) throws Exception {

		if (!(data instanceof FDA_DataSet))
			throw new Exception("Training data for FDA-Trainer is not type FDA_dataset.");

		FDA_DataSet fdadata = (FDA_DataSet) data;

		FDA_MLModel fdamodel = new FDA_MLModel();
		/*
		 * populate the dataset into the model.
		 */
		for (Document doc : fdadata.getDocuments()) {
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {

					/*
					 * get the named entities.
					 */
					List<NamedEntity> nes = sent.getNamedEntities();

					for (NamedEntity ne : nes) {
						int start = ne.getStart();
						int end = ne.getEnd();
						// entity starts the sentence
						String ptok = "[0PTOK]", pcap = "[0PCAP]", ppos = "[0PPOS]";
						String ntok = "[0NTOK]", ncap = "[0NCAP]", npos = "[0NPOS]";
						String ctok = "[0CTOK]", ccap = "[0CCAP]", cpos = "[0CPOS]";

						if (start != 0) {
							ptok = sent.wordAt(start - 1).getLemma();
						}
						if (end == sent.size() - 1) {
						}

					}

				}
			}
		}
		return fdamodel;
	}

}

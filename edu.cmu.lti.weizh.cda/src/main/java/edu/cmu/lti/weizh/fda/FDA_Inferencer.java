package edu.cmu.lti.weizh.fda;

import java.io.File;
import java.util.List;

import edu.cmu.lti.weizh.Interface.Inferencer;
import edu.cmu.lti.weizh.models.Document;
import edu.cmu.lti.weizh.models.Global;
import edu.cmu.lti.weizh.models.NamedEntity;
import edu.cmu.lti.weizh.models.Paragraph;
import edu.cmu.lti.weizh.models.Sentence;

public class FDA_Inferencer implements Inferencer<FDA_MLModel, FDA_DataSet> {

	public static void main(String arv[]) throws Exception {

		FDA_DataSet testSet = new FDA_DataSet();
		String path = "C:/Users/Wynn/Documents/wynnzh/data/LDC/data/english/annotations/";

		System.out.println("loading data:");
		ONF2FDADImporter.fill(new File(path), testSet);

		// testSet.prettyPrint();

		System.out.println("loading model:");
		FDA_MLModel model = FDA_MLModel.load("pp-p-n-nn(tok-pos).en.FDA_MLModel");
//		model.print();
		new FDA_Inferencer().infer(model, testSet);

		System.out.println("Print result:");
		testSet.predictionsStat();
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
						String ptok = "[0PTOK]";// , pcap = "[0PCAP]";
						String ntok = "[0NTOK]";// , ncap = "[0NCAP]";
						// String ccap = "[0CCAP]";//, ctok = "[0CTOK]",cpos =
						// "[0CPOS]";
						String pptok = "[-1PTOK]", nntok = "[1NTOK]";
						String ppos = "[0PPOS]", npos = "[0NPOS]";
						String pppos = "[-1PPOS]", nnpos = "[1NPOS]";

						if (start > 0) {
							ptok = sent.wordAt(start - 1).getLemma();
							ppos = sent.wordAt(start - 1).getPos();
							if (start > 1) {
								pptok = sent.wordAt(start - 2).getLemma();
								pppos = sent.wordAt(start - 2).getPos();
							}
						}

						String ctok = ne.getLemma();
//						String ctok = "[COLLAPSED]";
//						String ctok = ne.getPOS();
						// ccap = ne.getCap();
						// cpos = ne.getPos();

						if (end < sent.size() - 1) {
							ntok = sent.wordAt(end + 1).getLemma();
							npos = sent.wordAt(end + 1).getPos();
							if (end < sent.size() - 2) {
								nntok = sent.wordAt(end + 2).getLemma();
								nnpos = sent.wordAt(end + 2).getPos();
							}
						}

						String label = fdamodel.predict(ctok, 0.00001, 0.00001, 
								Global.F_PTOK, ptok, Global.F_NTOK, ntok
//								,Global.F_PPTOK, pptok, Global.F_NNTOK, nntok
								
//								,  Global.F_PPOS,ppos, Global.F_NPOS, npos, Global.F_PPPOS, pppos, Global.F_NNPOS, nnpos
								);

						ne.setPrediction(label);
					}
				}
			}
		}
	}

}

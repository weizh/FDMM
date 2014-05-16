package edu.cmu.lti.weizh.fda;

import java.io.File;
import java.util.List;

import edu.cmu.lti.weizh.Interface.Trainer;
import edu.cmu.lti.weizh.models.DataSet;
import edu.cmu.lti.weizh.models.Document;
import edu.cmu.lti.weizh.models.Global;
import edu.cmu.lti.weizh.models.NamedEntity;
import edu.cmu.lti.weizh.models.Paragraph;
import edu.cmu.lti.weizh.models.Sentence;

public class FDA_Trainer implements Trainer<FDA_MLModel, FDA_DataSet> {

	
  public static void main(String argv[]) throws Exception{
    FDA_DataSet fdad = new FDA_DataSet();
    String path = "C:/Users/Wynn/Documents/wynnzh/data/LDC/data/english/annotations/";
    ONF2FDADImporter.fill(new File(path), fdad);
    fdad.crossValidationSplit(5);
    System.out.println(fdad.getDocuments().size());
    fdad.prettyPrint();
    FDA_MLModel model = new FDA_Trainer().train(fdad);
    
    model.store("pp-p-n-nn(tok-pos).en.FDA_MLModel");  
    
  }
	public FDA_MLModel train(FDA_DataSet fdadata) throws Exception {

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
						
						System.out.println(doc.getDocId()+"\n"+sent.getPlainSentence());
						System.out.println("sent size is :"+sent.getWords().size());
						System.out.println(ne);
						
						int start = ne.getStart();
						int end = ne.getEnd();

						// entity starts the sentence
						String ptok = "[0PTOK]";//, pcap = "[0PCAP]";
						String ntok = "[0NTOK]";//, ncap = "[0NCAP]";
//						String  ccap = "[0CCAP]";//, ctok = "[0CTOK]",cpos = "[0CPOS]";
						String pptok = "[-1PTOK]",  nntok = "[1NTOK]";
						String ppos = "[0PPOS]", npos = "[0NPOS]";
						String pppos = "[-1PPOS]", nnpos = "[1NPOS]";

						if (start >0) {
							ptok = sent.wordAt(start - 1).getLemma();
							ppos = sent.wordAt(start - 1).getPos();
							if (start >1){
								pptok=sent.wordAt(start-2).getLemma();
								pppos = sent.wordAt(start-2).getPos();
							}
						}
						
						String ctok = ne.getLemma();
//						String ctok = "["+ne.getEntityType()+"]";
//						String ctok = "[COLLAPSED]";
//						String ctok = ne.getPOS();
//						ccap = ne.getCap();
//						cpos = ne.getPos();

						if (end < sent.size() - 1) {
							ntok = sent.wordAt(end+1).getLemma();
							npos = sent.wordAt(end+1).getPos();
							if (end<sent.size()-2){
								nntok = sent.wordAt(end+2).getLemma();
								nnpos = sent.wordAt(end+2).getPos();
							}
						}
						
						fdamodel.add(ctok,ne.getEntityType(),Global.F_PTOK,ptok);
						fdamodel.add(ctok,ne.getEntityType(),Global.F_NTOK,ntok);	
						fdamodel.add(ctok, ne.getEntityType(), Global.F_PPTOK, pptok);
						fdamodel.add(ctok, ne.getEntityType(), Global.F_NNTOK, nntok);
						fdamodel.add(ctok,ne.getEntityType(),Global.F_PPOS,ppos);
						fdamodel.add(ctok,ne.getEntityType(),Global.F_NPOS,npos);	
						fdamodel.add(ctok,ne.getEntityType(),Global.F_PPPOS,pppos);
						fdamodel.add(ctok,ne.getEntityType(),Global.F_NNPOS,nnpos);							
					}

				}
			}
		}
		return fdamodel;
	}

}

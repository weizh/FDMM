package edu.cmu.lti.weizh.hmm;

import java.io.File;

import edu.cmu.lti.weizh.Interface.Inferencer;
import edu.cmu.lti.weizh.models.Document;
import edu.cmu.lti.weizh.models.Global;
import edu.cmu.lti.weizh.models.Paragraph;
import edu.cmu.lti.weizh.models.Sentence;
import edu.cmu.lti.weizh.models.Word;
import edu.cmu.lti.weizh.ontonotes.ONFImporter;
import edu.cmu.lti.weizh.ontonotes.ONF_DataSet;
import edu.cmu.lti.weizh.utils.Stemmer;

public class HMM_Inferencer implements Inferencer{

	public static void main(String argvp[]) throws Exception
	{
		ONF_DataSet testSet = new ONF_DataSet();
		String path = "C:/Users/Wynn/Documents/wynnzh/data/LDC/data/english/annotations/nw/wsj/00";

		System.out.println("loading data:");
		ONFImporter.fill(new File(path), testSet);

//		testSet.fillCVFolds("10-crf-split.txt");
		testSet.fillCVFolds("10-POSMODEL-split.txt");
		
		int ifold = 0;
		
		HMM_MLModel hmmModel = HMM_MLModel.load("SENT-POS-fold10."+ifold+".en.HMM_MLModel");
		
		new HMM_Inferencer().infer(hmmModel, testSet, ifold);
		
		testSet.predictionsWordsStatSentPOS(ifold);
		
	}


	private void infer(HMM_MLModel hmmModel, ONF_DataSet testSet, int ifold) throws Exception {

		for (Document doc : testSet.getDocuments()) {
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {
					
					if (sent.getFolds()[ifold])
						continue;
					String[] label = hmmModel.veterbiDecode(sent);
					if (label ==null) continue;
					int l = 0;
					for (Word word : sent.getWords()) {
						word.setPrediction(label[l++]);
					}
					
				}
			}
		}
	}
		


	@Override
	public void infer(Object model, Object d) throws Exception {
		// TODO Auto-generated method stub
		
	}
}

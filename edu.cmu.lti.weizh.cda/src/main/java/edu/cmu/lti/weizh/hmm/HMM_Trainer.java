package edu.cmu.lti.weizh.hmm;

import java.io.File;
import java.util.List;

import edu.cmu.lti.weizh.Interface.Trainer;
import edu.cmu.lti.weizh.fda.FDA_MLModel;
import edu.cmu.lti.weizh.models.Document;
import edu.cmu.lti.weizh.models.Global;
import edu.cmu.lti.weizh.models.NamedEntity;
import edu.cmu.lti.weizh.models.Paragraph;
import edu.cmu.lti.weizh.models.Sentence;
import edu.cmu.lti.weizh.models.Word;
import edu.cmu.lti.weizh.ontonotes.ONFImporter;
import edu.cmu.lti.weizh.ontonotes.ONF_DataSet;
import edu.cmu.lti.weizh.utils.Stemmer;

public class HMM_Trainer  implements Trainer {

	public static void main(String argbp[]) throws Exception{
		ONF_DataSet trainset = new ONF_DataSet();
		String path = "C:/Users/Wynn/Documents/wynnzh/data/LDC/data/english/annotations/nw/wsj";

		System.out.println("loading data:");
		ONFImporter.fill(new File(path), trainset);

//		testSet.fillCVFolds("10-crf-split.txt");
		trainset.fillCVFolds("10-POSMODEL-split.txt");
		
		int fold = 10;
		
		HMM_Trainer hmmTrainer = new HMM_Trainer();
		
		for (int i = 0; i < (int) fold; i++) {
			HMM_MLModel hmm = hmmTrainer.trainSentPOS(trainset, fold, i);
			hmm.store("SENT-POS-fold10."+i+".en.HMM_MLModel");
		}
	}

	@Override
	public Object train(Object d, double fold, int foldId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Train POS with random sentences, not random tokens.
	 * @param fdadata
	 * @param fold
	 * @param ifold
	 * @return
	 * @throws Exception
	 */
	public HMM_MLModel trainSentPOS(ONF_DataSet fdadata, double fold, int ifold) throws Exception{

		HMM_MLModel hmmModel = new HMM_MLModel();
		
		/*
		 * populate the dataset into the model.
		 */
		for (Document doc : fdadata.getDocuments()) {
			for (Paragraph para : doc.getParagraphs()) {
				for (Sentence sent : para.getSentences()) {
					List<Word> words = sent.getWords();
					
					if (ifold != Double.POSITIVE_INFINITY)
						if (sent.getFolds()[ifold] == false)
							continue;
					
					/*
					 * get the named entities.
					 */
					List<NamedEntity> nes = sent.getNamedEntities();
					
					int wordposition = 0;
					
					// lemmatization is done in the 'add' function.
					hmmModel.add(sent,true);
				}
			}
		}
		return hmmModel;
	}
}

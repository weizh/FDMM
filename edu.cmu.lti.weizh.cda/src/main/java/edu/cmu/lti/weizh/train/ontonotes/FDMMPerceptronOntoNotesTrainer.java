package edu.cmu.lti.weizh.train.ontonotes;

import java.io.File;
import java.util.List;

import edu.cmu.lti.weizh.data.ontonotes.OntoNotesDataFiller;
import edu.cmu.lti.weizh.data.ontonotes.OntonotesDataSet;

import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.eval.EVAL_CONSTS;
import edu.cmu.lti.weizh.mlmodel.FDMM;
import edu.cmu.lti.weizh.train.AbstractFDMMPerceptronTrainer;


public class FDMMPerceptronOntoNotesTrainer 
extends AbstractFDMMPerceptronTrainer<String, FDMMPerceptronOntoNotesTrainer, OntonotesDataSet>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String argv[]) throws Exception {

		FDMMPerceptronOntoNotesTrainer fdatrainer = new FDMMPerceptronOntoNotesTrainer();

		String mode = "NER";
		double fold = 10;
		String date = "NOV08";

		OntonotesDataSet fdad = new OntonotesDataSet(300000, mode.equals("NER")?EVAL_CONSTS.NER_TYPE:EVAL_CONSTS.POS_TYPE );
		String path = "/home/wei/Documents/annotations";
		
		new OntoNotesDataFiller(fdad).fill(new File(path));
		
		String foldFile =  "10-randomSent-CVfolds-OCT25.txt";
		String modelFile = mode+"10-rich-randomSent-fold10.";

		fdad.fillCVFolds(foldFile);

		for (int i = 0; i < (int) fold; i++) {
			System.out.println("training " + mode + " fold :" + i);

			FDMM fdamodel = null;

		}
	}

	@Override
	public String getGoldLabel(Word w) {
		return w.getEntityType();
	}

	@Override
	protected FDMMPerceptronOntoNotesTrainer self() {
		return this;
	}

	@Override
	public List<Sentence> getSentences(OntonotesDataSet d) {
		try {
			return d.getDocuments().get(0).getParagraphs().get(0).getSentences();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
}

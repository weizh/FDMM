package edu.cmu.lti.weizh.nlp.ner;


import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.eval.Prediction;
import edu.cmu.lti.weizh.nlp.AbstractPercTagger;
import edu.cmu.lti.weizh.train.ontonotes.ON_PercTrain_NER;

public class PercTaggerNerOnf extends AbstractPercTagger<ON_PercTrain_NER> {

	public PercTaggerNerOnf(String model) {
		super(model);
	}
	
	@Override
	public ON_PercTrain_NER getTrainerModel() {
		return new ON_PercTrain_NER();
	}

	@Override
	protected void setWordLabel(Word w, String string) {
		w.setEntityType(string);
	}

	@Override
	protected void setWordLabelDist(Word word, Prediction prediction) {
		word.setNerPDist(prediction);
	}
}

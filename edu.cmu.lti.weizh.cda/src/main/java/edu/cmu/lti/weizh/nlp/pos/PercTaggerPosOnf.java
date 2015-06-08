package edu.cmu.lti.weizh.nlp.pos;

import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.eval.Prediction;
import edu.cmu.lti.weizh.nlp.AbstractPercTagger;
import edu.cmu.lti.weizh.train.ontonotes.ON_PercTrain_POS;

public class PercTaggerPosOnf extends AbstractPercTagger<ON_PercTrain_POS> {
	
	public PercTaggerPosOnf(String model) {
		super(model);
	}
	
	@Override
	public ON_PercTrain_POS getTrainerModel() {
		return new ON_PercTrain_POS();
	}

	@Override
	protected void setWordLabel(Word w, String string) {
		w.setPartOfSpeech(string);
	}

	@Override
	protected void setWordLabelDist(Word word, Prediction prediction) {
		word.setPosPDist(prediction);
	}
}

package edu.cmu.lti.weizh.fdmm;

import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.nlp.pos.PercTaggerPosOnf;
import edu.cmu.lti.weizh.nlp.tokenizer.EuroLangTwokenizer;

public class testPOS {
	
	public static void main(String argv[]) throws Exception{
		String s = "This is a first test.";
		Sentence sent = new Sentence(s);
		EuroLangTwokenizer.tokenize(sent);
		PercTaggerPosOnf postagger = new PercTaggerPosOnf("trainedModels/ONF_CNNTrain_POS_PERC_100_NegInf_basic.trainer");
		postagger.tag(sent);
		for (Word w : sent.getWords()){
			System.out.println(w.getWord()+"\t"+ w.getPartOfSpeech());
		}
	}

}

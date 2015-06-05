package edu.cmu.lti.weizh.fdmm;

import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.nlp.EuroLangTwokenizer;
import edu.cmu.lti.weizh.nlp.pos.POSTagger_ONF;

public class testPOS {
	
	public static void main(String argv[]) throws Exception{
		String s = "This is a first test.";
		Sentence sent = new Sentence(s);
		EuroLangTwokenizer.tokenize(sent);
		POSTagger_ONF postagger = new POSTagger_ONF("FakeModelName");
		postagger.tag(sent);
		
	}

}

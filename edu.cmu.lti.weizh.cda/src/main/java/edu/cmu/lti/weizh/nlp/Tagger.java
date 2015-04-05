package edu.cmu.lti.weizh.nlp;

import edu.cmu.lti.weizh.docmodel.Sentence;

public interface Tagger {

	Sentence tag(Sentence s) throws Exception;
}

package edu.cmu.lti.weizh.Interface;

import edu.cmu.lti.weizh.models.Sentence;

public interface Tagger {

	Sentence tag(Sentence s) throws Exception;
}

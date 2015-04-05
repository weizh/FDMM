package edu.cmu.lti.weizh.io;

import edu.cmu.lti.weizh.docmodel.Sentence;

public interface SentenceReader {

	public Sentence readNextSentence() throws Exception;
}

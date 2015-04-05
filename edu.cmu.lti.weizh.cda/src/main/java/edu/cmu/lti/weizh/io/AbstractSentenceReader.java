package edu.cmu.lti.weizh.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;


public abstract class AbstractSentenceReader implements SentenceReader{

	protected BufferedReader reader;

	public AbstractSentenceReader(Reader in) {
		reader = new BufferedReader(in);
	}
	
	public void close() throws IOException{
		reader.close();
	};

}

package edu.cmu.lti.weizh.data.nlpba;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.io.AbstractSentenceReader;

public class NLPBAReader extends AbstractSentenceReader {

	public NLPBAReader(Reader in) {
		super(in);
	}

	@Override
	public Sentence readNextSentence() throws Exception {

		Sentence sentence = null;
		List<Word> words = new ArrayList<Word>();
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line.length() != 0) {
				String[] t = line.trim().split(" ");
				Word w = new Word(t[0]);
				w.setChunkType(t[1]);
				words.add(w);
			} else {
				if (words.size() != 0) {
					sentence = new Sentence(words);
					words = new ArrayList<Word>();
				}
				break;
			}
		}
		return sentence;
	
	}

}

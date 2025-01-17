package edu.cmu.lti.weizh.data.conll2000;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.io.AbstractSentenceReader;

public class CONLL2kReader extends AbstractSentenceReader {

	public CONLL2kReader(Reader in) {
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
				Word w = new Word(t[0], t[1]);
				w.setChunkType(t[2]);
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

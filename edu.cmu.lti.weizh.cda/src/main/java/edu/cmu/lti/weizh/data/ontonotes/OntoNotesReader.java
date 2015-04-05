package edu.cmu.lti.weizh.data.ontonotes;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.weizh.docmodel.NamedEntity;
import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Tree;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.io.AbstractSentenceReader;

public class OntoNotesReader extends AbstractSentenceReader{

	Tree parsetree;

	String PTBSentence;

	List<NamedEntity> NEs;

	List<Word> words;

	String line;
	
	OntoNotesReader(Reader in) {
		super(in);
	}

	public Sentence readNextSentence() throws Exception {
		int state = 0;
		Sentence sentence = null;
		while ((line = reader.readLine()) != null) {
			if (state == 0) {
				if (line.startsWith("-----------------------------------------------------------------"))
					continue;
				else if (line.trim().length() == 0)
					continue;
				else if (line.startsWith("Plain sentence:")) {
					reader.readLine();
					StringBuilder sb = new StringBuilder();
					while ((line = reader.readLine()).length() != 0)
						sb.append(line);
					sentence = new Sentence(sb.toString());
					// System.out.println(this.sentence.getPlainSentence());
				} else if (line.startsWith("Treebanked sentence:")) {
					reader.readLine();
					this.PTBSentence = reader.readLine();
					while ((line = reader.readLine()).length() != 0)
						;
				} else if (line.startsWith("Tree:")) {
					state = 1;
					// System.out.println(line);
					reader.readLine();
					StringBuilder tree = new StringBuilder();
					this.words = new ArrayList<Word>();
					while ((line = reader.readLine()) != null) {
						if (line.trim().length() == 0)
							continue;
						if (line.startsWith("Leaves:")) {
							state = 2;
							break;
						}
						// System.out.println(line);
						if (line.trim().startsWith("(") && line.trim().endsWith(")") == false) {
							line = line + reader.readLine();
						}
						String[] toks = line.split("[(]");
						// System.out.println(toks[toks.length-1]);
						String meat = toks[toks.length - 1].split("[)]")[0];
						String[] sp = meat.split("[ ]+");
						// System.out.println(meat);
						words.add(new Word(sp[1], sp[0]));
						tree.append(line).append("\n");
					}
					sentence.setWords(this.words);
					sentence.setTree(parseTreeString(tree.toString()));
				}
			} else if (state == 2) {
				state = 0;
				this.NEs = new ArrayList<NamedEntity>();
				reader.readLine();
				while ((line = reader.readLine()) != null) {
					if (line.trim().length() == 0)
						break;
					if (line.startsWith("           ")) {
						// it's a feature.
						if (line.trim().startsWith("name:")) {
							// it's a named entity line
							String[] toks = line.trim().split("[ ]+");
							String[] pos = toks[2].split("-");
							if (pos[0].length()==3) pos[1]=pos[1].substring(0,3);
//							if (pos[1].length() > 3)
//								throw new Exception("position is corrupted:" + pos[1]);
							int start = Integer.parseInt(pos[0]), end = Integer.parseInt(pos[1]);
							NamedEntity ne = new NamedEntity(toks[1], start, end, sentence);
							this.NEs.add(ne);
						} else
							// it's proposition.
							continue;
					} else { // it's a word
						String[] toks = line.trim().split("[ ]+");
					}
				} // eow token list
				sentence.setNEs(this.NEs);
				break;
			} // eif leave
			else if (line.startsWith("Speaker information")) {
				while (reader.readLine().length() != 0)
					;
			} else if (line.startsWith("========================================================="))
				break;
		} // eow outer while

		return sentence;

	}

	public static Tree parseTreeString(String string) {
	    // TODO Auto-generated method stub
	    return new Tree();    
	  }
	
	public void close() throws IOException {

		reader.close();
	}

}

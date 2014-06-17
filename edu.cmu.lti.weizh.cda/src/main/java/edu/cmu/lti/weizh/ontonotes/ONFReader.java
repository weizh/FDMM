package edu.cmu.lti.weizh.ontonotes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.weizh.fda.Utils;
import edu.cmu.lti.weizh.models.NamedEntity;
import edu.cmu.lti.weizh.models.Sentence;
import edu.cmu.lti.weizh.models.Tree;
import edu.cmu.lti.weizh.models.Word;

public class ONFReader {

	Sentence sentence;

	Tree parsetree;

	String PTBSentence;

	List<NamedEntity> NEs;

	List<Word> words;

	BufferedReader br;

	String line;
	
	ONFReader(Reader in) {
		br = new BufferedReader(in);
	}

	public Sentence readNextSentence() throws Exception {
		int state = 0;
		sentence = null;
		while ((line = br.readLine()) != null) {
			if (state == 0) {
				if (line.startsWith("-----------------------------------------------------------------"))
					continue;
				else if (line.trim().length() == 0)
					continue;
				else if (line.startsWith("Plain sentence:")) {
					br.readLine();
					StringBuilder sb = new StringBuilder();
					while ((line = br.readLine()).length() != 0)
						sb.append(line);
					this.sentence = new Sentence(sb.toString());
					// System.out.println(this.sentence.getPlainSentence());
				} else if (line.startsWith("Treebanked sentence:")) {
					br.readLine();
					this.PTBSentence = br.readLine();
					while ((line = br.readLine()).length() != 0)
						;
				} else if (line.startsWith("Tree:")) {
					state = 1;
					// System.out.println(line);
					br.readLine();
					StringBuilder tree = new StringBuilder();
					this.words = new ArrayList<Word>();
					while ((line = br.readLine()) != null) {
						if (line.trim().length() == 0)
							continue;
						if (line.startsWith("Leaves:")) {
							state = 2;
							break;
						}
						// System.out.println(line);
						if (line.trim().startsWith("(") && line.trim().endsWith(")") == false) {
							line = line + br.readLine();
						}
						String[] toks = line.split("[(]");
						// System.out.println(toks[toks.length-1]);
						String meat = toks[toks.length - 1].split("[)]")[0];
						String[] sp = meat.split("[ ]+");
						// System.out.println(meat);
						words.add(new Word(sp[1], sp[0]));
						tree.append(line).append("\n");
					}
					this.sentence.setWords(this.words);
					this.sentence.setTree(Utils.parseTreeString(tree.toString()));
				}
			} else if (state == 2) {
				state = 0;
				this.NEs = new ArrayList<NamedEntity>();
				br.readLine();
				while ((line = br.readLine()) != null) {
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
							NamedEntity ne = new NamedEntity(toks[1], start, end, this.sentence);
							this.NEs.add(ne);
						} else
							// it's proposition.
							continue;
					} else { // it's a word
						String[] toks = line.trim().split("[ ]+");
					}
				} // eow token list
				this.sentence.setNEs(this.NEs);
				break;
			} // eif leave
			else if (line.startsWith("Speaker information")) {
				while (br.readLine().length() != 0)
					;
			} else if (line.startsWith("========================================================="))
				break;
		} // eow outer while

		return sentence;

	}

}

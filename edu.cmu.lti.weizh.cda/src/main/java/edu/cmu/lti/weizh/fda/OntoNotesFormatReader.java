package edu.cmu.lti.weizh.fda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.weizh.models.NamedEntity;
import edu.cmu.lti.weizh.models.Sentence;
import edu.cmu.lti.weizh.models.Tree;
import edu.cmu.lti.weizh.models.Word;

public class OntoNotesFormatReader {

  Sentence sentence;

  Tree parsetree;

  String PTBSentence;

  List<NamedEntity> NEs;

  List<Word> words;

  BufferedReader br;

  String line;

  OntoNotesFormatReader(Reader in) {
    br = new BufferedReader(in);
  }

  public Sentence readNextSentence() throws IOException {

    while ((line = br.readLine()) != null) {
      if (line.startsWith("-----------------------------------------------------------------"))
        continue;
      if (line.trim().length() == 0)
        continue;
      if (line.startsWith("Plain sentence:")) {
        br.readLine();
        this.sentence = new Sentence(br.readLine());
      }
      if (line.startsWith("Treebanked sentence:")) {
        br.readLine();
        this.PTBSentence = br.readLine();
      }
      if (line.startsWith("Tree")) {
        br.readLine();
        StringBuilder tree = new StringBuilder();
        this.words = new ArrayList<Word>();
        while ((line = br.readLine()) != null) {
          if (line.trim().length() == 0)
            break;
          String[] toks = line.split("(");
          String meat = toks[toks.length-1].split(")")[0];
          String[] sp = meat.split(" ");
          words.add(new Word(sp[0],sp[1]));
          tree.append(line).append("\n");
        }
        this.sentence.setWords(this.words);
        this.sentence.setTree(Utils.parseTreeString(tree.toString()));
      }

      if (line.startsWith("Leaves:")) {

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
              NamedEntity ne = new NamedEntity(toks[1], Integer.parseInt(pos[0]),
                      Integer.parseInt(pos[1]),this.sentence);
              this.NEs.add(ne);
            } else
              // it's proposition.
              continue;
          } else { // it's a word
            String[] toks = line.trim().split("[ ]+");
            this.words.add(new Word(toks[1]));
          }
        } // eow token list
        this.sentence.setNEs(this.NEs);
        break;
      } // eif leave
      
      
    } // eow outer while
    if (sentence.isEmpty())
      return null;
    else{
    	for (Word w : sentence.getWords()){
    		System.out.println(w);
    	}
    	for (NamedEntity e : sentence.getNamedEntities()){
    		System.out.println(e);
    	}
      return sentence;
    }
    
  }

}

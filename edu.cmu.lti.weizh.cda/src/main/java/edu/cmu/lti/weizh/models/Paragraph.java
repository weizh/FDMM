package edu.cmu.lti.weizh.models;

import java.util.ArrayList;
import java.util.List;

public class Paragraph {

  List<Sentence> sentences;

  public Paragraph(){
	  this.sentences=new ArrayList<Sentence>();
  }
  public List<Sentence> getSentences() throws Exception {
    return sentences;
  }
  
  public void addSentence(Sentence sentence){
	  sentences.add(sentence);
  }
}

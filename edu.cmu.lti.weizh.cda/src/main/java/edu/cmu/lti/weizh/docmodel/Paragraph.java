package edu.cmu.lti.weizh.docmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Paragraph implements Serializable {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
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

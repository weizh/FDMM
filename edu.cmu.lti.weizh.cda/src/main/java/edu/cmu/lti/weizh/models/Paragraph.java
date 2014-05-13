package edu.cmu.lti.weizh.models;

import java.util.List;

public class Paragraph {

  List<Sentence> sentences;

  public List<Sentence> getSentences() throws Exception {
    if (sentences ==null){
      throw new Exception("Sentence not created in Paragraph.");
    }
    return sentences;
  }
}

package edu.cmu.lti.weizh.models;

import java.util.List;

public class Sentence {

  /*
   * The real meats are in the word.
   */
  List<Word> words;
  
  /*
   * entities are only positions.
   */
  List<NamedEntity> entities;

  public List<NamedEntity> getNamedEntities() throws Exception {
    if ( entities==null){
      throw new Exception("Entities is null. Should be empty.");
    }
    return null;
  }

  public int size() throws Exception {
    if (words==null)
      throw new Exception("Empty Sentence. Size() function not working.");
    return words.size();
  }

  public  Word wordAt(int i) {
    return words.get(i);
  }
  
}

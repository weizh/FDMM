package edu.cmu.lti.weizh.models;

import java.util.ArrayList;
import java.util.List;

public class Sentence {

	/*
	 * The real meats are in the word.
	 */
	List<Word> words;
	String plainSentence;
	public String getPlainSentence() {
    return plainSentence;
  }

  public void setPlainSentence(String plainSentence) {
    this.plainSentence = plainSentence;
  }

  Tree parse;
	/*
	 * entities are only positions.
	 */
	List<NamedEntity> entities;

	public boolean isEmpty() {
		if ((words == null || words.size() == 0))
			return true;
		else
			return false;
	}

	public Sentence(String plainSentence) {
		this.plainSentence = plainSentence;
		this.words = new ArrayList<Word>();
	}

	public Sentence(List<Word> words) {
		if (words==null)
			try {
				throw new Exception("Null pointer assignment to initialize sentence.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		this.words = words;
	}

	public Sentence(Tree parse) {
		this.parse = parse;
		this.words = new ArrayList<Word>();
	}

	public List<NamedEntity> getNamedEntities() {
		if (entities == null) {
			System.err.println("Entity is null. Should be empty.");
		}
		return this.entities;
	}

	public int size() throws Exception {
		if (words == null)
			throw new Exception("Empty Sentence. Size() function not working.");
		return words.size();
	}

	public Word wordAt(int i) {
		return words.get(i);
	}

	public void setTree(Tree parsetree) {
		// TODO Auto-generated method stub
		this.parse = parsetree;
	}

	public void setNEs(List<NamedEntity> nEs) {
		// TODO Auto-generated method stub
		this.entities = nEs;

	}

	public void setWords(List<Word> words2) {
		// TODO Auto-generated method stub
		this.words = words2;

	}

	public List<Word> getWords() {
		// TODO Auto-generated method stub
		return this.words;
	}

}

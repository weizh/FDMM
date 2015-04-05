package edu.cmu.lti.weizh.docmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Sentence implements Serializable {

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

	/**
	 * Use this constructor to create a string sentence. List<Word> is
	 * initialized into empty list as well. Filling is done later.
	 * 
	 * @param plainSentence
	 */
	public Sentence(String plainSentence) {
		this.plainSentence = plainSentence;
		this.words = new ArrayList<Word>();
	}

	/**
	 * Initialize the sentence with a list of words. No string provided.
	 * 
	 * @param words
	 */
	public Sentence(List<Word> words) {
		if (words == null)
			try {
				throw new Exception("Null pointer assignment to initialize sentence.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		this.words = words;
	}

	public boolean[] getFolds() {
		Word pivot = words.get(0);
		return pivot.getBooleanFolds();
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

	public int size() {
		if (words == null)
			return 0;
		return words.size();
	}

	public Word wordAt(int i) {
		return words.get(i);
	}

	public void setTree(Tree parsetree) {
		this.parse = parsetree;
	}

	public void setNEs(List<NamedEntity> nEs) {
		this.entities = nEs;

	}

	public void setWords(List<Word> words2) {
		this.words = words2;

	}

	public List<Word> getWords() {
		return this.words;
	}

}

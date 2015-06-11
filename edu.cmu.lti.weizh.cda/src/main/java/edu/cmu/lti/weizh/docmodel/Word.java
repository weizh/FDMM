package edu.cmu.lti.weizh.docmodel;

import java.io.Serializable;

import edu.cmu.lti.weizh.eval.Prediction;

/**
 * This class is a word class where each word has features such as POS, NE type, Chunk type.
 * The class does not include any field for word position info, or sentence info.
 * The position of word is denoted by the position in the List of Word.
 * @author wei
 *
 */
public class Word implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String word;

	private String spellingCorrectedWord;

	private String partOfSpeech;

	private String entityType;

	private String predition;

	private boolean[] folds;

	private String chunkType;

	Prediction NerPDist;
	Prediction PosPDist;
	/**
	 * Constructor with just word string.
	 * 
	 * @param string
	 */
	public Word(String string) {
		this.word = string;
	}

	/**
	 * Constructor with word string as well as the part of speech.
	 * 
	 * @param string
	 * @param pos
	 */
	public Word(String string, String partOfSpeech) {
		this(string);
		this.partOfSpeech = partOfSpeech;
	}
	
	/////////////// AutoGen Methods ///////////////////
	
	public String getCorrected() {
		return spellingCorrectedWord;
	}

	public void setCorrected(String corrected) {
		this.spellingCorrectedWord = corrected;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getPartOfSpeech() {
		return partOfSpeech;
	}

	public void setPartOfSpeech(String pos) {
		this.partOfSpeech = pos;
	}

	public void setEntityType(String string) {
		this.entityType = string;
	}

	public String getEntityType() {
		return this.entityType;
	}

	public void setPrediction(String label) throws Exception {
		this.predition = label;
	}

	public String getPrediction() {
		return this.predition;
	}

	public void setNerPDist(Prediction prediction) {
		this.NerPDist = prediction;
	}

	public Prediction getNerPDist() {
		return this.NerPDist;
	}
	
	public void setBooleanFolds(boolean[] rand) {
		this.folds = rand;
	}

	public boolean[] getBooleanFolds() {
		return this.folds;
	}
	
	public String getChunkType() {
		return chunkType;
	}

	public void setChunkType(String chunkType) {
		this.chunkType = chunkType;
	}
	
	/////////////// Override toString ////////////////////
	public String toString() {
		return word +"\t[POS:"+ this.partOfSpeech+"]\t[NE:"+this.entityType+"]\t[CHUNK:"+ this.chunkType+"]";

	}
	
	
	////////////// Additional Methods //////////////////////
	public String getTrimLowered() {
		return word.trim().toLowerCase();
	}
	
	public String isCapitalizedFirst() {
		if (Character.isUpperCase(this.word.charAt(0)))
			return "A";
		else
			return "a";
		// char[] ch = new char[word.length()];
		// for (int i = 0; i < word.length(); i++) {
		// char c = word.charAt(i);
		//
		// if (Character.isUpperCase(c))
		// ch[i] = 'A';
		// else if (Character.isUpperCase(c))
		// ch[i] = 'a';
		// else if (Character.isDigit(c))
		// ch[i] = '0';
		// }
		// return new String(ch);
	}

	

	/**
	 * Capitalization does not count towards a form. Letter, Number, and
	 * apostrophe, dash
	 * 
	 * @return
	 */
	public String getWordForm() {
		char[] res = new char[word.length()];

		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			if (c >= 65 && c <= 90)
				res[i] = 'A';
			else if (c >= 97 && c <= 122)
				res[i] = 'a';
			else if (c >= 47 && c <= 57)
				res[i] = 'n';
			else
				res[i] = c;
		}
		String s = new String(res).replaceAll("A[A]+", "A+").replaceAll("a[a]+", "a+").replaceAll("n[n]+", "n+");
		return s;
	}

	public String getSuffix() {
		return word.substring(word.length() > 3 ? (word.length() - 3) : 0);
	}

	public String getSuffix(int i) {
		return word.substring(word.length() > i ? (word.length() - i) : 0);
	}

	public String getPreffix() {
		return word.substring(0, word.length() > 3 ? 3 : word.length());
	}

	public String getPreffix(int i) {
		return word.substring(0, word.length() > i ? i : word.length());
	}

	public void setPosPDist(Prediction prediction) {
		this.PosPDist = prediction;
	}

	public Prediction getPosPDist() {
		return PosPDist;
	}

	

}

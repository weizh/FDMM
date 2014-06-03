package edu.cmu.lti.weizh.models;

public class Word {

	String word;

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	String pos;

	String entityType;

	String predition;

	boolean[] folds;

	public Word(String string) {
		this.word = string;
	}

	public Word(String string, String pos) {
		this.word = string;
		this.pos = pos;
	}

	public String getLemma() {
		return word.toLowerCase();
	}

	public String getPOS() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String toString() {
		return word;

	}

	public String getCap() {
		if (Character.isUpperCase(this.word.charAt(0)))
				return "A";
		else
			return "a";
//		char[] ch = new char[word.length()];
//		for (int i = 0; i < word.length(); i++) {
//			char c = word.charAt(i);
//
//			if (Character.isUpperCase(c))
//				ch[i] = 'A';
//			else if (Character.isUpperCase(c))
//				ch[i] = 'a';
//			else if (Character.isDigit(c))
//				ch[i] = '0';
//		}
//		return new String(ch);
	}

	public void setEntityType(String string) {
		// TODO Auto-generated method stub
		this.entityType = string;
	}

	public String getEntityType() {
		// TODO Auto-generated method stub
		return this.entityType;
	}

	public void setPrediction(String label) throws Exception {
		// TODO Auto-generated method stub
		this.predition = label;
	}

	public String getPrediction() {
		// TODO Auto-generated method stub
		return this.predition;
	}

	public void setBooleanFolds(boolean[] rand) {
		// TODO Auto-generated method stub
		this.folds = rand;
	}

	public boolean[] getBooleanFolds() {
		// TODO Auto-generated method stub
		return this.folds;
	}
}

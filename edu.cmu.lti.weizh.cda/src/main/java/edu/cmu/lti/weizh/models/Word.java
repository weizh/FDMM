package edu.cmu.lti.weizh.models;

public class Word {

	String word;

	String pos;

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

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String toString() {
		return word;

	}

	public String getCap() {
		char[] ch = new char[word.length()];
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);

			if (Character.isUpperCase(c))
				ch[i] = 'A';
			else if (Character.isUpperCase(c))
				ch[i] = 'a';
			else if (Character.isDigit(c))
				ch[i] = '0';
		}
		return new String(ch);
	}
}

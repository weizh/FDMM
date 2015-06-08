package edu.cmu.lti.weizh.nlp.tokenizer;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;

public class JavaBreakIterator {
	static BreakIterator tokenizer = BreakIterator.getWordInstance();

	public static List<Word> tokenize(String s) {
		List<Word> words = new ArrayList<Word>();

		tokenizer.setText(s);
		int start = tokenizer.first();
		for (int end = tokenizer.next(); end != BreakIterator.DONE; start = end, end = tokenizer.next()) {
			if (s.substring(start, end).trim().length()>0)
				words.add(new Word(s.substring(start, end)));
		}
		return words;

	}

	public static Sentence tokenize(Sentence sent) {
		String s = sent.getPlainSentence();
		List<Word> words = new ArrayList<Word>();

		tokenizer.setText(s);
		int start = tokenizer.first();
		for (int end = tokenizer.next(); end != BreakIterator.DONE; start = end, end = tokenizer.next()) {
			if (s.substring(start, end).trim().length()>0)
				words.add(new Word(s.substring(start, end)));
		}
		sent.setWords(words);
		return sent;

	}
	
	public static void main(String args[]) {

		String stringToExamine = "�Star Wars� Director Rian Johnson Flashback: �I Don't Think I Could Ever Write for A Living�";
		// print each word in order
		BreakIterator boundary = BreakIterator.getWordInstance();
		boundary.setText(stringToExamine);
		printEachForward(boundary, stringToExamine);
		// print each sentence in reverse order
		boundary = BreakIterator.getSentenceInstance(Locale.US);
		boundary.setText(stringToExamine);
		printEachBackward(boundary, stringToExamine);
		printFirst(boundary, stringToExamine);
		printLast(boundary, stringToExamine);

	}

	// Print each element in order:
	public static void printEachForward(BreakIterator boundary, String source) {
		int start = boundary.first();
		for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {
			System.out.println(source.substring(start, end));
		}
	}

	// Print each element in reverse order:
	public static void printEachBackward(BreakIterator boundary, String source) {
		int end = boundary.last();
		for (int start = boundary.previous(); start != BreakIterator.DONE; end = start, start = boundary.previous()) {
			System.out.println(source.substring(start, end));
		}
	}

	// Print first element:
	public static void printFirst(BreakIterator boundary, String source) {
		int start = boundary.first();
		int end = boundary.next();
		System.out.println(source.substring(start, end));
	}

	// Print last element:
	public static void printLast(BreakIterator boundary, String source) {
		int end = boundary.last();
		int start = boundary.previous();
		System.out.println(source.substring(start, end));
	}

	// Print the element at a specified position:
	public static void printAt(BreakIterator boundary, int pos, String source) {
		int end = boundary.following(pos);
		int start = boundary.previous();
		System.out.println(source.substring(start, end));
	}

	// Find the next word:
	public static int nextWordStartAfter(int pos, String text) {
		BreakIterator wb = BreakIterator.getWordInstance();
		wb.setText(text);
		int last = wb.following(pos);
		int current = wb.next();
		while (current != BreakIterator.DONE) {
			for (int p = last; p < current; p++) {
				if (Character.isLetter(text.codePointAt(p)))
					return last;
			}
			last = current;
			current = wb.next();
		}
		return BreakIterator.DONE;
	}
}

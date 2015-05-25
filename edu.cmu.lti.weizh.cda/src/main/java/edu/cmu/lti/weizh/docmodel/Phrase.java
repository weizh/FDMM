package edu.cmu.lti.weizh.docmodel;

public class Phrase {

	Sentence sent;

	public Sentence getSent() {
		return sent;
	}
	public void setSent(Sentence sent) {
		this.sent = sent;
	}

	int start, end;

	public Phrase(){
		
	}
	public Phrase(Sentence sent, int start, int end){
		this.sent=sent;
		this.start=start;
		this.end=end;
	}
	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public int setStart(int s) {
		this.start = s;
		return start;
	}

	public int getEnd(int e) {
		this.end = e;
		return end;
	}

	public String getLemma() {
		if (start == end)
			return sent.getWords().get(start).getTrimLowered().toString();
		else if (start == end - 1)
			return sent.getWords().get(start).getTrimLowered() + '-' + sent.getWords().get(end).getTrimLowered();
		else if (start == end - 2)
			return sent.getWords().get(start).getTrimLowered() + '-' + sent.getWords().get(start + 1).getTrimLowered() + '-'
					+ sent.getWords().get(end).getTrimLowered();
		else {
			StringBuilder sb = new StringBuilder(sent.getWords().get(start).getTrimLowered());
			for (int i = start + 1; i <= end; i++)
				sb.append("-").append(sent.getWords().get(i).getTrimLowered());
			return sb.toString();
		}
	}

	public String getPOS() {
		if (start == end)
			return sent.getWords().get(start).getPartOfSpeech();
		else if (start + 1 == end)
			return sent.getWords().get(start).getPartOfSpeech() + '-' + sent.getWords().get(end).getPartOfSpeech();
		else if (start + 2 == end)
			return sent.getWords().get(start).getPartOfSpeech() + '-' + sent.getWords().get(start + 1).getPartOfSpeech() + '-'
					+ sent.getWords().get(end).getPartOfSpeech();
		else {
			StringBuilder pos = new StringBuilder(sent.getWords().get(start).getPartOfSpeech());
			for (int s = start + 1; s <= end; s++)
				pos.append('-').append(sent.getWords().get(s).getPartOfSpeech());
			return pos.toString();
		}
	}
	public String getWords() {
		// TODO Auto-generated method stub
		
		if (start == end)
			return sent.getWords().get(start).getWord().toString();
		else if (start == end - 1)
			return sent.getWords().get(start).getWord() + ' ' + sent.getWords().get(end).getWord();
		else if (start == end - 2)
			return sent.getWords().get(start).getWord() + ' ' + sent.getWords().get(start + 1).getWord() + ' '
					+ sent.getWords().get(end).getWord();
		else {
			StringBuilder sb = new StringBuilder(sent.getWords().get(start).getWord());
			for (int i = start + 1; i <= end; i++)
				sb.append(" ").append(sent.getWords().get(i).getWord());
			return sb.toString();
		}
	}
}
